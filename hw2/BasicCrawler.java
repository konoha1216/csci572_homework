package edu.usc.konoha.crawler;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.http.HttpStatus;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class BasicCrawler extends WebCrawler {

    private static final Pattern ALLOWED_EXTENSIONS = Pattern.compile(".*\\.(html|doc|pdf|bmp|gif|jpg|png)$");
    private final AtomicInteger fetches_attempted;
    private final AtomicInteger fetches_succeeded;
    private final AtomicInteger fetches_failed;
    private final AtomicInteger unique_urls;
    private final AtomicInteger unique_urls_within;
    private final AtomicInteger unique_urls_outside;
    private final AtomicInteger OK;
    private final AtomicInteger Moved;
    private final AtomicInteger Unauthorized;
    private final AtomicInteger Forbidden;
    private final AtomicInteger NotFound;
    private final AtomicInteger size1;
    private final AtomicInteger size2;
    private final AtomicInteger size3;
    private final AtomicInteger size4;
    private final AtomicInteger size5;
    private final HashSet<String>uniqueUrl;
    private final HashSet<String>uniqueUrl_fetch;
    private final AtomicInteger text_html;
    private final AtomicInteger image_gif;
    private final AtomicInteger image_jpeg;
    private final AtomicInteger image_png;
    private final AtomicInteger application_pdf;
    
    //private final AtomicInteger numSeenImages;

    /**
     * Creates a new crawler instance.
     *
     * @param numSeenImages This is just an example to demonstrate how you can pass objects to crawlers. In this
     * example, we pass an AtomicInteger to all crawlers and they increment it whenever they see a url which points
     * to an image.
     */
    public BasicCrawler(
    		AtomicInteger fetches_attempted,
    		AtomicInteger fetches_succeeded,
    		AtomicInteger fetches_failed,
    		AtomicInteger unique_urls,
    		AtomicInteger unique_urls_within,
    		AtomicInteger unique_urls_outside,
    		AtomicInteger OK,
    		AtomicInteger Moved,
    		AtomicInteger Unauthorized,
    		AtomicInteger Forbidden,
    		AtomicInteger NotFound,
    		AtomicInteger size1,
    		AtomicInteger size2,
    		AtomicInteger size3,
    		AtomicInteger size4,
    		AtomicInteger size5,
    		HashSet<String> uniqueUrl,
    		HashSet<String> uniqueUrl_fetch,
    		AtomicInteger text_html,
    		AtomicInteger image_gif,
    		AtomicInteger image_jpeg,
    		AtomicInteger image_png,
    		AtomicInteger application_pdf
    		) {
        this.fetches_attempted = fetches_attempted;
        this.fetches_succeeded = fetches_succeeded;
        this.fetches_failed = fetches_failed;
        this.unique_urls = unique_urls;
        this.unique_urls_within = unique_urls_within;
        this.unique_urls_outside = unique_urls_outside;
        this.OK = OK;
        this.Moved = Moved;
        this.Unauthorized = Unauthorized;
        this.Forbidden = Forbidden;
        this.NotFound = NotFound;
        this.size1 = size1;
        this.size2 = size2;
        this.size3 = size3;
        this.size4 = size4;
        this.size5 = size5;
        this.uniqueUrl = uniqueUrl;
        this.uniqueUrl_fetch = uniqueUrl_fetch;
        this.text_html = text_html;
        this.image_gif = image_gif;
        this.image_jpeg = image_jpeg;
        this.image_png = image_png;
        this.application_pdf = application_pdf;
    }
    
    private boolean checkContentType(String type) {
    	if (type.equals("text/html") ||type.equals("doc") ||
    		type.startsWith("image/") || type.equals("application/pdf")
    		||type.equals("application/msword")) {
    		return true;
    	}
    	return false;
    }

    /**
     * You should implement this function to specify whether the given url
     * should be crawled or not (based on your crawling logic).
     */
        
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        // Ignore the url if it has an extension that matches our defined set of image extensions.
        if(href.startsWith("https://www.nytimes.com")) {
        	saveRecord(url.getURL(),"OK","urls_NewsSite1.csv");
        	return ALLOWED_EXTENSIONS.matcher(href).matches()
        			|| checkContentType(referringPage.getContentType().split(";")[0]);
        } else {
        	saveRecord(url.getURL(),"N_OK","urls_NewsSite1.csv");
        	return false;
        }
    }
    
    public void saveRecord(String url, String resides,String filepath) {
    	try
        {
        	FileWriter fw = new FileWriter(filepath, true);
        	BufferedWriter bw = new BufferedWriter(fw);
        	PrintWriter pw = new PrintWriter(bw);
        	
        	pw.println(url+","+resides);
        	pw.flush();
        	pw.close();
        }
        catch(Exception e) {
        	JOptionPane.showMessageDialog(null, "Record not saved");
        }
    }
    
    public void saveRecord(String url, int status, String filepath) {
    	try
        {
        	FileWriter fw = new FileWriter(filepath, true);
        	BufferedWriter bw = new BufferedWriter(fw);
        	PrintWriter pw = new PrintWriter(bw);
        	
        	pw.println(url+","+String.valueOf(status));
        	pw.flush();
        	pw.close();
        }
        catch(Exception e) {
        	JOptionPane.showMessageDialog(null, "Record not saved");
        }
    }
    
    public void saveRecord(String url, int size, int outgoingUrls, String type, String filepath) {
    	try
        {
        	FileWriter fw = new FileWriter(filepath, true);
        	BufferedWriter bw = new BufferedWriter(fw);
        	PrintWriter pw = new PrintWriter(bw);
        	
        	pw.println(url+","+size+","+outgoingUrls+","+type);
        	pw.flush();
        	pw.close();
        }
        catch(Exception e) {
        	JOptionPane.showMessageDialog(null, "Record not saved");
        }
    }
    
    @Override
    protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
    	if(!uniqueUrl_fetch.contains(webUrl.getURL())) {
    		uniqueUrl_fetch.add(webUrl.getURL());
			saveRecord(webUrl.getURL(),statusCode,"fetch_NewsSite1.csv");
    	}
    	fetches_attempted.incrementAndGet();
    	if(fetches_attempted.get()%1000 == 0) {
    		Timestamp ts=new Timestamp(System.currentTimeMillis());  
            Date date=new Date(ts.getTime());  
    		saveRecord(date.toString(), fetches_attempted.get(),"progress.txt");
    	}
    	if (statusCode >= 200 &&statusCode <= 299) {
    		fetches_succeeded.incrementAndGet();
    		if(statusCode==200) {
    			OK.incrementAndGet();
    		}
    	} else {
    		fetches_failed.incrementAndGet();
    		if(statusCode == HttpStatus.SC_MOVED_PERMANENTLY) {
    			Moved.incrementAndGet();
    		} else if(statusCode == 401) {
    			Unauthorized.incrementAndGet();
    		} else if(statusCode == 403) {
    			Forbidden.incrementAndGet();
    		} else if(statusCode == 404) {
    			NotFound.incrementAndGet();
    		}
    	}
    }

    /**
     * This function is called when a page is fetched and ready to be processed
     * by your program.
     */
    @Override
    public void visit(Page page) {
//        int docid = page.getWebURL().getDocid();
        String url = page.getWebURL().getURL();
        int size = page.getContentData().length;
        if(size < 1024) {
        	size1.incrementAndGet();
        } else if(size>=1024 && size <10240) {
        	size2.incrementAndGet();
        } else if(size >=10240 && size < 102400) {
        	size3.incrementAndGet();
        } else if(size >= 102400 && size < 1024000) {
        	size4.incrementAndGet();
        } else {
        	size5.incrementAndGet();
        }
        int outgoingUrls = 0;
        if (page.getParseData() instanceof HtmlParseData) {
          HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
          outgoingUrls = htmlParseData.getOutgoingUrls().size();
          Set<WebURL> outgoingUrlsSet = htmlParseData.getOutgoingUrls();
          Iterator<WebURL> itr = outgoingUrlsSet.iterator(); 
          while(itr.hasNext()) {
        	  synchronized (this) {
        		  String str = itr.next().getURL();
        		  if(!uniqueUrl.contains(str)) {
        			  uniqueUrl.add(str);
        			  unique_urls.incrementAndGet();
        			  if(str.startsWith("https://www.nytimes.com")) {
        				  unique_urls_within.incrementAndGet();
        			  } else {
        				  unique_urls_outside.incrementAndGet();
        			  }
        		  }
        	  }
          }

         
        }
        String type = page.getContentType().split(";")[0];
        if(type.equals("text/html")) {
        	text_html.incrementAndGet();
        } else if(type.equals("image/gif")) {
        	image_gif.incrementAndGet();
        } else if (type.equals("image/jpeg")) {
        	image_jpeg.incrementAndGet();
        } else if (type.equals("image/png")) {
        	image_png.incrementAndGet();
        } else if (type.equals("application/pdf")) {
        	application_pdf.incrementAndGet();
        }
        saveRecord(url,size,outgoingUrls,type,"visit_NewsSite1.csv");
        
//        String status = String.valueOf(page.getStatusCode());
//        String domain = page.getWebURL().getDomain();
//        String path = page.getWebURL().getPath();
//        String subDomain = page.getWebURL().getSubDomain();
//        String parentUrl = page.getWebURL().getParentUrl();
//        String anchor = page.getWebURL().getAnchor();

//        logger.debug("Docid: {}", docid);
//        logger.info("URL: {}", url);
//        logger.debug("Domain: '{}'", domain);
//        logger.debug("Sub-domain: '{}'", subDomain);
//        logger.debug("Path: '{}'", path);
//        logger.debug("Parent page: {}", parentUrl);
//        logger.debug("Anchor text: {}", anchor);
        
//        if (page.getParseData() instanceof HtmlParseData) {
//            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
//            String text = htmlParseData.getText();
//            String html = htmlParseData.getHtml();
//            Set<WebURL> links = htmlParseData.getOutgoingUrls();
//
//            logger.debug("Text length: {}", text.length());
//            logger.debug("Html length: {}", html.length());
//            logger.debug("Number of outgoing links: {}", links.size());
//        }
//
//        Header[] responseHeaders = page.getFetchResponseHeaders();
//        if (responseHeaders != null) {
//            logger.debug("Response headers:");
//            for (Header header : responseHeaders) {
//                logger.debug("\t{}: {}", header.getName(), header.getValue());
//            }
//        }
//
//        logger.debug("=============");
    }
}
