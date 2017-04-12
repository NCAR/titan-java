///////////////////////////////////////////////////////////////////////
//
// DataQueue
//
// Synchronized queue for data messages - singleton
//
// Mike Dixon
//
// Feb 2004
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.util.*;

public class DataQueue extends MessageQueue
    
{
    
    /**
     * Single instance for this class - it is a singleton
     */
    
    private static final DataQueue _instance = new DataQueue();
    
    private Parameters _params = Parameters.getInstance();

    /**
     * get singleton instance
     */
    
    public static DataQueue getInstance() {
	return _instance;
    }
    
    /**
     * private constructor
     */
    
    private DataQueue() {
	setMaxSize(_params.rdasComm.maxDataQueueSize.getValue());
    }

}
