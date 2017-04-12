///////////////////////////////////////////////////////////////////////
//
// PpiCanvas
//
// JPanel as canvas for PPI display
//
// Mike Dixon
//
// Nov 2004
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

public class PpiCanvas extends JPanel implements BeamDataListener
    
{
    
    private Parameters _params = Parameters.getInstance();
    private PpiDisplay _ppi;
    private ColorScale _dbzColors = new DbzColorScale();
    private ColorScale _snrColors = new SnrColorScale();
    private DataField _field = DataField.DBZ;

    private BeamMessage _beam = null;
    private BeamMessage _beam_prev1 = null;
    private BeamMessage _beam_prev2 = null;
    private BeamMessage _prevPainted = null;
    private int _beamCount = 0;
    
    private long _lastRepaintTime = 0;
    
    private double _maxRange = 200.0;
    private int _leftMargin = 0;
    private int _rightMargin = 0;
    private int _topMargin = 0;
    private int _bottomMargin = 0;

    private String _frameTitle = "";
    private String _plotTitle = "";

    private boolean _newWorld = true;
    private WorldPlot _plotWorld;
    private WorldPlot _dataWorld;
    
    private Zoomed _unZoomed = new Zoomed(); // always exists
    private Zoomed _thisZoom = _unZoomed;
    private Zoomed _dataZoom = null;
    private boolean _zoomingRectangleEnabled = true;
    private Zooming _zooming = null;         // null if not currently zooming
    private Zoomed _prevZoom;                // keeps track of previous zoom
    
    private int _bgImageWidth = 0;
    private int _bgImageHeight = 0;
    private Image _bgImage = null;

    private BufferedImage _axisImage = null;
    private BufferedImage _plotImage = null;
    private BufferedImage _dbzImage = null;
    private BufferedImage _snrImage = null;

    private float[] _dbzData = null;
    private float[] _snrData = null;

    private Graphics _bgGraphics = null;
    private Graphics _axisGraphics = null;
    private Graphics _plotGraphics = null;
    private boolean _plotReady = false;

    private ArrayList _ppiPixArrayList[] = null;

    private long _clickTextTime = 0;
    private double _clickTextX = 0;
    private double _clickTextY = 0;
    private double _clickRange = 0;
    private double _clickAz = 0;
    private boolean _clickTextActive = false;

    private MessageQueue _pendingQueue;
    private MessageQueue _redrawQueue;
    private boolean _redrawBeams = false;
    
    public PpiCanvas(PpiDisplay ppi, DataField field) {
	
	_ppi = ppi;
	setField(field);
	
	ZoomMouseListener zoomListener = new ZoomMouseListener();
	addMouseListener(zoomListener);
	addMouseMotionListener(zoomListener);
	
	// create beam queues
	
	_pendingQueue = new MessageQueue();
	_redrawQueue = new MessageQueue();

	_pendingQueue.setMaxSize(_params.ppi.beamQueueSize.getValue());
	_redrawQueue.setMaxSize(_params.ppi.beamQueueSize.getValue());

	// add listener for changes in ppi params

	_params.ppi.addChangeListener(new PpiChangeListener());
	_updateFromParams();
	
	// add listener for changes in radar params
	
	_params.radar.addChangeListener(new RadarChangeListener());
	
	// add listener for new beam data
	
	BeamDataHandler.getInstance().addListener(this);

	// pixel object array

	_ppiPixArrayList = new ArrayList[3600];
	for (int i = 0; i < 3600; i++) {
	    _ppiPixArrayList[i] = new ArrayList();
	}

	// create the ticker thread, start it
	
	Ticker ticker = new Ticker();
	ticker.start();
	
     }

     // ticker thread
    
    private class Ticker extends Thread {
	public void run() {
	    while (true) {
		
		// first redraw beams if needed
		
		if (_redrawBeams) {
		    _redrawBeamsInQueue();
		    _redrawBeams = false;
		}

		// then draw beams in pending queue
		
		_drawBeamsPending();
		    
		// update canvas
		if (_beam != _prevPainted) {
		    doRepaint();
		    _prevPainted = _beam;
		}

		// check click text
		_checkClickText();

		// sleep a bit
		Thread t = Thread.currentThread();
		int repaintMsecs = _params.ppi.repaintMsecs.getValue();
		repaintMsecs = Math.max(50, repaintMsecs);
		try { t.sleep(repaintMsecs); }
		catch (InterruptedException e) { return; }

	    }
	}
    }
    
    // handle beam
    
    public void handleBeam(BeamMessage beam, double rate) {
	
 	if (_ppi.isFreezeOn()) {
 	    return;
 	}
	
	_pendingQueue.push(beam);
	
    }
    
    // draw beam
    
    private void _drawBeam(BeamMessage beam) {

	// we save 3 beams for drawing
	
	_beam_prev2 = _beam_prev1;
	_beam_prev1 = _beam;
	_beam = beam;
	_beamCount++;
	_updateDataImage();

    }
    
    // draw the beams in the pending queue
    
    private void _drawBeamsPending() {

	if (_pendingQueue == null) {
	    return;
	}
	
	while (!_pendingQueue.isEmpty()) {
 	    BeamMessage beam = (BeamMessage) _pendingQueue.pop();
	    if (beam != null) {
		_drawBeam(beam);
		_redrawQueue.push(beam);
	    }
 	}
	
    }

    // redraw the beams in the queue
    
    private void _redrawBeamsInQueue() {

	if (_redrawQueue == null) {
	    return;
	}

 	_beam_prev2 = null;
 	_beam_prev1 = null;
	
 	for (int ii = 0; ii < _redrawQueue.getSize(); ii++) {
 	    BeamMessage beam = (BeamMessage) _redrawQueue.get(ii);
	    _drawBeam(beam);
 	}
	
    }

    // set methods
    
    public void setUnzoomed() {
	_thisZoom = _unZoomed;
	_setNewWorld();
	_setRedrawBeams();
    }
    
    public void setResized() {
	_setNewWorld();
	_setRedrawBeams();
    }

    public void setField(DataField field) {
	_field = field;
	setTitleStr();
	_plotAxes();
	repaint();
	_checkClickText();
    }

    private void _setNewWorld() {
	_newWorld = true;
    }

    private void _setRedrawBeams() {
	_redrawBeams = true;
    }

    ////////////////
    // get plot size

    public int getPlotWidth() {
	return getWidth() - _leftMargin - _rightMargin;
    }

    public int getPlotHeight() {
	return getHeight() - _topMargin - _bottomMargin;
    }

    ///////////////////////////
    // overload paintComponent
    //
    // This gets called by Swing for repaints as required
    
    public void paintComponent (Graphics g) {
	super.paintComponent(g);
	_doRepaint(g);
    }

    //////////////////////////////////////////////////////////
    // do a repaint - normally called when a new beam arrives
    
    public void doRepaint() {
	_doRepaint(getGraphics());
    }

    private void _doRepaint(Graphics g) {

	Graphics2D g2 = (Graphics2D) g;

	// if width and height are not yet set, call repaint, which
	// will only call paintComponent() after the object is ready
	// for painting
	
	if (getWidth() == 0 || getHeight() == 0) {
	    repaint();
	    return;
	}

	// if window is not visible, don't paint

	if (!isVisible()) {
	    return;
	}

	// if we are zooming, don't paing

	if (_zooming != null) {
	    return;
	}

	// paint into background image
	
	_paint(_bgGraphics);

	// copy background to screen graphics

	if (g2 != null && _bgImage != null) {
	    g2.drawImage(_bgImage, 0, 0, _ppi);
	}
	
    }

    ///////////////////////////////////
    // update from parameters

    private void _updateFromParams() {
	
	_leftMargin = _params.ppi.leftMargin.getValue();
	_rightMargin = _params.ppi.rightMargin.getValue();
	_topMargin = _params.ppi.topMargin.getValue();
	_bottomMargin = _params.ppi.bottomMargin.getValue();
	_maxRange = _params.ppi.maxRange.getValue();
	
	if (_pendingQueue != null) {
	    _pendingQueue.setMaxSize(_params.ppi.beamQueueSize.getValue());
	}
	if (_redrawQueue != null) {
	    _redrawQueue.setMaxSize(_params.ppi.beamQueueSize.getValue());
	}

	_unZoomed.minX = -_maxRange;
	_unZoomed.maxX = _maxRange;
	_unZoomed.minY = -_maxRange;
	_unZoomed.maxY = _maxRange;
	
	setUnzoomed();

	_ppi.setPlotAspectRatioEqual();
	
    }

    ////////////////////////////////////////////
    // check that the world stuff is up to date
    
    private synchronized int _initWorld() {

	// is aspect ratio 1:1?

	if (getPlotWidth() != getPlotHeight()) {
	    return -1;
	}

	// does it need updating?

	if (_newWorld == false &&
	    _plotWorld != null &&
	    _plotWorld.getDeviceWidth() == getWidth() &&
	    _plotWorld.getDeviceHeight() == getHeight() &&
	    _dataZoom == _thisZoom) {
	    return 0;
	}

	// update world

	Dimension size = getSize();
	Insets insets = getInsets();

	int topMargin = insets.top + _topMargin;
	int bottomMargin = insets.bottom + _bottomMargin;
	int leftMargin = insets.left + _leftMargin;
	int rightMargin = insets.right + _rightMargin;
	
	// new world view
	
 	_plotWorld = new WorldPlot(getWidth(), getHeight(),
				   leftMargin, rightMargin,
				   topMargin, bottomMargin,
				   _thisZoom.minX, _thisZoom.maxX,
				   _thisZoom.minY, _thisZoom.maxY);
	
	// empty data window
	
	Graphics2D g2 = (Graphics2D) getGraphics();
	g2.setColor(getBackground());
	g2.fill(new Rectangle(0, 0, getWidth(), getHeight()));
	g2.setColor(getForeground());
	
	// initialize image graphics
	
	_initImageGraphics();
	
	// plot the axes to _axisImage
	
	_plotAxes();
	
	// allocate the data image
	
	_allocDataImage();

	_newWorld = false;
	_dataZoom = _thisZoom;

	return 0;

    }
    
    //////////////////////////////////////////
    // initialize images and graphics contexts
    
    private synchronized void _initImageGraphics() {
	
	if (_bgImageWidth == getWidth() &&
	    _bgImageHeight == getHeight() &&
	    _bgGraphics != null &&
	    _plotGraphics != null) {
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
	
    }
    
    ///////////////////////////////////////
    // allocate the image for the data
    //
    // This stores the colors in raw (az, gates) space
    
    private void _allocDataImage() {
	
	if (_plotWorld == null) {
	    return;
	}

	// create new image
	
 	_dbzImage = new BufferedImage(_plotWorld.getPlotWidth(),
				      _plotWorld.getPlotHeight(),
				      BufferedImage.TYPE_INT_RGB);

 	_snrImage = new BufferedImage(_plotWorld.getPlotWidth(),
				      _plotWorld.getPlotHeight(),
				      BufferedImage.TYPE_INT_RGB);
	
	_dbzData = new float[_plotWorld.getPlotWidth() *
			     _plotWorld.getPlotHeight()];
	
	_snrData = new float[_plotWorld.getPlotWidth() *
			     _plotWorld.getPlotHeight()];
	
 	_dataWorld = new WorldPlot(_plotWorld.getPlotWidth(),
				   _plotWorld.getPlotHeight(),
				   0, 0, 0, 0,
				   _thisZoom.minX, _thisZoom.maxX,
				   _thisZoom.minY, _thisZoom.maxY);
	
	// initialize new image to black
	
	int ii = 0;
	for (int iy = 0; iy < _dbzImage.getHeight(); iy++) {
	    for (int ix = 0; ix < _dbzImage.getWidth(); ix++, ii++) {
		_dbzImage.setRGB(ix, iy, Color.BLACK.getRGB());
		_snrImage.setRGB(ix, iy, Color.BLACK.getRGB());
		_dbzData[ii] = (float) -9999.0;
		_snrData[ii] = (float) -9999.0;
	    }
	}

	// clear pixel arrays
	
	for (int i = 0; i < 3600; i++) {
	    _ppiPixArrayList[i].clear();
	}

	// compute location and range of each pixel, load up array list

	int count = 0;
	for (int ix = 0; ix < _dbzImage.getWidth(); ix++) {
	    for (int iy = 0; iy < _dbzImage.getHeight(); iy++) {
		count++;
		double xx = _dataWorld.getXWorld(ix);
		double yy = _dataWorld.getYWorld(iy);
		double range = Math.sqrt(xx * xx + yy * yy);
		double az = 0.0;
		if (xx != 0.0 || yy != 0.0) {
		    az = Math.toDegrees(Math.atan2(xx, yy));
		}
		if (az < 0) {
		    az += 360.0;
		}
		int azIndex = (int) (az * 10.0 + 0.5);
		if (azIndex < 0) {
		    azIndex = 0;
		} else if (azIndex > 3599) {
		    azIndex = 3599;
		}
		_ppiPixArrayList[azIndex].add(new PpiPixel(ix, iy,
							   (float) range));
	    } // iy
	} // ix

    }
    
    ///////////////////////////////////////
    // update the image for the data
    
    private void _updateDataImage() {
	
 	if (!_ppi.isVisible() || _beam == null ||
	    _beam_prev1 == null || _beam_prev2 == null) {
 	    return;
 	}

	// check the world has been initialized
	
	if (_initWorld() != 0) {
	    return;
	}

	// compute az indices for prev1

 	double azPrev2 = _beam_prev2.getAz();
 	double azPrev1 = _beam_prev1.getAz();
 	double azLatest = _beam.getAz();
	
	double az1 = meanAz(azPrev2, azPrev1);
	double az2 = meanAz(azPrev1, azLatest);

	// condition the azimuths, to get lower and upper, taking care
	// across the 360 degree line

	if ((az2 - az1) > 180) {
	    az2 -= 360.0;
	} else if ((az1 - az2) > 180) {
	    double tmp = az1 - 360.0;
	    az1 = az2;
	    az2 = tmp;
	}

	double azLower, azUpper;
	if (az1 > az2) {
	    azLower = az2;
	    azUpper = az1;
	} else {
	    azLower = az1;
	    azUpper = az2;
	}
	
	double azDiff = azUpper - azLower;
	if (azDiff < 1.0) {
	    double extra = (1.0 - azDiff) / 2.0;
	    azLower -= extra;
	    azUpper += extra;
	}

	// compute cosine of elevation

	double cosEl = Math.cos(Math.toRadians(_beam_prev1.getEl()));
	double startRange = _beam_prev1.getStartRange();
	double gateSpacing = _beam_prev1.getGateSpacing();
	float dbz[] = _beam_prev1.getDbz();
	float snr[] = _beam_prev1.getSnr();

	// loop through the azimiths, by 0.1 degrees, computing the index

	for (double az = azLower; az <= azUpper; az += 0.1) {

	    int azIndex = (int) (az * 10.0 + 0.5);
	    if (azIndex < 0) {
		azIndex += 3600;
	    } else if (azIndex >= 3600) {
		azIndex -= 3600;
	    }

	    // loop through the ppiPixels for this index
	    
	    ArrayList ppiPixArray = _ppiPixArrayList[azIndex];
	    for (int ii = 0; ii < ppiPixArray.size(); ii++) {
		// find the pixel data
		PpiPixel pix = (PpiPixel) ppiPixArray.get(ii);
		// compute gate number
		int gateNum =
		    (int) ((pix.Range - startRange) / gateSpacing + 0.5);
		// compute array index
		int arrayIndex = pix.Iy * _dbzImage.getWidth() + pix.Ix;
		if (gateNum >= 0 &&
		    gateNum < _beam_prev1.getNGates() && azDiff < 5) {
		    // update data image
		    float dbzVal = dbz[gateNum];
		    Color dbzColor = _dbzColors.getColor(dbzVal);
		    _dbzImage.setRGB(pix.Ix, pix.Iy, dbzColor.getRGB());
		    _dbzData[arrayIndex] = dbzVal;
		    float snrVal = snr[gateNum];
		    Color snrColor = _snrColors.getColor(snrVal);
		    _snrImage.setRGB(pix.Ix, pix.Iy, snrColor.getRGB());
		    _snrData[arrayIndex] = snrVal;
		} else {
		    _dbzImage.setRGB(pix.Ix, pix.Iy, Color.BLACK.getRGB());
		    _snrImage.setRGB(pix.Ix, pix.Iy, Color.BLACK.getRGB());
		}
	    }

	}

    }

    // compute mean azimuth

    double meanAz(double az1, double az2) {
	
	if ((az2 - az1) > 180) {
	    az2 -= 360.0;
	} else if ((az1 - az2) > 180) {
	    double tmp = az1 - 360.0;
	    az1 = az2;
	    az2 = tmp;
	}

	double azmean = (az1 + az2) / 2.0;

	if (azmean < 0) {
	    azmean += 360.0;
	}

	return azmean;


    }
	
    ///////////////////////////////////////////
    // paint the plot into the graphics object
    
    public synchronized void _paint (Graphics g) {
	
	// check the world has been initialized

	if (_initWorld() != 0) {
	    return;
	}

	// set frame graphics context
	
	Graphics2D g2 = (Graphics2D) g;
	
	// set plot graphics context
	
	Graphics2D plotG2 = (Graphics2D) _plotGraphics;
	
	// return now if no beam data
	
	if (_beam == null) {
	    
	    // clear window
	    
	    plotG2.setColor(getBackground());
	    plotG2.fill(new Rectangle(0, 0, getWidth(), getHeight()));
 	    return;

	}

	// clear window
	
	plotG2.setColor(getBackground());
	plotG2.fill(new Rectangle(0, 0, getWidth(), getHeight()));

	// copy data image to plot image

	if (_field == DataField.DBZ) {
	    if (_dbzImage != null) {
		plotG2.drawImage(_dbzImage, _leftMargin, _topMargin, null);
	    }
	} else {
	    if (_snrImage != null) {
		plotG2.drawImage(_snrImage, _leftMargin, _topMargin, null);
	    }
	}

	// draw azimuth line on plot image

 	plotG2.setColor(Color.red);
 	_plotWorld.setClippingOn(plotG2);
 	double az = _beam.getAz();
	double azRad = Math.toRadians(az);
	double rr = _maxRange;
	double xx = rr * Math.sin(azRad);
	double yy = rr * Math.cos(azRad);
	_plotWorld.drawLine(plotG2, 0.0, 0.0, xx, yy);
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
	
	if (g2 != null && _plotImage != null) {
	    g2.drawImage(_plotImage, 0, 0, null);
	}
	if (g2 != null && _axisImage != null) {
	    g2.drawImage(_axisImage, 0, 0, null);
	}
	
	
    } // _paint

    //////////////////////////////////////////////
    // plot the axes if required
    
    private void _plotAxes() {

	if (_bgImageWidth < 1 || _bgImageHeight < 1) {
	    return;
	}

	// _axisImage = createImage(_bgImageWidth, _bgImageHeight);
	
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
	_plotWorld.drawAxisBottom(axisG2, "", true, true, false);
	_plotWorld.drawAxisLeft(axisG2, "", true, true, false);

	axisG2.setColor(getForeground());
	_plotWorld.drawAxisBottom(axisG2, "X(km)", false, false, true);
	_plotWorld.drawAxisLeft(axisG2, "Y(km)", false, false, true);

	// clipping on, color

	_plotWorld.setClippingOn(axisG2);
	axisG2.setColor(Color.white);

	// azimith lines every 30 degrees

	for (double az = 0.0; az < 180.0; az += 30.0) {
	    double azRad = Math.toRadians(az);
	    double rr = _maxRange;
	    double xx = rr * Math.sin(azRad);
	    double yy = rr * Math.cos(azRad);
	    _plotWorld.drawLine(axisG2, xx, yy, -xx, -yy);
	}

	// range rings

	double spacing = _params.ppi.rangeRingSpacing.getValue();
	for (double range = spacing; range <= _maxRange * 1.01;
	     range += spacing) {
	    _plotWorld.drawArc(axisG2,
			       -range, range,
			       range * 2.0, range * 2.0,
			       0.0, 360.0);
	}
			    
	
	// clipping off
	
	_plotWorld.setClippingOn(axisG2);
	axisG2.setColor(getForeground());

    }

    /////////////
    // set title

    private void setTitleStr() {
	
	_frameTitle = "PPI - " + _params.radar.siteName.getValue();
	if (_field == DataField.DBZ) {
	    _plotTitle = _params.radar.siteName.getValue() +
		" - Refl (dBZ) - PPI";
	} else {
	    _plotTitle = _params.radar.siteName.getValue() +
		" - SNR (dB) - PPI";
	}
	_ppi.setTitle(_frameTitle);

    }

    ////////////////////////////////////
    // set click text in parent window

    private void _setClickText() {

	if (!_clickTextActive) {
	    return;
	}

	int ixx = (int) Math.floor(_plotWorld.getXDevice(_clickTextX) + 0.5);
	int iyy = (int) Math.floor(_plotWorld.getYDevice(_clickTextY) + 0.5);
	
	int jxx = ixx - _plotWorld.getLeftMargin();
	int jyy = iyy - _plotWorld.getTopMargin();
	int arrayIndex = jyy * _dbzImage.getWidth() + jxx;
	
	if (_clickRange > _maxRange ||
	    _clickTextX < _plotWorld.getXMinWorld() ||
	    _clickTextX > _plotWorld.getXMaxWorld() ||
	    _clickTextY < _plotWorld.getYMinWorld() ||
	    _clickTextY > _plotWorld.getYMaxWorld() ||
	    arrayIndex < 0 || arrayIndex >= _dbzData.length) {
	    _clickTextActive = false;
	    _ppi.clearClickLabelText();
	    return;
	}

	float dbzVal = _dbzData[arrayIndex];
	float snrVal = _snrData[arrayIndex];
	
	if (dbzVal > -9000) {
	    String clickText =
		"X: " + NFormat.f1.format(_clickTextX) + " " +
		"Y: " + NFormat.f1.format(_clickTextY) + " km " +
		"rng: " + NFormat.f1.format(_clickRange) + " km  " + 
		"az: " + NFormat.f1.format(_clickAz) + " deg  ";
	    if (_field == DataField.DBZ) {
		clickText = clickText +
		    "Refl: " + NFormat.f1.format(dbzVal) + " dBZ";
	    } else {
		clickText = clickText +
		    "SNR: " + NFormat.f1.format(snrVal) + " dB";
	    }
	    _ppi.setClickLabelText(clickText);
	}

    }

    /////////////////////////////////////////
    // check that click text is still active
    
    private void _checkClickText() {

	long now = TimeManager.getTime();
	double secsSinceClick = (now - _clickTextTime) / 1000.0;
	int duration = _params.ppi.clickTextDuration.getValue();
	if (secsSinceClick > duration) {
	    _ppi.clearClickLabelText();
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

	int ixx = (int) Math.floor(_plotWorld.getXDevice(_clickTextX) + 0.5);
	int iyy = (int) Math.floor(_plotWorld.getYDevice(_clickTextY) + 0.5);
	int iconSize = _params.ppi.clickIconSize.getValue();
	
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
    // Listener for changes in ppi parameters

    private class PpiChangeListener implements CollectionChangeListener {
	public void reactToChange() {
	    _updateFromParams();
	}
    }
    
    // Inner class
    // Listener for changes in radar parameters

    private class RadarChangeListener implements CollectionChangeListener {
	public void reactToChange() {
	    _setNewWorld();
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
		
	    int ixx = e.getPoint().x;
	    int iyy = e.getPoint().y;
		
	    // We're zooming. Paint a rectangle to show zoom region.
	    // It should have the same aspect ratio as the window, and not
	    // be any smaller than is allowed by max_scale.
		
	    int rectWidth = (int) Math.abs(ixx - _zooming.startPoint.getX());
	    int rectHeight = (int) Math.abs(iyy - _zooming.startPoint.getY());
	    int rectOriginX = 0;
	    int rectOriginY = 0;
		
	    double aspectRatio = (float) getHeight() / getWidth();
	    rectWidth  = (int) Math.abs(ixx - _zooming.startPoint.getX());
	    int y_range_1 = (int) (aspectRatio * rectWidth);
	    int y_range_2 = (int) Math.abs(iyy - _zooming.startPoint.getY());
	    if(y_range_2 > y_range_1) {
	      // y-distance determines size
	      rectHeight = y_range_2;
	      rectWidth = (int) (rectHeight / aspectRatio);
	    } else {
	      // x-distance determines size
	      rectHeight = y_range_1;
	    }
	    
	    if(ixx > _zooming.startPoint.getX()) {
		rectOriginX = (int) _zooming.startPoint.getX();
	    } else {
		rectOriginX =
		    ((int) _zooming.startPoint.getX()) - rectWidth;
	    }
		
	    if(iyy > _zooming.startPoint.getY()) {
		rectOriginY = (int) _zooming.startPoint.getY();
	    } else {
		rectOriginY =
		    ((int) _zooming.startPoint.getY()) - rectHeight;
	    }
		
	    Graphics g = getGraphics();
	    g.setColor(Color.red);
	    g.setXORMode(getBackground());
	    // g.setXORMode(Color.white);
		
	    // reverse the previous zooming rectangle outline if
	    // this is not the 1st

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

	    if(_zooming == null || _plotWorld == null) {
		return;
	    }
		
	    // Get the last point into mouseDrag
	    mouseDragged(e);
		
	    // if the mouse didn't move much, assume we don't want to zoom

	    if(((int) _zooming.rect.getWidth()) < 10) {
		
		// compute the click location

		int ixx = e.getPoint().x;
		int iyy = e.getPoint().y;

		// set the click text in parent window
		
		_clickTextTime = TimeManager.getTime();

		_clickTextX = _plotWorld.getXWorld(ixx);
		_clickTextY = _plotWorld.getYWorld(iyy);
		_clickRange = Math.sqrt(_clickTextX * _clickTextX +
					_clickTextY * _clickTextY);
		
		_clickAz =
		    Math.toDegrees(Math.atan2(_clickTextX, _clickTextY));
		if (_clickAz < 0) {
		    _clickAz += 360.0;
		}
		
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
		
		_thisZoom = zoomed;
		_ppi.setZoomState(true);
		_setNewWorld();
		_setRedrawBeams();

	    }
	    
	    _zooming = null;
	    doRepaint();
	    
	} // mouseReleased
	    
    } // ZoomMouseListener


}

	
