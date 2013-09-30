/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author nikhillo
 * An abstract class that represents a dictionary object for a given index
 */
public abstract class Dictionary implements Writeable {
	
	protected Map<String, Integer> dict[]; 
	protected int activatedDict;
	
	public Dictionary (Properties props, INDEXFIELD field) {
		
		INDEXFIELD allIndexFields[] = INDEXFIELD.values();
		dict = new HashMap[allIndexFields.length];
		//System.out.println(allIndexFields.length);
		int count = 0;
		for(INDEXFIELD eachField: allIndexFields) {
			if(field.equals(eachField)){
				//System.out.println("Found match :: " + eachField+ " at " + activatedDict);
				dict[count] = new HashMap<String, Integer>();
				activatedDict = count;
			}
			count++;
		}
 	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#writeToDisk()
	 */
	public void writeToDisk() throws IndexerException {
		// TODO Implement this method

	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#cleanUp()
	 */
	public void cleanUp() {
		// TODO Implement this method

	}
	
	/**
	 * Method to check if the given value exists in the dictionary or not
	 * Unlike the subclassed lookup methods, it only checks if the value exists
	 * and does not change the underlying data structure
	 * @param value: The value to be looked up
	 * @return true if found, false otherwise
	 */
	public boolean exists(String value) {
		if(dict[activatedDict].containsKey(value))
			return true;
		else
			return false;
	}
	
	/**
	 * MEthod to lookup a given string from the dictionary.
	 * The query string can be an exact match or have wild cards (* and ?)
	 * Must be implemented ONLY AS A BONUS
	 * @param queryStr: The query string to be searched
	 * @return A collection of ordered strings enumerating all matches if found
	 * null if no match is found
	 */
	public Collection<String> query(String queryStr) {
		//TODO: Implement this method (FOR A BONUS)
		return null;
	}
	
	/**
	 * Method to get the total number of terms in the dictionary
	 * @return The size of the dictionary
	 */
	public int getTotalTerms() {
		int totalTerms = dict[activatedDict].size();
		return totalTerms;
	}
}
