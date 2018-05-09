package proofconverter;

import java.util.Comparator;

public class ProofComparer implements Comparator<Proof> {
	@Override
	public int compare(Proof p1, Proof p2) {
		return p1.getID() > p2.getID() ? 1 : 
				p1.getID() < p2.getID() ? -1 :
				0;
	}
}