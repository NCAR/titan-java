///////////////////////////////////////////////////////////////////////
//
// ErrorChannelParams
//
// Parameters for a single error flags channel
//
// Mike Dixon
//
// Sept 2007
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;
import edu.ucar.rap.jrp.*;

class ErrorChannelParams extends CollectionParameter
	    
{
    
  public StringParameter labelStr;
  public BooleanParameter active;
  public OptionParameter normalState;
  public BooleanParameter warn;
  
    
  // constructor

  public ErrorChannelParams(String name, String label, int depth)
  {
	
    super(name, label, depth);
	
    // channel label
    
    labelStr = new StringParameter("labelStr");
    labelStr.setLabel("Channel label");
    labelStr.setDescription("Label for this error flags channel");
    labelStr.setInfo("This label will appear in the GUI next " +
                     "to the controls and LED for this channel.");
    labelStr.setValue("not-set");
	
    // is this channel active?
    
    active = new BooleanParameter("active");
    active.setLabel("Channel active?");
    active.setDescription("Is this error flags channel active?");
    active.setInfo("The user configures which error flags channels are active. " +
                   "Only the active channels will be shown.");
    active.setValue(false);
    
    // normal state for this channel
    
    normalState = new OptionParameter("normalState");
    normalState.setLabel("Normal state");
    normalState.setDescription("Normal state for this channel.");
    normalState.setInfo("The light will be green if the channel is in this state");
    String options[] = {"OK", "ERR"};
    normalState.setOptions(options);
    normalState.setValue("OK");
    
    // warn on this channel
    
    warn = new BooleanParameter("warn");
    warn.setLabel("Warn");
    warn.setDescription("Warn on this channel if state is not normal?");
    warn.setInfo("If set, this channel will warn if the state is not the " +
                 "normal state selected.");
    warn.setValue(false);

    // add the parameters to the list
	
    add(labelStr);
    add(active);
    add(normalState);
    add(warn);

    // copy the values to the defaults
	
    setDefaultFromValue();

  }

}
