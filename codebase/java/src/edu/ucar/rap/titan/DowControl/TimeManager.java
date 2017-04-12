///////////////////////////////////////////////////////////////////////
//
// TimeManager
//
// Mike Dixon
//
// May 2013
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.DowControl;

import java.util.*;

public class TimeManager

{
    
    static private SimpleTimeZone _tz = new SimpleTimeZone(0, "UTC");

    static public long getTime() {
	Date date = new Date();
	return date.getTime();
    }
    
    static public Date getDate() {
	Date date = new Date();
	return date;
    }
    
    static public Calendar getCal() {
	return Calendar.getInstance(_tz);
    }
    
}
