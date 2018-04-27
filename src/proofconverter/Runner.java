package proofconverter;

import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class Runner {
	
	public static void main(String[] args) throws IOException {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			File input = null;
			
			if(args.length == 0) {
				System.err.println("ERROR: No arguments given. Use the help argument if you are unsure how to use this program.");
				return;
			}
			if(args.length == 2) {
				String command = args[0];
				String fileName = args[1];
				if(fileName.indexOf('.') == -1) {
					System.err.println("ERROR: Argument 2 is not a valid file. Please input a .bram file to use this program.");
					return;
				} else {
					String[] fileNameParts = fileName.split("\\.");
					if(!fileNameParts[1].equals("bram")) {
						System.err.println("ERROR: Argument 2 is not a .bram file. Please input a .bram file ot use this program.");
						return;
					}
				}
				if(command.equals("parse")) {
					input = new File(fileName);
					PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
					Document bram = dBuilder.parse(input);
					bram.getDocumentElement().normalize();
					String proofType = bram.getElementsByTagName("Program").item(0).getTextContent();
					NodeList proofList = bram.getElementsByTagName("proof");
					
					if(proofType != null) {
						if(proofType.equals("Fitch")) {
							String output = Fitch.parse(proofList);
							writer.println(output);
						} else if (proofType.equals("Sequent")) {
							String output = Sequent.parse(proofList);
							writer.println(output);
						} else {
							System.out.println("ERROR: " + proofType + " is not a verified Program tag value for this program. Exiting.");
							writer.close();
							return;
						}
					} else {
						System.out.println("ERROR: No Program tag was specified in the input file. Exiting.");
						writer.close();
						return;
					}
					
					writer.close();
				} else if(command.equals("convert")) {
					System.err.println("ERROR: Too few arguments given for command convert. Use the help argument if you are unsure how to use this program.");
					return;
				} else {
					System.err.println("ERROR: Unknown command " + command + ". Use the help argument if you are unsure how to use this program.");
					return;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}