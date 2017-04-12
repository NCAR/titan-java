///////////////////////////////////////////////////////////////////////
//
// ControlPanelParams
//
// Entry panel for control panel parameters
//
// Mike Dixon
//
// May 2013
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RapidDowControl;

import edu.ucar.rap.jrp.*;

class ControlPanelParams extends CollectionParameter
	    
{

  public TextParameter drxConfig;
  public FloatParameter fontSize;
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
	
    // digital receiver configuration

    drxConfig = new TextParameter("drxConfig");
    drxConfig.setLabel("DRX-config");
    drxConfig.setDescription("List of available config files");
    drxConfig.setInfo("<pre>Edit this list to give the user a choice of configurations.\n" +
                      "\n" +
                      "# - start comment lines start with '#'\n" +
                      "\n" +
                      "Each line entry should be formatted as follows:\n" +
                      "\n" +
                      "  label: config_file_name\n" +
                      "\n");
    drxConfig.setValue("# ========================\n" +
                       "# DRX configuration\n" +
                       "# ========================\n" +
                       "Test sample: dowdrx.text\n" +
                       "Short pulse: dowdrx.short_pulse\n" +
                       "Long pulse: dowdrx.long_pulse\n");
    
    // font size
    
    fontSize = new FloatParameter("fontSize");
    fontSize.setLabel("Font size");
    fontSize.setDescription("Font size for pos and time");
    fontSize.setInfo("This is the font size for the control panel.");
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
                         "Control Panel visible. If not, it will " +
                         "be hidden at startup");
    startVisible.setValue(false);
    startVisible.setHidden(true);

    // add the parameters to the list
	
    add(drxConfig);
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
