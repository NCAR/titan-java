///////////////////////////////////////////////////////////////////////
//
// ReceiveStatusDisplay
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

public class ReceiveStatusDisplay extends JPanel implements StatusDataListener

{

  private Parameters _params = Parameters.getInstance();
  
  private JLabel _nGatesValue;
  private JLabel _gateSpacingValue;
  private JLabel _startRangeValue;
  private JLabel _maxRangeValue;
  private JLabel _rangeMaskResValue;
  private JLabel _dbzCal1kmValue;
  private JLabel _noiseChan0Value;
  private JLabel _noiseChan1Value;
  
  private StatusData _status;

  public ReceiveStatusDisplay(JFrame frame) {
	
    setLayout(new BorderLayout());
    setBorder(CustomBorder.createTop(frame, "Receive status", 3));
	
    // n gates
    
    JLabel nGatesLabel = new JLabel("N gates", JLabel.CENTER);
    String nGatesStr = new String("-8888");
    _nGatesValue = new JLabel(nGatesStr);
    _nGatesValue.setForeground(Color.yellow);
    JPanel nGatesValuePanel = new JPanel();
    nGatesValuePanel.setBackground(Color.black);
    nGatesValuePanel.add(_nGatesValue);
    
    BorderLayout nGatesLayout = new BorderLayout();
    nGatesLayout.setHgap(5);
    JPanel nGatesPanel = new JPanel(nGatesLayout);
    nGatesPanel.add(nGatesLabel, BorderLayout.WEST);
    nGatesPanel.add(nGatesValuePanel, BorderLayout.EAST);
	    
    // gate spacing
    
    JLabel gateSpacingLabel = new JLabel("Gate spacing (m)", JLabel.CENTER);
    String gateSpacingStr = new String("-8888");
    _gateSpacingValue = new JLabel(gateSpacingStr);
    _gateSpacingValue.setForeground(Color.yellow);
    JPanel gateSpacingValuePanel = new JPanel();
    gateSpacingValuePanel.setBackground(Color.black);
    gateSpacingValuePanel.add(_gateSpacingValue);
	    
    BorderLayout gateSpacingLayout = new BorderLayout();
    gateSpacingLayout.setHgap(5);
    JPanel gateSpacingPanel = new JPanel(gateSpacingLayout);
    gateSpacingPanel.add(gateSpacingLabel, BorderLayout.WEST);
    gateSpacingPanel.add(gateSpacingValuePanel, BorderLayout.EAST);

    // maxRange
    
    JLabel maxRangeLabel = new JLabel("Max range (km)", JLabel.CENTER);
    String maxRangeStr = new String("-8888");
    _maxRangeValue = new JLabel(maxRangeStr);
    _maxRangeValue.setForeground(Color.yellow);
    JPanel maxRangeValuePanel = new JPanel();
    maxRangeValuePanel.setBackground(Color.black);
    maxRangeValuePanel.add(_maxRangeValue);
	    
    BorderLayout maxRangeLayout = new BorderLayout();
    maxRangeLayout.setHgap(5);
    JPanel maxRangePanel = new JPanel(maxRangeLayout);
    maxRangePanel.add(maxRangeLabel, BorderLayout.WEST);
    maxRangePanel.add(maxRangeValuePanel, BorderLayout.EAST);

    // rangeMaskRes
    
    JLabel rangeMaskResLabel = new JLabel("Range Mask Res (m)", JLabel.CENTER);
    String rangeMaskResStr = new String("-8888");
    _rangeMaskResValue = new JLabel(rangeMaskResStr);
    _rangeMaskResValue.setForeground(Color.yellow);
    JPanel rangeMaskResValuePanel = new JPanel();
    rangeMaskResValuePanel.setBackground(Color.black);
    rangeMaskResValuePanel.add(_rangeMaskResValue);
	    
    BorderLayout rangeMaskResLayout = new BorderLayout();
    rangeMaskResLayout.setHgap(5);
    JPanel rangeMaskResPanel = new JPanel(rangeMaskResLayout);
    rangeMaskResPanel.add(rangeMaskResLabel, BorderLayout.WEST);
    rangeMaskResPanel.add(rangeMaskResValuePanel, BorderLayout.EAST);

    // startRange
    
    JLabel startRangeLabel = new JLabel("Start range (km)", JLabel.CENTER);
    String startRangeStr = new String("-8888");
    _startRangeValue = new JLabel(startRangeStr);
    _startRangeValue.setForeground(Color.yellow);
    JPanel startRangeValuePanel = new JPanel();
    startRangeValuePanel.setBackground(Color.black);
    startRangeValuePanel.add(_startRangeValue);
    
    BorderLayout startRangeLayout = new BorderLayout();
    startRangeLayout.setHgap(5);
    JPanel startRangePanel = new JPanel(startRangeLayout);
    startRangePanel.add(startRangeLabel, BorderLayout.WEST);
    startRangePanel.add(startRangeValuePanel, BorderLayout.EAST);

    // dbzCal1km
    
    JLabel dbzCal1kmLabel = new JLabel("DBZ cal 1km (dBZ)", JLabel.CENTER);
    dbzCal1kmLabel.setToolTipText("dBZ at 1km for SNR=0");
    String dbzCal1kmStr = new String("-88.88");
    _dbzCal1kmValue = new JLabel(dbzCal1kmStr);
    _dbzCal1kmValue.setForeground(Color.yellow);
    JPanel dbzCal1kmValuePanel = new JPanel();
    dbzCal1kmValuePanel.setBackground(Color.black);
    dbzCal1kmValuePanel.add(_dbzCal1kmValue);
	    
    BorderLayout dbzCal1kmLayout = new BorderLayout();
    dbzCal1kmLayout.setHgap(5);
    JPanel dbzCal1kmPanel = new JPanel(dbzCal1kmLayout);
    dbzCal1kmPanel.add(dbzCal1kmLabel, BorderLayout.WEST);
    dbzCal1kmPanel.add(dbzCal1kmValuePanel, BorderLayout.EAST);

    // noiseChan0
    
    JLabel noiseChan0Label = new JLabel("Noise Chan 0 (dBm)", JLabel.CENTER);
    String noiseChan0Str = new String("-88.88");
    _noiseChan0Value = new JLabel(noiseChan0Str);
    _noiseChan0Value.setForeground(Color.yellow);
    JPanel noiseChan0ValuePanel = new JPanel();
    noiseChan0ValuePanel.setBackground(Color.black);
    noiseChan0ValuePanel.add(_noiseChan0Value);
	    
    BorderLayout noiseChan0Layout = new BorderLayout();
    noiseChan0Layout.setHgap(5);
    JPanel noiseChan0Panel = new JPanel(noiseChan0Layout);
    noiseChan0Panel.add(noiseChan0Label, BorderLayout.WEST);
    noiseChan0Panel.add(noiseChan0ValuePanel, BorderLayout.EAST);

    // noiseChan1
    
    JLabel noiseChan1Label = new JLabel("Noise Chan 1 (dBm)", JLabel.CENTER);
    String noiseChan1Str = new String("-88.88");
    _noiseChan1Value = new JLabel(noiseChan1Str);
    _noiseChan1Value.setForeground(Color.yellow);
    JPanel noiseChan1ValuePanel = new JPanel();
    noiseChan1ValuePanel.setBackground(Color.black);
    noiseChan1ValuePanel.add(_noiseChan1Value);
	    
    BorderLayout noiseChan1Layout = new BorderLayout();
    noiseChan1Layout.setHgap(5);
    JPanel noiseChan1Panel = new JPanel(noiseChan1Layout);
    noiseChan1Panel.add(noiseChan1Label, BorderLayout.WEST);
    noiseChan1Panel.add(noiseChan1ValuePanel, BorderLayout.EAST);

    // combine panels
	    
    BorderLayout layout1 = new BorderLayout();
    layout1.setVgap(3);
    JPanel panel1 = new JPanel(layout1);
    panel1.add(nGatesPanel, BorderLayout.NORTH);
    panel1.add(gateSpacingPanel, BorderLayout.CENTER);
    panel1.add(maxRangePanel, BorderLayout.SOUTH);

    BorderLayout layout2 = new BorderLayout();
    layout2.setVgap(3);
    JPanel panel2 = new JPanel(layout2);
    panel2.add(startRangePanel, BorderLayout.NORTH);
    panel2.add(rangeMaskResPanel, BorderLayout.CENTER);
    panel2.add(dbzCal1kmPanel, BorderLayout.SOUTH);
	    
	    
    BorderLayout layout3 = new BorderLayout();
    layout3.setVgap(3);
    JPanel panel3 = new JPanel(layout3);
    panel3.add(noiseChan0Panel, BorderLayout.NORTH);
    panel3.add(noiseChan1Panel, BorderLayout.CENTER);
    
    // add to main container
    
    BorderLayout mergedLayout = new BorderLayout();
    mergedLayout.setVgap(3);
    JPanel merged = new JPanel(mergedLayout);
    merged.add(panel1, BorderLayout.NORTH);
    merged.add(panel2, BorderLayout.CENTER);
    merged.add(panel3, BorderLayout.SOUTH);

    add(merged, BorderLayout.NORTH);

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
    
    if (_status.nGates > 999) {
      _nGatesValue.setText(NFormat.i4.format(_status.nGates));
    } else {
      _nGatesValue.setText(NFormat.i3.format(_status.nGates));
    }

    _gateSpacingValue.setText
      (NFormat.f2.format(_status.gateSpacingKm * 1000.0));
    
    _rangeMaskResValue.setText
      (NFormat.f2.format(_status.rangeMaskResKm * 1000));
    
    _startRangeValue.setText
      (NFormat.f3.format(_status.startRangeKm));
    
    _maxRangeValue.setText
      (NFormat.f2.format(_status.maxRangeKm));
    
    _dbzCal1kmValue.setText
      (NFormat.f2.format(_status.dbzCal1km));

    _noiseChan0Value.setText
      (NFormat.f2.format(_status.noiseChan0));

    _noiseChan1Value.setText
      (NFormat.f2.format(_status.noiseChan1));

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

