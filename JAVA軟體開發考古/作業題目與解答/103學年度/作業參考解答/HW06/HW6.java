import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Scanner;

public class HW6 {
	public static void main(String[] args) {
		InfixToPostfixConverter converter = new InfixToPostfixConverter();
		
		try (Scanner scanner = new Scanner(new FileInputStream(args[0]))) {
			while (scanner.hasNextLine()) {
				try {
					String postfix = converter.transform(scanner.nextLine());
					String value = converter.evaluate(postfix);
					if (value.equals("Invalid expression"))
						System.out.println("Invalid expression\nInvalid expression");
					else {
						System.out.println(postfix);
						System.out.println(value);
					}
				} catch (Exception e) {
					System.out.println("Invalid expression\nInvalid expression");
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}





class InfixToPostfixConverter {
	private final static char[] OPERATORS = {'+', '-', '*', '/', '(', ')'};
	
	// Convert from infix expr to postfix expr
	public String transform(String infix) throws Exception {
		StringBuilder postfix = new StringBuilder();
		Stack stack = new Stack(countOperatorNumber(infix));
		boolean lastTokenIsDigit = false; // the last element represents a digit
		int numOperand = 0;
 		int numOperator = 0;
		
		char[] infixChars = infix.toCharArray();
		for (int i=0; i<infixChars.length; i++) {
			char c = infixChars[i];
			switch (c) {
				case '+':	case '-':	case '*':	case '/':
					// c indicates a negative-sign
					if ( (c == '-') && (Character.isDigit(infixChars[i+1])) ) {
						postfix.append("-");
					} else {
						while ( !stack.isEmpty() && (precedence((char)stack.top()) <= precedence(c)) )
							postfix.append(" ").append(stack.pop());
						postfix.append(" ");
						stack.push(c);
					}
					numOperator++;
					lastTokenIsDigit = false;
					break;
				case '(':
					stack.push(c);
					break;
				case ')':
					while ((char)stack.top() != '(')
						postfix.append(" ").append(stack.pop());
					stack.pop(); // pop '('
					break;
				default:
					if (Character.isWhitespace(c)) {
						lastTokenIsDigit = false;
						continue;
					}
					if (!lastTokenIsDigit)
						numOperand++;
					if (numOperand > numOperator+1)
						throw new Exception();
					lastTokenIsDigit = true;
					postfix.append(c);
			}
		}
		while (!stack.isEmpty())
			postfix.append(" ").append(stack.pop());
		
		return postfix.toString();
	}
		
	// evaluate the postfix expression
	public String evaluate(String postfix) {
		String[] tokens = postfix.split("\\s+");
		Stack stack = new Stack(tokens.length);
		BigInteger result = null;
		
		for (String s : tokens) {
			if (s.matches("-*\\d+"))
				stack.push(s);
			else {
				if (stack.getElementNumber() < 2)
					return "Invalid expression";
				BigInteger num2 = new BigInteger(stack.pop().toString());
				BigInteger num1 = new BigInteger(stack.pop().toString());
				
				switch (s) {
					case "+":
						result = num1.add(num2);
						break;
					case "-":
						result = num1.subtract(num2);
						break;
					case "*":
						result = num1.multiply(num2);
						break;
					case "/":
						if (num2.equals(BigInteger.ZERO))
							return "Divide by zero";
						result = num1.divide(num2);
						break;
					default:
						return "Invalid expression";
				}
				if (isOverflow(num1) || isOverflow(num2) || isOverflow(result))
					return "Overflow";
				stack.push(result);
			}
		}
		
		result = new BigInteger(stack.pop().toString());
		return isOverflow(result) ? "Overflow" : result.toString();
	}
	
	// Count # of operators in the infix expression
	private int countOperatorNumber(String infix) {
		int count = 0;
		for (int i=0; i<infix.length(); i++) {
			for (char c : OPERATORS) {
				if (infix.charAt(i) == c)
					count++;
			}
		}
		return count;
	}
	
	// Get the precedence of the given operator
	private int precedence(char op) {
		switch (op) {
			case '*':	case '/':
				return 1;
			case '+':	case '-':
				return 2;
			default:
				return Integer.MAX_VALUE;
		}
	}
	
	// Determine if the number is overflow
	private boolean isOverflow(BigInteger num) {
		return ( (num.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) == 1) || 
				 (num.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) == -1) ) ? true : false;
	}
}





class Container {
	protected Object[] elements;
	protected int position;
	
	public Container(int size) {
		this.elements = new Object[size];
		position = -1;
	}
	
	public void add(Object o) {
		position++;
		if (position >= elements.length) 
			elements = Arrays.copyOf(elements, elements.length+1);
		elements[position] = o;
	}
	
	public Object remove(int index) {
		if ( (index<0) || (index>=elements.length) )
			return null;
		Object obj = null;
		Object[] temp = new Object[elements.length-1];
		for (int i=0, j=0; i<elements.length; i++) {
			if (i == index) {
				obj = elements[i];
				continue;
			}
			temp[j++] = elements[i];
		}
		elements = temp;
		position--;
		return obj;
	}
	
	public Object get(int index) {
		if ( (index<0) || (index>=elements.length) )
			return null;
		for (int i=0; i<elements.length; i++) { 
			if (i == index)
				return elements[i];
		}
		return null;
	}
	
	public boolean isEmpty() {
		return (position == -1) ? true : false;
	}
	
	public int getElementNumber() {
		return position + 1;
	}
}





class Stack extends Container {
	public Stack(int size) {
		super(size);
	}
	
	public void push(Object o) { 
		super.add(o); 
	}
	
	public Object pop() { 
		return super.remove(position); 
	}
	
	public Object top() { 
		return super.get(position); 
	}
}
