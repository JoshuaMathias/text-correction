package split_words;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import org.apache.commons.lang.StringUtils;

import shared.FileUtils;

public class SplitWords {
	HashSet<String> dict;
	BufferedWriter logWriter = null;
	int splitWordCount = 0;
	
	public SplitWords(HashSet<String> dict, String splitLogFile) {
		this.dict = dict;
		if (splitLogFile != null && splitLogFile.length()>0) {
			File logFile = new File(splitLogFile);
			System.out.println("Storing split words at "+splitLogFile);
			logWriter = FileUtils.getLineWriter(splitLogFile);
		}
	}
	
	public boolean inDictionary(String word) {
		if (StringUtils.isNumeric(word)) {
			return true;
		}
		if (dict.contains(word.toLowerCase())) {
			return true;
		}
		return false;
	}
	
	public String splitWord(String combinedWord) {
		int len = combinedWord.length();
		
		String beginStr = "";
		String endStr = "";
		ArrayList<SplitWordOption> possibleStrs = new ArrayList<SplitWordOption>();
		for (int i=len; i>0; i--) {
			beginStr = combinedWord.substring(0,i);
			endStr = combinedWord.substring(i,len);
			if (inDictionary(beginStr)) {
				if (inDictionary(endStr)) {
					return beginStr + " " + endStr;
				} else {
					possibleStrs.add(new SplitWordOption(beginStr, i));
				}
			}
		}
		endStr = combinedWord;
		if (!possibleStrs.isEmpty()) {
			SplitWordOption currentOption = possibleStrs.get(0);
			while (endStr.length()!=0 && !possibleStrs.isEmpty()) {
				System.out.println("curren option: "+currentOption.words);
				for (int j = len; j>currentOption.index; j--) {
					String addWord = combinedWord.substring(currentOption.index, j);
					if (inDictionary(addWord)) {
						if (j == len) {
							return currentOption.words + " " + addWord;
						}
						possibleStrs.add(new SplitWordOption(currentOption.words + " " + addWord, j));
					}
				}
				if (!possibleStrs.isEmpty()) {
					Collections.sort(possibleStrs);
					currentOption = possibleStrs.get(0);
					possibleStrs.remove(0);
				}
			}
		}
		return combinedWord;
	}
	
//	public String splitWordRec(String combinedWord, String currentWord) {
////		System.out.println("combinedWord: "+combinedWord);
//		int len = combinedWord.length();
//	
//		String splitWords = "";
//		int i=len;
//		String currentStr = "";
//		for (; i>=0; i--) {
//			String beginStr = combinedWord.substring(0,i);
//			String splitBegin = null;
//			System.out.println("beginStr: "+beginStr);
//			if (!inDictionary(beginStr) && beginStr.length() > 1) {
//				splitBegin = splitWordRec(combinedWord, );
////				if (splitBegin == null) {
////					splitBegin = beginStr;
////				}
//			} else {
//				splitBegin = beginStr;
//			}
//			System.out.println("len: "+len);
//			String endStr = combinedWord.substring(i,len);
//			String splitEnd = null;
//			System.out.println("endStr: "+endStr);
//			if (!inDictionary(endStr) && endStr.length() > 1) {
//				splitEnd = splitWordRec(combinedWord, endStr);
////				if (splitEnd == null) {
////					splitEnd = endStr;
////				}
//			} else {
//				splitEnd = endStr;
//			}
//			System.out.println("splitBegin: "+splitBegin+" splitEnd: "+splitEnd);
//			if (inDictionary(splitBegin) && inDictionary(splitEnd)) {
//				return splitBegin + " " + splitEnd;
//			} else {
//				return combinedWord;
//			}
////			if (!splitBegin.equals(null)) {
////				for (int j=0; j<len; j++) {
////					String beginStr = combinedWord.substring(0,j);
////					currentStr = combinedWord.substring(j,i);
////	//				System.out.println("i: "+i);
////	//				System.out.println("currentStr: "+currentStr);
////					
////					if (inDictionary(currentStr)) {
////						if (i<len) {
////							String beginStr = combinedWord.substring(i,len);
////							if (!inDictionary(beginStr)) {
////								String splitBegin = splitWord(beginStr);
////								if (splitBegin == null) {
////									splitWords += splitBegin;
////								} else {
////									continue;
////								}
////							}
////						}
////						splitWords += currentStr + " ";
////	//					System.out.println("Adding currentStr: "+currentStr);
////						break;
////					}
////				}
////			}
//			
//		}
//		if (i<len && i>-1) {
//			splitWords += splitWordRec(combinedWord, combinedWord.substring(i,len));
//		}
//		if (splitWords.length()==0) {
//			return null;
//		}
////		System.out.println("splitWords: "+splitWords);
//		return splitWords;
//	}
	
	public String splitFileWords(File file) {
		String splitFileString = "";
		BufferedReader reader = FileUtils.getLineReader(file.getAbsolutePath());
		String line = "";
		try {
			if (logWriter != null) {
				logWriter.write(file.getName()+":\n");
			}
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
						if (!inDictionary(currentWord) && currentWord.length()>1) {
							currentWord = splitWord(currentWord);
//							System.out.println("Split words: "+currentWord);
							if (logWriter != null) {
								logWriter.write(currentWord+"\n");
							}
							splitWordCount++;
						}
						splitFileString += currentWord;
						currentWord = "";
						splitFileString += letter;
					}
				}
				splitFileString += "\n";
			}
			if (logWriter != null) {
				logWriter.write("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return splitFileString;
	}
	
	public void closeWriter() {
		try {
			System.out.println("Words split: "+splitWordCount);
			if (logWriter!=null) {
				logWriter.write("Words split: "+splitWordCount);
				this.logWriter.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
