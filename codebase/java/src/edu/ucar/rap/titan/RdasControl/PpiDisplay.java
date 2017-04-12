///////////////////////////////////////////////////////////////////////
//
// PpiDisplay
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

public class PpiDisplay extends JFrame

{

    private Parameters _params = Parameters.getInstance();
    private JPanel _topPanel;
    private PpiCanvas _canvas;
    private String _infoContent;

    private boolean _freezeOn = false;
    private JButton _freezeButton;
    private JButton _unZoomButton;

    JLabel _clickLabel = null;
    private String _clickTextDefault =
	"Click in image to display data values ...";

    private BufferedImage _axisImage = null;
    private PpiColorScale _colorScale;
    private DataField _field = DataField.DBZ;

    MoveResizeListener _moveResizeListener = new MoveResizeListener();
    
    public PpiDisplay() {
	
	// startup field

	if (_params.ppi.startupField.getValue().
	    equals(DataField.DBZ.toString())) {
	    _field = DataField.DBZ;
	} else {
	    _field = DataField.SNR;
	}
	
	// basic window setup
	
	setTitle("PPI");
	setSize(_params.ppi.width.getValue(),
		_params.ppi.height.getValue());
	setLocation(_params.ppi.xx.getValue(),
		    _params.ppi.yy.getValue());
	setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	addComponentListener(_moveResizeListener);
  	_topPanel = new JPanel(new BorderLayout());
  	getContentPane().add(_topPanel);
	
	// canvas
	
	_canvas = new PpiCanvas(this, _field);
	JPanel canvasPanel = new JPanel(new BorderLayout());
	canvasPanel.add(_canvas, BorderLayout.CENTER);
	_topPanel.add(canvasPanel, BorderLayout.CENTER);

	// color scale

	_colorScale = new PpiColorScale(this, _field);
	JPanel scalePanel = new JPanel(new BorderLayout());
	scalePanel.add(_colorScale, BorderLayout.CENTER);
	_topPanel.add(scalePanel, BorderLayout.EAST);

	// click label
	
	JPanel clickPanel = new JPanel(new BorderLayout());
	_clickLabel = new JLabel(_clickTextDefault);
	_clickLabel.setHorizontalAlignment(SwingConstants.CENTER);
	_clickLabel.setVerticalAlignment(SwingConstants.CENTER);
	_clickLabel.setBorder(CustomBorder.createSimple(this, 1));
	clickPanel.add(_clickLabel, BorderLayout.CENTER);
	_topPanel.add(clickPanel, BorderLayout.SOUTH);
	
	// freeze button
	
	_freezeButton = new JButton("Freeze");
	_freezeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
		    if (!_freezeOn) {
			_freezeOn = true;
		    } else {
			_freezeOn = false;
		    }
		    _topPanel.validate();
                }
            });
	
	// unzoom button
	
	_unZoomButton = new JButton("UnZoom");
	_unZoomButton.setEnabled(false);
	_unZoomButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
		    _canvas.setUnzoomed();
		    _canvas.doRepaint();
		    _unZoomButton.setEnabled(false);
                }
            });

	// help button

	JButton helpButton = new JButton("Help");
	_infoContent =
	    "<h1>PPI help info</h1>\n" +
	    "<hr>\n" +
	    "<h2>Field selection</h2>\n" +
	    "<ul>\n" +
	    "<li>Select field by clicking DBZ or SNR.\n" +
	    "</ul>\n" +
	    "<hr>\n" +
	    "<h2>Zooming</h2>\n" +
	    "<ul>\n" +
	    "<li>Use left button to zoom in plot.\n" +
	    "<li>You can repeat to zoom in further.\n" +
	    "<li>Click <b>UnZoom</b> to cancel zoom.\n" +
	    "</ul>\n" +
	    "<hr>\n" +
	    "<h2>Freeze</h2>\n" +
	    "<ul>\n" +
	    "<li>Click <b>Freeze</b> to toggle freeze on/off.\n" +
	    "</ul>\n";

	helpButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
		    InfoFrame info =
			new InfoFrame(getX() + 30, getY() + 30, 600, 450);
		    info.setTitle("Info for PPI display");
		    info.setContent(_infoContent);
                }
            });

	// field selection

	JButton dbzButton = new JButton(DataField.DBZ.toString());
	dbzButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
		    setField(DataField.DBZ);
                }
            });

	JButton snrButton = new JButton(DataField.SNR.toString());
	snrButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
		    setField(DataField.SNR);
                }
            });

	// menu bar
	
	JMenuBar menuBar = new JMenuBar();
	setJMenuBar(menuBar);
	JButton editButton = new JButton(_params.ppi.getViewEditAction());
	menuBar.add(editButton);
	menuBar.add(dbzButton);
	menuBar.add(snrButton);
	menuBar.add(_freezeButton);
	menuBar.add(_unZoomButton);
	menuBar.add(helpButton);

	// set plot aspect ratio to 1:1

	setPlotAspectRatioEqual();

    } // constructor
    
    // is freeze on?

    public boolean isFreezeOn() {
	return _freezeOn;
    }

    // set zoom state
    
    public void setZoomState(boolean state) {
	_unZoomButton.setEnabled(state);
    }

    // set field

    public void setField(DataField field) {
	_field = field;
	_canvas.setField(field);
	_colorScale.setField(field);
    }

    // set click label text

    public void setClickLabelText(String text) {
	if (_clickLabel != null) {
	    _clickLabel.setText(text);
	}
    }

    public void clearClickLabelText() {
	if (_clickLabel != null) {
	    _clickLabel.setText(_clickTextDefault);
	}
    }

    // set aspect ratio in plot to 1:1

    public void setPlotAspectRatioEqual() {

	if (_canvas == null) {
	    return;
	}

	int plotWidth = _canvas.getPlotWidth();
	int plotHeight = _canvas.getPlotHeight();

	if (plotWidth <= 0 || plotHeight <= 0) {
	    return;
	}
	if (plotWidth == plotHeight) {
	    return;
	}

	// remove listener while we are busy

	removeComponentListener(_moveResizeListener);

	int plotDiff = plotHeight - plotWidth;
	int frameWidth = getWidth();
	int frameHeight = getHeight();
	
	if (plotDiff > 0) {
	    frameHeight -= plotDiff;
	} else {
	    frameWidth += plotDiff;
	}

	setSize(new Dimension(frameWidth, frameHeight));

	// set canvas world

	_canvas.setResized();

	// add listener back in
	
	addComponentListener(_moveResizeListener);

    }

    // window move/resize listener

    private class MoveResizeListener extends ComponentAdapter
    {
	
	public void componentMoved(ComponentEvent e) {
	    Parameters params = Parameters.getInstance();
	    int paramX = params.ppi.xx.getValue();
	    int paramY = params.ppi.yy.getValue();
	    if (paramX != getX() || paramY != getY()) {
		params.ppi.xx.setValue(getX());
		params.ppi.yy.setValue(getY());
	    }
	}
	
	public void componentResized(ComponentEvent e) {
	    Parameters params = Parameters.getInstance();
	    int paramWidth = params.ppi.width.getValue();
	    int paramHeight = params.ppi.height.getValue();
	    if (paramWidth != getWidth() || paramHeight != getHeight()) {
		params.ppi.width.setValue(getWidth());
		params.ppi.height.setValue(getHeight());
	    }
	    setPlotAspectRatioEqual();
	}
	
	public void componentShown(ComponentEvent e) {
	    Parameters params = Parameters.getInstance();
	    if (params.ppi.startVisible.getValue() == false) {
		params.ppi.startVisible.setValue(true);
	    }
	}
	
	public void componentHidden(ComponentEvent e) {
	    Parameters params = Parameters.getInstance();
	    if (params.ppi.startVisible.getValue() == true) {
		params.ppi.startVisible.setValue(false);
	    }
	}
	
    }

}
