// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:38:23 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;
import javax.swing.JOptionPane;

/*******************************************************************
 * Double parameter.
 * 
 * @author Mike Dixon
 */

public class DoubleParameter extends AbstractParameter

{

  /**
   * current value
   */
    
  private double _value;

  /**
   * default value
   */
    
  private double _default;

  /**
   * min and max value
   */
    
  private double _minValue = -Double.MAX_VALUE;
  private double _maxValue = Double.MAX_VALUE;

  /**
   * view associated with this object
   */
    
  private DoubleParameterView _view = null;

  /**
   * constructor
   *
   * @param name the name by which this parameter will be referenced
   */
    
  public DoubleParameter(String name) {
    super(name);
  }

  /**
   * Set the value from a double
   */
    
  public void setValue(double value) {
    _setValue(value);
  }
  
  /**
   * Set the value from a float
   */
    
  public void setValue(float value) {
    _setValue(value);
  }
  
  /**
   * Set the value from a Double
   */
    
  public void setValue(Double value) {
    _setValue(value.doubleValue());
  }

  /**
   * Set the value from a String
   *
   * @param value text representation of double
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
    
  public void setValueCheckRange(double value) throws DataValueException {
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
    double dvalue = _minValue;
    try {
      dvalue = Double.parseDouble(value);
    }
    catch (NumberFormatException e) {
      String errStr =
        "Invalid value for double\n" +
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
    
  public void setMinValue(double value) {
    _minValue = value;
  }

  public void setMaxValue(double value) {
    _maxValue = value;
  }

  /**
   * private for setting value
   */
    
  private void _setValue(double value) {
    _value = value;
    setUnsavedChanges(true);
  }

  /**
   * Get the parameter type
   * @return ParameterType.DOUBLE
   */
    
  public ParameterType getType() {
    return ParameterType.DOUBLE;
  }

  /**
   * get the current value
   */

  public double getValue() {
    return _value;
  }

  public double getMinValue() {
    return _minValue;
  }

  public double getMaxValue() {
    return _maxValue;
  }

  /**
   * return value as a string
   */
    
  public String toString() {
    Double val = new Double(_value);
    return val.toString();
  }

  /**
   * get the view associated with the object
   */
    
  public AbstractParameterView getView() {
    if (_view == null) {
      _view = new DoubleParameterView(this);
    }
    return (AbstractParameterView) _view;
  }

}
