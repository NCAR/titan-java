///////////////////////////////////////////////////////////////////////
//
// Parameters - singleton
//
// Mike Dixon
//
// April 2007
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.Rvp8Control;

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
  public SiteParams site;
  public ReceiveParams receive;
  public TransmitParams transmit;
  public MomentsParams moments;
  public SinglePolFieldParams singlePolFields;
  public DualPolFieldParams dualPolFields;
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
	
    // site params
    
    site = new SiteParams("site", "Site params", getNextDepth());
    site.setDescription("Params for the site");
    
    // receive parameters
    
    receive = new ReceiveParams("receive", "Receive params", getNextDepth());
    receive.setDescription("RVP8 receive parameters");
    receive.setInfo("These parameters control the RVP8 IFD" +
                    " and DSP. They set up the gate characteristics." +
                    " These apply to both active and passive modes.");
    
    // transmit parameters
	
    transmit = new TransmitParams("transmit",
                                  "Transmit params", getNextDepth());
    transmit.setDescription("RVP8 transmit mode parameters");
    transmit.setInfo("These parameters control the transmit modes" +
                     " set up by the RVP8 in active mode." +
                     " These do not apply in passive mode.");
    
    // moments parameters
	
    moments = new MomentsParams("moments", "Moments params", getNextDepth());
    moments.setDescription("Edit the moments generation parameters");
    moments.setInfo("These parameters control the way the RVP8 computes" +
                    " moments. They affect the moments computed on the RVP8" +
                    " itself. They DO NOT AFFECT moments computed from the" +
                    " time series data.");
    
    // singlePolFields parameters
    
    singlePolFields = new SinglePolFieldParams("singlePolFields",
                                               "Single Pol Fields",
                                               getNextDepth());
    singlePolFields.setDescription
      ("Select the single pol fields  to be computed on the RVP8.");
    singlePolFields.setInfo(" They DO NOT AFFECT moments computed from the" +
                            " time series data.");
    
    // dualPolFields parameters
    
    dualPolFields = new DualPolFieldParams("dualPolFields",
                                           "Dual Pol Fields",
                                           getNextDepth());
    dualPolFields.setDescription
      ("Select the dual pol fields  to be computed on the RVP8.");
    dualPolFields.setInfo(" They DO NOT AFFECT moments computed from the" +
                          " time series data.");
    
    // RVP8 driver communication parameters
	
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
    add(site);
    add(receive);
    add(transmit);
    add(moments);
    add(singlePolFields);
    add(dualPolFields);
    add(comms);
    add(mainWindow);
    add(control);
    add(status);
    add(JrpViewParameters.getInstance());
	
    // activate the frame for the gui

    activateFrame();

  }

}

