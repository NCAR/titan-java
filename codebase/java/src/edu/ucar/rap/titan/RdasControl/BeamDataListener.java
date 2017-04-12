package edu.ucar.rap.titan.RdasControl;

/*******************************************************************
 * Interface for listener for new beam data, as BeamMessage objects
 * 
 * @author Mike Dixon
 */

public interface BeamDataListener

{
    
    /**
     * handle new beam when it arrives
     */
    
    public void handleBeam(BeamMessage beam, double rate);

}
