///////////////////////////////////////////////////////////////////////
//
// GetStatus
//
// Mike Dixon
//
// April 2007
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.Rvp8Control;

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
  
  public GetStatus() {
	
    // create the ticker thread, start it
    
    Ticker ticker = new Ticker();
    ticker.start();
    
  }
	
  // ticker thread
    
  private class Ticker extends Thread {
    public void run() {
      int intervalMsecs =
        (int) (_params.status.intervalSecs.getValue() * 1000.0 + 0.5);
      while (true) {
        // update clock
        _getStatus();
        // sleep a bit
        Thread t = Thread.currentThread();
        try { t.sleep(intervalMsecs); }
        catch (InterruptedException e) { return; }
      }
    }
  }

  private int _getStatus() {
    
    StringBuffer xml = new StringBuffer();

    xml.append("<rvp8Message>\n");
    xml.append("<action>getStatus</action>\n");
    xml.append("</rvp8Message>\n");
    
    if (_params.verbose.getValue()) {
      System.err.println("Requesting status");
    }
    
    SocketComms sock = new SocketComms();
    String reply = "";
    try {
      reply = sock.communicate(xml.toString());
    }
    catch (IOException e) {
      String error =
        "ERROR sending sync request to RVP8 driver\n" + e.toString();
      System.err.println(error);
      return -1;
    }

    if (_checkReplyForErrors(reply) != 0) {
      return -1;
    }

    if (_handleStatusMessage(reply) != 0) {
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
        String error = "Rvp8Driver ERROR\n" + el.getText() + "\n";
        System.err.println(error);
        return -1;
      }
    }

    return 0;

  }

  /////////////////////////////////////////////
  // Sunc local settings with Rvp8Driver state
  
  private int _handleStatusMessage(String reply) {

    SAXBuilder builder = new SAXBuilder();
    Document doc;

    try {
      doc = builder.build(new StringReader(reply));
    }
    catch (JDOMException e) { 
      return -1;
    }
    catch (IOException e) { 
      return -1;
    }

    // load from XML

    StatusData status = new StatusData();
    _loadStatusFromXmlDoc(doc, status);

    // put the status onto the queue

    StatusQueue queue = StatusQueue.getInstance();
    queue.push(status);

    return 0;

  }

  /////////////////////////////////////////////
  // Load status data from XML document
  
  private void _loadStatusFromXmlDoc(Document doc,
                                     StatusData status) {

    Element root = doc.getRootElement();
    if (root.getName().equals("rvp8Message")) {
      Iterator itRoot = root.getChildren().iterator();
      while (itRoot.hasNext()) {
        Element elc = (Element) itRoot.next();
        if (elc.getName().equals("rvp8Status")) {
          Iterator itc = elc.getChildren().iterator();
          while (itc.hasNext()) {

            Element el = (Element) itc.next();
            String name = new String(el.getName());
            String text = new String(el.getText());

            if (name.equals("siteName")) {
              status.siteName = text;
            }
            if (name.equals("majorMode")) {
              status.majorMode = text;
            }
            if (name.equals("polarization")) {
              status.polarization = text;
            }
            if (name.equals("phaseCoding")) {
              status.phaseCoding = text;
            }
            if (name.equals("prfMode")) {
              status.prfMode = text;
            }
            if (name.equals("rdaVersion")) {
              status.rdaVersion = text;
            }

            if (name.equals("pulseWidthUs")) {
              double pulseWidthUs = -9999;
              try {
                pulseWidthUs = Double.parseDouble(text);
                status.pulseWidthUs = pulseWidthUs;
              }
              catch (NumberFormatException e) {
                System.err.println("Invalid value for pulseWidthUs: " + text);
              }
            }

            if (name.equals("dbzCal1km")) {
              double dbzCal1km = -9999;
              try {
                dbzCal1km = Double.parseDouble(text);
                status.dbzCal1km = dbzCal1km;
              }
              catch (NumberFormatException e) {
                System.err.println("Invalid value for dbzCal1km: " + text);
              }
            }

            if (name.equals("ifdClockMhz")) {
              double ifdClockMhz = -9999;
              try {
                ifdClockMhz = Double.parseDouble(text);
                status.ifdClockMhz = ifdClockMhz;
              }
              catch (NumberFormatException e) {
                System.err.println("Invalid value for ifdClockMhz: " + text);
              }
            }

            if (name.equals("wavelengthCm")) {
              double wavelengthCm = -9999;
              try {
                wavelengthCm = Double.parseDouble(text);
                status.wavelengthCm = wavelengthCm;
              }
              catch (NumberFormatException e) {
                System.err.println("Invalid value for wavelengthCm: " + text);
              }
            }


            if (name.equals("satPowerDbm")) {
              double satPowerDbm = -9999;
              try {
                satPowerDbm = Double.parseDouble(text);
                status.satPowerDbm = satPowerDbm;
              }
              catch (NumberFormatException e) {
                System.err.println("Invalid value for satPowwerDbm: " + text);
              }
            }

            if (name.equals("rangeMaskResKm")) {
              double rangeMaskResKm = -9999;
              try {
                rangeMaskResKm = Double.parseDouble(text);
                status.rangeMaskResKm = rangeMaskResKm;
              }
              catch (NumberFormatException e) {
                System.err.println("Invalid value for rangeMaskResKm: " + text);
              }
            }

            if (name.equals("startRangeKm")) {
              double startRangeKm = -9999;
              try {
                startRangeKm = Double.parseDouble(text);
                status.startRangeKm = startRangeKm;
              }
              catch (NumberFormatException e) {
                System.err.println("Invalid value for startRangeKm: " + text);
              }
            }

            if (name.equals("maxRangeKm")) {
              double maxRangeKm = -9999;
              try {
                maxRangeKm = Double.parseDouble(text);
                status.maxRangeKm = maxRangeKm;
              }
              catch (NumberFormatException e) {
                System.err.println("Invalid value for maxRangeKm: " + text);
              }
            }

            if (name.equals("gateSpacingKm")) {
              double gateSpacingKm = -9999;
              try {
                gateSpacingKm = Double.parseDouble(text);
                status.gateSpacingKm = gateSpacingKm;
              }
              catch (NumberFormatException e) {
                System.err.println("Invalid value for gateSpacingKm: " + text);
              }
            }


            if (name.equals("noiseChan0")) {
              double noiseChan0 = -9999;
              try {
                noiseChan0 = Double.parseDouble(text);
                status.noiseChan0 = noiseChan0;
              }
              catch (NumberFormatException e) {
                System.err.println("Invalid value for noiseChan0: " + text);
              }
            }

            if (name.equals("noiseChan1")) {
              double noiseChan1 = -9999;
              try {
                noiseChan1 = Double.parseDouble(text);
                status.noiseChan1 = noiseChan1;
              }
              catch (NumberFormatException e) {
                System.err.println("Invalid value for noiseChan1: " + text);
              }
            }

            if (name.equals("noiseSdevChan0")) {
              double noiseSdevChan0 = -9999;
              try {
                noiseSdevChan0 = Double.parseDouble(text);
                status.noiseSdevChan0 = noiseSdevChan0;
              }
              catch (NumberFormatException e) {
                System.err.println("Invalid val for noiseSdevChan0: " + text);
              }
            }

            if (name.equals("noiseSdevChan1")) {
              double noiseSdevChan1 = -9999;
              try {
                noiseSdevChan1 = Double.parseDouble(text);
                status.noiseSdevChan1 = noiseSdevChan1;
              }
              catch (NumberFormatException e) {
                System.err.println("Invalid val for noiseSdevChan1: " + text);
              }
            }

            if (name.equals("noisePrfHz")) {
              double noisePrfHz = -9999;
              try {
                noisePrfHz = Double.parseDouble(text);
                status.noisePrfHz = noisePrfHz;
              }
              catch (NumberFormatException e) {
                System.err.println("Invalid value for noisePrfHz: " + text);
              }
            }

            if (name.equals("prf")) {
              double prf = -9999;
              try {
                prf = Double.parseDouble(text);
                status.prf = prf;
              }
              catch (NumberFormatException e) {
                System.err.println("Invalid value for prf: " + text);
              }
            }

            if (name.equals("el")) {
              double elev = -9999;
              try {
                elev = Double.parseDouble(text);
                status.el = elev;
              }
              catch (NumberFormatException e) {
                System.err.println("Invalid value for el: " + text);
              }
            }

            if (name.equals("az")) {
              double az = -9999;
              try {
                az = Double.parseDouble(text);
                status.az = az;
              }
              catch (NumberFormatException e) {
                System.err.println("Invalid value for az: " + text);
              }
            }

            if (name.equals("nGates")) {
              int nGates = -9999;
              try {
                nGates = Integer.parseInt(text);
                status.nGates = nGates;
              }
              catch (NumberFormatException e) {
                System.err.println("Invalid value for nGates: " + text);
              }
            }

            if (name.equals("time")) {
              String[] parts = text.split("\\D");
              try {
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int day = Integer.parseInt(parts[2]);
                int hour = Integer.parseInt(parts[3]);
                int min = Integer.parseInt(parts[4]);
                int sec = Integer.parseInt(parts[5]);
                SimpleTimeZone tz = new SimpleTimeZone(0, "UTC");
                Calendar cal = Calendar.getInstance(tz);
                cal.set(year, month, day, hour, min, sec);
                status.time = cal;
                status.year = year;
                status.month = month;
                status.day = day;
                status.hour = hour;
                status.min = min;
                status.sec = sec;
              }
              catch (NumberFormatException e) {
                System.err.println("Invalid value for time: " + text);
              }
            }

          } // itc.hasNext()

        } // elc

      } // itroot

    } // root

  }

}
