///////////////////////////////////////////////////////////////////////
//
// StatusQueue
//
// Synchronized queue for data messages - singleton
//
// Mike Dixon
//
// May 2013
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.DowControl;

import java.util.*;

public class StatusQueue extends MessageQueue
    
{
    
  /**
   * Single instance for this class - it is a singleton
   */
    
  private static final StatusQueue _instance = new StatusQueue();
    
  private Parameters _params = Parameters.getInstance();

  /**
   * get singleton instance
   */
  
  public static StatusQueue getInstance() {
    return _instance;
  }
    
  /**
   * private constructor
   */
    
  private StatusQueue() {
    setMaxSize(100);
  }

}
