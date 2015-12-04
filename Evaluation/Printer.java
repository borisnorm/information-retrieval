import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import dnl.utils.text.table.TextTable;

/**
 * Helper class to print to file
 * 
 * @author Adib
 */
public class Printer {

	/**
	 * Print table with the following columns and values:
	 * 1) Rank 
	 * 2) Document ID
	 * 3) Document Score 
	 * 4) Relevance Level 
	 * 5) Precision 
	 * 6) Recall 
	 * 7) NDCG
	 * 
	 * @param outputFile The name of the file to write to
	 */
	public static void printTable(String outputFile,
			List<Evaluate.Document> documents, List<Double> precision,
			List<Double> recall, List<Double> ndcg) {

		try (
			FileOutputStream fileOutputStream = new FileOutputStream(new File(outputFile));
			PrintStream printStream = new PrintStream(fileOutputStream);
		) {

			// Change Standard output to file
			PrintStream console = System.out;
			System.setOut(printStream);

			String[] columnNames = { 
					"Rank", 
					"Document ID", 
					"Document Score",
					"Relevance Level", 
					"Precision", 
					"Recall", 
					"NDCG" 
				};
			
			Object[][] data = new Object[documents.size()][7];
			
			for (int i = 0; i < documents.size(); i++) {
				Evaluate.Document document = documents.get(i);
				data[i][0] = i + 1;
				data[i][1] = document.getFileName();
				data[i][2] = document.getRankingScore();
				data[i][3] = document.isRelevant() ? 1 : 0;
				data[i][4] = precision.get(i);
				data[i][5] = recall.get(i);
				data[i][6] = ndcg.get(i);
			}
			
			TextTable textTable = new TextTable(columnNames, data);
			textTable.printTable();
			
			// Change Standard output back to console
			System.setOut(console);

		} catch (IOException exception) {

			System.out.println("Unable to write to output file");
			exception.printStackTrace();

		}
	}

}
