import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawlerLeg {
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
	private List<String> links;
	private Document htmlDocument;

	public CrawlerLeg() {
		links = new LinkedList<String>();
	}
	
	/**
	 * Make an HTTP request and parse the response
	 * 
	 * @param url The URL to visit
	 */
	public void crawl(String url) {
		
		try {
			Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
			Document htmlDocument = connection.get();
			this.htmlDocument = htmlDocument;
			
			if (connection.response().statusCode() == 200) {
				System.out.println("\nVisiting: " + url);
			}
			
			if (!connection.response().contentType().contains("text/html")) {
				System.out.println("Failed");
			}
			
			Elements linksOnPage = htmlDocument.select("a[href]");
			System.out.println("Found total links: " + linksOnPage.size());
			
			for (Element link : linksOnPage) {
				
				String urlString = link.absUrl("href");
				String urlPrefix = "en.wikipedia.org/wiki/";
				
				// Check prefix http(s)://en.wikipedia.org/wiki/
				if (!urlString.contains(urlPrefix)) {
					continue;
				}
				
				int indexOfPrefix = urlString.indexOf(urlPrefix);
				String restUrlString = urlString.substring(indexOfPrefix + urlPrefix.length());
				String mainPageString = "Main_Page";
				
				// Remove all : pages and MainPage
				if (restUrlString.contains(":") || restUrlString.contains(mainPageString)) {
					continue;
				}
				
				// Remove URL after # (if any)
				String hashString = "#";
				if (urlString.contains(hashString)) {
					int indexOfHash = urlString.indexOf(hashString);
					urlString = urlString.substring(0, indexOfHash);
				}
				
				links.add(urlString);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Search on the body of the HTML document
	 * 
	 * @param keyPhrase The string to look for
	 * @return whether or not the word was found
	 */
	public boolean searchForWord(String keyPhrase) {
		
		if (htmlDocument == null) {
			return false;
		}
		
		String bodyText = htmlDocument.body().text();
		return bodyText.toLowerCase().contains(keyPhrase.toLowerCase());
		
	}
	
	public List<String> getLinks() {
		return links;
	}

}
