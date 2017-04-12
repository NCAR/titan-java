///////////////////////////////////////////////////////////////////////
//
// CommsParams
//
// Communication parameters
//
// Mike Dixon
//
// May 2013
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.DowControl;

import edu.ucar.rap.jrp.*;

public class CommsParams extends CollectionParameter
	    
{
  
  public StringParameter drxHost;
  public IntegerParameter drxPort;

  public StringParameter rayHost;
  public IntegerParameter rayPort;
  
  // constructor
  
  public CommsParams(String name, String label, int depth)
  {
    super(name, label, depth);
    
    drxHost = new StringParameter("drxHost");
    drxHost.setLabel("DRX host");
    drxHost.setDescription("IP address of digital receiver host");
    drxHost.setInfo("This can be either a hostname or IP address.");
    drxHost.setValue("drx");
    
    drxPort = new IntegerParameter("drxPort");
    drxPort.setLabel("Port for DRX process");
    drxPort.setDescription("TCP/IP port for DRX");
    drxPort.setValue(15000);
    
    rayHost = new StringParameter("rayHost");
    rayHost.setLabel("Host for ray data");
    rayHost.setDescription("We query this host to get the antenna angles from RadxMon");
    rayHost.setInfo("This can be either a hostname or IP address.");
    rayHost.setValue("mgen");
    
    rayPort = new IntegerParameter("rayPort");
    rayPort.setLabel("Port for ray data from RadxMon");
    rayPort.setDescription("TCP/IP port for ray");
    rayPort.setValue(10000);
    
    // add the parameters to the list
    
    add(drxHost);
    add(drxPort);
    
    add(rayHost);
    add(rayPort);
    
    // copy the values to the defaults
    
    setDefaultFromValue();
    
  }

}
