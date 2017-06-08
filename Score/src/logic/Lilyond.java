package logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class Lilyond {
	static String lilyponddir = "C:\\Program Files (x86)\\LilyPond\\usr\\bin";

	public static void generate(String inputfile) {
		try {
			File dir = new File("result");
			for (String fName : dir.list()) {
				new File("result/" + fName).delete();
			}
			Process process = Runtime.getRuntime()
					.exec('\"' + lilyponddir + "\\lilypond\" --png -dresolution=400 -o result " + inputfile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			while ((reader.readLine()) != null) {
			}
			BufferedReader reader2 = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((reader2.readLine()) != null) {
			}
			process.waitFor();
			reader.close();
			reader2.close();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
