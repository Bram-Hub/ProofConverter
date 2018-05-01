package proofconverter;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class Sequent {
	
	private static String[] subProofRules = {"NEGATION_INTRODUCTION", "OR_ELIMINATION", "CONDITIONAL_INTRODUCTION", "BICONDITIONAL_INTRODUCTION"};
	private static Proof seqProof;
	private static Map<Integer, Proof> proofs;
	
	public static String parse(NodeList proofList) {
		
		for(int i = 0; i < proofList.getLength(); i++) {
			Node proofNode = proofList.item(i);
			
			if(proofNode.getNodeType() == Node.ELEMENT_NODE) {
				seqProof = new Proof((Element) proofNode, "Sequent");
			}
		}
		
		return PrintProof(seqProof, 0);
	}
	
	public static String PrintProof(Proof p, int indent) {
		String output = "";
		int proofLength = p.getNumSteps();
		
		for(int i = 0; i < proofLength; i++) {
			SequentStep s = (SequentStep) p.getStep(i);

			String lineNum = s.getLineNum() + ". { ";
			String sequentStr = "";
			List<Sentence> sequent = s.getSequent();
			for(int j = 0; j < sequent.size(); j++) {
				if(j != sequent.size() - 1) {
					sequentStr += (sequent.get(j)).printSentence() + ", ";
				}
				else {
					sequentStr += (sequent.get(j)).printSentence() + " } \u22a8 ";
				}
			}
			String sen = s.getSentence().printSentence();
			String rule = s.getRule();

			String premises = "";
			for(int k = 0; k < s.getNumPremises(); k++) {
				premises += " " + s.getPremise(k);
			}
			output += String.format("%-6s%-40s%-30s\r\n", lineNum, sequentStr + sen, rule + premises);
		}
		
		System.out.println("Proof ID = " + p.getID());
		System.out.println("-----------------------------------------------------------");
		System.out.println(output);
		
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
	


	public static Document convert(NodeList proofList, Document outputDoc) {
		proofs = new HashMap<Integer, Proof>();
		
		for(int i = 0; i < proofList.getLength(); i++) {
			Node proofNode = proofList.item(i);
			
			if(proofNode.getNodeType() == Node.ELEMENT_NODE) {
				seqProof = new Proof((Element) proofNode, "Sequent");
			}
		}
		
		convertProof();
		
		
		Element rootElement = outputDoc.createElement("bram");
		
		Element program = outputDoc.createElement("Program");
		program.appendChild(outputDoc.createTextNode("Fitch"));
		
		Element version = outputDoc.createElement("Version");
		version.appendChild(outputDoc.createTextNode("1.0"));
		
		Element metadata = outputDoc.createElement("metadata");
		metadata.appendChild(outputDoc.createTextNode(" "));
		
		rootElement.appendChild(program);
		rootElement.appendChild(version);
		rootElement.appendChild(metadata);
		
		Iterator<Integer> itr = proofs.keySet().iterator();
		while(itr.hasNext()) {
			int proofId = itr.next();
			Element proof = outputDoc.createElement("proof");
			proof.setAttribute("id", Integer.toString(proofId));
			
			Proof newProof = proofs.get(proofId);
			for(int i = 0; i < newProof.getNumSteps(); i++) {
				Step s = newProof.getStep(i);
				if(s.getRule().equals("ASSUMPTION")) {
					Element assumption = outputDoc.createElement("assumption");
					assumption.setAttribute("linenum", Integer.toString(s.getLineNum()));
					
					Element goal = outputDoc.createElement("goal");
					Element goalSen = outputDoc.createElement("sen");
					goalSen.appendChild(outputDoc.createTextNode(newProof.getGoal().printSentencePrefix()));
					goal.appendChild(goalSen);
					proof.appendChild(goal);
					
					Element sen = outputDoc.createElement("sen");
					sen.appendChild(outputDoc.createTextNode(s.getSentence().printSentencePrefix()));
					
					assumption.appendChild(sen);
					
					proof.appendChild(assumption);
				} else {
					Element step = outputDoc.createElement("step");
					step.setAttribute("linenum", Integer.toString(s.getLineNum()));
					
					Element sen = outputDoc.createElement("sen");
					sen.appendChild(outputDoc.createTextNode(s.getSentence().printSentencePrefix()));
					
					step.appendChild(sen);
					
					for(int j = 0; j < s.getNumPremises(); j++) {
						Element premise = outputDoc.createElement("premise");
						premise.appendChild(outputDoc.createTextNode(s.getPremise(j)));
						step.appendChild(premise);
					}
					
					Element rule = outputDoc.createElement("rule");
					rule.appendChild(outputDoc.createTextNode(s.getRule()));
					
					step.appendChild(rule);
					
					proof.appendChild(step);
				}
			}
			
			rootElement.appendChild(proof);
		}
		
		
		
		outputDoc.appendChild(rootElement);
		
		return outputDoc;
	}
	
	public static int constructSubProofs(int proofId, List<Step> seqSteps, int step, String rule) {
		List<Step> subproof = new ArrayList<Step>();
		for(int i = step; i >= 0; i--) {
			Step s = seqSteps.get(i);
			System.out.println(s.getSentence().printSentence() + " " + s.getRule());
			subproof.add(s);
			
			if((s.getRule()).equals("ASSUMPTION")) {
				System.out.println("Here");
				Collections.sort(subproof, new StepComparer());
				Proof p = new Proof(proofId, subproof, "Sequent", subproof.get(subproof.size()-1).getSentence());
				proofs.put(proofId, p);
				if(proofId == 1) {
					continue;
				}
				else {
					return i;
				}
			}
			
			//if step(s) is a subProof rule
			for(int j = 0; j < subProofRules.length; j++) {
				if((s.getRule()).equals(subProofRules[j])) {
//					if((s.getRule()).equals("OR_ELIMINATION")) {
//						i = constructSubProofs(proofs, proofId + 1, seqSteps, i-1, "OR_ELIMINATION");
//						break;
//					}
//					else if((s.getRule()).equals("BICONDITIONAL_INTRODUCTION")) {
//						i = constructSubProofs(proofs, proofId + 1, seqSteps, i-1, "BICONDITIONAL_INTRODUCTION");
//						break;
//					}
//					else {
						String[] premises = new String[1];
						premises[0] = Integer.toString(proofId + 1);
						s.setPremise(premises);
						subproof.remove(subproof.size()-1);
						subproof.add(s);
						i = constructSubProofs(proofId + 1, seqSteps, i-1, "other");
						break;
					//}
				}
			}
			
			
			
		}
		
		return 0;
	}
	
	public static void convertProof() {

		List<Step> seqSteps = new ArrayList<Step>(seqProof.getSteps());
		Collections.sort(seqSteps, new StepComparer());
		constructSubProofs(1, seqSteps, seqSteps.size()-1, "none");

	}
}