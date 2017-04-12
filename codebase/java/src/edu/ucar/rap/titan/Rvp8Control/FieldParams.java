///////////////////////////////////////////////////////////////////////
//
// FieldParams
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

public class FieldParams extends CollectionParameter
	    
{

  public BooleanParameter dbzField;
  public BooleanParameter velField;
  public BooleanParameter widthField;
  public BooleanParameter dbtField;
  public BooleanParameter dbzcField;
  public BooleanParameter velcField;
  public BooleanParameter flagsField;
  public BooleanParameter kdpField;
  public BooleanParameter ldrhField;
  public BooleanParameter ldrvField;
  public BooleanParameter phidpField;
  public BooleanParameter phihField;
  public BooleanParameter phivField;
  public BooleanParameter rhohField;
  public BooleanParameter rhohvField;
  public BooleanParameter rhovField;
  public BooleanParameter sqiField;
  public BooleanParameter zdrField;

  // constructor
  
  public FieldParams(String name, String label, int depth)
  {
    
    super(name, label, depth);
    
    dbzField = new BooleanParameter("dbzField");
    dbzField.setLabel("Activate Dbz Field?");
    dbzField.setValue(true);
	
    velField = new BooleanParameter("velField");
    velField.setLabel("Activate Vel Field?");
    velField.setValue(true);
	
    widthField = new BooleanParameter("widthField");
    widthField.setLabel("Activate Width Field?");
    widthField.setValue(true);
	
    dbtField = new BooleanParameter("dbtField");
    dbtField.setLabel("Activate Dbt Field?");
    dbtField.setValue(false);
	
    dbzcField = new BooleanParameter("dbzcField");
    dbzcField.setLabel("Activate Dbzc Field?");
    dbzcField.setValue(false);
	
    velcField = new BooleanParameter("velcField");
    velcField.setLabel("Activate Velc Field?");
    velcField.setValue(false);
	
    flagsField = new BooleanParameter("flagsField");
    flagsField.setLabel("Activate Flags Field?");
    flagsField.setValue(false);
	
    kdpField = new BooleanParameter("kdpField");
    kdpField.setLabel("Activate Kdp Field?");
    kdpField.setValue(false);
	
    ldrhField = new BooleanParameter("ldrhField");
    ldrhField.setLabel("Activate Ldrh Field?");
    ldrhField.setValue(false);
	
    ldrvField = new BooleanParameter("ldrvField");
    ldrvField.setLabel("Activate Ldrv Field?");
    ldrvField.setValue(false);
	
    phidpField = new BooleanParameter("phidpField");
    phidpField.setLabel("Activate Phidp Field?");
    phidpField.setValue(false);
	
    phihField = new BooleanParameter("phihField");
    phihField.setLabel("Activate Phih Field?");
    phihField.setValue(false);
	
    phivField = new BooleanParameter("phivField");
    phivField.setLabel("Activate Phiv Field?");
    phivField.setValue(false);
	
    rhohField = new BooleanParameter("rhohField");
    rhohField.setLabel("Activate Rhoh Field?");
    rhohField.setValue(false);
	
    rhohvField = new BooleanParameter("rhohvField");
    rhohvField.setLabel("Activate Rhohv Field?");
    rhohvField.setValue(false);
	
    rhovField = new BooleanParameter("rhovField");
    rhovField.setLabel("Activate Rhov Field?");
    rhovField.setValue(false);
	
    sqiField = new BooleanParameter("sqiField");
    sqiField.setLabel("Activate Sqi Field?");
    sqiField.setValue(false);
	
    zdrField = new BooleanParameter("zdrField");
    zdrField.setLabel("Activate Zdr Field?");
    zdrField.setValue(false);
	
    // add the parameters to the list
	
    add(dbzField);
    add(velField);
    add(widthField);
    add(dbtField);
    add(dbzcField);
    add(velcField);
    add(flagsField);
    add(kdpField);
    add(ldrhField);
    add(ldrvField);
    add(phidpField);
    add(phihField);
    add(phivField);
    add(rhohField);
    add(rhohvField);
    add(rhovField);
    add(sqiField);
    add(zdrField);

    // copy the values to the defaults

    setDefaultFromValue();

  }

}


