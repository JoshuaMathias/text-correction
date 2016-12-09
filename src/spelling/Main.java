package spelling;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String path = "To_be_posted/";
		if (args.length > 0) {
			path = args[0];
		}
		String firstOut = "first.csv";
		String secondOut = "second.csv";
		if (args.length > 1) {
			firstOut = args[1];
		}
		if (args.length > 2) {
			secondOut = args[2];
		}
		System.out.println(path);
		System.out.println("Writing to "+firstOut+" and "+secondOut);
		Indexer indexer = new Indexer(path);
		try {
			Writer vocabWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("vocabSizes.csv"), "utf-8"));
			final CSVPrinter printerVocab = new CSVPrinter(vocabWriter,CSVFormat.DEFAULT);
			indexer.printVocabSizes(printerVocab);
			vocabWriter.close();
			printerVocab.close();
//			Writer writer = new BufferedWriter(new OutputStreamWriter(
//					new FileOutputStream(firstOut), "utf-8"));
//			final CSVPrinter printer = new CSVPrinter(writer,CSVFormat.DEFAULT);
//			indexer.printWords(printer);
//			writer = new BufferedWriter(new OutputStreamWriter(
//					new FileOutputStream(secondOut), "utf-8"));
//			final CSVPrinter printer2 = new CSVPrinter(writer,CSVFormat.DEFAULT);
//			indexer.printBigrams(printer2);
//			writer = new BufferedWriter(new OutputStreamWriter(
//					new FileOutputStream("both.csv"), "utf-8"));
//			final CSVPrinter printer3 = new CSVPrinter(writer,CSVFormat.DEFAULT);
//			indexer.printBoth(printer3);
//			printer.print("Search Results");
//			printer.println();
//			for (int i=2; i<args.length; i++) {
//				Query query = new Query(indexer,args[i]);
//				query.printResults(printer);
//			}
//			printer.close();
//			writer.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
