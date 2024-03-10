import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;


public class HW5 {
	public static void main(String[] args) {
		// Set up wordList from the dictionary
		List<Word> wordList = new ArrayList<>();
		try (Scanner scanner = new Scanner(new FileInputStream(args[0]))) {
			while (scanner.hasNext())
				wordList.add(new Word(scanner.nextLine()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// Read each line from the input file
		try (Scanner scanner = new Scanner(new FileInputStream(args[1]))) {
			while (scanner.hasNextLine()) {
				List<String> answerList = new ArrayList<>();
				String input = scanner.nextLine();
				// Traverse each word in the dictionary to see if the word is formable from the input
				for (Iterator<Word> iterator=wordList.iterator(); iterator.hasNext(); ) {
					Word word = iterator.next();
					if (isFormable(input, word))
						answerList.add(word.name);
				}
				// Output the result
				System.out.printf("%s => ", input);
				for (Iterator<String> iterator=answerList.iterator(); iterator.hasNext(); )
					System.out.print(String.format("%s, ", iterator.next()));
				System.out.println();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static boolean isFormable(String inputWordString, Word dictionaryWord) {
		Map<Character, Integer> dictionaryWordCharMap = dictionaryWord.charMap;
		Word inputWord = new Word(inputWordString);
		// Iterate each character (c) of dictionaryWord 
		// If inputWord doesn't contain c, or # of c is more than the one of inputWord,
		// the dictionaryWord cannot be formed from inputWord (return false)
		for (Entry<Character, Integer> entry : dictionaryWordCharMap.entrySet()) {
			Integer count = inputWord.charMap.get(entry.getKey()); // return null if inputWord doesn't contain such character
			if ( (count==null) || (entry.getValue()>count) ) 
				return false;
		}
		return true;
	}
}

class Word {
	public String name;
	public Map<Character, Integer> charMap = new HashMap<>();
	public Word(String name) {
		this.name = name;
		// Set up charMap to record the number of occurrence of each distinct character of String name
		for (char c : name.toCharArray()) {
			Integer i = charMap.get(c);
			charMap.put(c, (i==null)?1:i+1);	
		}
	}
}
