///////////////////////////////////////////////////////////////////////
//
// GetStatus
//
// Mike Dixon
//
// May 2013
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.DowControl;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.lang.Math;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import edu.ucar.rap.jrp.*;

public class GetStatus extends JPanel {

  private Parameters _params = Parameters.getInstance();
  private String _statusStr = new String();
  private String _rayInfoStr = new String();

  // ray info

  private String _rayTimeStr = new String();
  private long _rayNanoSecs;
  
  private int _volumeNumber;
  private int _sweepNumber;
  private String _sweepMode = new String();

  private double _elevationDeg;
  private double _azimuthDeg;
  private double _scanRateDegPerSec;
  private double _fixedAngleDeg;

  int _intervalMsecs =
    (int) (_params.status.intervalSecs.getValue() * 1000.0 + 0.5);

  public GetStatus() {
	
    // create the ticker thread, start it
    
    Ticker ticker = new Ticker();
    ticker.start();
    
    // create the getRay thread, start it
    
    GetRay getRay = new GetRay();
    getRay.start();
    
  }
	
  // ticker thread
    
  private class Ticker extends Thread {

    public void run() {

      while (true) {

        // get status

        if (_getStatus() != 0) {
          _sleep();
          continue;
        }

        // update the status
        
        StatusData statusData = new StatusData();
        if (_updateStatus(statusData) != 0) {
          _sleep();
          continue;
        }
        
        // update with the latest ray info
        
        _copyRayInfoToStatus(statusData);

        // put the status onto the queue
        
        StatusQueue queue = StatusQueue.getInstance();
        queue.push(statusData);

        // sleep a bit

        _sleep();

      } // while

    } // run()

  } // Ticker

  // getRay thread
    
  private class GetRay extends Thread {
    
    public void run() {

      while (true) {
        
        // get ray info
        
        if (_getRayInfo() == 0) {
          // update ray info
          _updateRayInfo();
        }

        // sleep a bit

        _sleep();

      } // while

    } // run()

  } // GetRay

  /////////////////////////////////////////
  // sleep a bit

  private void _sleep()
  {
    Thread t = Thread.currentThread();
    try { t.sleep(_intervalMsecs); }
    catch (InterruptedException e) { return; }
  }

  /////////////////////////////////////////
  // request status from DRX
  //
  // Loads _statusStr

  private int _getStatus() {
    
    StringBuffer xml = new StringBuffer();
    
    xml.append("<DowGuiRequest>\n");
    xml.append("  <action>SendStatus</action>\n");
    xml.append("</DowGuiRequest>\n");
    xml.append("\r\n\r\n");
    
    if (_params.verbose.getValue()) {
      System.err.println("Requesting status from DRX");
    }
    
    SocketComms sock = new SocketComms();
    _statusStr = "";
    try {
      _statusStr = sock.communicate(xml.toString(),
                                    _params.comms.drxHost.getValue(),
                                    _params.comms.drxPort.getValue());
    }
    catch (IOException e) {
      String error =
        "ERROR requesting status from DRX\n" + e.toString();
      System.err.println(error);
      System.err.println("ERROR reading status from DRX");
      System.err.println("  host: " + _params.comms.drxHost.getValue());
      System.err.println("  port: " + _params.comms.drxPort.getValue());
      return -1;
    }
    
    if (_checkReplyForErrors(_statusStr) != 0) {
      return -1;
    }

    return 0;
    
  }

  ///////////////////////////////////////
  // request ray information from MGEN
  //
  // loads _rayInfoStr

  private int _getRayInfo() {
    
    StringBuffer xml = new StringBuffer();

    xml.append("<RayInfoRequest>\n");
    xml.append("  <action>SendStatus</action>\n");
    xml.append("</RayInfoRequest>\n");
    xml.append("\r\n\r\n");
    
    if (_params.verbose.getValue()) {
      System.err.println("Requesting ray info from RadxMon server");
    }
    
    SocketComms sock = new SocketComms();
    _rayInfoStr = "";
    try {
      _rayInfoStr = sock.communicate(xml.toString(),
                                     _params.comms.rayHost.getValue(),
                                     _params.comms.rayPort.getValue());
    }
    catch (IOException e) {
      String error =
        "ERROR requesting ray info from RadxMon server\n" + e.toString();
      System.err.println(error);
      System.err.println("ERROR reading ray info");
      System.err.println("  host: " + _params.comms.rayHost.getValue());
      System.err.println("  port: " + _params.comms.rayPort.getValue());
      return -1;
    }
    
    if (_checkReplyForErrors(_rayInfoStr) != 0) {
      return -1;
    }

    return 0;
    
  }
    
  /////////////////////////////
  // check the reply for errors
  // returns 0 for no errors, -1 if error

  private int _checkReplyForErrors(String reply) {
    
    SAXBuilder builder = new SAXBuilder();
    Document doc;

    try {
      doc = builder.build(new StringReader(reply));
    }
    catch (JDOMException e) { 
      // indicates a well-formedness error
      String error = "Cannot parse status XML: " + reply + "\n";
      System.err.println(error);
      return -1;
    }
    catch (IOException e) { 
      // indicates a read error
      String error = "Cannot read status XML: " + reply + "\n";
      System.err.println(error);
      return -1;
    }
    
    Element root = doc.getRootElement();
    Iterator itRoot = root.getChildren().iterator();
    while (itRoot.hasNext()) {
      Element el = (Element) itRoot.next();
      String elName = el.getName();
      if (elName.equals("error")) {
        String error = "XML ERROR\n" + el.getText() + "\n";
        System.err.println(error);
        return -1;
      }
    }

    return 0;

  }

  /////////////////////////////////////////////
  // Update status
  
  private int _updateStatus(StatusData status)

  {
    
    SAXBuilder builder = new SAXBuilder();
    Document doc;

    try {
      doc = builder.build(new StringReader(_statusStr));
    }
    catch (JDOMException e) { 
      return -1;
    }
    catch (IOException e) { 
      return -1;
    }

    // load from XML

    _loadStatusFromXmlDoc(doc, status);
    
    if (_params.verbose.getValue()) {
      System.err.println("========================================");
      System.err.println("Got status from DRX: ----->>");
      status.print(System.err);
      System.err.println("========================================");
    }
    
    return 0;

  }

  /////////////////////////////////////////////
  // Load status data from XML document
  
  private void _loadStatusFromXmlDoc(Document doc,
                                     StatusData status) {

    double val;
    Element root = doc.getRootElement();
    if (root.getName().equals("DowDrxReply")) {
      Iterator itRoot = root.getChildren().iterator();
      while (itRoot.hasNext()) {
        Element elc = (Element) itRoot.next();
        if (elc.getName().equals("Status")) {
          Iterator itc = elc.getChildren().iterator();
          while (itc.hasNext()) {
            
            Element el = (Element) itc.next();
            String name = new String(el.getName());
            String text = new String(el.getText());

            if (name.equals("PentekHigh")) {
              _loadPentekStatus(status.pentekHigh, el);
            } else if (name.equals("PentekLow")) {
              _loadPentekStatus(status.pentekLow, el);
            }

            if (name.equals("params_dir")) {
              status.params_dir = text;
            }
            if (name.equals("params_file_name")) {
              status.params_file_name = text;
            }
            if (name.equals("radar_name")) {
              status.radar_name = text;
            }
            if (name.equals("gps_time")) {
              status.gps_time.decodeIso(text);
            }
            
            if (name.equals("latitude_deg")) {
              status.latitude_deg = _getDoubleFromStr(name, text);
            }
            if (name.equals("longitude_deg")) {
              status.longitude_deg = _getDoubleFromStr(name, text);
            }
            if (name.equals("altitude_m")) {
              status.altitude_m = _getDoubleFromStr(name, text);
            }
            if (name.equals("prt1_sec")) {
              status.prt1_sec = _getDoubleFromStr(name, text);
            }
            if (name.equals("prt2_sec")) {
              status.prt2_sec = _getDoubleFromStr(name, text);
            }
            if (name.equals("prt_ratio")) {
              status.prt_ratio = _getDoubleFromStr(name, text);
            }
            if (name.equals("prt_is_staggered")) {
              status.prt_is_staggered = _getBooleanFromStr(text);
            }
            if (name.equals("nominal_pulse_width_sec")) {
              status.nominal_pulse_width_sec = _getDoubleFromStr(name, text);
            }
            if (name.equals("n_gates")) {
              status.n_gates = _getIntFromStr(name, text);
            }
            if (name.equals("rx_gate_spacing_sec")) {
              status.rx_gate_spacing_sec = _getDoubleFromStr(name, text);
            }
            if (name.equals("rx_gate_spacing_m")) {
              status.rx_gate_spacing_m = _getDoubleFromStr(name, text);
            }

          } // itc.hasNext()

        } // elc

      } // itroot

    } // root

  }

  /////////////////////////////////////////////
  // Load pentek status
  
  private void _loadPentekStatus(StatusData.PentekData pentekData,
                                 Element pentekRoot) 
  {

    double val;
    Iterator itp = pentekRoot.getChildren().iterator();

    while (itp.hasNext()) {

      Element elp = (Element) itp.next();
      String name = new String(elp.getName());
      String text = new String(elp.getText());

      if (elp.getName().equals("start_time")) {
        pentekData.start_time.decodeIso(text);
      }
      if (elp.getName().equals("latest_time")) {
        pentekData.latest_time.decodeIso(text);
      }

      if (name.equals("pulse_seq_num")) {
        pentekData.pulse_seq_num = _getLongFromStr(name, text);
      }
      if (name.equals("pulse_count")) {
        pentekData.pulse_count = _getLongFromStr(name, text);
      }
      if (name.equals("n_gates")) {
        pentekData.n_gates = _getIntFromStr(name, text);
      }

      if (name.equals("fpga_temp_C")) {
        pentekData.fpga_temp_C = _getDoubleFromStr(name, text);
      }

      if (name.equals("circuit_board_temp_C")) {
        pentekData.circuit_board_temp_C = _getDoubleFromStr(name, text);
      }
      if (name.equals("g0_power_mean_dbm")) {
        pentekData.g0_power_mean_dbm = _getDoubleFromStr(name, text);
      }
      if (name.equals("g0_power_max_dbm")) {
        pentekData.g0_power_max_dbm = _getDoubleFromStr(name, text);
      }
      if (name.equals("g0_power_p90_dbm")) {
        pentekData.g0_power_p90_dbm = _getDoubleFromStr(name, text);
      }
      if (name.equals("g0_pulse_width_sec")) {
        pentekData.g0_pulse_width_sec = _getDoubleFromStr(name, text);
      }
      if (name.equals("duty_cycle")) {
        pentekData.duty_cycle = _getDoubleFromStr(name, text);
      }
      if (name.equals("g0_start_missing")) {
        pentekData.g0_start_missing = _getBooleanFromStr(text);
      }
      if (name.equals("g0_end_missing")) {
        pentekData.g0_end_missing = _getBooleanFromStr(text);
      }
      if (name.equals("g0_if2_freq_hz")) {
        pentekData.g0_if2_freq_hz = _getDoubleFromStr(name, text);
      }
      if (name.equals("afc_est_if1_hz")) {
        pentekData.afc_est_if1_hz = _getDoubleFromStr(name, text);
      }
      if (name.equals("afc_est_rf_hz")) {
        pentekData.afc_est_rf_hz = _getDoubleFromStr(name, text);
      }
      if (name.equals("nominal_rf_hz")) {
        pentekData.nominal_rf_hz = _getDoubleFromStr(name, text);
      }
      if (name.equals("afc_is_tracking")) {
        pentekData.afc_is_tracking = _getBooleanFromStr(text);
      }

    } // while (itp.hasNext())

  } // _loadPentekStatus()


  /////////////////////////////////////////////
  // Update ray info
  
  private void _copyRayInfoToStatus(StatusData status) 

  {
    
    status.ray_time.decodeIso(_rayTimeStr);
    status.ray_nano_secs = _rayNanoSecs;
    status.volume_number = _volumeNumber;
    status.sweep_number = _sweepNumber;
    status.sweep_mode = _sweepMode;
    status.elevation_deg = _elevationDeg;
    status.azimuth_deg = _azimuthDeg;
    status.fixed_angle_deg = _fixedAngleDeg;
    status.scan_rate_deg_per_sec = _scanRateDegPerSec;

  }

  /////////////////////////////////////////////
  // Update ray info
  
  private int _updateRayInfo() 
    
  {
    
    SAXBuilder builder = new SAXBuilder();
    Document doc;
    
    try {
      doc = builder.build(new StringReader(_rayInfoStr));
    }
    catch (JDOMException e) { 
      return -1;
    }
    catch (IOException e) { 
      return -1;
    }

    // load from XML
    
    _loadRayInfoFromXmlDoc(doc);
    
    if (_params.verbose.getValue()) {
      System.err.println("========================================");
      System.err.println("Got ray info from RadxMon ----->>");
      System.err.println("  rayTime:" + _rayTimeStr);
      System.err.println("  rayNanoSecs:" + _rayNanoSecs);
      System.err.println("  volumeNumber:" + _volumeNumber);
      System.err.println("  sweepNumber:" + _sweepNumber);
      System.err.println("  sweepMode:" + _sweepMode);
      System.err.println("  elevationDeg:" + _elevationDeg);
      System.err.println("  azimuthDeg:" + _azimuthDeg);
      System.err.println("  fixedAngleDeg:" + _fixedAngleDeg);
      System.err.println("  scanRateDegPerSec:" + _scanRateDegPerSec);
      System.err.println("========================================");
    }

    return 0;

  }

  /////////////////////////////////////////////
  // Load ray info from XML document
  
  private void _loadRayInfoFromXmlDoc(Document doc) {

    double val;
    Element root = doc.getRootElement();
    if (root.getName().equals("RadxMonReply")) {
      Iterator itRoot = root.getChildren().iterator();
      while (itRoot.hasNext()) {
        Element elc = (Element) itRoot.next();
        if (elc.getName().equals("Status")) {
          Iterator itc = elc.getChildren().iterator();
          while (itc.hasNext()) {
            
            Element el = (Element) itc.next();
            String name = new String(el.getName());
            String text = new String(el.getText());

            if (name.equals("ray_time")) {
              _rayTimeStr = text;
            }
            if (name.equals("ray_nano_secs")) {
              _rayNanoSecs = _getLongFromStr(name, text);
            }
            if (name.equals("volume_number")) {
              _volumeNumber = _getIntFromStr(name, text);
            }
            if (name.equals("sweep_number")) {
              _sweepNumber = _getIntFromStr(name, text);
            }
            if (name.equals("sweep_mode")) {
              _sweepMode = text;
            }
            
            if (name.equals("elevation_deg")) {
              _elevationDeg = _getDoubleFromStr(name, text);
            }
            if (name.equals("azimuth_deg")) {
              _azimuthDeg = _getDoubleFromStr(name, text);
            }
            if (name.equals("fixed_angle_deg")) {
              _fixedAngleDeg = _getDoubleFromStr(name, text);
            }
            if (name.equals("scan_rate_deg_per_sec")) {
              _scanRateDegPerSec = _getDoubleFromStr(name, text);
            }

          } // itc.hasNext()

        } // elc

      } // itroot

    } // root

  }

  /////////////////////////////////////////////
  // Load a double from a string
  
  private double _getDoubleFromStr(String name, String text) {
    double dval = -9999;
    try {
      dval = Double.parseDouble(text);
    }
    catch (NumberFormatException e) {
      System.err.println("Invalid double val: " + name + " = " + text);
    }
    return dval;

  }

  /////////////////////////////////////////////
  // Load int from a string
  
  private int _getIntFromStr(String name, String text) {
    int ival = -9999;
    try {
      ival = Integer.parseInt(text);
    }
    catch (NumberFormatException e) {
      System.err.println("Invalid long val: " + name + " = " + text);
    }
    return ival;

  }

  /////////////////////////////////////////////
  // Load a long int from a string
  
  private long _getLongFromStr(String name, String text) {
    long lval = -9999;
    try {
      lval = Long.parseLong(text);
    }
    catch (NumberFormatException e) {
      System.err.println("Invalid long val: " + name + " = " + text);
    }
    return lval;

  }

  /////////////////////////////////////////////
  // Load a boolean from a string
  
  private boolean _getBooleanFromStr(String text) {
    if (text.equals("true")) {
      return true;
    } else {
      return false;
    }
  }

}

