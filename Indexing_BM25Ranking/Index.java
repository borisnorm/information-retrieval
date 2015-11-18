import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * An index builder/loader class
 * 
 * @author Adib
 */
public class Index {

	/*
	 * Map from Token to a map from document id to term frequency
	 */
	private final Map<String, Map<Integer, Integer>> invertedIndex;
	
	/*
	 * Map from document id to number of token's in that document
	 */
	private final Map<Integer, Integer> tokenCount;
	
	public Index() {
		invertedIndex = new HashMap<String, Map<Integer, Integer>>();
		tokenCount = new HashMap<Integer, Integer>();
	}
	
	/**
	 * Build an inverted index from the given input file name
	 * 
	 * @param fileName The filename to read the data from
	 */
	public void buildIndex(String fileName) {
		try (
			FileReader fileReader = new FileReader(fileName);
			BufferedReader reader = new BufferedReader(fileReader);
		) {
			
			String currentLine;
			int currentDocument = 1;
			
			while ((currentLine = reader.readLine()) != null) {
				
				// Start of a document
				if (currentLine.matches("^#\\s*[0-9]+$")) {
					
					currentDocument = Integer.parseInt(currentLine.split("\\s")[1]);
					
				} else {
					
					String[] tokens = currentLine.split("\\s");
					
					for (String token : tokens) {
						if (!token.matches("[0-9]+")) {
							
							token = token.toLowerCase();
							if (!invertedIndex.containsKey(token)) {
								invertedIndex.put(token, new HashMap<Integer, Integer>());
							}
							
							Map<Integer, Integer> documentMap = invertedIndex.get(token);
							Integer termFrequency = documentMap.get(currentDocument);
							documentMap.put(
									currentDocument,
									termFrequency == null ? 1 : termFrequency + 1
							);
						}
					}
					
				}
			}
			
		} catch (IOException exception) {
			
			System.out.println("Unable to read from input file");
			exception.printStackTrace();
			
		}
	}

	/**
	 * Load an inverted index from the given input file name
	 * 
	 * @param fileName The filename to read the data from
	 */
	public void loadIndex(String fileName) {
		try (
			FileReader fileReader = new FileReader(fileName);
			BufferedReader reader = new BufferedReader(fileReader);
		) {
			
			String currentLine;
			
			while ((currentLine = reader.readLine()) != null) {
				
				String[] tokens = currentLine.split("\\s");
				
				if (!invertedIndex.containsKey(tokens[0])) {
					invertedIndex.put(tokens[0], new HashMap<Integer, Integer>());
				}
				
				Map<Integer, Integer> documentMap = invertedIndex.get(tokens[0]);
				
				for (int i = 1; i < tokens.length; i++) {
					Matcher matcher = Pattern.compile("([0-9]+),([0-9]+)").matcher(tokens[i]);
					
					if (matcher.find()) {
						int documentNumber = Integer.parseInt(matcher.group(1));
						int termFrequency = Integer.parseInt(matcher.group(2));
						
						documentMap.put(documentNumber, termFrequency);
						
						Integer numberOfToken = tokenCount.get(documentNumber);
						tokenCount.put(
								documentNumber,
								numberOfToken == null ? termFrequency
										: numberOfToken + termFrequency
						);
					}
					
				}
				
			}
			
		} catch (IOException exception) {
			
			System.out.println("Unable to read from input file");
			exception.printStackTrace();
			
		}
	}
	
	public Map<String, Map<Integer, Integer>> getInvertedIndex() {
		return invertedIndex;
	}

	public Map<Integer, Integer> getTokenCount() {
		return tokenCount;
	}
	
}
