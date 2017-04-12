///////////////////////////////////////////////////////////////////////
//
// ControlPanel
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
  private DrxConfig _drxConfig;

  private String _infoContent;
  
  private JMenuBar _menuBar;
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
    
    // create config panel

    _drxConfig = new DrxConfig(this);
    
    // add to top panel
    
    JPanel topPanel = new JPanel(new BorderLayout());
    Container cp = getContentPane();
    cp.add(topPanel);
    // use scroll pane instead of adding straight
    // topPanel.add(_drxConfig, BorderLayout.NORTH);
    cp.add(new JScrollPane(_drxConfig),  BorderLayout.CENTER);
    
    // menu bar
    
    _menuBar = new JMenuBar();
    setJMenuBar(_menuBar);
    
    // edit button
    
    _editButton = new JButton(_params.control.getViewEditAction());
    _menuBar.add(_editButton);

    // add configuration buttons

    _menuBar.add(_drxConfig.getApplyButton());
    _menuBar.add(_drxConfig.getCancelButton());
    
    // help button
    
    _helpButton = new JButton("Help");
    _menuBar.add(_helpButton);
    _infoContent =
      "<h1>ControlPanel help info</h1>\n" +
      "<hr>\n" +
      "<h2>Changing the DRX configuration</h2>\n" +
      "<ul>\n" +
      "<li>Select the desired configuration.\n" +
      "<li>Hit <b>Apply</b> to request the DRX to change the configuration.\n" +
      "<li>Disable the transmitter (you will get a warning dialog).\n" +
      "<li>Wait for the data to start flowing again.\n" +
      "<li>Then re-enable the transmitter.\n" +
      "</ul>\n" +
      "<hr>\n" +
      "<h2>Setting up the control panel window</h2>\n" +
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
      "SELECT PARAMS - " + _params.mainWindow.radarName.getValue();
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
  
  // change configuration file
  
  int changeDrxConfig(String paramsFileName) {

    String title = "Change DRX configuration?";
    String message[] = {
      "Change config to: " + paramsFileName,
      "    Are you sure?"
    };

    int result = JOptionPane.showConfirmDialog
      (this, message, title,
       JOptionPane.OK_CANCEL_OPTION,
       JOptionPane.WARNING_MESSAGE);
    
    if (result == JOptionPane.YES_OPTION) {

      String message2[] = {
        "Please disable transmitter until data starts again",
        "    Is tx disabled?"
      };
      
      int result2 = JOptionPane.showConfirmDialog
        (this, message2, title,
         JOptionPane.OK_CANCEL_OPTION,
         JOptionPane.WARNING_MESSAGE);
      
      if (result2 == JOptionPane.YES_OPTION) {
        if (_sendChangeParamsRequest(paramsFileName) == 0) {
          return 0;
        } else {
          return -1;
        }
      } // if (result2 == JOptionPane.YES_OPTION) {
      
    } // if (result == JOptionPane.YES_OPTION)

    return -1;
    
  }

  // Request DRX to change params
  // this contacts the server running on DRX

  private int _sendChangeParamsRequest(String paramsFileName) {

    StringBuffer xml = new StringBuffer();
    
    xml.append("<DowGuiRequest>\n");
    xml.append("  <action>ChangeParams</action>\n");
    xml.append("  <paramsFileName>" + paramsFileName + "</paramsFileName>\n");
    xml.append("</DowGuiRequest>\n");
    xml.append("\r\n\r\n");

    if (_params.debug.getValue()) {
      System.err.println("**** Sending Commands to DRX ****");
      System.err.println(xml);
    }
    
    SocketComms sock = new SocketComms();
    String reply = "";
    try {
      reply = sock.communicate(xml.toString(),
                               _params.comms.drxHost.getValue(),
                               _params.comms.drxPort.getValue());
    }
    catch (IOException e) {
      String error =
        "ERROR sending commands to DOW drx\n" + e.toString();
      JOptionPane.showMessageDialog(_helpButton, error,
                                    "Error sending commands",
                                    JOptionPane.ERROR_MESSAGE);
      return -1;
    }

    if (_params.debug.getValue()) {
      System.err.println("**** Got reply from DRX ****");
      System.err.println(reply);
    }
    
    if (_checkReplyForErrors(reply) == 0) {
      return 0;
    }

    return -1;

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

      Element elc = (Element) itRoot.next();
      String elName = elc.getName();

      if (elName.equals("Error")) {

        Iterator itc = elc.getChildren().iterator();
        while (itc.hasNext()) {
            
          Element el = (Element) itc.next();
          String name = new String(el.getName());
          String text = new String(el.getText());
          
          if (name.equals("cause")) {
            String msg = ("ERROR requesting DRX to change configs\n" +
                          "Cause: " + el.getText() + "\n");
            JOptionPane.showMessageDialog(this, msg,
                                          "DowDrx error",
                                          JOptionPane.ERROR_MESSAGE);
          }

        } // while (itc.hasNext()) 

        return -1;
        
      } // if (elName.equals("Error")) 

      if (elName.equals("ChangeParams")) {

        StringBuffer info = new StringBuffer();

        Iterator itc = elc.getChildren().iterator();
        while (itc.hasNext()) {
            
          Element el = (Element) itc.next();
          String name = new String(el.getName());
          String text = new String(el.getText());
          
          if (name.equals("new_params_path")) {
            info.append("  Params changing to: " + text + "\n");
          } else if (name.equals("status")) {
            info.append("  Status: " + text + "\n");
          }
          info.append("Please wait for data before enabling transmitter\n");

        } // while (itc.hasNext()) 

        JOptionPane.showMessageDialog(this, info.toString(),
                                      "DowDrx message",
                                      JOptionPane.WARNING_MESSAGE);
      } // if (elName.equals("Error")) 

    } // while (itRoot.hasNext())

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

