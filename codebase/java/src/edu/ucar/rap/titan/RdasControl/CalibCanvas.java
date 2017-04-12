///////////////////////////////////////////////////////////////////////
//
// CalibCanvas
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

public class CalibCanvas extends JPanel
    
{
    
    private Parameters _params = Parameters.getInstance();
    private Calibrate _calib;
    
    private WorldPlot _world1km;
    private WorldPlot _worldCalRng;
    private double _rangeCorr1kmToCalRng;
	
    private Color _countColor = Color.RED;
    
    private boolean _zoomingRectangleEnabled = true;
    private Zooming _zooming = null;     // null if not currently zooming
    private Zoomed _unZoomed;            // always exists
    private Zoomed _zoomed = null;       // null if not zoomed
    private Zoomed _prevZoom;            // keeps track of previous zoom

    private int _bgImageWidth = 0;
    private int _bgImageHeight = 0;
    private Image _bgImage = null;
    private Image _axisImage = null;
    private Graphics _scGraphics = null;
    private Graphics _bgGraphics = null;
    private Graphics _axisGraphics = null;

    public CalibCanvas(Calibrate calib) {
	
	_calib = calib;
	_unZoomed = new Zoomed();
	ZoomMouseListener zoomListener = new ZoomMouseListener();
	addMouseListener(zoomListener);
	addMouseMotionListener(zoomListener);

    }
    
    // set methods
    
    public void setUnzoomed() {
	_zoomed = null;
    }

    //////////////////////////////////////////////////////////
    // do a repaint - normally called when a new beam arrives
    
    public void doRepaint() {
	if (_zooming == null) {
	    repaint();
	}
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

	// compute margins
	
	Dimension size = getSize();
	Insets insets = getInsets();
	
	int topMargin =
	    insets.top + _params.calib.topMargin.getValue();
	int bottomMargin =
	    insets.bottom + _params.calib.bottomMargin.getValue();
	int leftMargin =
	    insets.left + _params.calib.leftMargin.getValue();
	int rightMargin =
	    insets.right + _params.calib.rightMargin.getValue();

	// init zoom

	_unZoomed.minDbz = _params.calib.minDbz.getValue()
	    - _params.calib.deltaDbz.getValue();
	_unZoomed.maxDbz = _params.calib.minDbz.getValue() +
	    (_params.calib.nPointsTable.getValue() *
	     _params.calib.deltaDbz.getValue());
	
	double fitDbzLow = _params.calib.fitDbzLow.getValue();
	double fitDbzHigh = _params.calib.fitDbzHigh.getValue();

	double minCount = 0.0;
	double maxCount = _params.eng.maxCount.getValue();

	_unZoomed.minCount = minCount;
	_unZoomed.maxCount = maxCount;
	
	// set this zoom
	
	Zoomed thisZoom;
	if (_zoomed != null) {
	    thisZoom = _zoomed;
	} else {
	    thisZoom = _unZoomed;
	}

	// plot the axes to _axisImage
	
 	_plotAxes(size.width, size.height,
 		  leftMargin, rightMargin,
 		  topMargin, bottomMargin,
 		  thisZoom);
	
	// copy axisImage into our graphics
	
 	g2.drawImage(_axisImage, 0, 0, null);
	
	// min and max DBZ lines

	g2.setColor(new Color(0, 100, 0));
	_worldCalRng.drawLine(g2,
			     fitDbzLow, thisZoom.minCount,
			     fitDbzLow, thisZoom.maxCount);
	_worldCalRng.drawLine(g2,
			     fitDbzHigh, thisZoom.minCount,
			     fitDbzHigh, thisZoom.maxCount);
	g2.setColor(getForeground());

	// measured points
	    
 	GeneralPath countPath = new GeneralPath();
 	for (int ii = 0; ii < _calib.getNPoints(); ii++) {
	    try {
		float dbz = (float) _calib.getDbzCalRngVal(ii);
		float count = (float) _calib.getCountVal(ii);
		if (ii == 0) {
		    countPath.moveTo(dbz, count);
		} else {
		    countPath.lineTo(dbz, count);
		}
	    }
	    catch (IndexOutOfBoundsException e) {
	    }
 	}
 	g2.setColor(_countColor);
	g2.setStroke(new BasicStroke(3));
 	_worldCalRng.drawPathClipped(g2, countPath);
 	g2.setColor(getForeground());
	g2.setStroke(new BasicStroke(1));

	// linear fit

	double lowCountCalRng = _calib.getCalOffsetCalRng() +
	    _calib.getCalSlope() * _unZoomed.minDbz;
	double highCountCalRng = _calib.getCalOffsetCalRng() +
	    _calib.getCalSlope() * _unZoomed.maxDbz;
	
 	GeneralPath fitPath = new GeneralPath();
	fitPath.moveTo((float) _unZoomed.minDbz, (float) lowCountCalRng);
	fitPath.lineTo((float) _unZoomed.maxDbz, (float) highCountCalRng);
 	_worldCalRng.drawPathClipped(g2, fitPath);

	// legends
	    
	ArrayList leftLegends = new ArrayList();
	Calendar cal = TimeManager.getCal();
	cal.setTime(new Date());
	String timeStr = new
	    String(cal.get(Calendar.YEAR) +
		   "/" + (cal.get(Calendar.MONTH) + 1) +
		   "/" + cal.get(Calendar.DAY_OF_MONTH) +
		   " " + cal.get(Calendar.HOUR_OF_DAY) +
		   ":" + cal.get(Calendar.MINUTE) +
		   ":" + cal.get(Calendar.SECOND));
	leftLegends.add(new String("Time: " + timeStr));
	leftLegends.add
	    (new String("Fit dBZ low: " + NFormat.f0.format(fitDbzLow)));
	leftLegends.add
	    (new String("Fit dBZ high: " + NFormat.f0.format(fitDbzHigh)));
	leftLegends.add
	    (new String("slope: " + NFormat.f2.format(_calib.getCalSlope())));
	leftLegends.add
	    (new String("offset-1km: " +
			NFormat.f2.format(_calib.getCalOffset1km())));
	leftLegends.add
	    (new String("offset-" +
			NFormat.f0.format(_params.calib.calRange.getValue()) +
			"km: " +
			NFormat.f2.format(_calib.getCalOffsetCalRng())));
	_worldCalRng.drawLegendsTopLeft(g2, leftLegends);
	
	// ArrayList rightLegends = new ArrayList();
	// _world1km.drawLegendsTopRight(g2, rightLegends);
	    
    } // _paint

    //////////////////////////////////////////////
    // plot the axes if required
    
    private void _plotAxes(int width,
			   int height,
			   int leftMargin,
			   int rightMargin,
			   int topMargin,
			   int bottomMargin,
			   Zoomed zoom) {

	// set up world plotting objects
	
	_worldCalRng = new WorldPlot(width, height,
				    leftMargin, rightMargin,
				    topMargin, bottomMargin,
				    zoom.minDbz, zoom.maxDbz,
				    zoom.minCount, zoom.maxCount);
	
	_rangeCorr1kmToCalRng = 40.0;
	
	_world1km = new WorldPlot(width, height,
				  leftMargin, rightMargin,
				  topMargin, bottomMargin,
				  zoom.minDbz - _rangeCorr1kmToCalRng,
				  zoom.maxDbz - _rangeCorr1kmToCalRng,
				  zoom.minCount, zoom.maxCount);
	
	// graphics contexts
	
	Graphics2D g2 = (Graphics2D) _axisGraphics;
	g2.setColor(getBackground());
	g2.fill(new Rectangle(0, 0, getWidth(), getHeight()));
	g2.setColor(getForeground());

	// title
	
	int defaultFontSize;
	// _world1km.drawTitleTopCenter(g2, "CALIBRATION", false);
	
	// bottom axis - dBZ at 100 km
	
	_worldCalRng.drawAxisBottom(g2, _calib.getDbzCalRngStr(),
				    true, true, true);
	_worldCalRng.drawLine(g2,
			     zoom.minDbz, zoom.maxCount,
			     zoom.maxDbz, zoom.maxCount);
	
	g2.setColor(_countColor);
	_worldCalRng.drawAxisLeft(g2, "count", true, true, true);
	_worldCalRng.drawLine(g2,
			     zoom.maxDbz, zoom.minCount,
			     zoom.maxDbz, zoom.maxCount);
	g2.setColor(getForeground());
	
	// top axis - dbz at 1km
	
	_world1km.drawAxisTop(g2, "dBZ-1km", true, true, true);

	// draw tick marks at offset points

	g2.setColor(_countColor);

	double countRange = zoom.maxCount - zoom.minCount;
	double countTick = countRange / 20.0;

	_world1km.drawLine(g2,
			   0.0, _calib.getCalOffset1km() - countTick,
			   0.0, _calib.getCalOffset1km() + countTick);

	_worldCalRng.drawLine(g2,
			     0.0, _calib.getCalOffsetCalRng() - countTick,
			     0.0, _calib.getCalOffsetCalRng() + countTick);

	double dbzRange = zoom.maxDbz - zoom.minDbz;
	double dbzTick = dbzRange / 30.0;

	_world1km.drawLine(g2,
			   -dbzTick, _calib.getCalOffset1km(),
			   dbzTick, _calib.getCalOffset1km());
	_worldCalRng.drawLine(g2,
			     -dbzTick, _calib.getCalOffsetCalRng(),
			     dbzTick, _calib.getCalOffsetCalRng());

	g2.setColor(getForeground());

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

	public double minCount;
	public double maxCount;
	public double minDbz;
	public double maxDbz;

	public void print(PrintStream out) {
	    out.println("========= Zoom ==========");
	    out.println("  minCount: " + minCount);
	    out.println("  maxCount: " + maxCount);
	    out.println("  minDbz: " + minDbz);
	    out.println("  maxDbz: " + maxDbz);
	}

	public boolean isSame(Zoomed other) {
	    if (other == null) {
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
	    return true;
	}
	
	public void copy(Zoomed other) {
	    minCount = other.minCount;
	    maxCount = other.maxCount;
	    minDbz = other.minDbz;
	    maxDbz = other.maxDbz;
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
		
	    // reverse the previous zooming rectangle outline
	    // if this is not the 1st

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

		zoomed.minDbz = (int) _worldCalRng.getXWorld(zoomX1);
		zoomed.maxDbz = (int) (_worldCalRng.getXWorld(zoomX2) + 1);
		zoomed.minCount = _worldCalRng.getYWorld(zoomY1);
		zoomed.maxCount = _worldCalRng.getYWorld(zoomY2);

		if (zoomed.minDbz < _unZoomed.minDbz) {
		    zoomed.minDbz = _unZoomed.minDbz;
		}
		if (zoomed.maxDbz > _unZoomed.maxDbz) {
		    zoomed.maxDbz = _unZoomed.maxDbz;
		}
		if (zoomed.minCount < _unZoomed.minCount) {
		    zoomed.minCount = _unZoomed.minCount;
		}
		if (zoomed.maxCount > _unZoomed.maxCount) {
		    zoomed.maxCount = _unZoomed.maxCount;
		}
		
		_zoomed = zoomed;
		_calib.setZoomState(true);
		
	    }
	    
	    _zooming = null;
	    doRepaint();
		
	} // mouseReleased
	    
    } // ZoomMouseListener

}
