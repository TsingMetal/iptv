package com.tsing.iptv;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class IptvView extends JFrame implements ViewInterface {
  
  JDialog inputDialog; // to receive input
  JTextField snField; // to receive SN input
  JTextField macField; // to receive Mac input

  JTextArea infoArea; // to show test infomation
  JTextArea retArea; // to show xml string return from STB

  MacWriter macWriter;
  XmlParser xmlParser;
  Logger logger;
  DBConnector dbConnector;
  XmlWriter xmlWriter;

  public IptvView() {
    xmlParser = IptvXmlParser(); // initialize xmlParser 
    dbConnector = new DBConnector(); //~~ DBConnector unimplemented yet 
    macWriter = new MacWriter(xmlParser, dbConnector); // initialize macWriter
    
    xmlWriter = new IptvXmlWriter(); //initialize xmlWriter 
    logger = new IptvLogger(xmlWriter); //initialize logger 

    macWriter.addMacWritingListener(this); // register IptvView with macWriter
    macWriter.addMacWritingListener(logger); // register logger with macWriter

    setUI();
  }

  private void setUI() {
    setTitle("Iptv Mac Writer"); // set frame title
    
    Toolkit kit = Toolkit.getDefaultToolkit();
    Dimension screenSize = kit.getScreenSize();
    int screenWidth = screenSize.width; // get screen width;
    int screenHeight = screenSize.height; // get screen height;
    setSize(screenWidth*3 / 4, screenHeight*3 / 4); // set frame size;
    setLocation(screenWidth*1 / 8, screenHeight*1 / 8); //set initial position;

    JPanel panel = new JPanel(new BorderLayout());

    infoArea = new JTextArea();
    infoArea.setBorder(
        BorderFactory.createTitledBorder("Test Information Area"));
    
    retArea = new JTextArea(12, 40);
    retArea.setBorder(
        BorderFactory.createTitledBorder("Ret XML Area"));
    retArea.setLineWrap(true);
    retArea.setVisible(false); // default not show retArea;

    panel.add(infoArea, BorderLayout.CENTER);
    panel.add(retArea, BorderLayout.NORTH);

    add(panel);  // add panel to frame

    JMenuBar menuBar = new JMenuBar();

    // set Operation menu:
    JMenu operationMenu = new JMenu("Operation");
    JMenuItem eraseMacItem = new JMenuItem("Erase Mac", 'E');
    JRadioButtonMenuItem restorationMode = 
      new JRadioButtonMenuItem("Restoration Mode");
    JMenuItem exitItem = new JMenuItem("Exit");
    operationMenu.add(eraseMacItem);
    operationMenu.add(restorationMode);
    operationMenu.add(exitItem);

    // set Setting Menu
    JMenu settingMenu = new JMenu("Setting");
    JRadioButtonMenuItem onTop = new JRadioButtonMenuItem("OnTop");
    JRadioButtonMenuItem showRetArea = 
      new JRadioButtonMenuItem("Show Ret Area");
    settingMenu.add(onTop);
    settingMenu.add(showRetArea);
    
    //set Help menu
    JMenu helpMenu = new JMenu("Help");
    JMenuItem aboutItem = new JMenuItem("About", 'A');
    helpMenu.add(aboutItem);

    menuBar.add(operationMenu);
    menuBar.add(settingMenu);
    menuBar.add(helpMenu);

    setJMenuBar(menuBar);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }
}
