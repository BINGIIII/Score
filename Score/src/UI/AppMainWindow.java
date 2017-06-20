package UI;

import javax.imageio.ImageIO;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.swing.*;
import javax.swing.colorchooser.ColorChooserComponentFactory;

import org.apache.commons.io.FileUtils;
import org.python.antlr.ast.Global;

import OMR.JianScore;
import OMR.Score;
import logic.Compiler;
import logic.Lilyond;
import logic.XML2lyCompiler;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.channels.NonWritableChannelException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Created by Bing on 4/25/17.
 */
public class AppMainWindow {
	JFrame mainFrame;
	JPanel mainPanel;
	JMenuBar menuBar;
	File lilypondFile;
	LyEditor lyEditor;
	ScorePanel scorePanel;
	TextPanel xmlPanel;
	Sequencer sequencer;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new AppMainWindow();// init the UI.
			}
		});
	}

	public AppMainWindow() {
		Logger.getGlobal().info("==================AppInitStart\n");

		mainFrame = new JFrame("Score");// init main frame and main panel.
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

		scorePanel = new ScorePanel();// init score panel, ly editor and xml
										// panel.
		lyEditor = new LyEditor();
		xmlPanel = new TextPanel();
	}

	private void initMenuBar() {
		menuBar = new JMenuBar();
		// ==============================================================File
		// menu
		JMenu fileMenu = new JMenu("文件");// file menu.
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
					// textPanel.showText(file);
					Logger.getGlobal().info("Opening: " + file.getName() + ".\n");
				} else {
					Logger.getGlobal().info("Open command cancelled by user.\n");
				}
			}
		});
		JMenuItem oimgMenuItem = new JMenuItem("打开乐谱...");
		oimgMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
				int returnVal = fc.showOpenDialog(mainFrame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					GlobalVariable.setScoreDir(file.getAbsolutePath());// set
																		// score
																		// path.
					mainPanel.removeAll();
					mainPanel.add(scorePanel);
					scorePanel.showScore(file);// show sheets.
					mainPanel.updateUI();
					Logger.getGlobal().info("Opening: " + file.getName() + ".\n");
				} else {
					Logger.getGlobal().info("Open command cancelled by user.\n");
				}
			}
		});
		fileMenu.add(oimgMenuItem);
//		fileMenu.add(omxmlMenuItem);

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
//		fileMenu.add(openly);
		JMenuItem svly = new JMenuItem("保存");
		svly.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new SwingWorker<Integer, Integer>() {

					@Override
					protected Integer doInBackground() throws Exception {
						try{
							if(GlobalVariable.getLilypondPath()!=null){
						    PrintWriter writer = new PrintWriter(GlobalVariable.getLilypondPath(), "UTF-8");
						    writer.write(scorePanel.getly());
						    writer.close();}else {
						    	GlobalVariable.setLilypondPath("ly.ly");
						    	PrintWriter writer = new PrintWriter("ly.ly", "UTF-8");
							    writer.write(scorePanel.getly());
							    writer.close();
							}
						    if(GlobalVariable.getJianputextPath()!=null){
						    PrintWriter writer = new PrintWriter(GlobalVariable.getJianputextPath(), "UTF-8");
						    writer.write(scorePanel.getjian());
						    writer.close();
						    }else{
						    	GlobalVariable.setJianputextPath("jian.txt");
						    	PrintWriter writer = new PrintWriter("jian.txt", "UTF-8");
							    writer.write(scorePanel.getjian());
							    writer.close();
						    }
						} catch (IOException e) {
						   // do something
						}
						return 0;
					}

					@Override
					protected void done() {
						super.done();
					}

				}.execute();
				
				
			}
		});
		fileMenu.add(svly);
		fileMenu.addSeparator();
		JMenuItem savely = new JMenuItem("导出LilyPond文件...");
		savely.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
				int returnVal = fc.showSaveDialog(mainFrame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File fileToSave = fc.getSelectedFile();
					try {
						Files.copy(new File("x.ly").toPath(), fileToSave.toPath(),
								java.nio.file.StandardCopyOption.REPLACE_EXISTING);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		fileMenu.add(savely);
		JMenuItem saveImage = new JMenuItem("导出乐谱...");
		saveImage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
				int returnVal = fc.showSaveDialog(mainFrame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File fileToSave = fc.getSelectedFile();
					try {
						File result = new File("result");
						Files.copy(result.toPath(), fileToSave.toPath(),
								java.nio.file.StandardCopyOption.REPLACE_EXISTING);
						FileUtils.copyDirectory(result, fileToSave);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}

			}
		});
		fileMenu.add(saveImage);
		JMenuItem saveMidi = new JMenuItem("导出MIDI文件...");
		saveMidi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
				int returnVal = fc.showSaveDialog(mainFrame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File fileToSave = fc.getSelectedFile();
					try {
						Files.copy(new File("result/x.mid").toPath(), fileToSave.toPath(),
								java.nio.file.StandardCopyOption.REPLACE_EXISTING);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}

			}
		});
		fileMenu.add(saveMidi);

		menuBar.add(fileMenu);
		// ==============================================================Step
		// menu
		//
		JMenu stepMenu = new JMenu("转换");
		JMenuItem stave2ly = new JMenuItem("五线谱转LilyPond");// for
																		// play
		stave2ly.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {// get stave path from
														// global,rewrite
														// lilypond file, set
														// lilypond path.
				if (GlobalVariable.getScoreDir() != null) {// get score path
					// from global
					// variable.
					Object[] possibilities = { "1 C", "2 C", "1 2/4", "2 2/4" };
					String s = (String) JOptionPane.showInputDialog(mainFrame, "Part Number :", "Customized Dialog",
							JOptionPane.PLAIN_MESSAGE, null, possibilities, "1");
					if (s != null) {
						int num = 0;

						switch (s) {
						case "1 C":
							num= 1;
							GlobalVariable.setBeats(1);
							break;
						case "2 C":
							num= 2;
							GlobalVariable.setBeats(1);
							break;
						case "1 2/4":
							num= 1;
							GlobalVariable.setBeats(2);
							break;
						case "2 2/4":
							num= 2;
							GlobalVariable.setBeats(2);
							break;

						default:
							break;
						}
						int temp =num; 
						GlobalVariable.setPartNum(num);

						new SwingWorker<Integer, Integer>() {

							@Override
							protected Integer doInBackground() throws Exception {
								new Score(GlobalVariable.getScoreDir(),temp).convert2lilypond("x.ly");
								return 0;
							}

							@Override
							protected void done() {
								super.done();
								//scorePanel.showData(new File("output"));
								GlobalVariable.setLilypondPath("x.ly");
								scorePanel.showLy(new File("x.ly"));
								scorePanel.showData(new File("output"));
								//scorePanel.showResult("result");
							}

						}.execute();
					}
				}
			}
		});
		stepMenu.add(stave2ly);

		JMenuItem ly2midi = new JMenuItem("LilyPond转MIDI");// get
																				// ly
																				// from
																				// mem
																				// or
																				// from
																				// disk,set
																				// midi
																				// path
		ly2midi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (GlobalVariable.getLilypondPath() != null) {
					new SwingWorker<Integer, Integer>() {
						@Override
						protected Integer doInBackground() throws Exception {
							Lilyond.generate(GlobalVariable.getLilypondPath());// outpu
																				// dir
																				// is
																				// result
							return 0;
						}

						@Override
						protected void done() {
							super.done();
							GlobalVariable.setMidiPath("result/x.mid");
							scorePanel.showData(new File("output"));
						}
					}.execute();

				}
			}
		});
		stepMenu.add(ly2midi);

		JMenuItem ly2stave = new JMenuItem("LilyPond转乐谱");
		
		ly2stave.addActionListener(new ActionListener() {// get stave from
															// disk,set jianpu
															// path
			@Override
			public void actionPerformed(ActionEvent e) {
				if (GlobalVariable.getLilypondPath() != null) {
					new SwingWorker<Integer, Integer>() {
						@Override
						protected Integer doInBackground() throws Exception {
							Lilyond.generate(GlobalVariable.getLilypondPath());// outpu
																				// dir
																				// is
																				// result
							return 0;
						}

						@Override
						protected void done() {
							super.done();
							scorePanel.showResult("result");
						}
					}.execute();
				}
			}
		});

		JMenuItem stave2jianputext = new JMenuItem("五线谱转简谱文本");
		stave2jianputext.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (GlobalVariable.getScoreDir() != null) {// get score path
					// from global
					// variable.
					Object[] possibilities = { "1 C", "2 C", "1 2/4", "2 2/4" };
					String s = (String) JOptionPane.showInputDialog(mainFrame, "Part Number :", "Customized Dialog",
							JOptionPane.PLAIN_MESSAGE, null, possibilities, "1");
					if (s != null) {
						int num = 0;

						switch (s) {
						case "1 C":
							num= 1;
							GlobalVariable.setBeats(1);
							break;
						case "2 C":
							num= 2;
							GlobalVariable.setBeats(1);
							break;
						case "1 2/4":
							num= 1;
							GlobalVariable.setBeats(2);
							break;
						case "2 2/4":
							num= 2;
							GlobalVariable.setBeats(2);
							break;

						default:
							break;
						}
						int temp =num; 
						GlobalVariable.setPartNum(num);

						new SwingWorker<Integer, Integer>() {

							@Override
							protected Integer doInBackground() throws Exception {
								new Score(GlobalVariable.getScoreDir(),temp).convert2jianpu("x.txt");
								return 0;
							}

							@Override
							protected void done() {
								super.done();
								//scorePanel.showData(new File("output"));
								GlobalVariable.setJianputextPath("x.txt");
								scorePanel.showjian(new File("x.txt"));
								scorePanel.showData(new File("output"));
								//scorePanel.showData(new File("output"));
								//scorePanel.showResult("result");
							}

						}.execute();
					}
				}
				
			}
		});
		JMenuItem jianputext2ly = new JMenuItem("简谱文本转LilyPond");
		jianputext2ly.addActionListener(new ActionListener() {// get jianpu frommem ordisk,set lilypath and lily mem
			@Override
			public void actionPerformed(ActionEvent e) {
				//
				
				new SwingWorker<Integer, Integer>() {

					@Override
					protected Integer doInBackground() throws Exception {
						logic.Compiler.jianpu2ly(GlobalVariable.getJianputextPath(), "x.ly");
				
						return 0;
					}

					@Override
					protected void done() {
						super.done();
						GlobalVariable.setLilypondPath("x.ly");
						scorePanel.showLy(new File(GlobalVariable.getLilypondPath()));
						//scorePanel.showData(new File("output"));
						//scorePanel.showResult("result");
					}

				}.execute();
			}
		});

		JMenuItem jianpu2ly = new JMenuItem("简谱转LilyPond");
		jianpu2ly.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {// get image from
														// disk,set lily mem and
														// path.
				if (GlobalVariable.getScoreDir() != null) {
					Object[] possibilities = { "1", "2", "3", "4" };
					String s = (String) JOptionPane.showInputDialog(mainFrame, "Part Number :", "Customized Dialog",
							JOptionPane.PLAIN_MESSAGE, null, possibilities, "1");
					if (s != null) {
						int num = Integer.parseInt(s);
						new SwingWorker<Integer, Integer>() {
							@Override
							protected Integer doInBackground() throws Exception {
								new JianScore(GlobalVariable.getScoreDir(), num).convert2ly("x.ly");
								GlobalVariable.setLilypondPath("x.ly");
								return 0;
							}

							@Override
							protected void done() {
								super.done();
								scorePanel.showLy(new File("x.ly"));
								scorePanel.showData(new File("output"));
							}
						}.execute();
					}
				}
			}
		});

		stepMenu.add(ly2stave);
		stepMenu.add(stave2jianputext);
		stepMenu.add(jianputext2ly);
		stepMenu.add(jianpu2ly);
		//stepMenu.addSeparator();
		JMenuItem stave2midi = new JMenuItem("五线谱转MIDI");
		stave2midi.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (GlobalVariable.getScoreDir() != null) {// get score path
															// from global
															// variable.
					Object[] possibilities = { "1", "2", "3", "4" };
					String s = (String) JOptionPane.showInputDialog(mainFrame, "Part Number :", "Customized Dialog",
							JOptionPane.PLAIN_MESSAGE, null, possibilities, "1");
					if (s != null) {
						int num = Integer.parseInt(s);

						new SwingWorker<Integer, Integer>() {

							@Override
							protected Integer doInBackground() throws Exception {
								new Score(GlobalVariable.getScoreDir(), num).convert2lilypond("x.ly");
								return 0;
							}

							@Override
							protected void done() {
								super.done();
								scorePanel.showData(new File("output"));
								Lilyond.generate("x.ly");
								GlobalVariable.setLilypondPath("x.ly");
								scorePanel.showData(new File("output"));
								scorePanel.showResult("result");
							}

						}.execute();
					}
				}

			}
		});
		JMenuItem xml2ly = new JMenuItem("Convert MusicXML to Lilypond");
		xml2ly.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (GlobalVariable.getXmlPath() != null) {
					new XML2lyCompiler().parser(GlobalVariable.getXmlPath(), "y.ly");
					lyEditor.setFile(new File("y.ly"));
					mainPanel.removeAll();
					mainPanel.add(lyEditor);
					mainPanel.updateUI();
				}
			}
		});
		JMenuItem jian2stave = new JMenuItem("简谱转五线谱");
		jian2stave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (GlobalVariable.getScoreDir() != null) {
					Object[] possibilities = { "1", "2", "3", "4" };
					String s = (String) JOptionPane.showInputDialog(mainFrame, "Part Number :", "Part Number",
							JOptionPane.PLAIN_MESSAGE, null, possibilities, "1");
					if (s != null) {
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
								// scorePanel.showMidiPlayer("result/x.mid");
							}

						}.execute();
					}
				}

			}
		});

		//stepMenu.add(stave2midi);
		//stepMenu.add(jian2stave);
		//stepMenu.add(xml2ly);

		menuBar.add(stepMenu);
		// ==============================================================View
		// menu
		JMenu viewMenu = new JMenu("视图");
		JMenuItem showLyEditor = new JMenuItem("打开Lilypond编辑器");
		showLyEditor.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mainPanel.removeAll();
				if (GlobalVariable.getLilypondPath() != null) {
					lyEditor.setFile(new File(GlobalVariable.getLilypondPath()));
				}
				mainPanel.add(lyEditor);
				mainPanel.updateUI();
				Logger.getGlobal().info("show lyEditor\n");
			}
		});
		viewMenu.add(showLyEditor);

		JMenuItem showScore = new JMenuItem("打开乐谱视图");
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
//		viewMenu.add(xmlView);

		menuBar.add(viewMenu);
		// ==============================================================play
		// menu
		JMenu playMenu = new JMenu("播放");
		JMenuItem play = new JMenuItem("播放");
		play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Logger.getGlobal().info("paly menu");

				try {
					if (GlobalVariable.getMidiPath() != null) {
						if (sequencer == null) {

							InputStream is = new BufferedInputStream(new FileInputStream(new File("result/x.mid")));
							sequencer = MidiSystem.getSequencer();
							sequencer.open();
							sequencer.setSequence(is);
						}
						sequencer.start();
					}
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (MidiUnavailableException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InvalidMidiDataException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

		JMenuItem pause = new JMenuItem("暂停");
		pause.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (sequencer != null) {
					Logger.getGlobal().info("pause menu");
					sequencer.stop();
				}
			}
		});
		JMenuItem stop = new JMenuItem("停止");
		stop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (sequencer != null) {
					Logger.getGlobal().info("stop menu");
					sequencer.close();
					sequencer = null;
				}
			}
		});
		playMenu.add(play);
		playMenu.add(pause);
		playMenu.add(stop);
		menuBar.add(playMenu);
		// ==============================================================Help
		// menu
		JMenu helpMenu = new JMenu("帮助");
		JMenuItem settingMenuItem = new JMenuItem("设置");
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
