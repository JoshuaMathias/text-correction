package spelling;

public class WordData {

	public int correctionCount=0;
	public int frequency=0;
	
	public WordData() {
		
	}
	
	public void addCorrectionCount() {
		correctionCount++;
	}
	
	public void addFreq() {
		frequency++;
	}

}
