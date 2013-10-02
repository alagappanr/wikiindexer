/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.util.HashMap;
import java.util.Map;

import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithm.WordListener;

import edu.buffalo.cse.ir.wikiindexer.indexer.INDEXFIELD;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;

/**
 * A simple map based token view of the transformed document
 * 
 * @author nikhillo
 * 
 */
public class IndexableDocument {

	private Map<INDEXFIELD, TokenStream> indDoc;
	private String docId;

	/**
	 * Default constructor
	 */
	public IndexableDocument() {
		indDoc = new HashMap<INDEXFIELD, TokenStream>();
	}

	/**
	 * MEthod to add a field and stream to the map If the field already exists
	 * in the map, the streams should be merged
	 * 
	 * @param field
	 *            : The field to be added
	 * @param stream
	 *            : The stream to be added.
	 */
	public void addField(INDEXFIELD field, TokenStream stream) {
		TokenStream mainStream;
		if (indDoc.containsKey(field)) {
			mainStream = indDoc.get(field);
			mainStream.seekEnd();
			mainStream.merge(stream);
			indDoc.remove(field);
			indDoc.put(field, mainStream);
		} else {
			indDoc.put(field, stream);
		}

	}

	/**
	 * Method to return the stream for a given field
	 * 
	 * @param key
	 *            : The field for which the stream is requested
	 * @return The underlying stream if the key exists, null otherwise
	 */
	public TokenStream getStream(INDEXFIELD key) {
		if (indDoc.containsKey(key)) {
			return indDoc.get(key);
		} else {
			return null;
		}
	}

	/**
	 * Method to return a unique identifier for the given document. It is left
	 * to the student to identify what this must be But also look at how it is
	 * referenced in the indexing process
	 * 
	 * @return A unique identifier for the given document
	 */
	public String getDocumentIdentifier() {
		return docId;
	}

	public void setDocId(String title) {
		title = Character.toString(title.charAt(0)).toUpperCase()
				+ title.substring(1);
		title = title.replace(" ", "_");
		this.docId = title;
	}

}
