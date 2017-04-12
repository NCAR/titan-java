// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:38:11 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;
import javax.swing.JOptionPane;

/*******************************************************************
 * Integer parameter.
 * 
 * @author Mike Dixon
 */

public class IntegerParameter extends AbstractParameter

{

  /**
   * current value
   */
    
  private int _value;

  /**
   * default value
   */
    
  private int _default;

  /**
   * min and max value
   */
    
  private int _minValue = -Integer.MAX_VALUE;
  private int _maxValue = Integer.MAX_VALUE;

  /**
   * view associated with this object
   */
    
  private IntegerParameterView _view = null;

  /**
   * constructor
   *
   * @param name the name by which this parameter will be referenced
   */
    
  public IntegerParameter(String name) {
    super(name);
  }

  /**
   * Set the value from a int
   */
    
  public void setValue(int value) {
    _setValue(value);
  }
  
  /**
   * Set the value from a Int
   */
    
  public void setValue(Integer value) {
    _setValue(value.intValue());
  }

  /**
   * Set the value from a String
   *
   * @param value text representation of int
   */
    
  public void setValue(String value) {
    try {
      setValueCheckRange(value);
    }
    catch (DataValueException e) {
      System.err.println(e.getMessage());
    }
  }

  /**
   * Set the value and check for exceptions
   * @throws DataValueException
   */
    
  public void setValueCheckRange(int value) throws DataValueException {
    if (value < _minValue) {
      String errStr =
        "Value less than legal minimum\n" +
        "   Parameter: " + getLabel() + "\n" +
        "   Requested value: " + value + "\n" +
        "   Min legal value: " + _minValue;
      throw(new DataValueException(errStr));
    } else if (value > _maxValue) {
      String errStr =
        "Value greater than legal maximum\n" +
        "   Parameter: " + getLabel() + "\n" +
        "   Requested value: " + value + "\n" +
        "   Max legal value: " + _maxValue;
      throw(new DataValueException(errStr));
    } else {
      _setValue(value);
    }
  }

  public void setValueCheckRange(String value) throws DataValueException {
    int dvalue = _minValue;
    try {
      dvalue = Integer.parseInt(value);
    }
    catch (NumberFormatException e) {
      String errStr =
        "Invalid value for int\n" +
        "   Parameter: " + getLabel() + "\n" +
        "   Requested value: " + value + "\n";
      throw(new DataValueException(errStr));
    }
    setValueCheckRange(dvalue);
  }

  /**
   * set the current value from the default value
   */
    
  public void setValueFromDefault() {
    _value = _default;
    setUnsavedChanges(true);
  }

  /**
   * set the default from the current value
   */
    
  public void setDefaultFromValue() {
    _default = _value;
  }

  /**
   * Set the min and max value
   */
    
  public void setMinValue(int value) {
    _minValue = value;
  }

  public void setMaxValue(int value) {
    _maxValue = value;
  }

  /**
   * private for setting value
   */
    
  private void _setValue(int value) {
    _value = value;
    setUnsavedChanges(true);
  }

  /**
   * Get the parameter type
   * @return ParameterType.INTEGER
   */
    
  public ParameterType getType() {
    return ParameterType.INTEGER;
  }

  /**
   * get the current value
   */

  public int getValue() {
    return _value;
  }

  public int getMinValue() {
    return _minValue;
  }

  public int getMaxValue() {
    return _maxValue;
  }

  /**
   * return value as a string
   */
    
  public String toString() {
    Integer val = new Integer(_value);
    return val.toString();
  }

  /**
   * get the view associated with the object
   */
    
  public AbstractParameterView getView() {
    if (_view == null) {
      _view = new IntegerParameterView(this);
    }
    return (AbstractParameterView) _view;
  }

}
