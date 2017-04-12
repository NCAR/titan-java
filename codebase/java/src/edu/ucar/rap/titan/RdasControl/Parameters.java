///////////////////////////////////////////////////////////////////////
//
// Parameters - singleton
//
// Mike Dixon
//
// October 2002
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

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
  public RadarParams radar;
  public ScanParams scan;
  public RdasCommParams rdasComm;
  public CalibParams calib;
  public EngineeringParams eng;
  public ControlPanelParams control;
  public AScopeParams aScope;
  public PpiParams ppi;
  public BscanParams bscan;
  public StatusMessageParams smessage;
  public LoggingParams logging;
  public WarningLogParams warningLog;
  public MainWindowParams mainWindow;
    
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
    verbose.setLabel("Verbose debugging flag");
    verbose.setIgnoreChanges(true);
    verbose.setValue(false);
	
    // radar parameters
	
    radar = new RadarParams("radar", "Radar params", getNextDepth());
    radar.setDescription("Edit the radar parameters");

    // scan parameters
	
    scan = new ScanParams("scan", "Scan strategy", getNextDepth());
    scan.setDescription("Edit the radar scan strategy");
	
    // RDAS communication parameters
	
    rdasComm = new RdasCommParams("rdasComm", "RDAS socket", getNextDepth());
    scan.setDescription("Edit the RDAS comm parameters");
	
    // calib parameters
	
    calib = new CalibParams("calib",
                            "Calibration parameters", getNextDepth());
    calib.setDescription("Edit the calibration values");
	
    // engineering parameters
	
    eng = new EngineeringParams("eng",
                                "Engineering parameters", getNextDepth());
    eng.setDescription("Edit the engineering values");
	
    // A-scope
	
    aScope = new AScopeParams("aScope", "A-Scope", getNextDepth());
    aScope.setDescription("A-scope display - for calibration");
    aScope.setInfo("Parameters for the size, location, etc." +
                   " of the A-scope");

    // Ppi display
	
    ppi = new PpiParams("ppi", "PPI Display", getNextDepth());
    ppi.setDescription("PPI Display for viewing polar radar data");
    ppi.setInfo("Parameters for the size, location, etc." +
                " of the PPI display");
	
    // Bscan display
	
    bscan = new BscanParams("bscan", "BSCAN Display", getNextDepth());
    bscan.setDescription("BSCAN Display for viewing raw radar data");
    bscan.setInfo("Parameters for the size, location, etc." +
                  " of the BSCAN display");

    // StatusMessage display
	
    smessage = new StatusMessageParams("smessage",
                                       "Statue Message Display", getNextDepth());
    smessage.setDescription("Status message display");
    smessage.setInfo("The status message display provides a scrolling pane containing the latest status messages.");

    // control panel
	
    control = new ControlPanelParams("control", "Control Panel", getNextDepth());
    control.setDescription("Radar control panel");
    control.setInfo("Parameters for the size, location, etc." +
                    " of the control panel");
    control.setHidden(false);

    // logging parameters
	
    logging = new LoggingParams("logging",
                                "Logging parameters", getNextDepth());
    logging.setDescription("Edit the logging params");
	
    // Warning log params
    
    warningLog = new WarningLogParams("warningLog",
                                      "Warning log parameters", getNextDepth());
    warningLog.setDescription("Edit the warning log params");
    
    // main window

    mainWindow = new MainWindowParams("mainWindow", "Main window",
                                      getNextDepth());
    mainWindow.setDescription("Main window");

    // add the elements
	
    add(debug);
    add(verbose);
    add(radar);
    add(scan);
    add(rdasComm);
    add(calib);
    add(eng);
    add(aScope);
    add(ppi);
    add(bscan);
    add(smessage);
    add(control);
    add(logging);
    add(warningLog);
    add(mainWindow);
    add(JrpViewParameters.getInstance());
	
    // activate the frame for the gui

    activateFrame();

  }

  /////////////////////////////////////////////////////
  // update params using buffer sent from relay server
    
  public boolean updateFromRelay(ByteBuffer buf) {
	
    if (debug.getValue()) {
      System.err.println("Setting params from relay server - start");
    }

    // save params which should not change
	
    boolean debugSave = debug.getValue();
    boolean verboseSave = verbose.getValue();
    boolean connectAtStartup = rdasComm.connectAtStartup.getValue();
    String host = rdasComm.host.getValue();
    int port = rdasComm.port.getValue();
    String relayHost = rdasComm.relayHost.getValue();
    int relayPort = rdasComm.relayPort.getValue();
    boolean viaRelay = rdasComm.viaRelay.getValue();
    double timeBetweenRelayBeams =
      rdasComm.timeBetweenRelayBeams.getValue();
    int maxDataQueueSize = rdasComm.maxDataQueueSize.getValue();
    int maxCommandQueueSize = rdasComm.maxCommandQueueSize.getValue();
    int connectDelay = rdasComm.connectDelay.getValue();
    int commandDelay = rdasComm.commandDelay.getValue();
    boolean bigEndian = rdasComm.bigEndian.getValue();

    // set rdas comms to ignore param changes
	
    RdasComms.getInstance().setIgnoreParamChangeListeners(true);

    // set params

    if (!setFromXmlBuffer(buf)) {
      System.err.println("ERROR - Parameters.updateFromRelay");
      System.err.println("  Cannot decode params from relay");
      return false;
    }

    // reset the params which should not change

    debug.setValue(debugSave);
    verbose.setValue(verboseSave);

    rdasComm.connectAtStartup.setValue(connectAtStartup);
    rdasComm.host.setValue(host);
    rdasComm.port.setValue(port);
    rdasComm.relayHost.setValue(relayHost);
    rdasComm.relayPort.setValue(relayPort);
    rdasComm.viaRelay.setValue(viaRelay);
    rdasComm.timeBetweenRelayBeams.setValue(timeBetweenRelayBeams);
    rdasComm.maxDataQueueSize.setValue(maxDataQueueSize);
    rdasComm.maxCommandQueueSize.setValue(maxCommandQueueSize);
    rdasComm.connectDelay.setValue(connectDelay);
    rdasComm.commandDelay.setValue(commandDelay);
    rdasComm.bigEndian.setValue(bigEndian);

    // change the param file path, to indicate it comes from
    // relay

    File currentParamFile = new File(getParameterFilePath());
    String relayParamName =
      new String("params."
                 + radar.siteName.getValue() + "_" +
                 rdasComm.relayHost.getValue()  + ".relay");
    File relayParamFile = new File(currentParamFile.getParent(),
                                   relayParamName);
    setParameterFilePath(relayParamFile.getAbsolutePath());
	
    // call change listeners, except for RdasComm

    radar.callChangeListenersRecursive();
    scan.callChangeListenersRecursive();
    rdasComm.callChangeListenersRecursive();
    calib.callChangeListenersRecursive();
    eng.callChangeListenersRecursive();
    control.callChangeListenersRecursive();
    logging.callChangeListenersRecursive();
    aScope.callChangeListenersRecursive();
    ppi.callChangeListenersRecursive();
    bscan.callChangeListenersRecursive();
    smessage.callChangeListenersRecursive();
       
    // set rdas comms to react to param changes
	
    RdasComms.getInstance().setIgnoreParamChangeListeners(false);

    if (debug.getValue()) {
      System.err.println("Setting params from relay server - success");
    }

    return true;

  }

}
