///////////////////////////////////////////////////////////////////////
//
// BeamMessage
//
// Mike Dixon
//
// Dec 2002
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.util.*;
import java.nio.*;

public class BeamMessage implements Message

{
    
    private Parameters _params = Parameters.getInstance();

    private MessageType _type = MessageType.BEAM;

    // header sizes
    
    public static final int nHeaderInts = 22;
    public static final int nFieldCodes = 12;
    public static final int nSpareInts = 9;

    public static final int nHeaderFloats = 12;
    public static final int nAnalogStatus = AnalogGroup.nChannels;
    public static final int nSpareFloats = 16;

    public static final int nStatusChars = 64;

    public static final int nwordsHeader = (nHeaderInts +
					    nFieldCodes +
					    nSpareInts +
					    nHeaderFloats +
					    nAnalogStatus + 
					    nSpareFloats);

    public static final int nbytesHeader = nwordsHeader * 4 + nStatusChars;

    // masks

    public static final int powerStatusMask1 = 0x01;
    public static final int powerStatusMask2 = 0x02;
    public static final int powerStatusMask3 = 0x04;
    public static final int powerStatusMask4 = 0x08;

    // position of fields in the header

    private static final int _nGatesIndex = 11;
    private static final int _nFieldsIndex = 12;
    private static final int _azIndex = 43;
    private static final int _elIndex = 44;

    // members
    
    private int _version;
    private int _struct_len;
    private int _countDataIncluded;
    private int _radar_id;
    private int _year;
    private int _month;
    private int _day;
    private int _hour;
    private int _min;
    private int _sec;
    private int _msec;
    private int _nGates;
    private int _nFields;
    private int _nSamples;
    private int _polarizationCode;
    private int _beamCount;
    private int _tiltCount;
    private int _endOfTiltFlag;
    private int _endOfVolFlag;
    private int _powerFlags;
    private int _statusFlags;
    private int _errorFlags;
    private int _fieldCodes[] = new int[nFieldCodes];
    private int _spareInts[] = new int[nSpareInts];

    private float _az;
    private float _el;
    private float _elTarget;
    private float _altKm;
    private float _latDeg;
    private float _latFracDeg;
    private float _lonDeg;
    private float _lonFracDeg;
    private float _gateSpacing;
    private float _startRange;
    private float _pulseWidth;
    private float _prf;
    private float _analogStatus[] = new float[nAnalogStatus];
    private float _spareFloats[] = new float[nSpareFloats];

    private String _dateString;
    private String _timeString;
    private String _dateTimeString;

    private String _statusString;

    private Date _date;
    private int _nCounts;
    private short _counts[] = null;
    private float _dbz[] = null;
    private float _snr[] = null;

    private ByteBuffer _hdrBuf = null;
    private ByteBuffer _countBuf = null;
    
    public BeamMessage() {
	_hdrBuf = ByteBuffer.allocate(nbytesHeader);
    }

    // disassemble header from byte buffer

    public void disassembleHeader() throws BufferUnderflowException {

	_hdrBuf.rewind();

	// direct from buffer

	_version = _hdrBuf.getInt();
	_struct_len = _hdrBuf.getInt();
	_countDataIncluded = _hdrBuf.getInt();
	_radar_id = _hdrBuf.getInt();
	_year = _hdrBuf.getInt();
	_month = _hdrBuf.getInt();
	_day = _hdrBuf.getInt();
	_hour = _hdrBuf.getInt();
	_min = _hdrBuf.getInt();
	_sec = _hdrBuf.getInt();
	_msec = _hdrBuf.getInt();
	_nGates = _hdrBuf.getInt();
	_nFields = _hdrBuf.getInt();
	_nSamples = _hdrBuf.getInt();
	_polarizationCode = _hdrBuf.getInt();
	_beamCount = _hdrBuf.getInt();
	_tiltCount = _hdrBuf.getInt();
	_endOfTiltFlag = _hdrBuf.getInt();
	_endOfVolFlag = _hdrBuf.getInt();
	_powerFlags = _hdrBuf.getInt();
	_statusFlags = _hdrBuf.getInt();
	for (int i = 0; i < nFieldCodes; i++) {
	    _fieldCodes[i] = _hdrBuf.getInt();
	}
	_errorFlags = _hdrBuf.getInt();
	for (int i = 0; i < nSpareInts; i++) {
	    _spareInts[i] = _hdrBuf.getInt();
	}
	
	_az = _hdrBuf.getFloat();
	_el = _hdrBuf.getFloat();
	_elTarget = _hdrBuf.getFloat();
	_altKm = _hdrBuf.getFloat();
	_latDeg = _hdrBuf.getFloat();
	_latFracDeg = _hdrBuf.getFloat();
	_lonDeg = _hdrBuf.getFloat();
	_lonFracDeg = _hdrBuf.getFloat();
	_gateSpacing = _hdrBuf.getFloat();
	_startRange = _hdrBuf.getFloat();
	_pulseWidth = _hdrBuf.getFloat();
	_prf = _hdrBuf.getFloat();
	for (int i = 0; i < nAnalogStatus; i++) {
	    _analogStatus[i] = _hdrBuf.getFloat();
	}
	for (int i = 0; i < nSpareFloats; i++) {
	    _spareFloats[i] = _hdrBuf.getFloat();
	}
	StringBuffer sbuf = new StringBuffer();
 	for (int i = 0; i < nStatusChars; i++) {
	    byte bb = _hdrBuf.get();
 	    if (bb == 0) {
 		break;
 	    }
	    if (bb != 0) {
		sbuf.append((char) bb);
	    }
 	}
 	_statusString = new String(sbuf);
	
	// derived

	_dateString = new
          String(NFormat.i4.format(_year) +
                 "/" +
                 NFormat.i2.format(_month) +
                 "/" + 
                 NFormat.i2.format(_day));
        
	_timeString = new
          String(NFormat.i2.format(_hour) +
                 ":" +
                 NFormat.i2.format(_min) +
                 ":" + 
                 NFormat.i2.format(_sec));
	
	_dateTimeString = new
          String(_dateString + " " + _timeString);
	
	_nCounts = _nFields * _nGates;
	
	Calendar cal = TimeManager.getCal();
	cal.set(_year, _month - 1, _day, _hour, _min, _sec);
	cal.set(Calendar.MILLISECOND, _msec);
	_date = cal.getTime();
	
	if (_params.verbose.getValue()) {
          System.out.println("Got beam, version, len, el, az, nGates: " +
                             _version + ", " + _struct_len + ", " +
			       _el + ", " + _az + ", " + _nGates);
          if (_statusString.length() > 0) {
            System.err.println("  status str: " + _statusString);
          }
	}
	
    }
    
    // disassemble selected parts of header from byte buffer
    // Only sets nGates, nFields, el, az

    public void disassembleHeaderSelected() throws BufferUnderflowException {
	
	_hdrBuf.rewind();

	_nGates = _hdrBuf.getInt(_nGatesIndex * 4);
	_nFields = _hdrBuf.getInt(_nFieldsIndex * 4);
	_nCounts = _nFields * _nGates;

	_az = _hdrBuf.getFloat(_azIndex * 4);
	_el = _hdrBuf.getFloat(_elIndex * 4);

    }

	// direct from buffer

    public void disassembleCounts() throws BufferUnderflowException {
	_counts = new short[_nCounts];
	_countBuf.rewind();
	for (int i = 0; i < _nCounts; i++) {
	    _counts[i] = _countBuf.getShort();
	}
    }
    
    public ByteBuffer getHeaderBuf() {
	return _hdrBuf;
    }

    public ByteBuffer getCountBuf() {
	if (_countBuf == null) {
	    _countBuf = ByteBuffer.allocate(_nCounts * 2);
	}
	return _countBuf;
    }

    public MessageType getType() {
	return _type;
    }
    
    public Date getDate() {
	return _date;
    }

    public int getYear() {
	return _year;
    }

    public int getMonth() {
	return _month;
    }

    public int getDay() {
	return _day;
    }

    public int getHour() {
	return _hour;
    }
    
    public int getMin() {
	return _min;
    }

    public int getSec() {
	return _sec;
    }

    public int getMsec() {
	return _msec;
    }

    public String getDateString() {
	return _dateString;
    }

    public String getTimeString() {
	return _timeString;
    }

    public String getDateTimeString() {
	return _dateTimeString;
    }

    public int getPowerStatus() {
      return _powerFlags;
    }
    
    public boolean getPowerStatus1() {
	return ((_powerFlags & powerStatusMask1) != 0);
    }
    
    public boolean getPowerStatus2() {
	return ((_powerFlags & powerStatusMask2) != 0);
    }
    
    public boolean getPowerStatus3() {
	return ((_powerFlags & powerStatusMask3) != 0);
    }

    public boolean getPowerStatus4() {
	return ((_powerFlags & powerStatusMask4) != 0);
    }

    public int getPowerFlags() {
	return _powerFlags;
    }

    public int getStatusFlags() {
	return _statusFlags;
    }

    public int getErrorFlags() {
	return _errorFlags;
    }

    public float[] getAnalogStatus() {
	return _analogStatus;
    }

    public double getEl() {
	return _el;
    }

    public double getAz() {
	return _az;
    }

    public double getStartRange() {
	return _startRange;
    }

    public double getGateSpacing() {
	return _gateSpacing;
    }
    
    public int getNGates() {
	return _nGates;
    }

    public double getPrf() {
	return _prf;
    }

    public String getStatusString() {
 	return _statusString;
    }

    public int getNCounts() {
	return _nCounts;
    }

    public short[] getCounts() {
	return _counts;
    }

    public float[] getDbz() {
	if (_counts == null) {
	    return null;
	}
	if (_dbz != null) {
	    return _dbz;
	}
	synchronized(this) {
	    _dbz = DbzArray.getInstance().computeDbz(_nGates, _startRange,
						     _gateSpacing, _counts);
	}
	return _dbz;
    }
    
    public float[] getSnr() {
	if (_counts == null) {
	    return null;
	}
	if (_snr != null) {
	    return _snr;
	}
	synchronized(this) {
	    _snr = DbzArray.getInstance().computeSnr(_nGates, _counts);
	}
	return _snr;
    }

}
