///////////////////////////////////////////////////////////////////////
//
// PentekStatusDisplay
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

public class PentekStatusDisplay extends JPanel implements StatusDataListener

{

  private Parameters _params = Parameters.getInstance();
  
  private String _label;
  
  private DateTimeDisplay _latestTimePanel;
  private DateTimeDisplay _startTimePanel;
  
  private JLabel _pulseSeqNumValue;
  private JLabel _nMissingPulsesValue;

  private JLabel _fpgaTempValue;
  private JLabel _boardTempValue;
  
  private JLabel _chan0FreqHzValue;
  private JLabel _chan1FreqHzValue;
  private JLabel _chan2FreqHzValue;

  private StatusData _status;

  private ArrayList<JComponent> _listForFonts = null;
  
  public PentekStatusDisplay(JFrame frame,
                             String label) {
    
    _label = label;
    setLayout(new BorderLayout());
    setBorder(CustomBorder.createTop(frame, "Pentek " + _label, 3));

    // create list for font updates

    _listForFonts = new ArrayList<JComponent>();

    // latest time and start time

    if (_label.equals("1")) {
      _latestTimePanel =
        new DateTimeDisplay(frame, DateTimeDisplay.LATEST_1);
      _startTimePanel =
        new DateTimeDisplay(frame, DateTimeDisplay.START_1);
    } else {
      _latestTimePanel =
        new DateTimeDisplay(frame, DateTimeDisplay.LATEST_2);
      _startTimePanel =
        new DateTimeDisplay(frame, DateTimeDisplay.START_2);
    }

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
    
    // channel frequencies

    _chan0FreqHzValue = createLabel("0000000");
    JPanel chan0FreqHzPanel = createPanel("Chan 0 Freq(Hz)",
                                          Color.yellow, Color.black,
                                          _chan0FreqHzValue);

    _chan1FreqHzValue = createLabel("0000000");
    JPanel chan1FreqHzPanel = createPanel("Chan 1 Freq(Hz)",
                                          Color.yellow, Color.black,
                                          _chan1FreqHzValue);

    _chan2FreqHzValue = createLabel("0000000");
    JPanel chan2FreqHzPanel = createPanel("Chan 2 Freq(Hz)",
                                          Color.yellow, Color.black,
                                          _chan2FreqHzValue);

    setFont();
    
    // combine panels
    
    JPanel combined = addPanel(null, _latestTimePanel);
    combined = addPanel(combined, _startTimePanel);
    combined = addPanel(combined, pulseSeqNumPanel);
    combined = addPanel(combined, nMissingPulsesPanel);
    combined = addPanel(combined, fpgaTempPanel);
    combined = addPanel(combined, boardTempPanel);
    combined = addPanel(combined, chan0FreqHzPanel);
    combined = addPanel(combined, chan1FreqHzPanel);
    combined = addPanel(combined, chan2FreqHzPanel);
    
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

    StatusData.PentekData data = _status.pentek1;
    if (_label.equals("2")) {
      data = _status.pentek2;
    }
    
    _latestTimePanel.redraw();
    _startTimePanel.redraw();

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

    _chan0FreqHzValue.setText(NFormat.e4.format(data.chan_0_freq_hz));
    _chan1FreqHzValue.setText(NFormat.e4.format(data.chan_1_freq_hz));
    _chan2FreqHzValue.setText(NFormat.e4.format(data.chan_2_freq_hz));

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

