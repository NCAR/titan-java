// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:38:15 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;

/*******************************************************************
 * Boolean parameter.
 * 
 * @author Mike Dixon
 */

public class BooleanParameter extends AbstractParameter

{

  /**
   * current value
   */
    
  private boolean _value;

  /**
   * default value
   */
    
  private boolean _default;

  /**
   * view associated with this object
   */
    
  private BooleanParameterView _view = null;

  /**
   * constructor
   *
   * @param name the name by which this parameter will be referenced
   */
    
  public BooleanParameter(String name) {
    super(name);
  }

  /**
   * Set the value from a boolean
   */
    
  public void setValue(boolean value) {
    _value = value;
    setUnsavedChanges(true);
  }

  /**
   * Set the value from a Boolean object
   */
    
  public void setValue(Boolean value) {
    _value = value.booleanValue();
    setUnsavedChanges(true);
  }

  /**
   * Set the value from a String
   *
   * @param value if equals 'true' in case-insensitive way, 
   *              parameter will be set to true
   *              otherwise parameter will be set to false
   */
    
  public void setValue(String value) {
    _value = Boolean.valueOf(value).booleanValue();
    setUnsavedChanges(true);
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
   * Get the parameter type
   * @return ParameterType.BOOLEAN
   */
    
  public ParameterType getType() {
    return ParameterType.BOOLEAN;
  }
    
  /**
   * get the current value
   */

  public boolean getValue() {
    return _value;
  }
    
  /**
   * return value as a string
   */
    
  public String toString() {
    Boolean val = new Boolean(_value);
    return val.toString();
  }

  /**
   * get the view associated with the object
   */
    
  public AbstractParameterView getView() {
    if (_view == null) {
      _view = new BooleanParameterView(this);
    }
    return (AbstractParameterView) _view;
  }
}
