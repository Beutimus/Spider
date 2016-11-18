package Tests;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.junit.Assert;
import org.testng.annotations.Test;

public class AudioFileTests {

	public static String somePathName = "C:/Users/Owner/workspace/STGSpider/Recording.aiff";

	@Test
	public void audioTestMassRead()
	{
		int totalFramesRead = 0;
		File fileIn = new File(somePathName);

		// somePathName is a pre-existing string whose value was
		// based on a user selection.
		try 
		{
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(fileIn);
			int bytesPerFrame = audioInputStream.getFormat().getFrameSize();

			if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
				// some audio formats may have unspecified frame size
				// in that case we may read any amount of bytes
				bytesPerFrame = 1;
			} 
			
			System.out.println("Bytes per frame: " + bytesPerFrame);

			// Set an arbitrary buffer size of 1024 frames.
			int numBytes = 1024 * bytesPerFrame; 
			byte[] audioBytes = new byte[numBytes];

			try {
				int numBytesRead = 0;
				int numFramesRead = 0;
				// Try to read numBytes bytes from the file.
				
				while ((numBytesRead = audioInputStream.read(audioBytes)) != -1) {
					// Calculate the number of frames actually read.
					numFramesRead = numBytesRead / bytesPerFrame;
					totalFramesRead += numFramesRead;
					// Here, do something useful with the audio data that's 
					// now in the audioBytes array...
					
					for (byte bytes : audioBytes)
					{
						System.out.print(bytes + " ");
					}
					
					System.out.println("");
					
					
				}
			} catch (Exception ex) { 
				ex.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		System.out.println("Total frames read: " + totalFramesRead);
	}
	
	@Test
	public void audioTestFrameByFrameRead()
	{
		int totalFramesRead = 0;
		File fileIn = new File(somePathName);

		// somePathName is a pre-existing string whose value was
		// based on a user selection.
		try 
		{
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(fileIn);
			int bytesPerFrame = audioInputStream.getFormat().getFrameSize();

			if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
				// some audio formats may have unspecified frame size
				// in that case we may read any amount of bytes
				bytesPerFrame = 1;
			} 
			
			System.out.println("Bytes per frame: " + bytesPerFrame);

			// Set an arbitrary buffer size of 1024 frames.
			int numBytes = 1024 * bytesPerFrame; 
			byte[] audioBytes = new byte[numBytes];

			try {
				int numBytesRead = 0;
				int numFramesRead = 0;
				// Try to read numBytes bytes from the file.
				
				while ((numBytesRead = audioInputStream.read(audioBytes, 0, bytesPerFrame)) != -1) {
					//System.out.println("Frame");
					// Calculate the number of frames actually read.
					numFramesRead = numBytesRead / bytesPerFrame;
					totalFramesRead += numFramesRead;
					// Here, do something useful with the audio data that's 
					// now in the audioBytes array...
					
					for (byte bytes : audioBytes)
					{
						//System.out.print(Byte.toString(bytes) + " |");
					}
					
					//System.out.println("");
					
					
				}
			} catch (Exception ex) { 
				ex.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		System.out.println("Total frames read: " + totalFramesRead);
	}

	
}
