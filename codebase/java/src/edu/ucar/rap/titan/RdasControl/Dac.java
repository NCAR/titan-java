///////////////////////////////////////////////////////////////////////
//
// Dac
//
// JPanel with representation for a DAC - Digital to analog converter
//
// Mike Dixon
//
// April 2004
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import edu.ucar.rap.jrp.*;

class Dac extends JPanel {
    
    private Parameters _params = Parameters.getInstance();
    private ControlModel _model = ControlModel.getInstance();
    
    private int _channelNum;
    private String _labelStr;
    private JLabel _label;
    private JSlider _slider;
    private int _value;
    private int _dataDelay;
    private long _timeOfLastCommand = TimeManager.getTime();
    
    public Dac(int channelNum,
	       String labelStr,
	       int initValue,
	       int minValue,
	       int maxValue,
	       int dataDelay) {
	
	_channelNum = channelNum;
	_labelStr = labelStr;
	
	_label = new JLabel(labelStr);
	
	_slider = new JSlider();
	_slider.setMinimum(minValue);
	_slider.setMaximum(maxValue);
	_slider.addChangeListener(new SliderListener());
	_setPreferredWidth();
	_dataDelay = dataDelay;

	setLabelText();
	
	setLayout(new BorderLayout());
	add(_label, BorderLayout.NORTH);
	add(_slider, BorderLayout.SOUTH);

	setValue(initValue);

    }
    
    // set label string - will be done in response to
    // changes in configuration
    
    public void setLabelStr(String labelStr) {
	_labelStr = labelStr;
	setLabelText();
    }
    
    public void setLabelText() {
	String text = _labelStr + ": " + _value;
	_label.setText(text);
	_setPreferredWidth();
    }

    // get value

    public int getValue() {
	return _value;
    }
    
    // set value

    public void setValue(int value) {
	_value = value;
	_slider.setValue(_value);
	_model.setDacValue(_channelNum, _value);
    }
    
    // preferred width

    private void _setPreferredWidth() {
	Dimension size = _label.getPreferredSize();
	size.width = _params.control.dacs.preferredWidth.getValue();
	_slider.setPreferredSize(size);
    }

    // enable control
    
    public void enableControl(boolean state) {
	_slider.setEnabled(state);
    }

    // set dac state
    
    private class SliderListener implements ChangeListener {
	public void stateChanged(ChangeEvent e) {
	    boolean sendCommand = true;
	    long now = TimeManager.getTime();
	    if (_slider.getValueIsAdjusting()) {
		long elapsed = now - _timeOfLastCommand;
		if (elapsed < _dataDelay) {
		    sendCommand = false;
		}
	    }
	    if (sendCommand) {
		_value = _slider.getValue();
		_model.setDacValue(_channelNum, _value);
		_timeOfLastCommand = now;
		setLabelText();
	    }
	}
    }

}

