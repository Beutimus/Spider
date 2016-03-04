package Tests;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import Spider.Spider;

public class SpiderTests {
	
	@Test
	public void InvalidSite()
	{
		Spider spider = new Spider("http://www.assdfsdfsdfdsdf.com/");
		
		spider.addSiteURL("http://www.assdfsdfsdfdsdf.com/");
		
		String errors = spider.walkSite();
		
		System.out.println("Errors:\n" + errors);
	}
	
	@DataProvider(name = "brokenImages")
	public static Object[][] brokenImages() {
		return new Object[][] {{"file:/C:/Users/Owner/workspace/STGSpider/brokenAnimatedGIF.gif"},
			{"file:/C:/Users/Owner/workspace/STGSpider/brokenGIF.gif"},
			{"file:/C:/Users/Owner/workspace/STGSpider/brokenJPG.jpg"},
			{"file:/C:/Users/Owner/workspace/STGSpider/brokenPNG.png"}
		};
	}
	
	@Test(dataProvider = "brokenImages")
	public void BrokenImageTest(String brokenImage)
	{
		Spider spider = new Spider("https://test.nuskin.com/content/nuskin/zh_HK/corporate/nuexpress/updatednews/corporate/20141212_NSnews.html");
		
		//spider.addSiteURL("https://test.nuskin.com/content/nuskin/zh_HK/corporate/nuexpress/updatednews/corporate/20141212_NSnews.html");
		
		//String errors = spider.walkSite();
		
		//System.out.println("Errors:\n" + errors);
		
		if (spider.isImageLoadedCorrectly(brokenImage))
		{
			Assert.fail("Image should be broken");
			//System.out.println("Image is not broken");
		}
		else
		{
			//System.out.println("Image is broken");
		}
	}
	
	@DataProvider(name = "workingImages")
	public static Object[][] workingImages() {
		return new Object[][] {{"https://media0.giphy.com/media/10juQ7fAaQjuHS/200_s.gif"},
			{"https://upload.wikimedia.org/wikipedia/commons/5/55/Tesseract.gif"},
			{"https://pbs.twimg.com/profile_images/696759008527421440/ClSB1Oo__400x400.jpg"},
			{"https://upload.wikimedia.org/wikipedia/commons/thumb/e/e9/Felis_silvestris_silvestris_small_gradual_decrease_of_quality.png/240px-Felis_silvestris_silvestris_small_gradual_decrease_of_quality.png"}
		};
	}
	
	@Test(dataProvider = "workingImages")
	public void WorkingImageTest(String workingImage)
	{
		Spider spider = new Spider("https://test.nuskin.com/content/nuskin/zh_HK/corporate/nuexpress/updatednews/corporate/20141212_NSnews.html");
		
		//spider.addSiteURL("https://test.nuskin.com/content/nuskin/zh_HK/corporate/nuexpress/updatednews/corporate/20141212_NSnews.html");
		
		//String errors = spider.walkSite();
		
		//System.out.println("Errors:\n" + errors);
		
		if (spider.isImageLoadedCorrectly(workingImage))
		{
			
			//System.out.println("Image is not broken");
		}
		else
		{
			Assert.fail("Image should not be broken");
			//System.out.println("Image is broken");
		}
	}
}
