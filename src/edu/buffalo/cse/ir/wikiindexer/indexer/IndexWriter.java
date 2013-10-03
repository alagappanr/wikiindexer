/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.HashMap;

/**
 * @author nikhillo
 * This class is used to write an index to the disk
 * 
 */
public class IndexWriter implements Writeable {
	private String indexType;
	private HashMap<Integer, HashMap<Integer, Integer>> termIndex;
	private HashMap<Integer, HashMap<Integer, Integer>> authorIndex;
	private HashMap<Integer, HashMap<Integer, Integer>> categoryIndex;
	private HashMap<Integer, HashMap<Integer, Integer>> linkIndex;
	private LocalDictionary keyDict;
	private SharedDictionary termDict;
	private LocalDictionary valueDict;
	private Integer partitionNumber;
	/**
	 * Constructor that assumes the underlying index is inverted
	 * Every index (inverted or forward), has a key field and the value field
	 * The key field is the field on which the postings are aggregated
	 * The value field is the field whose postings we are accumulating
	 * For term index for example:
	 * 	Key: Term (or term id) - referenced by TERM INDEXFIELD
	 * 	Value: Document (or document id) - referenced by LINK INDEXFIELD
	 * @param props: The Properties file
	 * @param keyField: The index field that is the key for this index
	 * @param valueField: The index field that is the value for this index
	 */
	public IndexWriter(Properties props, INDEXFIELD keyField, INDEXFIELD valueField) {
		this(props, keyField, valueField, false);
	}
	
	/**
	 * Overloaded constructor that allows specifying the index type as
	 * inverted or forward
	 * Every index (inverted or forward), has a key field and the value field
	 * The key field is the field on which the postings are aggregated
	 * The value field is the field whose postings we are accumulating
	 * For term index for example:
	 * 	Key: Term (or term id) - referenced by TERM INDEXFIELD
	 * 	Value: Document (or document id) - referenced by LINK INDEXFIELD
	 * @param props: The Properties file
	 * @param keyField: The index field that is the key for this index
	 * @param valueField: The index field that is the value for this index
	 * @param isForward: true if the index is a forward index, false if inverted
	 */
	public IndexWriter(Properties props, INDEXFIELD keyField, INDEXFIELD valueField, boolean isForward) {
		//TODO: Implement this method
		indexType = keyField.toString().toLowerCase();
        
		if (indexType=="author"){
			authorIndex = new HashMap<Integer, HashMap<Integer, Integer>>();
			keyDict = new LocalDictionary(props, INDEXFIELD.AUTHOR);
			valueDict = new LocalDictionary(props, INDEXFIELD.LINK);
		} else if (indexType == "link"){
			linkIndex = new HashMap<Integer, HashMap<Integer, Integer>>();
			keyDict = new LocalDictionary(props, INDEXFIELD.LINK);
			valueDict = new LocalDictionary(props, INDEXFIELD.LINK);
		} else if (indexType == "term"){
			termIndex = new HashMap<Integer, HashMap<Integer, Integer>>();
			termDict = new SharedDictionary(props, INDEXFIELD.TERM);
			valueDict = new LocalDictionary(props, INDEXFIELD.LINK);
		} else {
			categoryIndex = new HashMap<Integer, HashMap<Integer, Integer>>();
			keyDict = new LocalDictionary(props, INDEXFIELD.CATEGORY);
			valueDict = new LocalDictionary(props, INDEXFIELD.LINK);
		}
	}
	
	/**
	 * Method to make the writer self aware of the current partition it is handling
	 * Applicable only for distributed indexes.
	 * @param pnum: The partition number
	 */
	public void setPartitionNumber(int pnum) {
		//TODO: Optionally implement this method
		partitionNumber = pnum;
	}
	
	/**
	 * Method to add a given key - value mapping to the index
	 * @param keyId: The id for the key field, pre-converted
	 * @param valueId: The id for the value field, pre-converted
	 * @param numOccurances: Number of times the value field is referenced
	 *  by the key field. Ignore if a forward index
	 * @throws IndexerException: If any exception occurs while indexing
	 */
	public void addToIndex(int keyId, int valueId, int numOccurances) throws IndexerException {
		//TODO: Implement this method
		HashMap<Integer, Integer> indexItem;
		if (indexType=="author"){
			indexItem = authorIndex.get(keyId);
			if(indexItem==null){
				indexItem = new HashMap<Integer, Integer>();
				indexItem.put(valueId, numOccurances);
				authorIndex.put(keyId, indexItem);
			}
			else{
				indexItem.put(valueId, indexItem.get(valueId)+numOccurances);
			}
		} else if (indexType == "term"){
			indexItem = termIndex.get(keyId);
			if(indexItem==null){
				indexItem = new HashMap<Integer, Integer>();
				indexItem.put(valueId, numOccurances);
				termIndex.put(keyId, indexItem);
			}
			else{
				indexItem.put(valueId, indexItem.get(valueId)+numOccurances);
			}
		} else if (indexType == "category"){
			indexItem = categoryIndex.get(keyId);
			if(indexItem==null){
				indexItem = new HashMap<Integer, Integer>();
				indexItem.put(valueId, numOccurances);
				categoryIndex.put(keyId, indexItem);
			}
			else{
				indexItem.put(valueId, indexItem.get(valueId)+numOccurances);
			}
		} else if (indexType == "link"){
			indexItem = linkIndex.get(keyId);
			if(indexItem==null){
				indexItem = new HashMap<Integer, Integer>();
				indexItem.put(valueId, numOccurances);
				linkIndex.put(keyId, indexItem);
			}
			else{
				indexItem.put(valueId, indexItem.get(valueId)+numOccurances);
			}
		}
	}
	
	/**
	 * Method to add a given key - value mapping to the index
	 * @param keyId: The id for the key field, pre-converted
	 * @param value: The value for the value field
	 * @param numOccurances: Number of times the value field is referenced
	 *  by the key field. Ignore if a forward index
	 * @throws IndexerException: If any exception occurs while indexing
	 */
	public void addToIndex(int keyId, String value, int numOccurances) throws IndexerException {
		//TODO: Implement this method
		Integer valueId = valueDict.lookup(value);
		addToIndex(keyId, valueId, numOccurances);
	}
	
	/**
	 * Method to add a given key - value mapping to the index
	 * @param key: The key for the key field
	 * @param valueId: The id for the value field, pre-converted
	 * @param numOccurances: Number of times the value field is referenced
	 *  by the key field. Ignore if a forward index
	 * @throws IndexerException: If any exception occurs while indexing
	 */
	public void addToIndex(String key, int valueId, int numOccurances) throws IndexerException {
		//TODO: Implement this method
		Integer keyId;
		if(indexType=="term"){
			keyId = termDict.lookup(key);
		} else {
			keyId = keyDict.lookup(key);
		}
		addToIndex(keyId, valueId, numOccurances);
	}
	
	/**
	 * Method to add a given key - value mapping to the index
	 * @param key: The key for the key field
	 * @param value: The value for the value field
	 * @param numOccurances: Number of times the value field is referenced
	 *  by the key field. Ignore if a forward index
	 * @throws IndexerException: If any exception occurs while indexing
	 */
	public void addToIndex(String key, String value, int numOccurances) throws IndexerException {
		//TODO: Implement this method
		Integer keyId =0, valueId=0;
		if(indexType=="term"){
			keyId = termDict.lookup(key);
		} else {
			keyId = keyDict.lookup(key);
		}
		valueId = valueDict.lookup(value);
		addToIndex(keyId, valueId, numOccurances);
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#writeToDisk()
	 */
	public void writeToDisk() throws IndexerException {
		// TODO Implement this method
        cleanUp();

	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#cleanUp()
	 */
	public void cleanUp() {
		// TODO Implement this method
		String filename = null;
		File file = null;
		HashMap<Integer, HashMap<Integer, Integer>> writeable = null;
		if(indexType=="author"){
			filename = "AuthorIndex.txt";
			writeable = authorIndex;
		} else if (indexType=="term"){
			filename = "TermIndex"+partitionNumber.toString()+".txt";
			writeable = termIndex;
		} else if (indexType=="link"){
			filename = "LinkIndex.txt";
			writeable = linkIndex;
		} else if (indexType=="category"){
			filename = "CategoryIndex.txt";
			writeable = categoryIndex;
		}
		System.out.println("Filename-"+filename);
		file = new File(filename);
		
	      try
	      {
//	         FileOutputStream fileOut =
//	         new FileOutputStream("/tmp/employee.ser");
	         ObjectOutputStream out = getOOS(file);
	         out.writeObject(writeable);
	         out.close();
//	         file.close();
	         System.out.printf("Saved");
	      } catch(IOException i) {
	          i.printStackTrace();
	      } 
	      System.out.println("Success");
	}

	private static ObjectOutputStream getOOS(File storageFile)
			throws IOException {
		if (storageFile.exists()) {
			// this is a workaround so that we can append objects to an existing file
			return new AppendableObjectOutputStream(new FileOutputStream(storageFile, true));
		} else {
			return new ObjectOutputStream(new FileOutputStream(storageFile));
		}
	}

//	private static ObjectInputStream getOIS(FileInputStream fis)
//			throws IOException {
//		long pos = fis.getChannel().position();
//		return pos == 0 ? new ObjectInputStream(fis) : 
//			new AppendableObjectInputStream(fis);
//	}

	private static class AppendableObjectOutputStream extends
	ObjectOutputStream {

		public AppendableObjectOutputStream(OutputStream out)
				throws IOException {
			super(out);
		}

		@Override
		protected void writeStreamHeader() throws IOException {
			// do not write a header
		}
	}

//	private static class AppendableObjectInputStream extends ObjectInputStream {
//
//		public AppendableObjectInputStream(InputStream in) throws IOException {
//			super(in);
//		}
//
//		@Override
//		protected void readStreamHeader() throws IOException {
//			// do not read a header
//		}
//	}
}
