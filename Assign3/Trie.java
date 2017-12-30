
package structures;

import java.util.ArrayList;

/**
 * This class implements a compressed trie. Each node of the tree is a CompressedTrieNode, with fields for
 * indexes, first child and sibling.
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {
	
	/**
	 * Words indexed by this trie.
	 */
	ArrayList<String> words;
	
	/**
	 * Root node of this trie.
	 */
	TrieNode root;
	
	/**
	 * Initializes a compressed trie with words to be indexed, and root node set to
	 * null fields.
	 * 
	 * @param words
	 */
	public Trie() {
		root = new TrieNode(null, null, null);
		words = new ArrayList<String>();
	}
	
	
	/**
	 * Inserts a word into this trie. Converts to lower case before adding.
	 * The word is first added to the words array list, then inserted into the trie.
	 * 
	 * @param word Word to be inserted.
	 */
	public void insertWord(String word) {
		words.add(word);
		//adds word 
		
		int wordIndex = 0;
		//check if tree is null
		if (root.firstChild == null) {
			// No trie 
			Indexes i = new Indexes(wordIndex, (short) 0,(short)(word.length()-1));
			TrieNode n = new TrieNode(i,null,null);
			root.firstChild = n;
		}//end if
		else {
			TrieNode ptr = root.firstChild;
			int startDex = 0;
			while (ptr != null) {
				String prefix = "";
				String currNode = words.get(ptr.substr.wordIndex);
				
				for(int i = startDex; i <word.length(); i++) {
					if (i != currNode.length()-1)
						if(word.charAt(i) == currNode.charAt(i)) {
							prefix+=word.charAt(i);
						}//end if
						else {
							break;
						}//end else
					else {
						break;
					}//end else
				}//end for
				
				if (prefix.length() >= 1) {
					//child
					if (ptr.firstChild == null) {
						//fix ptr index
						ptr.substr.startIndex = (short) (startDex);
						ptr.substr.endIndex = (short) (startDex + prefix.length()-1);
						
						//add two children for trailing substring
						Indexes i1 = new Indexes(words.indexOf(currNode), (short) (startDex + prefix.length()), (short) (currNode.length()-1));
						TrieNode n1 = new TrieNode(i1,null,null);
						ptr.firstChild = n1;
						
						Indexes i2 = new Indexes(words.indexOf(word), (short) (startDex + prefix.length()), (short) (word.length()-1));
						TrieNode n2 = new TrieNode(i2,null,null);
						ptr.firstChild.sibling = n2;
						break;
					}//end if
					else {
						startDex  = startDex + prefix.length();
						ptr = ptr.firstChild;
						continue;
						//continues through loop
					}//end else
				}//end if
				else {
					// sibling
					if (ptr.sibling == null) {
						Indexes i = new Indexes(words.indexOf(word), (short) (0), (short) (word.length()-1));
						TrieNode n = new TrieNode(i,null,null);
						ptr.sibling = n;
						break;
					}
					else {
						ptr = ptr.sibling;
					}//end else
				}//end else
			}//end while
		}//end else			
	}//end insertWords
	
	/**
	 * Given a string prefix, returns its "completion list", i.e. all the words in the trie
	 * that start with this prefix. For instance, if the tree had the words bear, bull, stock, and bell,
	 * the completion list for prefix "b" would be bear, bull, and bell; for prefix "be" would be
	 * bear and bell; and for prefix "bell" would be bell. (The last example shows that a prefix can be
	 * an entire word.) The order of returned words DOES NOT MATTER. So, if the list contains bear and
	 * bell, the returned list can be either [bear,bell] or [bell,bear]
	 * 
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all words in tree that start with the prefix, order of words in list does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */	
	

	public ArrayList<String> completionList(String prefix) {
		ArrayList<String> set = new ArrayList<String>();
		return completionList(root, set, prefix);
	}
	
	private ArrayList<String> completionList(TrieNode root, ArrayList<String> set, String prefix) {
		if (root == null) {
			return null;
		}//end if
		//checks for null 
		if(root.substr != null) {
			boolean x = false;
			if(words.get(root.substr.wordIndex).startsWith(prefix)){
				x = true;
			}//end if
			if(x && !set.contains(words.get(root.substr.wordIndex))) {
				set.add(words.get(root.substr.wordIndex));
			}
		}//end if
		
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			completionList(ptr, set, prefix);
			//traversal
			/* 
			 * initially I used a basic arraylist to find the words that I would need to return
			 * but sesh emailed us saying that we couldn't do that.
			 * The best thing to do was to use the base traversal that is done in print and used it here
			 * Then you have to keep it going.
			 */
		}//end for
		return set;
	}//end completionList

	public void print() {
		print(root, 1, words);
	}
	
	private static void print(TrieNode root, int indent, ArrayList<String> words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			System.out.println("      " + words.get(root.substr.wordIndex));
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		System.out.println("(" + root.substr + ")");
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }