///////////////////////////////////////////////////////////////////////
//
// WorldImage
//
// World-coord plotting in an image.
//
// Mike Dixon
//
// Sept 2004
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.awt.*;
import java.util.*;
import java.awt.geom.*;
import java.awt.image.*;

public class WorldImage extends WorldPlot

{

    private BufferedImage _image = null;

    // constructor

    public WorldImage(int imageWidth,
		      int imageHeight,
		      double xMinWorld,
		      double xMaxWorld,
		      double yMinWorld,
		      double yMaxWorld) {

	super(imageWidth, imageHeight,
	      0, 0, 0, 0,
	      xMinWorld, xMaxWorld, yMinWorld, yMaxWorld);
	
	_image = new BufferedImage(imageWidth,
				   imageHeight,
				   BufferedImage.TYPE_INT_RGB);

    }

    // set a pixel to color, given world coords

    public void setRGB(double xx, double yy, int rgb) {

	_image.setRGB(getIxDevice(xx), getIyDevice(yy), rgb);

    }
    
    // draw image into target graphics, scaling and translating to
    // map the world coordinates of the world plot
    
    public void drawImageToGraphics(Graphics2D g2, WorldPlot plot) {
	
	plot.drawImage(g2, _image, _xMinWorld, _xMaxWorld, _yMinWorld, _yMaxWorld);

    }

}
