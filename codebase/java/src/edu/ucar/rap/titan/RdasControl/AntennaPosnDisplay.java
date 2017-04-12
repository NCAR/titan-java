///////////////////////////////////////////////////////////////////////
//
// AntennaPosnDisplay
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

public class AntennaPosnDisplay extends JPanel implements BeamDataListener {

    private Parameters _params = Parameters.getInstance();
    private JLabel _elValue;
    private JLabel _azValue;
    private JLabel _azRateValue;
    private BeamMessage _beam;
    private double _beamRate = 0.0;
    private double _slewRate = 0.0;

    private double _prevAzForSlewRate = 0.0;
    private long _prevTimeForAzSlewRate = TimeManager.getTime();
    
    public AntennaPosnDisplay(Component parent) {
	
	setBorder(CustomBorder.createTop(parent, "Antenna position", 1));
	
 	Font font =
 	    getFont().deriveFont(Font.BOLD,
				 _params.control.fontSize.getValue());

	// elevation panel
	
	JLabel elLabel = new JLabel("EL", JLabel.CENTER);
	
	_elValue = new JLabel("00.00", JLabel.CENTER);
	_elValue.setFont(font);
	_elValue.setForeground(Color.yellow);
	
	JPanel elValuePanel = new JPanel();
	elValuePanel.setBackground(Color.black);
	elValuePanel.add(_elValue);
	
	BorderLayout elLayout = new BorderLayout();
	elLayout.setHgap(2);
	JPanel elPanel = new JPanel(elLayout);
	elPanel.add(elLabel, BorderLayout.WEST);
	elPanel.add(elValuePanel, BorderLayout.EAST);
	
	// azimuth panel
	
	JLabel azLabel = new JLabel("AZ", JLabel.CENTER);
	
	_azValue = new JLabel("000.00", JLabel.CENTER);
	_azValue.setFont(font);
	_azValue.setForeground(Color.yellow);
	
	JPanel azValuePanel = new JPanel();
	azValuePanel.setBackground(Color.black);
	azValuePanel.add(_azValue);
	
	BorderLayout azLayout = new BorderLayout();
	azLayout.setHgap(2);
	JPanel azPanel = new JPanel(azLayout);
	azPanel.add(azLabel, BorderLayout.WEST);
	azPanel.add(azValuePanel, BorderLayout.EAST);
	
	// rate panel
	
	JLabel azRateLabel = new JLabel("AZ RATE", JLabel.CENTER);
	
	_azRateValue = new JLabel("000.00", JLabel.CENTER);
	_azRateValue.setFont(font);
	_azRateValue.setForeground(Color.yellow);
	
	JPanel azRateValuePanel = new JPanel();
	azRateValuePanel.setBackground(Color.black);
	azRateValuePanel.add(_azRateValue);
	
	BorderLayout azRateLayout = new BorderLayout();
	azRateLayout.setHgap(2);
	JPanel azRatePanel = new JPanel(azRateLayout);
	azRatePanel.add(azRateLabel, BorderLayout.WEST);
	azRatePanel.add(azRateValuePanel, BorderLayout.EAST);
	
	// create position panel
	
	JPanel posPanel = new JPanel();
	BorderLayout layout = new BorderLayout();
	layout.setHgap(2);
	posPanel.setLayout(layout);
	posPanel.add(elPanel, BorderLayout.WEST);
	posPanel.add(azPanel, BorderLayout.CENTER);
	posPanel.add(azRatePanel, BorderLayout.EAST);
	
	// add
	
	add(posPanel);
	
 	// add listener for new beam data
	
	BeamDataHandler.getInstance().addListener(this);
	
    }
	
     // handle new beam
    
    public void handleBeam(BeamMessage beam, double rate) {

	_beam = beam;
	_beamRate = rate;
	
	// compute azimuth slew rate
	
	long now = TimeManager.getTime();
	double deltaSecs = (now - _prevTimeForAzSlewRate) / 1000.0;
	if (deltaSecs > 4.0) {
	    double deltaAz = _beam.getAz() - _prevAzForSlewRate;
	    if (deltaAz > 180.0) {
		deltaAz -= 360.0;
	    } else if (deltaAz < -180.0) {
		deltaAz += 360.0;
	    }
	    _slewRate = deltaAz / deltaSecs;
	    _prevTimeForAzSlewRate = now;
	    _prevAzForSlewRate = _beam.getAz();
	}

    }
    
    // update the display
    
    public void redraw() {

	if (_beam == null) {
	    return;
	}
	
 	Font font =
 	    getFont().deriveFont(Font.BOLD,
				 _params.control.fontSize.getValue());

	_elValue.setFont(font);
	_azValue.setFont(font);
	_azRateValue.setFont(font);
	
	_elValue.setText(NFormat.el2.format(_beam.getEl()));
	_azValue.setText(NFormat.az2.format(_beam.getAz()));
	if (_slewRate >= 0) {
	    _azRateValue.setText(NFormat.f2_3.format(_slewRate));
	} else {
	    _azRateValue.setText(NFormat.f2_2.format(_slewRate));
	}
	
    }
    
}
