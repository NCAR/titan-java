///////////////////////////////////////////////////////////////////////
//
// AnalogParams
//
// Parameters for the group of 8 flag channels
//
// Mike Dixon
//
// Feb 2004
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import edu.ucar.rap.jrp.*;

class AnalogParams extends CollectionParameter
	    
{

    // API for analog channels - array of AnalogChannelParams

    public AnalogChannelParams channels[] =
	new AnalogChannelParams[AnalogGroup.nChannels];

    // constructor

    public AnalogParams(String name, String label, int depth)
    {
	
	super(name, label, depth);
	
	// set up and add the param objects

	for (int i = 0; i < AnalogGroup.nChannels; i++) {
	    
	    String nameStr = "analog_" + i;
	    String labelStr = "Analog " + i;
	    channels[i] =
		new AnalogChannelParams(nameStr, labelStr, getNextDepth());
	    channels[i].setDescription("Parameters for analog channel " + i);
	    add(channels[i]);
	    
	}

	// copy the values to the defaults

	setDefaultFromValue();

    }

}


