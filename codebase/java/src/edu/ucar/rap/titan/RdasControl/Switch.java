///////////////////////////////////////////////////////////////////////
//
// Switch
//
// JPanel with representation for switch
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

class Switch extends JPanel {
    
  private ControlModel _model = ControlModel.getInstance();

  private int _channelNum;
  private String _labelStr;
  private SwitchGroup _group;
  private ImageIcon _normalIcon, _abnormalIcon;
  private JLabel _label, _indicator;
  private JRadioButton _onButton, _offButton;
  private boolean _on = false;
  private boolean _indicatorOn = false;
  private boolean _switchEnabled = false;
  private boolean _active = false;
  private boolean _invertPolarity = false;
  private String _normalState = "ON";
  private boolean _warn = false;
    
  public Switch(int channelNum,
                SwitchGroup group,
                String labelStr,
                boolean initOn,
                boolean switchEnabled,
                boolean active,
                boolean invertPolarity,
                String normalState,
                boolean warn) {

    _channelNum = channelNum;
    _group = group;
    _labelStr = labelStr;
    _on = initOn;
    _switchEnabled = switchEnabled;
    _active = active;
    _invertPolarity = invertPolarity;
    _normalState = normalState;
    _warn = warn;
    setLayout(new BorderLayout());

    _createIcons();

    _label = new JLabel(labelStr);
    _indicator = new JLabel("OFF", _abnormalIcon, SwingConstants.LEFT);
    _onButton = new JRadioButton("On ", false);
    _offButton = new JRadioButton("Off ", true);
	
    JPanel buttonPanel = new JPanel(new BorderLayout());
    buttonPanel.add(_offButton, BorderLayout.WEST);
    buttonPanel.add(_onButton, BorderLayout.EAST);
	
    JPanel rightPanel = new JPanel(new BorderLayout());
    rightPanel.add(buttonPanel, BorderLayout.CENTER);
    rightPanel.add(_indicator, BorderLayout.EAST);
	
    add(_label, BorderLayout.WEST);
    add(rightPanel, BorderLayout.EAST);
	
    int ht = (int) _onButton.getPreferredSize().getHeight();
    setPreferredSize(new Dimension(getWidth(), ht));

    setOn(initOn);
    _indicatorOn = false;

    ActionListener offListener = new OffListener();
    _offButton.addActionListener(offListener);
	
    ActionListener onListener = new OnListener();
    _onButton.addActionListener(onListener);
	
  }

  // get state
    
  public boolean getOn() {
    return _on;
  }
    
  // set label string - will be done in response to
  // changes in configuration
    
  public void setLabelStr(String labelStr) {
    _label.setText(labelStr);
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

  // set the switch state

  public void setOn(boolean state) {
    synchronized(_onButton) {
      _on = state;
      _onButton.setSelected(state);
      _offButton.setSelected(!state);
      _model.setSwitchState(_channelNum, state);
    }
  }

  // set the indicator state - this will be done in response to
  // flags coming back from the client

  public void setIndicatorOn(boolean state) {
    if (_invertPolarity) {
      state = !state;
    }
    _indicatorOn = state;
  }

  // redraw the indicator, to make it match the indicator state

  public void redrawIndicator() {
    synchronized(_indicator) {
      if (_indicatorOn) {
        _indicator.setText("ON");
        _indicator.setIcon(_normalIcon);
      } else {
        _indicator.setText("OFF");
        _indicator.setIcon(_abnormalIcon);
      }
      // ensure switch position matches state
      if (_invertPolarity) {
        setOn(!_indicatorOn);
      } else {
        setOn(_indicatorOn);
      }
    }
  }
	
  // enable control
    
  public void enableControl(boolean state) {
    _onButton.setEnabled(state);
    _offButton.setEnabled(state);
  }

  // set switch enabled or not
    
  public void setSwitchEnabled(boolean state) {
    _switchEnabled = state;
    _onButton.setEnabled(state);
    _offButton.setEnabled(state);
    if (!state) {
      _onButton.setSelected(false);
      _offButton.setSelected(false);
    }
    _onButton.setVisible(state);
    _offButton.setVisible(state);
  }

  // set active
    
  public void setActive(boolean state) {
    _active = state;
  }

  // invert polarity
    
  public void setInvertPolarity(boolean state) {
    _invertPolarity = state;
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

  // set switch state

  private class OffListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      setOn(false);
      _model.sendSwitchFlags();
    }
  }
    
  private class OnListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      setOn(true);
      _model.sendSwitchFlags();
    }
  }

  // create icons for indicator state

  private void _createIcons() {

    TrDummyComponent dc = new TrDummyComponent();
    Image offImage = JrpImageLoad.getFromRes
      (dc, "/edu/ucar/rap/titan/RdasControl/images/off_light_small.png");
    Image onImage = JrpImageLoad.getFromRes
      (dc, "/edu/ucar/rap/titan/RdasControl/images/on_light_small.png");
    if (_normalState.equals("ON")) {
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
    }

    if (_normalState.equals("ON")) {
      if (_on) {
        return "0";
      } else {
        return "1";
      }
    } else {
      if (_on) {
        return "1";
      } else {
        return "0";
      }
    }

  }
    
  // get state as a string
  
  public String getStateText() {

    if (getStateBinary().equals("0")) {
      return "";
    } 

    String stateStr = "ON";
    if (!_on) {
      stateStr = "OFF";
    }
    
    return ("SWITCH: channel=" + _channelNum + ", " +
            "label=" + _labelStr + ", " +
            "normalState=" + _normalState + ", " +
            "state=" + stateStr + "\n");

  }
    
}
