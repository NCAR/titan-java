// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:38:19 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;

/*******************************************************************
 * Integer array parameter.
 * 
 * @author Mike Dixon
 */

public class IntegerArrayParameter extends ArrayParameter

{
    

  /**
   * view associated with this object
   */
    
  private IntegerArrayParameterView _view = null;
    
  /**
   * constructor
   *
   * @param name the name by which this parameter will be referenced
   * @param is2D set true is this is a 2D parameter
   */
    
  public IntegerArrayParameter(String name, boolean is2D) {
    super(name, is2D);
  }
    
  /**
   * add Integer value to the array
   */

  public void add(Integer value) {
    getValue().add(value);
    setUnsavedChanges(true);
  }
    
  /**
   * add int value to the array
   */

  public void add(int value) {
    add(new Integer(value));
  }
    
  /**
   * set Integer value at a given index.
   * If the array is not big enough it is enlarged.
   */

  public void setValue(int index, Integer value) {
    getValue().ensureCapacity(index + 1);
    getValue().set(index, value);
    setUnsavedChanges(true);
  }
    
  /**
   * set int value at a given index.
   * If the array is not big enough it is enlarged.
   */

  public void setValue(int index, int value) {
    setValue(index, new Integer(value));
  }

  /**
   * set int value from string at a given index.
   * If the array is not big enough it is enlarged.
   * @throws NumberFormatException
   */

  public void setValue(int index, String value) throws NumberFormatException {
    setValue(index, new Integer(value));
  }
    
  // set value from comma-delimited string

  /**
   * set values from array of strings.
   * If the array is not big enough it is enlarged.
   * @throws NumberFormatException
   */

  public void setValue(String value) throws NumberFormatException {
    String[] strArray = value.split(",");
    for (int i = 0; i < strArray.length; i++) {
      setValue(i, strArray[i]);
    }
  }

  /**
   * Get the parameter type
   * @return ParameterType.INTEGER_ARRAY
   */
    
  public ParameterType getType() {
    return ParameterType.INTEGER_ARRAY;
  }

  /**
   * get value at an index
   * @throws IndexOutOfBoundsException
   */
    
  public int getValue(int index) throws IndexOutOfBoundsException {
    return ((Integer) getValue().get(index)).intValue();
  }

  /**
   * get String representation of array
   */
    
  public String toString() {
    return getValue().toString();
  }

    
  /**
   * get the view associated with the object
   */
    
  public AbstractParameterView getView() {
    if (_view == null) {
      _view = new IntegerArrayParameterView(this);
    }
    return (AbstractParameterView) _view;
  }

}
