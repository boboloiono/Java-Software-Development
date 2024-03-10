package hw5;

import java.util.Scanner;

public class Hw5 {
	public static void main(String[] args) {
		Document document = new Document();
		Email email = new Email();
		File file = new File();;
		@SuppressWarnings("resource")
		Scanner inputScanner = new Scanner(System.in);
		String content, sender, recipient, title, path, keyword;
		String mode = inputScanner.nextLine();
		String type = inputScanner.nextLine();
		if(type.equals("Document")) {
			content = inputScanner.nextLine();
			document.setText(content);
		}
		else if(type.equals("Email")){
			sender = inputScanner.nextLine();
			recipient = inputScanner.nextLine();
			title = inputScanner.nextLine();
			content = inputScanner.nextLine();
			email.setSender(sender);
			email.setRecipient(recipient);
			email.setTitle(title);
			email.setText(content);
		}
		else {
			path = inputScanner.nextLine();
			content = inputScanner.nextLine();
			file.setPathname(path);
			file.setText(content);
		}		
		if(mode.equals("A")) {
			if(type.equals("Document"))
				System.out.println(document.toString());
			else if(type.equals("Email"))
				System.out.println(email.toString());
			else 
				System.out.println(file.toString());
		}
		else if(mode.equals("B")){
			keyword = inputScanner.next();
			if (type.equals("Email")) {
				if((email.getSender().indexOf(keyword) != -1) || (email.getRecipient().indexOf(keyword) != -1) || (email.getTitle().indexOf(keyword) != -1) || (content.indexOf(keyword) != -1))
					System.out.println("true");
				else
					System.out.println("false");
			}
			else {
				if(content.indexOf(keyword) != -1) 
					System.out.println("true");
				else 
					System.out.println("false");
			}
		}
		else {
			if(type.equals("Document")) {
				String text = inputScanner.nextLine();
				String revise = inputScanner.nextLine();
				if(text.equals("text"))
					document.setText(revise);
				System.out.println(document.toString());
			}
			else if(type.equals("Email")){
				String text = inputScanner.nextLine();
				String revise = inputScanner.nextLine();
				if(text.equals("text"))
					email.setText(revise);
				else if(text.equals("sender"))
					email.setSender(revise);
				else if(text.equals("recipient"))
					email.setRecipient(revise);
				else 
					email.setTitle(revise);
				System.out.println(email.toString());
			}
			else {
				String text = inputScanner.nextLine();
				String revise = inputScanner.nextLine();
				if(text.equals("text"))
					file.setText(revise);
				else
					file.setPathname(revise);
				System.out.println(file.toString());
			}
		}
	}
}
