import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.SlowCompositeReaderWrapper;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;


/**
 * Class used to retrieve/search information from an index
 * 
 * @author Adib
 */
public class Retrieve {
	
	/**
	 * Class used to represent a term-frequency pair
	 * 
	 * @author Adib
	 */
	static class TermFrequencyPair implements Comparable<TermFrequencyPair> {
		private final String term;
		private final long frequency;

		public TermFrequencyPair(String term, long frequency) {
			this.term = term;
			this.frequency = frequency;
		}

		@Override
		public int compareTo(TermFrequencyPair pair) {
			if (frequency > pair.getFrequency()) {
				return -1;
			} else if (frequency < pair.getFrequency()) {
				return 1;
			} else {
				return 0;
			}
		}

		public String getTerm() {
			return term;
		}

		public long getFrequency() {
			return frequency;
		}
	}
	
	/**
	 * Class used to represent rank-probability pair
	 * 
	 * @author Adib
	 */
	static class RankProbabilityPair {
		private final double rank;
		private final double probability;
		
		public RankProbabilityPair(double rank, double probability) {
			this.rank = rank;
			this.probability = probability;
		}

		public double getRank() {
			return rank;
		}
		
		public double getProbability() {
			return probability;
		}		
	}

	/**
	 * Class used to represent document-score pair 
	 * along with a text snippet
	 * 
	 * @author Adib
	 */
	static class DocumentScorePair {
		private final String document;
		private final float score;
		private final String textSnippet;
		
		public DocumentScorePair(String document, float score, String textSnippet) {
			this.document = document;
			this.score = score;
			this.textSnippet = textSnippet;
		}

		public String getDocument() {
			return document;
		}

		public float getScore() {
			return score;
		}

		public String getTextSnippet() {
			return textSnippet;
		}
	}
	
	/**
	 * Build a list of (unique_term, term_frequency) pairs over the 
	 * entire collection (sorted by frequency) for the given index
	 * 
	 * @param indexLocation The file location of the index
	 * @return List of (unique_term, term_frequency)
	 */
	public static List<TermFrequencyPair> getUniqueTermFrequency(String indexLocation) {
		List<TermFrequencyPair> pairs = new ArrayList<TermFrequencyPair>();

		try (
			IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexLocation)));
		) {
			
			Terms terms = SlowCompositeReaderWrapper.wrap(reader).terms(Index.CONTENT_FIELD);
			TermsEnum termsEnum = terms.iterator(null);

			BytesRef bytesRef;

			while ((bytesRef = termsEnum.next()) != null) {
				String term = bytesRef.utf8ToString();
				long frequency = reader.totalTermFreq(new Term(Index.CONTENT_FIELD, bytesRef));
				pairs.add(new TermFrequencyPair(term, frequency));
			}
			
		} catch (IOException exception) {
			
			System.out.println("Unable to generate term-frequency pair");
			exception.printStackTrace();
			
		}

		Collections.sort(pairs);
		return pairs;
	}

	/**
	 * Build a list of (rank, probability) pairs over the given 
	 * term-frequency pair (sorted by frequency)
	 * 
	 * @param termFrequencyPairs List of (unique_term, term_frequency)
	 * @return List of (rank, probability)
	 */
	public static List<RankProbabilityPair> getRankProbability(List<TermFrequencyPair> termFrequencyPairs) {
		List<RankProbabilityPair> rankProbabilityPairs = new ArrayList<RankProbabilityPair>();
		
		// Sort by rank of frequency
		Collections.sort(termFrequencyPairs);
		
		// Get total number of tokens
		double tokenCount = 0;
		for (TermFrequencyPair pair : termFrequencyPairs) {
			tokenCount += pair.getFrequency();
		}
		
		double rank = 1;
		for (TermFrequencyPair pair : termFrequencyPairs) {
			double probability = pair.getFrequency() / tokenCount;
			rankProbabilityPairs.add(new RankProbabilityPair(rank, probability));
			rank++;
		}
		
		return rankProbabilityPairs;
	}
	
	/**
	 * Search the given query in the index provided by the location 
	 * retrieving given number of top documents
	 * 
	 * @param indexLocation The file location of the index
	 * @param query The query to search
	 * @param topDocs The number of top documents to retrieve
	 * @return List of (DocumentName, Score)
	 */
	public static List<DocumentScorePair> searchQuery(String indexLocation, String query, int topDocs) {
		List<DocumentScorePair> pairs = new ArrayList<DocumentScorePair>();
		
		// Policy for extracting index terms from text
		Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_47);
		
		try (
			IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexLocation)));
		) {
			
			IndexSearcher searcher = new IndexSearcher(reader);
			TopScoreDocCollector collector = TopScoreDocCollector.create(topDocs, true);
			Query q = new QueryParser(Version.LUCENE_47, Index.CONTENT_FIELD, analyzer).parse(query);
			searcher.search(q, collector);

			ScoreDoc[] scoreDocs = collector.topDocs().scoreDocs;
			
			// Text Snippet Formatter
			SimpleHTMLFormatter formatter = new SimpleHTMLFormatter();
			Highlighter highlighter = new Highlighter(formatter, new QueryScorer(q));
			
			for (ScoreDoc scoreDoc : scoreDocs) {
				int docID = scoreDoc.doc;
				Document document = searcher.doc(docID);
				
				// Text Snippet
				TokenStream tokenStream = TokenSources.getTokenStream(reader, docID, Index.CONTENT_FIELD, analyzer);
				String textFragment = highlighter.getBestFragment(tokenStream, document.get(Index.CONTENT_FIELD));
				int textFragmentLength = textFragment.length() > 200 ? 200 : textFragment.length();
				
				pairs.add(new DocumentScorePair(
							document.get(Index.FILE_NAME_FIELD),
							scoreDoc.score, 
							textFragment.replaceAll("\\s", " ").substring(0, textFragmentLength)
						));
			}
			
		} catch (Exception exception) {
			
			System.out.println("Unable to retreive query result");
			exception.printStackTrace();
			
		}
		
		return pairs;
	}
	
}
