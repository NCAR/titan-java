// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:38:8 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;
import java.util.*;

/*******************************************************************
 * Abstract base class for array parameters.
 * 
 * @author Mike Dixon
 */

public abstract class ArrayParameter extends AbstractParameter

{

  /**
   * Flag to indicate this is a 2D array.
   * Cannot be changed after construction.
   */
    
  private boolean _is2D = false; // is this a 2D array? immutable

  /**
   * Number of columns in 2D array.
   */
    
  private int _nCols = 1;        // number of columns in 2D array
    
  /**
   * The array of values.
   */
    
  private ArrayList _value = new ArrayList();

  /**
   * The default array.
   */
    
  private ArrayList _default = new ArrayList();

  /**
   * Constructor
   *
   * @param name name by which parameter is known in calling program
   * @param is2D if true array is 2-dimensional
   *             if false array is 1-dimensional
   */
    
  public ArrayParameter(String name, boolean is2D) {
    super(name);
    _is2D = is2D;
  }
    
  /**
   * Clear the array
   */

  public void clear() {
    _value.clear();
  }
    
  /**
   * Set the number of columns in the array
   */
    
  public void setColumnCount(int nCols) {
    _nCols = nCols;
  }
    
  /**
   * Set the value
   */

  public void setValue(ArrayList val) {
    _value = val;
    setUnsavedChanges(true);
  }

  /**
   * Reset the value to the default
   */
    
  public void setValueFromDefault() {
    _value = new ArrayList(_default);
    setUnsavedChanges(true);
  }

  /**
   * Set the default from the current value
   */
    
  public void setDefaultFromValue() {
    _default = new ArrayList(_value);
  }
    
  /**
   * Get the current value
   */
    
  public ArrayList getValue() {
    return _value;
  }
    
  /**
   * Get the current column count
   */
    
  public int getColumnCount() {
    return _nCols;
  }

}
