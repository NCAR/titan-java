///////////////////////////////////////////////////////////////////////
//
// ControlPanelParams
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

class ControlPanelParams extends CollectionParameter
	    
{

  public OptionParameter startOpMode;
  public OptionParameter shutdownOpMode;
  public OptionParameter startScanMode;
  public DoubleParameter startElev;
  public DoubleParameter startAz;

  public FloatParameter fontSize;
  
  public PowerStatusParams powerStatus;
  public SwitchParams switches;
  public AnalogParams analogs;
  public DacParams dacs;
  public ErrorFlagsParams errorFlags;
  public BooleanParameter showErrorFlags;
  public BooleanParameter showDacOnRight;

  public BooleanParameter allowResize;
  public IntegerParameter xx;
  public IntegerParameter yy;
  public IntegerParameter width;
  public IntegerParameter height;
  public BooleanParameter startVisible;
    
  // constructor
    
  public ControlPanelParams(String name, String label, int depth)
  {
	
    super(name, label, depth);
	
    // initialize the parameter objects

    // startup / sutdown conditions

    startOpMode = new OptionParameter("startOpMode");
    startOpMode.setLabel("Startup Op Mode");
    startOpMode.setDescription("Op mode at startup");
    {
      String options[] = {"Off", "Standby", "Operate", "Calibrate"};
      startOpMode.setOptions(options);
    }
    startOpMode.setValue("Off");

    shutdownOpMode = new OptionParameter("shutdownOpMode");
    shutdownOpMode.setLabel("Shutdown Op Mode");
    shutdownOpMode.setDescription("Op mode at shutdown.");
    shutdownOpMode.setInfo
      ("The state in which to leave the radar when RdasControl shuts down.");
    {
      String options[] = {"Off", "Standby"};
      shutdownOpMode.setOptions(options);
    }
    shutdownOpMode.setValue("Off");

    startScanMode = new OptionParameter("startScanMode");
    startScanMode.setLabel("Startup Scan Mode");
    startScanMode.setDescription("Scan mode at startup");
    {
      String options[] = {"Manual", "Auto-Volume", "Auto-PPI", "Follow-Sun", "Stop"};
      startScanMode.setOptions(options);
    }
    startScanMode.setValue("Manual");

    startElev = new DoubleParameter("startElev");
    startElev.setLabel("Start elev (deg).");
    startElev.setDescription("Startup elevations, ppi or manual mode.");
    startElev.setInfo("If the system starts up in auto-PPI or manual mode, the antenna will initially be set to this elevation angle.");
    startElev.setValue(0.0);

    startAz = new DoubleParameter("startAz");
    startAz.setLabel("Start az (deg).");
    startAz.setDescription("Startup azimuth, manual mode.");
    startAz.setInfo("If the system starts up in manual mode, the antenna will initially be set to this azimuth angle.");
    startAz.setValue(0.0);

    // font size
	
    fontSize = new FloatParameter("fontSize");
    fontSize.setLabel("Font size");
    fontSize.setDescription("Font size for pos and time");
    fontSize.setInfo("This is the font size for rendering the antenna and sun position, and the radar and clock time.");
    fontSize.setValue(14.0);

    // power status parameters
    
    powerStatus = new PowerStatusParams("PowerStatus", "Power status", getNextDepth());
    powerStatus.setDescription("Params to set up power status.");
	
    // switch parameters
	
    switches = new SwitchParams("switches", "Switch channels", getNextDepth());
    switches.setDescription("Params to set up the switches.");
	
    // analog channel parameters
	
    analogs = new AnalogParams("analogs", "Analog channels", getNextDepth());
    analogs.setDescription("Set up the analog channels");
	
    // dac channel parameters
	
    dacs = new DacParams("dacs", "Dac channels", getNextDepth());
    dacs.setDescription("Set up the dac channels");
	
    // software status parameters
    
    errorFlags = new ErrorFlagsParams("ErrorFlags",
                                      "Error flags", getNextDepth());
    errorFlags.setDescription("Params to set up error flags.");
    errorFlags.setDescription("Error flags show errors for such items as data comminucations and antenna control.");
	
    // show error flags on control panel

    showErrorFlags = new BooleanParameter("showErrorFlags");
    showErrorFlags.setLabel("Show error flags in GUI");
    showErrorFlags.setDescription("Option to show error flag status window in GUI");
    showErrorFlags.setInfo("Set to true to see error flags reported in control panel.");
    showErrorFlags.setValue(true);

    // show DAC channel control in right panel

    showDacOnRight = new BooleanParameter("showDacOnRight");
    showDacOnRight.setLabel("DAC controls on right");
    showDacOnRight.setDescription("Show Dac controls in right-hand panel.");
    showDacOnRight.setInfo("If false, DAC control will appear on the left.");
    showDacOnRight.setValue(true);

    // allow the window to be resized

    allowResize = new BooleanParameter("allowResize");
    allowResize.setLabel("Allow resize");
    allowResize.setDescription("Allow the window to be resized");
    allowResize.setInfo("Normally this will be set to false. If the " +
                        "developer makes changes this may be set to " +
                        "true while the changes are made and then back " +
                        "to false when done.");
    allowResize.setValue(false);

    // x offset from parent
	
    xx = new IntegerParameter("xx");
    xx.setLabel("X offset");
    xx.setDescription("The X offset from the parent");
    xx.setInfo("This the the X offset, in pixels, from the" +
               " main frame of the application.");
    xx.setValue(600);
    xx.setHidden(true);

    // y offset from parent
	
    yy = new IntegerParameter("yy");
    yy.setLabel("Y offset");
    yy.setDescription("The Y offset from the parent");
    yy.setInfo("This the the Y offset, in pixels, from the" +
               " main frame of the application.");
    yy.setValue(0);
    yy.setHidden(true);

    // window width in pixels
	
    width = new IntegerParameter("width");
    width.setLabel("Width");
    width.setDescription("The window width in pixels");
    width.setValue(550);
    width.setHidden(true);

    // window height in pixels
	
    height = new IntegerParameter("height");
    height.setLabel("Height");
    height.setDescription("The window height in pixels");
    height.setValue(507);
    height.setHidden(true);

    // start with window visible

    startVisible = new BooleanParameter("startVisible");
    startVisible.setLabel("Start visible");
    startVisible.setDescription("Start with the window visible?");
    startVisible.setInfo("If true, the program will start with the " +
                         "Control Panel visible. If not, it will " +
                         "be hidden at startup");
    startVisible.setValue(false);
    startVisible.setHidden(true);

    // add the parameters to the list
	
    add(startOpMode);
    add(shutdownOpMode);
    add(startScanMode);
    add(startElev);
    add(startAz);
    add(fontSize);
    add(powerStatus);
    add(switches);
    add(analogs);
    add(dacs);
    add(errorFlags);
    add(showErrorFlags);
    add(showDacOnRight);
    add(allowResize);
    add(xx);
    add(yy);
    add(width);
    add(height);
    add(startVisible);

    // copy the values to the defaults

    setDefaultFromValue();

  }

}
