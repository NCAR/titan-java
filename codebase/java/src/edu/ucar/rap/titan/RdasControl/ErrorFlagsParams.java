///////////////////////////////////////////////////////////////////////
//
// ErrorFlagsParams
//
// Parameters for the group of 8 flag channels
//
// Mike Dixon
//
// Sept 2007
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import edu.ucar.rap.jrp.*;

class ErrorFlagsParams extends CollectionParameter
	    
{

  // API for softwareStatuses - array of ErrorFlagsChannelParams
  
  public ErrorChannelParams channels[] =
    new ErrorChannelParams[ErrorFlagsGroup.nChannels];
  
  // constructor
  
  public ErrorFlagsParams(String name, String label, int depth)
  {
    
    super(name, label, depth);
    
    // set up and add the param objects
    
    for (int i = 0; i < ErrorFlagsGroup.nChannels; i++) {
      
      String nameStr = "errorFlags_" + i;
      String labelStr = "ErrorFlags " + i;
      channels[i] = new ErrorChannelParams(nameStr, labelStr, getNextDepth());
      channels[i].setDescription("Parameters for error flags channel " + i);
      add(channels[i]);
      
    }
    
    // copy the values to the defaults
    
    setDefaultFromValue();
    
  }

}


