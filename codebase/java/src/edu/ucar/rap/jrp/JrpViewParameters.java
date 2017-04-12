// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:37:55 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;

/*******************************************************************
 * Parameters for the View classes.
 * <p>
 * This is somewhat recursive.
 * This class holds parameters which
 * affect the look and feel of the parameter view classes.
 * <p>
 * Singleton.
 * 
 * @author Mike Dixon
 */

public class JrpViewParameters extends CollectionParameter
	    
{
    
  /**
   * Single instance for this class - it is a singleton
   */
    
  private static final JrpViewParameters INSTANCE = new JrpViewParameters();
    
  /**
   * Cascade offset in x between collection frames of successive depth.
   * As depth increases, so does the cascade offset from the
   * upper-level window
   */
    
  public IntegerParameter cascadeOffsetX;

  /**
   * Cascade offset in y between collection frames of successive depth.
   * As depth increases, so does the cascade offset from the
   * upper-level window
   */
    
  public IntegerParameter cascadeOffsetY;

  /**
   * x location of info window
   */

  public IntegerParameter infoX;

  /**
   * y location of info window
   */

  public IntegerParameter infoY;

  /**
   * width of info window
   */

  public IntegerParameter infoWidth;

  /**
   * height of info window
   */

  public IntegerParameter infoHeight;
    
  /**
   * constructor is private in a singleton
   */
    
  private JrpViewParameters()
  {

    super("JrpViewParameters", "View params", 2);
    setHidden(true);
	
    // X offset when cascading parameters to next depth
	
    cascadeOffsetX = new IntegerParameter("cascadeOffsetX");
    cascadeOffsetX.setLabel("Cascade offset X");
    cascadeOffsetX.setDescription("The X offset when cascading.");
    cascadeOffsetX.setInfo
      ("Parameters collections may be nested to any depth." +
       " This is the offset applied to cascade the X offset for the" +
       " View window as the user opens a parameter at the next level" +
       " down.");
    cascadeOffsetX.setValue(50);

    // Y offset when cascading parameters to next depth
	
    cascadeOffsetY = new IntegerParameter("cascadeOffsetY");
    cascadeOffsetY.setLabel("Cascade offset Y");
    cascadeOffsetY.setDescription("The Y offset when cascading.");
    cascadeOffsetY.setInfo
      ("Parameters collections may be nested to any depth." +
       " This is the offset applied to cascade the Y offset for the" +
       " View window as the user opens a parameter at the next level" +
       " down.");
    cascadeOffsetY.setValue(50);

    // X location of Info window
	
    infoX = new IntegerParameter("infoX");
    infoX.setLabel("Info window X");
    infoX.setDescription("The X location of the Info window");
    infoX.setValue(800);

    // Y location of Info window
	
    infoY = new IntegerParameter("infoY");
    infoY.setLabel("Info window Y");
    infoY.setDescription("The Y location of the Info window");
    infoY.setValue(0);

    // Width location of Info window
	
    infoWidth = new IntegerParameter("infoWidth");
    infoWidth.setLabel("Info window width");
    infoWidth.setDescription("The width of the Info window");
    infoWidth.setValue(500);

    // Height location of Info window
	
    infoHeight = new IntegerParameter("infoHeight");
    infoHeight.setLabel("Info window height");
    infoHeight.setDescription("The height of the Info window");
    infoHeight.setValue(300);

    // add the params to the list
	
    add(cascadeOffsetX);
    add(cascadeOffsetY);
    add(infoX);
    add(infoY);
    add(infoWidth);
    add(infoHeight);

    // copy the values to the defaults

    setDefaultFromValue();

    // set label and description

    setLabel("View params");
    setDescription("Parameters for controlling the View.");
    setInfo("This sets parameters which control how the View for" +
            " editing the parameters is laid out.");

  }

  /**
   * get singleton instance
   */

  public static JrpViewParameters getInstance() {
    return INSTANCE;
  }

}


