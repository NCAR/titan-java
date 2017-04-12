///////////////////////////////////////////////////////////////////////
//
// DateTimeDisplay
//
// Mike Dixon
//
// May 2013
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RapidDowControl;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.lang.Math;
import edu.ucar.rap.jrp.*;

public class DateTimeDisplay extends JPanel implements StatusDataListener
                                                       
{
  
  private Parameters _params = Parameters.getInstance();
  private JLabel _dateLabel;
  private JLabel _timeLabel;
  private JLabel _dateValue;
  private JLabel _timeValue;
  private StatusData _status;
  private int _role;
  
  public static int GPS = 0;
  public static int LATEST_1 = 1;
  public static int LATEST_2 = 2;
  public static int START_1 = 3;
  public static int START_2 = 4;

  public DateTimeDisplay(JFrame frame,
                         int role) {

    _role = role;

    if (_role == GPS) {
      setBorder(CustomBorder.createTop(frame, "GPS time", 1));
    } else if (_role == LATEST_1) {
      setBorder(CustomBorder.createTop(frame, "Latest time 1", 1));
    } else if (_role == LATEST_2) {
      setBorder(CustomBorder.createTop(frame, "Latest time 2", 1));
    } else if (_role == START_1) {
      setBorder(CustomBorder.createTop(frame, "Start time 1", 1));
    } else if (_role == START_2) {
      setBorder(CustomBorder.createTop(frame, "Start time 2", 1));
    }
    
    // date panel
    
    _dateLabel = new JLabel("Date", JLabel.CENTER);
    
    _dateValue = new JLabel("0000/00/00", JLabel.CENTER);
    _dateValue.setForeground(Color.yellow);
    
    JPanel dateValuePanel = new JPanel();
    dateValuePanel.setBackground(Color.black);
    dateValuePanel.add(_dateValue);
    
    BorderLayout dateLayout = new BorderLayout();
    dateLayout.setHgap(3);
    JPanel datePanel = new JPanel(dateLayout);
    datePanel.add(_dateLabel, BorderLayout.WEST);
    datePanel.add(dateValuePanel, BorderLayout.EAST);
    
    // time panel
    
    _timeLabel = new JLabel("Time", JLabel.CENTER);
    
    _timeValue = new JLabel("00:00:00", JLabel.CENTER);
    _timeValue.setForeground(Color.yellow);
    
    JPanel timeValuePanel = new JPanel();
    timeValuePanel.setBackground(Color.black);
    timeValuePanel.add(_timeValue);
    
    BorderLayout timeLayout = new BorderLayout();
    timeLayout.setHgap(3);
    JPanel timePanel = new JPanel(timeLayout);
    timePanel.add(_timeLabel, BorderLayout.EAST);
    timePanel.add(timeValuePanel, BorderLayout.WEST);

    setFont();
    
    // create time panel
    
    JPanel dateTimePanel = new JPanel();
    BorderLayout layout = new BorderLayout();
    layout.setHgap(2);
    dateTimePanel.setLayout(layout);
    dateTimePanel.add(datePanel, BorderLayout.WEST);
    dateTimePanel.add(timePanel, BorderLayout.EAST);
    
    // add
    
    add(dateTimePanel);
    
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
    
    StatusData.DateTime dtime = _status.gps_time;
    if (_role == GPS) {
      dtime = _status.gps_time;
    } else if (_role == LATEST_1) {
      dtime = _status.pentek1.latest_time;
    } else if (_role == LATEST_2) {
      dtime = _status.pentek2.latest_time;
    } else if (_role == START_1) {
      dtime = _status.pentek1.start_time;
    } else if (_role == START_2) {
      dtime = _status.pentek2.start_time;
    }

    String dateString = new
      String(NFormat.i4.format(dtime.year) +
             "/" +
             NFormat.i2.format(dtime.month) +
             "/" + 
             NFormat.i2.format(dtime.day));
    
    String timeString = new
      String(NFormat.i2.format(dtime.hour) +
             ":" +
             NFormat.i2.format(dtime.min) +
             ":" + 
             NFormat.i2.format(dtime.sec));

    _dateValue.setText(dateString);
    _timeValue.setText(timeString);
    
  }
  
  // set the font
    
  public void setFont() {

    Font font =
      getFont().deriveFont(Font.BOLD,
                           _params.status.fontSize.getValue());

    _dateLabel.setFont(font);
    _timeLabel.setFont(font);
    _dateValue.setFont(font);
    _timeValue.setFont(font);
    
  }
    
  /////////////////////////////////////
  // Listener for changes in parameters
  
  private class ParamsChangeListener implements CollectionChangeListener {
    public void reactToChange() {
      setFont();
    }
  }
  
}

