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

package edu.ucar.rap.titan.RdasRelay;

import java.util.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.net.*;
import edu.ucar.rap.jrp.*;
import edu.ucar.rap.titan.RdasControl.*;

public class ClientComms extends Thread {
    
    private Parameters _params = Parameters.getInstance();

    // nio socket channel for reading and writing

    private SocketChannel _channel = null;
    private Selector _selector = null;
    
    // commands to the radar

    private CommandQueue _commandQueue = CommandQueue.getInstance();
    
    // data from the radar

    private DataBufQueue _dataQueue = DataBufQueue.getInstance();

    // beam count

    private int  _beamCount = 0;

    /**
     * constructor
     */
    
    public ClientComms(SocketChannel channel) {
	_channel = channel;
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
	    return;
	}

	// set up a read selector on the channel

	try {
	    _selector = Selector.open();
	    _channel.register(_selector, SelectionKey.OP_READ);
	} catch (IOException e) {
	    e.printStackTrace();
	    _closeChannel();
	    return;
	}
	
	while (true) {
	    
	    // return if channel has been closed
	    
	    if (_channel == null) {
		return;
	    }
	    
	    // handle data from queue if pending
	    
	    _handlePendingData();
	    
	    // read a command from the client, if available
	    
	    int available;
	    try {
		available = _selector.select(10);
	    } catch (IOException e) {
		e.printStackTrace();
		_closeChannel();
		return;
	    }
	    if (available > 0) {
		_readCommand();
	    } else {
		if (_params.verbose.getValue()) {
		    System.out.println("Waiting for command");
		}
	    }

	    // clear selector

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
	    
	} // while
	
    }
    
    ///////////////
    // close socket
    
    private void _closeChannel() {
	try {
	    _channel.close();
	} catch (IOException e) {
	}
	_channel = null;
    }

    ///////////////////////////////////////////////////
    // handle data if available
    //
    // returns 0 on success, -1 on failure
    
    private void _handlePendingData()
	
    {
	
	while (true) {
	    
	    // get a data buffer from the queue if available
	    
	    ByteBuffer dataBuf = (ByteBuffer) _dataQueue.pop();
	    
	    if (dataBuf == null) {
		return;
	    }
	    
	    if (_params.verbose.getValue()) {
		System.err.println("ClientComms received data");
	    }
	    if (_channel != null) {
		try {
		    _channel.write(dataBuf);
		} catch (IOException e) {
		    System.err.println("ERROR - ClientComms writing beam");
		    e.printStackTrace();
		    _closeChannel();
		    return;
		}
		if ((_beamCount % 360) == 0) {
		    _writeParams();
		    _writeCalib();
		}
		_beamCount++;
	    }

	} // while

    }
    
    //////////////////
    // read a command
    
    private int _readCommand() {
	
	// sync by reading in cookie
	
	ByteBuffer cookieBuf = ByteBuffer.allocate(1);
	byte c1 = 0, c2 = 0, c3 = 0, c4 = 0;
	int tries = 0;

	while (true) {
	    if (_readChannelIntoBuffer(cookieBuf) != 0) {
		return -1;
	    }
	    c4 = cookieBuf.get(0);
	    if (c1 == 0x5B && c2 == 0x5B && c3 == 0x5B && c4 == 0x5B) {
		break;
	    }
	    tries++;
	    if (tries > 10000) {
		System.err.println("WARNING - RdasRelay.ClientComms");
		System.err.println("  Too many tries without finding cookie");
		_closeChannel();
		return -1;
	    }
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
	if (_readChannelIntoBuffer(hdrBuf) != 0) {
	    return -1;
	}

	int commandType = hdrBuf.getInt();
	int commandLen = hdrBuf.getInt();
	
	if (_params.verbose.getValue()) {
	    System.out.println("Got command type: " + commandType);
	    System.out.println("    command len : " + commandLen);
	}

	// read in rest of command

	ByteBuffer commandBuf = ByteBuffer.allocate(0);
	if (commandLen > 0) {
	    commandBuf = ByteBuffer.allocate(commandLen);
	    if (_readChannelIntoBuffer(commandBuf) != 0) {
		return -1;
	    }

	}

	// re-pack command into single buffer

	ByteBuffer outBuf = ByteBuffer.allocate(4 + nbytesHeader + commandLen);
	outBuf.putInt(0x5B5B5B5B);
	hdrBuf.flip();
	outBuf.put(hdrBuf);
	outBuf.put(commandBuf);
	outBuf.flip();

	// push command into buffer

	_commandQueue.addBuffer(outBuf);
	    
	return 0;

    }

    ///////////////////////////////////////////////
    // read from the socket channel into the buffer
    
    private int _readChannelIntoBuffer(ByteBuffer buf) {
	
	buf.clear();
	int totRead = 0;
	
	// read until capacity reached
	
	while (totRead < buf.capacity()) {

	    // handle any waiting data

	    _handlePendingData();
	    
	    // read in data
	    
	    if (_channel == null) {
		return -1;
	    }

	    int nRead;
	    try {
		nRead = _channel.read(buf);
	    }
	    catch (IOException e) {
		System.err.println("ERROR - ClientComms._readChannelIntoBufferError");
		System.err.println(e);
		_closeChannel();
		return -1;
	    }
	    if (nRead < 0) {
		_closeChannel();
		return -1;
	    }
	    totRead += nRead;

	    // sleep if no data available
	    
	    if (nRead == 0) {
		_sleep(10);
	    }
	}

	// prepare buffer for use
	
	buf.flip();

	return 0;

    }

    ////////////////////////////////////
    // write parameters and calibration
    
    private int _writeParams() {

	// get params path

	String paramPath = _params.getParameterFilePath();
	if (_params.debug.getValue()) {
	    System.out.println("Params path: " + paramPath);
	}

	// get param file size

	File paramFile = new File(paramPath);
	if (!paramFile.exists()) {
	    System.err.println("  Param file does not exist: " + paramPath);
	    return -1;
	}
	int paramLen = (int) paramFile.length();
	if (_params.debug.getValue()) {
	    System.err.println("Param file length: " + paramLen);
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
	    System.err.println("  Cannot read input file into buffer: " + paramPath);
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

	// write to channel
	
	if (_channel != null) {
	    try {
		_channel.write(outBuf);
	    } catch (IOException e) {
		System.err.println("ERROR - ClientComms writing params");
		e.printStackTrace();
		_closeChannel();
		return -1;
	    }
	}

	return 0;

    }
	
    ////////////////////////////////////
    // write calibration
    
    private int _writeCalib() {
	
	// get calib path
	
	String calibPath = _params.calib.filePath.getValue();
	if (_params.debug.getValue()) {
	    System.out.println("Calib  path: " + calibPath);
	}
	
	// get calib file size

	File calibFile = new File(calibPath);
	if (!calibFile.exists()) {
	    System.err.println("  Calib file does not exist: " + calibPath);
	    return -1;
	}
	int calibLen = (int) calibFile.length();
	if (_params.debug.getValue()) {
	    System.err.println("Calib file length: " + calibLen);
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
	    System.err.println("  Cannot read input file into buffer: " + calibPath);
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

	// write to channel
	
	if (_channel != null) {
	    try {
		_channel.write(outBuf);
	    } catch (IOException e) {
		System.err.println("ERROR - ClientComms writing calibration");
		e.printStackTrace();
		_closeChannel();
		return -1;
	    }
	}

	return 0;

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

}
