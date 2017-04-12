///////////////////////////////////////////////////////////////////////
//
// ScanMode
//
// Type-safe enum for antenna modes
//
// Mike Dixon
//
// Dec 2002
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

public class ScanMode

{

    public final String name;

    private ScanMode(String name) { this.name = name; }

    public String toString() { return name; }

    public static final ScanMode MANUAL = new ScanMode("manual");
    public static final ScanMode AUTO_VOL = new ScanMode("auto_vol");
    public static final ScanMode AUTO_PPI = new ScanMode("auto_ppi");
    public static final ScanMode AUTO_RHI = new ScanMode("auto_rhi");
    public static final ScanMode FOLLOW_SUN = new ScanMode("follow_sun");
    public static final ScanMode STOP = new ScanMode("stop");
    
}
