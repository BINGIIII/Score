package UI;

import javax.imageio.ImageIO;

//import com.adobe.acrobat.Viewer;

import javax.swing.*;

import org.ghost4j.document.DocumentException;
import org.ghost4j.document.PDFDocument;
import org.ghost4j.renderer.RendererException;
import org.ghost4j.renderer.SimpleRenderer;

import OMR.ImageViewer;

import java.util.List;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Bing on 4/25/17.
 */
public class PdfPanel extends JPanel{

    public PdfPanel(String filePath){
    	 PDFDocument document = new PDFDocument();
    	 try {
			document.load(new File("a.pdf"));
			SimpleRenderer renderer = new SimpleRenderer();
		    // set resolution (in DPI)
		    renderer.setResolution(300);
		    List<Image> images = renderer.render(document);
		    for(Image img:images){
		    	SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						new ImageViewer((BufferedImage) img);
					}
				});
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RendererException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
