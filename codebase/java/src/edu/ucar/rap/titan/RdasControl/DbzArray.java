///////////////////////////////////////////////////////////////////////
//
// DbzArray - singleton
//
// Singleton
//
// Dbz array computations
//
// Mike Dixon
//
// Jan 2004
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.util.*;
import java.lang.*;

public class DbzArray
    
{
    
    private static final DbzArray _instance = new DbzArray();
    
    private double _atmosAtten = 0.014 * 1.0e-3;
    private double _calSlope = 100.0;
    private double _calOffset1km = 9000.0;
    private double _mdsCount = 0;
    private double _mdsPower = 0;
    
    private int _prevNGates = 0;
    private double _prevStartRange = 0.0;
    private double _prevGateSpacing = 0.0;
    private double _prevAtmosAtten = 0.0;
    private double _prevCalSlope = 0.0;
    private double _prevCalOffset1km = 0.0;
    private double _range[] = null;
    private double _rangeCorrection[] = null;
    
    // get singleton instance
    
    public static DbzArray getInstance() {
	return _instance;
    }
    
    // set cal parameters
    
    public void setCal(double atmosAtten,
		       double calSlope,
		       double calOffset1km,
		       double mdsCount,
		       double mdsPower) {
	_atmosAtten = atmosAtten;
	_calSlope = calSlope;
	_calOffset1km = calOffset1km;
	_mdsCount = mdsCount;
	_mdsPower = mdsPower;
    }
    
    // compute the DBZ array, returning it
    
    public float[] computeDbz(int nGates,
			      double startRange,
			      double gateSpacing,
			      short[] counts) {
	
	_computeRangeArrays(nGates, startRange, gateSpacing);
	float dbz[] = new float[nGates];
	for (int i = 0; i < nGates; i++) {
	    double dbz1km = (counts[i] - _calOffset1km) / _calSlope;
	    dbz[i] = (float) (dbz1km + _rangeCorrection[i]);
	}
	return dbz;
	
    } // computeDbz()

    // compute the SNR array, returning it
    
    public float[] computeSnr(int nGates,
			      short[] counts) {
	
	float snr[] = new float[nGates];
	for (int i = 0; i < nGates; i++) {
	    double dsnr = (counts[i] - _mdsCount) / _calSlope;
	    snr[i] = (float) dsnr;
	}
	return snr;
	
    } // computeDbz()

    // compute range and range correction arrays
    
    private void _computeRangeArrays(int nGates,
				     double startRange,
				     double gateSpacing) {
	
	// return now if all is same as before
	
	if (nGates == _prevNGates &&
	    startRange == _prevStartRange &&
	    gateSpacing == _prevGateSpacing &&
	    _atmosAtten == _prevAtmosAtten &&
	    _calSlope == _prevCalSlope &&
	    _calOffset1km == _prevCalOffset1km) {
	    return;
	}
	
	_range = new double[nGates];
	_rangeCorrection = new double[nGates];
	
	for (int i = 0; i < nGates; i++) {
	    _range[i] = startRange + i * gateSpacing;
	    double rangeMeters = _range[i] * 1000.0;
	    double log10Range = MathUtils.log10(rangeMeters) - 3.0;
	    _rangeCorrection[i] =
		20.0 * log10Range + rangeMeters * _atmosAtten;
	}
	
	_prevNGates = nGates;
	_prevStartRange = startRange;
	_prevGateSpacing = gateSpacing;
	_prevAtmosAtten = _atmosAtten;
	_prevCalSlope = _calSlope;
	_prevCalOffset1km = _calOffset1km;

    } // _computeRangeArrays()
	    
}
