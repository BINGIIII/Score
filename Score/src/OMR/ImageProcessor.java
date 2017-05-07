package OMR;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.sound.midi.Receiver;
import javax.sql.RowSet;
import javax.swing.SwingUtilities;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
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
	//Vector<Mat> pages;

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

	/*public static void creatLilypondFile(String dirName,String outFileName){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		PrintWriter out = null;
		
		//File f = new File(filename);
        try {
			 out = new PrintWriter(new FileWriter(outFileName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        out.print("{\\time 2/4");

		Vector<Mat> sheets = getSheets(dirName);
		
		//Vector<Note> notes = new Vector<>();

		for (Mat m : sheets) {
			Vector<Segment> segments = getStaveSegment(m,2);
			for (Segment segment : segments) {	
				//Mat out = segment.getImage().clone();	
				//Imgproc.cvtColor(out, out, Imgproc.COLOR_GRAY2BGR);											
				Vector<NoteGroup> groups = getNoteGroup(segment);
				
				//view the process.
				Mat canvas = segment.getImage().clone();	
				Imgproc.cvtColor(canvas, canvas, Imgproc.COLOR_GRAY2BGR);
				for(NoteGroup g:groups){
					Rect rect = g.getPos();
					Vector<Point> headLoc = g.getNoteHeadLoc();
					Imgproc.rectangle(canvas, rect.tl(), rect.br(), new Scalar(0,0,255));
					for(Point p:headLoc){
						Imgproc.circle(canvas, p, 10, new Scalar(0,255,0));
					}		
				}
				imshow(canvas);
				
				for(int index=0;index<groups.size();++index){
					if(groups.get(index).getPartid()==0){
						Vector<Integer> pinchs = getPinch(segment, groups.get(index));
						Vector<Integer> durations = getDuration(segment, groups.get(index));
					
						for(int i=0;i<pinchs.size();++i){
							//c' is  middle c, that means 1 is middle c.
							int pinch = pinchs.get(i);
							if(pinch>0){
								int dotnum = pinch/7 +1;
								int step = pinch%7;
								char c='c';
								switch (step){
								case 1:
									c = 'c';
									break;
								case 2:	c = 'd';
									break;
								case 3:	c = 'e';
									break;
								case 4:	c = 'f';
									break;
								case 5:	c = 'g';
									break;
								case 6:	c = 'a';
									break;
								case 7:	c = 'b';
									break;

								default:
									break;
								}
								out.print(c);
								for(int j=0;j<dotnum;++j){
									out.print('\'');
								}
								out.print(durations.get(i)+" ");
							}else {
								;
							}
						}
					}	
				}
			}
			
		}
		out.print("}");
		out.close();
		try {
			Runtime.getRuntime().exec("C:\\Program Files (x86)\\LilyPond\\usr\\bin\\lilypond a.ly");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	} */
	public static void visualize(String dirName) {
		//segment info
		//note group
		//note head
		//stave line
		//	
	}
	/*public static void main(String[] args) {
		creatLilypondFile("The Moment","a.ly");
	}*/
	/*public static Vector<Integer> getPinch(Segment segment,NoteGroup g){
		Vector<Integer> pinchs = new Vector<>();
		for(Point p:g.getNoteHeadLoc()){
			int partid = g.getPartid();
			Vector<Integer> tempLinLoc = new Vector<>();
			for(int i =4;i>0;--i ){
				tempLinLoc.add(segment.getLineLoc().get(5*partid)-19*i);
			}
			
			for(int i =0;i<5;++i ){
				tempLinLoc.add(segment.getLineLoc().get(i+5*partid));
			}
			
			for(int i =1;i<5;++i ){
				tempLinLoc.add(segment.getLineLoc().get(4+5*partid)+19*i);
			}
			for(int i=12;i>0;--i){
				tempLinLoc.add(i, (tempLinLoc.get(i)+tempLinLoc.get(i-1))/2);
			}
			int mindif = 100000;
			int pinch = 20;
			for(int y:tempLinLoc){
				int dif = (int) Math.abs(p.y-y);
				if(dif<mindif){
					mindif = dif;
					pinch--;
				}else {
					break;
				}
			}
			pinchs.add(pinch);
		}
		return pinchs;
	}*/
	
	/*public static Vector<Integer> getDuration(Segment segment,NoteGroup g){
		Vector<Integer> durations = new Vector<>();
		//Mat out = segment.getImage();
		if(g.isDown){
			for(Point p:g.getNoteHeadLoc()){
				int left=0,right=0;
				int leftcounter=0;
				boolean leftinbalck = true;
				boolean lefthead = true;
				int rightcounter=0;
				boolean rightinbalck = false;
				//boolean righthead = true;
				boolean leftAdded = false;
				boolean rightAdded = false;
				for(int i=(int) p.y;i>g.getPos().y;--i){//bottom to top.
					//count left
					if(segment.getImage().get(i, (int) p.x)[0]<0.2){//black
						if(lefthead){
							//continue;
						}else {
							if(leftinbalck){
								leftcounter++;
								if(leftcounter>3&&!leftAdded){
									left++;
									leftAdded = true;
								}
								;//do nothing.
							}else{
								leftinbalck = true;//first in black.
								leftcounter++;
							}
						}
					}else{//white.
						leftAdded = false;
						lefthead =false;
						if(!leftinbalck){
							//continue;
						}else {
							leftinbalck = false;
						}
					}
					//count right.
					int headWidth = 20;
					if(segment.getImage().get(i, (int) p.x+headWidth)[0]<0.2){//black
						
							if(rightinbalck){
								rightcounter++;
								if(rightcounter>3&&!rightAdded){
									right++;
									rightAdded = true;
								}
								;//do nothing.
							}else{
								rightinbalck = true;//first in black.
								rightcounter++;
							}
					
					}else{//white.
						rightAdded = false;
						//righthead =false;
						if(!rightinbalck){
							continue;
						}else {
							rightinbalck = false;
						}
					}
				}
				if(left>right){
					durations.add((int) Math.pow(2, left+2));
				}else {
					durations.add((int) Math.pow(2, right+2));
				}
			}
			
		}else {
			
			for(Point p:g.getNoteHeadLoc()){
				
				int left=0,right=0;
				int leftcounter=0;
				boolean leftinbalck = false;
				//boolean lefthead = true;
				int rightcounter=0;
				boolean rightinbalck = true;
				boolean righthead = true;
				boolean leftAdded = false;
				boolean rightAdded = false;
				for(int i=(int) p.y;i<g.getPos().y+g.getPos().height;++i){//top to bottom
					//Imgproc.line(out, new Point((int) p.x, g.getPos().y),new Point((int) p.x, g.getPos().y+g.getPos().height),  new Scalar(0,0,255));
					//count right
					if(segment.getImage().get(i, (int) p.x)[0]<0.2){//black
						
						if(righthead){
							//continue;
						}else {
							if(rightinbalck){
								rightcounter++;
								if(rightcounter>3&&!rightAdded){
									right++;
									rightAdded = true;
								}
								;//do nothing.
							}else{
								rightinbalck = true;//first in black.
								rightcounter++;
							}
						}
					}else{//white.
						rightAdded = false;
						righthead =false;
						if(!rightinbalck){
							//continue;
						}else {//first in white.
							rightinbalck = false;
						}
					}
					//Imgproc.line(out, new Point((int) p.x-20, g.getPos().y),new Point((int) p.x-20, g.getPos().y+g.getPos().height),  new Scalar(0,0,255));
					//count left.
					int headWidth = 20;
					if(segment.getImage().get(i, (int) p.x-headWidth)[0]<0.2){//black
						
							if(leftinbalck){
								leftcounter++;
								if(leftcounter>3&&!leftAdded){
									left++;
									leftAdded = true;
								}
								;//do nothing.
							}else{
								leftinbalck = true;//first in black.
								leftcounter++;
							}
				
					}else{//white.
						leftAdded=false;
				
						if(!leftinbalck){
							continue;
						}else {//first in white.
							leftinbalck = false;
						}
					}
				}
				if(left>right){
					durations.add((int) Math.pow(2, left+2));
				}else {
					durations.add((int) Math.pow(2, right+2));
				}
			}
			//imshow(out);
			
		}
		return durations;
	}*/

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

	/*public static int getNoteSize(Segment segment) {
		int lineWide = segment.getLineLoc().get(1)-segment.getLineLoc().get(0);
		int min = (int)Math.round(0.6*lineWide/2);
		int max = (int)Math.round(1.4*lineWide/2);
		Mat circles = new Mat();
		Mat currentFrame = segment.getImage();

		Imgproc.HoughCircles(currentFrame, circles, Imgproc.CV_HOUGH_GRADIENT, 1, 40, 1000, 6, (int)Math.round(0.6*lineWide/2), (int)Math.round(1.4*lineWide/2));

		Imgproc.cvtColor(currentFrame, currentFrame, Imgproc.COLOR_GRAY2BGR);
		Vector<Double> rad = new Vector<>();
		double sum = 0;
		for (int i = 0; i < circles.cols(); i++) {
			double[] vCircle = circles.get(0, i);

			Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
			int radius = (int) Math.round(vCircle[2]);
			sum+=vCircle[2];
			rad.add(vCircle[2]);

			Imgproc.circle(currentFrame, pt, radius, new Scalar(0, 0, 255));
		}
		Collections.sort(rad);
		System.out.println("mid===> " + rad.get(rad.size()/2));
		System.out.println("avg===> " + sum/circles.cols());
		imshow(currentFrame);

		return (int) (sum/circles.cols());
	}*/
	
	public static Mat getTemplate(String fName, int width, int height) {
		Mat template = Imgcodecs.imread(fName);
		// ImageProcessor.imshow(template);
		Imgproc.resize(template, template, new Size(width, height));
		Imgproc.cvtColor(template, template, Imgproc.COLOR_RGB2GRAY);
		Imgproc.threshold(template, template, 100, 255, Imgproc.THRESH_BINARY);

		return template;
	}

	/*public static Vector<Point> getNoteHeadLoc(Segment segment) {
		Mat template = Imgcodecs.imread("a.png");
		Imgproc.resize(template, template, new Size(26, 22));
		Imgproc.cvtColor(template, template, Imgproc.COLOR_RGB2GRAY);
		Imgproc.threshold(template, template, 100, 255, Imgproc.THRESH_BINARY);
		
		Mat matchResult = templatematch(segment.getImage(), template,0.7);
		
		Vector<Point> noteLoc = new Vector<>();
		for(int i=0;i<matchResult.rows();++i){
			for(int j=0;j<matchResult.cols();++j){ 
				if (matchResult.get(i, j)[0]>0.3) {
					noteLoc.add(new Point(j+13,i+11));		
				}
				
			}
		}
		
		return noteLoc;
	}*/
	
	/*public static Vector<NoteGroup> getNoteGroup(Segment segment) {
		Vector<NoteGroup> groups = new Vector<>();
		Vector<Point> noteLoc = getNoteHeadLoc(segment);
		
		List<MatOfPoint> contours = findContours(segment.getImage());
		contours.sort(new Comparator<MatOfPoint>() {

			@Override
			public int compare(MatOfPoint o1, MatOfPoint o2) {
				Rect rect1 = Imgproc.boundingRect(o1);
				Rect rect2 = Imgproc.boundingRect(o2);
				int id1 = segment.getPartIdByY(rect1.y+rect1.height/2);
				int id2 = segment.getPartIdByY(rect2.y+rect2.height/2);
				if(id1<id2){
					return -1;
				}else if(id1>id2){
					return 1;
				}else {
					return rect1.x-rect2.x;
				}
			}
			
		});
		for (MatOfPoint c : contours) {
			Mat template = Imgcodecs.imread("a.png");
			Imgproc.resize(template, template, new Size(26, 22));
			Imgproc.cvtColor(template, template, Imgproc.COLOR_RGB2GRAY);
			Imgproc.threshold(template, template, 100, 255, Imgproc.THRESH_BINARY);

			Rect rect = Imgproc.boundingRect(c);
			Mat src = segment.getImage().submat(rect);
			
			if (src.cols() >= template.cols() && src.rows() >= template.rows()) {
				Mat matchResult = templatematch(src, template,0.7);
				if(Core.minMaxLoc(matchResult).maxVal>0.3){//rect is a note box.
					//imshow(src);
					/*if(rect.width>310){
						imshow(src);
					}
					Vector<Integer> stemLoc = findPeakY(src, false);
					//imshow(src);
					int firstStemLoc = stemLoc.get(0)+rect.x;
					Vector<Point> noteHeadLoc = new Vector<>();
					int sumY = 0;
					for(Point point:noteLoc){
						if(rect.contains(point)){
							noteHeadLoc.add(point);
							sumY+=point.y;
						}
					}
					noteHeadLoc.sort(new Comparator<Point>() {

						@Override
						public int compare(Point o1, Point o2) {
							// TODO Auto-generated method stub
							if(o1.x>o2.x){
								return 1;
							}else {
								return -1;
							}
						}
					});
					int meanY = sumY/noteHeadLoc.size();
					int difMin = Math.abs(segment.getPartMid().get(0) -meanY);
					int partid = 0;
					for(int i=1;i<segment.getPartMid().size();++i){
						if(Math.abs(segment.getPartMid().get(i)-meanY)<difMin){
							difMin = Math.abs(segment.getPartMid().get(i)-meanY);
							partid = i;
						}
						
					}
					NoteGroup noteGroup = new NoteGroup(partid, noteHeadLoc.size()==1, noteHeadLoc.get(0).x<firstStemLoc,noteHeadLoc,rect);					
					groups.add(noteGroup);
				}
			}
		}
		return groups;
	}*/

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

	/*public static Vector<Segment> getStaveSegment(Mat mat, int partNum){
		// remove stave line and divide page into segment.
		
		Mat page = mat.clone();
		Vector<Segment> segments = new Vector<>();

		Vector<Integer> lineLoc = findPeaks(page);// array of index.
		
		int segmentWidth = lineLoc.get(5*partNum)-lineLoc.get(0);
		for(int i=0;i<lineLoc.size()/(5*partNum);++i){
			int segmentMid = (lineLoc.get(i*5*partNum)+lineLoc.get((i+1)*5*partNum-1))/2;
			int segmentStart = segmentMid-segmentWidth/2;
			//int segmentEnd = segmentMid+segmentWidth/2;
			
			Mat segmentMat = page.submat(new Rect(0, segmentStart, page.cols(), page.rows()-segmentStart>segmentWidth?segmentWidth:page.rows()-segmentStart));
			//imshow(segmentMat);
			
			Vector<Integer> localLineLoc = new Vector<>();
			
			for(int k=0;k<5*partNum;++k){
				localLineLoc.add(lineLoc.get(i*5*partNum+k)-segmentStart);
			}
			findPeakY(segmentMat, true);//remove bar line.
			segments.add(new Segment(segmentMat, segmentWidth/2, localLineLoc,lineLoc.get(1)-localLineLoc.get(0),partNum,segmentStart,page.rows()>segmentStart+segmentWidth?segmentStart+segmentWidth:page.rows()));
		}
		return segments;

		/*Vector<Integer> space = (Vector<Integer>) lineLoc.clone();// array of
																	// apace
																	// width
		space.add(0, 0);
		space.add(space.size(), page.rows());
		for (int i = 0; i < space.size() - 1; ++i) {
			space.set(i, space.get(i + 1) - space.get(i));
		}
		space.remove(space.size() - 1);

		Vector<Integer> temp = (Vector<Integer>) space.clone();
		Collections.sort(temp);

		Vector<Integer> kernel = new Vector<>();
		kernel.add(-1);
		kernel.add(-1);
		kernel.add(-1);
		kernel.add(-1);
		kernel.add(0);
		kernel.add(1);
		kernel.add(1);
		kernel.add(1);
		kernel.add(1);
		Vector<Integer> spaceFiltered = convolve(space, kernel);

		Vector<Integer> segmentMidIndex = new Vector<>();
		for (int i = 0; i < spaceFiltered.size(); ++i) {
			int shrehold = 5;
			if (Math.abs(spaceFiltered.get(i)) <= shrehold) {
				segmentMidIndex.add(i);
			}
		}

		// array of index.
		//int headSpaceWidth = space.get(0);
		int segmentWidth = lineLoc.get(10) - lineLoc.get(0);
		int segmentGap = space.get(5);
		for (int i = 0; i < segmentMidIndex.size(); i += 2) {
			int segmentEnd = lineLoc.get(segmentMidIndex.get(i) - 1) + segmentGap / 2 + segmentWidth / 2;
			int segmentStart = lineLoc.get(segmentMidIndex.get(i)) - segmentGap / 2 - segmentWidth / 2;
			int segmentMid = lineLoc.get(segmentMidIndex.get(i)) - segmentGap / 2;
			// page.submat(rowStart, rowEnd, colStart, colEnd)
			Mat image = page.submat(segmentStart, segmentEnd, 0, page.cols()).clone();
			// imshow(image);
			Vector<Integer> segLineLoc = new Vector<>();
			for(int y:lineLoc){
				if(y>segmentStart&&y<segmentEnd){
					segLineLoc.add(y-segmentStart);
				}
			}
			findPeakY(image, true);//remove bar line.
			segments.add(new Segment(image,segmentMid-segmentStart,segLineLoc));
		}

		/*
		 * for (int i = 0; i < lineLoc.size(); i++) {
		 * System.out.print(lineLoc.get(i)+"    ");
		 * System.out.println(spaceFiltered.get(i)); }
		 

		// int linespace = lineLoc.get(temp.size()/2);

		//return segments;
	}*/

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
		MinMaxLocResult maxResult = Core.minMaxLoc(matchResult);
		double maxval = Core.minMaxLoc(matchResult).maxVal;
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

	/*static Vector<Integer> convolve(Vector<Integer> Signal, Vector<Integer> kernel) {
		Vector<Integer> result = new Vector<>();
		for (int n = 0; n < Signal.size() + kernel.size() - 1; n++) {
			int kmin, kmax, k, i;

			result.add(0);
			if (n >= kernel.size() / 2 && n <= Signal.size() - kernel.size() / 2 - 1) {

				kmin = n - (kernel.size() / 2);
				kmax = n + kernel.size() / 2;

				for (k = kmin, i = 0; k <= kmax; k++, ++i) {
					result.set(n, result.get(n) + Signal.get(k) * kernel.get(i));
				}
			} else {
				result.set(n, 100);
			}
		}
		return result;
	}*/
	
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
