package UI;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by Bing on 4/25/17.
 */
public class TextPanel extends JPanel{
    //JScrollPane scrollPane;
    JTextArea text;
    JScrollPane scrollPane;
    public TextPanel(){
    	super(new BorderLayout());
    }
    public void showText(File file){
    	try {
            text = new JTextArea(20,100);
            scrollPane = new JScrollPane(text,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            this.add(scrollPane,BorderLayout.CENTER);
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            
            text.read(br,null);
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public TextPanel(File file){
        super(new BorderLayout());
        try {
            text = new JTextArea(20,100);
            scrollPane = new JScrollPane(text,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            this.add(scrollPane,BorderLayout.CENTER);
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            
            text.read(br,null);
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
