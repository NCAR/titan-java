///////////////////////////////////////////////////////////////////////
//
// MainWindowParams
//
// Mike Dixon
//
// May 2013
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RapidDowControl;

import edu.ucar.rap.jrp.*;
import java.awt.Color;

public class MainWindowParams extends CollectionParameter
	    
{
  
  public StringParameter radarName;
  public StringParameter imageName;
  public ColorParameter textColor;
  public FloatParameter fontSize;
  public IntegerParameter xx;
  public IntegerParameter yy;
  public IntegerParameter width;
  public IntegerParameter height;
  
  // constructor
  
  public MainWindowParams(String name, String label, int depth)
  {
    
    super(name, label, depth);
    
    // initialize the parameter objects
    
    // name of radar
    
    radarName = new StringParameter("radarName");
    radarName.setLabel("Radar name");
    radarName.setDescription("Name of radar");
    radarName.setValue("DOW7");
    
    // name of file for image in main window
    
    imageName = new StringParameter("imageName");
    imageName.setLabel("Image name");
    imageName.setDescription("Name of image in main window");
    imageName.setValue("/edu/ucar/rap/titan/RapidDowControl/images/main_window.png");
    
    // color of text in main window
    
    textColor = new ColorParameter("textColor");
    textColor.setLabel("Color of text labels");
    textColor.setDescription("Color of text in plot.");
    textColor.setValue(Color.RED);

    // font size
    
    fontSize = new FloatParameter("fontSize");
    fontSize.setLabel("Font size");
    fontSize.setDescription("Font size for pos and time");
    fontSize.setInfo("This is the font size for rendering the time and antenna position over the main image.");
    fontSize.setValue(20.0);
    
    // x offset from parent
    
    xx = new IntegerParameter("xx");
    xx.setLabel("X offset");
    xx.setDescription("The X offset from the parent");
    xx.setInfo("This the the X offset, in pixels, from the" +
               " main frame of the application.");
    xx.setValue(0);
    xx.setHidden(true);
    
    // y offset from parent
    
    yy = new IntegerParameter("yy");
    yy.setLabel("Y offset");
    yy.setDescription("The Y offset from the parent");
    yy.setInfo("This the the Y offset, in pixels, from the" +
               " main frame of the application.");
    yy.setValue(0);
    yy.setHidden(true);
    
    // window width in pixels
    
    width = new IntegerParameter("width");
    width.setLabel("Width");
    width.setDescription("The window width in pixels");
    width.setValue(600);
    width.setHidden(true);
    
    // window height in pixels
    
    height = new IntegerParameter("height");
    height.setLabel("Height");
    height.setDescription("The window height in pixels");
    height.setValue(506);
    height.setHidden(true);
    
    // add the parameters
    
    add(radarName);
    add(imageName);
    add(textColor);
    add(fontSize);
    add(xx);
    add(yy);
    add(width);
    add(height);
    
    // copy the values to the defaults
    
    setDefaultFromValue();
    
  }

}


