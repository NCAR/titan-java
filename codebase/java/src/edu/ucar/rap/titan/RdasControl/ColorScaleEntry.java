///////////////////////////////////////////////////////////////////////
//
// ColorScaleEntry
//
// Mike Dixon
//
// Jan 2004
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.awt.Color;

class ColorScaleEntry {
    
    private double _minVal;
    private double _maxVal;
    private Color _color;
    private String _label = "";
    
    public ColorScaleEntry(double minVal, double maxVal,
			   double r, double g, double b, double a)
    {
	_minVal = minVal;
	_maxVal = maxVal;
	_color = new Color((float) r, (float) g, (float) b, (float) a);
    }

    public ColorScaleEntry(double minVal, double maxVal,
			   double r, double g, double b, double a,
			   String label)
    {
	_minVal = minVal;
	_maxVal = maxVal;
	_color = new Color((float) r, (float) g, (float) b, (float) a);
	_label = new String(label);
    }

    public double getMinVal() {
	return _minVal;
    }

    public double getMaxVal() {
	return _maxVal;
    }

    public Color getColor() {
	return _color;
    }

    public String getLabel() {
	return _label;
    }

}
