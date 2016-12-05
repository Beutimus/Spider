package Spider;

import java.io.Console;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.nio.file.*;
import java.util.regex.*;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.google.common.io.Files;

public class Spider {
	
	//private static final String ValidURLPattern = "^https?://www\\.[A-Za-z0-9]+\\..+";
	// TODO: Refactor the walk section so there isn't repeated code with links/images
	// TODO: Maybe make it so users can configure what tags we look at on the page?
	// TODO: Make the return value of the 'walk' function something more than a straight String
	// TODO: Make spider a bit more resilient if the browser gets closed
	
	private String startURL = "";
	private boolean followAllLinks = true;
	private boolean gatherWords = true;
	private int waitTimeInMilliseconds = 1000; // Default wait time of 1 second
	
	private HashSet<String> whitelistedURLs; // List of allowed URLs
	private HashSet<String> siteURLs;        // List of URLs in the target 'site'
	private HashSet<String> ignoreURLs;      // List of URLs that should be ignored
	
	FileWriter writer;
	
	private Spider()
	{
		whitelistedURLs = new HashSet<String>();
		siteURLs = new HashSet<String>();
		ignoreURLs = new HashSet<String>();
		SetupFile();
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
	
	@Override
	protected void finalize() throws Throwable {
		try
		{
			CloseFile();
		} catch (Throwable t)
		{
			throw t;
		} finally {
			super.finalize();
		}
	}
	
	/**
	 * Add a URL that is 'safe' for the spider to visit
	 * Any site visited that isn't in the whitelist will cause an error to be generated
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
	 * Adds a URL to the ignore list. If a target URL matches this list, it won't be considered
	 * 
	 * @param ignoreURL
	 */
	public void addIgnoreURL(String ignoreURL)
	{
		if (ignoreURL == null || ignoreURL.isEmpty())
			throw new InvalidParameterException("Ignore URL must not be null or empty");
		
		ignoreURLs.add(ignoreURL);
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
		
		Dictionary words = new Dictionary();
		
		Queue<Strand> toVisitURLs = new LinkedBlockingQueue<Strand>();
		HashSet<String> visitedURLs = new HashSet<String>();

		WebDriver driver = new FirefoxDriver();
		
		toVisitURLs.add(new Strand ("", startURL));
		
		while (toVisitURLs.isEmpty() == false)
		{
			Strand visit = toVisitURLs.poll();
			//System.out.println("Spider is now considering visiting:" + visit);
			//if (visit.toLowerCase().contains("facebook"))
			//{
			//	System.out.println("Visiting Facebook");
			//}
			
			// Check if we've visited this URL already
			if (visitedURLs.contains(visit.getDestination()))
			{
				//System.out.println("Spider already visited that.")
				;
			}
			else
			{
				// Haven't visited it
				
				//System.out.println("Site matching?");
				// Is it a site URL that we have to actually visit?
				if (hashSetPartialMatch(siteURLs, visit.getDestination()))
				{
					// Verify the link works 
					if (checkLinkBroken(visit.getDestination()) == false)
					{
						errors = reportErrors("Tried to visit broken link: " + visit + "\n", errors);
						//errors += "Tried to visit broken link: " + visit + "\n";
					}
					
					//System.out.println("Spider is now visiting: " + visit);
					driver.get(visit.getDestination());
					
					try {
						Thread.sleep(waitTimeInMilliseconds);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					//System.out.println(" Recognized site URL, spidering through page");
					
					// Scan page for images and links to check
					scanPage(driver, toVisitURLs, "a", "href");
					
					scanPage(driver, toVisitURLs, "img", "src");
					
					getWordsFromPage(driver, words);
					
					// Verify we got redirected to the right place
					if (driver.getCurrentUrl().equals(visit.getDestination()) == false)
					{
						errors = reportErrors("Link did not lead to where expected.\n" +
					              "Current URL: " + driver.getCurrentUrl() + " link was " + visit.getDestination() + " source was " + visit.getSource() + "\n", errors);
						//errors += "Link did not lead to where expected.\n" +
					    //          "Current URL: " + driver.getCurrentUrl() + " link was " + visit.getDestination() + "|\n";
					}
					
					visitedURLs.add(visit.getDestination());
				}
				else
				{
					// Make sure the link is still valid.
					//System.out.println("Please implement verify link works");
					if (checkLinkBroken(visit.getDestination()) == false)
					{
						//System.out.println("DEBUG: Broken link found.");
						errors = reportErrors("Broken link found: " + visit + "\n", errors);
						//errors += "Broken link found: " + visit + "\n";
					}
					
					//if (visit.toLowerCase().contains("facebook"))
					//{
					//	System.out.println("In the whitelist stuff facebook	");
					//}
					
					//System.out.println("WhiteListMatch?");
					// Is it in the whitelist?
					if (hashSetPartialMatch(whitelistedURLs, visit.getDestination()) == true)
					{
						//System.out.println("Targeted url is whitelisted: " + visit);
					}
					else
					{
						errors = reportErrors("Non-whitelisted URL was linked: " + visit + "\n", errors);
						//errors += "Non-whitelisted URL was linked: " + visit + "\n";
					}
					
					visitedURLs.add(visit.getDestination());
				}
			}
		}
		
		if (driver != null)
		{
			driver.close();
			driver.quit();
		}
		
		// Process word output
		writeWordsToFile(words);
		
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
	
	// Perform a REST call to see if a link works
	private boolean checkLinkBroken(String url)
	{
		HttpURLConnection connection;
		
		//System.out.println("DEBUG: checkLinkBroken visiting " + url);
		
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			int response = connection.getResponseCode();
			
			//System.out.println("DEBUG: Response code: " + response);
			
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
			e.printStackTrace();
			return false;
		}		
	}

	// Scans the current driver page, extracting out URLs
	private void scanPage(WebDriver driver, Queue<Strand> toVisitURLs, String tagName, String targetAttribute)
	{
		List<WebElement> links = driver.findElements(By.tagName(tagName));
		
		for (WebElement link : links)
		{
			try
			{
				String target = link.getAttribute(targetAttribute);
				
				//System.out.println("  Spider found possible link to visit: " + target);
				
				if (target != null && 
					(target.startsWith("http://") || target.startsWith("https://")) &&
					hashSetPartialMatch(ignoreURLs, target) == false
				   )
				{
					//System.out.println("  Valid link found, adding to visit list: " + target);
					toVisitURLs.add(new Strand(driver.getCurrentUrl(),  target));
				}
			}
			catch (StaleElementReferenceException e)
			{
				// Don't really care about Stale Elements
				;
			}
		}
	}
	
	private void getWordsFromPage(WebDriver driver, Dictionary words) {
		Pattern alpha = Pattern.compile("[A-Za-z]");

		// Pull all text from the page
		String pageText;

		try
		{
			WebElement body = driver.findElement(By.tagName("body"));

			pageText = body.getText();

			// Split page text on common delimiters
			String[] splitPage = pageText.split("[\\s+/]");

			// Process splits
			for (String fragment : splitPage)
			{
				Matcher match = alpha.matcher(fragment);
				
				// Make sure the fragment contains at least one letter
				if (match.find() == false)
				{
					;
					//System.out.println("Non word found |" + fragment + "|");
				}
				else if (fragment.isEmpty() == false)
				{
					fragment = customTrim(fragment);
					
					words.addWord(fragment);
				}
			}
		}
		catch (NoSuchElementException|StaleElementReferenceException e)
		{
			// No body element, carry on
		}
	}
	
	private void SetupFile()
	{
		try
		{
			writer = new FileWriter(getFileName(), true);
		}
		catch (IOException e)
		{
			System.out.println("Failed to open file");
			e.printStackTrace();
		}		
	}
	
	private String getFileName()
	{
		LocalDateTime date = LocalDateTime.now();
		DateTimeFormatter format = DateTimeFormatter.ofPattern("'Date'ddMMMyyyy.'Time'H.m.s.S");	
		
		return "SpiderRun" + date.format(format) + ".txt";
	}
	
	private String reportErrors(String errorToReport, String existingErrors)
	{
		if (writer != null)
		{
			try {
				writer.write(errorToReport);
				//writer.write(System.getProperty( "line.separator" ));
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return existingErrors + errorToReport;
	}
	
	private void writeWordsToFile(Dictionary words) {
		if (writer != null)
		{
			try {
				writer.write(words.getAllWordsFormatted());
				writer.flush();
			} catch (IOException e) {
				System.out.println("Could not write out dictionary words.");
				e.printStackTrace();
			}
			
		}
	}
	
	private void CloseFile()
	{
		System.out.println("Commence closing");
		if (writer != null)
		{
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String customTrim(String toTrim)
	{	
		int begin = -1;
		int end = -1;
		
		for (int i = 0; i < toTrim.length() && begin == -1; i++)
		{
			if (Character.isAlphabetic(toTrim.charAt(i)))
			{
				begin = i;
			}
		}
		
		for (int i = (toTrim.length() - 1); i >= 0 && end == -1; i--)
		{
			if (Character.isAlphabetic(toTrim.charAt(i)))
			{
				end = i;
			}
		}
		
		if (end <= begin)
			return "";
		else
			return toTrim.substring(begin, end + 1);
	}
}
