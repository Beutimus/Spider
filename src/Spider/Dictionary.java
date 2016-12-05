package Spider;

import java.util.Set;
import java.util.TreeSet;

public class Dictionary {
	
	Set<String> words;
	
	Dictionary()
	{
		words = new TreeSet<String>();
	}
	
	public void addWord(String word)
	{
		words.add(word.toLowerCase().trim());
	}
	
	public String getAllWordsFormatted()
	{
		String output = "";
		
		for (String word : words)
		{
			output += word + "\r\n";
		}
		
		return output;
	}
}
