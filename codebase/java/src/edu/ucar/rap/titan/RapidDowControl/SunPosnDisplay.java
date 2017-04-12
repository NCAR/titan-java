///////////////////////////////////////////////////////////////////////
//
// SunPosnDisplay
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
import edu.ucar.rap.jrp.*;

public class SunPosnDisplay extends JPanel implements SunPosnListener {

  private Parameters _params = Parameters.getInstance();

  private JLabel _elValue;
  private JLabel _azValue;
  
  private JLabel _elLabel;
  private JLabel _azLabel;
  
  private double _el = 0.0;
  private double _az = 0.0;
    
    public SunPosnDisplay(JFrame frame) {
	
	setBorder(CustomBorder.createTop(frame, "Sun position", 1));
	
 	Font font =
 	    getFont().deriveFont(Font.BOLD,
				 _params.status.fontSize.getValue());
	
	// elevation panel
	
	_elLabel = new JLabel("EL", JLabel.CENTER);
	
	_elValue = new JLabel("00.00", JLabel.CENTER);
	_elValue.setFont(font);
	_elValue.setForeground(Color.yellow);
	
	JPanel elValuePanel = new JPanel();
	elValuePanel.setBackground(Color.black);
	elValuePanel.add(_elValue);
	
	BorderLayout elLayout = new BorderLayout();
	elLayout.setHgap(3);
	JPanel elPanel = new JPanel(elLayout);
	elPanel.add(_elLabel, BorderLayout.WEST);
	elPanel.add(elValuePanel, BorderLayout.EAST);
	
	// azimuth panel
	
	_azLabel = new JLabel("AZ", JLabel.CENTER);
	
	_azValue = new JLabel("000.00", JLabel.CENTER);
	_azValue.setFont(font);
	_azValue.setForeground(Color.yellow);
	
	JPanel azValuePanel = new JPanel();
	azValuePanel.setBackground(Color.black);
	azValuePanel.add(_azValue);
	
	BorderLayout azLayout = new BorderLayout();
	azLayout.setHgap(3);
	JPanel azPanel = new JPanel(azLayout);
	azPanel.add(_azLabel, BorderLayout.EAST);
	azPanel.add(azValuePanel, BorderLayout.WEST);
	
	// create position panel
	
	JPanel posPanel = new JPanel();
	BorderLayout posLayout = new BorderLayout();
	posLayout.setHgap(5);
	posPanel.setLayout(posLayout);
	posPanel.add(elPanel, BorderLayout.WEST);
	posPanel.add(azPanel, BorderLayout.EAST);
	
	// add
	
	add(posPanel);
	
	// add listener for new sun position
	
	SunTrack.getInstance().addSunPosnListener(this);
	
    }
    
    public void setSunPosn(double el, double az) {
	_el = el;
	_az = az;
    }
    
    public void redraw() {
 	Font font =
 	    getFont().deriveFont(Font.BOLD,
				 _params.status.fontSize.getValue());
	_elValue.setFont(font);
	_azValue.setFont(font);
	_elLabel.setFont(font);
	_azLabel.setFont(font);
	_elValue.setText(NFormat.el2.format(_el));
	_azValue.setText(NFormat.az2.format(_az));
    }
    
}
