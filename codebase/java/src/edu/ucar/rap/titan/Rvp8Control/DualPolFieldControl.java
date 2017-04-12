///////////////////////////////////////////////////////////////////////
//
// DualPolFieldControl
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

public class DualPolFieldControl extends JPanel

{
  
  private Parameters _params = Parameters.getInstance();
  
  private JCheckBox _kdpCheckBox;
  private JCheckBox _ldrhCheckBox;
  private JCheckBox _ldrvCheckBox;
  private JCheckBox _phidpCheckBox;
  private JCheckBox _phihCheckBox;
  private JCheckBox _phivCheckBox;
  private JCheckBox _rhohCheckBox;
  private JCheckBox _rhohvCheckBox;
  private JCheckBox _rhovCheckBox;
  private JCheckBox _zdrCheckBox;

  private KdpEditListener _kdpEditListener;
  private LdrhEditListener _ldrhEditListener;
  private LdrvEditListener _ldrvEditListener;
  private PhidpEditListener _phidpEditListener;
  private PhihEditListener _phihEditListener;
  private PhivEditListener _phivEditListener;
  private RhohEditListener _rhohEditListener;
  private RhohvEditListener _rhohvEditListener;
  private RhovEditListener _rhovEditListener;
  private ZdrEditListener _zdrEditListener;

  public DualPolFieldControl(JFrame frame) {
    
    setLayout(new BorderLayout());
    setBorder(CustomBorder.createTop(frame, "Dual pol fields", 3));
	
    // fonts
    
    Font defaultFont = getFont();
    Font midFont =
      defaultFont.deriveFont((float) (defaultFont.getSize() + 0));
    Font bigFont =
      defaultFont.deriveFont((float) (defaultFont.getSize() + 2));
    
    // kdp
    
    JLabel kdpLabel = new JLabel("kdp", JLabel.CENTER);
    kdpLabel.setFont(midFont);
    
    _kdpCheckBox = new JCheckBox();
    _kdpCheckBox.setSelected(_params.dualPolFields.kdp.getValue());
    _kdpEditListener = new KdpEditListener();
    _kdpCheckBox.addActionListener(_kdpEditListener);
	    
    BorderLayout kdpLayout = new BorderLayout();
    kdpLayout.setHgap(5);
    JPanel kdpPanel = new JPanel(kdpLayout);
    kdpPanel.add(kdpLabel, BorderLayout.WEST);
    kdpPanel.add(_kdpCheckBox, BorderLayout.EAST);
	    
     // ldrh
    
    JLabel ldrhLabel = new JLabel("ldrh", JLabel.CENTER);
    ldrhLabel.setFont(midFont);
    
    _ldrhCheckBox = new JCheckBox();
    _ldrhCheckBox.setSelected(_params.dualPolFields.ldrh.getValue());
    _ldrhEditListener = new LdrhEditListener();
    _ldrhCheckBox.addActionListener(_ldrhEditListener);
	    
    BorderLayout ldrhLayout = new BorderLayout();
    ldrhLayout.setHgap(5);
    JPanel ldrhPanel = new JPanel(ldrhLayout);
    ldrhPanel.add(ldrhLabel, BorderLayout.WEST);
    ldrhPanel.add(_ldrhCheckBox, BorderLayout.EAST);

    // ldrv
    
    JLabel ldrvLabel = new JLabel("ldrv", JLabel.CENTER);
    ldrvLabel.setFont(midFont);
    
    _ldrvCheckBox = new JCheckBox();
    _ldrvCheckBox.setSelected(_params.dualPolFields.ldrv.getValue());
    _ldrvEditListener = new LdrvEditListener();
    _ldrvCheckBox.addActionListener(_ldrvEditListener);
	    
    BorderLayout ldrvLayout = new BorderLayout();
    ldrvLayout.setHgap(5);
    JPanel ldrvPanel = new JPanel(ldrvLayout);
    ldrvPanel.add(ldrvLabel, BorderLayout.WEST);
    ldrvPanel.add(_ldrvCheckBox, BorderLayout.EAST);
	    
    // phidp
    
    JLabel phidpLabel = new JLabel("phidp", JLabel.CENTER);
    phidpLabel.setFont(midFont);
    
    _phidpCheckBox = new JCheckBox();
    _phidpCheckBox.setSelected(_params.dualPolFields.phidp.getValue());
    _phidpEditListener = new PhidpEditListener();
    _phidpCheckBox.addActionListener(_phidpEditListener);
	    
    BorderLayout phidpLayout = new BorderLayout();
    phidpLayout.setHgap(5);
    JPanel phidpPanel = new JPanel(phidpLayout);
    phidpPanel.add(phidpLabel, BorderLayout.WEST);
    phidpPanel.add(_phidpCheckBox, BorderLayout.EAST);
	    
    // phih
    
    JLabel phihLabel = new JLabel("phih", JLabel.CENTER);
    phihLabel.setFont(midFont);
    
    _phihCheckBox = new JCheckBox();
    _phihCheckBox.setSelected(_params.dualPolFields.phih.getValue());
    _phihEditListener = new PhihEditListener();
    _phihCheckBox.addActionListener(_phihEditListener);
	    
    BorderLayout phihLayout = new BorderLayout();
    phihLayout.setHgap(5);
    JPanel phihPanel = new JPanel(phihLayout);
    phihPanel.add(phihLabel, BorderLayout.WEST);
    phihPanel.add(_phihCheckBox, BorderLayout.EAST);
	    
    // phiv
    
    JLabel phivLabel = new JLabel("phiv", JLabel.CENTER);
    phivLabel.setFont(midFont);
    
    _phivCheckBox = new JCheckBox();
    _phivCheckBox.setSelected(_params.dualPolFields.phiv.getValue());
    _phivEditListener = new PhivEditListener();
    _phivCheckBox.addActionListener(_phivEditListener);
	    
    BorderLayout phivLayout = new BorderLayout();
    phivLayout.setHgap(5);
    JPanel phivPanel = new JPanel(phivLayout);
    phivPanel.add(phivLabel, BorderLayout.WEST);
    phivPanel.add(_phivCheckBox, BorderLayout.EAST);
	    
    // rhoh
    
    JLabel rhohLabel = new JLabel("rhoh", JLabel.CENTER);
    rhohLabel.setFont(midFont);
    
    _rhohCheckBox = new JCheckBox();
    _rhohCheckBox.setSelected(_params.dualPolFields.rhoh.getValue());
    _rhohEditListener = new RhohEditListener();
    _rhohCheckBox.addActionListener(_rhohEditListener);
	    
    BorderLayout rhohLayout = new BorderLayout();
    rhohLayout.setHgap(5);
    JPanel rhohPanel = new JPanel(rhohLayout);
    rhohPanel.add(rhohLabel, BorderLayout.WEST);
    rhohPanel.add(_rhohCheckBox, BorderLayout.EAST);
	    
    // rhohv
    
    JLabel rhohvLabel = new JLabel("rhohv", JLabel.CENTER);
    rhohvLabel.setFont(midFont);
    
    _rhohvCheckBox = new JCheckBox();
    _rhohvCheckBox.setSelected(_params.dualPolFields.rhohv.getValue());
    _rhohvEditListener = new RhohvEditListener();
    _rhohvCheckBox.addActionListener(_rhohvEditListener);
	    
    BorderLayout rhohvLayout = new BorderLayout();
    rhohvLayout.setHgap(5);
    JPanel rhohvPanel = new JPanel(rhohvLayout);
    rhohvPanel.add(rhohvLabel, BorderLayout.WEST);
    rhohvPanel.add(_rhohvCheckBox, BorderLayout.EAST);
	    
    // rhov
    
    JLabel rhovLabel = new JLabel("rhov", JLabel.CENTER);
    rhovLabel.setFont(midFont);
    
    _rhovCheckBox = new JCheckBox();
    _rhovCheckBox.setSelected(_params.dualPolFields.rhov.getValue());
    _rhovEditListener = new RhovEditListener();
    _rhovCheckBox.addActionListener(_rhovEditListener);
	    
    BorderLayout rhovLayout = new BorderLayout();
    rhovLayout.setHgap(5);
    JPanel rhovPanel = new JPanel(rhovLayout);
    rhovPanel.add(rhovLabel, BorderLayout.WEST);
    rhovPanel.add(_rhovCheckBox, BorderLayout.EAST);
	    
    // zdr
    
    JLabel zdrLabel = new JLabel("zdr", JLabel.CENTER);
    zdrLabel.setFont(midFont);
    
    _zdrCheckBox = new JCheckBox();
    _zdrCheckBox.setSelected(_params.dualPolFields.zdr.getValue());
    _zdrEditListener = new ZdrEditListener();
    _zdrCheckBox.addActionListener(_zdrEditListener);
	    
    BorderLayout zdrLayout = new BorderLayout();
    zdrLayout.setHgap(5);
    JPanel zdrPanel = new JPanel(zdrLayout);
    zdrPanel.add(zdrLabel, BorderLayout.WEST);
    zdrPanel.add(_zdrCheckBox, BorderLayout.EAST);
	    
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

    ///////////////////////////////////////////////////
    // combine items into a single panel
    
    BorderLayout upper1Layout = new BorderLayout();
    upper1Layout.setVgap(3);
    JPanel upper1Panel = new JPanel(upper1Layout);
    upper1Panel.add(kdpPanel, BorderLayout.NORTH);
    upper1Panel.add(ldrhPanel, BorderLayout.CENTER);
    upper1Panel.add(ldrvPanel, BorderLayout.SOUTH);
    
    BorderLayout upper2Layout = new BorderLayout();
    upper2Layout.setVgap(3);
    JPanel upper2Panel = new JPanel(upper2Layout);
    upper2Panel.add(phidpPanel, BorderLayout.NORTH);
    upper2Panel.add(phihPanel, BorderLayout.CENTER);
    upper2Panel.add(phivPanel, BorderLayout.SOUTH);
    
    BorderLayout upperLayout = new BorderLayout();
    upperLayout.setVgap(3);
    
    JPanel upperContainer = new JPanel(upperLayout);
    upperContainer.add(upper1Panel, BorderLayout.NORTH);
    upperContainer.add(upper2Panel, BorderLayout.CENTER);
	    
    BorderLayout lower1Layout = new BorderLayout();
    lower1Layout.setVgap(3);
    JPanel lower1Panel = new JPanel(lower1Layout);
    lower1Panel.add(rhohPanel, BorderLayout.NORTH);
    lower1Panel.add(rhohvPanel, BorderLayout.CENTER);
    lower1Panel.add(rhovPanel, BorderLayout.SOUTH);
    
    BorderLayout lower2Layout = new BorderLayout();
    lower2Layout.setVgap(3);
    JPanel lower2Panel = new JPanel(lower2Layout);
    lower2Panel.add(zdrPanel, BorderLayout.NORTH);
    
    BorderLayout lowerLayout = new BorderLayout();
    lowerLayout.setVgap(3);
    
    JPanel lowerContainer = new JPanel(lowerLayout);
    lowerContainer.add(lower1Panel, BorderLayout.NORTH);
    lowerContainer.add(lower2Panel, BorderLayout.CENTER);
    // lowerContainer.add(infoPanel, BorderLayout.SOUTH);
	    
    BorderLayout comboLayout = new BorderLayout();
    comboLayout.setVgap(3);
    
    JPanel entryContainer = new JPanel(comboLayout);
    entryContainer.add(upperContainer, BorderLayout.NORTH);
    entryContainer.add(lowerContainer, BorderLayout.CENTER);
    entryContainer.add(Box.createHorizontalStrut(100),
                       BorderLayout.SOUTH);
	    
    // add to main container
    
    add(entryContainer, BorderLayout.NORTH);

    // add params change listener
    
    _params.dualPolFields.addChangeListener(new DualPolParamsChangeListener());
    _params.transmit.addChangeListener(new TransmitParamsChangeListener());

    // check polarization, set fields accordingly

    _checkPolarization();

  } // constructor

  ///////////////////////////////
  // set fields enabled/disabled

  public void enableFields(boolean state) {

    _kdpCheckBox.setEnabled(state);
    _ldrhCheckBox.setEnabled(state);
    _ldrvCheckBox.setEnabled(state);
    _phidpCheckBox.setEnabled(state);
    _phihCheckBox.setEnabled(state);
    _phivCheckBox.setEnabled(state);
    _rhohCheckBox.setEnabled(state);
    _rhohvCheckBox.setEnabled(state);
    _rhovCheckBox.setEnabled(state);
    _zdrCheckBox.setEnabled(state);

  }

  //////////////////
  // edit actions

  public class KdpEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      _params.dualPolFields.kdp.setValue(_kdpCheckBox.isSelected());
      _params.dualPolFields.syncParamToView();
      _params.dualPolFields.callChangeListeners();
    }
  }

  public class LdrhEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      _params.dualPolFields.ldrh.setValue(_ldrhCheckBox.isSelected());
      _params.dualPolFields.syncParamToView();
      _params.dualPolFields.callChangeListeners();
    }
  }

  public class LdrvEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      _params.dualPolFields.ldrv.setValue(_ldrvCheckBox.isSelected());
      _params.dualPolFields.syncParamToView();
      _params.dualPolFields.callChangeListeners();
    }
  }

  public class PhidpEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      _params.dualPolFields.phidp.setValue(_phidpCheckBox.isSelected());
      _params.dualPolFields.syncParamToView();
      _params.dualPolFields.callChangeListeners();
    }
  }

  public class PhihEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      _params.dualPolFields.phih.setValue(_phihCheckBox.isSelected());
      _params.dualPolFields.syncParamToView();
      _params.dualPolFields.callChangeListeners();
    }
  }

  public class PhivEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      _params.dualPolFields.phiv.setValue(_phivCheckBox.isSelected());
      _params.dualPolFields.syncParamToView();
      _params.dualPolFields.callChangeListeners();
    }
  }

  public class RhohEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      _params.dualPolFields.rhoh.setValue(_rhohCheckBox.isSelected());
      _params.dualPolFields.syncParamToView();
      _params.dualPolFields.callChangeListeners();
    }
  }

  public class RhohvEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      _params.dualPolFields.rhohv.setValue(_rhohvCheckBox.isSelected());
      _params.dualPolFields.syncParamToView();
      _params.dualPolFields.callChangeListeners();
    }
  }

  public class RhovEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      _params.dualPolFields.rhov.setValue(_rhovCheckBox.isSelected());
      _params.dualPolFields.syncParamToView();
      _params.dualPolFields.callChangeListeners();
    }
  }

  public class ZdrEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      _params.dualPolFields.zdr.setValue(_zdrCheckBox.isSelected());
      _params.dualPolFields.syncParamToView();
      _params.dualPolFields.callChangeListeners();
    }
  }

  // info listener

  private class InfoListener implements ActionListener {
    public InfoListener() {
    }
    public void actionPerformed(ActionEvent event) {
      _params.dualPolFields.getCollectionFrame().setVisible(true);
    }
  }

  /////////////////////////////////////
  // Listener for changes in parameters
  
  private class DualPolParamsChangeListener
    implements CollectionChangeListener {
    public void reactToChange() {
      updateFromParams();
    }
  }
  
  protected void updateFromParams() {

    boolean kdp = _params.dualPolFields.kdp.getValue();
    _kdpCheckBox.setSelected(kdp);

    boolean ldrh = _params.dualPolFields.ldrh.getValue();
    _ldrhCheckBox.setSelected(ldrh);

    boolean ldrv = _params.dualPolFields.ldrv.getValue();
    _ldrvCheckBox.setSelected(ldrv);

    boolean phidp = _params.dualPolFields.phidp.getValue();
    _phidpCheckBox.setSelected(phidp);

    boolean phih = _params.dualPolFields.phih.getValue();
    _phihCheckBox.setSelected(phih);

    boolean phiv = _params.dualPolFields.phiv.getValue();
    _phivCheckBox.setSelected(phiv);

    boolean rhoh = _params.dualPolFields.rhoh.getValue();
    _rhohCheckBox.setSelected(rhoh);

    boolean rhohv = _params.dualPolFields.rhohv.getValue();
    _rhohvCheckBox.setSelected(rhohv);

    boolean rhov = _params.dualPolFields.rhov.getValue();
    _rhovCheckBox.setSelected(rhov);

    boolean zdr = _params.dualPolFields.zdr.getValue();
    _zdrCheckBox.setSelected(zdr);

  }

  private class TransmitParamsChangeListener implements CollectionChangeListener {
    public void reactToChange() {
      _checkPolarization();
    }
  }
  
  // check polarization and enable fields accordingly
  
  private void _checkPolarization() {
    String mode = _params.transmit.polarization.getValue();
    if (mode.equals("horizontal") || mode.equals("vertical")) {
      enableFields(false);
    } else {
      enableFields(true);
    }
  }

}

