// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:38:18 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;

/*******************************************************************
 * Frame in which collection view is rendered.
 * 
 * @author Mike Dixon
 */

public class CollectionFrame extends JFrame

{

  /**
   * Collection parameter for this frame
   */
    
  private CollectionParameter _param;

  /**
   * depth of collection relative to parameter tree
   */
    
  private int _depth;

  private JPanel _topPanel;
  private JPanel _entryPanel;
  private GridBagLayout _gridbag;
  private GridBagConstraints _gbc;
  private ArrayList _paramViewList;
  private ArrayList _changeListeners;
  private boolean _active = false;

  /**
   * constructor
   *
   * @param param Collection parameter for this frame
   * @param depth depth of collection relative to parameter tree
   */
    
  public CollectionFrame(CollectionParameter param, int depth) {
	
    _param = param;
    _depth = depth;
	
    // make a new JFrame
	
    setTitle(param.getLabel());
    setLocation(50 * depth, 50 * depth);
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    addComponentListener(new MoveResizeListener());
    _paramViewList = new ArrayList();
    _changeListeners = new ArrayList();

  }

  /**
   * clear view list
   */

  public void clear() {
    _paramViewList.clear();
  }

  /**
   * add a view to the list
   */
    
  public void add(AbstractParameterView viewElement) {
    _paramViewList.add(viewElement);
  }

  /**
   * add a listener for response when an Apply action occurs
   */

  public void addChangeListener(CollectionChangeListener listener) {
    _changeListeners.add(listener);
  }

  /**
   * call all of the registered change listeners
   */

  public void callChangeListeners() {
    for (Iterator ii = _changeListeners.iterator(); ii.hasNext();) {
      CollectionChangeListener listener =
        (CollectionChangeListener) ii.next();
      listener.reactToChange();
    }
  }

  /**
   * set frame visible
   */

  public void setVisible(boolean b) {
    _param.syncParamToView();
    JrpViewParameters viewParams = JrpViewParameters.getInstance();
    int offsetX = viewParams.cascadeOffsetX.getValue() * _depth;
    int offsetY = viewParams.cascadeOffsetY.getValue() * _depth;
    CollectionParameterView ed =
      (CollectionParameterView) _param.getView();
    if (ed.getX() == -1 && ed.getY() == -1) {
      setLocation(offsetX, offsetY);
    } else {
      setLocation(ed.getX(), ed.getY());
    }
    super.setVisible(b);
  }

  /**
   * activate the GUI
   */
    
  public void activate() {

    JrpViewParameters viewParams = JrpViewParameters.getInstance();
    int offsetX = viewParams.cascadeOffsetX.getValue() * _depth;
    int offsetY = viewParams.cascadeOffsetY.getValue() * _depth;
    CollectionParameterView ed = (CollectionParameterView) _param.getView();
    if (ed.getX() == -1 && ed.getY() == -1) {
      setLocation(offsetX, offsetY);
    } else {
      setLocation(ed.getX(), ed.getY());
    }

    _topPanel = new JPanel(new BorderLayout());
    getContentPane().add(_topPanel);
	
    _entryPanel = new JPanel();
    _entryPanel.setAutoscrolls(true);
    _gridbag = new GridBagLayout();
    _gbc = new GridBagConstraints();
    _entryPanel.setLayout(_gridbag);

    for (Iterator ii = _paramViewList.iterator(); ii.hasNext();) {
      AbstractParameterView viewElement =
        (AbstractParameterView) ii.next();
      viewElement.addTo3ColContainer(_entryPanel, _gridbag);
    }
	
    JScrollPane scrollPane = new JScrollPane(_entryPanel);
    _topPanel.add(scrollPane, BorderLayout.CENTER);
	
    //JLabel headerLabel =
    //    new JLabel("Cancel will cancel and close window\n" +
    //	       "Reset will set values back to previous values.");
    //_topPanel.add(headerLabel, BorderLayout.NORTH);
	
    JButton applyButton = new JButton("Apply");
    applyButton.addActionListener(new ApplyListener());
    applyButton.setToolTipText("Accept and close");
	
    JButton resetButton = new JButton("Reset");
    resetButton.addActionListener(new ResetListener());
    resetButton.setToolTipText("Reset all changes");
	
    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new CancelListener());
    cancelButton.setToolTipText("Cancel all changes and close");

    JPanel applyPanel = new JPanel();
    applyPanel.add(applyButton);
    applyPanel.add(resetButton);
    applyPanel.add(cancelButton);

    _topPanel.add(applyPanel, BorderLayout.SOUTH);

    Border basicBorder = BorderFactory.createCompoundBorder
      (BorderFactory.createEmptyBorder(5,5,5,5),
       BorderFactory.createEtchedBorder(EtchedBorder.LOWERED,
                                        getBackground().brighter(),
                                        getBackground().darker()));
    _topPanel.setBorder
      (BorderFactory.createTitledBorder
       (basicBorder, _param.getLabel() + " - edit parameters, then Apply"));
	
    pack();
	
    if (ed.getWidth() == -1 && ed.getHeight() == -1) {
      int height = getHeight();
      int width = getWidth();
      if (height > 550) {
        height = 550;
      }
      if (width > 750) {
        width = 750;
      }
      setSize(width, height);
    } else {
      setSize(ed.getWidth(), ed.getHeight());
    }

    _active = true;
	
  }

  /**
   * get name of the collection parameter
   */
    
  public String getName() {
    return _param.getName();
  }

  /**
   * get label for collection parameter
   */

  public String getLabel() {
    return _param.getLabel();
  }

  /**
   * get top frame
   */

  public JPanel getTopPanel() {
    return _topPanel;
  }

  // Inner classes

  // listeners
    
  private class ApplyListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      int iret = 0;
      for (Iterator ii = _paramViewList.iterator(); ii.hasNext();) {
        AbstractParameterView viewElement =
          (AbstractParameterView) ii.next();
        if (viewElement.syncViewToParam() != 0) {
          iret = -1;
        }
      }
      if (iret == 0) {
        setVisible(false);
        _param.applyChanges();
      }
      callChangeListeners();
    }
  }
    
  private class ResetListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      for (Iterator ii = _paramViewList.iterator(); ii.hasNext();) {
        AbstractParameterView viewElement =
          (AbstractParameterView) ii.next();
        viewElement.syncParamToView();
        _param.applyChanges();
      }
    }
  }
    
  private class CancelListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      for (Iterator ii = _paramViewList.iterator(); ii.hasNext();) {
        AbstractParameterView viewElement =
          (AbstractParameterView) ii.next();
        viewElement.syncParamToView();
        _param.applyChanges();
      }
      setVisible(false);
    }
  }
    
  // window move/resize listener

  private class MoveResizeListener extends ComponentAdapter
  {
    public void componentMoved(ComponentEvent e) {
      if (!_active) {
        return;
      }
      int paramX = _param.getView().getX();
      int paramY = _param.getView().getY();
      if (paramX != getX() || paramY != getY()) {
        _param.getView().setPosition(getX(), getY());
        _param.setUnsavedChanges(true);
      }
    }
    public void componentResized(ComponentEvent e) {
      if (!_active) {
        return;
      }
      int paramWidth = _param.getView().getWidth();
      int paramHeight = _param.getView().getHeight();
      if (paramWidth != getWidth() || paramHeight != getHeight()) {
        _param.getView().setSize(getWidth(), getHeight());
      }
    }
  }

}
