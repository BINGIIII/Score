package UI;

import java.io.File;

public class GlobleVariable {
	static int partNum;
	static String lilypondPath;
	static String scoreDir;
	static String xmlPath;
	static String outDir;
	static String midiPath;

	public static String getLilypondPath() {
		return lilypondPath;
	}

	public static void setLilypondPath(String lilypondPath) {
		GlobleVariable.lilypondPath = lilypondPath;
	}

	public static String getScoreDir() {
		return scoreDir;
	}

	public static void setScoreDir(String scoreDir) {
		GlobleVariable.scoreDir = scoreDir;
	}

	public static String getXmlPath() {
		return xmlPath;
	}

	public static void setXmlPath(String xmlPath) {
		GlobleVariable.xmlPath = xmlPath;
	}

	public static String getOutDir() {
		return outDir;
	}

	public static void setOutDir(String outDir) {
		GlobleVariable.outDir = outDir;
	}

	public static String getMidiPath() {
		return midiPath;
	}

	public static void setMidiPath(String midiPath) {
		GlobleVariable.midiPath = midiPath;
	}

	public static int getPartNum() {
		return partNum;
	}

	public static void setPartNum(int partNum) {
		GlobleVariable.partNum = partNum;
	}
}
