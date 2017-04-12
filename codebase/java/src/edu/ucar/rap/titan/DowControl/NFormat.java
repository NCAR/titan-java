///////////////////////////////////////////////////////////////////////
//
// NFormat
//
// Numbering formats
//
// Mike Dixon
//
// May 2013
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.DowControl;

import java.text.*;

public class NFormat

{

  private NumberFormat _nformat;
    
  // private constructor
    
  private NFormat(int minimumFracDigits,
                  int maximumFracDigits,
                  int minimumIntDigits) {

    _nformat = NumberFormat.getInstance();
    _nformat.setMinimumFractionDigits(minimumFracDigits);
    _nformat.setMaximumFractionDigits(maximumFracDigits);
    if (minimumIntDigits >= 0) {
      _nformat.setMinimumIntegerDigits(minimumIntDigits);
    }
    _nformat.setGroupingUsed(false);
	
  }
    
  private NFormat(String pattern) {
    _nformat = new DecimalFormat(pattern);
  }
    
  public String format(int value) {
    return _nformat.format(value);
  }

  public String format(double value) {
    return _nformat.format(value);
  }
    
  // public static instances

  // integer, various widths

  public static final NFormat i1 = new NFormat(0, 0, 1);
  public static final NFormat i2 = new NFormat(0, 0, 2);
  public static final NFormat i3 = new NFormat(0, 0, 3);
  public static final NFormat i4 = new NFormat(0, 0, 4);
  public static final NFormat i6 = new NFormat(0, 0, 6);
  public static final NFormat i8 = new NFormat(0, 0, 8);
  public static final NFormat i10 = new NFormat(0, 0, 10);
  public static final NFormat i12 = new NFormat(0, 0, 12);

  // 0 decimals

  public static final NFormat f0 = new NFormat(0, 0, -1);

  // 1 decimal

  public static final NFormat f1 = new NFormat(1, 1, -1);
  public static final NFormat f1_4 = new NFormat(1, 1, 4);

  // 2 decimals

  public static final NFormat f2 = new NFormat(2, 2, -1);
  public static final NFormat f2_2 = new NFormat(2, 2, 2);
  public static final NFormat f2_3 = new NFormat(2, 2, 3);
  public static final NFormat f2_4 = new NFormat(2, 2, 4);

  // 3 decimals
  
  public static final NFormat f3 = new NFormat(3, 3, -1);

  // 4 decimals

  public static final NFormat f4 = new NFormat(4, 4, -1);

  // 5 decimals

  public static final NFormat f5 = new NFormat(5, 5, -1);

  // 1 decimals scientific

  public static final NFormat e1 = new NFormat("0.#E0");

  // 2 decimals scientific

  public static final NFormat e2 = new NFormat("0.##E0");

  // 3 decimals scientific

  public static final NFormat e3 = new NFormat("0.###E0");

  // 4 decimals scientific

  public static final NFormat e4 = new NFormat("0.####E0");

  // elevation - 2 decimals, integer part 2

  public static final NFormat el2 = new NFormat(2, 2, 2);

  // azimuth - 1 or 2 decimals, integer part 3

  public static final NFormat az1 = new NFormat(1, 1, 3);
  public static final NFormat az2 = new NFormat(2, 2, 3);
    
}
