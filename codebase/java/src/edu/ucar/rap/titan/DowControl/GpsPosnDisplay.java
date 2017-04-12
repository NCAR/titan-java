///////////////////////////////////////////////////////////////////////
//
// GpsPosnDisplay
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

public class GpsPosnDisplay extends JPanel implements StatusDataListener {
  
  private Parameters _params = Parameters.getInstance();

  private JLabel _latValue;
  private JLabel _lonValue;
  private JLabel _altValue;

  JLabel _latLabel;
  JLabel _lonLabel;
  JLabel _altLabel;

  private StatusData _status;
  
  public GpsPosnDisplay(JFrame frame) {
    
    setBorder(CustomBorder.createTop(frame, "Gps position", 1));
    
    Font font =
      getFont().deriveFont(Font.BOLD,
                           _params.status.fontSize.getValue());

    // elevation panel
    
    _latLabel = new JLabel("LAT", JLabel.CENTER);
    
    _latValue = new JLabel("00.0000", JLabel.CENTER);
    _latValue.setFont(font);
    _latValue.setForeground(Color.yellow);
    
    JPanel latValuePanel = new JPanel();
    latValuePanel.setBackground(Color.black);
    latValuePanel.add(_latValue);
	
    BorderLayout latLayout = new BorderLayout();
    latLayout.setHgap(2);
    JPanel latPanel = new JPanel(latLayout);
    latPanel.add(_latLabel, BorderLayout.WEST);
    latPanel.add(latValuePanel, BorderLayout.EAST);
	
    // lonimuth panel
	
    _lonLabel = new JLabel("LON", JLabel.CENTER);
	
    _lonValue = new JLabel("000.0000", JLabel.CENTER);
    _lonValue.setFont(font);
    _lonValue.setForeground(Color.yellow);
	
    JPanel lonValuePanel = new JPanel();
    lonValuePanel.setBackground(Color.black);
    lonValuePanel.add(_lonValue);
    
    BorderLayout lonLayout = new BorderLayout();
    lonLayout.setHgap(2);
    JPanel lonPanel = new JPanel(lonLayout);
    lonPanel.add(_lonLabel, BorderLayout.WEST);
    lonPanel.add(lonValuePanel, BorderLayout.EAST);
    
    // rate panel
	
    _altLabel = new JLabel("ALT", JLabel.CENTER);
	
    _altValue = new JLabel("0000", JLabel.CENTER);
    _altValue.setFont(font);
    _altValue.setForeground(Color.yellow);
    
    JPanel altValuePanel = new JPanel();
    altValuePanel.setBackground(Color.black);
    altValuePanel.add(_altValue);
	
    BorderLayout altLayout = new BorderLayout();
    altLayout.setHgap(2);
    JPanel altPanel = new JPanel(altLayout);
    altPanel.add(_altLabel, BorderLayout.WEST);
    altPanel.add(altValuePanel, BorderLayout.EAST);
	
    // create position panel
	
    JPanel posPanel = new JPanel();
    BorderLayout layout = new BorderLayout();
    layout.setHgap(2);
    posPanel.setLayout(layout);
    posPanel.add(latPanel, BorderLayout.WEST);
    posPanel.add(lonPanel, BorderLayout.CENTER);
    posPanel.add(altPanel, BorderLayout.EAST);
	
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
    
    _latValue.setText(NFormat.f4.format(_status.latitude_deg));
    _lonValue.setText(NFormat.f4.format(_status.longitude_deg));
    _altValue.setText(NFormat.f0.format(_status.altitude_m));
	
  }
    
  // set the font
    
  public void setFont() {

    Font font =
      getFont().deriveFont(Font.BOLD,
                           _params.status.fontSize.getValue());

    _latValue.setFont(font);
    _lonValue.setFont(font);
    _altValue.setFont(font);
    
    _latLabel.setFont(font);
    _lonLabel.setFont(font);
    _altLabel.setFont(font);
    
  }
    
  /////////////////////////////////////
  // Listener for changes in parameters
  
  private class ParamsChangeListener implements CollectionChangeListener {
    public void reactToChange() {
      setFont();
    }
  }
  
}
