package main;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import lang_model.LangModel;
import lexicon.LexMaker;

import shared.FileUtils;
import shared.ParseUtils;
import split_words.SplitWords;
import org.apache.commons.io.FilenameUtils;

public class ParseFiles {

	public static HashSet<String> makeDictionary(String dictpath) {
		String dictString = FileUtils.readFile(dictpath);
		ArrayList<String> words = ParseUtils.getWords(dictString);
//		Trie dict = new Trie();
		HashSet<String> dict = new HashSet<String>();
		for (String word : words) {
			word = word.toLowerCase();
			if (word.length() <= 1 && !word.equals("a") && !word.equals("i")) {
				continue;
			}
//			String test = "sa";
//			if (word.equals(test)) {
////				System.out.println("found "+test);
//			}
//			System.out.println("Adding word: "+word);
			dict.add(word);
		}
		return dict;
	}
	
	public static HashMap<String, Double> makeProbsDictionary(String dictpath) {
		BufferedReader dictReader = FileUtils.getLineReader(dictpath);
		HashMap<String, Double> dict = new HashMap<String, Double>();
		try {
			String line = dictReader.readLine();
			while (line != null) {
//				System.out.println(line);
				String[] splitLine = line.split("\\s");
				if (splitLine.length<2) {
					line = dictReader.readLine();
					continue;
				}
				String word = splitLine[0];
				if (word.length() <= 1 && !word.equals("a") && !word.equals("i")) {
					line = dictReader.readLine();
					continue;
				}
//				System.out.println("prob: "+Double.parseDouble(splitLine[1]));
				dict.put(word, Double.parseDouble(splitLine[1]));
				line = dictReader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Make probs dictionary");
		return dict;
	}
	
	public static void printDifferences(ArrayList<File> files_in, ArrayList<File> files_out) {
		for (int i=0; i<files_in.size(); i++) {
			String in_string = FileUtils.readFile(files_in.get(i).getAbsolutePath());
			String out_string = FileUtils.readFile(files_out.get(i).getAbsolutePath());
			
		}
	}
	
	//Write a dictionary from a language model file.
	public static void writeDict(String lmPath) {
		LangModel langModel = new LangModel(lmPath,1);
		String basename = FilenameUtils.removeExtension(FilenameUtils.getName(lmPath));
//		langModel.write1GramDict(basename+"_lex.txt");
//		langModel.writeClean1GramDict(basename+"_clean_lex.txt");
		String dictName = "/home/joshuamonkey/498/lexicon/"+basename+"_clean_lex_probs_cutoff_6-5.txt";
		System.out.println("Writing dictionary to "+dictName);
		langModel.writeGram(1, dictName, -6.5, true);
	}
	
	//Write a lexicon/dictionary from a set of plain text files.
	public static void writeLexicon(ArrayList<File> files, String lexPath) {
		// Make lexicon
		LexMaker lexMaker = new LexMaker(files);
		lexMaker.writeLexicon(lexPath);
	}
	
	//Split words using a dictionary, appending ".split" to the output files.
	public static String splitWords(String text, String dictPath, String outputLoc, String splitLogFile) {
//		HashSet<String> dict = makeDictionary(dictPath);
		HashMap<String, Double> dict = makeProbsDictionary(dictPath);
		SplitWords splitWords = new SplitWords(dict, splitLogFile);
//		String wordToSplit = "2face-to-face";
//		System.out.println("split: "+wordToSplit+" to "+splitWords.splitWord(wordToSplit));
//		for (File file : files) {
//			if (file.getName().contains("split")) {
//				continue;
//			}
//			PrintWriter writer = null;
//			try {
//				writer = new PrintWriter(outputLoc+File.separator+file.getName()+".split");
////				System.out.println(outputLoc+File.separator+file.getName()+".split");
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//				return "";
//			}
//			List<String> lines = List<String> lines = Files.readAllLines(f));
			// Split words
			String correctedText = splitWords.splitWords(text);
			return correctedText;
//			writer.write(correctedText);
//			writer.close();
			
//		}
//		splitWords.closeWriter();
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<File> files = null;
		String inFilename = "";
		if (args.length > 0) {
			inFilename = args[0];
			files = FileUtils.listFiles(new File(inFilename));
		} else {
			System.out.println("No file path entered.");
			return;
		}
		if (files.size() == 0) {
			System.out.println("No files found.");
			return;
		}
		String outputLoc = "";
		if (args.length > 1) {
			outputLoc = args[1];
		} else {
			outputLoc = args[0];
		}
//		PrintWriter writer = null;
//		try {
//			writer = new PrintWriter("/home/joshuamonkey/498/all_caps_lm_1-0.txt");
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		ParseUtils.writeCapsExamples(files, writer);
//		writer.close();
//		LangModel lm = new LangModel("/home/joshuamonkey/498/lm/eng_20160927.arpa",1);
//		lm.printWordProbs("LDS");
		String regex = "<[^>\n]*>||([^.@\\s]+)(\\.[^.@\\s]+)*@([^.@\\s]+\\.)+([^.@\\s]+)||((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)||Off(Off)+||_(_)+";
		regex = "(^|\\s)\\p{Lu}[^\\s$]*\\p{Lu}[^\\s$]*";
		ParseUtils.printNumCategories(files, regex);
//		ParseUtils.printNumInstances(files, regex);
//		ParseUtils.printBlankFiles(files);
//		ParseUtils.printSmallFiles(files);
		String splitLogFile = "/home/joshuamonkey/498/log/split_words_probs_log_cutoff_test.txt";
		String dictPath = "/home/joshuamonkey/498/lexicon/eng_20160927_clean_lex_probs_cutoff_6-5.txt";
//		HashMap<String, Double> dict = makeProbsDictionary(dictPath);
//		ParseUtils.writeUnknownWords(files, dict);
//		SplitWords splitWords = new SplitWords(dict, splitLogFile);
		
//		for (File file : files) {
//			
//			if (file.getName().contains("split")) {
//				continue;
//			}
//			PrintWriter writer = null;
//			String fileStr = "";
//			try {
////				BufferedReader reader = FileUtils.getLineReader(file.getAbsolutePath());
//				fileStr = FileUtils.readFile(file.getAbsolutePath());
//				writer = new PrintWriter(outputLoc+File.separator+file.getName()+".out");
////				System.out.println(outputLoc+File.separator+file.getName()+".split");
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}
//			String correctedText = fileStr;
////			correctedText = ParseUtils.rmSpecialChars(correctedText);
////			correctedText = ParseUtils.rmCode(correctedText);
////			writeDict("/home/joshuamonkey/498/lm/eng_20160927.arpa");
////			writeLexicon(files, "/home/joshuamonkey/498/lexicon"+File.separator+"lexicon.txt");
////			(correctedText, "/home/joshuamonkey/498/lexicon/eng_20160927_clean_lex_probs_cutoff_6-5.txt", outputLoc, splitLogFile);
//			correctedText = splitWords.splitWords(correctedText);
////			correctedText = splitWords.splitWords(correctedText);
////			correctedText = ParseUtils.rmBlankSpace(correctedText);
////			writer.write(correctedText);
//			writer.close();
//		}
	}
	
	

}
