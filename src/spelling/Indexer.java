package spelling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Array;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.BreakIterator;
import java.util.*;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVPrinter;

import shared.FileUtils;

public class Indexer {

	HashMap<String, HashMap<String, Double>> index;
	StopWords stopper;
	PorterStemmer stemmer;
	HashMap<String, Double> scores;
	ArrayList<File> files;
	Double numDocs;
	HashMap<String, Double> maxFrequencies;
	HashMap<String, String> firstSentences;
	HashMap<String, Integer> frequencies;
	HashMap<String, Integer> bigrams;
	HashMap<String, Integer> both;
	ArrayList<Integer> vocabSizes;
	int wordCount = 0;

	public Indexer(String path) {
		vocabSizes = new ArrayList<Integer>();
		frequencies = new HashMap<String, Integer>();
		bigrams = new HashMap<String, Integer>();
		both = new HashMap<String, Integer>();
		index = new HashMap<String, HashMap<String, Double>>();
		stopper = new StopWords();
		stemmer = new PorterStemmer();
		maxFrequencies = new HashMap<String, Double>();
		firstSentences = new HashMap<String, String>();
		File dir = new File(path);
//		files = dir.listFiles();
		files = FileUtils.listFiles(dir);
		numDocs = (double) files.size();
		for (File file : files) {
			FileInputStream inStream = null;
			String fileName = null;
			try {
				inStream = new FileInputStream(file);
				fileName = file.getName();
			} catch (FileNotFoundException e) {
				System.out.println("File " + fileName + " not found");
			}
			String fileString = readStream(inStream);
			ArrayList<String> sentences = getSentences(fileString);
			for (String sentence : sentences) {
//				System.out.println(sentence);
				ArrayList<String> words = new ArrayList<String>();
				String[] wordsList = sentence.split("\\W");
				String firstWord = "";
				String secondWord = "";
				for (String word : wordsList) {
					secondWord = word;
					word = word.replace(" ", "").toLowerCase();
					if (stopper.contains(word)) {
						continue;
					}
					wordCount++;
					word = stemmer.stem(word);
					if (word.equals("Invalid term")
							|| word.equals("No term entered")) {
						continue;
					}
					// Find word in index. If there's a map of frequencies, add to
					// it. If not, make a new map.
					HashMap<String, Double> frequencyDocs;

					if (!index.containsKey(word)) {
						frequencyDocs = new HashMap<String, Double>();
					} else {
						frequencyDocs = index.get(word);
					}
					HashMap<String, Double> docFreq = null;
					Double frequency = 0.0;
					if (!frequencyDocs.containsKey(fileName)) {
						frequency = 1.0;
					} else {
						frequency = frequencyDocs.get(fileName) + 1;

					}
					frequencyDocs.put(fileName, frequency);
					if (!maxFrequencies.containsKey(fileName)
							|| maxFrequencies.containsKey(fileName)
							&& frequency > maxFrequencies.get(fileName)) {
						maxFrequencies.put(fileName, frequency);
					}
					index.put(word, frequencyDocs);
					frequencies = incrementOne(frequencies, word);
					if (firstWord != "" && secondWord != "") {
						String bigram = firstWord + " " + secondWord;
						bigrams = incrementOne(bigrams, bigram);
					}
					firstWord = secondWord;
				}
				firstSentences.put(fileName, getFirstSentence(fileString));
				if (!maxFrequencies.containsKey(fileName)) {
					maxFrequencies.put(fileName, 0.0);
				}
			}
			vocabSizes.add(frequencies.size());
		}

		for (File file : files) {
			if (!maxFrequencies.containsKey(file.getName())) {
				System.out.println("No max for " + file.getName());
			}
			// System.out.println(key+": "+maxFrequencies.get(key));
		}
	}
	
	public static <K, V extends Comparable<? super V>> Map<K, V> 
    sortByValue( Map<K, V> map )
{
    List<Map.Entry<K, V>> list =
        new LinkedList<>( map.entrySet() );
    Collections.sort( list, new Comparator<Map.Entry<K, V>>()
    {
        @Override
        public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
        {
            return ( o1.getValue() ).compareTo( o2.getValue() );
        }
    } );

    Map<K, V> result = new LinkedHashMap<>();
    for (Map.Entry<K, V> entry : list)
    {
        result.put( entry.getKey(), entry.getValue() );
    }
    return result;
}
	
	public static <K, V extends Comparable<? super V>> Map<K, V> 
	sortByValueDesc( Map<K, V> map )
	{
		List<Map.Entry<K, V>> list =
				new LinkedList<>( map.entrySet() );
				Collections.sort( list, new Comparator<Map.Entry<K, V>>()
						{
					@Override
					public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
					{
						return ( o2.getValue() ).compareTo( o1.getValue() );
					}
						} );
				
				Map<K, V> result = new LinkedHashMap<>();
				for (Map.Entry<K, V> entry : list)
				{
					result.put( entry.getKey(), entry.getValue() );
				}
				return result;
	}

	public HashMap<String, Integer> incrementOne(HashMap<String, Integer> map,
			String id) {
		Integer totalFreq = 0;
		if (!map.containsKey(id)) {
			totalFreq = 1;
		} else {
			totalFreq = map.get(id) + 1;

		}
		map.put(id, totalFreq);
//		if (!map.containsKey(id) || map.containsKey(id)
//				&& totalFreq > map.get(id)) {
//			map.put(id, totalFreq);
//		}
		return map;
	}

	public String getFirstSentence(String text) {
		BreakIterator boundary = BreakIterator.getSentenceInstance(Locale.US);
		boundary.setText(text);
		int start = boundary.first();
		int end = boundary.next();
		// System.out.println("start: "+start +" end: "+end);
		return text.substring(start, end);
	}

	public ArrayList<String> getSentences(String text) {
		ArrayList<String> sentences = new ArrayList<String>();
		BreakIterator boundary = BreakIterator.getSentenceInstance(Locale.US);
		int start = boundary.first();
		for (int end = boundary.next();
				end != BreakIterator.DONE;
				start = end, end = boundary.next()) {
//			 System.out.println("start: "+start +" end: "+end);
			sentences.add(text.substring(start,end));
		}
		if (sentences.size()==0) {
			sentences.add(text);
		}
		return sentences;
	}
	
	public Double getK(HashMap<String, Integer> map) {
		Set<String> keys = map.keySet();
		Double k = 0.0;
		Double total = 0.0;
		Double i = 1.0;
		for (String key : keys) {
			total += map.get(key) * Math.log(i);
			i++;
		}
		k = total/map.size();
		return k;
	}

	public void printWords(CSVPrinter printer) throws IOException {
		frequencies = (HashMap<String, Integer>) sortByValueDesc(frequencies);
		printer.print("Word");
		printer.print("Rank");
		printer.print("Frequency");
		printer.println();
		int i = 1;
		Set<String> keys = frequencies.keySet();
		for (String key : keys) {
			printer.print(key);
			printer.print(Math.log(i));
			printer.print(Math.log(frequencies.get(key)));
			printer.println();
			i++;
		}
		Double k = getK(frequencies);
		System.out.println("k for words = "+k);
	}
	
	public void printBigrams(CSVPrinter printer) throws IOException {
		bigrams = (HashMap<String, Integer>) sortByValueDesc(bigrams);
		printer.print("Bigram");
		printer.print("Rank");
		printer.print("Frequency");
		printer.println();
		Set<String> keys = bigrams.keySet();
		int i = 1;
		for (String key : keys) {
			printer.print(key);
			printer.print(Math.log(i));
			printer.print(Math.log(bigrams.get(key)));
			printer.println();
			i++;
		}
		Double k = getK(bigrams);
		System.out.println("k for bigrams = "+k);
	}
	
	public void printBoth(CSVPrinter printer) throws IOException {
		frequencies.putAll(bigrams);
		frequencies = (HashMap<String, Integer>) sortByValueDesc(frequencies);
		printer.print("N-Gram");
		printer.print("Rank");
		printer.print("Frequency");
		printer.println();
		Set<String> keys = frequencies.keySet();
		int i = 1;
		for (String key : keys) {
			printer.print(key);
			printer.print(Math.log(i));
			printer.print(Math.log(frequencies.get(key)));
			printer.println();
			i++;
		}
		Double k = getK(frequencies);
		System.out.println("k for both = "+k);
	}
	
	public void printVocabSizes(CSVPrinter printer) throws IOException {
		printer.print("# Docs");
		printer.print("Vocab");
		printer.println();
		for (int i =0; i<vocabSizes.size(); i++) {
			printer.print((i+1));
			printer.print(vocabSizes.get(i));
			printer.println();
		}
	}
	
	public Double getNumDocs() {
		return numDocs;
	}

	public Double getWordFreq(String word, String fileName) {
		if (index.containsKey(word)) {
			if (index.get(word).containsKey(fileName)) {
				return index.get(word).get(fileName);
			} else {
				return 0.0;
			}
		} else {
			return 0.0;
		}
	}

	public HashMap<String, String> getFirstSentences() {
		return firstSentences;
	}

	public void setFirstSentences(HashMap<String, String> firstSentences) {
		this.firstSentences = firstSentences;
	}

	public HashMap<String, Double> getMaxFrequencies() {
		return maxFrequencies;
	}

	public void setMaxFrequencies(HashMap<String, Double> maxFrequencies) {
		this.maxFrequencies = maxFrequencies;
	}

//	public File[] getFiles() {
//		return files;
//	}
//
//	public void setFiles(File[] files) {
//		this.files = files;
//	}

	
	public HashMap<String, HashMap<String, Double>> getIndex() {
		return index;
	}

	public ArrayList<File> getFiles() {
		return files;
	}

	public void setFiles(ArrayList<File> files) {
		this.files = files;
	}

	public int getNumAppear(String word) {
		if (index.containsKey(word)) {
			return index.get(word).size();
		} else {
			return 0;
		}
	}

	public void setIndex(HashMap<String, HashMap<String, Double>> index) {
		this.index = index;
	}

	// Get text from file
	public static String readStream(InputStream is) {
		StringBuilder sb = new StringBuilder(512);
		try {
			Reader r = new InputStreamReader(is, "UTF-8");
			int c = 0;
			while ((c = r.read()) != -1) {
				sb.append((char) c);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return sb.toString();
	}

	public int getWordCount() {
		return wordCount;
	}

	public void setWordCount(int wordCount) {
		this.wordCount = wordCount;
	}

	public int getFrequency(String word) {
		if (word.length()>2) {
			word = stemmer.stem(word);
			if (!frequencies.containsKey(word)) {
				return 0;
			} else {
				return frequencies.get(word);
			} 
		} else {
			return 0;
		}
	}

	public void setFrequencies(HashMap<String, Integer> frequencies) {
		this.frequencies = frequencies;
	}

	
	
}
