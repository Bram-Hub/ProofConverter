package proofconverter;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.util.List;
import java.util.ArrayList;

public class Fitch {
	
	public static String parse(NodeList proofList) {
		String output = "";
		
		for(int i = 0; i < proofList.getLength(); i++) {
			Node proofNode = proofList.item(i);
			
			if(proofNode.getNodeType() == Node.ELEMENT_NODE) {
				Element proof = (Element) proofNode;
				
				//Parse all assumptions first
				NodeList assumptions = proof.getElementsByTagName("assumption");
				for(int j = 0; j < assumptions.getLength(); j++) {
					Node assumptionNode = assumptions.item(j);
					
					if(assumptionNode.getNodeType() == Node.ELEMENT_NODE) {
						Element assumption = (Element) assumptionNode;
						
						String line = assumption.getAttribute("linenum");
						String sentence = assumption.getElementsByTagName("sen").item(0).getTextContent();
						
						output += String.format("%-30s %s", line + ". " + parseSentence(sentence), "Assumption\r\n");
					}
				}
				
				//Parse every step of the proof
				NodeList steps = proof.getElementsByTagName("step");
				for(int j = 0; j < steps.getLength(); j++) {
					Node stepNode = steps.item(j);
					
					if(stepNode.getNodeType() == Node.ELEMENT_NODE) {
						Element step = (Element) stepNode;
						
						String line = step.getAttribute("linenum");
						String sentence = step.getElementsByTagName("sen").item(0).getTextContent();
						String rule = step.getElementsByTagName("rule").item(0).getTextContent();
						rule.replaceAll("//_", " ");
						
						NodeList premiseList = step.getElementsByTagName("premise");
						String premises = "";
						for(int k = 0; k < premiseList.getLength(); k++) {
							premises += premiseList.item(k).getTextContent() + " ";
						}
						
						output += String.format("%-30s %s", line + ". " + parseSentence(sentence), rule + " " + premises + "\r\n");
					}
				}
			}
		}
		
		return output;
	}
	
	public static String parseSentence(String sentence) {
		String output = "";
		
		//If the first character is (, we must parse each of the sentences within the sentence. Otherwise, it is just an atomic sentence
		if(sentence.charAt(0) == ('(')) {
			sentence = sentence.substring(1, sentence.length() - 1);
			String operator = sentence.substring(0, sentence.indexOf(' '));
			sentence = sentence.substring(sentence.indexOf(' ') + 1, sentence.length()) + " ";
			
			//Negation operator is handled differently than other operators
			if(operator.equals("¬")) {
				output += operator + parseSentence(sentence);
			} else {
				List<String> sentenceParts = new ArrayList<String>();
				
				int lastSpace = -1;
				for(int i = 0; i < sentence.length(); i++) {
					if(sentence.charAt(i) == ' ') {
						sentenceParts.add(sentence.substring(lastSpace + 1, i));
						lastSpace = i;
					} else if(sentence.charAt(i) == '(') {
						String subSentence = sentence.substring(i, i + sentence.substring(i, sentence.length()).indexOf(')') + 1);
						sentenceParts.add("(" + parseSentence(subSentence) + ")");
						i = i + subSentence.indexOf(')') + 1;
					}
				}
				
				for(int i = 0; i < sentenceParts.size() - 1; i++) {
					output += sentenceParts.get(i) + operator;
				}
				output += sentenceParts.get(sentenceParts.size() - 1);
			}
		} else {
			output += sentence;
		}
		
		return output;
	}
	
}