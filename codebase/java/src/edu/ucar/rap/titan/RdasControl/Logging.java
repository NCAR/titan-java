//////////////////////////////////////////////////////////////////////
//
// Logging
//
// Mike Dixon
//
// May 2005
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.io.*;
import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import edu.ucar.rap.jrp.*;

public class Logging extends JFrame implements BeamDataListener

{

    private Parameters _params = Parameters.getInstance();

    // gui

    private JPanel _labelPanel;
    private JPanel _buttonPanel;
    
    private JLabel _filePathLabel;
    private JLabel _nBeamsLabel;
    
    private JButton _newFileButton;
    private JButton _closeFileButton;
    private JButton _startButton;
    private JButton _stopButton;
    
    private JTextPane _textPane;

    private String _fileName;
    private String _filePath;
    private boolean _isDbz = true;
    private boolean _isSnr = false;
    private boolean _isCount = false;
    
    private String _infoContent;
    
    private int _nBeamsLogged = 0;;
    private boolean _active = false;
    private PrintWriter _out = null;
    private long _prevBeamTime = 0;
    
    public Logging() {
	
	// construct top-level objects
	
	_setTitleStr();
 	setSize(_params.logging.width.getValue(),
		_params.logging.height.getValue());
	setLocation(_params.logging.xx.getValue(),
		    _params.logging.yy.getValue());
	setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	getContentPane().setLayout(new BorderLayout());
	addComponentListener(new MoveResizeListener());
	
	// menu bar
	
	JMenuBar menuBar = new JMenuBar();
	setJMenuBar(menuBar);
	_fillMenuBar(menuBar);
	
	// label panel
	
	_labelPanel = new JPanel(new BorderLayout());
	_addLabels();
	
	// add text area
	
	_textPane = new JTextPane();
	_textPane.setContentType("text/plain");
	_textPane.setAutoscrolls(true);
	_textPane.setEditable(false);
	_textPane.setHighlighter(null);
	_textPane.setBorder(CustomBorder.createTop(this, "Log text", 5));
	
	// combo panel

	JPanel comboPanel = new JPanel(new BorderLayout());
  	comboPanel.add(_labelPanel, BorderLayout.NORTH);
	// comboPanel.add(new JScrollPane(_textPane), BorderLayout.SOUTH);
	comboPanel.add(_textPane, BorderLayout.SOUTH);

	getContentPane().add(comboPanel, BorderLayout.CENTER);

 	// add listener for new beam data
	
	BeamDataHandler.getInstance().addListener(this);

    }
    
    // handle new beam
    
    public void handleBeam(BeamMessage beam, double rate) {

	if (!_active || _out == null) {
	    return;
	}

	// check time between beams
	
	long now = TimeManager.getTime();
	long diffMsecs = now - _prevBeamTime;
	if (diffMsecs < _params.logging.msecsBetweenBeams.getValue()) {
	    return;
	}

	_logBeam(beam);
	_prevBeamTime = now;
	_setNBeams(_nBeamsLogged + 1);
	
	if (_nBeamsLogged >= _params.logging.maxNBeams.getValue()) {
	    _closeLogFile();
	}
	
    }
    
    // set title
    
    private void _setTitleStr() {
	String frameTitle =
	    "LOGGING - " + _params.radar.siteName.getValue();
	setTitle(frameTitle);
    }

    // fill out menu bar
    
    private void _fillMenuBar(JMenuBar menuBar) {
	
	// parameter editing short-cut

	JButton editButton = new JButton(_params.logging.getViewEditAction());
	
	// new file
	
	_newFileButton = new JButton("New File");
	_newFileButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
		    _setNewFile();
		}
            });
	
	// start logging
	
	_startButton = new JButton("Start");
	_startButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
		    _active = true;
                }
            });
	
	// stop logging
	
	_stopButton = new JButton("Stop");
	_stopButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
		    _active = false;
                }
            });
	
	// close file
	
	_closeFileButton = new JButton("Close File");
	_closeFileButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
		    _closeLogFile();
		}
            });
	
	// help
	
	JButton helpMenu = new JButton("Help");
	helpMenu.setToolTipText("Display help window");
	_infoContent =
	    "<h1>Logging help info</h1>\n" +
	    "<hr>\n" +
	    "<h2>Use <b>Edit</b> to set logging parameters</h2>\n" +
	    "<h2>Operation:</h2>\n" +
	    "<ul>\n" +
	    "<li>Hit <b>New File</b> to start a fresh file.\n" +
	    "<li>Hit <b>Start</b> to start logging to the current file.\n" +
	    "<li>Hit <b>Stop</b> to stop logging to the current file.\n" +
	    "<li>Hit <b>Close File</b> to close out the file.\n" +
	    "</ul>\n" +
	    "<h2>The following will be printed to each line in the log</h2>\n" +
	    "<ul>\n" +
	    "<li>year (if logTime set true)\n" +
	    "<li>month (if logTime set true)\n" +
	    "<li>day (if logTime set true)\n" +
	    "<li>hour (if logTime set true)\n" +
	    "<li>min (if logTime set true)\n" +
	    "<li>secs (if logTime set true)\n" +
	    "<li>msecs (if logTime set true)\n" +
	    "<li>elevation\n" +
	    "<li>azimuth\n" +
	    "<li>ngates-log\n" +
	    "<li>data (dbz or snr) ...\n" +
	    "</ul>\n";
	
	helpMenu.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
		    InfoFrame info =
			new InfoFrame(getX() + 30, getY() + 30,
				      600, 600);
		    info.setTitle("Info for Logging");
		    info.setContent(_infoContent);
                }
            });
	
	// add all to menu bar

	menuBar.add(editButton);
	menuBar.add(_newFileButton);
	menuBar.add(_startButton);
	menuBar.add(_stopButton);
	menuBar.add(_closeFileButton);
	menuBar.add(helpMenu);

    }
    
    // add labels
    
    private void _addLabels() {
	
	_filePathLabel = new JLabel();
	_setFileName("none");
	_filePathLabel.setBorder(CustomBorder.createSimple(this, 2));
	
	_nBeamsLabel = new JLabel();
	_nBeamsLabel.setBorder(CustomBorder.createSimple(this, 2));
	_setNBeams(0);
	
	_labelPanel.add(_filePathLabel, BorderLayout.NORTH);
	_labelPanel.add(_nBeamsLabel, BorderLayout.SOUTH);

    }

    private void _setNewFile() {

	// compute file name
	
	_isDbz = false;
	_isSnr = false;
	_isCount = false;
	if (_params.logging.field.getValue().equals("DBZ")) {
	    _isDbz = true;
	} else if (_params.logging.field.getValue().equals("SNR")) {
	    _isSnr = true;
	} else {
	    _isCount = true;
	}

	Calendar cal = TimeManager.getCal();
	String timeStr = new String
	    (NFormat.i4.format(cal.get(Calendar.YEAR)) +
	     NFormat.i2.format(cal.get(Calendar.MONTH) + 1) +
	     NFormat.i2.format(cal.get(Calendar.DAY_OF_MONTH)) +
	     "_" +
	     NFormat.i2.format(cal.get(Calendar.HOUR_OF_DAY)) +
	     NFormat.i2.format(cal.get(Calendar.MINUTE)) +
	     NFormat.i2.format(cal.get(Calendar.SECOND)));
	String newName;
	if (_isDbz) {
	    newName = new String(timeStr + ".beams.dbz");
	} else if (_isSnr) {
	    newName = new String(timeStr + ".beams.snr");
	} else {
	    newName = new String(timeStr + ".beams.count");
	}
	_setFileName(newName);

 	// open output file

	_openLogFile();

    }
    
    private void _setFileName(String name) {
	_fileName = name;
	String dir = _params.logging.fileFolder.getValue();
	_filePath = new String(dir + "/" + _fileName);
	_filePathLabel.setText("Logging file :  " + _fileName);
    }
    
    private void _setNBeams(int nBeams) {
	_nBeamsLogged = nBeams;
	if (_nBeamsLabel != null) {
	    _nBeamsLabel.setText
		(new String("N Beams logged: " + _nBeamsLogged));
	}
    }

    // open logging file

    private void _openLogFile() {

	// close current file

	_closeLogFile();

	// create directory if needed

	String dir = _params.logging.fileFolder.getValue();
	File dirFile = new File(dir);
	if (!dirFile.exists()) {
	    boolean success = dirFile.mkdirs();
	    if (!success) {
		// Directory creation failed
		String errStr =
		    "Cannot create directory for logging files\n" +
		    "  Dir: " + dir + "\n";
		JOptionPane.showMessageDialog
		    (_buttonPanel, errStr,
		     "Cannot create logging file directory",
		     JOptionPane.ERROR_MESSAGE);
		_setFileName("none");
		return;
	    }
	}

	// open new file

 	FileWriter writer;
 	try {
 	    writer = new FileWriter(_filePath);
 	} catch ( IOException e) {
 	    // show error dialog
 	    String errStr =
 		"Problem opening logging file for writing\n" +
 		"  File: " + _filePath + "\n" + e;
	    JOptionPane.showMessageDialog
		(_buttonPanel, errStr,
		 "Cannot open logging file for writing",
		 JOptionPane.ERROR_MESSAGE);
	    _setFileName("none");
 	    return;
	    
 	}
	synchronized(this) {
	    _out = new PrintWriter(writer);
	}

	_setNBeams(0);

    }

    // close logging file

    private void _closeLogFile() {
	synchronized(this) {
	    if (_out != null) {
		_out.close();
	    }
	    _out = null;
	}
    }

    // perform logging

    private void _logBeam(BeamMessage beam) {

	if (_out == null) {
	    return;
	}

	int startGate = (int) ((_params.logging.startRange.getValue() -
				beam.getStartRange()) /
			       beam.getGateSpacing() + 0.5);;
	int endGate = (int) ((_params.logging.endRange.getValue() -
			      beam.getStartRange()) /
			     beam.getGateSpacing() + 0.5);;
	if (startGate < 0) {
	    startGate = 0;
	}
	if (endGate < startGate) {
	    endGate = startGate;
	}
	if (endGate > beam.getNGates() - 1) {
	    endGate = beam.getNGates() - 1;
	}
	int nGates = endGate - startGate + 1;

	StringBuffer logStr = new StringBuffer();
	if (_params.logging.logTime.getValue()) {
	    logStr.append(NFormat.i4.format(beam.getYear()));
	    logStr.append(" " + NFormat.i2.format(beam.getMonth()));
	    logStr.append(" " + NFormat.i2.format(beam.getDay()));
	    logStr.append(" " + NFormat.i2.format(beam.getHour()));
	    logStr.append(" " + NFormat.i2.format(beam.getMin()));
	    logStr.append(" " + NFormat.i2.format(beam.getSec()));
	    logStr.append(" " + NFormat.i4.format(beam.getMsec()));
	}
	logStr.append(" " + NFormat.el2.format(beam.getEl()));
	logStr.append(" " + NFormat.az2.format(beam.getAz()));
	logStr.append(" " + NFormat.i4.format(nGates));
	if (_isDbz) {
	    float[] vals = beam.getDbz();
	    for (int igate = startGate; igate <= endGate; igate++) {
		logStr.append(" " + NFormat.f1.format(vals[igate]));
	    }
	} else if (_isSnr) {
	    float[] vals = beam.getSnr();
	    for (int igate = startGate; igate <= endGate; igate++) {
		logStr.append(" " + NFormat.f1.format(vals[igate]));
	    }
	} else {
	    short[] vals = beam.getCounts();
	    for (int igate = startGate; igate <= endGate; igate++) {
		logStr.append(" " + vals[igate]);
	    }
	}

	synchronized(this) {
	    _out.println(logStr);
	    _out.flush();
	}
	
	_textPane.setText(logStr.toString());
	_filePathLabel.repaint();
	_nBeamsLabel.repaint();

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

    ////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////
    // MoveResizeListener class

    private class MoveResizeListener extends ComponentAdapter
    {

	public void componentMoved(ComponentEvent e) {
	    Parameters params = Parameters.getInstance();
	    int paramX = params.logging.xx.getValue();
	    int paramY = params.logging.yy.getValue();
	    if (paramX != getX() || paramY != getY()) {
		params.logging.xx.setValue(getX());
		params.logging.yy.setValue(getY());
	    }
	}
	
	public void componentResized(ComponentEvent e) {
	    Parameters params = Parameters.getInstance();
	    int paramWidth = params.logging.width.getValue();
	    int paramHeight = params.logging.height.getValue();
	    if (paramWidth != getWidth() || paramHeight != getHeight()) {
		params.logging.width.setValue(getWidth());
		params.logging.height.setValue(getHeight());
	    }
	}
	
	public void componentShown(ComponentEvent e) {
	    Parameters params = Parameters.getInstance();
	    if (params.logging.startVisible.getValue() == false) {
		params.logging.startVisible.setValue(true);
	    }
	}
	
	public void componentHidden(ComponentEvent e) {
	    Parameters params = Parameters.getInstance();
	    if (params.logging.startVisible.getValue() == true) {
		params.logging.startVisible.setValue(false);
	    }
	}
	
    }

}

