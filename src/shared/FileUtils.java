package shared;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
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

	//Get a reader that can be read from line by line.
	public static BufferedReader getLineReader(String filename) {
		BufferedReader reader = null;
		try {
			return new BufferedReader( new InputStreamReader(
                    new FileInputStream(filename), "UTF-8"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return reader;
	}
	
	//Get a writer to write line by line.
	public static BufferedWriter getLineWriter(String filename) {
		BufferedWriter writer = null;
		try {
			return new BufferedWriter
				    (new OutputStreamWriter(new FileOutputStream(filename),"UTF-8"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return writer;
	}
	
	// Get content of file as one string from an InputStream.
	public static String readFile(InputStream is) {
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
	
	// Get content of file as one string from filename.
	public static String readFile(String filename) {
		try {
			return readFile(new FileInputStream(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// Get text from file as one string from File object.
	public static String readFile(File file) {
		try {
			return readFile(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//Sorts a HashMap by values and returns a LinkedHashMap.
	public static LinkedHashMap<String, Integer> sortHashMapByValues(
	        HashMap<String, Integer> passedMap, boolean ascending) {
	    List<String> mapKeys = new ArrayList<>(passedMap.keySet());
	    List<Integer> mapValues = new ArrayList<>(passedMap.values());
	    if (ascending) {
	    	 Collections.sort(mapValues);
			 Collections.sort(mapKeys);
	    } else {
	    	Comparator<String> stringOrder = Collections.reverseOrder();
	    	Comparator<Integer> intOrder = Collections.reverseOrder();
	    	Collections.sort(mapValues, intOrder);
	    	Collections.sort(mapKeys, stringOrder);
	    }

	    LinkedHashMap<String, Integer> sortedMap =
	        new LinkedHashMap<>();

	    Iterator<Integer> valueIt = mapValues.iterator();
	    while (valueIt.hasNext()) {
	        Integer val = valueIt.next();
	        Iterator<String> keyIt = mapKeys.iterator();

	        while (keyIt.hasNext()) {
	            String key = keyIt.next();
	            Integer comp1 = passedMap.get(key);
	            Integer comp2 = val;

	            if (comp1.equals(comp2)) {
	                keyIt.remove();
	                sortedMap.put(key, val);
	                break;
	            }
	        }
	    }
	    return sortedMap;
	}
	
	//Increment the count of a specific key of a map within a map, whether it already exists or not.
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
	
	//Increment the count of a specific key of a map, whether it already exists or not.
	public static TreeMap<String, Integer> incrementOne(TreeMap<String, Integer> map,
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
	
	//Increment the count of a specific key of a map, whether it already exists or not.
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
	
	//Increment the count (Double) of a specific key of a map within a map, whether it already exists or not.
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
	
	//Increment the count (Double) of a specific key of a map, whether it already exists or not.
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
	
	//Return an ArrayList of sentences from a String.
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
	

	

}
