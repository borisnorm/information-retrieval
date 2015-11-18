
public class WebCrawler {
	public static void main(String[] args) {
		String urlString;
		String keyPhrase = null;
		
		if (args.length == 1) {
			urlString = args[0];
		} else if (args.length == 2) {
			urlString = args[0];
			keyPhrase = args[1];
		} else {
			System.out.println("Provide valid inputs");
			return;
		}
		
	System.out.println("Seed:" + urlString + "\nKeyPhrase:" + keyPhrase);
        Crawler crawler = new Crawler();
        //crawler.search("https://en.wikipedia.org/wiki/Hugh_of_Saint-Cher", "concordance");
        crawler.search(urlString, keyPhrase);
    }
}
