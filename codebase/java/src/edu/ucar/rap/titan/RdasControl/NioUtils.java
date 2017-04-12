///////////////////////////////////////////////////////////////////////
//
// NioUtils
//
// Mike Dixon
//
// April 2005
//
// Helper methods for reads and writes with SocketChannels
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public class NioUtils

{

    private NioTimeoutListener _listener = null;

    //////////////////////////////////////////////////////
    // constructor passing in object which implements the
    // NioTimeoutListener interface

    public NioUtils(NioTimeoutListener listener) {
	_listener = listener;
    }
    
    ///////////////////////////////////////////////
    // read from the socket channel into the buffer
    // filling it to capacity. If timeout occurs and
    // the listener is non-null, the listener will be
    // called.
    
    public int readCallTimeout(SocketChannel channel,
			       ByteBuffer buf,
			       int sleepMsecs) {
	
	if (channel == null) {
	    return -1;
	}

	buf.clear();
	int totRead = 0;
	
	// read until capacity reached
	
	while (totRead < buf.capacity()) {
	    
	    int nRead;
	    try {
		nRead = channel.read(buf);
	    }
	    catch (IOException e) {
		System.err.println("Error reading channel into buffer");
		// e.printStackTrace();
		return -1;
	    }
	    if (nRead < 0) {
		return -1;
	    }
	    totRead += nRead;
	    
	    // sleep if no data available
	    
	    if (nRead == 0) {
		if (_listener != null) {
		    _listener.readTimeout();
		}
		_sleep(sleepMsecs);
	    }

	} // while

	// prepare buffer for use
	
	buf.rewind();

	return 0;

    }

    ///////////////////////////////////////////////
    // read from the socket channel into the buffer
    // filling it to capacity
    
    static public int readIntoBuffer(SocketChannel channel,
				     ByteBuffer buf,
				     int sleepMsecs) {
	
	if (channel == null) {
	    return -1;
	}

	buf.clear();
	int totRead = 0;
	
	// read until capacity reached
	
	while (totRead < buf.capacity()) {
	    
	    int nRead;
	    try {
		nRead = channel.read(buf);
	    }
	    catch (IOException e) {
		System.err.println("Error reading channel into buffer");
		e.printStackTrace();
		return -1;
	    }
	    if (nRead < 0) {
		return -1;
	    }
	    totRead += nRead;
	    
	    // sleep if no data available
	    
	    if (nRead == 0) {
		_sleep(sleepMsecs);
	    }

	} // while

	// prepare buffer for use
	
	buf.rewind();

	return 0;

    }

    ///////////////////////////////////////////////////
    // write buffer to channel, up to the current limit.
    // If timeout occurs and the listener is non-null,
    // the listener will be called.
    
    public int writeCallTimeout(SocketChannel channel,
				ByteBuffer buf,
				int sleepMsecs) {

	if (channel == null || buf.limit() == 0) {
	    return -1;
	}

	int totWritten = 0;
	buf.rewind();
	
	// write until capacity reached
	
	while (totWritten < buf.limit()) {

	    int nWritten;
	    try {
		nWritten = channel.write(buf);
	    }
	    catch (IOException e) {
		System.err.println("Error writing buffer to channel");
		e.printStackTrace();
		return -1;
	    }
	    if (nWritten < 0) {
		return -1;
	    }
	    totWritten += nWritten;
	    
	    // sleep if stalled
	    
	    if (nWritten == sleepMsecs) {
		if (_listener != null) {
		    _listener.writeTimeout();
		}
		_sleep(10);
	    }
	}
	
	// rewind buffer, in case it will be reused
	
	buf.rewind();

	return 0;

    }

    ///////////////////////////////////////////////////
    // write buffer to channel, up to the current limit
    
    static public int writeFromBuffer(SocketChannel channel,
				      ByteBuffer buf,
				      int sleepMsecs) {

	if (channel == null || buf.limit() == 0) {
	    return -1;
	}

	int totWritten = 0;
	buf.rewind();
	
	// write until capacity reached
	
	while (totWritten < buf.limit()) {

	    int nWritten;
	    try {
		nWritten = channel.write(buf);
	    }
	    catch (IOException e) {
		System.err.println("ERROR - NioUtils.writeFromBuffer");
		System.err.println(" Writing buffer to channel");
		return -1;
	    }
	    catch (BufferOverflowException e) {
		System.err.println("ERROR - NioUtils.writeFromBuffer");
		System.err.println("  Buffer overflow");
		return -1;
	    }
	    if (nWritten < 0) {
		return -1;
	    }
	    totWritten += nWritten;
	    
	    // sleep if stalled
	    
	    if (nWritten == sleepMsecs) {
		_sleep(10);
	    }
	}
	
	// rewind buffer, in case it will be reused
	
	buf.rewind();

	return 0;

    }

    ///////////////////////////////////
    // sleep method
    
    static private void _sleep(int msecs) {
	
	Thread t = Thread.currentThread();
	try {
	    t.sleep(msecs);
	} catch (InterruptedException e) {
	}

    }

}
