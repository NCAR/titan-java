///////////////////////////////////////////////////////////////////////
//
// SunTrack
//
// Keeps track of the sun position
//
// Mike Dixon
//
// May 2013
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.DowControl;

import java.util.*;
import javax.swing.*;

public class SunTrack extends Thread  implements StatusDataListener 

{

  Runnable updateControlPanel;
  private Parameters _params = Parameters.getInstance();
  private SimpleTimeZone _tz;
  private double _el, _az;
  private ArrayList<SunPosnListener> _positionListeners =
    new ArrayList<SunPosnListener>();
  private StatusData _status;

  /**
   * Single instance for this class - it is a singleton
   */
    
  private static final SunTrack _instance = new SunTrack();

  /**
   * get singleton instance
   */
    
  public static SunTrack getInstance() {
    return _instance;
  }
    
  /**
   * private constructor
   */
    
  private SunTrack() {

    _tz = new SimpleTimeZone(0, "UTC");
    _el = -99.0;
    _az = -99.0;
	
    // add listener for new status data
    
    StatusDataHandler.getInstance().addListener(this);
	
  }
    
  /**
   * add position change listener
   */
    
  public void addSunPosnListener(SunPosnListener listener) {
    _positionListeners.add(listener);
  }

  // handle new status
    
  public void handleStatus(StatusData status) {
    _status = status;
  }
  
  // provide run method

  public void run() {
	
    while (true) {
	    
      // get the time in UTC
      Calendar cal = Calendar.getInstance(_tz);

      // compute the sun position

      computePosn(cal);

      // update listeners
	    
      for (Iterator ii = _positionListeners.iterator(); ii.hasNext();) {
        SunPosnListener listener = (SunPosnListener) ii.next();
        listener.setSunPosn(_el, _az);
      }
	    
      // sleep for a second
	    
      Thread t = Thread.currentThread();
      try { t.sleep(1000); }
      catch (InterruptedException e) { return; }
	    
    } // while
	
  }
    
  // compute sun position

  private void computePosn(Calendar cal) {

    if (_status == null) {
      return;
    }

    _el = 0.0;
    _az = 0.0;

    // get the time now in msecs

    double now = _status.gps_time.time.getTime().getTime();
	
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

    double lonDeg = _status.longitude_deg;
    double latDeg = _status.latitude_deg;

    double solar_time = (gmt_hr + (lonDeg / 15.0) + eqt);
	
    double hour_deg = solar_time * 15.0;
    double hour_rad = Math.toRadians(hour_deg);
	
    double lat = Math.toRadians(latDeg);
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
	
    if (_params.verbose.getValue()) {
      System.err.println("========================================");
      System.err.println("SunTrack: ----->>");
      System.err.println("    jday: " + jday);
      System.err.println("    jdeg: " + jdeg);
      System.err.println("    gmt_hr: " + gmt_hr);
      System.err.println("    solar_time: " + solar_time);
      System.err.println("    hour_deg: " + hour_deg);
      System.err.println("    lat: " + lat);
      System.err.println("    el: " + el_deg);
      System.err.println("    az: " + az_adjusted);
      System.err.println("========================================");
    }

    _el = el_deg;
    _az = az_adjusted;

  }
    
  double fmod(double xx, double yy) {
    int nn = (int) (xx / yy);
    return (xx - nn * yy);
  }
   
}


