package logic;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XML2lyCompiler{
	private String currentTag;
    char currentStep;
    int currentOctave;
    int currentDuration;

    int currentDot = 0;
    boolean octaveTag = true;

    int currentBeats;
    int currentBeatType;

    PrintWriter out;
    
    public static void main(String[] args) {
		new XML2lyCompiler().parser("a.xml", "a.ly");
		Lilyond.generate("a.ly");
	}
   
    public void parser(String filename,String outName){
    	System.setProperty("http.agent", "Mozilla/5.0 (X11; Linux x86_64; rv:47.0) Gecko/20100101 Firefox/47.0");
        DefaultHandler handler = new DefaultHandler() {
        	@Override
            //this is a while
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                super.startElement(uri, localName, qName, attributes);
                //set currentTag
                currentTag = localName;
                switch (localName) {
                    case "score-partwise":
                        out.print("{");
                        break;
                    case "part":
                        break;
                    case "measure":
                        break;
                    case "attributes":
                        break;
                    case "division":
                        break;
                    case "key":
                        break;
                    case "fifths":
                        break;
                    case "mode":
                        break;
                    case "time":
                        break;
                    case "beats":
                        break;
                    case "beat-type":
                        break;
                    case "clef":
                        break;
                    case "sign":
                        break;
                    case "line":
                        break;
                    case "note":
                        break;
                    case "pinch":
                        break;
                    case "step":
                        break;
                    case "alter":
                        break;
                    case "octave":
                        break;
                    case "duration":

                        break;
                    case "type":
                        break;
                    case "dot":
                        currentDot++;
                        break;
                    case "rest":
                        currentStep = 'r';
                        break;
                }
            }
        	
            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                super.characters(ch, start, length);
                String str = new String(ch, start, length);
                if (null != currentTag) {
                    switch (currentTag) {
                        case "score-partwise":
                            break;
                        case "part":
                            break;
                        case "measure":
                            break;
                        case "attributes":
                            break;
                        case "division":
                            break;
                        case "key":
                            break;
                        case "fifths":
                            break;
                        case "mode":
                            break;
                        case "time":
                            break;
                        case "beats":
                            currentBeats = Integer.parseInt(str);
                            break;
                        case "beat-type":
                            currentBeatType = Integer.parseInt(str);
                            break;
                        case "clef":
                            break;
                        case "sign":
                            break;
                        case "line":
                            break;
                        case "note":
                            break;
                        case "pinch":
                            break;
                        case "step":
                            currentStep = str.trim().toLowerCase().charAt(0);
                            break;
                        case "alter":
                            break;
                        case "octave":
                            currentOctave = Integer.parseInt(str);
                            break;
                        case "duration":

                            break;
                        case "type":
                            //out.println(str);
                            switch (str) {
                                case "whole":
                                    currentDuration = 1;
                                    break;
                                case "half":
                                    currentDuration = 2;
                                    break;
                                case "quarter":
                                    currentDuration = 4;
                                    break;
                                case "eighth":
                                    currentDuration = 8;
                                    break;
                                case "16th":
                                    currentDuration = 16;
                                    break;
                                case "32th":
                                    currentDuration = 32;
                                    break;
                                case "64th":
                                    currentDuration = 64;
                                    break;
                                case "128th":
                                    currentDuration = 128;
                                    break;
                                case "256th":
                                    currentDuration = 256;
                                    break;
                                case "512th":
                                    currentDuration = 512;
                                    break;
                                case "1024th":
                                    currentDuration = 1024;
                                    break;
                            }
                            break;
                    }
                }
            }

            
            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                super.endElement(uri, localName, qName);
                switch (localName) {
                    case "score-partwise":
                        out.print("}");
                        break;
                    case "part":
                        break;
                    case "measure":
                        break;
                    case "attributes":
                        break;
                    case "division":
                        break;
                    case "key":
                        break;
                    case "fifths":
                        break;
                    case "mode":
                        break;
                    case "time"://time 结束输出一个 time
                        out.print("\\time "+currentBeats+"/"+currentBeatType);
                        break;
                    case "beats":
                        break;
                    case "beat-type":
                        break;
                    case "clef"://clef结束输出一个clef
                        break;
                    case "sign":
                        break;
                    case "line":
                        break;
                    case "note"://note结束输出一个note
                        out.print(currentStep);
                        int sub = currentOctave - 3;
                        if(currentStep=='r'){
                            sub = 0;
                        }
                        if(sub>0){
                            for(int i=0;i<sub;++i){
                                out.print("'");
                            }
                        }else{
                            for(int i=0;i<-sub;++i){
                                out.print(",");
                            }
                        }
                        out.print(currentDuration);
                        for(int i=0;i<currentDot;++i){
                            out.print(".");
                        }
                        out.print(" ");
                        currentDot = 0;
                        break;
                    case "pinch":
                        break;
                    case "step":
                        break;
                    case "alter":
                        break;
                    case "octave":
                        break;
                    case "duration":

                        break;
                    case "type":
                        break;
                }
            }

        };
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        SAXParser parser;
		try {
			parser = factory.newSAXParser();
			File f = new File(filename);
	        out = new PrintWriter(new FileWriter(outName));
	        parser.parse(f, handler);      
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			out.close();
		}
        
    }
}
