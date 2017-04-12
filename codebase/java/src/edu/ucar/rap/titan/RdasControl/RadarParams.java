///////////////////////////////////////////////////////////////////////
//
// RadarParams
//
// Entry panel for radar parameters
//
// Mike Dixon
//
// August 2002
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import edu.ucar.rap.jrp.*;

public class RadarParams extends CollectionParameter
	    
{

    public IntegerParameter siteNum;
    public StringParameter siteName;
    public StringParameter userName;
    public DoubleParameter latitude;
    public DoubleParameter longitude;
    public DoubleParameter altitude;
    public DoubleParameter frequency;
    public DoubleParameter horizBeamWidth;
    public DoubleParameter vertBeamWidth;
    public DoubleParameter antGain;
    
    // constructor

    public RadarParams(String name, String label, int depth)
    {

	super(name, label, depth);

	// initialize the parameter objects
    
	// site number
	
	siteNum = new IntegerParameter("siteNum");
	siteNum.setLabel("Site number");
	siteNum.setDescription("The indexed number for this site");
	siteNum.setInfo("You should arrange these numbers with reference" +
			" to other sites in the area. The number is used" +
			" identify the radar site if mode than one radar" +
			" is used in the network");
	siteNum.setValue(0);

	// site name
	
	siteName = new StringParameter("siteName");
	siteName.setLabel("Site name");
	siteName.setDescription("The name of the site");
	siteName.setInfo("The site name must be a text description" +
			 " of the geographical geographical location of" +
			 " the radar installation, such as a farm name" +
			 " or nearby town.");
	siteName.setValue("Bethlehem");
	
	// user name
	
	userName = new StringParameter("userName");
	userName.setLabel("User name");
	userName.setDescription("The name of the technician or user");
	userName.setInfo
	    ("The technician or user of the program can" +
	     " put their name in this field. The reason for" +
	     " having it is to keep track of who performed" +
	     " the last calibration.");
	userName.setValue("Unknown");
	
	// latitude
	
	latitude = new DoubleParameter("latitude");
	latitude.setLabel("Latitude");
	latitude.setDescription("Radar latitude (decimal deg)");
	latitude.setValue(40.0);
	
	// longitude
	
	longitude = new DoubleParameter("longitude");
	longitude.setLabel("Longitude");
	longitude.setDescription("Radar longitude (decimal deg)");
	longitude.setValue(-105.0);
	
	// altitude
	
	altitude = new DoubleParameter("altitude");
	altitude.setLabel("Altitude");
	altitude.setDescription("Radar altitude (km)");
	altitude.setValue(1.7);
	
	// frequency
	
	frequency = new DoubleParameter("frequency");
	frequency.setLabel("Frequency (GHz)");
	frequency.setDescription("Radar frequency (GHz)");
	frequency.setValue(5.5);
	
	// horizontal beam width
	
	horizBeamWidth = new DoubleParameter("horizBeamWidth");
	horizBeamWidth.setLabel("Horiz beam width");
	horizBeamWidth.setDescription("Antenna horizontal beam width (deg)");
	horizBeamWidth.setInfo("Half-power horizontal beam width in degrees");
	horizBeamWidth.setValue(1.0);
	
	// vertical beam width
	
	vertBeamWidth = new DoubleParameter("vertBeamWidth");
	vertBeamWidth.setLabel("Vert beam width");
	vertBeamWidth.setDescription("Antenna vertical beam width (deg)");
	vertBeamWidth.setInfo("Half-power vertical beam width in degrees");
	vertBeamWidth.setValue(1.0);
	
	// antenna gain
	
	antGain = new DoubleParameter("antGain");
	antGain.setLabel("Antenna gain");
	antGain.setDescription("Antenna gain (dB)");
	antGain.setInfo("Antenna gain in dB");
	antGain.setValue(44.0);
	
	// add the parameters to the list
	
	add(siteNum);
	add(siteName);
	add(userName);
	add(latitude);
	add(longitude);
	add(altitude);
	add(frequency);
	add(horizBeamWidth);
	add(vertBeamWidth);
	add(antGain);

	// copy the values to the defaults

	setDefaultFromValue();

    }

}


