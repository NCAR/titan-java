// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:38:22 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.border.*;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.awt.Image;
import javax.swing.event.*;
import javax.swing.undo.*;

/*******************************************************************
 * Text parameter view.
 * Supports a multi-line text area.
 * 
 * @author Mike Dixon
 */

public class TextParameterView extends AbstractParameterView

{

  /**
   * editor component
   */

  private JPanel _topPanel;
  private JTextArea _textArea;
  private UndoManager _undo = new UndoManager();

  /**
   * parameter associated with this object
   */
  
  private TextParameter _param;

  /**
   * constructor
   *
   * @param param parameter to be associated with this object
   */
    
  public TextParameterView(TextParameter param) {

    // inheritance
    
    super(param);
    _param = param;

    // create the editable text area

    _textArea = new JTextArea(param.getValue());
    Document doc = _textArea.getDocument();
    doc.addUndoableEditListener(new _UndoableEditListener());

    // create panel to put it in

    _topPanel = new JPanel();
    _topPanel.setLayout(new BorderLayout());
    _topPanel.setBorder(_createBorder(_topPanel, "Edit this content", 3));

    // create edit panel and add to top panel

    JPanel editPanel = _createEditPanel();
    _topPanel.add(editPanel, BorderLayout.NORTH);

    // add text area to top panel

    _topPanel.add(_textArea, BorderLayout.SOUTH);

    // set the panel to be the editor

    setEditor(_topPanel);
    getEditor().setToolTipText(param.getDescription());

  }

  /**
   * Ensures view shows the current parameter value.
   */
    
  public void syncParamToView() {
    _textArea.setText(_param.getValue());
  }
    
  /**
   * Sets parameter value from the view.
   */
    
  public int syncViewToParam() {
    _param.setValue(_textArea.getText());
    return 0;
  }
  
  // create edit panel, with copy, cut and paste buttons

  private JPanel _createEditPanel() {
    
    JrpDummyComponent dc = new JrpDummyComponent();

    Image cutImage =
      JrpImageLoad.getFromRes(dc, "/edu/ucar/rap/jrp/images/cut.png");
    ImageIcon cutIcon = new ImageIcon(cutImage);
    JButton cutButton = new JButton(cutIcon);
    cutButton.addActionListener(new CutListener());
    cutButton.setToolTipText("Cut selected text");
	
    Image copyImage =
      JrpImageLoad.getFromRes(dc, "/edu/ucar/rap/jrp/images/copy.png");
    ImageIcon copyIcon = new ImageIcon(copyImage);
    JButton copyButton = new JButton(copyIcon);
    copyButton.addActionListener(new CopyListener());
    copyButton.setToolTipText("Copy selected text");
	
    Image pasteImage =
      JrpImageLoad.getFromRes(dc, "/edu/ucar/rap/jrp/images/paste.png");
    ImageIcon pasteIcon = new ImageIcon(pasteImage);
    JButton pasteButton = new JButton(pasteIcon);
    pasteButton.addActionListener(new PasteListener());
    pasteButton.setToolTipText("Paste from clipboard");
	
    Image undoImage =
      JrpImageLoad.getFromRes(dc, "/edu/ucar/rap/jrp/images/undo.png");
    ImageIcon undoIcon = new ImageIcon(undoImage);
    JButton undoButton = new JButton(undoIcon);
    undoButton.addActionListener(new UndoListener());
    undoButton.setToolTipText("Undo previous action");
	
    Image redoImage =
      JrpImageLoad.getFromRes(dc, "/edu/ucar/rap/jrp/images/redo.png");
    ImageIcon redoIcon = new ImageIcon(redoImage);
    JButton redoButton = new JButton(redoIcon);
    redoButton.addActionListener(new RedoListener());
    redoButton.setToolTipText("Redo previous undo");
	
    JPanel editPanel = new JPanel();
    editPanel.add(cutButton);
    editPanel.add(copyButton);
    editPanel.add(pasteButton);
    editPanel.add(undoButton);
    editPanel.add(redoButton);

    return editPanel;

  }

  // listeners
    
  private class CopyListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      _textArea.copy();
    }
  }
    
  private class CutListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      _textArea.cut();
    }
  }
    
  private class PasteListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      _textArea.paste();
    }
  }
    
  private class UndoListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      try {
        if (_undo.canUndo()) {
          _undo.undo();
        }
      } catch (CannotUndoException e) {
      }
    }
  }
    
  private class RedoListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      try {
        if (_undo.canRedo()) {
          _undo.redo();
        }
      } catch (CannotRedoException e) {
      }
    }
  }
    
  private class _UndoableEditListener
    implements UndoableEditListener {
    public void undoableEditHappened(UndoableEditEvent e) {
      _undo.addEdit(e.getEdit());
    }
  }

  // create a top-style border

  private Border _createBorder(JPanel panel, String titleLabel, int spacing) {
    
    Border etched =
      BorderFactory.createEtchedBorder(EtchedBorder.LOWERED,
                                       panel.getBackground().brighter(),
                                       panel.getBackground().darker());
    
    Border titled = BorderFactory.createTitledBorder
      (etched, titleLabel,
       TitledBorder.CENTER, TitledBorder.TOP);
    
    return BorderFactory.createCompoundBorder
      (titled,
       BorderFactory.createEmptyBorder(0,spacing,spacing,spacing));
    
  }
  
}

