///////////////////////////////////////////////////////////////////////
//
// Parameters - singleton
//
// Mike Dixon
//
// May 2013
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RapidDowControl;

import edu.ucar.rap.jrp.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.nio.*;
import org.jdom.*;
import org.jdom.input.SAXBuilder;

public class Parameters extends ParameterManager

{
    
  /**
   * Single instance for this class - it is a singleton
   */
    
  private static final Parameters _instance = new Parameters();

  public BooleanParameter debug;
  public BooleanParameter verbose;
  public CommsParams comms;
  public MainWindowParams mainWindow;
  public ControlPanelParams control;
  public StatusPanelParams status;
    
  /**
   * get singleton instance
   */
    
  public static Parameters getInstance() {
    return _instance;
  }

  private Parameters() {
    
    // debug?
	
    debug = new BooleanParameter("debug");
    debug.setLabel("Debugging flag");
    debug.setIgnoreChanges(true);
    debug.setValue(false);
	
    // verbose debug?
	
    verbose = new BooleanParameter("verbose");
    verbose.setLabel("Verbose flag");
    verbose.setIgnoreChanges(true);
    verbose.setValue(false);
	
    // DOW driver communication parameters
    
    comms =
      new CommsParams("comms", "Communications", getNextDepth());
    comms.setDescription("Edit the communication parameters");
    
    // main window
    
    mainWindow = new MainWindowParams("mainWindow", "Main window",
                                      getNextDepth());
    mainWindow.setDescription("Main window");
    
    // control panel
    
    control = new ControlPanelParams("control", "Control panel",
                                     getNextDepth());
    control.setDescription("Control panel");
    
    // status panel
    
    status = new StatusPanelParams("status", "Status panel",
                                   getNextDepth());
    status.setDescription("Status panel");
    
    // add the elements
	
    add(debug);
    add(verbose);
    add(mainWindow);
    add(comms);
    add(control);
    add(status);
    add(JrpViewParameters.getInstance());
	
    // activate the frame for the gui

    activateFrame();

  }

}

