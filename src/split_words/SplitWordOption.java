package split_words;

public class SplitWordOption implements Comparable {

	String words = "";
	int index = 0;
	Double score = 0.0;
	int numWords = 0;
	Double lmScore = 0.0;
	
	public SplitWordOption(String words, int index, Double lmScore) {
		this.words = words;
		this.index = index;
		this.numWords = words.split(" ").length;
		this.lmScore = lmScore;
		calculateScore();
	}
	
	public void calculateScore() {
		score = (double) index - 100*numWords + 10*lmScore;
//		score = (double) index - numWords;
		System.out.println("Words: "+words+"\tLM Score: "+lmScore+"\tScore: "+score);
	}
	
	public int compareTo(Object obj) {
		SplitWordOption otherSplitWordOption = (SplitWordOption) obj;
		return Double.compare(otherSplitWordOption.score, score);
	}
	
}
