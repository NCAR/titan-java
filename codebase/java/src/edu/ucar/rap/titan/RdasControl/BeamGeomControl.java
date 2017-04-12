///////////////////////////////////////////////////////////////////////
//
// BeamGeomControl panel
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

public class BeamGeomControl extends JPanel {
    
    private Parameters _params = Parameters.getInstance();
    private CommandQueue _commandQueue = CommandQueue.getInstance();

    private JTextField _nGatesTextField;
    private JTextField _startRangeTextField;
    private JTextField _gateSpacingTextField;
    private JTextField _prfTextField;

    private int _nGates;
    private double _startRange;
    private double _gateSpacing;
    private double _prf;
    
    private NGatesEditListener _nGatesEditListener;
    private StartRangeEditListener _startRangeEditListener;
    private GateSpacingEditListener _gateSpacingEditListener;
    private PrfEditListener _prfEditListener;
	
    public BeamGeomControl(Component parent) {
	
	setLayout(new BorderLayout());
	setBorder(CustomBorder.createTop(parent, "Beam geometry", 3));
	
	_nGates = _params.scan.nGates.getValue();
	_startRange = _params.scan.startRange.getValue();
	_gateSpacing = _params.scan.gateSpacing.getValue();
	_prf = _params.scan.prf.getValue();
    
	// nGates entry panel
	
	JLabel nGatesLabel = new JLabel("N Gates", JLabel.RIGHT);
	
	String nGatesStr = new
	    String(NFormat.f0.format(_params.scan.nGates.getValue()));
	_nGatesTextField = new JTextField(nGatesStr, 5);
	_nGatesTextField.setToolTipText("Set number of gates");
	_nGatesEditListener = new NGatesEditListener();
	_nGatesTextField.addActionListener(_nGatesEditListener);
	
	// startRange entry panel
	
	JLabel startRangeLabel = new JLabel("Start range", JLabel.RIGHT);
	
	String startRangeStr = new
	    String(NFormat.f3.format(_params.scan.startRange.getValue()));
	_startRangeTextField = new JTextField(startRangeStr, 5);
	_startRangeTextField.setToolTipText("Set start range (km)");
	_startRangeEditListener = new StartRangeEditListener();
	_startRangeTextField.addActionListener(_startRangeEditListener);
	
	// gateSpacing entry panel
	
	JLabel gateSpacingLabel = new JLabel("Gate spacing", JLabel.RIGHT);
	
	String gateSpacingStr = new
	    String(NFormat.f3.format(_params.scan.gateSpacing.getValue()));
	_gateSpacingTextField = new JTextField(gateSpacingStr, 5);
	_gateSpacingTextField.setToolTipText("Set gate spacing (km)");
	_gateSpacingEditListener = new GateSpacingEditListener();
	_gateSpacingTextField.addActionListener(_gateSpacingEditListener);
	
	// prf entry panel
	
	JLabel prfLabel = new JLabel("PRF", JLabel.RIGHT);
	
	String prfStr = new
	    String(NFormat.f0.format(_params.scan.prf.getValue()));
	_prfTextField = new JTextField(prfStr, 5);
	_prfTextField.setToolTipText("Set PRF (/s)");
	_prfEditListener = new PrfEditListener();
	_prfTextField.addActionListener(_prfEditListener);
	
	// combine entry panels
	
	GridLayout comboLayout = new GridLayout(2, 4, 5, 1);
	JPanel entryPanel = new JPanel(comboLayout);
	
	entryPanel.add(nGatesLabel);
	entryPanel.add(_nGatesTextField);
	entryPanel.add(startRangeLabel);
	entryPanel.add(_startRangeTextField);
	entryPanel.add(gateSpacingLabel);
	entryPanel.add(_gateSpacingTextField);
	entryPanel.add(prfLabel);
	entryPanel.add(_prfTextField);
	
	JPanel entryContainer = new JPanel();
	entryContainer.add(entryPanel);
	
	// add to main container
	
	add(entryContainer, BorderLayout.SOUTH);
	
	// add listeners so that parameter objects will update this
	// object when a parameter is edited

	_params.scan.addChangeListener(new ChangeListener());

    } // constructor
    
    // editor change listeners

    private class ChangeListener implements CollectionChangeListener {
	public void reactToChange() {
	    _updateFromParams();
	}
    }
    
    // update from parameters

    private void _updateFromParams() {

	String nGatesStr = new
	    String(NFormat.f0.format(_params.scan.nGates.getValue()));
	_nGatesTextField.setText(nGatesStr);

	String startRangeStr = new
	    String(NFormat.f3.format(_params.scan.startRange.getValue()));
	_startRangeTextField.setText(startRangeStr);

	String gateSpacingStr = new
	    String(NFormat.f3.format(_params.scan.gateSpacing.getValue()));
	_gateSpacingTextField.setText(gateSpacingStr);

	String prfStr = new
	    String(NFormat.f0.format(_params.scan.prf.getValue()));
	_prfTextField.setText(prfStr);

	validate();

    }

    // NGates edit listener
    private class NGatesEditListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    setNGatesFromTextField();
	}
    }
    
    // StartRange edit listener
    private class StartRangeEditListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    setStartRangeFromTextField();
	}
    }
    
    // GateSpacing edit listener
    private class GateSpacingEditListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    setGateSpacingFromTextField();
	}
    }
    
    // Prf edit listener
    private class PrfEditListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    setPrfFromTextField();
	}
    }
    
    // set the NGates from text field
    
    private void setNGatesFromTextField() { 
	int nGates = 0;
	boolean error = false;
	try {
	    nGates = Integer.parseInt(_nGatesTextField.getText());
	}
	catch (java.lang.NumberFormatException nfe) {
	    error = true;
	}
	if (nGates < 0) {
	    error = true;
	}
	if (error) {
	    // show error dialog
	    String errStr =
		"Illegal value for nGates: " +
		_nGatesTextField.getText() + "\n";
	    JOptionPane.showMessageDialog(_nGatesTextField, errStr,
					  "Illegal nGates value",
					  JOptionPane.ERROR_MESSAGE);
	    return;
	}
	_nGates = nGates;
	_params.scan.nGates.setValue(_nGates);
	_commandQueue.sendNGates();
    }
    
    // set the startRange from text field
    
    private void setStartRangeFromTextField() { 
	double startRange = 0.0;
	boolean error = false;
	try {
	    startRange = Double.parseDouble(_startRangeTextField.getText());
	}
	catch (java.lang.NumberFormatException nfe) {
	    error = true;
	}
	if (startRange < 0) {
	    error = true;
	}
	if (error) {
	    // show error dialog
	    String errStr =
		"Illegal value for startRange: " +
		_startRangeTextField.getText() + "\n";
	    JOptionPane.showMessageDialog(_startRangeTextField, errStr,
					  "Illegal startRange value",
					  JOptionPane.ERROR_MESSAGE);
	    return;
	}
	_startRange = startRange;
	_params.scan.startRange.setValue(_startRange);
	_commandQueue.sendStartRange();
    }
    
    // set the gateSpacing from text field
    
    private void setGateSpacingFromTextField() { 
	double gateSpacing = 0.0;
	boolean error = false;
	try {
	    gateSpacing = Double.parseDouble(_gateSpacingTextField.getText());
	}
	catch (java.lang.NumberFormatException nfe) {
	    error = true;
	}
	if (gateSpacing < 0) {
	    error = true;
	}
	if (error) {
	    // show error dialog
	    String errStr =
		"Illegal value for gateSpacing: " +
		_gateSpacingTextField.getText() + "\n";
	    JOptionPane.showMessageDialog(_gateSpacingTextField, errStr,
					  "Illegal gateSpacing value",
					  JOptionPane.ERROR_MESSAGE);
	    return;
	}
	_gateSpacing = gateSpacing;
	_params.scan.gateSpacing.setValue(_gateSpacing);
	_commandQueue.sendGateSpacing();
    }
    
    // set the prf from text field
    
    private void setPrfFromTextField() { 
	double prf = 0.0;
	boolean error = false;
	try {
	    prf = Double.parseDouble(_prfTextField.getText());
	}
	catch (java.lang.NumberFormatException nfe) {
	    error = true;
	}
	if (prf <= 0) {
	    error = true;
	}
	if (error) {
	    // show error dialog
	    String errStr =
		"Illegal value for prf: " +
		_prfTextField.getText() + "\n";
	    JOptionPane.showMessageDialog(_prfTextField, errStr,
					  "Illegal prf value",
					  JOptionPane.ERROR_MESSAGE);
	    return;
	}
	_prf = prf;
	_params.scan.prf.setValue(_prf);
	_commandQueue.sendPrf();
    }
    
} // Beam Management Panel
    
