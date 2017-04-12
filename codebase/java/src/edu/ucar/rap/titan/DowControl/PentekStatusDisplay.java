///////////////////////////////////////////////////////////////////////
//
// PentekStatusDisplay
//
// Mike Dixon
//
// May 2013
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.DowControl;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.lang.Math;
import edu.ucar.rap.jrp.*;

public class PentekStatusDisplay extends JPanel implements StatusDataListener

{

  private Parameters _params = Parameters.getInstance();
  
  private String _freqLabel;
  
  private DateTimeDisplay _latestTimePanel;
  private DateTimeDisplay _startTimePanel;
  
  private JLabel _nominalRfValue;

  private JLabel _pulseSeqNumValue;
  private JLabel _nMissingPulsesValue;

  private JLabel _fpgaTempValue;
  private JLabel _boardTempValue;
  
  private JLabel _g0PulseWidthValue;
  private JLabel _g0PowerMeanValue;
  private JLabel _g0PowerMaxValue;
  private JLabel _dutyCycleValue;

  private JLabel _g0SampleStartValue;
  private JLabel _g0SampleEndValue;

  private JLabel _afcIsTrackingValue;

  private JLabel _measuredRfValue;
  private JLabel _measuredIf1Value;
  private JLabel _measuredIf2Value;

  private StatusData _status;

  private ArrayList<JComponent> _listForFonts = null;
  
  public PentekStatusDisplay(JFrame frame,
                             String freqLabel) {
    
    _freqLabel = freqLabel;
    setLayout(new BorderLayout());
    setBorder(CustomBorder.createTop(frame, "Pentek " + _freqLabel, 3));

    // create list for font updates

    _listForFonts = new ArrayList<JComponent>();

    // latest time and start time

    if (_freqLabel.equals("high")) {
      _latestTimePanel =
        new DateTimeDisplay(frame, DateTimeDisplay.LATEST_HIGH);
      _startTimePanel =
        new DateTimeDisplay(frame, DateTimeDisplay.START_HIGH);
    } else {
      _latestTimePanel =
        new DateTimeDisplay(frame, DateTimeDisplay.LATEST_LOW);
      _startTimePanel =
        new DateTimeDisplay(frame, DateTimeDisplay.START_LOW);
    }

    // nominal frequency

    _nominalRfValue = createLabel("0000000");
    JPanel nominalRfPanel = createPanel("Nominal RF (Hz)",
                                        Color.yellow, Color.black,
                                        _nominalRfValue);
    
    // pulse seq num
    
    _pulseSeqNumValue = createLabel("000000000000");
    JPanel pulseSeqNumPanel = createPanel("Pulse seq num",
                                          Color.yellow, Color.black,
                                          _pulseSeqNumValue);
    
    // missing pulses
    
    _nMissingPulsesValue = createLabel("000000000000");
    JPanel nMissingPulsesPanel = createPanel("N missing pulses",
                                             Color.green, Color.black,
                                             _nMissingPulsesValue);
    
    // temperatures
    
    _fpgaTempValue = createLabel("000");
    JPanel fpgaTempPanel = createPanel("Pentek FPGA temp (C)",
                                       Color.yellow, Color.black,
                                       _fpgaTempValue);
    
    _boardTempValue = createLabel("000");
    JPanel boardTempPanel = createPanel("Pentek Board temp (C)",
                                        Color.yellow, Color.black,
                                        _boardTempValue);
    
    // g0 power
    
    _g0PulseWidthValue = createLabel("0000000");
    JPanel g0PulseWidthPanel = createPanel("Measured pulse width (us)",
                                           Color.yellow, Color.black,
                                           _g0PulseWidthValue);
    
    _g0PowerMeanValue = createLabel("0000000");
    JPanel g0PowerMeanPanel = createPanel("Burst power mean (dBm)",
                                          Color.yellow, Color.black,
                                          _g0PowerMeanValue);
    
    _g0PowerMaxValue = createLabel("0000000");
    JPanel g0PowerMaxPanel = createPanel("Burst power max (dBm)",
                                         Color.yellow, Color.black,
                                         _g0PowerMaxValue);
    
    _dutyCycleValue = createLabel("0000000");
    JPanel dutyCyclePanel = createPanel("Measured duty cycle (%)",
                                        Color.red, Color.black,
                                        _dutyCycleValue);
    
    _g0SampleStartValue = createLabel("FALSE");
    JPanel g0SampleStartPanel = createPanel("Burst sample start",
                                            Color.red, Color.black,
                                            _g0SampleStartValue);
    
    _g0SampleEndValue = createLabel("FALSE");
    JPanel g0SampleEndPanel = createPanel("Burst sample end",
                                          Color.red, Color.black,
                                          _g0SampleEndValue);
    
    _afcIsTrackingValue = createLabel("NO");
    JPanel afcIsTrackingPanel = createPanel("AFC is tracking burst",
                                            Color.red, Color.black,
                                            _afcIsTrackingValue);
    
    _measuredRfValue = createLabel("0000000");
    JPanel measuredRfPanel = createPanel("Measured RF (Hz)",
                                         Color.yellow, Color.black,
                                         _measuredRfValue);
    
    _measuredIf1Value = createLabel("0000000");
    JPanel measuredIf1Panel = createPanel("Measured IF1 (Hz)",
                                          Color.yellow, Color.black,
                                          _measuredIf1Value);
    
    _measuredIf2Value = createLabel("0000000");
    JPanel measuredIf2Panel = createPanel("Measured IF2 (Hz)",
                                          Color.yellow, Color.black,
                                          _measuredIf2Value);
    
    setFont();
    
    // combine panels
    
    JPanel combined = addPanel(null, _latestTimePanel);
    combined = addPanel(combined, _startTimePanel);
    combined = addPanel(combined, nominalRfPanel);
    combined = addPanel(combined, pulseSeqNumPanel);
    combined = addPanel(combined, nMissingPulsesPanel);
    combined = addPanel(combined, fpgaTempPanel);
    combined = addPanel(combined, boardTempPanel);
    combined = addPanel(combined, g0PulseWidthPanel);
    combined = addPanel(combined, g0PowerMeanPanel);
    combined = addPanel(combined, g0PowerMaxPanel);
    combined = addPanel(combined, dutyCyclePanel);
    combined = addPanel(combined, g0SampleStartPanel);
    combined = addPanel(combined, g0SampleEndPanel);
    combined = addPanel(combined, afcIsTrackingPanel);
    combined = addPanel(combined, measuredRfPanel);
    combined = addPanel(combined, measuredIf1Panel);
    combined = addPanel(combined, measuredIf2Panel);
    
    add(combined, BorderLayout.NORTH);

    // add params change listener
    
    _params.status.addChangeListener(new ParamsChangeListener());
    
    // add listener for new status data
    
    StatusDataHandler.getInstance().addListener(this);
	
  } // constructor
	
  // handle new status
    
  public void handleStatus(StatusData status) {
    _status = status;
    redraw();
  }
  
  // update the display
    
  public void redraw() {

    if (_status == null) {
      return;
    }

    StatusData.PentekData data = _status.pentekHigh;
    if (_freqLabel.equals("low")) {
      data = _status.pentekLow;
    }
    
    _latestTimePanel.redraw();
    _startTimePanel.redraw();

    _nominalRfValue.setText(NFormat.e4.format(data.nominal_rf_hz));

    _pulseSeqNumValue.setText(Long.toString(data.pulse_seq_num));
    
    long nMissing = data.pulse_seq_num - data.pulse_count;
    _nMissingPulsesValue.setText(Long.toString(nMissing));
    if (nMissing > 0) {
      _nMissingPulsesValue.setForeground(Color.red);
    } else {
      _nMissingPulsesValue.setForeground(Color.green);
    }

    _fpgaTempValue.setText(NFormat.f0.format(data.fpga_temp_C));
    if (data.fpga_temp_C > _params.status.maxFpgaTemp.getValue()) {
      _fpgaTempValue.setForeground(Color.red);
    } else {
      _fpgaTempValue.setForeground(Color.green);
    }

    _boardTempValue.setText(NFormat.f0.format(data.circuit_board_temp_C));
    if (data.circuit_board_temp_C > _params.status.maxBoardTemp.getValue()) {
      _boardTempValue.setForeground(Color.red);
    } else {
      _boardTempValue.setForeground(Color.green);
    }

    if (data.g0_pulse_width_sec > 0) {
      _g0PulseWidthValue.setText
        (NFormat.f2.format(data.g0_pulse_width_sec * 1.0e6));
      _g0PulseWidthValue.setForeground(Color.green);
    } else {
      _g0PulseWidthValue.setText("----");
      _g0PulseWidthValue.setForeground(Color.red);
    }

    _g0PowerMeanValue.setText(NFormat.f2.format(data.g0_power_mean_dbm));
    if (data.g0_power_mean_dbm < _params.status.minValidBurstPower.getValue()) {
      _g0PowerMeanValue.setForeground(Color.red);
    } else {
      _g0PowerMeanValue.setForeground(Color.green);
    }

    _g0PowerMaxValue.setText(NFormat.f2.format(data.g0_power_max_dbm));
    if (data.g0_power_max_dbm < _params.status.minValidBurstPower.getValue()) {
      _g0PowerMaxValue.setForeground(Color.red);
    } else {
      _g0PowerMaxValue.setForeground(Color.green);
    }

    double dutyCyclePercent = data.duty_cycle * 100.0;
    _dutyCycleValue.setText(NFormat.f2.format(dutyCyclePercent));
    if (dutyCyclePercent > _params.status.maxDutyCycle.getValue()) {
      _dutyCycleValue.setForeground(Color.red);
    } else {
      _dutyCycleValue.setForeground(Color.green);
    }

    if (data.g0_start_missing == true) {
      _g0SampleStartValue.setForeground(Color.red);
      _g0SampleStartValue.setText("MISSING");
    } else {
      _g0SampleStartValue.setForeground(Color.green);
      _g0SampleStartValue.setText("OK");
    }

    if (data.g0_end_missing == true) {
      _g0SampleEndValue.setForeground(Color.red);
      _g0SampleEndValue.setText("MISSING");
    } else {
      _g0SampleEndValue.setForeground(Color.green);
      _g0SampleEndValue.setText("OK");
    }

    if (data.afc_is_tracking == true) {
      _afcIsTrackingValue.setForeground(Color.green);
      _afcIsTrackingValue.setText("YES");
    } else {
      _afcIsTrackingValue.setForeground(Color.red);
      _afcIsTrackingValue.setText("NO");
    }

    _measuredRfValue.setText(NFormat.e4.format(data.afc_est_rf_hz));
    _measuredIf1Value.setText(NFormat.e4.format(data.afc_est_if1_hz));
    _measuredIf2Value.setText(NFormat.e4.format(data.g0_if2_freq_hz));

    if (Math.abs(data.g0_if2_freq_hz - _params.status.nominalIf2FreqHz.getValue()) >
        _params.status.maxFreqError.getValue()) {
      _measuredRfValue.setForeground(Color.red);
      _measuredIf1Value.setForeground(Color.red);
      _measuredIf2Value.setForeground(Color.red);
    } else {
      _measuredRfValue.setForeground(Color.green);
      _measuredIf1Value.setForeground(Color.green);
      _measuredIf2Value.setForeground(Color.green);
    }
    
  }
    
  // Listener for changes in parameters

  private class ParamsChangeListener implements CollectionChangeListener {
    public void reactToChange() {
      setFont();
    }
  }
  
  // add to label list
  
  public void addToFontList(JComponent comp)
  {
    _listForFonts.add(comp);
  }
  
  // create label and add to list
  
  public JLabel createLabel(String text)
  {
    JLabel label = new JLabel(text);
    _listForFonts.add(label);
    return label;
  }
  
  // create panel and add to list
  
  public JPanel createPanel(String text,
                            Color foreground, Color background,
                            JLabel value)
  {
    
    JLabel label = new JLabel(text, JLabel.CENTER);
    addToFontList(label);
    value.setForeground(foreground);
    JPanel valuePanel = new JPanel();
    valuePanel.setBackground(background);
    valuePanel.add(value);
    
    BorderLayout layout = new BorderLayout();
    layout.setHgap(5);
    JPanel panel = new JPanel(layout);
    panel.add(label, BorderLayout.WEST);
    panel.add(valuePanel, BorderLayout.EAST);

    return panel;

  }
  
  // take an existing panel
  // create a new panel, add an element to it and return it
  // new elements are added at the bottom
    
  public JPanel addPanel(JPanel parent,
                         JPanel child)
    
  {
    
    BorderLayout layout = new BorderLayout();
    layout.setVgap(3);
    JPanel grandParent = new JPanel(layout);
    if (parent != null) {
      grandParent.add(parent, BorderLayout.NORTH);
    }
    grandParent.add(child, BorderLayout.SOUTH);

    return grandParent;

  }
    
  // set the font on labels in the list
    
  public void setFont() {

    if (getFont() == null) {
      return;
    }

    Font font =
      getFont().deriveFont(Font.BOLD,
                           _params.status.fontSize.getValue());
    
    for (Iterator ii = _listForFonts.iterator(); ii.hasNext();) {
      JComponent comp = (JComponent) ii.next();
      comp.setFont(font);
    }

  }
    
}

