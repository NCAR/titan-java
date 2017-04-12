///////////////////////////////////////////////////////////////////////
//
// WorldPlot
//
// World-coord plotting.
//
// Mike Dixon
//
// May 2013
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RapidDowControl;

import java.awt.*;
import java.util.*;
import java.awt.geom.*;
import java.awt.image.*;

public class WorldPlot

{

    public static int HORIZ_CENTER = 0;
    public static int HORIZ_LEFT = 1;
    public static int HORIZ_RIGHT = 2;
    public static int VERT_CENTER = 3;
    public static int VERT_TOP = 4;
    public static int VERT_BOTTOM = 5;
    
    private int _deviceWidth;
    private int _deviceHeight;
  
    private int _leftMargin;
    private int _rightMargin;
    private int _topMargin;
    private int _bottomMargin;
    
    protected double _xMinWorld;
    protected double _xMaxWorld;
    protected double _yMinWorld;
    protected double _yMaxWorld;
  
    private int _plotWidth;
    private int _plotHeight;
  
    private int _xMinDevice;
    private int _yMinDevice;
    private int _xMaxDevice;
    private int _yMaxDevice;
  
    private double _xPixelsPerWorld;
    private double _yPixelsPerWorld;

    private int _axisTickLen = 7;
    private int _nTicksIdeal = 7;
    private int _textSpacer = 5;
    
    private AffineTransform _transform;

    // normal constructor
  
    public WorldPlot(int deviceWidth,
		     int deviceHeight,
		     int leftMargin,
		     int rightMargin,
		     int topMargin,
		     int bottomMargin,
		     double xMinWorld,
		     double xMaxWorld,
		     double yMinWorld,
		     double yMaxWorld) {
	
	_deviceWidth = deviceWidth;
	_deviceHeight = deviceHeight;
    
	_leftMargin = leftMargin;
	_rightMargin = rightMargin;
	_topMargin = topMargin;
	_bottomMargin = bottomMargin;
    
	_xMinWorld = xMinWorld;
	_xMaxWorld = xMaxWorld;
	_yMinWorld = yMinWorld;
	_yMaxWorld = yMaxWorld;

	_computeTransform();
    
    }

    // copy constructor
  
    public WorldPlot(WorldPlot orig) {
	
	_deviceWidth = orig._deviceWidth;
	_deviceHeight = orig._deviceHeight;
    
	_leftMargin = orig._leftMargin;
	_rightMargin = orig._rightMargin;
	_topMargin = orig._topMargin;
	_bottomMargin = orig._bottomMargin;
    
	_xMinWorld = orig._xMinWorld;
	_xMaxWorld = orig._xMaxWorld;
	_yMinWorld = orig._yMinWorld;
	_yMaxWorld = orig._yMaxWorld;

	_computeTransform();
    
    }

    // compute transform from basic values
    
    private void _computeTransform() {
	
	_plotWidth = _deviceWidth - _leftMargin - _rightMargin;
	_plotHeight = _deviceHeight - _topMargin - _bottomMargin;
	
	_xMinDevice = _leftMargin;
	_xMaxDevice = _xMinDevice + _plotWidth - 1;
	_yMinDevice = _topMargin + _plotHeight - 1;
	_yMaxDevice = _topMargin;

	_xPixelsPerWorld =
	    (_xMaxDevice - _xMinDevice) / (_xMaxWorld - _xMinWorld);
	_yPixelsPerWorld =
	    (_yMaxDevice - _yMinDevice) / (_yMaxWorld - _yMinWorld);
	
	_transform = new AffineTransform();
	_transform.translate(_xMinDevice, _yMinDevice);
	_transform.scale(_xPixelsPerWorld, _yPixelsPerWorld);
	_transform.translate(-_xMinWorld, -_yMinWorld);

    }

    public void setYscaleFromXscale() {

	if (_yPixelsPerWorld * _xPixelsPerWorld < 0) {
	    _yPixelsPerWorld = _xPixelsPerWorld * -1.0;
	} else {
	    _yPixelsPerWorld = _xPixelsPerWorld;
	}
	double yMean = (_yMaxWorld + _yMinWorld) / 2.0;
	double yHalf = ((_yMaxDevice - _yMinDevice) / 2.0) / _yPixelsPerWorld;
	_yMinWorld = yMean - yHalf; 
	_yMaxWorld = yMean + yHalf;
	_computeTransform();
	
    }

    public void setAxisTickLen(int len) {
	_axisTickLen = len;
    }

    public void setNTicksIdeal(int nTicks) {
	_nTicksIdeal = nTicks;
    }

    public int getDeviceWidth() {
	return _deviceWidth;
    }
  
    public int getDeviceHeight() {
	return _deviceHeight;
    }
  
    public int getLeftMargin() {
	return _leftMargin;
    }
  
    public int getRightMargin() {
	return _rightMargin;
    }
  
    public int getTopMargin() {
	return _topMargin;
    }
  
    public int getBottomMargin() {
	return _bottomMargin;
    }
  
    public int getPlotWidth() {
	return _plotWidth;
    }
  
    public int getPlotHeight() {
	return _plotHeight;
    }
  
    public double getXMinWorld() {
	return _xMinWorld;
    }
  
    public double getXMaxWorld() {
	return _xMaxWorld;
    }
  
    public double getYMinWorld() {
	return _yMinWorld;
    }
  
    public double getYMaxWorld() {
	return _yMaxWorld;
    }
  
    public double getXDevice(double xWorld) {
	return (xWorld - _xMinWorld) * _xPixelsPerWorld + _xMinDevice;
    }
  
    public double getYDevice(double yWorld) {
	return (yWorld - _yMinWorld) * _yPixelsPerWorld + _yMinDevice;
    }

    public int getIxDevice(double xWorld) {
	return (int) getXDevice(xWorld);
    }
    
    public int getIyDevice(double yWorld) {
	return (int) getYDevice(yWorld);
    }
  
    public double getXWorld(double xDevice) {
	return (xDevice - _xMinDevice) / _xPixelsPerWorld + _xMinWorld;
    }
  
    public double getYWorld(double yDevice) {
	return (yDevice - _yMinDevice) / _yPixelsPerWorld + _yMinWorld;
    }
  
    public double getXPixelsPerWorld() {
	return _xPixelsPerWorld;
    }
  
    public double getYPixelsPerWorld() {
	return _yPixelsPerWorld;
    }
  
    // draw a line in icon coords

    public void drawIconLine(Graphics2D g2,
			     double xx1, double yy1,
			     double xx2, double yy2) {
	
	g2.draw(new Line2D.Double(xx1, yy1, xx2, yy2));

    }

    // draw a line in world coords

    public void drawLine(Graphics2D g2,
			 double x1, double y1,
			 double x2, double y2) {
	
	double xx1 = getXDevice(x1);
	double yy1 = getYDevice(y1);
	double xx2 = xx1 + (x2 - x1) * _xPixelsPerWorld;
	double yy2 = yy1 + (y2 - y1) * _yPixelsPerWorld;
	g2.draw(new Line2D.Double(xx1, yy1, xx2, yy2));

    }

    // draw lines

    public void drawLines(Graphics2D g2, Point2D.Double[] points) {

	GeneralPath path = new GeneralPath();
    
	float xx = (float) getXDevice(points[0].x);
	float yy = (float) getXDevice(points[0].y);
	path.moveTo(xx, yy);
	for (int i = 1; i < points.length; i++) {
	    xx = (float) getXDevice(points[i].x);
	    yy = (float) getYDevice(points[i].y);
	    path.lineTo(xx, yy);
	}
	g2.draw(path);

    }

    // draw a rectangle

    public void drawRectangle(Graphics2D g2,
			      double x, double y,
			      double w, double h) {

	float xx = (float) getXDevice(x);
	float yy = (float) getYDevice(y);
	float ww = (float) (w * _xPixelsPerWorld);
	float hh = (float) (h * _yPixelsPerWorld * -1.0);
	yy -= hh;
	g2.draw(new Rectangle2D.Float(xx, yy, ww, hh));

    }

    // fill a rectangle

    public void fillRectangle(Graphics2D g2,
			      double x, double y,
			      double w, double h) {

	float xx = (float) getXDevice(x);
	float yy = (float) getYDevice(y);
	float ww = (float) (w * _xPixelsPerWorld);
	float hh = (float) (h * _yPixelsPerWorld * -1.0);
	yy -= hh;
	g2.fill(new Rectangle2D.Float(xx, yy, ww, hh));

    }

    // draw an arc

    public void drawArc(Graphics2D g2,
			double x, double y,
			double w, double h,
			double startAngle, double arcAngle) {
	
	float xx = (float) getXDevice(x);
	float yy = (float) getYDevice(y);
	float ww = (float) (w * _xPixelsPerWorld);
	float hh = (float) (h * _yPixelsPerWorld * -1.0);
	g2.draw(new Arc2D.Float(xx, yy, ww, hh,
				(float) startAngle, (float) arcAngle,
				Arc2D.Float.OPEN));

    }

    // draw a general path

    public void drawPath(Graphics2D g2, GeneralPath path) {
	path.transform(_transform);
	g2.draw(path);
    }

    // draw a general path clipped to within the margins

    public void drawPathClipped(Graphics2D g2, GeneralPath path) {
	g2.setClip(_xMinDevice, _yMaxDevice, _plotWidth,  _plotHeight);
	path.transform(_transform);
	g2.draw(path);
	g2.setClip(null);
    }

    // Text
    //
    // Justification is:
    //   HORIZ_CENTER, HORIZ_LEFT, HORIZ_RIGHT,
    //   VERT_CENTER, VERT_TOP, VERT_BOTTOM
    
    public void drawText(Graphics2D g2, String text,
			 double text_x, double text_y,
			 int horizJust, int vertJust) {
	
	double xx = getXDevice(text_x);
	double yy = getYDevice(text_y);
	
	FontMetrics metrics = g2.getFontMetrics();
	Rectangle2D rect = metrics.getStringBounds(text, g2);

	if (horizJust == HORIZ_CENTER) {
	    xx -= rect.getWidth() / 2;
	} else if (horizJust == HORIZ_RIGHT) {
	    xx -= rect.getWidth();
	}
	if (vertJust == VERT_CENTER) {
	    yy += rect.getHeight() / 2;
	} else if (vertJust == VERT_BOTTOM) {
	    yy += rect.getHeight();
	}
	
	g2.drawString(text, (float) xx, (float) yy);

    }
	
    public void drawTextCentered(Graphics2D g2, String text,
				 double text_x, double text_y) {
	drawText(g2, text, text_x, text_y, HORIZ_CENTER, VERT_CENTER);
    }
	
    // Title
    
    public void drawTitleTopCenter(Graphics2D g2, String title,
				   boolean above) {
	
	Font currentFont = g2.getFont();
	float fontSize = currentFont.getSize2D();
	Font titleFont = currentFont.deriveFont((float) (fontSize * 1.5));
	g2.setFont(titleFont);
	FontMetrics metrics = g2.getFontMetrics();
	Rectangle2D rect = metrics.getStringBounds(title, g2);
	float xx = (float)
	    ((_xMinDevice + _xMaxDevice - rect.getWidth()) / 2.0);
	float yy;
	if (above) {
	    yy = (float)  rect.getHeight();
	} else {
	    yy = (float) (_yMaxDevice + _axisTickLen +
			  _textSpacer + rect.getHeight());
	}
	g2.drawString(title, xx, yy);
	g2.setFont(currentFont);
	
    }
	
    // Legends at top left
    
    public void drawLegendsTopLeft(Graphics2D g2, ArrayList legends) {

	for (int i = 0; i < legends.size(); i++) {
	    String legend = (String) legends.get(i);
	    FontMetrics metrics = g2.getFontMetrics();
	    Rectangle2D rect = metrics.getStringBounds(legend, g2);
	    float xx = (float) (_xMinDevice + _axisTickLen + _textSpacer);
	    float yy = (float) (_yMaxDevice + _axisTickLen +
				i * _textSpacer +
				(i + 1) * rect.getHeight());
	    g2.drawString(legend, xx, yy);
	}

    }
	
    // Legends at top right
    
    public void drawLegendsTopRight(Graphics2D g2, ArrayList legends) {

	for (int i = 0; i < legends.size(); i++) {
	    String legend = (String) legends.get(i);
	    FontMetrics metrics = g2.getFontMetrics();
	    Rectangle2D rect = metrics.getStringBounds(legend, g2);
	    float xx = (float) (_xMaxDevice - _axisTickLen -
				_textSpacer - rect.getWidth());
	    float yy = (float) (_yMaxDevice + _axisTickLen +
				i * _textSpacer +
				(i + 1) * rect.getHeight());
	    g2.drawString(legend, xx, yy);
	}

    }
	
    // Legends at bottom left
    
    public void drawLegendsBottomLeft(Graphics2D g2, ArrayList legends) {
	
	for (int i = 0; i < legends.size(); i++) {
	    String legend = (String) legends.get(i);
	    FontMetrics metrics = g2.getFontMetrics();
	    Rectangle2D rect = metrics.getStringBounds(legend, g2);
	    float xx = (float) (_xMinDevice + _axisTickLen + _textSpacer);
	    float yy = (float) (_yMinDevice - _axisTickLen -
				i * _textSpacer -
				(i + 1) * rect.getHeight());
	    g2.drawString(legend, xx, yy);
	}

    }
	
    // Legends at bottom right
    
    public void drawLegendsBottomRight(Graphics2D g2, ArrayList legends) {
	
	for (int i = 0; i < legends.size(); i++) {
	    String legend = (String) legends.get(i);
	    FontMetrics metrics = g2.getFontMetrics();
	    Rectangle2D rect = metrics.getStringBounds(legend, g2);
	    float xx = (float) (_xMaxDevice - _axisTickLen -
				_textSpacer - rect.getWidth());
	    float yy = (float) (_yMinDevice - _axisTickLen -
				i * _textSpacer -
				(i + 1) * rect.getHeight());
	    g2.drawString(legend, xx, yy);
	}

    }
	
    // left axis
    
    public void drawAxisLeft(Graphics2D g2, String units,
			     boolean doLine, boolean doTicks,
			     boolean doLabels) {
	
	// axis line

	if (doLine) {
	    drawLine(g2, _xMinWorld, _yMinWorld, _xMinWorld, _yMaxWorld);
	}

	// axis units label

	FontMetrics metrics = g2.getFontMetrics();
	Rectangle2D unitsRect = metrics.getStringBounds(units, g2);
	float unitsX =
	    (float) (_xMinDevice - unitsRect.getWidth() - _textSpacer);
	float unitsY = (float) (_yMaxDevice + (unitsRect.getHeight() / 2));
	if (doLabels) {
	    g2.drawString(units, unitsX, unitsY);
	}

	// tick marks

	double[] ticks = linearTicks(_yMinWorld, _yMaxWorld, _nTicksIdeal);
	if (ticks.length < 2) {
	    return;
	}
	
	double delta = ticks[1] - ticks[0];
	for (int i = 0; i < ticks.length; i++) {
	    
	    double val = ticks[i];
	    double pix = getYDevice(val);
	    if (doTicks) {
		g2.draw(new Line2D.Double(_xMinDevice, pix,
					  _xMinDevice + _axisTickLen, pix));
	    }
	    
	    String label = getAxisLabel(delta, val);
	    Rectangle2D rect = metrics.getStringBounds(label, g2);
	    float strX = (float) (_xMinDevice - rect.getWidth() - _textSpacer);
	    float strY = (float) (pix + (rect.getHeight() / 2));
	    if (Math.abs(strY - unitsY) > rect.getHeight() + _textSpacer) {
		if (doLabels) {
		    g2.drawString(label, strX, strY);
		}
	    }
	    
	}
	
    } // drawAxisLeft

    // right axis
  
    public void drawAxisRight(Graphics2D g2, String units,
			      boolean doLine, boolean doTicks,
			      boolean doLabels) {
	
	// axis line

	if (doLine) {
	    drawLine(g2, _xMaxWorld, _yMinWorld, _xMaxWorld, _yMaxWorld);
	}

	// axis units label
	
	FontMetrics metrics = g2.getFontMetrics();
	Rectangle2D unitsRect = metrics.getStringBounds(units, g2);
	float unitsX = (float) (_xMaxDevice + _textSpacer);
	float unitsY = (float) (_yMaxDevice + (unitsRect.getHeight() / 2));
	if (doLabels) {
	    g2.drawString(units, unitsX, unitsY);
	}

	// tick marks

	double[] ticks = linearTicks(_yMinWorld, _yMaxWorld, _nTicksIdeal);
	if (ticks.length < 2) {
	    return;
	}
	
	double delta = ticks[1] - ticks[0];
	for (int i = 0; i < ticks.length; i++) {
	    
	    double val = ticks[i];
	    double pix = getYDevice(val);
	    if (doTicks) {
		g2.draw(new Line2D.Double(_xMaxDevice, pix,
					  _xMaxDevice - _axisTickLen, pix));
	    }
	    
	    String label = getAxisLabel(delta, val);
	    Rectangle2D rect = metrics.getStringBounds(label, g2);
	    float strX = (float) (_xMaxDevice + _textSpacer);
	    float strY = (float) (pix + (rect.getHeight() / 2));
	    if (Math.abs(strY - unitsY) > rect.getHeight() + _textSpacer) {
		if (doLabels) {
		    g2.drawString(label, strX, strY);
		}
	    }
	    
	}
	
    } // drawAxisRight
  
    // bottom axis
    
    public void drawAxisBottom(Graphics2D g2,
			       String units, boolean doLine,
			       boolean doTicks, boolean doLabels) {
	
	// axis line
	
	if (doLine) {
	    drawLine(g2, _xMinWorld, _yMinWorld, _xMaxWorld, _yMinWorld);
	}
	
	// axis units label
	
	FontMetrics metrics = g2.getFontMetrics();
	Rectangle2D unitsRect = metrics.getStringBounds(units, g2);
	float unitsX = (float) (_xMaxDevice - unitsRect.getWidth() / 2);
	float unitsY =
	    (float) (_yMinDevice + (unitsRect.getHeight() + _textSpacer));
	if (doLabels) {
	    g2.drawString(units, unitsX, unitsY);
	}

	// tick marks

	double[] ticks = linearTicks(_xMinWorld, _xMaxWorld, _nTicksIdeal);
	if (ticks.length < 2) {
	    return;
	}
	
	double delta = ticks[1] - ticks[0];
	for (int i = 0; i < ticks.length; i++) {
	    
	    double val = ticks[i];
	    double pix = getXDevice(val);
	    if (doTicks) {
		g2.draw(new Line2D.Double(pix, _yMinDevice,
					  pix, _yMinDevice - _axisTickLen));
	    }
	    
	    String label = getAxisLabel(delta, val);
	    Rectangle2D rect = metrics.getStringBounds(label, g2);
	    if ((pix + rect.getWidth() / 2 + _textSpacer) < unitsX) {
		float strX = (float) (pix - rect.getWidth() / 2);
		float strY = unitsY;
		if (doLabels) {
		    g2.drawString(label, strX, strY);
		}
	    }
	    
	}
	
    } // drawAxisBottom
  
    // top axis
    
    public void drawAxisTop(Graphics2D g2,
			    String units, boolean doLine,
			    boolean doTicks, boolean doLabels) {
	
	// axis line
	
	if (doLine) {
	    drawLine(g2, _xMinWorld, _yMaxWorld, _xMaxWorld, _yMaxWorld);
	}
	
	// axis units label
	
	FontMetrics metrics = g2.getFontMetrics();
	Rectangle2D unitsRect = metrics.getStringBounds(units, g2);
	float unitsX =
	    (float) (_xMaxDevice - unitsRect.getWidth() / 2);
	float unitsY =
	    (float) (_yMaxDevice - (unitsRect.getHeight() - _textSpacer));

	if (doLabels) {
	    g2.drawString(units, unitsX, unitsY);
	}

	// tick marks
	
	double[] ticks = linearTicks(_xMinWorld, _xMaxWorld, _nTicksIdeal);
	if (ticks.length < 2) {
	    return;
	}

	double delta = ticks[1] - ticks[0];
	for (int i = 0; i < ticks.length; i++) {
	    
	    double val = ticks[i];
	    double pix = getXDevice(val);
	    if (doTicks) {
		g2.draw(new Line2D.Double(pix, _yMaxDevice,
					  pix, _yMaxDevice + _axisTickLen));
	    }
	    
	    String label = getAxisLabel(delta, val);
	    Rectangle2D rect = metrics.getStringBounds(label, g2);
	    if ((pix + rect.getWidth() / 2 + _textSpacer) < unitsX) {
		float strX = (float) (pix - rect.getWidth() / 2);
		float strY = unitsY;
		if (doLabels) {
		    g2.drawString(label, strX, strY);
		}
	    }
	    
	}
	
    } // drawAxisTop
  
    // compute linear ticks

    static public double[] linearTicks(double minVal,
				       double maxVal,
				       long approxNticks) {
	
	double  approxInterval =
	    (maxVal - minVal) / (double) (approxNticks + 1);
	double logInterval = MathUtils.log10(Math.abs(approxInterval));
	double intPart = (int) logInterval;
	double fractPart = logInterval - intPart;

	if (fractPart < 0) {
	    fractPart += 1.0;
	    intPart -= 1.0;
	}
	
	double rem = Math.pow(10.0, fractPart);
	double base;
	if (rem > 7.5) {
	    base = 10.0;
	} else if (rem > 3.5) {
	    base = 5.0;
	} else if (rem > 1.5) {
	    base = 2.0;
	} else {
	    base = 1.0;
	}
	
	double deltaTick = (base * Math.pow (10.0, intPart));
	double tickMin = Math.floor(minVal / deltaTick) * deltaTick;
	if (tickMin < minVal) {
	  tickMin += deltaTick;
	}
	int nTicks = (int) ((maxVal - tickMin) / deltaTick) + 1;
	if (nTicks < 2) {
	    nTicks = 2;
	}
	
	double ticks[] = new double[nTicks];
	for (int i = 0; i < nTicks; i++) {
	    ticks[i] = tickMin + i * deltaTick;
	}

	return ticks;
	
    }

    // get axis label given value and delta
    
    private String getAxisLabel(double delta, double val) {

	if (delta >= 1.0) {
	    return NFormat.f0.format(val);
	} else if (delta >= 0.1) {
	    return NFormat.f1.format(val);
	} else if (delta >= 0.01) {
	    return NFormat.f2.format(val);
	} else if (delta >= 0.001) {
	    return NFormat.f3.format(val);
	} else if (delta >= 0.0001) {
	    return NFormat.f4.format(val);
	} else {
	    return NFormat.e3.format(val);
	}

    }
    
    // draw source image into graphics, scaling and translating to
    // map the world coordinates
    
    public void drawImage(Graphics2D g2, BufferedImage image,
			  double xMinWorldImage, double xMaxWorldImage,
			  double yMinWorldImage, double yMaxWorldImage) {
	
	double xPixelsPerWorldImage =
	    (double) image.getWidth() / (xMaxWorldImage - xMinWorldImage);
	double yPixelsPerWorldImage = -1.0 *
	    (double) image.getHeight() / (yMaxWorldImage - yMinWorldImage);

	double xMinDeviceDest = getXDevice(xMinWorldImage);
	double yMaxDeviceDest = getYDevice(yMaxWorldImage);
  
	double xScale = _xPixelsPerWorld / xPixelsPerWorldImage;
	double yScale = _yPixelsPerWorld / yPixelsPerWorldImage;
	
	double transX = xMinDeviceDest;
	double transY = yMaxDeviceDest;

//   	System.out.println("\n");
//   	System.out.println("\n");
//   	System.out.println("------>> destWorld:\n" + this);
//   	System.out.println("------>> srcImage\n");
//   	System.out.println("  xMinWorldImage: " + xMinWorldImage);
//   	System.out.println("  xMaxWorldImage: " + xMaxWorldImage);
//   	System.out.println("  yMinWorldImage: " + yMinWorldImage);
//   	System.out.println("  yMaxWorldImage: " + yMaxWorldImage);
//   	System.out.println("  image width: " + image.getWidth());
//   	System.out.println("  image height: " + image.getHeight());
//   	System.out.println("  xMinDeviceDest: " + xMinDeviceDest);
//   	System.out.println("  yMaxDeviceDest: " + yMaxDeviceDest);
//   	System.out.println("  xPixelsPerWorldImage: " + xPixelsPerWorldImage);
//   	System.out.println("  yPixelsPerWorldImage: " + yPixelsPerWorldImage);
//   	System.out.println("  xScale: " + xScale);
//   	System.out.println("  yScale: " + yScale);
//   	System.out.println("  transX: " + transX);
//   	System.out.println("  transY: " + transY);
	
	AffineTransform transform = new AffineTransform();
	transform.translate(transX, transY);
	transform.scale(xScale, yScale);

	setClippingOn(g2);
	g2.drawImage(image, transform, null);
	setClippingOff(g2);
	
    }

    // set clipping on to between margins

    public void setClippingOn(Graphics2D g2) {
	g2.setClip(_xMinDevice, _yMaxDevice, _plotWidth,  _plotHeight);
    }

    // set clipping off

    public void setClippingOff(Graphics2D g2) {
	g2.setClip(null);
    }

    public String toString() {

	String str =
	    new String("_deviceWidth: " + _deviceWidth + "\n" +
		       "_deviceHeight: " + _deviceHeight + "\n" +
		       "_leftMargin: " + _leftMargin + "\n" +
		       "_rightMargin: " + _rightMargin + "\n" +
		       "_topMargin: " + _topMargin + "\n" +
		       "_bottomMargin: " + _bottomMargin + "\n" +
		       "_xMinWorld: " + _xMinWorld + "\n" +
		       "_xMaxWorld: " + _xMaxWorld + "\n" +
		       "_yMinWorld: " + _yMinWorld + "\n" +
		       "_yMaxWorld: " + _yMaxWorld + "\n" +
		       "_plotWidth: " + _plotWidth + "\n" +
		       "_plotHeight: " + _plotHeight + "\n" +
		       "_xMinDevice: " + _xMinDevice + "\n" +
		       "_xMaxDevice: " + _xMaxDevice + "\n" +
		       "_yMinDevice: " + _yMinDevice + "\n" +
		       "_yMaxDevice: " + _yMaxDevice + "\n" +
		       "_xPixelsPerWorld: " + _xPixelsPerWorld + "\n" +
		       "_yPixelsPerWorld: " + _yPixelsPerWorld + "\n" +
		       "");

	return str;
	
    }
    
}
