///////////////////////////////////////////////////////////////////////
//
// StatusDataHandler
//
// Handle status data coming from the Rvp8 driver.
// These are StatusData objects.
//
// Mike Dixon
//
// April 2007
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.Rvp8Control;

import java.util.*;
import javax.swing.*;

public class StatusDataHandler extends Thread {
    
  private Parameters _params = Parameters.getInstance();
  private MessageQueue _statusQueue = StatusQueue.getInstance();
  private ArrayList _statusListeners = new ArrayList();

  private double _azRate = 0.0;
  private double _prevAzForRate = 0.0;
  private long _prevTimeForAzRate = TimeManager.getTime();
  
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
   * add a listener for response when a beam arrives
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

      // compute azimuth slew rate
      
      long now = TimeManager.getTime();
      double deltaSecs = (now - _prevTimeForAzRate) / 1000.0;
      if (deltaSecs > 4.0) {
        double deltaAz = status.az - _prevAzForRate;
        if (deltaAz > 180.0) {
          deltaAz -= 360.0;
        } else if (deltaAz < -180.0) {
          deltaAz += 360.0;
        }
        _azRate = deltaAz / deltaSecs;
        _prevTimeForAzRate = now;
        _prevAzForRate = status.az;
      }
      status.azRate = _azRate;
      
      // call all listeners
      
      for (Iterator ii = _statusListeners.iterator(); ii.hasNext();) {
        StatusDataListener listener = (StatusDataListener) ii.next();
        listener.handleStatus(status);
      }
      
    } // while
    
  } // run()
  
}


