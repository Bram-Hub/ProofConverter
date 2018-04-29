package proofconverter;

import java.util.List;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

public class SequentStep extends Step {
	private Sentence[] sequent;
	
	public SequentStep(Element step) {
		super(step);
		NodeList sequentList = step.getElementsByTagName("ant");
		sequent = new Sentence[sequentList.getLength()];
		for(int i = 0; i < sequentList.getLength(); i++) {
			sequent[i] = new Sentence(sequentList.item(i).getTextContent());
		}
	}
	
	public SequentStep(Step s, List<Step> steps) {
		super(s);
		if(this.rule.equals("ASSUMPTION")) {
			this.sequent = new Sentence[1];
			sequent[0] = this.sentence;
		} else {
			this.sequent = new Sentence[this.premises.length];
			for(int i = 0; i < this.premises.length; i++) {
				this.sequent[i] = steps.get(Integer.parseInt(this.premises[i]) - 1).getSentence();
			}
		}
	}
	
	public Sentence[] getSequent() {
		return sequent;
	}
}
