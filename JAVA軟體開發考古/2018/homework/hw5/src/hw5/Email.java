package hw5;

public class Email extends Document{
	public String sender;
	public String recipient;
	public String title;
	public Email() {
		super();
	}
	public String toString() {
		String allText="";
		allText += ("From: " + getSender() + "\n");
		allText += ("To: " + getRecipient() + "\n");
		allText += ("Title: " + getTitle() + "\n");
		allText += super.text;
		return allText;
	}
	public void setSender(String theSender) {
		sender = theSender;
	}
	public String getSender() {
		return sender;
	}
	public void setRecipient(String theRecipient) {
		recipient = theRecipient;
	}
	public String getRecipient() {
		return recipient;
	}
	public void setTitle(String theTitle) {
		title = theTitle;
	}
	public String getTitle() {
		return title;
	}
}
