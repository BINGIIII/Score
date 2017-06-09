package OMR;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import logic.Compiler;

public class JianScore {
	Vector<JPart> parts;

	public void convert2jianpu(String outName){
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(outName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.println("1=C");
		out.println("4/4");
		for(JPart part:parts){
			for(JMeasure measure:part.measures){
				for(JNote note:measure.notes){
					switch (note.duration) {
					case 8:
						out.print("q");
						break;
					case 16:
						out.print("s");
						break;
					case 32:
						out.print("d");
						break;
					case 64:
						out.print("h");
						break;
					default:
						break;
					}
					out.print(note.pinchStep);
					if(note.pinchOctave>0){
						out.print("'");
					}else if(note.pinchOctave<0){
						out.print(",");
					}
					if(note.dot!=0){
						out.print(".");
					}
					switch (note.duration) {
					case 1:
						out.print("---");
						break;
					case 2:
						out.print("-");
						break;
					default:
						break;
					}
					out.print(" ");
				}//note
				out.println();
			}//measure
		}//part
		out.close();

	}
	public static void main(String[] args) {
		new JianScore("jianpu", 2).convert2ly("xxx.ly");
//		Compiler.jianpu2ly("xxx.txt", "xxx.ly");
	}

	public void convert2ly(String outName){
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(outName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		final char[] map = {'r','c','d','e','f','g','a','b'};
		final String[] numMap = {"zero","one","two","three"};
		final String[] clefMap = {"treble","bass"};
		for(int i=0;i<parts.size();++i){
			JPart part = parts.get(i);
			out.println("Part"+numMap[i]+"={");
			out.print("\\clef \""+clefMap[i]+"\"");
			out.println("\\tempo 4=96 ");
			for(JMeasure measure:part.measures){
				for(JNote note:measure.notes){
					out.print(map[note.pinchStep]);
					if(note.pinchStep!=0){
						note.pinchOctave+=1;
					}
					if(note.pinchOctave>0){
						out.print("'");
					}else if(note.pinchOctave<0){
						out.print(",");
					}
					out.print(note.duration);
					if(note.dot!=0){
						out.print(".");
					}
					out.print(" ");
				}//note
				out.println();
			}//measure
			out.println("}");
		}//part
		String string = "\\score { \n << \n \\new PianoStaff << \n\\set PianoStaff.instrumentName = \"Piano\"\n\\context Staff = \"1\" << \n\\context Voice = \"PartPzero\" {\\Partzero }\n >> \\context Staff = \"2\" <<\n \\context Voice = \"Partone\" { \\Partone }  >> >>  >> \\layout {} \\midi {} }";
		out.print(string);
		out.close();
	}
	public JianScore(String dirName, int partNum) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		File dir = new File("measures");
		for (String fName : dir.list()) {
			new File("measures/" + fName).delete();
		}
		dir = new File("output");
		for (String fName : dir.list()) {
			new File("output/" + fName).delete();
		}
		// divide score mat into part mat
		parts = new Vector<>();

		Vector<Mat> sheets = ImageProcessor.getSheets(dirName);// get sheets
																// list in
																// binary
		// kformat.
		boolean[] firstPart = new boolean[partNum];
		for (int i = 0; i < partNum; ++i) {
			firstPart[i] = true;
		}
		for (Mat page : sheets) {
			Mat outMat = new Mat();
			Imgproc.cvtColor(page, outMat, Imgproc.COLOR_GRAY2BGR);
			Vector<Vector<JPartMat>> partMats = getPartMat(page, partNum);
			for (int k = 0; k < partNum; ++k) {
				//
				JPart part = new JPart(partMats.get(k).get(0).mat,
						partMats.get(k).get(0).start - partMats.get(k).get(0).yLoc,
						partMats.get(k).get(0).end - partMats.get(k).get(0).yLoc, partMats.get(k).get(0).yLoc,outMat);
				if (firstPart[k]) {
					parts.add(part);
					firstPart[k] = false;
				} else {
					parts.get(k).merge(part);
				}
				for (int i = 1; i < partMats.get(k).size(); ++i) {
					parts.get(k).merge(new JPart(partMats.get(k).get(i).mat,
							partMats.get(k).get(i).start - partMats.get(k).get(i).yLoc,
							partMats.get(k).get(i).end - partMats.get(k).get(i).yLoc, partMats.get(k).get(i).yLoc,outMat));
				}
			}
			Imgcodecs.imwrite("output/img"+page.hashCode()+".png", outMat);
		}

	}

	Vector<Vector<JPartMat>> getPartMat(Mat page, int partNum) {
		Vector<Vector<JPartMat>> partMats = new Vector<>();
		for (int i = 0; i < partNum; ++i) {
			partMats.add(new Vector<>());
		}
		Vector<Integer> partYs = getpartY(page);

		// int firstPartMid = partYs.get(0)+partYs.get(1)/2;
		int firstPartStart = partYs.get(0) - (partYs.get(2) - partYs.get(1)) / 2;
		int lastPartEnd = partYs.get(partYs.size() - 1)
				+ (partYs.get(partYs.size() - 2) - partYs.get(partYs.size() - 3)) / 2;
		Vector<Integer> divider = new Vector<>();
		divider.add(firstPartStart);
		for (int i = 1; i < partYs.size() - 2; i += 2) {
			divider.add((partYs.get(i) + partYs.get(i + 1)) / 2);
		}
		divider.add(lastPartEnd);
		for (int i = 0; i < divider.size() - 1; ++i) {
			Mat mat = page.submat(new Rect(0, divider.get(i), page.cols(), divider.get(i + 1) - divider.get(i)));
			partMats.get(i % partNum).add(new JPartMat(mat, partYs.get(i * 2), partYs.get(i * 2 + 1), divider.get(i)));
		}
		return partMats;

	}

	public Vector<Integer> getpartY(Mat page) {
		Vector<Integer> partYs = new Vector<>();
		Mat temp = page.clone();
		Core.bitwise_not(page, temp);

		Mat vector = new Mat();
		Core.reduce(temp, vector, 0, Core.REDUCE_AVG);
		int lastBarLoc = 0;
		for (int i = vector.cols() - 1; i >= 0; --i) {
			if (vector.get(0, i)[0] > 20 && vector.get(0, i - 1)[0] > 20) {
				lastBarLoc = i;
				break;
			}
		}
		boolean lastInBlack = false;
		for (int i = 0; i < page.rows(); ++i) {

			if (page.get(i, lastBarLoc)[0] > 0) {// white
				if (lastInBlack) {// first in white
					partYs.add(i);// end
				}
				lastInBlack = false;
			} else {// black
				if (!lastInBlack) {// first in black
					partYs.add(i);// start
				}
				lastInBlack = true;
			}
		}
		return partYs;
	}
}

class JPartMat {
	Mat mat;
	int start;// y in part
	int end;
	int yLoc;

	public JPartMat(Mat mat, int start, int end, int yLoc) {
		super();
		this.mat = mat;
		this.start = start;
		this.end = end;
		this.yLoc = yLoc;
	}
}

class JPart {
	Vector<JMeasure> measures;

	public void merge(JPart part) {
		measures.addAll(part.measures);
	}

	public JPart(Mat partMat, int start, int end, int yLoc, Mat page) {
		super();
		measures = new Vector<>();

		// Imgproc.line(partMat, new Point(0,start),new
		// Point(partMat.cols(),start),new Scalar(0,0,255));
		// Imgproc.line(partMat, new Point(0,end),new
		// Point(partMat.cols(),end),new Scalar(0,0,255));
		// Imgcodecs.imwrite("measures/img" +partMat.hashCode()+".png",
		// partMat);
		Vector<Integer> barLineLoc = new Vector<>();

		for (int i = 0; i < partMat.cols(); ++i) {
			boolean flag = true;
			for (int j = start + 1; j < end - 1; ++j) {
				if (partMat.get(j, i)[0] > 0.3) {// white
					flag = false;
					break;
				}
			}
			if (flag) {
				barLineLoc.add(i);
			}
		}

		Collections.sort(barLineLoc);
		for (int i = 0; i < barLineLoc.size(); ++i) {// remove double bar line
			if (i != 0 && Math.abs(barLineLoc.get(i) - barLineLoc.get(i - 1)) < 20) {
				barLineLoc.remove(i);
				--i;
			}
		}
		for (int i = 0; i < barLineLoc.size() - 1; ++i) {
			
			Mat measureMat = partMat.submat(0, partMat.rows(), barLineLoc.get(i), barLineLoc.get(i + 1));
			measures.add(new JMeasure(measureMat, start, end,barLineLoc.get(i),yLoc,page));
			Imgcodecs.imwrite("measures/img" + partMat.hashCode() + i + ".png", measureMat);
		}

	}
}

class JMeasure {
	int keyMode;
	int keyFifth;
	int beats;
	int beatType;
	Vector<JNote> notes;
	Mat page;
	int x;
	int y;
	public int getDot(Mat measureMat,Rect pos){
		int dotNum = 0;
		int width = measureMat.cols() - pos.x;//to the end of measure.
		Mat src = measureMat.submat(new Rect(pos.x, pos.y, width,pos.height));
		List<MatOfPoint> contours = ImageProcessor.findContours(src);
		for(MatOfPoint c:contours){
			Rect rect = Imgproc.boundingRect(c);
			if(rect.width<=10&&rect.height<=10){
				//dot
				dotNum++;
			}
			if (rect.height >= 25 && rect.width >= 10 && rect.height <= 45 && rect.width <= 45&&rect.x>pos.width) {//note
				break;//break when find another note.
			}
			
		}
		
		return dotNum;

	}
	public int getsubline(Mat measureMat, Rect pos){
		Mat src = measureMat.submat(new Rect(pos.x, pos.y-pos.height>0?pos.y-pos.height:0, pos.width,pos.height*3<measureMat.rows()?pos.height*3:measureMat.rows()));
		
		List<MatOfPoint> contours = ImageProcessor.findContours(src);
		int lineNum = 0;
		for(MatOfPoint c:contours){
			Rect rect = Imgproc.boundingRect(c);
			if(rect.height<9&&rect.width<9){
				//point
				continue;
			}
			if(rect.width>=pos.width-2&&rect.height<10){
				//line
				if(rect.x<pos.x){
					lineNum++;
				}
				
			}
		}
		return lineNum;
	}
	public int getdashLine(Mat measureMat, Rect pos){
		int lineNum = 0;
		int width = measureMat.cols() - pos.x;//to the end of measure.
		Mat src = measureMat.submat(new Rect(pos.x, pos.y, width,pos.height));
		List<MatOfPoint> contours = ImageProcessor.findContours(src);
		for(MatOfPoint c:contours){
			Rect rect = Imgproc.boundingRect(c);
			if(rect.height<10&&rect.width>=20){
				//dash line.
				lineNum++;
			}
			if (rect.height >= 25 && rect.width >= 10 && rect.height <= 45 && rect.width <= 45&&rect.x-pos.x>pos.width) {//note
				break;//break when find another note.
			}
			
		}
		
		return lineNum;
	}
	public int getpoint(Mat measureMat, Rect pos){
		int pointNum = 0;
		Mat src = measureMat.submat(new Rect(pos.x, 0, pos.width,measureMat.rows()));
		List<MatOfPoint> contours = ImageProcessor.findContours(src);
		for(MatOfPoint c:contours){
			Rect rect = Imgproc.boundingRect(c);
			if(rect.height<10&&rect.width<10){
				//point
				if(rect.y<pos.y){
					pointNum++;
				}else {
					pointNum--;
				}
			}
			
		}	
		return pointNum;
	}
	public JMeasure(Mat measureMat, int start, int end,int x,int y, Mat page) {
		// get note info from measureMat
		this.x = x;
		this.y = y;
		this.page = page;
		notes = new Vector<>();
		Mat temp = measureMat.clone();
		List<MatOfPoint> contours = ImageProcessor.findContours(measureMat);
		Imgproc.cvtColor(temp, temp, Imgproc.COLOR_GRAY2BGR);
		for (int i=0;i<contours.size();++i) {
			MatOfPoint c = contours.get(i);
			Rect rect = Imgproc.boundingRect(c);
			Mat src = measureMat.submat(rect);
			
			if (rect.y>start&&rect.height >= 25 && rect.width >= 10 && rect.height <= 45 && rect.width <= 45) {//get duration.
				int num = getNum(src, rect);
				int subNum = getsubline(measureMat, rect);
				int dashNum = 0;
				if(subNum == 0){
					dashNum = getdashLine(measureMat, rect);
				}
				int dotNum = 0;
				if(dashNum==0){
					dotNum = getDot(measureMat, rect);
				}
				int pointNum = getpoint(measureMat, rect);
//				if(i<contours.size()-1&&Math.abs(Imgproc.boundingRect(contours.get(i+1)).x-rect.x)<rect.width){
//					continue;//skip this note;
//				}
				JNote note = new JNote(dashNum, subNum, pointNum, num,dotNum);
				notes.add(note);
			}

		}

//				Imgcodecs.imwrite("output/img"+measureMat.hashCode()+".png", temp);
	}
	public int getNum(Mat src, Rect rect){
		Mat note1 = ImageProcessor.getTemplate("template/1.png", rect.width, rect.height);
		Mat matchResult = ImageProcessor.templatematch(src, note1, 0.3);
		if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
			Imgproc.rectangle(page, new Point(rect.x+x,rect.y+y), new Point(rect.x+x+rect.width,rect.y+y+rect.height), new Scalar(0,0,255),3);
//			System.out.print("1 ");

			return 1;
		}
		Mat note2 = ImageProcessor.getTemplate("template/2.png", rect.width, rect.height);
		matchResult = ImageProcessor.templatematch(src, note2, 0.5);
		if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
			Imgproc.rectangle(page, new Point(rect.x+x,rect.y+y), new Point(rect.x+x+rect.width,rect.y+y+rect.height), new Scalar(0,0,255),3);
//					System.out.print("2 ");
			return 2;
		}
		Mat note3 = ImageProcessor.getTemplate("template/3.png", rect.width, rect.height);
		matchResult = ImageProcessor.templatematch(src, note3, 0.5);
		if (Core.minMaxLoc(matchResult).maxVal > 0.3) {
			Imgproc.rectangle(page, new Point(rect.x+x,rect.y+y), new Point(rect.x+x+rect.width,rect.y+y+rect.height), new Scalar(0,0,255),3);
//					Imgproc.rectangle(temp, rect.br(), rect.tl(), new Scalar(0,0,255));
//					System.out.print("3 ");
			return 3;
		}
		Mat note4 = ImageProcessor.getTemplate("template/4.png", rect.width, rect.height);
		matchResult = ImageProcessor.templatematch(src, note4, 0.5);
		if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
			Imgproc.rectangle(page, new Point(rect.x+x,rect.y+y), new Point(rect.x+x+rect.width,rect.y+y+rect.height), new Scalar(0,0,255),3);
//					Imgproc.rectangle(temp, rect.br(), rect.tl(), new Scalar(0,0,255));
//					System.out.print("4 ");
			return 4;
		}
		Mat note5 = ImageProcessor.getTemplate("template/5.png", rect.width, rect.height);
		matchResult = ImageProcessor.templatematch(src, note5, 0.6);
		if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
			Imgproc.rectangle(page, new Point(rect.x+x,rect.y+y), new Point(rect.x+x+rect.width,rect.y+y+rect.height), new Scalar(0,0,255),3);
//					Imgproc.rectangle(temp, rect.br(), rect.tl(), new Scalar(0,0,255));
//					System.out.print("5 ");
			return 5;
		}
		Mat note6 = ImageProcessor.getTemplate("template/6.png", rect.width, rect.height);
		matchResult = ImageProcessor.templatematch(src, note6, 0.4);
		if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
			Imgproc.rectangle(page, new Point(rect.x+x,rect.y+y), new Point(rect.x+x+rect.width,rect.y+y+rect.height), new Scalar(0,0,255),3);
//					Imgproc.rectangle(temp, rect.br(), rect.tl(), new Scalar(0,0,255));
//					System.out.print("6 ");
			return 6;
		}
		Mat note7 = ImageProcessor.getTemplate("template/7.png", rect.width, rect.height);
		matchResult = ImageProcessor.templatematch(src, note7, 0.5);
		if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
			Imgproc.rectangle(page, new Point(rect.x+x,rect.y+y), new Point(rect.x+x+rect.width,rect.y+y+rect.height), new Scalar(0,0,255),3);
//					Imgproc.rectangle(temp, rect.br(), rect.tl(), new Scalar(0,0,255));
//					System.out.print("7 ");
			return 7;
		}
		Mat note0 = ImageProcessor.getTemplate("template/0.png", rect.width, rect.height);
		matchResult = ImageProcessor.templatematch(src, note0, 0.4);
		if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
			Imgproc.rectangle(page, new Point(rect.x+x,rect.y+y), new Point(rect.x+x+rect.width,rect.y+y+rect.height), new Scalar(0,0,255),3);
//					Imgproc.rectangle(temp, rect.br(), rect.tl(), new Scalar(0,0,255));
//					System.out.print("1 ");
			return 0;
		}
		return 0;
	}
}

class JNote {
	int duration;
	int pinchOctave;
	int pinchStep;// 1 to 7
	int dot;
	int accidentals;
	
	int x;//position
	int y;
	public JNote(int dashNum,int subNum,int pointNum,int num,int dotNum){
		pinchOctave = pointNum;
		pinchStep = num;
		duration = 4;
		if(subNum!=0){
			duration*=Math.pow(2, subNum);
		}else {
			if(dashNum==1){
				duration = 2;
			}else if (dashNum==3) {
				duration = 1;
			}
		}
		dot = dotNum;
	}
}
