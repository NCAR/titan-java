package edu.ucar.rap.titan.Rvp8Control;

/*******************************************************************
 * Interface for listener for new status data
 * 
 * @author Mike Dixon
 */

public interface StatusDataListener

{
    
    /**
     * handle new beam when it arrives
     */
    
    public void handleStatus(StatusData status);

}
