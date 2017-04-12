///////////////////////////////////////////////////////////////////////
//
// BscanColorScale
//
// JPanel as canvas for BSCAN color scale
//
// Mike Dixon
//
// Sept 2004
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import edu.ucar.rap.jrp.*;

public class BscanColorScale extends JPanel
    
{
    
    private Parameters _params = Parameters.getInstance();
    private BscanDisplay _bscan;
    private ColorScale _dbzColors = new DbzColorScale();
    private ColorScale _snrColors = new SnrColorScale();
    private DataField _field;
    private int _width;
    private int _topMargin;
    private int _bottomMargin;
    private int _leftMargin;
    private int _rightMargin;

    
    public BscanColorScale(BscanDisplay bscan, DataField field) {
	
	_bscan = bscan;
	_field = field;
	_params.bscan.addChangeListener(new ChangeListener());
	_updateFromParams();
	
    }
    
    ///////////////////////////////////
    // update from parameters

    private void _updateFromParams() {
	
	_width = _params.bscan.colorScaleWidth.getValue();
	_topMargin = _params.bscan.colorScaleTopMargin.getValue();
	_bottomMargin = _params.bscan.colorScaleBottomMargin.getValue();
	_leftMargin = _params.bscan.colorScaleLeftMargin.getValue();
	_rightMargin = _params.bscan.colorScaleRightMargin.getValue();

	setPreferredSize(new Dimension(_width, 100));

    }

    // set the data field

    public void setField(DataField field) {
	_field = field;
	repaint();
    }

    ///////////////////////////
    // overload paintComponent
    //
    // This gets called by Swing for repaints as required
    
    public void paintComponent (Graphics g) {
	super.paintComponent(g);
 	_paint(g);
    }
    
    ///////////////////////////////////////////
    // paint the plot into the graphics object
    
    public synchronized void _paint (Graphics g) {
	
	Graphics2D g2 = (Graphics2D) g;
	
	WorldPlot world = new WorldPlot(getWidth(), getHeight(),
					_leftMargin, _rightMargin,
					_topMargin, _bottomMargin,
					0.0, 1.0, 0.0, 1.0);

	ColorScale colors;
	if (_field == DataField.DBZ) {
	    colors = _dbzColors;
	} else {
	    colors = _snrColors;
	}
 
	int nColors = colors.getNColors();
	double dY = 1.0 / nColors;
	double minY = 0.0;
	for (int ii = 0; ii < nColors; ii++) {
	    ColorScaleEntry entry = colors.getEntry(ii);
	    g2.setColor(entry.getColor());
	    world.fillRectangle(g2, 0.0, minY, 0.7, dY);
	    minY += dY;
	}
	g2.setColor(getBackground());

	g2.setColor(Color.black);
	minY = 0.0;
	for (int ii = 0; ii < nColors; ii++) {
	    ColorScaleEntry entry = colors.getEntry(ii);
 	    String text = new String(NFormat.f0.format(entry.getMinVal()));
	    world.drawText(g2, text, 0.75, minY,
			   WorldPlot.HORIZ_LEFT, WorldPlot.VERT_CENTER);
	    minY += dY;
	    if (ii == nColors - 1) {
		text = new String(NFormat.f0.format(entry.getMaxVal()));
		world.drawText(g2, text, 0.75, minY,
			       WorldPlot.HORIZ_LEFT, WorldPlot.VERT_CENTER);
	    }
	}
	g2.setColor(getBackground());
	

   } // _paint

    // Inner class
    // Listener for changes in bscan parameters

    private class ChangeListener implements CollectionChangeListener {
	public void reactToChange() {
	    _updateFromParams();
	    _bscan.validate();
	    repaint();
	}
    }
    
}
