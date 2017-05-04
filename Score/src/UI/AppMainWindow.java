package UI;

import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by Bing on 4/25/17.
 */
public class AppMainWindow extends JFrame{
    private static JPanel mainPanel;
    public static PdfPanel pdfPanel;
    public  static TextPanel textPanel;

    public AppMainWindow(){
    	super("Score Demo");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem importMenuItem = new JMenuItem("Import");
        importMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File("./"));
                int result = chooser.showOpenDialog(AppMainWindow.this);
                if(result == JFileChooser.APPROVE_OPTION){
                }
            }
        });
        fileMenu.add(importMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        //mainPanel = new JPanel(true);
        //mainPanel.setBackground(Color.blue);

        //textPanel = new TextPanel("a.txt");
        pdfPanel = new PdfPanel("a.pdf");
        //mainPanel.add(Panel);

        //add(textPanel);
        add(pdfPanel);
        
        setLocationByPlatform(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);

    }
}
