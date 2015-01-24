//Based off Andrew Davison's open source project

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

public class Driver extends JFrame {

	private UserTracker trackPanel;
	
	public Driver() {
		super("User Tracker");
		
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		
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
