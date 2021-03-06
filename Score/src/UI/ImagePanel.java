package UI;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.channels.ShutdownChannelGroupException;
import java.util.Calendar;
import java.util.logging.Logger;

import javax.swing.JScrollPane;
import java.awt.GridBagConstraints;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.Insets;
import java.awt.MenuBar;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ImagePanel extends JPanel {
	MyImagePanel imagePanel;

	/**
	 * Create the panel.
	 */
	public static void main(String[] args){
		JFrame frame = new JFrame();
		ImagePanel imagePanel = new ImagePanel();
		frame.add(imagePanel);
		JMenuItem oimgMenuItem = new JMenuItem("������...");
		JMenuBar menuBar = new JMenuBar();
		oimgMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
				int returnVal = fc.showOpenDialog(frame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					imagePanel.showImage(file.getAbsolutePath());
				} else {
					Logger.getGlobal().info("Open command cancelled by user.\n");
				}
			}
		});
		menuBar.add(oimgMenuItem);
		frame.setJMenuBar(menuBar);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.pack();
		frame.setVisible(true);
	}
	public void clear(){
		imagePanel.clear();
	}
	public void showImage(String name){
		clear();
		imagePanel.showImage(name,50);
		updateUI();
		//repaint();
	}
	
	public ImagePanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JSlider slider = new JSlider();
		slider.setMinimum(10);
		slider.setValue(50);
		slider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
		        if (!source.getValueIsAdjusting()) {
		            int fps = (int)source.getValue();
		            imagePanel.showImage(fps);
		        }
			}
		});
		slider.setOrientation(SwingConstants.VERTICAL);
		GridBagConstraints gbc_slider = new GridBagConstraints();
		gbc_slider.fill = GridBagConstraints.VERTICAL;
		gbc_slider.anchor = GridBagConstraints.EAST;
		gbc_slider.gridx = 0;
		gbc_slider.gridy = 0;
		add(slider, gbc_slider);
		
		JScrollPane scrollPane = new JScrollPane();
		imagePanel = new MyImagePanel();
		scrollPane.setViewportView(imagePanel);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 0;
		add(scrollPane, gbc_scrollPane);
		

	}
	
	class MyImagePanel extends JPanel{
		int size=50;
	    private BufferedImage image;
	    public void clear(){
	    	image = null;
	    	repaint();
	    }
	    public void showImage(int size){
	    	setPreferredSize(new Dimension(image.getWidth()*size/100, image.getHeight()*size/100));
	    	this.size = size;
			repaint();
	    }
	    public void showImage(String name,int size){
	    	try {
	    		File file = new File(name);
				image = ImageIO.read(file);
				if(image==null){
					return;//not image
				}
				setPreferredSize(new Dimension(image.getWidth()*size/100, image.getHeight()*size/100));
				this.size = size;
				repaint();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

	    @Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        if(image!=null){
	        g.drawImage(image, 0, 0, image.getWidth()*size/100, image.getHeight()*size/100, this);
	        }
	        //g.drawImage(image, 0, 0, this); // see javadoc for more info on the parameters            
	    }

	}
}
