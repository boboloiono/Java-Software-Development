package hw6;

public class Subtraction implements IOperation{
	public String perform(String num1, String num2) {
		String result = null;
		if(num1.charAt(0) != '-')
			num1 = "+" + num1;
//		System.out.println(num1);
		if(num2.charAt(0) != '-')
			num2 = "+" + num2;
//		System.out.println(num2);
		int max_length = Math.max(num1.length(), num2.length()) + 1;
		char[] num1_arr = new char[max_length];
		char[] num2_arr = new char[max_length];
		char[] ans = new char[max_length]; 
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
//		System.out.println(num1_arr);
//		System.out.println(num2_arr);
		int sub = 0;
		if(num1.charAt(0) == '+' && num2.charAt(0) == '+') {
			IOperation compare = new Comparision();
			String new_num1 = new String(num1.substring(1));
			String new_num2 = new String(num2.substring(1));
			if(compare.perform(new_num1, new_num2) == "1") {
				for(int i = max_length - 1; i > 0; i--) {
					sub = Character.getNumericValue(num1_arr[i]) - Character.getNumericValue(num2_arr[i]); 
					if(sub < 0) {
						sub += 10;
						num1_arr[i - 1] -= 1;
					}	
					ans[i] = Character.forDigit(sub, 10);
				}
				ans[0] = num1.charAt(0);
			}
			else {
				for(int i = max_length - 1; i > 0; i--) {
					sub = Character.getNumericValue(num2_arr[i]) - Character.getNumericValue(num1_arr[i]); 
					if(sub < 0) {
						sub += 10;
						num2_arr[i - 1] -= 1;
					}	
					ans[i] = Character.forDigit(sub, 10);
				}
				ans[0] = '-';
			}
			result = String.copyValueOf(ans);
			if(result.charAt(0) != '-') {
				for(int i = 1; i < result.length(); i++) {
					if(result.charAt(i) != '0') {
						result = result.substring(i);
						break;
					}
				}
			}
			else {
				for(int i = 1; i < result.length(); i++) {
					if(result.charAt(i) != '0') {
						result = result.substring(i);
						result = "-" + result;
						break;
					}
				}
			}
			if(result.charAt(result.length()-1) == '0')
				result = "0";
		}
		else if(num1.charAt(0) == '-' && num2.charAt(0) == '-') {
			IOperation subtract = new Subtraction();
			String new_num1 = new String(num1.substring(1));
			String new_num2 = new String(num2.substring(1));
			result = subtract.perform(new_num2, new_num1);
		}
		else {
			IOperation addition = new Addition();
			String new_num1 = new String(num1.substring(1));
			String new_num2 = new String(num2.substring(1));
			result = addition.perform(new_num1, new_num2);
			if(num1.charAt(0) == '-')
				result = "-" + result; 
		}
		return result;
	}
}
