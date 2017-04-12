// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:37:58 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;

import javax.swing.*;
import javax.swing.border.*;

/*******************************************************************
 * Static factory methods for returning custom borders.
 * 
 * @author Mike Dixon
 */

public class JrpBorder

{

  /**
   * Create compound etched border with label above top
   *
   * @param frame       the frame for the border
   * @param titleLabel  the label above the frame
   * @param spacing     the spacing between border and frame drawing area
   */
    
  public static Border createAboveTop(JComponent parent, String titleLabel,
                                      int spacing) {
	
    Border basicBorder = BorderFactory.createCompoundBorder
      (BorderFactory.createEtchedBorder(EtchedBorder.LOWERED,
                                        parent.getBackground().brighter(),
                                        parent.getBackground().darker()),
       BorderFactory.createEmptyBorder(spacing,spacing,spacing,spacing));

    return BorderFactory.createTitledBorder
      (basicBorder, titleLabel,
       TitledBorder.CENTER, TitledBorder.ABOVE_TOP);


  }

  /**
   * Create compound etched border with label in top line
   *
   * @param frame       the frame for the border
   * @param titleLabel  the label in top frame
   * @param spacing     the spacing between border and frame drawing area
   */
    
  public static Border createTop(JComponent parent, String titleLabel,
                                 int spacing) {

    Border etched =
      BorderFactory.createEtchedBorder(EtchedBorder.LOWERED,
                                       parent.getBackground().brighter(),
                                       parent.getBackground().darker());

    Border titled = BorderFactory.createTitledBorder
      (etched, titleLabel,
       TitledBorder.CENTER, TitledBorder.TOP);

    return BorderFactory.createCompoundBorder
      (titled,
       BorderFactory.createEmptyBorder(0,spacing,spacing,spacing));
	
  }
    
  /**
   * Create compound etched border with no label
   *
   * @param frame       the frame for the border
   * @param spacing     the spacing between border and frame drawing area
   */
    
  public static Border createSimple(JComponent parent, int spacing) {
	
    Border etched =
      BorderFactory.createEtchedBorder(EtchedBorder.LOWERED,
                                       parent.getBackground().brighter(),
                                       parent.getBackground().darker());
	
    return BorderFactory.createCompoundBorder
      (etched,
       BorderFactory.createEmptyBorder(0,spacing,spacing,spacing));
	
  }

}
