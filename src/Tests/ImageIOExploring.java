package Tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class ImageIOExploring {

	public final String jpgURL = "https://upload.wikimedia.org/wikipedia/commons/d/db/Patern_test.jpg";
	
	@Test
	public void testVariousGets()
	{
		Iterator<ImageReader> test = ImageIO.getImageReadersByFormatName("jpg");
		
		while (test.hasNext())
		{
			ImageReader reader = test.next();
			System.out.println("Reader1");
			//System.out.println(reader.getFormatName());
			System.out.println(reader.toString());
			System.out.println(reader.getClass());
		}

		test = ImageIO.getImageReadersBySuffix(".jpg");
		
		while (test.hasNext())
		{
			ImageReader reader = test.next();
			System.out.println("Reader2");
			//System.out.println(reader.getFormatName());
			System.out.println(reader.toString());
			System.out.println(reader.getClass());
		}
		
		for(String suffix : ImageIO.getReaderFileSuffixes())
		{
			System.out.println("Suffix: " + suffix);
		}
		
		for(String format : ImageIO.getReaderFormatNames())
		{
			System.out.println("Format: " + format);
		}
		
		for(String mime : ImageIO.getReaderMIMETypes())
		{
			System.out.println("MIME: " + mime);
		}
	}
	
	@Test
	public void readJPEG()
	{
		String imageURL = this.jpgURL;
		
		String fileSuffix = imageURL.substring(imageURL.lastIndexOf('.') + 1);
		
		Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName(fileSuffix);
		
		if (it.hasNext() == false)
			Assert.fail("No image readers found for URL " + imageURL);
		
		ImageReader reader = it.next();

		try {
			URL target = new URL(jpgURL);
			BufferedImage img = ImageIO.read(target);
			
			System.out.println(img.getType());
			
			if (img.getPropertyNames() != null)
			{
				for(String prop : img.getPropertyNames())
				{
					System.out.println("Prop: " + prop);
				}
			}
			
			//reader.setInput(img);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
}
