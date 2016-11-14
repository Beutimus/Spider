package Tests;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class JPEGComparison {

	static String baseFile = "C:/Users/Owner/workspace/STGSpider/rawKitten.jpg";
	static String quality10FilePath = "C:/Users/Owner/workspace/STGSpider/rawKitten10.jpg";
	static String quality30FilePath = "C:/Users/Owner/workspace/STGSpider/rawKitten30.jpg";
	static String quality70FilePath = "C:/Users/Owner/workspace/STGSpider/rawKitten70.jpg";
	static String quality100FilePath = "C:/Users/Owner/workspace/STGSpider/rawKitten100.jpg";
	
	@DataProvider(name="filePaths")
	public static Object[][] filePaths()
	{
		return new Object[][] {{quality10FilePath},{quality30FilePath},{quality70FilePath}, {quality100FilePath}, {baseFile}};
	}
	
	@Test(dataProvider = "filePaths")
	public void compareToBaseQuality(String filePath)
	{
		// Set up files
		File baseImage = null;
		File compareImage = null;
		
		baseImage = new File(baseFile);
		compareImage = new File(filePath);
		
		try {
			// Open files
			BufferedImage base = ImageIO.read(baseImage);
			BufferedImage compare = ImageIO.read(compareImage);
			
			// Read dimensions
			int baseHeight = base.getHeight();
			int baseWidth = base.getWidth();
			
			int compareHeight = compare.getHeight();
			int compareWidth = compare.getWidth();
			
			// Alert if dimensions mismatch
			if (baseHeight != compareHeight)
			{
				System.out.println("Height mismatch. Base: " + baseHeight + " Compare: " + compareHeight);
			}
			
			if (baseWidth != compareWidth)
			{
				System.out.println("Width mismatch. Base: " + baseWidth + " Compare: " + compareWidth);
			}
			
			// Find smallest comparison area
			// TODO Do we want to abort at this point?
			int minHeight = java.lang.Math.min(compareHeight, baseHeight);
			int minWidth = java.lang.Math.min(compareWidth, baseWidth);
			
			// Compare two images
			float differentCount = 0;
			float maxPixels = minHeight * minWidth;
			for (int h = 0; h < minHeight; h++)
			{
				for (int w = 0; w < minWidth; w++)
				{
					int baseRGB = base.getRGB(w, h);
					int compareRGB = compare.getRGB(w, h);
					
					//System.out.println("Base: " + baseRGB + " Compare " + compareRGB);
					
					if (baseRGB != compareRGB)
						differentCount++;
				}
			}
			
			float differentPercent = differentCount/maxPixels;
			
			System.out.println("Difference " + differentPercent + "%");
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
