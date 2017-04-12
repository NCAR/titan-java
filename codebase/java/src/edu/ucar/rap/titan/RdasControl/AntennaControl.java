///////////////////////////////////////////////////////////////////////
//
// AntennaControl
//
// Mike Dixon
//
// Oct 2002
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.lang.Math;
import edu.ucar.rap.jrp.*;

public class AntennaControl extends JPanel implements SunPosnListener

{

    private Parameters _params = Parameters.getInstance();
    private ControlModel _model = ControlModel.getInstance();
    
    private ManualPanel _manualPanel;
    private ScanModePanel _scanModePanel;
    private SlewRatePanel _slewRatePanel;
    private ScanMode _scanMode = ScanMode.MANUAL;

    public AntennaControl(Component parent) {
	
	setLayout(new BorderLayout());
	setBorder(CustomBorder.createTop(parent, "Antenna control", 5));
	
	// manual set panel
	
	_manualPanel = new ManualPanel(parent);
	
	// combine mode panel and slew rate panel
	
	_scanModePanel = new ScanModePanel(parent);
	_slewRatePanel = new SlewRatePanel(parent);

	JPanel modeAndSlewPanel = new JPanel(new BorderLayout());
	modeAndSlewPanel.add(_scanModePanel, BorderLayout.NORTH);
	modeAndSlewPanel.add(_slewRatePanel, BorderLayout.SOUTH);
	
	// add to main panel
	
	add(_manualPanel, BorderLayout.CENTER);
	add(modeAndSlewPanel, BorderLayout.WEST);
	
	// add listener for sun position
	
	SunTrack.getInstance().addSunPosnListener(this);
	
    }
    
    // enable control GUI

    public void enableControl(boolean state) {
	_scanModePanel.enableControl(state);
	_slewRatePanel.enableControl(state);
	if (state) {
	    _manualPanel.setEnabledFromScanMode();
	} else {
	    _manualPanel.setEnabled(false);
	}
    }
    
    public void setScanModeManual() {
	_scanModePanel.setScanModeManual();
    }

    public void setScanModeAutoVol() {
	_scanModePanel.setScanModeAutoVol();
    }

    public void setScanModeAutoPpi() {
	_scanModePanel.setScanModeAutoPpi();
    }

    public void setScanModeFollowSun() {
	_scanModePanel.setScanModeFollowSun();
    }

    public void setScanModeStop() {
	_scanModePanel.setScanModeStop();
    }
    
    public void setSunPosn(double el, double az) {
	if (_scanMode == ScanMode.FOLLOW_SUN) {
	    if (el < _params.eng.antennaMinElev.getValue()) {
		el = _params.eng.antennaMinElev.getValue();
	    }
	    if (el > _params.eng.antennaMaxElev.getValue()) {
		el = _params.eng.antennaMaxElev.getValue();
	    }
	    _model.setTargetEl(el);
	    _model.setTargetAz(az);
	}
    }
    
   // manual position panel - inner class

    private class ManualPanel extends JPanel {

	private JTextField _elTextField;
	private JTextField _azTextField;
	private double _targetEl = _params.control.startElev.getValue();
	private double _targetAz = _params.control.startAz.getValue();
	
	private JButton _up;
	private JButton _down;
	private JButton _left;
	private JButton _right;
	    
	private DeltaDegPanel _deltaDegPanel;

	private AzEditListener _azEditListener;
	private ElEditListener _elEditListener;
	
	private double _deltaDeg;

	public ManualPanel(Component parent) {
	    
	    setLayout(new BorderLayout());
	    setBorder(CustomBorder.createTop(parent, "Manual set", 3));
	    
	    Font defaultFont = getFont();
	    Font midFont =
		defaultFont.deriveFont((float) (defaultFont.getSize() + 0));
	    Font bigFont =
		defaultFont.deriveFont((float) (defaultFont.getSize() + 2));
	    
	    // elevation entry panel
	    
	    JLabel elLabel = new JLabel("EL", JLabel.CENTER);
	    elLabel.setFont(midFont);
	    
	    _elTextField = new JTextField("00.00", 5);
	    _elTextField.setText(NFormat.el2.format(_targetEl));
	    _elTextField.setToolTipText("Set desired elevation");
	    _elEditListener = new ElEditListener();
	    _elTextField.addActionListener(_elEditListener);

	    BorderLayout elLayout = new BorderLayout();
	    elLayout.setVgap(3);
	    JPanel elPanel = new JPanel(elLayout);
	    elPanel.add(elLabel, BorderLayout.NORTH);
	    elPanel.add(_elTextField, BorderLayout.SOUTH);
	    
	    // azimuth entry panel
	    
	    JLabel azLabel = new JLabel("AZ", JLabel.CENTER);
	    azLabel.setFont(midFont);
	    
	    _azTextField = new JTextField("000.00", 5);
	    _azTextField.setText(NFormat.az2.format(_targetAz));
	    _azTextField.setToolTipText("Set desired azimuth");
	    _azEditListener = new AzEditListener();
	    _azTextField.addActionListener(_azEditListener);
	    
	    BorderLayout azLayout = new BorderLayout();
	    azLayout.setVgap(3);
	    JPanel azPanel = new JPanel(azLayout);
	    azPanel.add(azLabel, BorderLayout.NORTH);
	    azPanel.add(_azTextField, BorderLayout.SOUTH);
	    
	    // combine entry panels
	    
	    BorderLayout comboLayout = new BorderLayout();
	    comboLayout.setHgap(10);
	    JPanel entryPanel = new JPanel(comboLayout);
	    entryPanel.add(elPanel, BorderLayout.WEST);
	    entryPanel.add(azPanel, BorderLayout.EAST);
	    JPanel entryContainer = new JPanel();
	    entryContainer.add(entryPanel);
	    entryContainer.setBorder
		(CustomBorder.createTop(parent, "Enter position", 3));
	    
	    // arrow panel
	    
	    JPanel arrowPanel = new JPanel(new BorderLayout());

	    TrDummyComponent dc = new TrDummyComponent();

	    Image upImage = JrpImageLoad.getFromRes
		(dc, "/edu/ucar/rap/titan/RdasControl/images/up.png");
	    ImageIcon upIcon = new ImageIcon(upImage);

	    Image downImage = JrpImageLoad.getFromRes
		(dc, "/edu/ucar/rap/titan/RdasControl/images/down.png");
	    ImageIcon downIcon = new ImageIcon(downImage);

	    Image leftImage = JrpImageLoad.getFromRes
		(dc, "/edu/ucar/rap/titan/RdasControl/images/left.png");
	    ImageIcon leftIcon = new ImageIcon(leftImage);

	    Image rightImage = JrpImageLoad.getFromRes
		(dc, "/edu/ucar/rap/titan/RdasControl/images/right.png");
	    ImageIcon rightIcon = new ImageIcon(rightImage);

	    _up = new JButton(upIcon);
	    _down = new JButton(downIcon);
	    _left = new JButton(leftIcon);
	    _right = new JButton(rightIcon);
	    
	    _up.setToolTipText("Click to go up, or ALT up-arrow");
	    _up.setMnemonic(KeyEvent.VK_UP);
	    _down.setToolTipText("Click to go down, or ALT down-arrow");
	    _down.setMnemonic(KeyEvent.VK_DOWN);
	    _left.setToolTipText("Click to go left, or ALT left-arrow");
	    _left.setMnemonic(KeyEvent.VK_LEFT);
	    _right.setToolTipText("Click to go right, or ALT right-arrow");
	    _right.setMnemonic(KeyEvent.VK_RIGHT);

	    _up.addActionListener(new UpListener());
	    _down.addActionListener(new DownListener());
	    _left.addActionListener(new LeftListener());
	    _right.addActionListener(new RightListener());
	    
	    arrowPanel.add(_up, BorderLayout.NORTH);
	    arrowPanel.add(_down, BorderLayout.SOUTH);
	    arrowPanel.add(_left, BorderLayout.WEST);
	    arrowPanel.add(_right, BorderLayout.EAST);

	    JPanel arrowContainer = new JPanel();
	    arrowContainer.add(arrowPanel);

	    // adj res panel

	    _deltaDegPanel = new DeltaDegPanel(parent);

	    // put it all together

	    add(arrowContainer, BorderLayout.NORTH);
	    add(_deltaDegPanel, BorderLayout.CENTER);
	    add(entryContainer, BorderLayout.SOUTH);

	    setEnabledFromScanMode();
	    
	} // constructor

	// el edit action
	class ElEditListener implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
		setElFromTextField();
	    }
	}

	// az edit action
	class AzEditListener implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
		setAzFromTextField();
	    }
	}
	
	// up action listener
	class UpListener implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
		_targetEl += _deltaDeg;
		long rounded = Math.round(_targetEl / _deltaDeg);
		_targetEl = rounded * _deltaDeg;
		if (_targetEl > _params.eng.antennaMaxElev.getValue()) {
		    _targetEl = _params.eng.antennaMaxElev.getValue();
		}
		_elTextField.setText(NFormat.el2.format(_targetEl));
		setElFromTextField();
	    }
	}

	// down action listener
	class DownListener implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
		_targetEl -= _deltaDeg;
		long rounded = Math.round(_targetEl / _deltaDeg);
		_targetEl = rounded * _deltaDeg;
		if (_targetEl < _params.eng.antennaMinElev.getValue()) {
		    _targetEl = _params.eng.antennaMinElev.getValue();
		}
		_elTextField.setText(NFormat.el2.format(_targetEl));
		setElFromTextField();
	    }
	}

	// left action listener
	class LeftListener implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
		_targetAz -= _deltaDeg;
		long rounded = Math.round(_targetAz / _deltaDeg);
		_targetAz = rounded * _deltaDeg;
		if (_targetAz < 0) {
		    _targetAz += 360.0;
		}
		if (Math.abs(_targetAz - 0) < 0.00001 ||
		    Math.abs(_targetAz - 360) < 0.00001) {
		    _targetAz = 0.0;
		}
		_azTextField.setText(NFormat.az2.format(_targetAz));
		setAzFromTextField();
	    }
	}

	// right action listener

	class RightListener implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
		_targetAz += _deltaDeg;
		long rounded = Math.round(_targetAz / _deltaDeg);
		_targetAz = rounded * _deltaDeg;
		if (_targetAz >= 360) {
		    _targetAz -= 360.0;
		}
		if (Math.abs(_targetAz - 0) < 0.00001 ||
		    Math.abs(_targetAz - 360) < 0.00001) {
		    _targetAz = 0.0;
		}
		_azTextField.setText(NFormat.az2.format(_targetAz));
		setAzFromTextField();
	    }
	}

	// set the el from field

	public void setElFromTextField() { 
	    double el = 0.0;
	    boolean error = false;
	    try {
		el = Double.parseDouble(_elTextField.getText());
	    }
	    catch (java.lang.NumberFormatException nfe) {
		error = true;
	    }
	    if (el < _params.eng.antennaMinElev.getValue() ||
		el > _params.eng.antennaMaxElev.getValue()) {
		error = true;
	    }
	    if (error) {
		// show error dialog
		String errStr =
		    "Illegal value for elevation: " +
		    _elTextField.getText() + "\n" +
		    "Min el: " + _params.eng.antennaMinElev.getValue() + "\n" +
		    "Max el: " + _params.eng.antennaMaxElev.getValue() + "\n";
		JOptionPane.showMessageDialog(_elTextField, errStr,
					      "Illegal Double Value",
					      JOptionPane.ERROR_MESSAGE);
		return;
	    }
	    _targetEl = el;
	    _model.setTargetEl(el);
	}
	
	// set the az from field

	public void setAzFromTextField() { 
	    double az = 0.0;
	    boolean error = false;
	    try {
		az = Double.parseDouble(_azTextField.getText());
	    }
	    catch (java.lang.NumberFormatException nfe) {
		error = true;
	    }
	    if (az < 0.0 || az > 360.0) {
		error = true;
	    }
	    if (error) {
		// show error dialog
		String errStr =
		    "Illegal value for azimuth: " +
		    _azTextField.getText();
		JOptionPane.showMessageDialog(_azTextField, errStr,
					      "Illegal Double Value",
					      JOptionPane.ERROR_MESSAGE);
		return;
	    }
	    _targetAz = az;
	    _model.setTargetAz(az);
	}
	
	// enable the GUI components
    
	// set enabled from scan mode

	public void setEnabledFromScanMode() {
	    if (_scanMode == ScanMode.MANUAL) {
		setEnabled(true);
	    } else if (_scanMode == ScanMode.AUTO_PPI) {
		setHorizEnabled(false);
		setVertEnabled(true);
	    } else {
		setEnabled(false);
	    }
	}
	
	public void setEnabled(boolean enabled) {
	    setVertEnabled(enabled);
	    setHorizEnabled(enabled);
	}

	public void setVertEnabled(boolean enabled) {
	    _elTextField.setEnabled(enabled);
	    _up.setEnabled(enabled);
	    _down.setEnabled(enabled);
	    _deltaDegPanel.setEnabled(enabled);
	}

	public void setHorizEnabled(boolean enabled) {
	    _azTextField.setEnabled(enabled);
	    _left.setEnabled(enabled);
	    _right.setEnabled(enabled);
	    _deltaDegPanel.setEnabled(enabled);
	}

	// DeltaDegPanel - adjustment resolution
	
	private class DeltaDegPanel extends JPanel {
	
	    private JRadioButton _coarse, _medium, _fine;
	    
	    public DeltaDegPanel(Component parent) {
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setBorder(CustomBorder.createTop(parent, "Delta deg", 3));
		
		_coarse = new JRadioButton("1.0");
		_medium = new JRadioButton("0.1");
		_fine = new JRadioButton("0.01");

		add(_coarse);
		add(_medium);
		add(_fine);

		ButtonGroup group = new ButtonGroup();
		group.add(_coarse);
		group.add(_medium);
		group.add(_fine);
		
		_coarse.setSelected(true);
		_deltaDeg = 1.0;
		
		_coarse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    _deltaDeg = 1.0;
			}
		    });
			
		_medium.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    _deltaDeg = 0.1;
			}
		    });
			
		_fine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    _deltaDeg = 0.01;
			}
		    });
		
	    }
	    
	    // enable the GUI components
	    
	    public void setEnabled(boolean enabled) {
		_coarse.setEnabled(enabled);
		_medium.setEnabled(enabled);
		_fine.setEnabled(enabled);
	    }

	}
	
    } // ManualPanel

    // ScanModePanel - inner class

    private class ScanModePanel extends JPanel {
	
	private JRadioButton _manualButton;
	private JRadioButton _autoVolButton;
	private JRadioButton _autoPpiButton;
	private JRadioButton _followSunButton;
	private JRadioButton _stopButton;

	private ManualListener _manualListener;
	private AutoVolListener _autoVolListener;
	private AutoPpiListener _autoPpiListener;
	private FollowSunListener _followSunListener;
	private StopListener _stopListener;

	public ScanModePanel(Component parent) {
	    
	    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	    setBorder(CustomBorder.createTop(parent, "Scan mode", 3));
	    
	    _manualButton = new JRadioButton("Manual");
	    _autoVolButton = new JRadioButton("Auto volume");
	    _autoPpiButton = new JRadioButton("Auto PPI");
	    _followSunButton = new JRadioButton("Follow sun");
	    _stopButton = new JRadioButton("Stop");
	    
	    add(_manualButton);
	    add(_autoVolButton);
	    add(_autoPpiButton);
	    add(_followSunButton);
	    add(_stopButton);
	    
	    ButtonGroup group = new ButtonGroup();
	    group.add(_manualButton);
	    group.add(_autoVolButton);
	    group.add(_autoPpiButton);
	    group.add(_followSunButton);
	    group.add(_stopButton);
	    
	    String startScanModeStr = _params.control.startScanMode.getValue();
	    if (startScanModeStr.equals("Manual")) {
		setScanModeManual();
	    } else if (startScanModeStr.equals("Auto-Volume")) {
		setScanModeAutoVol();
	    } else if (startScanModeStr.equals("Auto-PPI")) {
		setScanModeAutoPpi();
	    } else if (startScanModeStr.equals("Follow-Sun")) {
		setScanModeFollowSun();
	    } else if (startScanModeStr.equals("Stop")) {
		setScanModeStop();
	    }
	    
	    _manualListener = new ManualListener();
	    _manualButton.addActionListener(_manualListener);

	    _autoVolListener = new AutoVolListener();
	    _autoVolButton.addActionListener(_autoVolListener);

	    _autoPpiListener = new AutoPpiListener();
	    _autoPpiButton.addActionListener(_autoPpiListener);

	    _followSunListener = new FollowSunListener();
	    _followSunButton.addActionListener(_followSunListener);

	    _stopListener = new StopListener();
	    _stopButton.addActionListener(_stopListener);

	}
	
	// enable control GUI
	
	public void enableControl(boolean state) {
	    _manualButton.setEnabled(state);
	    _autoVolButton.setEnabled(state);
	    _autoPpiButton.setEnabled(state);
	    _followSunButton.setEnabled(state);
	    _stopButton.setEnabled(state);
	}

	// set scan mode to manual

	public void setScanModeManual() {
	    _manualButton.setSelected(true);
	    _scanMode = ScanMode.MANUAL;
	    _manualPanel.setEnabled(true);
	    _model.setScanMode(ScanMode.MANUAL);
	}
	
	// set scan mode to auto-vol

	public void setScanModeAutoVol() {

	    _autoVolButton.setSelected(true);
	    _scanMode = ScanMode.AUTO_VOL;
	    _manualPanel.setEnabled(false);

	    String[] strArray =
		_params.scan.elevationList.getValue().split(",");
	    float[] elevArray = new float[strArray.length];
	    for (int i = 0; i < strArray.length; i++) {
		try {
		    elevArray[i] = Float.parseFloat(strArray[i]);
		}
		catch (java.lang.NumberFormatException nfe) {
		    System.err.println(nfe);
		}
	    }
	    
	    _model.setScanMode(ScanMode.AUTO_VOL);
	    
	}
	
	// set scan mode to auto-ppi

	public void setScanModeAutoPpi() {
	    _autoPpiButton.setSelected(true);
	    _scanMode = ScanMode.AUTO_PPI;
	    _manualPanel.setHorizEnabled(false);
	    _manualPanel.setVertEnabled(true);
	    _model.setScanMode(ScanMode.AUTO_PPI);
	}
	
	// set scan mode to follow Sun

	public void setScanModeFollowSun() {
	    _followSunButton.setSelected(true);
	    _scanMode = ScanMode.FOLLOW_SUN;
	    _manualPanel.setEnabled(false);
	    _model.setScanMode(ScanMode.FOLLOW_SUN);
	}
	
	// set scan mode to stop
	
	public void setScanModeStop() {
	    _stopButton.setSelected(true);
	    _scanMode = ScanMode.STOP;
	    _manualPanel.setEnabled(false);
	    _model.setScanMode(ScanMode.STOP);
	}

	// manual action listener
	class ManualListener implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
		setScanModeManual();
	    }
	}
	
	// auto vol action listener
	class AutoVolListener implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
		setScanModeAutoVol();
	    }
	}
	
	// auto ppi action listener
	class AutoPpiListener implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
		setScanModeAutoPpi();
	    }
	}
	
	// followSun action listener
	class FollowSunListener implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
		setScanModeFollowSun();
	    }
	}
	
	// stop action listener
	class StopListener implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
		setScanModeStop();
	    }
	}
	
    } // scanModePanel
    
    // slew rate panel - inner class

    private class SlewRatePanel extends JPanel {
	
	private JTextField _elRateTextField;
	private JTextField _azRateTextField;
	
	private AzRateEditListener _azRateEditListener;
	private ElRateEditListener _elRateEditListener;

	public SlewRatePanel(Component parent) {
	    
	    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	    setBorder(CustomBorder.createTop(parent, "Slew rates", 3));

	    // fonts
	    
	    Font defaultFont = getFont();
	    Font midFont =
		defaultFont.deriveFont((float) (defaultFont.getSize() + 0));
	    Font bigFont =
		defaultFont.deriveFont((float) (defaultFont.getSize() + 2));
	    
	    // elevation rate entry panel
	    
	    JLabel elRateLabel = new JLabel("El rate (deg/s)", JLabel.CENTER);
	    elRateLabel.setFont(midFont);

	    String elRateStr = new
		String(NFormat.f2.format(_params.scan.elSlewRate.getValue()));
	    _elRateTextField = new JTextField(elRateStr, 5);
	    _elRateTextField.setToolTipText("Set el slew rate (deg/s)");
	    _elRateEditListener = new ElRateEditListener();
	    _elRateTextField.addActionListener(_elRateEditListener);
	    
	    BorderLayout elLayout = new BorderLayout();
	    elLayout.setVgap(3);
	    JPanel elRatePanel = new JPanel(elLayout);
	    elRatePanel.add(elRateLabel, BorderLayout.NORTH);
	    elRatePanel.add(_elRateTextField, BorderLayout.SOUTH);
	    
	    // azimuth rate entry panel
	    
	    JLabel azRateLabel = new JLabel("Az rate (deg/s)", JLabel.CENTER);
	    azRateLabel.setFont(midFont);
	    
	    String azRateStr = new
		String(NFormat.f2.format(_params.scan.azSlewRate.getValue()));
	    _azRateTextField = new JTextField(azRateStr, 5);
	    _azRateTextField.setToolTipText("Set az slew rate (deg/s)");
	    _azRateEditListener = new AzRateEditListener();
	    _azRateTextField.addActionListener(_azRateEditListener);
	    
	    BorderLayout azLayout = new BorderLayout();
	    azLayout.setVgap(3);
	    JPanel azRatePanel = new JPanel(azLayout);
	    azRatePanel.add(azRateLabel, BorderLayout.NORTH);
	    azRatePanel.add(_azRateTextField, BorderLayout.SOUTH);
	    
	    // combine entry panels
	    
	    BorderLayout comboLayout = new BorderLayout();
	    comboLayout.setVgap(6);
	    JPanel entryPanel = new JPanel(comboLayout);
	    entryPanel.add(elRatePanel, BorderLayout.NORTH);
	    entryPanel.add(azRatePanel, BorderLayout.SOUTH);
	    JPanel entryContainer = new JPanel();
	    entryContainer.add(entryPanel);
	    
	    // add to main container
	    
	    add(entryContainer, BorderLayout.SOUTH);

	} // constructor
	
	// enable control GUI
	
	public void enableControl(boolean state) {
	    _elRateTextField.setEnabled(state);
	    _azRateTextField.setEnabled(state);
	}
	
	// el edit action
	class ElRateEditListener implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
		setElRateFromTextField();
	    }
	}

	// az edit action
	class AzRateEditListener implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
		setAzRateFromTextField();
	    }
	}
	
	// set the el rate from field
	
	public void setElRateFromTextField() { 
	    double rate = 0.0;
	    boolean error = false;
	    try {
		rate = Double.parseDouble(_elRateTextField.getText());
	    }
	    catch (java.lang.NumberFormatException nfe) {
		error = true;
	    }
	    double maxRate = _params.eng.antennaMaxElSlewRate.getValue();
	    if (rate < -maxRate || rate > maxRate) {
		error = true;
	    }
	    if (error) {
		// show error dialog
		String errStr =
		    "Illegal value for elevation slew rate: " +
		    _elRateTextField.getText() + "\n" +
		    "Min rate: " + -maxRate + "\n" +
		    "Max rate: " + maxRate + "\n";
		JOptionPane.showMessageDialog(_elRateTextField, errStr,
					      "Illegal el rate value",
					      JOptionPane.ERROR_MESSAGE);
		return;
	    }
	    _params.scan.elSlewRate.setValue(rate);
	    _model.setElSlewRate(rate);
	}
	
	// set the az rate from field
	
	public void setAzRateFromTextField() { 
	    double rate = 0.0;
	    boolean error = false;
	    try {
		rate = Double.parseDouble(_azRateTextField.getText());
	    }
	    catch (java.lang.NumberFormatException nfe) {
		error = true;
	    }
	    double maxRate = _params.eng.antennaMaxAzSlewRate.getValue();
	    if (rate < -maxRate || rate > maxRate) {
		error = true;
	    }
	    if (error) {
		// show error dialog
		String errStr =
		    "Illegal value for azimuth slew rate: " +
		    _azRateTextField.getText() + "\n" +
		    "Min rate: " + -maxRate + "\n" +
		    "Max rate: " + maxRate + "\n";
		JOptionPane.showMessageDialog(_azRateTextField, errStr,
					      "Illegal az rate value",
					      JOptionPane.ERROR_MESSAGE);
		return;
	    }
	    _params.scan.azSlewRate.setValue(rate);
	    _model.setAzSlewRate(rate);
	}
	
    } // SlewRatePanel
    
}

