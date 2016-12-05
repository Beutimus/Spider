package Tests;

import org.testng.annotations.Test;

import Spider.Spider;

public class SkiUtahTest {
	
	// Dictionary object
	// Can pull text from spans, from divs?
	// Look up getText from Div from Bukoos
	// Can pull text from links?
	
	@Test
	public void SkiUtahSite()
	{
		Spider spider = new Spider("https://www.skiutah.com/");
		
		spider.addSiteURL("https://www.skiutah.com/");
		
		String errors = spider.walkSite();
		
		System.out.println("Errors:\n" + errors);
	}

}
