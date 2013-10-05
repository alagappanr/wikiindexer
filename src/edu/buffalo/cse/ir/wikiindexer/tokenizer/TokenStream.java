/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class represents a stream of tokens as the name suggests. It wraps the
 * token stream and provides utility methods to manipulate it
 * 
 * @author nikhillo
 * 
 */
public class TokenStream implements Iterator<String> {

	private ArrayList<String> tokenStream;
	private int posIndex;

	/**
	 * Default constructor
	 * 
	 * @param bldr
	 *            : THe stringbuilder to seed the stream
	 */
	public TokenStream(StringBuilder bldr) {
		tokenStream = new ArrayList<String>();
		if(bldr != null && !(bldr.length()==0)) 
			tokenStream.add(bldr.toString());
		posIndex = -1;
		
	}

	/**
	 * Overloaded constructor
	 * 
	 * @param bldr
	 *            : THe stringbuilder to seed the stream
	 */
	public TokenStream(String string) {
		tokenStream = new ArrayList<String>();
		if (string != null && !string.isEmpty())
			tokenStream.add(string);
		posIndex = -1;
	}

	/**
	 * Method to append tokens to the stream
	 * 
	 * @param tokens
	 *            : The tokens to be appended
	 */
	public void append(String... tokens) {
		if (tokens != null) {
			for (String token : tokens) {
				if (token != null && !token.isEmpty()) {
					tokenStream.add(token);
				}
			}
		}
	}

	/**
	 * Method to retrieve a map of token to count mapping This map should
	 * contain the unique set of tokens as keys The values should be the number
	 * of occurrences of the token in the given stream
	 * 
	 * @return The map as described above, no restrictions on ordering
	 *         applicable
	 */
	public Map<String, Integer> getTokenMap() {
		if (tokenStream.size() == 0)
			return null;
		Map<String, Integer> tokenMap = new HashMap<String, Integer>();
		for (String token : tokenStream) {
			if (tokenMap.containsKey(token)) {
				int count = tokenMap.get(token);
				tokenMap.remove(token);
				tokenMap.put(token, ++count);
			} else {
				tokenMap.put(token, 1);
			}
		}

		return tokenMap;
	}

	/**
	 * Method to get the underlying token stream as a collection of tokens
	 * 
	 * @return A collection containing the ordered tokens as wrapped by this
	 *         stream Each token must be a separate element within the
	 *         collection. Operations on the returned collection should NOT
	 *         affect the token stream
	 */
	public Collection<String> getAllTokens() {
		if (tokenStream.size() != 0)
			return tokenStream;
		else
			return null;
	}

	/**
	 * Method to query for the given token within the stream
	 * 
	 * @param token
	 *            : The token to be queried
	 * @return: THe number of times it occurs within the stream, 0 if not found
	 */
	public int query(String token) {
		Iterator<String> ite = tokenStream.iterator();
		int count = 0;
		while (ite.hasNext()) {
			String element = ite.next();
			if (element.equalsIgnoreCase(token)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Iterator method: Method to check if the stream has any more tokens
	 * 
	 * @return true if a token exists to iterate over, false otherwise
	 */
	public boolean hasNext() {
		if (posIndex != tokenStream.size() - 1 && tokenStream.size() != 0)
			return true;
		else
			return false;
	}

	/**
	 * Iterator method: Method to check if the stream has any more tokens
	 * 
	 * @return true if a token exists to iterate over, false otherwise
	 */
	public boolean hasPrevious() {
		if (posIndex == -1)
			return false;
		else
			return true;
	}

	/**
	 * Iterator method: Method to get the next token from the stream Callers
	 * must call the set method to modify the token, changing the value of the
	 * token returned by this method must not alter the stream
	 * 
	 * @return The next token from the stream, null if at the end
	 */
	public String next() {
		if (hasNext()) {
			++posIndex;
			return tokenStream.get(posIndex);
		} else
			return null;
	}

	/**
	 * Iterator method: Method to get the previous token from the stream Callers
	 * must call the set method to modify the token, changing the value of the
	 * token returned by this method must not alter the stream
	 * 
	 * @return The next token from the stream, null if at the end
	 */
	public String previous() {
		if (hasPrevious()) {
			--posIndex;
			return tokenStream.get(posIndex + 1);
		} else
			return null;
	}

	/**
	 * Iterator method: Method to remove the current token from the stream
	 */
	public void remove() {
		if (tokenStream.size() != 0 && posIndex != tokenStream.size() - 1)
			tokenStream.remove(posIndex + 1);
	}

	/**
	 * Method to merge the current token with the previous token, assumes
	 * whitespace separator between tokens when merged. The token iterator
	 * should now point to the newly merged token (i.e. the previous one)
	 * 
	 * @return true if the merge succeeded, false otherwise
	 */
	public boolean mergeWithPrevious() {
		if (!hasPrevious() || tokenStream.size() == 1)
			return false;
		try {
			String currentToken = tokenStream.get(posIndex + 1);
			String prevToken = tokenStream.get(posIndex);
			String temp = prevToken + " " + currentToken;
			temp = temp.trim().replaceAll(" +", " ");
			tokenStream.add(posIndex, temp);
			tokenStream.remove(posIndex + 1);
			tokenStream.remove(posIndex + 1);
			posIndex--;
			return true;
		} catch (Exception e) {
			System.out.println("Error in TokenStream :: Mergewithprevious :: ");
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * Method to merge the current token with the next token, assumes whitespace
	 * separator between tokens when merged. The token iterator should now point
	 * to the newly merged token (i.e. the current one)
	 * 
	 * @return true if the merge succeeded, false otherwise
	 */
	public boolean mergeWithNext() {
		if (!hasNext() || tokenStream.size() == 1)
			return false;

		try {
			String currentToken = tokenStream.get(posIndex + 1);
			String nextToken = tokenStream.get(posIndex + 2);
			String temp = currentToken + " " + nextToken;
			temp = temp.trim().replaceAll(" +", " ");
			tokenStream.add(posIndex + 3, temp);
			tokenStream.remove(posIndex + 1);
			tokenStream.remove(posIndex + 1);
			return true;
		} catch (Exception e) {
			System.out.println("Error in TokenStream :: Mergewithnext");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Method to replace the current token with the given tokens The stream
	 * should be manipulated accordingly based upon the number of tokens set It
	 * is expected that remove will be called to delete a token instead of
	 * passing null or an empty string here. The iterator should point to the
	 * last set token, i.e, last token in the passed array.
	 * 
	 * @param newValue
	 *            : The array of new values with every new token as a separate
	 *            element within the array
	 */
	public void set(String... newValue) {

		if (newValue == null)
			return;
				
		for(int i=0; i<newValue.length;i++) {
			if(newValue[i]==null ||newValue[i].isEmpty())
				return;
		}
		
		if(tokenStream.size() == 0)
			return;
		
		Boolean checkRemoval = true;
		Boolean lastposition = (posIndex+1 == tokenStream.size());

		for (String newToken : newValue) {
			if(checkRemoval && !lastposition){
				tokenStream.remove(posIndex + 1);
				posIndex--;
				checkRemoval = false;
			}
			if(!lastposition) { 
				posIndex++;
				tokenStream.add(posIndex+1, newToken);
				
			} else { 
				tokenStream.add(newToken);
			}
			
		}
	}

	/**
	 * Iterator method: Method to reset the iterator to the start of the stream
	 * next must be called to get a token
	 */
	public void reset() {
		posIndex = -1;
	}

	/**
	 * Iterator method: Method to set the iterator to beyond the last token in
	 * the stream previous must be called to get a token
	 */
	public void seekEnd() {
		posIndex = tokenStream.size() - 1;
	}

	/**
	 * Method to merge this stream with another stream
	 * 
	 * @param other
	 *            : The stream to be merged
	 */
	public void merge(TokenStream other) {
		try {
			if (other != null && other.tokenStream.size() != 0) {
				tokenStream.addAll(other.tokenStream);
				// System.out.println(other);
			}
		} catch (Exception e) {
			System.out.println("Error in tokenStream :: merge :: ");
			e.printStackTrace();
		}
	}

	public String getFullTokenStream(){
		return tokenStream.toString();
	}
	
	public void removeAll(){
		tokenStream.clear();
	}
	
	public Integer getSize(){
		return tokenStream.size();
	}
	/*
	 * public static void main(String[] arg) { TokenStream stream;
	 * 
	 * stream = new TokenStream("test");
	 * stream.append("string","with","multiple","tokens");
	 * System.out.println(stream.tokenStream); assertEquals(new Object[]{"test",
	 * "string","with","multiple","tokens"}, stream.getAllTokens().toArray());
	 * stream = null;
	 * 
	 * //intermediate nulls and emptys stream = new TokenStream("test");
	 * stream.append("string","with",null,"and","","tokens");
	 * System.out.println(stream.tokenStream); assertEquals(new Object[]{"test",
	 * "string","with","and","tokens"}, stream.getAllTokens().toArray()); stream
	 * = null;
	 * 
	 * 
	 * }
	 */
}
