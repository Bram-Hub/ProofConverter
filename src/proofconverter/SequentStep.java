package proofconverter;

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
}
