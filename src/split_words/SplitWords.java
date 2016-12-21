package split_words;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import shared.FileUtils;

public class SplitWords {
	HashMap<String, Double> dict;
	BufferedWriter logWriter = null;
	int splitWordCount = 0;
	Double defaultLMScore= -6.5227585;
	Pattern nonWordPattern = Pattern.compile("[^\\p{L}]{2,}");
	
//	public SplitWords(HashSet<String> dict, String splitLogFile) {
//		this.dict = dict;
//		if (splitLogFile != null && splitLogFile.length()>0) {
//			File logFile = new File(splitLogFile);
//			System.out.println("Storing split words at "+splitLogFile);
//			logWriter = FileUtils.getLineWriter(splitLogFile);
//		}
//	}
	
	public SplitWords(HashMap<String,Double> dict, String splitLogFile) {
		this.dict = dict;
		if (splitLogFile != null && splitLogFile.length()>0) {
			File logFile = new File(splitLogFile);
			System.out.println("Storing split words at "+splitLogFile);
			logWriter = FileUtils.getLineWriter(splitLogFile);
		}
	}
	
	public boolean inDictionary(String word) {
		if (nonWordPattern.matcher(word).matches()) {
			return true;
		}
		if (dict.containsKey(word.toLowerCase())) {
			return true;
		}
		return false;
	}
	
	public String splitWord(String combinedWord) {
		int len = combinedWord.length();
//		try {
//			logWriter.write("\n"+combinedWord+"\n");
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//		System.out.println("Combined word: "+combinedWord);
		String beginStr = "";
		String endStr = "";
		ArrayList<SplitWordOption> possibleStrs = new ArrayList<SplitWordOption>();
		for (int i=len; i>0; i--) {
			beginStr = combinedWord.substring(0,i);
			endStr = combinedWord.substring(i,len);
			if (inDictionary(beginStr)) {
				if (dict.containsKey(beginStr.toLowerCase())) {
					possibleStrs.add(new SplitWordOption(beginStr, i, dict.get(beginStr.toLowerCase())));
				} else {
					possibleStrs.add(new SplitWordOption(beginStr, i, defaultLMScore));
				}
			}
		}
		Collections.sort(possibleStrs);
		
		if (!possibleStrs.isEmpty()) {
			SplitWordOption currentOption = possibleStrs.get(0);
			if (currentOption.index == len) {
				return currentOption.words;
			}
			while (!possibleStrs.isEmpty()) {
//				System.out.println("current option: "+currentOption.words);
				for (int j = len; j>currentOption.index; j--) {
					String addWord = combinedWord.substring(currentOption.index, j);
//					if (combinedWord.equals("theneighborhood")) {
//						System.out.println("addWord: "+addWord);
//					}
					if (inDictionary(addWord)) {
						String[] splitOption = currentOption.words.split(" ");
						Double totalScore = 0.0;
						for (String word : splitOption) {
							if (dict.containsKey(word)) {
//								if (combinedWord.equals("theneighborhood")) {
//									System.out.println("Adding "+dict.get(word.toLowerCase())+" for "+word);
//								}
								totalScore += dict.get(word.toLowerCase());
							} else {
//								System.out.println("Adding default "+defaultLMScore+" for "+word);
								totalScore += defaultLMScore;
							}
						}
//						System.out.println("Adding "+dict.get(addWord.toLowerCase())+" for "+addWord);
						if (dict.containsKey(addWord)) {
							totalScore += dict.get(addWord.toLowerCase());
						} else {
							totalScore += defaultLMScore;
						}
						
//						if (totalScore == 0.0) {
//							System.out.println("0 total score for "+currentOption.words);
//						}
//						System.out.println("lm score: "+totalScore/splitOption.length);
						possibleStrs.add(new SplitWordOption(currentOption.words + " " + addWord, j, totalScore/splitOption.length));
					}
				}
				if (!possibleStrs.isEmpty()) {
					Collections.sort(possibleStrs);
					currentOption = possibleStrs.get(0);
//					System.out.println(currentOption.index);
					if (currentOption.index == len) {
						try {
							for (SplitWordOption option : possibleStrs) {
//								System.out.println(option.words+"\tcurrentIndex: "+option.index+"\tnumWords: "+option.numWords+"\tlmScore: "+option.lmScore+"\tCalculated score: "+option.score+"\n");
								logWriter.write(option.words+"\tcurrentIndex: "+option.index+"\tnumWords: "+option.numWords+"\tlmScore: "+option.lmScore+"\tCalculated score: "+option.score+"\n");

							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					possibleStrs.remove(0);
					if (currentOption.index == len) {
						return currentOption.words;
					}
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
	
	public String splitWords(String fileStr) {
//		System.out.println("Splitting words of "+fileStr);
		String splitFileString = "";
//		BufferedReader reader = FileUtils.getLineReader(file.getAbsolutePath());
//		String fileStr = FileUtils.readFile(file.getAbsolutePath());
//		System.out.println(fileStr);
//		String line = "";
		try {
//			if (logWriter != null) {
//				logWriter.write(file.getName()+":\n");
//			}
			ArrayList<String> unsplitWords = new ArrayList<String>();
			String currentWord = "";
//			while ((line = reader.readLine()) != null) {
				currentWord = "";
				String letter = "";
				for (int i=0; i<fileStr.length(); i++) {
					letter = String.valueOf(fileStr.charAt(i));
//					if (currentWord.contains("of")) {
//						System.out.println("line: "+fileStr);
//						System.out.println("letter: "+letter);
//						System.out.println("currentWord: "+currentWord);
//					}
					if ((" \n").indexOf(letter) == -1) {
						currentWord += letter;
					} else {
						if (!inDictionary(currentWord) && currentWord.length()>1) {
							
							String splitWord = splitWord(currentWord);
//							System.out.println("Split words: "+currentWord);
							if (!splitWord.equals(currentWord)) {
								splitWordCount++;
								if (logWriter != null) {
									logWriter.write(currentWord+"\t"+splitWord+"\n");
								}
							} else {
								unsplitWords.add(currentWord);
							}
							currentWord = splitWord;
							
						}
						splitFileString += currentWord;
						currentWord = "";
						splitFileString += letter;
					}
//				}
//				splitFileString += "\n";
			}
//			if (logWriter != null) {
//				logWriter.write("\n");
//				if (unsplitWords.size()>0) {
//					logWriter.write("Unrecognized unsplit words:\n");
//					for (String word : unsplitWords) {
//						logWriter.write(word+"\n");
//					}
//					logWriter.write("\n");
//				}
//			}
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
			e.printStackTrace();
		}
	}
	
}
