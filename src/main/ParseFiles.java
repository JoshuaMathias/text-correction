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

	//Make a list of words from a file.
	//Doesn't include one letter words other than "a" and "i"
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
	
	//Make a list of words with likelihoods from a file.
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
//				System.out.println("word: "+word+" prob: "+Double.parseDouble(splitLine[1]));
				dict.put(word, Double.parseDouble(splitLine[1]));
				line = dictReader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Made probs dictionary from "+dictpath);
		return dict;
	}
	
	public static void printDifferences(ArrayList<File> files_in, ArrayList<File> files_out) {
		for (int i=0; i<files_in.size(); i++) {
			String in_string = FileUtils.readFile(files_in.get(i).getAbsolutePath());
			String out_string = FileUtils.readFile(files_out.get(i).getAbsolutePath());
			
		}
	}
	
	//Write a dictionary from a language model file.
	public static void writeDict(String lmPath, Double cutoff) {
		LangModel langModel = new LangModel(lmPath,1);
		String basename = FilenameUtils.removeExtension(lmPath);
//		langModel.write1GramDict(basename+"_lex.txt");
//		langModel.writeClean1GramDict(basename+"_clean_lex.txt");
		String dictName = basename+"_clean_lex_probs_cutoff.txt";
		System.out.println("Writing dictionary to "+dictName);
		langModel.writeGram(1, dictName, cutoff, true);
	}
	
	//Write a lexicon/dictionary from a set of plain text files.
	public static void writeLexicon(ArrayList<File> files, String lexPath) {
		// Make lexicon
		LexMaker lexMaker = new LexMaker(files);
		lexMaker.writeLexicon(lexPath);
	}

	
	/**
	 * @param args
	 * 0: inFilename - The directory or file to be processed (if a directory, all files in the directory will be processed).
	 * 1: outputLoc - The directory to write the corrected files.
	 * 2: 
	 */
	public static void main(String[] args) {
		ArrayList<File> files = null;
		String inFilename = "";
		String outputLoc = "";
		String dictPath = "";
		if (args.length > 2) {
			inFilename = args[0];
			files = FileUtils.listFiles(new File(inFilename));
			outputLoc = args[1];
			dictPath = args[2];
		} else {
			System.out.println("USAGE: INPUT_FILE_PATH OUTPUT_DIRECTORY DICTIONARY_FILE");
			return;
		}
		if (files.size() == 0) {
			System.out.println("No files found.");
			return;
		}

//		LangModel lm = new LangModel("eng_20160927.arpa",1);
//		writeDict("eng_20160927.arpa", -6.5);
		String splitLogFile = "split_words_probs_log_cutoff_test.txt";
		HashMap<String, Double> dict = makeProbsDictionary(dictPath);
		SplitWords splitWords = new SplitWords(dict, splitLogFile);
		
		for (File file : files) {
			
			if (file.getName().contains("split")) {
				continue;
			}
			PrintWriter writer = null;
			String fileStr = "";
			try {
				fileStr = FileUtils.readFile(file.getAbsolutePath());
				writer = new PrintWriter(outputLoc+File.separator+file.getName()+".out");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			String correctedText = fileStr;
			correctedText = ParseUtils.rmSpecialChars(correctedText);
			correctedText = splitWords.splitWords(correctedText);
			correctedText = ParseUtils.rmCode(correctedText);
			correctedText = ParseUtils.rmBlankSpace(correctedText);
			writer.write(correctedText);
			writer.close();
		}
	}
	
	

}
