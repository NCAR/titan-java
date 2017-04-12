///////////////////////////////////////////////////////////////////////
//
// SinglePolFieldParams
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

public class SinglePolFieldParams extends CollectionParameter
	    
{

  public BooleanParameter dbz;
  public BooleanParameter vel;
  public BooleanParameter width;
  public BooleanParameter dbt;
  public BooleanParameter dbzc;
  public BooleanParameter velc;
  public BooleanParameter flags;
  public BooleanParameter sqi;

  // constructor
  
  public SinglePolFieldParams(String name, String label, int depth)
  {
    
    super(name, label, depth);
    
    dbz = new BooleanParameter("dbz");
    dbz.setLabel("dbz");
    dbz.setValue(true);
	
    vel = new BooleanParameter("vel");
    vel.setLabel("vel");
    vel.setValue(true);
	
    width = new BooleanParameter("width");
    width.setLabel("width");
    width.setValue(true);
	
    dbt = new BooleanParameter("dbt");
    dbt.setLabel("dbt");
    dbt.setValue(false);
	
    dbzc = new BooleanParameter("dbzc");
    dbzc.setLabel("dbzc");
    dbzc.setValue(false);
	
    velc = new BooleanParameter("velc");
    velc.setLabel("velc");
    velc.setValue(false);
	
    flags = new BooleanParameter("flags");
    flags.setLabel("flags");
    flags.setValue(false);
	
    sqi = new BooleanParameter("sqi");
    sqi.setLabel("sqi");
    sqi.setValue(false);
	
    // add the parameters to the list
	
    add(dbz);
    add(vel);
    add(width);
    add(dbt);
    add(dbzc);
    add(velc);
    add(flags);
    add(sqi);

    // copy the values to the defaults

    setDefaultFromValue();

  }

}


