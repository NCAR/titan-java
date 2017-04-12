///////////////////////////////////////////////////////////////////////
//
// GunzipTest
//
// Mike Dixon
//
// April 2004
//
////////////////////////////////////////////////////////////////////////
package edu.ucar.rap.titan.RdasControl;

import java.io.*;
import java.util.zip.*;

class GunzipTest

{
    
    private boolean _debug= false;
    private String _inFileName = "junk.gz";
    private String _outFileName = "junk";
    
    public GunzipTest(String args[])
    {
	
	// parse command line
	
	parseArgs(args);
	
    }

    // print out usage and exit
    
    private void usage() {
        System.err.println("Usage: GunzipTest [options as below]");
        System.err.println("       [ -h, -help, -usage ] print usage");
        System.err.println("       [ -debug ] set debugging on");
        System.err.println("       [ -if ?] specify input filename");
        System.err.println("       [ -of ?] specify output filename");
        System.exit(1);
    }
    
    // parse command line
    
    void parseArgs(String[] args) {
	
	for (int i = 0; i < args.length; i++) {
	    if (args[i].equals("-debug")) {
		_debug = true;
	    } else if (args[i].equals("-h")) {
		usage();
	    } else if (args[i].equals("-help")) {
		usage();
	    } else if (args[i].equals("-usage")) {
		usage();
	    } else if (args[i].equals("-if")) {
		if (i == args.length - 1) {
		    System.err.println("ERROR - must specify input file name");
		    usage();
		} else {
		    _inFileName = args[++i];
		}
	    } else if (args[i].equals("-of")) {
		if (i == args.length - 1) {
		    System.err.println("ERROR - must specify output file name");
		    usage();
		} else {
		    _outFileName = args[++i];
		}
	    }
	} // i
	
    }

    // run

    int run() {
	
	if (_debug) {
	    System.out.println("Gunzip input file: " + _inFileName);
	    System.out.println(" into output file: " + _outFileName);
	}
	
	// get input file size

	File inFile = new File(_inFileName);
	if (!inFile.exists()) {
	    System.err.println("  Input file does not exist: " + _inFileName);
	    return -1;
	}
	int inLen = (int) inFile.length();
	if (_debug) {
	    System.err.println("Input file length: " + inLen);
	}

	// create an input stream on this file

	FileInputStream inStream = null;
	try {
	    inStream = new FileInputStream(inFile);
	} catch (FileNotFoundException e) {
	    System.err.println("  Input file does not exist: " + _inFileName);
	    e.printStackTrace();
	    return -1;
	}

	// open output file

	FileOutputStream outStream = null;
	try {
	    outStream = new FileOutputStream(_outFileName);
	} catch (FileNotFoundException e) {
	    System.err.println("  Cannot open output file: " + _outFileName);
	    e.printStackTrace();
	    return -1;
	}

	// read file into buffer

	byte[] gzBuf = new byte[inLen];
	try {
	    inStream.read(gzBuf);
	} catch (IOException e) {
	    System.err.println("  Cannot read input file into buffer: " + _inFileName);
	    e.printStackTrace();
	}

	// create byte array input stream from input buffer

	ByteArrayInputStream inByteStream = new ByteArrayInputStream(gzBuf);

	// create GZIP input stream from byte array stream

	GZIPInputStream inGzip;
	try {
	    inGzip = new GZIPInputStream(inByteStream);
	} catch (IOException e) {
	    e.printStackTrace();
	    return -1;
	}
	DataInputStream dis = new DataInputStream(inGzip);
	byte[] uncompBuf = new byte[7698];
	int nRead = 7698;
	try {
	    dis.readFully(uncompBuf);
	} catch (IOException e) {
	    e.printStackTrace();
	}
	if (_debug) {
	    System.out.println("Read uncompressed data: " + nRead);
	}
	
//  	int nRead = 0;
//  	int pos = 0;
//  	byte[] uncompBuf = new byte[10000];
//  	while (nRead >= 0) {
	    
//  	    // read data into uncompressed buffer
	    
//  	    try {
//  		nRead = inGzip.read(uncompBuf, pos, 10000 - pos);
//  	    } catch (IOException e) {
//  		e.printStackTrace();
//  		return -1;
//  	    }
//  	    if (nRead < 0) {
//  		break;
//  	    }
//  	    pos += nRead;
//  	    if (_debug) {
//  		System.out.println("Read uncompressed data: " + nRead);
//  	    }
	    
//  	} // while
	
	// write the buffer to the file
	
	try {
	    // outStream.write(uncompBuf, 0, pos);
	    outStream.write(uncompBuf, 0, nRead);
	} catch (IOException e) {
	    System.err.println("  Cannot write buf to output file: "
			       + _outFileName);
	    e.printStackTrace();
	    return -1;
	}

	return 0;

    }

    // main

    public static void main(String args[]) {
	// Create an instance of the application
	GunzipTest gtest = new GunzipTest(args);
	int iret = gtest.run();
	System.exit(iret);
    }

}


