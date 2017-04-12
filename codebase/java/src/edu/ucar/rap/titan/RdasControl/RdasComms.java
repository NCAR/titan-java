///////////////////////////////////////////////////////////////////////
//
// RdasComms
//
// Connect to radar using RDAS messaging
//
// Mike Dixon
//
// Nov 2003
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.util.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.net.*;
import edu.ucar.rap.jrp.*;

public class RdasComms extends Thread implements NioTimeoutListener {

    // params

    private Parameters _params = Parameters.getInstance();
    private boolean _connect = _params.rdasComm.connectAtStartup.getValue();

    // calibration

    private Calibrate _calib = null;
    
    // listener for when params chabge, and flag to tell this class
    // to ignore param changes if set

    private ParamsChangeListener _paramsChangeListener;
    private boolean _ignoreParamChangeListeners = false;

    // nio socket channel for reading and writing
    // selector for detecting pending data

    private SocketChannel _channel = null;
    private Selector _selector = null;
    private NioUtils _nio = new NioUtils(this);
    
    // commands to the radar

    private CommandQueue _commandQueue = CommandQueue.getInstance();

    // data from the radar

    private MessageQueue _dataQueue = DataQueue.getInstance();

    // status of the connection

    private String _connectionStatus = new String("Not connected");

    /**
     * Single instance for this class - it is a singleton
     */
    
    private static final RdasComms _instance = new RdasComms();

    /**
     * get singleton instance
     */
    
    public static RdasComms getInstance() {
	return _instance;
    }
    
    /**
     * private constructor
     */
    
    private RdasComms() {
    }

    // set connect flag

    public void setConnect(boolean state) {
	_connect = state;
	if (_params.verbose.getValue()) {
	    System.err.println("Setting connect to: " + state);
	}
    }

    // set flag to ignore/not ignore param changes

    public void setIgnoreParamChangeListeners(boolean state) {
	_ignoreParamChangeListeners = state;
    }

    // get connection status

    public String getConnectionStatus() {
	return _connectionStatus;
    }

    // provide run method for thread
    
    public void run() {
	
	// add listener for changes in params
	
	_paramsChangeListener = new ParamsChangeListener();
	_params.rdasComm.addChangeListener(_paramsChangeListener);
	
	while (true) {

	    // create the socket if not already open

	    if (!_connect) {
		_closeChannel();
	    }
	    
	    if (_channel == null) {
		_openChannel();
		if (_channel == null) {
		    _sleep(1000);
		    ProcmapReg.getInstance().autoRegister
			("Trying to open socket");
		    continue;
		}
	    }
	    
	    // handle command from queue if pending
	    
	    _handlePendingCommand();
	    
	    // read a beam from the radar if available
	    
	    ProcmapReg.getInstance().autoRegister("Reading beam");

	    int available = 0;
	    try {
		if (_selector != null) {
		    available = _selector.select(1000);
		}
	    } catch (IOException e) {
		_closeChannel();
		continue;
	    } catch (ClosedSelectorException e) {
		continue;
	    }
	    if (available > 0) {
		// read a beam from the radar if available
		BeamMessage beamMsg = new BeamMessage();
		if (_readBeam(beamMsg) == 0) {
		    // put beam message to data queue
		    _dataQueue.push(beamMsg);
		}
	    } else {
		if (_params.verbose.getValue()) {
		    System.err.println("RdasComms: waiting for data");
		}
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

    ///////////////////////////////////////////////////
    // handle command if available
    
    private int _handlePendingCommand()
	
    {

	ProcmapReg.getInstance().autoRegister("Handling command");

	// get a command from the queue if available
	
	ByteBuffer commandBuf = (ByteBuffer) _commandQueue.pop();
	
	if (commandBuf != null) {
	    if (_params.verbose.getValue()) {
		System.err.println("RdasComms sending command");
	    }
	    if (_channel != null) {
		if (_params.rdasComm.commandDelay.getValue() > 0) {
		    _sleep(_params.rdasComm.commandDelay.getValue());
		}
		if (_nio.writeFromBuffer(_channel, commandBuf, 10) != 0) {
		    System.err.println("ERROR - RdasComms writing command");
		    _closeChannel();
		    return -1;
		}
	    }
	}

	return 0;

    }

    ///////////////
    // open socket

    private void _openChannel() {


	if (!_connect) {
	    return;
	}

	if (_params.verbose.getValue()) {
	    System.err.println("Opening channel");
	}

	int port = _params.rdasComm.port.getValue();
	String host = _params.rdasComm.host.getValue();
	boolean viaRelay = _params.rdasComm.viaRelay.getValue();
	if (viaRelay) {
	    port = _params.rdasComm.relayPort.getValue();
	    host = _params.rdasComm.relayHost.getValue();
	}

	try {
	    
	    // Creates a non-blocking socket channel for the
	    // specified host name and port.
	    
	    _channel = SocketChannel.open();
	    _channel.configureBlocking(false);

	    _channel.connect(new InetSocketAddress(host, port));
	    
	    while (!_channel.finishConnect()) {
		_sleep(100);
	    }
	    
 	} catch (UnknownHostException e) {

	    _connectionStatus = new String("ERROR - cannot find host: " + host);
	    if (_params.debug.getValue()) {
		System.err.println(_connectionStatus);
	    }
 	    _channel = null;
 	    return;
	} catch (IOException e) {
	    _connectionStatus =
		new String("ERROR - cannot connect to host, port: " +
			   host + ", " + port);
	    if (_params.debug.getValue()) {
		System.err.println(_connectionStatus);
	    }
	    _channel = null;
	    return;
	}
	
	if (viaRelay) {
	    _connectionStatus =
		new String("Connected to relay, host, port: " +
			   host + ", " + port);
	} else {
	    _connectionStatus =
		new String("Connected to RDAS, host, port: " +
			   host + ", " + port);
	}
	if (_params.debug.getValue()) {
 	    System.err.println(_connectionStatus);
	}

	// delay after connection
	
	_sleep(_params.rdasComm.connectDelay.getValue());

	// send current state of control model to client

	if (!viaRelay) {
	    ControlModel.getInstance().sendCurrentState();
	}

	// send time between beams if appropriate

	double timeBetweenBeams =
	    _params.rdasComm.timeBetweenRelayBeams.getValue();
	if (viaRelay && timeBetweenBeams > 0) {
	    _commandQueue.sendBeamDecimate(timeBetweenBeams);
	}

	// set up a read selector on the channel
	
	try {
	    _selector = Selector.open();
	    if (_channel != null) {
		_channel.register(_selector, SelectionKey.OP_READ);
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	    _closeChannel();
	    return;
	}
	
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
	_connectionStatus = new String("Not connected");
    }

    ///////////////
    // read a beam

    private int _readBeam(BeamMessage beamMsg) {

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
	    if (c1 == 0x5A && c2 == 0x5A && c3 == 0x5A && c4 == 0x5A) {
		tries = 0;
		break;
	    } else if (c1 == 0x5C && c2 == 0x5C && c3 == 0x5C && c4 == 0x5C) {
		_readParams();
		tries = 0;
		continue;
	    } else if (c1 == 0x5D && c2 == 0x5D && c3 == 0x5D && c4 == 0x5D) {
		_readCalib();
		tries = 0;
		continue;
	    } else if (c1 == 0x5E && c2 == 0x5E && c3 == 0x5E && c4 == 0x5E) {
		_readCommandResponse();
		tries = 0;
		continue;
	    } 
	    tries++;
	    if (tries > 100000000) {
		System.err.println("Too many tries without finding cookie");
		_closeChannel();
		return -1;
	    }
	    c1 = c2;
	    c2 = c3;
	    c3 = c4;
	}

	// read in header info
	
	ByteBuffer hdrBuf = beamMsg.getHeaderBuf();
	if (!_params.rdasComm.bigEndian.getValue()) {
	    hdrBuf.order(ByteOrder.LITTLE_ENDIAN);
	}
	
	if (_nio.readCallTimeout(_channel, hdrBuf, 10) != 0) {
	    _closeChannel();
	    return -1;
	}

	// load beam message header from buffer

	try {
	    beamMsg.disassembleHeader();
	} catch (BufferUnderflowException e) {
	    System.err.println("ERROR - RdasComms._readBeam");
	    System.err.println("        Cannot decode header");
	    return -1;
	}
	    
	// read in counts
	
	ByteBuffer countBuf = beamMsg.getCountBuf();
	if (!_params.rdasComm.bigEndian.getValue()) {
	    countBuf.order(ByteOrder.LITTLE_ENDIAN);
	}
	if (_nio.readCallTimeout(_channel, countBuf, 10) != 0) {
	    _closeChannel();
	    return -1;
	}

	// load beam message counts from buffer
	
	try {
	    beamMsg.disassembleCounts();
	} catch (BufferUnderflowException e) {
	    System.err.println("ERROR - RdasComms._readBeam");
	    System.err.println("        Cannot decode counts");
	    return -1;
	}

	return 0;

    }

    ///////////////
    // read params

    private int _readParams() {
	
	ByteBuffer lenBuf = ByteBuffer.allocate(4);
	if (!_params.rdasComm.bigEndian.getValue()) {
	    lenBuf.order(ByteOrder.LITTLE_ENDIAN);
	}
	if (_nio.readCallTimeout(_channel, lenBuf, 10) != 0) {
	    _closeChannel();
	    return -1;
	}
	int paramsLength = lenBuf.getInt();

	// read in params
	
	ByteBuffer paramsBuf = ByteBuffer.allocate(paramsLength);
	if (_nio.readCallTimeout(_channel, paramsBuf, 10) != 0) {
	    _closeChannel();
	    return -1;
	}

	if (_params.debug.getValue()) {
	    System.err.println
		("-->> RdasComms: got params buf, length: " + paramsLength);
	}

	_params.updateFromRelay(paramsBuf);
	
	return 0;

    }

    // set calibration object

    public void setCalib(Calibrate calib) {
	_calib = calib;
    }

    ///////////////
    // read calib
    
    private int _readCalib() {

	ByteBuffer lenBuf = ByteBuffer.allocate(4);
	if (!_params.rdasComm.bigEndian.getValue()) {
	    lenBuf.order(ByteOrder.LITTLE_ENDIAN);
	}
	if (_nio.readCallTimeout(_channel, lenBuf, 10) != 0) {
	    _closeChannel();
	    return -1;
	}
	int calibLength = lenBuf.getInt();
	
	// read in calib
	
	ByteBuffer calibBuf = ByteBuffer.allocate(calibLength);
	if (_nio.readCallTimeout(_channel, calibBuf, 10) != 0) {
	    _closeChannel();
	    return -1;
	}

	if (_params.debug.getValue()) {
	    System.err.println
		("-->> RdasComms: got calib buf, length: " + calibLength);
	}

	if (!_calib.setFromXmlBuffer(calibBuf)) {
	    return -1;
	}
	
	return 0;

    }

    /////////////////////////
    // read command response
    
    private int _readCommandResponse() {
	
	ByteBuffer lenBuf = ByteBuffer.allocate(4);
	if (!_params.rdasComm.bigEndian.getValue()) {
	    lenBuf.order(ByteOrder.LITTLE_ENDIAN);
	}
	if (_nio.readCallTimeout(_channel, lenBuf, 10) != 0) {
	    _closeChannel();
	    return -1;
	}
	int responseLength = lenBuf.getInt();
	
	// read in response
	
	ByteBuffer intBuf = ByteBuffer.allocate(responseLength);
	if (_nio.readCallTimeout(_channel, intBuf, 10) != 0) {
	    _closeChannel();
	    return -1;
	}
	int commandType = intBuf.getInt();
	int response = intBuf.getInt();
	
	if (_params.debug.getValue()) {
	    System.err.println("-->> RdasComms: got command response:");
	    System.err.println("-->>   Command type: " + commandType);
	    System.err.println("-->>   Response: " + response);
	}

	if (commandType == CommandMessage.REQUEST_FOR_CONTROL_COMMAND) {
          if (ControlFrame.getInstance() != null &&
              ControlFrame.getInstance().getPanel() != null) {
            ControlPanel cpanel = ControlFrame.getInstance().getPanel();
            if (response == 1) {
              cpanel.showControlEnabledDialog(true);
              cpanel.setControlEnabled(true);
	    } else {
              cpanel.showControlEnabledDialog(false);
	    }
          }
        }
	
	return 0;

    }

    // timeout methods for NioTimeoutInterface

    public void readTimeout() {
	_handlePendingCommand();
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

    // Listener for changes in parameters
    
    private class ParamsChangeListener implements CollectionChangeListener {
	public void reactToChange() {
	    if (_ignoreParamChangeListeners) {
		return;
	    }
	    _closeChannel();
	}
    }
    
}


