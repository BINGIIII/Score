package UI;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import OMR.JianScore;
import logic.Lilyond;

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
import java.awt.BorderLayout;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

public class LyEditor extends JPanel {
	
	TextPanel lyText;
	ImagePanel lyImage;
	
	public void setFile(File file){
		lyImage.clear();
		lyText.setText(file); 
	}
	
	public LyEditor() {
		setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane, BorderLayout.CENTER);
		
		lyText = new TextPanel();
		tabbedPane.addTab("LilyPond", null, lyText, null);
		//lyText.setLayout(new BorderLayout(0, 0));
		
		lyImage = new ImagePanel();
		tabbedPane.addTab("Preview", null, lyImage, null);
		//lyImage.setLayout(new BorderLayout(0, 0));
	}

}
