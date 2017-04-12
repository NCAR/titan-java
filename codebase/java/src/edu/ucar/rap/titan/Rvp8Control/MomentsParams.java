///////////////////////////////////////////////////////////////////////
//
// MomentsParams
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

public class MomentsParams extends CollectionParameter
	    
{

  public BooleanParameter indexedBeams;
  public IntegerParameter nSamples;
  public OptionParameter majorMode;
  public OptionParameter fftWindow;
  public OptionParameter clutFilter;
  public OptionParameter rangeSmoothing;
  public OptionParameter speckleFilter;

  // constructor
  
  public MomentsParams(String name, String label, int depth)
  {
    
    super(name, label, depth);
    
    // initialize the parameter objects
    
    // indexed beams?

    indexedBeams = new BooleanParameter("indexedBeams");
    indexedBeams.setLabel("Index beams?");
    indexedBeams.setDescription("Index the beams to exact azimuths?");
    indexedBeams.setInfo("Only applies to RVP8 moments." +
                         " If true, beams are computed on exact, repeatable" +
                         " azimuths. If false, beams are computed every" +
                         " nPulses pulses.");
    indexedBeams.setValue(true);
	
    // nSamples
	
    nSamples = new IntegerParameter("nSamples");
    nSamples.setLabel("N Samples");
    nSamples.setDescription("N samples for RVP8 processing");
    nSamples.setInfo("This is the number of samples used to compute the" +
                     " RVP8 moments for a single beam.");
    nSamples.setValue(64);
    nSamples.setMinValue(1);
    nSamples.setMaxValue(10000);
	
    // RVP8 major mode
    
    majorMode = new OptionParameter("majorMode");
    majorMode.setLabel("Major mode");
    majorMode.setDescription("RVP8 major mode for moments");
    majorMode.setInfo("The major mode applies to the RVP8 moments.");
    {
      String options[] = {"pulse-pair", "fft",
                          "phase-coded", "staggered-prt"};
      majorMode.setOptions(options);
    }
    majorMode.setValue("pulse-pair");

    // FFT window type
    
    fftWindow = new OptionParameter("fftWindow");
    fftWindow.setLabel("FFT window");
    fftWindow.setDescription("Window function for FFT moments modes");
    fftWindow.setInfo("blkmanex: extended Blackman");
    {
      String options[] = {"rect", "hamming", "blackman",
                          "blkmanex", "vonhann", "adaptive"};
      fftWindow.setOptions(options);
    }
    fftWindow.setValue("vonhann");

    // clutter filtering
	
    clutFilter = new OptionParameter("clutFilter");
    clutFilter.setLabel("Clutter filter");
    clutFilter.setDescription("RVP8 clutter filter");
    clutFilter.setInfo("Ranges from 0 - 7." +
                        " 0: no filter." +
                        " 1: least aggressive filter." +
                        " 7: most aggressive filter.");
    {
      String options[] = {"0:none", "1:least", "2",
                          "3", "4", "5", "6", "7:most"};
      clutFilter.setOptions(options);
    }
    clutFilter.setValue("0:none");
	
    // range smoothing
	
    rangeSmoothing = new OptionParameter("rangeSmoothing");
    rangeSmoothing.setLabel("Range smoothing");
    rangeSmoothing.setDescription("Apply smoothing to RVP8 moments");
    rangeSmoothing.setInfo("Ranges from 0 - 3." +
                           " 0: no smoothing." +
                           " 1: smooth pairs." +
                           " 2: running smoothing on 3 gates." +
                           " 3: running smoothing on 4 gates.");
    {
      String options[] = {"0:none", "1:pairs",
                          "2:3_gates", "3:4-gates"};
      rangeSmoothing.setOptions(options);
    }
    rangeSmoothing.setValue("0:none");
	
    // speckle filter?

    speckleFilter = new OptionParameter("speckleFilter");
    speckleFilter.setLabel("Speckle filter?");
    speckleFilter.setDescription("Apply speckle filter dbz and/or vel.");
    speckleFilter.setInfo("If true, the RVP8 moments are cleaned up using" +
                          " a gate-to-gate speckle filter.");
    {
      String options[] = {"off", "dbz", "vel", "both"};
      speckleFilter.setOptions(options);
    }
    speckleFilter.setValue("off");
	
    // add the parameters to the list
	
    add(indexedBeams);
    add(nSamples);
    add(majorMode);
    add(fftWindow);
    add(clutFilter);
    add(rangeSmoothing);
    add(speckleFilter);

    // copy the values to the defaults

    setDefaultFromValue();

  }

}


