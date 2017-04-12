///////////////////////////////////////////////////////////////////////
//
// StatusDataHandler
//
// Handle status data coming from the Dow driver.
// These are StatusData objects.
//
// Mike Dixon
//
// May 2013
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.DowControl;

import java.util.*;
import javax.swing.*;

public class StatusDataHandler extends Thread {
    
  private Parameters _params = Parameters.getInstance();
  private MessageQueue _statusQueue = StatusQueue.getInstance();
  private ArrayList<StatusDataListener> _statusListeners = 
    new ArrayList<StatusDataListener>();

  /**
   * Single instance for this class - it is a singleton
   */
  
  private static final StatusDataHandler _instance = new StatusDataHandler();
  
  /**
   * get singleton instance
   */
  
  public static StatusDataHandler getInstance() {
    return _instance;
  }
  
  /**
   * private constructor
   */
  
  private StatusDataHandler() {
  }
  
  /**
   * add a listener for response when status arrives
   */
  
  public void addListener(StatusDataListener listener) {
    _statusListeners.add(listener);
  }
  
  // Reader reads messages from the radar
  
  public void run() {
    
    while (true) {
      
      // get a message from the read queue
      
      StatusData status = (StatusData) _statusQueue.pop();
      
      if (status == null) {
        // sleep a bit
        Thread t = Thread.currentThread();
        try { t.sleep(10); }
        catch (InterruptedException e) { return; }
        continue;
      }

      // call all listeners
      
      for (Iterator ii = _statusListeners.iterator(); ii.hasNext();) {
        StatusDataListener listener = (StatusDataListener) ii.next();
        listener.handleStatus(status);
      }
      
    } // while
    
  } // run()
  
}


