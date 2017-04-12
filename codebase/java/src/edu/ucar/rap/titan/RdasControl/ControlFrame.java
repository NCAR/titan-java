///////////////////////////////////////////////////////////////////////
//
// ControlPanel
//
// Mike Dixon
//
// Oct 2002
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.lang.Math;
import edu.ucar.rap.jrp.*;

public class ControlFrame extends JFrame

{

  private Parameters _params = Parameters.getInstance();
  private ControlPanel _controlPanel;
  private JMenuBar _menuBar;

  /**
   * Single instance for this class - it is a singleton
   */
  
  private static final ControlFrame _instance = new ControlFrame();
    
  /**
   * get singleton instance
   */
    
  public static ControlFrame getInstance() {
    return _instance;
  }
    
  private ControlFrame() {
	
    // titles
    
    _setTitleStr();
    setSize(_params.control.width.getValue(),
            _params.control.height.getValue());
    setLocation(_params.control.xx.getValue(),
                _params.control.yy.getValue());
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	
    // add listener for window property changes
    
    addComponentListener(new WindowPropertiesListener());
	
    // add listener for param changes
	
    _params.control.addChangeListener(new ControlParamsChangeListener());
    _params.radar.addChangeListener(new RadarParamsChangeListener());
    _updateFromParams();

    // menu bar
	
    _menuBar = new JMenuBar();
    setJMenuBar(_menuBar);
    
    // create control panel

    _controlPanel = new ControlPanel(this, _menuBar);
    getContentPane().add(_controlPanel);
	
  }

  // public set/get methods

  public ControlPanel getPanel() {
    return _controlPanel;
  }

  // set title
    
  private void _setTitleStr() {
    String frameTitle =
      "CONTROL PANEL - " + _params.radar.siteName.getValue();
    setTitle(frameTitle);
  }
  
  // update from parameters

  private void _updateFromParams() {

    if (_params.control.allowResize.getValue()) {
      setResizable(true);
    } else {
      setResizable(false);
    }

  }
  
  // listener for window properties

  private class WindowPropertiesListener extends ComponentAdapter {
	
    public void componentMoved(ComponentEvent e) {
      Parameters params = Parameters.getInstance();
      int paramX = params.control.xx.getValue();
      int paramY = params.control.yy.getValue();
      if (paramX != getX() || paramY != getY()) {
        params.control.xx.setValue(getX());
        params.control.yy.setValue(getY());
      }
    }
	
    public void componentResized(ComponentEvent e) {
      Parameters params = Parameters.getInstance();
      int paramWidth = params.control.width.getValue();
      int paramHeight = params.control.height.getValue();
      if (paramWidth != getWidth() || paramHeight != getHeight()) {
        params.control.width.setValue(getWidth());
        params.control.height.setValue(getHeight());
      }
    }
	
    public void componentShown(ComponentEvent e) {
      Parameters params = Parameters.getInstance();
      if (params.control.startVisible.getValue() == false) {
        params.control.startVisible.setValue(true);
      }
    }
	
    public void componentHidden(ComponentEvent e) {
      Parameters params = Parameters.getInstance();
      if (params.control.startVisible.getValue() == true) {
        params.control.startVisible.setValue(false);
      }
    }
	
  }

  // Listener for changes in control parameters

  private class ControlParamsChangeListener
    implements CollectionChangeListener {
    public void reactToChange() {
      _updateFromParams();
    }
  }
    
  // Listener for changes in radar parameters
    
  private class RadarParamsChangeListener
    implements CollectionChangeListener {
    public void reactToChange() {
      _setTitleStr();
    }
  }

}

