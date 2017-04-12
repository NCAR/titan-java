// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:38:22 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;

import javax.swing.*;

/*******************************************************************
 * String parameter view.
 * 
 * @author Mike Dixon
 */

public class StringParameterView extends AbstractParameterView

{

  /**
   * editor component
   */
    
  private JTextField _jTextField;

  /**
   * parameter associated with this object
   */
    
  private StringParameter _param;

  /**
   * constructor
   *
   * @param param parameter to be associated with this object
   */
    
  public StringParameterView(StringParameter param) {
    super(param);
    _param = param;
    _jTextField = new JTextField(param.getValue(), getTextFieldLen());
    setEditor(_jTextField);
    getEditor().setToolTipText(param.getDescription());
  }

  /**
   * Ensures view shows the current parameter value.
   */
    
  public void syncParamToView() {
    _jTextField.setText(_param.getValue());
  }
    
  /**
   * Sets parameter value from the view.
   */
    
  public int syncViewToParam() {
    _param.setValue(_jTextField.getText());
    return 0;
  }

}
