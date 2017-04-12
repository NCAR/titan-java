///////////////////////////////////////////////////////////////////////
//
// CommandQueue
//
// Synchronized queue for command messages - singleton
//
// Mike Dixon
//
// Sept 2003
//
///////////////////////////////////////////////////////////////////////
//
// This object is set inactive when constructed.
// No commands will be added until setActive(true) is called.
//
///////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.util.*;
import java.nio.*;

public class CommandQueue extends MessageQueue

{
    
    private Parameters _params = Parameters.getInstance();
    private boolean _active = false;

    /**
     * Single instance for this class - it is a singleton
     */
    
    private static final CommandQueue _instance = new CommandQueue();
    
    /**
     * get singleton instance
     */
    
    public static CommandQueue getInstance() {
	return _instance;
    }

    /**
     * private constructor
     */

    private CommandQueue() {
	setMaxSize(_params.rdasComm.maxCommandQueueSize.getValue());
    }

    // set active or inactive

    public void setActive(boolean state) {
	_active = state;
	if (_params.verbose.getValue()) {
	    System.err.println("!!!!!!!!! setting CommandQueue state: " + state);
	}
    }

    // set methods
    
    public void sendMode(int mode) {
	CommandMessage command = new CommandMessage();
	command.setOpMode(mode);
	addBuffer(command.assemble());
    }
    
    public void sendMainPower(boolean status) {
	CommandMessage command = new CommandMessage();
	command.setMainPower(status);
	addBuffer(command.assemble());
    }
	    
    public void sendServoPower(boolean status) {
	CommandMessage command = new CommandMessage();
	command.setServoPower(status);
	addBuffer(command.assemble());
    }
	    
    public void sendRadiate(boolean status) {
	CommandMessage command = new CommandMessage();
	command.setRadiate(status);
	addBuffer(command.assemble());
    }
    
    public void sendSwitchFlags() {
	CommandMessage command = new CommandMessage();
	command.setSwitchFlags(ControlModel.getInstance().getSwitchFlags());
	addBuffer(command.assemble());
    }
	    
    public void sendConfig() {
	CommandMessage command = new CommandMessage();
	command.setConfig();
	addBuffer(command.assemble());
    }
    
    public void sendNGates() {
	CommandMessage command = new CommandMessage();
	command.setNGates(_params.scan.nGates.getValue());
	addBuffer(command.assemble());
    }
    
    public void sendStartRange() {
	CommandMessage command = new CommandMessage();
	command.setStartRange((float) _params.scan.startRange.getValue());
	addBuffer(command.assemble());
    }

    public void sendGateSpacing() {
	CommandMessage command = new CommandMessage();
	command.setGateSpacing((float) _params.scan.gateSpacing.getValue());
	addBuffer(command.assemble());
    }

    public void sendPrf() {
	CommandMessage command = new CommandMessage();
	command.setPrf((float) _params.scan.prf.getValue());
	addBuffer(command.assemble());
    }
    
    public void sendEl(double el) {
	CommandMessage command = new CommandMessage();
	command.setEl((float) el);
	addBuffer(command.assemble());
    }
    
    public void sendAz(double az) {
	CommandMessage command = new CommandMessage();
	command.setAz((float) az);
	addBuffer(command.assemble());
    }
    
    public void sendAzSlewRate(double rate) {
	CommandMessage command = new CommandMessage();
	command.setAzSlewRate((float) rate);
	addBuffer(command.assemble());
    }
    
    public void sendAzSlewRate() {
	CommandMessage command = new CommandMessage();
	command.setAzSlewRate((float) _params.scan.azSlewRate.getValue());
	addBuffer(command.assemble());
    }
    
    public void sendElSlewRate(double rate) {
	CommandMessage command = new CommandMessage();
	command.setElSlewRate((float) rate);
	addBuffer(command.assemble());
    }

    public void sendElSlewRate() {
	CommandMessage command = new CommandMessage();
	command.setElSlewRate((float) _params.scan.elSlewRate.getValue());
	addBuffer(command.assemble());
    }
    
    public void sendScanMode(int mode) {
	CommandMessage command = new CommandMessage();
	command.setScanMode(mode);
	addBuffer(command.assemble());
    }
    
    public void sendElArray() {
	CommandMessage command = new CommandMessage();
	command.setElArray();
	addBuffer(command.assemble());
    }
    
    public void sendDacValue(int channel, int value) {
	CommandMessage command = new CommandMessage();
	command.setDacValue(channel, value);
	addBuffer(command.assemble());
    }
    
    public void sendBeamDecimate(double time_between_beams) {
	CommandMessage command = new CommandMessage();
	command.setBeamDecimate((float) time_between_beams);
	addBuffer(command.assemble());
	if (_params.debug.getValue()) {
	    System.err.println("====>> Setting beam decimate, time between beams: "
			       + time_between_beams);
	}
    }

    public void sendCommandState(int commandType, boolean state) {
	CommandMessage command = new CommandMessage();
	command.setCommandState(commandType, state);
	addBuffer(command.assemble());
    }
    
    public void sendRequestForControl(char[] password) {
	CommandMessage command = new CommandMessage();
	command.setRequestForControl(password);
	// force this to go
	push(command.assemble());
	if (_params.debug.getValue()) {
	    System.err.println("=====>> Sent request for control <<======");
	}
    }
    
    public void addBuffer(ByteBuffer buf) {
	if (_params.verbose.getValue()) {
	    if (_active) {
		System.err.println
		    ("**>> Adding command to queue <<**");
	    } else {
		System.err.println
		    ("**>> CommandQueue inactive, command not added <<**");
	    }
	}
	if (_active) {
	    push(buf);
	}
    }
    
}
