package OMR;

import java.util.Vector;

public class JianScore {
	Vector<JPart> parts;
}

class JPart{
	Vector<JMeasure> measures;
}

class JMeasure{
	int keyMode;
	int keyFifth;
	int beats;
	int beatType;	
	Vector<JNote> notes;
}

class JNote{
	int duration;
	int pinchOctave;
	int pinchStep;// 1 to 7
	int dot;
	int accidentals; 
}
