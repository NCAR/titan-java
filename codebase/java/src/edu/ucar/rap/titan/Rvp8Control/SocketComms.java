///////////////////////////////////////////////////////////////////////
//
// SocketComms
//
// Mike Dixon
//
// April 2005
//
// Communiacte with Rvp8Driver
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.Rvp8Control;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.Date;

public class SocketComms implements NioTimeoutListener
{
    
  // params

  private Parameters _params = Parameters.getInstance();

  // status

  private static boolean _priorityInProgress = false;

  private NioUtils _nio = new NioUtils(this);
 
  /**
   * constructor
   */
    
  public SocketComms() {
    
  }

  // timeout methods for NioTimeoutInterface
  
  public void readTimeout() {
  }
  
  public void writeTimeout() {
  }

  ////////////////////////////////
  // communicate
  //
  // Sends commands to driver
  // Reads status from driver
  // Returns status string.

  public String communicate(String message)
    throws UnknownHostException, IOException {
    
    // open socket
    
    String driverHost = _params.comms.driverHost.getValue();
    int driverPort = _params.comms.driverPort.getValue();
    
    SocketChannel channel;
    try {
      channel = SocketChannel.open();
      channel.connect(new InetSocketAddress(driverHost, driverPort));
    } catch (UnknownHostException e) {
      throw e;
    } catch (IOException e) {
      channel = null;
      throw e;
    }

    // load byte buffer from command string
    // adding NULL

    ByteBuffer commandBuf = ByteBuffer.allocate(message.length() + 2);
    commandBuf.put(message.getBytes());
    commandBuf.putChar(Character.MIN_VALUE);
	
    // rewind buffer, ready for sending
    
    commandBuf.rewind();
	
    // write message

    if (_params.verbose.getValue()) {
      System.err.println("Sending message: ----->>");
      System.err.print(message);
    }

    try {
      channel.write(commandBuf);
    } catch (IOException e) {
      System.err.println("ERROR - SocketComms.communicate");
      System.err.println("  Writing byte buffer");
      throw e;
    }

    // read reply

    StringBuilder reply = new StringBuilder(8192);
    if (_readStatus(channel, reply) != 0) {
      throw new IOException("Read reply timed out");
    }
    
    if (_params.verbose.getValue()) {
      System.err.println("Reading reply: ----->>");
      System.err.println(reply);
    }
	
    // close channel

    channel.close();

    return reply.toString();

  }

  ///////////////////////////////
  // read the status
  
  private int _readStatus(SocketChannel channel,
                          StringBuilder reply)
	
  {

    // read in 1 byte at a time
    // build into lines
    // check for ending line
	
    ByteBuffer cbuf = ByteBuffer.allocate(1);
    boolean done = false;
    
    StringBuilder line = new StringBuilder(1024);
    line.setLength(0);
    while (!done) {
      if (_nio.readCallTimeout(channel, cbuf, 60000) != 0) {
        return -1;
      }
      byte bb = cbuf.get(0);
      char cc = (char) bb;
      line.append(cc);
      if (bb == 0x0a) {
        String sline = line.toString();
        if (sline.contains("</rvp8Message>")) {
          done = true;
        }
        reply.append(sline);
        line.setLength(0);
      }
    }

    return 0;

  }
	
}

