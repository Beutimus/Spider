package Spider;

public class Strand {
	private final String source;
	private final String destination;
	private final boolean linkIsImage;
	
	public Strand(String s, String d, boolean l)
	{
		source = s;
		destination = d;
		linkIsImage = l;
	}

	public String getSource() { return source; }
	public String getDestination() { return destination; }
	public boolean isImageLink() {return linkIsImage; }
	
	@Override
	public String toString() {
		return "Source: " + source + " Destination: " + destination;
	}
}
