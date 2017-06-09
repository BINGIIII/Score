package UI;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;

import javax.swing.JScrollPane;
import java.awt.GridBagConstraints;
import javax.swing.JTextArea;
import java.awt.BorderLayout;

public class TextPanel extends JPanel {

	JTextArea textArea;
	public TextPanel() {
		setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane);
		
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
	}
	public void setText(File file){
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			textArea.read(br, null);
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void setText(String text){
		textArea.setText(text);
	}
	public String getText(){
		return textArea.getText();
	}
}
