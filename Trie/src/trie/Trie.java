package trie;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class implements a Trie. 
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {
	
	// prevent instantiation
	private Trie() { }
	
	/**
	 * Builds a trie by inserting all words in the input array, one at a time,
	 * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
	 * The words in the input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */
	public static TrieNode buildTrie(String[] allWords) {
		/** COMPLETE THIS METHOD **/
		
		TrieNode root = new TrieNode(null, null, null);
		if(allWords.length == 0)
			return root;
		
		//Since the array is not empty, initialize first child
		root.firstChild = new TrieNode(new Indexes(0, 
									  			 (short)(0), 
									  			 (short)(allWords[0].length() - 1)), null, null);
		
		//Initialize pointer and lastSeen nodes
		TrieNode ptr = root.firstChild, lastSeen = root.firstChild;
		int simUpTo = -1, startIndex = -1, endIndex = -1, wordIndex = -1;
		
		//Traverse through all words in array starting from second one and insert them
		for(int index = 1; index < allWords.length; index++) {
			String word = allWords[index];
			
			//Terminate if ptr becomes null in traversal (no match case)
			while(ptr != null) {
				startIndex = ptr.substr.startIndex;
				endIndex = ptr.substr.endIndex;
				wordIndex = ptr.substr.wordIndex;
				
				//Need to check whether startIndex < word.length()
				if(startIndex > word.length()) {
					lastSeen = ptr;
					ptr = ptr.sibling;
					continue;
				}
				
				simUpTo = similarUpTo(allWords[wordIndex].substring(startIndex, endIndex+1), 
						word.substring(startIndex)); //Find index up to which strings are similar
				
				if(simUpTo != -1)
					simUpTo += startIndex;
				
				if(simUpTo == -1) { //No match at all
					lastSeen = ptr;
					ptr = ptr.sibling;
				}
				else {
					if(simUpTo == endIndex) { //Full match
						lastSeen = ptr;
						ptr = ptr.firstChild;
					}
					else if (simUpTo < endIndex){ //Partial match
						lastSeen = ptr;
						break;
					}
				}
			}
			
			//We did not find a match
			if(ptr == null) {
				Indexes indexes = new Indexes(index, (short)startIndex, (short)(word.length()-1));
				lastSeen.sibling = new TrieNode(indexes, null, null);
			} else {
				//Otherwise we'll need to split up the current node
				Indexes currIndexes = lastSeen.substr; //Get the current indexes
				TrieNode currFirstChild = lastSeen.firstChild; //Save a reference to first child so we don't lose everything under it
				
				//Update "parent" node indexes for new word indexes based on similarity
				Indexes currWordNewIndexes = new Indexes(currIndexes.wordIndex, (short)(simUpTo+1), currIndexes.endIndex);
				currIndexes.endIndex = (short)simUpTo; //Update  "parent" word last index
				
				//Shift everything from before down and to the right of new parent
				lastSeen.firstChild = new TrieNode(currWordNewIndexes, null, null);
				lastSeen.firstChild.firstChild = currFirstChild;
				lastSeen.firstChild.sibling = new TrieNode(new Indexes((short)index, (short)(simUpTo+1), (short)(word.length()-1)), 
						null, null);
			}
			
			//At the very end, reset ptr and lastSeen
			ptr = lastSeen = root.firstChild;
			simUpTo = startIndex = endIndex = wordIndex = -1;
		}
		
		return root;
	}
	
	private static int similarUpTo(String inTrie, String insert) {
		//Placeholder return statement
		int upTo = 0;
		while(upTo < inTrie.length() && upTo < insert.length()
				&& inTrie.charAt(upTo) == insert.charAt(upTo))
			upTo++;
		
		return (upTo-1);
	}
	
	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf nodes in the 
	 * trie whose words start with this prefix. 
	 * For instance, if the trie had the words "bear", "bull", "stock", and "bell",
	 * the completion list for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell"; 
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and "bell", 
	 * and for prefix "bell", completion would be the leaf node that holds "bell". 
	 * (The last example shows that an input prefix can be an entire word.) 
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be",
	 * the returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 *
	 * @param root Root of Trie that stores all words to search on for completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the prefix, 
	 * 			order of leaf nodes does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public static ArrayList<TrieNode> completionList(TrieNode root,
										String[] allWords, String prefix) {
		if(root == null) return null;
		
		ArrayList<TrieNode> matches = new ArrayList<>();
		TrieNode ptr = root;
		
		while(ptr != null) {
			//Get the substring at this node
			if(ptr.substr == null) //Possible that we're checking on root
				ptr = ptr.firstChild;
			
			String s = allWords[ptr.substr.wordIndex];
			String a = s.substring(0, ptr.substr.endIndex+1);
			if(s.startsWith(prefix) || prefix.startsWith(a)) {
				if(ptr.firstChild != null) { //this is not a full word, go to children
					matches.addAll(completionList(ptr.firstChild, allWords, prefix));
					ptr = ptr.sibling;
				} else { //Otherwise this is a full string node
					matches.add(ptr);
					ptr = ptr.sibling;
				}
			} else {
				ptr = ptr.sibling;
			}
		}
		
		return matches;
	}
	
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
							.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }
