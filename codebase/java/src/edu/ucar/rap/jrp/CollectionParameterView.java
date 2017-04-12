// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:38:3 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;

import javax.swing.*;
import java.awt.Image;
import java.awt.event.*;

/*******************************************************************
 * Collection parameter view.
 *
 * A CollectionParametereter is a collection of parameters of any type.
 * 
 * @author Mike Dixon
 */

public class CollectionParameterView extends AbstractParameterView

{

  /**
   * editor component
   */
    
  private JButton _editButton;

  /**
   * editor component action
   */
    
  private EditAction _editAction;

  /**
   * parameter associated with this object
   */
    
  private CollectionParameter _param;

  /**
   * frame component for view
   */
    
  private CollectionFrame _frame;

  /**
   * constructor
   *
   * @param param parameter to be associated with this object
   * @param depth depth of this collection in the parameter tree
   */
    
  public CollectionParameterView(CollectionParameter param, int depth) {
    super(param);
    _param = param;
    _frame = new CollectionFrame(param, depth);
    JrpDummyComponent dc = new JrpDummyComponent();
    Image editImage =
      JrpImageLoad.getFromRes(dc, "/edu/ucar/rap/jrp/images/edit_small.png");
    ImageIcon editIcon = new ImageIcon(editImage);
    _editAction = new EditAction("Edit", editIcon);
    _editButton = new JButton(_editAction);
    setEditor(_editButton);
    getEditor().setToolTipText(param.getDescription());
  }

  /**
   * get the action for the Edit button
   */

  public AbstractAction getEditAction() {
    return _editAction;
  }

  /**
   * Interface method - a no-op for this object
   */

  public void syncParamToView() {
  }
    
  /**
   * Interface method - a no-op for this object
   */

  public int syncViewToParam() {
    return 0;
  }

  /**
   * Get the view component frame
   */
    
  public CollectionFrame getFrame() {
    return _frame;
  }

  /**
   * set the position of the view window
   */

  public void setPosition(int x, int y) {
    setX(x);
    setY(y);
    if (x != -1 && y != -1) {
      _frame.setLocation(x, y);
    }
  }

  /**
   * set the size of the view window
   */
    
  public void setSize(int width, int height) {
    setWidth(width);
    setHeight(height);
    if (width != -1 && height != -1) {
      _frame.setSize(width, height);
    }
  }
    
  /**
   * Activate the frame of this object and recursively activate
   * those contained by this object
   */
    
  public void activateFrame() {
    _frame.clear();
    for (int ii = 0; ii < _param.getNParams(); ii++) {
      AbstractParameter aparam = _param.getParam(ii);
      if (aparam.getType() == ParameterType.COLLECTION) {
        CollectionParameter cparam = (CollectionParameter) aparam;
        cparam.activateFrame();
      }
      _frame.add(_param.getParam(ii).getView());
    }
    _frame.activate();
  }
    
  /**
   * Edit action inner class
   */
    
  private class EditAction extends AbstractAction {
    public EditAction(String label, Icon icon) {
      super(label, icon);
    }
    public void actionPerformed(ActionEvent event) {
      CollectionFrame frame = _param.getCollectionFrame();
      _param.syncParamToView();
      frame.setVisible(true);
    }
  }
    
}

