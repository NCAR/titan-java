///////////////////////////////////////////////////////////////////////
//
// Spdb
//
// Symbolic product data base
//
// Mike Dixon
//
// May 2005
//
///////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.util.*;
import java.nio.*;

public class Spdb

{

    // private members
    
    private static int _fileMajorVersion = 1;
    private static int _fileMinorVersion = 0;
    private static final String _indxExt = "indx";
    private static final String _dataExt = "data";

    private static final int _minsInDay = 1440;
    private static final int _secsInDay = 86400;
    private static final int _secsInMin = 60;

    // product info
    
    private static final int _labelMax = 64;
    private String _prodLabel = "unknown";
    private int _prodId = 0;

    // file names
    
    private String _dir;
    private String _path;
    private String _indxPath;
    private String _dataPath;
    private String _lockPath;
    
    // file header and chunk refs
    
    // private header_t _hdr;
    private ByteBuffer _hdrRefBuf;
    
    // get() chunk refs and data
    
    private boolean _getRefsOnly = false;
    private boolean _respectZeroTypes = false;
    private int _nGetChunks = 0;
    private ByteBuffer _getRefBuf;
    private ByteBuffer _getDataBuf;
    // private vector<chunk_t> _getChunks;
    
    // put attributes
    
    private final Enum PUT_OVER = new Enum("over");
    private final Enum PUT_ADD = new Enum("add");
    private final Enum PUT_ADD_UNIQUE = new Enum("add_unique");
    private final Enum PUT_ONCE = new Enum("once");
    private final Enum PUT_ERASE = new Enum("erase");

    private Enum _putMode = PUT_OVER;
    private int _nPutChunks = 0;
    private ByteBuffer _putRefBuf;
    private ByteBuffer _putDataBuf;
    private long _latestValidTimePut;

    // uniqueness

    private final Enum UNIQUE_OFF = new Enum("unique_off");
    private final Enum UNIQUE_LATEST = new Enum("unique_latest");
    private final Enum UNIQUE_EARLIEST = new Enum("unique_earliest");

    private Enum _getUnique = UNIQUE_OFF;

    // flags etc
    
    private boolean _locked;
    private boolean _emptyDay;
    private int _openDay;
    
    // open mode
    
    private final Enum OPEN_READ = new Enum("open_read");
    private final Enum OPEN_WRITE = new Enum("open_write");
    private Enum _openMode = OPEN_READ;

    // file ops
    
    private int _indxFd;
    private int _dataFd;

    //     private FILE *_indxFile;
    //     private FILE *_dataFile;
    //     private FILE *_lockFile;
    private boolean _filesOpen = false;
    
    // times from getTimes()
    
    private long _firstTime;
    private long _lastTime;
    private long _lastValidTime;
    
    // time list
    
    private ArrayList _timeList;
    
    // errors
    
    private String _errStr;
    
    // name of application
    
    private String _appName = "unknown";

    ////////////////////////////////////////////////////////////
    // Header values
    //

    private int _nChunks;
    
    // number of fragmented bytes - these
    // are 'lost' because during overwrite
    // the new chunk did not occupy exactly
    // the same space as the old chunk.
    private int _nbytesFrag;
    
    // number of bytes of usable data -
    // file size = nbytes_data + nbytes_frag
    private int _nbytesData;
    
    // max number of secs over which a product
    // is valid
    private int _maxDuration;
    
    private int _startOfDay;   // time of start of day for this file */
    private int _endOfDay;     // time of end of day for this file */
    
    private int _startValid;    // start valid time of data in this file */
    private int _endValid;      // end valid time of data in this file */
    
    // the earliest time for which there are valid
    // products during this day. If no products from
    // previous files overlap into this day, this
    // value will be the same as start_valid.
    // If products from previous days are valid 
    // during this day, this time be set to the
    // earliest valid time for those products */
    private int _earliestValid;
    
    // latest expire time for data in this file */
    private int _latestExpire;
    
    // see lead_time_storage_t above
    private int _leadTimeStorage;
    
    // posn of data closest to each minute
    private int[] _minutePosn = new int[_minsInDay];
	
    // constructor
    
    public Spdb() {
    }

    // set put mode

    public void setPutModeOver() { _putMode = PUT_OVER; }
    public void setPutModeAdd() { _putMode = PUT_ADD; }
    public void setPutModeAddUnique() { _putMode = PUT_ADD_UNIQUE; }
    public void setPutModeOnce() { _putMode = PUT_ONCE; }
    public void setPutModeErase() { _putMode = PUT_ERASE; }

    ///////////////////////////////////////////////////////////////////////
    // Type-safe enum class for internal enums
    
    private class Enum {
	public final String name;
	private Enum(String name) { this.name = name; }
	public String toString() { return name; }
    }

    ///////////////////////////////////////////////////////////////////////
    // Header

    private class Header {
	
	private static final int _headerSize = 6144;
	private static final int _nSpareInts = 66;
	
	// buffer representation

	private ByteBuffer _buf = null;

	///////////////////////////////////////////
	// constructor, initializes internal buffer
	
	public Header() {
	    
	    _buf = ByteBuffer.allocate(_headerSize);
	    for (int ii = 0; ii < _buf.capacity(); ii++) {
		_buf.put(ii, (byte) 0);
	    }

	}
	
	// assemble state into buffer

	public void assemble() {
	    
	    _putString(_prodLabel, 0, _labelMax, _buf);
	    
	    IntBuffer ibuf = _buf.asIntBuffer();
	    ibuf.put(16, _fileMajorVersion);
	    ibuf.put(17, _fileMinorVersion);
	    ibuf.put(18, _prodId);
	    ibuf.put(19, _nChunks);
	    ibuf.put(20, _nbytesFrag);
	    ibuf.put(21, _nbytesData);
	    ibuf.put(22, _maxDuration);
	    ibuf.put(23, _startOfDay);
	    ibuf.put(24, _endOfDay);
	    ibuf.put(25, _startValid);
	    ibuf.put(26, _endValid);
	    ibuf.put(27, _earliestValid);
	    ibuf.put(28, _latestExpire);
	    ibuf.put(29, _leadTimeStorage);
	    
	    for (int ii = 0; ii < _minsInDay; ii++) {
		ibuf.put(96 + ii, _minutePosn[ii]);
	    }
	    
	}
	
	// disassemble from buffer

	public void disassemble() {

	    _prodLabel = new String(_buf.array(), 0, _labelMax);
	    
	    IntBuffer ibuf = _buf.asIntBuffer();

	    _fileMajorVersion = ibuf.get(16);
	    _fileMinorVersion = ibuf.get(17);
	    _prodId = ibuf.get(18);
	    _nChunks = ibuf.get(19);
	    _nbytesFrag = ibuf.get(20);
	    _nbytesData = ibuf.get(21);
	    _maxDuration = ibuf.get(22);
	    _startOfDay = ibuf.get(23);
	    _endOfDay = ibuf.get(24);
	    _startValid = ibuf.get(25);
	    _endValid = ibuf.get(26);
	    _earliestValid = ibuf.get(27);
	    _latestExpire = ibuf.get(28);
	    _leadTimeStorage = ibuf.get(29);
	    
	    for (int ii = 0; ii < _minsInDay; ii++) {
		_minutePosn[ii] = ibuf.get(96 + ii);
	    }
	    
	}
	
	// get buffer

	public ByteBuffer getBuf() {
	    return _buf;
	}
	
	///////////////////////////////
	// put string into byte buffer
	
	private void _putString(String text,
				int startIndex, int maxLen,
				ByteBuffer bbuf) {  
	    byte[] textBytes = text.getBytes();
	    int len = text.length();
	    if (len > maxLen) {
		len = maxLen;
	    }
	    int index = startIndex;
	    for (int ii = 0; ii < len; ii++, index++) {
		if (index < bbuf.capacity()) {
		    bbuf.put(index, textBytes[ii]);
		}
	    }
	}
	
    } // class PmuMessage

}
