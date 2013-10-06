/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.buffalo.cse.ir.wikiindexer.FileUtil;

/**
 * @author nikhillo This class is used to introspect a given index The
 *         expectation is the class should be able to read the index and all
 *         associated dictionaries.
 */
public class IndexReader {
	private INDEXFIELD indexType;
	private HashMap<Integer, HashMap<Integer, Integer>> index;
	private HashMap<Integer,Integer> indexCount;
	private Map<String, Integer> keyDict1;
	private Map<Integer, String> valueDict1;
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
		// System.out.println("Initializing...");
		String rootDir = FileUtil.getRootFilesFolder(props);
		switch (field) {
		case AUTHOR:
			indexFile = new File(rootDir + "AuthorIndex.txt");
			dictKeyFile = new File(rootDir + "AuthorDict.txt");
			dictValueFile = new File(rootDir + "LinkDict.txt");
			break;
		case LINK:
			indexFile = new File(rootDir + "LinkIndex.txt");
			dictKeyFile = new File(rootDir + "LinkDict.txt");
			dictValueFile = new File(rootDir + "LinkDict.txt");
			break;
		case TERM:
			indexFile = new File(rootDir + "TermIndex.txt");
			dictKeyFile = new File(rootDir + "TermDict.txt");
			dictValueFile = new File(rootDir + "LinkDict.txt");
			break;
		case CATEGORY:
			indexFile = new File(rootDir + "CategoryIndex.txt");
			dictKeyFile = new File(rootDir + "CategoryDict.txt");
			dictValueFile = new File(rootDir + "LinkDict.txt");

			break;

		}
		keyDict1 = buildKeyDict(dictKeyFile);
		valueDict1 = buildValueDict(dictValueFile);
		index = buildIndex(indexFile);

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
				bufferReader = new BufferedReader(new FileReader(
						dictKeyFile.getPath()));
				String line = bufferReader.readLine();
				while (line != null) {
					count++;
					line = bufferReader.readLine();
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
				bufferReader = new BufferedReader(new FileReader(
						dictValueFile.getPath()));
				String line = bufferReader.readLine();
				while (line != null) {
					count++;
					line = bufferReader.readLine();
				}

				bufferReader.close();
			} else {
				throw new Exception("Value Field for the key field :: "
						+ indexType.toString() + " has its file "
						+ dictValueFile.getAbsolutePath() + " missing.");
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
		HashMap<String, Integer> postings = new HashMap<String, Integer>();
		HashMap<Integer, Integer> postingsInt = new HashMap<Integer, Integer>();
		try {
			int keyId = keyDict1.containsKey(key) ? keyDict1.get(key) : -1;
			// System.out.println(keyId);
			if (keyId != -1) {
				postingsInt = index.get(keyId);

				// System.out.println(postingsInt.toString());
				/*
				 * for (Map.Entry<String, Integer> entry :
				 * valueDict1.entrySet()) { String dictKey = entry.getKey();
				 * Integer dictValue = entry.getValue(); // System.out.println(
				 * "Iterating through each item in the dictionary "
				 * +dictKey+" and "+dictValue);
				 * if(postingsInt.get(dictValue)!=null){ postings.put(dictKey,
				 * postingsInt.get(dictValue)); //
				 * System.out.println("Match found and inserted"); }
				 * 
				 * }
				 */
				for (Map.Entry<Integer, Integer> entry : postingsInt.entrySet()) {
					Integer docId = entry.getKey();
					Integer docOccurences = entry.getValue();
					// System.out.println("Iterating through each item in the dictionary "+dictKey+" and "+dictValue);
					if (postingsInt.get(docId) != null) {
						postings.put(valueDict1.get(docId), docOccurences);
						// System.out.println("Match found and inserted");
					}

				}

			} else {
				System.out
						.println("Key " + key + " not Found in the corpus!!!");

			}
		} catch (Exception e) {
			e.printStackTrace();
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

	public static <K, V> HashMap<V, K> reverse(Map<K, V> map) {
		HashMap<V, K> rev = new HashMap<V, K>();
		for (Map.Entry<K, V> entry : map.entrySet())
			rev.put(entry.getValue(), entry.getKey());
		return rev;
	}

	public Collection<String> getTopK(int k) {
		ArrayList<String> topK = new ArrayList<String>();
		Map<String, Integer> docMap;
		TreeMap<Integer, Integer> docOccSorted;
		occurencesComparator comp;
		Iterator<Integer> keyIte;
		int counter = 0;
		int requestedTerms = 0;
		try {
			if (k > index.size()) {
				return keyDict1.keySet();
			} else {
				//docMap = new HashMap<String, Integer>();
				comp = new occurencesComparator(indexCount);
				docOccSorted = new TreeMap<Integer, Integer>(comp);
				//System.out.println("Key: Counter Mapping Started");
				//long startTime = System.currentTimeMillis();
				//System.out.println(new Date() + "text count started: "+ startTime + "\n");

				/*for (Entry<Integer, HashMap<Integer, Integer>> keyEntry : index
						.entrySet()) {
					int keyId = keyEntry.getKey();
					counter = keyEntry.getValue().size();
					System.out.println("adding to document map: " + keyId + " "
							+ counter);
					docMap.put(reverse(keyDict1).get(keyId), counter);
					counter = 0;
				}*/
				
				//long endTime = System.currentTimeMillis();
				//System.out.println(new Date() + "text linking ended: "						+ endTime);
				//System.out.println(new Date() + "text linking time "						+ (endTime - startTime) + " milliseconds");

				//System.out.println("Adding to sorted map ");
				docOccSorted.putAll(indexCount);
				// System.out.println(docOccSorted);
				Set<Integer> keyTerms = docOccSorted.keySet();
				// System.out.println(keyTerms);
				keyIte = keyTerms.iterator();
				//System.out.println("Getting the top K terms ");
				while (keyIte.hasNext() && requestedTerms < k) {
					topK.add(reverse(keyDict1).get(keyIte.next()));
					
					requestedTerms++;
				}
				return topK;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	class occurencesComparator implements Comparator<Integer> {

		Map<Integer, Integer> base;

		public occurencesComparator(Map<Integer, Integer> base) {
			this.base = base;
		}

		// Note: this comparator imposes orderings that are inconsistent with
		// equals.
		public int compare(Integer a, Integer b) {
			//System.out.println("Comapring :: " + a + " :: " + b);
			if (base.get(a) >= base.get(b)) {
				return -1;
			} else {
				return 1;
			} // returning 0 would merge keys
		}
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

	public Map<String, Integer> buildKeyDict(File filename) {
		// System.out.println(filename);
		String key;
		Map<String, Integer> keyDiction = new HashMap<String, Integer>();
		Integer value;
		String[] splitLine;
		try {
			if (filename.exists()) {
				BufferedReader bufferReader;
				bufferReader = new BufferedReader(new FileReader(
						filename.getPath()));
				String line = bufferReader.readLine();
				while (line != null&& !line.equals("")) {
					try {
						if (!line.isEmpty()) {
							// System.out.println("Line-|" + line + "|");
							key = "";
							value = -1;
							splitLine = line.split("=");
							int splitLineLength = splitLine.length;
							if (splitLineLength > 1) {
								for (int i = 0; i < splitLineLength; i++) {
									if (i != splitLineLength - 1)
										key += splitLine[i];
									else
										value = Integer.parseInt(line
												.split("=")[i]);

								}
							}
							// System.out.println(key);
							// System.out.println(value);
							if (value != -1)
								keyDiction.put(key, value);
						}
						line = bufferReader.readLine();
					} catch (Exception e) {
						e.printStackTrace();
						line = bufferReader.readLine();
					}
				}
				bufferReader.close();
			} else {
				throw new Exception("Key Field " + indexType.toString()
						+ " File " + filename.getAbsolutePath() + " missing.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println("Size of Dict-" + keyDiction.size());
		return keyDiction;
	}

	public Map<Integer, String> buildValueDict(File filename) {
		// System.out.println(filename);
		String key;
		Map<Integer, String> valueDiction = new HashMap<Integer, String>();
		Integer value;
		// System.out.println(filename);

		String[] splitLine;
		try {
			if (filename.exists()) {
				BufferedReader bufferReader;
				bufferReader = new BufferedReader(new FileReader(
						filename.getPath()));
				String line = bufferReader.readLine();
				while (line != null && !line.equals("")) {
					try {
						if (!line.isEmpty()) {
							// System.out.println("Line-|" + line + "|");
							key = "";
							value = -1;
							splitLine = line.split("=");
							int splitLineLength = splitLine.length;
							if (splitLineLength > 1) {
								for (int i = 0; i < splitLineLength; i++) {
									if (i != splitLineLength - 1)
										key += splitLine[i];
									else
										value = Integer.parseInt(line
												.split("=")[i]);

								}
							}
							// System.out.println(key);
							// System.out.println(value);
							if (value != -1)
								valueDiction.put(value, key);
						}
						line = bufferReader.readLine();
					} catch (Exception e) {
						e.printStackTrace();
						line = bufferReader.readLine();
					}
				}
				bufferReader.close();
			} else {
				throw new Exception("Key Field " + indexType.toString()
						+ " File " + filename.getAbsolutePath() + " missing.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println("Size of Dict-" + keyDiction.size());
		return valueDiction;
	}

	public HashMap<Integer, HashMap<Integer, Integer>> buildIndex(File filename) {
		// System.out.println(filename);

		HashMap<Integer, HashMap<Integer, Integer>> fullIndex = new HashMap<Integer, HashMap<Integer, Integer>>();
		indexCount = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> innerMap;
		String line=null;
		int key=-1;
		int counter =0;
		try {
			if (filename.exists()) {
				BufferedReader bufferReader;
				bufferReader = new BufferedReader(new FileReader(
						filename.getPath()));
				 line = bufferReader.readLine();
				while (line != null) {
					try {
						if (line != null && !line.equals("")) {
							//System.out.println(line);
							//System.out.println(line);
							String[] a = line.replaceAll("[{}]", "").split("=",
									2);
							key = Integer.parseInt(a[0].trim());
							innerMap = new HashMap<>();
							for (String e : a[1].split(",")) {
								String[] b = e.split("=");
								
								innerMap.put(Integer.parseInt(b[0].trim()),	Integer.parseInt(b[1].trim()));
								counter++;
							}
							fullIndex.put(key, innerMap);
							indexCount.put(key, counter);
							counter=0;
						}
						line = bufferReader.readLine();
					} catch (Exception e) {
						e.printStackTrace();
						line = bufferReader.readLine();
					}
				}
				//System.out.println(indexCount);
				bufferReader.close();
			} else {
				throw new Exception("Key Field " + indexType.toString()
						+ " File " + filename.getAbsolutePath() + " missing.");
			}
		} catch (Exception e) {
			System.out.println("key :: "+line);		
			e.printStackTrace();
		}
		return fullIndex;
	}

	public static void main(String[] args) {
		String propFile = "C:\\Pals\\Code\\GitHub\\wikiindexer\\src\\edu\\buffalo\\cse\\ir\\wikiindexer\\properties.config";
		Properties props = null;
		// String propFile =
		// "/Users/shanmugamramu/Code/Masters/InfoRet/wikiindexer/src/edu/buffalo/cse/ir/wikiindexer/properties.config";
		try {
			props = FileUtil.loadProperties(propFile);

		} catch (IOException e) {
			e.printStackTrace();
		}
		IndexReader ir = new IndexReader(props, INDEXFIELD.TERM);
		System.out.println("Value terms " + ir.getTotalValueTerms());
		System.out.println("Key Terms " + ir.getTotalKeyTerms());
		System.out.println("Posting List :: " + ir.getPostings("Truand"));

		System.out.println("Posting Top K :: " + ir.getTopK(30));
		// ir.getTopK(7);

	}
}
