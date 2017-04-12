///////////////////////////////////////////////////////////////////////
//
// SocketComms
//
// Mike Dixon
//
// May 2013
//
// Communiacte with DowDrx
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.DowControl;

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
  // Sends commands to drx
  // Reads status from drx
  // Returns status string.

  public String communicate(String message,
                            String host,
                            int port)
    
    throws UnknownHostException, IOException {
    
    SocketChannel channel;
    try {
      channel = SocketChannel.open();
      channel.connect(new InetSocketAddress(host, port));
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

    StringBuilder reply = new StringBuilder(65536);
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
    // check for ending sequence (\r\n\r\n)
	
    ByteBuffer cbuf = ByteBuffer.allocate(1);
    boolean done = false;
    
    StringBuilder line = new StringBuilder(1024);
    line.setLength(0);
    char cc1 = 0;
    char cc2 = 0;
    char cc3 = 0;
    char cc4 = 0;
    while (!done) {
      if (_nio.readCallTimeout(channel, cbuf, 60000) != 0) {
        return -1;
      }
      byte bb = cbuf.get(0);
      char cc = (char) bb;
      cc4 = cc3;
      cc3 = cc2;
      cc2 = cc1;
      cc1 = cc;
      line.append(cc);
      if (bb == 0x0a) {
        String sline = line.toString();
        if (cc4 == '\r' && cc3 == '\n' &&
            cc2 == '\r' && cc1 == '\n') {
          done = true;
        }
        reply.append(sline);
        line.setLength(0);
      }
    }

    return 0;

  }
	
}

