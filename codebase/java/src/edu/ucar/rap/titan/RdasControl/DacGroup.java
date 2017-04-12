///////////////////////////////////////////////////////////////////////
//
// DacGroup
//
// Group of controls for the Data-to-analog converters
//
// Mike Dixon
//
// April 2004
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.lang.Math;
import edu.ucar.rap.jrp.*;

public class DacGroup extends JPanel {
    
    public static final int nChannels = 2;
    
    private Parameters _params = Parameters.getInstance();
    private Dac _dacs[] = new Dac[nChannels];
    private ParamsChangeListener _paramsChangeListener;
    
    public DacGroup(Component parent) {
	
	setBorder(CustomBorder.createTop(parent, "Dac channels", 5));
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	
	// create the dacs
	
	for (int i = 0; i < nChannels; i++) {
	    DacChannelParams channel = _params.control.dacs.channels[i];
	    _dacs[i] = new Dac(i,
			       channel.labelStr.getValue(),
			       channel.valueAtStartup.getValue(),
			       channel.minValue.getValue(),
			       channel.maxValue.getValue(),
			       channel.dataDelay.getValue());
	} // i
	
	_displayActive();
	
	// add listener for changes in params
	
	_paramsChangeListener = new ParamsChangeListener();
	_params.control.dacs.addChangeListener(_paramsChangeListener);
	for (int i = 0; i < nChannels; i++) {
	    _params.control.dacs.channels[i].addChangeListener(_paramsChangeListener);
	}
	
    }

    // enable control GUI

    public void enableControl(boolean state) {
	for (int i = 0; i < nChannels; i++) {
	    _dacs[i].enableControl(state);
	}
    }
    
    // get DAC value

    public int getValue(int index) {
	return _dacs[index].getValue();
    }
    
    // reset the switches to startup value
    
    public void resetToStartup() {
	
	for (int i = 0; i < nChannels; i++) {
	    DacChannelParams channel =
		_params.control.dacs.channels[i];
	    if (channel.active.getValue()) {
		_dacs[i].setValue(channel.valueAtStartup.getValue());
	    }
	}
	
    }

   // display active dacs

    private void _displayActive() {
	
	// remove all from panel
	
	removeAll();
	
	// update labels
	
	for (int i = 0; i < nChannels; i++) {
	    if (_params.control.dacs.channels[i].active.getValue()) {
		_dacs[i].setLabelStr
		    (_params.control.dacs.channels[i].labelStr.getValue());
	    }
	}
	
	// add to panel
	
	for (int i = 0; i < nChannels; i++) {
	    if (_params.control.dacs.channels[i].active.getValue()) {
		add(_dacs[i]);
	    }
	}
    }
    
    // Listener for changes in dac parameters
    
    private class ParamsChangeListener implements CollectionChangeListener {
	public void reactToChange() {
	    _displayActive();
	}
    }
    
}
