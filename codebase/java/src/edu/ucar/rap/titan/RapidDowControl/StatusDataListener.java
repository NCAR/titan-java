package edu.ucar.rap.titan.RapidDowControl;

/*******************************************************************
 * Interface for listener for new status data
 * 
 * @author Mike Dixon
 */

public interface StatusDataListener

{
    
    /**
     * handle new status when it arrives
     */
    
    public void handleStatus(StatusData status);

}
