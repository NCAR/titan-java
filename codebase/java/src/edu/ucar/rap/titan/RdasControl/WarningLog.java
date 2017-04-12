///////////////////////////////////////////////////////////////////////
//
// WarningLog
//
// Writes warning logs
//
// Mike Dixon
//
// Sept 2007
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.text.SimpleDateFormat;

public class WarningLog extends Thread {

  private Parameters _params = Parameters.getInstance();
  private String _prevState = "0000-000000000000-00000000-00000000";
  private int _nOperate = 0;
  private ControlPanel _controlPanel;
  
  /**
   * constructor
   */
  
  public WarningLog(ControlPanel panel) {
    _controlPanel = panel;
  }
  
  // provide run method
  
  public void run() {

    while (true) {
      
      // make sure we are in operate mode

      if (!_inOperateMode()) {
        _sleepASecond();
        continue;
      }
      
      // get the current state

      String currentState = _getCurrentState();

      // check we are still in operate mode
      
      if (!_inOperateMode()) {
        _sleepASecond();
        continue;
      }

      if (_params.debug.getValue()) {
        System.err.println("WarningLog, got currentState: " + currentState);
      }

      // has the state changed?

      if (!currentState.equals(_prevState)) {
        
        // check state - if good, continue

	if (!_params.warningLog.logChangeToNormal.getValue()) {
	  if (currentState.indexOf('1') < 0) {
	    _prevState = currentState;
	    continue;
	  }
	}

        boolean active = _params.warningLog.loggingActive.getValue();
        if (active) {
          if (_params.debug.getValue()) {
            System.err.println("---->> Warning state, will be reported");
          }
          _logWarning(currentState);
        } else {
          if (_params.debug.getValue()) {
            System.err.println("---->> Warning state, warnings not active");
            System.err.println("---->> Will be ignored");
          }
        }

        _prevState = currentState;

      } // if (!currentState.equals(prevState)) 

    } // while
    
  }

  /////////////////////////////////
  // get the current state

  private String _getCurrentState() {

    int minSecsSteady = _params.warningLog.minSecsSteady.getValue();
    String[] list = new String[minSecsSteady];

    for (int ii = 0; ii < minSecsSteady; ii++) {
      
      // sleep for a second
      
      _sleepASecond();
      
      // get current state
      
      String currentState = new String(_controlPanel.getStateBinary());
      if (_params.verbose.getValue()) {
        System.err.println("WarningLog, got currentState: " + currentState);
      }

      // add to list

      list[ii] = currentState;

    } // ii

    // count the number of entries which match other entries in the list
    
    int[] count = new int[minSecsSteady];
    for (int ii = 0; ii < minSecsSteady; ii++) {
      int thisCount = 0;
      for (int jj = 0; jj < minSecsSteady; jj++) {
        if (list[ii].equals(list[jj])) {
          thisCount++;
        }
      } // jj
      count[ii] = thisCount;
    } // ii

    // find the dominant entry

    int maxCount = 0;
    int maxIndex = 0;
    for (int ii = 0; ii < minSecsSteady; ii++) {
      if (count[ii] > maxCount) {
        maxCount = count[ii];
        maxIndex = ii;
      }
    }
    
    String currentState = list[maxIndex];
    
    return currentState;
      
  }

  /////////////////////////////////
  // log the bad state

    void _logWarning(String currentState) {

    // get time

    SimpleTimeZone tz = new SimpleTimeZone(0, "UTC");
    Calendar cal = Calendar.getInstance(tz);
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    String timeString = formatter.format(cal.getTime());

    // form message

    StringBuffer message = new StringBuffer();

    message.append("================= RdasControl STATUS REPORT ===================\n");
    message.append("Site name: " + _params.radar.siteName + "\n");
    message.append("Time: " + timeString + "\n");
    if (currentState.indexOf('1') < 0) {
	message.append("Status: NORMAL\n");
    } else {
	message.append("Status: ERROR\n");
	message.append(_controlPanel.getStateText());
    }
    message.append("===============================================================\n");

    // debug

    if (_params.debug.getValue()) {
      System.err.println(message.toString());
    }

    // write to log file

    _writeWarningFile(message.toString());

  }

  // test for operate mode

  // make sure we have been in operate mode for a while before
  // we generate warnings

  private boolean _inOperateMode() {
    
    OpMode opMode = ControlModel.getInstance().getOpMode();

    if (opMode == OpMode.OPERATE) {
      _nOperate++;
    } else {
      _nOperate = 0;
    }
  
    int minSecsOperateMode = _params.warningLog.minSecsOperateMode.getValue();
    if (_nOperate < minSecsOperateMode) {
      if (_params.debug.getValue()) {
        System.err.println("WarningLog: waiting for Operate Mode");
      }
      return false;
    }

    return true;

  }
      
  /////////////////////
  // sleep for a second
  
  private void _sleepASecond() {

    Thread t = Thread.currentThread();
    try { t.sleep(1000); }
    catch (InterruptedException e) { return; }

  }

  // write warning file

  public void _writeWarningFile(String warningMessage) {
    
    // create directory if it does not exist
    
    String warnDir = _params.warningLog.fileFolder.getValue();
    File warnDirFile = new File(warnDir);
    if (!warnDirFile.exists()) {
      boolean success = warnDirFile.mkdirs();
      if (!success) {
        // Directory creation failed
        System.err.println("ERROR - WarningLog.writeFile");
        System.err.println("  Cannot create directory for warning files\n" +
                           "  Dir: " + warnDir + "\n");
      }
      return;
    }
	
    // compute output time string
    
    Calendar cal = TimeManager.getCal();
    String timeStr = new
      String(NFormat.i4.format(cal.get(Calendar.YEAR)) +
             NFormat.i2.format(cal.get(Calendar.MONTH) + 1) +
             NFormat.i2.format(cal.get(Calendar.DAY_OF_MONTH)) +
             "_" +
             NFormat.i2.format(cal.get(Calendar.HOUR_OF_DAY)) +
             NFormat.i2.format(cal.get(Calendar.MINUTE)) +
             NFormat.i2.format(cal.get(Calendar.SECOND)));
    
    // compute simple text output path from time
    
    String filePath = new String(warnDir + "/" + timeStr + ".warning.txt");
    if (_params.debug.getValue()) {
      System.out.println("Writing warning file: " + filePath);
    }
    
    // open output file
    
    FileWriter writer;
    try {
      writer = new FileWriter(filePath);
    } catch ( IOException e) {
      System.err.println("ERROR - WarningLog.writeFile");
      System.err.println("  Cannot open warning text file for writing");
      System.err.println("  File: " + filePath);
      System.err.println("  " + e);
      return;
    }

    // write message to file

    PrintWriter out = new PrintWriter(writer);
    out.print(warningMessage);

    // close file

    out.close();

  }

}
        
