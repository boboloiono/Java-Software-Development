
public class HW1 {

	public static void main(String[] args) {
		String first = "jeremy";
		String last = "lin";
		String newFirst = 
				String.valueOf(first.charAt(1)).toUpperCase() +	// Capitalize the second character
				first.substring(2) + 							// Retrieve a substring begins at index 2 and extends to the end
				String.valueOf(first.charAt(0)) + 				// Get the first character as a string
				"ay";											// Append "ay" at the end
		String newLast =
				String.valueOf(last.charAt(1)).toUpperCase() + 
				last.substring(2) + 
				String.valueOf(last.charAt(0)) + 
				"ay";
		System.out.println(newFirst + " " + newLast);
	}
}
