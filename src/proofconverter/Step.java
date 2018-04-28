package proofconverter;

import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

public class Step {
	private int lineNum;
	private Sentence sentence;
	private String[] premises;
	private String rule;
	private int indent;
	
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
			return "";
		} else {
			return premises[i];
		}
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
	
	
}