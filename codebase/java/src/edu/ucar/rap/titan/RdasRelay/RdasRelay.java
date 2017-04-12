///////////////////////////////////////////////////////////////////////
//
// RdasRelay
//
// TITAN RDAS Relay
//
// Mike Dixon
//
// April 2004
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasRelay;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import edu.ucar.rap.jrp.*;
import edu.ucar.rap.titan.RdasControl.*;

class RdasRelay extends JFrame

{
    
    private Parameters _params = Parameters.getInstance();
    private boolean _debug = false;
    private boolean _verbose = false;
    private boolean _edit = false;
    private String _initParamPath = null;
    private CommandQueue _commandQueue = CommandQueue.getInstance();

    public RdasRelay(String args[])
    {
	
	// parse command line

	_parseArgs(args);
	
	// set up frame and top panel
	
	setTitle("RDAS RELAY");
	JPanel topPanel = new JPanel();
	topPanel.setLayout(new BorderLayout());
	getContentPane().add(topPanel);

	// set up _params - this object is a singleton
	
	_params = Parameters.getInstance();
	_params.setParentComponent(topPanel);
	_params.setParameterFilePath(_initParamPath);
	_params.initFromXml();
	if (_debug) {
	    _params.debug.setValue(true);
	}
	if (_verbose) {
	    _params.verbose.setValue(true);
	}

	// display the main image

	String mainImagePath = _params.mainWindow.imageName.getValue();
	TrDummyComponent dc = new TrDummyComponent();
	Image mainImage = JrpImageLoad.getFromRes(dc, mainImagePath);
	ImageIcon mainIcon =  new ImageIcon(mainImage);
	JLabel mainLabel = new JLabel(mainIcon);
	topPanel.add(mainLabel, BorderLayout.CENTER);
	
	// in edit mode, create menu
	
	if (_edit) {
	    _createGui(topPanel);
	    return;
	}

	// start server
	
	RelayServer.getInstance().start();
	
	// start the data handler thread
	
	RelayComms.getInstance().start();
	
    }

    // print out usage and exit
    
    private void _usage() {
        System.err.println("Usage: RdasRelay [-opts as below]");
        System.err.println("       [ -h, -help, -usage ] print usage");
        System.err.println("       [ -debug ] set debugging on");
        System.err.println("       [ -edit ] param edit mode");
        System.err.println("       [ -params ? ] specify params file");
        System.err.println("       [ -verbose ] set verbose debugging on");
        System.exit(1);
    }
    
    // parse command line
    
    private void _parseArgs(String[] args) {
	
	for (int i = 0; i < args.length; i++) {
	    if (args[i].equals("-params")) {
		if (i < args.length - 1) {
		    _initParamPath = new String(args[++i]);
		} else {
		    _usage();
		}
	    } else if (args[i].equals("-debug")) {
		_debug = true;
	    } else if (args[i].equals("-edit")) {
		_edit = true;
	    } else if (args[i].equals("-verbose")) {
		_verbose = true;
	    } else if (args[i].equals("-h")) {
		_usage();
	    } else if (args[i].equals("-help")) {
		_usage();
	    } else if (args[i].equals("-usage")) {
		_usage();
	    }
	} // i
	
    }

    // create gui

    private void _createGui(JPanel topPanel) {

	JMenuBar menu = new JMenuBar();
	setJMenuBar(menu);
	
	// Create the file & configuration menu, add exit to it
	
	JMenu fileMenu = _params.getMenu("File");
	fileMenu.setMnemonic('F');
	fileMenu.setToolTipText("Configuration menu");
	
	JMenuItem exit = fileMenu.add(new ExitAction("Exit"));
	exit.setMnemonic('x');
	exit.setToolTipText("Exit the application");
	
	menu.add(fileMenu);
	
	// set main window size
	
	setSize(_params.mainWindow.width.getValue(),
		_params.mainWindow.height.getValue());
	// addComponentListener(new MoveResizeListener());
	
	// set to exit on close
	
	setDefaultCloseOperation(EXIT_ON_CLOSE);
	
	// create the active file label
	
	JLabel activeFileLabel = _params.getActiveFileLabel();
	topPanel.add(activeFileLabel, BorderLayout.SOUTH);
	
	// create toolbar
	
 	JToolBar toolBar = new JToolBar();
	toolBar.setFloatable(false);
	topPanel.add(toolBar, BorderLayout.NORTH);
	
  	JButton openTool = toolBar.add(_params.getFileOpenAction());
  	openTool.setMnemonic('O');
  	openTool.setToolTipText("Open an existing config");
	
  	JButton editTool = toolBar.add(_params.getEditAction());
  	editTool.setMnemonic('E');
  	editTool.setToolTipText("Edit the parameters");
	
  	JButton saveTool = toolBar.add(_params.getFileSaveAction());
  	saveTool.setMnemonic('S');
  	saveTool.setToolTipText("Save this config");
	
 	// make it visible
	
	setVisible(true);

    }
	

    // exit

    private void _doExit() {
	
	// check for param changes
	
	if (_params.checkUnsavedChanges()) {
	    System.exit(0);
	}

    }

    // main

    public static void main(String args[])
    {

	// Create an instance of the application
	RdasRelay mainFrame = new RdasRelay(args);
    }

    ////////////////////////////////////////////////////
    // Inner classes for listeners
    ////////////////////////////////////////////////////

    private class ExitAction extends AbstractAction
    {
	public ExitAction(String name )
	{
	    super( name, null );
	}
	public void actionPerformed(ActionEvent event) {
	    _doExit();
	}
    }

    private class MoveResizeListener extends ComponentAdapter
    {
    
	public void componentMoved(ComponentEvent e) {
	    Parameters params = Parameters.getInstance();
	    int paramX = params.mainWindow.xx.getValue();
	    int paramY = params.mainWindow.yy.getValue();
	    if (paramX != getX() || paramY != getY()) {
		params.mainWindow.xx.setValue(getX());
		params.mainWindow.yy.setValue(getY());
	    }
	}
	
	public void componentResized(ComponentEvent e) {
	    Parameters params = Parameters.getInstance();
	    int paramWidth = params.mainWindow.width.getValue();
	    int paramHeight = params.mainWindow.height.getValue();
	    if (paramWidth != getWidth() || paramHeight != getHeight()) {
		params.mainWindow.width.setValue(getWidth());
		params.mainWindow.height.setValue(getHeight());
	    }
	}
	
    }

}


