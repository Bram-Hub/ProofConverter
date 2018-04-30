package proofconverter;

import java.util.List;
import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

public class Proof {
	private int id;
	private Sentence goal;
	private List<Step> steps;
	private String type;
	private int startLine;
	private int endLine;
	
	public Proof() {
		this.id = -1;
		this.goal = null;
		this.steps = null;
		this.startLine = -1;
		this.endLine = -1;
	}
	
	//Creating a Proof from an xml element, used for parsing purposes.
	public Proof(Element proof, String t) {
		this.type = t;
		this.goal = new Sentence(proof.getElementsByTagName("goal").item(0).getTextContent());
		this.steps = new ArrayList<Step>();
		this.id = Integer.parseInt(proof.getAttribute("id"));
		this.startLine = Integer.MAX_VALUE;
		this.endLine = 0;
		
		if(this.type.equals("Fitch")) {
			NodeList assumptionsList = proof.getElementsByTagName("assumption");
			for(int i = 0; i < assumptionsList.getLength(); i++) {
				Node assumptionNode = assumptionsList.item(i);
				if(assumptionNode.getNodeType() == Node.ELEMENT_NODE) {
					Step assumption = new Step((Element) assumptionNode);
					this.steps.add(assumption);
					if(assumption.getLineNum() < this.startLine) {
						this.startLine = assumption.getLineNum();
					}
					if(assumption.getLineNum() > this.endLine) {
						this.endLine = assumption.getLineNum();
					}
				}
			}
		
			NodeList stepList = proof.getElementsByTagName("step");
			for(int i = 0; i < stepList.getLength(); i++) {
				Node stepNode = stepList.item(i);
				if(stepNode.getNodeType() == Node.ELEMENT_NODE) {
					Step step = new Step((Element) stepNode);
					this.steps.add(step);
					if(step.getLineNum() < this.startLine) {
						this.startLine = step.getLineNum();
					}
					if(step.getLineNum() > this.endLine) {
						this.endLine = step.getLineNum();
					}
				}
			}
		} else if(this.type.equals("Sequent")) { 
			NodeList assumptionsList = proof.getElementsByTagName("assumption");
			for(int i = 0; i < assumptionsList.getLength(); i++) {
				Node assumptionNode = assumptionsList.item(i);
				if(assumptionNode.getNodeType() == Node.ELEMENT_NODE) {
					Step assumption = new SequentStep((Element) assumptionNode);
					this.steps.add(assumption);
				}
			}
			
			NodeList stepList = proof.getElementsByTagName("step");
			for(int i = 0; i < stepList.getLength(); i++) {
				Node stepNode = stepList.item(i);
				if(stepNode.getNodeType() == Node.ELEMENT_NODE) {
					Step step = new SequentStep((Element) stepNode);
					this.steps.add(step);
				}
			}
		}
	}
	
	public Proof(int newID, List<Step> s, String t, Sentence g) {
		this.id = newID;
		this.goal = g;
		this.steps = s;
		this.type = t;
		this.startLine = Integer.MAX_VALUE;
		this.endLine = 0;
		for(int i = 0; i < this.steps.size(); i ++) {
			if(this.steps.get(i).getLineNum() < this.startLine) {
				this.startLine = this.steps.get(i).getLineNum();
			}
			if(this.steps.get(i).getLineNum() > this.endLine) {
				this.endLine = this.steps.get(i).getLineNum();
			}
		}
	}
	
	public int getID() {
		return id;
	}
	
	public Sentence getGoal() {
		return this.goal;
	}
	
	public Step getStep(int i) {
		return this.steps.get(i);
	}
	
	public List<Step> getSteps() {
		return this.steps;
	}
	
	public Step getStepByLineNumber(int l) {
		for(int i = 0; i < this.steps.size(); i++) {
			if(this.steps.get(i).getLineNum() == l) {
				return this.steps.get(i);
			}
		}
		return null;
	}
	
	public int getNumSteps() {
		return this.steps.size();
	}
	
	public String getType() {
		return this.type;
	}
	
	public int getStartLine() {
		return this.startLine;
	}
	
	public int getEndLine() {
		return this.endLine;
	}
}