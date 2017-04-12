///////////////////////////////////////////////////////////////////////
//
// CustomBorder
//
// Static factory methods for returning custom borders
//
// Mike Dixon
//
// Nov 2002
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.awt.Component;
import javax.swing.*;
import javax.swing.border.*;

public class CustomBorder

{

    public static Border createAboveTop(Component parent, String titleLabel,
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

    public static Border createTop(Component parent, String titleLabel,
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
    
    public static Border createSimple(Component parent, int spacing) {
	
	Border etched =
	    BorderFactory.createEtchedBorder(EtchedBorder.LOWERED,
					     parent.getBackground().brighter(),
					     parent.getBackground().darker());
	
  	return BorderFactory.createCompoundBorder
  	    (etched,
	     BorderFactory.createEmptyBorder(0,spacing,spacing,spacing));
	
    }

}
