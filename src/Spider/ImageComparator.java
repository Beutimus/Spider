package Spider;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageComparator {

	/**
	 * Given two valid file paths to images, it will return a percentage difference between the two images
	 * 
	 * 1 = images exactly match
	 * 0 = images don't match at all
	 * -1 = error occurred
	 * 
	 * @param baseFilePath
	 * @param toCompareFilePath
	 * @return float value representing the difference between the images.
	 */
	public static float compare(String baseFilePath, String toCompareFilePath)
	{
		// Set up files
		File baseImage = null;
		File compareImage = null;

		baseImage = new File(baseFilePath);
		compareImage = new File(toCompareFilePath);

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
			
			return differentPercent;


		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}
}
