///////////////////////////////////////////////////////////////////////
//
// ReceiveControl
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

public class ReceiveControl extends JPanel

{

  private Parameters _params = Parameters.getInstance();
  
  private JTextField _nGatesTextField;
  private JTextField _gateSpacingTextField;
  
  private NGatesEditListener _nGatesEditListener;
  private GateSpacingEditListener _gateSpacingEditListener;

  public ReceiveControl(JFrame frame) {
	
    setLayout(new BorderLayout());
    setBorder(CustomBorder.createTop(frame, "Receive control", 3));
	
    // fonts
    
    Font defaultFont = getFont();
    Font midFont =
      defaultFont.deriveFont((float) (defaultFont.getSize() + 0));
    Font bigFont =
      defaultFont.deriveFont((float) (defaultFont.getSize() + 2));
    
    // n gates entry panel
    
    JLabel nGatesLabel = new JLabel("N gates", JLabel.CENTER);
    nGatesLabel.setFont(midFont);

    String nGatesStr = new
      String(_params.receive.nGates.toString());
    _nGatesTextField = new JTextField(nGatesStr, 5);
    _nGatesTextField.setToolTipText("Set receiver number of gates");
    _nGatesEditListener = new NGatesEditListener();
    _nGatesTextField.addActionListener(_nGatesEditListener);
	    
    BorderLayout nGatesLayout = new BorderLayout();
    nGatesLayout.setHgap(5);
    JPanel nGatesPanel = new JPanel(nGatesLayout);
    nGatesPanel.add(nGatesLabel, BorderLayout.WEST);
    nGatesPanel.add(_nGatesTextField, BorderLayout.EAST);
	    
    // gate spacing entry panel
    
    JLabel gateSpacingLabel = new JLabel("Gate spacing (m)", JLabel.CENTER);
    gateSpacingLabel.setFont(midFont);
    
    String gateSpacingStr =
      new String(_params.receive.gateSpacingM.toString());
    _gateSpacingTextField = new JTextField(gateSpacingStr, 5);
    _gateSpacingTextField.setToolTipText("Set receiver gate spacing (meters)");
    _gateSpacingEditListener = new GateSpacingEditListener();
    _gateSpacingTextField.addActionListener(_gateSpacingEditListener);
	    
    BorderLayout gateSpacingLayout = new BorderLayout();
    gateSpacingLayout.setHgap(5);
    JPanel gateSpacingPanel = new JPanel(gateSpacingLayout);
    gateSpacingPanel.add(gateSpacingLabel, BorderLayout.WEST);
    gateSpacingPanel.add(_gateSpacingTextField, BorderLayout.EAST);

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

    // combine entry panels
	    
    BorderLayout comboLayout = new BorderLayout();
    comboLayout.setVgap(3);
    JPanel entryPanel = new JPanel(comboLayout);
    entryPanel.add(nGatesPanel, BorderLayout.NORTH);
    entryPanel.add(gateSpacingPanel, BorderLayout.CENTER);
    entryPanel.add(infoPanel, BorderLayout.SOUTH);
	    
    // add to main container
    
    JPanel entryContainer = new JPanel(new BorderLayout());
    entryContainer.add(entryPanel, BorderLayout.NORTH);
    entryContainer.add(infoPanel, BorderLayout.SOUTH);

    add(entryContainer, BorderLayout.NORTH);

    // add params change listener

    _params.receive.addChangeListener(new ParamsChangeListener());
    
  } // constructor
	
  // n gates edit action
  public class NGatesEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      setNGatesFromTextField();
      _params.receive.syncParamToView();
      _params.receive.callChangeListeners();
    }
  }
	
  // gate spacing edit action
  class GateSpacingEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      setGateSpacingFromTextField();
      _params.receive.syncParamToView();
      _params.receive.callChangeListeners();
    }
  }
  
  // info listener

  private class InfoListener implements ActionListener {
    public InfoListener() {
    }
    public void actionPerformed(ActionEvent event) {
      _params.receive.getCollectionFrame().setVisible(true);
    }
  }

  // set n gates from text field
  
  public void setNGatesFromTextField() { 
    int nGates = 1000;
    int minNGates = _params.receive.nGates.getMinValue();
    int maxNGates = _params.receive.nGates.getMaxValue();
    boolean error = false;
    try {
      nGates = Integer.parseInt(_nGatesTextField.getText());
    }
    catch (java.lang.NumberFormatException nfe) {
      error = true;
    }
    if (nGates < minNGates || nGates > maxNGates) {
      error = true;
    }
    if (error) {
      // show error dialog
      String errStr =
        "Illegal value for number of gates: " +
        _nGatesTextField.getText() + "\n" +
        "Min n gates: " + minNGates + "\n" +
        "Max n gates: " + maxNGates + "\n";
      JOptionPane.showMessageDialog(_gateSpacingTextField, errStr,
                                    "Illegal number of gates",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }
    _params.receive.nGates.setValue(nGates);
  }
  
  // set the gate spacing from text field
  
  public void setGateSpacingFromTextField() { 
    double gateSpacing = 0.0;
    double minSpacing = _params.receive.gateSpacingM.getMinValue();
    double maxSpacing = _params.receive.gateSpacingM.getMaxValue();
    boolean error = false;
    try {
      gateSpacing = Double.parseDouble(_gateSpacingTextField.getText());
    }
    catch (java.lang.NumberFormatException nfe) {
      error = true;
    }
    if (gateSpacing < minSpacing || gateSpacing > maxSpacing) {
      error = true;
    }
    if (error) {
      // show error dialog
      String errStr =
        "Illegal value for gate spacing: " +
        _gateSpacingTextField.getText() + "\n" +
        "Min gate spacing: " + minSpacing + "\n" +
        "Max gate spacing: " + maxSpacing + "\n";
      JOptionPane.showMessageDialog(_gateSpacingTextField, errStr,
                                    "Illegal gate spacing",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }
    _params.receive.gateSpacingM.setValue(gateSpacing);
  }
  
  // Listener for changes in parameters

  private class ParamsChangeListener implements CollectionChangeListener {
    public void reactToChange() {
      updateFromParams();
    }
  }
  
  protected void updateFromParams() {
    _nGatesTextField.setText(_params.receive.nGates.toString());
    _gateSpacingTextField.setText(_params.receive.gateSpacingM.toString());
  }

}

