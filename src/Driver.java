//Based off Andrew Davison's open source project

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.io.*;

public class Driver extends JFrame {

	private UserTracker trackPanel;
	public static ChuckConnector CHUCK_CONNECTOR = new ChuckConnector();//TODO make sure this works!
	public Driver() {
		super("User Tracker");
		
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		try {
			CHUCK_CONNECTOR.start();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		trackPanel = new UserTracker();
		c.add(trackPanel, BorderLayout.CENTER);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) 
			{ trackPanel.closeDown(); }
		});
		
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
	} 
	
	
	public static void main(String args[]) {
		new Driver();
	}
	
	
	
}
