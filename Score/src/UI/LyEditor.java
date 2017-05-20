package UI;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.swing.JButton;

public class LyEditor extends JPanel {
	
	TextPanel lyText;
	ImagePanel lyImage;
	
	public void setFile(File file){
		lyImage.clear();
		lyText.setText(file); 
	}
	
	public LyEditor() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		lyText = new TextPanel();
		GridBagConstraints gbc_lyText = new GridBagConstraints();
		gbc_lyText.insets = new Insets(0, 0, 0, 5);
		gbc_lyText.fill = GridBagConstraints.BOTH;
		gbc_lyText.gridx = 0;
		gbc_lyText.gridy = 0;
		add(lyText, gbc_lyText);
		
		JPanel lyRight = new JPanel();
		GridBagConstraints gbc_lyRight = new GridBagConstraints();
		gbc_lyRight.fill = GridBagConstraints.BOTH;
		gbc_lyRight.gridx = 1;
		gbc_lyRight.gridy = 0;
		add(lyRight, gbc_lyRight);
		GridBagLayout gbl_lyRight = new GridBagLayout();
		gbl_lyRight.columnWidths = new int[]{0, 0};
		gbl_lyRight.rowHeights = new int[]{0, 0, 0};
		gbl_lyRight.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_lyRight.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		lyRight.setLayout(gbl_lyRight);
		
		JButton lyGenerate = new JButton("Preview");
		lyGenerate.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				PrintWriter out = null;
				try {
					out = new PrintWriter(new File("temply.ly"));
					out.print(lyText.getText());
					out.close();
					Logger.getGlobal().info("crete temp ly");
					logic.Lilyond.generate("temply.ly");
					Logger.getGlobal().info("create temp png");
					lyImage.showImage("./temply.png");
					Logger.getGlobal().info("show temp png");
				} catch (FileNotFoundException  e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		GridBagConstraints gbc_lyGenerate = new GridBagConstraints();
		gbc_lyGenerate.insets = new Insets(0, 0, 5, 0);
		gbc_lyGenerate.gridx = 0;
		gbc_lyGenerate.gridy = 0;
		lyRight.add(lyGenerate, gbc_lyGenerate);
		
		lyImage = new ImagePanel();
		GridBagConstraints gbc_lyImage = new GridBagConstraints();
		gbc_lyImage.fill = GridBagConstraints.BOTH;
		gbc_lyImage.gridx = 0;
		gbc_lyImage.gridy = 1;
		lyRight.add(lyImage, gbc_lyImage);
	}

}
