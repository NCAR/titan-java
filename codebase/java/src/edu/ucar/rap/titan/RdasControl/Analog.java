///////////////////////////////////////////////////////////////////////
//
// Analog
//
// JPanel with representation for an analog channel
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

class Analog extends JPanel {

  private int _channelNum;
  private String _labelStr;
  private String _unitsStr;
  private double _offset;
  private double _slope;
  private boolean _warn;
  private double _minimum;
  private double _maximum;
  private boolean _active;
  private double _value = 0.0;
    
  private AnalogGroup _group;
  private ImageIcon _okIcon, _badIcon, _grayIcon;
  private JLabel _label, _units, _indicator;
  private JLabel _valueField;
  private boolean _withinLimits = false;
    
    
  public Analog(AnalogGroup group,
                int channelNum,
                String labelStr,
                String unitsStr,
                double offset,
                double slope,
                boolean warn,
                double minimum,
                double maximum,
                boolean active) {
	
    _group = group;
    _channelNum = channelNum;
    _labelStr = labelStr;
    _unitsStr = unitsStr;
    _offset = offset;
    _slope = slope;
    _warn = warn;
    _minimum = minimum;
    _maximum = maximum;
    _active = active;
	
    TrDummyComponent dc = new TrDummyComponent();
    Image badImage = JrpImageLoad.getFromRes
      (dc, "/edu/ucar/rap/titan/RdasControl/images/off_light_small.png");
    _badIcon = new ImageIcon(badImage);
    Image okImage = JrpImageLoad.getFromRes
      (dc, "/edu/ucar/rap/titan/RdasControl/images/on_light_small.png");
    _okIcon = new ImageIcon(okImage);
    Image grayImage = JrpImageLoad.getFromRes
      (dc, "/edu/ucar/rap/titan/RdasControl/images/gray_light_small.png");
    _grayIcon = new ImageIcon(grayImage);
	
    _label = new JLabel(labelStr);
    _units = new JLabel(unitsStr);
    _indicator = new JLabel(_grayIcon);
	
    _valueField = new JLabel("-999.999", JLabel.CENTER);
    _valueField.setForeground(Color.green);
    _valueField.setBackground(Color.black);
    // _valueField.setFont(new Font("Helvetica", Font.BOLD, 16));
	
    Dimension size = _valueField.getPreferredSize();
    _valueField.setPreferredSize(size);
    JPanel valPanel = new JPanel();
    valPanel.setBackground(Color.black);
    valPanel.add(_valueField);

    add(_label);
    add(valPanel);
    add(_units);
    add(_indicator);
	
  }

  // set label and units strings
  // will be done in response to changes in configuration
    
  public void setLabelStr(String labelStr) {
    _label.setText(labelStr);
  }
  public void setUnitsStr(String unitsStr) {
    _units.setText(unitsStr);
  }

  // preferred widths

  public int getPreferredLabelWidth() {
    return (int) _label.getPreferredSize().getWidth();
  }

  public void setPreferredLabelWidth(int width) {
    Dimension size = _label.getPreferredSize();
    size.width = width;
    _label.setPreferredSize(size);
  }

  public int getPreferredUnitsWidth() {
    return (int) _units.getPreferredSize().getWidth();
  }

  public void setPreferredUnitsWidth(int width) {
    Dimension size = _units.getPreferredSize();
    size.width = width;
    _units.setPreferredSize(size);
  }

  // slope and offset

  public void setOffset(double offset) {
    _offset = offset;
  }
  public void setSlope(double slope) {
    _slope = slope;
  }

  // warn?
    
  public void setWarn(boolean warn) {
    _warn = warn;
  }
  public void setMinimum(double minimum) {
    _minimum = minimum;
  }
  public void setMaximum(double maximum) {
    _maximum = maximum;
  }

  // active?
    
  public void setActive(boolean active) {
    _active = active;
  }

  // set value - this will be done in response to
  // flags coming back from the client
    
  public void setValue(double voltage) {
    synchronized(_valueField) {
      _value = _offset + _slope * voltage;
      if (_value >= _minimum && _value <= _maximum) {
        _withinLimits = true;
      } else {
        _withinLimits = false;
      }
    }
  }
    
  // redraw
    
  public void redraw() {
    synchronized(_valueField) {
      String valStr = NFormat.f2.format(_value);
      _valueField.setText(valStr);
      if (_warn) {
        if (_withinLimits) {
          _indicator.setIcon(_okIcon);
          _valueField.setForeground(Color.green);
        } else {
          _indicator.setIcon(_badIcon);
          _valueField.setForeground(Color.pink);
        }
      } else {
        _indicator.setIcon(_grayIcon);
        _valueField.setForeground(Color.green);
      }
    }
  }
    
  // get state as a 0 or 1
  
  public String getStateBinary() {
    if (!_active || !_warn) {
      return "0";
    } else if (_withinLimits) {
      return "0";
    } else {
      return "1";
    }
  }

  // get state as a string
  
  public String getStateText() {
    
    if (getStateBinary().equals("0")) {
      return "";
    } 

    return ("ANALOG: channel=" + _channelNum + ", " +
            "label=" + _labelStr + ", " +
            "limits=[" + _minimum + ":" + _maximum + "], " +
            "value=" + NFormat.f4.format(_value) + "\n");

  }
    
}
