// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:38:21 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;
import java.awt.Color;
import java.lang.NumberFormatException;

/*******************************************************************
 * Color parameter.
 *
 * @author Mike Dixon
 */

public class ColorParameter extends AbstractParameter

{

  /**
   * current value
   */
    
  private Color _value = Color.BLACK;

  /**
   * default value
   */
    
  private Color _default;

  /**
   * view associated with this object
   */
    
  private ColorParameterView _view = null;

  /**
   * constructor
   *
   * @param name the name by which this parameter will be referenced
   */
    
  public ColorParameter(String name) {
    super(name);
  }

  /**
   * set the value from a String object
   */
    
  public void setValue(String colorStr) {
    try {
      _value = Color.decode(colorStr);
    }
    catch (NumberFormatException e) {
      System.err.println("ERROR - ColorParameter.setValue");
      System.err.println("  Cannot interpret colorStr: " + colorStr);
    }
    setUnsavedChanges(true);
  }

  /**
   * Set the color
   */

  public void setValue(Color color) {
    _value = color;
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
   * @return ParameterType.COLOR
   */
    
  public ParameterType getType() {
    return ParameterType.COLOR;
  }

  /**
   * get the current value
   */

  public Color getValue() {
    return _value;
  }

  public String toString() {
    String hex = Integer.toHexString(_value.hashCode());
    return new String("0x" + hex.substring(2));
  }

  public AbstractParameterView getView() {
    if (_view == null) {
      _view = new ColorParameterView(this);
    }
    return (AbstractParameterView) _view;
  }

}
