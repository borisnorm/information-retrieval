import java.util.HashMap;

public class PageRank {

	private static final String PAGE_RANK_FILE_NAME = "page_rank.txt";
	private static final String PERPLEXITY_FILE_NAME = "perplexity.txt";
	private static final String TOP_50_PAGE_RANK_FILE_NAME = "top_50_page_rank.txt";
	private static final String TOP_50_INLINK_FILE_NAME = "top_50_inlink.txt";
	
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Provide a file name as an argument");
			return;
		}
		
		// Form web graph
		WebGraph graph = new WebGraph();
		graph.initGraph(args[0]);
		
		// Run Page Rank
		PageRankAlgorithm algorithm =  new PageRankAlgorithm();
		HashMap<String, Double> pages = algorithm.calculatePageRank(graph);
		
		// Printer methods
		//Uncomment next line to print perplexity at each pagerank iteration
		//Printer.printPerplexity(algorithm.getPerplexity(), PERPLEXITY_FILE_NAME);
		
		Printer.printPageRank(pages, PAGE_RANK_FILE_NAME);
		
		//Uncomment next line to print top 50 pages sorted by page rank
		//Printer.printTopPageRank(pages, 50, TOP_50_PAGE_RANK_FILE_NAME);
		
		//Uncomment next line to print top 50 pages sorted by in-link count
		//Printer.printTopInLink(graph, 50, TOP_50_INLINK_FILE_NAME);
		
		System.out.println("Total Pages: " + graph.getNumberOfPages());
		System.out.println("Total Sources: " + graph.getNumberOfSourcePages());
		System.out.println("Total Sinks: " + graph.getNumberOfSinkPages());
		System.out.println("Total Decreased PageRank: " + algorithm.getNumberOfDegradedPages());
	}
}
