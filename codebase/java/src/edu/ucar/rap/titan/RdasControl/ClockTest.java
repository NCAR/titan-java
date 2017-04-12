///////////////////////////////////////////////////////////////////////
//
// ClockTest
//
// Mike Dixon
//
// March 2003
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;

class ClockTest extends JFrame

{

    private JFrame _frame = this;
    private boolean _debug= false;
    private boolean _verbose = false;
    private int _windowWidth = 500;
    private int _windowHeight = 500;
    private Canvas _canvas = null;
    
    public ClockTest(String args[])
    {
	
	// parse command line
	
	parseArgs(args);
	
	// set up frame and top panel
	
	setTitle("Clock test");
	JPanel topPanel = new JPanel();
	topPanel.setLayout(new BorderLayout());
	getContentPane().add(topPanel);

	// set main window size
	
	setSize(_windowWidth, _windowHeight);
	
	// set to exit on close
	
	setDefaultCloseOperation(EXIT_ON_CLOSE);

	// canvas
	
	_canvas = new Canvas();
	topPanel.add(_canvas, BorderLayout.CENTER);

	// create the clock tick thread, start it

	Ticker ticker = new Ticker();
	ticker.start();

    }

    // print out usage and exit
    
    private void usage() {
        System.err.println("Usage: ClockTest [-opts as below]");
        System.err.println("       [ -h, -help, -usage ] print usage");
        System.err.println("       [ -debug ] set debugging on");
        System.err.println("       [ -verbose ] set verbose debugging on");
        System.exit(1);
    }
    
    // parse command line
    
    void parseArgs(String[] args) {
	
	for (int i = 0; i < args.length; i++) {
	    if (args[i].equals("-debug")) {
		_debug = true;
	    } else if (args[i].equals("-verbose")) {
		_verbose = true;
	    } else if (args[i].equals("-h")) {
		usage();
	    } else if (args[i].equals("-help")) {
		usage();
	    } else if (args[i].equals("-usage")) {
		usage();
	    }
	} // i
	
    }

    // main
    public static void main(String args[]) {
	// Create an instance of the application
	ClockTest mainFrame = new ClockTest(args);
	mainFrame.setVisible(true);
    }

    ////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////

    // ticker thread
    
    private class Ticker extends Thread {
	public void run() {
	    while (true) {
		_canvas.doRepaint();
		// sleep a bit
		// Thread t = Thread.currentThread();
		// try { t.sleep(10); }
		// catch (InterruptedException e) { return; }
	    }
	}
    }

    // canvas

    private class Canvas extends JPanel {

	private int _bgImageWidth = 0;
	private int _bgImageHeight = 0;
	private Image _bgImage = null;
	private Image _labelImage = null;
	private Graphics _scGraphics = null;
	private Graphics _bgGraphics = null;
	private Graphics _labelGraphics = null;
	private boolean _labelRepaint = true;

	private int _count = 0;
	private double _displayedFrameRate = 0.0;
	private int _nFramesForRate = 0;
	private long _startTimeForRate = TimeManager.getTime();

	///////////////////////////////////
	// repint is called after each tick
	
	public void doRepaint() {
	    _paintBuffered();
	    //repaint(0);
	}

	public void _paintBuffered () {
	    
	    if (getWidth() == 0 || getHeight() == 0) {
		repaint();
		return;
	    }
	    
	    _initPaint();
	    
	    // draw into background image, copy to screen graphics
	    
	    _paint(_bgGraphics);
	    _scGraphics.drawImage(_bgImage, 0, 0, _frame);
	
	}
    
	///////////////////////////
	// overload paintComponent
	
	public void paintComponent (Graphics g) {
	    _initPaint();
	    _paint(g);
	}
	
	///////////////////////////////////////////
	// paint the plot into the graphics object
	
	public synchronized void _paint (Graphics g) {
	    
	    // compute displayed frame rate in frames/sec
	    
	    _count++;
	    _nFramesForRate++;
	    if (_nFramesForRate == 100) {
		long now = TimeManager.getTime();
		double intervalSecs =
		    (now - _startTimeForRate) / 1000.0;
		_displayedFrameRate = 100 / intervalSecs;
		_startTimeForRate = now;
		_nFramesForRate = 0;
		System.err.println("Frame rate: " + (int) _displayedFrameRate);
	    }

	    // set up world plot object

	    Dimension size = getSize();
	    Insets insets = getInsets();
	    
	    int leftMargin = insets.left + 20;
	    int rightMargin = insets.right + 20;
	    int topMargin = insets.top + 20;
	    int bottomMargin = insets.bottom + 20;
	    WorldPlot plot = new WorldPlot(size.width, size.height,
					   leftMargin, rightMargin,
					   topMargin, bottomMargin,
					   -1.0, 1.0,
					   -1.0, 1.0);
	    
	    // paint number labels if needed
	    
	    if (_labelRepaint) {
		_paintLabels(plot);
		_labelRepaint = false;
	    }
	    
	    // copy label image in
	    
	    g.drawImage(_labelImage, 0, 0, null);

	    Graphics2D g2 = (Graphics2D) g;
	    
	    // units hand

	    GeneralPath units = new GeneralPath();
	    double unitsAngle = (Math.PI / 2.0) - ((_count / 1000.0) * 2.0 * Math.PI);
	    units.moveTo(0.0f, 0.0f);
	    units.lineTo((float) (Math.cos(unitsAngle) * 0.8),
			 (float) (Math.sin(unitsAngle) * 0.8));
	    g2.setColor(Color.RED);
	    g2.setStroke(new BasicStroke(4));
	    plot.drawPathClipped(g2, units);
	    g2.setColor(getForeground());

	    // tens hand

	    GeneralPath tens = new GeneralPath();
	    double tensAngle = (Math.PI / 2.0) - ((_count / 10000.0) * 2.0 * Math.PI);
	    tens.moveTo(0.0f, 0.0f);
	    tens.lineTo((float) (Math.cos(tensAngle) * 0.6),
			(float) (Math.sin(tensAngle) * 0.6));
	    g2.setColor(Color.GREEN);
	    g2.setStroke(new BasicStroke(7));
	    plot.drawPathClipped(g2, tens);
	    g2.setColor(getForeground());

	    // hundreds hand
	    
	    GeneralPath hundreds = new GeneralPath();
	    double hundredsAngle = (Math.PI / 2.0) - ((_count / 100000.0) * 2.0 * Math.PI);
	    hundreds.moveTo(0.0f, 0.0f);
	    hundreds.lineTo((float) (Math.cos(hundredsAngle) * 0.4),
			    (float) (Math.sin(hundredsAngle) * 0.4));
	    g2.setColor(Color.BLUE);
	    g2.setStroke(new BasicStroke(10));
	    plot.drawPathClipped(g2, hundreds);
	    g2.setColor(getForeground());

	    // rate
	    
	    String rateLabel = new String("Frame rate: " + (int) _displayedFrameRate);
	    plot.drawTextCentered(g2, rateLabel, 0.0, 1.0);

	}

	private void _paintLabels(WorldPlot plot) {

	    Graphics2D labelG2 = (Graphics2D) _labelGraphics;
	    labelG2.setColor(getBackground());
	    labelG2.fill(new Rectangle(0, 0, getWidth(), getHeight()));
	    labelG2.setColor(getForeground());
	    
	    // numbers
	    
	    for (int i = 0; i < 10; i++) {
		    double angle = (Math.PI / 2.0) - ((i / 10.0) * 2.0 * Math.PI);
		    String label = Integer.toString(i);
		    double xx = Math.cos(angle) * 0.9;
		    double yy = Math.sin(angle) * 0.9;
		    plot.drawTextCentered(labelG2, label, xx, yy);
	    }

	}

	///////////////////////////////////////
	// initialize for painting
	//
	// set up images and graphics contexts
	
	private synchronized void _initPaint() {
	    
	    if (_bgImageWidth == getWidth() && _bgImageHeight == getHeight()) {
		return;
	    }
		
	    _bgImageWidth = getWidth();
	    _bgImageHeight = getHeight();
	    
	    _bgImage = createImage(_bgImageWidth, _bgImageHeight);
	    if (_bgGraphics != null) {
		_bgGraphics.dispose();
	    }
	    _bgGraphics = _bgImage.getGraphics();
	    
	    _labelImage = createImage(_bgImageWidth, _bgImageHeight);
	    if (_labelGraphics != null) {
		_labelGraphics.dispose();
	    }
	    _labelGraphics = _labelImage.getGraphics();
	    _labelRepaint = true;
	    
	    if (_scGraphics != null) {
		_scGraphics.dispose();
	    }
	    _scGraphics = getGraphics();
	    
	}
	    
    } // Canvas

}


