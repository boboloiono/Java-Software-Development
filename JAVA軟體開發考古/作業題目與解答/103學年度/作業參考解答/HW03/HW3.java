import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;


public class HW3 {
	public static void main(String[] args) {
		try (Scanner scanner = new Scanner(new FileInputStream("file.txt"))) {
			// Read each line of the file
			while (scanner.hasNextLine()) {
				String equation = scanner.nextLine();
				String[] terms = equation.split("[+]|[=]");	// Each term of the equation
				// Put each alphabetic character into charMap
				Map<Character, Integer> charMap = new TreeMap<>();	// Map each unique character to a digit
				for (char c : equation.toCharArray())
					if (Character.isAlphabetic(c))
						charMap.put(c, 0);
				// Construct a char array and filled with '0'. The array's length is the size of charMap.
				char[] initCounter = new char[charMap.size()];	// Iteration counter (init value)
				Arrays.fill(initCounter, '0');
				String pattern = new String(initCounter);	// For DecimalFormat
				String counter = new String(initCounter);	// Iteration counter 
				// Start finding a solution until a solution is found, or counter overflow (i.e. no solution)
				do {
					// Assign values to each char in charMap
					int index = 0;
					for (Entry<Character,Integer> entry : charMap.entrySet())
						entry.setValue(counter.charAt(index++)-'0');
					// counter += 1
					counter = addOne(counter, pattern);
				} while (!isSolutionFound(terms, charMap) && counter.length()==initCounter.length);
				// Print out the solution
				for (Entry<Character, Integer> entry : charMap.entrySet())
					System.out.print(String.format("(%c,%d); ", entry.getKey(), entry.getValue()));
				System.out.println();
			}
		} catch (FileNotFoundException e) {	
			e.printStackTrace();
		}
	}
	
	/**
	 * value += 1 (ex: 001 +1 = 002)
	 * @param value The value to be added
	 * @param pattern The pattern for DecimalFormat (ex: "000")
	 * @return value+1
	 */
	static String addOne(String value, String pattern) {
		// Use DecimalFormat to keep leading zero of value (ex: 001 instead of 1)
		NumberFormat formatter = new DecimalFormat(pattern);
		return formatter.format(Integer.parseInt(value)+1);
	}
	
	/**
	 * Tests if a solution is found.
	 * @param terms Each term of the equation
	 * @param charMap
	 * @return True if a solution if found, otherwise false.
	 */
	static boolean isSolutionFound(String[] terms, Map<Character, Integer> charMap) {
		// Use Set to test if all values in charMap are unique
		Set<Integer> set = new HashSet<>();
		for (Entry<Character, Integer> entry : charMap.entrySet())
			set.add(entry.getValue());
		if (set.size() != charMap.size())
			return false;
		
		int left = 0;	// Left-side  of the equation (addends)
		int right = 0;	// Right-side of the equation (sum)
		// Evaluate the equation
		for (int i=0; i<terms.length; i++) {
			char[] eachTerm = terms[i].trim().toCharArray();
			// The first letter cannot be 0
			if (charMap.get(eachTerm[0]) == 0)
				return false;
			// Convert letters to digits
			StringBuilder builder = new StringBuilder();
			for (char c : eachTerm)
				builder.append(charMap.get(c));
			if (i < terms.length-1)	// Sum up left side terms (addends)
				left += Integer.parseInt(builder.toString());
			else					// The last term is the sum of the equation
				right = Integer.parseInt(builder.toString());
		}
		return (left==right) ? true : false;
	}
}

/* 
 * 程式流程大概是：
 * 一行一行的讀equation(簡稱eq)進來，然後用String.split()記錄eq的每個term。
 * 例如: A+B=C，則terms={A,B,C}。最後一個term是總和，之外的都是加數。(接下來的eq都以此為例)
 * 接著，把eq裡面所有的字元push到TreeMap裡。TreeMap的所有key是唯一的，然後會自動幫你排序好。
 * 
 * 解法是用窮舉法，例如有3個獨立字元，則依序從000到999賦予每個字元一個值，然後再看這個值有沒有符合題目限制以及解出eq。
 * 我先創一個char陣列initCounter，然後初值設成"000"，之後兩個字串pattern和counter的初值都是"000"。
 * pattern是給DecimalFormat用來格式化counter用的，目的是要保留leading zeros。
 * counter是給接下來的do-while迴圈用的，每次迴圈counter值都會加1。
 * 迴圈的終止條件是找出解答，或是counter溢位，也就是counter超出999，此時counter的長度就不是3了。
 * 在do-while迴圈中，每次都會先把counter裡的每個字元轉成數字後，再依序設給charMap裡的每個key。
 * 例如，1st: {A=0,B=0,C=0}; 2nd: {A=0,B=0,C=1}; 依此類推。之後每次迴圈都會判斷這些值是不是解答。
 * 
 * 在isSolutionFound()中，首先去判斷charMap裡的value有沒有重複，也就是有沒有兩個letter對應到同一個digit。
 * 在這邊我利用Set的特性，首先把charMap裡的每個值丟進Set，
 * 如果最後Set的大小不等於charMap的大小的話，代表有值重複了，才不能加進Set裡。
 * 接著開始去計算eq的值，首先確認eq中的每個term的第一個字對應到的digit不能是0。
 * 接下來把每個term轉成數字並加總，最後一個term是eq的右式(總和)，之前的term都是eq的左式(加數)。
 * 最後去判斷左式是不是等於右式，如果是的話代表找到答案了。
 */
