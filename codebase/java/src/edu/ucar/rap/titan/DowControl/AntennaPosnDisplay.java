///////////////////////////////////////////////////////////////////////
//
// AntennaPosnDisplay
//
// Mike Dixon
//
// May 2013
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.DowControl;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.lang.Math;
import edu.ucar.rap.jrp.*;

public class AntennaPosnDisplay extends JPanel implements StatusDataListener {

  private Parameters _params = Parameters.getInstance();

  private JLabel _elValue;
  private JLabel _azValue;
  private JLabel _azRateValue;

  JLabel _elLabel;
  JLabel _azLabel;
  JLabel _azRateLabel;

  private StatusData _status;
  
  public AntennaPosnDisplay(JFrame frame) {
    
    setBorder(CustomBorder.createTop(frame, "Antenna position", 1));
	
    // elevation panel
	
    _elLabel = new JLabel("EL", JLabel.CENTER);
    _elValue = new JLabel("00.00", JLabel.CENTER);
    _elValue.setForeground(Color.yellow);
    
    JPanel elValuePanel = new JPanel();
    elValuePanel.setBackground(Color.black);
    elValuePanel.add(_elValue);
	
    BorderLayout elLayout = new BorderLayout();
    elLayout.setHgap(2);
    JPanel elPanel = new JPanel(elLayout);
    elPanel.add(_elLabel, BorderLayout.WEST);
    elPanel.add(elValuePanel, BorderLayout.EAST);
	
    // azimuth panel
	
    _azLabel = new JLabel("AZ", JLabel.CENTER);
    _azValue = new JLabel("000.00", JLabel.CENTER);
    _azValue.setForeground(Color.yellow);
    
    JPanel azValuePanel = new JPanel();
    azValuePanel.setBackground(Color.black);
    azValuePanel.add(_azValue);
	
    BorderLayout azLayout = new BorderLayout();
    azLayout.setHgap(2);
    JPanel azPanel = new JPanel(azLayout);
    azPanel.add(_azLabel, BorderLayout.WEST);
    azPanel.add(azValuePanel, BorderLayout.EAST);
	
    // rate panel
    
    _azRateLabel = new JLabel("RATE", JLabel.CENTER);
    _azRateValue = new JLabel("000.00", JLabel.CENTER);
    _azRateValue.setForeground(Color.yellow);
    
    JPanel azRateValuePanel = new JPanel();
    azRateValuePanel.setBackground(Color.black);
    azRateValuePanel.add(_azRateValue);
	
    BorderLayout azRateLayout = new BorderLayout();
    azRateLayout.setHgap(2);
    JPanel azRatePanel = new JPanel(azRateLayout);
    azRatePanel.add(_azRateLabel, BorderLayout.WEST);
    azRatePanel.add(azRateValuePanel, BorderLayout.EAST);
	
    // create position panel
	
    JPanel posPanel = new JPanel();
    BorderLayout layout = new BorderLayout();
    layout.setHgap(2);
    posPanel.setLayout(layout);
    posPanel.add(elPanel, BorderLayout.WEST);
    posPanel.add(azPanel, BorderLayout.CENTER);
    posPanel.add(azRatePanel, BorderLayout.EAST);

    // set the font

    setFont();
	
    // add
	
    add(posPanel);
	
    // add params change listener
    
    _params.control.addChangeListener(new ParamsChangeListener());
    
    // add listener for new status data
	
    StatusDataHandler.getInstance().addListener(this);
	
  }
	
  // handle new status
    
  public void handleStatus(StatusData status) {

    _status = status;
	
  }
    
  // update the display
    
  public void redraw() {

    if (_status == null) {
      return;
    }
  
    setFont();

    _elValue.setText(NFormat.el2.format(_status.elevation_deg));
    _azValue.setText(NFormat.az2.format(_status.azimuth_deg));
    if (_status.scan_rate_deg_per_sec >= 0) {
      _azRateValue.setText(NFormat.f2_3.format(_status.scan_rate_deg_per_sec));
    } else {
      _azRateValue.setText(NFormat.f2_2.format(_status.scan_rate_deg_per_sec));
    }
    
  }
    
  // set the font
    
  public void setFont() {

    Font font =
      getFont().deriveFont(Font.BOLD,
                           _params.status.fontSize.getValue());

    _elValue.setFont(font);
    _azValue.setFont(font);
    _azRateValue.setFont(font);
    
    _elLabel.setFont(font);
    _azLabel.setFont(font);
    _azRateLabel.setFont(font);

  }
    
  /////////////////////////////////////
  // Listener for changes in parameters
  
  private class ParamsChangeListener implements CollectionChangeListener {
    public void reactToChange() {
      setFont();
    }
  }
  
}
