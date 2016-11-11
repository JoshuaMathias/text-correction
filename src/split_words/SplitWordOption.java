package split_words;

public class SplitWordOption implements Comparable {

	String words = "";
	int index = 0;
	Double score = 0.0;
	int numWords = 0;
	
	public SplitWordOption(String words, int index) {
		this.words = words;
		this.index = index;
		this.numWords = words.split(" ").length;
		calculateScore();
	}
	
	public void calculateScore() {
		score = (double) index - numWords;
	}
	
	public int compareTo(Object obj) {
		SplitWordOption otherSplitWordOption = (SplitWordOption) obj;
		return Double.compare(otherSplitWordOption.score, score);
	}
	
}
