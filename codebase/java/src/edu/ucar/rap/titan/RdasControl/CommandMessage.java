///////////////////////////////////////////////////////////////////////
//
// CommandMessage
//
// ASCII Message
//
// Mike Dixon
//
// Dec 2002
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.io.*;
import java.nio.*;
import java.net.*;

public class CommandMessage implements Message
    
{
    
    public final static int OP_MODE_COMMAND = 100;
    public final static int SCAN_MODE_COMMAND = 110;

    public final static int MAIN_POWER_COMMAND = 200;
    public final static int SERVO_POWER_COMMAND = 220;
    public final static int RADIATE_COMMAND = 230;
    public final static int SWITCH_FLAGS_COMMAND = 240;

    public final static int N_GATES_COMMAND = 300;
    public final static int START_RANGE_COMMAND = 310;
    public final static int GATE_SPACING_COMMAND = 320;
    public final static int PRF_COMMAND = 330;

    public final static int AZ_COMMAND = 400;
    public final static int EL_COMMAND = 410;
    public final static int EL_ARRAY_COMMAND = 420;

    public final static int AZ_SLEW_RATE_COMMAND = 500;
    public final static int EL_SLEW_RATE_COMMAND = 510;
    public final static int MAX_AZ_SLEW_RATE_COMMAND = 520;
    public final static int MAX_EL_SLEW_RATE_COMMAND = 530;

    public final static int CONFIG_COMMAND = 600;
    
    public final static int DAC_COMMAND = 700;

    public final static int RDAS_COMMAND_MAX = 2000;

    // The following commands are intended to be sent from RdasControl to
    // a second instance of RdasControl running in relay mode.

    // BEAM_DECIMATE_COMMAND:
    // Used to indicate how much the beams should be decimated before
    // being sent to the client.

    public final static int BEAM_DECIMATE_COMMAND = 3000;
    
    // REQUEST_FROM_CONTROL_COMMAND:
    // Asks if command messages can be sent.
    
    public final static int REQUEST_FOR_CONTROL_COMMAND = 3010;

    // Message components

    public final static int OP_OFF = 0;
    public final static int OP_STANDBY = 1;
    public final static int OP_CALIBRATE = 2;
    public final static int OP_OPERATE = 3;
    
    public final static int SCAN_STOP = 0;
    public final static int SCAN_MANUAL = 1;
    public final static int SCAN_AUTO_VOL = 2;
    public final static int SCAN_AUTO_PPI = 3;
    public final static int SCAN_TRACK_SUN = 4;

    public final static int POLARIZATION_HORIZONTAL = 0;
    public final static int POLARIZATION_VERTICAL = 1;
    public final static int POLARIZATION_CIRCULAR = 2;

    private MessageType _type = MessageType.CONTROL;

    private Parameters _params = Parameters.getInstance();

    private int _command;
    private boolean _state;
    private int _iValue;
    private float _fValue;
    private float[] _fArray;
    private char[] _password;

    private boolean _elVoltagePositive;
    private boolean _azVoltagePositive;

    private int _nGates;
    private int _samplesPerAz;
    private int _samplesPerGate;
    private int _polarizationCode;
    private int _switchFlags;

    private int _dacChannel;
    private int _dacValue;
    private int _dac0;
    private int _dac1;
    private int _autoResetTimeout;

    private int _shutdownOpMode;
    
    private float _startRange;
    private float _gateSpacing;
    private float _prf;
    private float _pulseWidth;

    private float _antennaElCorr;
    private float _antennaAzCorr;
    private float _controlElCorr;
    private float _controlAzCorr;
    private float _antennaMinElev;
    private float _antennaMaxElev;
    private float _antennaElSlewRate;
    private float _antennaAzSlewRate;
    private float _antennaMaxElSlewRate;
    private float _antennaMaxAzSlewRate;

    private float _calibSlope;
    private float _calibOffset1km;

    private float _elevTolerance;
    private float _ppiAzOverlap;

    public CommandMessage() {
    }

    public void setOpMode(int mode) {
	_command = OP_MODE_COMMAND;
	_iValue = mode;
    }
    
    public void setScanMode(int mode) {
	_command = SCAN_MODE_COMMAND;
	_iValue = mode;
    }
    
    public void setCommandState(int commandType, boolean state) {
	_command = commandType;
	_state = state;
    }
    
    public void setMainPower(boolean state) {
	_command = MAIN_POWER_COMMAND;
	_state = state;
    }
    
    public void setServoPower(boolean state) {
	_command = SERVO_POWER_COMMAND;
	_state = state;
    }
    
    public void setRadiate(boolean state) {
	_command = RADIATE_COMMAND;
	_state = state;
    }
    
    public void setSwitchFlags(int flags) {
	_command = SWITCH_FLAGS_COMMAND;
	_iValue = flags;
    }
    
    public void setAz(float az) {
	_command = AZ_COMMAND;
	_fValue = az;
    }
    
    public void setEl(float el) {
	_command = EL_COMMAND;
	_fValue = el;
    }
    
    public void setNGates(int n_gates) {
	_command = N_GATES_COMMAND;
	_iValue = n_gates;
    }
    
    public void setNGates() {
	setNGates(_params.scan.nGates.getValue());
    }
    
    public void setStartRange(float start_range) {
	_command = START_RANGE_COMMAND;
	_fValue = start_range;
    }
    
    public void setStartRange() {
	setStartRange((float) _params.scan.startRange.getValue());
    }
    
    public void setGateSpacing(float gate_spacing) {
	_command = GATE_SPACING_COMMAND;
	_fValue = gate_spacing;
    }
    
    public void setGateSpacing() {
	setGateSpacing((float) _params.scan.gateSpacing.getValue());
    }
    
    public void setPrf(float prf) {
	_command = PRF_COMMAND;
	_fValue = prf;
    }
    
    public void setPrf() {
	setPrf((float) _params.scan.prf.getValue());
    }
    
    public void setAzSlewRate(float az_rate) {
	_command = AZ_SLEW_RATE_COMMAND;
	_fValue = az_rate;
    }
    
    public void setAzSlewRate() {
	setAzSlewRate((float) _params.scan.azSlewRate.getValue());
    }
    
    public void setElSlewRate(float el_rate) {
	_command = EL_SLEW_RATE_COMMAND;
	_fValue = el_rate;
    }
    
    public void setElSlewRate() {
	setElSlewRate((float) _params.scan.elSlewRate.getValue());
    }
    
    public void setMaxAzSlewRate(float max_az_rate) {
	_command = MAX_AZ_SLEW_RATE_COMMAND;
	_fValue = max_az_rate;
    }
    
    public void setMaxAzSlewRate() {
	setMaxAzSlewRate((float) _params.eng.antennaMaxAzSlewRate.getValue());
    }
    
    public void setMaxElSlewRate(float max_el_rate) {
	_command = MAX_EL_SLEW_RATE_COMMAND;
	_fValue = max_el_rate;
    }
    
    public void setMaxElSlewRate() {
	setMaxElSlewRate((float) _params.eng.antennaMaxElSlewRate.getValue());
    }
    
    public void setElArray(float[] el_array) {
	_command = EL_ARRAY_COMMAND;
	_fArray = new float[el_array.length];
	for (int ii = 0; ii < el_array.length; ii++) {
	    _fArray[ii] = el_array[ii];
	}
    }
    
    public void setElArray() {
	String[] strArray =
	    _params.scan.elevationList.getValue().split(",");
	float[] elevArray = new float[strArray.length];
	for (int i = 0; i < strArray.length; i++) {
	    float el = (float) 0.0;
	    try {
		el = Float.parseFloat(strArray[i]);
	    }
	    catch (java.lang.NumberFormatException nfe) {
		System.err.println(nfe);
	    }
	    elevArray[i] = el;
	}
	setElArray(elevArray);
    }
    
    public void setConfig() {

	_command = CONFIG_COMMAND;

	_nGates = _params.scan.nGates.getValue();
	_samplesPerAz = _params.scan.samplesPerAz.getValue();
	_samplesPerGate = _params.scan.samplesPerGate.getValue();

	String polarStr = _params.scan.polarization.getValue();
	if (polarStr.equals("Vertical")) {
	    _polarizationCode = CommandMessage.POLARIZATION_VERTICAL;
	} else if (polarStr.equals("Circular")) {
	    _polarizationCode = CommandMessage.POLARIZATION_CIRCULAR;
	} else {
	    _polarizationCode = CommandMessage.POLARIZATION_HORIZONTAL;
	}
	
	_elVoltagePositive = _params.eng.elVoltagePositive.getValue();
	_azVoltagePositive = _params.eng.azVoltagePositive.getValue();
	_switchFlags = ControlModel.getInstance().getSwitchFlags();
	_dac0 = ControlModel.getInstance().getDacValue(0);
	_dac1 = ControlModel.getInstance().getDacValue(1);
	_autoResetTimeout = _params.eng.autoResetTimeout.getValue();

        String shutdownOpModeStr = _params.control.shutdownOpMode.getValue();
        if (shutdownOpModeStr.equals("Standby")) {
          _shutdownOpMode = 1;
        } else {
          _shutdownOpMode = 0;
        }
	
	_startRange = (float) _params.scan.startRange.getValue();
	_gateSpacing = (float) _params.scan.gateSpacing.getValue();
	_prf = (float) _params.scan.prf.getValue();
	_pulseWidth = (float) _params.calib.pulseWidth.getValue();

	_antennaElCorr = (float) _params.eng.antennaElCorr.getValue();
	_antennaAzCorr = (float) _params.eng.antennaAzCorr.getValue();
	_controlElCorr = (float) _params.eng.controlElCorr.getValue();
	_controlAzCorr = (float) _params.eng.controlAzCorr.getValue();
	_antennaMinElev = (float) _params.eng.antennaMinElev.getValue();
	_antennaMaxElev = (float) _params.eng.antennaMaxElev.getValue();
	_antennaElSlewRate = (float) _params.scan.elSlewRate.getValue();
	_antennaAzSlewRate = (float) _params.scan.azSlewRate.getValue();
	_antennaMaxElSlewRate = (float) _params.eng.antennaMaxElSlewRate.getValue();
	_antennaMaxAzSlewRate = (float) _params.eng.antennaMaxAzSlewRate.getValue();

	_calibSlope = (float) _params.calib.slope.getValue();
	_calibOffset1km = (float) _params.calib.offset1km.getValue();

	_elevTolerance = (float) _params.eng.elevTolerance.getValue();
	_ppiAzOverlap = (float) _params.eng.ppiAzOverlap.getValue();

    }

    public void setDacValue(int channel, int value) {
	_command = DAC_COMMAND;
	_dacChannel = channel;
	_dacValue = value;
	if (channel == 0) {
	    _dac0 = value;
	} else {
	    _dac1 = value;
	}
    }
    
    public void setBeamDecimate(float time_between_beams) {
	_command = BEAM_DECIMATE_COMMAND;
	_fValue = time_between_beams;
    }
    
    public void setRequestForControl(char[] password) {
	_command = REQUEST_FOR_CONTROL_COMMAND;
	_password = password;
    }
    
    public MessageType getType() {
	return _type;
    }

    public int getCommand() {
	return _command;
    }

    public boolean getState() {
	return _state;
    }

    public int getIValue() {
	return _iValue;
    }

    public float getFValue() {
	return _fValue;
    }

    public float[] getFArray() {
	return _fArray;
    }

    public String compileCommand() {
	return new String("#0#0" + '\0');
    }

    public String toString() {

	switch (_command) {
	    
	case OP_MODE_COMMAND:
	    switch (_iValue) {
	    case OP_OFF:
		return "OP_MODE: OFF";
	    case OP_STANDBY:
		return "OP_MODE: STANDBY";
	    case OP_CALIBRATE:
		return "OP_MODE: CALIBRATE";
	    case OP_OPERATE:
		return "OP_MODE: OPERATE";
	    default:
		return "OP_MODE: UNKNOWN";
	    }

	case SCAN_MODE_COMMAND:
	    switch (_iValue) {
	    case SCAN_STOP:
		return "SCAN_MODE: STOP";
	    case SCAN_MANUAL:
		return "SCAN_MODE: MANUAL";
	    case SCAN_AUTO_VOL:
		return "SCAN_MODE: AUTO_VOL";
	    case SCAN_AUTO_PPI:
		return "SCAN_MODE: AUTO_PPI";
	    case SCAN_TRACK_SUN:
		return "SCAN_MODE: TRACK_SUN";
	    default:
		return "SCAN_MODE: UNKNOWN";
	    }

	case MAIN_POWER_COMMAND:
	    return "Main power: " + _state;

	case SERVO_POWER_COMMAND:
	    return "Servo power: " + _state;
	    
	case RADIATE_COMMAND:
	    return "Radiate: " + _state;
	    
	case SWITCH_FLAGS_COMMAND:
	    return "Switch flags: " + _iValue;
	    
	case N_GATES_COMMAND:
	    return "N gates: " + _iValue;
	    
	case START_RANGE_COMMAND:
	    return "Start range: " + _fValue;
	    
	case GATE_SPACING_COMMAND:
	    return "Gate spacing: " + _fValue;
	    
	case PRF_COMMAND:
	    return "Prf: " + _fValue;
	    
	case AZ_COMMAND:
	    return "Az: " + _fValue;
	    
	case EL_COMMAND:
	    return "El: " + _fValue;
	    
	case EL_ARRAY_COMMAND:
	    StringBuffer sb = new StringBuffer("El array: ");
	    for (int i = 0; i < _fArray.length; i++) {
		sb.append(_fArray[i]);
		if (i != _fArray.length - 1) {
		    sb.append(",");
		}
	    }
	    return new String(sb);
	    
	case AZ_SLEW_RATE_COMMAND:
	    return "Az slew rate: " + _fValue;
	    
	case EL_SLEW_RATE_COMMAND:
	    return "El slew rate: " + _fValue;
	    
	case MAX_AZ_SLEW_RATE_COMMAND:
	    return "Max az slew rate: " + _fValue;
	    
	case MAX_EL_SLEW_RATE_COMMAND:
	    return "Max el slew rate: " + _fValue;
	    
	case CONFIG_COMMAND:
	    return ("Configuration:\n" +
		    "  _nGates: " + _nGates + "\n" +
		    "  _samplesPerAz: " + _samplesPerAz + "\n" +
		    "  _samplesPerGate: " + _samplesPerGate + "\n" +
		    "  _polarizationCode: " + _polarizationCode + "\n" +
		    "  _elVoltagePositive: " + _elVoltagePositive + "\n" +
		    "  _azVoltagePositive: " + _azVoltagePositive + "\n" +
		    "  _switchFlags: " + _switchFlags + "\n" +
		    "  _autoResetTimeout: " + _autoResetTimeout + "\n" +
		    "  _shutdownOpMode: " + _shutdownOpMode + "\n" +
		    "  _startRange: " + _startRange + "\n" +
		    "  _gateSpacing: " + _gateSpacing + "\n" +
		    "  _prf: " + _prf + "\n" +
		    "  _pulseWidth: " + _pulseWidth + "\n" +
		    "  _antennaElCorr: " + _antennaElCorr + "\n" +
		    "  _antennaAzCorr: " + _antennaAzCorr + "\n" +
		    "  _antennaMinElev: " + _antennaMinElev + "\n" +
		    "  _antennaMaxElev: " + _antennaMaxElev + "\n" +
		    "  _antennaElSlewRate: " + _antennaElSlewRate + "\n" +
		    "  _antennaAzSlewRate: " + _antennaAzSlewRate + "\n" +
		    "  _antennaMaxElSlewRate: " + _antennaMaxElSlewRate + "\n" +
		    "  _antennaMaxAzSlewRate: " + _antennaMaxAzSlewRate + "\n" +
		    "  _controlElCorr: " + _controlElCorr + "\n" +
		    "  _controlAzCorr: " + _controlAzCorr + "\n" +
		    "  _calibSlope: " + _calibSlope + "\n" +
		    "  _calibOffset1km: " + _calibOffset1km + "\n" +
		    "  _elevTolerance: " + _elevTolerance + "\n" +
		    "  _ppiAzOverlap: " + _ppiAzOverlap + "\n");
	    
	case DAC_COMMAND:
	    return "DAC channel[" + _dacChannel + "] has value: " + _dacValue;

	case BEAM_DECIMATE_COMMAND:
	    return "Beam decimation, time between beams: " + _fValue;
	    
	case REQUEST_FOR_CONTROL_COMMAND:
	    return "Request for control, password: " + _password;
	    
	default:
	    return "COMMAND: UNKNOWN";
	    
	}

    }

    // assemble command into byte buffer
    
    public ByteBuffer assemble() {
	
	if (_params.verbose.getValue()) {
	    System.out.println("Assembling command:");
	    System.out.println(toString());
	}
	
	ByteBuffer outBuf = ByteBuffer.allocate(1024);
	
	if (!_params.rdasComm.bigEndian.getValue()) {
	    outBuf.order(ByteOrder.LITTLE_ENDIAN);
	}
    
	outBuf.clear();

	// cookie
	
	int cookie = 0x5B5B5B5B;
	outBuf.putInt(cookie);

	// command
	
	outBuf.putInt(_command);
	
	switch (_command) {
	    
	    // iValue commands
	    
	case OP_MODE_COMMAND:
	case SCAN_MODE_COMMAND:
	case N_GATES_COMMAND:
	case SWITCH_FLAGS_COMMAND:
	    {
		// length followed by iValue
		int len = 4;
		outBuf.putInt(len);
		outBuf.putInt(_iValue);
		break;
	    }

	    // state commands
	    
	case MAIN_POWER_COMMAND:
	case SERVO_POWER_COMMAND:
	case RADIATE_COMMAND:
	    {
		// length followed by state
		int len = 4;
		outBuf.putInt(len);
		if (_state) {
		    outBuf.putInt(1);
		} else {
		    outBuf.putInt(0);
		}
		break;
	    }

	    // fValue commands

	case START_RANGE_COMMAND:
	case GATE_SPACING_COMMAND:
	case PRF_COMMAND:
	case AZ_COMMAND:
	case EL_COMMAND:
	case AZ_SLEW_RATE_COMMAND:
	case EL_SLEW_RATE_COMMAND:
	case MAX_AZ_SLEW_RATE_COMMAND:
	case MAX_EL_SLEW_RATE_COMMAND:
	case BEAM_DECIMATE_COMMAND:
	    {
		// length followed by fValue
		int len = 4;
		outBuf.putInt(len);
		outBuf.putFloat(_fValue);
		break;
	    }
	    
	    // fArray commands

	case EL_ARRAY_COMMAND:
	    {
		// length followed by fArray
		int len = 4 * _fArray.length;
		outBuf.putInt(len);
		for (int i = 0; i < _fArray.length; i++) {
		    outBuf.putFloat(_fArray[i]);
		}
		break;
	    }
	    
	case CONFIG_COMMAND:
	    {

		// length followed by config structure

		int length = 4 * 64;
		int version = 1;
		outBuf.putInt(length);

		// 20 ints
		
		outBuf.putInt(version);
		outBuf.putInt(length);
		outBuf.putInt(_nGates);
		outBuf.putInt(_samplesPerAz);
		outBuf.putInt(_samplesPerGate);
		outBuf.putInt(_polarizationCode);
		if (_elVoltagePositive) {
		    outBuf.putInt(1);
		} else {
		    outBuf.putInt(0);
		}
		if (_azVoltagePositive) {
		    outBuf.putInt(1);
		} else {
		    outBuf.putInt(0);
		}
		outBuf.putInt(_switchFlags);
		outBuf.putInt(_dac0);
		outBuf.putInt(_dac1);
		outBuf.putInt(_autoResetTimeout);
		outBuf.putInt(_shutdownOpMode);
		for (int i = 0; i < 15; i++) {
		    outBuf.putInt(0);
		}

		// 26 floats

		outBuf.putFloat(_startRange);
		outBuf.putFloat(_gateSpacing);
		outBuf.putFloat(_prf);
		outBuf.putFloat(_pulseWidth);
		outBuf.putFloat(_antennaElCorr);
		outBuf.putFloat(_antennaAzCorr);
		outBuf.putFloat(_controlElCorr);
		outBuf.putFloat(_controlAzCorr);
		outBuf.putFloat(_antennaMinElev);
		outBuf.putFloat(_antennaMaxElev);
   		outBuf.putFloat(_antennaElSlewRate);
		outBuf.putFloat(_antennaAzSlewRate);
   		outBuf.putFloat(_antennaMaxElSlewRate);
		outBuf.putFloat(_antennaMaxAzSlewRate);
		outBuf.putFloat(_calibSlope);
		outBuf.putFloat(_calibOffset1km);
		outBuf.putFloat(_elevTolerance);
		outBuf.putFloat(_ppiAzOverlap);
		for (int i = 0; i < 18; i++) {
		    outBuf.putFloat(0);
		}
		break;
	    }
	    
	    // DAC channel command
	    
	case DAC_COMMAND:
	    {
		// length followed by channel and state
		int len = 8;
		outBuf.putInt(len);
		outBuf.putInt(_dacChannel);
		outBuf.putInt(_dacValue);
		break;
	    }

	case REQUEST_FOR_CONTROL_COMMAND:
	    {
		// length followed by password
		int len = 2 * _password.length;
		outBuf.putInt(len);
		for (int i = 0; i < _password.length; i++) {
		    outBuf.putChar(_password[i]);
		}
		break;
	    }

	default:
	    break;
	    
	}

	outBuf.flip();
	return outBuf;

    }

}

