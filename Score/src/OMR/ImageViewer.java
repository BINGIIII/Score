package OMR;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Bing on 4/27/17.
 */
public class ImageViewer extends JFrame{	
	BufferedImage image;
	
	public ImageViewer(BufferedImage img){
		image = img;
		
		initAndShowUI();
	}
	
    public ImageViewer(String filename){
    	File file = new File(filename);
    	try {
			image = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	initAndShowUI();
        
    }
    void initAndShowUI(){
    	JScrollPane scrollPane = new JScrollPane(new MyComponent(),JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(600,500));

        //scrollPane
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(scrollPane,BorderLayout.CENTER);

        this.setContentPane(panel);

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }

    private class MyComponent extends JComponent{
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            g.drawImage(image,0,0,null);//,20,20,300,300,null);
            setPreferredSize(new Dimension(image.getWidth(null),image.getHeight(null)));
            
        }
    }
}
