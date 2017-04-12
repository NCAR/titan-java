// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:38:32 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;

import java.awt.Color;
import javax.swing.*;
import java.awt.event.*;

/*******************************************************************
 * Color parameter view.
 * 
 * @author Mike Dixon
 */

public class ColorParameterView extends AbstractParameterView

{

  /**
   * editor component
   */
    
  private JButton _button;

  /**
   * parameter associated with this object
   */
    
  private ColorParameter _param;
    
  /**
   * constructor
   *
   * @param param parameter to be associated with this object
   */
    
  public ColorParameterView(ColorParameter param) {
    super(param);
    _param = param;
    _button = new JButton("Click to change");
    setEditor(_button);
    getEditor().setToolTipText("Click button to change color");
    _button.addActionListener(new ApplyListener());
    _setButtonColor(_param.getValue());
  }

  /**
   * Ensures view shows the current parameter value.
   */
    
  public void syncParamToView() {
    _setButtonColor(_param.getValue());
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
      Color selectedColor =
        JColorChooser.showDialog(_button,
                                 "Pick a color",
                                 _param.getValue());
      if (selectedColor != null) {
        _setButtonColor(selectedColor);
        _param.setValue(selectedColor);
      }
    }
  }

  // set the color

  private void _setButtonColor(Color color) {
    _button.setBackground(color);
    double brightness =
      (color.getRed() + color.getGreen() + color.getBlue()) / 3.0;
    if (brightness < 127) {
      _button.setForeground(Color.WHITE);
    } else {
      _button.setForeground(Color.BLACK);
    }
//     Color foregnd = new Color(255 - color.getRed(),
//                               255 - color.getGreen(),
//                               255 - color.getBlue());
//     _button.setForeground(foregnd);
  }
    
}

