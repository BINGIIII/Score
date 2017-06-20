package logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Compiler {
	public static void jianpu2ly(String jName, String lyName) {
		try {
			ProcessBuilder builder = new ProcessBuilder(
		            "cmd.exe", "/c", "python jianly.py <"+jName+"> "+lyName);
		        builder.redirectErrorStream(true);
		        Process p = builder.start();
		        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		        String line;
		        while (true) {
		            line = r.readLine();
		            if (line == null) { break; }
		            System.out.println(line);
		        }
			//System.out.println("python jianly.py <"+jName+"> "+lyName);
			////Process p = Runtime.getRuntime().exec("python jianly.py <"+jName+"> "+lyName);
			//String command = "python /c start python path\to\script\script.py";
			//Process p = Runtime.getRuntime().exec(command + param );
			/*BufferedReader stdInput = new BufferedReader(new 
				     InputStreamReader(p.getInputStream()));

				BufferedReader stdError = new BufferedReader(new 
				     InputStreamReader(p.getErrorStream()));

				// read the output from the command
				System.out.println("Here is the standard output of the command:\n");
				String s = null;
				while ((s = stdInput.readLine()) != null) {
				    System.out.println(s);
				}

				// read any errors from the attempted command
				System.out.println("Here is the standard error of the command (if any):\n");
				while ((s = stdError.readLine()) != null) {
				    System.out.println(s);
				}*/
            
            // read any errors from the attempted command
           
		} catch (IOException e) {
			System.out.println("exception happened - here's what I know: ");
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
