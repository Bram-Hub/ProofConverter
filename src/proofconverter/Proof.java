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
	
	public Proof(Element proof, String t) {
		this.type = t;
		this.steps = new ArrayList<Step>();
		this.id = Integer.parseInt(proof.getAttribute("id"));
		this.startLine = Integer.MAX_VALUE;
		this.endLine = 0;
		
		if(this.type == "Fitch") {
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
		}
		else { //this.type == "Sequent"
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
	
	public int getID() {
		return id;
	}
	
	public Sentence getGoal() {
		return this.goal;
	}
	
	public Step getStep(int i) {
		return this.steps.get(i);
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