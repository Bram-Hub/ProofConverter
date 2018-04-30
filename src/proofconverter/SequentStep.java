package proofconverter;

import java.util.List;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class SequentStep extends Step {
	private List<Sentence> sequent;
	
	public SequentStep(Element step) {
		super(step);
		NodeList sequentList = step.getElementsByTagName("ant");
		sequent = new ArrayList<Sentence>();
		for(int i = 0; i < sequentList.getLength(); i++) {
			sequent.add(new Sentence(sequentList.item(i).getTextContent()));
		}
	}
	
	public SequentStep(Step s, List<Step> steps, Map<Integer, Proof> proofs) {
		super(s);
		if(this.rule.equals("ASSUMPTION")) {
			this.sequent = new ArrayList<Sentence>();
			sequent.add(this.sentence);
		} else {
			this.sequent = new ArrayList<Sentence>();
			if(this.rule.equals("CONDITIONAL_INTRODUCTION")) {
				int endLineNum = proofs.get(Integer.parseInt(this.premises[0])).getEndLine();
				this.premises[0] = Integer.toString(endLineNum);
				List<Sentence> premiseSequents = ((SequentStep)steps.get(endLineNum - 1)).getSequent();
				for(int i = 0; i < premiseSequents.size(); i++) {
					if(this.sentence.getPrecedent() == null || !this.sentence.getPrecedent().equals(premiseSequents.get(i))) {
						if(!this.sequent.contains(premiseSequents.get(i))) {
							this.sequent.add(premiseSequents.get(i));
						}
					}
				}
			} else if(this.rule.equals("NEGATION_INTRODUCTION")) {
				int endLineNum = proofs.get(Integer.parseInt(this.premises[0])).getEndLine();
				this.premises[0] = Integer.toString(endLineNum);
				List<Sentence> premiseSequents = ((SequentStep)steps.get(endLineNum - 1)).getSequent();
				for(int i = 0; i < premiseSequents.size(); i++) {
					if(!this.sentence.getSingleSentence().equals(premiseSequents.get(i))) {
						if(!this.sequent.contains(premiseSequents.get(i))) {
							this.sequent.add(premiseSequents.get(i));
						}
					}
				}
			} else if(this.rule.equals("DISJUNCTION_ELIMINATION")) {
				int endLineNum1 = proofs.get(Integer.parseInt(this.premises[1])).getEndLine();
				int endLineNum2 = proofs.get(Integer.parseInt(this.premises[2])).getEndLine();
				this.premises[1] = Integer.toString(endLineNum1);
				this.premises[2] = Integer.toString(endLineNum2);
				List<Sentence> premiseSequents = new ArrayList<Sentence>();
				premiseSequents.addAll(((SequentStep)steps.get(Integer.parseInt(this.premises[0]) - 1)).getSequent());
				premiseSequents.addAll(((SequentStep)steps.get(endLineNum1 - 1)).getSequent());
				premiseSequents.addAll(((SequentStep)steps.get(endLineNum2 - 1)).getSequent());
				for(int i = 0; i < premiseSequents.size(); i++) {
					if(!steps.get(Integer.parseInt(this.premises[0]) - 1).getSentence().checkSentences(premiseSequents.get(i))) {
						if(!this.sequent.contains(premiseSequents.get(i))) {
							this.sequent.add(premiseSequents.get(i));
						}
					}
				}
			} else {
				for(int i = 0; i < this.premises.length; i++) {
					List<Sentence> premiseSequents = ((SequentStep) steps.get(Integer.parseInt(this.premises[i]) - 1)).getSequent();
					for(int j = 0; j < premiseSequents.size(); j++) {
						if(!this.sequent.contains(premiseSequents.get(j))) {
							this.sequent.add(premiseSequents.get(j));
						}
					}
				}
			}
		}
	}
	
	public List<Sentence> getSequent() {
		return sequent;
	}
}
