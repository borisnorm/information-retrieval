import java.util.Map;

/**
 * Create and build index for the given input corpus
 * 
 * @author Adib
 */
public class Indexer {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Format: <Corpus> <IndexFile>");
			return;
		}
		
		String inputCorpus = args[0];
		String outputFile = args[1];
		
		Index index = new Index();
		index.buildIndex(inputCorpus);
		Map<String, Map<Integer, Integer>> invertedIndex = index.getInvertedIndex();
		Printer.printIndex(outputFile, invertedIndex);
	}
}
