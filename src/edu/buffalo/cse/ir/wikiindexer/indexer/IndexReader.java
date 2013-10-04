/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import edu.buffalo.cse.ir.wikiindexer.FileUtil;

/**
 * @author nikhillo This class is used to introspect a given index The
 *         expectation is the class should be able to read the index and all
 *         associated dictionaries.
 */
public class IndexReader {
	private INDEXFIELD indexType;
	private HashMap<Integer, HashMap<Integer, Integer>> termIndex;
	private HashMap<Integer, HashMap<Integer, Integer>> authorIndex;
	private HashMap<Integer, HashMap<Integer, Integer>> categoryIndex;
	private HashMap<Integer, HashMap<Integer, Integer>> linkIndex;
	private LocalDictionary keyDict;
	private SharedDictionary termDict;
	private LocalDictionary valueDict;
	public Integer totalPartitions;
	private Properties props;
	private File indexFile;
	private File dictKeyFile;
	private File dictValueFile;

	/**
	 * Constructor to create an instance
	 * 
	 * @param props
	 *            : The properties file
	 * @param field
	 *            : The index field whose index is to be read
	 */
	public IndexReader(Properties props, INDEXFIELD field) {
		this.props = props;
		this.indexType = field;
		String rootDir = FileUtil.getRootFilesFolder(props);
		switch (field) {
		case AUTHOR:
			authorIndex = new HashMap<Integer, HashMap<Integer, Integer>>();
			keyDict = new LocalDictionary(props, INDEXFIELD.AUTHOR);
			valueDict = new LocalDictionary(props, INDEXFIELD.LINK);
			indexFile = new File(rootDir + "AuthorIndex.txt");
			dictKeyFile = new File(rootDir + "AuthorDict.txt");
			dictValueFile = new File(rootDir + "LinkDict.txt");
			break;
		case LINK:
			linkIndex = new HashMap<Integer, HashMap<Integer, Integer>>();
			keyDict = new LocalDictionary(props, INDEXFIELD.LINK);
			valueDict = new LocalDictionary(props, INDEXFIELD.LINK);
			indexFile = new File(rootDir + "LinkIndex.txt");
			dictKeyFile = new File(rootDir + "LinkDict.txt");
			dictValueFile = new File(rootDir + "LinkDict.txt");
			break;
		case TERM:
			// termIndex = new HashMap[Partitioner.getNumPartitions()];
			termIndex = new HashMap<Integer, HashMap<Integer, Integer>>();
			termDict = new SharedDictionary(props, INDEXFIELD.TERM);
			valueDict = new LocalDictionary(props, INDEXFIELD.LINK);
			dictKeyFile = new File(rootDir + "TermDict.txt");
			dictValueFile = new File(rootDir + "LinkDict.txt");
			break;
		case CATEGORY:
			categoryIndex = new HashMap<Integer, HashMap<Integer, Integer>>();
			keyDict = new LocalDictionary(props, INDEXFIELD.CATEGORY);
			valueDict = new LocalDictionary(props, INDEXFIELD.LINK);
			indexFile = new File(rootDir + "CategoryIndex.txt");
			dictKeyFile = new File(rootDir + "CategoryDict.txt");
			dictValueFile = new File(rootDir + "LinkDict.txt");

			break;

		}

	}

	/**
	 * Method to get the total number of terms in the key dictionary
	 * 
	 * @return The total number of terms as above
	 */
	public int getTotalKeyTerms() {
		int count = 0;
		try {
			if (dictKeyFile.exists()) {
				BufferedReader bufferReader;
				bufferReader = new BufferedReader(new FileReader(dictKeyFile.getPath()));
				String line = bufferReader.readLine();
				while (line != null) {
					count++;
					line = bufferReader.readLine();
					// System.out.println(line);
				}

				bufferReader.close();
			} else {
				throw new Exception("Key Field " + indexType.toString()
						+ " File " + dictKeyFile.getAbsolutePath()
						+ " missing.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * Method to get the total number of terms in the value dictionary
	 * 
	 * @return The total number of terms as above
	 */
	public int getTotalValueTerms() {
		int count = 0;
		try {
			if (dictValueFile.exists()) {
				BufferedReader bufferReader;
				bufferReader = new BufferedReader(new FileReader(dictValueFile.getPath()));
				String line = bufferReader.readLine();
				while (line != null) {
					count++;
					line = bufferReader.readLine();
					// System.out.println(line);
				}

				bufferReader.close();
			} else {
				throw new Exception("Value Field for the key field :: " + indexType.toString()
						+ " has its file " + dictValueFile.getAbsolutePath()
						+ " missing.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;

	}

	/**
	 * Method to retrieve the postings list for a given dictionary term
	 * 
	 * @param key
	 *            : The dictionary term to be queried
	 * @return The postings list with the value term as the key and the number
	 *         of occurrences as value. An ordering is not expected on the map
	 */
	public Map<String, Integer> getPostings(String key) {
		Map<String, Integer> postings = new HashMap<String, Integer>();
		if(indexType == INDEXFIELD.TERM) {
			
		} else {
			int keyId = keyDict.lookup(key);
			
		}
		return postings;
	}

	/**
	 * Method to get the top k key terms from the given index The top here
	 * refers to the largest size of postings.
	 * 
	 * @param k
	 *            : The number of postings list requested
	 * @return An ordered collection of dictionary terms that satisfy the
	 *         requirement If k is more than the total size of the index, return
	 *         the full index and don't pad the collection. Return null in case
	 *         of an error or invalid inputs
	 */
	public Collection<String> getTopK(int k) {
		// TODO: Implement this method
		return null;
	}

	/**
	 * Method to execute a boolean AND query on the index
	 * 
	 * @param terms
	 *            The terms to be queried on
	 * @return An ordered map containing the results of the query The key is the
	 *         value field of the dictionary and the value is the sum of
	 *         occurrences across the different postings. The value with the
	 *         highest cumulative count should be the first entry in the map.
	 */
	public Map<String, Integer> query(String... terms) {
		// TODO: Implement this method (FOR A BONUS)
		return null;
	}
	
	public static void main(String[] args) {
		String propFile = "C:\\Pals\\Code\\GitHub\\wikiindexer\\src\\edu\\buffalo\\cse\\ir\\wikiindexer\\properties.config";
		Properties props = null;
		//String propFile = "/Users/shanmugamramu/Code/Masters/InfoRet/wikiindexer/src/edu/buffalo/cse/ir/wikiindexer/properties.config";
		try {
			 props = FileUtil.loadProperties(propFile);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		IndexReader ir = new IndexReader(props, INDEXFIELD.AUTHOR);
		System.out.println(ir.getTotalValueTerms());
	}
}
