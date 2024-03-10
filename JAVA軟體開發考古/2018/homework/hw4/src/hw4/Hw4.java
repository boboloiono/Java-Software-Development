package hw4;

import java.text.DecimalFormat;
import java.util.Scanner;

public class Hw4 {
	public static void main(String[] args) {
		@SuppressWarnings("resource")
		Scanner inputScanner = new Scanner(System.in);
		DecimalFormat format = new DecimalFormat("#.##");
		double input = inputScanner.nextDouble();
		double guess = input / 2;
		double last_guess = guess;
		double diff = Double.MAX_VALUE;
		
		while(Math.abs(diff) >= 0.01) 
		{
			double r = input / guess;
			guess = (guess + r) / 2;
			diff = ((guess - last_guess) / last_guess);
			last_guess = guess;
		}
		System.out.println(format.format(guess));
	}
}