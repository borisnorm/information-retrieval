import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Helper class to print to file
 * 
 * @author Adib
 */
public class Printer {

	/**
	 * Print pages along with its page rank values to the given file
	 * 
	 * @param pages Map containing page and its page rank
	 * @param fileName The name of the file to write to
	 */
	public static void printPageRank(HashMap<String, Double> pages, String fileName) {
		// Null Check
		if (pages == null) {
			return;
		}
		
		try (
			PrintWriter outputStream = new PrintWriter(new File(fileName));
		) {
			
			for (Map.Entry<String, Double> entry : pages.entrySet()) {
				String printString = entry.getKey() + " " + entry.getValue();
				outputStream.println(printString);
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
	}
	
	/**
	 * Print perplexity values to the given file
	 * 
	 * @param perplexity The perplexity to print
	 * @param fileName The name of the file to write to
	 */
	public static void printPerplexity(ArrayList<Double> perplexity, String fileName) {
		// Null Check
		if (perplexity == null) {
			return;
		}
		
		try (
			PrintWriter outputStream = new PrintWriter(new File(fileName));
		) {
			
			for (double value : perplexity) {
				outputStream.println(value);
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
	}
	
	/**
	 * Print top pages along with its page rank values to the given file
	 * sorted by decreasing order of page-rank
	 * 
	 * @param pages Map containing page and its page rank
	 * @param top The number of top pages to print
	 * @param fileName The name of the file to write to
	 */
	public static void printTopPageRank(HashMap<String, Double> pages, int top, String fileName) {
		// Null Check
		if (pages == null) {
			return;
		}
		
		List<Map.Entry<String, Double>> listPages = 
				new ArrayList<Map.Entry<String, Double>>(pages.entrySet());
		
		// Sort according to decreasing order of page-rank
		Collections.sort(listPages, new Comparator<Map.Entry<String, Double>>() {
			@Override
			public int compare(Map.Entry<String, Double> o1, 
					Map.Entry<String, Double> o2) {
				
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
			PrintWriter outputStream = new PrintWriter(new File(fileName));
		) {
			
			int len = listPages.size();
			
			for (int i = 0; i < top && i < len; i++) {
				Map.Entry<String, Double> pageEntry = listPages.get(i);
				String printString = pageEntry.getKey() + " " + pageEntry.getValue();
				outputStream.println(printString);
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
	}
	
	/**
	 * Print top pages along with its in-links count to the given file
	 * sorted by decreasing order of in-links count
	 * 
	 * @param graph The Web graph
	 * @param top The number of top pages to print
	 * @param fileName The name of the file to write to
	 */
	public static void printTopInLink(WebGraph graph, int top, String fileName) {
		// Null Check
		if (graph == null) {
			return;
		}
		
		HashMap<String, HashSet<String>> inLinks = graph.getInLinks();
		
		List<Map.Entry<String, HashSet<String>>> listPages =
				new ArrayList<Map.Entry<String, HashSet<String>>>(inLinks.entrySet());
		
		// Sort according to decreasing order of size of in-links
		Collections.sort(listPages, new Comparator<Map.Entry<String, HashSet<String>>>() {
			@Override
			public int compare(Map.Entry<String, HashSet<String>> o1,
					Map.Entry<String, HashSet<String>> o2) {
				
				int o1Size = o1.getValue().size();
				int o2Size = o2.getValue().size(); 
				
				if (o1Size > o2Size) {
					return -1;
				} else if (o1Size < o2Size) {
					return 1;
				} else {
					return 0;
				}
				
			}
		});
		
		// Print to File
		try (
			PrintWriter outputStream = new PrintWriter(new File(fileName));
		) {
			
			int len = listPages.size();
			
			for (int i = 0; i < top && i < len; i++) {
				Map.Entry<String, HashSet<String>> pageEntry = listPages.get(i);
				String printString = pageEntry.getKey() + " " + pageEntry.getValue().size();
				outputStream.println(printString);
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
	}
	
	/**
	 * Print top pages along with its inlinks and outlink counts to the given file
	 * sorted by decreasing order of page-rank
	 * 
	 * @param pages Map containing page and its page rank
	 * @param top The number of top pages to print
	 * @param fileName The name of the file to write to
	 */
	public static void printTopPageRankWithLink(HashMap<String, Double> pages, int top,
			String fileName, WebGraph graph) {
		// Null Check
		if (pages == null) {
			return;
		}
		
		List<Map.Entry<String, Double>> listPages = 
				new ArrayList<Map.Entry<String, Double>>(pages.entrySet());
		
		// Sort according to decreasing order of page-rank
		Collections.sort(listPages, new Comparator<Map.Entry<String, Double>>() {
			@Override
			public int compare(Map.Entry<String, Double> o1, 
					Map.Entry<String, Double> o2) {
				
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
			PrintWriter outputStream = new PrintWriter(new File(fileName));
		) {
			
			int len = listPages.size();
			
			for (int i = 0; i < top && i < len; i++) {
				Map.Entry<String, Double> pageEntry = listPages.get(i);
				String page = pageEntry.getKey();
				String printString = page + " " + graph.getNumberOfInLinks(page) +
						" " + graph.getNumberOfOutLinks(page);
				outputStream.println(printString);
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
	}
	
	/**
	 * Print top pages along with its in-links and outlink count to the given file
	 * sorted by decreasing order of in-links count
	 * 
	 * @param graph The Web graph
	 * @param top The number of top pages to print
	 * @param fileName The name of the file to write to
	 */
	public static void printTopInLinkWithLink(WebGraph graph, int top, String fileName) {
		// Null Check
		if (graph == null) {
			return;
		}
		
		HashMap<String, HashSet<String>> inLinks = graph.getInLinks();
		
		List<Map.Entry<String, HashSet<String>>> listPages =
				new ArrayList<Map.Entry<String, HashSet<String>>>(inLinks.entrySet());
		
		// Sort according to decreasing order of size of in-links
		Collections.sort(listPages, new Comparator<Map.Entry<String, HashSet<String>>>() {
			@Override
			public int compare(Map.Entry<String, HashSet<String>> o1,
					Map.Entry<String, HashSet<String>> o2) {
				
				int o1Size = o1.getValue().size();
				int o2Size = o2.getValue().size(); 
				
				if (o1Size > o2Size) {
					return -1;
				} else if (o1Size < o2Size) {
					return 1;
				} else {
					return 0;
				}
				
			}
		});
		
		// Print to File
		try (
			PrintWriter outputStream = new PrintWriter(new File(fileName));
		) {
			
			int len = listPages.size();
			
			for (int i = 0; i < top && i < len; i++) {
				Map.Entry<String, HashSet<String>> pageEntry = listPages.get(i);
				String page = pageEntry.getKey();
				String printString = page + " " + graph.getNumberOfInLinks(page) +
						" " + graph.getNumberOfOutLinks(page);
				outputStream.println(printString);
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
	}
	
}
