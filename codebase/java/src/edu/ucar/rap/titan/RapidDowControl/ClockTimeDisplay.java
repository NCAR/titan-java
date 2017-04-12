///////////////////////////////////////////////////////////////////////
//
// ClockTimeDisplay
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

public class ClockTimeDisplay extends JPanel {

  private Parameters _params = Parameters.getInstance();
  private JLabel _dateValue;
  private JLabel _timeValue;
    
  public ClockTimeDisplay(JFrame frame) {
	
    setBorder(CustomBorder.createTop(frame, "Clock time", 3));
	
    Font font =
      getFont().deriveFont(Font.BOLD,
                           _params.status.fontSize.getValue());
	
    // date panel
	
    JLabel dateLabel = new JLabel("Date", JLabel.CENTER);
	
    _dateValue = new JLabel("0000/00/00", JLabel.CENTER);
    _dateValue.setFont(font);
    _dateValue.setForeground(Color.green);
	
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
    _timeValue.setForeground(Color.green);
	
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

    // create the ticker thread, start it

    Ticker ticker = new Ticker();
    ticker.start();
	
  }
	
  private void _update() {

    Calendar cal = Calendar.getInstance();
    cal.setTime(TimeManager.getDate());

    String dateStr = new
      String(NFormat.i4.format(cal.get(Calendar.YEAR)) +
             "/" +
             NFormat.i2.format(cal.get(Calendar.MONTH) + 1) +
             "/" + 
             NFormat.i2.format(cal.get(Calendar.DAY_OF_MONTH)));

    String timeStr = new
      String(NFormat.i2.format(cal.get(Calendar.HOUR_OF_DAY)) +
             ":" +
             NFormat.i2.format(cal.get(Calendar.MINUTE)) +
             ":" + 
             NFormat.i2.format(cal.get(Calendar.SECOND)));

    Font font =
      getFont().deriveFont(Font.BOLD,
                           _params.status.fontSize.getValue());
	
    _dateValue.setFont(font);
    _timeValue.setFont(font);
	
    _dateValue.setText(dateStr);
    _timeValue.setText(timeStr);
	
  }
    
  // ticker thread
    
  private class Ticker extends Thread {
    public void run() {
      while (true) {
        // update clock
        _update();
        // sleep a bit
        Thread t = Thread.currentThread();
        try { t.sleep(500); }
        catch (InterruptedException e) { return; }
      }
    }
  }

}
