import java.util.HashMap;
import java.util.Map;

/**
 * Class used for calculating the BM25 for a given query on 
 * the given inverted index
 * 
 * @author Adib
 */
public class BM25Algorithm {

	/*
	 * Map from Token to a map from document id to term frequency
	 */
	private final Map<String, Map<Integer, Integer>> invertedIndex;
	
	/*
	 * Map from document id to number of token's in that document
	 */
	private final Map<Integer, Integer> tokenCount;
	
	//BM25 Constants
	private final double k1;
	private final double k2;
	private final double b;
	
	// Average Document length
	private final double avdl;
	
	// Corpus Size
	private final int N;
	
	/**
	 * Initialize BM25 Algorithm 
	 * 
	 * @param invertedIndex Map from Token to a map from document id to term frequency
	 * @param tokenCount Map from document id to number of token's in that document
	 */
	public BM25Algorithm(Map<String, Map<Integer, Integer>> invertedIndex,
			Map<Integer, Integer> tokenCount, double k1, double k2, double b) {
		this.invertedIndex = invertedIndex;
		this.tokenCount = tokenCount;
		this.k1 = k1;
		this.k2 = k2;
		this.b = b;
		
		N = tokenCount.size();
		int sum = 0;
		
		for (int count : tokenCount.values()) {
			sum += count; 
		}
		
		avdl = sum / (double) N;
	}
	
	/**
	 * Run an instance of BM25 Algorithm for the query on the given corpus
	 * 
	 * @param query The query on which to rank documents
	 * @return Map from document id to its BM25 score
	 */
	public Map<Integer, Double> runBM25Algorithm(String query) {
		
		Map<Integer, Double> documentRank = new HashMap<Integer, Double>();
		Map<String, Integer> queryTermCount = new HashMap<String, Integer>();
		
		query = query.toLowerCase();
		String[] words = query.split("\\s");
		
		// Initialize Query Term Map
		for (String word : words) {
			Integer termCount = queryTermCount.get(word);
			queryTermCount.put(
				word,
				termCount == null ? 1 : termCount + 1
			);
		}
		
		// For each unique query term calculate BM25
		for (Map.Entry<String, Integer> entry : queryTermCount.entrySet()) {
			
			String word = entry.getKey();
			
			if (!invertedIndex.containsKey(word)) {
				continue;
			}
			
			int n = invertedIndex.get(word).size();
			int qf = entry.getValue();
			
			for (Map.Entry<Integer, Integer> documentEntry : invertedIndex.get(word).entrySet()) {
				
				int documentNumber = documentEntry.getKey();
				int dl = tokenCount.get(documentNumber);
				int f = documentEntry.getValue();
				
				double K = k1 * ((1 - b) + (b * dl / avdl));
				double documentWeight = (k1 + 1) * f / (K + f);
				double queryWeight = (k2 + 1) * qf / (k2 + qf);
				
				double score = Math.log((N - n + 0.5) / (n + 0.5)) * queryWeight * documentWeight;
				
				Double currentScore = documentRank.get(documentNumber); 
				documentRank.put(
					documentNumber,
					currentScore == null ? score : currentScore + score
				);
			}
		}
		
		return documentRank;
	}
	
}
