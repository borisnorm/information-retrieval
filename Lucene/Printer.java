import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


/**
 * Helper class to print to file
 * 
 * @author Adib
 */
public class Printer {

	/**
	 * Print the term-frequency pair to the given input file
	 * 
	 * @param outputFile The name of the file to write to
	 * @param pairs List of (unique_term, term_frequency)
	 */
	public static void printTermFrequencyPair(String outputFile, List<Retrieve.TermFrequencyPair> pairs) {
		try (
			PrintWriter outputStream = new PrintWriter(new File(outputFile));
		) {
			
			for (Retrieve.TermFrequencyPair pair : pairs) {
				outputStream.println("(" + pair.getTerm() + ", " + pair.getFrequency() + ")");
			}
			
		} catch (IOException exception) {
			
			System.out.println("Unable to write to output file");
			exception.printStackTrace();
			
		}
	}
	
	/**
	 * Print the name-score pair to the given input file
	 * 
	 * @param query The input query
	 * @param outputFile The name of the file to write to
	 * @param documentScorePairs List of (DocumentName, Score)
	 */
	public static void printQueryResult(String query, String outputFile, List<Retrieve.DocumentScorePair> documentScorePairs) {
		try (
			PrintWriter outputStream = new PrintWriter(new FileWriter(outputFile, true));
		) {
			
			outputStream.println("Found " + documentScorePairs.size() + " results for \"" + query + "\"");
			
			int rank = 1;
			for (Retrieve.DocumentScorePair documentScorePair : documentScorePairs) {
				outputStream.println(rank + ". " + documentScorePair.getDocument()
						+ " " + documentScorePair.getScore());
				outputStream.println(documentScorePair.getTextSnippet());
				rank++;
			}
			
			outputStream.println();
			
		} catch (IOException exception) {
			
			System.out.println("Unable to write to output file");
			exception.printStackTrace();
			
		}
	}
}
