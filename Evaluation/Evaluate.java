import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * Main class to run HW5
 * 
 * @author Adib
 */
public class Evaluate {

	// File Constants
	private static final String QUERY_1_FILE_NAME = "portable_operating_system_results.txt";
	private static final String QUERY_2_FILE_NAME = "code_optimization_for_space_efficiency_results.txt";
	private static final String QUERY_3_FILE_NAME = "parallel_algorithms_results.txt";
	private static final String RELEVANCE_JUDGEMENT_FILE_NAME = "cacm.rel";
	private static final String QUERY_1_TABLE_NAME = "portable_operating_system_table.txt";
	private static final String QUERY_2_TABLE_NAME = "code_optimization_for_space_efficiency_table.txt";
	private static final String QUERY_3_TABLE_NAME = "parallel_algorithms_table.txt";
	
	// Query Id Constants
	private static final String QUERY_1_ID = "12";
	private static final String QUERY_2_ID = "13";
	private static final String QUERY_3_ID = "19";
	
	/**
	 * Class representing document relevance:
	 * 1) Rank
	 * 2) FileName
	 * 3) Ranking Algorithm Score
	 * 4) Boolean Relevance
	 * 
	 * @author Adib
	 */
	static class Document implements Comparable<Document>{
		private final int rank;
		private final String fileName;
		private final double rankingScore;
		private boolean relevant;
		
		public Document(String fileName, boolean relevant) {
			this.relevant = relevant;
			this.fileName = fileName;
			rank = -1;
			rankingScore = 0;
		}
		
		public Document(int rank, String fileName, double rankingScore) {
			this.rank = rank;
			this.fileName = fileName;
			this.rankingScore = rankingScore;
		}

		@Override
		public int compareTo(Document document) {
			if (relevant && !document.isRelevant()) {
				return -1;
			} else if (!relevant && document.isRelevant()){
				return 1;
			} else {
				return 0;
			}
		}
		
		@Override
		public boolean equals(Object obj) {
			
			if (obj instanceof Document) {
				
				Document document = (Document) obj;
				
				int id1 = Integer.parseInt((fileName.split("-"))[1]);
				int id2 = Integer.parseInt((document.getFileName().split("-"))[1]);
				
				if (id1 == id2) {
					return true;
				}
			}
			
			return false;
		}
		
		public int getRank() {
			return rank;
		}

		public String getFileName() {
			return fileName;
		}

		public double getRankingScore() {
			return rankingScore;
		}

		public boolean isRelevant() {
			return relevant;
		}

		public void setRelevant(boolean relevant) {
			this.relevant = relevant;
		}
	}
	
	/**
	 * Compare Documents based on Rank
	 * 
	 * @author Adib
	 */
	static class RankComparator implements Comparator<Document> {
		@Override
		public int compare(Document d1, Document d2) {
			if (d1.getRank() > d2.getRank()) {
				return 1;
			} else if (d1.getRank() < d2.getRank()) {
				return -1;
			} else {
				return 0;
			}
		}
	}
	
	/**
	 * Get the document rank, fileName and ranking score
	 * 
	 * @param fileName The name of the results file
	 * @return List of Document containing those information 
	 */
	private static List<Document> getDocument(String fileName) {
		List<Document> documents = new ArrayList<Document>();
		
		try (
			FileReader fileReader = new FileReader(fileName);
			BufferedReader reader = new BufferedReader(fileReader);
		) {
			
			String currentLine;
			while ((currentLine = reader.readLine()) != null) {
				
				String[] tokens = currentLine.split("\\s");
				
				// Remove . from rank
				tokens[0] = tokens[0].substring(0, tokens[0].length() - 1);
				
				// Remove .html from string
				tokens[1] = tokens[1].substring(0, tokens[1].length() - 5);
				
				int docRank = Integer.parseInt(tokens[0]);
				String docFileName = tokens[1];
				double docScore = Double.parseDouble(tokens[2]);
				
				Document document = new Document(docRank, docFileName, docScore);
				documents.add(document);
				
			}
			
		} catch (IOException exception) {
			
			System.out.println("Unable to read from " + fileName);
			exception.printStackTrace();
			
		}
		
		return documents;
	}
	
	/**
	 * Get the document relevance for the given queries 
	 * 
	 * @param fileName The name of the relevance judgement file
	 * @param queryIds The query-id whole relevance is to be found
	 * @return List of documents for corresponding query-id
	 */
	private static List<List<Document>> getRelevantJudgement(String fileName, List<String> queryIds) {
		List<List<Document>> documents = new ArrayList<List<Document>>();
		
		// Initialize list
		for (int i = 0; i < queryIds.size(); i++) {
			documents.add(new ArrayList<Document>());
		}
		
		try (
			FileReader fileReader = new FileReader(fileName);
			BufferedReader reader = new BufferedReader(fileReader);
		) {
			
			String currentLine;
			while ((currentLine = reader.readLine()) != null) {
				
				String[] tokens = currentLine.split("\\s");
				int queryId = queryIds.indexOf(tokens[0]);
				
				if (queryId != -1) {
					List<Document> document = documents.get(queryId);
					boolean relevant = tokens[3].equals("1");
					
					document.add(new Document(tokens[2], relevant));
				}
				
			}
			
		} catch (IOException exception) {
			
			System.out.println("Unable to read from " + fileName);
			exception.printStackTrace();
			
		}
		
		return documents;
	}
	
	/**
	 * Set the boolean relevance for each document based on relevance judgement 
	 * 
	 * @param judgements The document relevance
	 * @param documents The documents whose relevance is to be set
	 */
	private static void setRelevantJudgement(List<Document> judgements, List<Document> documents) {
		for (Document document : documents) {
			int index = judgements.indexOf(document);
			if (index != -1) {
				document.setRelevant(judgements.get(index).isRelevant());
			}
		}
	}
	
	public static void main(String[] args) {
		// Get Relevance Judgement
		List<String> queries = new ArrayList<String>();
		queries.add(QUERY_1_ID);
		queries.add(QUERY_2_ID);
		queries.add(QUERY_3_ID);
		List<List<Document>> relevance = getRelevantJudgement(RELEVANCE_JUDGEMENT_FILE_NAME, queries);
		
		// Get each query measure
		List<Document> documents = getDocument(QUERY_1_FILE_NAME);
		setRelevantJudgement(relevance.get(0), documents);
		List<Double> precision = Measure.getPrecision(documents);
		System.out.println("P@20 for Query:" + QUERY_1_ID + " is " + precision.get(19));
		List<Double> recall = Measure.getRecall(documents, relevance.get(0).size());
		double averagePrecision1 = Measure.getAveragePrecision(documents, relevance.get(0).size());
		List<Double> ndcg = Measure.getNormalizedDiscountedCumulativeGain(documents, relevance.get(0).size());
		Printer.printTable(QUERY_1_TABLE_NAME, documents, precision, recall, ndcg);
		
		documents = getDocument(QUERY_2_FILE_NAME);
		setRelevantJudgement(relevance.get(1), documents);
		precision = Measure.getPrecision(documents);
		System.out.println("P@20 for Query:" + QUERY_2_ID + " is " + precision.get(19));
		recall = Measure.getRecall(documents, relevance.get(1).size());
		double averagePrecision2 = Measure.getAveragePrecision(documents, relevance.get(1).size());
		ndcg = Measure.getNormalizedDiscountedCumulativeGain(documents, relevance.get(1).size());
		Printer.printTable(QUERY_2_TABLE_NAME, documents, precision, recall, ndcg);
		
		documents = getDocument(QUERY_3_FILE_NAME);
		setRelevantJudgement(relevance.get(2), documents);
		precision = Measure.getPrecision(documents);
		System.out.println("P@20 for Query:" + QUERY_3_ID + " is " + precision.get(19));
		recall = Measure.getRecall(documents, relevance.get(2).size());
		double averagePrecision3 = Measure.getAveragePrecision(documents, relevance.get(2).size());
		ndcg = Measure.getNormalizedDiscountedCumulativeGain(documents, relevance.get(2).size());
		Printer.printTable(QUERY_3_TABLE_NAME, documents, precision, recall, ndcg);
		
		// Calculate Mean Average Precision (MAP)
		double meanAveragePrecision = (averagePrecision1 + averagePrecision2 + averagePrecision3) / (double) 3;
		System.out.println("Mean Average Precision: " + meanAveragePrecision);
	}
}
