package OMR;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.swing.SwingUtilities;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageProcessor {
	
	public static void main(String[] args) {
		File dir = new File("output");
		for (String fName : dir.list()) {
			new File("output/" + fName).delete();
		}
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Vector<Mat> sheets = getSheets("jianpu");
		for(Mat mat:sheets){
			Vector<Integer> peakLoc = new Vector<>();

			Mat temp = mat.clone();
			//Core.bitwise_not(mat, temp);

			Mat vector = new Mat();
			Core.reduce(temp, vector, 1, Core.REDUCE_AVG);

			int threshold = 100;
			double[] white = new double[1];
			white[0] = 255;
			
			List<MatOfPoint> contours = ImageProcessor.findContours(mat);
			contours.sort(new Comparator<MatOfPoint>() {

				@Override
				public int compare(MatOfPoint o1, MatOfPoint o2) {
					Rect rect1 = Imgproc.boundingRect(o1);
					Rect rect2 = Imgproc.boundingRect(o2);
					if(Math.abs(rect1.y-rect2.y)<60){
						return rect1.x-rect2.x;
					}else {
						return rect1.y-rect2.y;
					}
				}
			});
			
			Imgproc.cvtColor(temp, temp, Imgproc.COLOR_GRAY2BGR);
			for(MatOfPoint c:contours){
				Rect rect = Imgproc.boundingRect(c);
				Mat src = mat.submat(rect);
				if(rect.height>=25&&rect.width>=10&&rect.height<=45&&rect.width<=45){
					Mat note1 = ImageProcessor.getTemplate("template/1.png", rect.width,rect.height);
					Mat matchResult = ImageProcessor.templatematch(src, note1, 0.3);
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						//Imgproc.rectangle(temp, rect.br(), rect.tl(), new Scalar(0,0,255));
						System.out.print("1 ");
						continue;
					}
					Mat note2 = ImageProcessor.getTemplate("template/2.png", rect.width,rect.height);
					matchResult = ImageProcessor.templatematch(src, note2, 0.5);
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						///Imgproc.rectangle(temp, rect.br(), rect.tl(), new Scalar(0,0,255));
						System.out.print("2 ");
						continue;
					}
					Mat note3 = ImageProcessor.getTemplate("template/3.png", rect.width,rect.height);
					matchResult = ImageProcessor.templatematch(src, note3, 0.5);
					if (Core.minMaxLoc(matchResult).maxVal > 0.3) {
						//Imgproc.rectangle(temp, rect.br(), rect.tl(), new Scalar(0,0,255));
						System.out.print("3 ");
						continue;
					}
					Mat note4 = ImageProcessor.getTemplate("template/4.png", rect.width,rect.height);
					matchResult = ImageProcessor.templatematch(src, note4, 0.5);
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						//Imgproc.rectangle(temp, rect.br(), rect.tl(), new Scalar(0,0,255));
						System.out.print("4 ");
						continue;
					}
					Mat note5 = ImageProcessor.getTemplate("template/5.png", rect.width,rect.height);
					matchResult = ImageProcessor.templatematch(src, note5, 0.5);
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						//Imgproc.rectangle(temp, rect.br(), rect.tl(), new Scalar(0,0,255));
						System.out.print("5 ");
						continue;
					}
					Mat note6 = ImageProcessor.getTemplate("template/6.png", rect.width,rect.height);
					matchResult = ImageProcessor.templatematch(src, note6, 0.3);
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						//Imgproc.rectangle(temp, rect.br(), rect.tl(), new Scalar(0,0,255));
						System.out.print("6 ");
						continue;
					}
					Mat note7 = ImageProcessor.getTemplate("template/7.png", rect.width,rect.height);
					matchResult = ImageProcessor.templatematch(src, note7, 0.5);
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						//Imgproc.rectangle(temp, rect.br(), rect.tl(), new Scalar(0,0,255));
						System.out.print("7 ");
						continue;
					}
					//Imgcodecs.imwrite("output/img"+i+++".png", mat.submat(rect));
				}
				
			}
			System.out.println();
			//imshow(temp);
		}
	}
	
	public static BufferedImage mat2image(Mat mat) {
		BufferedImage img;
		if (mat != null) {
			int cols = mat.cols();
			int rows = mat.rows();
			int elemSize = (int) mat.elemSize();
			byte[] data = new byte[cols * rows * elemSize];
			int type;
			mat.get(0, 0, data);
			switch (mat.channels()) {
			case 1:
				type = BufferedImage.TYPE_BYTE_GRAY;
				break;
			case 3:
				type = BufferedImage.TYPE_3BYTE_BGR;
				// bgr to rgb
				byte b;
				for (int i = 0; i < data.length; i = i + 3) {
					b = data[i];
					data[i] = data[i + 2];
					data[i + 2] = b;
				}
				break;
			default:
				return null;
			}
			img = new BufferedImage(cols, rows, type);
			img.getRaster().setDataElements(0, 0, cols, rows, data);
		} else { // mat was null
			img = null;
		}
		return img;
	}

	public static void imshow(Mat image) {
		//SwingUtilities.invokeLater(new Runnable() {
			//@Override
			//public void run() {
				new ImageViewer(mat2image(image));
			//}
		//});
	}

	public static Mat getTemplate(String fName, int width, int height) {
		Mat template = Imgcodecs.imread(fName);
		// ImageProcessor.imshow(template);
		Imgproc.resize(template, template, new Size(width, height));
		Imgproc.cvtColor(template, template, Imgproc.COLOR_RGB2GRAY);
		Imgproc.threshold(template, template, 100, 255, Imgproc.THRESH_BINARY);

		return template;
	}

	public static Vector<Mat> getSheets(String dirName) {
		//to do: need some step to phase noise !!!
		// rgb2gray and gray2bw.
		File directory = new File(dirName);
		String[] fileList = directory.list();

		Vector<Mat> sheets = new Vector<>();

		for (String s : fileList) {
			Mat mat = Imgcodecs.imread(dirName + "/" + s);
			Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);
			//Imgproc.GaussianBlur(mat, mat, new Size(3,3),0);
			Imgproc.threshold(mat, mat, 200, 255, Imgproc.THRESH_BINARY);
			sheets.add(mat);
		}
		return sheets;
	}

	public static List<MatOfPoint> findContours(Mat mat) {
		Mat temp = new Mat();
		Core.bitwise_not(mat, temp);
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(temp, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		return contours;
	}

	public static Mat templatematch(Mat src, Mat template,double threshold) {
		//remove muti math!!!
		Mat matchResult = new Mat();
		if(src.cols()<template.cols()||src.rows()<template.rows()){
			matchResult = Mat.zeros(src.size(), CvType.CV_8UC1);
		}else{
		Imgproc.matchTemplate(src, template, matchResult, Imgproc.TM_CCOEFF_NORMED);
		double max = Core.minMaxLoc(matchResult).maxVal;
		Imgproc.threshold(matchResult, matchResult, threshold, 255, Imgproc.THRESH_BINARY);
		for(int i=0;i<matchResult.rows();++i){
			for(int j=0;j<matchResult.cols();++j){ 
				if (matchResult.get(i, j)[0]>0.3) {
					for(int m=-5;m<=5;++m){
						for(int n=-5;n<=5;++n){
							if(!(m==0&&n==0)&&(i+m<matchResult.rows()&&i+m>=0)&&
									(j+n<matchResult.cols()&&j+n>=0)&&
									matchResult.get(i+m, j+n)[0]>0.3){
								matchResult.put(i, j, 0);
							}
						}
					}				
				}
				
			}
		}}

		return matchResult;
	}

	public static Vector<Integer> findPeakY(Mat mat,boolean flag) {
		//to do: big arg problem, maybe can count length rather than projection.
		//usage 1:remove bar line???maybe set flag when needed.
		//usage 2:compute stem location
		Vector<Integer> peakLoc = new Vector<>();

		Mat temp = mat.clone();
		Core.bitwise_not(temp, temp);
		Mat vector = new Mat();
		Core.reduce(temp, vector, 0, Core.REDUCE_AVG);
		double threshold = (255*mat.rows()*0.4)/mat.rows();
		double[] white = new double[1];
		white[0] = 255;

		for (int i = 0; i < vector.cols(); ++i) {//cols
			if (vector.get(0, i)[0] > threshold) {// 1 is line
				if(flag){
				for (int j = 0; j < mat.rows(); ++j) {
					//if (!(Math.abs(mat.get(i - 3, j)[0]) < 5 || Math.abs(mat.get(i + 3, j)[0]) < 5)) {
						mat.put(j, i, white);
					//}
				}}
				if (i==0||vector.get(0, i-1)[0] > threshold) {// only catch the
															// first pixel
					peakLoc.add(i);
				}

			} else {
				// peakLoc.add(0);
			}
		}

		// imshow(temp);

		return peakLoc;
	}

	public static Vector<Integer> findPeaks(Mat mat) {
		Vector<Integer> peakLoc = new Vector<>();

		Mat temp = new Mat();
		mat.copyTo(temp);

		Mat vector = new Mat();
		Core.reduce(temp, vector, 1, Core.REDUCE_AVG);

		int threshold = 100;
		double[] white = new double[1];
		white[0] = 255;

		for (int i = 0; i < vector.rows(); ++i) {
			if (vector.get(i, 0)[0] < threshold) {// 1 is line
				for (int j = 0; j < mat.cols(); ++j) {
					if (!(Math.abs(mat.get(i - 3, j)[0]) < 5 || Math.abs(mat.get(i + 3, j)[0]) < 5)) {
						mat.put(i, j, white);
					}
				}
				if (vector.get(i - 1, 0)[0] > threshold) {// only catch the
															// first pixel.
					peakLoc.add(i+1);//
				}

			} else {
				// peakLoc.add(0);
			}
		}

		// imshow(temp);

		return peakLoc;
	}
}