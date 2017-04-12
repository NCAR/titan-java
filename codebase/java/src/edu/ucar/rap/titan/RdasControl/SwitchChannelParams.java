///////////////////////////////////////////////////////////////////////
//
// SwitchChannelParams
//
// Parameters for a single switch channel
//
// Mike Dixon
//
// Feb 2004
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import edu.ucar.rap.jrp.*;

class SwitchChannelParams extends CollectionParameter
	    
{
    
  public StringParameter labelStr;
  public BooleanParameter onAtStartup;
  public BooleanParameter enableSwitch;
  public BooleanParameter active;
  public BooleanParameter invertPolarity;
  public OptionParameter normalState;
  public BooleanParameter warn;
  
    
  // constructor

  public SwitchChannelParams(String name, String label, int depth)
  {
	
    super(name, label, depth);
	
    // channel label
	
    labelStr = new StringParameter("labelStr");
    labelStr.setLabel("Channel label");
    labelStr.setDescription("Label for this switch channel");
    labelStr.setInfo("This label will appear in the GUI next " +
                     "to the controls and LED for this channel.");
    labelStr.setValue("not-set");
	
    // is this channel on at startup?

    onAtStartup = new BooleanParameter("onAtStartup");
    onAtStartup.setLabel("OnAtStartup");
    onAtStartup.setDescription("Is this switch on at startup?");
    onAtStartup.setInfo("If true, the switch will be set on automatically " +
                        "at startup.");
    onAtStartup.setValue(false);

    // do we enable the switch for this channel, or just monitor the line
	
    enableSwitch = new BooleanParameter("enableSwitch");
    enableSwitch.setLabel("Enable switch?");
    enableSwitch.setDescription("Do we enable the switch for this channel?");
    enableSwitch.setInfo("If false, the channel is only used to monitor the line. " +
                         "The switch will be disabled (grayed-out).");
    enableSwitch.setValue(true);
	
    // is this channel active?

    active = new BooleanParameter("active");
    active.setLabel("Channel active?");
    active.setDescription("Is this switch channel active?");
    active.setInfo("The user can configure which switches are active. " +
                   "Only the active channels will be shown.");
    active.setValue(false);
	
    // invert the polarity on the indicator

    invertPolarity = new BooleanParameter("invertPolarity");
    invertPolarity.setLabel("Invert channel polarity?");
    invertPolarity.setDescription("Invert the polarity of this switch?");
    invertPolarity.setInfo
      ("Normally, the indicator will show green when the channel is on and red " +
       "when it is off." +
       "If the polarity is inverted, the indicator sense is inverted accordingly.");
    invertPolarity.setValue(false);
	
    // normal state for this channel
    
    normalState = new OptionParameter("normalState");
    normalState.setLabel("Normal state");
    normalState.setDescription("Normal state for this channel.");
    normalState.setInfo("The light will be green if the channel is in this state");
    String options[] = {"OFF", "ON"};
    normalState.setOptions(options);
    normalState.setValue("ON");
    
    // warn on this channel
    
    warn = new BooleanParameter("warn");
    warn.setLabel("Warn");
    warn.setDescription("Warn on this channel if state is not normal?");
    warn.setInfo("If set, this channel will warn if the state is not the " +
                 "normal state selected.");
    warn.setValue(false);

    // add the parameters to the list
	
    add(labelStr);
    add(onAtStartup);
    add(enableSwitch);
    add(active);
    add(invertPolarity);
    add(normalState);
    add(warn);

    // copy the values to the defaults
	
    setDefaultFromValue();

  }

}
