import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


/**
 * Helper class to print to file
 * 
 * @author Adib
 */
public class Printer {
	
	/**
	 * Print tokens along with the document number and term frequency
	 * that contains it to the given file
	 * 
	 * @param outputFile The name of the file to write to
	 */
	public static void printIndex(String outputFile,
			Map<String, Map<Integer, Integer>> invertedIndex) {
		
		try (
			PrintWriter outputStream = new PrintWriter(new File(outputFile));
		) {
			
			for (Map.Entry<String, Map<Integer, Integer>> entry : invertedIndex.entrySet()) {
				outputStream.print(entry.getKey() + " ");
				
				for (Map.Entry<Integer, Integer> documentEntry : entry.getValue().entrySet()) {
					outputStream.print("(" + documentEntry.getKey() + ","
							+ documentEntry.getValue() + ") ");
				}
				
				outputStream.println();
			}
			
		} catch (IOException exception) {
			
			System.out.println("Unable to write to output file");
			exception.printStackTrace();
			
		}
	}
	
	/**
	 * Print top documents along with its score values to the given file
	 * and standard output sorted by decreasing order of score
	 * Format: query_id Q0 doc_id rank BM25_score system_name
	 * 
	 * @param outputFile The name of the file to write to
	 * @param score Map from document id to its score
	 * @param top The number of top documents to print
	 * @param queryId The id of the query
	 */
	public static void printSortedScore(String outputFile,
			Map<Integer, Double> score, int top, int queryId) {
		
		List<Map.Entry<Integer, Double>> listScores = 
				new ArrayList<Map.Entry<Integer, Double>>(score.entrySet());
		
		// Sort according to decreasing order of scores
		Collections.sort(listScores, new Comparator<Map.Entry<Integer, Double>>() {
			@Override
			public int compare(Map.Entry<Integer, Double> o1, 
					Map.Entry<Integer, Double> o2) {
				
				double o1Value = o1.getValue();
				double o2Value = o2.getValue(); 
				
				if (o1Value > o2Value) {
					return -1;
				} else if (o1Value < o2Value) {
					return 1;
				} else {
					return 0;
				}
				
			}
		});
		
		// Print to File
		try (
			PrintWriter outputStream = new PrintWriter(new FileWriter(outputFile, true));
		) {
			
			int len = listScores.size();
			
			for (int i = 0; i < top && i < len; i++) {
				Map.Entry<Integer, Double> pageEntry = listScores.get(i);
				String printString = queryId + " " + "Q0" + " " + 
						pageEntry.getKey() + " " + (i + 1) +
						" " + pageEntry.getValue() + " " + "system_name";
				outputStream.println(printString);
				System.out.println(printString);
			}
			
			outputStream.println();
			System.out.println();
			
		} catch (IOException e) {
			
			System.out.println("Unable to write to output file");
			e.printStackTrace();
			
		}
	}
}
