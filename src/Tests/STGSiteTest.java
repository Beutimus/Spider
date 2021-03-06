package Tests;

import org.testng.*;
import org.testng.annotations.*;

import Spider.Spider;

public class STGSiteTest {
	
	@Test
	public void STGSite()
	{
		Spider spider = new Spider("http://www.stgconsulting.com");
		
		spider.addSiteURL("stgconsulting.com");
		spider.addWhiteListURL("www.softwaretechnologygroup.com");
		spider.addWhiteListURL("farm3.staticflickr.com");
		spider.addWhiteListURL("farm4.staticflickr.com");
		spider.addIgnoreURL("&");
		spider.addIgnoreURL("_page_id"); 
		
		String errors = spider.walkSite();
		
		System.out.println("Errors:\n" + errors);
	}
	
	@Test
	public void InvalidSite()
	{
		Spider spider = new Spider("http://www.assdfsdfsdfdsdf.com/");
		
		spider.addSiteURL("http://www.assdfsdfsdfdsdf.com/");
		
		String errors = spider.walkSite();
		
		System.out.println("Errors:\n" + errors);
	}
}
