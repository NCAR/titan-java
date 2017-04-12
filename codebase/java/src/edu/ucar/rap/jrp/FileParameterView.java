// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:38:5 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;

import java.io.File;
import javax.swing.*;
import java.awt.event.*;

/*******************************************************************
 * File parameter view.
 * 
 * @author Mike Dixon
 */

public class FileParameterView extends AbstractParameterView

{

  /**
   * editor component
   */
    
  private JButton _button;

  /**
   * parameter associated with this object
   */
    
  private FileParameter _param;

 /**
   * controls whether the user can select files, directories, or both.
   */

  private int _FileSelectionMode = JFileChooser.FILES_ONLY;


  /**
   * constructor
   *
   * @param param parameter to be associated with this object
   */
    
  public FileParameterView(FileParameter param) {
    super(param);
    _param = param;
    _button = new JButton("Click to select file");
    setEditor(_button);
    getEditor().setToolTipText("Click to select file");
    _button.addActionListener(new ApplyListener());
    _setButtonText();
  }


 /**
   * set the FileSelectionMode
   * mode can be one of the following:
   * JFileChooser.FILES_ONLY
   * JFileChooser.DIRECTORIES_ONLY
   * JFileChooser.FILES_AND_DIRECTORIES
   */
  public void setFileSelectionMode(int mode)
  {
    _FileSelectionMode = mode;
  }

  /**
   * Ensures view shows the current parameter value.
   */
    
  public void syncParamToView() {
    _setButtonText();
  }
    
  /**
   * Sets parameter value from the view.
   */
    
  public int syncViewToParam() {
    return 0;
  }

  // listener
    
  private class ApplyListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      JFileChooser chooser = new JFileChooser(_param.getValue());
      chooser.setFileSelectionMode(_FileSelectionMode);
      int state = chooser.showOpenDialog(null);
      File file = chooser.getSelectedFile();
      if (file != null &&
          state == JFileChooser.APPROVE_OPTION) {
        _param.setValue(file);
        _setButtonText();
      }
    }
  }

  // set the button text

  private void _setButtonText() {
    _button.setText(_param.getValue().getName());
  }

}

