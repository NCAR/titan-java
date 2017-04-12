///////////////////////////////////////////////////////////////////////
//
// DrxConfig
//
// Mike Dixon
//
// May 2013
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.DowControl;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.lang.Math;
import edu.ucar.rap.jrp.*;

public class DrxConfig extends JPanel implements StatusDataListener

{

  private ControlPanel _control;
  private Parameters _params = Parameters.getInstance();
  private java.util.List<Config> _configs = new ArrayList<Config>();
  private JPanel _chooser = null;
  ButtonGroup _radioGroup = new ButtonGroup();
  private StatusData _status;
  JButton _applyButton = null;
  JButton _cancelButton = null;

  private String _currentParamsFileName = new String();
  private String _selectedParamsFileName = new String();
  
  public DrxConfig(ControlPanel control) {
    
    _control = control;
    setLayout(new BorderLayout());
    setBorder(CustomBorder.createTop(control, "Select DRX configuration", 3));

    // set from params

    updateFromParams();
    
    _applyButton = new JButton("Apply");
    _applyButton.addActionListener(new ApplyListener());
    _applyButton.setToolTipText("Send new param request to DRX");
	
    _cancelButton = new JButton("Cancel");
    _cancelButton.addActionListener(new CancelListener());
    _cancelButton.setToolTipText("Cancel requested changes");

    // JPanel applyPanel = new JPanel();
    // applyPanel.add(_applyButton);
    // applyPanel.add(_cancelButton);
    // add(applyPanel, BorderLayout.SOUTH);

    // add params change listener
    
    _params.control.addChangeListener(new ParamsChangeListener());
    
    // add listener for new status data

    StatusDataHandler.getInstance().addListener(this);
    
  } // constructor
  
  // buttons

  public JButton getApplyButton() {
    return _applyButton;
  }
  public JButton getCancelButton() {
    return _cancelButton;
  }
  
  // handle incoming status
  
  public void handleStatus(StatusData status) {
    if (_currentParamsFileName.equals(status.params_file_name)) {
      return;
    }
    _currentParamsFileName = status.params_file_name;
    for (Iterator ii = _configs.iterator(); ii.hasNext();) {
      Config conf = (Config) ii.next();
      if (conf.fileNameStr.equals(_currentParamsFileName)) {
        conf.button.setSelected(true);
        _selectedParamsFileName = _currentParamsFileName;
        break;
      }
    }
    _params.setMainWindowLabel("Params in use: " + _currentParamsFileName);
  }
    
  // info listener

  private class InfoListener implements ActionListener {
    public InfoListener() {
    }
    public void actionPerformed(ActionEvent event) {
      _params.control.getCollectionFrame().setVisible(true);
    }
  }

  /////////////////////////////////////
  // Listener for changes in parameters
  
  private class ParamsChangeListener implements CollectionChangeListener {
    public void reactToChange() {
      updateFromParams();
    }
  }
  
  // apply listener
    
  private class ApplyListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      if (_control.changeDrxConfig(_selectedParamsFileName) != 0) {
        _currentParamsFileName = "";
        _selectedParamsFileName = "";
      }
    }
  }
  
  // cancel listener
  
  private class CancelListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      _currentParamsFileName = "";
      _selectedParamsFileName = "";
    }
  }

  ////////////////////////////
  // update when params change

  protected void updateFromParams() {

    // parsing the configs
    
    _configs.clear();
    String drxConfig = _params.control.drxConfig.getValue();
    String[] lines = drxConfig.split("\n");
    for (int ii = 0; ii < lines.length; ii++) {
      String line = new String(lines[ii]);
      try {
        Config conf = new Config(line);
        _configs.add(conf);
      }
      catch (ParseException e) {
      }
    }

    // clear radio button group

    for (Enumeration<AbstractButton> e = _radioGroup.getElements(); 
         e.hasMoreElements();) {
      _radioGroup.remove(e.nextElement());
    }
    
    // remove chooser if it already exists

    if (_chooser != null) {
      remove(_chooser);
      _chooser = null;
    }

    // put together into a panel

    _chooser = new JPanel();
    for (Iterator ii = _configs.iterator(); ii.hasNext();) {
      Config conf = (Config) ii.next();
      _radioGroup.add(conf.button);
      _chooser = addPanel(_chooser, conf.panel);
    }
    
    // add to main panel
    
    add(_chooser, BorderLayout.NORTH);
    revalidate();
    
  }

  // take an existing panel
  // create a new panel, add an element to it and return it
  // new elements are added at the bottom
    
  public JPanel addPanel(JPanel parent,
                         JPanel child)
    
  {

    BorderLayout layout = new BorderLayout();
    layout.setVgap(3);
    JPanel grandParent = new JPanel(layout);
    if (parent != null) {
      grandParent.add(parent, BorderLayout.NORTH);
    }
    grandParent.add(child, BorderLayout.SOUTH);

    return grandParent;

  }
    
  // inner class for config instace

  public class Config
  {
    
    public String labelStr = "unknown";
    public String fileNameStr = "unknown";
    public String buttonStr;
    public JRadioButton button;
    public JPanel panel;
    
    public Config(String line) throws ParseException 
    {

      Font font =
        getFont().deriveFont(Font.BOLD,
                             _params.control.fontSize.getValue());
      
      if (line.startsWith("#")) {
        throw new ParseException("Comment: " + line, 0);
      }

      String[] parts = line.split(":");
      if (parts.length == 2) {
        labelStr = parts[0];
        fileNameStr = parts[1].replaceAll(" ", "");
      } else {
        throw new ParseException("Bad config format: " + line, 0);
      }
      
      buttonStr = new String(labelStr + ": " + fileNameStr);
      button = new JRadioButton(buttonStr);
      button.setFont(font);
      panel = new JPanel(new BorderLayout());
      panel.add(button, BorderLayout.WEST);
      
      button.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            _selectedParamsFileName = fileNameStr;
          }
        });
      
    }

    // pad the label str in the button str, given the max
    // length of any label string in the group
    // this should line up the : delimiters

    public void padButtonLabel(int paddedLen) {
      if (labelStr.length() >= paddedLen) {
        return;
      }
      int nExtra = paddedLen - labelStr.length();
      StringBuffer buf = new StringBuffer(labelStr);
      for (int ii = 0; ii < nExtra; ii++) {
        buf.append(' ');
      }
      buttonStr = new String(buf.toString() + ": " + fileNameStr);
      button.setText(buttonStr);
    }

    // print

    public void print(PrintStream out, String spacer) {
      out.print(spacer);
      out.print(buttonStr);
    }
      
    public void println(PrintStream out, String spacer) {
      print(out, spacer);
      out.println("");
    }
      
  } // DateTime class

}

