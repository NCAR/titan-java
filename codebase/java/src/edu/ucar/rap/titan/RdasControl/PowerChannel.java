///////////////////////////////////////////////////////////////////////
//
// PowerChannel
//
// JPanel with on/off lights
//
// Mike Dixon
//
// Nov 2002
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import edu.ucar.rap.jrp.*;

class PowerChannel extends JPanel {
    
  private int _channelNum;
  private PowerStatusGroup _parent;
  private String _labelStr;
  private ImageIcon _onIcon, _offIcon;
  private JLabel _label, _indicator;
  private boolean _invertPolarity = false;
  private boolean _active = false;
  private boolean _warn = false;
  private boolean _state = false;
  private long _timeLastSet = 0;
  
  public PowerChannel(int channelNum,
                      PowerStatusGroup parent,
                      String labelStr,
                      boolean invertPolarity,
                      boolean active,
                      boolean warn) {
    
    _channelNum = channelNum;
    _parent = parent;
    _labelStr = labelStr;
    _invertPolarity = invertPolarity;
    _active = active;
    _warn = warn;
	
    TrDummyComponent dc = new TrDummyComponent();
    Image offImage = JrpImageLoad.getFromRes
      (dc, "/edu/ucar/rap/titan/RdasControl/images/off_light_small.png");
    _offIcon = new ImageIcon(offImage);

    Image onImage = JrpImageLoad.getFromRes
      (dc, "/edu/ucar/rap/titan/RdasControl/images/on_light_small.png");
    _onIcon = new ImageIcon(onImage);
	
    _label = new JLabel(_labelStr);
    _indicator = new JLabel(_offIcon);
    
    add(_label);
    add(_indicator);
	
  }
    
  public void setLabelStr(String text) {
    _labelStr = text;
    _label.setText(_labelStr);
  }

  public void setInvert(boolean invert) {
    _invertPolarity = invert;
  }

  public void setActive(boolean active) {
    _active = active;
  }

  public void setWarn(boolean warn) {
    _warn = warn;
  }

  public int getPreferredLabelWidth() {
    return (int) _label.getPreferredSize().getWidth();
  }
    
  public void setPreferredLabelWidth(int width) {
    Dimension size = _label.getPreferredSize();
    size.width = width;
    _label.setPreferredSize(size);
  }

  public void setState(boolean state) {
    if (_invertPolarity) {
      state = !state;
    }
    long now = TimeManager.getTime();
    boolean force = false;
    if (now - _timeLastSet > 5000) {
      force = true;
    }
    if (state != _state || force) {
      synchronized(_indicator) {
        if (state) {
          _indicator.setIcon(_onIcon);
        } else {
          _indicator.setIcon(_offIcon);
        }
      }
      _state = state;
      _timeLastSet = now;
    }
  }

  // get state as a 0 or 1

  public String getStateBinary() {
    if (!_active || !_warn || _state) {
      return "0";
    }
    return "1";
  }

  // get state as a string

  public String getStateText() {

    if (getStateBinary().equals("0")) {
      return "";
    } 

    return ("POWER : channel=" + _channelNum + ", " +
            "label=" + _labelStr + ", " +
            "state=DOWN\n");
  }

}

