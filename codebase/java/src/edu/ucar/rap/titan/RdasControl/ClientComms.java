///////////////////////////////////////////////////////////////////////
//
// ClientComms
//
// Connect to client using RDAS messaging
//
// Mike Dixon
//
// April 2004
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.util.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.net.*;
import edu.ucar.rap.jrp.*;
import edu.ucar.rap.titan.RdasControl.*;

public class ClientComms extends Thread
    implements NioTimeoutListener,BeamBufferListener {
    
    private Parameters _params = Parameters.getInstance();

    // nio socket channel for reading and writing

    private SocketChannel _channel = null;
    private Selector _selector = null;
    private NioUtils _nio = new NioUtils(this);

    // accept remote commands?

    private boolean _acceptRemoteCommands = false;
    
    // commands to the radar

    private CommandQueue _commandQueue = CommandQueue.getInstance();
    
    // data from the radar

    private MessageQueue _beamQueue = new MessageQueue();

    // beam count

    private int  _beamCount = 0;
    private double _timeBetweenBeams = 0.0;
    private long _lastBeamTime = 0;  // msecs since 1/1/1970

    /**
     * constructor
     */
    
    public ClientComms(SocketChannel channel) {
	_channel = channel;
	_beamQueue.setMaxSize(100);
	RelayComms.getInstance().addBeamBufferListener(this);
    }
    
    /**
     * handle new beam when it arrives
     */
    
    public void handleBeamBuffer(ByteBuffer beamBuf) {
	_beamQueue.push(beamBuf);
    }

    // provide run method for thread
    
    public void run() {

	// set channel non-blocking
	
	try {
	    _channel.configureBlocking(false);
	}
	catch (IOException e) {
	    System.err.println("ERROR - ClientComms.run\n" +
			       "  Cannot set channel to non-blocking.");
	    e.printStackTrace();
	    _tidyUp();
	    return;
	}

	// set up a read selector on the channel

	try {
	    _selector = Selector.open();
	    _channel.register(_selector, SelectionKey.OP_READ);
	} catch (IOException e) {
	    e.printStackTrace();
	    _closeChannel();
	    _tidyUp();
	    return;
	}

	// write params and calib to client

	_writeParams();
	_writeCalib();
	
	while (true) {

	    // register with procmap
	    
	    ProcmapReg.getInstance().autoRegister("Handling pending data");

	    // return if channel has been closed
	    
	    if (_channel == null || _selector == null) {
		_tidyUp();
		return;
	    }
	    
	    // handle data from queue if pending
	    
	    _handlePendingData();
	    
	    // read a command from the client, if available
	    
	    int available = 0;
	    try {
		if (_selector != null) {
		    available = _selector.select(10);
		}
	    } catch (IOException e) {
		e.printStackTrace();
		_closeChannel();
		_tidyUp();
		return;
	    } catch (ClosedSelectorException e) {
		return;
	    }
	    if (available > 0) {
		_readCommand();
	    }

	    // clear selector

	    if (_selector != null) {
		Iterator it = _selector.selectedKeys().iterator();
		while (it.hasNext()) {
		    SelectionKey key = (SelectionKey) it.next();
		    try {
			if (key.isReadable()) {
			    it.remove();
			}
		    } catch (CancelledKeyException e) {
		    }
		}
	    }
	    
	} // while
	
    }

    ///////////////
    // close socket
    
    private void _closeChannel() {
	if (_channel == null) {
	    return;
	}
 	try {
	    _channel.socket().close();
 	    _channel.close();
	    if (_selector != null) {
		_selector.close();
	    }
 	} catch (IOException e) {
 	}
	_channel = null;
	_selector = null;
    }

    ///////////////////////////////////////////////////
    // handle data if available
    //
    // returns 0 on success, -1 on failure
    
    private int _handlePendingData()
	
    {
	
	while (true) {
	    
	    // get a data buffer from the queue if available
	    
	    ByteBuffer dataBuf = (ByteBuffer) _beamQueue.pop();
	    
	    if (dataBuf == null) {
		return 0;
	    }
	    
	    // check beam rate if required

	    boolean sendBeam = true;
	    if (_timeBetweenBeams > 0) {
		long now = TimeManager.getTime();
		double secsSinceLastBeam = (now - _lastBeamTime) / 1000.0;
		if (secsSinceLastBeam < _timeBetweenBeams) {
		    sendBeam = false;
		} else {
		    _lastBeamTime = now;
		}
	    }

	    if (_channel != null && sendBeam) {
		if (_nio.writeFromBuffer(_channel, dataBuf, 10) != 0) {
		    System.err.println("ERROR - ClientComms writing beam");
		    _closeChannel();
		    return -1;
		}
		_beamCount++;
		// System.err.println("Sent beam, count: " + _beamCount);
	    }

	} // while

	// return 0;

    }
    
    //////////////////
    // read a command
    
    private int _readCommand() {
	
	// sync by reading in cookie
	
	ByteBuffer cookieBuf = ByteBuffer.allocate(1);
	byte c1 = 0, c2 = 0, c3 = 0, c4 = 0;
	int tries = 0;

	while (true) {
	    if (_nio.readCallTimeout(_channel, cookieBuf, 10) != 0) {
		_closeChannel();
		return -1;
	    }
	    c4 = cookieBuf.get(0);
	    if (c1 == 0x5B && c2 == 0x5B && c3 == 0x5B && c4 == 0x5B) {
		break;
	    }
	    tries++;
// 	    if (tries > 10000000) {
// 		System.err.println("WARNING - RdasRelay.ClientComms");
// 		System.err.println("  Too many tries without finding cookie");
// 		_closeChannel();
// 		return -1;
// 	    }
	    c1 = c2;
	    c2 = c3;
	    c3 = c4;
	}

	if (_params.verbose.getValue()) {
	    System.out.println("Got cookie 5B5B5B5B");
	}
	
	// read in header info - command type and length
	
	int nwordsHeader = 2;
	int nbytesHeader = nwordsHeader * 4;
	ByteBuffer hdrBuf = ByteBuffer.allocate(nbytesHeader);
	if (!_params.rdasComm.bigEndian.getValue()) {
	    hdrBuf.order(ByteOrder.LITTLE_ENDIAN);
	}
	if (_nio.readCallTimeout(_channel, hdrBuf, 10) != 0) {
	    _closeChannel();
	    return -1;
	}

	int commandType = hdrBuf.getInt();
	int commandLen = hdrBuf.getInt();
	
	// if (_params.verbose.getValue()) {
	if (_params.debug.getValue()) {
	    System.out.println("Got command type: " + commandType);
	    System.out.println("    command len : " + commandLen);
	}

	// read in rest of command

	ByteBuffer commandBuf = ByteBuffer.allocate(0);
	if (commandLen > 0) {
	    commandBuf = ByteBuffer.allocate(commandLen);
	    if (_nio.readCallTimeout(_channel, commandBuf, 10) != 0) {
		_closeChannel();
		return -1;
	    }
	}

	// send RDAS commands on
	
	if (commandType < CommandMessage.RDAS_COMMAND_MAX &&
	    _acceptRemoteCommands) {

	    // re-pack command into single buffer
	    
	    ByteBuffer outBuf =
		ByteBuffer.allocate(4 + nbytesHeader + commandLen);
	    outBuf.putInt(0x5B5B5B5B);
	    hdrBuf.flip();
	    outBuf.put(hdrBuf);
	    outBuf.put(commandBuf);
	    outBuf.flip();

	    // push command into buffer
	    
	    _commandQueue.addBuffer(outBuf);

	}
	
	// responsd to request for control

	if (commandType == CommandMessage.REQUEST_FOR_CONTROL_COMMAND) {

	    if (_params.debug.getValue()) {
		System.err.println
		    ("====>> Received request for control <<====");
		if (_params.rdasComm.acceptRemoteCommands.getValue()) {
		    System.err.println("  Will accept remote commands");
		} else {
		    System.err.println
			("  Secure mode - will NOT accept remote commands");
		}
	    }
	    
	    if (_params.rdasComm.acceptRemoteCommands.getValue()) {
		_writeCommandResponse(commandType, 1);
		_acceptRemoteCommands = true;
	    } else {
		_writeCommandResponse(commandType, 0);
	    }

	}
	    
	// responsd to request beam decimation

	if (commandType == CommandMessage.BEAM_DECIMATE_COMMAND) {
	    _timeBetweenBeams = commandBuf.getFloat();
	    if (_params.debug.getValue()) {
		System.err.println
		    ("====>> Received request beam decimation <<====");
		System.err.println
		    ("------->> timeBetweenBeams: " + _timeBetweenBeams);
	    }
	}
	    
	return 0;

    }

    ////////////////////////////////////
    // write parameters and calibration
    
    private int _writeParams() {

	// get params path

	String paramPath = _params.getParameterFilePath();
	if (_params.debug.getValue()) {
	    System.out.println("Sending params, params path: " + paramPath);
	}

	// get param file size

	File paramFile = new File(paramPath);
	if (!paramFile.exists()) {
	    System.err.println("  Param file does not exist: " + paramPath);
	    return -1;
	}
	int paramLen = (int) paramFile.length();
	if (_params.debug.getValue()) {
	    System.err.println("  Param file length: " + paramLen);
	}
	
	// create an input stream on this file
	
	FileInputStream paramStream = null;
	try {
	    paramStream = new FileInputStream(paramFile);
	} catch (FileNotFoundException e) {
	    System.err.println("  Param file does not exist: " + paramPath);
	    e.printStackTrace();
	    return -1;
	}

	// read file into buffer
	
	byte[] paramBuf = new byte[paramLen];
	try {
	    paramStream.read(paramBuf);
	} catch (IOException e) {
	    System.err.println("  Cannot read input file into buffer: " +
			       paramPath);
	    e.printStackTrace();
	}

	// create output byte buffer

	ByteBuffer outBuf = ByteBuffer.allocate(8 + paramLen);
	if (!_params.rdasComm.bigEndian.getValue()) {
	    outBuf.order(ByteOrder.LITTLE_ENDIAN);
	}
	int cookie = 0x5C5C5C5C;
	outBuf.putInt(cookie);
	outBuf.putInt(paramLen);
	outBuf.put(paramBuf);
	outBuf.flip();

	// write
	
	if (_nio.writeFromBuffer(_channel, outBuf, 10) != 0) {
	    System.err.println("ERROR - ClientComms writing params");
	    _closeChannel();
	    return -1;
	}

	return 0;

    }
	
    ////////////////////////////////////
    // write calibration
    
    private int _writeCalib() {
	
	// get calib path
	
	String calibPath = _params.calib.filePath.getValue();
	if (_params.debug.getValue()) {
	    System.out.println("Writing calib,  path: " + calibPath);
	}
	
	// get calib file size

	File calibFile = new File(calibPath);
	if (!calibFile.exists()) {
	    if (_params.debug.getValue()) {
		System.err.println("  Calib file does not exist: " + calibPath);
	    }
	    return -1;
	}
	int calibLen = (int) calibFile.length();
	if (_params.debug.getValue()) {
	    System.err.println("  Calib file length: " + calibLen);
	}
	
	// create an input stream on this file
	
	FileInputStream calibStream = null;
	try {
	    calibStream = new FileInputStream(calibFile);
	} catch (FileNotFoundException e) {
	    System.err.println("  Calib file does not exist: " + calibPath);
	    e.printStackTrace();
	    return -1;
	}

	// read file into buffer
	
	byte[] calibBuf = new byte[calibLen];
	try {
	    calibStream.read(calibBuf);
	} catch (IOException e) {
	    System.err.println("  Cannot read input file into buffer: " 
			       + calibPath);
	    e.printStackTrace();
	}

	// create output byte buffer

	ByteBuffer outBuf = ByteBuffer.allocate(8 + calibLen);
	if (!_params.rdasComm.bigEndian.getValue()) {
	    outBuf.order(ByteOrder.LITTLE_ENDIAN);
	}
	int cookie = 0x5D5D5D5D;
	outBuf.putInt(cookie);
	outBuf.putInt(calibLen);
	outBuf.put(calibBuf);
	outBuf.flip();

	// write
	
	if (_nio.writeFromBuffer(_channel, outBuf, 10) != 0) {
	    System.err.println("ERROR - ClientComms writing calib");
	    _closeChannel();
	    return -1;
	}

	return 0;

    }
	
    ////////////////////////////////////
    // write command reponse
    
    private int _writeCommandResponse(int commandType,
				      int response) {
	
	// create output byte buffer
	
	ByteBuffer outBuf = ByteBuffer.allocate(16);
	if (!_params.rdasComm.bigEndian.getValue()) {
	    outBuf.order(ByteOrder.LITTLE_ENDIAN);
	}
	int cookie = 0x5E5E5E5E;
	outBuf.putInt(cookie);
	outBuf.putInt(8);
	outBuf.putInt(commandType);
	outBuf.putInt(response);
	outBuf.flip();

	// write
	
	if (_nio.writeFromBuffer(_channel, outBuf, 10) != 0) {
	    System.err.println("ERROR - ClientComms writing command response");
	    _closeChannel();
	    return -1;
	}

	return 0;

    }

    //////////////////////////////////////////
    // timeout methods for NioTimeoutInterface

    public void readTimeout() {
	_handlePendingData();
    }

    public void writeTimeout() {
    }

    ///////////////////////////////////
    // sleep method
    
    private void _sleep(int msecs) {

	Thread t = Thread.currentThread();
	try {
	    t.sleep(msecs);
	} catch (InterruptedException e) {
	}

    }

    ////////////////////////
    // tidy up on thread exit

    private void _tidyUp()
    {
	RelayComms.getInstance().removeBeamBufferListener(this);
    }
    
}
