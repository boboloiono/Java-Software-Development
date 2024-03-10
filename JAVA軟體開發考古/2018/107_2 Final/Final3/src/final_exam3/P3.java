package final_exam3;

public class P3 {
	public static void main(String[] args) {
		if(args.length == 1) {
			int number = args[0].length();
			char[] arr = args[0].toCharArray();
			char[] origin1 = args[0].toCharArray();
			char temp;
			for(int i = 0; i < number; i++) {
				for(int times = 0; times < number-1; times++) {
					if(times == 0) {
						for(int k = 0; k < number; k++) {
							System.out.print(arr[k]);
						}
						System.out.print('\n');
					}
					for(int j = number - 2; j < number-1; j++) {
						temp = arr[j];
						arr[j] = arr[j+1];
						arr[j+1] = temp;
					}
					if(!(times == number-2) || number >= 4) {
						for(int k = 0; k < number; k++) {
							System.out.print(arr[k]);
						}
						System.out.print('\n');
					}
					for(int k = 0; k < number; k++) {
						arr[k]= origin1[k];
					}
					if(i+2+times < number && number >= 4) {
						temp = arr[i+2+times];
						arr[i+2+times] = arr[i+1];
						arr[i+1] = temp;
						for(int k = 0; k < number; k++) {
							System.out.print(arr[k]);
						}
						System.out.print('\n');
					}
				}
				for(int k = 0; k < number; k++) {
					arr[k]= args[0].charAt(k);
				}
				if(i+1 < number) {
					temp = arr[i+1];
					for(int k = i+1; k > 0; k--) {
						arr[k]= arr[k-1];
						origin1[k] = arr[k];
					}
					arr[0] = temp;
					origin1[0] = temp;
				}
			}
		}
		else {
			return;
		}
	}
}
