package proofconverter;

import java.util.List;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import java.util.List;
import java.util.ArrayList;

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
	
	public SequentStep(Step s, List<Step> steps) {
		super(s);
		if(this.rule.equals("ASSUMPTION")) {
			this.sequent = new ArrayList<Sentence>();
			sequent.add(this.sentence);
		} else {
			this.sequent = new ArrayList<Sentence>();
			for(int i = 0; i < this.premises.length; i++) {
				this.sequent.add(steps.get(Integer.parseInt(this.premises[i]) - 1).getSentence());
			}
		}
	}
	
	public List<Sentence> getSequent() {
		return sequent;
	}
}
