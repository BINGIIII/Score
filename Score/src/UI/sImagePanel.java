package UI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ImagePanel extends JPanel{
	BufferedImage image;
	JScrollPane scrollPane;
	
	
	public ImagePanel() {
		super(new BorderLayout());
	}
	
	void initAndShowUI(File img){
		try {
			image = ImageIO.read(img);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	scrollPane = new JScrollPane(new MyComponent(),JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(600,500));

        //scrollPane
        

        add(scrollPane,BorderLayout.CENTER);

 
    }

    private class MyComponent extends JComponent{
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            int hight = image.getHeight(null)<500?image.getHeight(null):1000;
            int width = image.getWidth(null)/image.getHeight(null)*hight;
            g.drawImage(image,0,0,width,hight,null);
            setPreferredSize(new Dimension(image.getWidth(null),image.getHeight(null)));       
        }
    }
}
