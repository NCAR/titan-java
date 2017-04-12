///////////////////////////////////////////////////////////////////////
//
// ProcmapReg
//
// Mike Dixon
//
// May 2013
//
// Register with procmap
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RapidDowControl;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.Date;

public class ProcmapReg
{
    
  // Time interval for registering

  private static final int _regInterval = 60;

  // Message types

  private static final int _registerId = 400;
  private static final int _unregisterId = 403;

  // procmap port

  private static final int _procmapPort = 5433;

  // Process info

  private int _pid;              // process ID
  private long _startTime;       // msecs since 1/1/1970
  private long _lastRegTime;     // msecs since 1/1/1970
  private int _maxRegInterval;   // secs
  private String _procName;      // process name
  private String _procInstance;  // process instance
  private String _hostName;      // this host
  private String _userName;      // user of this process

  // sequence number of message
    
  private static int _messSeqNo = 0;

  // reply message header values

  private int _replyLen;
  private int _replyMessageType;

  /**
   * Single instance for this class - it is a singleton
   */
    
  private static final ProcmapReg _instance = new ProcmapReg();
    
  /**
   * get singleton instance
   */
    
  public static ProcmapReg getInstance() {
    return _instance;
  }
    
  /**
   * private constructor
   */
    
  private ProcmapReg() {
	
    _pid = _findPid();
    if (_pid < 0) {
      _pid = 99999;
    }
    _startTime = TimeManager.getTime();
    _lastRegTime = _startTime - (_regInterval * 1000) - 1;
    _maxRegInterval = _regInterval * 2;

    InetAddress local;
    try {
      local = InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      local = null;
    }
    if (local != null) {
      _hostName = local.getHostName();
    } else {
      _hostName = "unknown";
    }

    try {
      _userName = System.getProperty("user.name");
      if (_userName == null) {
        _userName = "unknown";
      }
    } catch (RuntimeException e) {
      _userName = "unknown";
    }

  } // constructor

    // set proc name and instance

  void setProcName(String name) {
    _procName = name;
  }
  void setProcInstance(String instance) {
    _procInstance = instance;
  }
	
  /***********************************
   * Returns the string equivalent of the object.
   *
   * @return        the string equivalent of the object.
   */

  public String toString()
  {
    Date start = new Date(_startTime);
    return(_procName + " " + _procInstance + " " +
           _hostName + " " + _userName + " " +
           _pid + " " + _maxRegInterval + " " + start);
  }


  ////////////////////
  // Access methods //
  ////////////////////

  public int getPid() { return _pid; }
  public long getStartTime() { return _startTime; }
  public int getMaxRegInterval() { return _maxRegInterval; }

  public String getProcName() { return _procName; }
  public String getProcInstance() { return _procInstance; }
  public String getHostName() { return _hostName; }
  public String getUserName() { return _userName; }

  ////////////////////////////////
  // auto register - every minute

  public void autoRegister(String status) {

    long now = TimeManager.getTime();
    double secsSinceLastReg = (now - _lastRegTime) / 1000.0;
    if (secsSinceLastReg >= _regInterval) {
      register(status);
    }
	
  }
    
  ////////////////////////////////
  // force register - every call

  public void register(String status) {
	
    // set last reg time
	
    long now = TimeManager.getTime();
    _lastRegTime = now;
	
    int nowSecs = (int) (now / 1000);
    int startSecs = (int) (_startTime / 1000);

    // load up message

    PmuMessage msg = new PmuMessage(_pid, nowSecs,
                                    startSecs, _maxRegInterval,
                                    _procName, _procInstance,
                                    _hostName, _userName,
                                    status);

    // send buffer to procmap

    _send(msg.getBuf(), _registerId);

  }

  ////////////////////////////////
  // unregister

  public void unregister() {
	
    // get time
	
    long now = TimeManager.getTime();
    int nowSecs = (int) (now / 1000);
    int startSecs = (int) (_startTime / 1000);
	
    // load up message
	
    PmuMessage msg = new PmuMessage(_pid, nowSecs,
                                    startSecs, _maxRegInterval,
                                    _procName, _procInstance,
                                    _hostName, _userName,
                                    "");

    // send buffer to procmap

    _send(msg.getBuf(), _unregisterId);

  }

  ///////////////////////////////
  // put string into byte buffer

  private void _putString(String text, int startIndex, ByteBuffer bbuf)

  {

    byte[] textBytes = text.getBytes();
    int index = startIndex;
    for (int ii = 0; ii < text.length(); ii++, index++) {
      if (index < bbuf.capacity()) {
        bbuf.put(index, textBytes[ii]);
      }
    }

  }
	
  ///////////////////////////////
  // send the buffer procmap

  private int _send(ByteBuffer bbuf, int messageType)

  {
	
    int iret = 0;

    // open socket to procmap

    SocketChannel channel;
    try {
      channel = SocketChannel.open();
      channel.connect(new InetSocketAddress("localhost", _procmapPort));
    } catch (UnknownHostException e) {
      return -1;
    } catch (IOException e) {
      channel = null;
      return -1;
    }

    // write SKU header

    if (_writeHeader(channel, messageType,
                     bbuf.capacity(), _messSeqNo) != 0) {
      System.err.println("ERROR - ProcmapReg._send");
      System.err.println("  Writing SKU header");
      iret = -1;
    }	
    _messSeqNo++;
	
    // rewind buffer, ready for sending
	
    bbuf.rewind();
	
    // write buffer
	
    try {
      channel.write(bbuf);
    } catch (IOException e) {
      System.err.println("ERROR - ProcmapReg._send");
      System.err.println("  Writing byte buffer");
      iret = -1;
    }

    // read reply

    if (_readReply(channel) != 0) {
      System.err.println("ERROR - ProcmapReg._send");
      System.err.println("  Reading reply");
      iret = -1;
    }
	
    // close channel

    try {
      channel.close();
    } catch (IOException e) {
      iret = -1;
    }

    return iret;

  }
	
  ///////////////////////////////
  // write the header for a message

  private int _writeHeader(SocketChannel channel,
                           int messageType,
                           int bufferCapacity,
                           int seqNo)

  {

    int headerSize = 20;
    ByteBuffer header = ByteBuffer.allocate(headerSize);
    IntBuffer iHeader = header.asIntBuffer();
    iHeader.put(0, 0xf0f0f0f0);
    iHeader.put(1, 0xf0f0f0f0);
    iHeader.put(2, messageType);
    iHeader.put(3, bufferCapacity);
    iHeader.put(4, seqNo);
	
    try {
      channel.write(header);
    } catch (IOException e) {
      return -1;
    }

    return 0;

  }
	
  ///////////////////////////////
  // read the header for reply message
    
  private int _readReplyHeader(SocketChannel channel)
	
  {

    int headerSize = 20;
    ByteBuffer header = ByteBuffer.allocate(headerSize);
	
    try {
      channel.read(header);
    } catch (IOException e) {
      return -1;
    }
	
    header.rewind();
    IntBuffer iHeader = header.asIntBuffer();

    _replyMessageType = iHeader.get(2);
    _replyLen = iHeader.get(3);

    return 0;

  }
	
  ///////////////////////////////
  // read the reply message
    
  private int _readReply(SocketChannel channel)
	
  {

    int iret = 0;

    // read reply header
	
    if (_readReplyHeader(channel) != 0) {
      System.err.println("ERROR - ProcmapReg._send");
      System.err.println("  Reading reply header");
      iret = -1;
    }

	
    ByteBuffer bbuf = ByteBuffer.allocate(_replyLen);
    bbuf.rewind();

    try {
      channel.read(bbuf);
    } catch (IOException e) {
      return -1;
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

  // find PID if possible
  
  private static int _findPid() {

    String pidstg = null;

    try {
      Process proc = Runtime.getRuntime().exec(
              new String[] {"bash", "-c", "echo $PPID"});
      InputStream istm = proc.getInputStream();
      int buflen = 1000;
      byte[] bbuf = new byte[ buflen];
      int curlen = 0;
      while (true) {
        int readlen = istm.read( bbuf, curlen, buflen - curlen);
        if (readlen < 0) break;
        curlen += readlen;
      }
      istm.close();
      
      int rc = 0;
      try { rc = proc.waitFor(); }
      catch( InterruptedException exc) {}
      if (rc != 0) _throwerr("getPid: bad rc: " + rc);
      
      pidstg = new String( bbuf, 0, curlen).trim();
    }
    catch (IOException exc) {
      pidstg = null;
    }
    
    int pid = -1;
    if (pidstg != null) {
       try {pid = Integer.parseInt( pidstg); }
      catch( NumberFormatException exc) {}
    }
    return pid;
  } // end getPid
  
  
  private static void _throwerr(String msg) throws IOException {
    throw new IOException(msg);
  }
  
  /////////////////
  // INNER CLASSES

  // PmuMessage

  private class PmuMessage {

	
    // String lengths
	
    private static final int _nbytesInt = 32;
    private static final int _nameMax = 64;
    private static final int _instanceMax = 64;
    private static final int _hostMax = 64;
    private static final int _userMax = 32;
    private static final int _statusMax = 256;
    private static final int _messageSize = 512;

    // buffer

    private ByteBuffer _buf = null;

    // constructor, loads up internal buffer
	
    public PmuMessage(int pid,
                      int nowSecs,
                      int startSecs,
                      int maxRegInterval,
                      String procName,
                      String procInstance,
                      String hostName,
                      String userName,
                      String status) {
	    
      // allocate and initialize buffer
	    
      _buf = ByteBuffer.allocate(_messageSize);
      for (int ii = 0; ii < _buf.capacity(); ii++) {
        _buf.put(ii, (byte) 0);
      }
	    
      // put integer values into buffer
	    
      IntBuffer ibuf = _buf.asIntBuffer();
      ibuf.put(0, pid);
      ibuf.put(1, nowSecs);
      ibuf.put(2, startSecs);
      ibuf.put(3, maxRegInterval);
      ibuf.put(4, 0);
	    
      // put strings into buffer
	    
      int pos = _nbytesInt;
      _putString(procName, pos, _buf);
      pos += _nameMax;
      _putString(procInstance, pos, _buf);
      pos += _instanceMax;
      _putString(hostName, pos, _buf);
      pos += _hostMax;
      _putString(userName, pos, _buf);
      pos += _userMax;
      _putString(status, pos, _buf);
	    
    }

    // get buffer

    public ByteBuffer getBuf() {
      return _buf;
    }
	
    ///////////////////////////////
    // put string into byte buffer
	
    private void _putString(String text, int startIndex, ByteBuffer bbuf) {  
      byte[] textBytes = text.getBytes();
      int index = startIndex;
      for (int ii = 0; ii < text.length(); ii++, index++) {
        if (index < bbuf.capacity()) {
          bbuf.put(index, textBytes[ii]);
        }
      }
    }
	
  } // class PmuMessage

}

