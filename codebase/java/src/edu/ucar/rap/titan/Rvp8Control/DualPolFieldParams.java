///////////////////////////////////////////////////////////////////////
//
// DualPolFieldParams.java
//
// Entry panel for radar field parameters
//
// Mike Dixon
//
// April 2007
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.Rvp8Control;

import edu.ucar.rap.jrp.*;

public class DualPolFieldParams extends CollectionParameter
	    
{

  public BooleanParameter kdp;
  public BooleanParameter ldrh;
  public BooleanParameter ldrv;
  public BooleanParameter phidp;
  public BooleanParameter phih;
  public BooleanParameter phiv;
  public BooleanParameter rhoh;
  public BooleanParameter rhohv;
  public BooleanParameter rhov;
  public BooleanParameter zdr;

  // constructor
  
  public DualPolFieldParams(String name, String label, int depth)
  {
    
    super(name, label, depth);
    
    kdp = new BooleanParameter("kdp");
    kdp.setLabel("kdp");
    kdp.setValue(false);
	
    ldrh = new BooleanParameter("ldrh");
    ldrh.setLabel("ldrh");
    ldrh.setValue(false);
	
    ldrv = new BooleanParameter("ldrv");
    ldrv.setLabel("ldrv");
    ldrv.setValue(false);
	
    phidp = new BooleanParameter("phidp");
    phidp.setLabel("phidp");
    phidp.setValue(false);
	
    phih = new BooleanParameter("phih");
    phih.setLabel("phih");
    phih.setValue(false);
	
    phiv = new BooleanParameter("phiv");
    phiv.setLabel("phiv");
    phiv.setValue(false);
	
    rhoh = new BooleanParameter("rhoh");
    rhoh.setLabel("rhoh");
    rhoh.setValue(false);
	
    rhohv = new BooleanParameter("rhohv");
    rhohv.setLabel("rhohv");
    rhohv.setValue(false);
	
    rhov = new BooleanParameter("rhov");
    rhov.setLabel("rhov");
    rhov.setValue(false);
	
    zdr = new BooleanParameter("zdr");
    zdr.setLabel(" zdr");
    zdr.setValue(false);
	
    // add the parameters to the list
	
    add(kdp);
    add(ldrh);
    add(ldrv);
    add(phidp);
    add(phih);
    add(phiv);
    add(rhoh);
    add(rhohv);
    add(rhov);
    add(zdr);

    // copy the values to the defaults

    setDefaultFromValue();

  }

}


