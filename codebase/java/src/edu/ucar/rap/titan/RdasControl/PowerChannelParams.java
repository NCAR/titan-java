///////////////////////////////////////////////////////////////////////
//
// PowerChannelParams
//
// Parameters for a single power flags channel
//
// Mike Dixon
//
// Sept 2007
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;
import edu.ucar.rap.jrp.*;

class PowerChannelParams extends CollectionParameter
	    
{
    
  public StringParameter labelStr;
  public BooleanParameter active;
  public BooleanParameter invertPolarity;
  public BooleanParameter warn;
    
  // constructor
  
  public PowerChannelParams(String name, String label, int depth)
  {
	
    super(name, label, depth);
	
    labelStr = new StringParameter("labelStr");
    labelStr.setLabel("Power channel label");
    labelStr.setDescription("Label for power channel.");
    labelStr.setInfo
      ("Power channel can be used for any power circuit. " +
       "Set this label to indicate the purpose of this channel.");
    labelStr.setValue("not-set");
    
    // is this power channel active?

    active = new BooleanParameter("active");
    active.setLabel("Power channel active?");
    active.setDescription("Set to true to use this channel?");
    active.setInfo("Set to false if this power circuit is not used.");
    active.setValue(true);

    // invert the polarity on the indicator

    invertPolarity = new BooleanParameter("invertPolarity");
    invertPolarity.setLabel("Invert channel polarity?");
    invertPolarity.setDescription("Invert the polarity of this channel?");
    invertPolarity.setInfo
      ("Normally, the indicator will show green when the channel is on " +
       "and red when it is off." +
       "If the polarity is inverted, the indicator sense is also inverted.");
    invertPolarity.setValue(false);
	
    // warn on this channel
    
    warn = new BooleanParameter("warn");
    warn.setLabel("Warn");
    warn.setDescription("Warn on this channel if power is off?");
    warn.setInfo("If set, this channel will warn if the power is off.");
    warn.setValue(false);

    // add the parameters to the list
	
    add(labelStr);
    add(active);
    add(invertPolarity);
    add(warn);

    // copy the values to the defaults
	
    setDefaultFromValue();

  }

  public void setChannelLabel(String label) {
    labelStr.setValue(label);
  }

}
