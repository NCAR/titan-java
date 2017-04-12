// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:38:25 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;

import javax.swing.*;

/*******************************************************************
 * Option parameter view.
 * 
 * An OptionParametereter is rather like an enum, except that the options are
 * stored as strings rather than ints. You set the options as an
 * array of strings, and these appear in the view GUI as a
 * series of buttons from which you can choose one.
 * 
 * @author Mike Dixon
 */

public class OptionParameterView extends AbstractParameterView

{

  /**
   * editor component
   */
    
  private JComboBox _jComboBox;

  /**
   * parameter associated with this object
   */
    
  private OptionParameter _param;
    
  /**
   * constructor
   *
   * @param param parameter to be associated with this object
   */
    
  public OptionParameterView(OptionParameter param) {
    super(param);
    _param = param;
    _jComboBox = new JComboBox(param.getOptions());
    setEditor(_jComboBox);
    getEditor().setToolTipText(param.getDescription());
    String selected = param.getValue();
    for (int ii = 0; ii < _jComboBox.getItemCount(); ii++) {
      if (selected.equals(_jComboBox.getItemAt(ii))) {
        _jComboBox.setSelectedIndex(ii);
      }
    }
  }

  /**
   * Ensures view shows the current parameter value.
   */
    
  public void syncParamToView() {
    String selected = _param.getValue();
    for (int ii = 0; ii < _jComboBox.getItemCount(); ii++) {
      if (selected.equals(_jComboBox.getItemAt(ii))) {
        _jComboBox.setSelectedIndex(ii);
      }
    }
  }
    
  /**
   * Sets parameter value from the view.
   */
    
  public int syncViewToParam() {
    String selected = (String) _jComboBox.getSelectedItem();
    _param.setValue(selected);
    return 0;
  }

}

