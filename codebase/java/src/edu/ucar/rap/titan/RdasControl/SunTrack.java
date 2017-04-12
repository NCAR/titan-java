///////////////////////////////////////////////////////////////////////
//
// SunTrack
//
// Keeps track of the sun position
//
// Mike Dixon
//
// Nov 2002
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.util.*;
import javax.swing.*;

public class SunTrack extends Thread {

  private Parameters _params = Parameters.getInstance();
  private SimpleTimeZone _tz;
  private double _el, _az;
  private ArrayList _positionListeners = new ArrayList();
    
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
	
  }
    
  /**
   * add position change listener
   */
    
  public void addSunPosnListener(SunPosnListener listener) {
    _positionListeners.add(listener);
  }

  // provide run method

  public void run() {
	
    while (true) {
	    
      // get the time in UTC
      Calendar cal = Calendar.getInstance(_tz);

      // compute the sun position

      // computePosn(cal);
      computePosnNew(cal);

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

  ////////////////////////////////////////
  // compute sun position

  private void computePosn(Calendar cal) {

    _el = 0.0;
    _az = 0.0;

    // get the time now in msecs

    double now = cal.getTime().getTime();
	
    // get the time at the start of the year in msecs

    Calendar dayOne = (Calendar) cal.clone();
    dayOne.set(Calendar.MONTH, 0);
    dayOne.set(Calendar.DAY_OF_MONTH, 1);
    dayOne.set(Calendar.HOUR_OF_DAY, 0);
    dayOne.set(Calendar.MINUTE, 0);
    dayOne.set(Calendar.SECOND, 0);
    dayOne.set(Calendar.MILLISECOND, 0);
    double yearStart = dayOne.getTime().getTime();
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
      (gmt_hr + (_params.radar.longitude.getValue() / 15.0) + eqt);
	
    double hour_deg = solar_time * 15.0;
    double hour_rad = Math.toRadians(hour_deg);
	
    double lat = Math.toRadians(_params.radar.latitude.getValue());
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
      System.err.println("    jday: " + jday);
      System.err.println("    jdeg: " + jdeg);
      System.err.println("    gmt_hr: " + gmt_hr);
      System.err.println("    solar_time: " + solar_time);
      System.err.println("    hour_deg: " + hour_deg);
      System.err.println("    lat: " + lat);
      System.err.println("    el: " + el_deg);
      System.err.println("    az: " + az_adjusted);
    }

    _el = el_deg;
    _az = az_adjusted;

  }

  //////////////////////////////////////////////////////
  // compute sun position - using new method from Meeus
  // see libs/euclid/src/SunPosn

  private void computePosnNew(Calendar cal) {

    _el = 0.0;
    _az = 0.0;

    // get the time now in msecs

    double now = cal.getTime().getTime();
    double usecs = cal.getTime().getTime() / 1000.0;
    int year = cal.get(Calendar.YEAR);
	
    // get the time at the start of the year in msecs

    Calendar dayOne = (Calendar) cal.clone();
    dayOne.set(Calendar.MONTH, 0);
    dayOne.set(Calendar.DAY_OF_MONTH, 1);
    dayOne.set(Calendar.HOUR_OF_DAY, 0);
    dayOne.set(Calendar.MINUTE, 0);
    dayOne.set(Calendar.SECOND, 0);
    dayOne.set(Calendar.MILLISECOND, 0);
    double yearStart = dayOne.getTime().getTime();
    double jday = (now - yearStart) / 86400000.0;
	
    double Dtr = 0.017453292519943;
    double Rtd = 57.295779513082;
    double Pi = 3.1415926535898;
    double Pi2 = 2.0 * Pi;
    double Pio2 = Pi / 2.0;
    double Pi32 = Pi * (3.0/ 2.0);
    double Htr = 0.261799387799;
    
    //  LONGITUDE IN HOURS
    
    double lat = _params.radar.latitude.getValue();
    double lon = _params.radar.longitude.getValue();
    double S3 = (90.0 - lat) * Dtr;
    double Ourlon = lon / -15.0;
    
    // LOCAL TRUE TIME

    int Md = (int) jday + 1; // day in year
    int Myr = year - 1900; // year since 1900
    double Tgmt = fmod(usecs, 86400.0) / 3600; // hours

    double Tloc = Tgmt - Ourlon;
    if (Tloc >= 24) Tloc = Tloc - 24.0;
    if (Tloc < 0) Tloc = Tloc + 24.0;
    
    // TIME IN CENTURIES FROM 1900 JANUARY 0.5 OF GREENWITCH
    // MIDNIGHT OF THE DAY OF THE OBSERVATION
    
    int It = (int) (365.25 * (Myr - 1) + 365.0 + Md);
    double T = (It - 0.5) / 36525.0;
    
    // TIME OF OBSERVATION IN CENTURIES
    
    double Tp = T + Tgmt / 876600.0; // 876600 = hours per century
    
    // THE TIME DEPENDENT TERMS HAVE 2 FORMS GIVEN
    // THE LONG FORM IS THAT GIVEN BY MEEUS (USUALLY TAKEN FROM
    // THE AENA/AA)
    // THE SHORT FORM IS A LINEAR FIT AT 1985.0 (OR WAS IT 1982.0?)
    // OBLIQUITY OF THE ECLIPTIC
    
    double Obl = 23.452294 - (0.0130125 + (0.00000164 - 0.000000503 * Tp) * Tp) * Tp;
    // double Obl = 23.452294 - 0.0130135 * Tp;
    
    // ECCENTRICITY OF EARTH'S ORBIT - not used
    // CC ECS = 0.01675104 - 0.0000419 * TP
    // double Ecs = 0.01675104 - (0.0000418 + 0.000000126 * Tp) * Tp;
    
    // MEAN GECOENTRIC LONGITUDE OF THE SUN
    
    double Amb = (0.76892 + 0.0003025 * Tp) * Tp;
    double Ambd1 = 279.69668 + Amb + 36000.0 * Tp;
    double Ambda = fmod(Ambd1, 360.0);
    
    // MEAN ANNOMALLY OF THE SUN
    // CC ANOM = -0.95037 * TP
    double Anom = -(0.95025 + (0.00015 + 0.0000033 * Tp) * Tp) * Tp;
    double Ann = -1.52417 + Anom + 36000.0 * Tp;
    double Annom = fmod(Ann, 360.0);
    double Amr = Ambda * Dtr;
    double Anr = Annom * Dtr;
    
    // LONGITUDE OF MOON'S ASCENDING NODE
    // MAKE THE SOLUTION POSITIVE FOR THE NEXT FEW CENTURIES
    double Omeg = 2419.1833 - (1934.142 - 0.002078 * Tp) * Tp;
    Omeg = fmod(Omeg, 360.0);
    double Omega = Omeg * Dtr;
    
    // NUTATION TERMS
    double Sinom = 0.00479 * Math.sin(Omega);
    double Sinm = Math.sin(Anr);
    double S2l = Math.sin(2.0 * Amr);
    // double C2l = cos(2.0 * Amr);
    // double Snm = Math.sin(Anr);
    
    // MORE NUTATION
    Sinom = Sinom + 0.000354 * S2l;
    
    // EQUATION OF THE CENTER -- A SPECIALIZED FORM OF
    // KEPPLER'S EQUATION FOR THE SUN
    // CC   CENT = (-.001172 * SINM ** 2 + (1.920339 - .0048 * TP)) * SINM
    // CC 1 + 0.020012 * Sin(2.0 * ANR)
    // * CENT = (-.001172 * SINM ** 2 + (1.920339 -
    // * (4.789E-3 + 1.4E-5 * TP) * TP)) * SINM + (.020094 - 1.E-4 * TP) * SIN(2. * ANR)
    
    double Cent = ((-0.001172 * Sinm * Sinm +
                    (1.920339 - (0.004789 + 0.000014 * Tp) * Tp)) * 
                   Sinm + (0.020094 - 0.0001 * Tp) * Math.sin(2.0 * Anr));
    
    // CENTER = CENT * DTR
    double Solong = Ambda + Cent;
    
    // SIDEREAL TIME
    double Sid0 = (0.00002581 * T + 0.051262) * T + 6.6460656 + 2400.0 * T;
    double Sid = fmod(Sid0, 24.0);
    double Sidloc = Sid + 0.002737909 * Tgmt + Tloc;
    if (Sidloc >= 24) Sidloc = Sidloc - 24;
    
    // THE MAJOR PLANETARY PERTURBATIONS ON THE EARTH
    double Av = 153.23 + 22518.7541 * Tp;
    double Bv = 216.57 + 45037.5082 * Tp;
    double Cj = 312.69 + 32964.3577 * Tp;
    
    // CC DL = 267.113 * TP
    double Dl = (267.1142 - 0.00144 * Tp) * Tp;
    Dl = (Dl + 350.74) + 445000.0 * Tp;
    double Ec = 231.19 + 20.2 * Tp;
    double Aav = 0.00134 * Math.cos(Av * Dtr);
    double Bbv = 0.00154 * Math.cos(Bv * Dtr);
    double Ccj = 0.002 * Math.cos(Cj * Dtr);
    double Ddl = 0.00179 * Math.sin(Dl * Dtr);
    double Eec = 0.00178 * Math.sin(Ec * Dtr);
    double Corlo = Aav + Bbv + Ccj + Ddl + Eec;
    
    // APPARENT SOLAR LONGITUDE AND LOCAL SIDEREAL TIME
    double Solap = Solong - 0.00569 - Sinom + Corlo;
    double Sidap = Sidloc - Sinom * 0.061165;
    
    // IF(SIDAP .GE. 24.) SIDAP = SIDAP - 24.
    // IF(SIDAP .LT. 0.) SIDAP = SIDAP + 24.
    if (Sidap >= 24) Sidap = Sidap - 24;
    if (Sidap < 0) Sidap = Sidap + 24;
    
    // OBLIQUITY CORRECTED FOR NUTATION
    double Oblap = Obl + 0.00256 * Math.cos(Omega);
    double Solr = Solap * Dtr;
    
    // SIDR = sidap * Dtr
    double Oblr = Oblap * Dtr;
    
    // DECLINATION AND RIGHT ASCENSION
    double Rdec = Math.asin(Math.sin(Oblr) * Math.sin(Solr));
    double Ra = Math.atan(Math.cos(Oblr) * Math.tan(Solr));
    Ra = Ra * Rtd / 15.0;
    // double Dec = Rdec * Rtd;
    
    if (Solr > Pio2 && Solr < Pi32) Ra = Ra + 12;
    if (Ra < 0) Ra = Ra + 24;
    if (Ra >= 24) Ra = Ra - 24;
    
    // HOUR ANGLE FROM SIDEREAL TIME AND RA
    double Sha = Sidap - Ra;
    if (Sha > 12.0) Sha = Sha - 24;
    if (Sha <= -12.0) Sha = Sha + 24;
    
    double Rsha = Math.abs(Sha) * Htr;
    double S1 = Pio2 - Rdec;
    double A2 = Rsha;
    
    double Tnha2 = 1.0 / Math.tan(0.5 * A2);
    double Hds13 = 0.5 * (S1 - S3);
    double Hss13 = 0.5 * (S1 + S3);
    double Tnhda13 = Tnha2 * Math.sin(Hds13) / Math.sin(Hss13);
    double Tnhsa13 = Tnha2 * Math.cos(Hds13) / Math.cos(Hss13);
    double Hda13 = Math.atan(Tnhda13);
    double Hsa13 = Math.atan(Tnhsa13);
    
    if (Hsa13 < 0) Hsa13 = Hsa13 + Pi;
    double A1 = Hsa13 + Hda13;
    double A3 = Hsa13 - Hda13;
    
    double Sns2 = 0;
    if (Math.abs(Pio2 - A3) < Math.abs(Pio2 - A1)) {
      Sns2 = Math.sin(A2) * Math.sin(S3) / Math.sin(A3);
    } else {
      Sns2 = Math.sin(A2) * Math.sin(S1) / Math.sin(A1);
    }
    
    double Css2 = Math.cos(S1) * Math.cos(S3) + Math.sin(S1) * Math.sin(S3) * Math.cos(A2);
    
    double S2 = 0;
    if (Sns2 > 0.71) {
      S2 = Math.acos(Css2);
    } else {
      S2 = Math.asin(Sns2);
      if (Css2 < 0) S2 = Pi - S2;
    }
    
    double ZEN = S2 * Rtd;
    double azi = A1;
    
    if (Sha > 0) azi = Pi2 - A1;
    azi = azi * Rtd;
    
    _el = 90.0 - ZEN;
    _az = azi;

    if (_params.verbose.getValue()) {
      System.err.println("=== SUN POSN ===");
      System.err.println("    time: " + 
                         cal.get(Calendar.YEAR) + "/" +
                         cal.get(Calendar.MONTH) + "/" +
                         cal.get(Calendar.DAY_OF_MONTH) + " " +
                         cal.get(Calendar.HOUR_OF_DAY) + ":" +
                         cal.get(Calendar.MINUTE) + ":" +
                         cal.get(Calendar.SECOND));
      System.err.println("    lat: " + lat);
      System.err.println("    lon: " + lon);
      System.err.println("    sun el: " + _el);
      System.err.println("    sun az: " + _az);
    }

  }
    
  double fmod(double xx, double yy) {
    int nn = (int) (xx / yy);
    return (xx - nn * yy);
  }
   
}


