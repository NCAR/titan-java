///////////////////////////////////////////////////////////////////////
//
// InfoFrame
//
// Info panel
//
// Mike Dixon
//
// Jan 2003
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class InfoFrame extends JFrame implements ActionListener

{

    private JTextPane _textPane;

    public InfoFrame(int xx, int yy, int width, int height)
    {
	
	setSize(width, height);
	setLocation(xx, yy);
	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	
	JPanel topPanel = new JPanel(new BorderLayout());
	Container cp = getContentPane();
	cp.add(topPanel);
	
	_textPane = new JTextPane();
	cp.add(new JScrollPane(_textPane),  BorderLayout.CENTER);

	_textPane.setContentType("text/html");
	_textPane.setAutoscrolls(true);

	JButton okButton = new JButton("OK");
	okButton.addActionListener(this);
	okButton.setToolTipText("Close window");
	JPanel okPanel = new JPanel();
	okPanel.add(okButton);
	topPanel.add(okPanel, BorderLayout.SOUTH);
	
	setVisible(true);

    }

    public void setContent(String content) {
	_textPane.setText(content);
    }

    // close when OK is hit
    public void actionPerformed(ActionEvent event)
    {
	if (event.getActionCommand().equals("OK")) {
	    setVisible(false);
	}
    }

}


