///////////////////////////////////////////////////////////////////////
//
// OpMode
//
// Type-safe enum for op modes
//
// Mike Dixon
//
// March 2003
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

public class OpMode

{

    public final String name;
    
    private OpMode(String name) { this.name = name; }

    public String toString() { return name; }

    public static final OpMode OFF = new OpMode("off");
    public static final OpMode STANDBY = new OpMode("standby");
    public static final OpMode CALIBRATE = new OpMode("calibrate");
    public static final OpMode OPERATE = new OpMode("operate");
    
}
