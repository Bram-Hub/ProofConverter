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
						int j = i + 1, parentheses = 1;
						while(parentheses != 0) {
							if(s.charAt(j) == '(') {
								parentheses++;
							} else if(s.charAt(j) == ')') {
								parentheses--;
							}
							j++;
						}
						sentences.add(new Sentence(s.substring(i, j)));
						if(j != s.length()) {
							s = s.substring(j + 1, s.length());
							i = -1;
						} else {
							break;
						}
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
		if(type.equals("Atomic")) {
			return singleSentence.printSentence();
		} else if(type.equals("Negation")) {
			if(singleSentence.getType().equals("Atomic") || singleSentence.getType().equals("Negation")) {
				return operator + singleSentence.printSentence();
			} else {
				return operator + "(" + singleSentence.printSentence() + ")";
			}
		} else {
			String output = "";
			for(int i = 0; i < sentences.size() - 1; i++) {
				if(sentences.get(i).getType().equals("Atomic")) {
					output += sentences.get(i).printSentence() + " " + operator;
				} else {
					output += "(" + sentences.get(i).printSentence() + ")" + " " + operator;
				}
			}
			if(sentences.get(sentences.size() - 1).getType().equals("Atomic")) {
				output += " " + sentences.get(sentences.size() - 1).printSentence();
			} else {
				output += " (" + sentences.get(sentences.size() - 1).printSentence() + ")";
			}
			return output;
		}
	}
	
	public String printSentencePrefix() {
		if(type.equals("Atomic")) {
			return singleSentence.printSentence();
		} else if(type.equals("Negation")) {
			return "(" + operator + singleSentence.printSentencePrefix() + ")";
		} else {
			String output = "(" + operator;
			for(int i = 0; i < sentences.size(); i++) {
				output += " " + sentences.get(i).printSentencePrefix();
			}
			output += ")";
			return output;
		}
	}
	
	public String getType() {
		return type;
	}
	
	public Sentence getSingleSentence() {
		return singleSentence;
	}
	
	public Sentence getPrecedent() {
		if(this.type.equals("Conditional")) {
			return sentences.get(0);
		} else {
			return null;
		}
	}
	
	public boolean checkSentences(Sentence s) {
		return this.sentences.contains(s);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(!(obj instanceof Sentence)) {
			return false;
		}
		Sentence s = (Sentence) obj;
		if(this.printSentence().equals(s.printSentence())) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
	    int hash = 3;
	    hash = 53 * hash + (this.operator != null ? this.operator.hashCode() : 0);
	    hash = 53 * hash + (this.sentences != null ? this.sentences.hashCode() : 0);
	    hash = 53 * hash + (this.singleSentence != null ? this.singleSentence.hashCode() : 0);
	    return hash;
	}
}