///////////////////////////////////////////////////////////////////////
//
// RdasControl
//
// TITAN RDAS Control
//
// Mike Dixon
//
// August 2002
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import edu.ucar.rap.jrp.*;

class RdasControl

{

    private String _procName = "RdasControl";
    private String _procInstance = "default";
    private String _version = "1.10";
    private Parameters _params = Parameters.getInstance();
    private boolean _debug = false;
    private boolean _verbose = false;
    private boolean _relay = false;
    private boolean _viaRelay = false;
    private boolean _secure = false;
    private String _initParamPath = null;

    public RdasControl(String[] args)
    {

	// parse command line

	parseArgs(args);
	
	// set up _params - this object is a singleton
	
	_params = Parameters.getInstance();
	_params.setParameterFilePath(_initParamPath);
	_params.initFromXml();
	if (_debug) {
	    _params.debug.setValue(true);
	} else {
	    _params.debug.setValue(false);
	}
	if (_verbose) {
	    _params.verbose.setValue(true);
	} else {
	    _params.verbose.setValue(false);
	}
	if (_viaRelay) {
	    _params.rdasComm.viaRelay.setValue(true);
	}
	if (_secure) {
	    _params.rdasComm.acceptRemoteCommands.setValue(false);
	}
	_params.setUnsavedChanges(false);

	// create procmap registration object

	ProcmapReg pmap = ProcmapReg.getInstance();
	pmap.setProcName(_procName);
	pmap.setProcInstance(_procInstance);
	if (_debug) {
	    System.err.println("****** Register with procmap ************");
	    System.err.println(pmap);
	    System.err.println("*****************************************");
	}

	// add shutdown hook

	Runtime.getRuntime().addShutdownHook(new RdasControlShutdown());

	if (_relay) {

	    // create relay object
	    RelayOnly relay = new RelayOnly();

	} else {
	    
	    // Create interactive object

	    Interactive interactive = new Interactive(this);
	    interactive.setVisible(true);
	    
	}

    }

    // print out usage and exit
    
    private void usage() {
        System.out.println("Usage: RdasControl [-opts as below]");
        System.out.println("       [ -h, -help, -usage ] print usage");
        System.out.println("       [ -debug ] set debugging on");
        System.out.println("       [ -params ? ] specify params file");
        System.out.println("       [ -instance ? ] specify instance for procmap");
        System.out.println("       [ -relay ] act as a relay server");
        System.out.println("       [ -secure ] do not accept remote commands - relay mode");
        System.out.println("       [ -version ] print version");
        System.out.println("       [ -verbose ] set verbose debugging on");
        System.out.println("       [ -via_relay ] contact relay server instead of RDAS");
        System.exit(1);
    }
    
    private void version() {
        System.out.println("RdasControl version: " + _version);
        System.exit(1);
    }
    
    // parse command line
    
    void parseArgs(String[] args) {
	
	for (int i = 0; i < args.length; i++) {
	    if (args[i].equals("-params")) {
		if (i < args.length - 1) {
		    _initParamPath = new String(args[++i]);
		} else {
		    usage();
		}
	    } else if (args[i].equals("-instance")) {
		if (i < args.length - 1) {
		    _procInstance = new String(args[++i]);
		} else {
		    usage();
		}
	    } else if (args[i].equals("-debug")) {
		_debug = true;
	    } else if (args[i].equals("-verbose")) {
		_verbose = true;
	    } else if (args[i].equals("-h")) {
		usage();
	    } else if (args[i].equals("-help")) {
		usage();
	    } else if (args[i].equals("-relay")) {
		_relay = true;
	    } else if (args[i].equals("-secure")) {
		_secure = true;
	    } else if (args[i].equals("-via_relay")) {
		_viaRelay = true;
	    } else if (args[i].equals("-usage")) {
		usage();
	    } else if (args[i].equals("-version")) {
		version();
	    }
	} // i
	
    }

    public void doExit(boolean checkForParamsChanges) {

	boolean performExit = true;
	
	// check for param changes if requested

	if (checkForParamsChanges) {
	    if (!_params.checkUnsavedChanges()) {
		performExit = false;
	    }
	}
		
	if (performExit) {

	    // unregister with procmap
	    
	    ProcmapReg.getInstance().unregister();
	    
	    // exit
	    
	    System.exit(0);

	}

    }

    // main
    public static void main(String args[])
    {

	// Create an instance of the application
	RdasControl app = new RdasControl(args);
	
    }

    ////////////////////////////////////////////////////
    // Inner class for Interactive application

    private class Interactive extends JFrame
    {

	private JPanel _topPanel;

	public Interactive(RdasControl app) {

	    // set up frame and top panel
	    
	    _topPanel = new JPanel();
	    _topPanel.setLayout(new BorderLayout());
	    getContentPane().add(_topPanel);
	    
	    // attach params to top panel
	    
	    _params.setParentComponent(_topPanel);

	    // display the main image
	    
	    MainImagePanel mainImagePanel = new MainImagePanel();
	    mainImagePanel.setLayout(new BorderLayout());
	    _topPanel.add(mainImagePanel, BorderLayout.CENTER);

// 	    String mainImagePath = _params.mainWindow.imageName.getValue();
// 	    TrDummyComponent dc = new TrDummyComponent();
// 	    Image mainImage = JrpImageLoad.getFromRes(dc, mainImagePath);
// 	    ImageIcon mainIcon =  new ImageIcon(mainImage);
// 	    JLabel mainLabel = new JLabel(mainIcon);
// 	    _topPanel.add(mainLabel, BorderLayout.CENTER);
	    
	    // create the active file label
	    
	    JLabel activeFileLabel = _params.createActiveFileLabel();
	    _topPanel.add(activeFileLabel, BorderLayout.SOUTH);
	    
	    // create the menu, which creates all of the windows
	    
	    MainMenu menu = new MainMenu(app, this, _topPanel);

	    // listen for closing

	    addWindowListener(new MainWindowListener());

	    // set main window size
	    
	    setSize(_params.mainWindow.width.getValue(),
		    _params.mainWindow.height.getValue());
	    addComponentListener(new MoveResizeListener());
	    
	    // set to exit on close
	    
	    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	    
	    // start the sun track thread
	    
	    SunTrack.getInstance().start();
	    
	    // start the radar data handler thread
	    
	    BeamDataHandler.getInstance().start();

	    // set the commandQueue active

	    CommandQueue.getInstance().setActive(true);
	    
	    // create the socket communicator thread and start it

	    RdasComms.getInstance().start();
	    
	    // start the warning logging

	    ControlFrame controlFrame = ControlFrame.getInstance();
	    ControlPanel controlPanel = controlFrame.getPanel();
	    WarningLog warningLog = new WarningLog(controlPanel);
	    warningLog.start();
	    
	    // add change listener for params
	    
	    _params.radar.addChangeListener(new RadarParamsChangeListener());
	    
	    // set the title string
	    
	    _setTitleStr();

	}
	    
	// set title
	
	private void _setTitleStr() {
	    String frameTitle = "RDAS2000 CONTROL version " +
		_version + " - " +
		_params.radar.siteName.getValue();
	    setTitle(frameTitle);
	}
	
	////////////////////////////////////////////////////
	// Inner classes for listeners
	
	private class RadarParamsChangeListener
	    implements CollectionChangeListener {
	    public void reactToChange() {
		_setTitleStr();
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

	private class MainWindowListener extends WindowAdapter {
	    public void	windowClosing(WindowEvent e) {
		doExit(false);
	    }
	}

	////////////////////////////////////////////////////
	// Inner class for main image panel
	
	private class MainImagePanel extends JPanel
	    implements BeamDataListener

	{
	    
	    Image _bgImage;
	    int _bgImageHeight, _bgImageWidth;
	    BeamMessage _beam = null;
	    
	    public MainImagePanel() {
		String imagePath = _params.mainWindow.imageName.getValue();
		TrDummyComponent dc = new TrDummyComponent();
		_bgImage = JrpImageLoad.getFromRes(dc, imagePath);
		_bgImageHeight = _bgImage.getHeight(_topPanel);
		_bgImageWidth = _bgImage.getWidth(_topPanel);
		BeamDataHandler.getInstance().addListener(this);
		Ticker ticker = new Ticker();
		ticker.start();
	    }
	    
	    // overload paintComponent
	    
	    public void paintComponent (Graphics g) {

		super.paintComponent(g);
		
		// scale the main image into the panel

		double scaleX = (double) getWidth() / _bgImageWidth;
		double scaleY = (double) getHeight() / _bgImageHeight;
		AffineTransform transform = new AffineTransform();
		transform.setToScale(scaleX, scaleY);
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(_bgImage, transform, _topPanel);

		// draw legends for time and radar posn

		WorldPlot wplot = new WorldPlot(getWidth(), getHeight(),
						0, 0, 0, 0,
						0.0, 1.0, 0.0, 1.0);
		
		if (_beam != null) {
		    
		    String elStr = "El: " + NFormat.el2.format(_beam.getEl());
		    String azStr = "Az: " + NFormat.az1.format(_beam.getAz());
		    Font font = getFont().deriveFont
			(Font.BOLD, _params.mainWindow.fontSize.getValue());
		    g2.setFont(font);
		    ArrayList leftLegends = new ArrayList();
		    leftLegends.add(elStr);
		    leftLegends.add(azStr);
		    wplot.drawLegendsBottomLeft(g2, leftLegends);

		    ArrayList rightLegends = new ArrayList();
		    rightLegends.add(_beam.getDateString());
		    rightLegends.add(_beam.getTimeString());
		    wplot.drawLegendsBottomRight(g2, rightLegends);

		} // if (beam != null)
		
	    } // paintComponent
	    
	    // handle new beam
	    
	    public void handleBeam(BeamMessage beam, double rate) {
		_beam = beam;
	    }
	    
	    // ticker thread
	    
	    private class Ticker extends Thread {
		public void run() {
		    while (true) {
			// redraw
			repaint();
			// sleep a bit
			Thread t = Thread.currentThread();
			try { t.sleep(1000); }
			catch (InterruptedException e) { return; }
		    }
		}
	    }
	    
	} // class MainImagePanel

    } // class Interactive
	
    ////////////////////////////////////////////////////
    // Inner class for Relay application

    private class RelayOnly
    {

	public RelayOnly() {

	    // start server
	    
	    RelayServer.getInstance().start();
	    
	    // set the commandQueue active
	    
	    CommandQueue.getInstance().setActive(true);
            
	    // start the radar data handler thread
	    
	    BeamDataHandler.getInstance().start();

	    // start the data handler thread
	    
	    RelayComms.getInstance().start();
            
            // start the warning logging
            
            ControlPanel controlPanel = new ControlPanel(null, null);
            WarningLog warningLog = new WarningLog(controlPanel);
            warningLog.start();
	    
	}

    } // class RelayOnly

    //////////////////////
    // shutdown hook class

    private class RdasControlShutdown extends Thread {
	public void run() {

	    // unregister with procmap
	    
	    ProcmapReg.getInstance().unregister();
	    
	}
    }

}


