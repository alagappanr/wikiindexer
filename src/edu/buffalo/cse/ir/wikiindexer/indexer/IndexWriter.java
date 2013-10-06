/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.HashMap;
import java.util.Set;

import edu.buffalo.cse.ir.wikiindexer.FileUtil;

/**
 * @author nikhillo This class is used to write an index to the disk
 * 
 */
public class IndexWriter implements Writeable {
	private INDEXFIELD indexType;
	private HashMap<Integer, HashMap<Integer, Integer>> termIndex;
	private HashMap<Integer, HashMap<Integer, Integer>> authorIndex;
	private HashMap<Integer, HashMap<Integer, Integer>> categoryIndex;
	private HashMap<Integer, HashMap<Integer, Integer>> linkIndex;
	private LocalDictionary keyDict;
	private SharedDictionary termDict;
	private LocalDictionary valueDict;
	public Integer partitionNumber;
	private Properties props;
	//Partitioner part;
	
	/**
	 * Constructor that assumes the underlying index is inverted Every index
	 * (inverted or forward), has a key field and the value field The key field
	 * is the field on which the postings are aggregated The value field is the
	 * field whose postings we are accumulating For term index for example: Key:
	 * Term (or term id) - referenced by TERM INDEXFIELD Value: Document (or
	 * document id) - referenced by LINK INDEXFIELD
	 * 
	 * @param props
	 *            : The Properties file
	 * @param keyField
	 *            : The index field that is the key for this index
	 * @param valueField
	 *            : The index field that is the value for this index
	 */
	public IndexWriter(Properties props, INDEXFIELD keyField,
			INDEXFIELD valueField) {
		this(props, keyField, valueField, false);
	}

	/**
	 * Overloaded constructor that allows specifying the index type as inverted
	 * or forward Every index (inverted or forward), has a key field and the
	 * value field The key field is the field on which the postings are
	 * aggregated The value field is the field whose postings we are
	 * accumulating For term index for example: Key: Term (or term id) -
	 * referenced by TERM INDEXFIELD Value: Document (or document id) -
	 * referenced by LINK INDEXFIELD
	 * 
	 * @param props
	 *            : The Properties file
	 * @param keyField
	 *            : The index field that is the key for this index
	 * @param valueField
	 *            : The index field that is the value for this index
	 * @param isForward
	 *            : true if the index is a forward index, false if inverted
	 */
	public IndexWriter(Properties props, INDEXFIELD keyField,
			INDEXFIELD valueField, boolean isForward) {

		this.props = props;
		this.indexType = keyField;
		//System.out.println("called flush files");
		flushOldFiles();
		//part = new Partitioner();
		switch (keyField) {
		case AUTHOR:
			authorIndex = new HashMap<Integer, HashMap<Integer, Integer>>();
			keyDict = new LocalDictionary(props, INDEXFIELD.AUTHOR);
			valueDict = new LocalDictionary(props, INDEXFIELD.LINK);
			// System.out.println("Inside Author");
			break;
		case LINK:
			linkIndex = new HashMap<Integer, HashMap<Integer, Integer>>();
			keyDict = new LocalDictionary(props, INDEXFIELD.LINK);
			valueDict = new LocalDictionary(props, INDEXFIELD.LINK);
			break;
		case TERM:
			// termIndex = new HashMap[Partitioner.getNumPartitions()];
			termIndex = new HashMap<Integer, HashMap<Integer, Integer>>();
			termDict = new SharedDictionary(props, INDEXFIELD.TERM);
			valueDict = new LocalDictionary(props, INDEXFIELD.LINK);
			break;
		case CATEGORY:
			categoryIndex = new HashMap<Integer, HashMap<Integer, Integer>>();
			keyDict = new LocalDictionary(props, INDEXFIELD.CATEGORY);
			valueDict = new LocalDictionary(props, INDEXFIELD.LINK);
			break;

		}
	}

	/**
	 * Method to make the writer self aware of the current partition it is
	 * handling Applicable only for distributed indexes.
	 * 
	 * @param pnum
	 *            : The partition number
	 */
	public void setPartitionNumber(int pnum) {
		partitionNumber = pnum;
	}

	/**
	 * Method to add a given key - value mapping to the index
	 * 
	 * @param keyId
	 *            : The id for the key field, pre-converted
	 * @param valueId
	 *            : The id for the value field, pre-converted
	 * @param numOccurances
	 *            : Number of times the value field is referenced by the key
	 *            field. Ignore if a forward index
	 * @throws IndexerException
	 *             : If any exception occurs while indexing
	 */
	public void addToIndex(int keyId, int valueId, int numOccurances)
			throws IndexerException {
		HashMap<Integer, Integer> indexItem;
		int numofTimes = 0;
		switch (indexType) {
		case AUTHOR:
			indexItem = authorIndex.get(keyId);
			if (indexItem == null) {
				indexItem = new HashMap<Integer, Integer>();
				indexItem.put(valueId, numOccurances);
				authorIndex.put(keyId, indexItem);
			} else {
				if (indexItem.containsKey(valueId)) {
					numofTimes = indexItem.get(valueId) + numOccurances;

				} else {
					numofTimes = numOccurances;
				}
				indexItem.put(valueId, numofTimes);
				authorIndex.put(keyId, indexItem);
			}
			// System.out.println("authorIndex" + authorIndex);
			break;
		case TERM:
			/*
			 * if (termIndex[partitionNumber] == null) {
			 * termIndex[partitionNumber] = new HashMap<Integer,
			 * HashMap<Integer, Integer>>(); }
			 */
			indexItem = termIndex.get(keyId);
			if (indexItem == null) {
				indexItem = new HashMap<Integer, Integer>();
				indexItem.put(valueId, numOccurances);
				termIndex.put(keyId, indexItem);
			} else {
				if (indexItem.containsKey(valueId)) {
					numofTimes = indexItem.get(valueId) + numOccurances;

				} else {
					numofTimes = numOccurances;
				}
				indexItem.put(valueId, numofTimes);
				termIndex.put(keyId, indexItem);

			}
			// System.out.println("termIndex" + termIndex[partitionNumber]);
			// System.out.println("partitionNumber" + partitionNumber);
			// System.out.println("__________________");
			break;
		case CATEGORY:
			indexItem = categoryIndex.get(keyId);
			if (indexItem == null) {
				indexItem = new HashMap<Integer, Integer>();
				indexItem.put(valueId, numOccurances);
				categoryIndex.put(keyId, indexItem);
			} else {
				if (indexItem.containsKey(valueId)) {
					numofTimes = indexItem.get(valueId) + numOccurances;

				} else {
					numofTimes = numOccurances;
				}
				indexItem.put(valueId, numofTimes);
				categoryIndex.put(keyId, indexItem);

			}
			// System.out.println("categoryIndex" + categoryIndex);
			break;
		case LINK:
			indexItem = linkIndex.get(keyId);
			// System.out.println("indexItem :: " + indexItem);
			// System.out.println("keyId :: " + keyId);
			if (indexItem == null) {

				indexItem = new HashMap<Integer, Integer>();
				indexItem.put(valueId, numOccurances);
				linkIndex.put(keyId, indexItem);
			} else {
				// System.out.println("Inside index :: "+indexItem);
				// System.out.println("Inside valueId :: "+valueId);
				// System.out.println("Inside indexItem.get(valueId) :: "+indexItem.get(valueId));
				// System.out.println("numOccurances :: "+numOccurances);

				if (indexItem.containsKey(valueId)) {
					numofTimes = indexItem.get(valueId) + numOccurances;

				} else {
					numofTimes = numOccurances;
				}
				indexItem.put(valueId, numofTimes);
				linkIndex.put(keyId, indexItem);
			}
			// System.out.println("linkIndex" + linkIndex);
			break;
		}

	}

	/**
	 * Method to add a given key - value mapping to the index
	 * 
	 * @param keyId
	 *            : The id for the key field, pre-converted
	 * @param value
	 *            : The value for the value field
	 * @param numOccurances
	 *            : Number of times the value field is referenced by the key
	 *            field. Ignore if a forward index
	 * @throws IndexerException
	 *             : If any exception occurs while indexing
	 */
	public void addToIndex(int keyId, String value, int numOccurances)
			throws IndexerException {
		Integer valueId = valueDict.lookup(value);
		addToIndex(keyId, valueId, numOccurances);

	}

	/**
	 * Method to add a given key - value mapping to the index
	 * 
	 * @param key
	 *            : The key for the key field
	 * @param valueId
	 *            : The id for the value field, pre-converted
	 * @param numOccurances
	 *            : Number of times the value field is referenced by the key
	 *            field. Ignore if a forward index
	 * @throws IndexerException
	 *             : If any exception occurs while indexing
	 */
	public void addToIndex(String key, int valueId, int numOccurances)
			throws IndexerException {
		Integer keyId;
		if (indexType == INDEXFIELD.TERM) {
			keyId = termDict.lookup(key);
		} else {
			keyId = keyDict.lookup(key);
		}
		// System.out.println(key+" :: "+valueId+" :: "+numOccurances+" :: "+partitionNumber+" :: "+keyId);
		addToIndex(keyId, valueId, numOccurances);
	}

	/**
	 * Method to add a given key - value mapping to the index
	 * 
	 * @param key
	 *            : The key for the key field
	 * @param value
	 *            : The value for the value field
	 * @param numOccurances
	 *            : Number of times the value field is referenced by the key
	 *            field. Ignore if a forward index
	 * @throws IndexerException
	 *             : If any exception occurs while indexing
	 */
	public void addToIndex(String key, String value, int numOccurances)
			throws IndexerException {
		Integer keyId = 0, valueId = 0;
		if (indexType == INDEXFIELD.TERM) {
			keyId = termDict.lookup(key);
		} else {
			keyId = keyDict.lookup(key);
		}
		valueId = valueDict.lookup(value);
		addToIndex(keyId, valueId, numOccurances);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#writeToDisk()
	 */
	public void writeToDisk() throws IndexerException {

		String indexFileName = null;
		File file = null;
		HashMap<Integer, HashMap<Integer, Integer>> writeable = null;
		Boolean firstWrite = true;
		if (indexType == INDEXFIELD.AUTHOR) {
			indexFileName = "AuthorIndex.txt";
			writeable = authorIndex;
			keyDict.writeToDisk();
			// System.out.println("authorIndex :: " + authorIndex);
		} else if (indexType == INDEXFIELD.TERM) {
			indexFileName = "TermIndex_" + partitionNumber.toString() + ".txt";
			writeable = termIndex;
			termDict.writeToDisk();
			// System.out.println("termIndex :: " + termIndex);
		} else if (indexType == INDEXFIELD.LINK) {
			indexFileName = "LinkIndex.txt";
			writeable = linkIndex;
			//System.out.println("keyDict :: "+keyDict.dict[keyDict.activatedDict]);
			keyDict.writeToDisk();
			// System.out.println("linkIndex :: " + linkIndex);
		} else if (indexType == INDEXFIELD.CATEGORY) {
			indexFileName = "CategoryIndex.txt";
			writeable = categoryIndex;
			keyDict.writeToDisk();
			// System.out.println("categoryIndex :: " + categoryIndex);
		}

		// file = new File(indexFileName);

		try {
			String indexFilePath = FileUtil.getRootFilesFolder(props);
			file = new File(indexFilePath + indexFileName);
			// System.out.println("Filename-" + indexFilePath+indexFileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fileWritter = new FileWriter(file, true);
			// System.out.println(file.getName());
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			// bufferWritter.write("pals");

			Iterator<Integer> keyIte = writeable.keySet().iterator();
			while (keyIte.hasNext()) {
				Integer key = keyIte.next();
				bufferWritter.write("{" + key + "={");
				// System.out.print("{"+key+"={");
				Map<Integer, Integer> valueMap = writeable.get(key);
				Iterator<Integer> valueIte = valueMap.keySet().iterator();
				while (valueIte.hasNext()) {
					Integer value = valueIte.next();
					if (!firstWrite) {
						bufferWritter.write(", ");
						// System.out.print(", ");
						firstWrite = false;
					}
					// System.out.println(indexType.toString() +
					// " : "+firstWrite);
					bufferWritter.write(value + "=" + valueMap.get(value));
					// System.out.print(value+"="+valueMap.get(value));
					firstWrite = false;
				}
				bufferWritter.write("}}");
				// System.out.print("}}");
				bufferWritter.newLine();
				// System.out.println("");
				firstWrite = true;

			}
			bufferWritter.close();
			bufferWritter = null;
			//synchronized(part) {
				if(bufferWritter == null) {
					//int mergeCount = part.getMergerPartition();
					Partitioner.setMergerPartition();
					//System.out.println("pals write check :: " +Partitioner.getMergerPartition() +" :: "+Thread.currentThread());
				}
			//}
			
			// fileWritter.close();
			// FileOutputStream fileOut =
			// new FileOutputStream("/tmp/employee.ser");
			// ObjectOutputStream out = getOOS(file);
			// out.writeObject(writeable);
			// out.close();
			// file.close();
			// System.out.printf("Saved Successfully"+indexFileName);
		} catch (IOException i) {
			i.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println("Success");
	}

	private void flushOldFiles() {
		//System.out.println("flushing files");
		File indexFilePath = new File(FileUtil.getRootFilesFolder(props));
		File[] files = indexFilePath.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.getName().endsWith(".txt")) {
					file.delete();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#cleanUp()
	 */
	public void cleanUp() {
		//System.out.println("clean called");
		if (indexType == INDEXFIELD.TERM) {
			//System.out.println("term indexer invoked");
			File outputFile = new File(FileUtil.getRootFilesFolder(props)
					+ "TermIndex.txt");
			//System.out.println(outputFile.exists());
			if (!outputFile.exists()) {
				File[] inputFile = new File[Partitioner.getNumPartitions()];
				for (int i = 0; i < Partitioner.getNumPartitions(); i++) {
					inputFile[i] = new File(FileUtil.getRootFilesFolder(props)
							+ "TermIndex_" + i + ".txt");
					// System.out.println(inputFile[i].getAbsolutePath());
				}
				//synchronized (part) {
					//System.out.println("called merger" + Thread.currentThread());
					merge(inputFile, outputFile);
				//}
			}
		}
		keyDict = null;
		termDict = null;
		valueDict = null;
		indexType = null;
		termIndex = null;
		authorIndex = null;
		categoryIndex = null;
		linkIndex = null;
		partitionNumber = null;
		props = null;

	}

	public void merge(File[] inputFile, File outputFile) {

		try {
			//System.out.println("entered merger 1");
			if (!outputFile.exists()) {
				//System.out.println("entered merger");
				FileWriter fileWritter = new FileWriter(outputFile, true);
				BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
				while (Partitioner.getMergerPartition()!=(Partitioner.getNumPartitions()+3)) {
					// System.out.println("Reached111111111"+ inputFile[i]);
					//System.out.println("writeCheck :: " +Partitioner.getMergerPartition());
					long sleepTime = 2000;
					if (sleepTime < 1200000) {
						Thread.sleep(sleepTime);
						sleepTime += 1000;
					} 
				}
				for (int i = 0; i < inputFile.length; i++) {
					//System.out.println();
					
//					System.out.println("Reached : " + inputFile[i]
//							+ " . Thread Name"
//							+ Thread.currentThread().getName());
					BufferedReader bufferReader;
					bufferReader = new BufferedReader(new FileReader(
							inputFile[i].getPath()));
					String line = bufferReader.readLine();
					while (line != null) {
						bufferWritter.write(line);
						bufferWritter.newLine();
						line = bufferReader.readLine();
						// System.out.println(line);
					}

					bufferReader.close();

				}
				bufferWritter.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
