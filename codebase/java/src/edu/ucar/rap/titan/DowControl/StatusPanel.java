///////////////////////////////////////////////////////////////////////
//
// StatusPanel
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

public class StatusPanel extends JFrame implements StatusDataListener

{

  private Parameters _params = Parameters.getInstance();

  private DateTimeDisplay _gpsTimeDisplay;
  private AntennaPosnDisplay _antennaPosnDisplay;
  private GpsPosnDisplay _gpsPosnDisplay;
  private SunPosnDisplay _sunPosnDisplay;
  
  private JLabel _radarNameValue;
  private JLabel _paramsFileNameValue;

  private JLabel _prt1Value;
  private JLabel _prt2Value;
  private JLabel _prtRatioValue;
  private JLabel _prtIsStaggeredValue;

  private JLabel _nGatesValue;
  private JLabel _gateSpacingValue;
  private JLabel _nominalPulseWidthValue;

  private JPanel _leftPanel;
  private PentekStatusDisplay _pentekStatusHigh;
  private PentekStatusDisplay _pentekStatusLow;

  private StatusData _status;
   
  private JPanel _topLevel;
  private JMenuBar _menuBar;
  private JButton _editButton;
  private JButton _helpButton;
  private String _infoContent;

  private ArrayList<JComponent> _listForFonts = null;
  
  /**
   * Single instance for this class - it is a singleton
   */
    
  private static final StatusPanel _instance = new StatusPanel();
    
  /**
   * get singleton instance
   */
    
  public static StatusPanel getInstance() {
    return _instance;
  }
    
  private StatusPanel() {
	
    // titles

    _setTitleStr();
    setSize(_params.status.width.getValue(),
            _params.status.height.getValue());
    setLocation(_params.status.xx.getValue(),
                _params.status.yy.getValue());
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

    // create list for font updates

    _listForFonts = new ArrayList<JComponent>();

    // add listener for window property changes
	
    addComponentListener(new WindowPropertiesListener());
	
    // add listener for new status data

    StatusDataHandler.getInstance().addListener(this);
    
    // main configuration
    
    JPanel configuration = new JPanel();
    configuration.setLayout(new BorderLayout());
    configuration.setBorder(CustomBorder.createTop(this, "Configuration", 3));
    addToFontList(configuration);
    
    JPanel genStatus = new JPanel();
    _radarNameValue = createLabel("DOWX");
    JPanel radarNamePanel = createPanel("Radar name",
                                        Color.yellow, Color.black,
                                        _radarNameValue);
    genStatus = addPanel(genStatus, radarNamePanel);
    
    _paramsFileNameValue = createLabel("ops");
    JPanel paramsFileNamePanel = createPanel("Params name",
                                             Color.yellow, Color.black,
                                             _paramsFileNameValue);
    genStatus = addPanel(genStatus, paramsFileNamePanel);
    
    _prt1Value = createLabel("0000");
    JPanel prt1Panel = createPanel("PRT1(us)/PRF1",
                                   Color.yellow, Color.black,
                                   _prt1Value);
    genStatus = addPanel(genStatus, prt1Panel);
    
    _prt2Value = createLabel("0000");
    JPanel prt2Panel = createPanel("PRT2(us)/PRF2",
                                   Color.yellow, Color.black,
                                   _prt2Value);
    genStatus = addPanel(genStatus, prt2Panel);
    
    _prtRatioValue = createLabel("0000");
    JPanel prtRatioPanel = createPanel("PRT ratio",
                                       Color.yellow, Color.black,
                                       _prtRatioValue);
    genStatus = addPanel(genStatus, prtRatioPanel);
    
    _prtIsStaggeredValue = createLabel("false");
    JPanel prtIsStaggeredPanel = createPanel("PRT is staggered",
                                             Color.yellow, Color.black,
                                             _prtIsStaggeredValue);
    genStatus = addPanel(genStatus, prtIsStaggeredPanel);
    
    _nGatesValue = createLabel("0000");
    JPanel nGatesPanel = createPanel("N gates",
                                     Color.yellow, Color.black,
                                     _nGatesValue);
    genStatus = addPanel(genStatus, nGatesPanel);
    
    _gateSpacingValue = createLabel("000000");
    JPanel gateSpacingPanel = createPanel("Gate spacing(m)",
                                          Color.yellow, Color.black,
                                          _gateSpacingValue);
    genStatus = addPanel(genStatus, gateSpacingPanel);
    
    _nominalPulseWidthValue = createLabel("0000000");
    JPanel nominalPulseWidthPanel =
      createPanel("Nominal pulse width (us)",
                  Color.yellow, Color.black,
                  _nominalPulseWidthValue);
    genStatus = addPanel(genStatus, nominalPulseWidthPanel);
    
    configuration.add(genStatus, BorderLayout.NORTH);
    
    // clocks, antenna position, sun position
    
    _gpsTimeDisplay = new DateTimeDisplay(this, DateTimeDisplay.GPS);
    _antennaPosnDisplay = new AntennaPosnDisplay(this);
    _gpsPosnDisplay = new GpsPosnDisplay(this);
    _sunPosnDisplay = new SunPosnDisplay(this);
    
    JPanel timeAndPosn = addPanel(null, _gpsTimeDisplay);
    timeAndPosn = addPanel(timeAndPosn, _antennaPosnDisplay);
    timeAndPosn = addPanel(timeAndPosn, _gpsPosnDisplay);
    timeAndPosn = addPanel(timeAndPosn, _sunPosnDisplay);
    
    // left panel
    
    _leftPanel = new JPanel(new BorderLayout());
    _leftPanel.add(timeAndPosn, BorderLayout.SOUTH);
    _leftPanel.add(configuration, BorderLayout.NORTH);

    // pentek status
	
    _pentekStatusHigh = new PentekStatusDisplay(this, "high");
    _pentekStatusLow = new PentekStatusDisplay(this, "low");

    // top level panel
	
    _topLevel = new JPanel(new BorderLayout());
    getContentPane().add(_topLevel);
    
    _topLevel.add(_leftPanel, BorderLayout.WEST);
    if (_params.status.showHighFreq.getValue()) {
      _topLevel.add(_pentekStatusHigh, BorderLayout.CENTER);
    }
    if (_params.status.showLowFreq.getValue()) {
      _topLevel.add(_pentekStatusLow, BorderLayout.EAST);
    }

    // menu bar
	
    _menuBar = new JMenuBar();
    setJMenuBar(_menuBar);

    // edit button
    
    _editButton = new JButton(_params.status.getViewEditAction());
    _menuBar.add(_editButton);
	
    // help button
	
    _helpButton = new JButton("Help");
    _menuBar.add(_helpButton);
    _infoContent =
      "<h1>StatusPanel help info</h1>\n" +
      "<hr>\n" +
      "<h2>Interpretation</h2>\n" +
      "<ul>\n" +
      "<li>The status panel updates at regular intervals.\n" +
      "<li>Status is obtained from the dowdrx server.\n" +
      "</ul>\n" +
      "<hr>\n" +
      "<h2>Configuration</h2>\n" +
      "<h3>Changing font size and status data interval:</h3>\n" +
      "<ul>\n" +
      "<li>Use Edit button to bring up parameters panel.\n" +
      "<li>Use info buttons if help is needed.\n" +
      "<li>Set parameters as appropriate.\n" +
      "</ul>\n" +
      "<h3>Setting the window size to accommodate the various panels:</h3>\n" +
      "<li>Use Edit button to bring up parameters panel.\n" +
      "<li>Select 'allow resize', hit apply.\n" +
      "<li>Resize window to suit.\n" +
      "<li>Turn off 'Allow resize' to fix window size.\n" +
      "</ul>\n";
    _helpButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          InfoFrame info =
            new InfoFrame(getX() + 30, getY() + 30,
                          600, 500);
          info.setTitle("Info for Status Panel");
          info.setContent(_infoContent);
        }
      });
	
    // add listener for param changes
    
    _params.status.addChangeListener(new StatusParamsChangeListener());
    _updateFromParams();
    
    // create the ticker thread, start it
	
    Ticker ticker = new Ticker();
    ticker.start();
	
  }

  // set title
    
  private void _setTitleStr() {
    String frameTitle =
      "STATUS PANEL - " + _params.mainWindow.radarName.getValue();
    setTitle(frameTitle);
  }

  // update from parameters

  private void _updateFromParams() {
    setFont();
    if (_params.status.allowResize.getValue()) {
      setResizable(true);
    } else {
      setResizable(false);
    }
    _topLevel.removeAll();
    _topLevel.add(_leftPanel, BorderLayout.WEST);
    if (_params.status.showHighFreq.getValue()) {
      _topLevel.add(_pentekStatusHigh, BorderLayout.CENTER);
    }
    if (_params.status.showLowFreq.getValue()) {
      _topLevel.add(_pentekStatusLow, BorderLayout.EAST);
    }
  }

  // handle incoming status

  public void handleStatus(StatusData status) {
    _status = status;
  }
    
  // redraw beam-related items

  private void _redraw() {

    if (_status == null) {
      return;
    }

    _sunPosnDisplay.redraw();
    _antennaPosnDisplay.redraw();
    _gpsTimeDisplay.redraw();
    _gpsPosnDisplay.redraw();

    _pentekStatusHigh.redraw();
    _pentekStatusLow.redraw();
    
    _radarNameValue.setText(_status.radar_name);
    // _paramsDirValue.setText(_status.params_dir);
    _paramsFileNameValue.setText(_status.params_file_name);

    String prt1Str = NFormat.f2.format(_status.prt1_sec * 1.0e6);
    prt1Str += " / ";
    prt1Str += NFormat.f2.format(1.0 / _status.prt1_sec);

    String prt2Str = NFormat.f2.format(_status.prt2_sec * 1.0e6);
    prt2Str += " / ";
    prt2Str += NFormat.f2.format(1.0 / _status.prt2_sec);

    _prt1Value.setText(prt1Str);
    _prt2Value.setText(prt2Str);

    if (_status.prt_is_staggered) {
      if (_status.prt_ratio < 0.7) {
        _prtRatioValue.setText("2/3");
      } else if (_status.prt_ratio < 0.77) {
        _prtRatioValue.setText("3/4");
      } else {
        _prtRatioValue.setText("4/5");
      }
    } else {
      _prtRatioValue.setText("1");
    }
    if (_status.prt_is_staggered) {
      _prtIsStaggeredValue.setText("TRUE");
    } else {
      _prtIsStaggeredValue.setText("FALSE");
    }

    _nGatesValue.setText(Integer.toString(_status.n_gates));
    _gateSpacingValue.setText(NFormat.f3.format(_status.rx_gate_spacing_m));

    _nominalPulseWidthValue.setText
      (NFormat.f2.format(_status.nominal_pulse_width_sec * 1.0e6));

  }
    
  // ticker thread
    
  private class Ticker extends Thread {
    public void run() {
      while (true) {
        // redraw
        _redraw();
        // sleep a bit
        Thread t = Thread.currentThread();
        try { t.sleep(500); }
        catch (InterruptedException e) { return; }
      }
    }
  }

  // listener for window properties

  private class WindowPropertiesListener extends ComponentAdapter {
	
    public void componentMoved(ComponentEvent e) {
      Parameters params = Parameters.getInstance();
      int paramX = params.status.xx.getValue();
      int paramY = params.status.yy.getValue();
      if (paramX != getX() || paramY != getY()) {
        params.status.xx.setValue(getX());
        params.status.yy.setValue(getY());
      }
    }
	
    public void componentResized(ComponentEvent e) {
      Parameters params = Parameters.getInstance();
      int paramWidth = params.status.width.getValue();
      int paramHeight = params.status.height.getValue();
      if (paramWidth != getWidth() || paramHeight != getHeight()) {
        params.status.width.setValue(getWidth());
        params.status.height.setValue(getHeight());
      }
    }

    public void componentShown(ComponentEvent e) {
      Parameters params = Parameters.getInstance();
      if (params.status.startVisible.getValue() == false) {
        params.status.startVisible.setValue(true);
      }
    }
	
    public void componentHidden(ComponentEvent e) {
      Parameters params = Parameters.getInstance();
      if (params.status.startVisible.getValue() == true) {
        params.status.startVisible.setValue(false);
      }
    }
	
  }

  // Listener for changes in status parameters

  private class StatusParamsChangeListener implements CollectionChangeListener {
    public void reactToChange() {
      _updateFromParams();
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
