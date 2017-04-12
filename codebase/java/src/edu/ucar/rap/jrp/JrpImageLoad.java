// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:38:20 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;

/***********************************
 * Class for loading images.
 * <p>
 * Code copied from graphics/ImageUtilities.java
 *
 * @author Arnaud Dumont
 */

public class JrpImageLoad {

  /***********************************
   * Downloads the given image.  Doesn't return until the image is
   * fully downloaded.  Note that the component used in the download
   * can be the GenericComponent defined in this package.
   *
   * @param image_file   the filename for the image file.
   * @param component    the component whose ImageObserver interface
   *                       will be used when downloading the image.
   *
   * @return             the downloaded image.
   *
   * @see edu.ucar.rap.jrp.JrpDummyComponent
   */

  public static Image getFromFile(Component component, String image_file) 
  {
    // Create the image object.
    Image image = Toolkit.getDefaultToolkit().getImage(image_file);

    // Wait for the image to be downloaded.
    MediaTracker media_tracker = new MediaTracker(component);
    media_tracker.addImage(image, 0);
    try {
      media_tracker.waitForID(0);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
    return image;
  }

  /***********************************************
   * Downloads the given image from a java resource. The resource can be a 
   * file in the users classpath or a file inside of a java archive. Allows 
   * images to be located as easily for applets or applications. This method
   * gets around the bug in Netscape's ClassLoader implementation which 
   * prevents getResource() from succeeding in returning a URL reference.
   * To get around the bug in Netscape 6, the Component context must be the 
   * same object which will draw the image.
   *
   * @param   context  a Component from whom's Class Loader to locate the 
   *                  resource and which will track the loading of the image
   * @param   resource  a String indicating the file or full class name to load
   * @return  a reference to the loaded Image or null if the image is not found
   */

  public static Image getFromRes(Component context, String resource)
  {  
    Image image;

    // Set up the ByteStream for loading the image resource
    try { 
      InputStream in = context.getClass().getResourceAsStream(resource); 
      if (in == null) { 
        System.err.println("Image \"" + resource + "\" not found."); 
        return null; 
      } 
      
      // read the image stream
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      int c;
      while((c = in.read()) >= 0) {
        baos.write(c);       
      }
      image = Toolkit.getDefaultToolkit().createImage(baos.toByteArray()); 
    } catch (java.io.IOException e) { 
      System.err.println("Unable to read image \"" + resource + ".\""); 
      e.printStackTrace(); 
      return null;
    }

    // Wait for the image to be downloaded.
    MediaTracker media_tracker = new MediaTracker(context);
    media_tracker.addImage(image, 0);
    try {
      media_tracker.waitForID(0);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
    return image;
  }

  /**
   * Downloads an image from the given URL. The resource can be a 
   * file in the users classpath or a file inside of a java archive. Allows 
   * images to be located as easily for applets or applications. This method
   * gets around the bug in Netscape's ClassLoader implementation which 
   * prevents getResource() from succeeding in returning a URL reference.
   * To get around the bug in Netscape 6, the Component context must be the 
   * same object which will draw the image.
   *
   * @param context  the Component which will track the loading of the image
   * @param url      the URL to the file or java archive component to load
   * @return  a reference to the loaded Image or null if the image is not found
   */

  public static Image getFromUrl(Component context, URL url)
  {  
    Image image;

    // Set up the ByteStream for loading the image resource
    try { 
      InputStream in = url.openConnection().getInputStream(); 
      if (in == null) { 
        System.err.println("Image not found at URL: " + url); 
        return null; 
      } 
      
      // read the image stream
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      int c;
      while((c = in.read()) >= 0) {
        baos.write(c);       
      }
      image = Toolkit.getDefaultToolkit().createImage(baos.toByteArray()); 
    } catch (java.io.IOException e) { 
      System.err.println("Unable to read image from URL \"" + url + ".\""); 
      e.printStackTrace(); 
      return null;
    }

    // Wait for the image to be downloaded.
    MediaTracker media_tracker = new MediaTracker(context);
    media_tracker.addImage(image, 0);
    try {
      media_tracker.waitForID(0);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
    return image;
  }

}
