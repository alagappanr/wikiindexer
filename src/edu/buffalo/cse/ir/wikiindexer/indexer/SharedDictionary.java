/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.util.Properties;

/**
 * @author nikhillo This class represents a subclass of a Dictionary class that
 *         is shared by multiple threads. All methods in this class are
 *         synchronized for the same reason.
 */
public class SharedDictionary extends Dictionary {

	public static int counter = 0;

	/**
	 * Public default constructor
	 * 
	 * @param props
	 *            : The properties file
	 * @param field
	 *            : The field being indexed by this dictionary
	 */
	public SharedDictionary(Properties props, INDEXFIELD field) {
		super(props, field);
	}

	/**
	 * Method to lookup and possibly add a mapping for the given value in the
	 * dictionary. The class should first try and find the given value within
	 * its dictionary. If found, it should return its id (Or hash value). If not
	 * found, it should create an entry and return the newly created id.
	 * 
	 * @param value
	 *            : The value to be looked up
	 * @return The id as explained above.
	 */
	public synchronized int lookup(String value) {

		if (!dict[activatedDict].containsKey(value)) {

			dict[activatedDict].put(value, ++counter);
		}
		return dict[activatedDict].get(value);
	}

}
