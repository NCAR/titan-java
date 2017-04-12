///////////////////////////////////////////////////////////////////////
//
// WarningLogParams
//
// Entry panel for engineering parameters
//
// Mike Dixon
//
// Sept 2003
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import edu.ucar.rap.jrp.*;

class WarningLogParams extends CollectionParameter
	    
{

  public BooleanParameter loggingActive;
  public BooleanParameter logChangeToNormal;
  public IntegerParameter minSecsOperateMode; 
  public IntegerParameter minSecsSteady;
  public StringParameter fileFolder;

  // constructor
  
  public WarningLogParams(String name, String label, int depth)
  {
    
    super(name, label, depth);
    
    // initialize the parameter objects
    
    // is logging active
    
    loggingActive = new BooleanParameter("loggingActive");
    loggingActive.setLabel("Is logging active?");
    loggingActive.setDescription("Should warnings be logged to file?");
    loggingActive.setInfo("If true, warnings will be logged to a file when an error occurs or the error state changes.");
    loggingActive.setValue(true);
    
    // log change to all good state
    
    logChangeToNormal = new BooleanParameter("logChangeToNormal");
    logChangeToNormal.setLabel("Log change back to normal?");
    logChangeToNormal.setDescription("Log change from error state back to normal?");
    logChangeToNormal.setInfo("If true, a log message will be generated when the status returns from error to normal. If false, log messages will only be generated when (a) a state change occurs and (b) an error condition still exists.");
    logChangeToNormal.setValue(true);
    
    // min seconds in operate mode before logging warnings
    
    minSecsOperateMode = new IntegerParameter("minSecsOperateMode");
    minSecsOperateMode.setLabel("Min secs in Operate mode");
    minSecsOperateMode.setDescription("Min number of secs in operate mode before logging starts.");
    minSecsOperateMode.setInfo("Warnings should only be logged in Operate mode. This is the number of seconds in which the system must be in Operate mode before we start logging warnings.");
    minSecsOperateMode.setValue(60);

    // min seconds in steady state before logging warnings
    
    minSecsSteady = new IntegerParameter("minSecsSteady");
    minSecsSteady.setLabel("Min secs in steady state");
    minSecsSteady.setDescription("Min number of secs in steady state before warnings are logged.");
    minSecsSteady.setInfo("Warnings should only be logged if the warning state remains steady. This is the minimum number of seconds for the warning state to be steady before a warnings are logged.");
    minSecsSteady.setValue(60);
    
    // file folder for warning files
    
    fileFolder = new StringParameter("fileFolder");
    fileFolder.setLabel("Warning logs file folder");
    fileFolder.setDescription("Folder in which to save and warning logs");
    fileFolder.setValue("./warnings");
	
    // add the parameters to the list
    
    add(loggingActive);
    add(logChangeToNormal);
    add(minSecsOperateMode);
    add(minSecsSteady);
    add(fileFolder);
    
    // copy the values to the defaults
    
    setDefaultFromValue();
    
  }

}


