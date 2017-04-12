//////////////////////////////////////////////////////////////////////
//
// ImageTest
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

class ImageTest extends JFrame

{
    
    private boolean _debug= false;
    private boolean _verbose = false;
    private boolean _redraw = false;
    private int _windowWidth = 510;
    private int _windowHeight = 431;
    private Canvas _canvas = null;
    
    public ImageTest(String args[])
    {
	
	// parse command line
	
	parseArgs(args);
	
	// set up frame and top panel
	
	setTitle("Image test");
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

	// paint

	if (_redraw) {
	    // create the image tick thread, start it
	    Ticker ticker = new Ticker();
	    ticker.start();
	} else {
	    _canvas.doRepaint();
	}


    }

    // print out usage and exit
    
    private void usage() {
        System.err.println("Usage: ImageTest [-opts as below]");
        System.err.println("       [ -h, -help, -usage ] print usage");
        System.err.println("       [ -debug ] set debugging on");
        System.err.println("       [ -redraw ] redraw as fast as possible");
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
	    } else if (args[i].equals("-redraw")) {
		_redraw = true;
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
	ImageTest mainFrame = new ImageTest(args);
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
	private WorldImage _image = null;
	private Image _bgImage = null;
	private Graphics _scGraphics = null;
	private Graphics _bgGraphics = null;

	private int _count = 0;
	private double _displayedFrameRate = 0.0;
	private int _nFramesForRate = 0;
	private long _startTimeForRate = TimeManager.getTime();

	public Canvas() {

	    // create new image

	    int imageWidth = 360;
	    int imageHeight = 500;
	    
	    _image = new WorldImage(imageWidth, imageHeight,
				    0, imageWidth - 1,
				    0, imageHeight - 1);

	    // set pixel colors directly
	    
	    for (int ix = 0; ix < imageWidth; ix++) {
		for (int iy = 0; iy < imageHeight; iy++) {
		    
		    _image.setRGB(ix, iy, Color.WHITE.getRGB());
		    
		    if ((ix == 200) || (iy == 200)) {
			_image.setRGB(ix, iy, Color.BLUE.getRGB());
		    }

		} // iy
	    } // ix
	    
	}

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
	    _scGraphics.drawImage(_bgImage, 0, 0, null);
	
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

	    Graphics2D g2 = (Graphics2D) g;

	    // compute displayed frame rate in frames/sec
	    
	    if (_redraw) {
		_count++;
		_nFramesForRate++;
		if (_nFramesForRate == 10) {
		    long now = TimeManager.getTime();
		    double intervalSecs =
			(now - _startTimeForRate) / 1000.0;
		    _displayedFrameRate = 10 / intervalSecs;
		    _startTimeForRate = now;
		    _nFramesForRate = 0;
		    System.err.println("Frame rate: " + (int) _displayedFrameRate);
		}
	    }
	    
	    // set up world plot object
	    
	    Dimension size = getSize();
	    Insets insets = getInsets();

	    int leftMargin = insets.left + 50;
	    int rightMargin = insets.right + 50;
	    int topMargin = insets.top + 50;
	    int bottomMargin = insets.bottom + 50;

// 	    double minX = 0.0;
// 	    double minY = 0.0;
// 	    double maxX = 360.0;
// 	    double maxY = 500.0;
	    double minX = 90.0;
	    double minY = 90.0;
	    double maxX = 310.0;
	    double maxY = 310.0;
	    
	    WorldPlot out = new WorldPlot(size.width, size.height,
					  leftMargin, rightMargin,
					  topMargin, bottomMargin,
					  minX, maxX, minY, maxY);

	    // copy in the image

	    // g.drawImage(image, 0, 0, null);
	    
	    _image.drawImageToGraphics(g2, out);

	    g2.setColor(Color.red);
	    out.drawLine(g2, minX, minY, maxX, minY);
	    out.drawLine(g2, minX, minY, minX, maxY);
	    out.drawLine(g2, maxX, minY, maxX, maxY);
	    out.drawLine(g2, minX, maxY, maxX, maxY);

	    g2.setColor(Color.orange);
	    out.drawLine(g2, minX, 199, maxX, 199);
	    out.drawLine(g2, 199, minY, 199, maxY);

	    g2.setColor(Color.magenta);
	    out.drawLine(g2, minX, 200, maxX, 200);
	    out.drawLine(g2, 200, minY, 200, maxY);

	    g2.setColor(Color.cyan);
	    out.drawLine(g2, minX, 201, maxX, 201);
	    out.drawLine(g2, 201, minY, 201, maxY);

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
	    
	    if (_scGraphics != null) {
		_scGraphics.dispose();
	    }
	    _scGraphics = getGraphics();
	    
	}
	    
    } // Canvas

}


