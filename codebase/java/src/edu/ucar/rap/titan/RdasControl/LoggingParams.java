///////////////////////////////////////////////////////////////////////
//
// LoggingParams
//
// Logging parameters
//
// Mike Dixon
//
// Jan 2003
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import edu.ucar.rap.jrp.*;

public class LoggingParams extends CollectionParameter
	    
{
    
    public StringParameter fileFolder;
    public OptionParameter field;
    public DoubleParameter msecsBetweenBeams;
    public IntegerParameter maxNBeams;
    public DoubleParameter startRange;
    public DoubleParameter endRange;
    public BooleanParameter logTime;
    
    public IntegerParameter xx;
    public IntegerParameter yy;
    public IntegerParameter width;
    public IntegerParameter height;
    public BooleanParameter startVisible;

    // constructor

    public LoggingParams(String name, String label, int depth)
    {
	super(name, label, depth);

	// logging file folder
	
	fileFolder = new StringParameter("fileFolder");
	fileFolder.setLabel("Logging file folder");
	fileFolder.setDescription("Folder in which to save and retrieve logging files");
	fileFolder.setValue("./logging");

	// field for logging data

	field = new OptionParameter("field");
	field.setLabel("Field");
	field.setDescription("Logging field");
	String options[] = {"DBZ", "SNR", "Count"};
	field.setOptions(options);
 	field.setValue("DBZ");
	
	// time between beams for logging
	
	msecsBetweenBeams = new DoubleParameter("msecsBetweenBeams");
	msecsBetweenBeams.setLabel("Msecs between beams");
	msecsBetweenBeams.setDescription("Time between logging beams (msecs).");
	msecsBetweenBeams.setInfo("This is the number of msecs between saving out beams to the log. Set to 0 to save every beam.");
 	msecsBetweenBeams.setValue(1000);

	// max number of beams saved to a file
	
	maxNBeams = new IntegerParameter("maxNBeams");
	maxNBeams.setLabel("Max N Beams");
	maxNBeams.setDescription("Max number of beams logged per file.");
	maxNBeams.setInfo("When this number of beams has been logged to a file, the logging is stopped automatically.");
 	maxNBeams.setValue(10000);

	// start range for saved data
	
	startRange = new DoubleParameter("startRange");
	startRange.setLabel("Start range");
	startRange.setDescription("Start range for saved data (km).");
	startRange.setInfo("Only data between the start and end ranges will be logged.");
 	startRange.setValue(0.0);

	// end range for saved data
	
	endRange = new DoubleParameter("endRange");
	endRange.setLabel("End range");
	endRange.setDescription("End range for saved data (km).");
	endRange.setInfo("Only data between the start and end ranges will be logged.");
 	endRange.setValue(10.0);

	// Add time to the log line

	logTime = new BooleanParameter("logTime");
	logTime.setLabel("Log time");
	logTime.setDescription("Include time on log line?");
	logTime.setInfo("If true, year, month, day, hour, min, sec, msec are added to the start of the log line.");
	logTime.setValue(false);

	// Window size and position

	// x offset from parent
	
	xx = new IntegerParameter("xx");
	xx.setLabel("X offset");
	xx.setDescription("The X offset from the parent");
	xx.setInfo("This the the X offset, in pixels, from the " +
		   "main frame of the application.");
	xx.setValue(0);
	xx.setHidden(true);

	// y offset from parent
	
	yy = new IntegerParameter("yy");
	yy.setLabel("Y offset");
	yy.setDescription("The Y offset from the parent");
	yy.setInfo("This the the Y offset, in pixels, from the " +
		   "main frame of the application.");
	yy.setValue(510);
	yy.setHidden(true);

	// window width in pixels
	
	width = new IntegerParameter("width");
	width.setLabel("Width");
	width.setDescription("The window width in pixels");
	width.setValue(600);
	width.setHidden(true);

	// window height in pixels
	
	height = new IntegerParameter("height");
	height.setLabel("Height");
	height.setDescription("The window height in pixels");
	height.setValue(240);
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
	
	add(fileFolder);
	add(field);
	add(msecsBetweenBeams);
	add(maxNBeams);
	add(startRange);
	add(endRange);
	add(logTime);

	add(xx);
	add(yy);
	add(width);
	add(height);
	add(startVisible);

	// copy the values to the defaults

	setDefaultFromValue();

    }

}
