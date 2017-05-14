package UI;

import javax.swing.*;
import javax.swing.colorchooser.ColorChooserComponentFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Logger;

/**
 * Created by Bing on 4/25/17.
 */
public class AppMainWindow{
	JFrame mainFrame;
	JMenuBar menuBar;
	
    //private static JPanel mainPanel;
    public static ImagePanel imagePanel;
    public  static TextPanel textPanel;
    
    public static void main(String[] args){
    	EventQueue.invokeLater(new Runnable() {	
			@Override
			public void run() {
				new AppMainWindow();//init the UI.
			}
		});
    }

    public AppMainWindow(){
    	Logger.getGlobal().info("==================AppInitStart\n");
    	
        mainFrame = new JFrame("Score Demo");
        mainFrame.setLayout(new BorderLayout());
        try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        initMenuBar();
        
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        JTabbedPane tabbedPane = new JTabbedPane();

    	textPanel = new TextPanel();
    	tabbedPane.addTab("TEXT", textPanel);
    	
    	imagePanel = new ImagePanel();
    	tabbedPane.addTab("IMAGE", imagePanel);
   
    	mainFrame.add(tabbedPane);
       
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
    private void initMenuBar(){
    	menuBar = new JMenuBar();
    	
        JMenu fileMenu = new JMenu("File");//file menu.
        JMenuItem omxmlMenuItem = new JMenuItem("Open MusicXML...");
        omxmlMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
				int returnVal = fc.showOpenDialog(mainFrame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            textPanel.showText(file);
		            Logger.getGlobal().info("Opening: " + file.getName() + ".\n");
		        } else {
		        	Logger.getGlobal().info("Open command cancelled by user.\n");
		        }
			}
		});
        JMenuItem oimgMenuItem = new JMenuItem("Open image...");
        oimgMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
				int returnVal = fc.showOpenDialog(mainFrame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            
		            imagePanel.initAndShowUI(file);
		            Logger.getGlobal().info("Opening: " + file.getName() + ".\n");
		        } else {
		        	Logger.getGlobal().info("Open command cancelled by user.\n");
		        }
			}
		});
        
        fileMenu.add(oimgMenuItem);
        fileMenu.add(omxmlMenuItem);
        menuBar.add(fileMenu);
        
        mainFrame.setJMenuBar(menuBar);
    }
}
