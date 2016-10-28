package shared;


public class Trie {
	int nodeCount=1;
	Node root;
	int wordCount=0;
	
	public Trie() {
		root=new Node("");
	}
	
	public Node getRoot() {
		return root;
	}
	
	public Boolean checkDictionary(String word) {
		return false;
	}
	
	public void add(String word) {
		String lWord=word.toLowerCase();
		Node currentNode=root;
		Boolean isNew=false;
		for (int i=0; i<lWord.length(); i++) {
			char currentLetter;
				currentLetter=lWord.charAt(i);
			if (currentNode.getNodes()[currentLetter-'a']==null) {
				isNew=true;
				nodeCount+=1;
				currentNode.insertNode(currentLetter-'a',new Node(String.valueOf(currentLetter)));

			}
				if (i==lWord.length()-1) {
					currentNode.getNodes()[currentLetter-'a'].addFrequency();
				}
			currentNode=currentNode.getNodes()[currentLetter-'a'];
		}
		if (isNew) {
			wordCount+=1;
		}
	}
	
	public int findIndex(char letter) {
		return letter-'a';
	}
	/**
	 * Searches the trie for the specified word
	 * 
	 * @param word The word being searched for
	 * 
	 * @return A reference to the trie node that represents the word,
	 * 			or null if the word is not in the trie
	 */
	public Node find(String word) {
		Node tempNode=root;
		for (int i=0; tempNode!=null && i<word.length(); i++) {
			tempNode=tempNode.nextNode(findIndex(word.charAt(i)));
		}
		if (tempNode==root || (tempNode!=null && tempNode.getFrequency()==0)) {
			return null;
		}
		return ((Node)tempNode);
	}
	
	public int getFrequency(String word) {
		Node tempNode=root;
		for (int i=0; tempNode!=null && i<word.length(); i++) {
			tempNode=tempNode.nextNode(findIndex(word.charAt(i)));
			if (i==word.length()-1) {
				if (tempNode!=null) {
					return tempNode.getFrequency();
				} else {
					return 0;
				}
			}
		}
		return 0;
	}
	
	/**
	 * Returns the number of unique words in the trie
	 * 
	 * @return The number of unique words in the trie
	 */
	public int getWordCount() {
		return wordCount;
	}
	
	/**
	 * Returns the number of nodes in the trie
	 * 
	 * @return The number of nodes in the trie
	 */
	public int getNodeCount() {
		return nodeCount;
	}
	
	
	public void appendNode(StringBuilder tempString, StringBuilder trieString, Node nextN) {
		if (nextN!=null) {
			tempString.append(nextN.toString());
			if (nextN.getFrequency()>0) {
				trieString.append(tempString.toString()+" "+nextN.getFrequency()+"\n");
			}
			for (int i=0; i<26; i++) {
					appendNode(tempString, trieString, nextN.getNodes()[i]);
			}
			if (tempString.length()>0) {
				tempString.replace(tempString.length()-1, tempString.length(), "");
			}
		}
	}
	
	/**
	 * The toString specification is as follows:
	 * For each word, in alphabetical order:
	 * <word> <count>\n
	 */
	@Override
	public String toString() {
		StringBuilder trieString=new StringBuilder("");
		StringBuilder tempString=new StringBuilder("");
		appendNode(tempString,trieString, root);                                                              
		return trieString.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + nodeCount;
		result = prime * result + wordCount;
		return result;
	}
	
	public Boolean checkNode(Node firstNode, Node secondNode) {
			if ((firstNode==null && secondNode!=null) || (firstNode!=null && secondNode==null)) {
				return false;
			} else if (firstNode!=null && secondNode!=null){
				if (firstNode.getFrequency()!=secondNode.getFrequency()) {
					return false;
				}
				for (int i=0; i<26; i++) {
				if (!checkNode(firstNode.nextNode(i), secondNode.nextNode(i))) {
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Trie other = (Trie) obj;
		if (nodeCount != other.nodeCount)
			return false; 
		if (wordCount != other.wordCount)
			return false;
		return checkNode(root, other.getRoot());
	}
	
	public class Node {
		int frequency=0;
		Node[] letterNodes;
		String letter;
		Boolean mark=false;
	
		
		public Node(String inLetter) {
			letterNodes=new Node[26];
			letter=inLetter;
		}
		
		public Boolean getMark() {
			return mark;
		}
		
		public void resetMark() {
			mark=false;
		}
		
		public void mark() {
			mark=true;
		}
		
		public int getValue() {
			return frequency;
		}
		
		public void addFrequency() {
			frequency+=1;
		}
		
		public int getFrequency() {
			return frequency;
		}
		
		public Node[] getNodes() {
			return letterNodes;
		}
	
		public Node nextNode(int index) {
			if (index>=0 && index<26) {
				return letterNodes[index];
			} else {
				return null;
			}
		}
		
		public void insertNode(int index, Node newNode) {
			letterNodes[index]=newNode;
		}
		
		public String toString() {
			return letter;
		}
	}
	
}