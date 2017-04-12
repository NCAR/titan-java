///////////////////////////////////////////////////////////////////////
//
// ScanParams
//
// Scan strategy parameters
//
// Mike Dixon
//
// Sept 2002
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import edu.ucar.rap.jrp.*;

class ScanParams extends CollectionParameter
	    
{

    public StringParameter elevationList;
    public DoubleParameter azSlewRate;
    public DoubleParameter elSlewRate;
    public IntegerParameter nGates;
    public DoubleParameter startRange;
    public DoubleParameter gateSpacing;
    public DoubleParameter prf;
    public IntegerParameter samplesPerGate;
    public IntegerParameter samplesPerAz;
    public OptionParameter polarization;
    
    // constructor

    public ScanParams(String name, String label, int depth)
    {
	super(name, label, depth);

	elevationList = new StringParameter("elevationList");
	elevationList.setLabel("Elevation list");
	elevationList.setDescription("Comma-delimited elevation angle list");
	elevationList.setInfo("Enter the elevation angles in a" +
			      " comma-delimited list.");
	elevationList.setValue("0.5, 1.5, 2.5, 3.5, 4.5," +
			       " 5.5, 6.5, 7.5, 8.5, 9.9");
	
	azSlewRate = new DoubleParameter("azSlewRate");
	azSlewRate.setLabel("Azimuth slew rate");
	azSlewRate.setDescription("Antenna slew rate in azimuth (deg/sec)");
 	azSlewRate.setValue(18.0);
	
	elSlewRate = new DoubleParameter("elSlewRate");
	elSlewRate.setLabel("Elevation slew rate");
	elSlewRate.setDescription("Antenna slew rate in elevation (deg/sec)");
 	elSlewRate.setValue(10.0);
	
	nGates = new IntegerParameter("nGates");
	nGates.setLabel("N Gates");
	nGates.setDescription("Number of gates per beam");
 	nGates.setValue(500);

	startRange = new DoubleParameter("startRange");
	startRange.setLabel("Start range");
	startRange.setDescription("Range to center of first gate (km)");
 	startRange.setValue(0.125);

	gateSpacing = new DoubleParameter("gateSpacing");
	gateSpacing.setLabel("Gate spacing");
	gateSpacing.setDescription("Spacing between gates (km)");
 	gateSpacing.setValue(0.250);
	
	prf = new DoubleParameter("prf");
	prf.setLabel("PRF");
	prf.setDescription("Pulse Repetition Frequency (/s)");
 	prf.setValue(1000);

	samplesPerAz = new IntegerParameter("samplesPerAz");
	samplesPerAz.setLabel("N samples per az");
	samplesPerAz.setDescription("Number of samples per az");
	samplesPerAz.setInfo("If more than 1, this implies averaging in azimuth");
 	samplesPerAz.setValue(8);

	samplesPerGate = new IntegerParameter("samplesPerGate");
	samplesPerGate.setLabel("N samples per gate");
	samplesPerGate.setDescription("Number of samples per gate");
	samplesPerGate.setInfo("If more than 1, this implies averaging in range");
 	samplesPerGate.setValue(4);

	polarization = new OptionParameter("polarization");
	polarization.setLabel("Polarization");
	polarization.setDescription("Polarization type");
	String options[] = {"Horizontal", "Vertical", "Circular"};
	polarization.setOptions(options);
 	polarization.setValue("Horizontal");

	// add the parameters to the list
	
	add(elevationList);
	add(azSlewRate);
	add(elSlewRate);
	add(nGates);
	add(startRange);
	add(gateSpacing);
	add(prf);
	add(samplesPerAz);
	add(samplesPerGate);
	add(polarization);

	// copy the values to the defaults

	setDefaultFromValue();

    }

}
