///////////////////////////////////////////////////////////////////////
//
// AScope
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
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import edu.ucar.rap.jrp.*;

public class AScope extends JFrame implements BeamDataListener

{

    private Parameters _params = Parameters.getInstance();
    private JPanel _topPanel;
    private JPanel _footPanel;
    private AScopeCanvas _canvas;
    private DataField _field = DataField.DBZ;
    private String _infoContent;
    
    private int _nGates;

    private boolean _performSampling = false;
    private boolean _setupSampling = false;

    private int _sampleIndex;
    private int _sampleCenter;
    private int _sampleNGates;
    private int _sampleNBeams;
    private int _sampleNGatesUsed;
    private int _sampleNBeamsUsed;
    private int _sampleStartGate;
    private int _sampleMidGate;
    private int _sampleEndGate;
    private double _sampleMidRange;
    private double _sampleCount;
    private double _sampleDbz;
    private double _sampleSnr;

    private JButton _setupSamplingButton;
    private SamplingPanel _samplePanel;
    private JCheckBox _sampleToggle;
    private JSlider _sampleSlider;
    private JSpinner _nGatesSpinner;
    private SpinnerNumberModel _nGatesSpinnerModel;
    private JSpinner _nBeamsSpinner;
    private SpinnerNumberModel _nBeamsSpinnerModel;

    private boolean _freezeOn = false;
    private JButton _freezeButton;
    private JSlider _freezeSlider;
    private FreezePanel _freezePanel;
    private MessageQueue _freezeQueue;

    private JButton _unZoomButton;

    private BufferedImage _axisImage = null;

    public AScope() {

	// initialize from params

	_updateFromParams();
	_nGates = _params.scan.nGates.getValue();

	// startup field
	
	if (_params.bscan.startupField.getValue().
	    equals(DataField.DBZ.toString())) {
	    _field = DataField.DBZ;
	} else {
	    _field = DataField.SNR;
	}
	
	// basic window setup
	
	_setTitleStr();
	setSize(_params.aScope.width.getValue(),
		_params.aScope.height.getValue());
	setLocation(_params.aScope.xx.getValue(),
		    _params.aScope.yy.getValue());
	setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	addComponentListener(new MoveResizeListener());
  	_topPanel = new JPanel(new BorderLayout());
  	getContentPane().add(_topPanel);

	// canvas
	
	_canvas = new AScopeCanvas(this, _field);
	_topPanel.add(_canvas, BorderLayout.CENTER);

	// field selection

	JButton dbzButton = new JButton(DataField.DBZ.toString());
	dbzButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
		    setField(DataField.DBZ);
                }
            });

	JButton snrButton = new JButton(DataField.SNR.toString());
	snrButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
		    setField(DataField.SNR);
                }
            });

	// foot panel for sampling and freeze gui

	_footPanel = new JPanel(new BorderLayout());
	_topPanel.add(_footPanel, BorderLayout.SOUTH);
	
	// set up sampling panel

  	_samplePanel = new SamplingPanel(this, new BorderLayout());

	// set_sampling button
	
	_setupSamplingButton = new JButton("Sampling");
	_setupSamplingButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
		    if (!_setupSampling) {
			_setupSampling = true;
			_samplePanel.update();
			_footPanel.add(_samplePanel, BorderLayout.SOUTH);
		    } else {
			_setupSampling = false;
			_samplePanel.update();
			_footPanel.remove(_samplePanel);
		    }
		    _topPanel.validate();
                }
            });
	
	// set up freeze panel

	_freezeQueue = new MessageQueue();
  	_freezePanel = new FreezePanel(this, new BorderLayout());

	// freeze button
	
	_freezeButton = new JButton("Freeze");
	_freezeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
		    if (!_freezeOn) {
			_freezeOn = true;
			updateFreezeSlider();
			_footPanel.add(_freezePanel, BorderLayout.NORTH);
			_freezeButton.setText("UnFreeze");
		    } else {
			_freezeOn = false;
			_footPanel.remove(_freezePanel);
			_freezeButton.setText("Freeze");
		    }
		    _topPanel.validate();
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

	// help button

	JButton helpButton = new JButton("Help");
	_infoContent =
	    "<h1>AScope help info</h1>\n" +
	    "<hr>\n" +
	    "<h2>Field selection</h2>\n" +
	    "<ul>\n" +
	    "<li>Select field by clicking DBZ or SNR.\n" +
	    "</ul>\n" +
	    "<hr>\n" +
	    "<h2>Zooming</h2>\n" +
	    "<ul>\n" +
	    "<li>Use left button to zoom in plot.\n" +
	    "<li>You can repeat to zoom in further.\n" +
	    "<li>Click <b>UnZoom</b> to cancel zoom.\n" +
	    "</ul>\n" +
	    "<hr>\n" +
	    "<h2>Freeze/Replay</h2>\n" +
	    "<ul>\n" +
	    "<li>Click <b>Freeze</b> to toggle freeze on/off.\n" +
	    "<li>Use slider to examine stored beams.\n" +
	    "<li>Click <b>Continue</b> to cancel freeze.\n" +
	    "</ul>\n" +
	    "<hr>\n" +
	    "<h2>Sampling</h2>\n" +
	    "<ul>\n" +
	    "<li>Click <b>Sampling</b> to bring up sampling panel at bottom.\n" +
	    "<li>Click <b>Activate sampling</b> to toggle sampling on/off.\n" +
	    "<li>Use slider to set sampling position in range.\n" +
	    "<li>Use <b>N gates</b> to adjust number of gates to average.\n" +
	    "<li>Use <b>N beams</b> to adjust number of beams to average.\n" +
	    "<li>Click <b>Close</b> to close sampling panel.\n" +
	    "</ul>\n";
	    
	helpButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
		    InfoFrame info =
			new InfoFrame(getX() + 30, getY() + 30,
				      600, 650);
		    info.setTitle("Info for AScope");
		    info.setContent(_infoContent);
                }
            });

	// menu bar
	
	JMenuBar menuBar = new JMenuBar();
	setJMenuBar(menuBar);
	JButton editButton = new JButton(_params.aScope.getViewEditAction());
	menuBar.add(editButton);
	menuBar.add(dbzButton);
	menuBar.add(snrButton);
	menuBar.add(_setupSamplingButton);
	menuBar.add(_freezeButton);
	menuBar.add(_unZoomButton);
	menuBar.add(helpButton);

	// update from parameters again to set queue size etc.

	_updateFromParams();
	_params.aScope.setUnsavedChanges(false);
	
	// add listener for new beam data
	
	BeamDataHandler.getInstance().addListener(this);

	// add change listener for params
	
	_params.aScope.addChangeListener(new AScopeParamsChangeListener());
	_params.radar.addChangeListener(new RadarParamsChangeListener());

    } // constructor
    
   // handle new beam
    
    public void handleBeam(BeamMessage beam, double rate) {
 	if (!isVisible() || _freezeOn) {
 	    return;
 	}
	_freezeQueue.push(beam);
	_sampleIndex = 0;
	_computeSample();
	if (_nGates != beam.getNGates()) {
	    _nGates = beam.getNGates();
	    _nGatesSpinnerModel.setMaximum(new Integer(_nGates));
	    _samplePanel.update();
	}
	_canvas.setBeam(beam);
	_canvas.setIncomingBeamRate(rate);
    }
    
    // set zoom state

    public void setZoomState(boolean state) {
	_unZoomButton.setEnabled(state);
    }

     // set field

    public void setField(DataField field) {
	_field = field;
	_canvas.setField(_field);
    }

    // get methods
    
    public int getSampleNGatesUsed() {
	return _sampleNGatesUsed;
    }

    public int getSampleNBeamsUsed() {
	return _sampleNBeamsUsed;
    }

    public int getSampleStartGate() {
	return _sampleStartGate;
    }
    
    public int getSampleMidGate() {
	return _sampleMidGate;
    }
    
    public int getSampleEndGate() {
	return _sampleEndGate;
    }
    
    public double getSampleMidRange() {
	return _sampleMidRange;
    }

    public double getSampleCount() {
	return _sampleCount;
    }

    public double getSampleDbz() {
	return _sampleDbz;
    }

    public double getSampleSnr() {
	return _sampleSnr;
    }

    // update from parameters

    private void _updateFromParams() {
	_performSampling = _params.aScope.performSampling.getValue();
	_sampleCenter = _params.aScope.sampleCenter.getValue();
	_sampleNGates = _params.aScope.sampleNGates.getValue();
	_sampleNBeams = _params.aScope.sampleNBeams.getValue();
	if (_freezeQueue != null) {
	    _freezeQueue.setMaxSize(_params.aScope.nFreeze.getValue());
	}
	if (_samplePanel != null) {
	    _samplePanel.update();
	}
    }

    // set title
    
    private void _setTitleStr() {
	String frameTitle = "A-SCOPE - " + _params.radar.siteName.getValue();
	setTitle(frameTitle);
    }

    // Listener for changes in ascope parameters
    
    private class AScopeParamsChangeListener
	implements CollectionChangeListener {
	public void reactToChange() {
	    _updateFromParams();
	}
    }
    
    // Listener for changes in radar parameters
    
    private class RadarParamsChangeListener implements CollectionChangeListener {
	public void reactToChange() {
	    _setTitleStr();
	}
    }

    // sampling panel class

    private class SamplingPanel extends JPanel {

	SamplingPanel(Component parent, LayoutManager layout) {
	    
	    super(layout);

	    // set the border

	    setBorder
		(CustomBorder.createTop(parent, "Set up sampling", 5));

	    // toggle sampling on/off
	    
	    _sampleToggle =
		new JCheckBox("Activate sampling?", _performSampling);
	    _sampleToggle.addItemListener(new SampleToggleListener());
	    _sampleToggle.setHorizontalTextPosition(SwingConstants.LEFT);
	    
	    // number of gates
	    
	    JLabel ngatesLabel = new JLabel("N gates: ");
	    _nGatesSpinnerModel =
		new SpinnerNumberModel(new Integer(_sampleNGates),
				       new Integer(1),
				       new Integer(_nGates),
				       new Integer(1));
	    _nGatesSpinner = new JSpinner(_nGatesSpinnerModel);
	    _nGatesSpinner.setEnabled(_performSampling);
	    _nGatesSpinner.addChangeListener(new NGatesSpinnerListener());
	
	    JPanel ngatesSamplePanel = new JPanel();
	    ngatesSamplePanel.add(ngatesLabel);
	    ngatesSamplePanel.add(_nGatesSpinner);

	    // number of beams
	    
	    JLabel nbeamsLabel = new JLabel("N beams: ");
	    _nBeamsSpinnerModel =
		new SpinnerNumberModel(new Integer(_sampleNBeams),
				       new Integer(1),
				       new Integer(360),
				       new Integer(1));
	    _nBeamsSpinner = new JSpinner(_nBeamsSpinnerModel);
	    _nBeamsSpinner.setEnabled(_performSampling);
	    _nBeamsSpinner.addChangeListener(new NBeamsSpinnerListener());
	
	    JPanel nbeamsSamplePanel = new JPanel();
	    nbeamsSamplePanel.add(nbeamsLabel);
	    nbeamsSamplePanel.add(_nBeamsSpinner);

	    // close button
	    
	    JButton sampleCloseButton = new JButton("Close");
	    sampleCloseButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			_setupSampling = false;
			update();
			_footPanel.remove(_samplePanel);
			_topPanel.validate();
		    }
		});

	    // top panel
	    
	    JPanel panel = new JPanel();
	    panel.add(_sampleToggle);
	    panel.add(ngatesSamplePanel);
	    panel.add(nbeamsSamplePanel);
	    panel.add(sampleCloseButton);

	    // slider
	    
	    _sampleSlider = new JSlider();
	    _sampleSlider.setMinimum(1);
	    _sampleSlider.setMaximum(_nGates);
	    _sampleSlider.setValue(_sampleCenter);
	    _sampleSlider.setEnabled(_performSampling);
	    _sampleSlider.addChangeListener(new SampleSliderListener());
	    
	    // add the components
	    
	    add(_sampleSlider, BorderLayout.NORTH);
	    add(panel, BorderLayout.CENTER);

	} // constructor

	// update the sampling components
	
	public void update() {
	    
	    _sampleToggle.setSelected(_performSampling);

	    int samplerMin = _sampleNGates / 2;
	    int samplerMax = _nGates - (_sampleNGates / 2) - 1;
	    
	    _sampleSlider.setMinimum(samplerMin);
	    _sampleSlider.setMaximum(samplerMax);
	    _sampleSlider.setValue(_sampleCenter);
	    _sampleSlider.setEnabled(_performSampling);
	    _nGatesSpinner.setEnabled(_performSampling);
	    _nBeamsSpinner.setEnabled(_performSampling);
	    
	}
    
    } // SamplingPanel
	    
    // sampling listeners

    private class SampleToggleListener implements ItemListener {
	public void itemStateChanged(ItemEvent e) {
	    if (e.getStateChange() == ItemEvent.SELECTED) {
		_performSampling = true;
	    } else {
		_performSampling = false;
	    }
	    _params.aScope.performSampling.setValue(_performSampling);
	    _canvas.setPerformSampling(_performSampling);
	    _computeSample();
	    _canvas.doRepaint();
	    _samplePanel.update();
	}
    }
    
    private class NGatesSpinnerListener implements ChangeListener {
	public void stateChanged(ChangeEvent e) {
	    _sampleNGates = _nGatesSpinnerModel.getNumber().intValue();
	    _params.aScope.sampleNGates.setValue(_sampleNGates);
	    _computeSample();
	    _canvas.doRepaint();
	    _samplePanel.update();
	}
    }

    private class NBeamsSpinnerListener implements ChangeListener {
	public void stateChanged(ChangeEvent e) {
	    _sampleNBeams = _nBeamsSpinnerModel.getNumber().intValue();
	    _params.aScope.sampleNBeams.setValue(_sampleNBeams);
	    _computeSample();
	    _canvas.doRepaint();
	    _samplePanel.update();
	}
    }

    private class SampleSliderListener implements ChangeListener {
	public void stateChanged(ChangeEvent e) {
	    _sampleCenter = _sampleSlider.getValue();
	    _params.aScope.sampleCenter.setValue(_sampleCenter);	
	    _computeSample();
	    _canvas.doRepaint();
	}
    }

    // freeze panel class
    
    private class FreezePanel extends JPanel {
	
	FreezePanel(Component parent, LayoutManager layout) {
	    
	    super(layout);
	    
	    // set the border
	    
	    setBorder
		(CustomBorder.createTop(parent, "Display stored beams", 5));
	    
	    // close button
	    
	    JButton freezeCloseButton = new JButton("Continue");
	    freezeCloseButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			_freezeOn = false;
			_freezeButton.setText("Freeze");
			_footPanel.remove(_freezePanel);
			// _topPanel.validate();
		    }
		});
	    
	    // top panel
	    
	    JPanel freezeTopPanel = new JPanel();
	    freezeTopPanel.add(freezeCloseButton);
	    
	    // slider
	    
	    _freezeSlider = new JSlider();
	    _freezeSlider.addChangeListener(new FreezeSliderListener());
	    updateFreezeSlider();

	    // add the components
	    
	    add(_freezeSlider, BorderLayout.NORTH);
	    add(freezeTopPanel, BorderLayout.CENTER);

	} // constructor
	
    } // FreezePanel

    // update the freeze slider
    
    private void updateFreezeSlider() {

	// set range

	_freezeSlider.setMinimum(0);
	_freezeSlider.setMaximum(_freezeQueue.getMaxSize() - 1);

	// set value

	_freezeSlider.setValue(_freezeSlider.getMaximum());

	// set ticks

	_freezeSlider.setMajorTickSpacing(_freezeSlider.getMaximum() / 10);
	_freezeSlider.setPaintTicks(true);
	
	// set labels
	
	Hashtable labelTable = new Hashtable();
	labelTable.put(new Integer(_freezeSlider.getMinimum()),
		       new JLabel("Earliest") );
	labelTable.put(new Integer(_freezeSlider.getMaximum()),
		       new JLabel("Latest") );
	_freezeSlider.setLabelTable(labelTable);
	_freezeSlider.setPaintLabels(true);
	
    }
    
    // compute sample from stored beams

    private void _computeSample() {

	if (!_performSampling) {
	    return;
	}

	// beams

	int startIndex = _sampleIndex;
	int endIndex = startIndex + _sampleNBeams - 1;
	if (endIndex > _freezeQueue.getMaxSize() - 1) {
	    endIndex = _freezeQueue.getMaxSize() - 1;
	}
	_sampleNBeamsUsed = endIndex - startIndex + 1;
	
	// gates
	
	BeamMessage beam = (BeamMessage) _freezeQueue.get(startIndex);
	if (beam == null) {
	    return;
	}
	_sampleStartGate = _sampleCenter - _sampleNGates / 2;
	if (_sampleStartGate < 0) {
	    _sampleStartGate = 0;
	}
	_sampleEndGate = _sampleStartGate + _sampleNGates - 1;
	if (_sampleEndGate > beam.getNGates() - 1) {
	    _sampleEndGate = beam.getNGates() - 1;
	}
	_sampleNGatesUsed = _sampleEndGate - _sampleStartGate + 1;
	_sampleMidGate = (_sampleStartGate + _sampleEndGate) / 2;
	_sampleMidRange =
	    beam.getStartRange() + _sampleMidGate * beam.getGateSpacing();
	
	double sumCount = 0.0;
	double sumDbz = 0.0;
	double sumSnr = 0.0;
	double nn = 0.0;
	
	for (int ii = startIndex; ii <= endIndex; ii++) {
	    beam = (BeamMessage) _freezeQueue.get(ii);
            if (beam != null) {
              float dbz[] = beam.getDbz();
              float snr[] = beam.getSnr();
              short[] counts = beam.getCounts();
              for (int jj = _sampleStartGate; jj <= _sampleEndGate; jj++) {
		sumCount += counts[jj];
		sumDbz += dbz[jj];
		sumSnr += snr[jj];
		nn++;
              } // i
            }
	}
	if (nn == 0) {
	    nn = 1;
	}
	_sampleCount = sumCount / nn;
	_sampleDbz = sumDbz / nn;
	_sampleSnr = sumSnr / nn;
	_sampleCount = (int) (_sampleCount * 10.0 + 0.5) / 10.0;
	_sampleDbz = (int) (_sampleDbz * 100.0 + 0.5) / 100.0;
	_sampleSnr = (int) (_sampleSnr * 100.0 + 0.5) / 100.0;

    }

    private class FreezeSliderListener implements ChangeListener {
	public void stateChanged(ChangeEvent e) {
	    _sampleIndex =
		_freezeQueue.getMaxSize() - _freezeSlider.getValue() - 1;
	    _computeSample();
	    BeamMessage beam = (BeamMessage) _freezeQueue.get(_sampleIndex);
	    if (beam != null) {
		_sampleSlider.setMaximum(beam.getNGates());
		_nGatesSpinnerModel.setMaximum(new Integer(beam.getNGates()));
		_canvas.setBeam(beam);
		_canvas.doRepaint();
	    }
	}
    }

    // window move/resize listener

    private class MoveResizeListener extends ComponentAdapter
    {

	public void componentMoved(ComponentEvent e) {
	    Parameters params = Parameters.getInstance();
	    int paramX = params.aScope.xx.getValue();
	    int paramY = params.aScope.yy.getValue();
	    if (paramX != getX() || paramY != getY()) {
		_params.aScope.xx.setValue(getX());
		_params.aScope.yy.setValue(getY());
	    }
	}
	
	public void componentResized(ComponentEvent e) {
	    Parameters params = Parameters.getInstance();
	    int paramWidth = params.aScope.width.getValue();
	    int paramHeight = params.aScope.height.getValue();
	    if (paramWidth != getWidth() || paramHeight != getHeight()) {
		params.aScope.width.setValue(getWidth());
		params.aScope.height.setValue(getHeight());
	    }
	}
	
	public void componentShown(ComponentEvent e) {
	    Parameters params = Parameters.getInstance();
	    if (params.aScope.startVisible.getValue() == false) {
		params.aScope.startVisible.setValue(true);
	    }
	}
	
	public void componentHidden(ComponentEvent e) {
	    Parameters params = Parameters.getInstance();
	    if (params.aScope.startVisible.getValue() == true) {
		params.aScope.startVisible.setValue(false);
	    }
	}
	
    }

}
