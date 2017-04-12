///////////////////////////////////////////////////////////////////////
//
// RelayServer
//
// Listen for connections from RdasControl and TITAN
// Service those connections
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

public class RelayServer extends Thread {
    
    private Parameters _params = Parameters.getInstance();

    // nio socket channel for reading and writing
    private SocketChannel _channel = null;
    
    // commands to the radar

    private CommandQueue _commandQueue = CommandQueue.getInstance();

    // data from the radar

    private MessageQueue _dataQueue = DataQueue.getInstance();

    /**
     * Single instance for this class - it is a singleton
     */
    
    private static final RelayServer _instance = new RelayServer();
    
    /**
     * get singleton instance
     */
    
    public static RelayServer getInstance() {
	return _instance;
    }
    
    /**
     * private constructor
     */
    
    private RelayServer() {
    }

    // provide run method for thread
    
    public void run() {

	// open and bind server channel, non-blocking

	int port = _params.rdasComm.relayPort.getValue();
	if (_params.debug.getValue()) {
	    System.out.println("RelayServer - listening on port: " + port);
	}

	ServerSocketChannel server = null;
	try {
	    server = ServerSocketChannel.open();
	    server.socket().bind(new InetSocketAddress(port));
	    server.configureBlocking(false);
	}
	catch (IOException e) {
	    System.err.println("ERROR - RelayServer.run\n" +
			       "  Cannot open and bind server channel");
	    e.printStackTrace();
	}
	
	while (true) {
	    
	    if (_params.verbose.getValue()) {
		System.out.println("Waiting for connections");
	    }
	    
	    SocketChannel incoming = null;
	    try {
		incoming = server.accept();
	    }
	    catch (IOException e) {
		System.err.println("ERROR - RelayServer.run\n" +
				   "  Cannot accept connections.");
		e.printStackTrace();
	    }

	    if (incoming == null) {

		// no connection, sleep

		_sleep(1000);

	    } else {
		
		if (_params.debug.getValue()) {
		    System.out.println("RelayServer - got connection");
		    System.out.println
			("  Remote host is: " +
			 incoming.socket().getRemoteSocketAddress());
		}

		// spawn thread to handle this connection
		
		ClientComms clientComms = new ClientComms(incoming);
		clientComms.start();

	    }

	} // while
	    
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


