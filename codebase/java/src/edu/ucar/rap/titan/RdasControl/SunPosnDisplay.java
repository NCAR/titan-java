///////////////////////////////////////////////////////////////////////
//
// SunPosnDisplay
//
// Mike Dixon
//
// Jan 2003
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

public class SunPosnDisplay extends JPanel implements SunPosnListener {

    private Parameters _params = Parameters.getInstance();
    private JLabel _elValue;
    private JLabel _azValue;
    private double _el = 0.0;
    private double _az = 0.0;
    
    public SunPosnDisplay(Component parent) {
	
	setBorder(CustomBorder.createTop(parent, "Sun position", 1));
	
 	Font font =
 	    getFont().deriveFont(Font.BOLD,
				 _params.control.fontSize.getValue());
	
	// elevation panel
	
	JLabel elLabel = new JLabel("EL", JLabel.CENTER);
	
	_elValue = new JLabel("00.00", JLabel.CENTER);
	_elValue.setFont(font);
	_elValue.setForeground(Color.green);
	
	JPanel elValuePanel = new JPanel();
	elValuePanel.setBackground(Color.black);
	elValuePanel.add(_elValue);
	
	BorderLayout elLayout = new BorderLayout();
	elLayout.setHgap(3);
	JPanel elPanel = new JPanel(elLayout);
	elPanel.add(elLabel, BorderLayout.WEST);
	elPanel.add(elValuePanel, BorderLayout.EAST);
	
	// azimuth panel
	
	JLabel azLabel = new JLabel("AZ", JLabel.CENTER);
	
	_azValue = new JLabel("000.00", JLabel.CENTER);
	_azValue.setFont(font);
	_azValue.setForeground(Color.green);
	
	JPanel azValuePanel = new JPanel();
	azValuePanel.setBackground(Color.black);
	azValuePanel.add(_azValue);
	
	BorderLayout azLayout = new BorderLayout();
	azLayout.setHgap(3);
	JPanel azPanel = new JPanel(azLayout);
	azPanel.add(azLabel, BorderLayout.EAST);
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
				 _params.control.fontSize.getValue());
	_elValue.setFont(font);
	_azValue.setFont(font);
	_elValue.setText(NFormat.el2.format(_el));
	_azValue.setText(NFormat.az2.format(_az));
    }
    
}
