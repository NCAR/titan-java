///////////////////////////////////////////////////////////////////////
//
// DataField
//
// Type-safe name for display fields
//
// Mike Dixon
//
// Dec 2004
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

public class DataField

{

    public final String name;

    private DataField(String name) { this.name = name; }
    
    public String toString() { return name; }

    public static final DataField DBZ = new DataField("DBZ");
    public static final DataField SNR = new DataField("SNR");
    
}
