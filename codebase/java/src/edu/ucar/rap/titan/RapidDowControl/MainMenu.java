///////////////////////////////////////////////////////////////////////
//
// MainMenu
//
// Main menu for RapidDowControl
//
// Mike Dixon
//
// May 2013
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RapidDowControl;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import edu.ucar.rap.jrp.*;

class MainMenu

{

  // members
    
  private RapidDowControl _app = null;
  private JFrame _frame = null;
  private JPanel _topPanel;
  private JMenuBar _menuBar = null;
  private Parameters _params = Parameters.getInstance();
  private ControlPanel _controlPanel = ControlPanel.getInstance();
  private StatusPanel _statusPanel = StatusPanel.getInstance();

  public MainMenu(RapidDowControl app,
                  JFrame frame,
                  JPanel topPanel)
  {

    // save context

    _app = app;
    _frame = frame;
    _topPanel = topPanel;
	
    // Create the main menu bar
	
    _menuBar = new JMenuBar();
	
    // Set this instance as the application's menu bar

    _frame.setJMenuBar(_menuBar);
	
    // Create a new toolbar
	
    JToolBar toolBar = new JToolBar();
    toolBar.setFloatable(false);
    _topPanel.add(toolBar, BorderLayout.NORTH);
	
    // Create the file & configuration menu, add exit to it
	
    JMenu fileMenu = _params.createMenu("File");
    fileMenu.setToolTipText("Configuration menu");
	
    JMenuItem exit = fileMenu.add(new ExitAction("Exit"));
    exit.setToolTipText("Exit the program");
	
    // Create the control menu

    JMenu controlMenu = new JMenu("Control");
    populateControlMenu(toolBar, controlMenu);
	
    // Create the status menu

    JMenu statusMenu = new JMenu("Status");
    populateStatusMenu(toolBar, statusMenu);
	
    // Create the comms menu
	
    _menuBar.add(fileMenu);
    _menuBar.add(controlMenu);
    _menuBar.add(statusMenu);

    // create toolbar
	
    JButton openTool = toolBar.add(_params.getFileOpenAction());
    openTool.setToolTipText("Open an existing config");
	
    JButton editTool = toolBar.add(_params.getEditAction());
    editTool.setToolTipText("Edit the parameters");
	
    JButton saveTool = toolBar.add(_params.getFileSaveAction());
    saveTool.setToolTipText("Save this config");
	
  }

  public void populateControlMenu(JToolBar toolBar, JMenu menu)
  {

    // control panel
    
    AbstractAction controlPanelAction = 
      new ControlPanelAction("Show control panel", null);
    JMenuItem controlPanelItem = menu.add(controlPanelAction);
    if (_params.control.startVisible.getValue()) {
      _controlPanel.setVisible(true);
    }
	
  }
    
  public void populateStatusMenu(JToolBar toolBar, JMenu menu)
  {
	
    // status panel
	
    AbstractAction statusPanelAction =
      new StatusPanelAction("Show status panel", null);
    JMenuItem statusPanelItem = menu.add(statusPanelAction);
    if (_params.status.startVisible.getValue()) {
      _statusPanel.setVisible(true);
    }
	
  }
    
  ////////////////////////////////////////////////////
  // Inner classes for actions
  ////////////////////////////////////////////////////

  // exit action
  private class ExitAction extends AbstractAction
  {
    public ExitAction(String name) {
      super(name, null);
    }
    public void actionPerformed(ActionEvent event) {
      _app.doExit(true);
    }
  }

  // controlPanel action
  public class ControlPanelAction extends AbstractAction
  {
    public ControlPanelAction(String label, ImageIcon icon)	{
      super(label, icon);
    }
    public void actionPerformed(ActionEvent event) {
      _controlPanel.setVisible(true);
    }
  }

  // statusPanel action
  public class StatusPanelAction extends AbstractAction
  {
    public StatusPanelAction(String label, ImageIcon icon)	{
      super(label, icon);
    }
    public void actionPerformed(ActionEvent event) {
      _statusPanel.setVisible(true);
    }
  }

}

