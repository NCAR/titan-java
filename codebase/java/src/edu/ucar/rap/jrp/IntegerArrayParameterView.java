// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:38:26 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/*******************************************************************
 * Integer array parameter view.
 * 
 * @author Mike Dixon
 */

public class IntegerArrayParameterView extends AbstractParameterView {

  /**
   * editor component
   */
    
  private JButton _editButton;
    
  /**
   * parameter associated with this object
   */
    
  private IntegerArrayParameter _param;
    
  private ArrayParameter _aParam;
  private JFrame _frame;
  private EditAction _editAction;
  private CutAction _cutAction;
  private CopyAction _copyAction;
  private PasteAction _pasteAction;
  private UndoAction _undoAction;
  private ClearSelectAction _clearSelectAction;

  private JTable _table;
  private JrpSimpleTableModel _tableModel;
    
  private boolean _selectionActive = false;
  private int _selectionStartRow;
  private int _selectionEndRow;
    
  private ArrayList _clipBoard = new ArrayList();
  private ArrayList _undoBoard = new ArrayList();  

  private boolean _cellValueError = false;

  /**
   * constructor
   *
   * @param param parameter to be associated with this object
   */
    
  public IntegerArrayParameterView(IntegerArrayParameter param) {
	
    super(param);
    _param = param;
    _aParam = param;

    // create editor

    JrpDummyComponent dc = new JrpDummyComponent();
    Image editImage =
      JrpImageLoad.getFromRes(dc, "/edu/ucar/rap/jrp/images/edit_small.png");
    ImageIcon editIcon = new ImageIcon(editImage);
    _editAction = new EditAction("Edit", editIcon);
    _editButton = new JButton(_editAction);
    setEditor(_editButton);
    getEditor().setToolTipText(param.getDescription());
	
    // create frame

    _frame = new JFrame("Param: '" + _param.getName() + "'");
    _frame.setLocation(100, 100);
	
    // set up a top panel

    JPanel topPanel = new JPanel();
    topPanel.setLayout(new BorderLayout());
    _frame.getContentPane().add(topPanel);

    // set to exit on close
	
    _frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

    // create menu
	
    _frame.setJMenuBar(_createMenuBar());
	
    // create tool bar
	
    topPanel.add(_createToolBar(), BorderLayout.NORTH);
	
    // create table

    topPanel.add(_createTable(), BorderLayout.CENTER);

    // create apply panel, with apply, reset and cancel buttons
	
    topPanel.add(_createApplyPanel(), BorderLayout.SOUTH);

    _frame.pack();

  }

  /**
   * Ensures view shows the current parameter value.
   */
    
  public void syncParamToView() {

    ArrayList array = _aParam.getValue();
    _tableModel.setRowCount(array.size());
    for (int i = 0; i < array.size(); i++) {
      _tableModel.setValueAt(array.get(i), i, 1);
    }
    _tableModel.setIndices();
    _disableAllActions();
    _undoBoard.clear();
    _clipBoard.clear();

  }
    
  /**
   * Sets parameter value from the view.
   */
    
  public int syncViewToParam() {
	
    // then get the value from each cell, return if an
    // error occurs
	
    ArrayList vals = new ArrayList();
    for (int i = 0; i < _table.getRowCount(); i++) {
      Object val;
      Integer intVal = null;
      val = _table.getValueAt(i, 1);
      if (val.getClass() == Integer.class) {
        intVal = (Integer) val;
      } else if (val.getClass() == String.class) {
        try {
          intVal = Integer.valueOf((String) val);
        } catch (NumberFormatException e) {
          // show error dialog
          String errStr =
            "Illegal integer value: " + (String) val + ", row: " + i;
          JOptionPane.showMessageDialog(_table, errStr,
                                        "Illegal integer value",
                                        JOptionPane.ERROR_MESSAGE);
          return -1;
        }
      }
      vals.add(intVal);
    }
	
    // set param values
	
    _aParam.setValue(vals);
	
    return 0;
  }

  ///////////////////////////////////////////////////////////
  // private methods

  // create menu bar

  private JMenuBar _createMenuBar() {
	
    // menu bar for edit and help functions
	
    JMenuBar menuBar = new JMenuBar();

    JMenu editMenu = new JMenu("Edit");
    editMenu.setMnemonic('E');
    JMenu helpMenu = new JMenu("Help");
    helpMenu.setMnemonic('H');
    menuBar.add(editMenu);
    menuBar.add(helpMenu);
	
    JrpDummyComponent dc = new JrpDummyComponent();
    Image cutImage =
      JrpImageLoad.getFromRes(dc, "/edu/ucar/rap/jrp/images/cut.png");
    ImageIcon cutIcon = new ImageIcon(cutImage);
    _cutAction = new CutAction("Cut", cutIcon);
    JMenuItem cut = editMenu.add(_cutAction);
    cut.setMnemonic('t');
    cut.setToolTipText("Cut data to the clipboard");

    Image copyImage =
      JrpImageLoad.getFromRes(dc, "/edu/ucar/rap/jrp/images/copy.png");
    ImageIcon copyIcon = new ImageIcon(copyImage);
    _copyAction = new CopyAction("Copy", copyIcon);
    JMenuItem copy = editMenu.add(_copyAction);
    copy.setMnemonic('C');
    copy.setToolTipText("Copy data to the clipboard");
	
    Image pasteImage =
      JrpImageLoad.getFromRes(dc, "/edu/ucar/rap/jrp/images/paste.png");
    ImageIcon pasteIcon = new ImageIcon(pasteImage);
    _pasteAction = new PasteAction("Paste", pasteIcon);
    JMenuItem paste = editMenu.add(_pasteAction);
    paste.setMnemonic('P');
    paste.setToolTipText("Paste data from the clipboard");

    Image undoImage =
      JrpImageLoad.getFromRes(dc, "/edu/ucar/rap/jrp/images/undo.png");
    ImageIcon undoIcon = new ImageIcon(undoImage);
    _undoAction = new UndoAction("Undo", undoIcon);
    JMenuItem undo = editMenu.add(_undoAction);
    undo.setMnemonic('U');
    undo.setToolTipText("Undo cut, paste or selection");

    Image clearSelectImage =
      JrpImageLoad.getFromRes(dc, "/edu/ucar/rap/jrp/images/no_selection.png");
    ImageIcon clearSelectIcon = new ImageIcon(clearSelectImage);
    _clearSelectAction = new ClearSelectAction("Clear selection", clearSelectIcon);
    JMenuItem clearSelect = editMenu.add(_clearSelectAction);
    clearSelect.setMnemonic('s');
    clearSelect.setToolTipText("Clear selection");

    _disableAllActions();
	
    editMenu.add(cut);
    editMenu.add(copy);
    editMenu.add(paste);
    editMenu.add(undo);
    editMenu.add(clearSelect);

    return menuBar;

  }

  // Create tool bar
    
  private JComponent _createToolBar() {
	
    JToolBar toolBar = new JToolBar();
    toolBar.setFloatable(false);
	
    JButton cutTool = toolBar.add(_cutAction);
    cutTool.setMnemonic('t');
    cutTool.setToolTipText("Cut data to the clipboard");
	
    JButton copyTool = toolBar.add(_copyAction);
    copyTool.setMnemonic('C');
    copyTool.setToolTipText("Copy data to the clipboard");
	
    JButton pasteTool = toolBar.add(_pasteAction);
    pasteTool.setMnemonic('P');
    pasteTool.setToolTipText("Paste data from the clipboard");

    JButton undoTool = toolBar.add(_undoAction);
    undoTool.setMnemonic('U');
    undoTool.setToolTipText("Undo previous cut or paste");
	
    JButton clearSelectTool = toolBar.add(_clearSelectAction);
    clearSelectTool.setMnemonic('s');
    clearSelectTool.setToolTipText("Clear selection");
	
    return toolBar;

  }

  // create apply panel, with apply, reset and cancel buttons

  private JComponent _createApplyPanel() {
	
    JButton applyButton = new JButton("Apply");
    applyButton.addActionListener(new ApplyListener());
    applyButton.setToolTipText("Accept and close");
	
    JButton resetButton = new JButton("Reset");
    resetButton.addActionListener(new ResetListener());
    resetButton.setToolTipText("Reset to previous");
	
    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new CancelListener());
    cancelButton.setToolTipText("Cancel all changes and close");
	
    JPanel applyPanel = new JPanel();
    applyPanel.add(applyButton);
    applyPanel.add(resetButton);
    applyPanel.add(cancelButton);

    return applyPanel;

  }

  private JComponent _createTable() {

    _tableModel = new JrpSimpleTableModel();
    _table = new JTable(_tableModel);
    _table.setPreferredScrollableViewportSize(new Dimension(200, 300));
    // _table.setCellSelectionEnabled(true);
    _table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    _table.getSelectionModel().addListSelectionListener(new SelectionListener());

    _table.getColumn("Index").setCellRenderer(new CenterRenderer());
    _table.getColumn("Index").setMinWidth(50);
    _table.getColumn("Value").setCellRenderer(new CenterRenderer());
    _table.getColumn("Value").setCellEditor(new IntegerEditor(new JTextField()));
    _table.getColumn("Value").setMinWidth(50);
	
    // Create the scroll pane and add the table to it.
	

    JScrollPane tableScroll = new JScrollPane(_table);
    _table.setPreferredScrollableViewportSize(new Dimension(300, 70));

    return tableScroll;

  }

  // copy selection to clipboard

  private void _copySelectionToClipBoard() {
    _clipBoard.clear();
    for (int row = _selectionStartRow; row <= _selectionEndRow; row++) {
      _clipBoard.add(_tableModel.getValueAt(row, 1));
    }
    if (_clipBoard.size() > 0) {
      _pasteAction.setEnabled(true);
    } else {
      _pasteAction.setEnabled(false);
    }
  }

  // save for undo later

  private void _saveForUndo() {
    _undoBoard.clear();
    for (int row = 0; row < _tableModel.getRowCount(); row++) {
      _undoBoard.add(_tableModel.getValueAt(row, 1));
    }
    if (_undoBoard.size() > 0) {
      _undoAction.setEnabled(true);
    } else {
      _undoAction.setEnabled(false);
    }
  }

  // perform undo

  private void _performUndo() {
    _tableModel.setRowCount(_undoBoard.size());
    _tableModel.setIndices();
    for (int i = 0; i < _tableModel.getRowCount(); i++) {
      _tableModel.setValueAt(_undoBoard.get(i), i, 1);
    }
    _table.repaint();
    _undoBoard.clear();
    _undoAction.setEnabled(false);
  }

  // clear the selection

  private void _clearSelection() {
    _table.clearSelection();
    _clearSelectAction.setEnabled(false);
  }

  // stop editing

  private int _stopEditing() {
    _cellValueError = false;
    for (int i = 0; i < _table.getRowCount(); i++) {
      _table.getCellEditor(i, 1).stopCellEditing();
    }
    if (_cellValueError) {
      return -1;
    } else {
      return 0;
    }
  }
	
  // cancel editing

  private void _cancelEditing() {
    for (int i = 0; i < _table.getRowCount(); i++) {
      _table.getCellEditor(i, 1).cancelCellEditing();
    }
  }
	
  // disable all actions

  private void _disableAllActions() {

    _cutAction.setEnabled(false);
    _copyAction.setEnabled(false);
    _pasteAction.setEnabled(false);
    _undoAction.setEnabled(false);
    _clearSelectAction.setEnabled(false);

  }

  //////////////////////////////////////////////////////////////////////////
  // INNER CLASSES

  // renderer and editor for table cells
    
  private class CenterRenderer extends DefaultTableCellRenderer {
    CenterRenderer() {
      setHorizontalAlignment(JLabel.CENTER);
    }
  }
    
  private class CenterEditor extends DefaultCellEditor {
    CenterEditor(JTextField text) {
      super(text);
      text.setHorizontalAlignment(JLabel.CENTER);
    }
  }
    
  private class IntegerEditor extends DefaultCellEditor {
    private JTextField _field;
    IntegerEditor(JTextField field) {
      super(field);
      _field = field;
      _field.setHorizontalAlignment(JLabel.CENTER);
      setClickCountToStart(1);
    }
	
    // Override getCellEditorValue method to return an Integer
    public Object getCellEditorValue() {
      Object val;
      try {
        val = Integer.valueOf(_field.getText());
      } catch (NumberFormatException e) {
        // show error dialog
        String errStr =
          "Illegal integer value: " + _field.getText();
        JOptionPane.showMessageDialog(_field, errStr,
                                      "Illegal integer value",
                                      JOptionPane.ERROR_MESSAGE);
        val = _field.getText();
        _cellValueError = true;
      }
      return val;
    }

  }
    
  // edit action
    
  private class EditAction extends AbstractAction {
    public EditAction(String label, Icon icon) {
      super(label, icon);
    }
    public void actionPerformed(ActionEvent event) {
      syncParamToView();
      _frame.setVisible(true);
    }
  }
    
  // copy action
    
  private class CopyAction extends AbstractAction {
    public CopyAction(String label, Icon icon) {
      super(label, icon);
    }
    public void actionPerformed( ActionEvent event ) {
      _copySelectionToClipBoard();
    }
  }

  // cut action
    
  private class CutAction extends AbstractAction {
    public CutAction(String label, Icon icon) {
      super(label, icon);
    }
    public void actionPerformed( ActionEvent event ) {
      _saveForUndo();
      _copySelectionToClipBoard();
      // move data up
      int nBelow = _tableModel.getRowCount() - _selectionEndRow - 1;
      for (int i = 0; i < nBelow; i++) {
        Object toMove = _tableModel.getValueAt(_selectionEndRow + i + 1, 1);
        _tableModel.setValueAt(toMove, _selectionStartRow + i, 1);
      }
      // remove rows from end
      _tableModel.setRowCount(_tableModel.getRowCount() - _clipBoard.size());
      _clearSelection();
      _cancelEditing();
    }
  }

  // paste action

  private class PasteAction extends AbstractAction {
    public PasteAction(String label, Icon icon) {
      super(label, icon);
    }
    public void actionPerformed( ActionEvent event ) {
      _saveForUndo();
      // should we paste?
      int nPaste = _clipBoard.size();
      if (nPaste == 0) {
        return;
      }
      // decide where to paste 
      int rowCountBefore = _tableModel.getRowCount();
      int pasteIndex;
      if (_selectionActive) {
        pasteIndex = _selectionStartRow;
      } else {
        pasteIndex = rowCountBefore;
      }
      // add rows to table, set index values
      _tableModel.setRowCount(rowCountBefore + nPaste);
      int rowCountAfter = _tableModel.getRowCount();
      _tableModel.setIndices();
      // move data down
      for (int i = rowCountAfter - 1; i >= pasteIndex + nPaste; i--) {
        Object toMove = _tableModel.getValueAt(i - nPaste, 1);
        _tableModel.setValueAt(toMove, i, 1);
      }
      // paste in from clipboard
      for (int i = 0; i < nPaste; i++) {
        _tableModel.setValueAt(_clipBoard.get(i), pasteIndex + i, 1);
      }
      _clearSelection();
      _cancelEditing();
    }
  }

  // undo action

  private class UndoAction extends AbstractAction {
    public UndoAction(String label, Icon icon) {
      super(label, icon);
    }
    public void actionPerformed( ActionEvent event ) {
      _performUndo();
      _clearSelection();
    }
  }

  // clear selection action

  private class ClearSelectAction extends AbstractAction {
    public ClearSelectAction(String label, Icon icon) {
      super(label, icon);
    }
    public void actionPerformed( ActionEvent event ) {
      _clearSelection();
    }
  }

  // listeners
    
  private class ApplyListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      if (_stopEditing() != 0) {
        return;
      }
      if (syncViewToParam() != 0) {
        return;
      }
      _cancelEditing();
      _clearSelection();
      _frame.setVisible(false);
    }
  }
    
  private class ResetListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      _cancelEditing();
      _clearSelection();
      syncParamToView();
      _table.repaint();
    }
  }
    
  private class CancelListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      _clearSelection();
      _cancelEditing();
      _frame.setVisible(false);
    }
  }
    
  private class SelectionListener implements ListSelectionListener {
    public void valueChanged(ListSelectionEvent e) {
      //Ignore extra messages.
      if (e.getValueIsAdjusting()) return;
      ListSelectionModel lsm =
        (ListSelectionModel)e.getSource();
      if (lsm.isSelectionEmpty()) {
        _selectionActive = false;
        _cutAction.setEnabled(false);
        _copyAction.setEnabled(false);
        _clearSelectAction.setEnabled(false);
      } else {
        _selectionActive = true;
        _cutAction.setEnabled(true);
        _copyAction.setEnabled(true);
        _clearSelectAction.setEnabled(true);
        _selectionStartRow = lsm.getMinSelectionIndex();
        _selectionEndRow = lsm.getMaxSelectionIndex();
      }
    }
  }

  // table data model

  private class JrpSimpleTableModel extends DefaultTableModel {
	
    public JrpSimpleTableModel() {

      addColumn("Index");
      addColumn("Value");
	    
    }

    // Override getColumnClass() so JTable will used specified renderers
	
    public Class getColumnClass(int c) {
      // return Integer.class;
      return getValueAt(0, c).getClass();
    }
	
    public void setIndices() {
      for (int i = 0; i < getRowCount(); i++) {
        setValueAt(new Integer(i + 1), i, 0);
      }
    }
	
    // Set data cells editable
        
    public boolean isCellEditable(int row, int col) {
      if (col == 0) { 
        return false;
      } else {
        return true;
      }
    }

    public void print() {
      int numRows = getRowCount();
      int numCols = getColumnCount();

      for (int i=0; i < numRows; i++) {
        System.out.print("    row " + i + ":");
        for (int j=0; j < numCols; j++) {
          System.out.print("  " + getValueAt(i, j));
          System.out.print("  class is: " + getValueAt(i, j).getClass());
        }
        System.out.println();
      }
      System.out.println("--------------------------");
    }
  }

  //////////////////////////////////////////////////////////////////////////
  // Testing main()

  public static void main(String[] args) {

    IntegerArrayParameter iParam = new IntegerArrayParameter("test", false);
    iParam.add(100);
    iParam.add(200);
    iParam.add(400);
    iParam.add(800);
    iParam.add(1600);
    iParam.add(3200);
    iParam.add(6400);
    iParam.setDefaultFromValue();
	
    JFrame frame = new JFrame("Test app");
    JPanel topPanel = new JPanel();
    topPanel.setLayout(new BorderLayout());
    frame.getContentPane().add(topPanel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    topPanel.add(iParam.getView().getEditor(), BorderLayout.CENTER);
    frame.pack();
    frame.setVisible(true);

  }

}
