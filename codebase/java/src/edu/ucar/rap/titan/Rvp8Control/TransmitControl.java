///////////////////////////////////////////////////////////////////////
//
// TransmitControl
//
// Mike Dixon
//
// Oct 2002
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

public class TransmitControl extends JPanel

{

  private Parameters _params = Parameters.getInstance();

  private JComboBox _opsModeComboBox;
  private JComboBox _prfModeComboBox;
  private JTextField _prfTextField;
  private JComboBox _phaseCodingComboBox;
  private JComboBox _polarizationComboBox;

  private OpsModeEditListener _opsModeEditListener;
  private PrfModeEditListener _prfModeEditListener;
  private PrfEditListener _prfEditListener;
  private PhaseCodingEditListener _phaseCodingEditListener;
  private PolarizationEditListener _polarizationEditListener;

  public TransmitControl(JFrame frame) {
    
    setLayout(new BorderLayout());
    setBorder(CustomBorder.createTop(frame, "Transmit control", 3));
	
    // fonts
    
    Font defaultFont = getFont();
    Font midFont =
      defaultFont.deriveFont((float) (defaultFont.getSize() + 0));
    Font bigFont =
      defaultFont.deriveFont((float) (defaultFont.getSize() + 2));
    
    // operations mode
    
    JLabel opsModeLabel = new JLabel("Ops mode", JLabel.CENTER);
    opsModeLabel.setFont(midFont);
    
    String opsMode = new String(_params.transmit.opsMode.getValue());
    String[] opsModes = _params.transmit.opsMode.getOptions();
    _opsModeComboBox = new JComboBox(opsModes);
    for (int ii = 0; ii < _opsModeComboBox.getItemCount(); ii++) {
      if (opsMode.equals(_opsModeComboBox.getItemAt(ii))) {
        _opsModeComboBox.setSelectedIndex(ii);
      }
    }
    _opsModeComboBox.setToolTipText("Set RVP8 ops mode");
    _opsModeEditListener = new OpsModeEditListener();
    _opsModeComboBox.addActionListener(_opsModeEditListener);
	    
    BorderLayout opsModeLayout = new BorderLayout();
    opsModeLayout.setHgap(5);
    JPanel opsModePanel = new JPanel(opsModeLayout);
    opsModePanel.add(opsModeLabel, BorderLayout.WEST);
    opsModePanel.add(_opsModeComboBox, BorderLayout.EAST);
	    
    // prf mode
    
    JLabel prfModeLabel = new JLabel("Prf mode", JLabel.CENTER);
    prfModeLabel.setFont(midFont);
    
    String prfMode = new String(_params.transmit.prfMode.getValue());
    String[] prfModes = _params.transmit.prfMode.getOptions();
    _prfModeComboBox = new JComboBox(prfModes);
    for (int ii = 0; ii < _prfModeComboBox.getItemCount(); ii++) {
      if (prfMode.equals(_prfModeComboBox.getItemAt(ii))) {
        _prfModeComboBox.setSelectedIndex(ii);
      }
    }
    _prfModeComboBox.setToolTipText("Set RVP8 prf mode");
    _prfModeEditListener = new PrfModeEditListener();
    _prfModeComboBox.addActionListener(_prfModeEditListener);
	    
    BorderLayout prfModeLayout = new BorderLayout();
    prfModeLayout.setHgap(5);
    JPanel prfModePanel = new JPanel(prfModeLayout);
    prfModePanel.add(prfModeLabel, BorderLayout.WEST);
    prfModePanel.add(_prfModeComboBox, BorderLayout.EAST);
	    
    // prf
    
    JLabel prfLabel = new JLabel("PRF (/s)", JLabel.CENTER);
    prfLabel.setFont(midFont);
    
    String prfStr = new String(_params.transmit.prf.toString());
    _prfTextField = new JTextField(prfStr, 5);
    _prfTextField.setToolTipText("Set transmitter PRF (/secs)");
    _prfEditListener = new PrfEditListener();
    _prfTextField.addActionListener(_prfEditListener);
	    
    BorderLayout prfLayout = new BorderLayout();
    prfLayout.setHgap(5);
    JPanel prfPanel = new JPanel(prfLayout);
    prfPanel.add(prfLabel, BorderLayout.WEST);
    prfPanel.add(_prfTextField, BorderLayout.EAST);
	    
    // phase coding
    
    JLabel phaseCodingLabel = new JLabel("Phase coding", JLabel.CENTER);
    phaseCodingLabel.setFont(midFont);
    
    String phaseCoding = new String(_params.transmit.phaseCoding.getValue());
    String[] phaseCodings = _params.transmit.phaseCoding.getOptions();
    _phaseCodingComboBox = new JComboBox(phaseCodings);
    for (int ii = 0; ii < _phaseCodingComboBox.getItemCount(); ii++) {
      if (phaseCoding.equals(_phaseCodingComboBox.getItemAt(ii))) {
        _phaseCodingComboBox.setSelectedIndex(ii);
      }
    }
    _phaseCodingComboBox.setToolTipText("Set phase coding");
    _phaseCodingEditListener = new PhaseCodingEditListener();
    _phaseCodingComboBox.addActionListener(_phaseCodingEditListener);
	    
    BorderLayout phaseCodingLayout = new BorderLayout();
    phaseCodingLayout.setHgap(5);
    JPanel phaseCodingPanel = new JPanel(phaseCodingLayout);
    phaseCodingPanel.add(phaseCodingLabel, BorderLayout.WEST);
    phaseCodingPanel.add(_phaseCodingComboBox, BorderLayout.EAST);
	    
    // polarization
    
    JLabel polarizationLabel = new JLabel("Polarization", JLabel.CENTER);
    polarizationLabel.setFont(midFont);
    
    String polarization = _params.transmit.polarization.getValue();
    String[] polarizations = _params.transmit.polarization.getOptions();
    _polarizationComboBox = new JComboBox(polarizations);
    for (int ii = 0; ii < _polarizationComboBox.getItemCount(); ii++) {
      if (polarization.equals(_polarizationComboBox.getItemAt(ii))) {
        _polarizationComboBox.setSelectedIndex(ii);
      }
    }
    _polarizationComboBox.setToolTipText("Set polarization");
    _polarizationEditListener = new PolarizationEditListener();
    _polarizationComboBox.addActionListener(_polarizationEditListener);
	    
    BorderLayout polarizationLayout = new BorderLayout();
    polarizationLayout.setHgap(5);
    JPanel polarizationPanel = new JPanel(polarizationLayout);
    polarizationPanel.add(polarizationLabel, BorderLayout.WEST);
    polarizationPanel.add(_polarizationComboBox, BorderLayout.EAST);
	    
    // info button
	    
    JrpDummyComponent dc = new JrpDummyComponent();
    Image infoImage =
      JrpImageLoad.getFromRes(dc, "/edu/ucar/rap/jrp/images/info.png");
    ImageIcon infoIcon = new ImageIcon(infoImage);
    JButton infoButton = new JButton("", infoIcon);
    infoButton.setPreferredSize(new Dimension(infoImage.getWidth(dc),
                                              infoImage.getHeight(dc)));
    infoButton.setToolTipText("Click for more info");
    infoButton.addActionListener(new InfoListener());
    JPanel infoPanel = new JPanel(new FlowLayout());
    infoPanel.add(infoButton);

    // combine items into a single panel
    
    BorderLayout upperLayout = new BorderLayout();
    upperLayout.setVgap(5);
    JPanel upperPanel = new JPanel(upperLayout);
    upperPanel.add(opsModePanel, BorderLayout.NORTH);
    upperPanel.add(prfModePanel, BorderLayout.CENTER);
    upperPanel.add(prfPanel, BorderLayout.SOUTH);
    
    BorderLayout lowerLayout = new BorderLayout();
    lowerLayout.setVgap(3);
    JPanel lowerPanel = new JPanel(lowerLayout);
    lowerPanel.add(phaseCodingPanel, BorderLayout.NORTH);
    lowerPanel.add(polarizationPanel, BorderLayout.SOUTH);
    
    BorderLayout comboLayout = new BorderLayout();
    comboLayout.setVgap(3);

    JPanel entryContainer = new JPanel(comboLayout);
    entryContainer.add(upperPanel, BorderLayout.NORTH);
    entryContainer.add(lowerPanel, BorderLayout.CENTER);
    entryContainer.add(infoPanel, BorderLayout.SOUTH);
	    
    // add to main container
    
    add(entryContainer, BorderLayout.NORTH);

    // add params change listener
    
    _params.transmit.addChangeListener(new TransmitParamsChangeListener());

    // update state from parameters

    updateFromParams();

  } // constructor

  //////////////////
  // edit actions

  // ops mode edit action

  public class OpsModeEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      String selected = (String) _opsModeComboBox.getSelectedItem();
      _params.transmit.opsMode.setValue(selected);
      _params.transmit.syncParamToView();
      _params.transmit.callChangeListeners();
    }
  }

  // prf mode edit action

  public class PrfModeEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      String selected = (String) _prfModeComboBox.getSelectedItem();
      _params.transmit.prfMode.setValue(selected);
      _params.transmit.syncParamToView();
      _params.transmit.callChangeListeners();
    }
  }

  // gate spacing edit action
  class PrfEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      setPrfFromTextField();
      _params.transmit.syncParamToView();
      _params.transmit.callChangeListeners();
    }
  }

  // ops mode edit action

  public class PhaseCodingEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      String selected = (String) _phaseCodingComboBox.getSelectedItem();
      _params.transmit.phaseCoding.setValue(selected);
      _params.transmit.syncParamToView();
      _params.transmit.callChangeListeners();
    }
  }

  // prf mode edit action

  public class PolarizationEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      String selected = (String) _polarizationComboBox.getSelectedItem();
      _params.transmit.polarization.setValue(selected);
      _params.transmit.syncParamToView();
      _params.transmit.callChangeListeners();
    }
  }

  // info listener

  private class InfoListener implements ActionListener {
    public InfoListener() {
    }
    public void actionPerformed(ActionEvent event) {
      _params.transmit.getCollectionFrame().setVisible(true);
    }
  }

  //////////////////////////////////////////
  // Setting the members

  // set ops mode from string

  private void _setOpsMode(String mode) {
    for (int ii = 0; ii < _opsModeComboBox.getItemCount(); ii++) {
      if (mode.equals(_opsModeComboBox.getItemAt(ii))) {
        _opsModeComboBox.setSelectedIndex(ii);
      }
    }
  }

  // set prf mode from string

  private void _setPrfMode(String mode) {
    for (int ii = 0; ii < _prfModeComboBox.getItemCount(); ii++) {
      if (mode.equals(_prfModeComboBox.getItemAt(ii))) {
        _prfModeComboBox.setSelectedIndex(ii);
      }
    }
  }

  // set the PRF from text field
  
  public void setPrfFromTextField() { 
    double prf = 250.0;
    boolean error = false;
    try {
      prf = Double.parseDouble(_prfTextField.getText());
    }
    catch (java.lang.NumberFormatException nfe) {
      error = true;
    }
    if (error) {
      // show error dialog
      String errStr =
        "Illegal value for PRF: " +
        _prfTextField.getText() + "\n";
      JOptionPane.showMessageDialog(_prfTextField, errStr,
                                    "Illegal PRF", JOptionPane.ERROR_MESSAGE);
      return;
    }
    _params.transmit.prf.setValue(prf);
  }

  // set ops mode from string

  private void _setPhaseCoding(String mode) {
    for (int ii = 0; ii < _phaseCodingComboBox.getItemCount(); ii++) {
      if (mode.equals(_phaseCodingComboBox.getItemAt(ii))) {
        _phaseCodingComboBox.setSelectedIndex(ii);
      }
    }
  }

  // set prf mode from string

  private void _setPolarization(String mode) {
    for (int ii = 0; ii < _polarizationComboBox.getItemCount(); ii++) {
      if (mode.equals(_polarizationComboBox.getItemAt(ii))) {
        _polarizationComboBox.setSelectedIndex(ii);
      }
    }
  }

  /////////////////////////////////////
  // Listener for changes in parameters
  
  private class TransmitParamsChangeListener implements CollectionChangeListener {
    public void reactToChange() {
      updateFromParams();
    }
  }
  
  protected void updateFromParams() {

    String opsMode = _params.transmit.opsMode.getValue();
    _setOpsMode(opsMode);

    String prfMode = _params.transmit.prfMode.getValue();
    _setPrfMode(prfMode);

    _prfTextField.setText(_params.transmit.prf.toString());

    String phaseCoding = _params.transmit.phaseCoding.getValue();
    _setPhaseCoding(phaseCoding);

    String polarization = _params.transmit.polarization.getValue();
    _setPolarization(polarization);

    if (opsMode.equals("active")) {
      _prfModeComboBox.setEnabled(true);
      _prfTextField.setEnabled(true);
    } else {
      _prfModeComboBox.setEnabled(false);
      _prfTextField.setEnabled(false);
    }
      
  }

}

