package OMR;

import java.io.File;
import java.lang.annotation.Retention;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.tools.DocumentationTool.Location;

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

class PartMat{
	Mat mat;
	Vector<Integer> lineLoc;
	public PartMat(Mat mat, Vector<Integer> lineLoc) {
		super();
		this.mat = mat;
		this.lineLoc = lineLoc;
	}
	public Mat getMat() {
		return mat;
	}
	public void setMat(Mat mat) {
		this.mat = mat;
	}
	public Vector<Integer> getLineLoc() {
		return lineLoc;
	}
	public void setLineLoc(Vector<Integer> lineLoc) {
		this.lineLoc = lineLoc;
	}
}

public class Score {
	Vector<Part> parts;

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		File dir = new File("output");
		for (String fName : dir.list()) {
			new File("output/" + fName).delete();
		}
		
		Score score = new Score("Maybe", 2);
		
		for(Part part:score.getParts()){
			System.out.println("===============part============");
			//for(Measure measure:part.getMeasures()){
			for(int i=0;i<part.getMeasures().size();++i){
				//ImageProcessor.imshow(measure.getImage());
				Measure measure = part.getMeasures().get(i);
				Imgcodecs.imwrite("output/img"+part.id+(i+1000)+".png",measure.getImage());
				for(BeamedNote group:measure.getNoteGroups()){
					for(Note note:group.getNotes()){
						System.out.println(note.getPinchStep()+" "+note.getDuration());
					}
					System.out.println("-------[goup]--------");
				}
				System.out.println("========bar line================");
			}
		}
	}

	public Score(String dirName, int partNum) {
		// divide score mat into part mat
		parts = new Vector<>();

		Vector<Mat> sheets = getSheets(dirName);//get sheets list in binary format.
		boolean[] firstPart = new boolean[partNum];
		for(int i=0;i<partNum;++i){
			firstPart[i] = true;
		}
		for (Mat page : sheets) {
			Vector<Vector<PartMat>> partArray = getPart(page, partNum);
			for (int k = 0; k < partNum; ++k) {
				//
				Part part = new Part(partArray.get(k).get(0).getMat(),partArray.get(k).get(0).getLineLoc(),k);
				if(firstPart[k]){	
					parts.add(part);
					firstPart[k] = false;
				}else {
					parts.get(k).merge(part);
				}
				for (int i = 1; i < partArray.get(k).size(); ++i) {
					parts.get(k).merge(new Part(partArray.get(k).get(i).getMat(),partArray.get(k).get(i).getLineLoc(),k));
				}
			}
		}
	}

	public static Vector<Vector<PartMat>> getPart(Mat pageMat, int partNum) {
		// need return stave line info otherwise need to recalculate the linLoc.
		// remove stave line and divide page into segment.

		Vector<Vector<PartMat>> partArray = new Vector<>();
		for (int i = 0; i < partNum; ++i) {
			partArray.add(new Vector<>());
		}

		Mat page = pageMat.clone();// not change the source mat

		Vector<Integer> lineLoc = ImageProcessor.findPeaks(page);// array of
																	// index.

		//Mat out = page.clone();
		// Imgproc.cvtColor(out, out, Imgproc.COLOR_GRAY2BGR);

		int segmentWidth = lineLoc.get(5 * partNum) - lineLoc.get(0);
		int firstSegmentMid = (lineLoc.get(0) + lineLoc.get(5 * partNum - 1)) / 2;
		int segmentStart = firstSegmentMid - segmentWidth / 2 > 0 ? firstSegmentMid - segmentWidth / 2 : 0;
		int lastSegmentMid = (lineLoc.get(lineLoc.size() - 9) + lineLoc.get(lineLoc.size() - 1)) / 2;
		int segmentEnd = lastSegmentMid + segmentWidth / 2 < page.rows() ? lastSegmentMid + segmentWidth / 2
				: page.rows();
		Vector<Integer> divider = new Vector<>();
		divider.add(segmentStart);
		for (int i = 0; i < lineLoc.size() / 5 - 1; ++i) {
			divider.add((lineLoc.get(5 * i + 4) + lineLoc.get(5 * i + 5)) / 2);
		}
		divider.add(segmentEnd);

		// for(int i:divider){
		// Imgproc.line(out, new Point(0, i) ,new Point(page.cols(),i), new
		// Scalar(0,0,255));
		// }
		// ImageProcessor.imshow(out);
		
		for (int i = 0; i < divider.size() - 1; ++i) {
			Vector<Integer> localLineLocs = new Vector<>();
			Mat partMat = page.submat(new Rect(0, divider.get(i), page.cols(), divider.get(i + 1) - divider.get(i)));
			for(int j=0;j<5;++j){
				localLineLocs.add(lineLoc.get(i*5+j)-divider.get(i));
			}
			// ImageProcessor.imshow(partMat);
			// System.out.println(i);
			partArray.get(i % partNum).add(new PartMat(partMat, localLineLocs));
		}
		return partArray;
	}

	public static Vector<Mat> getSheets(String dirName) {
		// to do: need some step to phase noise !!!
		// rgb2gray and gray2bw.
		File directory = new File(dirName);
		String[] fileList = directory.list();

		Vector<Mat> sheets = new Vector<>();

		for (String s : fileList) {
			Mat mat = Imgcodecs.imread(dirName + "/" + s);
			Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);
			// Imgproc.GaussianBlur(mat, mat, new Size(3, 3), 0);
			Imgproc.threshold(mat, mat, 100, 255, Imgproc.THRESH_BINARY);
			sheets.add(mat);
		}
		return sheets;
	}

	public Vector<Part> getParts() {
		return parts;
	}
}

class Part {
	int id;
	Vector<Measure> measures;
	
	Vector<Measure> getMeasures(){
		return measures;
	}

	public void merge(Part part) {
		measures.addAll(part.getMeasures());
	}

	public Part(Mat partMat,Vector<Integer> lineLoc,int id) {
		// divide part mat into measure mat.
		// need to find bar line first.

		// Mat out = partMat.clone();
		// Imgproc.cvtColor(out, out, Imgproc.COLOR_GRAY2BGR);
		this.id = id;
		measures = new Vector<>();

		List<MatOfPoint> contours = ImageProcessor.findContours(partMat);
		contours.sort(new Comparator<MatOfPoint>() {

			@Override
			public int compare(MatOfPoint o1, MatOfPoint o2) {
				//sort by x
				return Imgproc.boundingRect(o1).x - Imgproc.boundingRect(o2).x;
			}
		});
		Vector<Integer> barLineLoc = new Vector<>();
		for (int i = 0; i < contours.size(); ++i) {
			Rect box = Imgproc.boundingRect(contours.get(i));
			if(box.y>lineLoc.get(0)||box.y+box.height<lineLoc.get(4)){
				continue;
			}
			if (box.width < 10 && box.height > 50) {//stand alone bar line. 
				barLineLoc.add(box.x);
				//Imgproc.drawContours(out, contours, i, new Scalar(0,0,255));
				continue;
			}else if(box.height > 70){
				Mat temp = partMat.submat(box).clone();
				Mat vector = new Mat();
				Core.reduce(temp, vector, 0, Core.REDUCE_AVG);
				boolean flag = false;
				for (int m = 0; m < vector.cols(); ++m) {//cols
					if (vector.get(0, m)[0] ==0) {
						barLineLoc.add(box.x+m);
						flag = true;
						break;
					}
				}
				if(flag){
					continue;
				}
			}
			if(box.height > 80){
				Mat temp = partMat.submat(box).clone();
				Mat vector = new Mat();
				Core.reduce(temp, vector, 0, Core.REDUCE_AVG);
				for (int m = 0; m < vector.cols(); ++m) {//cols
					if (vector.get(0, m)[0] <255*0.1) {// 1 is line
						//Imgcodecs.imwrite("output/img"+partMat.hashCode()+i+".png",partMat.submat(box));
						Vector<Point> half = BeamedNote.getNoteHeadLoc(partMat, box, "template/half.png");
						Vector<Point> quarter = BeamedNote.getNoteHeadLoc(partMat, box, "template/quarter.png");
						boolean flag = false;
						for(Point p:half){
							if(Math.abs(p.x-m)<20){
								flag = true;
							}
						}
						for(Point p:quarter){
							if(Math.abs(p.x-m)<20){
								flag = true;
							}
						}
						if(!flag){
							barLineLoc.add(box.x+m);
						}
						break;
					}
				}
			}
		}
		Collections.sort(barLineLoc);
		for (int i = 0; i < barLineLoc.size(); ++i) {// remove double bar line
			if (i != 0 && Math.abs(barLineLoc.get(i) - barLineLoc.get(i - 1)) < 40) {
				barLineLoc.remove(i);
			} else {
				// Imgproc.line(out, new Point(barLineLoc.get(i),0), new
				// Point(barLineLoc.get(i),partMat.cols()), new
				// Scalar(0,0,255));
			}
		}
		for (int i = 0; i < barLineLoc.size() - 1; ++i) {
			Mat measureMat = partMat.submat(0, partMat.rows(), barLineLoc.get(i) + 4, barLineLoc.get(i + 1) - 4);
			// ImageProcessor.imshow(measureMat);
			// Imgcodecs.imwrite("output/img"+partMat.hashCode()+i+contours.hashCode()+".png",
			// measureMat);
			measures.add(new Measure(measureMat,lineLoc));
		}
		// ImageProcessor.imshow(out);

	}
}

class Measure {
	// display info
	Mat image;
	Rect position;
	Vector<Integer> lineLocs;

	// music info
	int keyMode;
	int keyFifth;
	int beats;
	int beatType;
	int clefSign;
	int clefLine;
	Vector<BeamedNote> groups;

	public Measure(Mat measureImage,Vector<Integer> lineLocs) {
		image = measureImage;
		//ImageProcessor.imshow(measureImage);
		groups = new Vector<>();
		List<MatOfPoint> contours = ImageProcessor.findContours(measureImage);
		contours.sort(new Comparator<MatOfPoint>() {

			@Override
			public int compare(MatOfPoint o1, MatOfPoint o2) {
				//sort by x
				return Imgproc.boundingRect(o1).x - Imgproc.boundingRect(o2).x;
			}
		});
		// Imgproc.cvtColor(measureImage, measureImage, Imgproc.COLOR_GRAY2BGR);
		// int count = 0;
		//Mat out = measureImage.clone();
		//Imgproc.cvtColor(out, out, Imgproc.COLOR_GRAY2BGR);
		//for(int i:lineLocs){
		//	Imgproc.line(out, new Point(0,i),new Point(out.cols(),i), new Scalar(0,0,255));
		//}
		
		for (int i = 0; i < contours.size(); ++i) {
			// Imgproc.drawContours(measureImage, contours, i, new
			// Scalar(0,0,255));
			Rect rect = Imgproc.boundingRect(contours.get(i));
			if (rect.height > 3 && rect.width > 3) {
				// divide contours into:note group,dot,clef,key,accidential.

				// detect note
				Mat src = measureImage.submat(rect);
				//ImageProcessor.imshow(src);
				// ---------------------------------------------
				Mat quarterNote = ImageProcessor.getTemplate("template/quarter.png", 26, 22);
				if (src.cols() >= 26 && src.rows() >= 22) {
					Mat matchResult = ImageProcessor.templatematch(src, quarterNote,0.7);
					//ImageProcessor.imshow(measureImage.submat(rect));
					double max = Core.minMaxLoc(matchResult).maxVal;
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						// note
						//Imgproc.rectangle(out, rect.tl(), rect.br(), new Scalar(0,0,255));
						groups.add(new BeamedNote(measureImage, rect, 0,lineLocs));
						continue;
					}
					// ---------------------------------------------
					Mat halfNote = ImageProcessor.getTemplate("template/half.png", 26, 22);
					matchResult = ImageProcessor.templatematch(src, halfNote,0.55);
					max = Core.minMaxLoc(matchResult).maxVal;
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						// note
						//Imgproc.rectangle(out, rect.tl(), rect.br(), new Scalar(0,255,0));
						groups.add(new BeamedNote(measureImage, rect, 2,lineLocs));
						continue;
					}
					// ---------------------------------------------
					Mat wholeNote = ImageProcessor.getTemplate("template/whole.png", 26, 22);
					matchResult = ImageProcessor.templatematch(src, wholeNote,0.5);
					max = Core.minMaxLoc(matchResult).maxVal;
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						// note
						//Imgproc.rectangle(out, rect.tl(), rect.br(), new Scalar(255,0,0));
						groups.add(new BeamedNote(measureImage, rect, 1,lineLocs));
						continue;
					}
				}
				// ---------------------------------------------
				if (src.cols() >= 40 && src.rows() >= 100) {
					Mat gClef = ImageProcessor.getTemplate("template/g.png", rect.width, rect.height);
					Mat matchResult = ImageProcessor.templatematch(src, gClef,0.4);//0.44
					double max = Core.minMaxLoc(matchResult).maxVal;
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						// g clef
						//Imgproc.rectangle(out, rect.tl(), rect.br(), new Scalar(0,255,255));
						clefSign = 1;
						clefLine = 2;
						continue;
					}
				}
				// ---------------------------------------------
				if (src.cols() >= 30 && src.rows() >= 50) {
					Mat fClef = ImageProcessor.getTemplate("template/f.png", rect.width, rect.height);
					Mat matchResult = ImageProcessor.templatematch(src, fClef,0.3);
					double max = Core.minMaxLoc(matchResult).maxVal;
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						// f clef
						//Imgproc.rectangle(out, rect.tl(), rect.br(), new Scalar(255,0,255));
						clefSign = 2;
						clefLine = 4;
						continue;
					}
				}
				// ---------------------------------------------21 58
				if (src.cols() >= 12 && src.rows() >= 45) {//give a min size.
					Mat sharp = ImageProcessor.getTemplate("template/sharp.png", rect.width, rect.height);
					Mat matchResult = ImageProcessor.templatematch(src, sharp,0.4);
					double max = Core.minMaxLoc(matchResult).maxVal;
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						// sharp
						//Imgproc.rectangle(out, rect.tl(), rect.br(), new Scalar(255,255,0));
						continue;
					}
					// ---------------------------------------------
					//ImageProcessor.imshow(src);
					Mat flat = ImageProcessor.getTemplate("template/flat.png", rect.width, rect.height);
					matchResult = ImageProcessor.templatematch(src,flat,0.15);
					max = Core.minMaxLoc(matchResult).maxVal;
					//System.out.println(max);
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						// flat
						//Imgproc.rectangle(out, rect.tl(), rect.br(), new Scalar(0,146,255));
						continue;
					}
					// ---------------------------------------------
					//ImageProcessor.imshow(src);
					Mat natural = ImageProcessor.getTemplate("template/natural.png", rect.width, rect.height);
					matchResult = ImageProcessor.templatematch(src, natural,0.25);
					max = Core.minMaxLoc(matchResult).maxVal;
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						// natural
						//Imgproc.rectangle(out, rect.tl(), rect.br(), new Scalar(0,146,255));
						continue;
					}
				}
				if (src.cols() >= 20 && src.rows() >= 50) {
				Mat qurterRest = ImageProcessor.getTemplate("template/quarterrest.png", rect.width, rect.height);
				Mat matchResult = ImageProcessor.templatematch(src, qurterRest,0.3);
				double max = Core.minMaxLoc(matchResult).maxVal;
				if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
					// 4rest
					//Imgproc.rectangle(out, rect.tl(), rect.br(), new Scalar(0,255,0));
					groups.add(new BeamedNote(src, rect, -4,lineLocs));
					continue;
				}}
				if (src.cols() >= 15 && src.rows() >= 25) {
				Mat rest8 = ImageProcessor.getTemplate("template/8rest.png", rect.width, rect.height);
				Mat matchResult = ImageProcessor.templatematch(src, rest8,0.5);
				double max = Core.minMaxLoc(matchResult).maxVal;
				if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
					// 8rest
					//Imgproc.rectangle(out, rect.tl(), rect.br(), new Scalar(255,255,0));
					groups.add(new BeamedNote(src, rect, -8,lineLocs));
					continue;
				}}
				if (src.cols() >= 15 && src.rows() >= 50) {
				Mat rest16 = ImageProcessor.getTemplate("template/16rest.png", rect.width, rect.height);
				Mat matchResult = ImageProcessor.templatematch(src, rest16,0.3);
				double max = Core.minMaxLoc(matchResult).maxVal;
				if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
					// 16rest
					//Imgproc.rectangle(out, rect.tl(), rect.br(), new Scalar(30,190,165));
					groups.add(new BeamedNote(src, rect, -16,lineLocs));
					continue;
				}}
				if (src.cols() >= 15 && src.rows() >= 60) {
				Mat rest32 = ImageProcessor.getTemplate("template/32rest.png", rect.width, rect.height);
				Mat matchResult = ImageProcessor.templatematch(src, rest32,0.3);
				double max = Core.minMaxLoc(matchResult).maxVal;
				if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
					// 32rest
					//Imgproc.rectangle(out, rect.tl(), rect.br(), new Scalar(24,147,230));
					groups.add(new BeamedNote(src, rect, -32,lineLocs));
					continue;
				}}
				if (src.cols() >= 24 && src.cols() <= 28&&src.rows() >= 8&&src.rows() <= 12) {
					//ImageProcessor.imshow(src);
					Mat wholeHalfRest = ImageProcessor.getTemplate("template/wholehalfrest.png", rect.width, rect.height);
					Mat matchResult = ImageProcessor.templatematch(src, wholeHalfRest,0.98);
					double max = Core.minMaxLoc(matchResult).maxVal;
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						// whole rest
						//Imgproc.rectangle(out, rect.tl(), rect.br(), new Scalar(0,0,255));
						//groups.add(new BeamedNote(src, rect, -1));
						continue;
					}}

				// something else, skip
				;

				// Imgproc.rectangle(measureImage, rect.tl(), rect.br(), new
				// Scalar(0,0,255));
				// count++;
			}
		}
		// Imgproc.putText(measureImage, "num of box: "+count, new
		// Point(40,40),Core.FONT_HERSHEY_PLAIN, 1.0 ,new Scalar(0,0,255));
		//Imgcodecs.imwrite("output/img"+measureImage.hashCode()+".png",out);
	}

	public Vector<BeamedNote> getNoteGroups() {
		return groups;
	}

	public Mat getImage() {
		return image;
	}

}

class BeamedNote {
	// display info
	Rect position;
	int direction;

	// music info
	Vector<Note> notes;
	
	public static int getPinch(Mat measureMat,Point headLoc,Vector<Integer> lineLoc){
		//warning : headLoc is measurewide.
		Vector<Integer> tempLinLoc = new Vector<>();
		for(int i =4;i>0;--i ){
			tempLinLoc.add(lineLoc.get(0)-19*i);
		}
		
		for(int i =0;i<5;++i ){
			tempLinLoc.add(lineLoc.get(i));
		}
		
		for(int i =1;i<5;++i ){
			tempLinLoc.add(lineLoc.get(4)+19*i);
		}
		for(int i=12;i>0;--i){
			tempLinLoc.add(i, (tempLinLoc.get(i)+tempLinLoc.get(i-1))/2);
		}
		int mindif = 100000;
		int pinch = 20;
		for(int y:tempLinLoc){
			int dif = (int) Math.abs(headLoc.y-y);
			if(dif<mindif){
				mindif = dif;
				pinch--;
			}else {
				break;
			}
		}
		return pinch;
	}
	
	public static Vector<Point> getNoteHeadLoc(Mat measureMat,Rect rect,String template) {
		//warning : head loc is group wide.
		Vector<Point> noteLoc = new Vector<>();
		if(rect.height>=22&&rect.width>=26){
		Mat teMat = ImageProcessor.getTemplate(template, 26, 22);
		Mat matchResult = ImageProcessor.templatematch(measureMat.submat(rect),teMat,0.5);
		
		for(int i=0;i<matchResult.rows();++i){
			for(int j=0;j<matchResult.cols();++j){ 
				if (matchResult.get(i, j)[0]>0.3) {
					noteLoc.add(new Point(j+13,i+11));		
				}
				
			}
		}}
		
		return noteLoc;
	}
	public Vector<Integer> getDuration(Mat groupMat,Vector<Point> headLocs){
		//determine the note direction, up or down
		Vector<Integer> durations = new Vector<>();
		Vector<Integer> stemLocs = ImageProcessor.findPeakY(groupMat, false);
		if(headLocs.get(0).x<stemLocs.get(0)){
			//down,
			for(Point p:headLocs){
				int left=0,right=0;
				int leftcounter=0;
				boolean leftinbalck = true;
				boolean lefthead = true;
				int rightcounter=0;
				boolean rightinbalck = false;
				//boolean righthead = true;
				boolean leftAdded = false;
				boolean rightAdded = false;
				for(int i=(int) p.y;i>0;--i){//bottom to top.
					//count left
					if(groupMat.get(i, (int) p.x)[0]<0.2){//black
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
					if(p.x+headWidth<groupMat.cols()){
					
					if(groupMat.get(i, (int) p.x+headWidth)[0]<0.2){//black
						
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
					}}
				}
				if(left>right){
					durations.add((int) Math.pow(2, left+2));
				}else {
					durations.add((int) Math.pow(2, right+2));
				}
			}
		}else{
			//up,
			for(Point p:headLocs){
				
				int left=0,right=0;
				int leftcounter=0;
				boolean leftinbalck = false;
				//boolean lefthead = true;
				int rightcounter=0;
				boolean rightinbalck = true;
				boolean righthead = true;
				boolean leftAdded = false;
				boolean rightAdded = false;
				for(int i=(int) p.y;i<groupMat.rows();++i){//top to bottom
					//Imgproc.line(out, new Point((int) p.x, g.getPos().y),new Point((int) p.x, g.getPos().y+g.getPos().height),  new Scalar(0,0,255));
					//count right
					if(groupMat.get(i, (int) p.x)[0]<0.2){//black
						
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
					if(p.x-headWidth>0){
					if(groupMat.get(i, (int) p.x-headWidth)[0]<0.2){//black
						
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
					}}
				}
				if(left>right){
					durations.add((int) Math.pow(2, left+2));
				}else {
					durations.add((int) Math.pow(2, right+2));
				}
			}
		}
		
		return durations;
	}

	public BeamedNote(Mat measureMat, Rect position,int duration,Vector<Integer> lineLoc) {
		// get note info from note group
		
		notes = new Vector<>();
		int pinch = 0;
		//if(duration!=0){
			switch (duration) {
			case 0:
				Vector<Point> headLoc = getNoteHeadLoc(measureMat, position, "template/quarter.png");
				Vector<Integer> durations = getDuration(measureMat.submat(position), headLoc);
				for(int i=0;i<headLoc.size();++i){
					pinch = getPinch(measureMat, new Point(headLoc.get(i).x+position.x, headLoc.get(i).y+position.y), lineLoc);
					notes.add(new Note(durations.get(i), 0, pinch, 0));
				}
				break;
			case 1:
				pinch = getPinch(measureMat, getNoteHeadLoc(measureMat, position, "template/whole.png").get(0), lineLoc);
				notes.add(new Note(1, 0, pinch, 0));
				break;
			case 2:
				pinch = getPinch(measureMat, getNoteHeadLoc(measureMat, position, "template/half.png").get(0), lineLoc);
				notes.add(new Note(2, 0, pinch, 0));
				break;
			case -1:
				notes.add(new Note(1, 0,0,0));
				break;
			case -2:
				notes.add(new Note(2, 0,0,0));
				break;
			case -4:
				notes.add(new Note(4, 0,0,0));
				break;
			case -8:
				notes.add(new Note(8, 0,0,0));
				break;
			case -16:
				notes.add(new Note(16, 0,0,0));
				break;
			case -32:
				notes.add(new Note(32, 0,0,0));
				break;

			default:
				break;
			}
		//}
	}

	public Vector<Note> getNotes() {
		return notes;
	}
}

class Note {
	// diaplay info
	Point headLoc;
	int headHight;
	int beamtype;

	// music info
	int duration;
	int pinchOctave;
	int pinchStep;// 1 to 7
	int dot;
	int accidentals;

	public Note(int duration, int pinchOctave, int pinchStep, int dot) {
		super();
		this.duration = duration;
		this.pinchOctave = pinchOctave;
		this.pinchStep = pinchStep;
		this.dot = dot;
	}

	public int getDuration() {
		return duration;
	}

	public int getPinchStep() {
		return pinchStep;
	}

}