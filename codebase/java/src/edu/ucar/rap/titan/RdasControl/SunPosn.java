///////////////////////////////////////////////////////////////////////
//
// SunPosn
//
// Computes sun postion using NOVA routines
//
// Mike Dixon
//
// Sept 2007
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.util.*;
import javax.swing.*;

public class SunPosn extends Thread {

  private double _prevTime = 0.0;
  private double _lat = 0.0, _lon = 0.0, _alt_m = 0.0;
  private double _el = 0.0, _az = 0.0;
  private double _ra = 0, _declination = 0, _distanceAU = 0;
  private double _deltat = -0.45;
  private double _zenith_distance = 0;
  private double _azimuth = 0;
  private double _rar = 0, _decr = 0;
  private double _lighttime = 0;
  
  /**
   * constructor
   */
  
  private SunPosn() {
  }
    
  // set the lat/lon/alt for which sun position is computed
  // lat/lon in degrees, alt_m in meters
  
  public void setLocation(double lat, double lon, double alt_m)
    
  {
    _prevTime = 0.0;
    _lat = lat;
    _lon = lon;
    _alt_m = alt_m;
  }

  // compute sun position using NOVA routines
  
  public void computePosn(long now)
    
  {
    
    // set up site info
    
    double tempC = 20;
    double pressureMb = 1013;
    
    // set time
    
    // long now = TimeManager.getTime();
    long tnow = now / 1000;
    
    // compute sun posn
    
    _computePosAtTime(tnow);
    
  }

  private double fmod(double xx, double yy) {
    int nn = (int) (xx / yy);
    return (xx - nn * yy);
  }

  private class body {
    int _type;
    int _number;
    String _name;
    body(int type, int number, String name) {
      _type = type;
      _number = number;
      _name = name;
    }
  }
   
  private double _get_tdt_julian_date_at_time(long desired_time) {
    
    double epic2k=2451545.0;
    double secs_per_geoid = 86400.0;
    long ty2k = 946684800;
    double seconds_since_epic2k = desired_time - ty2k;
    double tdt_julian_date = epic2k + (seconds_since_epic2k/secs_per_geoid) - 0.5;
    return tdt_julian_date;

  }

  /*---------------------------------------------------------*/
  /**
   * This function uses the NOVAS software library to compute the position 
   * of the Sun on the local horizon, but for an input time, instead of the current time.
   *
   * --------------------------------------------------------
   *  
   * @param here (site_info)  The point on the surface of the earth we are at.
   * @param deltat (double)   The difference between terrestrial dynamical time system 1 time,
   * and universal time.
   * @param *SunAz (double *) The returned sun azimuth, in degrees from true north.
   * @param *SunEl (double *) The returned sun elevation, in degrees above the local horizon.
   * @param time_in (long)  The utc time in seconds that we need to compute the Sun's position.
   *
   * --------------------------------------------------------
   */
  private void _computePosAtTime(long time_in) {

    body earth = new body(0,3,"Earth");
    body sun = new body(0,10,"Sun");
    
    double tdt_julian_date = _get_tdt_julian_date_at_time(time_in);

    // Call NOVOS routine topo_planet to get the right ascension and declination 
    // of the Sun in topographic/equatorial coordinate system. */

    topo_planet(tdt_julian_date, sun, earth);
    
    // Call NOVAS subroutine equ2hor to convert the Sun's position to
    // local horizon coordinates.
    
    equ2hor(tdt_julian_date,
            0.0 /*x_ephemeris*/, 0.0 /*y_ephemeris*/,
            _ra, _declination, 1/*normal refraction*/);
    
    // Convert zenith_distance reference point so that the Sun's elevation
    // is referenced to the local horizon. */

    _el = 90.0 - _zenith_distance;
    
    // Azimuth needs no conversion, its local horizon reference is already true north.
    
    _az = _azimuth;

  }
  
  // topo_planet
  // sets _ra, _declination, _distanceAU

  private int topo_planet(double tjd, body ss_object, body earth) {
    
   int error = 0;
   int j;

   double ujd, t2, t3, gast;
   double pos1[] = new double[3], pos2[] = new double[3];
   double pos4[] = new double[3], pos5[] = new double[3];
   double pos6[] = new double[3], pos7[] = new double[3];
   double vel1[] = new double[3], vel2[] = new double[3];
   double pog[] = new double[3], vog[] = new double[3], pob[] = new double[3];
   double vob[] = new double[3], pos[] = new double[3];
   double peb[] = new double[3], veb[] = new double[3];
   double epes[] = new double[3], ves[] = new double[3];
   double tdb;
   double oblm, oblt, eqeq, psi, eps;

   /*
     Compute 'ujd', the UT1 Julian date corresponding to 'tjd'.
   */

   ujd = tjd - (_deltat / 86400.0);

   /*
     Compute position and velocity of the observer, on mean equator
     and equinox of J2000.0, wrt the solar system barycenter and
     wrt to the center of the Sun.
   */

//    error = get_earth (tjd, earth, tdb, peb, veb, pes, ves);
//    if (error != 0) {
//      _ra = 0.0;
//      _declination = 0.0;
//      _distanceAU = 0.0;
//      return (error + 10);
//    }

//    earthtilt (tdb, oblm, oblt, eqeq, psi, eps);

//    sidereal_time (ujd,0.0,eqeq, gast);
//    terra (location,gast, pos1,vel1);
//    nutate (tdb,FN1,pos1, pos2);
//    precession (tdb,pos2,T0, pog);

//    nutate (tdb,FN1,vel1, vel2);
//    precession (tdb,vel2,T0, vog);

//    for (j = 0; j < 3; j++)
//    {
//       pob[j] = peb[j] + pog[j];
//       vob[j] = veb[j] + vog[j];
//       pos[j] = pes[j] + pog[j];
//    }

   /*
     Compute the apparent place of the planet using the position and
     velocity of the observer.
     
     Get position of planet wrt barycenter of solar system.
   */
   
//    error = ephemeris (tdb,ss_object,BARYC, pos1,vel1);
//    if (error != 0) {
//      _ra = 0.0;
//      _declination = 0.0;
//      _distanceAU = 0.0;
//      return error;
//    }

//    bary_to_geo (pos1,pob, pos2);
//    _distanceAU = _lighttime * C;
//    t3 = tdb - _lighttime;
   
//    do
//    {
//       t2 = t3;

//       if ((error = ephemeris (t2,ss_object,BARYC, pos1,vel1)))
//       {
//         _ra = 0.0;
//         _declination = 0.0;
//         _distanceAU = 0.0;
//          return error;
//       }
//       bary_to_geo (pos1,pob, pos2);
//       t3 = tdb - _lighttime;

//    } while (fabs (t3-t2) > 1.0e-8);

/*
   Finish topocentric place.
*/

//    sun_field (pos2,pos, pos4);
//    aberration (pos4,vob,_lighttime, pos5);
//    precession (T0,pos5,tdb, pos6);
//    nutate (tdb,FN0,pos6, pos7);
//    vector2radec (pos7, ra,dec);

    return 0;
    
  }

  // equ2hor
  // sets _deltat, _zenith_distance, _azimuth, _rar, _decr

  private void equ2hor (double tjd, double x, double y, 
                        double ra, double dec, 
                        int ref_option) {
    
  }

}


