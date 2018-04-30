package proofconverter;

import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Runner {
	
	public static void main(String[] args) throws IOException {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			File input = null;
			
			if(args.length == 0) {
				System.err.println("ERROR: No arguments given. Use the help argument if you are unsure how to use this program.");
				return;
			} else if(args.length == 2) {
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
			} else if(args.length == 3) {
				String command = args[0];
				String inputFile = args[1];
				String outputFile = args[2];
				
				if(inputFile.indexOf('.') == -1) {
					System.err.println("ERROR: Argument 2 is not a valid file. Please input a .bram file to use this program.");
					return;
				} else {
					String[] fileNameParts = inputFile.split("\\.");
					if(!fileNameParts[1].equals("bram")) {
						System.err.println("ERROR: Argument 2 is not a .bram file. Please input a .bram file ot use this program.");
						return;
					}
				}
				
				if(command.equals("convert")) {
					input = new File(inputFile);
					Document inputDoc = dBuilder.parse(input);
					Document outputDoc = dBuilder.newDocument();
					inputDoc.getDocumentElement().normalize();
					String proofType = inputDoc.getElementsByTagName("Program").item(0).getTextContent();
					NodeList proofList = inputDoc.getElementsByTagName("proof");
					
					if(proofType != null) {
						if(proofType.equals("Fitch")) {
							outputDoc = Fitch.convert(proofList, outputDoc);
						} else if (proofType.equals("Sequent")) {
							outputDoc = Sequent.convert(proofList, outputDoc);
						} else {
							System.out.println("ERROR: " + proofType + " is not a verified Program tag value for this program. Exiting.");
							return;
						}
					} else {
						System.out.println("ERROR: No Program tag was specified in the input file. Exiting.");
						return;
					}
					
					TransformerFactory transformerFactory = TransformerFactory.newInstance();
					Transformer transformer = transformerFactory.newTransformer();
					transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
					transformer.setOutputProperty(OutputKeys.INDENT, "yes");
					transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
					DOMSource source = new DOMSource(outputDoc);
					StreamResult result = new StreamResult(new File(outputFile));
					
					transformer.transform(source, result);
				} else if(command.equals("parse")) {
					System.err.println("ERROR: Too many arguments given for parse convert. Use the help argument if you are unsure how to use this program.");
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