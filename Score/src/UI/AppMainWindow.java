package UI;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.colorchooser.ColorChooserComponentFactory;

import OMR.Score;
import logic.XML2lyCompiler;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by Bing on 4/25/17.
 */
public class AppMainWindow{
	JFrame mainFrame;
	JPanel mainPanel;
	JMenuBar menuBar;
	
	File lilypondFile;
	LyEditor lyEditor;
	ScorePanel scorePanel;
	TextPanel xmlPanel;
	
    //private static JPanel mainPanel;
    //public static ImagePanel imagePanel;
    //public  static TextPanel textPanel;
    
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
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainFrame.add(mainPanel);
        try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        initMenuBar();
        
        mainFrame.setMinimumSize(new Dimension(800, 600));
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        /*JTabbedPane tabbedPane = new JTabbedPane();

    	textPanel = new TextPanel();
    	tabbedPane.addTab("TEXT", textPanel);
    	
    	imagePanel = new ImagePanel();
    	tabbedPane.addTab("IMAGE", imagePanel);
   
    	mainFrame.add(tabbedPane);*/
       
        mainFrame.pack();
        mainFrame.setVisible(true);
        
        
        scorePanel = new ScorePanel(null);
        lyEditor = new LyEditor();
        xmlPanel = new TextPanel();
    }
    private void initMenuBar(){
    	menuBar = new JMenuBar();
    	//==============================================================File menu
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
		            GlobleVariable.setXmlPath(file.getAbsolutePath());
		            mainPanel.removeAll();
		            mainPanel.add(xmlPanel);
		            xmlPanel.setText(file);
		            mainPanel.updateUI();
		            //textPanel.showText(file);
		            Logger.getGlobal().info("Opening: " + file.getName() + ".\n");
		        } else {
		        	Logger.getGlobal().info("Open command cancelled by user.\n");
		        }
			}
		});
        JMenuItem oimgMenuItem = new JMenuItem("Open Sheets...");
        oimgMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
				int returnVal = fc.showOpenDialog(mainFrame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            GlobleVariable.setScoreDir(file.getAbsolutePath());
		            mainPanel.removeAll();
		            mainPanel.add(scorePanel);
		            scorePanel.showScore(file);
		            mainPanel.updateUI();
		            //imagePanel.initAndShowUI(file);
		            Logger.getGlobal().info("Opening: " + file.getName() + ".\n");
		        } else {
		        	Logger.getGlobal().info("Open command cancelled by user.\n");
		        }
			}
		});
        fileMenu.add(oimgMenuItem);
        fileMenu.add(omxmlMenuItem);
        
        JMenuItem openly = new JMenuItem("Open Lilypond File...");
        openly.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
				int returnVal = fc.showOpenDialog(mainFrame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            lilypondFile = fc.getSelectedFile();
		            mainPanel.removeAll();
		            mainPanel.add(lyEditor);
		            lyEditor.setFile(lilypondFile);
		            mainPanel.updateUI();
		            Logger.getGlobal().info("Opening: " + lilypondFile.getName() + ".\n");
		        }
			}
		});
        fileMenu.add(openly);
        menuBar.add(fileMenu);
        //==============================================================Step menu
        JMenu stepMenu = new JMenu("Step");
        JMenuItem loadStave = new JMenuItem("Load Stave...");
        JMenuItem loadjianpu = new JMenuItem("Load Jianpu...");
        JMenuItem stave2xml = new JMenuItem("Convert Stave to MusicXML");
        JMenuItem stave2ly = new JMenuItem("Convert Stave to Lilypond");
        stave2ly.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(GlobleVariable.getScoreDir()!=null){
					Object[] possibilities = {"1", "2", "3","4"};
					String s = (String)JOptionPane.showInputDialog(
					                    mainFrame,
					                    "Part Number :",
					                    "Customized Dialog",
					                    JOptionPane.PLAIN_MESSAGE,
					                    null,
					                    possibilities,      
					                    "1");
					if(s!=null){
						int num = Integer.parseInt(s);
						new Score(GlobleVariable.getScoreDir(), num).convert2lilypond("x.ly");
						Logger.getGlobal().info("score2ly");
						lyEditor.setFile(new File("x.ly"));
						mainPanel.removeAll();
						mainPanel.add(lyEditor);
						mainPanel.updateUI();
						Logger.getGlobal().info("show lyEditor\n");
					}
				}
				
			}
		});
        JMenuItem xml2ly = new JMenuItem("Convert MusicXML to Lilypond");
        xml2ly.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(GlobleVariable.getXmlPath()!=null){
					new XML2lyCompiler().parser(GlobleVariable.getXmlPath(), "y.ly");
					lyEditor.setFile(new File("y.ly"));
					mainPanel.removeAll();
					mainPanel.add(lyEditor);
					mainPanel.updateUI();
				}
			}
		});
        JMenuItem ly2stave = new JMenuItem("Engrave ly to Stave");
        JMenuItem ly2jianpu = new JMenuItem("Engrave ly to Jianpu");
        JMenuItem ly2midi = new JMenuItem("Produce MIDI From ly");
        stepMenu.add(loadStave);
        stepMenu.add(loadjianpu);
        stepMenu.addSeparator();
        stepMenu.add(stave2ly);
        stepMenu.add(stave2xml);
        stepMenu.addSeparator();
        stepMenu.add(xml2ly);
        stepMenu.addSeparator();
        stepMenu.add(ly2stave);
        stepMenu.add(ly2jianpu);
        stepMenu.add(ly2midi);
        
        menuBar.add(stepMenu);
        //==============================================================View menu        
        JMenu viewMenu = new JMenu("View");
        JMenuItem showLyEditor = new JMenuItem("Show Lilypond Edior");
        showLyEditor.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				mainPanel.removeAll();
				mainPanel.add(lyEditor);
				mainPanel.updateUI();
				Logger.getGlobal().info("show lyEditor\n");
			}
		});
        viewMenu.add(showLyEditor);
        
        JMenuItem showScore = new JMenuItem("Show Score View");
        showScore.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				mainPanel.removeAll();
				mainPanel.add(scorePanel);
				mainPanel.updateUI();
				Logger.getGlobal().info("show scorePanel\n");
			}
		});
        viewMenu.add(showScore);
        
        JMenuItem xmlView = new JMenuItem("Show MusicXML View");
        xmlView.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				mainPanel.removeAll();
				mainPanel.add(xmlPanel);
				mainPanel.updateUI();
				Logger.getGlobal().info("show xmlPanel\n");
			}
		});
        viewMenu.add(xmlView);
        
        menuBar.add(viewMenu);
        //==============================================================Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem settingMenuItem = new JMenuItem("Setting");
        settingMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new SettingPanel();
			}
		});
        helpMenu.add(settingMenuItem);
        menuBar.add(helpMenu);
        

        
        mainFrame.setJMenuBar(menuBar);
    }
}
