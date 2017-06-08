package UI;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.colorchooser.ColorChooserComponentFactory;

import OMR.JianScore;
import OMR.Score;
import logic.Lilyond;
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
    	
        mainFrame = new JFrame("Score");//init main frame and main panel.
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
        mainFrame.pack();
        mainFrame.setVisible(true);
        
        scorePanel = new ScorePanel();//init score panel, ly editor and xml panel.
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
		            GlobalVariable.setXmlPath(file.getAbsolutePath());
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
		            GlobalVariable.setScoreDir(file.getAbsolutePath());//set score path.
		            mainPanel.removeAll();
		            mainPanel.add(scorePanel);
		            scorePanel.showScore(file);//show sheets.
		            mainPanel.updateUI();
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

        fileMenu.addSeparator();
        JMenuItem savely = new JMenuItem("Save LilyPond File...");
        fileMenu.add(savely);
        JMenuItem saveImage = new JMenuItem("Save Sheets...");
        fileMenu.add(saveImage);
        JMenuItem saveMidi = new JMenuItem("Save MIDI File...");
        fileMenu.add(saveMidi);
        
        menuBar.add(fileMenu);
        //==============================================================Step menu
        JMenu stepMenu = new JMenu("Step");
        JMenuItem stave2midi = new JMenuItem("Convert Stave to MIDI");
        stave2midi.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(GlobalVariable.getScoreDir()!=null){//get score path from global variable.
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
						
						new SwingWorker<Integer, Integer>() {

							@Override
							protected Integer doInBackground() throws Exception {
								// TODO Auto-generated method stub
								new Score(GlobalVariable.getScoreDir(), num).convert2lilypond("x.ly");
								return 0;
							}

							@Override
							protected void done() {
								// TODO Auto-generated method stub
								super.done();
								scorePanel.showData(new File("output"));
								Lilyond.generate("x.ly");
								GlobalVariable.setLilypondPath("x.ly");
								scorePanel.showData(new File("output"));
								scorePanel.showResult("result");
								scorePanel.showMidiPlayer("result/x.mid");

//								scorePanel.showMidiPlayer();
								//Logger.getGlobal().info("score2ly");
								//lyEditor.setFile(new File("x.ly"));
								//mainPanel.removeAll();
								//mainPanel.add(lyEditor);
								//mainPanel.updateUI();
								//Logger.getGlobal().info("show lyEditor\n");
							}
							
						}.execute();
						/*new Score(GlobleVariable.getScoreDir(), num).convert2lilypond("x.ly");
						Logger.getGlobal().info("score2ly");
						lyEditor.setFile(new File("x.ly"));
						mainPanel.removeAll();
						mainPanel.add(lyEditor);
						mainPanel.updateUI();
						Logger.getGlobal().info("show lyEditor\n");*/
								
						
						
					}
				}
				
			}
		});
        JMenuItem xml2ly = new JMenuItem("Convert MusicXML to Lilypond");
        xml2ly.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(GlobalVariable.getXmlPath()!=null){
					new XML2lyCompiler().parser(GlobalVariable.getXmlPath(), "y.ly");
					lyEditor.setFile(new File("y.ly"));
					mainPanel.removeAll();
					mainPanel.add(lyEditor);
					mainPanel.updateUI();
				}
			}
		});
        JMenuItem jian2stave = new JMenuItem("Convert Jianpu to Stave");
        jian2stave.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				if(GlobalVariable.getScoreDir()!=null){
					Object[] possibilities = {"1", "2", "3","4"};
					String s = (String)JOptionPane.showInputDialog(
					                    mainFrame,
					                    "Part Number :",
					                    "Part Number",
					                    JOptionPane.PLAIN_MESSAGE,
					                    null,
					                    possibilities,      
					                    "1");
					if(s!=null){
						int num = Integer.parseInt(s);
						
						new SwingWorker<Integer, Integer>() {

							@Override
							protected Integer doInBackground() throws Exception {
								// TODO Auto-generated method stub
								new JianScore(GlobalVariable.getScoreDir(), num).convert2ly("x.ly");
								return 0;
							}

							@Override
							protected void done() {
								super.done();
								Lilyond.generate("x.ly");
								GlobalVariable.setLilypondPath("x.ly");
								scorePanel.showData(new File("output"));
								scorePanel.showResult("result");
								scorePanel.showMidiPlayer("result/x.mid");
							}
							
						}.execute();
					}
				}
				
			}
		});

        stepMenu.addSeparator();
        stepMenu.add(stave2midi);
        stepMenu.addSeparator();
        stepMenu.add(jian2stave);
        stepMenu.addSeparator();
        stepMenu.add(xml2ly);
        stepMenu.addSeparator();
        
        menuBar.add(stepMenu);
        //==============================================================View menu        
        JMenu viewMenu = new JMenu("View");
        JMenuItem showLyEditor = new JMenuItem("Show Lilypond Edior");
        showLyEditor.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				mainPanel.removeAll();
				if(GlobalVariable.getLilypondPath()!=null){
					lyEditor.setFile(new File(GlobalVariable.getLilypondPath()));
				}
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
