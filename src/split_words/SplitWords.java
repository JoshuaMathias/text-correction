package split_words;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import shared.FileUtils;

public class SplitWords {
	HashSet<String> dict;
	
	public SplitWords(HashSet<String> dict) {
		this.dict = dict;
	}
	
	public boolean inDictionary(String word) {
		for (String entry : dict) {
			if (word.toLowerCase().equals(entry)) {
				return true;
			}
		}
		return false;
	}
	
	public String splitWord(String combinedWord) {
//		System.out.println("combinedWord: "+combinedWord);
		int len = combinedWord.length();
	
		String splitWords = "";
		int i=len;
		String currentStr = "";
		for (; i>=0; i--) {
			currentStr = combinedWord.substring(0,i);
//			System.out.println("i: "+i);
//			System.out.println("currentStr: "+currentStr);
			if (inDictionary(currentStr)) {
				splitWords += currentStr + " ";
//				System.out.println("Adding currentStr: "+currentStr);
				break;
			}
			
		}
		if (i<len && i>-1) {
			splitWords += splitWord(combinedWord.substring(i,len));
		}
		if (splitWords.length()==0) {
			return combinedWord;
		}
//		System.out.println("splitWords: "+splitWords);
		return splitWords;
	}
	
	public String splitFileWords(File file) {
		String splitFileString = "";
		BufferedReader reader = FileUtils.getLineReader(file.getAbsolutePath());
		String line = "";
		try {
			String currentWord = "";
			while ((line = reader.readLine()) != null) {
				currentWord = "";
				String letter = "";
				for (int i=0; i<line.length(); i++) {
					letter = String.valueOf(line.charAt(i));
//					System.out.println("letter: "+letter);
//					System.out.println("currentWord: "+currentWord);
					if (("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-'").indexOf(letter) != -1) {
						currentWord += letter;
					} else {
						if (inDictionary(currentWord) || currentWord.length()<2) {
							splitFileString += currentWord;
						} else {
							splitFileString += splitWord(currentWord);
						}
						currentWord = "";
						splitFileString += letter;
					}
				}
				splitFileString += "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return splitFileString;
	}
}
