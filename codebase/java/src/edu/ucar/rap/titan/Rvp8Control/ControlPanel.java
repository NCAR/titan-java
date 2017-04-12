///////////////////////////////////////////////////////////////////////
//
// ControlPanel
//
// Mike Dixon
//
// April 2007
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.Rvp8Control;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.lang.Math;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import edu.ucar.rap.jrp.*;

public class ControlPanel extends JFrame

{
  
  private Parameters _params = Parameters.getInstance();
  private ReceiveControl _receiveControl;
  private TransmitControl _transmitControl;
  private MomentsControl _momentsControl;
  private SinglePolFieldControl _singlePolFieldControl;
  private DualPolFieldControl _dualPolFieldControl;

  private String _infoContent;
  
  private JMenuBar _menuBar;
  private JButton _sendButton;
  private JButton _syncButton;
  private JButton _editButton;
  private JButton _helpButton;
  
  /**
   * Single instance for this class - it is a singleton
   */
  
  private static final ControlPanel _instance = new ControlPanel();
  
  /**
   * get singleton instance
   */
  
  public static ControlPanel getInstance() {
    return _instance;
  }
  
  private ControlPanel() {
    
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
    _updateFromParams();
    
    _singlePolFieldControl = new SinglePolFieldControl(this);
    _dualPolFieldControl = new DualPolFieldControl(this);
    _receiveControl = new ReceiveControl(this);
    _transmitControl = new TransmitControl(this);
    _momentsControl = new MomentsControl(this);
    
    // fill out top panel
    
    JPanel topPanel = new JPanel(new BorderLayout());
    getContentPane().add(topPanel);
    
    JPanel upperLeft = new JPanel(new BorderLayout());
    upperLeft.add(_transmitControl, BorderLayout.NORTH);
    upperLeft.add(_receiveControl, BorderLayout.CENTER);
    upperLeft.add(_momentsControl, BorderLayout.SOUTH);

    JPanel leftPanel = new JPanel(new BorderLayout());
    leftPanel.add(upperLeft, BorderLayout.NORTH);

    JPanel upperRight = new JPanel(new BorderLayout());
    upperRight.add(_singlePolFieldControl, BorderLayout.NORTH);
    upperRight.add(_dualPolFieldControl, BorderLayout.CENTER);

    JPanel rightPanel = new JPanel(new BorderLayout());
    rightPanel.add(upperRight, BorderLayout.NORTH);

    // add left and right parts
    
    topPanel.add(leftPanel, BorderLayout.WEST);
    topPanel.add(rightPanel, BorderLayout.CENTER);
    topPanel.add(Box.createHorizontalGlue(), BorderLayout.EAST);
    
    // menu bar
    
    _menuBar = new JMenuBar();
    setJMenuBar(_menuBar);
    
    // send button
    
    _sendButton = new JButton("Send");
    _menuBar.add(_sendButton);
    _sendButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          String title = "Send commands to RVP8?";
          String message[] = {
            "Send current command settings to RVP8",
            "    Are you sure?"
          };
          int result = JOptionPane.showConfirmDialog
            (_sendButton, message, title,
             JOptionPane.YES_NO_CANCEL_OPTION,
             JOptionPane.WARNING_MESSAGE);
          if (result == JOptionPane.YES_OPTION) {
            _sendCommands();
          }
        }
      });
                                  
    // sync button
    
    _syncButton = new JButton("Sync");
    _menuBar.add(_syncButton);
    _syncButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          String title = "Sync settings from RVP8?";
          String message[] = {
            "Sync settings from RVP8?",
            "This will copy RVP8 status to this GUI.",
            "    Are you sure?"
          };
          int result = JOptionPane.showConfirmDialog
            (_syncButton, message, title,
             JOptionPane.YES_NO_CANCEL_OPTION,
             JOptionPane.WARNING_MESSAGE);
          if (result == JOptionPane.YES_OPTION) {
            _syncWithRvp8();
          }
        }
      });
    
    // edit button
    
    _editButton = new JButton(_params.control.getViewEditAction());
    _menuBar.add(_editButton);
    
    // help button
    
    _helpButton = new JButton("Help");
    _menuBar.add(_helpButton);
    _infoContent =
      "<h1>ControlPanel help info</h1>\n" +
      "<hr>\n" +
      "<h2>Sending settings to RVP8</h2>\n" +
      "<ul>\n" +
      "<li>Set RVP8 parameters using the Control Panel.\n" +
      "<li>Hit <b>Send</b> to send the settings to the RVP8.\n" +
      "</ul>\n" +
      "<hr>\n" +
      "<h2>Synchronizing settings from RVP8</h2>\n" +
      "<ul>\n" +
      "<li>Use <b>Sync</b> button to read settings from RVP8" +
      "<li>This will copy settings from the RVP8.\n" +
      "</ul>\n" +
      "<hr>\n" +
      "<h2>Configuration</h2>\n" +
      "<h3>Setting the window size to accommodate the various panels:</h3>\n" +
      "<li>Use Edit button to bring up parameters panel.\n" +
      "<li>Select 'allow resize', hit apply.\n" +
      "<li>Resize window to suit.\n" +
      "<li>Turn off 'Allow resize' to fix window size.\n" +
      "</ul>\n";

      _helpButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          InfoFrame info =
            new InfoFrame(getX() + 30, getY() + 30,
                          600, 500);
          info.setTitle("Info for Control Panel");
          info.setContent(_infoContent);
        }
      });
    
  }

  // set title
  
  private void _setTitleStr() {
    String frameTitle =
      "CONTROL PANEL - " + _params.site.siteName.getValue();
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

  // send commands to RVP8

  private void _sendCommands() {

    StringBuffer xml = new StringBuffer();

    xml.append("<rvp8Message>\n");
    xml.append("<action>applyCommands</action>\n");
    xml.append("<rvp8Commands>\n");

    // single pol fields

    if (_params.singlePolFields.dbz.getValue()) {
      xml.append("  <field>dbz</field>\n");
    }
    if (_params.singlePolFields.vel.getValue()) {
      xml.append("  <field>vel</field>\n");
    }
    if (_params.singlePolFields.width.getValue()) {
      xml.append("  <field>width</field>\n");
    }
    if (_params.singlePolFields.dbt.getValue()) {
      xml.append("  <field>dbt</field>\n");
    }
    if (_params.singlePolFields.dbzc.getValue()) {
      xml.append("  <field>dbzc</field>\n");
    }
    if (_params.singlePolFields.velc.getValue()) {
      xml.append("  <field>velc</field>\n");
    }
    if (_params.singlePolFields.flags.getValue()) {
      xml.append("  <field>flags</field>\n");
    }
    if (_params.singlePolFields.sqi.getValue()) {
      xml.append("  <field>sqi</field>\n");
    }

    // dual pol fields

    String polarization = _params.transmit.polarization.getValue();
    if (polarization.equals("alternating") ||
        polarization.equals("simultaneous")) {
      if (_params.dualPolFields.kdp.getValue()) {
        xml.append("  <field>kdp</field>\n");
      }
      if (_params.dualPolFields.ldrh.getValue()) {
        xml.append("  <field>ldrh</field>\n");
      }
      if (_params.dualPolFields.ldrv.getValue()) {
        xml.append("  <field>ldrv</field>\n");
      }
      if (_params.dualPolFields.phidp.getValue()) {
        xml.append("  <field>phidp</field>\n");
      }
      if (_params.dualPolFields.phih.getValue()) {
        xml.append("  <field>phih</field>\n");
      }
      if (_params.dualPolFields.phiv.getValue()) {
        xml.append("  <field>phiv</field>\n");
      }
      if (_params.dualPolFields.rhoh.getValue()) {
        xml.append("  <field>rhoh</field>\n");
      }
      if (_params.dualPolFields.rhohv.getValue()) {
        xml.append("  <field>rhohv</field>\n");
      }
      if (_params.dualPolFields.rhov.getValue()) {
        xml.append("  <field>rhov</field>\n");
      }
      if (_params.dualPolFields.zdr.getValue()) {
        xml.append("  <field>zdr</field>\n");
      }
    }

    // receive

    xml.append("  <nGates>" +
               _params.receive.nGates.getValue() + "</nGates>\n");
    xml.append("  <gateSpacingKm>" +
               (_params.receive.gateSpacingM.getValue() / 1000.0) +
               "</gateSpacingKm>\n");

    // transmit

    String opsMode = _params.transmit.opsMode.getValue();
    if (opsMode.equals("active")) {
      xml.append("  <prfMode>" +
                 _params.transmit.prfMode.getValue() + "</prfMode>\n");
      xml.append("  <prf>" +
                 _params.transmit.prf.getValue() + "</prf>\n");
    }
    xml.append("  <phaseCoding>" +
               _params.transmit.phaseCoding.getValue() + "</phaseCoding>\n");
    xml.append("  <polarization>" +
               _params.transmit.polarization.getValue() + "</polarization>\n");
    

    // moments

    if (_params.moments.indexedBeams.getValue()) {
      xml.append("  <angSyncMode>dynamic</angSyncMode>\n");
    } else {
      xml.append("  <angSyncMode>none</angSyncMode>\n");
    }
    xml.append("  <nSamples>" +
               _params.moments.nSamples.getValue() + "</nSamples>\n");
    xml.append("  <majorMode>" +
               _params.moments.majorMode.getValue() + "</majorMode>\n");
    xml.append("  <windowType>" +
               _params.moments.fftWindow.getValue() + "</windowType>\n");
    String clutFilter = _params.moments.clutFilter.getValue();
    xml.append("  <clutterFilterNumber>" + clutFilter.charAt(0) +
               "</clutterFilterNumber>\n");
    String rangeSmoothing = _params.moments.rangeSmoothing.getValue();
    xml.append("  <rangeSmoothing>" + rangeSmoothing.charAt(0) +
               "</rangeSmoothing>\n");
    xml.append("  <speckleFilter>" +
               _params.moments.speckleFilter.getValue() + "</speckleFilter>\n");

    xml.append("</rvp8Commands>\n");
    xml.append("</rvp8Message>\n");

    if (_params.debug.getValue()) {
      System.err.println("**** Sending Commands to RVP8 ****");
      System.err.println(xml);
    }

    SocketComms sock = new SocketComms();
    String reply = "";
    try {
      reply = sock.communicate(xml.toString());
    }
    catch (IOException e) {
      String error =
        "ERROR sending commands to RVP8 driver\n" + e.toString();
      JOptionPane.showMessageDialog(_helpButton, error,
                                    "Error sending commands",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    _checkReplyForErrors(reply);

  }

  /////////////////////////////////////////////
  // sync with Rvp8Driver
  //
  // Request the current command state on the driver
  // Set these values locally.

  private void _syncWithRvp8() {

    StringBuffer xml = new StringBuffer();

    xml.append("<rvp8Message>\n");
    xml.append("<action>queryCommands</action>\n");
    xml.append("</rvp8Message>\n");

    if (_params.debug.getValue()) {
      System.err.println("**** Sending sync request to RVP8 ****");
      System.err.println(xml);
    }

    SocketComms sock = new SocketComms();
    String reply = "";
    try {
      reply = sock.communicate(xml.toString());
    }
    catch (IOException e) {
      String error =
        "ERROR sending sync request to RVP8 driver\n" + e.toString();
      JOptionPane.showMessageDialog(_helpButton, error,
                                    "Error sending sync request",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (_checkReplyForErrors(reply) != 0) {
      return;
    }

    _performSync(reply);
    
  }

  /////////////////////////////
  // check the reply for errors
  // returns 0 for no errors, -1 if error

  private int _checkReplyForErrors(String reply) {
    
    SAXBuilder builder = new SAXBuilder();
    Document doc;

    try {
      doc = builder.build(new StringReader(reply));
      // If there are no well-formedness errors, 
      // then no exception is thrown
    }
    catch (JDOMException e) { 
      // indicates a well-formedness error
      String error = "Cannot parse reply XML: " + reply + "\n";
      JOptionPane.showMessageDialog(_helpButton, error,
                                    "Cannot parse reply",
                                    JOptionPane.ERROR_MESSAGE);
      return -1;
    }
    catch (IOException e) { 
      // indicates a read error
      String error = "Cannot read reply XML: " + reply + "\n";
      JOptionPane.showMessageDialog(_helpButton, error,
                                    "Cannot read reply",
                                    JOptionPane.ERROR_MESSAGE);
      return -1;
    }
    
    Element root = doc.getRootElement();
    Iterator itRoot = root.getChildren().iterator();
    while (itRoot.hasNext()) {
      Element el = (Element) itRoot.next();
      String elName = el.getName();
      if (elName.equals("error")) {
        String error = "Rvp8Driver ERROR\n" + el.getText() + "\n";
        JOptionPane.showMessageDialog(_helpButton, error,
                                      "Rvp8Driver error",
                                      JOptionPane.ERROR_MESSAGE);
        return -1;
      }
    }

    return 0;

  }

  /////////////////////////////////////////////
  // Sunc local settings with Rvp8Driver state

  private int _performSync(String reply) {
    
    SAXBuilder builder = new SAXBuilder();
    Document doc;

    try {
      doc = builder.build(new StringReader(reply));
      // If there are no well-formedness errors, 
      // then no exception is thrown
    }
    catch (JDOMException e) { 
      // indicates a well-formedness error
      String error = "Cannot parse reply XML: " + reply + "\n";
      JOptionPane.showMessageDialog(_helpButton, error,
                                    "Cannot parse reply",
                                    JOptionPane.ERROR_MESSAGE);
      return -1;
    }
    catch (IOException e) { 
      // indicates a read error
      // indicates a well-formedness error
      String error = "Cannot read reply XML: " + reply + "\n";
      JOptionPane.showMessageDialog(_helpButton, error,
                                    "Cannot read reply",
                                    JOptionPane.ERROR_MESSAGE);
      return -1;
    }

    // initialize

    _params.singlePolFields.dbz.setValue(false);
    _params.singlePolFields.vel.setValue(false);
    _params.singlePolFields.width.setValue(false);
    _params.singlePolFields.dbt.setValue(false);
    _params.singlePolFields.dbzc.setValue(false);
    _params.singlePolFields.velc.setValue(false);
    _params.singlePolFields.flags.setValue(false);
    _params.singlePolFields.sqi.setValue(false);
    _params.dualPolFields.kdp.setValue(false);
    _params.dualPolFields.ldrh.setValue(false);
    _params.dualPolFields.ldrv.setValue(false);
    _params.dualPolFields.phidp.setValue(false);
    _params.dualPolFields.phih.setValue(false);
    _params.dualPolFields.phiv.setValue(false);
    _params.dualPolFields.rhoh.setValue(false);
    _params.dualPolFields.rhohv.setValue(false);
    _params.dualPolFields.rhov.setValue(false);
    _params.dualPolFields.zdr.setValue(false);

    Element root = doc.getRootElement();
    if (root.getName().equals("rvp8Message")) {
      Iterator itRoot = root.getChildren().iterator();
      while (itRoot.hasNext()) {
        Element elc = (Element) itRoot.next();
        if (elc.getName().equals("rvp8Commands")) {
          Iterator itc = elc.getChildren().iterator();
          while (itc.hasNext()) {
            Element el = (Element) itc.next();
            String name = new String(el.getName());
            String text = new String(el.getText());

            // fields

            if (name.equals("field")) {
              if (text.equals("dbz")) {
                _params.singlePolFields.dbz.setValue(true);
              }
              if (text.equals("vel")) {
                _params.singlePolFields.vel.setValue(true);
              }
              if (text.equals("width")) {
                _params.singlePolFields.width.setValue(true);
              }
              if (text.equals("dbt")) {
                _params.singlePolFields.dbt.setValue(true);
              }
              if (text.equals("dbzc")) {
                _params.singlePolFields.dbzc.setValue(true);
              }
              if (text.equals("velc")) {
                _params.singlePolFields.velc.setValue(true);
              }
              if (text.equals("flags")) {
                _params.singlePolFields.flags.setValue(true);
              }
              if (text.equals("sqi")) {
                _params.singlePolFields.sqi.setValue(true);
              }
              if (text.equals("kdp")) {
                _params.dualPolFields.kdp.setValue(true);
              }
              if (text.equals("ldrh")) {
                _params.dualPolFields.ldrh.setValue(true);
              }
              if (text.equals("ldrv")) {
                _params.dualPolFields.ldrv.setValue(true);
              }
              if (text.equals("phidp")) {
                _params.dualPolFields.phidp.setValue(true);
              }
              if (text.equals("phih")) {
                _params.dualPolFields.phih.setValue(true);
              }
              if (text.equals("phiv")) {
                _params.dualPolFields.phiv.setValue(true);
              }
              if (text.equals("rhoh")) {
                _params.dualPolFields.rhoh.setValue(true);
              }
              if (text.equals("rhohv")) {
                _params.dualPolFields.rhohv.setValue(true);
              }
              if (text.equals("rhov")) {
                _params.dualPolFields.rhov.setValue(true);
              }
              if (text.equals("zdr")) {
                _params.dualPolFields.zdr.setValue(true);
              }
            }

            if (name.equals("nGates")) {
              int nGates = _params.receive.nGates.getValue();
              try {
                nGates = Integer.parseInt(text);
              }
              catch (NumberFormatException e) {
                System.err.println("Invalid value for nGates: " + text);
              }
              _params.receive.nGates.setValue(nGates);
            }

            if (name.equals("gateSpacingKm")) {
              double gateSpacing = _params.receive.gateSpacingM.getValue();
              try {
                gateSpacing = Double.parseDouble(text) * 1000;
              }
              catch (NumberFormatException e) {
                System.err.println("Invalid value for gateSpacingKm: " + text);
              }
              _params.receive.gateSpacingM.setValue(gateSpacing);
            }

            if (name.equals("prfMode")) {
              _params.transmit.prfMode.setValue(text);
            }

            if (name.equals("prf")) {
              double prf = _params.transmit.prf.getValue();
              try {
                prf = Double.parseDouble(text);
              }
              catch (NumberFormatException e) {
                System.err.println("Invalid value for prf: " + text);
              }
              _params.transmit.prf.setValue(prf);
            }
            
            if (name.equals("phaseCoding")) {
              _params.transmit.phaseCoding.setValue(text);
            }

            if (name.equals("polarization")) {
              _params.transmit.polarization.setValue(text);
            }

            if (name.equals("angSyncMode")) {
              if (text.equals("none")) {
                _params.moments.indexedBeams.setValue(false);
              } else {
                _params.moments.indexedBeams.setValue(true);
              }
            }

            if (name.equals("nSamples")) {
              int nSamples = _params.moments.nSamples.getValue();
              try {
                nSamples = Integer.parseInt(text);
              }
              catch (NumberFormatException e) {
                System.err.println("Invalid value for nSamples: " + text);
              }
              _params.moments.nSamples.setValue(nSamples);
            }

            if (name.equals("majorMode")) {
              _params.moments.majorMode.setValue(text);
            }

            if (name.equals("windowType")) {
              _params.moments.fftWindow.setValue(text);
            }
            
            if (name.equals("clutterFilterNumber")) {
              String[] options = _params.moments.clutFilter.getOptions();
              int index = -1;
              try {
                index = Integer.parseInt(text);
              }
              catch (NumberFormatException e) {
                System.err.println("Invalid value for clutterFilterNumber: "
                                   + text);
              }
              if (index >= 0 && index < options.length) {
                _params.moments.clutFilter.setValue(options[index]);
              }
            }

            if (name.equals("rangeSmoothing")) {
              String[] options = _params.moments.rangeSmoothing.getOptions();
              int index = -1;
              try {
                index = Integer.parseInt(text);
              }
              catch (NumberFormatException e) {
                System.err.println("Invalid value for rangeSmoothing: "
                                   + text);
              }
              if (index >= 0 && index < options.length) {
                _params.moments.rangeSmoothing.setValue(options[index]);
              }
            }

            if (name.equals("speckleFilter")) {
              _params.moments.speckleFilter.setValue(text);
            }
            
          } // itc.hasNext()

        } // elc

      } // itroot

    } // root
    
    _singlePolFieldControl.updateFromParams();
    _dualPolFieldControl.updateFromParams();
    _receiveControl.updateFromParams();
    _transmitControl.updateFromParams();
    _momentsControl.updateFromParams();

    return 0;

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
    
}

