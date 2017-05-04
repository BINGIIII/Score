package UI;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by Bing on 4/25/17.
 */
public class TextPanel extends JPanel{
    //JScrollPane scrollPane;
    JTextArea text;
    JScrollPane scrollPane;
    public TextPanel(String filePath){
        super(new BorderLayout());
        try {
            text = new JTextArea(20,100);
            //text.setLineWrap(true);
            //text.setWrapStyleWord(true);
            scrollPane = new JScrollPane(text,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            this.add(scrollPane,BorderLayout.CENTER);
            FileReader reader = new FileReader(filePath);
            BufferedReader br = new BufferedReader(reader);
            text.read(br,null);
            //text.setBackground(Color.red);
            br.close();
            //System.out.println(text.ge+" "+text.getLineCount());
            //text.setColumns(text.getColumns());
            //text.setRows(text.getRows());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
