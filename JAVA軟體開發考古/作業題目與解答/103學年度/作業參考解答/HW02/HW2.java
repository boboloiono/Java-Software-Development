import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class HW2 {

	public static void main(String[] args) {
		String data = null;	// File content
		int numLines = 0;	// Number of lines of the file
		// Input file name
		Scanner scanner = new Scanner(System.in);
		String fileName = scanner.nextLine();
		// Read file
		try {
			scanner = new Scanner(new FileInputStream(fileName));
			StringBuilder dataBuilder = new StringBuilder();
			while (scanner.hasNext()) {
				dataBuilder.append(scanner.nextLine()).append("\n");
				numLines++;
			}
			scanner.close();
			data = dataBuilder.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}
		// Data processing
		data = data.replaceAll("(?i)java", "Java Software Development");
		// If the file has even number of lines, index is set to data.lastIndexOf("hate"); 
		// otherwise, index is set to data.indexOf("hate").
		int index = (numLines%2 == 0) ? data.lastIndexOf("hate") : data.indexOf("hate");
		System.out.print(new StringBuilder(data).replace(index, index+"hate".length(), "love"));
	}
}
