import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Class that is used to represent web pages and links between those pages
 * 
 * @author Adib
 */
public class WebGraph {
	 
	private final HashMap<String, HashSet<String>> inLinks;
	private final HashMap<String, HashSet<String>> outLinks;
	
	public WebGraph() {
		inLinks = new HashMap<String, HashSet<String>>();
		outLinks = new HashMap<String, HashSet<String>>();
	}
	
	/**
	 * Create a web graph from the given input file name
	 * 
	 * @param fileName The filename to read the data from
	 */
	public void initGraph(String fileName) {
		try (
			FileReader fileReader = new FileReader(fileName);
			BufferedReader reader = new BufferedReader(fileReader);
		) {
			
			String currentLine;
			
			while ((currentLine = reader.readLine()) != null) {
				String words[] = currentLine.split(" ");
				String page = words[0];
				
				// Add to InLink Hash
				if (!inLinks.containsKey(page)) {
					inLinks.put(page, new HashSet<String>());
				}
				
				// Add to OutLink Hash
				if (!outLinks.containsKey(page)) {
					outLinks.put(page, new HashSet<String>());
				}
				
				HashSet<String> inLinkSet = inLinks.get(page);
				
				for (int i = 1; i < words.length; i++) {
					// Add to in-link set for given page
					inLinkSet.add(words[i]);
					
					// Add to in-link Hash if not present
					if (!inLinks.containsKey(words[i])) {
						inLinks.put(words[i], new HashSet<String>());
					}
					
					// Add to out-link Hash if not present
					if (outLinks.containsKey(words[i])) {
						
						HashSet<String> outLinkSet = outLinks.get(words[i]);
						outLinkSet.add(page);
						
					} else {
						
						HashSet<String> outLinkSet = new HashSet<String>();
						outLinkSet.add(page);
						outLinks.put(words[i], outLinkSet);
						
					}
				}
			}
			
		} catch (IOException exception) {
			
			System.out.println("Unable to read from input file");
			exception.printStackTrace();
			
		}
	}
	
	/**
	 * Return all unique pages in the web-graph
	 * 
	 * @return HashSet containing those pages
	 */
	public HashSet<String> getAllPages() {
		HashSet<String> pages = new HashSet<String>();
		
		for (String page : outLinks.keySet()) {
			pages.add(page);
		}
		
		return pages;
	}
	
	/**
	 * Return all Sink Pages in the graph
	 * 
	 * @return Set containing those pages
	 */
	public HashSet<String> getSinkPages() {
		HashSet<String> pages = new HashSet<String>();
		
		for (Map.Entry<String, HashSet<String>> entry : outLinks.entrySet()) {
			if (entry.getValue().isEmpty()) {
				pages.add(entry.getKey());
			}
		}
		
		return pages;
	}
	
	/**
	 * Return the number of source pages i.e. they have no in-links
	 * 
	 * @return The number of sources
	 */
	public int getNumberOfSourcePages() {
		int count = 0;
		
		for (HashSet<String> set : inLinks.values()) {
			if (set.isEmpty()) {
				count++;
			}
		}
		
		return count;
	}
	
	/**
	 * Return the number of sink pages i.e. they have no out-links
	 * 
	 * @return The number of sinks
	 */
	public int getNumberOfSinkPages() {
		int count = 0;
		
		for (HashSet<String> set : outLinks.values()) {
			if (set.isEmpty()) {
				count++;
			}
		}
		
		return count;
	}
	
	/**
	 * Return all out-links pages for the given page
	 * 
	 * @param page The page whose out-link is to be determined
	 * @return Set containing those out-links
	 */
	public HashSet<String> getOutLinks(String page) {
		return outLinks.get(page);
	}
	
	/**
	 * Return all in-links pages for the given page
	 * 
	 * @param page The page whose in-link is to be determined
	 * @return Set containing those in-links
	 */
	public HashSet<String> getInLinks(String page) {
		return inLinks.get(page);
	}
	
	/**
	 * Return number of out-links to a page
	 * 
	 * @param page The page whose number of out-link is to be determined
	 * @return Number of those out-links
	 */
	public int getNumberOfOutLinks(String page) {
		return outLinks.get(page).size();
	}
	
	/**
	 * Return number of in-links to a page
	 * 
	 * @param page The page whose number of in-link is to be determined
	 * @return Number of those in-links
	 */
	public int getNumberOfInLinks(String page) {
		return inLinks.get(page).size();
	}
	
	/**
	 * Print the Out-link Map on console
	 */
	public void printOutLinks() {
		System.out.println("OutLinks:");
		
		for (Map.Entry<String, HashSet<String>> entry : outLinks.entrySet()) {
			System.out.print(entry.getKey() + ": ");
			
			for (String link : entry.getValue()) {
				System.out.print(link + " ");
			}
			
			System.out.println();
		}
	}

	/**
	 * Print the In-link Map on console
	 */
	public void printInLinks() {
		System.out.println("InLinks:");
		
		for (Map.Entry<String, HashSet<String>> entry : inLinks.entrySet()) {
			System.out.print(entry.getKey() + ": ");
			
			for (String link : entry.getValue()) {
				System.out.print(link + " ");
			}
			
			System.out.println();
		}
	}

	/**
	 * Return the number of unique pages in the web graph
	 * 
	 * @return The number of unique pages
	 */
	public int getNumberOfPages() {
		return outLinks.size();
	}

	public HashMap<String, HashSet<String>> getInLinks() {
		return inLinks;
	}
	
}
