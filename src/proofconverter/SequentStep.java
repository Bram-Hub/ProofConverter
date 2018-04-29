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
				List<Sentence> premiseSequents = ((SequentStep)steps.get(endLineNum - 1)).getSequent();
				for(int i = 0; i < premiseSequents.size(); i++) {
					System.out.println("Precedent: " + this.sentence.getPrecedent().printSentence() + " Sequent: " + premiseSequents.get(i).printSentence());
					if(this.sentence.getPrecedent() == null || !this.sentence.getPrecedent().equals(premiseSequents.get(i))) {
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
