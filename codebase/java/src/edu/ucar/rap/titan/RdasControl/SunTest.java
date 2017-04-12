//////////////////////////////////////////////////////////////////////
//
// SunTest
//
// Mike Dixon
//
// Nov 2004
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.util.*;
import java.lang.Math;

class SunTest

{
    
    private boolean _debug= false;
    private boolean _verbose = false;
    private double _radarLat = 40.0;
    private double _radarLon = -105.0;
    
    public SunTest(String args[])
    {
	
	// parse command line
	
	parseArgs(args);

	SimpleTimeZone tz = new SimpleTimeZone(0, "UTC");
	Calendar cal = Calendar.getInstance(tz);
	
	for (int ihour = 0; ihour < 24; ihour++) {

	    cal.set(Calendar.YEAR, 2004);
	    cal.set(Calendar.MONTH, 9);
	    cal.set(Calendar.DAY_OF_MONTH, 31);
	    cal.set(Calendar.HOUR_OF_DAY, ihour);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);

	    computePosn(cal, ihour);

	}

    }

    // print out usage and exit
    
    private void usage() {
        System.err.println("Usage: SunTest [-opts as below]");
        System.err.println("       [ -h, -help, -usage ] print usage");
        System.err.println("       [ -debug ] set debugging on");
        System.err.println("       [ -verbose ] set verbose debugging on");
        System.exit(1);
    }
    
    // parse command line
    
    void parseArgs(String[] args) {
	
	for (int i = 0; i < args.length; i++) {
	    if (args[i].equals("-debug")) {
		_debug = true;
	    } else if (args[i].equals("-verbose")) {
		_verbose = true;
	    } else if (args[i].equals("-h")) {
		usage();
	    } else if (args[i].equals("-help")) {
		usage();
	    } else if (args[i].equals("-usage")) {
		usage();
	    }
	} // i
	
    }

    // main
    public static void main(String args[]) {
	// Create an instance of the application
	SunTest mainFrame = new SunTest(args);
    }

    // compute sun position

    private void computePosn(Calendar cal, int ihour) {

	// get the time now in msecs

	double now = cal.getTime().getTime();
	
	// get the time at the start of the year in msecs

	cal.set(Calendar.MONTH, 0);
	cal.set(Calendar.DAY_OF_MONTH, 1);
	cal.set(Calendar.HOUR_OF_DAY, 0);
	cal.set(Calendar.MINUTE, 0);
	cal.set(Calendar.SECOND, 0);
	cal.set(Calendar.MILLISECOND, 0);
	double yearStart = cal.getTime().getTime();
	double jday = (now - yearStart) / 86400000.0;
	double jdeg = jday * (360.0 / 365.25);
	
	double eqt = 0.123 * Math.cos(Math.toRadians(jdeg + 87.0)) -
	    0.16666667 * Math.sin(Math.toRadians(jdeg + 10.0) * 2.0);
	
	double decl =
	    -23.5 * Math.cos(Math.toRadians((jday + 10.3) * (360.0 / 365.25)));
	double decl_rad = Math.toRadians(decl);
	
	double hr = now / 3600000.0;
	double gmt_hr = fmod((hr - 12.0), 24.0);

	double solar_time =
	    (gmt_hr + (_radarLon / 15.0) + eqt);
	
	double hour_deg = solar_time * 15.0;
	double hour_rad = Math.toRadians(hour_deg);
	
	double lat = Math.toRadians(_radarLat);
	double sin_el = Math.sin(lat) * Math.sin(decl_rad) +
	    Math.cos(lat) * Math.cos(decl_rad) * Math.cos(hour_rad);
	double el = Math.asin(sin_el);
	double el_deg = Math.toDegrees(el);
	
	double tan_az = (Math.sin(hour_rad) /
			 ((Math.cos(hour_rad) * Math.sin(lat)) -
			  (Math.tan(decl_rad) * Math.cos(lat))));
	
	double az = Math.atan(tan_az);
	double az_deg = Math.toDegrees(az);

	double az_adjusted = az_deg;
	
 	if (hour_deg >= 0.0 && hour_deg < 180.0) {
 	    if (az_adjusted > 0.0) {
 		az_adjusted += 180.0;
 	    } else {
 		az_adjusted += 360.0;
 	    }
 	} else {
 	    if (az_adjusted < 0.0) {
 		az_adjusted += 180.0;
 	    }
 	}
	
	if (_verbose) {
	    System.err.println("    jday: " + jday);
	    System.err.println("    jdeg: " + jdeg);
	    System.err.println("    gmt_hr: " + gmt_hr);
	    System.err.println("    solar_time: " + solar_time);
	    System.err.println("    hour_deg: " + hour_deg);
	    System.err.println("    lat: " + lat);
	    System.err.println("    el: " + el_deg);
	    System.err.println("    az: " + az_deg);
	}

	System.err.println("----------------------------------");

	System.err.println("hour, az, el: "
			   + ihour + ", "
			   + NFormat.f3.format(az_adjusted) + ", "
			   + NFormat.f3.format(el_deg));

	System.err.println("      hour_deg, az_deg, az_adjusted: "
			   + NFormat.f3.format(hour_deg) + ", "
			   + NFormat.f3.format(az_deg) + ", "
			   + NFormat.f3.format(az_adjusted));

	System.err.println("      jday, jdeg, decl, eqt(hr/sec): "
			   + NFormat.f3.format(jday) + ", "
			   + NFormat.f3.format(jdeg) + ", "
			   + NFormat.f3.format(decl) + ", "
			   + NFormat.f3.format(eqt) + "/"
			   + (int) (eqt * 3600.0 + 0.5));

	System.err.println("      hr, gmt_hr, solar_time, lat: "
			   + NFormat.f3.format(hr) + ", "
			   + NFormat.f3.format(gmt_hr) + ", "
			   + NFormat.f3.format(solar_time) + ", "
			   + NFormat.f3.format(lat));
	
    }
    
    double fmod(double xx, double yy) {
	int nn = (int) (xx / yy);
	return (xx - nn * yy);
    }
   
}
