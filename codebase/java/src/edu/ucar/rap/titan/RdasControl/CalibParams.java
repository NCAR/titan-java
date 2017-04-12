///////////////////////////////////////////////////////////////////////
//
// CalibParams
//
// Calibration parameters
//
// Mike Dixon
//
// Jan 2003
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import edu.ucar.rap.jrp.*;

public class CalibParams extends CollectionParameter
	    
{

    public StringParameter fileFolder;
    public StringParameter filePath;

    public DoubleParameter peakPower;
    public DoubleParameter pulseWidth;
    
    public DoubleParameter waveguideLoss;
    public DoubleParameter radomeLoss;
    public DoubleParameter receiverLoss;
    public DoubleParameter testCableAtten;
    public DoubleParameter couplerAtten;

    public IntegerParameter nPointsTable;
    public DoubleParameter minDbz;
    public DoubleParameter deltaDbz;
    public DoubleParameter fitDbzLow;
    public DoubleParameter fitDbzHigh;
    public DoubleParameter calRange;

    public BooleanParameter roundSiggen;
    public DoubleParameter siggenRoundValue;

    public DoubleParameter slope;
    public DoubleParameter offset1km;
    public DoubleParameter offsetCalRng;

    public IntegerParameter topMargin;
    public IntegerParameter bottomMargin;
    public IntegerParameter leftMargin;
    public IntegerParameter rightMargin;

    public IntegerParameter xx;
    public IntegerParameter yy;
    public IntegerParameter width;
    public IntegerParameter height;
    public IntegerParameter tableRowHt;
    public BooleanParameter startVisible;

    public FileParameter testPath;

    // constructor

    public CalibParams(String name, String label, int depth)
    {
	super(name, label, depth);

	// calibration file folder
	
	fileFolder = new StringParameter("fileFolder");
	fileFolder.setLabel("Calibration file folder");
	fileFolder.setDescription("Folder in which to save and retrieve calibration files");
	fileFolder.setValue("./calib");
	
	// calibration file path
	
	filePath = new StringParameter("filePath");
	filePath.setLabel("Calibration file path");
	filePath.setDescription("Current calibration file");
	filePath.setValue("./calib/test");
	
	// peak transmitter power
	
	peakPower = new DoubleParameter("peakPower");
	peakPower.setLabel("Peak power (dBm)");
	peakPower.setDescription("Peak transmitter power (dBm)");
	peakPower.setValue(84.0);

	// pulse length
	
	pulseWidth = new DoubleParameter("pulseWidth");
	pulseWidth.setLabel("Pulse width");
	pulseWidth.setDescription("Pulse width (us)");
	pulseWidth.setValue(2.0);

	// one way waveguide loss
	
	waveguideLoss = new DoubleParameter("waveguideLoss");
	waveguideLoss.setLabel("Waveguide loss");
	waveguideLoss.setDescription("One-way waveguide loss (dB)");
	waveguideLoss.setInfo("Typical loss values:\n" +
			      "<p>  C-band: 1.75 dB per 100 ft / 30 m\n" +
			      "<p>  S-band: 0.75 dB per 100 ft / 30 m\n");
	waveguideLoss.setValue(0.5);
	
	// two-way radome loss
	
	radomeLoss = new DoubleParameter("radomeLoss");
	radomeLoss.setLabel("Radome loss");
	radomeLoss.setDescription("Two-way radome loss (dB)");
	radomeLoss.setInfo("If no radome set to 0.");
	radomeLoss.setValue(0.5);
	
	// receiver bandwidth loss
	
	receiverLoss = new DoubleParameter("receiverLoss");
	receiverLoss.setLabel("Receiver loss");
	receiverLoss.setDescription("Receiver bandwidth loss (dB)");
	receiverLoss.setInfo("Loss due to the effective bandpass filter "
			     + "associated with the receiver (dB)");
	receiverLoss.setValue(0.0);
	
	// test cable attenuation
	
	testCableAtten = new DoubleParameter("testCableAtten");
	testCableAtten.setLabel("Test cable attenuation");
	testCableAtten.setDescription("Test cable attenuation (dB)");
	testCableAtten.setInfo("Antenna gain in dB");
	testCableAtten.setInfo("Typical values:\n" +
			       "<p>C-band: 0.6 dB/ft with RG58 or similar\n" +
			       "<p>S-band: 0.4 dB/ft with RG58 or similar\n");
	testCableAtten.setValue(3.8);
	
	// coupler attenuation
	
	couplerAtten = new DoubleParameter("couplerAtten");
	couplerAtten.setLabel("Coupler attenuation");
	couplerAtten.setDescription("Coupler attenuation (dB)");
	couplerAtten.setInfo("If the signal generator input is connected " +
			     "directly to the receiver set this to 0");
	couplerAtten.setValue(30.75);

	// sampling points
	
	nPointsTable = new IntegerParameter("nPointsTable");
	nPointsTable.setLabel("N Points Table");
	nPointsTable.setDescription("Number of points in calibration table");
	nPointsTable.setInfo("This is the number of available points in the " +
			     "table. Not all of the points are necessarily used.");
	nPointsTable.setValue(15);

	// min and delta dBZ
	
	minDbz = new DoubleParameter("minDbz");
	minDbz.setLabel("Min dBZ");
	minDbz.setDescription("Minimim dBZ for sampling");
	minDbz.setInfo("This the min dBZ value in the calibration table");
	minDbz.setValue(0);

	deltaDbz = new DoubleParameter("deltaDbz");
	deltaDbz.setLabel("Delta dBZ");
	deltaDbz.setDescription("dBZ step for calib table");
	deltaDbz.setInfo("This the dBZ step in the calibration table");
	deltaDbz.setValue(5);
	
	// low and high dBZ for linear fit
	
	fitDbzLow = new DoubleParameter("fitDbzLow");
	fitDbzLow.setLabel("Fit dBZ low");
	fitDbzLow.setDescription("Low dBZ limit for linear fit");
	fitDbzLow.setInfo("The linear fit is computed for points which " +
			  "lie between the low and high dBZ limits");
	fitDbzLow.setValue(5);
	
	fitDbzHigh = new DoubleParameter("fitDbzHigh");
	fitDbzHigh.setLabel("Fit dBZ high");
	fitDbzHigh.setDescription("High dBZ limit for linear fit");
	fitDbzHigh.setInfo("The fit is computed for points which " +
			   "lie between the low and high dBZ limits");
	fitDbzHigh.setValue(65);
	
	calRange = new DoubleParameter("calRange");
	calRange.setLabel("Calib range (km)");
	calRange.setDescription("Range for calibration test pulse (km)");
	calRange.setInfo("The is the effective range for the test pulse. " +
			 "Final calibration is computed for 1km");
	calRange.setValue(100);
	
	// rounding of siggen values

	roundSiggen = new BooleanParameter("roundSiggen");
	roundSiggen.setLabel("Round siggen?");
	roundSiggen.setDescription("Option to round siggen values in table.");
	roundSiggen.setDescription("If true, the siggen values will be " +
				   "rounded to the nearest 'siggenRoundValue'. " +
				   "Other columns in the table will be adjusted " +
				   "accordingly.");
	roundSiggen.setValue(true);
	
	siggenRoundValue = new DoubleParameter("siggenRoundValue");
	siggenRoundValue.setLabel("Siggen round value");
	siggenRoundValue.setDescription("Val to which siggen vals are rounded");
	siggenRoundValue.setInfo("See roundSiggen");
	siggenRoundValue.setValue(5.0);

	// slope and offset of cal line
	
	slope = new DoubleParameter("slope");
	slope.setLabel("Slope");
	slope.setDescription("Slope of calibration line");
	slope.setValue(100.0);
	// slope.setHidden(true);

	offset1km = new DoubleParameter("offset1km");
	offset1km.setLabel("Offset-1km");
	offset1km.setDescription("Offset (in counts) for dBZ at 1km");
	offset1km.setValue(7000.0);
	// offset1km.setHidden(true);

	offsetCalRng = new DoubleParameter("offsetCalRng");
	offsetCalRng.setLabel("Offset at cal range");
	offsetCalRng.setDescription("Offset (in counts) for dBZ at calib range");
	offsetCalRng.setValue(1000.0);
	// offsetcalRng.setHidden(true);

	// top margin
	
	topMargin = new IntegerParameter("topMargin");
	topMargin.setLabel("Top margin");
	topMargin.setDescription("Top margin (pixels)");
	topMargin.setValue(5);

	// bottom margin
	
	bottomMargin = new IntegerParameter("bottomMargin");
	bottomMargin.setLabel("Bottom margin");
	bottomMargin.setDescription("Bottom margin (pixels)");
	bottomMargin.setValue(30);

	// left margin
	
	leftMargin = new IntegerParameter("leftMargin");
	leftMargin.setLabel("Left margin");
	leftMargin.setDescription("Left margin (pixels)");
	leftMargin.setValue(45);

	// right margin
	
	rightMargin = new IntegerParameter("rightMargin");
	rightMargin.setLabel("Right margin");
	rightMargin.setDescription("Right margin (pixels)");
	rightMargin.setValue(20);

	// Window size and position

	// x offset from parent
	
	xx = new IntegerParameter("xx");
	xx.setLabel("X offset");
	xx.setDescription("The X offset from the parent");
	xx.setInfo("This the the X offset, in pixels, from the " +
		   "main frame of the application.");
	xx.setValue(780);
	xx.setHidden(true);

	// y offset from parent
	
	yy = new IntegerParameter("yy");
	yy.setLabel("Y offset");
	yy.setDescription("The Y offset from the parent");
	yy.setInfo("This the the Y offset, in pixels, from the " +
		   "main frame of the application.");
	yy.setValue(0);
	yy.setHidden(true);

	// window width in pixels
	
	width = new IntegerParameter("width");
	width.setLabel("Width");
	width.setDescription("The window width in pixels");
	width.setValue(557);
	width.setHidden(true);

	// window height in pixels
	
	height = new IntegerParameter("height");
	height.setLabel("Height");
	height.setDescription("The window height in pixels");
	height.setValue(769);
	height.setHidden(true);

	// table height in pixels
	
	tableRowHt = new IntegerParameter("tableRowHt");
	tableRowHt.setLabel("TableRowHeight");
	tableRowHt.setDescription("Height of a table row in pixels");
	tableRowHt.setInfo("Adjust so that the table fits nicely into " +
			   "the viewport and just does not scroll.");
	tableRowHt.setValue(16);
	tableRowHt.setHidden(true);
	
	// start with window visible

	startVisible = new BooleanParameter("startVisible");
	startVisible.setLabel("Start visible");
	startVisible.setDescription("Start with the window visible?");
	startVisible.setInfo("If true, the program will start with the " +
			     "PPI visible. If not, it will " +
			     "be hidden at startup");
	startVisible.setValue(false);
	startVisible.setHidden(true);

	// test path

	testPath = new FileParameter("testPath");
	testPath.setLabel("Test path");
	testPath.setDescription("Path for test file");
	testPath.setValue("unknown");
	testPath.setHidden(true);

	// add the parameters to the list
	
	add(fileFolder);
	add(filePath);

	add(peakPower);
	add(pulseWidth);

	add(waveguideLoss);
	add(radomeLoss);
	add(receiverLoss);
	add(testCableAtten);
	add(couplerAtten);

	add(nPointsTable);
	add(minDbz);
	add(deltaDbz);
	add(fitDbzLow);
	add(fitDbzHigh);
	add(calRange);

	add(roundSiggen);
	add(siggenRoundValue);

	add(slope);
	add(offset1km);
	add(offsetCalRng);

	add(topMargin);
	add(bottomMargin);
	add(leftMargin);
	add(rightMargin);

	add(xx);
	add(yy);
	add(width);
	add(height);
	add(tableRowHt);
	add(startVisible);

	add(testPath);

	// copy the values to the defaults

	setDefaultFromValue();

    }

}
