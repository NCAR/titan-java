///////////////////////////////////////////////////////////////////////
//
// StatusData
//
// Mike Dixon
//
// May 2013
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RapidDowControl;

import java.util.*;
import java.nio.*;
import java.io.PrintStream;

public class StatusData

{
    
  private Parameters _params = Parameters.getInstance();
  
  // members
    
  public String params_dir;
  public String params_file_name = new String();
  public String radar_name;
  public DateTime gps_time = new DateTime();
  
  public double latitude_deg = -9999;
  public double longitude_deg = -9999;
  public double altitude_m = -9999;

  public boolean prt_is_staggered;
  public double prt1_sec;
  public double prt2_sec;
  public double prt_ratio;
  
  public double pulse_width_sec;
  public double tx_envelope_width_sec;
  public double duty_cycle;

  public int n_gates;
  public double rx_gate_spacing_sec;
  public double rx_gate_spacing_m;

  public PentekData pentek1 = new PentekData();
  public PentekData pentek2 = new PentekData();
  
  public DateTime ray_time = new DateTime();
  public long ray_nano_secs;
  
  public int volume_number;
  public int sweep_number;
  public String sweep_mode = new String();

  public double elevation_deg;
  public double azimuth_deg;
  public double scan_rate_deg_per_sec;
  public double fixed_angle_deg;

  public StatusData()
  {

    elevation_deg = -9999;
    azimuth_deg = -9999;
    fixed_angle_deg = -9999;
    scan_rate_deg_per_sec = 0;
    volume_number = 0;
    sweep_number = 0;

  }

  // printing

  public void print(PrintStream out) {

    out.println("params_dir: " + params_dir);
    out.println("params_file_name: " + params_file_name);
    out.println("radar_name: " + radar_name);
    out.print("gps_time: ");
    gps_time.println(out, "");

    out.println("latitude_deg: " + latitude_deg);
    out.println("longitude_deg: " + longitude_deg);
    out.println("altitude_m: " + altitude_m);
    
    out.println("prt_is_staggered: " + prt_is_staggered);
    out.println("prt1_sec: " + prt1_sec);
    out.println("prt2_sec: " + prt2_sec);
    out.println("prt_ratio: " + prt_ratio);
    
    out.println("pulse_width_sec: " + pulse_width_sec);
    out.println("tx_envelope_width_sec: " + tx_envelope_width_sec);
    out.println("duty_cycle: " + duty_cycle);

    out.println("n_gates: " + n_gates);
    out.println("rx_gate_spacing_sec: " + rx_gate_spacing_sec);
    out.println("rx_gate_spacing_m: " + rx_gate_spacing_m);

    out.print("ray_time: ");
    ray_time.println(out, "");
    out.println("ray_nano_secs: " + ray_nano_secs);

    out.println("volume_number: " + volume_number);
    out.println("sweep_number: " + sweep_number);
    out.println("sweep_mode: " + sweep_mode);
    out.println("elevation_deg: " + elevation_deg);
    out.println("azimuth_deg: " + azimuth_deg);
    out.println("fixed_angle_deg: " + fixed_angle_deg);
    out.println("scan_rate_deg_per_sec: " + scan_rate_deg_per_sec);
    
    if (pentek1 != null) {
      out.println("Pentek1:");
      pentek1.print(out, "  ");
    }

    if (pentek2 != null) {
      out.println("Pentek2:");
      pentek2.print(out, "  ");
    }

  }

  // inner class for Pentek Data

  public class PentekData
  {
    
    public DateTime start_time;
    public DateTime latest_time;
    
    public long pulse_seq_num;
    public long pulse_count;
    public int n_gates;

    public double fpga_temp_C;
    public double circuit_board_temp_C;

    public double chan_0_freq_hz;
    public double chan_1_freq_hz;
    public double chan_2_freq_hz;

    public PentekData() {
      start_time = new DateTime();
      latest_time = new DateTime();
    }
    
    // printing
    
    public void print(PrintStream out, String spacer) {

      out.print(spacer + "start_time: ");
      start_time.println(out, "");

      out.print(spacer + "latest_time: ");
      latest_time.println(out, "");
      
      out.println(spacer + "pulse_seq_num: " + pulse_seq_num);
      out.println(spacer + "pulse_count: " + pulse_count);
      out.println(spacer + "n_gates: " + n_gates);

      out.println(spacer + "fpga_temp_C: " + fpga_temp_C);
      out.println(spacer + "circuit_board_temp_C: " + circuit_board_temp_C);

      out.println(spacer + "chan_0_freq_hz: " + chan_0_freq_hz);
      out.println(spacer + "chan_1_freq_hz: " + chan_1_freq_hz);
      out.println(spacer + "chan_2_freq_hz: " + chan_2_freq_hz);

    }
      
  } // Pentek class

  // inner class for handling date/time and decoding ISO string

  public class DateTime
  {
    
    public Calendar time;
    public int year, month, day, hour, min, sec;
    public String iso_string;
    
    public DateTime() {
      SimpleTimeZone tz = new SimpleTimeZone(0, "UTC");
      time = Calendar.getInstance(tz);
      year = -9999;
      month = -99;
      day = -99;
      hour = -99;
      min = -99;
      sec = -99;
    }
    
    public void decodeIso(String text) {
      
      iso_string = text;
      String[] parts = iso_string.split("\\D");
      try {
        year = Integer.parseInt(parts[0]);
        month = Integer.parseInt(parts[1]);
        day = Integer.parseInt(parts[2]);
        hour = Integer.parseInt(parts[3]);
        min = Integer.parseInt(parts[4]);
        sec = Integer.parseInt(parts[5]);
        SimpleTimeZone tz = new SimpleTimeZone(0, "UTC");
        time = Calendar.getInstance(tz);
        time.set(year, month, day, hour, min, sec);
      }
      catch (NumberFormatException e) {
        year = -9999;
        month = -99;
        day = -99;
        hour = -99;
        min = -99;
        sec = -99;
      }
      
    }

    public void print(PrintStream out, String spacer) {
      
      String dateString = new
        String(NFormat.i4.format(year) +
               "/" +
               NFormat.i2.format(month) +
               "/" + 
               NFormat.i2.format(day));
      
      String timeString = new
        String(NFormat.i2.format(hour) +
               ":" +
               NFormat.i2.format(min) +
               ":" + 
               NFormat.i2.format(sec));

      out.print(spacer);
      out.print(dateString);
      out.print(" ");
      out.print(timeString);
      
    }
      
    public void println(PrintStream out, String spacer) {
      
      print(out, spacer);
      out.println("");
      
    }
      
  } // DateTime class

}
