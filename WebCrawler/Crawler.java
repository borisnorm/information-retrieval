import java.io.File;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Crawler {
	
	private static final int MAX_PAGES_TO_SEARCH = 1000;
	private static final int MAX_DEPTH = 5;
	private static final String TARGET_FILE_NAME_STRING = "output.txt";
	
	private Set<String> pagesVisited;
	private List<String> pagesToVisit;
	private int currentDepth = 1;
	private int totalCrawled = 0;

	public Crawler() {
		pagesVisited = new HashSet<String>();
		pagesToVisit = new LinkedList<String>();
	}
	
	/**
	 * Search for a key-phrase given the seed URL
	 * 
	 * @param url The seed URL
	 * @param keyPhrase An optional key-phrase to search for
	 */
	public void search(String url, String keyPhrase) {
		
		pagesToVisit.add(url);
		pagesToVisit.add(null);
		pagesVisited.add(null);
		
		try (
			PrintWriter outputStream = new PrintWriter(new File(TARGET_FILE_NAME_STRING));
		) {
			while (pagesVisited.size() <= MAX_PAGES_TO_SEARCH &&
					currentDepth <= MAX_DEPTH) {
				
				CrawlerLeg leg = new CrawlerLeg();
				String currentUrl = nextUrl();
				
				if (currentUrl == null) {
					return;
				}
				
				leg.crawl(currentUrl);
				totalCrawled++;
				Thread.sleep(1000);
				
				if (keyPhrase == null) {
					outputStream.println(currentUrl);
					pagesToVisit.addAll(leg.getLinks());
					pagesVisited.add(currentUrl);
					continue;
				}
				
				boolean isFound = leg.searchForWord(keyPhrase);
				
				if (isFound) {
					outputStream.println(currentUrl);
					pagesToVisit.addAll(leg.getLinks());
					pagesVisited.add(currentUrl);
				}
				
				if (currentDepth == 1 && !isFound) {
					pagesToVisit.addAll(leg.getLinks());
				}
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
		
		System.out.println("Completed");
		
	}

	/**
	 * Returns the next URL to move ahead with BFS
	 * It maintains depth of search as well
	 * 
	 * @return The next URL to visit in BFS
	 */
	private String nextUrl() {
		String nextUrl;
		
		do {
			nextUrl = pagesToVisit.remove(0);
			if (nextUrl == null) {
				// Check if last element in BFS search
				if (pagesToVisit.size() == 0) {
					return null;
				} else {
					currentDepth++;
					pagesToVisit.add(null);
				}
			}
		} while (!notVisited(nextUrl));

		return nextUrl;
	}
	
	/**
	 * Check if the URL string is present in the already visited unique pages
	 * 
	 * @param urlString The URL to check for
	 * @return true, it exists. False, otherwise
	 */
	private boolean notVisited(String urlString) {

		if (urlString == null) {
			return false;
		}
		
		boolean isHttps = urlString.charAt(4) == 's';
		
		if (isHttps) {
			String urlHttpString = urlString.substring(0, 4) + urlString.substring(5);
			return !pagesVisited.contains(urlHttpString) &&
					!pagesVisited.contains(urlString);
		} else {
			String urlHttpsString = urlString.substring(0, 4) + "s" + urlString.substring(4);
			return !pagesVisited.contains(urlHttpsString) &&
					!pagesVisited.contains(urlString);
		}
		
	}

}
