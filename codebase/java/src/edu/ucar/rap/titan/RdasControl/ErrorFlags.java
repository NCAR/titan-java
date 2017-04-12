///////////////////////////////////////////////////////////////////////
//
// ErrorFlags
//
// JPanel with representation for softwareStatus
//
// Mike Dixon
//
// Feb 2004
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import edu.ucar.rap.jrp.*;

class ErrorFlags extends JPanel {
    
  private ControlModel _model = ControlModel.getInstance();
  
  private int _channelNum;
  private String _labelStr;
  private ErrorFlagsGroup _group;
  private ImageIcon _normalIcon, _abnormalIcon;
  private JLabel _label, _indicator;
  private boolean _active = false;
  private boolean _on = false;
  private String _normalState = "OK";
  private boolean _warn = false;
  
  public ErrorFlags(int channelNum,
                    ErrorFlagsGroup group,
                    String labelStr,
                    boolean active,
                    String normalState,
                    boolean warn) {
    
    _channelNum = channelNum;
    _group = group;
    _labelStr = labelStr;
    _active = active;
    _normalState = normalState;
    _warn = warn;
    setLayout(new BorderLayout());

    _createIcons();
    
    _label = new JLabel(labelStr);
    _indicator = new JLabel("ERR", _abnormalIcon, SwingConstants.LEFT);
	
    JPanel rightPanel = new JPanel(new BorderLayout());
    rightPanel.add(_indicator, BorderLayout.EAST);
	
    add(_label, BorderLayout.WEST);
    add(rightPanel, BorderLayout.EAST);
	
    int ht = (int) (_indicator.getPreferredSize().getHeight() * 1.5);
    setPreferredSize(new Dimension(getWidth(), ht));
    
  }

  // set label string - will be done in response to
  // changes in configuration
  
  public void setLabelStr(String labelStr) {
    _label.setText(labelStr);
  }

  // set active?

  public void setActive(boolean state) {
    _active = state;
  }

  // preferred widths for labels
  
  public int getPreferredLabelWidth() {
    return (int) _label.getPreferredSize().getWidth();
  }
  
  public void setPreferredLabelWidth(int width) {
    Dimension size = _label.getPreferredSize();
    size.width = width;
    _label.setPreferredSize(size);
  }
  
  // preferred widths for indicator

  public int getPreferredIndicatorWidth() {
    return (int) _indicator.getPreferredSize().getWidth();
  }

  public void setPreferredIndicatorWidth(int width) {
    Dimension size = _indicator.getPreferredSize();
    size.width = width;
    _indicator.setPreferredSize(size);
  }

  // set the indicator state - this will be done in response to
  // flags coming back from the client

  public void setIndicatorOn(boolean state) {
    _on = state;
    redrawIndicator();
  }

  // redraw the indicator, to make it match the indicator state

  public void redrawIndicator() {
    synchronized(_indicator) {
      if (_on) {
        _indicator.setText("ERR");
        _indicator.setIcon(_abnormalIcon);
      } else {
        _indicator.setText("OK");
        _indicator.setIcon(_normalIcon);
      }
    }
  }
	
  // warn
    
  public void setWarn(boolean state) {
    _warn = state;
  }

  // normalState
    
  public void setNormalState(String state) {
    _normalState = state;
    _createIcons();
  }

  // create icons for indicator state

  private void _createIcons() {

    TrDummyComponent dc = new TrDummyComponent();
    Image offImage = JrpImageLoad.getFromRes
      (dc, "/edu/ucar/rap/titan/RdasControl/images/off_light_small.png");
    Image onImage = JrpImageLoad.getFromRes
      (dc, "/edu/ucar/rap/titan/RdasControl/images/on_light_small.png");
    if (_normalState.equals("OK")) {
      _normalIcon = new ImageIcon(onImage);
      _abnormalIcon = new ImageIcon(offImage);
    } else {
      _normalIcon = new ImageIcon(offImage);
      _abnormalIcon = new ImageIcon(onImage);
    }

  }

  // get state as a 0 or 1
  
  public String getStateBinary() {
    if (!_active || !_warn) {
      return "0";
    } else if (_on) {
      return "1";
    } else {
      return "0";
    }
  }

  // get state as a string
  
  public String getStateText() {

    if (getStateBinary().equals("0")) {
      return "";
    } 

    String stateStr = "OK";
    if (_on) {
      stateStr = "ERR";
    }
    
    return ("ERROR : channel=" + _channelNum + ", " +
            "label=" + _labelStr + ", " +
            "normalState=" + _normalState + ", " +
            "state=" + stateStr + "\n");

  }
    
}
