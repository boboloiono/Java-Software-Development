package hw1;

public class Hw1 {
	public static void main(String[] args) {
		for(int i = 0; i < args.length; i++) {
			if(args[i].charAt(0) == 'a' ||args[i].charAt(0) == 'e' || args[i].charAt(0) == 'i' || args[i].charAt(0) == 'o' || args[i].charAt(0) == 'u') {
				args[i] = args[i] + "ay";
			}
			else {
				String str_replace = args[i].charAt(0) + "ay";
				args[i] = args[i].substring(1) + str_replace;
			}
			args[i] = args[i].substring(0, 1).toUpperCase() + args[i].substring(1);
			System.out.printf("%s ", args[i]);
		}
	}
}
