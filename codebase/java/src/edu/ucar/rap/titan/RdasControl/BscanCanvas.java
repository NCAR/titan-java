///////////////////////////////////////////////////////////////////////
//
// BscanCanvas
//
// JPanel as canvas for BSCAN display
//
// Mike Dixon
//
// Jan 2004
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import edu.ucar.rap.jrp.*;

public class BscanCanvas extends JPanel implements BeamDataListener
    
{
    
    private Parameters _params = Parameters.getInstance();
    private BscanDisplay _bscan;
    private ColorScale _dbzColors = new DbzColorScale();
    private ColorScale _snrColors = new SnrColorScale();
    private DataField _field = DataField.DBZ;
 
    private BeamMessage _beam = null;
    private BeamMessage _prevBeam = null;
    private int _beamCount = 0;
    
    private long _lastRepaintTime = 0;
    
    private double _maxRange = 0.0;
    private int _leftMargin = 0;
    private int _rightMargin = 0;
    private int _topMargin = 0;
    private int _bottomMargin = 0;

    private String _frameTitle = "";
    private String _plotTitle = "";

    private WorldPlot _plotWorld;
    private boolean _newWorld = true;
    
    private Zoomed _unZoomed = new Zoomed(); // always exists
    private Zoomed _zoomed = null;           // null if not zoomed
    private boolean _zoomingRectangleEnabled = true;
    private Zooming _zooming = null;         // null if not currently zooming
    private Zoomed _prevZoom;                // keeps track of previous zoom
    
    private int _nAz = 0;
    private int _nGates = 0;

    private int _bgImageWidth = 0;
    private int _bgImageHeight = 0;
    private Image _bgImage = null;

    private BufferedImage _axisImage = null;
    private BufferedImage _plotImage = null;
    private BufferedImage _dbzImage = null;
    private BufferedImage _snrImage = null;

    private float[] _dbzData = null;
    private float[] _snrData = null;

    private Graphics _scGraphics = null;
    private Graphics _bgGraphics = null;
    private Graphics _axisGraphics = null;
    private Graphics _plotGraphics = null;

    private long _clickTextTime = 0;
    private double _clickRange = 0;
    private double _clickAz = 0;
    private boolean _clickTextActive = false;

    public BscanCanvas(BscanDisplay bscan, DataField field) {
	
	_bscan = bscan;
	
	ZoomMouseListener zoomListener = new ZoomMouseListener();
	addMouseListener(zoomListener);
	addMouseMotionListener(zoomListener);

	// add listener for chanegs in bscan params

	_params.bscan.addChangeListener(new BscanChangeListener());
	_updateFromParams();
	setField(field);
	
	// add listener for changes in radar params

	_params.radar.addChangeListener(new RadarChangeListener());
	
	// add listener for new beam data
	
	BeamDataHandler.getInstance().addListener(this);

	// create the ticker thread, start it
	
	Ticker ticker = new Ticker();
	ticker.start();
	
     }

     // ticker thread
    
    private class Ticker extends Thread {
	public void run() {
	    while (true) {

		// update canvas
		if (_beam != _prevBeam) {
		    doRepaint();
		    _prevBeam = _beam;
		}

		// check click text
		_checkClickText();

		// sleep a bit
		Thread t = Thread.currentThread();
		int repaintMsecs = _params.bscan.repaintMsecs.getValue();
		repaintMsecs = Math.max(50, repaintMsecs);
		try { t.sleep(repaintMsecs); }
		catch (InterruptedException e) { return; }

	    }
	}
    }

   // handle beam
    
    public void handleBeam(BeamMessage beam, double rate) {
 	if (_bscan.isFreezeOn()) {
 	    return;
 	}
	_beam = beam;
	if (_nGates != _beam.getNGates()) {
	    _nGates = _beam.getNGates();
	    _unZoomed.maxY = _nGates - 1;
	    setNewWorld();
	}
	_beamCount++;
	_updateDataImages();
    }
    
    // set methods
    
    public void setUnzoomed() {
	_zoomed = null;
	setNewWorld();
    }
    
    public void setNewWorld() {
	_newWorld = true;
    }
    
    public void setField(DataField field) {
	_field = field;
	setTitleStr();
	_plotAxes();
	repaint();
	_checkClickText();
    }

    ///////////////////////////
    // overload paintComponent
    //
    // This gets called by Swing for repaints as required
    
    public void paintComponent (Graphics g) {
	super.paintComponent(g);
 	_initPaint();
 	_paint(g);
    }
    
    //////////////////////////////////////////////////////////
    // do a repaint - normally called when a new beam arrives
    
    public void doRepaint() {

	// if width and height are not yet set, call repaint, which
	// will only call paintComponent() after the object is ready
	// for painting
	
	if (getWidth() == 0 || getHeight() == 0) {
	    repaint();
	    return;
	}

	if (!isVisible()) {
	    return;
	}

	if (_zooming != null) {
	    return;
	}

	// initialize for painting
	
	_initPaint();
	
	// paint into background image
	
	_paint(_bgGraphics);

	// copy background to screen graphics

	_scGraphics.drawImage(_bgImage, 0, 0, _bscan);
	
    }

    ///////////////////////////////////
    // update from parameters

    private void _updateFromParams() {

	Insets insets = getInsets();
	_leftMargin = insets.left + _params.bscan.leftMargin.getValue();
	_rightMargin = insets.right + _params.bscan.rightMargin.getValue();
	_topMargin = insets.top + _params.bscan.topMargin.getValue();
	_bottomMargin = insets.bottom + _params.bscan.bottomMargin.getValue();
	_nAz = (int) (360.0 / _params.bscan.deltaAzimuth.getValue() + 0.5);

	_unZoomed.minX = 0.0;
	_unZoomed.maxX = 359.0;
	_unZoomed.minY = 0.0;
	_unZoomed.maxY = _nGates;
	
	setUnzoomed();

    }

    ///////////////////////////////////////
    // initialize for painting
    //
    // set up images and graphics contexts
    
    private synchronized void _initPaint() {
	
	if (_bgImageWidth == getWidth() && _bgImageHeight == getHeight() &&
	    _bgGraphics != null && _plotGraphics != null && _scGraphics != null) {
	    return;
	}
	
	_bgImageWidth = getWidth();
	_bgImageHeight = getHeight();
	
	_bgImage = createImage(_bgImageWidth, _bgImageHeight);
	if (_bgGraphics != null) {
	    _bgGraphics.dispose();
	}
	_bgGraphics = _bgImage.getGraphics();
	
	_plotImage = new BufferedImage(_bgImageWidth, _bgImageHeight,
				       BufferedImage.TYPE_INT_RGB);
	if (_plotGraphics != null) {
	    _plotGraphics.dispose();
	}
	_plotGraphics = _plotImage.getGraphics();
	
	if (_scGraphics != null) {
	    _scGraphics.dispose();
	}
	_scGraphics = getGraphics();

    }
    
    ///////////////////////////////////////
    // allocate the images and data arrays
    //
    // Data is stored in (az, gates) space
    
    private void _allocData() {

	if (_dbzImage != null &&
	    _dbzImage.getWidth() == _nAz &&
	    _dbzImage.getHeight() == _nGates) {
	    return;
	}

	_dbzImage = _allocImage(_dbzImage);
	_snrImage = _allocImage(_snrImage);

	int npoints = _nGates * _nAz;
	_dbzData = new float[npoints];
	_snrData = new float[npoints];

    }
	
    private BufferedImage _allocImage(BufferedImage image) {

	// save old image

	BufferedImage saveImage = image;

	// create new image

	image = new BufferedImage(_nAz, _nGates,
				  BufferedImage.TYPE_INT_RGB);

	// copy saved data to new image, if number of azimuths has not changed

	if (saveImage != null && _nAz == saveImage.getWidth()) {
	    int ny = Math.min(_nGates, saveImage.getHeight());
	    for (int ix = 0; ix < _nAz; ix++) {
		for (int iy = 0; iy < ny; iy++) {
		    image.setRGB(ix, iy, saveImage.getRGB(ix, iy));
		}
	    }
	}

	return image;

    }
    
    ///////////////////////////////////////
    // update the image for the data
    
    private void _updateDataImages() {
	
	if (_beam == null) {
	    return;
	}
	_allocData();

	// update data image
	
	double az = _beam.getAz();
	double deltaAz = _params.bscan.deltaAzimuth.getValue();
	int iaz = (int) ((az / deltaAz) + 0.5);
	while (iaz > _nAz - 1) {
	    iaz -= _nAz;
	}
	int nGates = _beam.getNGates();
	float dbz[] = _beam.getDbz();
	int jgate = _nGates - 1;
	for (int igate = 0; igate < _nGates; igate++, jgate--) {
	    float dbzVal = dbz[igate];
	    _dbzData[igate * _nAz + iaz] = dbzVal;
	    Color color = _dbzColors.getColor(dbzVal);
	    _dbzImage.setRGB(iaz, jgate, color.getRGB());
	}
	jgate = _nGates - 1;
	float snr[] = _beam.getSnr();
	for (int igate = 0; igate < _nGates; igate++, jgate--) {
	    float snrVal = snr[igate];
	    _snrData[igate * _nAz + iaz] = snrVal;
	    Color color = _snrColors.getColor(snrVal);
	    _snrImage.setRGB(iaz, jgate, color.getRGB());
	}

    }
	
    ///////////////////////////////////////////
    // paint the plot into the graphics object
    
    public synchronized void _paint (Graphics g) {

	// frame graphics

	Graphics2D g2 = (Graphics2D) g;

	// plot graphics

	Graphics2D plotG2 = (Graphics2D) _plotGraphics;
	
	// return now if no beam data
	
	if (_beam == null) {
	    
	    // clear window
	    
	    plotG2.setColor(getBackground());
	    plotG2.fill(new Rectangle(0, 0, getWidth(), getHeight()));
 	    return;

	}
	
	// set current zoom
	
	Zoomed thisZoom;
	if (_zoomed != null) {
	    thisZoom = _zoomed;
	} else {
	    thisZoom = _unZoomed;
	}

	// compute margins
	
	if (_newWorld) {
	    
	    // new world view
	    
	    _plotWorld = new WorldPlot(getWidth(), getHeight(),
				       _leftMargin, _rightMargin,
				       _topMargin, _bottomMargin,
				       thisZoom.minX, thisZoom.maxX,
				       thisZoom.minY, thisZoom.maxY);
	    
	    // empty data window
	    
	    plotG2.setColor(getBackground());
	    plotG2.fill(new Rectangle(0, 0, getWidth(), getHeight()));
	    plotG2.setColor(getForeground());

	    // plot the axes to _axisImage
	    
	    _plotAxes();
	    _newWorld = false;

	}

	// copy data image to plot image
	
	if (_field == DataField.DBZ) {
	    _plotWorld.drawImage(plotG2, _dbzImage, 0, 359, 0, _nGates - 1);
	} else {
	    _plotWorld.drawImage(plotG2, _snrImage, 0, 359, 0, _nGates - 1);
	}

	// draw azimuth line on plot image

	plotG2.setColor(Color.red);
	_plotWorld.setClippingOn(plotG2);
	double az = _beam.getAz();
	_plotWorld.drawLine(plotG2, az, 0, az, _nGates - 1);
	_plotWorld.setClippingOff(plotG2);
	plotG2.setColor(getForeground());

	// draw click point if active
	
  	_drawClickTextIcon(plotG2);

	// legends
	    
	ArrayList leftLegends = new ArrayList();
	leftLegends.add(new String("Date: " + _beam.getDateString()));
	leftLegends.add(new String("Time: " + _beam.getTimeString()));

	plotG2.setColor(Color.orange);
	_plotWorld.drawLegendsTopLeft(plotG2, leftLegends);
	plotG2.setColor(getForeground());
	    
	ArrayList rightLegends = new ArrayList();
	rightLegends.add
	    (new String("El: " + NFormat.f2.format(_beam.getEl())));
	rightLegends.add
	    (new String("Az: " + NFormat.f2.format(_beam.getAz())));

	plotG2.setColor(Color.orange);
	_plotWorld.drawLegendsTopRight(plotG2, rightLegends);
	plotG2.setColor(getForeground());

	// copy plot image and axim image to graphics
	
	g2.drawImage(_plotImage, 0, 0, null);
	g2.drawImage(_axisImage, 0, 0, null);
	
	
    } // _paint

    //////////////////////////////////////////////
    // plot the axes if required
    
    private void _plotAxes() {

	if (_bgImageWidth < 1 || _bgImageHeight < 1) {
	    return;
	}

	Zoomed zoom = _unZoomed;
	if (_zoomed != null) {
	    zoom = _zoomed;
	}
	
	_axisImage = new BufferedImage(_bgImageWidth, _bgImageHeight,
				       BufferedImage.TYPE_INT_ARGB);
	if (_axisGraphics != null) {
	    _axisGraphics.dispose();
	}
	_axisGraphics = _axisImage.getGraphics();
	Graphics2D axisG2 = (Graphics2D) _axisGraphics;
	axisG2.setColor(getForeground());
	
	// title

	setTitleStr();
	_plotWorld.drawTitleTopCenter(axisG2, _plotTitle, true);

	// axes
	
	axisG2.setColor(Color.white);
	_plotWorld.drawAxisLeft(axisG2, "", true, true, false);
	_plotWorld.drawAxisBottom(axisG2, "", true, true, false);
	axisG2.setColor(getForeground());
	_plotWorld.drawAxisLeft(axisG2, "Gate", false, false, true);
	_plotWorld.drawAxisBottom(axisG2, "Az(deg)", false, false, true);

	// borders
	
	axisG2.setColor(Color.red);
	_plotWorld.drawLine(axisG2,
			    zoom.minX, zoom.minY,
			    zoom.minX, zoom.maxY);
	_plotWorld.drawLine(axisG2,
			    zoom.minX, zoom.minY,
			    zoom.maxX, zoom.minY);
	_plotWorld.drawLine(axisG2,
			    zoom.maxX, zoom.minY,
			    zoom.maxX, zoom.maxY);
	_plotWorld.drawLine(axisG2,
			    zoom.minX, zoom.maxY,
			    zoom.maxX, zoom.maxY);
	axisG2.setColor(getForeground());
	
    }

    // set title

    private void setTitleStr() {
	
	_frameTitle = "B-SCAN - " + _params.radar.siteName.getValue();
	if (_field == DataField.DBZ) {
	    _plotTitle = _params.radar.siteName.getValue() +
		" - Refl (dBZ) - B-SCAN";
	} else {
	    _plotTitle = _params.radar.siteName.getValue() +
		" - SNR (dB) - B-SCAN";
	}
	_bscan.setTitle(_frameTitle);

    }

    ////////////////////////////////////
    // set click text in parent window

    private void _setClickText() {

	if (!_clickTextActive) {
	    return;
	}
	
	int iaz = (int) (_clickAz + 0.5);
	int irange = (int) (_clickRange + 0.5);
	int arrayIndex = irange * _nAz + iaz;
	if (iaz < 0 || iaz >= _nAz ||
	    irange < 0 || irange >= _nGates ||
	    arrayIndex < 0 || arrayIndex >= _dbzData.length) {
	    _clickTextActive = false;
	    _bscan.clearClickLabelText();
	    return;
	}
	float dbzVal = _dbzData[arrayIndex];
	float snrVal = _snrData[arrayIndex];
	
	if (dbzVal > -9000) {
	    String clickText =
		"rng: " + NFormat.f1.format(_clickRange) + " km  " + 
		"az: " + NFormat.f1.format(_clickAz) + " deg  ";
	    if (_field == DataField.DBZ) {
		clickText = clickText +
		    "Refl: " + NFormat.f1.format(dbzVal) + " dBZ";
	    } else {
		clickText = clickText +
		    "SNR: " + NFormat.f1.format(snrVal) + " dB";
	    }
	    _bscan.setClickLabelText(clickText);
	}

    }

    /////////////////////////////////////////
    // check that click text is still active
    
    private void _checkClickText() {

	long now = TimeManager.getTime();
	double secsSinceClick = (now - _clickTextTime) / 1000.0;
	int duration = _params.bscan.clickTextDuration.getValue();
	if (secsSinceClick > duration) {
	    _bscan.clearClickLabelText();
	    _clickTextActive = false;
	}
	_setClickText();

    }
    
    /////////////////////////////////////////
    // draw click text icon
    
    private void _drawClickTextIcon(Graphics2D plotG2) {

	if (!_clickTextActive) {
	    return;
	}
	
	int ixx = (int) Math.floor(_plotWorld.getXDevice(_clickAz) + 0.5);
	int iyy = (int) Math.floor(_plotWorld.getYDevice(_clickRange) + 0.5);
	int iconSize = _params.bscan.clickIconSize.getValue();

	plotG2.setColor(Color.cyan);
	_plotWorld.setClippingOn(plotG2);
	
	_plotWorld.drawIconLine(plotG2,
				(double) ixx, (double) iyy - iconSize,
				(double) ixx, (double) iyy + iconSize);
	_plotWorld.drawIconLine(plotG2,
				(double) ixx - iconSize, (double) iyy,
				(double) ixx + iconSize, (double) iyy);
	
	_plotWorld.setClippingOff(plotG2);
	plotG2.setColor(getForeground());

    }

    // Inner class
    // Listener for changes in bscan parameters

    private class BscanChangeListener implements CollectionChangeListener {
	public void reactToChange() {
	    _updateFromParams();
	}
    }
    
    // Inner class
    // Listener for changes in radar parameters

    private class RadarChangeListener implements CollectionChangeListener {
	public void reactToChange() {
	    setNewWorld();
	    setTitleStr();
	}
    }
    
    // Inner class used for tracking the zoom rectangle
    // and click origin
    
    private class Zooming {
	    
	public Rectangle rect = new Rectangle(0, 0, 0, 0);
	public boolean isFirstZoomRect = true;
	public Point startPoint;
	public int activeButton;
	    
	public Zooming(Point start_point, int active_button) {
	    startPoint = new Point(start_point.x, start_point.y);
	    activeButton = active_button;
	}
	    
    }
	
    // Inner class for current zoom state

    private class Zoomed {

	public double minX;
	public double minY;
	public double maxX;
	public double maxY;
	
	public void print(PrintStream out) {
	    out.println("========= Zoom ==========");
	    out.println("  minX: " + minX);
	    out.println("  minY: " + minY);
	    out.println("  maxX: " + maxX);
	    out.println("  maxY: " + maxY);
	}

	public boolean isSame(Zoomed other) {
	    if (other == null) {
		return false;
	    }
	    if (minX != other.minX) {
		return false;
	    }
	    if (maxX != other.maxX) {
		return false;
	    }
	    if (minY != other.minY) {
		return false;
	    }
	    if (maxY != other.maxY) {
		return false;
	    }
	    return true;
	}
	
	public void copy(Zoomed other) {
	    minX = other.minX;
	    maxX = other.maxX;
	    minY = other.minY;
	    maxY = other.maxY;
	}
	
    }
    
    // zoom mouse listener
    
    private class ZoomMouseListener
	implements MouseListener, MouseMotionListener {
	    
	public void mouseClicked(MouseEvent e) {
	}
	    
	public void mouseDragged(MouseEvent e) {
	    
	    if(_zooming == null) {
		return;
	    }
		
	    int xx = e.getPoint().x;
	    int yy = e.getPoint().y;
		
	    // We're zooming. Paint a rectangle to show zoom region.
	    // It should have the same aspect ratio as the window, and not
	    // be any smaller than is allowed by max_scale.
		
	    int rectWidth = (int) Math.abs(xx - _zooming.startPoint.getX());
	    int rectHeight = (int) Math.abs(yy - _zooming.startPoint.getY());
	    int rectOriginX = 0;
	    int rectOriginY = 0;
		
	    // double aspectRatio = (float) getHeight() / getWidth();
	    // rectWidth  = (int) Math.abs(xx - _zooming.startPoint.getX());
	    // int y_range_1 = (int) (aspectRatio * rectWidth);
	    // int y_range_2 = (int) Math.abs(yy - _zooming.startPoint.getY());
	    // if(y_range_2 > y_range_1) {
	    //   // y-distance determines size
	    //   rectHeight = y_range_2;
	    //   rectWidth = (int) (rectHeight / aspectRatio);
	    // } else {
	    //   // x-distance determines size
	    //   rectHeight = y_range_1;
	    // }
	    
	    if(xx > _zooming.startPoint.getX()) {
		rectOriginX = (int) _zooming.startPoint.getX();
	    } else {
		rectOriginX =
		    ((int) _zooming.startPoint.getX()) - rectWidth;
	    }
		
	    if(yy > _zooming.startPoint.getY()) {
		rectOriginY = (int) _zooming.startPoint.getY();
	    } else {
		rectOriginY =
		    ((int) _zooming.startPoint.getY()) - rectHeight;
	    }
		
	    Graphics g = getGraphics();
	    g.setColor(Color.red);
	    g.setXORMode(getBackground());
	    // g.setXORMode(Color.white);
		
	    // reverse the previous zooming rectangle outline if this is not the 1st
	    if(! _zooming.isFirstZoomRect) {
		Rectangle prevZoomRect = _zooming.rect;
		drawZoomRect(g,
			     (int) prevZoomRect.getX(),
			     (int) prevZoomRect.getY(),
			     (int) prevZoomRect.getWidth(),
			     (int) prevZoomRect.getHeight());
	    } else {
		_zooming.isFirstZoomRect = false;
	    }
		
	    Rectangle zoomRect = _zooming.rect;
	    zoomRect.setBounds(rectOriginX, rectOriginY, rectWidth, rectHeight);
	    drawZoomRect(g, rectOriginX, rectOriginY, rectWidth, rectHeight);
	    g.dispose();
		
	} // mouseDragged
	    
	// Draws with zoom rect line thickness of 2
	    
	private void drawZoomRect(Graphics g, int x, int y, int w, int h) {
	    g.drawRect(x, y, w, h);
	    g.drawRect(x + 1, y + 1, w - 2, h - 2);
	}
	    
	public void mouseEntered(MouseEvent e) {
	}
	    
	public void mouseExited(MouseEvent e)	{
	}
	    
	public void mouseMoved(MouseEvent e) {
	}
	    
	public void mousePressed(MouseEvent e) {
	    // Set up our zoom struct with initial click origin,
	    // just in case we're trying to zoom
	    if(_zoomingRectangleEnabled) {
		_zooming = new
		    Zooming(new Point(e.getX(), e.getY()), e.getButton());
	    }
	}
	    
	public void mouseReleased(MouseEvent e) {

	    if(_zooming == null) {
		return;
	    }
		
	    int x = e.getPoint().x;
	    int y = e.getPoint().y;
		
	    // Get the last point into mouseDrag
	    mouseDragged(e);
		
	    // if the mouse didn't move much, assume we don't want to zoom
	    if(((int) _zooming.rect.getWidth()) < 10) {

		// compute the click location

		int ixx = e.getPoint().x;
		int iyy = e.getPoint().y;

		// set the click text in parent window
		
		_clickTextTime = TimeManager.getTime();
		_clickAz = _plotWorld.getXWorld(ixx);
		_clickRange = _plotWorld.getYWorld(iyy);
		_clickTextActive = true;
		_setClickText();

		// clear the zooming rectangle outline if this is not the 1st
		Graphics g = getGraphics();
		if(g != null) {
		    try {
			g.setColor(Color.red);
			g.setXORMode(Color.white);
			drawZoomRect(g,
				     (int) _zooming.rect.getX(),
				     (int) _zooming.rect.getY(),
				     (int) _zooming.rect.getWidth(),
				     (int) _zooming.rect.getHeight());
		    } finally {
			g.dispose();
		    }
		}

	    } else {
		
		double zoomX1 = _zooming.rect.getX();
		double zoomY1 = _zooming.rect.getY() + _zooming.rect.getHeight();
		double zoomX2 = _zooming.rect.getX() + _zooming.rect.getWidth();
		double zoomY2 = _zooming.rect.getY();
		
		Zoomed zoomed = new Zoomed();
		
		zoomed.minX = (int) _plotWorld.getXWorld(zoomX1);
		zoomed.maxX = (int) (_plotWorld.getXWorld(zoomX2) + 1);
		zoomed.minY = _plotWorld.getYWorld(zoomY1);
		zoomed.maxY = _plotWorld.getYWorld(zoomY2);

		if (zoomed.minX < _unZoomed.minX) {
		    zoomed.minX = _unZoomed.minX;
		}
		if (zoomed.maxX > _unZoomed.maxX) {
		    zoomed.maxX = _unZoomed.maxX;
		}
		if (zoomed.minY < _unZoomed.minY) {
		    zoomed.minY = _unZoomed.minY;
		}
		if (zoomed.maxY > _unZoomed.maxY) {
		    zoomed.maxY = _unZoomed.maxY;
		}
		
		_zoomed = zoomed;
		_bscan.setZoomState(true);
		setNewWorld();

	    }
	    
	    _zooming = null;
	    doRepaint();
	    
	} // mouseReleased
	    
    } // ZoomMouseListener


}
