///////////////////////////////////////////////////////////////////////
//
// ComboPanel - provides methods for combining panels
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

public class ComboPanel

{

  // create panel
  // sets label
    
  static public JPanel makePanel(String labelText,
                                 Color foreground,
                                 Color background,
                                 JLabel value)
    
  {

    JLabel label = new JLabel(labelText, JLabel.CENTER);
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

  // make panel
  // and sets label

  static public JPanel makePanel(String labelText,
                                 Color foreground,
                                 Color background,
                                 JLabel value,
                                 JLabel label)
    
  {

    label = new JLabel(labelText, JLabel.CENTER);
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
    
  static public JPanel addPanel(JPanel parent,
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
    
}

