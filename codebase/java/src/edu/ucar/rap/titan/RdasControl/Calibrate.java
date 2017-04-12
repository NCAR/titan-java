//////////////////////////////////////////////////////////////////////
//
// Calibrate
//
// Mike Dixon
//
// Aug 2003
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.io.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.util.*;
import java.nio.*;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import edu.ucar.rap.jrp.*;

public class Calibrate extends JFrame

{

    private Parameters _params = Parameters.getInstance();
    private AScope _aScope;

    // gui

    private JPanel _valuePanel;
    private JPanel _plotPanel;
    private JPanel _tablePanel;
    private CalibCanvas _canvas;
    private JButton _unZoomButton;
    private String _infoContent;
    
    private JLabel _peakPowerLabel;
    private JLabel _radarConstLabel;
    private JLabel _mdsCountLabel;
    private JLabel _mdsPowerLabel;
    
    private JSlider _fitDbzLowSlider;
    private JSlider _fitDbzHighSlider;

    private CalTable _calTable;
    private CalTableModel _calTableModel;
    
    private boolean _tableSelectionActive = false;
    private int _tableSelectionRow = 0;
    
    private static final int siggenCol = 0;
    private static final int dbmCol = 1;
    private static final int dbz1kmCol = 2;
    private static final int dbzCalRngCol = 3;
    private static final int countCol = 4;

    private FileFolderAction _fileFolderAction;
    private JMenuItem _fileFolderItem;
    private FileReadAction _fileReadAction;
    private FileSaveAction _fileSaveAction;
    private JLabel _filePathLabel;
    
    JButton _sampleTableRowButton;
    JButton _clearTableRowButton;
    JButton _clearTableAllButton;

    // constants

    private static final double _piCubed = Math.pow(Math.PI, 3.0);
    private static final double _lightSpeed = 3.0e8;
    private static final double _kSquared = 9.3e-1;

    // from radar params

    private String _siteName;
    private int _siteNum;
    private String _userName;
    private double _freqGhz;
    private double _horizBeamWidth;
    private double _vertBeamWidth;
    private double _antGain;

    // from calib params
    
    private double _peakPower;
    private double _pulseWidth;
    
    private double _waveguideLoss;
    private double _radomeLoss;
    private double _receiverLoss;
    private double _testCableAtten;
    private double _couplerAtten;

    private int _nPointsTable = 15;
    private double _minDbz = -5;
    private double _maxDbz = 70;
    private double _deltaDbz = 5;
    private double _fitDbzLow = -35;
    private double _fitDbzHigh = 25;

    // derived

    private double _freqHz;
    private double _lambda;
    private double _atmosAtten;
    private double _radarConst;

    private double _calRng;
    private String _dbzCalRngStr;
    private double _rangeCorrCalRng;
    private double _rangeCorr1km;
    private double _atmosAttenCalRng;
    private double _atmosAtten1km;
    private double _corr1km2CalRng;

    // calib results
    
    private double _mdsCount;
    private double _mdsSiggen;
    private double _mdsPower;

    private double _calSlope;
    private double _calOffsetCalRng;
    private double _calOffset1km;

    private ArrayList _posnArray;
    private ArrayList _siggenArray;
    private ArrayList _dbmArray;
    private ArrayList _dbz1kmArray;
    private ArrayList _dbzCalRngArray;
    private ArrayList _countArray;

    private boolean _calibAvail = false;

    public Calibrate(AScope aScope) {
	
	// get parameters
	
	_updateFromParams();

	// construct top-level objects

	_aScope = aScope;
	
	_setTitleStr();
 	setSize(_params.calib.width.getValue(),
		_params.calib.height.getValue());
	setLocation(_params.calib.xx.getValue(),
		    _params.calib.yy.getValue());
	setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	getContentPane().setLayout(new BorderLayout());
	
	addComponentListener(new MoveResizeListener());
	
	// menu bar
	
	JMenuBar menuBar = new JMenuBar();
	setJMenuBar(menuBar);
	_fillMenuBar(menuBar);

	// label panel
	
	_valuePanel = new JPanel(new BorderLayout());
	_valuePanel.setBorder(CustomBorder.createSimple(this, 5));
	_addValueLabels();
  	getContentPane().add(_valuePanel, BorderLayout.NORTH);

	// table panel
	
	_tablePanel = new JPanel(new BorderLayout());
	_tablePanel.setBorder
	    (CustomBorder.createTop(this, "Table", 5));
	_tablePanel.add(_createCalTable(), BorderLayout.CENTER);
  	getContentPane().add(_tablePanel, BorderLayout.SOUTH);
	_addTableButtons();
	
	// plot panel
	
	JLabel fitDbzLowLabel = new JLabel("Fit low: ");
	_fitDbzLowSlider = new JSlider();
	_fitDbzLowSlider.setMinimum(-20);
	_fitDbzLowSlider.setMaximum(80);
	_fitDbzLowSlider.setValue((int) _params.calib.fitDbzLow.getValue());
	_fitDbzLowSlider.addChangeListener(new FitDbzLowSliderListener());
	JPanel fitDbzLowPanel = new JPanel(new BorderLayout());
	fitDbzLowPanel.add(fitDbzLowLabel, BorderLayout.WEST);
	fitDbzLowPanel.add(_fitDbzLowSlider, BorderLayout.EAST);

	JLabel fitDbzHighLabel = new JLabel("Fit high: ");
	_fitDbzHighSlider = new JSlider();
	_fitDbzHighSlider.setMinimum(-20);
	_fitDbzHighSlider.setMaximum(80);
	_fitDbzHighSlider.setValue((int) _params.calib.fitDbzHigh.getValue());
	_fitDbzHighSlider.addChangeListener(new FitDbzHighSliderListener());
	JPanel fitDbzHighPanel = new JPanel(new BorderLayout());
	fitDbzHighPanel.add(fitDbzHighLabel, BorderLayout.WEST);
	fitDbzHighPanel.add(_fitDbzHighSlider, BorderLayout.EAST);

	JPanel sliderPanel = new JPanel(new BorderLayout());
	sliderPanel.add(fitDbzLowPanel, BorderLayout.WEST);
	sliderPanel.add(fitDbzHighPanel, BorderLayout.EAST);
	
	_plotPanel = new JPanel(new BorderLayout());
	_plotPanel.setBorder
	    (CustomBorder.createTop(this, "Plot", 5));
	_canvas = new CalibCanvas(this);
	_plotPanel.add(_canvas, BorderLayout.CENTER);
	_plotPanel.add(sliderPanel, BorderLayout.SOUTH);
  	getContentPane().add(_plotPanel, BorderLayout.CENTER);
	
	// create arrays
	
	_posnArray = new ArrayList();
	_siggenArray = new ArrayList();
	_dbmArray = new ArrayList();
	_dbz1kmArray = new ArrayList();
	_dbzCalRngArray = new ArrayList();
	_countArray = new ArrayList();

	// update parameters again, to reset values in objects
	
	_updateFromParams();

	// prepare for calib computations
	
	_updateTable();

	// read in cal file if it is set

	String calibPath = _params.calib.filePath.getValue();
	if (!calibPath.equals("none")) {
	    _readXmlFile(calibPath);
	    _params.calib.setUnsavedChanges(false);
	}

    }
    
     // set title

    private void _setTitleStr() {
	String frameTitle =
	    "CALIBRATION - " + _params.radar.siteName.getValue();
	setTitle(frameTitle);
    }

    // set zoom state for calibration plot

    public void setZoomState(boolean state) {
	_unZoomButton.setEnabled(state);
    }

    // is calibration available?

    public boolean isCalibAvail() {
	return _calibAvail;
    }

    public void setCalibAvail(boolean status) {

	_calibAvail = status;
	_sampleTableRowButton.setEnabled(!status);
	_clearTableRowButton.setEnabled(!status);

	if (status) {

	    // sync to DbzArray class static members
	    
	    DbzArray.getInstance().setCal(_atmosAtten, _calSlope,
					  _calOffset1km,
					  _mdsCount, _mdsPower);
	    
	    // sync to params
	    
	    _params.calib.slope.setValue(_calSlope);
	    _params.calib.offset1km.setValue(_calOffset1km);
	    _params.calib.offsetCalRng.setValue(_calOffsetCalRng);

	}
	
    }

    // get array size and values

    public int getNPoints() {
	return _countArray.size();
    }

    public int getPosnVal(int index) {
	if (_posnArray.size() == 0) {
	    return 0;
	}
	if (index >= _posnArray.size()) {
	    index = _posnArray.size() - 1;
	}
	return ((Integer) _posnArray.get(index)).intValue();
    }
    
    public double getSiggenVal(int index) {
	if (_siggenArray.size() == 0) {
	    return 0;
	}
	if (index >= _siggenArray.size()) {
	    index = _siggenArray.size() - 1;
	}
	return ((Double) _siggenArray.get(index)).doubleValue();
    }
    
    public double getDbmVal(int index) {
	if (_dbmArray.size() == 0) {
	    return 0;
	}
	if (index >= _dbmArray.size()) {
	    index = _dbmArray.size() - 1;
	}
	return ((Double) _dbmArray.get(index)).doubleValue();
    }

    public double getDbz1kmVal(int index) {
	if (_dbz1kmArray.size() == 0) {
	    return 0;
	}
	if (index >= _dbz1kmArray.size()) {
	    index = _dbz1kmArray.size() - 1;
	}
	return ((Double) _dbz1kmArray.get(index)).doubleValue();
    }
    
    public String getDbzCalRngStr() {
	return _dbzCalRngStr;
    }

    public double getDbzCalRngVal(int index) {
	if (_dbzCalRngArray.size() == 0) {
	    return 0;
	}
	if (index >= _dbzCalRngArray.size()) {
	    index = _dbzCalRngArray.size() - 1;
	}
	return ((Double) _dbzCalRngArray.get(index)).doubleValue();
    }
    
    public double getCountVal(int index) {
	if (_countArray.size() == 0) {
	    return 0;
	}
	if (index >= _countArray.size()) {
	    index = _countArray.size() - 1;
	}
	return ((Double) _countArray.get(index)).doubleValue();
    }

    // cal results

    public double getCalSlope() {
	return _calSlope;
    }

    public double getCalOffset1km() {
	return _calOffset1km;
    }

    public double getCalOffsetCalRng() {
	return _calOffsetCalRng;
    }

    // fill out menu bar
    
    private void _fillMenuBar(JMenuBar menuBar) {
	
	// file menu

	JMenu fileMenu = new JMenu("File");
	// fileMenu.setMnemonic('F');
	fileMenu.setBorder(CustomBorder.createSimple(this, 0));

	// file folder button
	
	TrDummyComponent dc = new TrDummyComponent();
	Image fileFolderImage =
	    JrpImageLoad.getFromRes(dc, "/edu/ucar/rap/jrp/images/folder.png");
	ImageIcon fileFolderIcon = new ImageIcon(fileFolderImage);
	_fileFolderAction = new FileFolderAction("Set folder", fileFolderIcon);
	_fileFolderItem = fileMenu.add(_fileFolderAction);
	_fileFolderItem.setToolTipText("Set calib folder");
	
	// file read button

	Image readImage =
	    JrpImageLoad.getFromRes(dc, "/edu/ucar/rap/jrp/images/open.png");
	ImageIcon readIcon = new ImageIcon(readImage);
	_fileReadAction = new FileReadAction("Read in file", readIcon);
	JMenuItem readItem = fileMenu.add(_fileReadAction);
	readItem.setToolTipText("Read in existing calibration file");
	
	// file save button
	
	Image saveImage =
	    JrpImageLoad.getFromRes(dc, "/edu/ucar/rap/jrp/images/save.png");
	ImageIcon saveIcon = new ImageIcon(saveImage);
  	_fileSaveAction = new FileSaveAction("Save to file", saveIcon);
	// _fileSaveAction.setEnabled(false);
  	JMenuItem saveItem = fileMenu.add(_fileSaveAction);
  	saveItem.setToolTipText("Save the latest calibration");
	
	// parameter editing short-cut

	JButton editButton = new JButton(_params.calib.getViewEditAction());

	// add listeners so that parameter objects will update this
	// object when a parameter is edited

	_params.radar.addChangeListener(new ParamChangeListener());
	_params.calib.addChangeListener(new ParamChangeListener());

	// sample MDS
	
	JButton sampleMdsButton = new JButton("Sample MDS");
	sampleMdsButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e1) {
		    String ss = JOptionPane.showInputDialog
			(_valuePanel, "Input Siggen setting: ");
		    if (ss == null) {
			return;
		    }
		    try {
			Double power = new Double(ss);
			_mdsSiggen = power.doubleValue();
			double testAtten = 
			    _params.calib.testCableAtten.getValue()
			    + _params.calib.couplerAtten.getValue();
			_mdsPower = _mdsSiggen - testAtten;
		    } catch (NumberFormatException e2) {
			JOptionPane.showMessageDialog
			    (_valuePanel,
			     "Bad numerical format - try again",
			     "Bad input",
			     JOptionPane.ERROR_MESSAGE);
			return;
		    }
		    _mdsCount = _aScope.getSampleCount();
		    _setMdsCountLabel(_mdsCount);
		    _setMdsPowerLabel(_mdsPower);
		    _computeCal();
                }
            });

	// unzoom button
	
	_unZoomButton = new JButton("UnZoom");
	_unZoomButton.setEnabled(false);
	_unZoomButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
		    _canvas.setUnzoomed();
		    _canvas.doRepaint();
		    _unZoomButton.setEnabled(false);
                }
            });

	// help

	JButton helpMenu = new JButton("Help");
	// helpMenu.setMnemonic('H');
	helpMenu.setToolTipText("Display help window");
	_infoContent =
	    "<h1>Calibration help info</h1>\n" +
	    "<hr>\n" +
	    "<h2>Setup</h2>\n" +
	    "<ul>\n" +
	    "<li>Set Op Mode to <b>Calibrate</b>.\n" +
	    "<li>Bring up AScope, check that pulse is correctly sampled.\n" +
	    "</ul>\n" +
	    "<hr>\n" +
	    "<h2>MDS - minimum detectable signal</h2>\n" +
	    "<ul>\n" +
	    "<li>Adjust siggen so pulse is barely visibile above noise.\n" +
	    "<li>Hit <b>Sample MDS</b>\n" +
	    "</ul>\n" +
	    "<hr>\n" +
	    "<h2>Calibration table</h2>\n" +
	    "<ul>\n" +
	    "<li>Hit <b>Clear all</b> to clear table\n" +
	    "<li>Set siggen for each setting, hit <b>Sample row</b>\n" +
	    "<li>If you make a mistake, you can resample a given row, " +
	    "just click on the row and repeat the sampling procedure\n" +
	    "<li>Adjust <b>Fit low</b> and <b>Fit high</b> for the linear fit.\n" +
	    "</ul>\n" +
	    "<hr>\n" +
	    "<h2>Save to file</h2>\n" +
	    "<ul>\n" +
	    "<li>When satisfied, hit <b>File --> Save to file</b>\n" +
	    "</ul>\n";

	helpMenu.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
		    InfoFrame info =
			new InfoFrame(getX() + 30, getY() + 30,
				      600, 600);
		    info.setTitle("Info for Calibration");
		    info.setContent(_infoContent);
                }
            });

	// add all to menu bar

	menuBar.add(fileMenu);
	menuBar.add(editButton);
	menuBar.add(sampleMdsButton);
	menuBar.add(_unZoomButton);
	menuBar.add(helpMenu);

    }

    /////////////////////////////////////
    // create cal table

    private JComponent _createCalTable() {
	
        _calTableModel = new CalTableModel();
        _calTable = new CalTable();
	_calTable.setModel(_calTableModel);
	
	_calTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	_calTable.getSelectionModel().addListSelectionListener
	    (new SelectionListener());
	_calTable.setRowSelectionInterval(_tableSelectionRow, _tableSelectionRow);

	_calTable.getColumn("SigGen").setMinWidth(30);
	_calTable.getColumn("dBm").setMinWidth(30);
	_calTable.getColumn("dBZ-1km").setMinWidth(30);
	_calTable.getColumn(_dbzCalRngStr).setMinWidth(30);
	_calTable.getColumn("Count").setMinWidth(30);
	
	_calTable.getColumn("SigGen").setCellRenderer(new CenterRenderer());
	_calTable.getColumn("dBm").setCellRenderer(new CenterRenderer());
	_calTable.getColumn("dBZ-1km").setCellRenderer(new CenterRenderer());
	_calTable.getColumn(_dbzCalRngStr).setCellRenderer(new CenterRenderer());
	_calTable.getColumn("Count").setCellRenderer(new CenterRenderer());

        // Create the scroll pane and add the table to it.
	
	_calTable.setSize();
	JScrollPane tableScroll = new JScrollPane(_calTable);
 	return tableScroll;

    }

    // add labels
    
    private void _addValueLabels() {

	_filePathLabel = new JLabel("Calib path: none");
	_filePathLabel.setBorder(CustomBorder.createSimple(this, 1));
	
	_peakPowerLabel = new JLabel();
	_peakPowerLabel.setBorder(CustomBorder.createSimple(this, 1));
	_setPeakPowerLabel(_params.calib.peakPower.getValue());
	
	_radarConstLabel = new JLabel();
	_radarConstLabel.setBorder(CustomBorder.createSimple(this, 1));
	_radarConst = -150.0;
	_setRadarConstLabel(_radarConst);
	
	_mdsCountLabel = new JLabel();
	_mdsCountLabel.setBorder(CustomBorder.createSimple(this, 1));
	_mdsCount = 0.0;
	_setMdsCountLabel(_mdsCount);
	
	_mdsPowerLabel = new JLabel();
	_mdsPowerLabel.setBorder(CustomBorder.createSimple(this, 1));
	_mdsPower = 0.0;
	_setMdsCountLabel(_mdsPower);
	
	JPanel labelPanel = new JPanel(new GridLayout(2,2,3,0));
	labelPanel.add(_peakPowerLabel);
	labelPanel.add(_radarConstLabel);
	labelPanel.add(_mdsCountLabel);
	labelPanel.add(_mdsPowerLabel);
	
	_valuePanel.add(_filePathLabel, BorderLayout.NORTH);
	_valuePanel.add(labelPanel, BorderLayout.SOUTH);

    }

    // add buttons
    
    private void _addTableButtons() {

	// sample row button
	
	_sampleTableRowButton = new JButton("Sample row");
	_sampleTableRowButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
		    Double count = new Double(_aScope.getSampleCount());
		    _calTable.setValueAt(count, _tableSelectionRow, countCol);
		    _computeCal();
		    if (_tableSelectionRow < _calTable.getRowCount() - 1) {
			_tableSelectionRow++;
		    }
		    _calTable.setRowSelectionInterval(_tableSelectionRow,
						      _tableSelectionRow);
                }
            });

	// clear row

	_clearTableRowButton = new JButton("Clear row");
	_clearTableRowButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
		    _calTable.setValueAt(null, _tableSelectionRow, countCol);
		    _computeCal();
                }
            });
	
	_clearTableAllButton = new JButton("Clear all");
	_clearTableAllButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
		    _updateFromParams();
		    _updateTable();
		    // _computeCal();
		    _setCalibFilePath("none");
		    setCalibAvail(false);
		    _tableSelectionRow = 0;
		    _calTable.setRowSelectionInterval(_tableSelectionRow,
						      _tableSelectionRow);
                }
            });

	// populate panel
	
	JPanel buttonPanel = new JPanel();
	buttonPanel.add(_sampleTableRowButton);
	buttonPanel.add(_clearTableRowButton);
	buttonPanel.add(_clearTableAllButton);

	_tablePanel.add(buttonPanel, BorderLayout.NORTH);

    }

    ///////////////////////////////////
    // update from parameters

    private void _updateFromParams() {

	// from the radar params
	
	_siteName = _params.radar.siteName.getValue();
	_siteNum = _params.radar.siteNum.getValue();
	_userName = _params.radar.userName.getValue();
	_freqGhz = _params.radar.frequency.getValue();
	_horizBeamWidth = _params.radar.horizBeamWidth.getValue();
	_vertBeamWidth = _params.radar.vertBeamWidth.getValue();
	_antGain = _params.radar.antGain.getValue();

	// from the calib params
	
	_peakPower = _params.calib.peakPower.getValue();
	_setPeakPowerLabel(_peakPower);
	_pulseWidth = _params.calib.pulseWidth.getValue();

	_waveguideLoss = _params.calib.waveguideLoss.getValue();
	_radomeLoss = _params.calib.radomeLoss.getValue();
	_receiverLoss = _params.calib.receiverLoss.getValue();
	_testCableAtten =  _params.calib.testCableAtten.getValue();
	_couplerAtten = _params.calib.couplerAtten.getValue();

	_nPointsTable = _params.calib.nPointsTable.getValue();
	_minDbz = _params.calib.minDbz.getValue();
	_deltaDbz = _params.calib.deltaDbz.getValue();
	_maxDbz = _minDbz + (_nPointsTable - 1) * _deltaDbz;
	double meanDbz = (_minDbz + _maxDbz) / 2.0;
	_fitDbzLow = _params.calib.fitDbzLow.getValue();
	if (_fitDbzLow > meanDbz) {
	    _fitDbzLow = meanDbz;
	    _params.calib.fitDbzLow.setValue(_fitDbzLow);
	}
	_fitDbzHigh = _params.calib.fitDbzHigh.getValue();
	if (_fitDbzHigh < meanDbz) {
	    _fitDbzHigh = meanDbz;
	    _params.calib.fitDbzHigh.setValue(_fitDbzHigh);
	}

	if (_fitDbzLowSlider != null) {
	    _fitDbzLowSlider.setMinimum((int) _minDbz);
	    _fitDbzLowSlider.setMaximum((int) meanDbz);
	    _fitDbzLowSlider.setValue((int) _fitDbzLow);
	}

	if (_fitDbzHighSlider != null) {
	    _fitDbzHighSlider.setMinimum((int) meanDbz);
	    _fitDbzHighSlider.setMaximum((int) _maxDbz);
	    _fitDbzHighSlider.setValue((int) _fitDbzHigh);
	}
	
	// compute derived values

	_freqHz = _freqGhz * 1.0e9;
	_lambda = _lightSpeed / _freqHz;

	if (_freqGhz > 8.0) {
	    _atmosAtten = 0.024 * 1.0e-3; // X-band, dB/m
	} else if (_freqGhz < 4.0) {
	    _atmosAtten = 0.012 * 1.0e-3; // S-band, dB/m
	} else {
	    _atmosAtten = 0.014 * 1.0e-3; // C-band, dB/m
	}

	_computeRadarConst();

	_calRng = _params.calib.calRange.getValue();
	String prevDbzCalRngStr = _dbzCalRngStr;
	_dbzCalRngStr = "dBZ-" + NFormat.f0.format(_calRng) + "km";
	_rangeCorrCalRng =
	    20.0 * MathUtils.log10(1000.0 * _calRng);
	_rangeCorr1km = 20.0 * MathUtils.log10(1000.0);
	_atmosAttenCalRng = _atmosAtten * (1000.0 * _calRng);
	_atmosAtten1km = _atmosAtten * 1000.0;
	_corr1km2CalRng = ((_rangeCorrCalRng - _rangeCorr1km) +
			   (_atmosAttenCalRng - _atmosAtten1km));

	if (_calTable != null) {
	    _calTable.getColumn(prevDbzCalRngStr).setHeaderValue(_dbzCalRngStr);
	    invalidate();
	    validate();
	}

	// init other variables

	_mdsCount = 0.0;
	_mdsSiggen = 0.0;
	_mdsPower = 0.0;
	_calSlope = 0.0;
	_calOffset1km = 0.0;
	_calOffsetCalRng = 0.0;
	
    }
	
    ///////////////////////////////////
    // update the table
    
    private void _updateTable() {
	
	// set up the table
	
	_calTableModel.setTableFromParams();
	_calTable.setSize();
	_canvas.doRepaint();
	
	// update the gui
	
	validate();
	
    }

    private void _setCalibFilePath(String path) {
	_params.calib.filePath.setValue(path);
	_filePathLabel.setText("Calib file :  " + path);
    }

    private void _setPeakPowerLabel(double power) {
	if (_peakPowerLabel != null) {
	    _peakPowerLabel.setText
		(new String("Peak pwr (dBm) : " + NFormat.f2.format(power)));
	}
    }

    private void _setRadarConstLabel(double val) {
	if (_radarConstLabel != null) {
	    _radarConstLabel.setText
		(new String("Rad const (dB) : " + NFormat.f2.format(val)));
	}
    }

    private void _setMdsCountLabel(double val) {
	if (_mdsCountLabel != null) {
	    _mdsCountLabel.setText
		(new String("MDS count : " + NFormat.f1.format(val)));
	}
    }
    
    private void _setMdsPowerLabel(double val) {
	if (_mdsPowerLabel != null) {
	    _mdsPowerLabel.setText
		(new String("MDS power (dBm) : " + NFormat.f2.format(val)));
	}
    }
    
    private void _computeRadarConst() {
	
	double antGainPower = Math.pow(10.0, _antGain / 10.0);
	double gainSquared = antGainPower * antGainPower;
	double lambdaSquared = _lambda * _lambda;
	double pulseMeters = _pulseWidth * 1.0e-6 * _lightSpeed;

	double hBeamWidthRad = Math.toRadians(_horizBeamWidth);
	double vBeamWidthRad = Math.toRadians(_vertBeamWidth);
	
	double num = (_piCubed * pulseMeters * gainSquared *
		      hBeamWidthRad * vBeamWidthRad * _kSquared * 1.0e-18);
	double denom = (1024.0 * Math.log(2.0) * lambdaSquared);
	
	double factor = num / denom;
	
	_radarConst = (10.0 * MathUtils.log10(factor)
		       - 2.0 * _waveguideLoss - _radomeLoss - _receiverLoss);
	
	_setRadarConstLabel(_radarConst);
	
	if (_params.verbose.getValue() == true) {
	    System.out.println("Computing radar const:");
	    System.out.println("  pulseMeters: " + pulseMeters);
	    System.out.println("  lambda: " + _lambda);
	    System.out.println("  antGainPower: " + antGainPower);
	    System.out.println("  gainSquared: " + gainSquared);
	    System.out.println("  hBeamWidthRad: " + hBeamWidthRad);
	    System.out.println("  vBeamWidthRad: " + vBeamWidthRad);
	    System.out.println("  radConstDb: " + _radarConst);
	}
	
    }

    // compute calibration
    
    private void _computeCal() {
	
	// load up arrays
	
	_posnArray.clear();
	_siggenArray.clear();
	_dbmArray.clear();
	_dbz1kmArray.clear();
	_dbzCalRngArray.clear();
	_countArray.clear();
	
	for (int ii = 0; ii < _calTable.getRowCount(); ii++) {
	    Integer posn = new Integer(ii);
	    Double siggen = (Double) _calTable.getValueAt(ii, siggenCol);
	    Double dbm = (Double) _calTable.getValueAt(ii, dbmCol);
	    Double dbz1km = (Double) _calTable.getValueAt(ii, dbz1kmCol);
	    Double dbzCalRng = (Double) _calTable.getValueAt(ii, dbzCalRngCol);
	    Double count = (Double) _calTable.getValueAt(ii, countCol);
	    if (count != null) {
		_posnArray.add(posn);
		_siggenArray.add(siggen);
		_dbmArray.add(dbm);
		_dbz1kmArray.add(dbz1km);
		_dbzCalRngArray.add(dbzCalRng);
		_countArray.add(count);
	    }
	}
	
	if (getNPoints() < 2) {
	    _canvas.doRepaint();
	    return;
	}

	// compute linear fits at both 1km and CalRng range,
	// within the set low and high limits
	
	LinearFitResults results = new LinearFitResults();
	
	ArrayList countArray = new ArrayList();
	ArrayList dbzCalRngArray = new ArrayList();
	ArrayList dbz1kmArray = new ArrayList();
	
	for (int ii = 0; ii < _countArray.size(); ii++) {
	    if (ii < _countArray.size() &&
		ii < _dbz1kmArray.size() &&
		ii < _dbzCalRngArray.size()) {
		Double count = (Double) _countArray.get(ii);
		Double dbzCalRng = (Double) _dbzCalRngArray.get(ii);
		Double dbz1km = (Double) _dbz1kmArray.get(ii);
		if ((dbzCalRng.doubleValue() >= _fitDbzLow) &&
		    (dbzCalRng.doubleValue() <= _fitDbzHigh)) {
		    countArray.add(count);
		    dbz1kmArray.add(dbz1km);
		    dbzCalRngArray.add(dbzCalRng);
		}
	    }
	}

	_computeLinearFit(dbz1kmArray, countArray, results);
	_calSlope = results.slope;
	_calOffset1km = results.offset;

	_computeLinearFit(dbzCalRngArray, countArray, results);
	_calOffsetCalRng = results.offset;

	// redraw
	
	_canvas.doRepaint();
	
    }

    private void _computeLinearFit(ArrayList xxArray,
				   ArrayList yyArray,
				   LinearFitResults results)
	
    {
	
	int npts = Math.min(xxArray.size(), yyArray.size());
	
	// accumumate regression counters
	
	double sumx = 0.0;
	double sumy = 0.0;
	double sumx2 = 0.0;
	double sumy2 = 0.0;
	double sumxy = 0.0;
	double nn = 0.0;
	
	for (int ii = 0; ii < npts; ii++) {
	    
	    double xx = ((Double) xxArray.get(ii)).doubleValue();
	    double yy = ((Double) yyArray.get(ii)).doubleValue();

	    sumx += xx;
	    sumx2 += xx * xx;
	    sumy += yy;
	    sumy2 += yy * yy;
	    sumxy += xx * yy;
	    nn++;
	    
	}

	// compute mean vals
	
	double meany = sumy / nn;
	double meanx = sumx / nn;
	
	// compute y-on-x slope
	
	double num = nn * sumxy - sumx * sumy;
	double denom = nn * sumx2 - sumx * sumx;
	double slope_y_on_x;
  
	if (denom != 0.0) {
	    slope_y_on_x = num / denom;
	} else {
	    slope_y_on_x = 0.0;
	}
	
	// compute x-on-y slope
	
	denom = nn * sumy2 - sumy * sumy;
	double slope_x_on_y;
	
	if (denom != 0.0) {
	    slope_x_on_y = num / denom; // mm/s
	} else {
	    slope_x_on_y = 0.0;
	}

	// compute mean slope
	
	if (slope_y_on_x != 0.0 && slope_x_on_y != 0.0) {
	    results.slope = (slope_y_on_x + 1.0 / slope_x_on_y) / 2.0;
	} else if (slope_y_on_x != 0.0) {
	    results.slope = slope_y_on_x;
	} else if (slope_x_on_y != 0.0) {
	    results.slope = 1.0 / slope_x_on_y;
	} else {
	    results.slope = 0.0;
	}

	results.offset = meany - results.slope * meanx;

    }

    // round to given precision
    
    public double round(double val, double precision) {
	double fac = 1.0 / precision;
	if (val >= 0) {
	    return ((int) (val * fac + 0.5)) / fac;
	} else {
	    return ((int) (val * fac - 0.5)) / fac;
	}
    }
	
    // right justify a string
    
    public String rJust(String in, int width) {
	if (in.length() >= width) {
	    return in;
	}
	StringBuffer buf = new StringBuffer();
	int extraNeeded = width - in.length();
	for (int i = 0; i < extraNeeded; i++) {
	    buf.append(" ");
	}
	buf.append(in);
	return buf.toString();
    }

    ////////////////////////////////////////////
    // read in XML

    private void _readXmlFile(String calibPath)
    {
	
	File calibFile = new File(calibPath);
	if (!calibFile.exists()) {
	    if (_params.debug.getValue()) {
		System.err.println("Calib file does not exist: "
				   + calibPath);
	    }
	    return;
	}
	
	SAXBuilder builder = new SAXBuilder();
	Document doc;
	
	try {
	    doc = builder.build(calibPath);
	    // If there are no well-formedness errors, 
	    // then no exception is thrown
	}
	catch (JDOMException e) { 
	    // indicates a well-formedness error
	    if (_params.debug.getValue()) {
		System.err.println("ERROR - Calibrate:_readXmlFile");
		System.err.println("  Parsing XML calib file");
		System.err.println("  " + calibPath + " is not well-formed.");
		System.err.println("  " + e.getMessage());
		e.printStackTrace();
	    }
	    return;
	}
	catch (IOException e) { 
	    // indicates a read error
	    if (_params.debug.getValue()) {
		System.err.println("ERROR - Calibrate:_readXmlFile");
		System.err.println("  Parsing XML calib file");
		System.err.println("  Cannot read: " + calibPath);
		System.err.println("  " + e.getMessage());
		e.printStackTrace();
	    }
	    return;
	}

	if (!_setFromXmlDoc(calibPath, doc)) {
	    // error - show error dialog
	    String errStr =
		"Calibrate:_readXmlFile: \n" +
		"Bad calib file: " + calibPath + "\n" +
		"No calibration available" + "\n";
	    JOptionPane.showMessageDialog(_canvas, errStr,
					  "Bad calibration file",
					  JOptionPane.ERROR_MESSAGE);
	    _setCalibFilePath("none");
	    setCalibAvail(false);
	    return;
	}
	setCalibAvail(true);

    } // readFile
    
    private boolean _setFromXmlDoc(String calibPath, Document doc)
    {
	
	
	Element root = doc.getRootElement();
	Iterator itRoot = root.getChildren().iterator();
	try {
	    _loadFromXml(itRoot);
	}
	catch (NumberFormatException e) {
	    // indicates a read error
	    if (_params.debug.getValue()) {
		System.err.println("ERROR - Calibrate:_setFromXmlDoc");
		System.err.println("  Parsing XML calib file");
		System.err.println("  Bad number format: " + calibPath);
		System.err.println("  " + e.getMessage());
		e.printStackTrace();
	    }
	    return false;
	}
	_calTableModel.setTableFromData();
	_calTable.setSize();
	_canvas.doRepaint();
	validate();

	return true;
	
    } // _setFromXmlDoc()
    
    private void _loadFromXml(Iterator itRoot) throws NumberFormatException
    {
	    
	// clear arrays, ready to add points
	    
	_posnArray.clear();
	_siggenArray.clear();
	_dbmArray.clear();
	_dbz1kmArray.clear();
	_dbzCalRngArray.clear();
	_countArray.clear();
	    
	while (itRoot.hasNext()) {
		
	    Element elRoot = (Element) itRoot.next();
		
	    if (elRoot.getName().equals("point")) {
		Iterator itPoint = elRoot.getChildren().iterator();
		while (itPoint.hasNext()) {
		    Element elPoint = (Element) itPoint.next();
		    if (elPoint.getName().equals("posn")) {
			Integer posn = new Integer(elPoint.getText());
			_posnArray.add(posn);
		    } else if (elPoint.getName().equals("siggen")) {
			Double siggen = new Double(elPoint.getText());
			_siggenArray.add(siggen);
		    } else if (elPoint.getName().equals("dbm")) {
			Double dbm = new Double(elPoint.getText());
			_dbmArray.add(dbm);
		    } else if (elPoint.getName().equals("dbz1km")) {
			Double dbz1km = new Double(elPoint.getText());
			_dbz1kmArray.add(dbz1km);
		    } else if (elPoint.getName().equals("dbzCalRng")) {
			Double dbzCalRng = new Double(elPoint.getText());
			_dbzCalRngArray.add(dbzCalRng);
		    } else if (elPoint.getName().equals("count")) {
			Double count = new Double(elPoint.getText());
			_countArray.add(count);
		    }
			
		} // while (itRoot.hasNext()) 
	    } else {
		if (elRoot.getName().equals("siteName")) {
		    _siteName = new String(elRoot.getText());
		} else if (elRoot.getName().equals("siteNum")) {
		    _siteNum = Integer.parseInt(elRoot.getText());
		} else if (elRoot.getName().equals("userName")) {
		    _userName = new String(elRoot.getText());
		} else if (elRoot.getName().equals("freqGhz")) {
		    _freqGhz = Double.parseDouble(elRoot.getText());
		} else if (elRoot.getName().equals("freqHz")) {
		    _freqHz = Double.parseDouble(elRoot.getText());
		} else if (elRoot.getName().equals("lambda")) {
		    _lambda = Double.parseDouble(elRoot.getText());
		} else if (elRoot.getName().equals("horizBeamWidth")) {
		    _horizBeamWidth = Double.parseDouble(elRoot.getText());
		} else if (elRoot.getName().equals("vertBeamWidth")) {
		    _vertBeamWidth = Double.parseDouble(elRoot.getText());
		} else if (elRoot.getName().equals("antGain")) {
		    _antGain = Double.parseDouble(elRoot.getText());
		} else if (elRoot.getName().equals("peakPower")) {
		    _peakPower = Double.parseDouble(elRoot.getText());
		} else if (elRoot.getName().equals("pulseWidth")) {
		    _pulseWidth = Double.parseDouble(elRoot.getText());
		} else if (elRoot.getName().equals("waveguideLoss")) {
		    _waveguideLoss = Double.parseDouble(elRoot.getText());
		} else if (elRoot.getName().equals("radomeLoss")) {
		    _radomeLoss = Double.parseDouble(elRoot.getText());
		} else if (elRoot.getName().equals("receiverLoss")) {
		    _receiverLoss = Double.parseDouble(elRoot.getText());
		} else if (elRoot.getName().equals("testCableAtten")) {
		    _testCableAtten = Double.parseDouble(elRoot.getText());
		} else if (elRoot.getName().equals("couplerAtten")) {
		    _couplerAtten = Double.parseDouble(elRoot.getText());
		} else if (elRoot.getName().equals("nPointsTable")) {
		    _nPointsTable = Integer.parseInt(elRoot.getText());
		} else if (elRoot.getName().equals("minDbz")) {
		    _minDbz = Double.parseDouble(elRoot.getText());
		} else if (elRoot.getName().equals("deltaDbz")) {
		    _deltaDbz = Double.parseDouble(elRoot.getText());
		} else if (elRoot.getName().equals("fitDbzLow")) {
		    _fitDbzLow = Double.parseDouble(elRoot.getText());
		} else if (elRoot.getName().equals("fitDbzHigh")) {
		    _fitDbzHigh = Double.parseDouble(elRoot.getText());
		} else if (elRoot.getName().equals("atmosAtten")) {
		    _atmosAtten = Double.parseDouble(elRoot.getText());
		} else if (elRoot.getName().equals("radarConst")) {
		    _radarConst = Double.parseDouble(elRoot.getText());
		} else if (elRoot.getName().equals("mdsCount")) {
		    _mdsCount = Double.parseDouble(elRoot.getText());
		} else if (elRoot.getName().equals("mdsPower")) {
		    _mdsPower = Double.parseDouble(elRoot.getText());
		} else if (elRoot.getName().equals("mdsSiggen")) {
		    _mdsSiggen = Double.parseDouble(elRoot.getText());
		} else if (elRoot.getName().equals("calSlope")) {
		    _calSlope = Double.parseDouble(elRoot.getText());
		} else if (elRoot.getName().equals("calOffset1km")) {
		    _calOffset1km = Double.parseDouble(elRoot.getText());
		} else if (elRoot.getName().equals("calOffsetCalRng")) {
		    _calOffsetCalRng = Double.parseDouble(elRoot.getText());
		} 
	    }
		
	} // while (itRoot.hasNext()) 

    } // _loadFromXml

    ////////////////////////////////////////////////
    // read calib from an XML buffer
    
    public boolean setFromXmlBuffer(ByteBuffer buf) {
	
	ByteArrayInputStream in = new ByteArrayInputStream(buf.array());
	SAXBuilder builder = new SAXBuilder();
	Document doc;
	
	try {
	    doc = builder.build(in);
	    // If there are no well-formedness errors, 
	    // then no exception is thrown
	}
	// JDOMException indicates a well-formedness error
	catch (JDOMException e) { 
	    System.err.println("ERROR - Calibrate.setFromXmlBuffer");
	    System.err.println("  Parsing XML buffer");
	    System.err.println("  Buffer is not well-formed.");
	    System.err.println("  " + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
	// IOException indicates reading error
  	catch (IOException e) { 
	    System.err.println("ERROR - Calibrate.setFromXmlBuffer");
	    System.err.println("  Parsing XML buffer");
	    System.err.println("  Buffer is not well-formed.");
  	    System.err.println("  " + e.getMessage());
  	    e.printStackTrace();
  	    return false;
  	}
	
	if (!_setFromXmlDoc("Remote site calib", doc)) {
	    System.err.println("ERROR - setFromXmlBuffer");
	    return false;
	}
	
	return true;
	
    }

    ////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////

    // class for linear fit results

    private class LinearFitResults {
	public double slope;
	public double offset;
    }

    // listeners for sliders for dbz low and high

    private class FitDbzLowSliderListener implements ChangeListener {
	public void stateChanged(ChangeEvent e) {
	    _fitDbzLow = _fitDbzLowSlider.getValue();
 	    _params.calib.fitDbzLow.setValue(_fitDbzLow);
	    _computeCal();
	    _canvas.doRepaint();
	}
    }

    private class FitDbzHighSliderListener implements ChangeListener {
	public void stateChanged(ChangeEvent e) {
	    _fitDbzHigh = _fitDbzHighSlider.getValue();
 	    _params.calib.fitDbzHigh.setValue(_fitDbzHigh);
	    _computeCal();
	    _canvas.doRepaint();
	}
    }

    ///////////////////////////////////////////////////////
    // MoveResizeListener class

    private class MoveResizeListener extends ComponentAdapter
    {

	public void componentMoved(ComponentEvent e) {
	    Parameters params = Parameters.getInstance();
	    int paramX = params.calib.xx.getValue();
	    int paramY = params.calib.yy.getValue();
	    if (paramX != getX() || paramY != getY()) {
		params.calib.xx.setValue(getX());
		params.calib.yy.setValue(getY());
	    }
	}
	
	public void componentResized(ComponentEvent e) {
	    Parameters params = Parameters.getInstance();
	    int paramWidth = params.calib.width.getValue();
	    int paramHeight = params.calib.height.getValue();
	    if (paramWidth != getWidth() || paramHeight != getHeight()) {
		params.calib.width.setValue(getWidth());
		params.calib.height.setValue(getHeight());
	    }
	}
	
	public void componentShown(ComponentEvent e) {
	    Parameters params = Parameters.getInstance();
	    if (params.calib.startVisible.getValue() == false) {
		params.calib.startVisible.setValue(true);
	    }
	}
	
	public void componentHidden(ComponentEvent e) {
	    Parameters params = Parameters.getInstance();
	    if (params.calib.startVisible.getValue() == true) {
		params.calib.startVisible.setValue(false);
	    }
	}
	
    }

    /////////////////////////////////////
    // cal table class

    private class CalTable extends JTable
    {
	public void setSize() {
	    int ht = _params.calib.tableRowHt.getValue() * (getRowCount() + 1);
	    setPreferredScrollableViewportSize(new Dimension(300, ht));
	}
    }

    /////////////////////////////////////////////////////////
    // cal data model class

    private class CalTableModel extends DefaultTableModel {
	
	public CalTableModel() {
	    
	    addColumn("SigGen");
	    addColumn("dBm");
	    addColumn("dBZ-1km");
	    addColumn(_dbzCalRngStr);
	    addColumn("Count");
	    _updateFromParams();
	    setTableFromParams();
	    
	}
	
	// set table from parameters
	
	public void setTableFromParams() {

	    setRowCount(_nPointsTable);
	    
	    // Table computed for a range of 1000m

	    double testAtten =  _params.calib.testCableAtten.getValue()
		+ _params.calib.couplerAtten.getValue();
	    
	    double xmt = _peakPower + _radarConst;
	    
	    for (int ii = 0; ii < getRowCount(); ii++) {
		
		double dbzCalRng = _minDbz + ii * _deltaDbz;
		double dbz1km = dbzCalRng - _corr1km2CalRng;
		double dbm = dbzCalRng + xmt - _rangeCorrCalRng - _atmosAttenCalRng;
		double siggen = dbm + testAtten;

		if (_params.calib.roundSiggen.getValue()) {

		    double roundVal = _params.calib.siggenRoundValue.getValue();
		    double siggenRounded = round(siggen, roundVal);
		    double corr = siggenRounded - siggen;
		    
		    dbz1km += corr;
		    dbzCalRng += corr;
		    siggen += corr;
		    dbm += corr;

		}
		
		dbz1km = round(dbz1km, 0.01);
		dbzCalRng = round(dbzCalRng, 0.01);
		siggen = round(siggen, 0.01);
		dbm = round(dbm, 0.01);

		setValueAt(new Double(siggen), ii, siggenCol);
		setValueAt(new Double(dbm), ii, dbmCol);
		setValueAt(new Double(dbz1km), ii, dbz1kmCol);
		setValueAt(new Double(dbzCalRng), ii, dbzCalRngCol);
		setValueAt(null, ii, countCol);

	    }

	}

	// set table from data members
	
	public void setTableFromData() {
	    
	    setRowCount(getNPoints());
	    for (int ii = 0; ii < getRowCount(); ii++) {
		setValueAt(new Double(getSiggenVal(ii)), ii, siggenCol);
		setValueAt(new Double(getDbmVal(ii)), ii, dbmCol);
		setValueAt(new Double(getDbz1kmVal(ii)), ii, dbz1kmCol);
		setValueAt(new Double(getDbzCalRngVal(ii)), ii, dbzCalRngCol);
		setValueAt(new Double(getCountVal(ii)), ii, countCol);
	    }
	    
	    _setPeakPowerLabel(_peakPower);
	    _setRadarConstLabel(_radarConst);
	    _setMdsCountLabel(_mdsCount);
	    _setMdsPowerLabel(_mdsPower);

	}

        // Set data cells editable
        
	public boolean isCellEditable(int row, int col) {
	    return false;
        }
	
        public void print() {
            for (int i=0; i < getRowCount(); i++) {
                System.out.print("    row " + i + ":");
                for (int j=0; j < getColumnCount(); j++) {
                    System.out.print("  " + getValueAt(i, j));
                    System.out.print("  class is: " + getValueAt(i, j).getClass());
                }
                System.out.println();
            }
            System.out.println("--------------------------");
        }

    } // class CalTableModel

    /////////////////////////////////////////////////////////
    // renderer and editor for table cells
    
    private class CenterRenderer extends DefaultTableCellRenderer {
	CenterRenderer() {
	    setHorizontalAlignment(JLabel.CENTER);
	}
    }

    /////////////////////////////////////////////////////////
    // selection listener for table
    
    private class SelectionListener implements ListSelectionListener {
	public void valueChanged(ListSelectionEvent e) {
	    //Ignore extra messages.
	    if (e.getValueIsAdjusting()) return;
	    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
	    if (lsm.isSelectionEmpty()) {
		_tableSelectionActive = false;
	    } else {
		_tableSelectionActive = true;
		_tableSelectionRow = lsm.getMinSelectionIndex();
	    }
	}
    }

    ///////////////////////////////////////////////////////
    // editor apply listener
    //
    // Called by functions which have updated the params

    private class ParamChangeListener implements CollectionChangeListener {
	public void reactToChange() {
	    _updateFromParams();
	    _updateTable();
	    _setTitleStr();
	}
    }

    ////////////////////////////////////////////////////
    // Inner classes for actions
    ////////////////////////////////////////////////////

    /////////////////////////////////////////////////////
    // file folder action class

    private class FileFolderAction extends AbstractAction
    {
	public FileFolderAction(String label, ImageIcon icon)
	{
	    super(label, icon);
	}
	
	public void actionPerformed(ActionEvent event)
	{
 	    JFileChooser chooser = new JFileChooser();
	    File dir = new File(_params.calib.fileFolder.getValue());
	    chooser.setCurrentDirectory(dir);
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
 	    int state = chooser.showDialog(null, "Accept");
 	    File folder = chooser.getSelectedFile();
 	    if (folder != null &&
 		state == JFileChooser.APPROVE_OPTION) {
		_params.calib.fileFolder.setValue(folder.getPath());
 	    }
	}
    }

    //////////////////////////////////////////////////////////
    // file read action class
    
    private class FileReadAction extends AbstractAction
    {
	public FileReadAction(String label, ImageIcon icon)
	{
	    super(label, icon);
	}
	
	public void actionPerformed(ActionEvent event)
	{
 	    JFileChooser chooser = new JFileChooser();
	    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    File toRead = new File(_params.calib.fileFolder.getValue());
	    chooser.setCurrentDirectory(toRead);
	    chooser.setFileFilter(new XmlCalFilter());
 	    int state = chooser.showDialog(null, "Read");
 	    File calibFile = chooser.getSelectedFile();
 	    if (calibFile != null && state == JFileChooser.APPROVE_OPTION) {
		_readXmlFile(calibFile.getPath());
		_setCalibFilePath(calibFile.getPath());
	    }
	} // actionPerformed
	
	////////////////////////////
	// inner class - cal filter
	    
	private class XmlCalFilter extends javax.swing.filechooser.FileFilter {
	    public boolean accept(File f) {
		if (f.isDirectory()) {
		    return false;
		}
		if (f.getPath().endsWith("cal.xml")) {
		    return true;
		} else {
		    return false;
		}
	    }
	    public String getDescription() {
		return "Calibration XML files (*.cal.xml)";
	    }
	}
	
    } // FileReadAction

    //////////////////////////////////////////////////////
    // file save action class

    private class FileSaveAction extends AbstractAction
    {
	public FileSaveAction(String label, ImageIcon icon)
	{
	    super(label, icon);
	}
	
	public void actionPerformed(ActionEvent event)
	{
	    
	    // create directory if it does not exist
	    
	    String calDir = _params.calib.fileFolder.getValue();
	    File calDirFile = new File(calDir);
	    if (!calDirFile.exists()) {
		boolean success = calDirFile.mkdirs();
		if (!success) {
		    // Directory creation failed
		    String errStr =
			"Cannot create directory for calib files\n" +
			"  Dir: " + calDir + "\n";
		    JOptionPane.showMessageDialog(_canvas, errStr,
						  "Cannot create cal file directory",
						  JOptionPane.ERROR_MESSAGE);
		    return;
		}
	    }
	
	    // compute output time string
	    
	    Calendar cal = TimeManager.getCal();
	    String timeStr = new
		String(NFormat.i4.format(cal.get(Calendar.YEAR)) +
		       NFormat.i2.format(cal.get(Calendar.MONTH) + 1) +
		       NFormat.i2.format(cal.get(Calendar.DAY_OF_MONTH)) +
		       "_" +
		       NFormat.i2.format(cal.get(Calendar.HOUR_OF_DAY)) +
		       NFormat.i2.format(cal.get(Calendar.MINUTE)) +
		       NFormat.i2.format(cal.get(Calendar.SECOND)));

	    // write text file
	    
	    _writeTextFile(calDir, cal, timeStr);
	    
	    // write XML file
	    
	    String xmlFilePath = _writeXmlFile(calDir, cal, timeStr);
	    
	    // set the file path in the params
	    
	    if (xmlFilePath != null) {
		_setCalibFilePath(xmlFilePath);
		setCalibAvail(true);
	    }

	}
	    
	private String _writeXmlFile(String calDir, Calendar cal, String timeStr)
	{
	    
	    // compute xml output path from time
	
	    String xmlFilePath = new String(calDir + "/" + timeStr + ".cal.xml");
	    if (_params.debug.getValue()) {
		System.out.println("CAL Xml file path: " + xmlFilePath);
	    }
	    
	    // open output file
	    
	    FileWriter writer;
	    try {
		writer = new FileWriter(xmlFilePath);
	    } catch ( IOException e) {
		// show error dialog
		String errStr =
		    "Problem opening XML calib file for writing\n" +
		    "  File: " + xmlFilePath + "\n" + e;
		JOptionPane.showMessageDialog(_canvas, errStr,
					      "Cannot open XML calib file for writing",
					      JOptionPane.ERROR_MESSAGE);
		return null;
	    
	    }
	    PrintWriter out = new PrintWriter(writer);
	    
	    out.println("<?xml version=\"1.0\" " +
			"encoding=\"ISO-8859-1\" " +
			"standalone=\"yes\"?>");

	    out.println("<!DOCTYPE calibration [");

	    out.println(" <!ELEMENT calibration (point*) >");
	    out.println(" <!ELEMENT point (posn, siggen, dbm, dbz1km, dbzCalRng, count) >");
	    out.println(" <!ELEMENT posn (#PCDATA)>");
	    out.println(" <!ELEMENT siggen (#PCDATA)>");
	    out.println(" <!ELEMENT dbm (#PCDATA)>");
	    out.println(" <!ELEMENT dbz1km (#PCDATA)>");
	    out.println(" <!ELEMENT dbzCalRng (#PCDATA)>");
	    out.println(" <!ELEMENT count (#PCDATA)>");
	    
	    out.println("]>");
	    out.println("");
	
	    out.println("<calibration>");
	    out.println("");

	    out.println("  <siteName>" + _siteName + "</siteName>");
	    out.println("  <siteNum>" + _siteNum + "</siteNum>");
	    out.println("  <userName>" + _userName + "</userName>");
	    out.println("  <year>" + NFormat.i4.format(cal.get(Calendar.YEAR)) +
			"</year>");
	    out.println("  <month>" + NFormat.i2.format(cal.get(Calendar.MONTH) + 1) +
			"</month>");
	    out.println("  <day>" + NFormat.i2.format(cal.get(Calendar.DAY_OF_MONTH)) +
			"</day>");
	    out.println("  <hour>" + NFormat.i2.format(cal.get(Calendar.HOUR_OF_DAY)) +
			"</hour>");
	    out.println("  <min>" + NFormat.i2.format(cal.get(Calendar.MINUTE)) +
			"</min>");
	    out.println("  <sec>" + NFormat.i2.format(cal.get(Calendar.SECOND)) +
			"</sec>");
	    out.println("");
	    
	    out.println("  <freqGhz>" + _freqGhz + "</freqGhz>");
	    out.println("  <freqHz>" + _freqHz + "</freqHz>");
	    out.println("  <lambda>" + round(_lambda, 0.0001) + "</lambda>");
	    out.println("  <horizBeamWidth>" + _horizBeamWidth + "</horizBeamWidth>");
	    out.println("  <vertBeamWidth>" + _vertBeamWidth + "</vertBeamWidth>");
	    out.println("  <antGain>" + _antGain + "</antGain>");
	    out.println("  <peakPower>" + _peakPower + "</peakPower>");
	    out.println("  <pulseWidth>" + _pulseWidth + "</pulseWidth>");
	    out.println("  <waveguideLoss>" + _waveguideLoss + "</waveguideLoss>");
	    out.println("  <radomeLoss>" + _radomeLoss + "</radomeLoss>");
	    out.println("  <receiverLoss>" + _receiverLoss + "</receiverLoss>");
	    out.println("  <testCableAtten>" + _testCableAtten + "</testCableAtten>");
	    out.println("  <couplerAtten>" + _couplerAtten + "</couplerAtten>");
	    out.println("  <nPointsTable>" + _nPointsTable + "</nPointsTable>");
	    out.println("  <minDbz>" + _minDbz + "</minDbz>");
	    out.println("  <deltaDbz>" + _deltaDbz + "</deltaDbz>");
	    out.println("  <fitDbzLow>" + _fitDbzLow + "</fitDbzLow>");
	    out.println("  <fitDbzHigh>" + _fitDbzHigh + "</fitDbzHigh>");
	    out.println("  <atmosAtten>" + _atmosAtten + "</atmosAtten>");
	    out.println("  <radarConst>" + round(_radarConst, 0.0001) + "</radarConst>");
	    out.println("");
	    
	    out.println("  <mdsCount>" + _mdsCount + "</mdsCount>");
	    out.println("  <mdsPower>" + _mdsPower + "</mdsPower>");
	    out.println("  <mdsSiggen>" + _mdsSiggen + "</mdsSiggen>");
	    out.println("  <calSlope>" + round(_calSlope, 0.0001) + "</calSlope>");
	    out.println("  <calOffset1km>" + round(_calOffset1km, 0.0001) + "</calOffset1km>");
	    out.println("  <calOffsetCalRng>" + round(_calOffsetCalRng, 0.0001) + "</calOffsetCalRng>");
	    out.println("");
	    
	    for (int ii = 0; ii < getNPoints(); ii++) {
		out.println("  <point>");
		out.println("    <posn>" + getPosnVal(ii) + "</posn>");
		out.println("    <siggen>" + getSiggenVal(ii) + "</siggen>");
		out.println("    <dbm>" + getDbmVal(ii) + "</dbm>");
		out.println("    <dbz1km>" + getDbz1kmVal(ii) + "</dbz1km>");
		out.println("    <dbzCalRng>" + getDbzCalRngVal(ii) + "</dbzCalRng>");
		out.println("    <count>" + getCountVal(ii) + "</count>");
		out.println("  </point>");
		out.println("");
	    }

	    out.println("</calibration>");
	    out.println("");
	    out.close();

	    // return path

	    return xmlFilePath;
	
	}
    
	private void _writeTextFile(String calDir, Calendar cal, String timeStr)
	{
	    
	    // compute simple text output path from time
	    
	    String textFilePath = new String(calDir + "/" + timeStr + ".cal.txt");
	    if (_params.debug.getValue()) {
		System.out.println("CAL text file path: " + textFilePath);
	    }
	    
	    // open output file
	    
	    FileWriter writer;
	    try {
		writer = new FileWriter(textFilePath);
	    } catch ( IOException e) {
		// show error dialog
		String errStr =
		    "Problem opening calib text file for writing\n" +
		    "  File: " + textFilePath + "\n" + e;
		JOptionPane.showMessageDialog(_canvas, errStr,
					      "Cannot open calib text file for writing",
					      JOptionPane.ERROR_MESSAGE);
		return;
	    
	    }
	    PrintWriter out = new PrintWriter(writer);
	    
	    out.println("RDAS CALIBRATION");
	    out.println("================");
	    out.println("");
	    out.println("Site name: " + _params.radar.siteName.getValue());
	    out.println("Site num: " + _params.radar.siteNum.getValue());
	    out.println("User name: " + _params.radar.userName.getValue());
	    out.println("");
	    out.println("Time: " +
			NFormat.i4.format(cal.get(Calendar.YEAR)) + "/" +
			NFormat.i2.format(cal.get(Calendar.MONTH) + 1) + "/" +
			NFormat.i2.format(cal.get(Calendar.DAY_OF_MONTH)) +
			"  " +
			NFormat.i2.format(cal.get(Calendar.HOUR_OF_DAY)) + ":" + 
			  NFormat.i2.format(cal.get(Calendar.MINUTE)) + ":" +
			NFormat.i2.format(cal.get(Calendar.SECOND)));
	    out.println("");
	    
	    out.println("Parameters: ");
	    out.println("");
	    out.println("  freqGhz: " + _freqGhz);
	    out.println("  freqHz: " + _freqHz);
	    out.println("  lambda: " + round(_lambda, 0.0001));
	    out.println("  horizBeamWidth: " + _horizBeamWidth);
	    out.println("  vertBeamWidth: " + _vertBeamWidth);
	    out.println("  antGain: " + _antGain);
	    out.println("  peakPower: " + _peakPower);
	    out.println("  pulseWidth: " + _pulseWidth);
	    out.println("  waveguideLoss: " + _waveguideLoss);
	    out.println("  radomeLoss: " + _radomeLoss);
	    out.println("  receiverLoss: " + _receiverLoss);
	    out.println("  testCableAtten: " + _testCableAtten);
	    out.println("  couplerAtten: " + _couplerAtten);
	    out.println("  nPointsTable: " + _nPointsTable);
	    out.println("  minDbz: " + _minDbz);
	    out.println("  deltaDbz: " + _deltaDbz);
	    out.println("  fitDbzLow: " + _fitDbzLow);
	    out.println("  fitDbzHigh: " + _fitDbzHigh);
	    out.println("  atmosAtten: " + _atmosAtten);
	    out.println("  radarConst: " + round(_radarConst, 0.0001));
	    out.println("");

	    out.println("Results: ");
	    out.println("");
	    out.println("  mdsCount: " + _mdsCount);
	    out.println("  mdsPower: " + _mdsPower);
	    out.println("  mdsSiggen: " + _mdsSiggen);
	    out.println("  calSlope: " + round(_calSlope, 0.0001));
	    out.println("  calOffset1km: " + round(_calOffset1km, 0.0001));
	    out.println("  calOffsetCalRng: " + round(_calOffsetCalRng, 0.0001));
	    out.println("");
	    
	    out.println("  N calibration points: " + getNPoints());
	    out.println("");

	    out.println("  " +
			rJust("Posn", 10) + "  " +
			rJust("Siggen", 10) + "  " +
			rJust("dBM", 10) + "  " +
			rJust("dBZ-1km", 10) + "  " +
			rJust(_dbzCalRngStr, 10) + "  " +
			rJust("count", 10));
	    
	    out.println("  " +
			rJust("====", 10) + "  " +
			rJust("======", 10) + "  " +
			rJust("===", 10) + "  " +
			rJust("=======", 10) + "  " +
			rJust("=========", 10) + "  " +
			rJust("=====", 10));
	    
	    for (int ii = 0; ii < getNPoints(); ii++) {
		out.println
		    ("  " +
		     rJust(new Double(getPosnVal(ii)).toString(), 10) + "  " +
		     rJust(new Double(getSiggenVal(ii)).toString(), 10) + "  " +
		     rJust(new Double(getDbmVal(ii)).toString(), 10) + "  " +
		     rJust(new Double(getDbz1kmVal(ii)).toString(), 10) + "  " +
		     rJust(new Double(getDbzCalRngVal(ii)).toString(), 10) + "  " +
		     rJust(new Double(getCountVal(ii)).toString(), 10));
	    }
	    out.close();
	    
	}
    
    } // class FileSaveAction
    
}

