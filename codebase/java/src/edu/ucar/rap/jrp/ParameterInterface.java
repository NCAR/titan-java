// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:37:51 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;

/*******************************************************************
 * Core methods for all parameters
 * 
 * @author Mike Dixon
 */

public interface ParameterInterface

{

  /**
   * set the value from a String object
   */
    
  public void setValue(String value);

  /**
   * copy default value to current value
   */
    
  public void setValueFromDefault();
    
  /**
   * copy current value to default value
   */
    
  public void setDefaultFromValue();

  /**
   * get the parameter type
   */
    
  public ParameterType getType();
    
  /**
   * get the view associated with this parameter
   */
    
  public AbstractParameterView getView();

}
