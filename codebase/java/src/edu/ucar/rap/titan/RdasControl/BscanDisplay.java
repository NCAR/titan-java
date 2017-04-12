///////////////////////////////////////////////////////////////////////
//
// BscanDisplay
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

public class BscanDisplay extends JFrame

{

    private Parameters _params = Parameters.getInstance();
    private JPanel _topPanel;
    private BscanCanvas _canvas;
    private String _infoContent;

    private boolean _freezeOn = false;
    private JButton _freezeButton;
    private JButton _unZoomButton;

    JLabel _clickLabel = null;
    private String _clickTextDefault =
	"Click in image to display data values ...";

    private BufferedImage _axisImage = null;
    private BscanColorScale _colorScale;
    private DataField _field = DataField.DBZ;
    
    public BscanDisplay() {

	// startup field

	if (_params.bscan.startupField.getValue().
	    equals(DataField.DBZ.toString())) {
	    _field = DataField.DBZ;
	} else {
	    _field = DataField.SNR;
	}
	
	// basic window setup

	setTitle("BSCAN");
	setSize(_params.bscan.width.getValue(),
		_params.bscan.height.getValue());
	setLocation(_params.bscan.xx.getValue(),
		    _params.bscan.yy.getValue());
	setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	addComponentListener(new MoveResizeListener());
  	_topPanel = new JPanel(new BorderLayout());
  	getContentPane().add(_topPanel);
	
	// canvas
	
	_canvas = new BscanCanvas(this, _field);
	JPanel canvasPanel = new JPanel(new BorderLayout());
	canvasPanel.add(_canvas, BorderLayout.CENTER);
	_topPanel.add(canvasPanel, BorderLayout.CENTER);

	// click label
	
	JPanel clickPanel = new JPanel(new BorderLayout());
	_clickLabel = new JLabel(_clickTextDefault);
	_clickLabel.setHorizontalAlignment(SwingConstants.CENTER);
	_clickLabel.setVerticalAlignment(SwingConstants.CENTER);
	_clickLabel.setBorder(CustomBorder.createSimple(this, 1));
	clickPanel.add(_clickLabel, BorderLayout.CENTER);
	_topPanel.add(clickPanel, BorderLayout.SOUTH);
	
	// color scale

	_colorScale = new BscanColorScale(this, _field);
	JPanel scalePanel = new JPanel(new BorderLayout());
	scalePanel.add(_colorScale, BorderLayout.CENTER);
	_topPanel.add(scalePanel, BorderLayout.EAST);
	
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
	    "<h1>BSCAN help info</h1>\n" +
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
		    info.setTitle("Info for Bscan");
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
	JButton editButton = new JButton(_params.bscan.getViewEditAction());
	menuBar.add(editButton);
	menuBar.add(dbzButton);
	menuBar.add(snrButton);
	menuBar.add(_freezeButton);
	menuBar.add(_unZoomButton);
	menuBar.add(helpButton);

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

    // window move/resize listener

    private class MoveResizeListener extends ComponentAdapter
    {
	
	public void componentMoved(ComponentEvent e) {
	    Parameters params = Parameters.getInstance();
	    int paramX = params.bscan.xx.getValue();
	    int paramY = params.bscan.yy.getValue();
	    if (paramX != getX() || paramY != getY()) {
		params.bscan.xx.setValue(getX());
		params.bscan.yy.setValue(getY());
	    }
	}
	
	public void componentResized(ComponentEvent e) {
	    Parameters params = Parameters.getInstance();
	    int paramWidth = params.bscan.width.getValue();
	    int paramHeight = params.bscan.height.getValue();
	    if (paramWidth != getWidth() || paramHeight != getHeight()) {
		params.bscan.width.setValue(getWidth());
		params.bscan.height.setValue(getHeight());
	    }
	    _canvas.setNewWorld();
	}
	
	public void componentShown(ComponentEvent e) {
	    Parameters params = Parameters.getInstance();
	    if (params.bscan.startVisible.getValue() == false) {
		params.bscan.startVisible.setValue(true);
	    }
	}
	
	public void componentHidden(ComponentEvent e) {
	    Parameters params = Parameters.getInstance();
	    if (params.bscan.startVisible.getValue() == true) {
		params.bscan.startVisible.setValue(false);
	    }
	}
	
    }

}
