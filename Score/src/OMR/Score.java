package OMR;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.FormatFlagsConversionMismatchException;
import java.util.List;
import java.util.Vector;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.IconifyAction;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import UI.GlobleVariable;

class PartMat {
	Mat mat;
	Vector<Integer> lineLoc;
	int yloc;

	public PartMat(Mat mat, Vector<Integer> lineLoc,int yloc) {
		super();
		this.yloc =yloc;
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

	public int getYloc() {
		return yloc;
	}

	public void setYloc(int yloc) {
		this.yloc = yloc;
	}
}

public class Score {
	Vector<Part> parts;

	public void convert2lilypond(String outName){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		
		
		

		//Score score = new Score(GlobleVariable.getScoreDir(), GlobleVariable.getPartNum());

		PrintWriter out = null;

		// File f = new File(filename);
		try {
			out = new PrintWriter(new FileWriter(outName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.print("{ << ");
		
		for (Part part : getParts()) {
			out.print("\\new Staff{ \n");
			
				out.print("\\clef treble ");
				out.print("\\time 2/4 ");
			// System.out.println("===============part============");
			// for(Measure measure:part.getMeasures()){
			for (int i = 0; i < part.getMeasures().size(); ++i) {
				// ImageProcessor.imshow(measure.getImage());
				int time = 0;// 16/16
				Measure measure = part.getMeasures().get(i);
				for (BeamedNote group : measure.getNoteGroups()) {
					for (Vector<Note> notes : group.getNotes()) {
						// System.out.println(note.getPinchStep()+"
						// "+note.getDuration());
						if(notes.size()!=1)
							out.print("<");
						
						int duration = 0;
						boolean first = true;
						for(Note note:notes){
						int pitch = note.getPinchStep();
	
						if (!group.isRest) {
							int step=0;
							int dotnum =0;
							if(pitch>=0){
								dotnum = pitch / 7 + 1;
								step = pitch % 7;
								if(step==0){
									dotnum--;
								}
							}else {
								int t = pitch+7;
								dotnum = 1;
								step = t % 7;
								
							}
							char c = 'c';
							switch (step) {
							case 1:
								c = 'c';
								break;
							case 2:
								c = 'd';
								break;
							case 3:
								c = 'e';
								break;
							case 4:
								c = 'f';
								break;
							case 5:
								c = 'g';
								break;
							case 6:
								c = 'a';
								break;
							case 0:
								c = 'b';
								break;

							default:
								break;
							}
							out.print(c);
							if(pitch>=0){
							for (int j = 0; j < dotnum; ++j) {
								out.print('\'');
							}}else {
								for (int j = 0; j < dotnum; ++j) {
									out.print(',');
								}
							}
							if(notes.size()==1){
								out.print(note.getDuration());
								time+=16/note.getDuration();
							}else {
								duration = note.getDuration();
								if (first) {
									time+=16/note.getDuration();
									first = false;
								}
							}
							int sTime = note.getDuration();
							
							for(int j=0;j<note.dot;++j){
								out.print('.');
								sTime*=2;
								time+=16/sTime;
							}
							out.print(" ");
						} else {
							int sTime = note.getDuration();
							out.print("r" + note.getDuration());
							time+=16/sTime;
							for(int j=0;j<note.dot;++j){
								out.print('.');
								sTime*=2;
								time+=16/sTime;
							}
							out.print(" ");
						}
						// System.out.println("-------[goup]--------");
					}
						if(notes.size()!=1){
							out.print(">"+duration+" ");
						}
					}
					
				}
				if(time==16){
					out.print("[Y "+time+"]");
				}else {
					out.print("[N "+time+"]");
				}
				out.print(" |\n");
			}
			out.println("}");
		}
		out.print(">> }");
		out.close();
		try {
			Runtime.getRuntime().exec("C:\\Program Files (x86)\\LilyPond\\usr\\bin\\lilypond a.ly");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Score(String dirName, int partNum) {
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

		Vector<Mat> sheets = getSheets(dirName);// get sheets list in binary
												// format.
		boolean[] firstPart = new boolean[partNum];
		for (int i = 0; i < partNum; ++i) {
			firstPart[i] = true;
		}
		
		for (Mat page : sheets) {
			
			
			Vector<Vector<PartMat>> partArray = getPart(page, partNum);
			Mat out = page.clone();
			Imgproc.cvtColor(out, out, Imgproc.COLOR_GRAY2BGR);
			for (int k = 0; k < partNum; ++k) {
				//
				Part part = new Part(partArray.get(k).get(0).getMat(), partArray.get(k).get(0).getLineLoc(), k,out,partArray.get(k).get(0).getYloc());
				if (firstPart[k]) {
					parts.add(part);
					firstPart[k] = false;
				} else {
					parts.get(k).merge(part);
				}
				for (int i = 1; i < partArray.get(k).size(); ++i) {
					parts.get(k)
							.merge(new Part(partArray.get(k).get(i).getMat(), partArray.get(k).get(i).getLineLoc(), k,out,partArray.get(k).get(i).getYloc()));
				}
			}
			Imgcodecs.imwrite("output/img"+page.hashCode()+".png",out);
			
		}
	}

	public static Vector<Vector<PartMat>> getPart(Mat pageMat, int partNum) {
		// need return stave line info otherwise need to recalculate the linLoc.
		// remove stave line and divide page into segment.

		Vector<Vector<PartMat>> partArray = new Vector<>();
		for (int i = 0; i < partNum; ++i) {
			partArray.add(new Vector<>());
		}

		Mat page = pageMat;// not change the source mat

		Vector<Integer> lineLoc = ImageProcessor.findPeaks(page);// array of
																	// index.

		// Mat out = page.clone();
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
			for (int j = 0; j < 5; ++j) {
				localLineLocs.add(lineLoc.get(i * 5 + j) - divider.get(i));
			}
			// ImageProcessor.imshow(partMat);
			// System.out.println(i);
			partArray.get(i % partNum).add(new PartMat(partMat, localLineLocs,divider.get(i)));
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

	Vector<Measure> getMeasures() {
		return measures;
	}

	public void merge(Part part) {
		measures.addAll(part.getMeasures());
	}

	public Part(Mat partMat, Vector<Integer> lineLoc, int id,Mat sheet,int y) {
		//should pass sheet mat and part mat location.
		// divide part mat into measure mat.
		// need to find bar line first.

		// Mat out = partMat.clone();
		// Imgproc.cvtColor(out, out, Imgproc.COLOR_GRAY2BGR);
		this.id = id;
		measures = new Vector<>();
		
		
		
		Vector<Integer> barLineLoc = new Vector<>();
		//ImageProcessor.imshow(partMat);
		for(int i=0;i<partMat.cols();++i){
			boolean flag = true;
			for(int j=lineLoc.get(0)+5;j<lineLoc.get(4)-5;++j){
				if(partMat.get(j, i)[0]>0.3){//white
					flag = false;
					break;
				}
				
			}
			if(flag){//remove stem.
				int left = i-30>0?i-30:0;
				int width = 60;
				if(left==0){
					continue;
					//width = 30;
				}
				if(partMat.cols()<left+width){
					continue;
				}
				//ImageProcessor.imshow(partMat.submat(new Rect(left, 0, width, partMat.rows())));
				Vector<Point> half = BeamedNote.getNoteHeadLoc(partMat, new Rect(left, lineLoc.get(0)-80, width, 240), "template/half.png");
				Vector<Point> quarter = BeamedNote.getNoteHeadLoc(partMat,new Rect(left, lineLoc.get(0)-80, width, 240), "template/quarter.png");
				if(half.size()==0&&quarter.size()==0){
					barLineLoc.add(i);
				}
			}
		}
		/*List<MatOfPoint> contours = ImageProcessor.findContours(partMat);
		contours.sort(new Comparator<MatOfPoint>() {

			@Override
			public int compare(MatOfPoint o1, MatOfPoint o2) {
				// sort by x
				return Imgproc.boundingRect(o1).x - Imgproc.boundingRect(o2).x;
			}
		});
		Vector<Integer> barLineLoc = new Vector<>();
		for (int i = 0; i < contours.size(); ++i) {
			Rect box = Imgproc.boundingRect(contours.get(i));
			if (box.y > lineLoc.get(0) || box.y + box.height < lineLoc.get(4)) {// between
																				// stave
																				// line.
				continue;
			}

			Mat temp = partMat.submat(box).clone();
			Mat vector = new Mat();
			Core.reduce(temp, vector, 0, Core.REDUCE_AVG);
			for (int m = 0; m < vector.cols(); ++m) {// cols
				if (vector.get(0, m)[0] < 255 * 0.3) {// 1 is line
					// Imgcodecs.imwrite("output/img"+partMat.hashCode()+i+".png",partMat.submat(box));
					Vector<Point> half = BeamedNote.getNoteHeadLoc(partMat, box, "template/half.png");
					Vector<Point> quarter = BeamedNote.getNoteHeadLoc(partMat, box, "template/quarter.png");
					boolean flag = false;
					for (Point p : half) {
						if (Math.abs(p.x - m) < 20) {
							flag = true;
						}
					}
					for (Point p : quarter) {
						if (Math.abs(p.x - m) < 20) {
							flag = true;
						}
					}
					if (!flag) {
						int left = m > 0 ? m : 0;
						int right = left + 1 < box.width ? 1 : box.width - left;
						List<MatOfPoint> tempcont = ImageProcessor
								.findContours(temp.submat(new Rect(left, 0, right, box.height)));
						for (int k = 0; k < tempcont.size(); ++k) {
							Rect tRect = Imgproc.boundingRect(tempcont.get(k));
							if (tRect.height > 70) {
								barLineLoc.add(box.x + m);
								break;
							}
						}
						// barLineLoc.add(box.x+m);
					}
					break;
				}
			}*/

			/*
			 * if (box.width < 10 && box.height > 50) {//stand alone bar line.
			 * barLineLoc.add(box.x); //Imgproc.drawContours(out, contours, i,
			 * new Scalar(0,0,255)); continue; } if(box.height > 70){ Mat temp =
			 * partMat.submat(box).clone(); Mat vector = new Mat();
			 * Core.reduce(temp, vector, 0, Core.REDUCE_AVG); boolean flag =
			 * false; for (int m = 0; m < vector.cols(); ++m) {//cols if
			 * (vector.get(0, m)[0] ==0) { barLineLoc.add(box.x+m); flag = true;
			 * break; } } if(flag){ continue; } } if(box.height > 80){ Mat temp
			 * = partMat.submat(box).clone(); Mat vector = new Mat();
			 * Core.reduce(temp, vector, 0, Core.REDUCE_AVG); for (int m = 0; m
			 * < vector.cols(); ++m) {//cols if (vector.get(0, m)[0] <255*0.1)
			 * {// 1 is line
			 * //Imgcodecs.imwrite("output/img"+partMat.hashCode()+i+".png",
			 * partMat.submat(box)); Vector<Point> half =
			 * BeamedNote.getNoteHeadLoc(partMat, box, "template/half.png");
			 * Vector<Point> quarter = BeamedNote.getNoteHeadLoc(partMat, box,
			 * "template/quarter.png"); boolean flag = false; for(Point p:half){
			 * if(Math.abs(p.x-m)<20){ flag = true; } } for(Point p:quarter){
			 * if(Math.abs(p.x-m)<20){ flag = true; } } if(!flag){
			 * barLineLoc.add(box.x+m); } break; } } }
			 */
		//}
		Collections.sort(barLineLoc);
		for (int i = 0; i < barLineLoc.size(); ++i) {// remove double bar line
			
			if (i != 0 && Math.abs(barLineLoc.get(i) - barLineLoc.get(i - 1)) < 40) {
				barLineLoc.remove(i);
				--i;
			} else {
				//System.out.println(barLineLoc.get(i));
				Imgproc.line(sheet, new Point(barLineLoc.get(i),y) ,new Point(barLineLoc.get(i),y+200), new Scalar(0,255,0), 5);
				//ImageProcessor.imshow(sheet);
			}
		}
		for (int i = 0; i < barLineLoc.size() - 1; ++i) {
			Mat measureMat = partMat.submat(0, partMat.rows(), barLineLoc.get(i) + 10, barLineLoc.get(i + 1) - 10);
			// ImageProcessor.imshow(measureMat);
			// Imgcodecs.imwrite("output/img"+partMat.hashCode()+i+contours.hashCode()+".png",
			// measureMat);
			measures.add(new Measure(measureMat, lineLoc,sheet, barLineLoc.get(i) + 10,y));
			Imgcodecs.imwrite("measures/img"+id+partMat.hashCode()+i+".png", measureMat);
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

	public Measure(Mat measureImage, Vector<Integer> lineLocs,Mat sheet,int x,int y) {
		//should pass:measure mat location in sheet,sheet id or sheet mat
		image = measureImage;
		// ImageProcessor.imshow(measureImage);
		groups = new Vector<>();
		List<MatOfPoint> contours = ImageProcessor.findContours(measureImage);
		contours.sort(new Comparator<MatOfPoint>() {

			@Override
			public int compare(MatOfPoint o1, MatOfPoint o2) {
				// sort by x
				return Imgproc.boundingRect(o1).x - Imgproc.boundingRect(o2).x;
			}
		});
		// Imgproc.cvtColor(measureImage, measureImage, Imgproc.COLOR_GRAY2BGR);
		// int count = 0;
		Mat out = sheet;
		//Imgproc.cvtColor(out, out, Imgproc.COLOR_GRAY2BGR);
		// for(int i:lineLocs){
		// Imgproc.line(out, new Point(0,i),new Point(out.cols(),i), new
		// Scalar(0,0,255));
		// }

		for (int i = 0; i < contours.size(); ++i) {
			// Imgproc.drawContours(measureImage, contours, i, new
			// Scalar(0,0,255));
			Rect rect = Imgproc.boundingRect(contours.get(i));
			if (rect.height > 3 && rect.width > 3) {
				// divide contours into:note group,dot,clef,key,accidential.

				// detect note
				Mat src = measureImage.submat(rect);
				// ImageProcessor.imshow(src);
				// ---------------------------------------------			
				
				// ---------------------------------------------
				if (src.cols() >= 40 && src.rows() >= 100) {
					Mat gClef = ImageProcessor.getTemplate("template/g.png", rect.width, rect.height);
					Mat matchResult = ImageProcessor.templatematch(src, gClef, 0.4);// 0.44
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						Imgproc.rectangle(out, new Point(rect.x+x, rect.y+y),new Point(rect.x+x+rect.width, rect.y+y+rect.height), new Scalar(255,0,0));
						// g clef
						// Imgproc.rectangle(out, rect.tl(), rect.br(), new
						// Scalar(0,255,255));
						clefSign = 1;
						clefLine = 2;
						continue;
					}
				}
				// ---------------------------------------------
				if (src.cols() >= 30 && src.rows() >= 50) {
					Mat fClef = ImageProcessor.getTemplate("template/f.png", rect.width, rect.height);
					Mat matchResult = ImageProcessor.templatematch(src, fClef, 0.4);
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						Imgproc.rectangle(out,new Point(rect.x+x, rect.y+y),new Point(rect.x+x+rect.width, rect.y+y+rect.height), new Scalar(255,0,0));
						// f clef
						// Imgproc.rectangle(out, rect.tl(), rect.br(), new
						// Scalar(255,0,255));
						clefSign = 2;
						clefLine = 4;
						continue;
					}
				}
				
				if (src.cols() >= 26 && src.rows() >= 22) {//notes
					Mat matchResult = null;
					if(src.rows()>60){
					Mat quarterNote = ImageProcessor.getTemplate("template/quarter.png", 26, 22);
					matchResult = ImageProcessor.templatematch(src, quarterNote, 0.55);
					if (Core.minMaxLoc(matchResult).maxVal > 1) {
						// <4 note
						Imgproc.rectangle(out, new Point(rect.x+x, rect.y+y),new Point(rect.x+x+rect.width, rect.y+y+rect.height), new Scalar(0,0,255));
						groups.add(new BeamedNote(measureImage, rect, 0, lineLocs,sheet,x,y));
						
						continue;
					}
					// ---------------------------------------------
					Mat halfNote = ImageProcessor.getTemplate("template/half.png", 26, 22);
					matchResult = ImageProcessor.templatematch(src, halfNote, 0.45);
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						// 2note
						Imgproc.rectangle(out, new Point(rect.x+x, rect.y+y),new Point(rect.x+x+rect.width, rect.y+y+rect.height), new Scalar(0,0,255));
						groups.add(new BeamedNote(measureImage, rect, 2, lineLocs,sheet,x,y));
						continue;
					}}
					// ---------------------------------------------
					Mat wholeNote = ImageProcessor.getTemplate("template/whole.png", 26, 22);
					matchResult = ImageProcessor.templatematch(src, wholeNote, 0.4);
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						// 1note
						Imgproc.rectangle(out, new Point(rect.x+x, rect.y+y),new Point(rect.x+x+rect.width, rect.y+y+rect.height), new Scalar(0,0,255));
						groups.add(new BeamedNote(measureImage, rect, 1, lineLocs,sheet,x,y));
						continue;
					}
				}
				// ---------------------------------------------21 58
				if (src.cols() >= 12 && src.rows() >= 45) {// give a min size.
					Mat sharp = ImageProcessor.getTemplate("template/sharp.png", rect.width, rect.height);
					Mat matchResult = ImageProcessor.templatematch(src, sharp, 0.4);
					if (Core.minMaxLoc(matchResult).maxVal > 0.55) {
						Imgproc.rectangle(out, new Point(rect.x+x, rect.y+y),new Point(rect.x+x+rect.width, rect.y+y+rect.height), new Scalar(0,255,0));
						// sharp
						
						continue;
					}
					// ---------------------------------------------
					
					Mat flat = ImageProcessor.getTemplate("template/flat.png", rect.width, rect.height);
					matchResult = ImageProcessor.templatematch(src, flat, 0.15);
					// System.out.println(max);
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						Imgproc.rectangle(out, new Point(rect.x+x, rect.y+y),new Point(rect.x+x+rect.width, rect.y+y+rect.height), new Scalar(0,255,0));
						// flat
						
						continue;
					}
					// ---------------------------------------------
					// ImageProcessor.imshow(src);
					Mat natural = ImageProcessor.getTemplate("template/natural.png", rect.width, rect.height);
					matchResult = ImageProcessor.templatematch(src, natural, 0.25);
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						Imgproc.rectangle(out, new Point(rect.x+x, rect.y+y),new Point(rect.x+x+rect.width, rect.y+y+rect.height), new Scalar(0,255,0));
						// natural
						
						continue;
					}
				}
				if (src.cols() >= 20 && src.rows() >= 50) {
					Mat qurterRest = ImageProcessor.getTemplate("template/quarterrest.png", rect.width, rect.height);
					Mat matchResult = ImageProcessor.templatematch(src, qurterRest, 0.4);
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						Imgproc.rectangle(out, new Point(rect.x+x, rect.y+y),new Point(rect.x+x+rect.width, rect.y+y+rect.height), new Scalar(255,255,0));
						// 4rest
						
						groups.add(new BeamedNote(src, rect, -4, lineLocs,null,0,0));
						continue;
					}
				}
				if (src.cols() >= 15 && src.rows() >= 25) {
					Mat rest8 = ImageProcessor.getTemplate("template/8rest.png", rect.width, rect.height);
					Mat matchResult = ImageProcessor.templatematch(src, rest8, 0.4);
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						Imgproc.rectangle(out, new Point(rect.x+x, rect.y+y),new Point(rect.x+x+rect.width, rect.y+y+rect.height), new Scalar(255,255,0));
						// 8rest
						
						groups.add(new BeamedNote(src, rect, -8, lineLocs,null,0,0));
						continue;
					}
				}
				if (src.cols() >= 15 && src.rows() >= 50) {
					Mat rest16 = ImageProcessor.getTemplate("template/16rest.png", rect.width, rect.height);
					Mat matchResult = ImageProcessor.templatematch(src, rest16, 0.3);
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						Imgproc.rectangle(out, new Point(rect.x+x, rect.y+y),new Point(rect.x+x+rect.width, rect.y+y+rect.height), new Scalar(255,255,0));
						// 16rest
						// Imgproc.rectangle(out, rect.tl(), rect.br(), new
						// Scalar(30,190,165));
						groups.add(new BeamedNote(src, rect, -16, lineLocs,null,0,0));
						continue;
					}
				}
				if (src.cols() >= 15 && src.rows() >= 60) {
					Mat rest32 = ImageProcessor.getTemplate("template/32rest.png", rect.width, rect.height);
					Mat matchResult = ImageProcessor.templatematch(src, rest32, 0.3);
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						Imgproc.rectangle(out, new Point(rect.x+x, rect.y+y),new Point(rect.x+x+rect.width, rect.y+y+rect.height), new Scalar(255,255,0));
						// 32rest
						// Imgproc.rectangle(out, rect.tl(), rect.br(), new
						// Scalar(24,147,230));
						groups.add(new BeamedNote(src, rect, -32, lineLocs,null,0,0));
						continue;
					}
				}
				if (src.cols() >= 24 && src.cols() <= 28 && src.rows() >= 8 && src.rows() <= 12) {
					// ImageProcessor.imshow(src);
					Mat wholeHalfRest = ImageProcessor.getTemplate("template/wholehalfrest.png", rect.width,
							rect.height);
					Mat matchResult = ImageProcessor.templatematch(src, wholeHalfRest, 0.98);
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						Imgproc.rectangle(out, new Point(rect.x+x, rect.y+y),new Point(rect.x+x+rect.width, rect.y+y+rect.height), new Scalar(255,255,0));
						// whole rest
						// Imgproc.rectangle(out, rect.tl(), rect.br(), new
						// Scalar(0,0,255));
						// groups.add(new BeamedNote(src, rect, -1));
						continue;
					}
				}
				/*if(src.cols()<11&&src.rows()<11){
					if(groups.size()>0){
						groups.get(groups.size()-1).notes.get(0).dot++;
					}
				}*/

				if(src.cols()<12&&src.rows()<12){
					//dot,should attach to the closest note in early group.
					Imgproc.rectangle(out, new Point(rect.x+x, rect.y+y),new Point(rect.x+x+rect.width, rect.y+y+rect.height), new Scalar(255,255,0),1);
					if(!groups.isEmpty()){
						BeamedNote beamedNote = groups.get(groups.size()-1);//last group.
						Vector<Vector<Note>> notes = beamedNote.getNotes();//no loc info.
						if(beamedNote.isRest&&notes.get(0).get(0).headLoc.x-rect.x<30){
							notes.get(0).get(0).addDot();
						}else{
							
						for(int ii=0;ii<notes.size();++ii){
							while(ii<notes.size()&&rect.x-notes.get(ii).get(0).headLoc.x>30){
								++ii;
							}
							if(ii<notes.size()){
								for(int xx=notes.get(ii).size()-1;xx>=0;--xx){
									if(Math.abs(notes.get(ii).get(xx).headLoc.y-rect.y)<20){
										
										notes.get(ii).get(xx).addDot();
										break;
									}
								}
								break;
							}
						}}
					}
				}
				// something else, skip
				;

				//Imgproc.rectangle(out, rect.tl(), rect.br(), new Scalar(0,0,255));
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
	boolean isRest = false;

	// music info
	Vector<Vector<Note>> notePlaces;

	public static int getPinch(Mat measureMat, Point headLoc, Vector<Integer> lineLoc) {
		// warning : headLoc is measurewide.
		//Mat out = measureMat.clone();
		//Imgproc.cvtColor(out, out, Imgproc.COLOR_GRAY2BGR);
		/*for(int i=0;i<lineLoc.size();++i){
			
			Imgproc.line(out, new Point(0,lineLoc.get(i)), new Point(measureMat.cols(),lineLoc.get(i)), new Scalar(0,255,0));
		}*/
		Vector<Integer> tempLinLoc = new Vector<>();
		for (int i = 4; i > 0; --i) {
			tempLinLoc.add(lineLoc.get(0) - 19 * i);
		}

		for (int i = 0; i < 5; ++i) {
			tempLinLoc.add(lineLoc.get(i));
		}

		for (int i = 1; i < 5; ++i) {
			tempLinLoc.add(lineLoc.get(4) + 19 * i);
		}
		for (int i = 12; i > 0; --i) {
			tempLinLoc.add(i, (tempLinLoc.get(i) + tempLinLoc.get(i - 1)) / 2);
		}
		/*for(int i=0;i<tempLinLoc.size();++i){
			
			Imgproc.line(out, new Point(0,tempLinLoc.get(i)), new Point(measureMat.cols(),tempLinLoc.get(i)), new Scalar(0,255,0));
		}*/
		//Imgproc.circle(out, headLoc, 10, new Scalar(0,0,255));
		//ImageProcessor.imshow(out);
		int mindif = 100000;
		int pinch = 20;
		for (int y : tempLinLoc) {
			int dif = (int) Math.abs(headLoc.y - y);
			if (dif < mindif) {
				mindif = dif;
				pinch--;
			} else {
				break;
			}
		}
		return pinch;
	}

	public static Vector<Point> getNoteHeadLoc(Mat measureMat, Rect rect, String template) {
		// warning : head loc is group wide.
		//headloc was sorted!!!!
		Vector<Point> noteLoc = new Vector<>();
		if (rect.height >= 22 && rect.width >= 26) {
			Mat teMat = ImageProcessor.getTemplate(template, 26, 22);
			double thresh = 0.4;
			if(template.equals("template/quarter.png")){
				thresh=0.55;
			}else if(template.equals("template/half.png")){
				thresh = 0.45;
			}
			Mat matchResult = ImageProcessor.templatematch(measureMat.submat(rect), teMat, thresh);

			for (int i = 0; i < matchResult.cols(); ++i) {
				for (int j = 0; j < matchResult.rows(); ++j) {
					if (matchResult.get(j, i)[0] > 0.3) {
						noteLoc.add(new Point(i + 13, j + 11));
					}

				}
			}
		}
		noteLoc.sort(new Comparator<Point>() {

			@Override
			public int compare(Point o1, Point o2) {
				if(Math.abs(o1.x-o2.x)<20){
					return (int) (o1.y-o2.y);
				}else {
					return (int) (o1.x-o2.x);
				}
			}
		});
		return noteLoc;
	}

	public Vector<Integer> getDuration(Mat groupMat, Vector<Point> headLocs) {
		// determine the note direction, up or down
		
		
		Vector<Integer> durations = new Vector<>();
		Vector<Integer> stemLocs = ImageProcessor.findPeakY(groupMat, false);
		if (headLocs.get(0).x < stemLocs.get(0)) {
			// down,
			for (int x=0;x<headLocs.size();++x){
				Point p = headLocs.get(x);
				int left = 0, right = 0;
				int leftcounter = 0;
				boolean leftinbalck = true;
				boolean lefthead = true;
				int rightcounter = 0;
				boolean rightinbalck = false;
				// boolean righthead = true;
				boolean leftAdded = false;
				boolean rightAdded = false;
				for (int i = (int) p.y; i > 0; --i) {// bottom to top.
					// count left
					if (groupMat.get(i, (int) p.x+5)[0] < 0.2) {// black
						if (lefthead) {
							// continue;
						} else {
							if (leftinbalck) {
								leftcounter++;
								if (leftcounter > 5 && !leftAdded) {
									left++;
									leftAdded = true;
								}
								;// do nothing.
							} else {
								leftinbalck = true;// first in black.
								leftcounter++;
							}
						}
					} else {// white.
						leftcounter=0;
						leftAdded = false;
						lefthead = false;
						if (!leftinbalck) {
							// continue;
						} else {
							leftinbalck = false;
						}
					}
					// count right.
					int headWidth = 20;
					if (p.x + headWidth < groupMat.cols()) {

						if (groupMat.get(i, (int) p.x + headWidth)[0] < 0.2) {// black

							if (rightinbalck) {
								rightcounter++;
								if (rightcounter > 5 && !rightAdded) {
									right++;
									rightAdded = true;
								}
								;// do nothing.
							} else {
								rightinbalck = true;// first in black.
								rightcounter++;
							}

						} else {// white.
							rightcounter=0;
							rightAdded = false;
							// righthead =false;
							if (!rightinbalck) {
								continue;
							} else {
								rightinbalck = false;
							}
						}
					}
				}
				if (left > right) {
					durations.add((int) Math.pow(2, left + 2));
				} else {
					durations.add((int) Math.pow(2, right + 2));
				}
				if(x-1>=0&&Math.abs(headLocs.get(x).x-headLocs.get(x-1).x)<20){
					++x;//escape!!!!
				}
			}
			
		} else {
			// up,
			for (int x=0;x<headLocs.size();++x) {
				while(x+1<headLocs.size()&&Math.abs(headLocs.get(x).x-headLocs.get(x+1).x)<20){
					++x;//escape!!!!
				}
				Point p = headLocs.get(x);
				int left = 0, right = 0;
				int leftcounter = 0;
				boolean leftinbalck = false;
				// boolean lefthead = true;
				int rightcounter = 0;
				boolean rightinbalck = true;
				boolean righthead = true;
				boolean leftAdded = false;
				boolean rightAdded = false;
				for (int i = (int) p.y; i < groupMat.rows(); ++i) {// top to
																	// bottom
					// Imgproc.line(out, new Point((int) p.x, g.getPos().y),new
					// Point((int) p.x, g.getPos().y+g.getPos().height), new
					// Scalar(0,0,255));
					// count right
					if (groupMat.get(i, (int) p.x-5)[0] < 0.2) {// black

						if (righthead) {
							// continue;
						} else {
							if (rightinbalck) {
								rightcounter++;
								if (rightcounter > 5 && !rightAdded) {
									right++;
									rightAdded = true;
								}
								;// do nothing.
							} else {
								rightinbalck = true;// first in black.
								rightcounter++;
							}
						}
					} else {// white.
						rightcounter=0;
						rightAdded = false;
						righthead = false;
						if (!rightinbalck) {
							// continue;
						} else {// first in white.
							rightinbalck = false;
						}
					}
					
					int headWidth = 20;
					if (p.x - headWidth > 0) {
						if (groupMat.get(i, (int) p.x - headWidth)[0] < 0.2) {// black

							if (leftinbalck) {
								leftcounter++;
								if (leftcounter > 5 && !leftAdded) {
									left++;
									leftAdded = true;
								}
								;// do nothing.
							} else {
								leftinbalck = true;// first in black.
								leftcounter++;
							}

						} else {// white.
							leftcounter=0;
							leftAdded = false;

							if (!leftinbalck) {
								continue;
							} else {// first in white.
								leftinbalck = false;
							}
						}
					}
				}
				if (left > right) {
					durations.add((int) Math.pow(2, left + 2));
				} else {
					durations.add((int) Math.pow(2, right + 2));
				}
			}
		}

		return durations;
	}

	public BeamedNote(Mat measureMat, Rect position, int duration, Vector<Integer> lineLoc,Mat sheet,int x,int y) {
		// notes in group sort by x.
		if(duration<0){
			isRest = true;
		}
		notePlaces = new Vector<>();
		int pinch = 0;
		Vector<Note> noteT;
		// if(duration!=0){
		switch (duration) {
		case 0://4 or less.
			Vector<Point> headLoc = getNoteHeadLoc(measureMat, position, "template/quarter.png");
			for(Point p:headLoc){
				Imgproc.circle(sheet, new Point(p.x+x+position.x,p.y+position.y+y), 10, new Scalar(255,0,0),5);
			}
			
			Vector<Vector<Point>> places = new Vector<>();
			for(int i =0;i<headLoc.size();++i){
				Vector<Point> place = new Vector<>();
				place.add(headLoc.get(i));
				while(i+1<headLoc.size()&&Math.abs(headLoc.get(i).x-headLoc.get(i+1).x)<25){
					place.add(headLoc.get(i+1));
					++i;
				}
				places.add(place);
			}
			if(places.size()==1&&places.get(0).size()==3){
				System.err.println();
			}
			
			Vector<Integer> durations = getDuration(measureMat.submat(position), headLoc);
			
			for(int i=0;i<places.size();++i){
				Vector<Note> notes = new Vector<>();
				
				for(int j=0;j<places.get(i).size();++j){
					pinch = getPinch(measureMat,new Point(places.get(i).get(j).x+ position.x,places.get(i).get(j).y+position.y), lineLoc);
					notes.add(new Note(durations.get(i), 0, pinch, new Point(places.get(i).get(j).x+ position.x,places.get(i).get(j).y+position.y)));
					
				}
				
				notePlaces.add(notes);
			}
			break;
		case 1:
			
			Vector<Point> headLocWhole = getNoteHeadLoc(measureMat, position, "template/whole.png");
			for(Point p:headLocWhole){
				Imgproc.circle(sheet, new Point(p.x+x+position.x,p.y+position.y+y), 10, new Scalar(0,255,0),5);
			}
			noteT = new Vector<>();
			for(Point p:headLocWhole){
				pinch = getPinch(measureMat, new Point(p.x + position.x, p.y + position.y), lineLoc);
				noteT.add(new Note(1, 0, pinch, new Point(p.x + position.x, p.y + position.y)));
			}
			notePlaces.add(noteT);
			break;
		case 2:
			
			Vector<Point> headLochalf = getNoteHeadLoc(measureMat, position, "template/half.png");
			
			
			noteT = new Vector<>();
			for(Point p:headLochalf){
				Imgproc.circle(sheet, new Point(p.x+x+position.x,p.y+position.y+y), 10, new Scalar(0,0,255),5);
				pinch = getPinch(measureMat, new Point(p.x + position.x,p.y + position.y), lineLoc);
				noteT.add(new Note(2, 0, pinch, new Point(p.x+position.x,p.y+position.y)));
			}
			notePlaces.add(noteT);
			break;
		case -1:
			noteT = new Vector<>();
			noteT.add(new Note(1, 0, 0, position.br()));
			notePlaces.add(noteT);
			break;
		case -2:
			noteT = new Vector<>();
			noteT.add(new Note(2, 0, 0,  position.br()));
			notePlaces.add(noteT);
			break;
		case -4:
			noteT = new Vector<>();
			noteT.add(new Note(4, 0, 0,  position.br()));
			notePlaces.add(noteT);
			break;
		case -8:
			noteT = new Vector<>();
			noteT.add(new Note(8, 0, 0,  position.br()));
			notePlaces.add(noteT);
			break;
		case -16:
			noteT = new Vector<>();
			noteT.add(new Note(16, 0, 0,  position.br()));
			notePlaces.add(noteT);
			break;
		case -32:
			noteT = new Vector<>();
			noteT.add(new Note(32, 0, 0,  position.br()));
			notePlaces.add(noteT);
			break;

		default:
			break;
		}
		
	}

	public Vector<Vector<Note>> getNotes() {
		return notePlaces;
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

	public Note(int duration, int pinchOctave, int pinchStep,Point headLoc) {
		super();
		this.duration = duration;
		this.pinchOctave = pinchOctave;
		this.pinchStep = pinchStep;
		this.headLoc = headLoc;
	}

	public int getDuration() {
		return duration;
	}

	public int getPinchStep() {
		return pinchStep;
	}
	
	public void addDot(){
		dot++;
	}
	public int getDot() {
		return dot;
	}

	public void setDot(int dot) {
		this.dot = dot;
	}

}