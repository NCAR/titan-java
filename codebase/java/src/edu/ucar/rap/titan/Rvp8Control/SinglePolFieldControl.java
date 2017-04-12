///////////////////////////////////////////////////////////////////////
//
// SinglePolFieldControl
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

public class SinglePolFieldControl extends JPanel

{
  
  private Parameters _params = Parameters.getInstance();
  
  private JCheckBox _dbzCheckBox;
  private JCheckBox _velCheckBox;
  private JCheckBox _widthCheckBox;
  private JCheckBox _dbtCheckBox;
  private JCheckBox _dbzcCheckBox;
  private JCheckBox _velcCheckBox;
  private JCheckBox _flagsCheckBox;
  private JCheckBox _sqiCheckBox;

  private DbzEditListener _dbzEditListener;
  private VelEditListener _velEditListener;
  private WidthEditListener _widthEditListener;
  private DbtEditListener _dbtEditListener;
  private DbzcEditListener _dbzcEditListener;
  private VelcEditListener _velcEditListener;
  private FlagsEditListener _flagsEditListener;
  private SqiEditListener _sqiEditListener;

  public SinglePolFieldControl(JFrame frame) {
    
    setLayout(new BorderLayout());
    setBorder(CustomBorder.createTop(frame, "Single pol fields", 3));
	
    // fonts
    
    Font defaultFont = getFont();
    Font midFont =
      defaultFont.deriveFont((float) (defaultFont.getSize() + 0));
    Font bigFont =
      defaultFont.deriveFont((float) (defaultFont.getSize() + 2));
    
    // dbz
    
    JLabel dbzLabel = new JLabel("dbz          ", JLabel.CENTER);
    dbzLabel.setFont(midFont);
    
    _dbzCheckBox = new JCheckBox();
    _dbzCheckBox.setSelected(_params.singlePolFields.dbz.getValue());
    _dbzEditListener = new DbzEditListener();
    _dbzCheckBox.addActionListener(_dbzEditListener);
	    
    BorderLayout dbzLayout = new BorderLayout();
    dbzLayout.setHgap(5);
    JPanel dbzPanel = new JPanel(dbzLayout);
    dbzPanel.add(dbzLabel, BorderLayout.WEST);
    dbzPanel.add(_dbzCheckBox, BorderLayout.EAST);
	    
     // vel
    
    JLabel velLabel = new JLabel("vel", JLabel.CENTER);
    velLabel.setFont(midFont);
    
    _velCheckBox = new JCheckBox();
    _velCheckBox.setSelected(_params.singlePolFields.vel.getValue());
    _velEditListener = new VelEditListener();
    _velCheckBox.addActionListener(_velEditListener);
	    
    BorderLayout velLayout = new BorderLayout();
    velLayout.setHgap(5);
    JPanel velPanel = new JPanel(velLayout);
    velPanel.add(velLabel, BorderLayout.WEST);
    velPanel.add(_velCheckBox, BorderLayout.EAST);

    // width
    
    JLabel widthLabel = new JLabel("width", JLabel.CENTER);
    widthLabel.setFont(midFont);
    
    _widthCheckBox = new JCheckBox();
    _widthCheckBox.setSelected(_params.singlePolFields.width.getValue());
    _widthEditListener = new WidthEditListener();
    _widthCheckBox.addActionListener(_widthEditListener);
	    
    BorderLayout widthLayout = new BorderLayout();
    widthLayout.setHgap(5);
    JPanel widthPanel = new JPanel(widthLayout);
    widthPanel.add(widthLabel, BorderLayout.WEST);
    widthPanel.add(_widthCheckBox, BorderLayout.EAST);
	    
    // dbt
    
    JLabel dbtLabel = new JLabel("dbt       ", JLabel.CENTER);
    dbtLabel.setFont(midFont);
    
    _dbtCheckBox = new JCheckBox();
    _dbtCheckBox.setSelected(_params.singlePolFields.dbt.getValue());
    _dbtEditListener = new DbtEditListener();
    _dbtCheckBox.addActionListener(_dbtEditListener);
	    
    BorderLayout dbtLayout = new BorderLayout();
    dbtLayout.setHgap(5);
    JPanel dbtPanel = new JPanel(dbtLayout);
    dbtPanel.add(dbtLabel, BorderLayout.WEST);
    dbtPanel.add(_dbtCheckBox, BorderLayout.EAST);
	    
    // dbzc
    
    JLabel dbzcLabel = new JLabel("dbzc", JLabel.CENTER);
    dbzcLabel.setFont(midFont);
    
    _dbzcCheckBox = new JCheckBox();
    _dbzcCheckBox.setSelected(_params.singlePolFields.dbzc.getValue());
    _dbzcEditListener = new DbzcEditListener();
    _dbzcCheckBox.addActionListener(_dbzcEditListener);
	    
    BorderLayout dbzcLayout = new BorderLayout();
    dbzcLayout.setHgap(5);
    JPanel dbzcPanel = new JPanel(dbzcLayout);
    dbzcPanel.add(dbzcLabel, BorderLayout.WEST);
    dbzcPanel.add(_dbzcCheckBox, BorderLayout.EAST);
	    
    // velc
    
    JLabel velcLabel = new JLabel("velc", JLabel.CENTER);
    velcLabel.setFont(midFont);
    
    _velcCheckBox = new JCheckBox();
    _velcCheckBox.setSelected(_params.singlePolFields.velc.getValue());
    _velcEditListener = new VelcEditListener();
    _velcCheckBox.addActionListener(_velcEditListener);
	    
    BorderLayout velcLayout = new BorderLayout();
    velcLayout.setHgap(5);
    JPanel velcPanel = new JPanel(velcLayout);
    velcPanel.add(velcLabel, BorderLayout.WEST);
    velcPanel.add(_velcCheckBox, BorderLayout.EAST);
	    
    // flags
    
    JLabel flagsLabel = new JLabel("flags", JLabel.CENTER);
    flagsLabel.setFont(midFont);
    
    _flagsCheckBox = new JCheckBox();
    _flagsCheckBox.setSelected(_params.singlePolFields.flags.getValue());
    _flagsEditListener = new FlagsEditListener();
    _flagsCheckBox.addActionListener(_flagsEditListener);
	    
    BorderLayout flagsLayout = new BorderLayout();
    flagsLayout.setHgap(5);
    JPanel flagsPanel = new JPanel(flagsLayout);
    flagsPanel.add(flagsLabel, BorderLayout.WEST);
    flagsPanel.add(_flagsCheckBox, BorderLayout.EAST);
	    
    // sqi
    
    JLabel sqiLabel = new JLabel("sqi", JLabel.CENTER);
    sqiLabel.setFont(midFont);
    
    _sqiCheckBox = new JCheckBox();
    _sqiCheckBox.setSelected(_params.singlePolFields.sqi.getValue());
    _sqiEditListener = new SqiEditListener();
    _sqiCheckBox.addActionListener(_sqiEditListener);
	    
    BorderLayout sqiLayout = new BorderLayout();
    sqiLayout.setHgap(5);
    JPanel sqiPanel = new JPanel(sqiLayout);
    sqiPanel.add(sqiLabel, BorderLayout.WEST);
    sqiPanel.add(_sqiCheckBox, BorderLayout.EAST);
	    
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
    
    BorderLayout upperLayout = new BorderLayout();
    upperLayout.setVgap(3);
    JPanel upperPanel = new JPanel(upperLayout);
    upperPanel.add(dbzPanel, BorderLayout.NORTH);
    upperPanel.add(velPanel, BorderLayout.CENTER);
    upperPanel.add(widthPanel, BorderLayout.SOUTH);
    
    BorderLayout middleLayout = new BorderLayout();
    middleLayout.setVgap(3);
    JPanel middlePanel = new JPanel(middleLayout);
    middlePanel.add(dbtPanel, BorderLayout.NORTH);
    middlePanel.add(dbzcPanel, BorderLayout.CENTER);
    middlePanel.add(velcPanel, BorderLayout.SOUTH);
    
    BorderLayout lowerLayout = new BorderLayout();
    lowerLayout.setVgap(3);
    JPanel lowerPanel = new JPanel(lowerLayout);
    lowerPanel.add(flagsPanel, BorderLayout.NORTH);
    lowerPanel.add(sqiPanel, BorderLayout.CENTER);
    // lowerPanel.add(infoPanel, BorderLayout.SOUTH);
    
    BorderLayout comboLayout = new BorderLayout();
    comboLayout.setVgap(3);
    
    JPanel entryContainer = new JPanel(comboLayout);
    entryContainer.add(upperPanel, BorderLayout.NORTH);
    entryContainer.add(middlePanel, BorderLayout.CENTER);
    entryContainer.add(lowerPanel, BorderLayout.SOUTH);
	    
    // add to main container
    
    add(entryContainer, BorderLayout.NORTH);

    // add params change listener
    
    _params.singlePolFields.addChangeListener(new ParamsChangeListener());
    
  } // constructor

  //////////////////
  // edit actions

  public class DbzEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      _params.singlePolFields.dbz.setValue(_dbzCheckBox.isSelected());
      _params.singlePolFields.syncParamToView();
      _params.singlePolFields.callChangeListeners();
    }
  }

  public class VelEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      _params.singlePolFields.vel.setValue(_velCheckBox.isSelected());
      _params.singlePolFields.syncParamToView();
      _params.singlePolFields.callChangeListeners();
    }
  }

  public class WidthEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      _params.singlePolFields.width.setValue(_widthCheckBox.isSelected());
      _params.singlePolFields.syncParamToView();
      _params.singlePolFields.callChangeListeners();
    }
  }

  public class DbtEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      _params.singlePolFields.dbt.setValue(_dbtCheckBox.isSelected());
      _params.singlePolFields.syncParamToView();
      _params.singlePolFields.callChangeListeners();
    }
  }

  public class DbzcEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      _params.singlePolFields.dbzc.setValue(_dbzcCheckBox.isSelected());
      _params.singlePolFields.syncParamToView();
      _params.singlePolFields.callChangeListeners();
    }
  }

  public class VelcEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      _params.singlePolFields.velc.setValue(_velcCheckBox.isSelected());
      _params.singlePolFields.syncParamToView();
      _params.singlePolFields.callChangeListeners();
    }
  }

  public class FlagsEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      _params.singlePolFields.flags.setValue(_flagsCheckBox.isSelected());
      _params.singlePolFields.syncParamToView();
      _params.singlePolFields.callChangeListeners();
    }
  }

  public class SqiEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      _params.singlePolFields.sqi.setValue(_sqiCheckBox.isSelected());
      _params.singlePolFields.syncParamToView();
      _params.singlePolFields.callChangeListeners();
    }
  }

  // info listener

  private class InfoListener implements ActionListener {
    public InfoListener() {
    }
    public void actionPerformed(ActionEvent event) {
      _params.singlePolFields.getCollectionFrame().setVisible(true);
    }
  }

  /////////////////////////////////////
  // Listener for changes in parameters
  
  private class ParamsChangeListener implements CollectionChangeListener {
    public void reactToChange() {
      updateFromParams();
    }
  }
  
  protected void updateFromParams() {

    boolean dbz = _params.singlePolFields.dbz.getValue();
    _dbzCheckBox.setSelected(dbz);

    boolean vel = _params.singlePolFields.vel.getValue();
    _velCheckBox.setSelected(vel);

    boolean width = _params.singlePolFields.width.getValue();
    _widthCheckBox.setSelected(width);

    boolean dbt = _params.singlePolFields.dbt.getValue();
    _dbtCheckBox.setSelected(dbt);

    boolean dbzc = _params.singlePolFields.dbzc.getValue();
    _dbzcCheckBox.setSelected(dbzc);

    boolean velc = _params.singlePolFields.velc.getValue();
    _velcCheckBox.setSelected(velc);

    boolean flags = _params.singlePolFields.flags.getValue();
    _flagsCheckBox.setSelected(flags);

    boolean sqi = _params.singlePolFields.sqi.getValue();
    _sqiCheckBox.setSelected(sqi);

  }

}

