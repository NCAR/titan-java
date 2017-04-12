///////////////////////////////////////////////////////////////////////
//
// DowControl
//
// DOW DRX Control
//
// Mike Dixon
//
// May 2013
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.DowControl;

import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import edu.ucar.rap.jrp.*;

class DowControl

{
  
  private String _procName = "DowControl";
  private String _procInstance = "ops";
  private String _version = "v1.0";
  private Parameters _params = Parameters.getInstance();
  private boolean _debug = false;
  private boolean _verbose = false;
  private String _initParamPath = null;
  
  public DowControl(String[] args)
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
    _params.setUnsavedChanges(false);

    // create procmap registration object
    
    ProcmapReg pmap = ProcmapReg.getInstance();
    pmap.setProcName(_procName);
    pmap.setProcInstance(_procInstance);
    if (_verbose) {
      System.err.println("****** Register with procmap ************");
      System.err.println(pmap);
      System.err.println("*****************************************");
    }

    // add shutdown hook
    
    Runtime.getRuntime().addShutdownHook(new DowControlShutdown());

    // register with procmap

    ProcmapReg.getInstance().autoRegister("Starting up");

    // Create gui object
    
    Gui gui = new Gui(this);
    gui.setVisible(true);

    // set status gathering thread going

    new GetStatus();
    
  }
  
  // print out usage and exit
  
  private void usage() {
    System.out.println("Usage: DowControl [-opts as below]");
    System.out.println("       [ -h, -help, -usage ] print usage");
    System.out.println("       [ -debug ] set debugging on");
    System.out.println("       [ -params ? ] specify params file");
    System.out.println("       [ -instance ? ] specify instance for procmap");
    System.out.println("       [ -version ] print version");
    System.out.println("       [ -verbose ] set verbose debugging on");
    System.exit(1);
  }
    
  private void version() {
    System.out.println("DowControl version: " + _version);
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
    DowControl app = new DowControl(args);
	
  }

  ////////////////////////////////////////////////////
  // Inner class for Gui application

  private class Gui extends JFrame
  {

    private JPanel _topPanel;
    
    public Gui(DowControl app) {

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
	    
      // start the status data handler thread
	    
      StatusDataHandler.getInstance().start();
      
      // set the commandQueue active

      // CommandQueue.getInstance().setActive(true);
	    
      // create the socket communicator thread and start it

      // RdasComms.getInstance().start();
      
      // add change listener for params
	    
      _params.mainWindow.addChangeListener(new MainParamsChangeListener());
	    
      // set the title string
      
      _setTitleStr();
      
    }
	    
    // set title
    
    private void _setTitleStr() {
      String frameTitle = "DOW CONTROL " +
        _version + " - " +
        _params.mainWindow.radarName.getValue();
      setTitle(frameTitle);
    }
	
    ////////////////////////////////////////////////////
    // Inner classes for listeners
	
    private class MainParamsChangeListener
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
      implements StatusDataListener

    {
	    
      Image _bgImage;
      int _bgImageHeight, _bgImageWidth;
      StatusData _status = null;
	    
      public MainImagePanel() {
        String imagePath = _params.mainWindow.imageName.getValue();
        TrDummyComponent dc = new TrDummyComponent();
        _bgImage = JrpImageLoad.getFromRes(dc, imagePath);
        _bgImageHeight = _bgImage.getHeight(_topPanel);
        _bgImageWidth = _bgImage.getWidth(_topPanel);
        StatusDataHandler.getInstance().addListener(this);
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
		
        if (_status != null) {

          // el/az and lower left
          
          String elStr = "El: " + NFormat.el2.format(_status.elevation_deg);
          String azStr = "Az: " + NFormat.az1.format(_status.azimuth_deg);
          Font font = getFont().deriveFont
            (Font.BOLD, _params.mainWindow.fontSize.getValue());
          g2.setFont(font);
          g2.setPaint(_params.mainWindow.textColor.getValue());
          ArrayList botLeftLegends = new ArrayList();
          botLeftLegends.add(elStr);
          botLeftLegends.add(azStr);
          wplot.drawLegendsBottomLeft(g2, botLeftLegends);

          // prf and az rate at top left
          
          double prf = 1.0 / _status.prt1_sec;
          String prfStr = "PRF: " + NFormat.f1.format(prf);
          String azRateStr = "Antenna rate: " + 
            NFormat.f1.format(_status.scan_rate_deg_per_sec);
          ArrayList topLeftLegends = new ArrayList();
          topLeftLegends.add(prfStr);
          topLeftLegends.add(azRateStr);
          wplot.drawLegendsTopLeft(g2, topLeftLegends);

          // date and time at lower right
          
          String dateString = new
            String(NFormat.i4.format(_status.gps_time.year) +
                   "/" +
                   NFormat.i2.format(_status.gps_time.month) +
                   "/" + 
                   NFormat.i2.format(_status.gps_time.day));
          
          String timeString = new
            String(NFormat.i2.format(_status.gps_time.hour) +
                   ":" +
                   NFormat.i2.format(_status.gps_time.min) +
                   ":" + 
                   NFormat.i2.format(_status.gps_time.sec));
          
          ArrayList botRightLegends = new ArrayList();
          botRightLegends.add(dateString);
          botRightLegends.add(timeString);
          wplot.drawLegendsBottomRight(g2, botRightLegends);

          // gate spacing and n gates at top right

          int nGates = _status.pentekHigh.n_gates;
          String nGatesStr = "N Gates: " + NFormat.i4.format(nGates);
          if (nGates < 1000) {
            nGatesStr = "N Gates: " + NFormat.i3.format(nGates);
          }
          String gateSpacingStr = "Spacing: " +
            NFormat.f1.format(_status.rx_gate_spacing_m);
          ArrayList topRightLegends = new ArrayList();
          topRightLegends.add(nGatesStr);
          topRightLegends.add(gateSpacingStr);
          wplot.drawLegendsTopRight(g2, topRightLegends);

        } // if (status != null)
		
      } // paintComponent
	    
      // handle new status
	    
      public void handleStatus(StatusData status) {
        _status = status;
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

  } // class Gui
  
    //////////////////////
    // shutdown hook class
  
  private class DowControlShutdown extends Thread {
    public void run() {
      
      // unregister with procmap
      
      ProcmapReg.getInstance().unregister();
	    
    }
  }

}


