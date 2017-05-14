package OMR;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.SwingUtilities;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageProcessor {
	public static void main(String[] args){
		Vector<Mat> sheets = getSheets("jianpu");
		for(Mat m:sheets){
			imshow(m);
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

	public ImageProcessor() {
		// image = Imgcodecs.imread("a.png");
		// Imgcodecs.imwrite("temp.png", image);
		// new ImageViewer("temp.png");
	}

	public static void imshow(Mat image) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new ImageViewer(mat2image(image));
			}
		});
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
			Imgproc.threshold(mat, mat, 100, 255, Imgproc.THRESH_BINARY);
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
		Mat matchResult = new Mat();
		Imgproc.matchTemplate(src, template, matchResult, Imgproc.TM_CCOEFF_NORMED);
		//MinMaxLocResult maxResult = Core.minMaxLoc(matchResult);
		//double maxval = Core.minMaxLoc(matchResult).maxVal;
		//System.out.println(maxval);
		//imshow(src);
		Imgproc.threshold(matchResult, matchResult, threshold, 255, Imgproc.THRESH_BINARY);
		
		//remove the multiple match.
		//Vector<Point> noteLoc = new Vector<>();
		for(int i=0;i<matchResult.rows();++i){
			for(int j=0;j<matchResult.cols();++j){ 
				if (matchResult.get(i, j)[0]>0.3) {
					for(int m=-3;m<=3;++m){
						for(int n=-3;n<=3;++n){
							if(!(m==0&&n==0)&&(i+m<matchResult.rows()&&i+m>=0)&&
									(j+n<matchResult.cols()&&j+n>=0)&&
									matchResult.get(i+m, j+n)[0]>0.3){
								matchResult.put(i, j, 0);
							}
						}
					}				
				}
				
			}
		}
		// System.out.println("max : "+ max);
		/*
		 * for(int i=0;i<result.rows();++i){ for(int j=0;j<result.cols();++j){
		 * //System.out.println(result.get(i, j)[0]); if(result.get(i,
		 * j)[0]>0.6){ Imgproc.rectangle(src, new Point(j, i),new
		 * Point(j+26,i+22), new Scalar(0,0,255)); }
		 * //System.out.println(result.get(i, j)[0]); } }
		 */
		//imshow(src);

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

class Segment {
	//segment loc in page
	int segmentStart;
	int segmentEnd;
	
	int noteHeadHight;
	int partNum;
	//part info
	Vector<Integer> partMid;
	int segmentMid;
	
	Mat image;
	Vector<Integer> lineLoc;
	public int getPartNum() {
		return partNum;
	}


	public void setPartNum(int partNum) {
		this.partNum = partNum;
	}


	public Vector<Integer> getPartMid() {
		return partMid;
	}


	public void setPartMid(Vector<Integer> partMid) {
		this.partMid = partMid;
	}


	public Mat getImage() {
		return image;
	}
	

	public int getSegmentMid() {
		return segmentMid;
	}


	public void setSegmentMid(int segmentMid) {
		this.segmentMid = segmentMid;
	}


	public Vector<Integer> getLineLoc() {
		return lineLoc;
	}


	public void setLineLoc(Vector<Integer> lineLoc) {
		this.lineLoc = lineLoc;
	}


	public void setImage(Mat image) {
		this.image = image;
	}

	public int getPartIdByY(int y){
		int difMin = Math.abs(getPartMid().get(0) -y);
		int partid = 0;
		for(int i=1;i<getPartMid().size();++i){
			if(Math.abs(getPartMid().get(i)-y)<difMin){
				difMin = Math.abs(getPartMid().get(i)-y);
				partid = i;
			}
			
		}
		return partid;
	}
	public Segment(Mat image,int mid,Vector<Integer> loc,int noteHeadHight,int partNum,int start,int end) {
		segmentStart = start;
		segmentEnd = end;
		this.partNum = partNum;
		this.noteHeadHight = noteHeadHight;
		this.image = image;
		segmentMid = mid;
		lineLoc = loc;
		partMid  = new Vector<>();
		for(int i=0;i<loc.size();i+=5){
			partMid.add((loc.get(i+1)+loc.get(i+2))/2);
		}
		// this.lineLoc = lineLoc;
	}
}
class NoteGroup{
	int partid;
	boolean isSingle;
	boolean isDown;
	//int noteNum;
	Vector<Point> noteHeadLoc;//sort by x index.
	Rect pos;
	
	
	
	public NoteGroup(int partid, boolean isSingle, boolean isDown, Vector<Point> noteHeadLoc, Rect pos) {
		super();
		this.partid = partid;
		this.isSingle = isSingle;
		this.isDown = isDown;
		this.noteHeadLoc = noteHeadLoc;
		this.pos = pos;
	}
	public int getPartid() {
		return partid;
	}
	public void setPartid(int partid) {
		this.partid = partid;
	}
	public boolean isSingle() {
		return isSingle;
	}
	public void setSingle(boolean isSingle) {
		this.isSingle = isSingle;
	}
	public boolean isDown() {
		return isDown;
	}
	public void setDown(boolean isDown) {
		this.isDown = isDown;
	}
	public Vector<Point> getNoteHeadLoc() {
		return noteHeadLoc;
	}
	public void setNoteHeadLoc(Vector<Point> noteHeadLoc) {
		this.noteHeadLoc = noteHeadLoc;
	}
	public Rect getPos() {
		return pos;
	}
	public void setPos(Rect pos) {
		this.pos = pos;
	}
	
}
