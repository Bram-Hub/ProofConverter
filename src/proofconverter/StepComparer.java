package proofconverter;

import java.util.Comparator;

public class StepComparer implements Comparator<Step> {
	@Override
	public int compare(Step s1, Step s2) {
		return s1.getLineNum() > s2.getLineNum() ? 1 : 
				s1.getLineNum() < s2.getLineNum() ? -1 :
				0;
	}
}