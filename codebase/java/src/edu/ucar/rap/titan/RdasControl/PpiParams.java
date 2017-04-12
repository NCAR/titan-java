///////////////////////////////////////////////////////////////////////
//
// PpiParams
//
// Mike Dixon
//
// Jan 2004
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import edu.ucar.rap.jrp.*;

class PpiParams extends CollectionParameter
    
{
    
    public DoubleParameter maxRange;
    public DoubleParameter rangeRingSpacing;
    public OptionParameter startupField;

    public IntegerParameter topMargin;
    public IntegerParameter bottomMargin;
    public IntegerParameter leftMargin;
    public IntegerParameter rightMargin;

    public IntegerParameter colorScaleWidth;
    public IntegerParameter colorScaleTopMargin;
    public IntegerParameter colorScaleBottomMargin;
    public IntegerParameter colorScaleLeftMargin;
    public IntegerParameter colorScaleRightMargin;

    public IntegerParameter repaintMsecs;
    public IntegerParameter clickTextDuration;
    public IntegerParameter clickIconSize;
    public IntegerParameter beamQueueSize;

    public IntegerParameter xx;
    public IntegerParameter yy;
    public IntegerParameter width;
    public IntegerParameter height;
    public BooleanParameter startVisible;
    
    // constructor
    
    public PpiParams(String name, String label, int depth)
    {
	
	super(name, label, depth);
	
	// initialize the parameter objects

	// max range
	
	maxRange = new DoubleParameter("maxRange");
	maxRange.setLabel("Max range");
	maxRange.setDescription("Max range to be displayed - km");
	maxRange.setValue(200.0);

	// range ring spacing
	
	rangeRingSpacing = new DoubleParameter("rangeRingSpacing");
	rangeRingSpacing.setLabel("Range ring spacing");
	rangeRingSpacing.setDescription("Spacing of range rings (km)");
	rangeRingSpacing.setValue(25.0);

	// startup field

	startupField = new OptionParameter("startupField");
	startupField.setLabel("Field at startup");
	startupField.setDescription("Field to be displayed at startup");
	String options[] = {DataField.DBZ.toString(), DataField.SNR.toString()};
	startupField.setOptions(options);
 	startupField.setValue("DBZ");

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
	leftMargin.setValue(50);

	// right margin
	
	rightMargin = new IntegerParameter("rightMargin");
	rightMargin.setLabel("Right margin");
	rightMargin.setDescription("Right margin (pixels)");
	rightMargin.setValue(20);

	// color scale width
	
	colorScaleWidth = new IntegerParameter("colorScaleWidth");
	colorScaleWidth.setLabel("Color scale width");
	colorScaleWidth.setDescription("Color scale width (pixels)");
	colorScaleWidth.setValue(60);

	// color scale top margin
	
	colorScaleTopMargin = new IntegerParameter("colorScaleTopMargin");
	colorScaleTopMargin.setLabel("Color scale top margin");
	colorScaleTopMargin.setDescription("Color scale top margin (pixels)");
	colorScaleTopMargin.setValue(10);

	// color scale bottom margin
	
	colorScaleBottomMargin = new IntegerParameter("colorScaleBottomMargin");
	colorScaleBottomMargin.setLabel("Color scale bottom margin");
	colorScaleBottomMargin.setDescription("Color scale bottom margin (pixels)");
	colorScaleBottomMargin.setValue(10);

	// color scale left margin
	
	colorScaleLeftMargin = new IntegerParameter("colorScaleLeftMargin");
	colorScaleLeftMargin.setLabel("Color scale left margin");
	colorScaleLeftMargin.setDescription("Color scale left margin (pixels)");
	colorScaleLeftMargin.setValue(5);

	// color scale right margin
	
	colorScaleRightMargin = new IntegerParameter("colorScaleRightMargin");
	colorScaleRightMargin.setLabel("Color scale right margin");
	colorScaleRightMargin.setDescription("Color scale right margin (pixels)");
	colorScaleRightMargin.setValue(15);

	// repaint interval
	
	repaintMsecs = new IntegerParameter("repaintMsecs");
	repaintMsecs.setLabel("Repaint interval");
	repaintMsecs.setDescription("Repaint interval (microsecs)");
	repaintMsecs.setInfo("The image is drawn to the screen at this interval. The lower this number the more CPU will be used.");
	repaintMsecs.setValue(1000);

	// duration of text after clicking in the image
	
	clickTextDuration = new IntegerParameter("clickTextDuration");
	clickTextDuration.setLabel("Click text duration");
	clickTextDuration.setDescription("Duration of click data label (secs)");
	clickTextDuration.setInfo("After clicking in the image, the position and data value will be printed in the bottom margin. This is the number of seconds the text will remain there before being aged off.");
	clickTextDuration.setValue(60);

	// click icon size
	
	clickIconSize = new IntegerParameter("clickIconSize");
	clickIconSize.setLabel("Click icon size");
	clickIconSize.setDescription("Size of icon showing click position");
	clickIconSize.setInfo("When you click in the image, to obtain the position and data value, a cross is drawn to show the click point. This is the size of each limb of the cross. For example, if it is set to 5, the cross will be from -5 to +5 in each direction around the click point.");
	clickIconSize.setValue(5);

	// size of beam queue
	
	beamQueueSize = new IntegerParameter("beamQueueSize");
	beamQueueSize.setLabel("Size of beam queue");
	beamQueueSize.setDescription("Number of beams saved for redrawing on zoom.");
	beamQueueSize.setInfo("A number of beams are saved so that the image can be redrawn when a zoom occurs. This should be set to the typical number of beams in a PPI.");
	beamQueueSize.setValue(400);

	// WINDOW PARAMETERS

	// x offset from parent
	
	xx = new IntegerParameter("xx");
	xx.setLabel("X offset");
	xx.setDescription("The X offset from the parent");
	xx.setInfo("This the the X offset, in pixels, from the" +
			" main frame of the application.");
	xx.setValue(300);
	xx.setHidden(true);

	// y offset from parent
	
	yy = new IntegerParameter("yy");
	yy.setLabel("Y offset");
	yy.setDescription("The Y offset from the parent");
	yy.setInfo("This the the Y offset, in pixels, from the" +
			" main frame of the application.");
	yy.setValue(300);
	yy.setHidden(true);

	// window width in pixels
	
	width = new IntegerParameter("width");
	width.setLabel("Width");
	width.setDescription("The window width in pixels");
	width.setValue(800);
	width.setHidden(true);

	// window height in pixels
	
	height = new IntegerParameter("height");
	height.setLabel("Height");
	height.setDescription("The window height in pixels");
	height.setValue(800);
	height.setHidden(true);

	// start with window visible

	startVisible = new BooleanParameter("startVisible");
	startVisible.setLabel("Start visible");
	startVisible.setDescription("Start with the window visible?");
	startVisible.setInfo("If true, the program will start with the " +
			     "PPI visible. If not, it will " +
			     "be hidden at startup");
	startVisible.setValue(false);
	startVisible.setHidden(true);

	// add the parameters to the list
	
	add(maxRange);
	add(rangeRingSpacing);
	add(startupField);

	add(topMargin);
	add(bottomMargin);
	add(leftMargin);
	add(rightMargin);

	add(colorScaleWidth);
	add(colorScaleTopMargin);
	add(colorScaleBottomMargin);
	add(colorScaleLeftMargin);
	add(colorScaleRightMargin);

	add(repaintMsecs);
	add(clickTextDuration);
	add(clickIconSize);
	add(beamQueueSize);

	add(xx);
	add(yy);
	add(width);
	add(height);
	add(startVisible);

	// copy the values to the defaults

	setDefaultFromValue();

    }

}
