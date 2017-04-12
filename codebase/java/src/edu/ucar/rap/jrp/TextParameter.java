// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:38:3 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;

/*******************************************************************
 * Text parameter.
 * Supports a multi-line text area.
 * 
 * @author Mike Dixon
 */

public class TextParameter extends AbstractParameter

{


  /**
   * current value
   */
    
  private String _value;

  /**
   * default value
   */
    
  private String _default;

  /**
   * view associated with this object
   */
    
  private TextParameterView _view = null;
    
  /**
   * constructor
   *
   * @param name the name by which this parameter will be referenced
   */
    
  public TextParameter(String name) {
    super(name);
  }
    
  /**
   * Set the value from a String
   */
    
  public void setValue(String value) {
    _value = new String(value);
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
   * @return ParameterType.STRING
   */
    
  public ParameterType getType() {
    return ParameterType.STRING;
  }

  /**
   * get the current value
   */

  public String getValue() {
    return _value;
  }

  /**
   * return value as a string
   */
    
  public String toString() {
    return new String(_value);
  }
    
  /**
   * get the view associated with the object
   */
    
  public AbstractParameterView getView() {
    if (_view == null) {
      _view = new TextParameterView(this);
    }
    return (AbstractParameterView) _view;
  }

}
