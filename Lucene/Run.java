import java.io.File;
import java.util.List;


/**
 * Main Class to run HW4
 * 
 * @author Adib
 */
public class Run {

	// File Constants
	private static final String INDEX_FILE_NAME = "index";
	private static final String CORPUS_FILE_NAME = "cacm";
	private static final String TERM_FREQUENCY_FILE_NAME = "term_frequency.txt";
	private static final String CORPUS_NO_TAGS_FILE_NAME = "cacm_no_tags";
	private static final String RESULT_FILE_NAME = "results.txt";
	
	// Query Constants
	private static final String QUERY_1 = "portable operating systems";
	private static final String QUERY_2 = "code optimization for space efficiency";
	private static final String QUERY_3 = "parallel algorithms";
	private static final String QUERY_4 = "parallel processor in information retrieval";
	private static final int DOCS_TO_RETRIEVE = 100;
	
	// Corpus Length
	private static final int CORPUS_LENGTH;
	
	// Calculate number of files in corpus
	static {
		CORPUS_LENGTH = new File(CORPUS_FILE_NAME).list().length;
	}
	
	/**
	 * Delete the given directory and its sub-directories and files
	 * 
	 * @param file The directory to be deleted
	 */
	private static void deleteDirectory(File file) {
		if (file.exists()) {
			File[] files = file.listFiles();

			for (File f : files) {
				if (f.isDirectory()) {
					deleteDirectory(f);
				} else {
					f.delete();
				}
			}
		}

		file.delete();
	}

	public static void main(String[] args) {
		
		// Delete existing query result file
		File file = new File(RESULT_FILE_NAME);
		file.delete();
        
		// Delete existing index
		deleteDirectory(new File(INDEX_FILE_NAME));
		
		// Delete existing parsed corpus
		deleteDirectory(new File(CORPUS_NO_TAGS_FILE_NAME));

		// Create an index for given corpus
		Index.removeHTMLTags(CORPUS_FILE_NAME, CORPUS_NO_TAGS_FILE_NAME);
		Index index = new Index(INDEX_FILE_NAME);
		index.indexFileOrDirectory(CORPUS_NO_TAGS_FILE_NAME);
		index.closeIndex();

		// Print Term-Frequency pair
		List<Retrieve.TermFrequencyPair> termFrequencyPairs = Retrieve.getUniqueTermFrequency(INDEX_FILE_NAME);
		Printer.printTermFrequencyPair(TERM_FREQUENCY_FILE_NAME, termFrequencyPairs);
		
		// Plot Zipfian curve
		List<Retrieve.RankProbabilityPair> rankProbabilityPairs = Retrieve.getRankProbability(termFrequencyPairs);
		
		int len = rankProbabilityPairs.size();
		int arrayIndex = 0;
		double[] x = new double[len];
		double[] y = new double[len];
		
		for (Retrieve.RankProbabilityPair pair : rankProbabilityPairs) {
			x[arrayIndex] = pair.getRank();
			y[arrayIndex] = pair.getProbability();
			arrayIndex++;
		}
		
		PlotGraph.plotGraph(x, y);
		
		// Search for queries
		List<Retrieve.DocumentScorePair> documentScorePairs = Retrieve.searchQuery(INDEX_FILE_NAME, QUERY_1, DOCS_TO_RETRIEVE);
		Printer.printQueryResult(QUERY_1, RESULT_FILE_NAME, documentScorePairs);
		
		documentScorePairs = Retrieve.searchQuery(INDEX_FILE_NAME, QUERY_2, DOCS_TO_RETRIEVE);
		Printer.printQueryResult(QUERY_2, RESULT_FILE_NAME, documentScorePairs);
		
		documentScorePairs = Retrieve.searchQuery(INDEX_FILE_NAME, QUERY_3, DOCS_TO_RETRIEVE);
		Printer.printQueryResult(QUERY_3, RESULT_FILE_NAME, documentScorePairs);
		
		documentScorePairs = Retrieve.searchQuery(INDEX_FILE_NAME, QUERY_4, DOCS_TO_RETRIEVE);
		Printer.printQueryResult(QUERY_4, RESULT_FILE_NAME, documentScorePairs);
		
		// Print maximum number of matches for each query
		documentScorePairs = Retrieve.searchQuery(INDEX_FILE_NAME, QUERY_1, CORPUS_LENGTH);
		System.out.println("Total Number of docs retrieved for \"" + QUERY_1 +"\" is " + documentScorePairs.size());
		
		documentScorePairs = Retrieve.searchQuery(INDEX_FILE_NAME, QUERY_2, CORPUS_LENGTH);
		System.out.println("Total Number of docs retrieved for \"" + QUERY_2 +"\" is " + documentScorePairs.size());
		
		documentScorePairs = Retrieve.searchQuery(INDEX_FILE_NAME, QUERY_3, CORPUS_LENGTH);
		System.out.println("Total Number of docs retrieved for \"" + QUERY_3 +"\" is " + documentScorePairs.size());
		
		documentScorePairs = Retrieve.searchQuery(INDEX_FILE_NAME, QUERY_4, CORPUS_LENGTH);
		System.out.println("Total Number of docs retrieved for \"" + QUERY_4 +"\" is " + documentScorePairs.size());
	}

}
