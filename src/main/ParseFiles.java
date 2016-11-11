package main;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;

import lang_model.LangModel;
import lexicon.LexMaker;

import shared.FileUtils;
import split_words.SplitWords;
import org.apache.commons.io.FilenameUtils;

public class ParseFiles {

	public static HashSet<String> makeDictionary(String dictpath) {
		String dictString = FileUtils.readFile(dictpath);
		ArrayList<String> words = FileUtils.getWords(dictString);
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
	
	public static void printDifferences(ArrayList<File> files_in, ArrayList<File> files_out) {
		for (int i=0; i<files_in.size(); i++) {
			String in_string = FileUtils.readFile(files_in.get(i).getAbsolutePath());
			String out_string = FileUtils.readFile(files_out.get(i).getAbsolutePath());
			
		}
	}
	
	//Write a dictionary from a language model file.
	public static void writeDict(String lmPath) {
		LangModel langModel = new LangModel(lmPath,1);
		String basename = FilenameUtils.removeExtension(lmPath);
//		langModel.write1GramDict(basename+"_lex.txt");
		langModel.writeClean1GramDict(basename+"_clean_lex.txt", 0);
	}
	
	//Write a lexicon/dictionary from a set of plain text files.
	public static void writeLexicon(ArrayList<File> files, String lexPath) {
		// Make lexicon
		LexMaker lexMaker = new LexMaker(files);
		lexMaker.writeLexicon(lexPath);
	}
	
	//Split words using a dictionary, appending ".split" to the output files.
	public static void splitWords(ArrayList<File> files, String dictPath, String outputLoc, String splitLogFile) {
		HashSet<String> dict = makeDictionary(dictPath);
		SplitWords splitWords = new SplitWords(dict, splitLogFile);
//		String wordToSplit = "2face-to-face";
//		System.out.println("split: "+wordToSplit+" to "+splitWords.splitWord(wordToSplit));
		for (File file : files) {
			if (file.getName().contains("split")) {
				continue;
			}
			PrintWriter writer = null;
			try {
				writer = new PrintWriter(outputLoc+File.separator+file.getName()+".split");
//				System.out.println(outputLoc+File.separator+file.getName()+".split");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}
//			List<String> lines = List<String> lines = Files.readAllLines(f));
			// Split words
			
			writer.write(splitWords.splitFileWords(file));
			writer.close();
			
		}
		splitWords.closeWriter();
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
//		writeDict("/home/joshuamonkey/498/lm/eng_20160927.arpa");
//		writeLexicon(files, "/home/joshuamonkey/498/lexicon"+File.separator+"lexicon.txt");
		String splitLogFile = "/home/joshuamonkey/498/log/split_words_log.txt";
		splitWords(files, "/home/joshuamonkey/498/lexicon/eng_20160927_lex.txt", outputLoc, splitLogFile);
		
	}

}
