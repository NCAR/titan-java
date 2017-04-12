// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:38:15 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;

import javax.swing.*;

/*******************************************************************
 * Boolean parameter view.
 * 
 * @author Mike Dixon
 */

public class BooleanParameterView extends AbstractParameterView

{

  /**
   * editor component
   */
    
  private JCheckBox _checkBox;

  /**
   * parameter associated with this object
   */
    
  private BooleanParameter _param;
    
  /**
   * constructor
   *
   * @param param parameter to be associated with this object
   */
    
  public BooleanParameterView(BooleanParameter param) {
    super(param);
    _param = param;
    _checkBox = new JCheckBox();
    setEditor(_checkBox);
    getEditor().setToolTipText(param.getDescription());
  }
    
  /**
   * Ensures view shows the current parameter value.
   */
    
  public void syncParamToView() {
    _checkBox.setSelected(_param.getValue());
  }
    
  /**
   * Sets parameter value from the view.
   */
    
  public int syncViewToParam() {
    if (_checkBox.isSelected()) {
      _param.setValue(true);
    } else {
      _param.setValue(false);
    }
    return 0;
  }
    
}
