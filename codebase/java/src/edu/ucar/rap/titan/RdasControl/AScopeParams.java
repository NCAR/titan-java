///////////////////////////////////////////////////////////////////////
//
// AScopeParams
//
// Entry panel for control panel parameters
//
// Mike Dixon
//
// Oct 2002
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import edu.ucar.rap.jrp.*;
import java.awt.Color;

class AScopeParams extends CollectionParameter
	    
{

    public ColorParameter dbzColor;

    public OptionParameter startupField;

    public DoubleParameter minDbz;
    public DoubleParameter maxDbz;
    public DoubleParameter minSnr;
    public DoubleParameter maxSnr;
    public IntegerParameter maxCount; 
    
    public BooleanParameter plotCount;
    public BooleanParameter plotDbzSnr;
    public BooleanParameter plotBeamRate;

    public BooleanParameter performSampling;
    public IntegerParameter sampleCenter;
    public IntegerParameter sampleNGates;
    public IntegerParameter sampleNBeams;

    public BooleanParameter fastPaint;
    public IntegerParameter repaintMsecs;
    public IntegerParameter nFreeze;

    public IntegerParameter topMargin;
    public IntegerParameter bottomMargin;
    public IntegerParameter leftMargin;
    public IntegerParameter rightMargin;

    public IntegerParameter xx;
    public IntegerParameter yy;
    public IntegerParameter width;
    public IntegerParameter height;
    public BooleanParameter startVisible;

    // constructor
    
    public AScopeParams(String name, String label, int depth)
    {
	
	super(name, label, depth);
	
	// initialize the parameter objects
    
	// color of DBZ line

	dbzColor = new ColorParameter("dbzColor");
	dbzColor.setLabel("dBZ plot color");
	dbzColor.setDescription("Color of dBZ line on plot.");
 	dbzColor.setValue(Color.BLACK);
	dbzColor.setHidden(true);

	// startup field

	startupField = new OptionParameter("startupField");
	startupField.setLabel("Field at startup");
	startupField.setDescription("Field to be displayed at startup");
	String options[] = {DataField.DBZ.toString(), DataField.SNR.toString()};
	startupField.setOptions(options);
 	startupField.setValue("DBZ");

	// min Dbz
	
	minDbz = new DoubleParameter("minDbz");
	minDbz.setLabel("Min dbz");
	minDbz.setDescription("Min dbz to be displayed - Y axis");
	minDbz.setValue(-20);

	// max Dbz
	
	maxDbz = new DoubleParameter("maxDbz");
	maxDbz.setLabel("Max dbz");
	maxDbz.setDescription("Max dbz to be displayed - Y axis");
	maxDbz.setValue(80);

	// min Snr
	
	minSnr = new DoubleParameter("minSnr");
	minSnr.setLabel("Min snr");
	minSnr.setDescription("Min snr to be displayed - Y axis");
	minSnr.setValue(0);

	// max Snr
	
	maxSnr = new DoubleParameter("maxSnr");
	maxSnr.setLabel("Max snr");
	maxSnr.setDescription("Max snr to be displayed - Y axis");
	maxSnr.setValue(120);

	// max Count
	
	maxCount = new IntegerParameter("maxCount");
	maxCount.setLabel("Max count");
	maxCount.setDescription("Max count plotted on AScope");
	maxCount.setValue(16384);

	// plot count
	
	plotCount = new BooleanParameter("plotCount");
	plotCount.setLabel("Plot count?");
	plotCount.setDescription("Option to plot count values");
	plotCount.setValue(true);

	// plot dbz
	
	plotDbzSnr = new BooleanParameter("plotDbzSnr");
	plotDbzSnr.setLabel("Plot dbz/snr?");
	plotDbzSnr.setDescription("Option to plot DBZ/SNR values");
	plotDbzSnr.setValue(true);

	// perform sampling
	
	performSampling = new BooleanParameter("performSampling");
	performSampling.setLabel("Perform sampling?");
	performSampling.setDescription("Option to sample count and dBZ data.");
	performSampling.setInfo("You have the option to sample the values" +
				" over a selected number of gates.");
	performSampling.setValue(false);

	// plot beamRate
	
	plotBeamRate = new BooleanParameter("plotBeamRate");
	plotBeamRate.setLabel("Plot beam rate?");
	plotBeamRate.setDescription("Option to plot beam rates.");
	plotBeamRate.setInfo("You have the option to plot the incoming" +
			     " beam rate and the displayed beam rate.");
	plotBeamRate.setValue(false);

	// center of sampled location
	
	sampleCenter = new IntegerParameter("sampleCenter");
	sampleCenter.setLabel("Sample center");
	sampleCenter.setDescription
	    ("Location of the center of the sampled region (gates).");
	sampleCenter.setValue(320);

	// number of gates in sample
	
	sampleNGates = new IntegerParameter("sampleNGates");
	sampleNGates.setLabel("Sample N Gates");
	sampleNGates.setDescription("Number of gates in sample.");
	sampleNGates.setInfo("This is the width of the sample in gates");
	sampleNGates.setValue(11);

	// number of beams in sample
	
	sampleNBeams = new IntegerParameter("sampleNBeams");
	sampleNBeams.setLabel("Sample N Beams");
	sampleNBeams.setDescription("Number of beams for averaging sample.");
	sampleNBeams.setInfo("This is the number of beams used to compute the average sample");
	sampleNBeams.setValue(30);

	// fast paint option
	
	fastPaint = new BooleanParameter("fastPaint");
	fastPaint.setLabel("Fast paint");
	fastPaint.setDescription("Force fast paint");
	fastPaint.setInfo
	    ("Normally the repaint method will be used for redrawing the AScope. " +
	     "Setting this option to true forces draws directly to the graphics " +
	     " object which speeds up painting.");
	fastPaint.setValue(true);
	fastPaint.setHidden(true);

	// repaint delay in millisecs
	
	repaintMsecs = new IntegerParameter("repaintMsecs");
	repaintMsecs.setLabel("Repaint msecs");
	repaintMsecs.setDescription("Delay before repaint - millisecs.");
	repaintMsecs.setInfo
	    ("This is the max delay before repainting after a beam " +
	     "arrives. Set to a low value for fast painting. " +
	     " This will also increase the CPU usage");
	repaintMsecs.setValue(50);
	// repaintMsecs.setHidden(true);

	// max size of freeze queue
	
	nFreeze = new IntegerParameter("nFreeze");
	nFreeze.setLabel("N freeze");
	nFreeze.setDescription("Number of freeze data bemas.");
	nFreeze.setInfo("You can freeze the AScope to look at past data." +
			" This is the number of beams in the freeze queue");
 	nFreeze.setValue(360);

	// MARGINS

	// top margin
	
	topMargin = new IntegerParameter("topMargin");
	topMargin.setLabel("Top margin");
	topMargin.setDescription("Top margin (pixels)");
	topMargin.setValue(30);

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
	rightMargin.setValue(50);

	// WINDOW PARAMETERS

	// x offset from parent
	
	xx = new IntegerParameter("xx");
	xx.setLabel("X offset");
	xx.setDescription("The X offset from the parent");
	xx.setInfo("This the the X offset, in pixels, from the" +
			" main frame of the application.");
	xx.setValue(0);
	xx.setHidden(true);

	// y offset from parent
	
	yy = new IntegerParameter("yy");
	yy.setLabel("Y offset");
	yy.setDescription("The Y offset from the parent");
	yy.setInfo("This the the Y offset, in pixels, from the" +
			" main frame of the application.");
	yy.setValue(510);
	yy.setHidden(true);

	// window width in pixels
	
	width = new IntegerParameter("width");
	width.setLabel("Width");
	width.setDescription("The window width in pixels");
	width.setValue(1149);
	width.setHidden(true);

	// window height in pixels
	
	height = new IntegerParameter("height");
	height.setLabel("Height");
	height.setDescription("The window height in pixels");
	height.setValue(443);
	height.setHidden(true);

	// start with window visible

	startVisible = new BooleanParameter("startVisible");
	startVisible.setLabel("Start visible");
	startVisible.setDescription("Start with the window visible?");
	startVisible.setInfo("If true, the program will start with the " +
			     "A-Scope visible. If not, it will " +
			     "be hidden at startup");
	startVisible.setValue(false);
	startVisible.setHidden(true);

	// add the parameters to the list
	
	add(dbzColor);
	add(startupField);
	add(minDbz);
	add(maxDbz);
	add(minSnr);
	add(maxSnr);
	add(maxCount);
	add(plotCount);
	add(plotDbzSnr);
	add(performSampling);
	add(plotBeamRate);
	add(sampleCenter);
	add(sampleNGates);
	add(sampleNBeams);
	add(fastPaint);
	add(repaintMsecs);
	add(nFreeze);

	add(topMargin);
	add(bottomMargin);
	add(leftMargin);
	add(rightMargin);

	add(xx);
	add(yy);
	add(width);
	add(height);
	add(startVisible);

	// copy the values to the defaults

	setDefaultFromValue();

    }

}


