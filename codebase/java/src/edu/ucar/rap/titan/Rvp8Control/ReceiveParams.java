///////////////////////////////////////////////////////////////////////
//
// ReceiveParams
//
// Entry panel for radar parameters
//
// Mike Dixon
//
// April 2007
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.Rvp8Control;

import edu.ucar.rap.jrp.*;

public class ReceiveParams extends CollectionParameter
	    
{

  public IntegerParameter nGates;
  public DoubleParameter gateSpacingM;

  // constructor
  
  public ReceiveParams(String name, String label, int depth)
  {
    
    super(name, label, depth);
    
    // initialize the parameter objects
    
    // nGates
	
    nGates = new IntegerParameter("nGates");
    nGates.setLabel("N gates");
    nGates.setDescription("Number of gates requested");
    nGates.setInfo("If the number of gates requested exceeds the unambiguous" +
                   " range, the measured number will be reduced accordingly.");
    nGates.setValue(1000);
    nGates.setMinValue(1);
    nGates.setMaxValue(3072);
	
    // gateSpacingM
	
    gateSpacingM = new DoubleParameter("gateSpacingM");
    gateSpacingM.setLabel("Gate spacing (m)");
    gateSpacingM.setDescription("Gate-to-gate spacing (meters)");
    gateSpacingM.setValue(150);
    gateSpacingM.setMinValue(25);
    gateSpacingM.setMaxValue(2000);
	
    // add the parameters to the list
	
    add(nGates);
    add(gateSpacingM);

    // copy the values to the defaults

    setDefaultFromValue();

  }

}


