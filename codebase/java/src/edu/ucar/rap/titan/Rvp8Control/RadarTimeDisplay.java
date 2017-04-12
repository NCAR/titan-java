///////////////////////////////////////////////////////////////////////
//
// RadarTimeDisplay
//
// Mike Dixon
//
// Oct 2004
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.Rvp8Control;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.lang.Math;
import edu.ucar.rap.jrp.*;

public class RadarTimeDisplay extends JPanel implements StatusDataListener

{
  
  private Parameters _params = Parameters.getInstance();
  private JLabel _dateValue;
  private JLabel _timeValue;
  private StatusData _status;
  
  public RadarTimeDisplay(JFrame frame) {
    
    setBorder(CustomBorder.createTop(frame, "Radar time", 1));
    
    Font font =
      getFont().deriveFont(Font.BOLD,
                           _params.status.fontSize.getValue());
    // date panel
    
    JLabel dateLabel = new JLabel("Date", JLabel.CENTER);
    
    _dateValue = new JLabel("0000/00/00", JLabel.CENTER);
    _dateValue.setFont(font);
    _dateValue.setForeground(Color.yellow);
    
    JPanel dateValuePanel = new JPanel();
    dateValuePanel.setBackground(Color.black);
    dateValuePanel.add(_dateValue);
    
    BorderLayout dateLayout = new BorderLayout();
    dateLayout.setHgap(3);
    JPanel datePanel = new JPanel(dateLayout);
    datePanel.add(dateLabel, BorderLayout.WEST);
    datePanel.add(dateValuePanel, BorderLayout.EAST);
    
    // time panel
    
    JLabel timeLabel = new JLabel("Time", JLabel.CENTER);
    
    _timeValue = new JLabel("00:00:00", JLabel.CENTER);
    _timeValue.setFont(font);
    _timeValue.setForeground(Color.yellow);
    
    JPanel timeValuePanel = new JPanel();
    timeValuePanel.setBackground(Color.black);
    timeValuePanel.add(_timeValue);
    
    BorderLayout timeLayout = new BorderLayout();
    timeLayout.setHgap(3);
    JPanel timePanel = new JPanel(timeLayout);
    timePanel.add(timeLabel, BorderLayout.EAST);
    timePanel.add(timeValuePanel, BorderLayout.WEST);
    
    // create time panel
    
    JPanel dateTimePanel = new JPanel();
    BorderLayout layout = new BorderLayout();
    layout.setHgap(2);
    dateTimePanel.setLayout(layout);
    dateTimePanel.add(datePanel, BorderLayout.WEST);
    dateTimePanel.add(timePanel, BorderLayout.EAST);
    
    // add
    
    add(dateTimePanel);
    
    // add listener for new status data
    
    StatusDataHandler.getInstance().addListener(this);
    
  }
  
  // handle new beam
  
  public void handleStatus(StatusData status) {
    _status = status;
  }
    
  // update the display
  
  public void redraw() {

    if (_status == null) {
      return;
    }
    
    Font font =
      getFont().deriveFont(Font.BOLD,
                           _params.status.fontSize.getValue());
    
    _dateValue.setFont(font);
    _timeValue.setFont(font);
    
    String dateString = new
      String(NFormat.i4.format(_status.year) +
             "/" +
             NFormat.i2.format(_status.month) +
             "/" + 
             NFormat.i2.format(_status.day));
    
    String timeString = new
      String(NFormat.i2.format(_status.hour) +
             ":" +
             NFormat.i2.format(_status.min) +
             ":" + 
             NFormat.i2.format(_status.sec));

    _dateValue.setText(dateString);
    _timeValue.setText(timeString);
    
  }
  
}

