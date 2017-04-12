///////////////////////////////////////////////////////////////////////
//
// TransmitStatusDisplay
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

public class TransmitStatusDisplay extends JPanel implements StatusDataListener

{

  private Parameters _params = Parameters.getInstance();
  
  private JLabel _wavelengthCmValue;
  private JLabel _prfModeValue;
  private JLabel _prfValue;
  private JLabel _phaseCodingValue;
  private JLabel _polarizationValue;
  private JLabel _pulseWidthUsValue;
  
  private StatusData _status;

  public TransmitStatusDisplay(JFrame frame) {
	
    setLayout(new BorderLayout());
    setBorder(CustomBorder.createTop(frame, "Transmit status", 3));
    
    // wavelength
    
    JLabel wavelengthCmLabel = new JLabel("Wavelength (cm)", JLabel.CENTER);
    String wavelengthCmStr = new String("-8888");
    _wavelengthCmValue = new JLabel(wavelengthCmStr);
    _wavelengthCmValue.setForeground(Color.yellow);
    JPanel wavelengthCmValuePanel = new JPanel();
    wavelengthCmValuePanel.setBackground(Color.black);
    wavelengthCmValuePanel.add(_wavelengthCmValue);
	    
    BorderLayout wavelengthCmLayout = new BorderLayout();
    wavelengthCmLayout.setHgap(5);
    JPanel wavelengthCmPanel = new JPanel(wavelengthCmLayout);
    wavelengthCmPanel.add(wavelengthCmLabel, BorderLayout.WEST);
    wavelengthCmPanel.add(wavelengthCmValuePanel, BorderLayout.EAST);

    // prfMode
    
    JLabel prfModeLabel = new JLabel("PRF mode", JLabel.CENTER);
    String prfModeStr = new String("-8888");
    _prfModeValue = new JLabel(prfModeStr);
    _prfModeValue.setForeground(Color.yellow);
    JPanel prfModeValuePanel = new JPanel();
    prfModeValuePanel.setBackground(Color.black);
    prfModeValuePanel.add(_prfModeValue);
    
    BorderLayout prfModeLayout = new BorderLayout();
    prfModeLayout.setHgap(5);
    JPanel prfModePanel = new JPanel(prfModeLayout);
    prfModePanel.add(prfModeLabel, BorderLayout.WEST);
    prfModePanel.add(prfModeValuePanel, BorderLayout.EAST);

    // prf
    
    JLabel prfLabel = new JLabel("PRF (hz)", JLabel.CENTER);
    String prfStr = new String("-8888");
    _prfValue = new JLabel(prfStr);
    _prfValue.setForeground(Color.yellow);
    JPanel prfValuePanel = new JPanel();
    prfValuePanel.setBackground(Color.black);
    prfValuePanel.add(_prfValue);
	    
    BorderLayout prfLayout = new BorderLayout();
    prfLayout.setHgap(5);
    JPanel prfPanel = new JPanel(prfLayout);
    prfPanel.add(prfLabel, BorderLayout.WEST);
    prfPanel.add(prfValuePanel, BorderLayout.EAST);

    // phaseCoding
    
    JLabel phaseCodingLabel = new JLabel("Phase coding", JLabel.CENTER);
    String phaseCodingStr = new String("-8888");
    _phaseCodingValue = new JLabel(phaseCodingStr);
    _phaseCodingValue.setForeground(Color.yellow);
    JPanel phaseCodingValuePanel = new JPanel();
    phaseCodingValuePanel.setBackground(Color.black);
    phaseCodingValuePanel.add(_phaseCodingValue);
	    
    BorderLayout phaseCodingLayout = new BorderLayout();
    phaseCodingLayout.setHgap(5);
    JPanel phaseCodingPanel = new JPanel(phaseCodingLayout);
    phaseCodingPanel.add(phaseCodingLabel, BorderLayout.WEST);
    phaseCodingPanel.add(phaseCodingValuePanel, BorderLayout.EAST);
    
    // polarization
    
    JLabel polarizationLabel = new JLabel("Polarization", JLabel.CENTER);
    String polarizationStr = new String("-88.88");
    _polarizationValue = new JLabel(polarizationStr);
    _polarizationValue.setForeground(Color.yellow);
    JPanel polarizationValuePanel = new JPanel();
    polarizationValuePanel.setBackground(Color.black);
    polarizationValuePanel.add(_polarizationValue);
	    
    BorderLayout polarizationLayout = new BorderLayout();
    polarizationLayout.setHgap(5);
    JPanel polarizationPanel = new JPanel(polarizationLayout);
    polarizationPanel.add(polarizationLabel, BorderLayout.WEST);
    polarizationPanel.add(polarizationValuePanel, BorderLayout.EAST);

    // pulseWidthUs
    
    JLabel pulseWidthUsLabel = new JLabel("Pulse width (us)", JLabel.CENTER);
    String pulseWidthUsStr = new String("-88.88");
    _pulseWidthUsValue = new JLabel(pulseWidthUsStr);
    _pulseWidthUsValue.setForeground(Color.yellow);
    JPanel pulseWidthUsValuePanel = new JPanel();
    pulseWidthUsValuePanel.setBackground(Color.black);
    pulseWidthUsValuePanel.add(_pulseWidthUsValue);
	    
    BorderLayout pulseWidthUsLayout = new BorderLayout();
    pulseWidthUsLayout.setHgap(5);
    JPanel pulseWidthUsPanel = new JPanel(pulseWidthUsLayout);
    pulseWidthUsPanel.add(pulseWidthUsLabel, BorderLayout.WEST);
    pulseWidthUsPanel.add(pulseWidthUsValuePanel, BorderLayout.EAST);

    // combine panels
	    
    BorderLayout layout1 = new BorderLayout();
    layout1.setVgap(3);
    JPanel panel1 = new JPanel(layout1);
    panel1.add(wavelengthCmPanel, BorderLayout.NORTH);
    panel1.add(prfModePanel, BorderLayout.CENTER);
    panel1.add(prfPanel, BorderLayout.SOUTH);

    BorderLayout layout2 = new BorderLayout();
    layout2.setVgap(3);
    JPanel panel2 = new JPanel(layout2);
    panel2.add(phaseCodingPanel, BorderLayout.NORTH);
    panel2.add(polarizationPanel, BorderLayout.CENTER);
    panel2.add(pulseWidthUsPanel, BorderLayout.SOUTH);

    BorderLayout combinedLayout = new BorderLayout();
    combinedLayout.setVgap(3);
    JPanel combinedPanel = new JPanel(combinedLayout);
    combinedPanel.add(panel1, BorderLayout.NORTH);
    combinedPanel.add(panel2, BorderLayout.CENTER);

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
    
    _wavelengthCmValue.setText
      (NFormat.f3.format(_status.wavelengthCm));
    
    _prfModeValue.setText(_status.prfMode);
    
    _prfValue.setText(NFormat.f2_2.format(_status.prf));
    
    _phaseCodingValue.setText(_status.phaseCoding);
    
    _polarizationValue.setText(_status.polarization);

    _pulseWidthUsValue.setText
      (NFormat.f2.format(_status.pulseWidthUs));

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

