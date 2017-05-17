package UI;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.Vector;

import javax.swing.JTabbedPane;
import java.awt.GridBagConstraints;

public class ScorePanel extends JPanel {
	JTabbedPane tabbedPane;
	/**
	 * Create the panel.
	 * @return 
	 */
	public void showScore(File file){
		tabbedPane.removeAll();
		if(file.isDirectory()){
			for(String s:file.list()){
				ImagePanel panel = new ImagePanel();
				tabbedPane.add(s, panel);
				panel.showImage(file.getPath()+'\\'+s);
			}
		}else {
			ImagePanel panel = new ImagePanel();
			tabbedPane.add(file.getName(), panel);
			panel.showImage(file.getAbsolutePath());
		}
	}
	
	public ScorePanel(Vector<String> images) {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 0;
		add(tabbedPane, gbc_tabbedPane);
		if(images!=null){
		for(String path:images){
			ImagePanel panel = new ImagePanel();
			tabbedPane.add(path, panel);
			panel.showImage(path);
		}}

	}

}
