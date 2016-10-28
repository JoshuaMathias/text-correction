package shared;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {

	
	
	// If the path is a directory, recursively get a list of all the files.
	public static ArrayList<File> listFiles(File path) {
		ArrayList<File> files = new ArrayList<File>();
		if (path.isDirectory()) {
			for (File currentFile : path.listFiles()) {
				if (currentFile.isFile()) {
					files.add(currentFile);
				} else if (currentFile.isDirectory()) {
					files.addAll(listFiles(currentFile));
				}
			}
		} else if (files.size() == 0 && path.exists()) {
			files.add(path);
		}
		return files;
	}
	
	// If the path is a directory, recursively get a list of all the files.
	// Return the files as a map of folders. Ignores files not in subfolders.
	public static HashMap<String, ArrayList<File>> listFolders(File path) {
		HashMap<String, ArrayList<File>> fileMap = new HashMap<String, ArrayList<File>>();
		if (path.isDirectory()) {
			for (File currentFile : path.listFiles()) {
				if (currentFile.isDirectory()) {
					ArrayList<File> files = listFiles(currentFile);
					fileMap.put(currentFile.getName(), files);
				}
			}
		}
		return fileMap;
	}

	public static BufferedReader getLineReader(String filename) {
		BufferedReader reader = null;
		try {
			return new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return reader;
	}
	
	public static BufferedWriter getLineWriter(String filename) {
		BufferedWriter writer = null;
		try {
			return new BufferedWriter(new FileWriter(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return writer;
	}
	
	// Get text from file as one string
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
	
	// Get text from file as one string from filename
	public static String readFile(String filename) {
		try {
			return readStream(new FileInputStream(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//Increment the count of a specific key of a map within a map, whether it already exists or not
	public static HashMap<String, HashMap<String, Integer>> incrementOneMap(HashMap<String, HashMap<String, Integer>> map, String key, String innerKey) {
		HashMap<String, Integer> innerMap = null;
		if (!map.containsKey(key)) {
			innerMap = new HashMap<String, Integer>();
		} else {
			innerMap = map.get(key);

		}
		map.put(key, incrementOne(innerMap, innerKey));
		return map;
	}
	
	//Increment the count of a specific key of a map, whether it already exists or not
	public static HashMap<String, Integer> incrementOne(HashMap<String, Integer> map,
			String id) {
		Integer totalFreq = 0;
		if (!map.containsKey(id)) {
			totalFreq = 1;
		} else {
			totalFreq = map.get(id) + 1;

		}
		map.put(id, totalFreq);
		return map;
	}
	
	public static HashMap<String, HashMap<String, Double>> incrementDoubleMap(HashMap<String, HashMap<String, Double>> map, String key, String innerKey) {
		HashMap<String, Double> innerMap = null;
		if (!map.containsKey(key)) {
			innerMap = new HashMap<String, Double>();
		} else {
			innerMap = map.get(key);

		}
		map.put(key, incrementDouble(innerMap, innerKey));
		return map;
	}
	
	public static HashMap<String, Double> incrementDouble(HashMap<String, Double> map,
			String id) {
		Double totalFreq = 0.0;
		if (!map.containsKey(id)) {
			totalFreq = 1.0;
		} else {
			totalFreq = map.get(id) + 1.0;

		}
		map.put(id, totalFreq);
		return map;
	}
	
	public static ArrayList<String> getSentences(String text) {
		ArrayList<String> sentences = new ArrayList<String>();
		BreakIterator boundary = BreakIterator.getSentenceInstance(Locale.ENGLISH);
		boundary.setText(text);
		int start = boundary.first();
//		System.out.println("start: "+start);
		for (int end = boundary.next();
				end != BreakIterator.DONE;
				start = end, end = boundary.next()) {
//			 System.out.println("start: "+start +" end: "+end);
			sentences.add(text.substring(start,end).replace("\n", ""));
		}
		if (sentences.size()==0) {
			sentences.add(text);
		}
//		System.out.println("Number of sentences: "+sentences.size());
		return sentences;
	}
	
	public static String getFirstSentence(String text) {
		BreakIterator boundary = BreakIterator.getSentenceInstance(Locale.US);
		boundary.setText(text);
		int start = boundary.first();
		int end = boundary.next();
		// System.out.println("start: "+start +" end: "+end);
		return text.substring(start, end);
	}
	
	public static ArrayList<String> getWords(String text) {
//		return text.split("\\W");
		ArrayList<String> words = new ArrayList<String>();
		char[] textArray = text.toCharArray();
		String currentWord = "";
		Pattern pattern = Pattern.compile("^\\w-'");
		
		for (char letter : textArray) {
		
//			Matcher matcher = pattern.matcher(String.valueOf(letter));
			if (("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-'").indexOf(letter) != -1) {
				currentWord += letter;
			} else if (currentWord.length() > 0){
//				System.out.println("Adding word: "+currentWord);
				words.add(currentWord);
				currentWord = "";
			}
//			System.out.println(currentWord);
		}
		if (currentWord.length() > 0) {
			words.add(currentWord);
		}
		return words;
	}
	

}
