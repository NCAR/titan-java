// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:37:57 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;

import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/*******************************************************************
 * Top-level parameter manager
 * 
 * @author Mike Dixon
 */

public class ParameterManager extends CollectionParameter
    
{
    
  private Component _parentComponent = null;
  private String _paramFilePath = null;
  private String _mainWindowLabel = null;
  private NewAction _newAction = null;
  private FileOpenAction _fileOpenAction = null;
  private FileSaveAction _fileSaveAction = null;
  private FileSaveAsAction _fileSaveAsAction = null;
  private EditAction _editAction = null;
  private ResetViewAction _resetViewAction = null;
  private ArrayList _activeFileLabels = new ArrayList();

 /**
   * Default constrctor
   *
   * If this constructor is used, consider whether after construction you
   * should call the following methods:
   *
   *  setParentComponent()   - sets parent component for dialogs.
   *                           Only needed if the GUI will be active.
   *
   *  setParameterFilePath() - set the initial parameter file path.
   */
    
  public ParameterManager() {
    super("Root", "Param Manager", 1);
    setLabel("Param manager");
    setDescription("Root-depth parameter manager");
  }
    
  /**
   * Constrctor
   *
   * @param parentComponent       top-level component
   * @param initParamPath  initial XML parameter file path
   */
    
  public ParameterManager(Component parentComponent,
                          String initParamPath) {
    super("Root", "Param Manager", 1);
    setLabel("Param manager");
    setDescription("Root-depth parameter manager");
    _parentComponent = parentComponent;
    _paramFilePath = initParamPath;
  }

  /**
   * Set top panel
   */

  public void setParentComponent(Component parentComponent) {
    _parentComponent = parentComponent;
  }

  /**
   * set param file path
   *
   * Side-effect: also sets the text for each JLabel which has been
   * created to display the active file path.
   */

  public void setParameterFilePath(String path) {
    _paramFilePath = path;
    for (Iterator ii = _activeFileLabels.iterator(); ii.hasNext();) {
      JLabel label = (JLabel) ii.next();
      _setActiveFileLabel(label);
    }
  }

  /**
   * set main window label
   *
   * Overrides param file path as main window label
   * If not set, _paramFilePath is used instead
   */

  public void setMainWindowLabel(String path) {
    _mainWindowLabel = path;
    for (Iterator ii = _activeFileLabels.iterator(); ii.hasNext();) {
      JLabel label = (JLabel) ii.next();
      _setActiveFileLabel(label);
    }
  }

  /**
   * get param file path
   */

  public String getParameterFilePath() {
    return _paramFilePath;
  }

  /**
   * create a JMenu for the top-level collection
   *
   * @param label the label to appear in the menu
   */
    
  public JMenu createMenu(String label) {

    JMenu menu = new JMenu(label);
	
    // new-file button

    JrpDummyComponent dc = new JrpDummyComponent();
    Image newImage = JrpImageLoad.getFromRes
      (dc, "/edu/ucar/rap/jrp/images/new_file.png");
    ImageIcon newIcon = new ImageIcon(newImage);
    _newAction = new NewAction("New", newIcon);
    JMenuItem newItem = menu.add(_newAction);
    newItem.setMnemonic('N');
    newItem.setToolTipText("Start new default config");
	
    // open-file button

    Image openImage = JrpImageLoad.getFromRes
      (dc, "/edu/ucar/rap/jrp/images/open.png");
    ImageIcon openIcon = new ImageIcon(openImage);
    _fileOpenAction = new FileOpenAction("Open", openIcon);
    JMenuItem openItem = menu.add(_fileOpenAction);
    openItem.setMnemonic('O');
    openItem.setToolTipText("Open existing config");
	
    // edit button
	
    Image editImage = JrpImageLoad.getFromRes
      (dc, "/edu/ucar/rap/jrp/images/edit.png");
    ImageIcon editIcon = new ImageIcon(editImage);
    _editAction = new EditAction("Edit", editIcon);
    JMenuItem editItem = menu.add(_editAction);
    editItem.setMnemonic('E');
    editItem.setToolTipText("Edit the parameters");

    // save button
	
    Image saveImage = JrpImageLoad.getFromRes
      (dc, "/edu/ucar/rap/jrp/images/save.png");
    ImageIcon saveIcon = new ImageIcon(saveImage);
    _fileSaveAction = new FileSaveAction("Save", saveIcon);
    // _fileSaveAction.setEnabled(false);
    JMenuItem saveItem = menu.add(_fileSaveAction);
    saveItem.setMnemonic('S');
    saveItem.setToolTipText("Save this config");
	
    // save-as-file button
	
    _fileSaveAsAction = new FileSaveAsAction("Save as");
    JMenuItem saveAsItem = menu.add(_fileSaveAsAction);
    saveAsItem.setMnemonic('A');
    saveAsItem.setToolTipText("Save this config to a new file");
	
    // reset view button
	
    _resetViewAction = new ResetViewAction("Reset", null);
    JMenuItem resetView = menu.add(_resetViewAction);
    editItem.setMnemonic('R');
    editItem.setToolTipText("Reset view panel locations");

    return menu;

  }

  /**
   * get Action for 'File New'
   */

  public AbstractAction getNewAction() { 
    return _newAction;
  }

  /**
   * get Action for 'File Open'
   */

  public AbstractAction  getFileOpenAction() {
    return _fileOpenAction;
  }

  /**
   * get Action for 'File Save'
   */

  public AbstractAction  getFileSaveAction() {
    return _fileSaveAction;
  }

  /**
   * get Action for 'File Save As'
   */

  public AbstractAction  getFileSaveAsAction() {
    return _fileSaveAsAction;
  }

  /**
   * get Action for 'Edit'
   */

  public AbstractAction  getEditAction() {
    return _editAction;
  }

  /**
   * create a label indicating the active file path
   * return JLabel object
   */

  public JLabel createActiveFileLabel() {
    JLabel label = new JLabel();
    label.setHorizontalAlignment(SwingConstants.CENTER);
    label.setVerticalAlignment(SwingConstants.CENTER);
    label.setBorder(JrpBorder.createSimple(label, 1));
    _activeFileLabels.add(label);
    _setActiveFileLabel(label);
    return label;
  }

  /**
   * release use of an active file label
   */

  public void releaseActiveFileLabel(JLabel label) {
    _activeFileLabels.remove(label);
  }

  /**
   * print the parameters out to a file as XML
   */
    
  public void saveAsXml() throws IOException {
    saveAsXml(_paramFilePath);
  }

  /**
   * print the parameters out to a file as XML
   *
   * @param paramFilePath file path to which to write the XML
   * @throws IOException
   */
    
  public void saveAsXml(String paramFilePath) throws IOException {
	
    // open output file
	
    FileWriter fw = new FileWriter(paramFilePath);

    PrintWriter pwout = new PrintWriter(fw);
	
    pwout.println("<?xml version=\"1.0\" " +
                  "encoding=\"ISO-8859-1\" " +
                  "standalone=\"yes\"?>");
    pwout.println("<!DOCTYPE parameters [");
    pwout.println(" <!ELEMENT parameters (param+) >");
    pwout.println(" <!ELEMENT param" +
                  " (label?, description?, info?, value?, param*) >");
    pwout.println(" <!ELEMENT label (#PCDATA)>");
    pwout.println(" <!ELEMENT description (#PCDATA)>");
    pwout.println(" <!ELEMENT info (#PCDATA)>");
    pwout.println(" <!ELEMENT value (#PCDATA)>");
    pwout.println(" <!ATTLIST param name NMTOKEN #REQUIRED>");
    pwout.println(" <!ATTLIST param type NMTOKEN #REQUIRED>");

    pwout.println(" <!ATTLIST param type ( " +
                  ParameterType.STRING + " | " +
                  ParameterType.BOOLEAN + " | " +
                  ParameterType.INTEGER + " | " +
                  ParameterType.FLOAT + " | " +
                  ParameterType.DOUBLE + " | " +
                  ParameterType.OPTION + " | " +
                  ParameterType.COLLECTION +
                  " ) #REQUIRED>");

    pwout.println(" <!ATTLIST param view_x NMTOKEN #IMPLIED>");
    pwout.println(" <!ATTLIST param view_y NMTOKEN #IMPLIED>");
    pwout.println(" <!ATTLIST param view_width NMTOKEN #IMPLIED>");
    pwout.println(" <!ATTLIST param view_height NMTOKEN #IMPLIED>");

    pwout.println("]>");
    pwout.println("");

    pwout.println("<parameters>");
    pwout.println("");
	
    printAsXml(pwout, 1);

    pwout.println("</parameters>");
    pwout.println("");
    pwout.close();

    setUnsavedChanges(false);
    // _fileSaveAction.setEnabled(false);

  }

  /**
   * initialize parameters from an XML file
   */
    
  public void initFromXml() {

    if (_paramFilePath == null) {
      setUnsavedChanges(false);
      return;
    }
	
    readFromXml();
	
  }

  /**
   * read parameters from an XML file
   *
   * @return returns true on success, false on failure
   */
    
  public boolean readFromXml() {
	
    if (_paramFilePath == null) {
      return false;
    }
	
    if (!readFromXml(_paramFilePath)) {
      System.err.println("*******************************");
      setParameterFilePath(null);
      return false;
    }

    return true;
	
  }

  /**
   * read parameters from an XML file
   *
   * @param paramFilePath the file path containing the XML
   * @return returns true on success, false on failure
   */
    
  public boolean readFromXml(String paramFilePath) {
	
    if (!_doReadFromXml(paramFilePath)) {
      // error - show error dialog
      String errStr =
        "ParameterManager.readFromXml(): \n" +
        "Cannot initialize params from file: " +
        paramFilePath + "\n" +
        "Will use default parameters";
      if (_parentComponent != null) {
        JOptionPane.showMessageDialog(_parentComponent, errStr,
                                      "Bad param file",
                                      JOptionPane.ERROR_MESSAGE);
      } else {
        System.out.println("ERROR - readFromXml");
        System.out.println("  " + errStr);
      }
      return false;
    }
    setUnsavedChanges(false);
    // 	if (_fileSaveAction != null){
    // 	    _fileSaveAction.setEnabled(false);
    // 	}
    return true;

  }

  /**
   * read parameters from an XML buffer
   *
   * @param buf the buffer containing the XML
   * @return returns true on success, false on failure
   */
    
  public boolean setFromXmlBuffer(ByteBuffer buf) {
	
    ByteArrayInputStream in = new ByteArrayInputStream(buf.array());
    SAXBuilder builder = new SAXBuilder();
    Document doc;
	
    try {
      doc = builder.build(in);
      // If there are no well-formedness errors, 
      // then no exception is thrown
    }
    // JDOMException indicates a well-formedness error
    catch (JDOMException e) { 
      System.err.println("ERROR - ParameterManager.setFromXmlBuffer");
      System.err.println("  Parsing XML buffer");
      System.err.println("  Buffer is not well-formed.");
      System.err.println("  " + e.getMessage());
      e.printStackTrace();
      return false;
    }
    // IOException indicates reading error
    catch (IOException e) { 
      System.err.println("ERROR - ParameterManager.setFromXmlBuffer");
      System.err.println("  Parsing XML buffer");
      System.err.println("  Buffer is not well-formed.");
      System.err.println("  " + e.getMessage());
      e.printStackTrace();
      return false;
    }
	
    if (!_setFromDoc(doc)) {
      System.err.println("ERROR - setFromXmlBuffer");
      return false;
    }
	
    return true;
	
  }

  ///////////////////////////
  // read from XML using JDom
  // returns true on success, false on failure

  private boolean _doReadFromXml(String paramFilePath) {
	
    SAXBuilder builder = new SAXBuilder();
    Document doc;
	
    try {
      doc = builder.build(paramFilePath);
      // If there are no well-formedness errors, 
      // then no exception is thrown
    }
    // JDOMException indicates a well-formedness error
    catch (JDOMException e) { 
      System.err.println("ERROR - ParameterManager._doReadFromXml");
      System.err.println("  Parsing XML param file");
      System.err.println("  " + paramFilePath + " is not well-formed.");
      System.err.println("  " + e.getMessage());
      e.printStackTrace();
      return false;
    }
    // IOException indicates reading error
    catch (IOException e) { 
      System.err.println("ERROR - ParameterManager._doReadFromXml");
      System.err.println("  Parsing XML param file");
      System.err.println("  " + paramFilePath + " is not well-formed.");
      System.err.println("  " + e.getMessage());
      e.printStackTrace();
      return false;
    }

    if (!_setFromDoc(doc)) {
      System.err.println("ERROR - _doReadFromXml");
      return false;
    }
	
    return true;

  }

  ///////////////////////////
  // set from JDOM Document
  // returns true on success, false on failure

  private boolean _setFromDoc(Document doc) {
	
    Element root = doc.getRootElement();
    List children = root.getChildren();
    Iterator iterator = children.iterator();
    if (iterator.hasNext()) {
      Element topCollectionElement = (Element) iterator.next();
      try {
        loadFromXml(topCollectionElement);
      }
      catch (Exception e) {
        System.err.println("ERROR - ParameterManager._setFromDoc");
        System.err.println("  Problem with XML");
        System.err.println(e.getMessage());
        System.err.println("-------------------------------------");
        e.printStackTrace();
        return false;
      }
    }
	
    return true;
  }

  /**
   * set the active file path in a provided label
   */
    
  private void _setActiveFileLabel(JLabel label) {
    if (_mainWindowLabel != null) {
      label.setText(_mainWindowLabel);
    } else if (_paramFilePath == null) {
      label.setText("Current param file: none");
    } else {
      File paramFile = new File(_paramFilePath);
      label.setText("Current param file: " +
                    paramFile.getName());
    }
  }

  /**
   * print the given number of spaces to stdout
   */
    
  private static void printSpaces(int n) {
	
    for (int i = 0; i < n; i++) {
      System.out.print(' '); 
    }
	
  }

  /**
   * apply changes which are pending
   */
    
  public void applyChanges() {
    setFileSaveActionState();
  }
    
  /**
   * set the file save action enabled state
   */

  public void setFileSaveActionState() {
    if (hasUnsavedChanges()) {
      _fileSaveAction.setEnabled(true);
    } else {
      // _fileSaveAction.setEnabled(false);
    }
  }

  /**
   * check if there are unsaved changes to be saved
   *
   * @return true if caller can proceed<p>
   *         false if caller should not proceed
   */
    
  public boolean checkUnsavedChanges() {

    if (hasUnsavedChanges()) {

      if (_paramFilePath == null) {

        if (_parentComponent == null) {
          return false;
        }
		
        String title = "Checking on unsaved changes";
        String message[] = {
          "Parameters have not been saved.\n",
          "Save changes before proceeding?"
        };
        int result = JOptionPane.showConfirmDialog
          (_parentComponent, message, title,
           JOptionPane.YES_NO_OPTION,
           JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
          _fileSaveAsAction.actionPerformed(null);
          return true;
        } else if (result == JOptionPane.NO_OPTION) {
          return true;
        } else {
          return false;
        }
		
      } else { // if (_paramFilePath == null)
		
        if (_parentComponent == null) {
          return false;
        }
		
        String title = "Checking on unsaved changes";
        String message[] = {
          "Parameters have not been saved.\n",
          "Save changes in " + _paramFilePath + " before proceeding?"
        };
        int result = JOptionPane.showConfirmDialog
          (_parentComponent, message, title,
           JOptionPane.YES_NO_OPTION,
           JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
          _fileSaveAction.actionPerformed(null);
          return true;
        } else if (result == JOptionPane.NO_OPTION) {
          return true;
        } else {
          return false;
        }
		
      } // if (_paramFilePath == null)

    } else {
	    
      return true;

    }

  }

  ////////////////////////////////////////////////////
  // Inner classes for actions
  ////////////////////////////////////////////////////

  // new file action

  private class NewAction extends AbstractAction
  {
    public NewAction(String label, ImageIcon icon)
    {
      super(label, icon);
    }
	
    public void actionPerformed(ActionEvent event)
    {
      if (!checkUnsavedChanges()) {
        return;
      }
      setValueFromDefault();
      syncParamToView();
      _paramFilePath = null;
      callChangeListenersRecursive();
      // _fileSaveAction.setEnabled(false);
    }
  }

  // file open action

  private class FileOpenAction extends AbstractAction
  {
    public FileOpenAction(String label, ImageIcon icon)
    {
      super(label, icon);
    }
	
    public void actionPerformed(ActionEvent event)
    {
      if (!checkUnsavedChanges()) {
        return;
      }
      JFileChooser chooser = new JFileChooser(_paramFilePath);
      int state = chooser.showOpenDialog(null);
      File file = chooser.getSelectedFile();
      if (file != null &&
          state == JFileChooser.APPROVE_OPTION) {
        if (!readFromXml(file.getPath())) {
          return;
        }
        setParameterFilePath(file.getPath());
        callChangeListenersRecursive();
      }
    }
  }
    
  // edit action

  private class EditAction extends AbstractAction
  {
	
    public EditAction(String label, ImageIcon icon)
    {
      super(label, icon);
    }
	
    public void actionPerformed(ActionEvent event)
    {
      syncParamToView();
      getCollectionFrame().setVisible(true);
    }
  }
    
  // file save action

  private class FileSaveAction extends AbstractAction
  {
    public FileSaveAction(String label, ImageIcon icon)
    {
      super(label, icon);
    }
	
    public void actionPerformed(ActionEvent event)
    {

      if (_parentComponent == null) {
        return;
      }
		
      if (_paramFilePath == null) {
        // show error dialog
        String errStr = "Cannot save - no file specified";
        JOptionPane.showMessageDialog(_parentComponent,
                                      errStr,
                                      "Illegal save",
                                      JOptionPane.ERROR_MESSAGE);
      } else {
        String title = "Saving parameters to file";
        String message[] = {
          "Overwrite file: " + _paramFilePath
        };
        int result = JOptionPane.showConfirmDialog
          (_parentComponent, message, title,
           JOptionPane.YES_NO_OPTION,
           JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
          try {
            saveAsXml();
          }
          catch (IOException e) {
            // show error dialog
            String errStr = e.getMessage();
            JOptionPane.showMessageDialog
              (_parentComponent,
               errStr,
               "Illegal save",
               JOptionPane.ERROR_MESSAGE);
            return;
          }
        }
      }
    }
  }

  // file save-as action

  private class FileSaveAsAction extends AbstractAction
  {
    public FileSaveAsAction(String label)
    {
      super(label, null);
    }
	
    public void actionPerformed(ActionEvent event)
    {
      if (_parentComponent == null) {
        return;
      }
      JFileChooser chooser = new JFileChooser(_paramFilePath);
      int state = chooser.showSaveDialog(null);
      File file = chooser.getSelectedFile();
      if (file != null &&
          state == JFileChooser.APPROVE_OPTION) {
        try {
          saveAsXml(file.getPath());
        }
        catch (IOException e) {
          // show error dialog
          String errStr = e.getMessage();
          JOptionPane.showMessageDialog
            (_parentComponent,
             errStr,
             "Illegal save",
             JOptionPane.ERROR_MESSAGE);
          return;
        }
        setParameterFilePath(file.getPath());
      }
		
    }
  }

  // restore default menu locations
    
  private class ResetViewAction extends AbstractAction
  {
	
    public ResetViewAction(String label, ImageIcon icon)
    {
      super(label, icon);
    }
	
    public void actionPerformed(ActionEvent event)
    {
      resetViewPositionAndSize();
    }
  }
    
}
