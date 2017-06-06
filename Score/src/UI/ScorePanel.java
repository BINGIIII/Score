package UI;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.Vector;

import javax.swing.JTabbedPane;
import java.awt.GridBagConstraints;
import java.awt.BorderLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Insets;
import java.awt.FlowLayout;
import java.awt.GridLayout;

public class ScorePanel extends JPanel {
	JTabbedPane mainPanel;
	JTabbedPane rawPanel;//raw image.
	JTabbedPane dataPanel;//OMR view
	TextPanel lyPanel;
	TextPanel jiaPanel;
	MIDIPanel midiPanel;//midi player
	JTabbedPane outPanel;

	public void showResult(File file){
		mainPanel.addTab("Output", null, outPanel, null);
		outPanel.removeAll();
		if(file.isDirectory()){
			for(String s:file.list()){
				if(!s.substring(s.length()-4).equals(".png")){
					continue;
				}
				ImagePanel panel = new ImagePanel();
				outPanel.add(s, panel);
				panel.showImage(file.getPath()+'\\'+s);
			}
		}else {
			ImagePanel panel = new ImagePanel();
			outPanel.add(file.getName(), panel);
			panel.showImage(file.getAbsolutePath());
		}
	}

	public void showMidiPlayer(String filename){
		midiPanel.setFile(filename);
		add(midiPanel, BorderLayout.EAST);
	}
	public void showData(File file){
		mainPanel.addTab("Date", null, dataPanel, "OMR data");
		dataPanel.removeAll();
		if(file.isDirectory()){
			for(String s:file.list()){
				ImagePanel panel = new ImagePanel();
				dataPanel.add(s, panel);
				panel.showImage(file.getPath()+'\\'+s);
			}
		}else {
			ImagePanel panel = new ImagePanel();
			dataPanel.add(file.getName(), panel);
			panel.showImage(file.getAbsolutePath());
		}
	}
	public void showScore(File file){
		mainPanel.addTab("Image", null, rawPanel, null);
		rawPanel.removeAll();
		if(file.isDirectory()){
			for(String s:file.list()){
				ImagePanel panel = new ImagePanel();
				rawPanel.add(s, panel);
				panel.showImage(file.getPath()+'\\'+s);
			}
		}else {
			ImagePanel panel = new ImagePanel();
			rawPanel.add(file.getName(), panel);
			panel.showImage(file.getAbsolutePath());
		}
	}
	
	public ScorePanel(Vector<String> images) {
		setLayout(new BorderLayout(0, 0));
		
		mainPanel = new JTabbedPane(JTabbedPane.TOP);
		add(mainPanel, BorderLayout.CENTER);
		
		rawPanel = new JTabbedPane(JTabbedPane.TOP);
//		mainPanel.addTab("Image", null, rawPanel, null);
		
		dataPanel = new JTabbedPane(JTabbedPane.TOP);
//		mainPanel.addTab("Date", null, dataPanel, null);
	
		outPanel = new JTabbedPane(JTabbedPane.TOP);
	
		midiPanel = new MIDIPanel();
	
		if(images!=null){
		for(String path:images){
			ImagePanel panel = new ImagePanel();
			rawPanel.add(path, panel);
			panel.showImage(path);
		}}

	}

}
