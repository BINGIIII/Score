package UI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SettingPanel extends JFrame{
	public SettingPanel(){
		setTitle("Setting");
		JPanel mainPanel = new JPanel();
		mainPanel.setPreferredSize(new Dimension(400, 200));
		JLabel label = new JLabel("lilypond path:");
		JTextField textField = new JTextField();
		textField.setColumns(20);
		JButton button = new JButton("Browse...");
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			    fc.setAcceptAllFileFilterUsed(false);
				fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
				int returnVal = fc.showOpenDialog(SettingPanel.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            textField.setText(file.getPath());
		        } 
			}
		});
		JButton confirm = new JButton("Apply");
		confirm.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				GlobleVariable.setLilypondPath(textField.getText());
				SettingPanel.this.dispose();
			}
		});
		
		mainPanel.add(label);
		mainPanel.add(textField);
		mainPanel.add(button);
		mainPanel.add(confirm);
		
		setContentPane(mainPanel);
		//setDefaultCloseOperation();
		pack();
		setVisible(true);
	}
}
