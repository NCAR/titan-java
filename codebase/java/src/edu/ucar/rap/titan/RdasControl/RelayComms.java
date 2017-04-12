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

package edu.ucar.rap.titan.RdasControl;

import edu.ucar.rap.titan.RdasControl.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.net.*;

public class RelayComms extends Thread implements NioTimeoutListener {
    
  private Parameters _params = Parameters.getInstance();
    
  // nio socket channel for reading and writing
  // selector for detecting pending data
    
  private SocketChannel _channel = null;
  private Selector _selector = null;
  private NioUtils _nio = new NioUtils(this);

  // commands to the radar
    
  private CommandQueue _commandQueue = CommandQueue.getInstance();
    
  // data from the radar
  
  private MessageQueue _dataQueue = DataQueue.getInstance();
  
  // list of listeners for the raw data
    
  private ArrayList _beamListeners = new ArrayList();
    
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

  /**
   * add a listener for response when a beam arrives
   */
    
  public void addBeamBufferListener(BeamBufferListener listener) {
    _beamListeners.add(listener);
  }

  /**
   * remove a listener for response when a beam arrives
   */
    
  public void removeBeamBufferListener(BeamBufferListener listener) {
    _beamListeners.remove(listener);
  }

  // provide run method for thread
    
  public void run() {
	
    while (true) {

      // create the socket if not already open
	    
      if (_channel == null) {
        _openChannel();
        if (_channel == null) {
          _sleep(1000);
          ProcmapReg.getInstance().autoRegister
            ("Trying to open relay socket");
          continue;
        }
      }

      // handle command from queue if pending
	    
      // _handlePendingCommands();
      _handlePendingCommand();
	    
      // read a beam from the radar if available
	    
      ProcmapReg.getInstance().autoRegister("Reading beam");
      int available = 0;
      try {
        if (_selector != null) {
          available = _selector.select(100);
        }
      } catch (IOException e) {
        e.printStackTrace();
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
          System.out.println("RelayComms: waiting for data");
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
      System.out.println("RelayComms - connected to RDAS");
      System.out.println
        ("  RDAS host is: " +
         _channel.socket().getRemoteSocketAddress());
    }

    // delay after connection

    _sleep(_params.rdasComm.connectDelay.getValue());

    // send current state of control model to client

    ControlModel.getInstance().sendCurrentState();

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
  // handle command if available
    
  private int _handlePendingCommand()
	
  {

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
          System.err.println("ERROR - RdasRelay writing command");
          _closeChannel();
          return -1;
        }
      }
    }

    return 0;

  }

  ///////////////
  // read a beam

  private int _readBeam(BeamMessage beamMsg) {
	
    // sync by reading in cookie
	
    ByteBuffer cookieBuf = ByteBuffer.allocate(1);
    byte c1 = 0, c2 = 0, c3 = 0, c4 = 0;
    int tries = 0;
	
    while (true) {
      if (_nio.readIntoBuffer(_channel, cookieBuf, 10) != 0) {
        _closeChannel();
        return -1;
      }
      c4 = cookieBuf.get(0);
      if (c1 == 0x5A && c2 == 0x5A && c3 == 0x5A && c4 == 0x5A) {
        break;
      }
      tries++;
      if (tries > 100000) {
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

    ByteBuffer hdrBuf = beamMsg.getHeaderBuf();
    if (!_params.rdasComm.bigEndian.getValue()) {
      hdrBuf.order(ByteOrder.LITTLE_ENDIAN);
    }
    if (_nio.readIntoBuffer(_channel, hdrBuf, 10) != 0) {
      _closeChannel();
      return -1;
    }

    // load beam message header from buffer

    try {
      beamMsg.disassembleHeader();
    } catch (BufferUnderflowException e) {
      System.err.println("ERROR - RelayComms._readBeam");
      System.err.println("        Cannot decode header");
      return -1;
    }
	    
    if (_params.verbose.getValue()) {
      double az = beamMsg.getAz();
      double el = beamMsg.getEl();
      int nGates = beamMsg.getNGates();
      System.out.println("Got beam, el, az, nGates: " +
                         el + ", " + az + ", " + nGates);
    }

    // read in data
	
    ByteBuffer countBuf = beamMsg.getCountBuf();
    if (countBuf.capacity() > 0) {
      if (_nio.readIntoBuffer(_channel, countBuf, 10) != 0) {
        _closeChannel();
        return -1;
      }
    }

    // re-pack beam into single buffer
	
    ByteBuffer beamBuf =
      ByteBuffer.allocate(4 + hdrBuf.capacity() + countBuf.capacity());
    beamBuf.putInt(0x5A5A5A5A);
    hdrBuf.rewind();
    beamBuf.put(hdrBuf);
    countBuf.rewind();
    beamBuf.put(countBuf);
    beamBuf.flip();
	
    // calling all listeners to handle beam buffer
	
    for (Iterator ii = _beamListeners.iterator(); ii.hasNext();) {
      BeamBufferListener listener = (BeamBufferListener) ii.next();
      listener.handleBeamBuffer(beamBuf);
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

}


  ///////////////////////////////////////////////////
  // handle commands if available
    
  //     private void _handlePendingCommands()
    
  //     {
    
  // 	while (true) {
    
  // 	    // get a command from the queue if available
    
  // 	    ByteBuffer commandBuf = (ByteBuffer) _commandQueue.pop();
    
  // 	    if (commandBuf == null) {
  // 		return;
  // 	    }
    
  // 	    if (_params.verbose.getValue()) {
  // 		System.err.println("RelayComms - sending command");
  // 	    }
  // 	    if (_channel != null) {
  // 		if (_params.rdasComm.commandDelay.getValue() > 0) {
  // 		    _sleep(_params.rdasComm.commandDelay.getValue());
  // 		}
  // 		try {
  // 		    _channel.write(commandBuf);
  // 		} catch (IOException e) {
  // 		    System.err.println("ERROR - RelayComms writing command");
  // 		    e.printStackTrace();
  // 		    _closeChannel();
  // 		    return;
  // 		}
  // 	    }
    
  // 	} // while
    
  //     }

