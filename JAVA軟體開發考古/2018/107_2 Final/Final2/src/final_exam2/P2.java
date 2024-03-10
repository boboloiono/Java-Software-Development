package final_exam2;

public class P2 {
	public static void main(String[] args) {
		if(args.length == 2) {
			if(args[0].length() == args[1].length()) {
				int A = 0, B = 0;
				for(int i = 0; i < args[0].length(); i++) {
					for(int j = 0; j < args[0].length(); j++) {
						if(args[0].charAt(i) == args[1].charAt(j) && i == j)
							A += 1;
						if(args[0].charAt(i) == args[1].charAt(j) && i != j)
							B += 1;						
					}
				}
				System.out.print(A);
				System.out.print("A");
				System.out.print(B);
				System.out.print("B");
			}
			else {
				return;
			}
		}
		else {
			return;
		}
	}
}
