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

  // MacWriter macWriter;
  XmlParser xmlParser;
  Logger logger;
  DBConnector dbConnector;
  XmlWriter xmlWriter;

  public IptvView() {
		/*
    xmlParser = IptvXmlParser(); // initialize xmlParser 
    dbConnector = new DBConnector(); //~~ DBConnector unimplemented yet 
    macWriter = new MacWriter(xmlParser, dbConnector); // initialize macWriter
    
    xmlWriter = new IptvXmlWriter(); //initialize xmlWriter 
    logger = new IptvLogger(xmlWriter); //initialize logger 

    macWriter.addMacWritingListener(this); // register IptvView with macWriter
    macWriter.addMacWritingListener(logger); // register logger with macWriter
		*/
		
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

		JLabel statusLabel = new JLabel("PASS", SwingConstants.CENTER);
		statusLabel.setFont(new Font("Serif", Font.BOLD, 48));
		statusLabel.setForeground(Color.GREEN);

		JScrollPane scrollPane = new JScrollPane(infoArea);
    panel.add(scrollPane, BorderLayout.CENTER);
		panel.add(statusLabel, BorderLayout.SOUTH);
    panel.add(retArea, BorderLayout.NORTH);

    add(panel, BorderLayout.CENTER);  // add panel to frame
  }

  private void setToolBar() {
    // set toolbar:
    toolBar = new JToolBar();
    toolBar.setBorder(BorderFactory.createLineBorder(Color.CYAN, 2));
    toolBar.setEnabled(false);
    JButton getButton = new JButton("Get Mac");
    getButton.setToolTip("get SN and Mac from STB");
    getButton.addActionListener(event -> {
      macWriter.getMac();
    });
    JButton getAdvButton = new JButton("Get Adv");
    getAdvButton.setToolTip("get adv-security from STB");
    getAdvButton.addActionListener(event -> {
      macWriter.checkAdv();
    });
    JButton setAdvButton = new JButton("Enable Adv");
    setAdvButton.setToolTip("Enable adv-security");
    setAdvButton.addActionListener(event -> {
      macWriter.setAdv();
    });
    JButton setButton = new JButton("Set Mac");
    setButton.setToolTip("Write Mac and SN to STB");
    setButton.addActionListener(event -> {
      // unimplemented
    });
    JButton eraseButton = new JButton("Erase Mac");
    eraseButton.setToolTip("Erase mac from STB");
    setButton.addActionListener(event -> {
      macWriter.eraseMac();
    });
    JButton rebootSTBButton = new JButton("Reboot STB");
    rebootSTBButton.setToolTip("Reboot STB");
    rebootSTBButton.addActionListener(event -> {
      macWriter.rebootSTB();
    });

    add(toolBar, BorderLayout.NORTH); //add toolbar to frame
  }

  private void setMenuBar() {
    JMenuBar menuBar = new JMenuBar();

    // set Operation menu:
    JMenu operationMenu = new JMenu("Operation");
    JRadioButtonMenuItem repairMode = 
      new JRadioButtonMenuItem("Restoration Mode");
    repairMode.addActionListener(event -> {
      String password = JOptionPane.showInputDialog(this,
          "Enter password to authenrize yourself:");
      if (password == "tsing") 
        macWriter.setRepairMode(repairMode.isSelected());
      else 
        JOptionPane.showMessageDialog(this, "Wrong password!");
    });

    JMenuItem exitItem = new JMenuItem("Exit");
    exitItem.addActionListener(event -> System.exit(0));

    JRadioButtonMenuItem engineerMode = new JMenuItem("Engineer");
    engineerMode.addActionListener(event -> {
      String password =  JOptionPane.showInputDialog(this, 
          "Enter password ot enter engineer mode:");
      if (password == "tsing") {
        toolBar.setEnabled(engineerMode.isSelected());
      } else {
        JOptionPane.showMessageDialog(this, "Wrong password!");
      }
    });

    operationMenu.add(repairMode);
    operationMenu.add(exitItem);
    operationMenu.add(engineerMode);

    // set Setting Menu
    JMenu settingMenu = new JMenu("Setting");
    JRadioButtonMenuItem onTop = new JRadioButtonMenuItem("OnTop");
    onTop.addActionListener(event -> {
      setAlwaysOnTop(ontop.isSelected());
    });

    JRadioButtonMenuItem showRetArea = 
      new JRadioButtonMenuItem("Show Ret Area");
    showRetArea.addActionListener(event -> {
      retArea.setVisible(showRetArea.isSelected());
    });

    JRadioButtonMenuItem showToolBar = 
      new JRadioButtonMenuItem("Show Toolbar"); 
    showToolBar.addActionListener(event -> {
      toolBar.setVisible(showToolBar.isSelected());
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
    // unimplemented yet
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
			/*
			macField.addActionListener(new MacListener());
			*/
		}

		class SnListener implements ActionListener {
			String sn;
			String mac;

			public void actionPerformed(ActionEvent e) {
				sn = snField.getText().trim();

				if (sn == null || sn.length() < 25) {
					JOptionPane.showMessageDialog(InputDialog.this,
							"Invalidate SN, please Check!",
							"Wrong SN",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				macField.setFocusable(true);
				macField.requestFocus(true);
			}
		}
	}

	public static void main(String[] args) {
		IptvView view = new IptvView();
	}
}
