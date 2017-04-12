///////////////////////////////////////////////////////////////////////
//
// AnalogChannelParams
//
// Parameters for a single analog channel
//
// Mike Dixon
//
// Feb 2004
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import edu.ucar.rap.jrp.*;

class AnalogChannelParams extends CollectionParameter
	    
{

    public StringParameter labelStr;
    public StringParameter unitsStr;
    public DoubleParameter offset;
    public DoubleParameter slope;
    public BooleanParameter warn;
    public DoubleParameter minimum;
    public DoubleParameter maximum;
    public BooleanParameter active;

    // constructor

    public AnalogChannelParams(String name, String label, int depth)
    {

	super(name, label, depth);

	// channel label

	labelStr = new StringParameter("labelStr");
	labelStr.setLabel("Channel label");
	labelStr.setDescription("Label for this channel");
	labelStr.setInfo("This label will appear in the GUI next " +
			 "to the readout for this channel.");
	labelStr.setValue("Not-set");

	// unitsStr
	
	unitsStr = new StringParameter("unitsStr");
	unitsStr.setLabel("Units");
	unitsStr.setDescription("Units for this channel");
	unitsStr.setInfo("The units string will appear in the GUI next " +
			 "to the readout for this channel.");
	unitsStr.setValue("not-set");
	
	// offset
	
	offset = new DoubleParameter("offset");
	offset.setLabel("Offset");
	offset.setDescription("Offset for computing analog value.");
	offset.setInfo("The analog channel will return a voltage between " +
		       "-10V and +10V. This real value will be computed " +
		       "as volts * slope + offset.");
	offset.setValue(0.0);

	// slope
	
	slope = new DoubleParameter("slope");
	slope.setLabel("Slope");
	slope.setDescription("Slope for computing analog value.");
	slope.setInfo("The analog channel will return a voltage between " +
		       "-10V and +10V. This real value will be computed " +
		       "as volts * slope + offset.");
	slope.setValue(1.0);

	// warn on this channel

	warn = new BooleanParameter("warn");
	warn.setLabel("Warn");
	warn.setDescription("Warn on this channel if out of limits?");
	warn.setInfo("If set, this channel will warn if the value is not between " +
		     "the minimum and maximum values.");
 	warn.setValue(false);

	// minimum
	
	minimum = new DoubleParameter("minimum");
	minimum.setLabel("Minimum");
	minimum.setDescription("Minimum valid valud for this channel.");
	minimum.setInfo("If the value goes below this, and warn is true " +
			"this channel will generate a warning");
	minimum.setValue(-10.0);

	// maximum
	
	maximum = new DoubleParameter("maximum");
	maximum.setLabel("Maximum");
	maximum.setDescription("Maximum valid valud for this channel.");
	maximum.setInfo("If the value goes above this, and warn is true " +
			"this channel will generate a warning");
	maximum.setValue(10.0);

	// is this channel active?

	active = new BooleanParameter("active");
	active.setLabel("Active");
	active.setDescription("Is this channel active?");
	active.setInfo("It is possible that not all analog channels will be active. " +
		       "This flag indicates the active status of the channel.");
 	active.setValue(false);

	// add the parameters to the list
	
	add(labelStr);
	add(unitsStr);
	add(offset);
	add(slope);
	add(warn);
	add(minimum);
	add(maximum);
	add(active);

	// copy the values to the defaults
	
	setDefaultFromValue();

    }

}


