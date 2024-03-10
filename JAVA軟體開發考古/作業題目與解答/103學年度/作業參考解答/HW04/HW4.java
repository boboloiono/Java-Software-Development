import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;


public class HW4 {
	
	public static void main(String[] args) {
		List<Product> productList = new ArrayList<>();
		
		// Read file and set each Product's data
		try (Scanner scanner = new Scanner(new FileInputStream(args[0]))) {
			// Set up productList by the first line
			String[] products = scanner.nextLine().split(",");
			for (String name : products)
				productList.add(new Product(name));
			// Set up each Product's ratingList by the following lines
			while (scanner.hasNextLine()) {
				String[] ratings = scanner.nextLine().split(",", -1);
				for (int i=0; i<productList.size(); i++) {
					if (ratings[i].length() > 0)
						productList.get(i).ratingList.add(Integer.parseInt(ratings[i]));
				}
			}
		} catch (Exception e) {	
			e.printStackTrace();
		}
		
		// Compute each Product's average and standard deviation
		for (Iterator<Product> iterator=productList.iterator(); iterator.hasNext(); ) {
			Product product = iterator.next();
			product.computeAverage();
			product.computeStandardDeviation();
		}
		
		// Sort productList by Products' average value in descending order
		Collections.sort(productList, new Comparator<Product>() {
			@Override
			public int compare(Product o1, Product o2) {
				return -1*Double.compare(o1.average, o2.average);
			}
		});
		
		// Print out each Product's data
		for (Iterator<Product> iterator=productList.iterator(); iterator.hasNext(); ) {
			Product product = iterator.next();
			System.out.printf("[%-25s%.2f, %d, %d, %.2f]%n", 
					product.name+",", 
					product.average, 
					Collections.max(product.ratingList),
					Collections.min(product.ratingList),
					product.standardDeviation);
		}
	}
}

class Product {
	String name;
	List<Integer> ratingList = new ArrayList<>();
	double average, standardDeviation;
	
	public Product(String name) {
		this.name = name;
	}
	
	public void computeAverage() {
		double sum = 0;
		for (Iterator<Integer> iterator=ratingList.iterator(); iterator.hasNext(); )
			sum += iterator.next();
		this.average = sum/ratingList.size();
	}
	
	public void computeStandardDeviation() {
		double sum = 0;
		for (Iterator<Integer> iterator=ratingList.iterator(); iterator.hasNext(); )
			sum += Math.pow(iterator.next()-this.average, 2);
		this.standardDeviation = Math.sqrt(1.0/(ratingList.size()-1)*sum);
	}
}
