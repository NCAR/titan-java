// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:38:19 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;
import javax.swing.JOptionPane;

/*******************************************************************
 * Float parameter.
 * 
 * @author Mike Dixon
 */

public class FloatParameter extends AbstractParameter

{

  /**
   * current value
   */
    
  private float _value;

  /**
   * default value
   */
    
  private float _default;

  /**
   * min and max value
   */
  
  private float _minValue = -Float.MAX_VALUE;
  private float _maxValue = Float.MAX_VALUE;

  /**
   * view associated with this object
   */
    
  private FloatParameterView _view = null;

  /**
   * constructor
   *
   * @param name the name by which this parameter will be referenced
   */
    
  public FloatParameter(String name) {
    super(name);
  }


  /**
   * Set the value from a float
   */
  
  public void setValue(float value) {
    _setValue(value);
  }
  
  /**
   * Set the value from a double
   */
  
  public void setValue(double value) {
    _setValue((float) value);
  }
  
  /**
   * Set the value from a Float
   */
    
  public void setValue(Float value) {
    _setValue(value.floatValue());
  }

  /**
   * Set the value from a String
   *
   * @param value text representation of float
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
    
  public void setValueCheckRange(float value) throws DataValueException {
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
    float fvalue = _minValue;
    try {
      fvalue = Float.parseFloat(value);
    }
    catch (NumberFormatException e) {
      String errStr =
        "Invalid value for float\n" +
        "   Parameter: " + getLabel() + "\n" +
        "   Requested value: " + value + "\n";
      throw(new DataValueException(errStr));
    }
    setValueCheckRange(fvalue);
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
    
  public void setMinValue(float value) {
    _minValue = value;
  }

  public void setMaxValue(float value) {
    _maxValue = value;
  }

  /**
   * private for setting value
   */
    
  private void _setValue(float value) {
    _value = value;
    setUnsavedChanges(true);
  }

  /**
   * Get the parameter type
   * @return ParameterType.FLOAT
   */
    
  public ParameterType getType() {
    return ParameterType.FLOAT;
  }

  /**
   * get the current value
   */

  public float getValue() {
    return _value;
  }

  public float getMinValue() {
    return _minValue;
  }

  public float getMaxValue() {
    return _maxValue;
  }

  /**
   * return value as a string
   */
    
  public String toString() {
    Float val = new Float(_value);
    return val.toString();
  }
    
  /**
   * get the view associated with the object
   */
    
  public AbstractParameterView getView() {
    if (_view == null) {
      _view = new FloatParameterView(this);
    }
    return (AbstractParameterView) _view;
  }

}
