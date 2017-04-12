///////////////////////////////////////////////////////////////////////
//
// DacChannelParams
//
// Parameters for a single dac channel
//
// Mike Dixon
//
// Feb 2004
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import edu.ucar.rap.jrp.*;

class DacChannelParams extends CollectionParameter
			       
{
    
    public StringParameter labelStr;
    public IntegerParameter minValue;
    public IntegerParameter maxValue;
    public IntegerParameter valueAtStartup;
    public IntegerParameter dataDelay;
    public BooleanParameter active;
    
    // constructor

    public DacChannelParams(String name, String label, int depth)
    {
	
	super(name, label, depth);
	
	// channel label
	
	labelStr = new StringParameter("labelStr");
	labelStr.setLabel("Channel label");
	labelStr.setDescription("Label for this dac channel");
	labelStr.setInfo("This label will appear in the GUI above " +
			 "the slider for this channel.");
	labelStr.setValue("not-set");
	
	// Min value for slider
	
	minValue = new IntegerParameter("minValue");
	minValue.setLabel("Min value");
	minValue.setDescription("Minimum value for slider");
 	minValue.setValue(0);

	// Max value for slider
	
	maxValue = new IntegerParameter("maxValue");
	maxValue.setLabel("Max value");
	maxValue.setDescription("Maximum value for slider");
 	maxValue.setValue(4095);

	// What is the value for this channel at startup?

	valueAtStartup = new IntegerParameter("valueAtStartup");
	valueAtStartup.setLabel("ValueAtStartup");
	valueAtStartup.setDescription("Value for DAC at startup.");
 	valueAtStartup.setValue(2048);

	// What is the value for this channel at startup?

	dataDelay = new IntegerParameter("dataDelay");
	dataDelay.setLabel("Delay between saving data value (msecs)");
	dataDelay.setDescription("Slider value will be saved at this interval.");
 	dataDelay.setValue(200);
	
	// is this channel active?

	active = new BooleanParameter("active");
	active.setLabel("Channel active?");
	active.setDescription("Is this dac channel active?");
	active.setInfo("The user can configure which daces are active. " +
		       "Only the active channels will be shown.");
 	active.setValue(false);
	
	// add the parameters to the list
	
	add(labelStr);
	add(valueAtStartup);
	add(dataDelay);
	add(active);

	// copy the values to the defaults
	
	setDefaultFromValue();

    }

}


