package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		HashMap<String,Occurrence> hm = new HashMap<>();
		
		Scanner sc = new Scanner(new File(docFile));
		while (sc.hasNext()) {
			String word = sc.next();
			if(word.length() == 0) //Nothing to parse
				continue;
			
			String keyword = getKeyword(word);
			if(keyword == null) //Don't want to add null word
				continue;
			
			Occurrence oc = hm.get(keyword);
			if(oc == null) {
				hm.put(keyword, new Occurrence(docFile, 1));
			} else {
				oc.frequency++;
				hm.put(keyword, oc);
			}
		}
		
		sc.close();
		return hm;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		Set<Map.Entry<String, Occurrence>> set = (Set<Map.Entry<String, Occurrence>>)kws.entrySet();
		Iterator<Map.Entry<String, Occurrence>> it = set.iterator();
		
		while(it.hasNext()) {
			Map.Entry<String, Occurrence> pair = (Map.Entry<String, Occurrence>)it.next();
			String str = pair.getKey();
			Occurrence oc = pair.getValue();
			
			ArrayList<Occurrence> occs = keywordsIndex.get(str);
			if(occs == null) {
				occs = new ArrayList<>();
				occs.add(oc);
				keywordsIndex.put(str, occs);
			} else {
				occs.add(oc);
				insertLastOccurrence(occs);
				keywordsIndex.put(str, occs);
			}
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		//Strip trailing punctuation
		ArrayList<Character> delims = new ArrayList<>(Arrays.asList(new Character[]{'.', ',', '?', ':', ';', '!'}));
		
		for(int i = word.length() - 1; i >= 0; i--) {
			if(!delims.contains(word.charAt(i))) { //the first non-punctuation character
				word = word.substring(0, i+1); //include this letter, since it is not punc.
				break;
			}
		}
		
		word = word.toLowerCase();
		if(noiseWords.contains(word) || word.length() == 0) { //if noise word, return null.
			//System.out.println(word + " is null");
			return null;
		}
		
		for(int i = 0; i < word.length(); i++) {
			char character = word.charAt(i);
			if(!Character.isAlphabetic(character)) {
				//System.out.println(word + " is null");
				return null;
			}
		}
		
		//System.out.println("Returning: " + word + " from " + original);
		return word;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		if(occs == null || occs.size() == 1)
			return null;
		
		//System.out.println("\n\n" + occs.toString());
		Occurrence oc = occs.remove(occs.size() - 1);
		
		ArrayList<Integer> visited = new ArrayList<>();
		int lo = 0, hi = occs.size() - 1;
		int mid = (lo + hi)/2;
		while(lo <= hi) {
			//System.out.println("lo " + lo + " hi " + hi);
			mid = (lo + hi)/2;
			visited.add(mid);
			
			if(occs.get(mid).frequency == oc.frequency)
				break;
			
			//Opposite of binary search since it's in descending order
			else if(occs.get(mid).frequency > oc.frequency)
				lo = mid + 1;
			else
				hi = mid - 1;
		}
		
		if(oc.frequency > occs.get(mid).frequency)
			occs.add(mid, oc);
		else //terminated at a point where mid.frequency >= oc.frequency
			occs.add(mid + 1, oc);
		
		return visited;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 * frequencies. The result size is limited to 5 documents. If there are no matches, returns null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		ArrayList<Occurrence> keywordOneOccs = keywordsIndex.get(kw1);
		ArrayList<Occurrence> keywordTwoOccs = keywordsIndex.get(kw2);
		System.out.println("\n\n" + keywordOneOccs);
		System.out.println(keywordTwoOccs);
		
		if(keywordOneOccs == null && keywordTwoOccs == null)
			return null;
		
		ArrayList<String> docs = new ArrayList<>();
		//If either one is null, fill up docs with top 5 from the other ArrayList
		if(keywordOneOccs == null) {
			for(int i = 0; i < 5 && i < keywordTwoOccs.size(); i++) {
				docs.add(keywordTwoOccs.get(i).document);
			}
			return docs;
		}
		if(keywordTwoOccs == null) {
			for(int i = 0; i < 5 && i < keywordOneOccs.size(); i++) {
				docs.add(keywordOneOccs.get(i).document);
			}
			return docs;
		}
		
		//Use two pointer method to get the documents with highest number of hits
		int i = 0, j = 0; //i is for kw1Occs, j is for kw2Occs
		int k = 0; //k keeps track of total number of documents added
		for(; k < 5 && i < keywordOneOccs.size() && j < keywordTwoOccs.size(); ) {
			Occurrence kw1Oc = keywordOneOccs.get(i);
			int kw1FreqInDoc = kw1Oc.frequency;
			
			Occurrence kw2Oc = keywordTwoOccs.get(j);
			int kw2FreqInDoc = kw2Oc.frequency;
			
			if(kw1FreqInDoc == kw2FreqInDoc) {
				if(!docs.contains(kw1Oc.document)) {
					docs.add(kw1Oc.document); 
					k++;
				}
				i++;
				
				if(k < 5 && !docs.contains(kw2Oc.document)) { 
					docs.add(kw2Oc.document); 
					k++;
				}
				j++;
			} else if (kw1FreqInDoc > kw2FreqInDoc) {				
				if(!docs.contains(kw1Oc.document)) {
					docs.add(kw1Oc.document); 
					k++;
				}
				i++;
			} else {
				if(!docs.contains(kw2Oc.document)) {
					docs.add(kw2Oc.document); 
					k++;
				}
				j++;
			}
		}
		
		for(; k < 5 && i < keywordOneOccs.size(); i++) {
			Occurrence kw1Oc = keywordOneOccs.get(i);
			
			if(!docs.contains(kw1Oc.document)) {
				docs.add(kw1Oc.document); 
				k++;
			}
		}
		
		for(; k < 5 && j < keywordTwoOccs.size(); j++) {
			Occurrence kw2Oc = keywordTwoOccs.get(j);
			
			if(!docs.contains(kw2Oc.document)) {
				docs.add(kw2Oc.document); 
				k++;
			}
		}
		
		return docs;
	}
}
