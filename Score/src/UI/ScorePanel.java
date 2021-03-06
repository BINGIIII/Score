package UI;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.Vector;

import javax.swing.JTabbedPane;
import javax.xml.soap.Text;

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
	//MIDIPanel midiPanel;//midi player
	JTabbedPane outPanel;//result panel.
	TextPanel lyPanel;
	TextPanel jianPanel;
//	File rawFile;
//	File resultFile;
//	File dataFile;
//	File midiFile;
	public String  getly(){
		return lyPanel.getText();
	}
	public String getjian(){
		return jianPanel.getText();
	}
	public void showResult(String path){
		File file = new File(path);
		outPanel.removeAll();
		if(file.isDirectory()){
			for(String s:file.list()){
				if(!s.substring(s.length()-4).equals(".png")){
					continue;
				}
				ImagePanel panel = new ImagePanel();
				outPanel.add(s, panel);
				panel.showImage(path+'\\'+s);
			}
		}else {
			ImagePanel panel = new ImagePanel();
			outPanel.add(file.getName(), panel);
			panel.showImage(file.getAbsolutePath());
		}
	}

	/*public void showMidiPlayer(String filename){
		midiPanel.setFile(filename);
	}*/
	public void showData(File file){
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
	public void showLy(File file){
		lyPanel.setText(file);
	}
	public void showjian(File file){
		jianPanel.setText(file);
	}
	
	public ScorePanel() {
		setLayout(new BorderLayout(0, 0));
		
		mainPanel = new JTabbedPane(JTabbedPane.TOP);//init main tabbed panel
		add(mainPanel, BorderLayout.CENTER);
		
		lyPanel = new TextPanel();
		jianPanel = new TextPanel();
		mainPanel.addTab("LilyPond", null, lyPanel, null);
		mainPanel.addTab("简谱文本", null, jianPanel, null);
		rawPanel = new JTabbedPane(JTabbedPane.TOP);//init all 4 panel
		mainPanel.addTab("乐谱", null, rawPanel, null);
		dataPanel = new JTabbedPane(JTabbedPane.TOP);
		mainPanel.addTab("数据", null, dataPanel, "OMR data");
		outPanel = new JTabbedPane(JTabbedPane.TOP);
		mainPanel.addTab("输出", null, outPanel, null);
		//midiPanel = new MIDIPanel();
		//add(midiPanel, BorderLayout.EAST);
	}

}
