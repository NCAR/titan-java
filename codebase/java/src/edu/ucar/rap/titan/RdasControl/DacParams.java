///////////////////////////////////////////////////////////////////////
//
// DacParams
//
// Parameters for the group of 2 DAC channels
//
// Mike Dixon
//
// April 2004
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import edu.ucar.rap.jrp.*;

class DacParams extends CollectionParameter
			
{

    // API for switches - array of DacChannelParams
    
    public DacChannelParams channels[] = new DacChannelParams[DacGroup.nChannels];
    
    // preferred width for the slider object
    
    public IntegerParameter preferredWidth;
    
    // constructor
    
    public DacParams(String name, String label, int depth)
    {
	
	super(name, label, depth);
	
	// set up and add the param objects
	
	for (int i = 0; i < DacGroup.nChannels; i++) {
	    String nameStr = "dac_" + i;
	    String labelStr = "Dac " + i;
	    channels[i] = new DacChannelParams(nameStr, labelStr, getNextDepth());
	    channels[i].setDescription("Parameters for dac channel " + i);
	    add(channels[i]);
	}
	
	// Preferred width for slider in pixels
	
	preferredWidth = new IntegerParameter("preferredWidth");
	preferredWidth.setLabel("PreferredWidth");
	preferredWidth.setDescription("Set width for slider in pixels");
	preferredWidth.setInfo("Used to manage window layout.");
 	preferredWidth.setValue(250);
	add(preferredWidth);
	
	// copy the values to the defaults
	
	setDefaultFromValue();
	
    }

}


