package spelling;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVPrinter;

public class Query {
	Indexer indexer;
	String query;
	Double score;
	ArrayList<String> words;
	StopWords stopper;
	PorterStemmer stemmer;
	ArrayList<SearchResult> results;
	
	public Query(Indexer indexer, String query) {
		this.indexer = indexer;
		this.query = query;
		this.score = 0.0;
		stopper = new StopWords();
		stemmer = new PorterStemmer();
		results = new ArrayList<SearchResult>();
		getWords();
		calculateScores();
		Collections.sort(results);
	}
	
	public void getWords() {
		words = new ArrayList<String>();
		String[] wordsList = query.split("\\W");
		for (String word : wordsList) {
			word = word.replace(" ", "").toLowerCase();
			if (stopper.contains(word)) {
				continue;
			}
			word = stemmer.stem(word);
			if (word.equals("Invalid term") || word.equals("No term entered")) {
				continue;
			}
			words.add(word);
		}
	}
	
	public Double TF(String word,String fileName) {
//		System.out.println("wordFreq: "+indexer.getWordFreq(word, fileName));
//		System.out.println("Max freq: "+indexer.getMaxFrequencies().get(fileName));
		return indexer.getWordFreq(word, fileName) / indexer.getMaxFrequencies().get(fileName);
	}
	
	public Double IDF(String word) {
		Integer numAppear = indexer.getNumAppear(word);
		if (numAppear>0) {
			Double ratio = indexer.getNumDocs()/numAppear;
			return Math.log(ratio)/Math.log(2);
		} else {
			return 0.0;
		}
	}

	
	public void calculateScores() {
		ArrayList<File> files = indexer.getFiles();
		for (File file : files) {
//			System.out.println("Scoring file: "+file.getName());
			score = 0.0;
			for (String word : words) {
//				System.out.println("query word: "+word);
				score += TF(word,file.getName()) * IDF(word);
			}
//			System.out.println("Score: "+score);
			results.add(new SearchResult(file.getAbsolutePath(),score));
		}
	}
	
	
	public void printResults(CSVPrinter printer) throws IOException {
		printer.print("Query: "+query+"\n");
		printer.println();
		printer.print("Ranked Documents");
		printer.print("Document ID");
		printer.print("First Sentence");
		printer.print("Ranking Score");
		printer.println();
		Pattern parenPattern = Pattern.compile("\\(([^)]+)\\)");
		int numResults = 0;
		if (results.size()>10) {
			numResults = 10;
		} else {
			numResults = results.size();
		}
		
		for (int i=0; i<numResults; i++) {
			String fileName = results.get(i).getFileName();
			Matcher parenMatcher = parenPattern.matcher(fileName);
			String docID = "";
			if (parenMatcher.find()) {
				docID = parenMatcher.group(1);
			} else {
				docID = fileName;
			}
			printer.print(i+1);
			printer.print(docID);
			printer.print(indexer.firstSentences.get(fileName));
			printer.print(results.get(i).getScore());
			printer.println();
			printer.println();
		}
	}

	public ArrayList<SearchResult> getResults() {
		return results;
	}

	public void setResults(ArrayList<SearchResult> results) {
		this.results = results;
	}

	
}
