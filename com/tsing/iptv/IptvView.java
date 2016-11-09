package com.tsing.iptv;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.*;

public class IptvView extends JFrame implements ViewInterface {
  
  public JDialog inputDialog; // to receive input
  public JTextField snField; // to receive SN input
  public JTextField macField; // to receive Mac input

  public JTextPane infoArea; // to show test infomation
  public JTextArea retArea; // to show xml string return from STB
	public JLabel resultLabel; // to show test result(fail or pass)

  private JToolBar toolBar;

  MacWriter macWriter;
  XmlParser xmlParser;
  Logger logger;
  DBConnector dbConnector;
  XmlWriter xmlWriter;

  public IptvView() {
    xmlParser = new IptvXmlParser(); // initialize xmlParser 
    // dbConnector = new DBConnector(); //~~ DBConnector unimplemented yet 
    macWriter = new MacWriter(xmlParser, dbConnector); // initialize macWriter
    
    xmlWriter = new IptvXmlWriter(); //initialize xmlWriter 
    logger = new IptvLogger(xmlWriter); //initialize logger 

    macWriter.addMacWritingListener(this); // register IptvView with macWriter
    macWriter.addMacWritingListener(logger); // register logger with macWriter
		
    EventQueue.invokeLater(() -> {
      setUI();
    });
  }

  private void setUI() {
    setTitle("Iptv Mac Writer"); // set frame title
    
    Toolkit kit = Toolkit.getDefaultToolkit();
    Dimension screenSize = kit.getScreenSize();
    int screenWidth = screenSize.width; // get screen width;
    int screenHeight = screenSize.height; // get screen height;
    setSize(screenWidth*3 / 4, screenHeight*3 / 4); // set frame size;
    setLocation(screenWidth*1 / 8, screenHeight*1 / 8); //set initial position;

    setMainPanel();
    setToolBar();
    setMenuBar();
    setInputDialog();

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }

  private void setMainPanel() {
    // set infomation area:
    JPanel panel = new JPanel(new BorderLayout(5, 10));

    infoArea = new JTextPane();
		infoArea.setEditable(false);
		infoArea.setBackground(Color.BLACK);
    infoArea.setBorder(
        BorderFactory.createTitledBorder("Test Information Area"));
    
    retArea = new JTextArea(8, 40);
		retArea.setEditable(false);
		retArea.setBackground(Color.CYAN);
    retArea.setBorder(
        BorderFactory.createTitledBorder("Ret XML Area"));
    retArea.setLineWrap(true);
    retArea.setVisible(false); // default not show retArea;

		resultLabel = new JLabel("PASS", SwingConstants.CENTER);
		resultLabel.setFont(new Font("Serif", Font.BOLD, 48));
		resultLabel.setForeground(Color.GREEN);

		JScrollPane scrollPane = new JScrollPane(infoArea);
    panel.add(scrollPane, BorderLayout.CENTER);
		panel.add(resultLabel, BorderLayout.SOUTH);
    panel.add(retArea, BorderLayout.NORTH);

    add(panel, BorderLayout.CENTER);  // add panel to frame
  }

  private void setToolBar() {
    // set toolbar:
    toolBar = new JToolBar();
    toolBar.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
    toolBar.setEnabled(false);
    toolBar.setVisible(false);
    
    JButton getButton = new JButton("Get Mac");
    getButton.setToolTipText("get SN and Mac from STB");
    getButton.addActionListener(event -> {
      macWriter.getMac();
    });
    toolBar.add(getButton);

    JButton getAdvButton = new JButton("Get Adv");
    getAdvButton.setToolTipText("get adv-security from STB");
    getAdvButton.addActionListener(event -> {
      macWriter.checkAdv();
    });
    toolBar.add(getAdvButton);

    JButton setAdvButton = new JButton("Enable Adv");
    setAdvButton.setToolTipText("Enable adv-security");
    setAdvButton.addActionListener(event -> {
      macWriter.setAdv();
    });
    toolBar.add(setAdvButton);

    JButton setButton = new JButton("Set Mac");
    setButton.setToolTipText("Write Mac and SN to STB");
    setButton.addActionListener(event -> {
      // unimplemented
    });
    toolBar.add(setButton);

    JButton eraseButton = new JButton("Erase Mac");
    eraseButton.setToolTipText("Erase mac from STB");
    eraseButton.addActionListener(event -> {
      macWriter.eraseMac();
    });
    toolBar.add(eraseButton);

    JButton rebootSTBButton = new JButton("Reboot STB");
    rebootSTBButton.setToolTipText("Reboot STB");
    rebootSTBButton.addActionListener(event -> {
      macWriter.rebootSTB();
    });
    toolBar.add(rebootSTBButton);

    toolBar.addSeparator();

    JCheckBox showRetArea = new JCheckBox("Show Ret");
    showRetArea.setToolTipText("Show XML String returned by STB");
    showRetArea.addActionListener(event -> {
      retArea.setVisible(showRetArea.isSelected());
    });
    toolBar.add(showRetArea);

    JCheckBox showInputDialog = new JCheckBox("Show Input Dialog");
    showInputDialog.setToolTipText("Display the SN and Mac input dialog");
    showInputDialog.addActionListener(event -> {
      inputDialog.setVisible(showInputDialog.isSelected());
    });
    toolBar.add(showInputDialog);

    add(toolBar, BorderLayout.NORTH); //add toolbar to frame
  }

  private void setMenuBar() {
    JMenuBar menuBar = new JMenuBar();

    // set Operation menu:
    JMenu operationMenu = new JMenu("Operation");
    JRadioButtonMenuItem repairMode = 
      new JRadioButtonMenuItem("Restoration Mode");
    repairMode.addActionListener(event -> {
      if (repairMode.isSelected()) {
        String password = JOptionPane.showInputDialog(menuBar,
            "Enter password to authenrize yourself:");
        if (password.equals("tsing")) { 
          macWriter.setRepairMode(repairMode.isSelected());
          toolBar.setVisible(macWriter.isRepairMode());
        } else {
          JOptionPane.showMessageDialog(this, "Wrong password!");
          repairMode.setSelected(false);
          macWriter.setRepairMode(false);
        }
      } else {
        macWriter.setRepairMode(false);
        toolBar.setVisible(false);
      }
    });

    JMenuItem exitItem = new JMenuItem("Exit");
    exitItem.addActionListener(event -> System.exit(0));

    operationMenu.add(repairMode);
    operationMenu.addSeparator();
    operationMenu.add(exitItem);

    // set Setting Menu
    JMenu settingMenu = new JMenu("Setting");
    JCheckBoxMenuItem onTop = new JCheckBoxMenuItem("OnTop");
    onTop.addActionListener(event -> {
      setAlwaysOnTop(onTop.isSelected());
    });

    JCheckBoxMenuItem showRetArea = 
      new JCheckBoxMenuItem("Show Ret Area");
    showRetArea.addActionListener(event -> {
      retArea.setVisible(showRetArea.isSelected());
    });


    settingMenu.add(onTop);
    settingMenu.add(showRetArea);
    
    //set Help menu
    JMenu helpMenu = new JMenu("Help");
    JMenuItem aboutItem = new JMenuItem("About");
    aboutItem.addActionListener(event -> {
      JOptionPane.showMessageDialog(this,
          "Mac Writer\nversion 1.0.0.0\nAuthor Tsing\n" + "Tel: 15285647630");
    });
    helpMenu.add(aboutItem);

    menuBar.add(operationMenu);
    menuBar.add(settingMenu);
    menuBar.add(helpMenu);

    setJMenuBar(menuBar);
  }

		
  private void setInputDialog() {
		inputDialog = new InputDialog(this, true);
		inputDialog.setVisible(true);
  }

	public void macWritingPerformed(MacWritingEvent e) {
    String cmd = e.getCmd();
    String status = e.getStatus();
    showInfo(cmd, status);

    if ((cmd.equals("write_mac_to_stb") && e.getStatus().equals("pass")) 
        || status.equals("fail"))
    {
      if (status.equals("pass")) {
        resultLabel.setForeground(Color.GREEN);
      } else {
        resultLabel.setForeground(Color.RED);
      }
      resultLabel.setText(status);
      
      snField.requestFocus(true);
      snField.setBorder(BorderFactory.createLineBorder(Color.RED, 5));
      macField.setBorder(null);
      snField.setText("");
      macField.setText("");
    }
  }

  private void showInfo(String cmd, String status) {
    String info = String.format("\n%s\t\t%s\n", cmd, status);
    Document doc = infoArea.getDocument();
    SimpleAttributeSet attrSet = new SimpleAttributeSet();
    try {
      if (status.equals("pass")) {
        StyleConstants.setForeground(attrSet, Color.GREEN);
      } else if (status.equals("fail")) {
        StyleConstants.setForeground(attrSet, Color.RED);
      } else {
        StyleConstants.setForeground(attrSet, Color.YELLOW);
      }
      doc.insertString(doc.getLength(), info, attrSet);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

	public class InputDialog extends JDialog {

		public InputDialog(Frame parent, boolean modal) {
			super(parent, modal);

			JPanel inputPanel = new JPanel();
			inputPanel.setLayout(new GridLayout(2, 2, 0, 10));

			JLabel snLabel = new JLabel("Input SN: ", SwingConstants.CENTER);
			snField = new JTextField(40);
			snField.setBorder(BorderFactory.createLineBorder(Color.RED, 5));

			JLabel macLabel = new JLabel("Input mac: ", SwingConstants.CENTER);
			macField = new JTextField(40);
			
			inputPanel.add(snLabel);
			inputPanel.add(snField);
			inputPanel.add(macLabel);
			inputPanel.add(macField);
			
			JButton closeButton = new JButton("X");
			closeButton.addActionListener(event -> {
				this.setVisible(false);
			});

			this.setLayout(new BorderLayout());
			this.add(inputPanel, BorderLayout.CENTER);
			this.add(closeButton, BorderLayout.SOUTH);

			this.pack();
			
			snField.addActionListener(new SnListener());
			macField.addActionListener(new MacListener());
		}

		class SnListener implements ActionListener {
			String sn;
			String mac;

			public void actionPerformed(ActionEvent e) {
				sn = snField.getText().trim();

				if (sn == null || sn.length() < 20) {
					JOptionPane.showMessageDialog(InputDialog.this,
							"Invalid SN, please check!",
							"Wrong SN",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

        snField.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5));
				macField.setFocusable(true);
				macField.requestFocus(true);
        macField.setBorder(BorderFactory.createLineBorder(Color.RED, 5));
			}
		}

    class MacListener implements ActionListener {
      String sn;
      String mac;
      public void actionPerformed(ActionEvent e) { 
        sn = snField.getText();
        mac = macField.getText().trim();

        if (mac == null || mac.length() < 12) {
          JOptionPane.showMessageDialog(InputDialog.this,
              "Invalid Mac, please check!",
              "Wrong Mac",
              JOptionPane.WARNING_MESSAGE);
          return;
        } // else { not implemented yet } 

        macField.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5));
        
        // clear JTextPane if the text length exceeds 20000;
        // not efficient becouse of the need to check per circle;
        Document doc = infoArea.getDocument();
        if (doc.getLength() > 20000) 
          infoArea.setText("");

        resultLabel.setForeground(Color.BLUE);
        resultLabel.setText("Testing...");
        
        WriteThread thread = new WriteThread(mac, sn);
        Thread writeThread = new Thread(thread);
        writeThread.start();
      }
    }

    class WriteThread implements Runnable {
      String mac;
      String sn;

      public WriteThread(String mac, String sn) {
        this.mac = mac;
        this.sn = sn;
      }

      public void run() {
        macWriter.setMac(mac, sn);
      }
    }
  }

	public static void main(String[] args) {
		IptvView view = new IptvView();
	}
}
