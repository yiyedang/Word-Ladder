import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

/**
 * This class contains methods and implementations that takes in a text file 
 * and then (randomly) generates new text in the same style.
 * @author yiyedang
 *
 */
public class RandomWriter {
	/**
	 * This is the main method of the Random Writer
	 * @param args (not used)
	 */
	public static void main(String[] args) {
		System.out.println("Welcome to CS 106B Random Writer ('N-Grams').\n" + 
				" This program makes random text based on a document.\n" + 
				" Give me an input file and an 'N' value for groups\n" + 
				" of words, and I'll create random text for you.");
		System.out.println();
		Scanner scnr = new Scanner(System.in);
		System.out.print("Input file name? ");
		String fileName = scnr.nextLine();
		File f = new File("file"+ File.separatorChar + fileName);
		//prompt for user input of a valid file name
		while(!f.exists()) {
			System.out.print("Unable to open that file.  Try again. ");
			fileName = scnr.nextLine();
			f = new File("file"+ File.separatorChar + fileName);
		}

		int n;
		//get the number N from the user 
		while(true){
			System.out.print("Value of N? ");
			//prompt for a valid integer input as n
			if(!scnr.hasNextInt()) {
				System.out.println("Illegal integer format. Try again. ");
				scnr.nextLine();
				continue;
			}
			n = scnr.nextInt();
			if(n >= 2) {
				break;
			}
			else {
				System.out.println("N must be 2 or greater. ");
			}
		}
		scnr.nextLine();
		
		//build the map according to the file
		HashMap<ArrayList<String>, ArrayList<String>> map = buildMap(n, f);

		while(true) {
			System.out.print("# of random words to generate (0 to quit)? ");
			//prompt for a valid integer input as the word number
			if(!scnr.hasNextInt()) {
				System.out.println("Illegal integer format. Try again. ");
				scnr.nextLine();
				continue;
			}
			int wordNum = scnr.nextInt();
			//quit the program if word # is 0
			if(wordNum == 0) {
				System.out.print("Exit");
				break;
			}
			else if(wordNum <= n - 1) {
				System.out.println("Must be at least " + n + " words. ");
				System.out.println();
			}
			else {
				randTextGen(n, wordNum, map);
				System.out.println();
			}
		}
		scnr.close();
			
	}
	
	/**
	 * Finds and constructs a map from each (N-1)-gram in your input text to 
	 * all of the words that occur directly after it in the input.
	 * @param n
	 * @param f
	 * @return map
	 */
	public static HashMap<ArrayList<String>, ArrayList<String>> buildMap(int n , File f){
		HashMap<ArrayList<String>, ArrayList<String>> map = new HashMap<ArrayList<String>, ArrayList<String>>();
		ArrayList<String> window = new ArrayList<String>();
		ArrayList<String> start = new ArrayList<String>();
		String word = "";
		Scanner inFile;
		try {
			inFile = new Scanner(f);
			//add the first set of n-1 words into window
			for(int i = 0; i < n - 1; i++) {
				word = inFile.next();
				window.add(word);
				start.add(word);
			}

			//while the file is not read to the end
			while(inFile.hasNext()) {
				word = inFile.next();
				map = addToMap(word, window, map);
				//discard the first word and append the new word to the window
				ArrayList<String> tmpWindow = new ArrayList<String>();
				tmpWindow.addAll(window);
				tmpWindow.remove(0);
				tmpWindow.add(word);
				window = tmpWindow;
			}

			for(int k = 0; k < n - 1; k++) {
				word = start.get(k);
				map = addToMap(word, window, map);
				//discard the first word and append the new word to the window
				ArrayList<String> tmpWindow = new ArrayList<String>();
				tmpWindow.addAll(window);
				tmpWindow.remove(0);
				tmpWindow.add(word);
				window = tmpWindow;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * Adds a suffix to the current prefix's or a new suffix collection
	 * @param word
	 * @param window
	 * @param map
	 * @return
	 */
	public static HashMap<ArrayList<String>, ArrayList<String>> addToMap(String word, ArrayList<String> window, HashMap<ArrayList<String>, ArrayList<String>> map) {
		ArrayList<String> value;
		//if the map already contains current prefix
		if(map.containsKey(window)) {
			//add current suffix to its suffix collection
			value = map.get(window);
			value.add(word);
			map.put(window, value);
		}
		else {
			//create a new prefix and add its suffix
			value = new ArrayList<String>();
			value.add(word);
			map.put(window, value);
		}
		return map;
	}

	/**
	 * Generates random text of provided number of words from your map of N-grams
	 * @param n
	 * @param wordNum
	 * @param map
	 */
	public static void randTextGen(int n, int wordNum, HashMap<ArrayList<String>, ArrayList<String>> map) {
		Set<ArrayList<String>> keyCollection = map.keySet();// a collection of all keys in the map
		Random rand = new Random(); 
		int keyIndex = rand.nextInt(keyCollection.size()); 
		ArrayList<String> window = new ArrayList<String>();
		Iterator<ArrayList<String>> mark = keyCollection.iterator();
		int i = 0;
		//choose a random key from the collection
		while(mark.hasNext()) {
			ArrayList<String> key = mark.next();
			if(i == keyIndex) {
				window.addAll(key);
			}
			i++;
		}

		System.out.print("... ");
		//print out the first chosen prefix
		for(int k = 0; k < window.size(); k++) {
			System.out.print(window.get(k) + " ");
		}

		//choose one random suffix of the key, print, increment the window, and repeat
		for(int k = 0; k < wordNum - (n-1); k++) {
			keyIndex = rand.nextInt(map.get(window).size());
			String nextWord = map.get(window).get(keyIndex);
			System.out.print(nextWord + " ");
			window.remove(0);
			window.add(nextWord);
		}
		System.out.println("...");
	}

}
