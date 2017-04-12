package edu.ucar.rap.titan.Rvp8Control;

/*******************************************************************
 * Interface for listener for new sun positions
 * 
 * @author Mike Dixon
 */

public interface SunPosnListener

{
    
    /**
     * set new position
     */
    
    public void setSunPosn(double elevation, double azimuth);

}
