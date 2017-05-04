package OMR;

import java.util.Vector;

public class Score {
	Vector<Part> parts;

	public Score(Vector<Part> parts) {
		super();
		this.parts = parts;
	}
}

class Part{
	Vector<Measure> measures;

	public Part(Vector<Measure> measures) {
		super();
		this.measures = measures;
	}
}
class Measure{
	int keyMode;
	int keyFifth;
	int beats;
	int beatType;
	int clefSign;
	int clefLine;
	Vector<Note> notes;
	public Measure(int keyMode, int keyFifth, int beats, int beatType, int clefSign, int clefLine, Vector<Note> notes) {
		super();
		this.keyMode = keyMode;
		this.keyFifth = keyFifth;
		this.beats = beats;
		this.beatType = beatType;
		this.clefSign = clefSign;
		this.clefLine = clefLine;
		this.notes = notes;
	}
}
class Note{
	int duration;
	int pinchOctave;
	int pinchStep;//1 to 7
	public Note(int duration, int pinchOctave, int pinchStep) {
		super();
		this.duration = duration;
		this.pinchOctave = pinchOctave;
		this.pinchStep = pinchStep;
	}
}