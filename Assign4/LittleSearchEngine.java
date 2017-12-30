package search;

import java.io.*;
import java.util.*;

/**
 * This class encapsulates an occurrence of a keyword in a document. It stores the
 * document name, and the frequency of occurrence in that document. Occurrences are
 * associated with keywords in an index hash table.
 * 
 * @author Sesh Venugopal
 * 
 */
class Occurrence {
	/**
	 * Document in which a keyword occurs.
	 */
	String document;
	
	/**
	 * The frequency (number of times) the keyword occurs in the above document.
	 */
	int frequency;
	
	/**
	 * Initializes this occurrence with the given document,frequency pair.
	 * 
	 * @param doc Document name
	 * @param freq Frequency
	 */
	public Occurrence(String doc, int freq) {
		document = doc;
		frequency = freq;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + document + "," + frequency + ")";
	}
}

/**
 * This class builds an index of keywords. Each keyword maps to a set of documents in
 * which it occurs, with frequency of occurrence in each document. Once the index is built,
 * the documents can searched on for keywords.
 *
 */
public class LittleSearchEngine {

   /**
   * This is a hash table of all keywords. The key is the actual keyword, and
   * the associated value is an array list of all occurrences of the keyword
   * in documents. The array list is maintained in descending order of
   * occurrence frequencies.
   */
   HashMap<String, ArrayList<Occurrence>> keywordsIndex;

   /**
   * The hash table of all noise words - mapping is from word to itself.
   */
   HashMap<String, String> noiseWords;

   /**
   * Creates the keyWordsIndex and noiseWords hash tables.
   */
   public LittleSearchEngine() {
       keywordsIndex = new HashMap<String, ArrayList<Occurrence>>(1000, 2.0f);
       noiseWords = new HashMap<String, String>(100, 2.0f);
   }

   /**
   * This method indexes all keywords found in all the input documents. When
   * this method is done, the keywordsIndex hash table will be filled with all
   * keywords, each of which is associated with an array list of Occurrence
   * objects, arranged in decreasing frequencies of occurrence.
   *
   * @param docsFile
   * Name of file that has a list of all the document file names,
   * one name per line
   * @param noiseWordsFile
   * Name of file that has a list of noise words, one noise word
   * per line
   * @throws FileNotFoundException
   * If there is a problem locating any of the input files on disk
   */
   public void makeIndex(String docsFile, String noiseWordsFile)
           throws FileNotFoundException {

       boolean valid = false;

       while (!valid) {
           try {
               Scanner sc = new Scanner(new File(noiseWordsFile));
               valid = true;
           } catch (FileNotFoundException e) {
               return;
           }
       }

       Scanner sc = new Scanner(new File(noiseWordsFile));

       while (sc.hasNext()) {
           String word = sc.next();
           noiseWords.put(word, word);
       }

       valid = false;

       while (!valid) {
           try {
               sc = new Scanner(new File(docsFile));
               valid = true;
           } catch (FileNotFoundException e) {
               return;
           }
       }

       sc = new Scanner(new File(docsFile));
       while (sc.hasNext()) {
           String docFile = sc.next();
           HashMap<String, Occurrence> kws = loadKeyWords(docFile);
           mergeKeyWords(kws);
       }

   }

   /**
   * Scans a document, and loads all keywords found into a hash table of
   * keyword occurrences in the document. Uses the getKeyWord method to
   * separate keywords from other words.
   *
   * @param docFile
   * Name of the document file to be scanned and loaded
   * @return Hash table of keywords in the given document, each associated
   * with an Occurrence object
   * @throws FileNotFoundException
   * If the document file is not found on disk
   */
   public HashMap<String, Occurrence> loadKeyWords(String docFile)
           throws FileNotFoundException {

       HashMap<String, Occurrence> keywords = new HashMap<String, Occurrence>();

       boolean valid = false;

       while (!valid) {
           try {
               Scanner words = new Scanner(new File(docFile));
               valid = true;
           } catch (FileNotFoundException e) {
               return keywords;
           }
       }

       Scanner words = new Scanner(new File(docFile));
       int freq = 1;

       while (words.hasNext()) {
           String word = words.next();

           if (getKeyWord(word) != null) {
               word = getKeyWord(word);
               if (!keywords.containsKey(word)) {
                   Occurrence occurs = new Occurrence(docFile, freq);
                   keywords.put(word, occurs);
               } else {
                   keywords.get(word).frequency++;
               }
           }
       }

       return keywords;
   }

   /**
   * Merges the keywords for a single document into the master keywordsIndex
   * hash table. For each keyword, its Occurrence in the current document must
   * be inserted in the correct place (according to descending order of
   * frequency) in the same keyword's Occurrence list in the master hash
   * table. This is done by calling the insertLastOccurrence method.
   *
   * @param kws
   * Keywords hash table for a document
   */
   public void mergeKeyWords(HashMap<String, Occurrence> kws) {

       ArrayList<Occurrence> l = new ArrayList<Occurrence>();

       for (String key : kws.keySet()) {
           Occurrence occ = kws.get(key);

           if (!keywordsIndex.containsKey(key)) {
               ArrayList<Occurrence> oList = new ArrayList<Occurrence>();
               oList.add(occ);
               keywordsIndex.put(key, oList);
           } else {
               l = keywordsIndex.get(key);
               l.add(occ);
               insertLastOccurrence(l);
               keywordsIndex.put(key, l);
           }
       }
   }

   /**
   * Given a word, returns it as a keyword if it passes the keyword test,
   * otherwise returns null. A keyword is any word that, after being stripped
   * of any trailing punctuation, consists only of alphabetic letters, and is
   * not a noise word. All words are treated in a case-INsensitive manner.
   *
   * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
   *
   * @param word
   * Candidate word
   * @return Keyword (word without trailing punctuation, LOWER CASE)
   */
   public String getKeyWord(String word) {
       word = word.trim();
       char e = word.charAt(word.length() - 1);

       while (e == '.' || e == ',' || e == '?' || e == ':' || e == ';' || e == '!') {
           word = word.substring(0, word.length() - 1);

           if (word.length() > 1) {
               e = word.charAt(word.length() - 1);
           } else {
               break;
           }
       }

       word = word.toLowerCase();

       for (String n : noiseWords.keySet()) {
           if (word.equalsIgnoreCase(n)) {
               return null;
           }
       }

       for (int i = 0; i < word.length(); i++) {
           if (!Character.isLetter(word.charAt(i))) {
               return null;
           }
       }

       return word;
   }

   /**
   * Inserts the last occurrence in the parameter list in the correct position
   * in the same list, based on ordering occurrences on descending
   * frequencies. The elements 0..n-2 in the list are already in the correct
   * order. Insertion is done by first finding the correct spot using binary
   * search, then inserting at that spot.
   *
   * @param occs
   * List of Occurrences
   * @return Sequence of mid point indexes in the input list checked by the
   * binary search process, null if the size of the input list is 1.
   * This returned array list is only used to test your code - it is
   * not used elsewhere in the program.
   */
   public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {

       if (occs.size() == 1) {
           return null;
       }

       int lFreq = occs.get(occs.size() - 1).frequency;
       Occurrence temp = occs.get(occs.size() - 1);
       int low = 0;
       int hi = occs.size() - 1;
       int mid;
       ArrayList<Integer> midEx = new ArrayList<Integer>();

       while (low <= hi) {
           mid = (low + hi) / 2;
           midEx.add(mid);

           if (lFreq > occs.get(mid).frequency) {
               hi = mid - 1;
           } else if (lFreq < occs.get(mid).frequency) {
               low = mid + 1;
           } else {
               break;
           }
       }

       if (midEx.get(midEx.size() - 1) == 0) {
           if (temp.frequency < occs.get(0).frequency) {
               occs.add(1, temp);
               occs.remove(occs.size() - 1);
               return midEx;
           }
       }

       occs.add(midEx.get(midEx.size() - 1), temp);
       occs.remove(occs.size() - 1);
      
       return midEx;
   }

   /**
   * Search result for "kw1 or kw2". A document is in the result set if kw1 or
   * kw2 occurs in that document. Result set is arranged in descending order
   * of occurrence frequencies. (Note that a matching document will only
   * appear once in the result.) Ties in frequency values are broken in favor
   * of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and
   * kw2 is in doc2 also with the same frequency f1, then doc1 will appear
   * before doc2 in the result. The result set is limited to 5 entries. If
   * there are no matching documents, the result is null.
   *
   * @param kw1
   * First keyword
   * @param kw1
   * Second keyword
   * @return List of NAMES of documents in which either kw1 or kw2 occurs,
   * arranged in descending order of frequencies. The result size is
   * limited to 5 documents. If there are no matching documents, the
   * result is null.
   */
	public ArrayList<String> top5search(String kw1, String kw2) {
		ArrayList<String> top5 = new ArrayList<String>();
		String K1 = getKeyWord(kw1); 
		String K2 = getKeyWord(kw2); 
		ArrayList<Occurrence> k1List = keywordsIndex.get(K1);
		ArrayList<Occurrence> k2List = keywordsIndex.get(K2);
		
		if ((K1 == null && K2 == null)||(k1List == null && k2List == null)){ 
			return null;
		}
		//if null
		
		if (K1 != null && k1List != null && k2List == null){ 
			for (int i = 0; i < k1List.size() && (top5.size() < 5 || top5.size() != 5); i++){ 
				top5.add(k1List.get(i).document); 
				System.out.println(top5);
			}
			System.out.println(top5);
			return top5;
		} 
		// kw1 is a keyword, and is in the master hash
		
		if (K2 != null && k2List != null && k1List == null){ 
			for (int i = 0; k2List != null &&i < k2List.size() && (top5.size() < 5 || top5.size() != 5); i++){ 
				top5.add(k2List.get(i).document); 
				System.out.println(top5);
			}
			System.out.println(top5);
			return top5;
		}
		// kw2 is a keyword, and is in the master hash
		
		while ((k1List.isEmpty() == false || k2List.isEmpty() == false) && (top5.size() < 5 || top5.size() != 5)) {
			Occurrence W1 = null; 
			Occurrence W2 = null;
			int x = 0;
			int y = 0;
			
			if (k2List.isEmpty() == false){
				W2 = k2List.get(0);
				y = W2.frequency;
			}
			
			if (k1List.isEmpty() == false){
				W1 = k1List.get(0);
				x = W1.frequency;
			}
			
			if (y > x){
				top5.add(W2.document);
				int count = 0;
				while (count < k2List.size()){
					if (k2List.get(count).document.equals(W2.document)){ 
						k2List.remove(count); 
					} else { 
						count++; 
					}
				}
				
				count = 0;
				while (count < k1List.size()){
					if (k1List.get(count).document.equals(W2.document)){
						k1List.remove(count);
					} else {
						count++;
					}
				}
			}
			else if (x > y){
				top5.add(W1.document);
				int count = 0;
				while (count < k1List.size()) {
					if (k1List.get(count).document.equals(W1.document)){ 
						k1List.remove(count);
					} else {
						count++;
					}
				}
				count = 0;
				while (count < k2List.size()){
					if (k2List.get(count).document.equals(W1.document)){ 
						k2List.remove(count);
					} else {
						count++;
					}
				}
			}
			else if (x == y){ 
				if (!(W1.document.equals(W2.document))){ 
					top5.add(W1.document);
					top5.add(W2.document); 
					int count = 0;
					while (count < k1List.size()){
						if (k1List.get(count).document.equals(W1.document)){ 
							k1List.remove(count);							
						} else {
							count++;
						}
					}

					count = 0;
					while (count < k2List.size()){
						if (k2List.get(count).document.equals(W1.document)){ 
							k2List.remove(count);
						} else {
							count++;
						}
					}
					count = 0;
					while (count < k2List.size()){
						if (k2List.get(count).document.equals(W2.document)){ 
							k2List.remove(count); 
						} else { 
							count++; 
						}
					}
					count = 0;
					while (count < k1List.size()){
						if (k1List.get(count).document.equals(W2.document)){ 
							k1List.remove(count);
						} else {
							count++;
						}
					}
				} else { 
					top5.add(W1.document);
					int count = 0;
					while (count < k1List.size()){
						if (k1List.get(count).document.equals(W1.document)){ 
							k1List.remove(count);
						} else {
							count++;
						}
					}
					count = 0;
					while (count < k2List.size()){
						if (k2List.get(count).document.equals(W1.document)){ 
							k2List.remove(count);
						} else {
							count++;
						}
					}
				}
			}
		}
		//Both in Master Hash
		
		if (top5.size() == 0){
			System.out.println(top5);
			return null;
		}
		
		else {
			System.out.println(top5);
				System.out.println();
				return top5;
		}
	}
}