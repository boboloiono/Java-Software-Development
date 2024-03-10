import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class HW7 {

	public static void main(String[] args) {
		Map<String, String> comparisonTypeMap = new HashMap<>();
		comparisonTypeMap.put("<", "-1");
		comparisonTypeMap.put("=", "0");
		comparisonTypeMap.put(">", "1");
		
		BigIntegerCalculator calculator = new BigIntegerCalculator();
		Addition addition = new Addition();
		Subtraction subtraction = new Subtraction();
		Comparison comparison = new Comparison();
		
		try (Scanner scanner = new Scanner(new FileInputStream(args[0]))) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] tokens = line.split("\\s+");
				String num1 = null;
				String comparisonType = null;
				for (int i=0; i<tokens.length; i++) {
					String token = tokens[i];
					switch (token) {
						case "+": 
							calculator.operation = addition; 
							break;
						case "-": 
							calculator.operation = subtraction; 
							break;
						case ">":
						case "<":
						case "=": 
							calculator.operation = comparison;
							comparisonType = token;
							break;
						default:
							if (num1 == null)
								num1 = token;
							else {
								String num2 = token;
								String result = calculator.operation.perform(num1, num2).toString();
								// If encountered the last token, print out the result
								if (i == tokens.length-1) {
									if (calculator.operation instanceof Comparison)
										System.out.println(result.equals(comparisonTypeMap.get(comparisonType)) ? "true" : "false");
									else
										System.out.println(result);
								} else {
									num1 = result;
								}
							}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}

class BigIntegerCalculator {
	IOperation operation;
}

interface IOperation {
	Object perform(Object o1, Object o2);
}

class Addition implements IOperation {
	@Override
	public Object perform(Object o1, Object o2) {
		String num1 = o1.toString();
		String num2 = o2.toString();
		boolean num1_negative = (num1.charAt(0)=='-') ? true : false;
		boolean num2_negative = (num2.charAt(0)=='-') ? true : false;
		
		// +a + -b => a - b
		if (!num1_negative && num2_negative)
			return new Subtraction().perform(num1, num2.substring(1));
		// -a + +b => b - a
		if (num1_negative && !num2_negative)
			return new Subtraction().perform(num2, num1.substring(1));
		
		num1 = new StringBuilder(num1_negative ? num1.substring(1) : num1).reverse().toString();
		num2 = new StringBuilder(num2_negative ? num2.substring(1) : num2).reverse().toString();
		
		int length = (num1.length() >= num2.length()) ? num1.length() : num2.length();
		int[] num = new int[length + 1];
		
		for (int i=0; i<length; i++) {
			if (i >= num1.length())
				num[i] += (num2.charAt(i)-'0');
			else if (i >= num2.length())
				num[i] += (num1.charAt(i)-'0');
			else
				num[i] += (num1.charAt(i)-'0') + (num2.charAt(i)-'0');
			num[i+1] += num[i] / 10;
			num[i] %= 10;
		}
		
		char[] result = new char[num.length];
		for (int i=0; i<result.length; i++)
			result[i] = (num[i]!=Integer.MIN_VALUE) ? (char)(num[i]+'0') : ' ';
		String resultString = new StringBuilder(new String(result)).reverse().toString().replaceFirst("^0+", "");
		resultString = (num1_negative&&num2_negative) ? "-" + resultString : resultString;	// -a + -b => -(a + b)
		return (resultString.length()==0) ? "0" : resultString;
	}
}

class Subtraction implements IOperation {
	@Override
	public Object perform(Object o1, Object o2) {
		String num1 = o1.toString();
		String num2 = o2.toString();
		boolean num1_negative = (num1.charAt(0)=='-') ? true : false;
		boolean num2_negative = (num2.charAt(0)=='-') ? true : false;
		
		// +a - -b => a + b
		if (!num1_negative && num2_negative)
			return new Addition().perform(num1, num2.substring(1));
		// -a - +b => -(a + b)
		if (num1_negative && !num2_negative)
			return "-" + new Addition().perform(num1.substring(1), num2);
		// -a - -b => b - a
		if (num1_negative && num2_negative)
			return new Subtraction().perform(num2.substring(1), num1.substring(1));
		
		// Determine whether num1 < num2
		// If num1 < num2, do num2 - num1 and flip the sign.
		boolean flag = false;
		if ((int)new Comparison().perform(num1, num2) == -1) {
			flag = true;
			String temp = num1;
			num1 = num2;
			num2 = temp;
		}
		
		num1 = new StringBuilder(num1_negative ? num1.substring(1) : num1).reverse().toString();
		num2 = new StringBuilder(num2_negative ? num2.substring(1) : num2).reverse().toString();
		
		int length = (num1.length() >= num2.length()) ? num1.length() : num2.length();
		int[] num = new int[length];
		
		for (int i=0; i<length; i++) {
			if (i >= num1.length())
				num[i] += (num2.charAt(i)-'0');
			else if (i >= num2.length())
				num[i] += (num1.charAt(i)-'0');
			else
				num[i] += (num1.charAt(i)-'0') - (num2.charAt(i)-'0');
			if (num[i] < 0) {
				num[i+1]--;
				num[i] += 10;
			}
		}
		
		char[] result = new char[num.length];
		for (int i=0; i<result.length; i++)
			result[i] = (num[i]!=Integer.MIN_VALUE) ? (char)(num[i]+'0') : ' ';
		String resultString = new StringBuilder(new String(result)).reverse().toString().replaceFirst("^0+", "");
		resultString = flag ? "-" + resultString : resultString;
		return (resultString.length()==0) ? "0" : resultString;
	}
}

class Comparison implements IOperation {
	@Override
	public Object perform(Object o1, Object o2) {
		String num1 = o1.toString();
		String num2 = o2.toString();
		boolean num1_negative = (num1.charAt(0)=='-') ? true : false;
		boolean num2_negative = (num2.charAt(0)=='-') ? true : false;
		// num1 and num2 have opposite sign
		if (num1_negative && !num2_negative)
			return -1;
		if (!num1_negative && num2_negative)
			return 1;
		// num1 and num2 have the same sign
		if (num1.length() > num2.length())
			return num1_negative ? -1 : 1;
		else if (num1.length() < num2.length())
			return num1_negative ? 1 : -1;
		else {	// num1 and num2 have the same length
			for (int i=0; i<num1.length(); i++) {
				if ( (num1.charAt(i)-'0') > (num2.charAt(i)-'0') )
					return num1_negative ? -1 : 1;
				else if ( (num1.charAt(i)-'0') < (num2.charAt(i)-'0') )
					return num1_negative ? 1 : -1;
			}
		}
		return 0;
	}
}
