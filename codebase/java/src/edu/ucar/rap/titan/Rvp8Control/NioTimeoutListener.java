package edu.ucar.rap.titan.Rvp8Control;

/*******************************************************************
 * Interface for listener for NIO timeouts
 * 
 * @author Mike Dixon
 */

public interface NioTimeoutListener

{
    
    /**
     * method to call on read timeout
     */
    
    public void readTimeout();

    /**
     * method to call on write timeout
     */
    
    public void writeTimeout();

}
