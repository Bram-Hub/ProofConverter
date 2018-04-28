package proofconverter;

import java.util.List;
import java.util.ArrayList;

public class Sentence {
	private String operator;
	private List<Sentence> sentences;
	private Sentence singleSentence;
	private String type;
	
	public Sentence() {
		operator = "";
		sentences = null;
		type = "";
	}
	
	public Sentence(String s) {
		sentences = new ArrayList<Sentence>();
		//If the first character is (, we must parse each of the sentences within the sentence. Otherwise, it is just an atomic sentence
		if(s.charAt(0) == ('(')) {
			s = s.substring(1, s.length() - 1);
			this.operator = s.substring(0, s.indexOf(' '));
			s = s.substring(s.indexOf(' ') + 1, s.length());
			
			if(this.operator.equals("¬")) {
				sentences = null;
				singleSentence = new Sentence(s);
			} else {
				for(int i = 0; i < s.length(); i++) {
					if(s.charAt(i) == '(') {
						sentences.add(new Sentence(s.substring(i, s.indexOf(')') + 1)));
						s = s.substring(s.indexOf(')') + 1, s.length());
						i = -1;
					} else if(s.charAt(i) != ' ') {
						if(s.indexOf(' ') == -1) {
							sentences.add(new Sentence(s.substring(i, s.length())));
						} else {
							sentences.add(new Sentence(s.substring(i, s.indexOf(" "))));
							s = s.substring(s.indexOf(' ') + 1, s.length());
							i = -1;
						}
					}
				}
			}
		} else {
			operator = "";
			sentences = null;
			singleSentence = new AtomicSentence(s);
		}
		
		//Determine the type of sentence based on the operator
		if(operator.equals("∧")) {
			type = "Conjunction";
		} else if(operator.equals("∨")) {
			type = "Disjunction";
		} else if(operator.equals("¬")) {
			type = "Negation";
		} else if(operator.equals("→")) {
			type = "Conditional";
		} else if(operator.equals("↔")) {
			type = "Biconditional";
		} else if(operator.equals("")) {
			type = "Atomic";
		}
	}
	
	public String printSentence() {
		if(type == "Atomic") {
			return singleSentence.printSentence();
		} else if(type == "Negation") {
			if(singleSentence.getType().equals("Atomic") || singleSentence.getType().equals("Negation")) {
				return operator + singleSentence.printSentence();
			} else {
				return operator + "(" + singleSentence.printSentence() + ")";
			}
		} else {
			String output = "";
			for(int i = 0; i < sentences.size() - 1; i++) {
				output += sentences.get(i).printSentence() + " " + operator;
			}
			output +=  " " + sentences.get(sentences.size() - 1).printSentence();
			return output;
		}
	}
	
	public String getType() {
		return type;
	}
}