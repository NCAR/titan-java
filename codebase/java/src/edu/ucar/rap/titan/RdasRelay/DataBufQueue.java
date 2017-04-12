///////////////////////////////////////////////////////////////////////
//
// DataBufQueue
//
// Synchronized queue for relaying data messages - singleton
//
// Mike Dixon
//
// April 2004
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasRelay;

import edu.ucar.rap.titan.RdasControl.*;
import java.util.*;
import java.nio.*;

public class DataBufQueue extends MessageQueue
				       
{
    
    /**
     * Single instance for this class - it is a singleton
     */
    
    private static final DataBufQueue _instance = new DataBufQueue();
    
    private Parameters _params = Parameters.getInstance();

    /**
     * get singleton instance
     */
    
    public static DataBufQueue getInstance() {
	return _instance;
    }
    
    /**
     * private constructor
     */
    
    private DataBufQueue() {
	setMaxSize(100);
    }

    public void addDataBuffer(ByteBuffer buf) {
	push(buf);
    }
	    
}
