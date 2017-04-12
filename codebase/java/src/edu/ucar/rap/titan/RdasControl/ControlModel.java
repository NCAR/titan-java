///////////////////////////////////////////////////////////////////////
//
// ControlModel - data associated with control panel
//
// Mike Dixon
//
// Jan 2005
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

public class ControlModel

{

    private Parameters _params = Parameters.getInstance();
    private CommandQueue _commandQueue = CommandQueue.getInstance();

    // control model status
    // these members hold the state of the model
    
    private OpMode _opMode;
    private ScanMode _scanMode;

    private double _targetEl = _params.control.startElev.getValue();
    private double _targetAz = _params.control.startAz.getValue();

    private double _elSlewRate = _params.scan.elSlewRate.getValue();;
    private double _azSlewRate = _params.scan.azSlewRate.getValue();;
    
    private boolean _switchStates[] = new boolean[SwitchGroup.nChannels];

    private int _dacValues[] = new int[DacGroup.nChannels];

    /**
     * Single instance for this class - it is a singleton
     */
    
    private static final ControlModel _instance = new ControlModel();
    
    /**
     * get singleton instance
     */
    
    public static ControlModel getInstance() {
	return _instance;
    }

    // private constructor
    
    private ControlModel() {
	
	String startOpModeStr = _params.control.startOpMode.getValue();
	if (startOpModeStr.equals("Off")) {
	    _opMode = OpMode.OFF;
	} else if (startOpModeStr.equals("Standby")) {
	    _opMode = OpMode.STANDBY;
	} else if (startOpModeStr.equals("Operate")) {
	    _opMode = OpMode.OPERATE;
	} else if (startOpModeStr.equals("Calibrate")) {
	    _opMode = OpMode.CALIBRATE;
	}
	
	String startScanModeStr = _params.control.startScanMode.getValue();
	if (startScanModeStr.equals("Manual")) {
	    _scanMode = ScanMode.MANUAL;
	} else if (startScanModeStr.equals("Auto-Volume")) {
	    _scanMode = ScanMode.AUTO_VOL;
	} else if (startScanModeStr.equals("Auto-PPI")) {
	    _scanMode = ScanMode.AUTO_PPI;
	} else if (startScanModeStr.equals("Follow-Sun")) {
	    _scanMode = ScanMode.FOLLOW_SUN;
	} else if (startScanModeStr.equals("Stop")) {
	    _scanMode = ScanMode.STOP;
	}
	
 	for (int ii = 0; ii < SwitchGroup.nChannels; ii++) {
	    if (_params.control.switches.channels[ii].active.getValue()) {
		_switchStates[ii] =
		    _params.control.switches.channels[ii].onAtStartup.getValue();
	    } else {
		_switchStates[ii] = false;
	    }
	}
	
 	for (int ii = 0; ii < DacGroup.nChannels; ii++) {
	    if (_params.control.dacs.channels[ii].active.getValue()) {
		_dacValues[ii] =
		    _params.control.dacs.channels[ii].valueAtStartup.getValue();
	    } else {
		_dacValues[ii] = 0;
	    }
	}

    }

    // set methods
    // most set methods set the value in the model and also send a command
    // to the client

    public void setOpMode(OpMode mode) {
	_opMode = mode;
	if (mode == OpMode.OFF) {
	    _sendOpModeOff();
	} else if (mode == OpMode.STANDBY) {
	    _sendOpModeStandby();
	} else if (mode == OpMode.CALIBRATE) {
	    _sendOpModeCalibrate();
	} else if (mode == OpMode.OPERATE) {
	    _sendOpModeOperate();
	}
    }

    public void setScanMode(ScanMode mode) {
	_scanMode = mode;
	if (mode == ScanMode.MANUAL) {
	    _sendScanModeManual();
	} else if (mode == ScanMode.AUTO_VOL) {
	    _sendScanModeAutoVol();
	} else if (mode == ScanMode.AUTO_PPI) {
	    _sendScanModeAutoPpi();
	} else if (mode == ScanMode.AUTO_RHI) {
	    _sendScanModeAutoRhi();
	} else if (mode == ScanMode.FOLLOW_SUN) {
	    _sendScanModeFollowSun();
	} else if (mode == ScanMode.STOP) {
	    _sendScanModeStop();
	}
    }
    
    public void setTargetEl(double el) {
	_targetEl = el;
	_sendTargetEl();
    }

    public void setTargetAz(double az) {
	_targetAz = az;
	_sendTargetAz();
    }

    public void setElSlewRate(double rate) {
	_elSlewRate = rate;
	_sendElSlewRate();
    }
    
    public void setAzSlewRate(double rate) {
	_azSlewRate = rate;
	_sendAzSlewRate();
    }
    
    public void setDacValue(int channel, int value) {
	if (channel < DacGroup.nChannels) {
	    _dacValues[channel] = value;
	    _sendDacValue(channel);
	}
    }
    
    // setSwitchState does not automatically send the command
    // since there are multiple channels and we do not want
    // to send repetitively.
    // Call sendSwitchFlags() to send the command

    public void setSwitchState(int channel, boolean state) {
	if (channel < SwitchGroup.nChannels) {
	    _switchStates[channel] = state;
	}
    }
    
    public void sendSwitchFlags() {
	_sendSwitchFlags();
    }
    
    // get methods

    public OpMode getOpMode() { return _opMode; }
    public ScanMode getScanMode() { return _scanMode; }
    public double getTargetEl() { return _targetEl; }
    public double getTargetAz() { return _targetAz; }
    public double getElSlewRate() { return _elSlewRate; }
    public double getAzSlewRate() { return _azSlewRate; }

    public boolean getSwitchState(int channel) {
	if (channel < SwitchGroup.nChannels) {
	    if (_params.control.switches.channels[channel].active.getValue()) {
		return _switchStates[channel];
	    }
	}
	return false;
    }
    
    public int getSwitchFlags() {
	int flags = 0;
	for (int ii = 0; ii < SwitchGroup.nChannels; ii++) {
	    if (_params.control.switches.channels[ii].active.getValue()  &&
		_switchStates[ii]) {
		flags |= (1 << ii);
	    }
	} // ii
	return flags;
    }
    
    public int getDacValue(int channel) {
	if (channel < DacGroup.nChannels) {
	    if (_params.control.dacs.channels[channel].active.getValue()) {
		return _dacValues[channel];
	    }
	}
	return 0;
    }
    
    // Resend the current state.
    // This is used to sync with RDAS after a break in comms
    
    public void sendCurrentState() {

	_commandQueue.sendConfig();

	if (_opMode == OpMode.OFF) {
	    _sendOpModeOff();
	} else if (_opMode == OpMode.STANDBY) {
	    _sendOpModeStandby();
	} else if (_opMode == OpMode.CALIBRATE) {
	    _sendOpModeCalibrate();
	} else if (_opMode == OpMode.OPERATE) {
	    _sendOpModeOperate();
	}

	if (_scanMode == ScanMode.MANUAL) {
	    _sendScanModeManual();
	} else if (_scanMode == ScanMode.AUTO_VOL) {
	    _sendScanModeAutoVol();
	} else if (_scanMode == ScanMode.AUTO_PPI) {
	    _sendScanModeAutoPpi();
	} else if (_scanMode == ScanMode.FOLLOW_SUN) {
	    _sendScanModeFollowSun();
	} else if (_scanMode == ScanMode.STOP) {
	    _sendScanModeStop();
	}

    }

    // private methods
    
    private void _sendOpModeOff() {
	_commandQueue.sendMainPower(false);
	_commandQueue.sendServoPower(false);
	_commandQueue.sendRadiate(false);
	_commandQueue.sendMode(CommandMessage.OP_OFF);
    }
    
    private void _sendOpModeStandby() {
	_commandQueue.sendMainPower(true);
	_commandQueue.sendServoPower(true);
	_commandQueue.sendRadiate(false);
	_commandQueue.sendMode(CommandMessage.OP_STANDBY);
    }
    
    private void _sendOpModeCalibrate() {
	_commandQueue.sendMainPower(true);
	_commandQueue.sendServoPower(true);
	_commandQueue.sendRadiate(false);
	_commandQueue.sendMode(CommandMessage.OP_STANDBY);
	_commandQueue.sendMode(CommandMessage.OP_CALIBRATE);
    }
    
    private void _sendOpModeOperate() {
	_commandQueue.sendMainPower(true);
	_commandQueue.sendServoPower(true);
	_commandQueue.sendRadiate(true);
	_commandQueue.sendMode(CommandMessage.OP_OPERATE);
    }

    private void _sendScanModeManual() {
	_sendTargetEl();
	_sendTargetAz();
	_commandQueue.sendScanMode(CommandMessage.SCAN_MANUAL);
    }
    
    private void _sendScanModeAutoVol() {
	_commandQueue.sendElArray();
	_commandQueue.sendScanMode(CommandMessage.SCAN_AUTO_VOL);
    }
    
    private void _sendScanModeAutoPpi() {
	_sendTargetEl();
	_commandQueue.sendScanMode(CommandMessage.SCAN_AUTO_PPI);
    }
	
    private void _sendScanModeAutoRhi() {
    }
    
    private void _sendScanModeFollowSun() {
	_commandQueue.sendScanMode(CommandMessage.SCAN_TRACK_SUN);
    }
    
    private void _sendScanModeStop() {
	_commandQueue.sendScanMode(CommandMessage.SCAN_STOP);
    }
    
    private void _sendTargetEl() {
	_commandQueue.sendEl(_targetEl);
    }

    private void _sendTargetAz() {
	_commandQueue.sendAz(_targetAz);
    }
    
    private void _sendElSlewRate() {
	_commandQueue.sendElSlewRate(_elSlewRate);
    }
    
    private void _sendAzSlewRate() {
	_commandQueue.sendAzSlewRate(_azSlewRate);
    }
    
    public void _sendSwitchFlags() {
	_commandQueue.sendSwitchFlags();
    }
    
    public void _sendDacValue(int channel) {
	if (channel < DacGroup.nChannels) {
	    _commandQueue.sendDacValue(channel, _dacValues[channel]);
	}
    }
    
}
