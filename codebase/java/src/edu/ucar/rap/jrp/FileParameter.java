// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:38:17 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;
import java.io.File;
import javax.swing.*;

/*******************************************************************
 * File parameter.
 *
 * @author Mike Dixon
 */

public class FileParameter extends AbstractParameter

{

  /**
   * current value
   */
    
  private File _value = null;

  /**
   * default value
   */
    
  private File _default = null;

  /**
   * view associated with this object
   */
    
  private FileParameterView _view = null;

  /**
   * controls whether the user can select files, directories, or both.
   */

  private int _FileSelectionMode = JFileChooser.FILES_ONLY;


  /**
   * constructor
   *
   * @param name the name by which this parameter will be referenced
   */
    
  public FileParameter(String name) {
    super(name);
  }

  /**
   * set the FileSelectionMode
   * mode can be one of the following:
   * JFileChooser.FILES_ONLY
   * JFileChooser.DIRECTORIES_ONLY
   * JFileChooser.FILES_AND_DIRECTORIES
   */
  public void setFileSelectionMode(int mode)
  {
    _FileSelectionMode = mode;
  }

  /**
   * set the value from a String object
   */
    
  public void setValue(String pathname) {
    try {
      _value = new File(pathname);
    }
    catch (NullPointerException e) {
      System.err.println("ERROR - FileParameter.setValue");
      System.err.println("  pathname is null");
    }
    setUnsavedChanges(true);
  }

  /**
   * Set the file
   */

  public void setValue(File file) {
    _value = file;
  }

  /**
   * set the current value from the default value
   */
    
  public void setValueFromDefault() {
    _value = _default;
    setUnsavedChanges(true);
  }

  /**
   * set the default from the current value
   */
    
  public void setDefaultFromValue() {
    _default = _value;
  }

  /**
   * Get the parameter type
   * @return ParameterType.FILE
   */
    
  public ParameterType getType() {
    return ParameterType.FILE;
  }

  /**
   * get the current value
   */

  public File getValue() {
    return _value;
  }

  public String toString() {
    return new String(_value.toString());
  }

  public AbstractParameterView getView() {
    if (_view == null) {
      _view = new FileParameterView(this);
      _view.setFileSelectionMode(_FileSelectionMode);
    }
    return (AbstractParameterView) _view;
  }

}
