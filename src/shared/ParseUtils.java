package shared;

import java.io.BufferedReader;
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

public class ParseUtils {
	public static String wordChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-'";
	public static String punctuation = ".;,)('\"";
	
	//Removes 
	public static String rmSpecialChars(String fileStr) {
//		String regexStr = "\\p{C}\n^";
		fileStr = fileStr.replaceAll("[“‟”＂❝❞]", "\""); //Use one standard quotation mark.
		fileStr = fileStr.replaceAll("[‘‛’❛❜’]", "'"); //Use one standard apostrophe.
		String regexStr = "[^\\s\n\\w\\-\\‑⁃‒–⎯—―~⁓'\"\\p{P}\\p{L}/©]";
//		int count = StringUtils.countMatches(fileStr, regexStr);
//		System.out.println("Special characters removed: "+count);
		return fileStr.replaceAll(regexStr, " ");
//		return fileStr.replaceAll(" ", "  ");
	}
	
	public static String getUnicode(char ch) {
		return "\\u" + Integer.toHexString(ch | 0x10000).substring(1);
	}
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
	
	public static void printNumInstances(ArrayList<File> files, String regex) {
//		Pattern p = Pattern.compile("[\\t\\f\\r\\x0B\u0020\u00A0\u1680\u180E\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u200B\u202F\u205F\u3000\uFEFF]");  // insert your pattern here
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
	
	//Removes blank lines and replaces all whitespace with a single space.
	public static String rmBlankSpace(String fileStr) {
//		int count = StringUtils.countMatches(fileStr, "^*\n\\s*(\n|$)");
//		System.out.println("Blank lines removed: "+count);
//		fileStr = fileStr.replaceAll("\n\\s*\n", "\n"); //Double or blank lines
//		fileStr = fileStr.replaceAll("^\\s*\n|\n\\s*$", ""); //Space at beginning or end of file.
//		fileStr = fileStr.replaceAll("\n\\s+|\\s+\n", "\n"); //Space at the beginning or end of a line.
		Pattern p = Pattern.compile("\\t\\f\\r\\x0B\u0020\u00A0\u1680\u180E\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u200B\u202F\u205F\u3000\uFEFF");  // insert your pattern here
		Matcher m = p.matcher(fileStr);
		if (m.find()) {
			for (int i=1; i<=m.groupCount(); i++) {
				System.out.println(m.group());
			}
		}
		fileStr = fileStr.replaceAll("[\\t\\f\\r\\x0B\u0020\u00A0\u1680\u180E\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u200B\u202F\u205F\u3000\uFEFF]", " ");
		fileStr = fileStr.replaceAll("^\\s+|\\s+$|\\s*(\n)\\s*|(\\s)\\s*", "$1$2"); //Replace double spacing and other non-line spacing with one space.
		return fileStr;
	}
	
	public static String rmCode(String fileStr) {
		fileStr = fileStr.replaceAll("<[^>]*>|^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", " "); //Remove HTML and XML
		fileStr = fileStr.replaceAll("([^.@\\s]+)(\\.[^.@\\s]+)*@([^.@\\s]+\\.)+([^.@\\s]+)", " "); //Remove email addresses
		fileStr = fileStr.replaceAll("Off(Off)+", " ");
		fileStr = fileStr.replaceAll("_(_)+", " ");
		return fileStr;
	}
	
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
	
	public static String getFirstSentence(String text) {
		BreakIterator boundary = BreakIterator.getSentenceInstance(Locale.US);
		boundary.setText(text);
		int start = boundary.first();
		int end = boundary.next();
		// System.out.println("start: "+start +" end: "+end);
		return text.substring(start, end);
	}
	
	public static String stripNonWordChars(String text) {
		String strippedStr = "";
		char[] textArray = text.toCharArray();
		
		for (char letter : textArray) {
			if (("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-'").indexOf(letter) != -1) {
				strippedStr += letter;
			}
		}
		return strippedStr;
	}
	
	
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
	
	public String removeExtraLines(String text) {
		return text.replace("\n\n", "\n");
	}
	
}
