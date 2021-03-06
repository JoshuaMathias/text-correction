package shared;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import spelling.Speller;

public class ParseUtils {
	public static String wordChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-'";
	public static String punctuation = ".;,)('\"";
	
	// Replaces different versions of symbols with a standard symbol.
	// Removes uncommon and unrecognized symbols.
	public static String rmSpecialChars(String fileStr) {
//		String regexStr = "\\p{C}\n^";
		fileStr = fileStr.replaceAll("[“‟”＂❝❞]", "\""); //Use one standard quotation mark.
		fileStr = fileStr.replaceAll("[‘‛’❛❜’]", "'"); //Use one standard apostrophe.
		String regexStr = "[^\\s\\p{P}\\p{L}\\p{N}©]"; //Only keep these characters.
//		int count = StringUtils.countMatches(fileStr, regexStr);
//		System.out.println("Special characters removed: "+count);
		return fileStr.replaceAll(regexStr, " ");
//		return fileStr.replaceAll(" ", "  ");
	}
	
	// Returns the Unicode value of a char.
	// Example: \u002e
	public static String getUnicode(char ch) {
		return "\\u" + Integer.toHexString(ch | 0x10000).substring(1);
	}
	
	// Searches among the files given for capitalized words and writes them to a file.
	public static void writeCapsExamples(ArrayList<File> files, PrintWriter writer) {
		String regex = "(\\p{Lu}{2,}\\s*\\p{Lu}*){2,}";
		Pattern p = Pattern.compile(regex);
		for (File file : files) {
			BufferedReader lineReader = FileUtils.getLineReader(file.getAbsolutePath());
			String fileStr = "";
			try {
				while (lineReader.ready()) {
//					fileStr = FileUtils.readFile(file.getAbsolutePath());
					fileStr = lineReader.readLine();
					String[] splitLine = fileStr.split("\t");
					if (splitLine.length > 2 && Double.parseDouble(splitLine[0])>-1.0) {
						Matcher m = p.matcher(fileStr);
						if (m.find()) {
							writer.write(splitLine[0]+"\t"+splitLine[1]+"\n");
						}
					}
				}
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}
	
	// Prints the number of files that contain 20 or fewer characters.
	// Also prints the number of files with each amount of characters.
	public static void printSmallFiles(ArrayList<File> files) {
//		String regex = "^([^\\s\\x0B\u0020\u00A0\u1680\u180E\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u200B\u202F\u205F\u3000\uFEFF]{1,20})$";
//		String regex = "^([^\\s]{1,20})$";
//		Pattern p = Pattern.compile(regex);
		int numBlank = 0;
		HashMap<String, Integer> instances = new HashMap<String, Integer>();
		for (File file : files) {
			String fileStr = FileUtils.readFile(file.getAbsolutePath());
			fileStr = fileStr.replaceAll("[\\s\\x0B\u0020\u00A0\u1680\u180E\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u200B\u202F\u205F\u3000\uFEFF]", "");
//			Matcher m = p.matcher(fileStr);
			if (fileStr.length() < 21) {
//				System.out.println(file.getName());
				numBlank += 1;
				instances = FileUtils.incrementOne(instances, String.valueOf(fileStr.length()));
			}
		}
		System.out.println("Number of small files: "+numBlank);
		LinkedHashMap<String, Integer> orderedInstances = FileUtils.sortHashMapByValues(instances, false);
		for (String key : orderedInstances.keySet()) {
			System.out.println("Frequency of "+key+" non-space characters: "+orderedInstances.get(key));
		}
	}
	
	// Prints the number of files that contain only whitespace.
	// Also prints the number of blank files with each amount of whitespace.
	public static void printBlankFiles(ArrayList<File> files) {
		String regex = "^[\\s\\x0B\u0020\u00A0\u1680\u180E\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u200B\u202F\u205F\u3000\uFEFF]*$";
		Pattern p = Pattern.compile(regex);
		int numBlank = 0;
		HashMap<String, Integer> instances = new HashMap<String, Integer>();
		for (File file : files) {
			String fileStr = FileUtils.readFile(file.getAbsolutePath());
			Matcher m = p.matcher(fileStr);
			if (m.find()) {
//				System.out.println(file.getName());
				numBlank += 1;
				instances = FileUtils.incrementOne(instances, String.valueOf(fileStr.length()));
			}
		}
		System.out.println("Number of blank files: "+numBlank);
		LinkedHashMap<String, Integer> orderedInstances = FileUtils.sortHashMapByValues(instances, false);
		for (String key : orderedInstances.keySet()) {
			System.out.println("File length "+key+": "+orderedInstances.get(key));
		}
	}
	
	// Prints the number of instances found for the given regex of one character.
	// Prints each character with the number of instances for each character, in descending order.
	public static void printNumInstances(ArrayList<File> files, String regex) {
//		Pattern p = Pattern.compile("[\\t\\f\\r\\x0B\u0020\u00A0\u1680\u180E\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u200B\u202F\u205F\u3000\uFEFF]");  // insert your pattern here
		System.out.println(regex+"\n");
		Pattern p = Pattern.compile(regex);
		HashMap<String, Integer> instances = new HashMap<String, Integer>();
		for (File file : files) {
			String fileStr = FileUtils.readFile(file.getAbsolutePath());
			Matcher m = p.matcher(fileStr);
			while (m.find()) {
					instances = FileUtils.incrementOne(instances, getUnicode(m.group().toCharArray()[0])+" ("+m.group()+")");
			}
		}
		LinkedHashMap<String, Integer> orderedInstances = FileUtils.sortHashMapByValues(instances, false);
		for (String key : orderedInstances.keySet()) {
			System.out.println(key+": "+orderedInstances.get(key));
		}
	}
	
	// Prints the number of instances found for the given regex category (may be multiple characters).
	// Each regex category is separated by ||
	// Prints each category with the number of instances for each category, in descending order.
	// Example regex: "<[^>\n]*>||Off(Off)+||_(_)+"
	public static void printNumCategories(ArrayList<File> files, String regex) {
		Pattern regexPattern = Pattern.compile(regex.replace("||", "|"));
		String[] regexes = regex.split("\\|\\|");
		ArrayList<Pattern> patterns = new ArrayList<Pattern>();
//		try {
		ArrayList<String> foundStrs = new ArrayList<String>();
//		BufferedWriter writer = FileUtils.getLineWriter("output.txt");
		for (String regCategory : regexes) {
			System.out.println("Pattern: "+regCategory);

//			writer.write("Pattern: "+regCategory+"\n");
			patterns.add(Pattern.compile(regCategory));
		}
		HashMap<String, Integer> instances = new HashMap<String, Integer>();
		for (File file : files) {
			String fileStr = ParseUtils.rmBlankSpace(FileUtils.readFile(file.getAbsolutePath()));
			Matcher m = regexPattern.matcher(fileStr);
			while (m.find()) {
					String foundStr = m.group().replace("\\s", "");
//					System.out.println("foundStr: "+foundStr);
					if (!foundStrs.contains(foundStr)) {
//						System.out.println("foundStr: "+foundStr+"\n");
//						writer.write("foundStr: "+foundStr+"\n\n");
						foundStrs.add(foundStr);
					}
					for (Pattern pattern : patterns) {
						if (pattern.matcher(foundStr).matches()) {
//							System.out.println("Pattern matched: "+pattern.toString());
							instances = FileUtils.incrementOne(instances, pattern.toString());
						}
					}
			}
		}
		LinkedHashMap<String, Integer> orderedInstances = FileUtils.sortHashMapByValues(instances, false);
		for (String key : orderedInstances.keySet()) {
			System.out.println(key+": "+orderedInstances.get(key));
		}
//		writer.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	// Write to a file all words found that aren't in the dictionary.
	// Sorted by the number of instances of each words, in descending order.
	public static void writeUnknownWords(ArrayList<File> files, HashMap<String, Double> dict) {
		BufferedWriter writer = FileUtils.getLineWriter("unknown_words.txt");
		HashMap<String, Integer> instances = new HashMap<String, Integer>();
		try {
			for (File file : files) {
				// Only check English files.
				if (!file.getName().contains("eng")) {
					continue;
				}
				String text = FileUtils.readFile(file);
				ArrayList<String> words = getWords(text);
				for (String word : words) {
					word = word.toLowerCase();
					if (!dict.containsKey(word)) {
						instances = FileUtils.incrementOne(instances, word);
					}
				}
				
			}
			LinkedHashMap<String, Integer> orderedInstances = FileUtils.sortHashMapByValues(instances, false);
			for (String key : orderedInstances.keySet()) {
				writer.write(key+": "+orderedInstances.get(key)+"\n");
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Write spelling suggestions for unrecognized words in the given files.
	public static void writeSpellingSuggestions(ArrayList<File> files, HashMap<String, Double> dict) {
		BufferedWriter writer = FileUtils.getLineWriter("spelling_suggestions.txt");
		HashMap<String, Integer> instances = new HashMap<String, Integer>();
		Speller speller = new Speller(dict);
		try {
			for (File file : files) {
				if (!file.getName().contains("eng")) {
					continue;
				}
				String text = FileUtils.readFile(file);
				ArrayList<String> words = getWords(text);
				for (String word : words) {
					word = word.toLowerCase();
					String suggestion = speller.suggestSimilarWord(word);
					if (!suggestion.equals(word)) {
						instances = FileUtils.incrementOne(instances, word+": "+suggestion);
					}
				}

			}
			LinkedHashMap<String, Integer> orderedInstances = FileUtils.sortHashMapByValues(instances, false);
			for (String key : orderedInstances.keySet()) {
				writer.write(key+": "+orderedInstances.get(key)+"\n");
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Removes blank lines and replaces all whitespace with a single space.
	public static String rmBlankSpace(String fileStr) {
//		int count = StringUtils.countMatches(fileStr, "^*\n\\s*(\n|$)");
//		System.out.println("Blank lines removed: "+count);
//		fileStr = fileStr.replaceAll("\n\\s*\n", "\n"); //Double or blank lines
//		fileStr = fileStr.replaceAll("^\\s*\n|\n\\s*$", ""); //Space at beginning or end of file.
//		fileStr = fileStr.replaceAll("\n\\s+|\\s+\n", "\n"); //Space at the beginning or end of a line.
//		Pattern p = Pattern.compile("\\t\\f\\r\\x0B\u0020\u00A0\u1680\u180E\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u200B\u202F\u205F\u3000\uFEFF");  // insert your pattern here
//		Matcher m = p.matcher(fileStr);
//		if (m.find()) {
//			for (int i=1; i<=m.groupCount(); i++) {
//				System.out.println(m.group());
//			}
//		}
		fileStr = fileStr.replaceAll("[\\t\\f\\x0B\u0020\u00A0\u1680\u180E\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u200B\u202F\u205F\u3000\uFEFF]", " ");
		fileStr = fileStr.replaceAll("[\\u000A\\u000D\u0085\u000B\u000C\u2028\u2029]", "\n");
		fileStr = fileStr.replaceAll("^\\s+|\\s+$|\\s*(\n)\\s*|(\\s)\\s*", "$1$2"); //Replace double spacing and other non-line spacing with one space.
		return fileStr;
	}
	
	// Remove code or formatting syntax.
	public static String rmCode(String fileStr) {
		fileStr = fileStr.replaceAll("<[^>\n]*>", " "); //Remove HTML and XML
		fileStr = fileStr.replaceAll("([^.@\\s]+)(\\.[^.@\\s]+)*@([^.@\\s]+\\.)+([^.@\\s]+)", " _ "); //Remove email addresses
		fileStr = fileStr.replaceAll("((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)", " "); //Remove URLs
		fileStr = fileStr.replaceAll("Off(Off)+", " "); //Text extracted from PDF files often contain the word Off for each checkbox.
		fileStr = fileStr.replaceAll("_(_)+", " _ "); //Remove fill in the blanks: ______
		fileStr = fileStr.replaceAll("(\\s|^)[^\\p{L}]{5,}(\\s|$)", " _ "); //Remove tokens with five or more characters that contain no letters (includes phone numbers).
		return fileStr;
	}
	
	// Correct casing to the most likely casing.
//	public static String correctCasing(HashMap<String, Double> dict, String fileStr) {
//		String[] words = fileStr.split("\\s");
//		char[] textArray = fileStr.toCharArray();
//		for (char letter : textArray) {
//			if (dict.containsKey(word)) {
//				
//			}
//		}
////		fileStr = 
//	}
	
	// Get the first sentence of a String.
	public static String getFirstSentence(String text) {
		BreakIterator boundary = BreakIterator.getSentenceInstance(Locale.US);
		boundary.setText(text);
		int start = boundary.first();
		int end = boundary.next();
		// System.out.println("start: "+start +" end: "+end);
		return text.substring(start, end);
	}
	
	// Strip a String of all characters other than those defined in wordChars.
	public static String stripNonWordChars(String text) {
		String strippedStr = "";
		char[] textArray = text.toCharArray();
		
		for (char letter : textArray) {
			if ((wordChars).indexOf(letter) != -1) {
				strippedStr += letter;
			}
		}
		return strippedStr;
	}
	
	// Returns an ArrayList of words, keeping only characters defined in wordChars.
	public static ArrayList<String> getWords(String text) {
//		return text.split("\\W");
		ArrayList<String> words = new ArrayList<String>();
		char[] textArray = text.toCharArray();
		String currentWord = "";
//		Pattern pattern = Pattern.compile("^\\w-'");
		
		for (char letter : textArray) {
		
//			Matcher matcher = pattern.matcher(String.valueOf(letter));
			if ((wordChars).indexOf(letter) != -1) {
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
