package proofconverter;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class Sequent {
	
	private static List<String> subProofRules = Arrays.asList("NEGATION_INTRODUCTION", "DISJUNCTION_ELIMINATION", "CONDITIONAL_INTRODUCTION", "BICONDITIONAL_INTRODUCTION");
	private static Proof seqProof;
	private static Map<Integer, Proof> proofs;
	private static List<Step> steps = new ArrayList<Step>();
	private static List<Step> fitchSteps = new ArrayList<Step>();
	
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
			
			Element goal = outputDoc.createElement("goal");
			Element goalSen = outputDoc.createElement("sen");
			goalSen.appendChild(outputDoc.createTextNode(newProof.getGoal().printSentencePrefix()));
			goal.appendChild(goalSen);
			proof.appendChild(goal);
			
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
	
	public static String[] createSubProof(int proofID, List<Step> oldSteps, int currStep, List<Sentence> initialAssumptions) {
		SequentStep initialStep = (SequentStep)oldSteps.get(currStep - 1);
		List<Step> newSteps = new ArrayList<Step>();
		String[] premises = null;
		if(initialStep.getRule().equals("CONDITIONAL_INTRODUCTION") || initialStep.getRule().equals("NEGATION_INTRODUCTION")) {
			List<Sentence> sequents = ((SequentStep)oldSteps.get(oldSteps.size() - Integer.parseInt(initialStep.getPremise(0)))).getSequent();
			Sentence seq = null;
			premises = new String[1];
			
			for(int i = 0; i < sequents.size(); i++) {
				if(!initialAssumptions.contains(sequents.get(i))) {
					seq = sequents.get(i);
				}
			}
			
			for(int i = currStep; i < oldSteps.size(); i++) {
				SequentStep step = (SequentStep)steps.get(i);
				if(step.getSequent().contains(seq)) {
					Step newStep = new Step(step);
					if(!fitchSteps.contains(newStep)) {
						if(subProofRules.contains(step.getRule())) {
							if(newStep.getRule().equals("CONDITIONAL_INTRODUCTION") || newStep.getRule().equals("NEGATION_INTRODUCTION")) {
								String[] newPremises = createSubProof(proofID, steps, i + 1, initialAssumptions);
								newStep.setPremise(newPremises);
							}
							proofID--;
						} 
						newSteps.add(newStep);
						fitchSteps.add(newStep);
					}
				}
			}
			
			Proof p = new Proof(proofID, newSteps, "Fitch", initialStep.getSentence());
			proofs.put(Integer.valueOf(p.getID()), p);
			premises[0] = Integer.toString(p.getID());
		} else if(initialStep.getRule().equals("BICONDITIONAL_INTRODUCTION")) {
			//Create two subproofs, one for premise 0 and one for premise 1
			premises = new String[2];
			for(int i = 0; i < 2; i++) {
				List<Sentence> sequents = ((SequentStep)oldSteps.get(oldSteps.size() - Integer.parseInt(initialStep.getPremise(i)))).getSequent();
				Sentence seq = null;
				
				for(int j = 0; j < sequents.size(); j++) {
					if(!initialAssumptions.contains(sequents.get(j))) {
						seq = sequents.get(j);
					}
				}
				
				for(int j = currStep; j < oldSteps.size(); j++) {
					SequentStep step = (SequentStep)steps.get(j);
					if(step.getSequent().contains(seq)) {
						Step newStep = new Step(step);
						if(!fitchSteps.contains(newStep)) {
							if(subProofRules.contains(step.getRule())) {
								if(newStep.getRule().equals("CONDITIONAL_INTRODUCTION") || newStep.getRule().equals("NEGATION_INTRODUCTION")) {
									String[] newPremises = createSubProof(proofID, steps, j + 1, initialAssumptions);
									newStep.setPremise(newPremises);
								}
								proofID--;
							} 
							newSteps.add(newStep);
							fitchSteps.add(newStep);
						}
					}
				}
				Proof p = new Proof(proofID, newSteps, "Fitch", initialStep.getSentence());
				proofs.put(Integer.valueOf(p.getID()), p);
				newSteps.clear();
				premises[i] = Integer.toString(p.getID());
				proofID--;
			}
		} else if(initialStep.getRule().equals("DISJUNCTION_ELIMINATION")) {
			premises = new String[3];
			premises[0] = initialStep.getPremise(0);
			for(int i = 1; i < 3; i++) {
				System.out.println(oldSteps.size() + " " + initialStep.getPremise(i));
				List<Sentence> sequents = ((SequentStep)oldSteps.get(oldSteps.size() - Integer.parseInt(initialStep.getPremise(i)))).getSequent();
				Sentence seq = null;
				
				for(int j = 0; j < sequents.size(); j++) {
					if(!initialAssumptions.contains(sequents.get(j))) {
						seq = sequents.get(j);
					}
				}
				
				for(int j = currStep; j < oldSteps.size(); j++) {
					SequentStep step = (SequentStep)steps.get(j);
					if(step.getSequent().contains(seq)) {
						Step newStep = new Step(step);
						if(!fitchSteps.contains(newStep)) {
							if(subProofRules.contains(step.getRule())) {
								if(newStep.getRule().equals("CONDITIONAL_INTRODUCTION") || newStep.getRule().equals("NEGATION_INTRODUCTION")) {
									String[] newPremises = createSubProof(proofID, steps, j + 1, initialAssumptions);
									newStep.setPremise(newPremises);
								}
								proofID--;
							} 
							newSteps.add(newStep);
							fitchSteps.add(newStep);
						}
					}
				}
				Proof p = new Proof(proofID, newSteps, "Fitch", initialStep.getSentence());
				proofs.put(Integer.valueOf(p.getID()), p);
				newSteps.clear();
				premises[i] = Integer.toString(p.getID());
				proofID--;
			}
		}
		
		return premises;
	}
	
	public static void convertProof() {
		List<Step> newSteps = new ArrayList<Step>();
		int numProofs = 1;
		
		for(int i = 0; i < seqProof.getNumSteps(); i++) {
			if(seqProof.getStep(i).getRule().equals("CONDITIONAL_INTRODUCTION") || seqProof.getStep(i).getRule().equals("NEGATION_INTRODUCTION")) {
				numProofs++;
			} else if(seqProof.getStep(i).getRule().equals("DISJUNCTION_ELIMINATION") || seqProof.getStep(i).getRule().equals("BICONDITIONAL_INTRODUCTION")) {
				numProofs += 2;
			}
			steps.add(seqProof.getStep(i));
		}
		Collections.sort(steps, new StepComparer());
		Collections.reverse(steps);
		
		List<Sentence> initialAssumptions = ((SequentStep)steps.get(steps.size() - 1)).getSequent();
		
		for(int i = 0; i < steps.size(); i++) {
			SequentStep step = (SequentStep)steps.get(i);
			Step newStep = new Step(step);
			if(!fitchSteps.contains(newStep)) {
				if(subProofRules.contains(step.getRule())) {
					String[] newPremises = createSubProof(numProofs - proofs.size(), steps, i + 1, initialAssumptions);
					newStep.setPremise(newPremises);
				} 
				newSteps.add(newStep);
				fitchSteps.add(newStep);
			}
		}
		Proof p = new Proof(1, newSteps, "Fitch", seqProof.getGoal());
		proofs.put(1, p);
		
//		
//		List<Step> seqSteps = new ArrayList<Step>(seqProof.getSteps());
//		Iterator<Step> stepItr = seqSteps.iterator();
//		Collections.sort(seqSteps, new StepComparer());
//		constructSubProofs(1, seqSteps, seqSteps.size()-1, "none");

	}
}