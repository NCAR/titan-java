///////////////////////////////////////////////////////////////////////
//
// BeamDataHandler
//
// Handle beam data coming from the radar.
// Beams are in BeamMessage objects.
//
// Mike Dixon
//
// Feb 2003
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.util.*;
import javax.swing.*;

public class BeamDataHandler extends Thread {
    
    private Parameters _params = Parameters.getInstance();
    private MessageQueue _dataQueue = DataQueue.getInstance(); // data from radar
    private int _nBeamsForRate = 0;
    private double _beamRate = 0;
    private long _startTimeForRate = TimeManager.getTime();
    private ArrayList _beamListeners = new ArrayList();
    
    /**
     * Single instance for this class - it is a singleton
     */
    
    private static final BeamDataHandler _instance = new BeamDataHandler();

    /**
     * get singleton instance
     */
    
    public static BeamDataHandler getInstance() {
	return _instance;
    }
    
    /**
     * private constructor
     */
    
    private BeamDataHandler() {
    }

    /**
     * add a listener for response when a beam arrives
     */

    public void addListener(BeamDataListener listener) {
	_beamListeners.add(listener);
    }

    // Reader reads messages from the radar
	
    public void run() {
	
	while (true) {
	    
	    // get a message from the read queue
	    
	    Message message = (Message) _dataQueue.pop();
	    
	    if (message == null) {
		// sleep a bit
		Thread t = Thread.currentThread();
		try { t.sleep(10); }
		catch (InterruptedException e) { return; }
		continue;
	    }

	    if (message.getType() == MessageType.BEAM) {
		
		// compute incoming beam rate in beams/sec
		
		_nBeamsForRate++;
		if (_nBeamsForRate == 100) {
		    long now = TimeManager.getTime();
		    double intervalSecs =
			(now - _startTimeForRate) / 1000.0;
		    _beamRate = 100 / intervalSecs;
		    _startTimeForRate = now;
		    _nBeamsForRate = 0;
		}
		
		// calling all listeners
		
		BeamMessage beam = (BeamMessage) message;
		for (Iterator ii = _beamListeners.iterator(); ii.hasNext();) {
		    BeamDataListener listener = (BeamDataListener) ii.next();
		    listener.handleBeam(beam, _beamRate);
		}

	    }
	    
	} // while
	
    } // run()

}


