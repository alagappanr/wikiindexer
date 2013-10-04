/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.buffalo.cse.ir.wikiindexer.FileUtil;

/**
 * @author nikhillo An abstract class that represents a dictionary object for a
 *         given index
 */
public abstract class Dictionary implements Writeable {

	protected Map<String, Integer> dict[];
	protected int activatedDict;
	Properties props;

	public Dictionary(Properties props, INDEXFIELD field) {
		this.props = props;
		INDEXFIELD allIndexFields[] = INDEXFIELD.values();
		dict = new HashMap[allIndexFields.length];
		int count = 0;
		for (INDEXFIELD eachField : allIndexFields) {
			if (field.equals(eachField)) {
				dict[count] = new HashMap<String, Integer>();
				activatedDict = count;
			}
			count++;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#writeToDisk()
	 */
	public void writeToDisk() throws IndexerException {

		String dictFileName = null;
		File file;

		switch (activatedDict) {
		case 0:
			dictFileName = "TermDict.txt";
			break;
		case 1:
			dictFileName = "AuthorDict.txt";
			break;
		case 2:
			dictFileName = "CategoryDict.txt";
			break;
		case 3:
			dictFileName = "LinkDict.txt";
			break;
		default:
			System.out.println("Not a valid index constants in dictionary");
			break;
		}

		try {
			String dictFilePath = FileUtil.getRootFilesFolder(props);
			file = new File(dictFilePath + dictFileName);
			//System.out.println("Filename-" + dictFilePath + dictFileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fileWritter = new FileWriter(file,true);
			// System.out.println(file.getName());
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			// bufferWritter.write("pals");

			Iterator<String> keyIte = dict[activatedDict].keySet().iterator();
			while (keyIte.hasNext()) {
				String key = keyIte.next();
				bufferWritter.write(key + "=" + dict[activatedDict].get(key));
				bufferWritter.newLine();
			}
			bufferWritter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#cleanUp()
	 */
	public void cleanUp() {

		dict[activatedDict] = null;
		dict = null;

	}

	/**
	 * Method to check if the given value exists in the dictionary or not Unlike
	 * the subclassed lookup methods, it only checks if the value exists and
	 * does not change the underlying data structure
	 * 
	 * @param value
	 *            : The value to be looked up
	 * @return true if found, false otherwise
	 */
	public boolean exists(String value) {
		if (dict[activatedDict].containsKey(value))
			return true;
		else
			return false;
	}

	/**
	 * MEthod to lookup a given string from the dictionary. The query string can
	 * be an exact match or have wild cards (* and ?) Must be implemented ONLY
	 * AS A BONUS
	 * 
	 * @param queryStr
	 *            : The query string to be searched
	 * @return A collection of ordered strings enumerating all matches if found
	 *         null if no match is found
	 */
	public Collection<String> query(String queryStr) {
		int count = 0;
		String key;
		List<String> matches = new ArrayList<String>();
		try {
			Set<String> keys = dict[activatedDict].keySet();
			if (queryStr.contains("*")) {
				queryStr = queryStr.replaceAll("[\\*]", "[A-Za-z0-9]*");
			} else if (queryStr.contains("?")) {
				queryStr = queryStr.replaceAll("[\\?]", "[A-Za-z0-9]");
			}
			Pattern p = Pattern.compile("(^" + queryStr + "$)");
			Matcher m;
			Iterator<String> ite = keys.iterator();

			while (ite.hasNext()) {
				key = ite.next();
				m = p.matcher(key);
				if (m.matches() && m.group() != null) {
					matches.add(key);
					count++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (count == 0)
			return null;
		else
			return matches;
	}

	/**
	 * Method to get the total number of terms in the dictionary
	 * 
	 * @return The size of the dictionary
	 */
	public int getTotalTerms() {
		int totalTerms = dict[activatedDict].size();
		return totalTerms;
	}
}
