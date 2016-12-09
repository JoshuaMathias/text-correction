package spelling;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeSet;




//import org.joda.time.DateTime;
//import org.joda.time.LocalTime;

//import Spell.SpellCorrector.NoSimilarWordFoundException;

public class SpellingCorrector implements SpellCorrector {
		String similarWord;
		MyTrie trie;
		String[] alphabet;
		HashMap<String, HashMap<String,Integer>> correctedWords;
		HashMap<String, HashMap<Integer,ArrayList<String>>> sessions;
		HashMap<String, WordData> correctWords;
		ArrayList<String> dictionary;
		Indexer indexer;
		
		public SpellingCorrector(Indexer indexer) {
			similarWord=null;
			trie=new MyTrie();
			correctedWords = new HashMap<String, HashMap<String,Integer>>();
			dictionary = new ArrayList<String>();
			correctWords = new HashMap<String, WordData>();
			sessions = new HashMap<String, HashMap<Integer,ArrayList<String>>>();
			this.indexer = indexer;
			alphabet=new String[]{"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
		}
		
		public MyTrie getTrie() {
			return trie;
		}
	
		
    	public void useDictionary(String dictionaryFileName) {
    		BufferedReader br = null;
    		try {
    			br = new BufferedReader(new FileReader(new File(dictionaryFileName)));
    			String word;
    			while((word = br.readLine()) != null) {
    				dictionary.add(word);            
    			}
    		} catch (FileNotFoundException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		} finally {
    			if (br != null) {
    				try {
    					br.close();
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
    			}
    		}
    		//        	Scanner sc = null;
    		//			try {
    		//				sc = new Scanner(new File(dictionaryFileName));
    		//			} catch (FileNotFoundException e) {
    		//				e.printStackTrace();
    		//			}
    		//        	while (sc.hasNextLine()) {
    		////        		correctedWords.put(sc.next(), new HashMap<String, Integer>());
    		//        		String word = sc.nextLine();
    		//        		System.out.println("word: "+word);
    		//        		dictionary.add(word);
    		////        		trie.add(sc.next());
    		//        	}
    		//        	sc.close();
    	}

    	public void useLogs(String logFileName) {
//    		String str1 = " ";
//    		String str2 = "";
//    		System.out.println("Edit distance between "+str1+" and "+str2+": "+getEditDistance(str1,str2));
//    		if (str1.equals(str1)) {
//    			return;
//    		}
    		File file = new File(logFileName);
    		if (file.exists()) {
    			BufferedReader br = null;
    			try {
    				// parse and collect/cluster words in logs
    				br = new BufferedReader(new FileReader(file));
    				String line;
    				while((line = br.readLine()) != null) {
    					String[] data = line.split("\t");
    					String currentWord = "";
    					if (data.length > 1) {
    						String id = data[0];
    						String[] query = data[1].split("\\s");
    						HashMap<Integer,ArrayList<String>> positions = null;
    						if (!sessions.containsKey(id)) {
//    							System.out.println("Added session id "+id);
    							positions = new HashMap<Integer,ArrayList<String>>();
    						} else {
    							positions = sessions.get(id);
    						}
    						for (int i=0; i<query.length; i++) {
    							currentWord = query[i];
                                if (currentWord.length()<2 || currentWord.equals(" ")) {
                                    continue;
                                }
//                                if (dictionary.contains(currentWord)) {
//                                    if (!correctWords.containsKey(currentWord)) {
//                                        correctWords.put(currentWord, new WordData());
//                                    } else {
//                                    	correctWords.get(currentWord).addFreq();
//                                    }
//                                }
                                ArrayList<String> wordsList = null;
                                if (!positions.containsKey(i)) {
//                                  System.out.println("Added position "+i+" to id "+id);
                                    wordsList = new ArrayList<String>();
                                } else {
                                    wordsList = positions.get(i);
                                }
//                              System.out.println("Added word "+currentWord+" at position "+i);
                                wordsList.add(currentWord);
    							positions.put(i, wordsList);
    						}
    						sessions.put(id, positions);
    					}
    				}
    				// Get list of corrections with counts
    				String first = "";
    				String second = "";
    				String correction = "";
    				String error = "";
    				int k=0;
//    				System.out.println("Number of sessions: "+sessions.size());
    				for (HashMap<Integer,ArrayList<String>> sessionPositions : sessions.values()) {
//    					System.out.println("Session position "+k);
    					k++;
    					for (ArrayList<String> positionWords : sessionPositions.values()) {
    						for (int i=0; i<positionWords.size(); i++) {
    							first = positionWords.get(i);
    							for (int j=i; j<positionWords.size(); j++) {
    								second = positionWords.get(j);
    								if (getEditDistance(second,first)==1 || getEditDistance(second,first)==2) {
    									
//    									System.out.println("first: "+first);
//    									System.out.println("second: "+second);
    									if (!dictionary.contains(first)) {
    										if (dictionary.contains(second)) {
	    										error = first;
	    										correction = second;
    										} else {
    											continue;
    										}
    									} else if (!dictionary.contains(second)) {
    										error = second;
    										correction = first;
    									} else {
    										continue;
    									}

    								}
//    								System.out.println("correction: "+correction);
    								if (!correctWords.containsKey(correction)) {
                                      correctWords.put(correction, new WordData());
    								}
    								correctWords.get(correction).addCorrectionCount();
    								//If correction exists, make new map. Otherwise, ++
    								HashMap<String,Integer> corrections = null;
    								if (!correctedWords.containsKey(error)) {
    									corrections = new HashMap<String,Integer>();
    									corrections.put(correction, 1);
    									correctedWords.put(correction, corrections);
    								} else {
    									corrections = correctedWords.get(error);
    									if (!corrections.containsKey(correction)) {
    										corrections.put(correction, 1);
    									} else {
    										corrections.put(correction, corrections.get(correction)+1);
    									}
    									if (error.equals("movi")) {
//    										System.out.println("Added correction "+correction+" for "+error);
    									}
    								}
    								
    								correctedWords.put(error, corrections);
    							} 
    						}
    					}
    				}
    			} catch (FileNotFoundException e) {
    				e.printStackTrace();
    			} catch (IOException e) {
    				e.printStackTrace();
    			} finally {
    				if (br != null) {
    					try {
    						br.close();
    					} catch (IOException e) {
    						e.printStackTrace();
    					}
    				}
    			}
//    			System.out.println("Parsing logs in file "+file.getName());
//    			Scanner scan = null;
//    			try {
//    				scan = new Scanner(file);
//    			} catch (FileNotFoundException e) {
//    				System.out.println("File " + file.getName() + " not found");
//    			}
//    			if (scan.hasNextLine()) {
//    				// Skip the labels
//    				scan.nextLine();
//    			}
//    			String previousID = "";
//    			String[] prevQuery = null;
//    			while (scan.hasNextLine()) {
//    				String line = scan.nextLine();
//    				String[] data = line.split("\t");
//    				if (data.length > 1) {
//    					String id = data[0];
//    					String[] query = data[1].split("\\s");
////    					if (previousID.equals(id)) {
//    					if (prevQuery != null) {
//    						for (String word : query) {
////    							System.out.println("Checking suggestion for word "+word);
//    							if (!dictionary.contains(word)) {
//    								System.out.println("Dictionary doesn't contain "+word);
//    								for (String prevWord : prevQuery) {
//    									if (dictionary.contains(prevWord) && (getEditDistance(prevWord,word)==1 || getEditDistance(prevWord,word)==2)) {
//    										
//    										//If correction exists, make new map. Otherwise, ++
//										if (!correctedWords.containsKey(word) || !correctedWords.get(word).containsKey(prevWord)) {
//    											HashMap<String,Integer> corrections = new HashMap<String,Integer>();
//    											corrections.put(prevWord, 1);
//    											correctedWords.put(prevWord, corrections);
//    										} else {
//    											HashMap<String,Integer> corrections = correctedWords.get(word);
//    											corrections.put(prevWord, corrections.get(prevWord)+1);
//    											correctedWords.put(word, corrections);
//    											System.out
//														.println("Added correction "+prevWord+" for "+word);
//    										}
//    									}
//    								}
//    							}
//    						}
//    					}
//    					prevQuery = query;
//    					previousID = id;
//    				}
//    			}
    		}
    	}
    	
    	public String getSoundex(String word) {
    		char[] wordChars = word.toCharArray();
    		String soundex = "";
    		int strLen = word.length();
    		StringBuilder strB = new StringBuilder();
    		if (strLen>0) {
    			strB.append(word.substring(0,1).toUpperCase());
    		}
    		char[] hyphenLetters = {'a','e','i','o','u','y','h','w'};
    		for (int i=1; i<strLen; i++) {
    			for (int j=0; j<hyphenLetters.length; j++) {
    				if (wordChars[i]==hyphenLetters[j]) {
    					wordChars[i] = '-';
    				}
    			}
    		}
    		for (int i=1; i<strLen; i++) {
    			switch (wordChars[i]) {
    			case 'b':
    			case 'f':
    			case 'p':
    			case 'v':
    				wordChars[i]='1';
    				break;
    			case 'c':
    			case 'g':
    			case 'j':
    			case 'k':
    			case 'q':
    			case 's':
    			case 'x':
    			case 'z':
    				wordChars[i]='2';
    				break;
    			case 'd':
    			case 't':
    				wordChars[i]='3';
    				break;
    			case 'l':
    				wordChars[i]='4';
    				break;
    			case 'm':
    			case 'n':
    				wordChars[i]='5';
    				break;
    			case 'r':
    				wordChars[i]='6';
    				break;
    			}
    		}
    		char prev = 0;
    		int charCount = 0;
    		for (int i=1; charCount<3; i++) {
    			if (i>=strLen) {
    				strB.append('0');
    				charCount++;
    				continue;
    			}
    			if (!(prev == wordChars[i]) && wordChars[i]!='-') {
    				strB.append(wordChars[i]);
    				charCount++;
    			}
    			prev = wordChars[i];
    		}
    		return strB.toString();
    	}
    	
    	public String correctWord(String word, QueryCorrection qCorrection) {
    		String bestWord = word;
    		String soundex = getSoundex(word);
    		System.out.println("Word to correct: "+word);
    		System.out.println("Soundex code: "+soundex);
    		if (correctedWords.containsKey(word)) {
//    			ArrayList<String> correctionsList = new ArrayList<String>();
    			HashMap<String, Integer> corrections = correctedWords.get(word);
    			Double bestScore = 0.0;
    			for (String correction : corrections.keySet()) {
    				Double Pew = (double)corrections.get(correction) / 
    						correctWords.get(correction).correctionCount;
    				Double Pw = (double)indexer.getFrequency(correction)
    						/ indexer.getWordCount();
    				if (Pw==0) {
    					Pw = .00000001;
    				}
    				Double newScore = Pew * Pw;
//    				if (word.equals("screning") ) {
//    					System.out.println("Total correction count for "+correction+": "+correctWords.get(correction).correctionCount);
//    					System.out.println("Correction count: "+(double)corrections.get(correction));
//    					System.out.println("Indexed frequency: "+(double)indexer.getFrequency(correction));
//	    				System.out.println("Pew="+Pew);
//	    				System.out.println("Pw="+Pw);
//    				}
//    				System.out.print(correction+" ");
    				if (newScore > bestScore) {
    					bestScore = newScore;
    					bestWord = correction;
    				}
    			}
//    			System.out.println("best score: "+bestScore);
    		} else {
//    			if (!dictionary.contains(word)) {
//    				System.out.println("Dictionary doesn't contain "+word);
//    			}
    			for (String correctWord : dictionary) {
    				if (getEditDistance(word, correctWord) <= 2 && getSoundex(correctWord).equals(soundex)) {
//    					System.out.println("Found correct word: "+correctWord);
    					bestWord = correctWord;
    				}
    			}
//    			System.out.println("No suggestions for "+word);
    		}
    		System.out.print("Suggested corrections: ");
    		for (String correctWord : dictionary) {
				if (getEditDistance(word, correctWord) <= 2 && getSoundex(correctWord).equals(soundex)) {
					System.out.print(correctWord+" ");
				}
    		}
			System.out.print("\n");
    		return bestWord;
    	}
    	
    	public String correctQuery(String query) {
    		String[] queryWords = query.split("\\s");
    		String newQuery = "";
    		QueryCorrection qCorrection = new QueryCorrection();
    		qCorrection.origQuery = query;
    		for (String word : queryWords) {
    			if (dictionary.contains(word)) {
    				newQuery += word+" ";
    			} else {
    				newQuery += correctWord(word, qCorrection)+" ";
    			}
    		}
    		return newQuery;
    	}
    	
    	public TreeSet<String> checkDeletion(String word) {
    		TreeSet<String> newWords=new TreeSet<String>();
    		for (int i=0; i<word.length(); i++) {
    			StringBuilder newString=new StringBuilder(word);
    			newString.deleteCharAt(i);
				if (newString.toString()!=null && !newString.toString().equals("")) {
					newWords.add(newString.toString());
				}
    		}
    		return newWords;
    	}
    	
    	public TreeSet<String> checkTransposition(String word) {
    		TreeSet<String> newWords=new TreeSet<String>();
    		for (int i=1; i<word.length();i++) {
    			char[] wordArray=word.toCharArray();
    			char temp=wordArray[i-1];
    			wordArray[i-1]=wordArray[i];
    			wordArray[i]=temp;
    			String newString= new String(wordArray);
				if (newString!=null && !newString.equals("")) {
					newWords.add(newString);
				}
    		}
    		return newWords;
    	}
    	
        private static int minimum(int a, int b, int c) {                            
            return Math.min(Math.min(a, b), c);                                      
        }       
    	
    	//Levenshtein Distance
    	public int getEditDistance(String first, String second) {
    		char[] firstChars = first.toCharArray();
    		char[] secondChars = second.toCharArray();
    		int firstLen = firstChars.length+1;
    		int secondLen = secondChars.length+1;
//    		System.out.println("firstLen: "+firstLen);
//    		System.out.println("secondLen: "+secondLen);
    		int[][] distances = new int[firstLen][secondLen]; 
    		for (int i=0; i<firstLen; i++) {
    			distances[i][0] = i;
    		}
    		for (int j=0; j<secondLen; j++) {
    			distances[0][j] = j;
    		}
    		for (int i=1; i<firstLen; i++) {
    			for (int j=1; j<secondLen; j++) {
//    				int cost = 1;
//    				if (firstLen > i && secondLen > j && firstChars[i]==secondChars[j]) {
//    					cost = 0;
//    				}
    				distances[i][j] = minimum(distances[i-1][j]+1,distances[i][j-1]+1,distances[i-1][j-1]+
    						((firstChars[i-1]==secondChars[j-1]) ? 0 : 1));
    			}
    		}
    		return distances[firstLen-1][secondLen-1];
    	}
    	
    	public TreeSet<String> checkAlteration(String word) {
    		TreeSet<String> newWords=new TreeSet<String>();
    		for (int i=0; i<word.length(); i++) {
    			for (int j=0; j<26; j++) {
    				StringBuilder newBuilder=new StringBuilder(word);
    				String newString=newBuilder.replace(i, i+1, alphabet[j]).toString();
					if (newString!=null && !newString.equals("")) {
						newWords.add(newString);
					}
    			}
    		}
    		return newWords;
    	}
    	
    	
    	public TreeSet<String> checkInsertion(String word) {
    		TreeSet<String> newWords=new TreeSet<String>();
    		for (int i=0; i<=word.length(); i++) {
    			for (int j=0; j<26; j++) {
    				StringBuilder newBuilder=new StringBuilder(word);
    				String newString=newBuilder.insert(i, alphabet[j]).toString();
    				if (!newString.equals(null) && !newString.equals("")) {
    					newWords.add(newString);
    				}
    			}
    		}
    		return newWords;
    	}
    	
    	public TreeSet<String> getEditDistanceOne(String word) {
    		TreeSet<String> suggestions=new TreeSet<String>();
    		TreeSet<String> newSuggestions=checkDeletion(word);
    		if (!newSuggestions.isEmpty()) {
    			for (String it : newSuggestions) {
    				suggestions.add(it);
    			}
    		}
//    		newSuggestions=checkTransposition(word);
//    		if (!newSuggestions.isEmpty()) {
//    			for (String it : newSuggestions) {
//    				suggestions.add(it);
//    			}
//    		}
    		newSuggestions=checkAlteration(word);
    		if (!newSuggestions.isEmpty()) {
    			for (String it : newSuggestions) {
    				suggestions.add(it);
    			}
    		}
    		newSuggestions=checkInsertion(word);
    		if (!newSuggestions.isEmpty()) {
    			for (String it : newSuggestions) {
    				suggestions.add(it);
    			}
    		}
    		return suggestions;
    	}
    	
    	public String getBestWord(TreeSet<String> suggestions) {
    		String bestWord=null;
    		int bestFreq=0;
    		for (String it : suggestions) {
    			Trie.Node wordNode=trie.find(it);
    			if (wordNode!=null && !it.equals("") && ((MyTrie.ANode)wordNode).getValue()>bestFreq) {
    				bestWord=it;
    				bestFreq=((MyTrie.ANode)wordNode).getValue();
    			}
    		}
    		return bestWord;
    	}
    	
    	public TreeSet<String> getEditDistanceTwo(String word) {
    		TreeSet<String> allSuggestions = new TreeSet<String>();
    		TreeSet<String> suggestions=getEditDistanceOne(word);
			
    		TreeSet<String> secondSuggestions=new TreeSet<String>();
    		for (String it : suggestions) {
    			secondSuggestions=getEditDistanceOne(it);
    			allSuggestions.addAll(secondSuggestions);
    		}
			return allSuggestions;
    	}
    	
    	
    	public String suggestSimilarWord(String input) throws NoSimilarWordFoundException {
    		String inputWord=input.toLowerCase();
    		if (inputWord==null || inputWord.equals("")) {
    			throw new NoSimilarWordFoundException();
    		}
    		if (trie.find(inputWord)==null) {
    			TreeSet<String> suggestions=getEditDistanceOne(inputWord);
    			similarWord=getBestWord(suggestions);
    			if (similarWord==null || similarWord.equals("")) {
    				TreeSet<String> newBestWords=new TreeSet<String>();
    				TreeSet<String> secondSuggestions=new TreeSet<String>();
    				for (String it : suggestions) {
    					secondSuggestions=getEditDistanceOne(it);
    					String newWord=getBestWord(secondSuggestions);
    					if (newWord!=null) {
    						newBestWords.add(newWord);
    					}
    				}
    				similarWord=getBestWord(newBestWords);
    			}
    			if (similarWord==null || similarWord.equals("")) {
    				throw new NoSimilarWordFoundException();
    			}
    		} else {
    			similarWord=inputWord;
    		}
    		return similarWord;
    	}
    }