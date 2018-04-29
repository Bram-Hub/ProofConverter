package proofconverter;

public class AtomicSentence extends Sentence {
	private String sentence;
	
	public AtomicSentence() {
		sentence = "";
	}
	
	public AtomicSentence(String s) {
		sentence = s;
	}
	
	public String printSentence() {
		return sentence;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(!(obj instanceof AtomicSentence)) {
			return false;
		}
		AtomicSentence as = (AtomicSentence) obj;
		if(!this.sentence.equals(as.sentence)) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		int hash = 3;
		hash = 53 * hash + (this.sentence != null ? this.sentence.hashCode() : 0);
		return hash;
	}
}