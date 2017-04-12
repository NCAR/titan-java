// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:38:24 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
///////////////////////////////////////////////////////////////////////
//
// ParameterInformationFrame.java
//
// Info panel for parameters
//
// Singleton
//
// Mike Dixon
//
// Sept 2002
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.jrp;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/*******************************************************************
 * Info panel for parameters.
 * <p>
 * The panel is used to display parameter information text.
 * <p>
 * Singleton.
 *
 * @author Mike Dixon
 */

public class ParameterInformationFrame extends JFrame implements ActionListener

{

  /**
   * private instance for singleton
   */

  private static final ParameterInformationFrame INSTANCE
    = new ParameterInformationFrame();

  /**
   * text pane to hold text
   */
    
  private static JTextPane _textPane;
    
  /**
   * private constructor for singleton.
   */

  private ParameterInformationFrame()
  {

    JrpViewParameters viewParams = JrpViewParameters.getInstance();
    int infoX = viewParams.infoX.getValue();
    int infoY = viewParams.infoY.getValue();
    int infoWidth = viewParams.infoWidth.getValue();
    int infoHeight = viewParams.infoHeight.getValue();
	
    setSize(infoWidth, infoHeight);
    setLocation(infoX, infoY);
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    addComponentListener(new MoveResizeListener());

    JPanel topPanel = new JPanel(new BorderLayout());
    Container cp = getContentPane();
    cp.add(topPanel);
	
    _textPane = new JTextPane();
    cp.add(new JScrollPane(_textPane),  BorderLayout.CENTER);

    _textPane.setContentType("text/html");
    _textPane.setAutoscrolls(true);

    JButton okButton = new JButton("OK");
    okButton.addActionListener(this);
    okButton.setToolTipText("Close window");
    JPanel okPanel = new JPanel();
    okPanel.add(okButton);
    topPanel.add(okPanel, BorderLayout.SOUTH);
	
  }

  /**
   * get singleton instance
   */

  public static ParameterInformationFrame getInstance() {
    return INSTANCE;
  }
    
  /**
   * show information for given param
   *
   * @param param specifies parameter for which to show information.
   */

  public void show(AbstractParameter param) {

    setTitle("Info for param: " + param.getLabel());
	
    String infoStr =
      "<h2>" + param.getLabel() + "</h2>\n\n" +
      "<p>"  + param.getDescription() + "\n\n" +
      "<p>"  + param.getInfo() + "\n\n";

    _textPane.setText(infoStr);
    setVisible(true);

  }
    
  /**
   * close window when OK is hit
   */

  public void actionPerformed(ActionEvent event)
  {
    if (event.getActionCommand().equals("OK")) {
      setVisible(false);
    }
  }

  // Inner classes

  private class MoveResizeListener extends ComponentAdapter
  {
	
    public void componentMoved(ComponentEvent e) {
      JrpViewParameters viewParams = JrpViewParameters.getInstance();
      int infoX = viewParams.infoX.getValue();
      int infoY = viewParams.infoY.getValue();
      if (infoX != getX() || infoY != getY()) {
        viewParams.infoX.setValue(getX());
        viewParams.infoY.setValue(getY());
      }
    }
	
    public void componentResized(ComponentEvent e) {
      JrpViewParameters viewParams = JrpViewParameters.getInstance();
      int infoWidth = viewParams.infoWidth.getValue();
      int infoHeight = viewParams.infoHeight.getValue();
      if (infoWidth != getWidth() || infoHeight != getHeight()) {
        viewParams.infoWidth.setValue(getWidth());
        viewParams.infoHeight.setValue(getHeight());
      }
    }
	
  }

}


