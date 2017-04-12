///////////////////////////////////////////////////////////////////////
//
// RelayComms
//
// Relay connection to rdas
//
// Mike Dixon
//
// Nov 2003
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasRelay;

import edu.ucar.rap.titan.RdasControl.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.net.*;

public class RelayComms extends Thread {
    
    private Parameters _params = Parameters.getInstance();
    
    // nio socket channel for reading and writing
    
    private SocketChannel _channel = null;
    private Selector _selector = null;

    // position of fields in the header

    private static final int _nGatesIndex = 12;
    private static final int _nFieldsIndex = 13;
    private static final int _azIndex = 44;
    private static final int _elIndex = 45;
    
    // commands to the radar
    
    private CommandQueue _commandQueue = CommandQueue.getInstance();
    
    // data from the radar
    
    private DataBufQueue _dataQueue = DataBufQueue.getInstance();
    
    /**
     * Single instance for this class - it is a singleton
     */
    
    private static final RelayComms _instance = new RelayComms();

    /**
     * get singleton instance
     */
    
    public static RelayComms getInstance() {
	return _instance;
    }
    
    /**
     * private constructor
     */
    
    private RelayComms() {
    }

    // provide run method for thread
    
    public void run() {
	
	while (true) {

	    // create the socket if not already open
	    
	    if (_channel == null) {
		_openChannel();
		if (_channel == null) {
		    _sleep(1000);
		    continue;
		}
	    }
	    
	    // handle command from queue if pending
	    
	    _handlePendingCommands();
	    
	    // read a beam from the radar if available
	    
	    int available;
	    try {
		available = _selector.select(1000);
	    } catch (IOException e) {
		e.printStackTrace();
		_closeChannel();
		continue;
	    }
	    if (available > 0) {
		_readData();
	    } else {
		if (_params.verbose.getValue()) {
		    System.out.println("RelayComms: waiting for data");
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
    // open socket

    private void _openChannel() {
	
	int port = _params.rdasComm.port.getValue();
	String host = _params.rdasComm.host.getValue();
	
	try {
	    
	    // Creates a non-blocking socket channel for the
	    // specified host name and port.
	    
	    _channel = SocketChannel.open();
	    _channel.configureBlocking(false);
	    
	    _channel.connect(new InetSocketAddress(host, port));
	    
	    while (!_channel.finishConnect()) {
		_sleep(1000);
	    }
	    
 	} catch (UnknownHostException e) {
 	    System.err.println("Cannot find RDAS host: " +
 			       _params.rdasComm.host.getValue());
 	    _channel = null;
 	    return;
	} catch (IOException e) {
	    System.err.println("Cannot connect to port: " + port +
			       ", host: " + host);
	    _channel = null;
	    return;
	}

	if (_params.debug.getValue()) {
	    System.out.println("RelayServer - connected to RDAS service");
	    System.out.println
		("  RDAS host is: " +
		 _channel.socket().getRemoteSocketAddress());
	}

	// create temp antenna and op mode controls to initialize modes

	JFrame tmpFrame = new JFrame();
	AntennaControl antennaControl = new AntennaControl(tmpFrame);
	OpModeControl opModeControl = new OpModeControl(tmpFrame, antennaControl);
	
	// set up a read selector on the channel
	
	try {
	    _selector = Selector.open();
	    _channel.register(_selector, SelectionKey.OP_READ);
	} catch (IOException e) {
	    e.printStackTrace();
	    _closeChannel();
	    return;
	}
	
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
    // handle command if available
    
    private void _handlePendingCommands()
	
    {
	
	while (true) {
	    
	    // get a command from the queue if available
	    
	    ByteBuffer commandBuf = (ByteBuffer) _commandQueue.pop();
	    
	    if (commandBuf == null) {
		return;
	    }

	    if (_params.verbose.getValue()) {
		System.err.println("RelayComms - sending command");
	    }
	    if (_channel != null) {
		try {
		    _channel.write(commandBuf);
		} catch (IOException e) {
		    System.err.println("ERROR - RelayComms writing command");
		    e.printStackTrace();
		    _closeChannel();
		    return;
		}
	    }

	} // while

    }

    ///////////////
    // read a beam

    private int _readData() {
	
	// sync by reading in cookie
	
	ByteBuffer cookieBuf = ByteBuffer.allocate(1);
	byte c1 = 0, c2 = 0, c3 = 0, c4 = 0;
	int tries = 0;
	
	while (true) {
	    if (_readChannelIntoBuffer(cookieBuf) != 0) {
		return -1;
	    }
	    c4 = cookieBuf.get(0);
	    if (c1 == 0x5A && c2 == 0x5A && c3 == 0x5A && c4 == 0x5A) {
		break;
	    }
	    tries++;
	    if (tries > 10000) {
		if (_params.verbose.getValue()) {
		    System.err.println("Too many tries without finding cookie");
		}
		_closeChannel();
		return -1;
	    }
	    c1 = c2;
	    c2 = c3;
	    c3 = c4;
	}

	if (_params.verbose.getValue()) {
	    System.out.println("Got cookie 5A5A5A5A");
	}
	
	// read in header info - we have already removed cookie, so we
	// have one less word to read
	
	int nwordsHeader = (BeamMessage.nHeaderInts +
			    BeamMessage.nSpareInts +
			    BeamMessage.nFieldCodes +
			    BeamMessage.nHeaderFloats +
			    BeamMessage.nSpareFloats +
			    BeamMessage.nAnalogStatus);
	int nbytesHeader = nwordsHeader * 4 + BeamMessage.nStatusChars;
	ByteBuffer hdrBuf = ByteBuffer.allocate(nbytesHeader);
 	if (!_params.rdasComm.bigEndian.getValue()) {
 	    hdrBuf.order(ByteOrder.LITTLE_ENDIAN);
 	}
	if (_readChannelIntoBuffer(hdrBuf) != 0) {
	    return -1;
	}

	// set nGates and nFields
	// subtract 1 from index because we have stripped the cookie
	
	int nGates = hdrBuf.getInt((_nGatesIndex - 1) * 4);
	int nFields = hdrBuf.getInt((_nFieldsIndex - 1) * 4);
	int nBytesData = nGates * nFields * 2;
	
	if (_params.verbose.getValue()) {
	    float az = hdrBuf.getFloat((_azIndex - 1) * 4);
	    float el = hdrBuf.getFloat((_elIndex - 1) * 4);
	     	    System.out.println("Got beam, el, az, nGates: " +
				       el + ", " + az + ", " + nGates);
	}

	// read in data
	
	ByteBuffer dataBuf = ByteBuffer.allocate(0);
	if (nBytesData > 0) {
	    dataBuf = ByteBuffer.allocate(nBytesData);
	    if (_readChannelIntoBuffer(dataBuf) != 0) {
		return -1;
	    }
	    
	}

	// re-pack beam into single buffer
	
	ByteBuffer outBuf = ByteBuffer.allocate(4 + nbytesHeader + nBytesData);
	outBuf.putInt(0x5A5A5A5A);
	outBuf.put(hdrBuf);
	outBuf.put(dataBuf);
	outBuf.flip();

	// push command into buffer

	_dataQueue.addDataBuffer(outBuf);
	
	return 0;

    }

    ///////////////////////////////////////////////
    // read from the socket channel into the buffer
    
    private int _readChannelIntoBuffer(ByteBuffer buf) {
	
	buf.clear();
	int totRead = 0;
	
	// read until capacity reached
	
	while (totRead < buf.capacity()) {

	    // handle any waiting commands

	    _handlePendingCommands();
	    
	    // read in data
	    
	    if (_channel == null) {
		return -1;
	    }

	    int nRead;
	    try {
		nRead = _channel.read(buf);
	    }
	    catch (IOException e) {
		System.err.println("Error reading channel into buffer");
		e.printStackTrace();
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


