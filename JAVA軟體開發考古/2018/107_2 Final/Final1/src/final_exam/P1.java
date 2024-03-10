package final_exam;

import java.util.Scanner;

public class P1 {
	public static void main(String[] args) {
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in); 
		int number = scanner.nextInt();
		if(number < 2 || number > 100) {
			return;
		}
		else {
			for (int i = 0; i < number; i++) {
				for(int j = 0; j < 2*number-1; j++) {
					if(i == number - 1) {
						System.out.print('*');
					}
					else if(j == number-1-i || j == number-1+i) {
						System.out.print('*');
					}
					else {
						System.out.print('.');
					}
				}
				System.out.print('\n');
			}
		}
	}
}
