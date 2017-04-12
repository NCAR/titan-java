///////////////////////////////////////////////////////////////////////
//
// SiteParams
//
// Entry panel for site parameters
//
// Mike Dixon
//
// April 2007
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.Rvp8Control;

import edu.ucar.rap.jrp.*;

public class SiteParams extends CollectionParameter
	    
{

    public StringParameter siteName;
    public DoubleParameter latitude;
    public DoubleParameter longitude;
    public DoubleParameter altitude;
    
    // constructor

    public SiteParams(String name, String label, int depth)
    {

	super(name, label, depth);

	// initialize the parameter objects
    
	// site name
	
	siteName = new StringParameter("siteName");
	siteName.setLabel("Site name");
	siteName.setDescription("The name of the site");
	siteName.setInfo("The site name must be a text description" +
			 " of the geographical geographical location of" +
			 " the radar installation, such as a farm name" +
			 " or nearby town.");
	siteName.setValue("Marshall");
	
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
	
	// add the parameters to the list
	
	add(siteName);
	add(latitude);
	add(longitude);
	add(altitude);

	// copy the values to the defaults

	setDefaultFromValue();

    }

}


