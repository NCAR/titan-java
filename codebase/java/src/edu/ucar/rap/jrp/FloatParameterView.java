// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:38:1 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;

import javax.swing.*;

/*******************************************************************
 * Float parameter view.
 * 
 * @author Mike Dixon
 */

public class FloatParameterView extends AbstractParameterView

{

  /**
   * editor component
   */
    
  private JTextField _jTextField;

  /**
   * parameter associated with this object
   */
    
  private FloatParameter _param;

  /**
   * constructor
   *
   * @param param parameter to be associated with this object
   */
    
  public FloatParameterView(FloatParameter param) {
    super(param);
    _param = param;
    _jTextField =
      new JTextField(Float.toString(param.getValue()),
                     getTextFieldLen());
    setEditor(_jTextField);
    getEditor().setToolTipText(param.getDescription());
  }

  /**
   * Ensures view shows the current parameter value.
   */
    
  public void syncParamToView() {
    _jTextField.setText(Float.toString(_param.getValue()));
  }
    
  /**
   * Sets parameter value from the view.
   */
    
  public int syncViewToParam() {
    try {
      _param.setValueCheckRange(_jTextField.getText());
    }
    catch (DataValueException e) {
      JOptionPane.showMessageDialog(_jTextField, e.getMessage(),
                                    "Illegal float value",
                                    JOptionPane.ERROR_MESSAGE);
      return -1;
    }
    return 0;
  }

}
