// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:38:0 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;

import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import org.jdom.*;

/*******************************************************************
 * Collection parameter.
 *
 * A CollectionParametereter is a collection of parameters of any type.
 * 
 * @author Mike Dixon
 */

public class CollectionParameter extends AbstractParameter
    
{

  /**
   * Depth of this collection in the parameter tree
   */
    
  private int _depth;

  /**
   * Array of parameter objects in this collection
   */
    
  private ArrayList _paramList = new ArrayList(); // array of param objects

  /**
   * view associated with this object
   */
    
  private CollectionParameterView _view = null;
    
  /**
   * constructor
   *
   * @param name the name by which this parameter will be referenced
   * @param label the label in the GUI one level above this one
   * @param depth the depth of this collection in the parameter tree
   */
    
  public CollectionParameter(String name, String label, int depth) {
    super(name);
    setLabel(label);
    _depth = depth;
  }

  /**
   * For this class this is a no-op.
   * It is overridden in the ParameterManager class which sub-classes
   * from this one.
   */

  public void applyChanges() {
  }
    
  /**
   * add a listener to be called when an Apply action occurs
   */
    
  public void addChangeListener(CollectionChangeListener listener) {
    _view.getFrame().addChangeListener(listener);
  }
    
  /**
   * call all registered change listeners
   */
    
  public void callChangeListeners() {
    _view.getFrame().callChangeListeners();
  }
    
  /**
   * override method to call all registered change listeners, 
   * and recursive call those on inner params
   */
    
  public void callChangeListenersRecursive() {
    for (int ii = 0; ii < getNParams(); ii++) {
      getParam(ii).callChangeListenersRecursive();
    }
    callChangeListeners();
  }

  /**
   * get the frame component for the view associated with this object
   */
    
  public CollectionFrame getCollectionFrame() {
    getView(); // make sure view exists
    return _view.getFrame();
  }

  /**
   * get the edit action for the view associated with this object
   */

  public AbstractAction getViewEditAction() {
    getView(); // make sure view exists
    return _view.getEditAction();
  }
    
  /**
   * get the editor for the view associated with this object
   */

  public JComponent getViewEditor() {
    return getView().getEditor();
  }
    
  /**
   * This is a no-op for this class
   */

  public void setValue(String value) {
  }
    
  /**
   * Add a parameter to the collection
   */

  public void add(AbstractParameter param) {
    _paramList.add(param);
  }
    
  /**
   * Activate the frames recursively if they are not hidden.
   * Should be called after all parameters have been added to collection.
   */

  public void activateFrame() {
    getView(); // make sure view exists
    _view.activateFrame();
  }
    
  /**
   * For each parameter in the collection,
   * set the current value from the default value
   */

  public void setValueFromDefault() {
    for (int ii = 0; ii < getNParams(); ii++) {
      AbstractParameter aparam = getParam(ii);
      aparam.setValueFromDefault();
    }
  }

  /**
   * For each parameter in the collection,
   * set the default value from the current value
   */

  public void setDefaultFromValue() {
    for (int ii = 0; ii < getNParams(); ii++) {
      AbstractParameter aparam = getParam(ii);
      aparam.setDefaultFromValue();
    }
  }
    
  /**
   * Get the parameter type
   * @return ParameterType.COLLECTION
   */
    
  public ParameterType getType() {
    return ParameterType.COLLECTION;
  }

  /**
   * get depth of this collection relative to parameter tree
   */

  public int getDepth() {
    return _depth;
  }

  /**
   * get depth of next level down relative to parameter tree
   */

  public int getNextDepth() {
    return _depth + 1;
  }
    
  /**
   * get number of parameters in collection
   */
    
  public int getNParams() {
    return _paramList.size();
  }

  /**
   * get a parameter from the collection
   * @param index the location of the desired parameter
   */
    
  public AbstractParameter getParam(int index)
    throws IndexOutOfBoundsException {
    return (AbstractParameter) _paramList.get(index);
  }

  /**
   * Get the View for this object
   * The creation of the View is delayed until the first use so that
   * the properties of the object, such as label and description,
   * can be set first.
   */
    
  public AbstractParameterView getView() {
    if (_view == null) {
      _view = new CollectionParameterView(this, _depth);
    }
    return (AbstractParameterView) _view;
  }

  /**
   * For each parameter in collection, set flag to indicate
   * unsaved status
   */

  public void setUnsavedChanges(boolean unsaved) {
    for (int ii = 0; ii < getNParams(); ii++) {
      getParam(ii).setUnsavedChanges(unsaved);
    }
  }
    
  /**
   * For each parameter in collection, check status of unsaved changes.
   * @return true   if any parameter in this collection, or at any level
   *                below, has unsaved changes
   *         false  if no parameter in this collection, or at any level
   *                below, has unsaved changes
   */

  public boolean hasUnsavedChanges() {
    for (int ii = 0; ii < getNParams(); ii++) {
      if (getParam(ii).hasUnsavedChanges()) {
        return true;
      }
    }
    return false;
  }

  /**
   * For each param in the collection,
   * ensure view shows the current parameter value.
   */
    
  public void syncParamToView() {
    for (int ii = 0; ii < getNParams(); ii++) {
      getParam(ii).syncParamToView();
    }
  }

  /**
   * print the collection and all collections below it to
   * the output object in XML format
   */

  public void printAsXml(PrintWriter pwout, int depth) {

    StringBuffer spacer = new StringBuffer();
    for (int i = 0; i < depth; i++) {
      spacer.append("  ");
    }

    pwout.println(spacer + "<!-- ====== start of collection "
                  + getName() + " ====== -->");
    pwout.println("");
    printXmlHdr(pwout, spacer);
    pwout.println("");
	
    for (int ii = 0; ii < getNParams(); ii++) {
      getParam(ii).printAsXml(pwout, depth + 1);
    }

    pwout.println(spacer + "</param>");
    pwout.println("");
    pwout.println(spacer + "<!-- ======= end of collection " +
                  getName() + " ======= -->");
    pwout.println("");
	
  }

  /**
   * reset view window and size to defaults
   */
    
  public void resetViewPositionAndSize() {
    for (int ii = 0; ii < getNParams(); ii++) {
      getParam(ii).resetViewPositionAndSize();
    }
    getView().setPosition(-1, -1);
    getView().setSize(-1, -1);
  }
    
  /**
   * load parameter values from JDom XML representation
   */

  public void loadFromXml(Element top)

  {
	
    // make sure the top element points to a collection
    // of the correct name
	
    String nameAtt = top.getAttribute("name").getValue();
    String typeAtt = top.getAttribute("type").getValue();
	
    if (!nameAtt.equals(getName())) {
      System.err.println("ERROR - incorrect name: " + nameAtt);
      System.err.println("        Expecting name: " + getName());
      return;
    }
    if (!typeAtt.equals("Collection")) {
      System.err.println("ERROR - incorrect type: " + typeAtt);
      System.err.println("        Expecting type: " + "Collection");
      return;
    }
	
    // iterate through the params in this collection,
    // matching up with the correct XML entry
	
    for (int ii = 0; ii < getNParams(); ii++) {
	    
      AbstractParameter aparam = getParam(ii);
      Iterator itt = top.getChildren().iterator();
      while (itt.hasNext()) {
        Element child = (Element) itt.next();
        if (child.getName().equals("param")) {
          Attribute childNameAtt = child.getAttribute("name");
          if (childNameAtt != null &&
              childNameAtt.getValue().equals(aparam.getName())) {
            loadParam(child, aparam);
            break;
          }
        }
      } // while

    } // ii

    // set position and size if attributes are set
	
    Attribute xAtt = top.getAttribute("view_x");
    Attribute yAtt = top.getAttribute("view_y");

    if (xAtt != null && yAtt != null) {
      int xx = -1, yy = -1;
      boolean ok = true;
      try {
        xx = Integer.parseInt(xAtt.getValue());
        yy = Integer.parseInt(yAtt.getValue());
      } catch (NumberFormatException e) {
        ok = false;
      }
      if (ok) {
        CollectionParameterView ed = (CollectionParameterView)getView();
        ed.setPosition(xx, yy);
      }
    }
	
    Attribute widthAtt = top.getAttribute("view_width");
    Attribute heightAtt = top.getAttribute("view_height");

    if (widthAtt != null && heightAtt != null) {
      int width = -1, height = -1;
      boolean ok = true;
      try {
        width = Integer.parseInt(widthAtt.getValue());
        height = Integer.parseInt(heightAtt.getValue());
      } catch (NumberFormatException e) {
        ok = false;
      }
      if (ok) {
        CollectionParameterView ed = (CollectionParameterView)getView();
        ed.setSize(width, height);
      }
    }
	
  } // loadFromXml

    /**
     * load given parameter given JDOM element
     * @param top JDOM element from which to start looking for values
     * @param aparam parameter to be loaded
     */
    
  private void loadParam(Element top, AbstractParameter aparam) {

    // is this a collection?
	
    String nameAtt = top.getAttribute("name").getValue();
    String typeAtt = top.getAttribute("type").getValue();
    if (typeAtt.equals("Collection")) {
      CollectionParameter cparam = (CollectionParameter) aparam;
      cparam.loadFromXml(top);
      return;
    }

    List children = top.getChildren();
    Iterator itt = children.iterator();

    // set value from the XML text
	
    Iterator jtt = children.iterator();
    while (jtt.hasNext()) {
      Element child = (Element) jtt.next();
      if (child.getName().equals("value")) {
        aparam.setValue(child.getText());
      }
    }
	
  }

  /**
   * Check if this collection matches the JDOM representation.
   * Used for debugging purposes.
   * @param top JDOM element from which to start looking for values
   * @param aparam parameter to be matched
   */

  private boolean collectionMatches(Element top, AbstractParameter aparam) {

    List children = top.getChildren();
    Iterator itt = children.iterator();
    boolean nameMatches = false;
    while (itt.hasNext()) {
      Element child = (Element) itt.next();
      if (child.getName().equals("name") &&
          child.getText().equals(aparam.getName())) {
        nameMatches = true;
        break;
      }
    }
	
    if (!nameMatches) {
      return false;
    }
	
    CollectionParameter cparam = (CollectionParameter) aparam;
    cparam.loadFromXml(top);
    return true;
	
  }

  /**
   * List children of JDOM element.
   * Used for debugging purposes.
   * @param element JDOM element for which children are needed.
   */

  public void listChildren(Element current) {
	
    System.out.println("--->Child:");
    System.out.println("    xml name: " + current.getName());
    System.out.println("    xml text: " + current.getText());
    List children = current.getChildren();
    Iterator itt = children.iterator();
    while (itt.hasNext()) {
      Element child = (Element) itt.next();
      // listChildren(child);
    }
	
  }
    
}
