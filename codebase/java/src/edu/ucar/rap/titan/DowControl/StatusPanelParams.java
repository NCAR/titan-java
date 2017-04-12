///////////////////////////////////////////////////////////////////////
//
// StatusPanelParams
//
// Entry panel for control panel parameters
//
// Mike Dixon
//
// May 2013
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.DowControl;

import edu.ucar.rap.jrp.*;

class StatusPanelParams extends CollectionParameter
	    
{

  public DoubleParameter intervalSecs;
  public DoubleParameter minValidBurstPower;
  public DoubleParameter maxFpgaTemp;
  public DoubleParameter maxBoardTemp;
  public DoubleParameter maxFreqError;
  public DoubleParameter maxDutyCycle;
  public DoubleParameter nominalIf2FreqHz;

  public BooleanParameter showHighFreq;
  public BooleanParameter showLowFreq;

  public FloatParameter fontSize;
  public BooleanParameter allowResize;
  public IntegerParameter xx;
  public IntegerParameter yy;
  public IntegerParameter width;
  public IntegerParameter height;
  public BooleanParameter startVisible;

  // constructor
    
  public StatusPanelParams(String name, String label, int depth)
  {
	
    super(name, label, depth);
	
    // initialize the parameter objects

    // status query interval - secs

    intervalSecs = new DoubleParameter("intervalSecs");
    intervalSecs.setLabel("Status data interval (s).");
    intervalSecs.setDescription("Interval for getting status (secs).");
    intervalSecs.setInfo("A special thread is started to gather status information from the DowDriver. This is the interval, in seconds, between gathering the status data.");
    intervalSecs.setValue(1.0);

    // min valid burst power

    minValidBurstPower = new DoubleParameter("minValidBurstPower");
    minValidBurstPower.setLabel("Min valid burst power (dBm).");
    minValidBurstPower.setDescription("Min expected power from burst.");
    minValidBurstPower.setInfo("If the burst power is less than this, red text is used to indicate a problem.");
    minValidBurstPower.setValue(-15.0);

    // max temp for FPGA

    maxFpgaTemp = new DoubleParameter("maxFpgaTemp");
    maxFpgaTemp.setLabel("Max safe temperature for FPGA (C).");
    maxFpgaTemp.setDescription("Max safe temp for FPGA.");
    maxFpgaTemp.setInfo("If the temp exceeds this, red text is used to indicate a problem.");
    maxFpgaTemp.setValue(70);

    // max temp for BOARD

    maxBoardTemp = new DoubleParameter("maxBoardTemp");
    maxBoardTemp.setLabel("Max safe temperature for Pentek board (C).");
    maxBoardTemp.setDescription("Max safe temp for Pentek board.");
    maxBoardTemp.setInfo("If the temp exceeds this, red text is used to indicate a problem.");
    maxBoardTemp.setValue(70);

    // max frequency error for AFC

    maxFreqError = new DoubleParameter("maxFreqError");
    maxFreqError.setLabel("Max AFC frequency error (Hz).");
    maxFreqError.setDescription("Max AFC frequency error for reliable operations.");
    maxFreqError.setInfo("If the error exceeds this this limit, red text is used to indicate a problem.");
    maxFreqError.setValue(100000);

    // max frequency error for AFC

    maxDutyCycle = new DoubleParameter("maxDutyCycle");
    maxDutyCycle.setLabel("Max measured duty cycle (%).");
    maxDutyCycle.setDescription("Max safe duty cycle.");
    maxDutyCycle.setInfo("If the value exceeds this this limit, red text is used to indicate a problem.");
    maxDutyCycle.setValue(0.1);

    // nominal frequency for IF2

    nominalIf2FreqHz = new DoubleParameter("nominalIf2FreqHz");
    nominalIf2FreqHz.setLabel("Nominal frequency for IF2 (hz).");
    nominalIf2FreqHz.setDescription("Set this to the expected IF frequency.");
    nominalIf2FreqHz.setInfo("When tracking is operational, the measured IF should be close to this value.");
    nominalIf2FreqHz.setValue(2.5e7);

    // show the high frequency status

    showHighFreq = new BooleanParameter("showHighFreq");
    showHighFreq.setLabel("Show status for high frequency");
    showHighFreq.setDescription("For a single frequency system, this is normally true.");
    showHighFreq.setInfo("For dual frequency system, we show both the high and low frequency status in this panel. For single frequency systems, we normally only use the high frequency data stream.");
    showHighFreq.setValue(true);

    // show the low frequency status

    showLowFreq = new BooleanParameter("showLowFreq");
    showLowFreq.setLabel("Show status for low frequency");
    showLowFreq.setDescription("For a single frequency system, this is normally false.");
    showLowFreq.setInfo("For dual frequency system, we show both the low and low frequency status in this panel, so set this true. For single frequency systems, we normally only use the high frequency data stream, so set this to false.");
    showLowFreq.setValue(true);

    // font size
	
    fontSize = new FloatParameter("fontSize");
    fontSize.setLabel("Font size");
    fontSize.setDescription("Font size for pos and time");
    fontSize.setInfo("This is the font size for rendering the antenna and sun position, and the radar and clock time.");
    fontSize.setValue(14.0);

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
    width.setValue(364);
    width.setHidden(true);

    // window height in pixels

    height = new IntegerParameter("height");
    height.setLabel("Height");
    height.setDescription("The window height in pixels");
    height.setValue(566);
    height.setHidden(true);

    // start with window visible

    startVisible = new BooleanParameter("startVisible");
    startVisible.setLabel("Start visible");
    startVisible.setDescription("Start with the window visible?");
    startVisible.setInfo("If true, the program will start with the " +
                         "Status Panel visible. If not, it will " +
                         "be hidden at startup");
    startVisible.setValue(false);
    startVisible.setHidden(true);

    // add the parameters to the list
	
    add(intervalSecs);
    add(minValidBurstPower);
    add(maxFpgaTemp);
    add(maxBoardTemp);
    add(maxFreqError);
    add(maxDutyCycle);
    add(nominalIf2FreqHz);
    add(showHighFreq);
    add(showLowFreq);
    add(fontSize);
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
