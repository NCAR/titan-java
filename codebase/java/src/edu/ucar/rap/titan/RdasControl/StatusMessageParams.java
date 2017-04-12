///////////////////////////////////////////////////////////////////////
//
// StatusMessageParams
//
// Mike Dixon
//
// Jan 2004
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import edu.ucar.rap.jrp.*;

class StatusMessageParams extends CollectionParameter
    
{
    
    public IntegerParameter nLines;
    public BooleanParameter topLatest;
    public BooleanParameter printToStderr;
    
    public IntegerParameter xx;
    public IntegerParameter yy;
    public IntegerParameter width;
    public IntegerParameter height;
    public BooleanParameter startVisible;
    
    // constructor
    
    public StatusMessageParams(String name, String label, int depth)
    {
	
	super(name, label, depth);
	
	// initialize the parameter objects
	
	// number of lines
	
	nLines = new IntegerParameter("nLines");
	nLines.setLabel("N lines");
	nLines.setDescription("Number of lines in text area");
	nLines.setValue(30);

	// put latest messages at top of window

	topLatest = new BooleanParameter("topLatest");
	topLatest.setLabel("Latest at top?");
	topLatest.setDescription("Place latest messages at top of windowd");
	topLatest.setInfo("If true, latest messages appear at the top. " +
			  "If false, latest messages appear at the end of the list." +
			  "False gives the normal scrolling text window behavior.");
	topLatest.setValue(false);

	// put latest messages at top of window

	printToStderr = new BooleanParameter("printToStderr");
	printToStderr.setLabel("Print status to stderr?");
	printToStderr.setDescription("Print status messages to stderr");
	printToStderr.setInfo("If true, messages will be echoed to stderr. " +
                              "Only unique messages will be echoed." +
                              "They will only be printed when a status change occurs.");
	printToStderr.setValue(false);

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
	
	add(nLines);
	add(topLatest);
	add(printToStderr);
	add(xx);
	add(yy);
	add(width);
	add(height);
	add(startVisible);

	// copy the values to the defaults

	setDefaultFromValue();

    }

}
