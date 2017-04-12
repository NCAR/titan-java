// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:38:27 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;

import javax.swing.*;

/*******************************************************************
 * Core methods for ParamView
 * 
 * @author Mike Dixon
 */

public interface ParameterViewInterface

{

  /**
   * get the label component
   * This contains the parameter label in the GUI.
   */

  public JLabel getJLabel();

  /**
   * get the editor component
   * This is the component for setting the parameter
   */
    
  public JComponent getEditor();

  /**
   * Ensures view shows the current parameter value.
   */
    
  public void syncParamToView();

  /**
   * Sets parameter value from the view state.
   */
    
  public int syncViewToParam(); // returns 0 on success, -1 on failure
    
}
