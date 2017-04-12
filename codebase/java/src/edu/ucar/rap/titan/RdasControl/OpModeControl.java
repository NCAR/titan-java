///////////////////////////////////////////////////////////////////////
//
// OpModeControl
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

public class OpModeControl extends JPanel
				   
{

    private Parameters _params = Parameters.getInstance();
    private ControlModel _model = ControlModel.getInstance();
   
    private JRadioButton _offButton;
    private JRadioButton _standbyButton;
    private JRadioButton _operateButton;
    private JRadioButton _calibrateButton;
    
    private OffListener _offListener;
    private StandbyListener _standbyListener;
    private CalibrateListener _calibrateListener;
    private OperateListener _operateListener;
    
    private AntennaControl _antennaControl;
    
    public OpModeControl(Component parent,
			 AntennaControl antennaControl) {
	
	_antennaControl = antennaControl;
	
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	setBorder(CustomBorder.createTop(parent, "Ops mode", 3));
	
	_offButton = new JRadioButton("Off");
	_standbyButton = new JRadioButton("Standby");
	_calibrateButton = new JRadioButton("Calibrate");
	_operateButton = new JRadioButton("Operate");
	
	add(_offButton);
	add(_standbyButton);
	add(_operateButton);
	add(_calibrateButton);
	
	ButtonGroup group = new ButtonGroup();
	group.add(_offButton);
	group.add(_standbyButton);
	group.add(_operateButton);
	group.add(_calibrateButton);
	
	String startOpModeStr = _params.control.startOpMode.getValue();
	if (startOpModeStr.equals("Off")) {
	    setOpModeOff();
	} else if (startOpModeStr.equals("Standby")) {
	    setOpModeStandby();
	} else if (startOpModeStr.equals("Operate")) {
	    setOpModeOperate();
	} else if (startOpModeStr.equals("Calibrate")) {
	    setOpModeCalibrate();
	}

	_offListener = new OffListener();
	_offButton.addActionListener(_offListener);
	
	_standbyListener = new StandbyListener();
	_standbyButton.addActionListener(_standbyListener);
	
	_calibrateListener = new CalibrateListener();
	_calibrateButton.addActionListener(_calibrateListener);
	
	_operateListener = new OperateListener();
	_operateButton.addActionListener(_operateListener);
	
    }

    // enable control GUI

    public void enableControl(boolean state) {
	_offButton.setEnabled(state);
	_standbyButton.setEnabled(state);
	_operateButton.setEnabled(state);
	_calibrateButton.setEnabled(state);
    }
    
    // set opmode off

    public void setOpModeOff() {
	if (ControlFrame.getInstance() != null &&
            ControlFrame.getInstance().getPanel() != null) {
          ControlPanel cpanel = ControlFrame.getInstance().getPanel();
          cpanel.getSwitchGroup().resetToStartup(); 
          cpanel.getDacGroup().resetToStartup(); 
	}
	_offButton.setSelected(true);
	// turn ASCOPE sampling off
	if (_params.aScope.performSampling.getValue()) {
	    _params.aScope.performSampling.setValue(false);
	}
	_params.aScope.callChangeListeners();
	// set model
	_model.setOpMode(OpMode.OFF);
    }
    
    // set opmode standby

    public void setOpModeStandby() {
	_standbyButton.setSelected(true);
	// turn ASCOPE sampling off
	if (_params.aScope.performSampling.getValue()) {
	    _params.aScope.performSampling.setValue(false);
	}
	_params.aScope.callChangeListeners();
	// set model
	_model.setOpMode(OpMode.STANDBY);
    }
    
    // set opmode calibrate

    public void setOpModeCalibrate() {
	_calibrateButton.setSelected(true);
	_antennaControl.setScanModeStop();
	// turn ASCOPE sampling on
	if (!_params.aScope.performSampling.getValue()) {
	    _params.aScope.performSampling.setValue(true);
	}
	_params.aScope.callChangeListeners();
	// set model
	_model.setOpMode(OpMode.CALIBRATE);
    }
    
    // set opmode operate
    
    public void setOpModeOperate() {
	_operateButton.setSelected(true);
	// turn ASCOPE sampling off
	if (_params.aScope.performSampling.getValue()) {
	    _params.aScope.performSampling.setValue(false);
	}
	_params.aScope.callChangeListeners();
	// set model
	_model.setOpMode(OpMode.OPERATE);
    }
    
    // off action listener
    class OffListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    setOpModeOff();
	}
    }
    
    // standby action listener
    class StandbyListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    setOpModeStandby();
	}
    }
    
    // calibrate action listener
    class CalibrateListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    setOpModeCalibrate();
	}
    }
    
    // operate action listener
    class OperateListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    setOpModeOperate();
	}
    }

}
