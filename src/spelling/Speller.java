package spelling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import spelling.SpellCorrector.NoSimilarWordFoundException;

public class Speller {
	HashMap<String, Double> dictionary;
	String[] alphabet=new String[]{"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
	
	public Speller(HashMap<String, Double> dictionary) {
		this.dictionary = dictionary;
	}
	
//	public String getSuggestion(String word) {
//		String bestSuggestion = null;
//		if (dictionary.containsKey(word)) {
//			return null;
//		}
//		TreeSet<String> suggestions = getEditDistanceTwo(word);
//		return bestSuggestion;
//	}
	
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
    
	//Levenshtein Distance
	public int getEditDistance(String first, String second) {
		char[] firstChars = first.toCharArray();
		char[] secondChars = second.toCharArray();
		int firstLen = firstChars.length+1;
		int secondLen = secondChars.length+1;
//		System.out.println("firstLen: "+firstLen);
//		System.out.println("secondLen: "+secondLen);
		int[][] distances = new int[firstLen][secondLen]; 
		for (int i=0; i<firstLen; i++) {
			distances[i][0] = i;
		}
		for (int j=0; j<secondLen; j++) {
			distances[0][j] = j;
		}
		for (int i=1; i<firstLen; i++) {
			for (int j=1; j<secondLen; j++) {
//				int cost = 1;
//				if (firstLen > i && secondLen > j && firstChars[i]==secondChars[j]) {
//					cost = 0;
//				}
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
//		newSuggestions=checkTransposition(word);
//		if (!newSuggestions.isEmpty()) {
//			for (String it : newSuggestions) {
//				suggestions.add(it);
//			}
//		}
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
//	
	public String getBestWord(TreeSet<String> suggestions) {
		String bestWord=null;
		Double bestScore=0.0;
		String bestSoundex = null;
		for (String word : suggestions) {
//			Trie.Node wordNode=trie.find(it);
//			if (wordNode!=null && !it.equals("") && ((MyTrie.ANode)wordNode).getValue()>bestFreq) {
//				bestWord=it;
//				bestFreq=((MyTrie.ANode)wordNode).getValue();
//			}
			if (bestScore < dictionary.get(word)) {
				String soundex = getSoundex(word);
				if (bestSoundex != null) {
					if (soundex.equals(bestSoundex)) {
						bestWord = word;
						bestSoundex = soundex;
						bestScore = dictionary.get(word);
					}
				} else {
					bestWord = word;
					bestSoundex = soundex;
					bestScore = dictionary.get(word);
				}
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
	
	
	public String suggestSimilarWord(String input) {
		String inputWord=input.toLowerCase();
		String similarWord = null;
//		if (inputWord==null || inputWord.equals("")) {
//			throw new NoSimilarWordFoundException();
//		}
		if (!dictionary.containsKey(inputWord)) {
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
//			if (similarWord==null || similarWord.equals("")) {
//				throw new NoSimilarWordFoundException();
//			}
		} else {
			similarWord=inputWord;
		}
		return similarWord;
	}
}
