///////////////////////////////////////////////////////////////////////
//
// StatusData
//
// Mike Dixon
//
// April 2007
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.Rvp8Control;

import java.util.*;
import java.nio.*;
import java.io.PrintStream;

public class StatusData

{
    
  private Parameters _params = Parameters.getInstance();
  
  // members
    
  public Calendar time;
  public int year, month, day, hour, min, sec;
  public double el;
  public double az;
  public double azRate;

  public double prf;
  public double nGates;

  public double pulseWidthUs;
  public double dbzCal1km;
  public double ifdClockMhz;
  public double wavelengthCm;
  public double satPowerDbm;
  public double rangeMaskResKm;
  public double startRangeKm;
  public double maxRangeKm;
  public double gateSpacingKm;
  public double noiseChan0;
  public double noiseChan1;
  public double noiseSdevChan0;
  public double noiseSdevChan1;
  public double noiseRangeKm;
  public double noisePrfHz;

  public String siteName;
  public String majorMode;
  public String polarization;
  public String phaseCoding;
  public String prfMode;
  public String rdaVersion;

  public StatusData() {
  }

  // print to stderr

  public void print(PrintStream out) {

    out.println("year: " + year);
    out.println("month: " + month);
    out.println("day: " + day);
    out.println("hour: " + hour);
    out.println("min: " + min);
    out.println("sec: " + sec);
    out.println("el: " + el);
    out.println("az: " + az);
    out.println("prf: " + prf);
    out.println("nGates: " + nGates);
    out.println("pulseWidthUs: " + pulseWidthUs);
    out.println("dbzCal1km: " + dbzCal1km);
    out.println("ifdClockMhz: " + ifdClockMhz);
    out.println("wavelengthCm: " + wavelengthCm);
    out.println("satPowerDbm: " + satPowerDbm);
    out.println("rangeMaskResKm: " + rangeMaskResKm);
    out.println("startRangeKm: " + startRangeKm);
    out.println("maxRangeKm: " + maxRangeKm);
    out.println("gateSpacingKm: " + gateSpacingKm);
    out.println("noiseChan0: " + noiseChan0);
    out.println("noiseChan1: " + noiseChan1);
    out.println("noiseSdevChan0: " + noiseSdevChan0);
    out.println("noiseSdevChan1: " + noiseSdevChan1);
    out.println("noiseRangeKm: " + noiseRangeKm);
    out.println("noisePrfHz: " + noisePrfHz);
    out.println("siteName: " + siteName);
    out.println("majorMode: " + majorMode);
    out.println("polarization: " + polarization);
    out.println("phaseCoding: " + phaseCoding);
    out.println("prfMode: " + prfMode);
    out.println("rdaVersion: " + rdaVersion);

  }
    
}
