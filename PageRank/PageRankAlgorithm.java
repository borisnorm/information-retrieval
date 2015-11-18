import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class used for calculating the Page Rank for a given web graph
 * 
 * @author Adib
 */
public class PageRankAlgorithm {
	
	// Constants for Page Rank
	private final static double DAMPING_FACTOR = 0.85;
	private final static double INVERSE_DAMPING_FACTOR;
	private final static int CONSECUTIVE_ITERATION_COUNT = 4;
	private final static double LOG_BASE_TWO = Math.log(2);
	private final ArrayList<Double> perplexity;
	
	// The number of iterations that has passed since the change in
	// perplexity is less than 1
	private int convergenceCount;
	
	private HashMap<String, Double> pageRank;
	private HashMap<String, Double> initPageRank;
	
	static {
		INVERSE_DAMPING_FACTOR = 1 - DAMPING_FACTOR;
	}
	
	public PageRankAlgorithm() {
		perplexity = new ArrayList<Double>();
		pageRank = new HashMap<String, Double>();
		initPageRank = new HashMap<String, Double>();
	}
	
	/**
	 * Calculate the page rank for the given web-graph
	 * 
	 * @param graph The web graph
	 * @return Page along with its page-rank
	 */
	public HashMap<String, Double> calculatePageRank(WebGraph graph) {
		
		// Null Check
		if (graph == null) {
			return null;
		}
		
		int numberOfPages = graph.getNumberOfPages();
		HashMap<String, Double> pageRank = new HashMap<String, Double>();
		
		// Initial Value
		double initPageRankValue = 1 / (double) numberOfPages;
		for (String page : graph.getAllPages()) {
			pageRank.put(page, initPageRankValue);
		}
		
		// Copy page rank to Init Page Rank
		for (Map.Entry<String, Double> entry : pageRank.entrySet()) {
			initPageRank.put(entry.getKey(), entry.getValue());
		}
		
		perplexity.add(calculatePerplexity(pageRank));
		
		// Page Rank iteration
		do {
			double sinkPR = 0;
			
			// Calculate total sink PR
			for (String page : graph.getSinkPages()) {
				sinkPR += pageRank.get(page);
			}
			
			HashMap<String, Double> newPageRank = new HashMap<String, Double>();
			
			for (String page : graph.getAllPages()) {
				
				// Teleportation
				double rank = INVERSE_DAMPING_FACTOR / (double) numberOfPages;
				
				// Spread remaining sink PR evenly
				rank += (DAMPING_FACTOR * sinkPR) / (double) numberOfPages;
				
				// Pages pointing to p
				for (String inLink : graph.getInLinks(page)) {
					
					// Add share of page rank from in-links
					rank += (DAMPING_FACTOR * pageRank.get(inLink)) / 
							(double) graph.getNumberOfOutLinks(inLink);
				}
				
				newPageRank.put(page, rank);
			}
			
			pageRank = newPageRank;
			
		} while (!isConverged(pageRank));
		
		this.pageRank = pageRank;
		return pageRank;
	}
	
	/**
	 * Check whether the convergence of page rank is achieved or not
	 * 
	 * @param pageRank The given page rank for a web graph
	 * @return true iff the convergence is achieved. False, otherwise
	 */
	private boolean isConverged(HashMap<String, Double> pageRank) {
		if (pageRank == null) {
			return true;
		}
		
		double currentPerplexity = calculatePerplexity(pageRank);
		double oldPerplexity = perplexity.get(perplexity.size() - 1);
		
		// Difference is less than 1
		if (Math.abs(currentPerplexity - oldPerplexity) < 1) {
			convergenceCount++;
			
			if (convergenceCount == CONSECUTIVE_ITERATION_COUNT) {
				return true;
			}
			
		} else {
			convergenceCount = 0;
		}
		
		perplexity.add(currentPerplexity);
		
		return false;
	}
	
	/**
	 * Calculate the perplexity given by:
	 * 2 raised to shannon's entropy 2 ^ H(PR)
	 * 
	 * Shannon's entropy is given by:
	 * H(PR) = -sum(PR(i) * log(PR(i))) for all i = 0 to N - 1
	 * 
	 * @param pageRank The page rank for a web graph
	 * @return The perplexity
	 */
	private double calculatePerplexity(HashMap<String, Double> pageRank) {
		double entropy = 0;
		
		for (Double rank : pageRank.values()) {
			entropy += rank * Math.log(rank) / LOG_BASE_TWO;
		}
		
		entropy *= -1;
		
		return Math.pow(2, entropy);
	}
	
	/**
	 * Return the perplexity value calculated by each page rank iteration
	 * 
	 * @return The perplexities
	 */
	public ArrayList<Double> getPerplexity() {
		return perplexity;
	}
	
	/**
	 * Return number pages whose page rank is less than their initial, uniform value  
	 * 
	 * @return Number of degraded pages 
	 */
	public int getNumberOfDegradedPages() {
		int count = 0;
		
		for (Map.Entry<String, Double> entry : pageRank.entrySet()) {
			
			String page = entry.getKey();
			double pageRank = entry.getValue();
			
			// page rank is less than their initial, uniform value
			if (pageRank < initPageRank.get(page)) {
				count++;
			}
			
		}
		
		return count;
	}
	
}
