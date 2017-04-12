// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:38:22 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;

/*******************************************************************
 * Option parameter.
 *
 * An OptionParameter is rather like an enum, except that the options are
 * stored as strings rather than ints. You set the options as an
 * array of strings, and these appear in the view GUI as a
 * series of buttons from which you can choose one.
 * 
 * @author Mike Dixon
 */

public class OptionParameter extends AbstractParameter

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
   * Options stored as array of strings
   */
    
  private String[] _options;

  /**
   * view associated with this object
   */
    
  private OptionParameterView _view = null;

  /**
   * constructor
   *
   * @param name the name by which this parameter will be referenced
   */
    
  public OptionParameter(String name) {
    super(name);
  }

  /**
   * Set the option array
   */

  public void setOptions(String[] options) {
    _options = new String[options.length];
    for (int i = 0; i < options.length; i++) {
      _options[i] = new String(options[i]);
    }
  }

  /**
   * Set the value from a string.
   * Check whether the value is legal.
   * @throws IllegalArgumentException
   */

  public void setValue(String value) throws IllegalArgumentException {
    boolean valid = false;
    for (int i = 0; i < _options.length; i++) {
      if (value.compareTo(_options[i]) == 0) {
        valid = true;
        break;
      }
    }
    if (valid == false) {
      throw new IllegalArgumentException(value + " not a valid option");
    }
    _value = value;
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
   * @return ParameterType.OPTION
   */
    
  public ParameterType getType() {
    return ParameterType.OPTION;
  }

  /**
   * get the current value
   */

  public String getValue() {
    return _value;
  }

  public String toString() {
    return new String(_value);
  }

  public String[] getOptions() {
    String[] options = new String[_options.length];
    for (int ii = 0; ii < _options.length; ii++) {
      options[ii] = new String(_options[ii]);
    }
    return options;
  }

  public AbstractParameterView getView() {
    if (_view == null) {
      _view = new OptionParameterView(this);
    }
    return (AbstractParameterView) _view;
  }

}
