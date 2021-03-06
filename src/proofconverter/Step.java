package proofconverter;

import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

public class Step {
	protected int lineNum;
	protected Sentence sentence;
	protected String[] premises;
	protected String rule;
	protected int indent;
	
	public Step() {
		sentence = null;
		rule = "";
		premises = new String[1];
		premises[0] = "";
		indent = 0;
	}
	
	public Step(Element step) {
		indent = 0;
		lineNum = Integer.parseInt(step.getAttribute("linenum"));
		sentence = new Sentence(step.getElementsByTagName("sen").item(0).getTextContent());
		if(!(step.getElementsByTagName("rule").item(0) == null)) {
			rule = step.getElementsByTagName("rule").item(0).getTextContent();
		} else {
			rule = "ASSUMPTION";
		}
		
		NodeList premiseList = step.getElementsByTagName("premise");
		premises = new String[premiseList.getLength()];
		for(int i = 0; i < premiseList.getLength(); i++) {
			premises[i] = premiseList.item(i).getTextContent();
		}
	}
	
	public Step(Step s) {
		this.lineNum = s.getLineNum();
		this.sentence = s.getSentence();
		this.rule = s.getRule();
		this.premises = new String[s.getNumPremises()];
		for(int i = 0; i < premises.length; i++) {
			this.premises[i] = s.getPremise(i);
		}
	}
	
	public int getLineNum() {
		return lineNum;
	}
	
	public Sentence getSentence() {
		return sentence;
	}
	
	public String getRule() {
		return rule;
	}
	
	public String getPremise(int i) {
		if(i < 0 || i >= premises.length) {
			return " ";
		} else {
			return premises[i];
		}
	}
	
	public void setPremise(String[] p) {
		this.premises = new String[p.length];
		for(int i = 0; i < p.length; i++) {
			this.premises[i] = p[i];
		}
	}
	
	public void setPremise(int i, String s) {
		this.premises[i] = s;
	}
	
	public int getNumPremises() {
		return premises.length;
	}
	
	public int getIndent() {
		return this.indent;
	}
	
	public void setIndent(int i) {
		this.indent = i;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(!(obj instanceof Step)) {
			return false;
		}
		Step s = (Step) obj;
		if(this.sentence.equals(s.sentence) && this.rule.equals(s.rule) && this.lineNum == s.lineNum) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
	    int hash = 3;
	    hash = 53 * hash + this.lineNum;
	    hash = 53 * hash + (this.sentence != null ? this.sentence.hashCode() : 0);
	    hash = 53 * hash + (this.rule != null ? this.rule.hashCode() : 0);
	    return hash;
	}
	
	
}