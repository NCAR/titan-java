///////////////////////////////////////////////////////////////////////
//
// ColorScale
//
// Mike Dixon
//
// Jan 2004
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.util.*;
import java.lang.*;
import java.awt.Color;

public class ColorScale {
    
    private boolean _avail = false;
    private ArrayList _colorArray;
    private double _minVal;
    private double _maxVal;
    private final int _nBins = 1000;
    private double _binWidth;
    private Color[] _color;
    private Color _transparent;
    
    public ColorScale() {
	_colorArray = new ArrayList();
	_color = new Color[_nBins];
	_transparent = new Color((float) 0.0, (float) 0.0, (float) 0.0, (float) 0.0);
    }

    public void clear() {
	_colorArray.clear();
	_avail = false;
    }

    public void addEntry(double minVal, double maxVal,
			 double r, double g, double b, double a) {
	ColorScaleEntry entry = new ColorScaleEntry(minVal, maxVal, r, g, b, a);
	_colorArray.add(entry);
    }
    
    public void addEntry(double minVal, double maxVal,
			 double r, double g, double b, double a,
		  String label) {
	ColorScaleEntry entry = new ColorScaleEntry(minVal, maxVal, r, g, b, a, label);
	_colorArray.add(entry);
    }

    public void compute() {

	if (_colorArray.size() == 0) {
	    return;
	}

	for (int ii = 0; ii < _nBins; ii++) {
	    _color[ii] = null;
	}

	_minVal = 1.0e99;
	_maxVal = -1.0e99;
	for (int ii = 0; ii < _colorArray.size(); ii++) {
	    _minVal = Math.min(_minVal, ((ColorScaleEntry) _colorArray.get(ii)).getMinVal());
	    _maxVal = Math.max(_maxVal, ((ColorScaleEntry) _colorArray.get(ii)).getMaxVal());
	}

	_binWidth= (_maxVal - _minVal) / _nBins;

	for (int ii = 0; ii < _colorArray.size(); ii++) {
	    
	    int startBin = (int)
		((((ColorScaleEntry) _colorArray.get(ii)).getMinVal() - _minVal) / _binWidth);
	    int endBin = (int)
		((((ColorScaleEntry) _colorArray.get(ii)).getMaxVal() - _minVal) / _binWidth);
	    if (startBin < 0) {
		startBin = 0;
	    }
	    if (endBin > _nBins - 1) {
		endBin = _nBins - 1;
	    }
	    
	    for (int jj = startBin; jj <= endBin; jj++) {
		_color[jj] = ((ColorScaleEntry) _colorArray.get(ii)).getColor();
	    }

	}

	for (int ii = 0; ii < _nBins; ii++) {
	    if (_color[ii] == null) {
		_color[ii] = _transparent;
	    }
	}

    }

    public Color getColor(double val) {
	if (val < _minVal || val > _maxVal) {
	    return _transparent;
	}
	int bin = (int) ((val - _minVal) / _binWidth);
	if (bin < 0) {
	    bin = 0;
	} else if (bin > _nBins - 1) {
	    bin = _nBins - 1;
	}
	return _color[bin];
    }

    public int getNColors() {
	return _colorArray.size();
    }
    
    public ColorScaleEntry getEntry(int ii) {
	return (ColorScaleEntry) _colorArray.get(ii);
    }
    
}

