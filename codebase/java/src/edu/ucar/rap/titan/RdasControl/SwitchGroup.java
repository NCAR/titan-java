///////////////////////////////////////////////////////////////////////
//
// SwitchGroup
//
// Mike Dixon
//
// Jan 2003
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

public class SwitchGroup extends JPanel implements BeamDataListener {
    
  public static final int nChannels = 12;
    
  private Parameters _params = Parameters.getInstance();
  private ControlModel _model = ControlModel.getInstance();
  private Switch _channels[] = new Switch[nChannels];
  private ParamsChangeListener _paramsChangeListener;
  private int _switchFlags = 0;
    
  public SwitchGroup(Component parent) {
	
    setBorder(CustomBorder.createTop(parent, "Switch channels", 10));
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	
    // create the switches

    for (int i = 0; i < nChannels; i++) {
      SwitchChannelParams channel =
        _params.control.switches.channels[i];
      _channels[i] = new Switch(i,
                                this,
                                channel.labelStr.getValue(), 
                                channel.onAtStartup.getValue(),
                                channel.enableSwitch.getValue(),
                                channel.active.getValue(),
                                channel.invertPolarity.getValue(),
                                channel.normalState.getValue(),
                                channel.warn.getValue());
    } // i

    _displayActive();
	
    // add listener for changes in params

    _paramsChangeListener = new ParamsChangeListener();
    _params.control.switches.addChangeListener(_paramsChangeListener);
    for (int i = 0; i < nChannels; i++) {
      _params.control.switches.channels[i].
        addChangeListener(_paramsChangeListener);
    }
	
    // add listener for new beam data

    BeamDataHandler.getInstance().addListener(this);
	
    // sync the switch states to the client

    _model.sendSwitchFlags();

  }
    
  // enable control GUI

  public void enableControl(boolean state) {
    for (int i = 0; i < nChannels; i++) {
      _channels[i].enableControl(state);
    }
  }
    
  // set the indicator for the specified switch
    
  public void setIndicatorOn(int switchNum, boolean state) {
    _channels[switchNum].setIndicatorOn(state);
  }

  // set the indicator for the specified switch
    
  public void redraw() {
    for (int i = 0; i < nChannels; i++) {
      _channels[i].redrawIndicator();
    }
  }

  // reset the switches to startup value
    
  public void resetToStartup() {

    for (int i = 0; i < nChannels; i++) {
      SwitchChannelParams channel =
        _params.control.switches.channels[i];
      if (channel.enableSwitch.getValue()) {
        _channels[i].setOn(channel.onAtStartup.getValue());
      }
    }
    _model.sendSwitchFlags();
	
  }

  // display active switches

  private void _displayActive() {
	
    // remove all from panel

    removeAll();
	
    // update labels
	
    for (int i = 0; i < nChannels; i++) {
      _channels[i].setLabelStr
        (_params.control.switches.channels[i].labelStr.getValue());
      _channels[i].setSwitchEnabled
        (_params.control.switches.channels[i].enableSwitch.getValue());
      _channels[i].setActive
        (_params.control.switches.channels[i].active.getValue());
      _channels[i].setInvertPolarity
        (_params.control.switches.channels[i].invertPolarity.getValue());
      _channels[i].setWarn
        (_params.control.switches.channels[i].warn.getValue());
      _channels[i].setNormalState
        (_params.control.switches.channels[i].normalState.getValue());
    }
	
    // set widths to be constant

    _setWidths();

    // add to panel

    for (int i = 0; i < nChannels; i++) {
      _channels[i].repaint();
      if (_params.control.switches.channels[i].active.getValue()) {
        add(_channels[i]);
      }
    }
  }
    
  // set the preferred label widths to the max of all active ones
    
  private void _setWidths() {

    // get the max width
	
    int maxLabelWidth = 0;
    int maxIndicatorWidth = 0;
    for (int i = 0; i < nChannels; i++) {
      if (_params.control.switches.channels[i].active.getValue()) {
        maxLabelWidth =
          Math.max(maxLabelWidth,
                   (int) _channels[i].getPreferredLabelWidth());
        maxIndicatorWidth =
          Math.max(maxIndicatorWidth,
                   (int) _channels[i].getPreferredIndicatorWidth());
      }
    }
	    
    // set all active to max width
	
    for (int i = 0; i < nChannels; i++) {
      if (_params.control.switches.channels[i].active.getValue()) {
        _channels[i].setPreferredLabelWidth(maxLabelWidth);
        _channels[i].setPreferredIndicatorWidth(maxIndicatorWidth);
      }
    }

	
  }

  // Listener for changes in switch parameters
    
  private class ParamsChangeListener implements CollectionChangeListener {
    public void reactToChange() {
      _displayActive();
    }
  }
    
  // handle incoming beam

  public void handleBeam(BeamMessage beam, double rate) {
    setStatus(beam.getStatusFlags());
  }
    
  // set the switch status
    
  public void setStatus(int flag_status2) {
    for (int i = 0; i < nChannels; i++) {
      if ((flag_status2 & (1 << i)) == 0) {
        setIndicatorOn(i, false);
      } else {
        setIndicatorOn(i, true);
      }
    }
  }

  // get control flags value

  public int getSwitchFlags() {
    return _switchFlags;
  }

  // get binary state of all channels, concatenated
  
  public String getStateBinary() {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < nChannels; i++) {
      buf.append(_channels[i].getStateBinary());
    }
    return buf.toString();
  }
    
  public String getStateText() {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < nChannels; i++) {
      buf.append(_channels[i].getStateText());
    }
    return buf.toString();
  }
    
}
