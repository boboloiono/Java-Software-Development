package hw6;

import java.util.Scanner;
import java.util.StringTokenizer;

public class hw6 {
	public static void main(String[] args) {
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String input = scanner.nextLine();
		StringTokenizer tokenizer = new StringTokenizer(input);
		String num1, num2, op, result = null;
		num1 = tokenizer.nextToken();
		while(tokenizer.hasMoreTokens()){
			op = tokenizer.nextToken();
			num2 = tokenizer.nextToken();
			if(op.equals("+")) {
				IOperation operation = new Addition();
				result = operation.perform(num1, num2);
				num1 = result;
			}
			else if (op.equals("-")) {
				IOperation operation = new Subtraction();
				result = operation.perform(num1, num2);
				num1 = result;
			}
			else {
				IOperation operation = new Comparision();
				if(op.equals(">")) {
					if(operation.perform(num1, num2).equals("1"))
						result = "true";
					else
						result = "false";
				}
				else if(op.equals("<")) {
					if(operation.perform(num1, num2).equals("-1"))
						result = "true";
					else
						result = "false";
				}
				else {
					if(operation.perform(num1, num2).equals("0"))
						result = "true";
					else
						result = "false";
				}
			}
			
		}
		
		
		System.out.println(result);

	}
}
