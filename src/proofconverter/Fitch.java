package proofconverter;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

public class Fitch {
	
	private static String[] subProofRules = {"NEGATION_INTRODUCTION"};
	private static Map<Integer, Proof> proofs;
	private static List<Step> steps = new ArrayList<Step>();
	
	public static String parse(NodeList proofList) {
		proofs = new HashMap<Integer, Proof>();
		
		for(int i = 0; i < proofList.getLength(); i++) {
			Node proofNode = proofList.item(i);
			
			if(proofNode.getNodeType() == Node.ELEMENT_NODE) {
				Proof p = new Proof((Element) proofNode, "Fitch");
				proofs.put(p.getID(), p);
			}
		}
		
		setIndents(proofs.get(1), 0);
		Collections.sort(steps, new StepComparer());
		
		String output = "";
		for(int i = 0; i < steps.size(); i++) {
			String lineNum = steps.get(i).getLineNum() + ". ";
			String indent = "";
			for(int j = 0; j < steps.get(i).getIndent(); j++) {
				indent += "| ";
			}
			String sentence = steps.get(i).getSentence().printSentence();
			String rule = steps.get(i).getRule();
			output += String.format("%-6s%-32s%-30s\r\n", lineNum, indent + sentence, rule);
		}
		
		System.out.println(output);
		
		return output;
	}
	
	public static void setIndents(Proof p, int indent) {
		int proofLength = p.getNumSteps();
		
		for(int i = 0; i < proofLength; i++) {
			Step s = p.getStep(i);
			s.setIndent(indent);
			for(int j = 0; j < subProofRules.length; j++) {
				if(s.getRule().equals(subProofRules[j])) {
					setIndents(proofs.get(Integer.valueOf(s.getPremise(0))), indent + 1);
				}
			}
			steps.add(s);
		}
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