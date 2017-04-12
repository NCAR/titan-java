///////////////////////////////////////////////////////////////////////
//
// PowerStatusGroup
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

public class PowerStatusGroup extends JPanel implements BeamDataListener {
  
  public static final int nChannels = 4;
  
  private Parameters _params = Parameters.getInstance();
  private PowerChannel _channels[] = new PowerChannel[nChannels];
  private ParamsChangeListener _paramsChangeListener;
  
  public PowerStatusGroup(Component parent) {
    
    setBorder(CustomBorder.createTop(parent, "Power Status", 5));
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    
    // create the channels
    
    for (int i = 0; i < nChannels; i++) {
      PowerChannelParams channel = _params.control.powerStatus.channels[i];
      _channels[i] = new PowerChannel(i,
                                      this,
                                      channel.labelStr.getValue(), 
                                      channel.invertPolarity.getValue(),
                                      channel.active.getValue(),
                                      channel.warn.getValue());
    } // i

    _displayActive();
	
    // add listener for changes in params

    _paramsChangeListener = new ParamsChangeListener();
    _params.control.powerStatus.addChangeListener(_paramsChangeListener);
    for (int i = 0; i < nChannels; i++) {
      _params.control.powerStatus.channels[i].
        addChangeListener(_paramsChangeListener);
    }
	
    // add listener for new beam data

    BeamDataHandler.getInstance().addListener(this);
	
  }
    
  // set the indicator for the specified powerStatus
    
//   public void setIndicatorOn(int powerStatusNum, boolean state) {
//     _channels[powerStatusNum].setIndicatorOn(state);
//   }

  // set the indicator for the specified powerStatus
    
//   public void redraw() {
//     for (int i = 0; i < nChannels; i++) {
//       _channels[i].redrawIndicator();
//     }
//   }

  // display active channels

  private void _displayActive() {
	
    // remove all from panel

    removeAll();
	
    // update labels
	
    for (int i = 0; i < nChannels; i++) {
      if (_params.control.powerStatus.channels[i].active.getValue()) {
        _channels[i].setLabelStr
          (_params.control.powerStatus.channels[i].labelStr.getValue());
        _channels[i].setInvert
          (_params.control.powerStatus.channels[i].invertPolarity.getValue());
        _channels[i].setActive
          (_params.control.powerStatus.channels[i].active.getValue());
        _channels[i].setWarn
          (_params.control.powerStatus.channels[i].warn.getValue());
      }
    }
	
    // set widths to be constant

    _setWidths();

    // add to panel

    for (int i = 0; i < nChannels; i++) {
      _channels[i].repaint();
      if (_params.control.powerStatus.channels[i].active.getValue()) {
        add(_channels[i]);
      }
    }

  }
    
  // set the preferred label widths to the max of all active ones
    
  private void _setWidths() {

    // get the max width
	
    int maxLabelWidth = 0;
    for (int i = 0; i < nChannels; i++) {
      if (_params.control.powerStatus.channels[i].active.getValue()) {
        maxLabelWidth =
          Math.max(maxLabelWidth,
                   (int) _channels[i].getPreferredLabelWidth());
      }
    }
	    
    // set all active to max width
	
    for (int i = 0; i < nChannels; i++) {
      if (_params.control.powerStatus.channels[i].active.getValue()) {
        _channels[i].setPreferredLabelWidth(maxLabelWidth);
      }
    }

  }

  // Listener for changes in powerStatus parameters
    
  private class ParamsChangeListener implements CollectionChangeListener {
    public void reactToChange() {
      _displayActive();
    }
  }
    
  // handle incoming beam

  public void handleBeam(BeamMessage beam, double rate) {
    setStatus(beam.getPowerStatus());
  }
    
  // set the power status
  
  public void setStatus(int powerStatus) {
    for (int i = 0; i < nChannels; i++) {
      if ((powerStatus & (1 << i)) == 0) {
        _channels[i].setState(false);
      } else {
        _channels[i].setState(true);
      }
    }
  }

  public void setStatus(int channelNum, int powerStatus) {
    if ((powerStatus & (1 << channelNum)) == 0) {
      _channels[channelNum].setState(false);
    } else {
      _channels[channelNum].setState(true);
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
