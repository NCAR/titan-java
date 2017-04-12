///////////////////////////////////////////////////////////////////////
//
// SwitchParams
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

class SwitchParams extends CollectionParameter
	    
{

    // API for switches - array of SwitchChannelParams

    public SwitchChannelParams channels[] =
	new SwitchChannelParams[SwitchGroup.nChannels];

    // constructor

    public SwitchParams(String name, String label, int depth)
    {
	
	super(name, label, depth);
	
	// set up and add the param objects

	for (int i = 0; i < SwitchGroup.nChannels; i++) {
	    
	    String nameStr = "switch_" + i;
	    String labelStr = "Switch " + i;
	    channels[i] = new SwitchChannelParams(nameStr, labelStr, getNextDepth());
	    channels[i].setDescription("Parameters for switch channel " + i);
	    add(channels[i]);
	    
	}

	// copy the values to the defaults

	setDefaultFromValue();

    }

}


