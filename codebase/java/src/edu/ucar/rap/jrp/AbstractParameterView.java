// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:38:16 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/*******************************************************************
 * Abstract base class for parameter view.
 * 
 * @author Mike Dixon
 */

public abstract class AbstractParameterView implements ParameterViewInterface

{

  /**
   * The parameter for which this is the view.
   */
    
  private AbstractParameter _param;

  /**
   * Used to label the view in the GUI
   */

  private JLabel _jLabel;

  /**
   * The button to use for bringing up the info window
   */

  private JButton _infoButton;

  /**
   * The length of the edtor window for text fields
   */

  static final private int _textFieldLen = 24;

  /**
   * The generic editor for this view.
   * The form of the editor varies between parameter types.
   */
    
  private JComponent _editor;

  /**
   * x position of the view window
   */
    
  private int _x = -1;

  /**
   * y position of the view window
   */
    
  private int _y = -1;

  /**
   * width of the view window
   */
    
  private int _width = -1;

  /**
   * height of the view window
   */
    
  private int _height = -1;

  /**
   * Constructor
   *
   * @param param the parameter for which this is the view
   */

  public AbstractParameterView(AbstractParameter param) {
    _param = param;
    _jLabel = new JLabel(param.getLabel() + "  ");
    _jLabel.setToolTipText(param.getDescription());
    JrpDummyComponent dc = new JrpDummyComponent();
    Image infoImage =
      JrpImageLoad.getFromRes(dc, "/edu/ucar/rap/jrp/images/info.png");
    ImageIcon infoIcon = new ImageIcon(infoImage);
    _infoButton = new JButton("", infoIcon);
    _infoButton.setPreferredSize(new Dimension(infoImage.getWidth(dc),
                                               infoImage.getHeight(dc)));
    _infoButton.setToolTipText("Click for full info on "
                               + param.getLabel());
    _infoButton.addActionListener(new InfoListener(param));
  }

  /**
   * Get the JLabel component which will label the associated parameter
   */
    
  public JLabel getJLabel() {
    return _jLabel;
  }

  /**
   * Get the component which will be used for editing
   */
    
  public JComponent getEditor() {
    return _editor;
  }
    
  /**
   * Get x position of view window
   */

  public int getX() {
    return _x;
  }

  /**
   * Get y position of view window
   */

  public int getY() {
    return _y;
  }
    
  /**
   * Get width of view window
   */

  public int getWidth() {
    return _width;
  }

  /**
   * Get height of view window
   */

  public int getHeight() {
    return _height;
  }

  /**
   * Get length of text field for text-type parameters
   */

  public int getTextFieldLen() {
    return _textFieldLen;
  }

  /**
   * Set position of view window
   */
    
  public void setPosition(int x, int y) {
    _x = x;
    _y = y;
  }

  /**
   * Set size of view window
   */
    
  public void setSize(int width, int height) {
    _width = width;
    _height = height;
  }

  /**
   * Set x position of view window
   */

  protected void setX(int x) {
    _x = x;
  }

  /**
   * Set y position of view window
   */

  protected void setY(int y) {
    _y = y;
  }
    
  /**
   * Set width of view window
   */

  protected void setWidth(int width) {
    _width = width;
  }

  /**
   * Set height of view window
   */

  protected void setHeight(int height) {
    _height = height;
  }

  /**
   * Set the component to be used for editing
   */

  protected void setEditor(JComponent editor) {
    _editor = editor;
  }

  /**
   * Adds this view to the container used for editing.
   *
   * @param container the container to which this view is added
   * @param gridbag the 3-col gridbag layout manager used for the container
   */

  public void addTo3ColContainer(Container container,
                                 GridBagLayout gridbag) {

    if (_param.getHidden()) {
      return;
    }

    GridBagConstraints c = new GridBagConstraints();
	
    c.insets = new Insets(2, 3, 3, 2);
    c.anchor = GridBagConstraints.EAST;
    c.gridwidth = 1;
    c.fill = GridBagConstraints.NONE;      //reset to default
    c.weightx = 0.0;                       //reset to default
    gridbag.setConstraints(_jLabel, c);
    container.add(_jLabel);
	
    c.anchor = GridBagConstraints.WEST;
    c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
    c.fill = GridBagConstraints.NONE;      //reset to default
    c.weightx = 0.0;                       //reset to default
    gridbag.setConstraints(_editor, c);
    container.add(_editor);
	
    c.anchor = GridBagConstraints.CENTER;
    c.gridwidth = GridBagConstraints.REMAINDER;     //end row
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 1.0;
    gridbag.setConstraints(_infoButton, c);
    container.add(_infoButton);
  }

  // Inner classes for listeners

  private class InfoListener implements ActionListener
  {
    private AbstractParameter _param;
    public InfoListener(AbstractParameter param) {
      _param = param;
    }
    public void actionPerformed(ActionEvent event)
    {
      ParameterInformationFrame infoFrame = ParameterInformationFrame.getInstance();
      infoFrame.show(_param);
    }
  }

}
