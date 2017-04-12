///////////////////////////////////////////////////////////////////////
//
// ErrorFlagsGroup
//
// Mike Dixon
//
// Sept 2007
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

public class ErrorFlagsGroup extends JPanel implements BeamDataListener {
  
  public static final int nChannels = 8;
  
  private Parameters _params = Parameters.getInstance();
  private ErrorFlags _channels[] = new ErrorFlags[nChannels];
  private ParamsChangeListener _paramsChangeListener;
    
  public ErrorFlagsGroup(Component parent) {
	
    setBorder(CustomBorder.createTop(parent, "Error flags", 10));
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	
    // create the channels

    for (int i = 0; i < nChannels; i++) {
      ErrorChannelParams chanParams =
        _params.control.errorFlags.channels[i];
      _channels[i] = new ErrorFlags(i,
                                    this,
                                    chanParams.labelStr.getValue(), 
                                    chanParams.active.getValue(),
                                    chanParams.normalState.getValue(),
                                    chanParams.warn.getValue());
    } // i
    
    _displayActive();
    
    // add listener for changes in params

    _paramsChangeListener = new ParamsChangeListener();
    _params.control.errorFlags.addChangeListener(_paramsChangeListener);
    for (int i = 0; i < nChannels; i++) {
      _params.control.errorFlags.channels[i].
        addChangeListener(_paramsChangeListener);
    }
	
    // add listener for new beam data

    BeamDataHandler.getInstance().addListener(this);
	
  }
    
  // set the indicator for the specified errorFlags
    
  public void setIndicatorOn(int errorFlagsNum, boolean state) {
    _channels[errorFlagsNum].setIndicatorOn(state);
  }

  // set the indicator for the specified errorFlags
    
  public void redraw() {
    for (int i = 0; i < nChannels; i++) {
      _channels[i].redrawIndicator();
    }
  }

  // display active channels

  private void _displayActive() {
	
    // remove all from panel

    removeAll();
	
    // update labels
    
    for (int i = 0; i < nChannels; i++) {
      if (_params.control.errorFlags.channels[i].active.getValue()) {
        _channels[i].setLabelStr
          (_params.control.errorFlags.channels[i].labelStr.getValue());
        _channels[i].setActive
          (_params.control.errorFlags.channels[i].active.getValue());
        _channels[i].setWarn
          (_params.control.errorFlags.channels[i].warn.getValue());
        _channels[i].setNormalState
          (_params.control.errorFlags.channels[i].normalState.getValue());
      }
    }
    
    // set widths to be constant
    
    _setWidths();

    // add to panel

    for (int i = 0; i < nChannels; i++) {
      _channels[i].repaint();
      if (_params.control.errorFlags.channels[i].active.getValue()) {
        add(_channels[i]);
      }
    }

    redraw();

  }
    
  // set the preferred label widths to the max of all active ones
    
  private void _setWidths() {

    // get the max width
	
    int maxLabelWidth = 0;
    int maxIndicatorWidth = 0;
    for (int i = 0; i < nChannels; i++) {
      if (_params.control.errorFlags.channels[i].active.getValue()) {
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
      if (_params.control.errorFlags.channels[i].active.getValue()) {
        _channels[i].setPreferredLabelWidth(maxLabelWidth);
        _channels[i].setPreferredIndicatorWidth(maxIndicatorWidth);
      }
    }

  }

  // Listener for changes in errorFlags parameters
    
  private class ParamsChangeListener implements CollectionChangeListener {
    public void reactToChange() {
      _displayActive();
    }
  }
    
  // handle incoming beam

  public void handleBeam(BeamMessage beam, double rate) {
    setStatus(beam.getErrorFlags());
  }
    
  // set the errorFlags status
  
  public void setStatus(int errorFlags) {
    for (int i = 0; i < nChannels; i++) {
      if ((errorFlags & (1 << i)) == 0) {
        setIndicatorOn(i, false);
      } else {
        setIndicatorOn(i, true);
      }
    }
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
