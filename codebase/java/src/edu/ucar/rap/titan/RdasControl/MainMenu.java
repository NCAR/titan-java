///////////////////////////////////////////////////////////////////////
//
// MainMenu
//
// Main menu for RdasControl
//
// Mike Dixon
//
// August 2002
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import edu.ucar.rap.jrp.*;

class MainMenu

{

    private final int ITEM_PLAIN = 0;	// Item types
    private final int ITEM_CHECK = 1;
    private final int ITEM_RADIO = 2;

    // members
    
    private RdasControl _app = null;
    private JFrame _frame = null;
    private JPanel _topPanel;
    private JMenuBar _menuBar = null;
    private Parameters _params = Parameters.getInstance();
    private ControlFrame _controlFrame = ControlFrame.getInstance();
    private AScope _aScope = null;
    private PpiDisplay _ppiDisplay = null;
    private BscanDisplay _bscanDisplay = null;
    private StatusMessageDisplay _smessageDisplay = null;
    private Calibrate _calibrate = null;
    private Logging _logging = null;
    private RdasComms _rdasComms = RdasComms.getInstance();
    
    public MainMenu(RdasControl app,
		    JFrame frame,
		    JPanel topPanel)
    {

	// save context

	_app = app;
	_frame = frame;
	_topPanel = topPanel;
	
	// Create the main menu bar
	
	_menuBar = new JMenuBar();
	
	// Set this instance as the application's menu bar

	_frame.setJMenuBar(_menuBar);
	
	// Create a new toolbar
	
 	JToolBar toolBar = new JToolBar();
	toolBar.setFloatable(false);
	_topPanel.add(toolBar, BorderLayout.NORTH);
	
	// Create the file & configuration menu, add exit to it
	
	JMenu fileMenu = _params.createMenu("File");
	// fileMenu.setMnemonic('F');
	fileMenu.setToolTipText("Configuration menu");
	
	JMenuItem exit = fileMenu.add(new ExitAction("Exit"));
	// exit.setMnemonic('x');
	exit.setToolTipText("Exit the program");
	
	// Create the control menu

	JMenu controlMenu = new JMenu("Control");
	// controlMenu.setMnemonic('C');
	controlMenu.setToolTipText("Control panel");
	populateControlMenu(toolBar, controlMenu);
	
	// Create the display menu

	JMenu displayMenu = new JMenu("Displays");
	// displayMenu.setMnemonic('D');
	displayMenu.setToolTipText("Open displays");
	populateDisplayMenu(toolBar, displayMenu);
	
	// Create the calibrate menu
	// has to occur after display menu because the AScope object
	// is needed
	
	JMenu calMenu = new JMenu("Calibrate");
	// calMenu.setMnemonic('a');
	calMenu.setToolTipText("Perform calibration");
	populateCalMenu(toolBar, calMenu);
	
	// Create the comms menu
	
	JMenu commsMenu = new JMenu("Comms");
	// commsMenu.setMnemonic('o');
	commsMenu.setToolTipText("Connect to RDAS or relay");
	populateCommsMenu(toolBar, commsMenu);
	
	// Create the logging menu
	// has to occur after display menu because the AScope object
	// is needed
	
	JMenu loggingMenu = new JMenu("Logging");
	loggingMenu.setToolTipText("Perform logging");
	populateLoggingMenu(toolBar, loggingMenu);
	
	_menuBar.add(fileMenu);
	_menuBar.add(controlMenu);
	_menuBar.add(displayMenu);
	_menuBar.add(calMenu);
	_menuBar.add(commsMenu);
	_menuBar.add(loggingMenu);

	// create toolbar
	
  	JButton openTool = toolBar.add(_params.getFileOpenAction());
  	// openTool.setMnemonic('O');
  	openTool.setToolTipText("Open an existing config");
	
  	JButton editTool = toolBar.add(_params.getEditAction());
  	// editTool.setMnemonic('E');
  	editTool.setToolTipText("Edit the parameters");
	
  	JButton saveTool = toolBar.add(_params.getFileSaveAction());
  	// saveTool.setMnemonic('S');
  	saveTool.setToolTipText("Save this config");
	
    }

    private void populateEditMenu(JMenu menu)
    {
	
	// Create setup menu options
	
	JMenuItem cut = menu.add(new CutAction("Cut", "cut.gif"));
	// cut.setMnemonic('t');
	cut.setToolTipText("Cut data to the clipboard");

	JMenuItem copy =
	    menu.add(new CopyAction("Copy", "copy.gif"));
	// copy.setMnemonic('C');
	copy.setToolTipText("Copy data to the clipboard");
	
	JMenuItem paste =
	    menu.add(new PasteAction("Paste", "paste.gif"));
	// paste.setMnemonic('P');
	paste.setToolTipText("Paste data from the clipboard");

    }

    public void populateControlMenu(JToolBar toolBar, JMenu menu)
    {

	// control panel

	TrDummyComponent dc = new TrDummyComponent();
	Image controlFrameImage = JrpImageLoad.getFromRes
	    (dc, "/edu/ucar/rap/titan/RdasControl/images/gauge.png");
	ImageIcon controlFrameIcon = new ImageIcon(controlFrameImage);
	AbstractAction controlFrameAction =
	    new ControlFrameAction("Control panel", controlFrameIcon);
	JMenuItem controlFrameItem = menu.add(controlFrameAction);
	controlFrameItem.setToolTipText("Open control panel");
	if (_params.control.startVisible.getValue()) {
	    _controlFrame.setVisible(true);
	}
	
    }
    
    public void populateDisplayMenu(JToolBar toolBar, JMenu menu)
    {
	
	// aScope
	
	_aScope = new AScope();
	AbstractAction aScopeAction = new AScopeAction("A-scope", null);
	JMenuItem aScopeItem = menu.add(aScopeAction);
	aScopeItem.setToolTipText("Open A Scope");
	if (_params.aScope.startVisible.getValue()) {
	    _aScope.setVisible(true);
	}
	
	
	// ppiDisplay
	
	_ppiDisplay = new PpiDisplay();
	AbstractAction ppiDisplayAction =
	    new PpiDisplayAction("PPI Display", null);
	JMenuItem ppiDisplayItem = menu.add(ppiDisplayAction);
	ppiDisplayItem.setToolTipText("Open PPI Display");
	if (_params.ppi.startVisible.getValue()) {
	    _ppiDisplay.setVisible(true);
	}
	
	// bscanDisplay
	
	_bscanDisplay = new BscanDisplay();
	AbstractAction bscanDisplayAction =
	    new BscanDisplayAction("BSCAN Display", null);
	JMenuItem bscanDisplayItem = menu.add(bscanDisplayAction);
	bscanDisplayItem.setToolTipText("Open BSCAN Display");
	if (_params.bscan.startVisible.getValue()) {
	    _bscanDisplay.setVisible(true);
	}
	
	// statusMessageDisplay
	
	_smessageDisplay = new StatusMessageDisplay();
	AbstractAction smessageDisplayAction =
	    new SmessageDisplayAction("STATUS MESSAGE Display", null);
	JMenuItem smessageDisplayItem = menu.add(smessageDisplayAction);
	smessageDisplayItem.setToolTipText("Open STATUS MESSAGE Display");
	if (_params.smessage.startVisible.getValue()) {
	    _smessageDisplay.setVisible(true);
	}
	
    }
    
    public void populateCalMenu(JToolBar toolBar, JMenu menu)
    {

	// calibrate
	
	_calibrate = new Calibrate(_aScope);
	AbstractAction calibAction =
	    new CalibrateAction("Calibration", null);
	JMenuItem calibItem = menu.add(calibAction);
	calibItem.setToolTipText("Open Calibration Display");
	if (_params.calib.startVisible.getValue()) {
	    _calibrate.setVisible(true);
	}
	RdasComms.getInstance().setCalib(_calibrate);
	
    }
    
    public void populateCommsMenu(JToolBar toolBar, JMenu menu)
    {

	// communication with RDAS or relay

	// connect

	AbstractAction connectAction = new ConnectAction("Connect", null);
	JMenuItem connectItem = menu.add(connectAction);
	connectItem.setToolTipText("Connect to RDAS or relay");

	// disconnect

	AbstractAction disconnectAction =
	    new DisconnectAction("Disconnect", null);
	JMenuItem disconnectItem = menu.add(disconnectAction);
	disconnectItem.setToolTipText("Disconnect from RDAS or relay");

	// get status

	AbstractAction connectStatusAction =
	    new ConnectionStatusAction("Status", null);
	JMenuItem connectStatusItem = menu.add(connectStatusAction);
	connectStatusItem.setToolTipText("Show connection status");
	
	// edit button
	
	TrDummyComponent dc = new TrDummyComponent();
	Image editImage = JrpImageLoad.getFromRes
	    (dc, "/edu/ucar/rap/jrp/images/edit_small.png");
	ImageIcon editIcon = new ImageIcon(editImage);
	CommsEditAction editAction = new CommsEditAction("Edit", editIcon);
  	JMenuItem editItem = menu.add(editAction);
  	editItem.setToolTipText("Edit the comms parameters");

    }
    
    public void populateLoggingMenu(JToolBar toolBar, JMenu menu) {
	
	// logging
	
	_logging = new Logging();
	AbstractAction loggingAction =
	    new LoggingAction("Logging", null);
	JMenuItem loggingItem = menu.add(loggingAction);
	loggingItem.setToolTipText("Open logging display");
	if (_params.logging.startVisible.getValue()) {
	    _logging.setVisible(true);
	}
	
    }
    
    ////////////////////////////////////////////////////
    // Inner classes for actions
    ////////////////////////////////////////////////////

    // exit action
    private class ExitAction extends AbstractAction
    {
	public ExitAction(String name) {
	    super(name, null);
	}
	public void actionPerformed(ActionEvent event) {
	    _app.doExit(true);
	}
    }

    // controlFrame action
    public class ControlFrameAction extends AbstractAction
    {
	public ControlFrameAction(String label, ImageIcon icon)	{
	    super(label, icon);
	}
	public void actionPerformed(ActionEvent event) {
	    _controlFrame.setVisible(true);
	}
    }

    // aScope action
    public class AScopeAction extends AbstractAction
    {
	public AScopeAction(String label, ImageIcon icon) {
	    super(label, icon);
	}
	public void actionPerformed(ActionEvent event) {
	    _aScope.setVisible(true);
	}
    }
    
    // ppiDisplay action
    public class PpiDisplayAction extends AbstractAction
    {
	public PpiDisplayAction(String label, ImageIcon icon) {
	    super(label, icon);
	}
	public void actionPerformed(ActionEvent event) {
	    _ppiDisplay.setVisible(true);
	}
    }

    // bscanDisplay action
    public class BscanDisplayAction extends AbstractAction
    {
	public BscanDisplayAction(String label, ImageIcon icon) {
	    super(label, icon);
	}
	public void actionPerformed(ActionEvent event) {
	    _bscanDisplay.setVisible(true);
	}
    }

    // statusMessageDisplay action
    public class SmessageDisplayAction extends AbstractAction
    {
	public SmessageDisplayAction(String label, ImageIcon icon) {
	    super(label, icon);
	}
	public void actionPerformed(ActionEvent event) {
	    _smessageDisplay.setVisible(true);
	}
    }

    // calib action
    public class CalibrateAction extends AbstractAction
    {
	public CalibrateAction(String label, ImageIcon icon) {
	    super(label, icon);
	}
	public void actionPerformed(ActionEvent event) {
	    _calibrate.setVisible(true);
	}
    }

    // logging action
    public class LoggingAction extends AbstractAction
    {
	public LoggingAction(String label, ImageIcon icon) {
	    super(label, icon);
	}
	public void actionPerformed(ActionEvent event) {
	    _logging.setVisible(true);
	}
    }

    // copy action
    public class CopyAction extends AbstractAction
    {
	public CopyAction(String label, String icon_file) {
	    super(label, new ImageIcon(icon_file));
	}
	public void actionPerformed(ActionEvent event) {
	    System.out.println(event);
	}
    }

    // cut action
    public class CutAction extends AbstractAction
    {
	public CutAction(String label, String icon_file) {
	    super(label, new ImageIcon(icon_file));
	}
	public void actionPerformed(ActionEvent event) {
	    System.out.println(event);
	}
    }

    // paste action
    private class PasteAction extends AbstractAction
    {
	public PasteAction(String label, String icon_file) {
	    super(label, new ImageIcon(icon_file));
	}
	public void actionPerformed(ActionEvent event) {
	    System.out.println(event);
	}
    }

    // connect action
    public class ConnectAction extends AbstractAction
    {
	public ConnectAction(String label, ImageIcon icon) {
	    super(label, icon);
	}
	public void actionPerformed(ActionEvent event) {
	    _rdasComms.setConnect(true);
	}
    }
    
    // disconnect action
    public class DisconnectAction extends AbstractAction
    {
	public DisconnectAction(String label, ImageIcon icon) {
	    super(label, icon);
	}
	public void actionPerformed(ActionEvent event) {
	    _rdasComms.setConnect(false);
	}
    }

    // connect status
    public class ConnectionStatusAction extends AbstractAction
    {
	public ConnectionStatusAction(String label, ImageIcon icon) {
	    super(label, icon);
	}
	public void actionPerformed(ActionEvent event) {
	    String status = RdasComms.getInstance().getConnectionStatus();
	    if (status.indexOf("ERROR") != -1) {
		// FIX LATER	       
		//  		JOptionPane.showMessageDialog
		//  		    (_topPanel, status, "Connection status",
		//  		     JOptionPane.ERROR_MESSAGE);
		JOptionPane.showMessageDialog
		    (_topPanel, status);
	    } else {
		//  		JOptionPane.showMessageDialog
		//  		    (_topPanel, status, "Connection status",
		//  		     JOptionPane.INFORMATION_MESSAGE);
		JOptionPane.showMessageDialog
		    (_topPanel, status);
	    }
	}
    }
    
    // comms edit action

    private class CommsEditAction extends AbstractAction
    {
	public CommsEditAction(String label, ImageIcon icon)
	{
	    super(label, icon);
	}
	public void actionPerformed(ActionEvent event)
	{
	    _params.rdasComm.getCollectionFrame().setVisible(true);
	}
    }
    
    // get methods

    public ControlFrame getControlFrame() {
	return _controlFrame;
    }

    public AScope getAScope() {
	return _aScope;
    }

    public PpiDisplay getPpiDisplay() {
	return _ppiDisplay;
    }

    public BscanDisplay getBscanDisplay() {
	return _bscanDisplay;
    }

    public StatusMessageDisplay getStatusMessageDisplay() {
	return _smessageDisplay;
    }

    public Calibrate getCalibrate() {
	return _calibrate;
    }

    public Logging getLogging() {
	return _logging;
    }

}

