package logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class Lilyond {
	static String lilyponddir = "C:\\Program Files (x86)\\LilyPond\\usr\\bin";
	
	public static void generate(String inputfile){
		try {
			Process process = Runtime.getRuntime().exec('\"'+lilyponddir+"\\lilypond\" --png -dresolution=400 "+inputfile);
			BufferedReader reader =
					new BufferedReader(new InputStreamReader(process.getErrorStream()));
					while ((reader.readLine()) != null) {}
					process.waitFor();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
