package edu.ucar.rap.titan.RdasControl;

import java.nio.*;

/*******************************************************************
 * Interface for listener for new beam data, as ByteBuffer objects
 * 
 * @author Mike Dixon
 */

public interface BeamBufferListener

{
    
    /**
     * handle new beam when it arrives
     */
    
    public void handleBeamBuffer(ByteBuffer beamBuf);

}
