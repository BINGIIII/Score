package OMR;

public class OMRVraible {
	public static int lineDistance;
	public static int headHight;
	public static int headWidth;
	public static int getLineDistance() {
		return lineDistance;
	}

	public static void setLineDistance(int lineDistance) {
		OMRVraible.lineDistance = lineDistance;
		headHight = lineDistance+1;
		headWidth = (int) (headHight*1.19);
	}
}
