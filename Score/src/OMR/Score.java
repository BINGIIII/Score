package OMR;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.crypto.Mac;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;



public class Score {
	Vector<Part> parts;
	public static void main(String[] args){
		new Score("canon", 2).convert2lilypond("x.ly");
	}

	public void convert2lilypond(String outName) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		PrintWriter out = null;

		try {
			out = new PrintWriter(new FileWriter(outName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		out.print("{ << ");
		final String[] numMap = {"zero","one","two","three"};
		final String[] clefMap = {"treble","bass"};
		for(int ii=0;ii<parts.size();++ii){
			Part part = parts.get(ii);
//			out.print("\\new Staff{ \n");//part start

//			out.print("\\clef treble ");
//			out.print("\\time 2/4 ");
			out.println("Part"+numMap[ii]+"={");
			out.print("\\clef \""+clefMap[ii]+"\"");
			out.println("\\tempo 4=96 ");

			for (int i = 0; i < part.getMeasures().size(); ++i) {

				int time = 0;// 32/32
				Measure measure = part.getMeasures().get(i);
				StringBuilder builder = new StringBuilder();
				for (BeamedNote group : measure.getNoteGroups()) {
					for (Vector<Note> notes : group.getNotes()) {

						if (notes.size() != 1)
							builder.append("<");

						int duration = 0;
						boolean first = true;
						for (Note note : notes) {
							int pitch = note.getPinchStep();

							if (!group.isRest) {
								int step = 0;
								int dotnum = 0;
								//if (pitch >= 0) {
								int kk = 0;
								while(pitch<0){
									pitch+=7;
									++kk;
								}
									dotnum = pitch / 7 + 1 -kk;
									step = pitch % 7;
									//if (step == 0) {
										//dotnum--;
									//}
								//} else {
									//int t = pitch + 7;
									//dotnum = 1;
									//step = t % 7;

								//}
								char c = 'c';
								switch (step) {
								case 0:
									c = 'c';
									break;
								case 1:
									c = 'd';
									break;
								case 2:
									c = 'e';
									break;
								case 3:
									c = 'f';
									break;
								case 4:
									c = 'g';
									break;
								case 5:
									c = 'a';
									break;
								case 6:
									c = 'b';
									break;

								default:
									break;
								}
								builder.append(c);
								if (dotnum >= 0) {
									for (int j = 0; j < dotnum; ++j) {
										builder.append('\'');
									}
								} else {
									for (int j = 0; j < -dotnum; ++j) {
										builder.append(',');
									}
								}
								if (notes.size() == 1) {
									builder.append(note.getDuration());
									time += 32 / note.getDuration();
									int sTime = note.getDuration();

									for (int j = 0; j < note.dot; ++j) {
										builder.append('.');
										sTime *= 2;
										time += 32 / sTime;
									}
									builder.append(" ");
								} else {
									duration = note.getDuration();
									if (first) {
										time += 32 / note.getDuration();
										first = false;
									}
									builder.append(" ");
								}
								
							} else {
								int sTime = note.getDuration();
								builder.append("r" + note.getDuration());
								time += 32 / sTime;
								for (int j = 0; j < note.dot; ++j) {
									builder.append('.');
									sTime *= 2;
									time += 32 / sTime;
								}
								builder.append(" ");
							}

						}
						if (notes.size() != 1) {
							builder.append(">" + duration + " ");
							/*int sTime = note.getDuration();

							for (int j = 0; j < note.dot; ++j) {
								builder.append('.');
								sTime *= 2;
								time += 16 / sTime;
							}
							builder.append(" ");*/
						}
					}

				}
				if (time == 32) {
					out.print(builder.toString());
					//builder.append("[Y " + time + "]");
				} else if(time<32){
					if(32%(32-time)!=0){
						int count = 32-time;
						while(count>1){
							count-=2;
							builder.append("r"+16);
						}
						if(count==1){
							builder.append("r"+32);
						}
					}else {
						builder.append("r"+32/(32-time));
					}
					
					out.print(builder.toString());
//					builder.append("[N " + time + "]");
				}else {
					out.print("r1");
				}
				out.print(" |\n");//end of measure!!!!
			}
			out.println("}");//end of part!!
		}
		String string = "\\score { \n << \n \\new PianoStaff << \n\\set PianoStaff.instrumentName = \"Piano\"\n\\context Staff = \"1\" << \n\\context Voice = \"PartPzero\" {\\Partzero }\n >> \\context Staff = \"2\" <<\n \\context Voice = \"Partone\" { \\Partone }  >> >>  >> \\layout {} \\midi {} }";
		out.print(string);//end of score!!
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
				Part part = new Part(partArray.get(k).get(0).getMat(), partArray.get(k).get(0).getLineLoc(), k, out,
						partArray.get(k).get(0).getYloc());
				if (firstPart[k]) {
					parts.add(part);
					firstPart[k] = false;
				} else {
					parts.get(k).merge(part);
				}
				for (int i = 1; i < partArray.get(k).size(); ++i) {
					parts.get(k).merge(new Part(partArray.get(k).get(i).getMat(), partArray.get(k).get(i).getLineLoc(),k, out, partArray.get(k).get(i).getYloc()));
				}
			}
			Imgcodecs.imwrite("output/img" + page.hashCode() + ".png", out);

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

		
		Vector<Integer> lineLoc = ImageProcessor.findPeaks(page);
		OMRVraible.setLineDistance(lineLoc.get(1)-lineLoc.get(0));
		
		int segmentWidth = lineLoc.get(5 * partNum) - lineLoc.get(0);
		int firstSegmentMid = (lineLoc.get(0) + lineLoc.get(5 * partNum - 1)) / 2;
		int segmentStart = firstSegmentMid - segmentWidth / 2 > 0 ? firstSegmentMid - segmentWidth / 2 : 0;
		
		int lastSegmentMid = (lineLoc.get(lineLoc.size() - partNum*5) + lineLoc.get(lineLoc.size() - 1)) / 2;
		int segmentEnd = lastSegmentMid + segmentWidth / 2 < page.rows() ? lastSegmentMid + segmentWidth / 2
				: page.rows();
		Vector<Integer> divider = new Vector<>();
		divider.add(segmentStart);
		for (int i = 0; i < lineLoc.size() / 5 - 1; ++i) {
			divider.add((lineLoc.get(5 * i + 4) + lineLoc.get(5 * i + 5)) / 2);
		}
		divider.add(segmentEnd);
		for (int i = 0; i < divider.size() - 1; ++i) {
			Vector<Integer> localLineLocs = new Vector<>();
			Mat partMat = page.submat(new Rect(0, divider.get(i), page.cols(), divider.get(i + 1) - divider.get(i)));
			for (int j = 0; j < 5; ++j) {
				localLineLocs.add(lineLoc.get(i * 5 + j) - divider.get(i));
			}

			partArray.get(i % partNum).add(new PartMat(partMat, localLineLocs, divider.get(i)));
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
			Imgproc.threshold(mat, mat, 200, 255, Imgproc.THRESH_BINARY);
			sheets.add(mat);
		}
		return sheets;
	}

	public Vector<Part> getParts() {
		return parts;
	}
}
class PartMat {
	Mat mat;
	Vector<Integer> lineLoc;
	int yloc;

	public PartMat(Mat mat, Vector<Integer> lineLoc, int yloc) {
		super();
		this.yloc = yloc;
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

class Part {
	int id;
	Vector<Measure> measures;

	Vector<Measure> getMeasures() {
		return measures;
	}

	public void merge(Part part) {
		measures.addAll(part.getMeasures());
	}

	public Part(Mat partMat, Vector<Integer> lineLoc, int id, Mat sheet, int y) {
		// should pass sheet mat and part mat location.
		// divide part mat into measure mat.
		// need to find bar line first.
		this.id = id;
		measures = new Vector<>();

		Vector<Integer> barLineLoc = new Vector<>();
		 
		for (int i = 0; i < partMat.cols(); ++i) {
			boolean flag = true;
			for (int j = lineLoc.get(0) + 1; j < lineLoc.get(4) - 1; ++j) {
				if (partMat.get(j, i)[0] > 0.3) {// white
					flag = false;
					break;
				}
			}
			if (flag) {// remove stem.
				int left = (int) (i - OMRVraible.headWidth*1.2 > 0 ? i - OMRVraible.headWidth*1.2 : 0);
				int width = (int) (OMRVraible.headWidth*2.4+left<partMat.cols()?OMRVraible.headWidth*2.4:partMat.cols()-left);
				int yy= lineLoc.get(0) -OMRVraible.lineDistance*4>0?lineLoc.get(0) -OMRVraible.lineDistance*4:0;
				int hight = yy+OMRVraible.lineDistance*12<partMat.rows()?OMRVraible.lineDistance*12:partMat.rows()-yy;
				Vector<Point> half = BeamedNote.getNoteHeadLoc(partMat, new Rect(left, yy, width, hight),
						"template/half.png");
				Vector<Point> quarter = BeamedNote.getNoteHeadLoc(partMat,
						new Rect(left, yy, width, hight), "template/quarter.png");
				if (half.size() == 0 && quarter.size() == 0) {
					barLineLoc.add(i);
				}
			}
		}

		Collections.sort(barLineLoc);
		for (int i = 0; i < barLineLoc.size(); ++i) {// remove double bar line

			if (i != 0 && Math.abs(barLineLoc.get(i) - barLineLoc.get(i - 1)) < OMRVraible.lineDistance*2) {
				barLineLoc.remove(i);
				--i;
			} else {//bingii
				Imgproc.line(sheet, new Point(barLineLoc.get(i), y), new Point(barLineLoc.get(i), y + OMRVraible.lineDistance*10),
						new Scalar(0, 255, 0), 5);

			}
		}
		
		//no first bar line
		if(barLineLoc.get(0)>OMRVraible.lineDistance*20){
			Mat measureMat = partMat.submat(0, partMat.rows(), 0, barLineLoc.get(0) - OMRVraible.lineDistance/2);

			measures.add(new Measure(measureMat, lineLoc, sheet, 0, y,id));
			Imgcodecs.imwrite("measures/img" + id + partMat.hashCode() + "00.png", measureMat);
		}
		for (int i = 0; i < barLineLoc.size() - 1; ++i) {
			Mat measureMat = partMat.submat(0, partMat.rows(), barLineLoc.get(i) + OMRVraible.lineDistance/2, barLineLoc.get(i + 1) - OMRVraible.lineDistance/2);

			measures.add(new Measure(measureMat, lineLoc, sheet, barLineLoc.get(i) + OMRVraible.lineDistance/2, y,id));
			//show contours
			/*List<MatOfPoint> contours = ImageProcessor.findContours(measureMat);
			Imgproc.cvtColor(measureMat, measureMat, Imgproc.COLOR_GRAY2BGR);
			for(MatOfPoint c:contours){
				Rect rect = Imgproc.boundingRect(c);
				Imgproc.rectangle(measureMat, rect.tl(), rect.br(), new Scalar(0,0,255),2 );
			}*/
			Imgcodecs.imwrite("measures/img" + id + partMat.hashCode() + i + ".png", measureMat);
		}
	
		

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

	public Measure(Mat measureImage, Vector<Integer> lineLocs, Mat sheet, int x, int y,int partID) {
		// should pass:measure mat location in sheet,sheet id or sheet mat
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

		Mat out = sheet;

		for (int i = 0; i < contours.size(); ++i) {

			Rect rect = Imgproc.boundingRect(contours.get(i));
			if (rect.height > 3 && rect.width > 3) {

				Mat src = measureImage.submat(rect);

				if (src.cols() >= OMRVraible.lineDistance*2 && src.rows() >= OMRVraible.lineDistance*5) {
					Mat gClef = ImageProcessor.getTemplate("template/g.png", rect.width, rect.height);
					Mat matchResult = ImageProcessor.templatematch(src, gClef, 0.4);// 0.44
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						Imgproc.rectangle(out, new Point(rect.x + x, rect.y + y),
								new Point(rect.x + x + rect.width, rect.y + y + rect.height), new Scalar(255, 0, 0));
						// g clef

						clefSign = 1;
						clefLine = 2;
						continue;
					}
				}
				// ---------------------------------------------
				if (src.cols() >= OMRVraible.lineDistance*1.5 && src.rows() >= OMRVraible.lineDistance*2.5) {
					Mat fClef = ImageProcessor.getTemplate("template/f.png", rect.width, rect.height);
					Mat matchResult = ImageProcessor.templatematch(src, fClef, 0.4);
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						Imgproc.rectangle(out, new Point(rect.x + x, rect.y + y),
								new Point(rect.x + x + rect.width, rect.y + y + rect.height), new Scalar(255, 0, 0));
						// f clef
						clefSign = 2;
						clefLine = 4;
						continue;
					}
				}

				if (src.cols() >= OMRVraible.headWidth && src.rows() >= OMRVraible.headHight) {// notes
					Mat matchResult = null;
					if (src.rows() > 60) {
						Mat quarterNote = ImageProcessor.getTemplate("template/quarter.png", OMRVraible.headWidth, OMRVraible.headHight);
						matchResult = ImageProcessor.templatematch(src, quarterNote, 0.55);
						if (Core.minMaxLoc(matchResult).maxVal > 1) {
							// <4 note
							Imgproc.rectangle(out, new Point(rect.x + x, rect.y + y),
									new Point(rect.x + x + rect.width, rect.y + y + rect.height),
									new Scalar(0, 0, 255),2);
							groups.add(new BeamedNote(measureImage, rect, 0, lineLocs, sheet, x, y,partID));

							continue;
						}
						// ---------------------------------------------
						Mat halfNote = ImageProcessor.getTemplate("template/half.png", OMRVraible.headWidth, OMRVraible.headHight);
						matchResult = ImageProcessor.templatematch(src, halfNote, 0.45);
						if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
							// 2note
							Imgproc.rectangle(out, new Point(rect.x + x, rect.y + y),
									new Point(rect.x + x + rect.width, rect.y + y + rect.height),
									new Scalar(0, 255, 0),2);
							groups.add(new BeamedNote(measureImage, rect, 2, lineLocs, sheet, x, y,partID));
							continue;
						}
					}
					// ---------------------------------------------
					Mat wholeNote = ImageProcessor.getTemplate("template/whole.png", OMRVraible.headWidth, OMRVraible.headHight);
					matchResult = ImageProcessor.templatematch(src, wholeNote, 0.4);
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						// 1note
						Imgproc.rectangle(out, new Point(rect.x + x, rect.y + y),
								new Point(rect.x + x + rect.width, rect.y + y + rect.height), new Scalar(0, 0, 255),2);
						groups.add(new BeamedNote(measureImage, rect, 1, lineLocs, sheet, x, y,partID));
						continue;
					}
				}
				// ---------------------------------------------21 58
				if (src.cols() >= OMRVraible.lineDistance/2 && src.rows() >= OMRVraible.lineDistance*2) {// give a min size.
					Mat sharp = ImageProcessor.getTemplate("template/sharp.png", rect.width, rect.height);
					Mat matchResult = ImageProcessor.templatematch(src, sharp, 0.4);
					if (Core.minMaxLoc(matchResult).maxVal > 0.55) {
						Imgproc.rectangle(out, new Point(rect.x + x, rect.y + y),
								new Point(rect.x + x + rect.width, rect.y + y + rect.height), new Scalar(0, 255, 0));
						// sharp

						continue;
					}
					// ---------------------------------------------

					Mat flat = ImageProcessor.getTemplate("template/flat.png", rect.width, rect.height);
					matchResult = ImageProcessor.templatematch(src, flat, 0.15);
					// System.out.println(max);
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						Imgproc.rectangle(out, new Point(rect.x + x, rect.y + y),
								new Point(rect.x + x + rect.width, rect.y + y + rect.height), new Scalar(0, 255, 0));
						// flat

						continue;
					}
					// ---------------------------------------------

					Mat natural = ImageProcessor.getTemplate("template/natural.png", rect.width, rect.height);
					matchResult = ImageProcessor.templatematch(src, natural, 0.25);
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						Imgproc.rectangle(out, new Point(rect.x + x, rect.y + y),
								new Point(rect.x + x + rect.width, rect.y + y + rect.height), new Scalar(0, 255, 0));
						// natural

						continue;
					}
				}
				if (src.cols() >= OMRVraible.lineDistance && src.rows() >= OMRVraible.lineDistance*2.5) {
					Mat qurterRest = ImageProcessor.getTemplate("template/quarterrest.png", rect.width, rect.height);
					Mat matchResult = ImageProcessor.templatematch(src, qurterRest, 0.4);
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						Imgproc.rectangle(out, new Point(rect.x + x, rect.y + y),
								new Point(rect.x + x + rect.width, rect.y + y + rect.height), new Scalar(255, 255, 0));
						// 4rest

						groups.add(new BeamedNote(src, rect, -4, lineLocs, null, 0, 0,partID));
						continue;
					}
				}
				if (src.cols() >= OMRVraible.lineDistance/2 && src.rows() >= OMRVraible.lineDistance) {
					Mat rest8 = ImageProcessor.getTemplate("template/8rest.png", rect.width, rect.height);
					Mat matchResult = ImageProcessor.templatematch(src, rest8, 0.4);
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						Imgproc.rectangle(out, new Point(rect.x + x, rect.y + y),
								new Point(rect.x + x + rect.width, rect.y + y + rect.height), new Scalar(255, 255, 0));
						// 8rest

						groups.add(new BeamedNote(src, rect, -8, lineLocs, null, 0, 0,partID));
						continue;
					}
				}
				if (src.cols() >= OMRVraible.lineDistance/2 && src.rows() >= OMRVraible.lineDistance*2.5) {
					Mat rest16 = ImageProcessor.getTemplate("template/16rest.png", rect.width, rect.height);
					Mat matchResult = ImageProcessor.templatematch(src, rest16, 0.3);
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						Imgproc.rectangle(out, new Point(rect.x + x, rect.y + y),
								new Point(rect.x + x + rect.width, rect.y + y + rect.height), new Scalar(255, 255, 0));
						// 16rest

						groups.add(new BeamedNote(src, rect, -16, lineLocs, null, 0, 0,partID));
						continue;
					}
				}
				if (src.cols() >= OMRVraible.lineDistance*0.7 && src.rows() >= OMRVraible.lineDistance*3) {
					Mat rest32 = ImageProcessor.getTemplate("template/32rest.png", rect.width, rect.height);
					Mat matchResult = ImageProcessor.templatematch(src, rest32, 0.3);
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						Imgproc.rectangle(out, new Point(rect.x + x, rect.y + y),
								new Point(rect.x + x + rect.width, rect.y + y + rect.height), new Scalar(255, 255, 0));
						// 32rest

						groups.add(new BeamedNote(src, rect, -32, lineLocs, null, 0, 0,partID));
						continue;
					}
				}
				if (src.cols() >= OMRVraible.lineDistance*1.2 && src.cols() <= OMRVraible.lineDistance*1.4 && src.rows() >= OMRVraible.lineDistance/2.5 && src.rows() <= OMRVraible.lineDistance*0.6) {

					Mat wholeHalfRest = ImageProcessor.getTemplate("template/wholehalfrest.png", rect.width,
							rect.height);
					Mat matchResult = ImageProcessor.templatematch(src, wholeHalfRest, 0.98);
					if (Core.minMaxLoc(matchResult).maxVal > 0.7) {
						Imgproc.rectangle(out, new Point(rect.x + x, rect.y + y),
								new Point(rect.x + x + rect.width, rect.y + y + rect.height), new Scalar(255, 255, 0));
						// whole rest

						continue;
					}
				}

				if (src.cols() < OMRVraible.lineDistance*0.6 && src.rows() < OMRVraible.lineDistance*0.6) {
					// dot,should attach to the closest note in early group.
					Imgproc.rectangle(out, new Point(rect.x + x, rect.y + y),
							new Point(rect.x + x + rect.width, rect.y + y + rect.height), new Scalar(255, 255, 0), 1);
					if (!groups.isEmpty()) {
						BeamedNote beamedNote = groups.get(groups.size() - 1);// last
																				// group.
						Vector<Vector<Note>> notes = beamedNote.getNotes();// no
																			// loc
																			// info.
						if (beamedNote.isRest && notes.get(0).get(0).headLoc.x - rect.x < 30) {
							notes.get(0).get(0).addDot();
						} else {

							for (int ii = 0; ii < notes.size(); ++ii) {
								while (ii < notes.size() && rect.x - notes.get(ii).get(0).headLoc.x > OMRVraible.lineDistance*1.5) {
									++ii;
								}
								if (ii < notes.size()) {
									for (int xx = notes.get(ii).size() - 1; xx >= 0; --xx) {
										if (Math.abs(notes.get(ii).get(xx).headLoc.y - rect.y) < OMRVraible.lineDistance) {

											notes.get(ii).get(xx).addDot();
											break;
										}
									}
									break;
								}
							}
						}
					}
				}
				// something else, skip
				;

			}
		}

	}

	public Vector<BeamedNote> getNoteGroups() {
		return groups;
	}

	public Mat getImage() {
		return image;
	}

}

class BeamedNote {
	static int partID;
	// display info
	Rect position;
	int direction;
	boolean isRest = false;

	// music info
	Vector<Vector<Note>> notePlaces;

	public static int getPinch(Mat measureMat, Point headLoc, Vector<Integer> lineLoc,Mat sheet,int yLoc) {
		// warning : headLoc is measurewide.

		Vector<Integer> tempLinLoc = new Vector<>();
		for (int i = 4; i > 0; --i) {
			tempLinLoc.add(lineLoc.get(0) - OMRVraible.lineDistance * i);
		}

		for (int i = 0; i < 5; ++i) {
			tempLinLoc.add(lineLoc.get(i));
		}

		for (int i = 1; i < 5; ++i) {
			tempLinLoc.add(lineLoc.get(4) + OMRVraible.lineDistance * i);
		}
		for (int i = 12; i > 0; --i) {
			tempLinLoc.add(i, (tempLinLoc.get(i) + tempLinLoc.get(i - 1)) / 2);
		}
//		for(int i:tempLinLoc){
//			Imgproc.line(sheet, new Point(0, yLoc+i), new Point(sheet.cols(), yLoc+i), new Scalar(255,0,0));
//		}
//		for(int i:lineLoc){
//			Imgproc.line(sheet, new Point(0, yLoc+i), new Point(sheet.cols(), yLoc+i), new Scalar(255,0,0));
//		}

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
		if(partID==1){
			pinch-=12;
		}
		pinch--;
		return pinch;
	}

	public static Vector<Point> getNoteHeadLoc(Mat measureMat, Rect rect, String template) {
		// warning : head loc is group wide.
		// headloc was sorted!!!!
		Vector<Point> noteLoc = new Vector<>();
		if (rect.height >= OMRVraible.headHight && rect.width >= OMRVraible.headWidth) {
			Mat teMat = ImageProcessor.getTemplate(template, OMRVraible.headWidth, OMRVraible.headHight);
			double thresh = 0.4;
			if (template.equals("template/quarter.png")) {
				thresh = 0.55;
			} else if (template.equals("template/half.png")) {
				thresh = 0.45;
			}
			Mat matchResult = ImageProcessor.templatematch(measureMat.submat(rect), teMat, thresh);

			for (int i = 0; i < matchResult.cols(); ++i) {
				for (int j = 0; j < matchResult.rows(); ++j) {
					if (matchResult.get(j, i)[0] > 0.3) {
						noteLoc.add(new Point(i + OMRVraible.headWidth/2, j + OMRVraible.headHight/2));
					}

				}
			}
		}
		noteLoc.sort(new Comparator<Point>() {

			@Override
			public int compare(Point o1, Point o2) {
				if (Math.abs(o1.x - o2.x) < 20) {
					return (int) (o1.y - o2.y);
				} else {
					return (int) (o1.x - o2.x);
				}
			}
		});
		return noteLoc;
	}

	public Vector<Integer> getDuration(Mat groupMat, Vector<Point> headLocs,boolean isUp) {
		// determine the note direction, up or down

		Vector<Integer> durations = new Vector<>();
		//Vector<Integer> stemLocs = ImageProcessor.findPeakY(groupMat, false);
		
		//if (headLocs.get(0).x < stemLocs.get(0)) {
		if(!isUp){
			// down,
			
			for (int x = 0; x < headLocs.size(); ++x) {
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
					if (groupMat.get(i, (int) p.x + OMRVraible.lineDistance/4)[0] < 0.2) {// black
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
						leftcounter = 0;
						leftAdded = false;
						lefthead = false;
						if (!leftinbalck) {
							// continue;
						} else {
							leftinbalck = false;
						}
					}
					// count right.
					int headWidth = OMRVraible.lineDistance;
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
							rightcounter = 0;
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
				if (x - 1 >= 0 && Math.abs(headLocs.get(x).x - headLocs.get(x - 1).x) < OMRVraible.lineDistance) {
					++x;// escape!!!!
				}
			}

		} else {
			// up,
			for (int x = 0; x < headLocs.size(); ++x) {
				while (x + 1 < headLocs.size() && Math.abs(headLocs.get(x).x - headLocs.get(x + 1).x) < OMRVraible.lineDistance) {
					++x;// escape!!!!
				}
				Point p = headLocs.get(x);
				int left = 0, right = 0;
				int leftcounter = 0;
				boolean leftinbalck = false;

				int rightcounter = 0;
				boolean rightinbalck = true;
				boolean righthead = true;
				boolean leftAdded = false;
				boolean rightAdded = false;
				for (int i = (int) p.y; i < groupMat.rows(); ++i) {// top to
																	// bottom

					if (groupMat.get(i, (int) p.x - OMRVraible.lineDistance/4)[0] < 0.2) {// black

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
						rightcounter = 0;
						rightAdded = false;
						righthead = false;
						if (!rightinbalck) {
							// continue;
						} else {// first in white.
							rightinbalck = false;
						}
					}

					int headWidth = OMRVraible.lineDistance;
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
							leftcounter = 0;
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

	public BeamedNote(Mat measureMat, Rect position, int duration, Vector<Integer> lineLoc, Mat sheet, int x, int y,int partID) {
		this.partID = partID;
		// notes in group sort by x.
		if (duration < 0) {
			isRest = true;
		}
		notePlaces = new Vector<>();
		int pinch = 0;
		Vector<Note> noteT;
		// if(duration!=0){
		switch (duration) {
		case 0:// 4 or less.
			Vector<Point> headLoc = getNoteHeadLoc(measureMat, position, "template/quarter.png");
			for (Point p : headLoc) {
				Imgproc.circle(sheet, new Point(p.x + x + position.x, p.y + position.y + y), OMRVraible.lineDistance/2, new Scalar(0, 0, 255),
						5);
			}

			Vector<Vector<Point>> places = new Vector<>();
			boolean isUp = false;//group type, up or down.
			boolean flag = true;
			for (int i = 0; i < headLoc.size(); ++i) {
				if(flag&&headLoc.get(i).y<OMRVraible.lineDistance){
					isUp = true;
					flag = false;
				}
				if(flag&&position.height-headLoc.get(i).y<OMRVraible.lineDistance){
					isUp = false;
					flag = false;
				}
				Vector<Point> place = new Vector<>();
				place.add(headLoc.get(i));
				while (i + 1 < headLoc.size() && Math.abs(headLoc.get(i).x - headLoc.get(i + 1).x) < OMRVraible.headWidth) {
					place.add(headLoc.get(i + 1));
					++i;
				}
				places.add(place);
			}

			Vector<Integer> durations = getDuration(measureMat.submat(position), headLoc,isUp);

			for (int i = 0; i < places.size(); ++i) {
				Vector<Note> notes = new Vector<>();

				for (int j = 0; j < places.get(i).size(); ++j) {
					pinch = getPinch(measureMat,
							new Point(places.get(i).get(j).x + position.x, places.get(i).get(j).y + position.y),
							lineLoc,sheet,y);
					notes.add(new Note(durations.get(i), 0, pinch,
							new Point(places.get(i).get(j).x + position.x, places.get(i).get(j).y + position.y)));

				}

				notePlaces.add(notes);
			}
			break;
		case 1:

			Vector<Point> headLocWhole = getNoteHeadLoc(measureMat, position, "template/whole.png");
			for (Point p : headLocWhole) {
				Imgproc.circle(sheet, new Point(p.x + x + position.x, p.y + position.y + y), OMRVraible.lineDistance/2, new Scalar(0, 255, 0),
						5);
			}
			noteT = new Vector<>();
			for (Point p : headLocWhole) {
				pinch = getPinch(measureMat, new Point(p.x + position.x, p.y + position.y), lineLoc,sheet,y);
				noteT.add(new Note(1, 0, pinch, new Point(p.x + position.x, p.y + position.y)));
			}
			notePlaces.add(noteT);
			break;
		case 2:

			Vector<Point> headLochalf = getNoteHeadLoc(measureMat, position, "template/half.png");

			noteT = new Vector<>();
			for (Point p : headLochalf) {
				Imgproc.circle(sheet, new Point(p.x + x + position.x, p.y + position.y + y), OMRVraible.lineDistance/2, new Scalar(0, 255,0 ),
						5);
				pinch = getPinch(measureMat, new Point(p.x + position.x, p.y + position.y), lineLoc,sheet,y);
				noteT.add(new Note(2, 0, pinch, new Point(p.x + position.x, p.y + position.y)));
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
			noteT.add(new Note(2, 0, 0, position.br()));
			notePlaces.add(noteT);
			break;
		case -4:
			noteT = new Vector<>();
			noteT.add(new Note(4, 0, 0, position.br()));
			notePlaces.add(noteT);
			break;
		case -8:
			noteT = new Vector<>();
			noteT.add(new Note(8, 0, 0, position.br()));
			notePlaces.add(noteT);
			break;
		case -16:
			noteT = new Vector<>();
			noteT.add(new Note(16, 0, 0, position.br()));
			notePlaces.add(noteT);
			break;
		case -32:
			noteT = new Vector<>();
			noteT.add(new Note(32, 0, 0, position.br()));
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

	public Note(int duration, int pinchOctave, int pinchStep, Point headLoc) {
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

	public void addDot() {
		dot++;
	}

	public int getDot() {
		return dot;
	}

	public void setDot(int dot) {
		this.dot = dot;
	}

}