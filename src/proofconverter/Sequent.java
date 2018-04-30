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
	private static Map<Integer, Proof> proofs;
	
	public static String parse(NodeList proofList) {
		proofs = new HashMap<Integer, Proof>();
		
		for(int i = 0; i < proofList.getLength(); i++) {
			Node proofNode = proofList.item(i);
			
			if(proofNode.getNodeType() == Node.ELEMENT_NODE) {
				Proof p = new Proof((Element) proofNode, "Sequent");
				proofs.put(p.getID(), p);
			}
		}
		
		return PrintProof(proofs.get(1), 0);
	}
	
	public static String PrintProof(Proof p, int indent) {
		String output = "";
		int proofLength = p.getNumSteps();
		
		for(int i = 0; i < proofLength; i++) {
			SequentStep s = (SequentStep) p.getStep(i);
//			for(int j = 0; j < subProofRules.length; j++) {
//				if(s.getRule().equals(subProofRules[j])) {
//					output += PrintProof(proofs.get(Integer.valueOf(s.getPremise(0))), indent + 1);
//				}
//			}
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
			//output += s.getSentence().printSentence() + "\t\t" + s.getRule() + "\r\n";
			output += String.format("%-6s%-40s%-30s\r\n", lineNum, sequentStr + sen, rule);
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
				Proof p = new Proof((Element) proofNode, "Sequent");
				proofs.put(p.getID(), p);
			}
		}
		
		Proof newProof = convertProof();
		
		Element rootElement = outputDoc.createElement("bram");
		
		Element program = outputDoc.createElement("Program");
		program.appendChild(outputDoc.createTextNode("Fitch"));
		
		Element version = outputDoc.createElement("Version");
		version.appendChild(outputDoc.createTextNode("1.0"));
		
		Element metadata = outputDoc.createElement("metadata");
		metadata.appendChild(outputDoc.createTextNode(" "));
		
		Element proof = outputDoc.createElement("proof");
		proof.setAttribute("id", "1");
		
		for(int i = 0; i < newProof.getNumSteps(); i++) {
			Step s = newProof.getStep(i);
			if(s.getRule().equals("ASSUMPTION")) {
				Element assumption = outputDoc.createElement("assumption");
				assumption.setAttribute("linenum", Integer.toString(s.getLineNum()));
				
				Element sequent = outputDoc.createElement("sequent");
				List<Sentence> seq = ((SequentStep) s).getSequent();
				
				for(int j = 0; j < seq.size(); j++) {
					Element ant = outputDoc.createElement("ant");
					ant.appendChild(outputDoc.createTextNode(seq.get(j).printSentencePrefix()));
					sequent.appendChild(ant);
				}
				
				Element sen = outputDoc.createElement("sen");
				sen.appendChild(outputDoc.createTextNode(s.getSentence().printSentencePrefix()));
				
				assumption.appendChild(sequent);
				assumption.appendChild(sen);
				
				proof.appendChild(assumption);
			} else {
				Element step = outputDoc.createElement("step");
				step.setAttribute("linenum", Integer.toString(s.getLineNum()));
				
				Element sequent = outputDoc.createElement("sequent");
				List<Sentence> seq = ((SequentStep) s).getSequent();
				
				for(int j = 0; j < seq.size(); j++) {
					Element ant = outputDoc.createElement("ant");
					ant.appendChild(outputDoc.createTextNode(seq.get(j).printSentencePrefix()));
					sequent.appendChild(ant);
				}
				
				Element sen = outputDoc.createElement("sen");
				sen.appendChild(outputDoc.createTextNode(s.getSentence().printSentencePrefix()));
				
				step.appendChild(sequent);
				step.appendChild(sen);
				
				for(int j = 0; j < s.getNumPremises(); j++) {
					Element premise = outputDoc.createElement("premise");
					premise.appendChild(outputDoc.createTextNode(s.getPremise(i)));
					step.appendChild(premise);
				}
				
				Element rule = outputDoc.createElement("rule");
				rule.appendChild(outputDoc.createTextNode(s.getRule()));
				
				step.appendChild(rule);
				
				proof.appendChild(step);
			}
		}
		
		rootElement.appendChild(program);
		rootElement.appendChild(version);
		rootElement.appendChild(metadata);
		rootElement.appendChild(proof);
		
		outputDoc.appendChild(rootElement);
		
		return outputDoc;
	}
	
	public static int constructSubProofs(List<List<Step>> proofs, int proofId, List<Step> seqSteps, int step, String rule) {
		List<Step> subproof = new ArrayList<Step>();
		for(int i = step; i >= 0; i++) {
			Step s = seqSteps.get(step);
			subproof.add(s);
			
			if((s.getRule()).equals("ASSUMPTION")) {
				
				proofs.set(proofId, subproof);
				return step - 1;
			}
			
			//if step(s) is a subProof rule
			for(int j = 0; j < subProofRules.length; j++) {
				if((s.getRule()).equals(subProofRules[j])) {
					if((s.getRule()).equals("OR_ELIMINATION")) {
						i = constructSubProofs(proofs, proofId + 1, seqSteps, i-1, "OR_ELIMINATION");
						break;
					}
					else if((s.getRule()).equals("BICONDITIONAL_INTRODUCTION")) {
						i = constructSubProofs(proofs, proofId + 1, seqSteps, i-1, "BICONDITIONAL_INTRODUCTION");
						break;
					}
					else {
						i = constructSubProofs(proofs, proofId + 1, seqSteps, i-1, "other");
					}
				}
			}
			
			
			
		}
		
		return 0;
	}
	
	public static Proof convertProof() {
		Proof newProof = null;
//		Iterator<Integer> iter = proofs.keySet().iterator();
//		List<Step> newSteps = new ArrayList<Step>();
//		
//		while(iter.hasNext()) {
//			Proof p = proofs.get(iter.next());
//			List<Step> seqSteps = new ArrayList<Step>(p.getSteps());
//			Collections.sort(seqSteps, new StepComparer());
//			List<List<Step>> proofs = new ArrayList<List<Step>>();
//			constructSubProofs(proofs, 0, seqSteps, seqSteps.size()-1, "none");
//
//		}
//		Collections.sort(steps, new StepComparer());
//		
//		for(int i = 0; i < steps.size(); i++) {
//			Step step = steps.get(i);
//			SequentStep newStep = new SequentStep(step, newSteps, proofs);
//			newSteps.add(newStep);
//		}
//		
//		newProof = new Proof(1, newSteps, "Sequent");
//		
		return newProof;
	}
}