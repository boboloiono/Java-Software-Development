package hw5;

public class File extends Document{
	public String pathname;
	public File() {
		super();
	}
	public String toString() {
		String allText="";
		allText += ("Path: " + getPathname() + "\n");
		allText += super.text;
		return allText;
	}
	public void setPathname(String thePathname) {
		pathname = thePathname;
	}
	public String getPathname() {
		return pathname;
	}
}
