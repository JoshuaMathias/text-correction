package lexicon;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import shared.FileUtils;

public class LexMaker {
	ArrayList<File> files;
	
	
	
	public LexMaker(ArrayList<File> files) {
		this.files = files;
	}
	
	public void writeLexicon(String out_filename) {
		HashSet<String> lexicon = new HashSet<String>();
		for (File file : files) {
//			System.out.println(file.getName());
			String text = FileUtils.readFile(file.getAbsolutePath());
			ArrayList<String> words = FileUtils.getWords(text);
			for (String word : words) {
				if (!lexicon.contains(word)) {
//					System.out.println("Added word: "+word);
					lexicon.add(word);
				}
			}
		}
		try {
			FileWriter writer = new FileWriter(out_filename);
//			System.out.println(out_filename);
			for (String word : lexicon) {
//				System.out.println("Wrote word: "+word);
				writer.write(word+"\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
