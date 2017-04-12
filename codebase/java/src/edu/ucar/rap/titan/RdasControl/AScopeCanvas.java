///////////////////////////////////////////////////////////////////////
//
// AScopeCanvas
//
// JPanel as canvas for AScope
//
// Mike Dixon
//
// March 2003
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

public class AScopeCanvas extends JPanel
    
{
    
    private Parameters _params = Parameters.getInstance();
    private AScope _aScope;
    private DataField _field = DataField.DBZ;
    private BeamMessage _beam = null;
    private BeamMessage _prevBeam = null;

    private long _lastRepaintTime = 0;

    private int _nGates;
    private double _startRange;
    private double _gateSpacing;
    
    private WorldPlot _countWorld;
    private WorldPlot _dbzWorld;
    private WorldPlot _snrWorld;

    private boolean _plotCount = true;
    private boolean _plotDbzSnr = true;
    private boolean _performSampling = false;

    private Color _countColor = Color.RED;
    private Color _dbzColor = Color.BLUE;
    private Color _snrColor = Color.BLUE;
    
    private boolean _zoomingRectangleEnabled = true;
    private Zooming _zooming = null;         // null if not currently zooming
    private Zoomed _unZoomed = new Zoomed(); // always exists
    private Zoomed _zoomed = null;           // null if not zoomed
    private Zoomed _prevZoom;                // keeps track of previous zoom

    private double _incomingBeamRate = 0.0;
    private double _displayedBeamRate = 0.0;
    private int _nBeamsForRate = 0;
    private long _startTimeForRate = TimeManager.getTime();

    private int _bgImageWidth = 0;
    private int _bgImageHeight = 0;
    private Image _bgImage = null;
    private Image _axisImage = null;
    private Graphics _scGraphics = null;
    private Graphics _bgGraphics = null;
    private Graphics _axisGraphics = null;

    private boolean _refreshAxes = true;
    private boolean _axisPlotCount = false;
    private boolean _axisPlotDbzSnr = false;
    private int _axisWidth = 0;
    private int _axisHeight = 0;
    private int _axisLeftMargin = 0;
    private int _axisRightMargin = 0;
    private int _axisTopMargin = 0;
    private int _axisBottomMargin = 0;
    private Zoomed _axisZoom = new Zoomed();
    private DataField _axisField = DataField.SNR;

    public AScopeCanvas(AScope aScope, DataField field) {

	_aScope = aScope;
	setField(field);

	_plotCount = _params.aScope.plotCount.getValue();
	_plotDbzSnr = _params.aScope.plotDbzSnr.getValue();
	_performSampling = _params.aScope.performSampling.getValue();
	_nGates = _params.scan.nGates.getValue();

	ZoomMouseListener zoomListener = new ZoomMouseListener();
	addMouseListener(zoomListener);
	addMouseMotionListener(zoomListener);

	_params.radar.addChangeListener(new RadarParamsChangeListener());
	
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
		// sleep a bit
		Thread t = Thread.currentThread();
		int repaintMsecs = _params.aScope.repaintMsecs.getValue();
		repaintMsecs = Math.max(10, repaintMsecs);
		try { t.sleep(repaintMsecs); }
		catch (InterruptedException e) { return; }
	    }
	}
    }

    // set methods

    public void setBeam(BeamMessage beam) {
	_beam = beam;
    }
    
    public void setIncomingBeamRate(double rate) {
	_incomingBeamRate = rate;
    }
    
    public void setField(DataField field) {
	_field = field;
	repaint();
    }
    
   public void setUnzoomed() {
	_zoomed = null;
    }

    public void setPlotCount(boolean state) {
	_plotCount = state;
    }
    public void setPlotDbzSnr(boolean state) {
	_plotDbzSnr = state;
    }
    public void setPerformSampling(boolean state) {
	_performSampling = state;
    }

    // get methods

    //////////////////////////////////////////////////////////
    // do a repaint - normally called when a new beam arrives
    
    public void doRepaint() {
	if (_zooming == null) {
	    if (!_params.aScope.fastPaint.getValue()) {
		repaint();
		return;
	    }
	    _paintBuffered();
	}
    }

    //////////////////////////////////////////////
    // paint into background image, copy to screen

    public void _paintBuffered () {
	
	// if width and height are not yet set, call repaint, which
	// will only call paintComponent() after the object is ready
	// for painting
	
	if (getWidth() == 0 || getHeight() == 0) {
	    repaint();
	    return;
	}

	// initialize for painting

	_initPaint();
	
	// paint into background image, then copy background to screen graphics
	
	_paint(_bgGraphics);
	_scGraphics.drawImage(_bgImage, 0, 0, _aScope);
	
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
	
	_axisImage = createImage(_bgImageWidth, _bgImageHeight);
	if (_axisGraphics != null) {
	    _axisGraphics.dispose();
	}
	_axisGraphics = _axisImage.getGraphics();
	
	if (_scGraphics != null) {
	    _scGraphics.dispose();
	}
	_scGraphics = getGraphics();
	
    }
    
    ///////////////////////////////////////////
    // paint the plot into the graphics object
    
    public synchronized void _paint (Graphics g) {

	// graphics contexts

 	Graphics2D g2 = (Graphics2D) g;

	// compute displayed beam rate in beams/sec
	
	_nBeamsForRate++;
	if (_nBeamsForRate == 100) {
	    long now = TimeManager.getTime();
	    double intervalSecs =
		(now - _startTimeForRate) / 1000.0;
	    _displayedBeamRate = 100 / intervalSecs;
	    _startTimeForRate = now;
	    _nBeamsForRate = 0;
	}
	
	// return now if no beam data
	
	if (_beam == null) {
	    // empty window
 	    g2.setColor(getBackground());
 	    g2.fill(new Rectangle(0, 0, getWidth(), getHeight()));
 	    return;
	}
	short[] counts = _beam.getCounts();

	// plot options

	_plotCount = _params.aScope.plotCount.getValue();
	_plotDbzSnr = _params.aScope.plotDbzSnr.getValue();

	// compute margins
	    
	Dimension size = getSize();
	Insets insets = getInsets();

	int topMargin =
	    insets.top + _params.aScope.topMargin.getValue();
	int bottomMargin =
	    insets.bottom + _params.aScope.bottomMargin.getValue();
	int leftMargin =
	    insets.left + _params.aScope.leftMargin.getValue();
	if (!_plotCount) {
	    leftMargin /= 2;
	}
	int rightMargin =
	    insets.right + _params.aScope.rightMargin.getValue();
	if (!_plotDbzSnr) {
	    rightMargin /= 2;
	}

	// gates and range

	_nGates = _beam.getNGates();
	_startRange = _beam.getStartRange();
	_gateSpacing = _beam.getGateSpacing();
	    
	_unZoomed.minGate = 0;
	_unZoomed.maxGate = _nGates - 1;
	_unZoomed.minRange = _startRange - _gateSpacing * 0.5;
	_unZoomed.maxRange = _startRange + (_nGates - 0.5) * _gateSpacing;
		
	// count limits

	_unZoomed.minCount = 0.0;
	_unZoomed.maxCount = _params.aScope.maxCount.getValue();
	    
	// dbz limits

	_unZoomed.minDbz = _params.aScope.minDbz.getValue();
	_unZoomed.maxDbz = _params.aScope.maxDbz.getValue();
	_unZoomed.minSnr = _params.aScope.minSnr.getValue();
	_unZoomed.maxSnr = _params.aScope.maxSnr.getValue();

	// set this zoom

	Zoomed thisZoom;
	if (_zoomed != null) {
	    thisZoom = _zoomed;
	} else {
	    thisZoom = _unZoomed;
	}

	// plot the axes to _axisImage

 	_plotAxes(_plotCount, _plotDbzSnr,
		  size.width, size.height,
 		  leftMargin, rightMargin,
 		  topMargin, bottomMargin,
 		  thisZoom, _field);
	    
	// copy axisImage into our graphics
	
 	g2.drawImage(_axisImage, 0, 0, null);
	
	// count trace
	    
	if (_plotCount) {
	    GeneralPath countPath = new GeneralPath();
	    for (int i = thisZoom.minGate; i <= thisZoom.maxGate; i++) {
		if (i == thisZoom.minGate) {
		    countPath.moveTo((float) (i + 0.5), counts[i]);
		} else {
		    countPath.lineTo((float) (i + 0.5), counts[i]);
		}
	    }
	    g2.setColor(_countColor);
	    _countWorld.drawPathClipped(g2, countPath);
	    g2.setColor(getForeground());
	}
	    
	// dBZ trace
	
	float dbz[] = _beam.getDbz();
	float snr[] = _beam.getSnr();
	double range[] = new double[_nGates];
	for (int i = 0; i < _nGates; i++) {
	    range[i] = _startRange + i * _gateSpacing;
	}
	
	if (_plotDbzSnr) {
	    if (_field == DataField.DBZ) {
		GeneralPath dbzPath = new GeneralPath();
		for (int i = thisZoom.minGate; i <= thisZoom.maxGate; i++) {
		    if (i == thisZoom.minGate) {
			dbzPath.moveTo((float) range[i], (float) dbz[i]);
		    } else {
			dbzPath.lineTo((float) range[i], (float) dbz[i]);
		    }
		}
		g2.setColor(_dbzColor);
		_dbzWorld.drawPathClipped(g2, dbzPath);
		g2.setColor(getForeground());
	    } else {
		GeneralPath snrPath = new GeneralPath();
		for (int i = thisZoom.minGate; i <= thisZoom.maxGate; i++) {
		    if (i == thisZoom.minGate) {
			snrPath.moveTo((float) range[i], (float) snr[i]);
		    } else {
			snrPath.lineTo((float) range[i], (float) snr[i]);
		    }
		}
		g2.setColor(_snrColor);
		_snrWorld.drawPathClipped(g2, snrPath);
		g2.setColor(getForeground());
	    }
	}

	// sampling
	    
	if (_performSampling) {

	    g2.setColor(new Color(0, 100, 0));
	    _countWorld.drawLine
		(g2,
		 _aScope.getSampleStartGate(), thisZoom.minCount,
		 _aScope.getSampleStartGate(), thisZoom.maxCount);
	    _countWorld.drawLine
		(g2,
		 _aScope.getSampleEndGate(), thisZoom.minCount,
		 _aScope.getSampleEndGate(), thisZoom.maxCount);
	    g2.setColor(getForeground());

	}

	// legends
	    
	ArrayList leftLegends = new ArrayList();
	leftLegends.add(new String("Time: " + _beam.getDateTimeString()));
	leftLegends.add
	    (new String("El: " + NFormat.f2.format(_beam.getEl())));
	leftLegends.add
	    (new String("Az: " + NFormat.f2.format(_beam.getAz())));
	leftLegends.add(new String("PRF: " + _beam.getPrf()));
	if (_params.aScope.plotBeamRate.getValue()) {
	    leftLegends.add
		(new String("Incoming beam rate: " +
			    NFormat.f2.format(_incomingBeamRate)));
	    leftLegends.add
		(new String("Displayed beam rate: " +
			    NFormat.f2.format(_displayedBeamRate)));
	}
	_countWorld.drawLegendsTopLeft(g2, leftLegends);
	    
	ArrayList rightLegends = new ArrayList();
	if (_performSampling) {
	    if (_plotCount) {
		rightLegends.add
		    (new String("Sample count: " +
				NFormat.f2.format(_aScope.getSampleCount())));
	    }
	    if (_plotDbzSnr) {
		if (_field == DataField.DBZ) {
		    rightLegends.add
			(new String("Average DBZ: " +
				    NFormat.f2.format(_aScope.getSampleDbz())));
		} else {
		    rightLegends.add
			(new String("Average SNR: " +
				    NFormat.f2.format(_aScope.getSampleSnr())));
		}
	    }
	    rightLegends.add
		(new String("Sample n beams: " +
			    NFormat.f0.format(_aScope.getSampleNBeamsUsed())));
	    rightLegends.add
		(new String("Sample n gates: " +
			    NFormat.f0.format(_aScope.getSampleNGatesUsed())));
 	    rightLegends.add
 		(new String("Sample mid gate: " +
 			    NFormat.f0.format(_aScope.getSampleMidGate())));
	    rightLegends.add
		(new String("Sample mid range(km): " +
			    NFormat.f2.format(_aScope.getSampleMidRange())));
	}
	_countWorld.drawLegendsTopRight(g2, rightLegends);
	    
    } // _paint

    //////////////////////////////////////////////
    // plot the axes if required
    
    private void _plotAxes(boolean plotCount,
			   boolean plotDbzSnr,
			   int width,
			   int height,
			   int leftMargin,
			   int rightMargin,
			   int topMargin,
			   int bottomMargin,
			   Zoomed zoom,
			   DataField field) {

	// determine if things have changed
	
	if (!_refreshAxes &&
	    plotCount == _axisPlotCount &&
	    plotDbzSnr == _axisPlotDbzSnr &&
	    width == _axisWidth &&
	    height == _axisHeight &&
	    leftMargin == _axisLeftMargin &&
	    rightMargin == _axisRightMargin &&
	    topMargin == _axisTopMargin &&
	    bottomMargin == _axisBottomMargin &&
	    zoom.isSame(_axisZoom) &&
	    field == _axisField) {
	    return;
	}

	// set up world plotting objects
	
	_countWorld = new WorldPlot(width, height,
				    leftMargin, rightMargin,
				    topMargin, bottomMargin,
				    zoom.minGate, zoom.maxGate,
				    zoom.minCount, zoom.maxCount);
	
	_dbzWorld = new WorldPlot(width, height,
				  leftMargin, rightMargin,
				  topMargin, bottomMargin,
				  zoom.minRange, zoom.maxRange,
				  zoom.minDbz, zoom.maxDbz);

	_snrWorld = new WorldPlot(width, height,
				  leftMargin, rightMargin,
				  topMargin, bottomMargin,
				  zoom.minRange, zoom.maxRange,
				  zoom.minSnr, zoom.maxSnr);

	// graphics contexts
	
	Graphics2D axisG2 = (Graphics2D) _axisGraphics;
	axisG2.setColor(getBackground());
	axisG2.fill(new Rectangle(0, 0, getWidth(), getHeight()));
	axisG2.setColor(getForeground());

	// title
	
	int defaultFontSize;
	if (_field == DataField.DBZ) {
	    _countWorld.drawTitleTopCenter(axisG2, "A-SCOPE - DBZ", false);
	} else {
	    _countWorld.drawTitleTopCenter(axisG2, "A-SCOPE - SNR", false);
	}

	// axes
	
	_countWorld.drawAxisBottom(axisG2, "gates", true, true, true);
	_dbzWorld.drawAxisTop(axisG2, "km", true, true, true);
	    
	if (plotCount) {
	    axisG2.setColor(_countColor);
	    _countWorld.drawAxisLeft(axisG2, "count", true, true, true);
	    axisG2.setColor(getForeground());
	} else {
	    _countWorld.drawLine(axisG2,
				 zoom.minGate, zoom.minCount,
				 zoom.minGate, zoom.maxCount);
	}
	    
	if (plotDbzSnr) {
	    if (_field == DataField.DBZ) {
		axisG2.setColor(_dbzColor);
		_dbzWorld.drawAxisRight(axisG2, "dBZ", true, true, true);
		axisG2.setColor(getForeground());
	    } else {
		axisG2.setColor(_snrColor);
		_snrWorld.drawAxisRight(axisG2, "SNR", true, true, true);
		axisG2.setColor(getForeground());
	    }
	} else {
	    _dbzWorld.drawLine(axisG2,
			       zoom.maxRange, zoom.minDbz,
			       zoom.maxRange, zoom.maxDbz);
	}

	// save context
	
	_axisPlotCount = plotCount;
	_axisPlotDbzSnr = plotDbzSnr;
	_axisWidth = width;
	_axisHeight = height;
	_axisLeftMargin = leftMargin;
	_axisRightMargin = rightMargin;
	_axisTopMargin = topMargin;
	_axisBottomMargin = bottomMargin;
	_axisZoom.copy(zoom);
	_axisField = field;
	_refreshAxes = false;
	
    }
	    
    // Inner class
    // Listener for changes in radar parameters
    
    private class RadarParamsChangeListener
	implements CollectionChangeListener {
	public void reactToChange() {
	    _refreshAxes = true;
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

	public int minGate;
	public int maxGate;
	public double minRange;
	public double maxRange;
	public double minCount;
	public double maxCount;
	public double minDbz;
	public double maxDbz;
	public double minSnr;
	public double maxSnr;

	public void print(PrintStream out) {
	    out.println("========= Zoom ==========");
	    out.println("  minGate: " + minGate);
	    out.println("  maxGate: " + maxGate);
	    out.println("  minRange: " + minRange);
	    out.println("  maxRange: " + maxRange);
	    out.println("  minCount: " + minCount);
	    out.println("  maxCount: " + maxCount);
	    out.println("  minDbz: " + minDbz);
	    out.println("  maxDbz: " + maxDbz);
	    out.println("  minSnr: " + minSnr);
	    out.println("  maxSnr: " + maxSnr);
	}

	public boolean isSame(Zoomed other) {
	    if (other == null) {
		return false;
	    }
	    if (minGate != other.minGate) {
		return false;
	    }
	    if (maxGate != other.maxGate) {
		return false;
	    }
	    if (minRange != other.minRange) {
		return false;
	    }
	    if (maxRange != other.maxRange) {
		return false;
	    }
	    if (minCount != other.minCount) {
		return false;
	    }
	    if (maxCount != other.maxCount) {
		return false;
	    }
	    if (minDbz != other.minDbz) {
		return false;
	    }
	    if (maxDbz != other.maxDbz) {
		return false;
	    }
	    if (minSnr != other.minSnr) {
		return false;
	    }
	    if (maxSnr != other.maxSnr) {
		return false;
	    }
	    return true;
	}

	public void copy(Zoomed other) {
	    minGate = other.minGate;
	    maxGate = other.maxGate;
	    minRange = other.minRange;
	    maxRange = other.maxRange;
	    minCount = other.minCount;
	    maxCount = other.maxCount;
	    minDbz = other.minDbz;
	    maxDbz = other.maxDbz;
	    minSnr = other.minSnr;
	    maxSnr = other.maxSnr;
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
	    // Be any smaller than is allowed by max_scale.
		
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
		zoomed.minGate = (int) _countWorld.getXWorld(zoomX1);
		zoomed.maxGate = (int) (_countWorld.getXWorld(zoomX2) + 1);
		if (_plotCount) {
		    zoomed.minCount = _countWorld.getYWorld(zoomY1);
		    zoomed.maxCount = _countWorld.getYWorld(zoomY2);
		} else {
		    if (_zoomed != null) {
			zoomed.minCount = _zoomed.minCount;
			zoomed.maxCount = _zoomed.maxCount;
		    } else {
			zoomed.minCount = _unZoomed.minCount;
			zoomed.maxCount = _unZoomed.maxCount;
		    }
		}
		if (_plotDbzSnr) {
		    zoomed.minDbz = _dbzWorld.getYWorld(zoomY1);
		    zoomed.maxDbz = _dbzWorld.getYWorld(zoomY2);
		    zoomed.minSnr = _snrWorld.getYWorld(zoomY1);
		    zoomed.maxSnr = _snrWorld.getYWorld(zoomY2);
		} else {
		    if (_zoomed != null) {
			zoomed.minDbz = _zoomed.minDbz;
			zoomed.maxDbz = _zoomed.maxDbz;
			zoomed.minSnr = _zoomed.minSnr;
			zoomed.maxSnr = _zoomed.maxSnr;
		    } else {
			zoomed.minDbz = _unZoomed.minDbz;
			zoomed.maxDbz = _unZoomed.maxDbz;
			zoomed.minSnr = _unZoomed.minSnr;
			zoomed.maxSnr = _unZoomed.maxSnr;
		    }
		}
		    
		if (zoomed.minGate < _unZoomed.minGate) {
		    zoomed.minGate = _unZoomed.minGate;
		}
		if (zoomed.maxGate > _unZoomed.maxGate) {
		    zoomed.maxGate = _unZoomed.maxGate;
		}
		if (zoomed.minCount < _unZoomed.minCount) {
		    zoomed.minCount = _unZoomed.minCount;
		}
		if (zoomed.maxCount > _unZoomed.maxCount) {
		    zoomed.maxCount = _unZoomed.maxCount;
		}
		if (zoomed.minDbz < _unZoomed.minDbz) {
		    zoomed.minDbz = _unZoomed.minDbz;
		}
		if (zoomed.maxDbz > _unZoomed.maxDbz) {
		    zoomed.maxDbz = _unZoomed.maxDbz;
		}
		if (zoomed.minSnr < _unZoomed.minSnr) {
		    zoomed.minSnr = _unZoomed.minSnr;
		}
		if (zoomed.maxSnr > _unZoomed.maxSnr) {
		    zoomed.maxSnr = _unZoomed.maxSnr;
		}
		zoomed.minRange =
		    _startRange + _gateSpacing * (zoomed.minGate - 0.5);
		zoomed.maxRange =
		    _startRange + _gateSpacing * (zoomed.maxGate + 0.5);

		_zoomed = zoomed;
		_aScope.setZoomState(true);

	    }
	    
	    _zooming = null;
	    doRepaint();
		
	} // mouseReleased
	    
    } // ZoomMouseListener

}
