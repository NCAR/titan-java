///////////////////////////////////////////////////////////////////////
//
// TimeManager
//
// Mike Dixon
//
// Dec 2002
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.Rvp8Control;

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
