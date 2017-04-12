// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:38:20 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;

import java.io.*;

/*******************************************************************
 * Abstract base class for parameters.
 * 
 * @author Mike Dixon
 */

public abstract class AbstractParameter implements ParameterInterface

{
    
  /**
   * The name by which the client references this parameter.
   */
    
  private String _name;

  /**
   * The label which appears in the view GUI.
   */

  private String _label = "";

  /**
   * The tool-tip description in the View GUI.
   */
    
  private String _description = "";

  /**
   * Full information on the parameter.
   * In the View GUI, the user may pop up an Info window.
   * The text in this window is formed by concatenating the
   * description string with the info string.
   */

  private String _info = "";

  /**
   * Flag to control whether the parameter appears in the View GUI or not.
   * Some paramaters are not intended to be set by the user. A good example
   * is the size and position of a window. This is generally set by the code
   * when the user resizes or moves a window. For such parameters, set
   * hidden to true, to keep them from appearing in the GUI. This 
   * keeps the GUI less cluttered.
   */

  private boolean _hidden = false;

  /**
   * Flag to keep track of unsaved changes.
   * If the setValue() method is called, this will be set to true.
   */

  private boolean _unsavedChanges = false;

  /**
   * Ignore unsaved changes?
   * If true, this parameter will not set _unsavedChanges when the
   * setValue() method is called.
   */

  private boolean _ignoreChanges = false;

  /**
   * Contructor
   *
   *  @param name the name by which the client references this parameter.
   */

  public AbstractParameter(String name) {
    _name = new String(name);
  }

  /**
   * set the label which will appear in the View GUI
   */
    
  public void setLabel(String label) {
    _label = new String(label);
  }

  /**
   * Set the tooltip text for the View GUI
   */
    
  public void setDescription(String description) {
    _description = new String(description);
  }
    
  /**
   * Set the text to appear in the information window
   */
    
  public void setInfo(String info) {
    _info = new String(info);
  }

  /**
   * Set whether this parameter will appear in the GUI
   */
    
  public void setHidden(boolean value) {
    _hidden = value;
  }

  /**
   * Set whether to ignore unsaved changes
   */
    
  public void setIgnoreChanges(boolean value) {
    _ignoreChanges = value;
    if (_ignoreChanges) {
      _unsavedChanges = false;
    }
  }

  /**
   * Get the parameter name
   */
    
  public String getName() {
    return _name;
  }

  /**
   * Get the label for the view GUI
   */
    
  public String getLabel() {
    return _label;
  }

  /**
   * Get the tooltip text for the view GUI
   */
    
  public String getDescription() {
    return _description;
  }

  /**
   * Get the text for the information window
   */
    
  public String getInfo() {
    return _info;
  }

  /**
   * Is this parameter hidden from the GUI?
   */
    
  public boolean getHidden() {
    return _hidden;
  }

  /**
   * Set unsaved changes
   *
   * @param usaved true if there are unsaved changes
   *               false otherwise
   */

  protected void setUnsavedChanges(boolean unsaved) {
    if (!_ignoreChanges) {
      _unsavedChanges = false;
    } else {
      _unsavedChanges = unsaved;
    }
  }

  /**
   * does this parameter have changes which have not been saved?
   */
   
  public boolean hasUnsavedChanges() {
    if (_ignoreChanges) {
      return false;
    } else {
      return _unsavedChanges;
    }
  }

  /**
   * Synchronizes the parameter to the view.
   * Ensures that the view reflects the value of the
   * parameter.
   */

  public void syncParamToView() {
    getView().syncParamToView();
  }

  /**
   * Set view position and size to defaults.
   * When the defaults are set, the view will be placed
   * automatically.
   */
    
  public void resetViewPositionAndSize() {
    getView().setPosition(-1, -1);
    getView().setSize(-1, -1);
  }
    
  /**
   * call all registered change listeners, and recursive call
   * those on inner params
   *
   * This is an empty method, to be overridden only in the
   * case of the CollectionParameter class
   */
    
  public void callChangeListenersRecursive() {
  }

  /** Print the parameter in XML format.
   * 
   * @param pwout output object
   * @param depth Reflects the depth of the parameter in the tree. 
   *              Controls the indenting in the XML output.
   */
    
  public void printAsXml(PrintWriter pwout, int depth) {
	
    StringBuffer spacer = new StringBuffer();
    for (int i = 0; i < depth; i++) {
      spacer.append("  ");
    }

    printXmlHdr(pwout, spacer);
    pwout.println(spacer + "  <value>" + toString() + "</value>");
    pwout.println(spacer + "</param>");
    pwout.println("");
	
  }

  /**
   * Print out XML header for this parameter.
   * 
   * @param pwout  output object
   * @param spacer string to preceed text on each line
   *               useful for controlling indent formatting
   */

  protected void printXmlHdr(PrintWriter pwout, StringBuffer spacer) {

    AbstractParameterView view = getView();
    if (view == null ||
        (view.getX() == -1 && view.getY() == -1 &&
         view.getWidth() == -1 && view.getHeight() == -1)) {
	    
      pwout.println(spacer +
                    "<param" +
                    " name=\"" + getName() + "\"" +
                    " type=\"" + getType() + "\"" +
                    ">");

    } else {
	    
      pwout.println(spacer +
                    "<param" +
                    " name=\"" + getName() + "\"" +
                    " type=\"" + getType() + "\"" +
                    " view_x=\"" + view.getX() + "\"" +
                    " view_y=\"" + view.getY() + "\"" +
                    " view_width=\"" + view.getWidth() + "\"" +
                    " view_height=\"" + view.getHeight() + "\"" +
                    ">");

    }

    pwout.println(spacer + "  <label>" + getLabel() + "</label>");
    pwout.println(spacer + "  <description>" + getDescription() +
                  "</description>");
    if (getInfo().length() > 0) {
      pwout.println(spacer + "  <info>");
      pwout.println(spacer + "    <![CDATA[");
      pwout.println(spacer + "    " + getInfo());
      pwout.println(spacer + "    ]]>");
      pwout.println(spacer + "  </info>");
    }
	
  }

}
