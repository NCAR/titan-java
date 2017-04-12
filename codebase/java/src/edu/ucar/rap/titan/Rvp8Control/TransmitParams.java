///////////////////////////////////////////////////////////////////////
//
// TransmitParams
//
// Entry panel for transmit parameters
//
// Mike Dixon
//
// April 2007
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.Rvp8Control;

import edu.ucar.rap.jrp.*;

public class TransmitParams extends CollectionParameter
	    
{

  public OptionParameter opsMode;
  public OptionParameter prfMode;
  public DoubleParameter prf;
  public OptionParameter phaseCoding;
  public OptionParameter polarization;

  // constructor
  
  public TransmitParams(String name, String label, int depth)
  {
    
    super(name, label, depth);
    
    // active or passive mode?
    
    opsMode = new OptionParameter("opsMode");
    opsMode.setLabel("Operational mode");
    opsMode.setDescription("RVP8 mode");
    opsMode.setInfo("Set to active if the RVP8 triggers the transmitter." +
                    " Set to passive if not.");
    {
      String options[] = {"active", "passive"};
      opsMode.setOptions(options);
    }
    opsMode.setValue("active");
    
    // prf mode
    
    prfMode = new OptionParameter("prfMode");
    prfMode.setLabel("Prf mode");
    prfMode.setDescription("RVP8 PRF mode");
    prfMode.setInfo("Fixed or staggered PRT modes." +
                   " Has no effect in RVP8 passive mode.");
    {
      String options[] = {"fixed", "2_3", "3_4", "4_5"};
      prfMode.setOptions(options);
    }
    prfMode.setValue("fixed");

    // prf
	
    prf = new DoubleParameter("prf");
    prf.setLabel("PRF");
    prf.setDescription("Pulse repetition frequency (s-1)");
    prf.setInfo("Has no effect in RVP8 passive mode.");
    prf.setValue(1000);

    // Phase sequence
    
    phaseCoding = new OptionParameter("phaseCoding");
    phaseCoding.setLabel("Phase coding");
    phaseCoding.setDescription("Phase coding sequence");
    phaseCoding.setInfo("fixed is normal klystron mode." +
                        " random is normal magnetron mode.");
    {
      String options[] = {"fixed", "random", "custom", "sz8_64"};
      phaseCoding.setOptions(options);
    }
    phaseCoding.setValue("fixed");

    // Polarization
    
    polarization = new OptionParameter("polarization");
    polarization.setLabel("Polarization");
    polarization.setDescription("Polarization setup");
    polarization.setInfo
      ("Has no effect in passive mode." +
       " alternating: fast alternating, as in SPOL and CHILL" +
       " simultaneous: with a splitter, as in NEXRAD");
    {
      String options[] = {"horizontal", "vertical",
                          "alternating", "simultaneous"};
      polarization.setOptions(options);
    }
    polarization.setValue("horizontal");

    // add the parameters to the list
	
    add(opsMode);
    add(prfMode);
    add(prf);
    add(phaseCoding);
    add(polarization);

    // copy the values to the defaults

    setDefaultFromValue();

  }

}


