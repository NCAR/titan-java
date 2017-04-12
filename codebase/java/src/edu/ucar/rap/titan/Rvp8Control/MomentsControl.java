///////////////////////////////////////////////////////////////////////
//
// MomentsControl
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

public class MomentsControl extends JPanel

{

  private Parameters _params = Parameters.getInstance();

  private JCheckBox _indexedBeamsCheckBox;
  private JTextField _nSamplesTextField;
  private JComboBox _majorModeComboBox;
  private JComboBox _fftWindowComboBox;
  private JComboBox _clutFilterComboBox;
  private JComboBox _rangeSmoothingComboBox;
  private JComboBox _speckleFilterComboBox;

  private IndexedBeamsEditListener _indexedBeamsEditListener;
  private NSamplesEditListener _nSamplesEditListener;
  private MajorModeEditListener _majorModeEditListener;
  private FftWindowEditListener _fftWindowEditListener;
  private ClutFilterEditListener _clutFilterEditListener;
  private RangeSmoothingEditListener _rangeSmoothingEditListener;
  private SpeckleFilterEditListener _speckleFilterEditListener;

  public MomentsControl(JFrame frame) {
    
    setLayout(new BorderLayout());
    setBorder(CustomBorder.createTop(frame, "Moments control", 3));
	
    // fonts
    
    Font defaultFont = getFont();
    Font midFont =
      defaultFont.deriveFont((float) (defaultFont.getSize() + 0));
    Font bigFont =
      defaultFont.deriveFont((float) (defaultFont.getSize() + 2));
    
     // indexed beams?
    
    JLabel indexedBeamsLabel = new JLabel("Indexed beams?", JLabel.CENTER);
    indexedBeamsLabel.setFont(midFont);
    
    _indexedBeamsCheckBox = new JCheckBox();
    _indexedBeamsCheckBox.setSelected(_params.moments.indexedBeams.getValue());
    _indexedBeamsEditListener = new IndexedBeamsEditListener();
    _indexedBeamsCheckBox.addActionListener(_indexedBeamsEditListener);
	    
    BorderLayout indexedBeamsLayout = new BorderLayout();
    indexedBeamsLayout.setHgap(5);
    JPanel indexedBeamsPanel = new JPanel(indexedBeamsLayout);
    indexedBeamsPanel.add(indexedBeamsLabel, BorderLayout.WEST);
    indexedBeamsPanel.add(_indexedBeamsCheckBox, BorderLayout.EAST);
	    
    // n samples entry panel
    
    JLabel nSamplesLabel = new JLabel("N samples", JLabel.CENTER);
    nSamplesLabel.setFont(midFont);

    String nSamplesStr = new
      String(_params.moments.nSamples.toString());
    _nSamplesTextField = new JTextField(nSamplesStr, 5);
    _nSamplesTextField.setToolTipText("Number of samples per beam");
    _nSamplesEditListener = new NSamplesEditListener();
    _nSamplesTextField.addActionListener(_nSamplesEditListener);
	    
    BorderLayout nSamplesLayout = new BorderLayout();
    nSamplesLayout.setHgap(5);
    JPanel nSamplesPanel = new JPanel(nSamplesLayout);
    nSamplesPanel.add(nSamplesLabel, BorderLayout.WEST);
    nSamplesPanel.add(_nSamplesTextField, BorderLayout.EAST);
	    
    // operations mode
    
    JLabel majorModeLabel = new JLabel("Major mode", JLabel.CENTER);
    majorModeLabel.setFont(midFont);
    
    String majorMode = new String(_params.moments.majorMode.getValue());
    String[] majorModes = _params.moments.majorMode.getOptions();
    _majorModeComboBox = new JComboBox(majorModes);
    for (int ii = 0; ii < _majorModeComboBox.getItemCount(); ii++) {
      if (majorMode.equals(_majorModeComboBox.getItemAt(ii))) {
        _majorModeComboBox.setSelectedIndex(ii);
      }
    }
    _majorModeComboBox.setToolTipText("Set RVP8 major mode");
    _majorModeEditListener = new MajorModeEditListener();
    _majorModeComboBox.addActionListener(_majorModeEditListener);
	    
    BorderLayout majorModeLayout = new BorderLayout();
    majorModeLayout.setHgap(5);
    JPanel majorModePanel = new JPanel(majorModeLayout);
    majorModePanel.add(majorModeLabel, BorderLayout.WEST);
    majorModePanel.add(_majorModeComboBox, BorderLayout.EAST);
	    
    // fft window
    
    JLabel fftWindowLabel = new JLabel("Fft window", JLabel.CENTER);
    fftWindowLabel.setFont(midFont);
    
    String fftWindow = new String(_params.moments.fftWindow.getValue());
    String[] fftWindows = _params.moments.fftWindow.getOptions();
    _fftWindowComboBox = new JComboBox(fftWindows);
    for (int ii = 0; ii < _fftWindowComboBox.getItemCount(); ii++) {
      if (fftWindow.equals(_fftWindowComboBox.getItemAt(ii))) {
        _fftWindowComboBox.setSelectedIndex(ii);
      }
    }
    _fftWindowComboBox.setToolTipText("For FFT mode");
    _fftWindowEditListener = new FftWindowEditListener();
    _fftWindowComboBox.addActionListener(_fftWindowEditListener);
	    
    BorderLayout fftWindowLayout = new BorderLayout();
    fftWindowLayout.setHgap(5);
    JPanel fftWindowPanel = new JPanel(fftWindowLayout);
    fftWindowPanel.add(fftWindowLabel, BorderLayout.WEST);
    fftWindowPanel.add(_fftWindowComboBox, BorderLayout.EAST);
	    
    // clutter filter
    
    JLabel clutFilterLabel = new JLabel("Clutter filter", JLabel.CENTER);
    clutFilterLabel.setFont(midFont);
    
    String clutFilter = new String(_params.moments.clutFilter.getValue());
    String[] clutFilters = _params.moments.clutFilter.getOptions();
    _clutFilterComboBox = new JComboBox(clutFilters);
    for (int ii = 0; ii < _clutFilterComboBox.getItemCount(); ii++) {
      if (clutFilter.equals(_clutFilterComboBox.getItemAt(ii))) {
        _clutFilterComboBox.setSelectedIndex(ii);
      }
    }
    _clutFilterComboBox.setToolTipText("Clutter filter");
    _clutFilterEditListener = new ClutFilterEditListener();
    _clutFilterComboBox.addActionListener(_clutFilterEditListener);
	    
    BorderLayout clutFilterLayout = new BorderLayout();
    clutFilterLayout.setHgap(5);
    JPanel clutFilterPanel = new JPanel(clutFilterLayout);
    clutFilterPanel.add(clutFilterLabel, BorderLayout.WEST);
    clutFilterPanel.add(_clutFilterComboBox, BorderLayout.EAST);
	    
    // rangeSmoothing
    
    JLabel rangeSmoothingLabel = new JLabel("RangeSmoothing", JLabel.CENTER);
    rangeSmoothingLabel.setFont(midFont);
    
    String rangeSmoothing = new String(_params.moments.rangeSmoothing.getValue());
    String[] rangeSmoothings = _params.moments.rangeSmoothing.getOptions();
    _rangeSmoothingComboBox = new JComboBox(rangeSmoothings);
    for (int ii = 0; ii < _rangeSmoothingComboBox.getItemCount(); ii++) {
      if (rangeSmoothing.equals(_rangeSmoothingComboBox.getItemAt(ii))) {
        _rangeSmoothingComboBox.setSelectedIndex(ii);
      }
    }
    _rangeSmoothingComboBox.setToolTipText("RVP8 range smoothing");
    _rangeSmoothingEditListener = new RangeSmoothingEditListener();
    _rangeSmoothingComboBox.addActionListener(_rangeSmoothingEditListener);
	    
    BorderLayout rangeSmoothingLayout = new BorderLayout();
    rangeSmoothingLayout.setHgap(5);
    JPanel rangeSmoothingPanel = new JPanel(rangeSmoothingLayout);
    rangeSmoothingPanel.add(rangeSmoothingLabel, BorderLayout.WEST);
    rangeSmoothingPanel.add(_rangeSmoothingComboBox, BorderLayout.EAST);
	    
     // speckle filter
    
    JLabel speckleFilterLabel = new JLabel("Speckle filter");
    speckleFilterLabel.setFont(midFont);
    
    String speckleFilter = new String(_params.moments.speckleFilter.getValue());
    String[] speckleFilters = _params.moments.speckleFilter.getOptions();
    _speckleFilterComboBox = new JComboBox(speckleFilters);
    for (int ii = 0; ii < _speckleFilterComboBox.getItemCount(); ii++) {
      if (speckleFilter.equals(_speckleFilterComboBox.getItemAt(ii))) {
        _speckleFilterComboBox.setSelectedIndex(ii);
      }
    }
    _speckleFilterComboBox.setToolTipText("Speckle filter");
    _speckleFilterEditListener = new SpeckleFilterEditListener();
    _speckleFilterComboBox.addActionListener(_speckleFilterEditListener);
	    
    BorderLayout speckleFilterLayout = new BorderLayout();
    speckleFilterLayout.setHgap(5);
    JPanel speckleFilterPanel = new JPanel(speckleFilterLayout);
    speckleFilterPanel.add(speckleFilterLabel, BorderLayout.WEST);
    speckleFilterPanel.add(_speckleFilterComboBox, BorderLayout.EAST);

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
    upperLayout.setVgap(5);
    JPanel upperPanel = new JPanel(upperLayout);
    upperPanel.add(indexedBeamsPanel, BorderLayout.NORTH);
    upperPanel.add(nSamplesPanel, BorderLayout.CENTER);
    
    BorderLayout middleLayout = new BorderLayout();
    middleLayout.setVgap(5);
    JPanel middlePanel = new JPanel(middleLayout);
    middlePanel.add(majorModePanel, BorderLayout.NORTH);
    middlePanel.add(fftWindowPanel, BorderLayout.CENTER);
    middlePanel.add(clutFilterPanel, BorderLayout.SOUTH);
    
    BorderLayout lowerLayout = new BorderLayout();
    lowerLayout.setVgap(3);
    JPanel lowerPanel = new JPanel(lowerLayout);
    lowerPanel.add(rangeSmoothingPanel, BorderLayout.NORTH);
    lowerPanel.add(speckleFilterPanel, BorderLayout.CENTER);
    lowerPanel.add(infoPanel, BorderLayout.SOUTH);
    
    BorderLayout comboLayout = new BorderLayout();
    comboLayout.setVgap(3);

    JPanel entryContainer = new JPanel(comboLayout);
    entryContainer.add(upperPanel, BorderLayout.NORTH);
    entryContainer.add(middlePanel, BorderLayout.CENTER);
    entryContainer.add(lowerPanel, BorderLayout.SOUTH);
	    
    // add to main container
    
    add(entryContainer, BorderLayout.NORTH);

    // add params change listener
    
    _params.moments.addChangeListener(new ParamsChangeListener());
    
  } // constructor

  //////////////////
  // edit actions

  // n samples edit action
  public class NSamplesEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      setNSamplesFromTextField();
      _params.moments.syncParamToView();
      _params.moments.callChangeListeners();
    }
  }
	
  // indexed beams edit action

  public class IndexedBeamsEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      _params.moments.indexedBeams.setValue(_indexedBeamsCheckBox.isSelected());
      _params.moments.syncParamToView();
      _params.moments.callChangeListeners();
    }
  }

  // major mode edit action

  public class MajorModeEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      String selected = (String) _majorModeComboBox.getSelectedItem();
      _params.moments.majorMode.setValue(selected);
      _params.moments.syncParamToView();
      _params.moments.callChangeListeners();
    }
  }

  // fft window edit action

  public class FftWindowEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      String selected = (String) _fftWindowComboBox.getSelectedItem();
      _params.moments.fftWindow.setValue(selected);
      _params.moments.syncParamToView();
      _params.moments.callChangeListeners();
    }
  }

  // major mode edit action

  public class ClutFilterEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      String selected = (String) _clutFilterComboBox.getSelectedItem();
      _params.moments.clutFilter.setValue(selected);
      _params.moments.syncParamToView();
      _params.moments.callChangeListeners();
    }
  }

  // rangeSmoothing edit action

  public class RangeSmoothingEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      String selected = (String) _rangeSmoothingComboBox.getSelectedItem();
      _params.moments.rangeSmoothing.setValue(selected);
      _params.moments.syncParamToView();
      _params.moments.callChangeListeners();
    }
  }

  // speckle filter edit action

  public class SpeckleFilterEditListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      String selected = (String) _speckleFilterComboBox.getSelectedItem();
      _params.moments.speckleFilter.setValue(selected);
      _params.moments.syncParamToView();
      _params.moments.callChangeListeners();
    }
  }

  // info listener

  private class InfoListener implements ActionListener {
    public InfoListener() {
    }
    public void actionPerformed(ActionEvent event) {
      _params.moments.getCollectionFrame().setVisible(true);
    }
  }

  //////////////////////////////////////////
  // Setting the members

  // set n samples from text field
  
  public void setNSamplesFromTextField() { 
    int nSamples = 1000;
    int minNSamples = _params.moments.nSamples.getMinValue();
    int maxNSamples = _params.moments.nSamples.getMaxValue();
    boolean error = false;
    try {
      nSamples = Integer.parseInt(_nSamplesTextField.getText());
    }
    catch (java.lang.NumberFormatException nfe) {
      error = true;
    }
    if (nSamples < minNSamples || nSamples > maxNSamples) {
      error = true;
    }
    if (error) {
      // show error dialog
      String errStr =
        "Illegal value for number of samples: " +
        _nSamplesTextField.getText() + "\n" +
        "Min n samples: " + minNSamples + "\n" +
        "Max n samples: " + maxNSamples + "\n";
      JOptionPane.showMessageDialog(_nSamplesTextField, errStr,
                                    "Illegal number of samples",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }
    _params.moments.nSamples.setValue(nSamples);
  }
  
  // set major mode from string

  private void _setMajorMode(String mode) {
    for (int ii = 0; ii < _majorModeComboBox.getItemCount(); ii++) {
      if (mode.equals(_majorModeComboBox.getItemAt(ii))) {
        _majorModeComboBox.setSelectedIndex(ii);
      }
    }
  }

  // set fft window from string

  private void _setFftWindow(String mode) {
    for (int ii = 0; ii < _fftWindowComboBox.getItemCount(); ii++) {
      if (mode.equals(_fftWindowComboBox.getItemAt(ii))) {
        _fftWindowComboBox.setSelectedIndex(ii);
      }
    }
  }

  // set major mode from string

  private void _setClutFilter(String mode) {
    for (int ii = 0; ii < _clutFilterComboBox.getItemCount(); ii++) {
      if (mode.equals(_clutFilterComboBox.getItemAt(ii))) {
        _clutFilterComboBox.setSelectedIndex(ii);
      }
    }
  }

  // set rangeSmoothing from string

  private void _setRangeSmoothing(String mode) {
    for (int ii = 0; ii < _rangeSmoothingComboBox.getItemCount(); ii++) {
      if (mode.equals(_rangeSmoothingComboBox.getItemAt(ii))) {
        _rangeSmoothingComboBox.setSelectedIndex(ii);
      }
    }
  }

  // set speckleFilter from string

  private void _setSpeckleFilter(String mode) {
    for (int ii = 0; ii < _speckleFilterComboBox.getItemCount(); ii++) {
      if (mode.equals(_speckleFilterComboBox.getItemAt(ii))) {
        _speckleFilterComboBox.setSelectedIndex(ii);
      }
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

    _nSamplesTextField.setText(_params.moments.nSamples.toString());
    
    boolean indexedBeams = _params.moments.indexedBeams.getValue();
    _indexedBeamsCheckBox.setSelected(indexedBeams);

    String majorMode = _params.moments.majorMode.getValue();
    _setMajorMode(majorMode);

    String fftWindow = _params.moments.fftWindow.getValue();
    _setFftWindow(fftWindow);

    String clutFilter = _params.moments.clutFilter.getValue();
    _setClutFilter(clutFilter);

    String rangeSmoothing = _params.moments.rangeSmoothing.getValue();
    _setRangeSmoothing(rangeSmoothing);

    String speckleFilter = _params.moments.speckleFilter.getValue();
    _setSpeckleFilter(speckleFilter);

  }

}

