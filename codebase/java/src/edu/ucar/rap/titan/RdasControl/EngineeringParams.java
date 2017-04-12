///////////////////////////////////////////////////////////////////////
//
// EngineeringParams
//
// Entry panel for engineering parameters
//
// Mike Dixon
//
// Sept 2003
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import edu.ucar.rap.jrp.*;

class EngineeringParams extends CollectionParameter
	    
{

    public IntegerParameter maxCount; 
    public IntegerParameter autoResetTimeout;

    public DoubleParameter elevTolerance;
    public DoubleParameter ppiAzOverlap;
    public DoubleParameter antennaElCorr;
    public DoubleParameter antennaAzCorr;
    public DoubleParameter controlElCorr;
    public DoubleParameter controlAzCorr;
    public DoubleParameter antennaMinElev;
    public DoubleParameter antennaMaxElev;
    public DoubleParameter antennaMaxElSlewRate;
    public DoubleParameter antennaMaxAzSlewRate;

    public BooleanParameter elVoltagePositive;
    public BooleanParameter azVoltagePositive;
    
    // constructor

    public EngineeringParams(String name, String label, int depth)
    {

	super(name, label, depth);

	// initialize the parameter objects
    
	// max Count
	
	maxCount = new IntegerParameter("maxCount");
	maxCount.setLabel("Max count");
	maxCount.setDescription("Max count possible from RDAS");
	maxCount.setValue(16384);

	// timeout for auto-reset
	
	autoResetTimeout = new IntegerParameter("autoResetTimeout");
	autoResetTimeout.setLabel("Auto-reset timeout");
	autoResetTimeout.setDescription("Timeout (s) to trigger auto-reset.");
	autoResetTimeout.setInfo("Sometimes the system will lock up and require resetting. If the system locks up for more than this timeout values, in secs, a reset will be triggered.");
	autoResetTimeout.setValue(10);

	// elevation tolerance
	
	elevTolerance = new DoubleParameter("elevTolerance");
	elevTolerance.setLabel("Elev tolerance");
	elevTolerance.setDescription("Elevation control tolerance (deg).");
	elevTolerance.setInfo("The elevation angle must be withing this tolerance of the theoretical value before a PPI can start.");
	elevTolerance.setValue(0.3);

	// azimuth overlap for full PPI
	
	ppiAzOverlap = new DoubleParameter("ppiAzOverlap");
	ppiAzOverlap.setLabel("PPI azimuth overlap");
	ppiAzOverlap.setDescription("Azimuth overlap for full PPI (deg)");
	ppiAzOverlap.setInfo("In order to complete a full PPI, some overlap is required to avoid missing beams. This is the overlap which must be reached before a PPI will complete.");
	ppiAzOverlap.setValue(10.0);

	// elevation antenna correction
	
	antennaElCorr = new DoubleParameter("antennaElCorr");
	antennaElCorr.setLabel("Antenna El Correction");
	antennaElCorr.setDescription("Correction for pedestal antenna position " +
				     "in elevation (decimal deg)");
	antennaElCorr.setInfo("This value is added to the pedestal readout " +
			      "to correct the elevation value.");
	antennaElCorr.setValue(0.0);

	// azimuth antenna correction
	
	antennaAzCorr = new DoubleParameter("antennaAzCorr");
	antennaAzCorr.setLabel("Antenna Az Correction");
	antennaAzCorr.setDescription("Correction for pedestal antenna position " +
				     "in azimuth (decimal deg)");
	antennaAzCorr.setInfo("This value is added to the pedestal readout " +
			      "to correct the azimuth value.");
	antennaAzCorr.setValue(0.0);
	
	// elevation control correction
	
	controlElCorr = new DoubleParameter("controlElCorr");
	controlElCorr.setLabel("Control El Correction");
	controlElCorr.setDescription("Correction for driving antenna " +
				     "in elevation (decimal deg)");
	controlElCorr.setInfo("If the antenna elevation readout does not " +
			      "match the intended value, correct it by " +
			      "inserting a correction value here.");
	controlElCorr.setValue(0.0);

	// azimuth control correction
	
	controlAzCorr = new DoubleParameter("controlAzCorr");
	controlAzCorr.setLabel("Control Az Correction");
	controlAzCorr.setDescription("Correction for driving antenna " +
				     "in azimuth (decimal deg)");
	controlAzCorr.setInfo("If the antenna azimuth readout does not " +
			      "match the intended value, correct it by " +
			      "inserting a correction value here.");
	controlAzCorr.setValue(0.0);
	
	// antenna min and max elevation
	
	antennaMinElev = new DoubleParameter("antennaMinElev");
	antennaMinElev.setLabel("Antenna min el");
	antennaMinElev.setDescription("Min antenna elevation (deg)");
	antennaMinElev.setInfo
	    ("The antenna cannot go below this elevation." +
	     " Therefore the control program will not request elevation" +
	     " angles below this value.");
	antennaMinElev.setValue(0.0);
	
	antennaMaxElev = new DoubleParameter("antennaMaxElev");
	antennaMaxElev.setLabel("Antenna max el");
	antennaMaxElev.setDescription("Max antenna elevation (deg)");
	antennaMaxElev.setInfo
	    ("The antenna cannot go above this elevation." +
	     " Therefore the control program will not request elevation" +
	     " angles above this value.");
	antennaMaxElev.setValue(45.0);
	
	// antenna max slew rates
	
	antennaMaxElSlewRate = new DoubleParameter("antennaMaxElSlewRate");
	antennaMaxElSlewRate.setLabel("Antenna max el slew rate");
	antennaMaxElSlewRate.setDescription("Max el slew rate (deg/s)");
	antennaMaxElSlewRate.setInfo
	    ("The antenna cannot slew faster than this." +
	     " Therefore the control program will not request slew" +
	     " rates above this value.");
	antennaMaxElSlewRate.setValue(10.0);
	
	antennaMaxAzSlewRate = new DoubleParameter("antennaMaxAzSlewRate");
	antennaMaxAzSlewRate.setLabel("Antenna max az slew rate");
	antennaMaxAzSlewRate.setDescription("Max az slew rate (deg/s)");
	antennaMaxAzSlewRate.setInfo
	    ("The antenna cannot slew faster than this." +
	     " Therefore the control program will not request slew" +
	     " rates above this value.");
	antennaMaxAzSlewRate.setValue(24.0);

	// control voltages

	elVoltagePositive = new BooleanParameter("elVoltagePositive");
	elVoltagePositive.setLabel("El voltage positive?");
	elVoltagePositive.setDescription("Is the el control voltage sense positive?");
	elVoltagePositive.setInfo("If the el control voltage sense is negative, " +
				  "set this to false.");
 	elVoltagePositive.setValue(true);

	azVoltagePositive = new BooleanParameter("azVoltagePositive");
	azVoltagePositive.setLabel("Az voltage positive?");
	azVoltagePositive.setDescription("Is the az control voltage sense positive?");
	azVoltagePositive.setInfo("If the az control voltage sense is negative, " +
				  "set this to false.");
 	azVoltagePositive.setValue(true);

	// add the parameters to the list
	
	add(maxCount);
	add(autoResetTimeout);
	add(elevTolerance);
	add(ppiAzOverlap);
	add(antennaElCorr);
	add(antennaAzCorr);
	add(controlElCorr);
	add(controlAzCorr);
	add(antennaMinElev);
	add(antennaMaxElev);
	add(antennaMaxElSlewRate);
	add(antennaMaxAzSlewRate);
	add(elVoltagePositive);
	add(azVoltagePositive);

	// copy the values to the defaults

	setDefaultFromValue();

    }

}


