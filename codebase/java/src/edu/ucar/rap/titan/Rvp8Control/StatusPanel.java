///////////////////////////////////////////////////////////////////////
//
// StatusPanel
//
// Mike Dixon
//
// April 2007
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

public class StatusPanel extends JFrame implements StatusDataListener

{

  private Parameters _params = Parameters.getInstance();

  private TransmitStatusDisplay _transmitStatusDisplay;
  private ReceiveStatusDisplay _receiveStatusDisplay;
  private MomentsStatusDisplay _momentsStatusDisplay;
  private ClockTimeDisplay _clockTimeDisplay;
  private RadarTimeDisplay _radarTimeDisplay;
  private AntennaPosnDisplay _antennaPosnDisplay;
  private SunPosnDisplay _sunPosnDisplay;
  
  private StatusData _status;
   
  private JMenuBar _menuBar;
  private JButton _editButton;
  private JButton _helpButton;
  private String _infoContent;

  /**
   * Single instance for this class - it is a singleton
   */
    
  private static final StatusPanel _instance = new StatusPanel();
    
  /**
   * get singleton instance
   */
    
  public static StatusPanel getInstance() {
    return _instance;
  }
    
  private StatusPanel() {
	
    // titles

    _setTitleStr();
    setSize(_params.status.width.getValue(),

            _params.status.height.getValue());
    setLocation(_params.status.xx.getValue(),
                _params.status.yy.getValue());
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    
    // add listener for window property changes
	
    addComponentListener(new WindowPropertiesListener());
	
    // add listener for param changes
	
    _params.status.addChangeListener(new StatusParamsChangeListener());
    _updateFromParams();
    
    // add listener for new beam data

    StatusDataHandler.getInstance().addListener(this);
    
    // transmit, receive and moments
	
    _transmitStatusDisplay = new TransmitStatusDisplay(this);
    _receiveStatusDisplay = new ReceiveStatusDisplay(this);
    _momentsStatusDisplay = new MomentsStatusDisplay(this);

    // clocks, antenna position, sun position

    _clockTimeDisplay = new ClockTimeDisplay(this);
    _radarTimeDisplay = new RadarTimeDisplay(this);
    _antennaPosnDisplay = new AntennaPosnDisplay(this);
    _sunPosnDisplay = new SunPosnDisplay(this);
	
    // top panel
	
    JPanel topPanel = new JPanel(new BorderLayout());
    getContentPane().add(topPanel);
    
    // transmit and moments

    JPanel xmitMomentsReceivePanel = new JPanel(new BorderLayout());
    xmitMomentsReceivePanel.add(_transmitStatusDisplay, BorderLayout.NORTH);
    xmitMomentsReceivePanel.add(_momentsStatusDisplay, BorderLayout.SOUTH);

    // upper panel

    JPanel upperPanel = new JPanel(new BorderLayout());
    upperPanel.add(xmitMomentsReceivePanel, BorderLayout.WEST);
    upperPanel.add(_receiveStatusDisplay, BorderLayout.EAST);
    
    // radar time, clock time, antenna posn and sun posn

    JPanel radarTimeAntenna = new JPanel(new BorderLayout());
    radarTimeAntenna.add(_antennaPosnDisplay, BorderLayout.NORTH);
    radarTimeAntenna.add(_radarTimeDisplay, BorderLayout.SOUTH);
    
    JPanel sunClock = new JPanel(new BorderLayout());
    sunClock.add(_clockTimeDisplay, BorderLayout.NORTH);
    sunClock.add(_sunPosnDisplay, BorderLayout.SOUTH);

    JPanel radarSun = new JPanel(new BorderLayout());
    radarSun.add(radarTimeAntenna, BorderLayout.NORTH);
    radarSun.add(sunClock, BorderLayout.SOUTH);
    
    // lower panel in flow layout so that it will be centered

    JPanel lowerPanel = new JPanel(new FlowLayout());
    lowerPanel.add(radarSun);
    
    // add left and right parts
    
    topPanel.add(upperPanel, BorderLayout.NORTH);
    topPanel.add(lowerPanel, BorderLayout.SOUTH);

    // menu bar
	
    _menuBar = new JMenuBar();
    setJMenuBar(_menuBar);

    // edit button
    
    _editButton = new JButton(_params.status.getViewEditAction());
    _menuBar.add(_editButton);
	
    // help button
	
    _helpButton = new JButton("Help");
    _menuBar.add(_helpButton);
    _infoContent =
      "<h1>StatusPanel help info</h1>\n" +
      "<hr>\n" +
      "<h2>Interpretation</h2>\n" +
      "<ul>\n" +
      "<li>The status panel updates at regular intervals.\n" +
      "<li>Status is obtained from the Rvp8Driver server.\n" +
      "</ul>\n" +
      "<hr>\n" +
      "<h2>Configuration</h2>\n" +
      "<h3>Changing font size and status data interval:</h3>\n" +
      "<ul>\n" +
      "<li>Use Edit button to bring up parameters panel.\n" +
      "<li>Use info buttons if help is needed.\n" +
      "<li>Set parameters as appropriate.\n" +
      "</ul>\n" +
      "<h3>Setting the window size to accommodate the various panels:</h3>\n" +
      "<li>Use Edit button to bring up parameters panel.\n" +
      "<li>Select 'allow resize', hit apply.\n" +
      "<li>Resize window to suit.\n" +
      "<li>Turn off 'Allow resize' to fix window size.\n" +
      "</ul>\n";
    _helpButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          InfoFrame info =
            new InfoFrame(getX() + 30, getY() + 30,
                          600, 500);
          info.setTitle("Info for Status Panel");
          info.setContent(_infoContent);
        }
      });
	
    // create the ticker thread, start it
	
    Ticker ticker = new Ticker();
    ticker.start();
	
  }

  // set title
    
  private void _setTitleStr() {
    String frameTitle =
      "STATUS PANEL - " + _params.site.siteName.getValue();
    setTitle(frameTitle);
  }

  // update from parameters

  private void _updateFromParams() {
    if (_params.status.allowResize.getValue()) {
      setResizable(true);
    } else {
      setResizable(false);
    }
  }

  // handle incoming beam

  public void handleStatus(StatusData status) {
    _status = status;
  }
    
  // redraw beam-related items

  private void _redraw() {

    if (_status == null) {
      return;
    }

    _transmitStatusDisplay.redraw();
    _receiveStatusDisplay.redraw();
    _momentsStatusDisplay.redraw();
    _sunPosnDisplay.redraw();
    _antennaPosnDisplay.redraw();
    _radarTimeDisplay.redraw();
	
  }
    
  // ticker thread
    
  private class Ticker extends Thread {
    public void run() {
      while (true) {
        // redraw
        _redraw();
        // sleep a bit
        Thread t = Thread.currentThread();
        try { t.sleep(500); }
        catch (InterruptedException e) { return; }
      }
    }
  }

  // listener for window properties

  private class WindowPropertiesListener extends ComponentAdapter {
	
    public void componentMoved(ComponentEvent e) {
      Parameters params = Parameters.getInstance();
      int paramX = params.status.xx.getValue();
      int paramY = params.status.yy.getValue();
      if (paramX != getX() || paramY != getY()) {
        params.status.xx.setValue(getX());
        params.status.yy.setValue(getY());
      }
    }
	
    public void componentResized(ComponentEvent e) {
      Parameters params = Parameters.getInstance();
      int paramWidth = params.status.width.getValue();
      int paramHeight = params.status.height.getValue();
      if (paramWidth != getWidth() || paramHeight != getHeight()) {
        params.status.width.setValue(getWidth());
        params.status.height.setValue(getHeight());
      }
    }
	
    public void componentShown(ComponentEvent e) {
      Parameters params = Parameters.getInstance();
      if (params.status.startVisible.getValue() == false) {
        params.status.startVisible.setValue(true);
      }
    }
	
    public void componentHidden(ComponentEvent e) {
      Parameters params = Parameters.getInstance();
      if (params.status.startVisible.getValue() == true) {
        params.status.startVisible.setValue(false);
      }
    }
	
  }

  // Listener for changes in status parameters

  private class StatusParamsChangeListener implements CollectionChangeListener {
    public void reactToChange() {
      _updateFromParams();
    }
  }
    
}

