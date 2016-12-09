package spelling;

public class SearchResult implements Comparable<SearchResult> {
	Double score;
	String fileName;
	
	public SearchResult(String fileName, Double score) {
		this.fileName = fileName;
		this.score = score;
	}

	@Override
	public int compareTo(SearchResult other) {
		SearchResult otherResult = (SearchResult) other;
		return Double.compare(otherResult.score, score);
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	
}
