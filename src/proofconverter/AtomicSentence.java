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
}