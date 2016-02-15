package Spider;

import java.io.Console;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.*;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class Spider {
	
	//private static final String ValidURLPattern = "^https?://www\\.[A-Za-z0-9]+\\..+";
	
	private String startURL = "";
	private boolean followAllLinks = true;
	
	private HashSet<String> whitelistedURLs; // List of allowed URLs
	private HashSet<String> siteURLs;        // List of URLs in the target 'site'
	
	private Spider()
	{
		whitelistedURLs = new HashSet<String>();
		siteURLs = new HashSet<String>();
	}
	
	/**
	 * Sets up a spider object with the given starting URL
	 * 
	 * @param startingURL A URL where the spider should start
	 */
	
	public Spider(String startingURL)
	{
		this();
		
		if (startingURL == null || startingURL.isEmpty())
			throw new InvalidParameterException("Starting URL must not be null or empty");
		
		//System.out.println(startingURL);
		//System.out.println(ValidURLPattern);
		
		//System.out.println("Bool? " + Pattern.matches(ValidURLPattern, startingURL));
		
		// I was experimenting with validating that the URL was valid, but it proved to be too difficult
		
		startURL = startingURL;
	}
	
	/**
	 * Add a URL that is 'safe' for the spider to visit
	 * 
	 * @param whiteListURL URL to add
	 */
	public void addWhiteListURL(String whiteListURL)
	{
		if (whiteListURL == null || whiteListURL.isEmpty())
			throw new InvalidParameterException("Whitelist URL must not be null or empty");
		
		whitelistedURLs.add(whiteListURL);
	}
	
	/**
	 * Add a URL to the list that the spider is meant to visit
	 * 
	 * @param siteURL URL to be added
	 */
	public void addSiteURL(String siteURL)
	{
		if (siteURL == null || siteURL.isEmpty())
			throw new InvalidParameterException("Site URL must not be null or empty");
		
		siteURLs.add(siteURL);
		whitelistedURLs.add(siteURL);
	}

	/**
	 * Orders the spider to 'walk' through the given site.
	 * @return A String containing all of the errors found.
	 */
	public String walkSite()
	{
		// Verify there isn't a problem with the starting URL
		if (startURL == null || startURL.isEmpty())
			return "Starting URL is null or empty.";
		
		String errors = "";
		
		Queue<String> toVisitURLs = new LinkedBlockingQueue<String>();
		HashSet<String> visitedURLs = new HashSet<String>();

		WebDriver driver = new FirefoxDriver();
		
		toVisitURLs.add(startURL);
		
		while (toVisitURLs.isEmpty() == false)
		{
			String visit = toVisitURLs.poll();
			//System.out.println("Spider is now considering visiting:" + visit);
			//if (visit.toLowerCase().contains("facebook"))
			//{
			//	System.out.println("Visiting Facebook");
			//}
			
			// Check if we've visited this URL already
			if (visitedURLs.contains(visit))
			{
				//System.out.println("Spider already visited that.")
				;
			}
			else
			{
				// Haven't visited it
				
				//System.out.println("Site matching?");
				// Is it a site URL that we have to actually visit?
				if (hashSetPartialMatch(siteURLs, visit))
				{
					// Verify the link works
					if (checkLinkBroken(visit) == false)
					{
						errors += "Tried to visit broken link: " + visit + "\n";
					}
					
					//System.out.println("Spider is now visiting: " + visit);
					driver.get(visit);
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//System.out.println(" Recognized site URL, spidering through page");
					
					//System.out.println(" Please implement link spider");
					List<WebElement> links = driver.findElements(By.tagName("a"));
					
					for (WebElement link : links)
					{
						try
						{
							String target = link.getAttribute("href");
							
							//System.out.println("  Spider found possible link to visit: " + target);
							
							if (target != null && 
								(target.startsWith("http://") || target.startsWith("https://")))
							{
								//System.out.println("  Valid link found, adding to visit list: " + target);
								toVisitURLs.add(target);
								
								// DEBUG
								if (target.toLowerCase().contains("facebook"))
								{
									System.out.println("Facebook found");
								}
							}
						}
						catch (StaleElementReferenceException e)
						{
							// Don't really care about Stale Elements
							;
						}
					}
					
					//System.out.println(" Please Implement image spider");
					List<WebElement> images = driver.findElements(By.tagName("img"));
					
					for (WebElement image : images)
					{
						try
						{
							String target = image.getAttribute("src");
							
							//System.out.println("  Spider found possible link to visit: " + target);
							
							if (target != null && 
								(target.startsWith("http://") || target.startsWith("https://")))
							{
								//System.out.println("  Valid link found, adding to visit list: " + target);
								toVisitURLs.add(target);
							}
						}
						catch (StaleElementReferenceException e)
						{
							// Don't really care about Stale Elements
							;
						}
					}
					
					visitedURLs.add(visit);
				}
				else
				{
					// Make sure the link is still valid.
					//System.out.println("Please implement verify link works");
					if (checkLinkBroken(visit) == false)
					{
						errors += "Broken link found: " + visit + "\n";
					}
					
					//if (visit.toLowerCase().contains("facebook"))
					//{
					//	System.out.println("In the whitelist stuff facebook	");
					//}
					
					//System.out.println("WhiteListMatch?");
					// Is it in the whitelist?
					if (hashSetPartialMatch(whitelistedURLs, visit) == true)
					{
						System.out.println("Targeted url is whitelisted: " + visit);
					}
					else
					{
						errors += "Non-whitelisted URL was linked: " + visit + "\n";
					}
					
					visitedURLs.add(visit);
				}
			}
		}
		
		if (driver != null)
		{
			driver.close();
			driver.quit();
		}
		
		return errors;
	}
	
	// Iterate through a set, and find if something partially matches the
	// passed in string.
	private boolean hashSetPartialMatch(HashSet<String> set, String toMatch)
	{
		for(String str : set)
		{
			//System.out.println(toMatch + ".contains(" + str + ") == " + toMatch.contains(str));
			//System.out.println(str + ".contains(" + toMatch + ") == " + str.contains(toMatch));
			//System.out.println("Comparing |" + str + "|" + toMatch + "|");
			if (toMatch.contains(str) || str.contains(toMatch))
				return true;
		}
		
		return false;
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean checkLinkBroken(String url)
	{
		HttpURLConnection connection;
		
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			int response = connection.getResponseCode();
			
			// Check if response is in one of the 'acceptable' ranges
			if (response >= 200 && response < 400)
			{
				return true;
			}
			else
			{
				return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}		
	}
}
