package UI;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import OMR.Score;
import logic.Lilyond;

public class MIDIPanel extends JPanel {
	String filename;
	Sequencer sequencer;
	public void setFile(String filename){
			this.filename = filename;
	}
	public MIDIPanel() {
		setBackground(Color.GRAY);
		setLayout(new GridLayout(0, 1, 0, 0));
		JButton play = new JButton("Play");
		play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new SwingWorker<Integer, Integer>() {

					@Override
					protected Integer doInBackground() throws Exception {
						InputStream is = new BufferedInputStream(new FileInputStream(new File(filename)));
						sequencer = MidiSystem.getSequencer();
						sequencer.open();
						sequencer.setSequence(is);
						sequencer.start();
						return 0;
					}
					@Override
					protected void done() {
						
					}
					
				}.execute();
			}
		});
		add(play);

		JButton pause = new JButton("Pause");
		add(pause);

		JButton stop = new JButton("Stop");
		stop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				sequencer.close();
			}
		});
		add(stop);


	}

}
