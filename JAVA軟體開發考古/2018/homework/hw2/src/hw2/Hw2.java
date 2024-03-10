package hw2;

import java.lang.Math;
import java.util.Scanner;
import java.text.DecimalFormat;

public class Hw2 {
	public static void main(String[] args) {
		double a, b, c, root1, root2;
		String output_format;
		@SuppressWarnings("resource")
		Scanner inputScanner = new Scanner(System.in);
		a = inputScanner.nextDouble();
		b = inputScanner.nextDouble();
		c = inputScanner.nextDouble();
		output_format = inputScanner.next();
		DecimalFormat format = new DecimalFormat(output_format);
		if(a != 0)
		{
			root1 = (-b + Math.sqrt(Math.pow(b, 2) - (4 * a * c))) / (2 * a);
			root2 = (-b - Math.sqrt(Math.pow(b, 2) - (4 * a * c))) / (2 * a);
			if(root1 == -0)
				root1 = 0;
			if(root2 == -0)
				root2 = 0;
			if(a > 0)
			{
				System.out.println(format.format((root1 > root2) ? root1 : root2));
				System.out.print(format.format((root1 < root2) ? root1 : root2));
			}
			else 
			{
				System.out.println(format.format((root1 < root2) ? root1 : root2));
				System.out.print(format.format((root1 > root2) ? root1 : root2));
			}
		}
		else 
		{
			if((c / -b) == -0)
				System.out.println(format.format(-c / -b));
			else 
				System.out.println(format.format(c / -b));
		}
	}
}
