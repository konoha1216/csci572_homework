package edu.usc.konoha.crawler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JOptionPane;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class BasicCrawlController {

    public static void main(String[] args) throws Exception {
        CrawlConfig config = new CrawlConfig();

        // Set the folder where intermediate crawl data is stored (e.g. list of urls that are extracted from previously
        // fetched pages and need to be crawled later).
        config.setCrawlStorageFolder("/tmp/crawler4j/");

        // Be polite: Make sure that we don't send more than 1 request per second (1000 milliseconds between requests).
        // Otherwise it may overload the target servers.
        config.setPolitenessDelay(20);

        // You can set the maximum crawl depth here. The default value is -1 for unlimited depth.
        config.setMaxDepthOfCrawling(16);

        // You can set the maximum number of pages to crawl. The default value is -1 for unlimited number of pages.
        config.setMaxPagesToFetch(20000);

        // Should binary data should also be crawled? example: the contents of pdf, or the metadata of images etc
        config.setIncludeBinaryContentInCrawling(true);

        // Do you need to set a proxy? If so, you can use:
        // config.setProxyHost("proxyserver.example.com");
        // config.setProxyPort(8080);

        // If your proxy also needs authentication:
        // config.setProxyUsername(username); config.getProxyPassword(password);

        // This config parameter can be used to set your crawl to be resumable
        // (meaning that you can resume the crawl from a previously
        // interrupted/crashed crawl). Note: if you enable resuming feature and
        // want to start a fresh crawl, you need to delete the contents of
        // rootFolder manually.
        config.setResumableCrawling(false);

        // Set this to true if you want crawling to stop whenever an unexpected error
        // occurs. You'll probably want this set to true when you first start testing
        // your crawler, and then set to false once you're ready to let the crawler run
        // for a long time.
        //config.setHaltOnError(true);

        // Instantiate the controller for this crawl.
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        // For each crawl, you need to add some seed urls. These are the first
        // URLs that are fetched and then the crawler starts following links
        // which are found in these pages
        controller.addSeed("https://www.nytimes.com");
        //controller.addSeed("https://www.ics.uci.edu/~lopes/");
        //controller.addSeed("https://www.ics.uci.edu/~welling/");

        // Number of threads to use during crawling. Increasing this typically makes crawling faster. But crawling
        // speed depends on many other factors as well. You can experiment with this to figure out what number of
        // threads works best for you.
        //int numberOfCrawlers = 8;
        int numberOfCrawlers = 7;

        // To demonstrate an example of how you can pass objects to crawlers, we use an AtomicInteger that crawlers
        // increment whenever they see a url which points to an image.
        AtomicInteger fetches_attempted = new AtomicInteger();
        AtomicInteger fetches_succeeded = new AtomicInteger();
        AtomicInteger fetches_failed = new AtomicInteger();
        AtomicInteger unique_urls = new AtomicInteger();
        AtomicInteger unique_urls_within = new AtomicInteger();
        AtomicInteger unique_urls_outside = new AtomicInteger();
        AtomicInteger OK = new AtomicInteger();
        AtomicInteger Moved = new AtomicInteger();
        AtomicInteger Unauthorized = new AtomicInteger();
        AtomicInteger Forbidden = new AtomicInteger();
        AtomicInteger NotFound = new AtomicInteger();
        AtomicInteger size1 = new AtomicInteger();
        AtomicInteger size2 = new AtomicInteger();
        AtomicInteger size3 = new AtomicInteger();
        AtomicInteger size4 = new AtomicInteger();
        AtomicInteger size5 = new AtomicInteger();
        AtomicInteger text_html = new AtomicInteger();
        AtomicInteger image_gif = new AtomicInteger();
        AtomicInteger image_jpeg = new AtomicInteger();
        AtomicInteger image_png = new AtomicInteger();
        AtomicInteger application_pdf = new AtomicInteger();
        
        HashSet<String> uniqueURL = new HashSet<>();
        HashSet<String> uniqueURL_fetch = new HashSet<>();

        // The factory which creates instances of crawlers.
        CrawlController.WebCrawlerFactory<BasicCrawler> factory = () -> new BasicCrawler(
        		fetches_attempted,
        		fetches_succeeded, 
        		fetches_failed,
        		unique_urls,
        		unique_urls_within,
        		unique_urls_outside,
        		OK,
        		Moved,
        		Unauthorized,
        		Forbidden,
        		NotFound,
        		size1,
        		size2,
        		size3,
        		size4,
        		size5,
        		uniqueURL,
        		uniqueURL_fetch,
        		text_html,
        		image_gif,
        		image_jpeg,
        		image_png,
        		application_pdf);
        FileWriter fw = new FileWriter("fetch_NewsSite1.csv", true);
    	BufferedWriter bw = new BufferedWriter(fw);
    	PrintWriter pw = new PrintWriter(bw);
    	pw.println("https://www.nytimes.com,"+"status");
    	pw.flush();
    	pw.close();
        fw = new FileWriter("visit_NewsSite1.csv", true);
    	bw = new BufferedWriter(fw);
    	pw = new PrintWriter(bw);
    	pw.println("URL,"+"size,"+"outgoingUrls,"+"type");
    	pw.flush();
    	pw.close();
        fw = new FileWriter("urls_NewsSite1.csv", true);
    	bw = new BufferedWriter(fw);
    	pw = new PrintWriter(bw);
    	pw.println("URL,"+"resides");
    	pw.flush();
    	pw.close();
        
        
        // Start the crawl. This is a blocking operation, meaning that your code
        // will reach the line after this only when crawling is finished.
        
        controller.start(factory, numberOfCrawlers);
    	try
        {
        	fw = new FileWriter("CrawlerReport_NewsSite1.txt", true);
        	bw = new BufferedWriter(fw);
        	pw = new PrintWriter(bw);
        	pw.println("Name: Zhengran Gao");
        	pw.println("USC ID: 2121161814");
        	pw.println("News site crawled: nytimes.com");
        	pw.println("Number of threads: 7");
        	pw.println();
        	pw.println("Fetch Statistics");
        	pw.println("================");
        	pw.println("# fetches attempted:"+ fetches_attempted);
        	pw.println("# fetches succeeded:"+ fetches_succeeded);
        	pw.println("# fetches failed or aborted:"+ fetches_failed);
        	pw.println();
        	pw.println("Outgoing URLs:");
        	pw.println("==============");
        	pw.println("Total URLs extracted:");
        	pw.println("# unique URLs extracted:"+unique_urls);
        	pw.println("# unique URLs within News Site:"+unique_urls_within);
        	pw.println("# unique URLs outside News Site:"+unique_urls_outside);
        	pw.println("Status Codes:");
        	pw.println("============");
        	pw.println("200 OK:"+OK);
        	pw.println("301 Moved Permanentlty:"+Moved);
        	pw.println("401 Unauthorized:"+Unauthorized);
        	pw.println("403 Forbidden:"+Forbidden);
        	pw.println("404 Not Found:"+NotFound);
        	pw.println("File Sizes:");
        	pw.println("============");
        	pw.println("< 1 KB:"+ size1);
        	pw.println("1KB ~ <10KB:"+ size2);
        	pw.println("10KB ~ <100KB:"+ size3);
        	pw.println("100KB ~ <1MB:"+ size4);
        	pw.println(">= 1MB:"+ size5);
        	pw.println("Content Types:");
        	pw.println("============");
        	pw.println("text/html:"+text_html);
        	pw.println("image/gif:"+image_gif);
        	pw.println("image/jpeg:"+image_jpeg);
        	pw.println("image/png:"+image_png);
        	pw.println("application/pdf:"+application_pdf);
        	
        	pw.flush();
        	pw.close();
        }
        catch(Exception e) {
        	JOptionPane.showMessageDialog(null, "Record not saved");
        }
    }
  

}
