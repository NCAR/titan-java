///////////////////////////////////////////////////////////////////////
//
// PowerStatusParams
//
// Power status indicator parameters
//
// Mike Dixon
//
// Nov 2006
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import edu.ucar.rap.jrp.*;

class PowerStatusParams extends CollectionParameter
	    
{

  // API for softwareStatuses - array of ErrorFlagsChannelParams
  
  public PowerChannelParams channels[] =
    new PowerChannelParams[PowerStatusGroup.nChannels];
  
  // constructor
  
  public PowerStatusParams(String name, String label, int depth)
  {
    
    super(name, label, depth);
    
    // set up and add the param objects
    
    for (int i = 0; i < PowerStatusGroup.nChannels; i++) {
      
      String nameStr = "powerChannel_" + i;
      String labelStr = "PowerChannel " + i;
      channels[i] = new PowerChannelParams(nameStr, labelStr, getNextDepth());
      channels[i].setDescription("Params for power channel " + i);
      if (i == 0) {
        channels[i].setChannelLabel("Main power");
      } else if (i == 1) {
        channels[i].setChannelLabel("System ready");
      } else if (i == 2) {
        channels[i].setChannelLabel("Servo power");
      } else if (i == 3) {
        channels[i].setChannelLabel("Radiate");
      }
      add(channels[i]);
      
    }
    
    // copy the values to the defaults
    
    setDefaultFromValue();
    
  }

}
