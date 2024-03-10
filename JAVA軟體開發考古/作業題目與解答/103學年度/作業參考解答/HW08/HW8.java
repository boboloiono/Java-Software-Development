import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;


public class HW8 {
	
	public static void main(String[] args) {
		try (Scanner scanner = new Scanner(new FileInputStream(args[0]));
			 BufferedWriter writer = new BufferedWriter(new FileWriter(args[1]))) {
			while (scanner.hasNextLine()) {
				double[] numbers = new double[3];
				for (int i=0; i<numbers.length; i++)
					numbers[i] = scanner.nextDouble();
				writer.write(compute(numbers));
				writer.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static String compute(double[] numbers) {
		double a = numbers[0];
		double b = numbers[1];
		double c = numbers[2];
		Double[] roots = new Double[2];
		
		Double discriminant = Math.pow(b,2) - 4*a*c;
		if (discriminant < 0) {
			return "Roots are imaginary";
		} else {
			roots[0] = (-b + Math.sqrt(discriminant)) / (2*a);
			roots[1] = (-b - Math.sqrt(discriminant)) / (2*a);
			Arrays.sort(roots, Collections.reverseOrder());
			return String.format("(%.5f, %.5f)", roots[0], roots[1]);
		}
	}
}
