///////////////////////////////////////////////////////////////////////
//
// AnalogGroup
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

public class AnalogGroup extends JPanel implements BeamDataListener {
    
  public static final int nChannels = 8;
    
  private Parameters _params = Parameters.getInstance();
  private Analog _channels[] = new Analog[nChannels];
  private ChangeListener _changeListener;
    
  public AnalogGroup(Component parent) {
	
    setBorder(CustomBorder.createTop(parent, "Analog channels", 5));
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	
    // create the analogs
	
    for (int i = 0; i < nChannels; i++) {
      _channels[i] = new Analog
        (this, i,
         _params.control.analogs.channels[i].labelStr.getValue(), 
         _params.control.analogs.channels[i].unitsStr.getValue(), 
         _params.control.analogs.channels[i].offset.getValue(), 
         _params.control.analogs.channels[i].slope.getValue(), 
         _params.control.analogs.channels[i].warn.getValue(), 
         _params.control.analogs.channels[i].minimum.getValue(), 
         _params.control.analogs.channels[i].maximum.getValue(),
         _params.control.analogs.channels[i].active.getValue());
    } // i

    _displayActive();
	
    // add listener for changes in params
	
    _changeListener = new ChangeListener();
    _params.control.switches.addChangeListener(_changeListener);
    for (int i = 0; i < nChannels; i++) {
      _params.control.analogs.channels[i].addChangeListener(_changeListener);
    }
	
    // add listener for new beam data

    BeamDataHandler.getInstance().addListener(this);
	
  }
    
  // set the analog values

  public void setStatus(float[] analogStatus) {
    for (int i = 0; i < nChannels; i++) {
      setValue(i, analogStatus[i]);
    }
  }
    
  public void setValue(int channel, double voltage) {
    if (_params.control.analogs.channels[channel].active.getValue()) {
      _channels[channel].setValue(voltage);
    }
  }
    
  public void redraw() {
    for (int ii = 0; ii < nChannels; ii++) {
      _channels[ii].redraw();
    }
  }
    
  // display active analogs
    
  private void _displayActive() {
	
    // remove all from panel

    removeAll();
	
    // update from params
	
    for (int i = 0; i < nChannels; i++) {
      if (_params.control.analogs.channels[i].active.getValue()) {
        _channels[i].setLabelStr
          (_params.control.analogs.channels[i].labelStr.getValue());
        _channels[i].setUnitsStr
          (_params.control.analogs.channels[i].unitsStr.getValue());
        _channels[i].setOffset
          (_params.control.analogs.channels[i].offset.getValue());
        _channels[i].setSlope
          (_params.control.analogs.channels[i].slope.getValue());
        _channels[i].setWarn
          (_params.control.analogs.channels[i].warn.getValue());
        _channels[i].setMinimum
          (_params.control.analogs.channels[i].minimum.getValue());
        _channels[i].setMaximum
          (_params.control.analogs.channels[i].maximum.getValue());
      }
    }

    // set widths to be constant

    _setWidths();

    // add to panel
	
    for (int i = 0; i < nChannels; i++) {
      if (_params.control.analogs.channels[i].active.getValue()) {
        add(_channels[i]);
      }
    }
  }
    
  // set the preferred label widths to the max of all active ones
    
  private void _setWidths() {

    // set all labels to same width
	
    int maxLabelWidth = 0;
    for (int i = 0; i < nChannels; i++) {
      if (_params.control.analogs.channels[i].active.getValue()) {
        maxLabelWidth =
          Math.max(maxLabelWidth,
                   (int) _channels[i].getPreferredLabelWidth());
      }
    }
    for (int i = 0; i < nChannels; i++) {
      if (_params.control.analogs.channels[i].active.getValue()) {
        _channels[i].setPreferredLabelWidth(maxLabelWidth);
      }
    }
	
    int maxUnitsWidth = 0;
    for (int i = 0; i < nChannels; i++) {
      if (_params.control.analogs.channels[i].active.getValue()) {
        maxUnitsWidth =
          Math.max(maxUnitsWidth,
                   (int) _channels[i].getPreferredUnitsWidth());
      }
    }
    for (int i = 0; i < nChannels; i++) {
      if (_params.control.analogs.channels[i].active.getValue()) {
        _channels[i].setPreferredUnitsWidth(maxUnitsWidth);
      }
    }
	
  }
    
  // Listener for changes in analog parameters
    
  private class ChangeListener implements CollectionChangeListener {
    public void reactToChange() {
      _displayActive();
    }
  }
    
  // handle incoming beam
    
  public void handleBeam(BeamMessage beam, double rate) {
    setStatus(beam.getAnalogStatus());
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
