package hw6;

public class Comparision implements IOperation{
	public String perform(String num1, String num2) {
		if(num1.charAt(0) != '-')
			num1 = "+" + num1;
		if(num2.charAt(0) != '-')
			num2 = "+" + num2;
		int max_length = Math.max(num1.length(), num2.length()) + 1;
		char[] num1_arr = new char[max_length];
		char[] num2_arr = new char[max_length];
		int index1 = num1.length() - 1;
		int index2 = num2.length() - 1;
		for(int i = max_length - 1; i >= 1; i--) {
			if(index1 > 0) {
				num1_arr[i] = num1.charAt(index1);
				index1--;
			}
			else
				num1_arr[i]= '0'; 
			if(index2 > 0) {
				num2_arr[i] = num2.charAt(index2);
				index2--;
			}
			else
				num2_arr[i]= '0'; 
		}
		num1_arr[0] = num1.charAt(0);
		num2_arr[0] = num2.charAt(0);
		if((num1.charAt(0) == '+' && num2.charAt(0) == '+')) {
			for(int i = 1; i < max_length; i++) {
				if(Character.getNumericValue(num1_arr[i]) > Character.getNumericValue(num2_arr[i]))
					return "1";
				else if(Character.getNumericValue(num1_arr[i]) < Character.getNumericValue(num2_arr[i]))
					return "-1";
				else 
					if(i == max_length - 1) 
						return "0";
			}
		}
		else if(num1.charAt(0) == '-' && num2.charAt(0) == '-') {
			for(int i = 1; i < max_length; i++) {
				if(Character.getNumericValue(num1_arr[i]) < Character.getNumericValue(num2_arr[i]))
					return "1";
				else if(Character.getNumericValue(num1_arr[i]) > Character.getNumericValue(num2_arr[i]))
					return "-1";
				else 
					if(i == max_length - 1) 
						return "0";
			}
		}
		else {
			if(num1.charAt(0) == '+' && num2.charAt(0) == '-')
				return "1";
			else 
				return "-1";
		}
		return "1"; // Useless
	}
}
