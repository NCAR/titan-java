///////////////////////////////////////////////////////////////////////
//
// ControlPanel
//
// Mike Dixon
//
// Oct 2002
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

public class ControlPanel extends JPanel implements BeamDataListener

{

  private Parameters _params = Parameters.getInstance();
  private boolean _viaRelay = _params.rdasComm.viaRelay.getValue();

  private PowerStatusGroup _powerStatus;
  private OpModeControl _opModeControl;
  private BeamGeomControl _beamGeomControl;
  private AntennaControl _antennaControl;
  private SwitchGroup _switchGroup;
  private AnalogGroup _analogGroup;
  private DacGroup _dacGroup;
  private ErrorFlagsGroup _errorFlagsGroup;
    
  private ClockTimeDisplay _clockTimeDisplay;
  private RadarTimeDisplay _radarTimeDisplay;
  private AntennaPosnDisplay _antennaPosnDisplay;
  private SunPosnDisplay _sunPosnDisplay;

  private BeamMessage _beam;
  private String _infoContent;
   
  private JMenuBar _menuBar;
  private JButton _editButton;
  private ControlButton _controlButton;
  private JButton _helpButton;
  private JPanel _left3 = null;
  private JPanel _rightUpper = null;

  private boolean _controlEnabled = true;
  private boolean _errorFlagsDisplayed = false;
  private boolean _dacOnRight = true;

  public ControlPanel(Component parent,
                      JMenuBar menuBar) {

    _menuBar = menuBar;

    // add listener for param changes
	
    _params.control.addChangeListener(new ControlParamsChangeListener());
    _params.radar.addChangeListener(new RadarParamsChangeListener());
    _updateFromParams();

    ConfigCommandChangeListener configListener =
      new ConfigCommandChangeListener();
    
    _params.calib.addChangeListener(configListener);
    _params.control.addChangeListener(configListener);
    _params.eng.addChangeListener(configListener);
    _params.scan.addChangeListener(configListener);

    // add listener for new beam data

    BeamDataHandler.getInstance().addListener(this);
	
    // power, opMode, beam and antenna sub-panels
	
    _powerStatus = new PowerStatusGroup(this);
    _switchGroup = new SwitchGroup(this);
    _analogGroup = new AnalogGroup(this);
    _dacGroup = new DacGroup(this);
    _errorFlagsGroup = new ErrorFlagsGroup(this);
    _antennaControl = new AntennaControl(this);
    _beamGeomControl = new BeamGeomControl(this);
    _opModeControl = new OpModeControl(this, _antennaControl);

    // clocks, antenna position, sun position

    _clockTimeDisplay = new ClockTimeDisplay(this);
    _radarTimeDisplay = new RadarTimeDisplay(this);
    _antennaPosnDisplay = new AntennaPosnDisplay(this);
    _sunPosnDisplay = new SunPosnDisplay(this);
	
    // top panel
	
    JPanel topPanel = new JPanel(new BorderLayout());
    add(topPanel);
	
    // ops mode, power switches, analog and dacs channels go in left panel

    JPanel opPower = new JPanel(new BorderLayout());
    opPower.add(_opModeControl, BorderLayout.WEST);
    opPower.add(_powerStatus, BorderLayout.EAST);

    JPanel left1 = new JPanel(new BorderLayout());
    left1.add(opPower, BorderLayout.NORTH);
    left1.add(_switchGroup, BorderLayout.CENTER);
	
    JPanel left2 = new JPanel(new BorderLayout());
    left2.add(left1, BorderLayout.NORTH);
    left2.add(_analogGroup, BorderLayout.SOUTH);

    _left3 = new JPanel(new BorderLayout());
    _left3.add(left2, BorderLayout.NORTH);
    if (!_params.control.showDacOnRight.getValue()) {
      _left3.add(_dacGroup, BorderLayout.CENTER);
      _dacOnRight = false;
    }
    if (_params.control.showErrorFlags.getValue()) {
      _left3.add(_errorFlagsGroup, BorderLayout.SOUTH);
      _errorFlagsDisplayed = true;
    }
	
    JPanel leftPanel = new JPanel(new BorderLayout());
    leftPanel.add(_left3, BorderLayout.NORTH);

    // antenna control, times and positions in right panel
	
    _rightUpper = new JPanel(new BorderLayout());
    _rightUpper.add(_antennaControl, BorderLayout.NORTH);
    if (_params.control.showDacOnRight.getValue()) {
      _rightUpper.add(_dacGroup, BorderLayout.CENTER);
      _dacOnRight = true;
    }
	
    JPanel radarPanel = new JPanel(new BorderLayout());
    radarPanel.add(_radarTimeDisplay, BorderLayout.SOUTH);
    radarPanel.add(_antennaPosnDisplay, BorderLayout.NORTH);

    JPanel sunPanel = new JPanel(new BorderLayout());
    sunPanel.add(_clockTimeDisplay, BorderLayout.NORTH);
    sunPanel.add(_sunPosnDisplay, BorderLayout.SOUTH);

    JPanel rightLower = new JPanel(new BorderLayout());
    rightLower.add(radarPanel, BorderLayout.NORTH);
    rightLower.add(sunPanel, BorderLayout.SOUTH);

    JPanel rightPanel = new JPanel(new BorderLayout());
    rightPanel.add(_rightUpper, BorderLayout.NORTH);
    rightPanel.add(rightLower, BorderLayout.SOUTH);

    // add left and right parts
	
    topPanel.add(leftPanel, BorderLayout.WEST);
    topPanel.add(rightPanel, BorderLayout.EAST);

    // populate menu in interactive mode

    if (_menuBar != null) {
      
      // edit button
    
      _editButton = new JButton(_params.control.getViewEditAction());
      _menuBar.add(_editButton);
	
      // control button
      
      if (_viaRelay) {
        _controlButton = new ControlButton();
        _menuBar.add(_controlButton);
        setControlEnabled(false);
      }
      
      // help button
      
      _helpButton = new JButton("Help");
      _menuBar.add(_helpButton);
      _infoContent =
        "<h1>ControlPanel help info</h1>\n" +
        "<hr>\n" +
        "<h2>Operation</h2>\n" +
        "<ul>\n" +
        "<li>Select Ops mode.\n" +
        "<li>Select scan mode.\n" +
        "<li>Adjust slew rates as appropriate.\n" +
        "<li>In manual mode, point antenna with buttons. " +
        "Delta deg sets the resolution of each click\n" +
        "<li>Set switches and DAC channels.\n" +
        "</ul>\n" +
        "<hr>\n" +
        "<h2>Configuration</h2>\n" +
        "<ul>\n" +
        "<li>Use Edit button to bring up parameters panel.\n" +
        "<li>Set as appropriate.\n" +
        "<li>Select 'allow resize', apply.\n" +
        "<li>Resize window to suit.\n" +
        "<li>Turn off 'Allow resize' to fix window size.\n" +
        "</ul>\n";
      _helpButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            InfoFrame info =
              new InfoFrame(getX() + 30, getY() + 30,
                            600, 500);
            info.setTitle("Info for Control Panel");
            info.setContent(_infoContent);
          }
        });

      // create the ticker thread, start it
      
      Ticker ticker = new Ticker();
      ticker.start();

    } // if (_menuBar != null)

  }
  
  // public set/get methods

  public SwitchGroup getSwitchGroup() {
    return _switchGroup;
  }

  public AnalogGroup getAnalogGroup() {
    return _analogGroup;
  }

  public DacGroup getDacGroup() {
    return _dacGroup;
  }

  public ErrorFlagsGroup getErrorFlagsGroup() {
    return _errorFlagsGroup;
  }

  // get binary state of power, switch, analog and error channels
  
  public String getStateBinary() {

    StringBuffer buf = new StringBuffer();

    // power status

    buf.append(_powerStatus.getStateBinary());
    buf.append("-");

    // switch channels

    buf.append(_switchGroup.getStateBinary());
    buf.append("-");
    
    // analog channels

    buf.append(_analogGroup.getStateBinary());
    buf.append("-");

    // error flags

    buf.append(_errorFlagsGroup.getStateBinary());
    
    return buf.toString();

  }
    
  // get binary string of power, switch, analog and error channels
  
  public String getStateText() {

    StringBuffer buf = new StringBuffer();

    // power status

    buf.append(_powerStatus.getStateText());

    // switch channels

    buf.append(_switchGroup.getStateText());
    
    // analog channels

    buf.append(_analogGroup.getStateText());

    // error flags

    buf.append(_errorFlagsGroup.getStateText());
    
    return buf.toString();

  }
    
  // enable commands

  public void setControlEnabled(boolean state) {
    if (state != _controlEnabled) {
      _opModeControl.enableControl(state);
      _antennaControl.enableControl(state);
      _switchGroup.enableControl(state);
      _dacGroup.enableControl(state);
      _controlEnabled = state;
      _controlButton.setText();
    }
  } 

  public void showControlEnabledDialog(boolean state) {
	
    if (state) {
      JOptionPane.showMessageDialog
        (_controlButton,
         "Relay process accepting control.",
         "Remote control accepted",
         JOptionPane.INFORMATION_MESSAGE);
    } else {
      JOptionPane.showMessageDialog
        (_controlButton,
         "Relay process not allowing remote control.",
         "Remote control denied",
         JOptionPane.ERROR_MESSAGE);
    }

  }

  // update from parameters

  private void _updateFromParams() {

    if (_params.control.showErrorFlags.getValue()) {
      if (_left3 != null && !_errorFlagsDisplayed) {
        _left3.add(_errorFlagsGroup, BorderLayout.SOUTH);
        _errorFlagsDisplayed = true;
      }
    } else {
      if (_left3 != null && _errorFlagsDisplayed) {
        _left3.remove(_errorFlagsGroup);
        _errorFlagsDisplayed = false;
      }
    }

    if (_params.control.showDacOnRight.getValue()) {
      if (!_dacOnRight) {
        _left3.remove(_dacGroup);
        _rightUpper.add(_dacGroup, BorderLayout.CENTER);
        _dacOnRight = true;
      }
    } else {
      if (_dacOnRight) {
        _rightUpper.remove(_dacGroup);
        _left3.add(_dacGroup, BorderLayout.CENTER);
        _dacOnRight = false;
      }
    }

  }

  // handle incoming beam

  public void handleBeam(BeamMessage beam, double rate) {
    _beam = beam;
  }
    
  // redraw beam-related items

  private void _redraw() {

    if (_beam == null) {
      return;
    }

//     setPowerIndicator1(_beam.getPowerStatus1());
//     setPowerIndicator2(_beam.getPowerStatus2());
//     setPowerIndicator3(_beam.getPowerStatus3());
//     setPowerIndicator4(_beam.getPowerStatus4());

    _sunPosnDisplay.redraw();
    _antennaPosnDisplay.redraw();
    _radarTimeDisplay.redraw();
    // _powerStatus.redraw();
    _switchGroup.redraw();
    _switchGroup.redraw();
    _analogGroup.redraw();
	
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
      int paramX = params.control.xx.getValue();
      int paramY = params.control.yy.getValue();
      if (paramX != getX() || paramY != getY()) {
        params.control.xx.setValue(getX());
        params.control.yy.setValue(getY());
      }
    }
	
    public void componentResized(ComponentEvent e) {
      Parameters params = Parameters.getInstance();
      int paramWidth = params.control.width.getValue();
      int paramHeight = params.control.height.getValue();
      if (paramWidth != getWidth() || paramHeight != getHeight()) {
        params.control.width.setValue(getWidth());
        params.control.height.setValue(getHeight());
      }
    }
	
    public void componentShown(ComponentEvent e) {
      Parameters params = Parameters.getInstance();
      if (params.control.startVisible.getValue() == false) {
        params.control.startVisible.setValue(true);
      }
    }
	
    public void componentHidden(ComponentEvent e) {
      Parameters params = Parameters.getInstance();
      if (params.control.startVisible.getValue() == true) {
        params.control.startVisible.setValue(false);
      }
    }
	
  }

  // Listener for changes in control parameters

  private class ControlParamsChangeListener implements CollectionChangeListener {
    public void reactToChange() {
      _updateFromParams();
    }
  }
    
  // Listener for changes in radar parameters
    
  private class RadarParamsChangeListener implements CollectionChangeListener {
    public void reactToChange() {
    }
  }

  // Listener for changes in parameters for config command
    
  private class ConfigCommandChangeListener implements CollectionChangeListener {
    public void reactToChange() {
      CommandQueue.getInstance().sendConfig();
    }
  }

  // control button
    
  private class ControlButton extends JButton {
	
    public ControlButton() {
      setText();
      addActionListener(new ToggleListener());
    }
	
    public void setText() {
      if (_controlEnabled) {
        setText("Disable control");
      } else {
        setText("Enable control");
      }
    }
	
    // action listener for toggle

    private class ToggleListener implements ActionListener {
	    
      public void actionPerformed(ActionEvent e) {
		
        if (_controlEnabled) {

          // simple toggle to disabled state
          setControlEnabled(false);
		    
        } else {

          String password = new String("testxxxx");
          CommandQueue.getInstance().sendRequestForControl
            (password.toCharArray());
		    
        }

      }

    } // ToggleListener
	
  } // ControlButton

}

