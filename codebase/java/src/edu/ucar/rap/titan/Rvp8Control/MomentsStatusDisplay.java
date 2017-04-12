///////////////////////////////////////////////////////////////////////
//
// MomentsStatusDisplay
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
import edu.ucar.rap.jrp.*;

public class MomentsStatusDisplay extends JPanel implements StatusDataListener

{

  private Parameters _params = Parameters.getInstance();
  
  private JLabel _majorModeValue;
  private JLabel _rdaVersionValue;
  
  private StatusData _status;

  public MomentsStatusDisplay(JFrame frame) {
	
    setLayout(new BorderLayout());
    setBorder(CustomBorder.createTop(frame, "Moments status", 3));
    
    // majorMode
    
    JLabel majorModeLabel = new JLabel("Major mode", JLabel.CENTER);
    String majorModeStr = new String("-8888");
    _majorModeValue = new JLabel(majorModeStr);
    _majorModeValue.setForeground(Color.yellow);
    JPanel majorModeValuePanel = new JPanel();
    majorModeValuePanel.setBackground(Color.black);
    majorModeValuePanel.add(_majorModeValue);
    
    BorderLayout majorModeLayout = new BorderLayout();
    majorModeLayout.setHgap(5);
    JPanel majorModePanel = new JPanel(majorModeLayout);
    majorModePanel.add(majorModeLabel, BorderLayout.WEST);
    majorModePanel.add(majorModeValuePanel, BorderLayout.EAST);

    // rdaVersion
    
    JLabel rdaVersionLabel = new JLabel("RdaVersion", JLabel.CENTER);
    String rdaVersionStr = new String("-88.88");
    _rdaVersionValue = new JLabel(rdaVersionStr);
    _rdaVersionValue.setForeground(Color.yellow);
    JPanel rdaVersionValuePanel = new JPanel();
    rdaVersionValuePanel.setBackground(Color.black);
    rdaVersionValuePanel.add(_rdaVersionValue);
	    
    BorderLayout rdaVersionLayout = new BorderLayout();
    rdaVersionLayout.setHgap(5);
    JPanel rdaVersionPanel = new JPanel(rdaVersionLayout);
    rdaVersionPanel.add(rdaVersionLabel, BorderLayout.WEST);
    rdaVersionPanel.add(rdaVersionValuePanel, BorderLayout.EAST);

    // combine panels
	    
    BorderLayout layout1 = new BorderLayout();
    layout1.setVgap(3);
    JPanel panel1 = new JPanel(layout1);
    panel1.add(majorModePanel, BorderLayout.NORTH);
    panel1.add(rdaVersionPanel, BorderLayout.CENTER);

    BorderLayout combinedLayout = new BorderLayout();
    combinedLayout.setVgap(3);
    JPanel combinedPanel = new JPanel(combinedLayout);
    combinedPanel.add(panel1, BorderLayout.NORTH);

    // add to main container
    
    add(combinedPanel, BorderLayout.NORTH);
    
    // add params change listener

    _params.status.addChangeListener(new ParamsChangeListener());
    
    // add listener for new beam data
    
    StatusDataHandler.getInstance().addListener(this);
	
  } // constructor
	
  // handle new beam
    
  public void handleStatus(StatusData status) {
    _status = status;
    redraw();
  }
  
  // update the display
    
  public void redraw() {

    if (_status == null) {
      return;
    }
    
    _majorModeValue.setText(_status.majorMode);
    
    _rdaVersionValue.setText(_status.rdaVersion);

  }
    
  // Listener for changes in parameters

  private class ParamsChangeListener implements CollectionChangeListener {
    public void reactToChange() {
      updateFromParams();
    }
  }
  
  protected void updateFromParams() {
  }

}

