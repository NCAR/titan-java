///////////////////////////////////////////////////////////////////////
//
// CommsParams
//
// Communication parameters
//
// Mike Dixon
//
// April 2007
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.Rvp8Control;

import edu.ucar.rap.jrp.*;

public class CommsParams extends CollectionParameter
	    
{
  
  public StringParameter driverHost;
  public IntegerParameter driverPort;
    
  // constructor
  
  public CommsParams(String name, String label, int depth)
  {
    super(name, label, depth);
    
    driverHost = new StringParameter("driverHost");
    driverHost.setLabel("RVP8 driver host");
    driverHost.setDescription("IP name of driver host");
    driverHost.setInfo("This can be either a hostname or IP address.");
    driverHost.setValue("localhost");
    
    driverPort = new IntegerParameter("driverPort");
    driverPort.setLabel("RVP8 driver port");
    driverPort.setDescription("TCP/IP port for RVP8 driver");
    driverPort.setValue(11000);
    
    // add the parameters to the list
    
    add(driverHost);
    add(driverPort);
    
    // copy the values to the defaults
    
    setDefaultFromValue();
    
  }

}
