package proofconverter;

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
	
	public List<Sentence> getSequent() {
		return sequent;
	}
}
