///////////////////////////////////////////////////////////////////////
//
// MathUtils
//
// Mike Dixon
//
// Dec 2002
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.util.*;

public class MathUtils

{
    
    static private double _ln10 = Math.log(10.0);

    static public double log10(double arg) {
	return Math.log(arg) / _ln10;
    }
    
}
