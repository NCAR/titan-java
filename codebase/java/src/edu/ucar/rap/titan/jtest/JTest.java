///////////////////////////////////////////////////////////////////////
//
// JTest
//
// Mike Dixon
//
// July 2005
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.jtest;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;

import com.sun.media.jai.codec.*;
import com.sun.media.jai.codecimpl.*;

import edu.ucar.rap.sysview.params.*;

class JTest

{

  private JFrame _frame;
  private JMenuBar _menuBar;
  private JMenu _fileMenu;
  private JMenu _paramsMenu;
  private JMenu _diagramMenu;
  private JMenu _exportMenu;
  private JMenu _editMenu;
  private JMenu _queryMenu;
  private JCheckBoxMenuItem _editShowGridItem;
  private JMenu _modeMenu;
  private JRadioButton _modeRunButton;
  private JRadioButton _modeEditButton;
  
  public JTest(String[] args) {
    
    _frame = new JFrame("JTest");
    _frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    _frame.setSize(new Dimension(500, 500));
    
    _menuBar = new JMenuBar();
    _frame.setJMenuBar(_menuBar);
    
    _addFileMenu();
    _addParamsMenu();
    _addDiagramMenu();
    _addExportMenu();
    _addEditMenu();
    _addQueryMenu();
    _addModeMenu();

    JPanel topPanel = new JPanel();
    topPanel.setLayout(new BorderLayout());
    _frame.getContentPane().add(topPanel);
    _frame.setVisible(true);
    
    MyCanvas canvas = new MyCanvas();
    canvas.setSize(new Dimension(500, 500));
    topPanel.add(canvas, BorderLayout.CENTER);
    canvas.doPaint();
    
  }
  
  // main
  public static void main(String args[])
  {
    
    // Create an instance of the application
    JTest app = new JTest(args);
    
  }
  
  class MyCanvas extends JPanel {
    
    public MyCanvas() {
    }
    
    public void doPaint() {
      paintComponent(getGraphics());
    }
    
    public void paintComponent(Graphics g) {
      
      // System.err.println("paintComponent");
      
      BufferedImage image =
        new BufferedImage(500, 500,
                          BufferedImage.TYPE_INT_ARGB);
      Graphics2D ig = image.createGraphics();
      // System.err.println("ig: " + ig);
      
      if (ig != null) {
        
        ig.setColor(getBackground());
        ig.fill(new Rectangle(0, 0,
                              image.getWidth(), image.getHeight()));
        ig.setColor(getForeground());
        ig.draw(new Line2D.Double(100, 100, 300, 300));
        ig.draw(new Line2D.Double(100, 300, 300, 100));
        // System.err.println("   -->> drawing");
        
        Graphics2D g2 = (Graphics2D) g;
        if (g2 != null) {
          g2.setColor(getBackground());
          g2.fill(new Rectangle(0, 0, getWidth(), getHeight()));
          g2.setColor(getForeground());
          g2.drawImage(image, null, null);
        }
        
        // Save the image to a file
        try {
          OutputStream output = new BufferedOutputStream
            (new FileOutputStream("/tmp/Jtest.png"));
          ImageEncoder encoder =
            ImageCodec.createImageEncoder("PNG", output, null);
          encoder.encode(image);
          output.close();
        } catch (Exception e) {
          // System.err.println("ERROR - Converting image to PNG" + e);
        }
        
      }
      
      
      // Image image = createImage(500, 500);
      // System.err.println("Image: " + image);
      
    }
    
  }
  
  /**
   * Add file menu
   */
 
  private void _addFileMenu() {
    
    _fileMenu = new JMenu("File");
    _menuBar.add(_fileMenu);
    
    JMenuItem quit = new JMenuItem("Quit");
    quit.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.err.println("Quit");
          System.exit(0);
        }
      });
    _fileMenu.add(quit);
    
  }

  /**
   * Add params menu
   */
 
  private void _addParamsMenu() {
    
    _paramsMenu = Parameters.getInstance().createMenu("Params");
    _menuBar.add(_paramsMenu);

  }

  /**
   * Add diagram menu
   */
 
  private void _addDiagramMenu() {
    
    _diagramMenu = new JMenu("Diagram");
    _menuBar.add(_diagramMenu);

    JMenuItem fileNewItem = new JMenuItem("New");
    fileNewItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.err.println("New");
        }
      });
    _diagramMenu.add(fileNewItem);

    JMenuItem fileOpenItem = new JMenuItem("Open");
    fileOpenItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.err.println("Open");
        }
      });
    _diagramMenu.add(fileOpenItem);
    
    JMenuItem fileSaveAsItem = new JMenuItem("Save As");
    fileSaveAsItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.err.println("Save As");
        }
      });
    _diagramMenu.add(fileSaveAsItem);

    JMenuItem fileSaveItem = new JMenuItem("Save");
    fileSaveItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.err.println("Save");
        }
      });
    _diagramMenu.add(fileSaveItem);

  } // addDiagramMenu

  /**
   * Add export menu
   */
  
  private void _addExportMenu() {
    
    _exportMenu = new JMenu("Export", true);
    _menuBar.add(_exportMenu);

    JMenuItem filePrintItem = new JMenuItem("Print");
    filePrintItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.err.println("Print");
        }
      });
    _exportMenu.add(filePrintItem);

    JMenuItem fileExportDiagramItem = new JMenuItem("Export diagram");
    fileExportDiagramItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.err.println("Export diagram");
        }
      });
    _exportMenu.add(fileExportDiagramItem);

    JMenuItem fileExportAllItem = new JMenuItem("Export all diagrams");
    fileExportAllItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.err.println("Export all diagrams");
        }
      });
    _exportMenu.add(fileExportAllItem);

    JMenuItem fileExportAllHtml = new JMenuItem("Export all with HTML");
    fileExportAllHtml.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.err.println("Export all with HTML");
        }
      });
    _exportMenu.add(fileExportAllHtml);

  }
  
  /**
   * Add edit menu
   */
  private void _addEditMenu() {

    _editMenu = new JMenu("Edit");
    _menuBar.add(_editMenu);

    JMenuItem editChangeTitleItem = new JMenuItem("Change Title ...");
    editChangeTitleItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.err.println("Change Title ...");
        }
      });
    _editMenu.add(editChangeTitleItem);

    JMenuItem editChangeHostsItem = new JMenuItem("Change All Hosts ...");
    editChangeHostsItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.err.println("Change All Hosts ...");
        }
      });
    _editMenu.add(editChangeHostsItem);

    _editMenu.addSeparator();

    JMenuItem editAddProcessItem = new JMenuItem("Add Process ...");
    editAddProcessItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.err.println("Add Process ...");
        }
      });
    _editMenu.add(editAddProcessItem);

    JMenuItem editAddDatasetItem = new JMenuItem("Add Data Set ...");
    editAddDatasetItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.err.println("Add Data Set ...");
        }
      });
    _editMenu.add(editAddDatasetItem);

    _editMenu.addSeparator();

    JMenuItem editAddTextBoxItem = new JMenuItem("Add Text Box ...");
    editAddTextBoxItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.err.println("Add Text Box ...");
        }
      });
    _editMenu.add(editAddTextBoxItem);

    JMenuItem editAddAnnotItem = new JMenuItem("Add Annotation...");
    editAddAnnotItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.err.println("Add Annotation...");
        }
      });
    _editMenu.add(editAddAnnotItem);

    JMenuItem editAddFlowLineItem = new JMenuItem("Add Flow Line");
    editAddFlowLineItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.err.println("Add Flow Line");
        }
      });
    _editMenu.add(editAddFlowLineItem);

    JMenuItem editAddPolylineItem = new JMenuItem("Add Polyline");
    editAddPolylineItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.err.println("Add Polyline");
        }
      });
    _editMenu.add(editAddPolylineItem);

    JMenuItem editAddDiagramItem = new JMenuItem("Add Sub-diagram");
    editAddDiagramItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.err.println("Add Sub-diagram");
         }
      });
    _editMenu.add(editAddDiagramItem);

    JMenuItem editAddZudnikItem = new JMenuItem("Add Zudnik Object ...");
    editAddZudnikItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.err.println("Add Zudnik Object ...");
        }
      });
    _editMenu.add(editAddZudnikItem);

    _editMenu.addSeparator();

    _editShowGridItem = new JCheckBoxMenuItem("Show Grid", true);
    _editShowGridItem.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent event) {
          System.err.println("Show Grid");
        }
      });
    _editMenu.add(_editShowGridItem);

  }

  /**
   * Add query menu
   */
 
  private void _addQueryMenu() {

    _queryMenu = new JMenu("Query");
    _menuBar.add(_queryMenu);

    JMenuItem queryMappersItem = new JMenuItem("Query Mappers");
    queryMappersItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.err.println("Query Mappers");
        }
      });
    _queryMenu.add(queryMappersItem);
    
  }

  /**
   * Add run mode menu
   */

  private void _addModeMenu() {

    _modeMenu = new JMenu("Run-mode");
    _menuBar.add(_modeMenu);

    JPanel panel = new JPanel(new BorderLayout());
    _modeRunButton = new JRadioButton("Run");
    _modeEditButton = new JRadioButton("Edit");
    panel.add(_modeRunButton, BorderLayout.NORTH);
    panel.add(_modeEditButton, BorderLayout.SOUTH);
    _modeMenu.add(panel);
    
    ButtonGroup group = new ButtonGroup();
    group.add(_modeRunButton);
    group.add(_modeEditButton);
    
    _modeRunButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
          System.err.println("Run");
        }
      });

    _modeEditButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
          System.err.println("Edit");
        }
      });
    
  }

}
