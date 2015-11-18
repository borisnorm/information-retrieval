import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;


/**
 * Load index and rank documents for given queries
 * 
 * @author Adib
 */
public class BM25 {
	
	private static final String RESULT_FILE = "results.eval"; 
	
	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("Format: <IndexFile> <QueriesFile> <DesiredNumberOfResults>");
			return;
		}
		
		String indexFile = args[0];
		String queryFile = args[1];
		int top = Integer.parseInt(args[2]);
		
		// Load index and get the inverted index
		Index index = new Index();
		index.loadIndex(indexFile);
		Map<String, Map<Integer, Integer>> invertedIndex = index.getInvertedIndex();
		Map<Integer, Integer> tokenCount = index.getTokenCount();
		
		// Run BM25 on each query
		BM25Algorithm algorithm = new BM25Algorithm(invertedIndex, tokenCount, 1.2, 100, 0.75);
		try (
			FileReader fileReader = new FileReader(queryFile);
			BufferedReader reader = new BufferedReader(fileReader);
		) {
			
			String query;
			int queryId = 1;
			File file = new File(RESULT_FILE);
			file.delete();
			
			while ((query = reader.readLine()) != null) {
				Map<Integer, Double> score = algorithm.runBM25Algorithm(query);
				Printer.printSortedScore(RESULT_FILE, score, top, queryId);
				queryId++;
			}
			
		} catch (IOException exception) {
			
			System.out.println("Unable to read from input file");
			exception.printStackTrace();
			
		}
	}
}
