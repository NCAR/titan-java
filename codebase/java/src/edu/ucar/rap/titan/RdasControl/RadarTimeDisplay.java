///////////////////////////////////////////////////////////////////////
//
// RadarTimeDisplay
//
// Mike Dixon
//
// Oct 2004
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

public class RadarTimeDisplay extends JPanel implements BeamDataListener

{

    private Parameters _params = Parameters.getInstance();
    private JLabel _dateValue;
    private JLabel _timeValue;
    private BeamMessage _beam;
    
    public RadarTimeDisplay(Component parent) {
	
	setBorder(CustomBorder.createTop(parent, "Radar time", 1));
	
 	Font font =
 	    getFont().deriveFont(Font.BOLD,
				 _params.control.fontSize.getValue());
	// date panel
	
	JLabel dateLabel = new JLabel("Date", JLabel.CENTER);
	
	_dateValue = new JLabel("0000/00/00", JLabel.CENTER);
	_dateValue.setFont(font);
	_dateValue.setForeground(Color.yellow);
	
	JPanel dateValuePanel = new JPanel();
	dateValuePanel.setBackground(Color.black);
	dateValuePanel.add(_dateValue);
	
	BorderLayout dateLayout = new BorderLayout();
	dateLayout.setHgap(3);
	JPanel datePanel = new JPanel(dateLayout);
	datePanel.add(dateLabel, BorderLayout.WEST);
	datePanel.add(dateValuePanel, BorderLayout.EAST);
	
	// time panel
	
	JLabel timeLabel = new JLabel("Time", JLabel.CENTER);
	
	_timeValue = new JLabel("00:00:00", JLabel.CENTER);
	_timeValue.setFont(font);
	_timeValue.setForeground(Color.yellow);
	
	JPanel timeValuePanel = new JPanel();
	timeValuePanel.setBackground(Color.black);
	timeValuePanel.add(_timeValue);
	
	BorderLayout timeLayout = new BorderLayout();
	timeLayout.setHgap(3);
	JPanel timePanel = new JPanel(timeLayout);
	timePanel.add(timeLabel, BorderLayout.EAST);
	timePanel.add(timeValuePanel, BorderLayout.WEST);
	
	// create time panel
	
	JPanel dateTimePanel = new JPanel();
	BorderLayout layout = new BorderLayout();
	layout.setHgap(2);
	dateTimePanel.setLayout(layout);
	dateTimePanel.add(datePanel, BorderLayout.WEST);
	dateTimePanel.add(timePanel, BorderLayout.EAST);
	
	// add
	
	add(dateTimePanel);
	
 	// add listener for new beam data
	
	BeamDataHandler.getInstance().addListener(this);

    }
    
    // handle new beam
    
    public void handleBeam(BeamMessage beam, double rate) {
	_beam = beam;
    }
    
    // update the display
    
    public void redraw() {

	if (_beam == null) {
	    return;
	}
	
 	Font font =
 	    getFont().deriveFont(Font.BOLD,
				 _params.control.fontSize.getValue());
	
	_dateValue.setFont(font);
	_timeValue.setFont(font);

	_dateValue.setText(_beam.getDateString());
	_timeValue.setText(_beam.getTimeString());

    }
    
}

