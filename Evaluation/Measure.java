import java.util.ArrayList;
import java.util.List;


/**
 * Class used to compute Effectiveness Metrics
 * 
 * @author Adib
 */
public class Measure {
	
	/**
	 * Calculate the ratio of number of relevant documents that the 
	 * system succeeded to retrieve for a given query, to the number 
	 * of retrieved documents i.e. precision
	 * 
	 * @param relevance List of document relevance (sorted by rank)
	 * @return List of precision values
	 */
	static List<Double> getPrecision(List<Evaluate.Document> documents) {
		List<Double> precision = new ArrayList<Double>();
		double rank = 0;
		double relevantTillNow = 0;
		
		for (Evaluate.Document document : documents) {
			relevantTillNow += document.isRelevant() ? 1 : 0;
			rank++;
			precision.add(relevantTillNow / rank);
		}
		
		return precision;
	}
	
	/**
	 * Calculate the ratio of number of relevant documents that the 
	 * system succeeded to retrieve for a given query, to the number 
	 * of the relevant documents for that query
	 * 
	 * @param relevance List of document relevance (sorted by rank)
	 * @param relevantDocument The number of relevant documents
	 * @return List of recall values
	 */
	static List<Double> getRecall(List<Evaluate.Document> documents, int relevantDocument) {
		List<Double> recall = new ArrayList<Double>();
		double relevantTillNow = 0;
		
		for (Evaluate.Document document : documents) {
			relevantTillNow += document.isRelevant() ? 1 : 0;
			recall.add(relevantTillNow / (double) relevantDocument);
		}
		
		return recall;
	}
	
	/**
	 * Calculate the average of precision values where a relevant document 
	 * was retrieved   
	 * 
	 * @param documents List of document relevance (sorted by rank)
	 * @param relevantDocument The number of relevant documents
	 * @return Average Precision Value 
	 */
	static double getAveragePrecision(List<Evaluate.Document> documents, int relevantDocument) {
		double sum = 0;
		double rank = 0;
		double relevantTillNow = 0;
		
		for (Evaluate.Document document : documents) {
			rank++;
			if (document.isRelevant()) {
				relevantTillNow++;
				sum += (relevantTillNow / rank);
			}
		}
		
		return sum / relevantDocument;
	}
	
	/**
	 * Get the total accumulated for each document rank
	 * 
	 * @param documents List of document relevance (sorted by rank)
	 * @return List of DiscountedCumulativeGain values
	 */
	static List<Double> getDiscountedCumulativeGain(List<Evaluate.Document> documents) {
		List<Double> dcg = new ArrayList<Double>();
		
		if (documents.size() == 0) {
			return dcg;
		}
		
		double logTwo = Math.log(2);
		
		double[] discountedGain = new double[documents.size()]; 
		
		// First DG
		discountedGain[0] = documents.get(0).isRelevant() ? 1 : 0;
		
		// Calculate DG
		for (int i = 1; i < documents.size(); i++) {
			if (documents.get(i).isRelevant()) {
				discountedGain[i] = logTwo / Math.log(i + 1); 
			} else {
				discountedGain[i] = 0;
			}
		}
		
		dcg.add(discountedGain[0]);
		
		for (int i = 1; i < documents.size(); i++) {
			double prevDCG = dcg.get(i - 1);
			dcg.add(prevDCG + discountedGain[i]);
		}
		
		return dcg;
	}
	
	/**
	 * Get the total accumulated for each document rank (normalized) by 
	 * comparing it to perfect ranking
	 * 
	 * @param documents List of document relevance (sorted by rank)
	 * @param relevantDocument The number of relevant documents
	 * @return List of NormalizedDiscountedCumulativeGain values
	 */
	static List<Double> getNormalizedDiscountedCumulativeGain(List<Evaluate.Document> documents, int relevantDocument) {
		List<Double> ndcg = new ArrayList<Double>();
		
		List<Double> dcg = getDiscountedCumulativeGain(documents);
		
		// Create ideal ranking list
		List<Evaluate.Document> idealDocuments = new ArrayList<Evaluate.Document>();
		for (int i = 0; i < relevantDocument; i++) {
			idealDocuments.add(new Evaluate.Document("", true));
		}
		while (idealDocuments.size() != documents.size()) {
			idealDocuments.add(new Evaluate.Document("", false));
		}
		
		List<Double> idcg = getDiscountedCumulativeGain(idealDocuments); 
		
		for (int i = 0; i < documents.size(); i++) {
			ndcg.add(dcg.get(i) / idcg.get(i));
		}
		
		
		return ndcg;
	}
}
