///////////////////////////////////////////////////////////////////////
//
// StatusMessageDisplay
//
// Mike Dixon
//
// Jan 2004
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

public class StatusMessageDisplay extends JFrame implements BeamDataListener

{

  private Parameters _params = Parameters.getInstance();
  private JPanel _topPanel;
  private String _infoContent;
  private String _prevStatus;
  private JTextPane _textPane;
  private MessageQueue _queue;
  
    
  MoveResizeListener _moveResizeListener = new MoveResizeListener();
    
  public StatusMessageDisplay() {
	
    // basic window setup
	
    setTitle("Status Message Display");
    setSize(_params.smessage.width.getValue(),
            _params.smessage.height.getValue());
    setLocation(_params.smessage.xx.getValue(),
                _params.smessage.yy.getValue());
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    addComponentListener(_moveResizeListener);
    _topPanel = new JPanel(new BorderLayout());
    getContentPane().add(_topPanel);
	
    // help button

    JButton helpButton = new JButton("Help");
    _infoContent =
      "<h1>Status message window info</h1>\n" +
      "<hr>\n" +
      "<ul>\n" +
      "<li>Messages will be displayed in order.\n" +
      "<li>Scroll to see older messages.\n" +
      "</ul>\n";
	
    helpButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          InfoFrame info =
            new InfoFrame(getX() + 30, getY() + 30, 600, 300);
          info.setTitle("Status Message Display Info");
          info.setContent(_infoContent);
        }
      });
	
    // menu bar
	
    JMenuBar menuBar = new JMenuBar();
    setJMenuBar(menuBar);
    JButton editButton = new JButton(_params.smessage.getViewEditAction());
    menuBar.add(editButton);
    menuBar.add(helpButton);

    // message queue

    _queue = new MessageQueue();
    _queue.setMaxSize(_params.smessage.nLines.getValue());

    // add text area

    _textPane = new JTextPane();
    _textPane.setContentType("text/plain");
    _textPane.setAutoscrolls(true);
    _topPanel.add(new JScrollPane(_textPane), BorderLayout.CENTER);
	
    // add listener for new beam data
	
    BeamDataHandler.getInstance().addListener(this);
	
    // add listener for changes in params
	
    _params.smessage.addChangeListener(new ParamsChangeListener());
	
  } // constructor
    
    // handle beam
    
  public void handleBeam(BeamMessage beam, double rate) {
	
    String beamStatus = beam.getStatusString();
    if (beamStatus.length() == 0) {
      return;
    }
    if (beamStatus.equals(_prevStatus)) {
      return;
    }

    _prevStatus = beamStatus;
	
    if (_params.verbose.getValue()) {
      System.err.println(beamStatus);
    }

    // compute the combined string
	
    String elStr = new
      String(" el: " + NFormat.f2_2.format(beam.getEl()));

    String azStr = new
      String(" az: " + NFormat.f2_3.format(beam.getAz()));
	
    String statusStr = new
      String(" status: " + beamStatus + "\n");

    StringBuffer combined = new StringBuffer();
    combined.append(beam.getDateTimeString());
    combined.append(elStr);
    combined.append(azStr);
    combined.append(statusStr);

    if (_params.smessage.printToStderr.getValue()) {
      System.err.print(combined);
    }

    // push the combined string onto the queue

    _queue.push(new String(combined));

    // now form the text from the queue
	
    StringBuffer textBuf = new StringBuffer();
    if (_params.smessage.topLatest.getValue()) {
      for (int ii = 0; ii < _queue.getSize(); ii++) {
        textBuf.append((String) _queue.get(ii));
      }
    } else {
      for (int ii = _queue.getSize() - 1; ii >= 0; ii--) {
        textBuf.append((String) _queue.get(ii));
      }
    }

    // set the text in the pane

    _textPane.setText(new String(textBuf));

  }
    
  // Listener for changes in radar parameters

  private class ParamsChangeListener implements CollectionChangeListener {
    public void reactToChange() {
      _queue.setMaxSize(_params.smessage.nLines.getValue());
    }
  }
    
  // window move/resize listener

  private class MoveResizeListener extends ComponentAdapter
  {
	
    public void componentMoved(ComponentEvent e) {
      Parameters params = Parameters.getInstance();
      int paramX = params.smessage.xx.getValue();
      int paramY = params.smessage.yy.getValue();
      if (paramX != getX() || paramY != getY()) {
        params.smessage.xx.setValue(getX());
        params.smessage.yy.setValue(getY());
      }
    }
	
    public void componentResized(ComponentEvent e) {
      Parameters params = Parameters.getInstance();
      int paramWidth = params.smessage.width.getValue();
      int paramHeight = params.smessage.height.getValue();
      if (paramWidth != getWidth() || paramHeight != getHeight()) {
        params.smessage.width.setValue(getWidth());
        params.smessage.height.setValue(getHeight());
      }
    }
	
    public void componentShown(ComponentEvent e) {
      Parameters params = Parameters.getInstance();
      if (params.smessage.startVisible.getValue() == false) {
        params.smessage.startVisible.setValue(true);
      }
    }
	
    public void componentHidden(ComponentEvent e) {
      Parameters params = Parameters.getInstance();
      if (params.smessage.startVisible.getValue() == true) {
        params.smessage.startVisible.setValue(false);
      }
    }
	
  }

}
