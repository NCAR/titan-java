///////////////////////////////////////////////////////////////////////
//
// PpiPixel
//
// Pixel location for PPI
//
// Mike Dixon
//
// Dec 2002
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

public class PpiPixel

{

    public int Ix;
    public int Iy;
    public float Range;
    
    public PpiPixel(int ix, int iy, float range) {
	Ix = ix;
	Iy = iy;
	Range = range;
    }
    
}
