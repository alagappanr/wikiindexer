/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.parsers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaDocument;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaParser;

/**
 * @author nikhillo
 * 
 */
public class Parser {
	/* */
	private final Properties props;
	Collection<WikipediaDocument> documents;

	/**
	 * 
	 * @param idxConfig
	 * @param parser
	 */
	public Parser(Properties idxProps) {
		props = idxProps;
	}

	/* TODO: Implement this method */
	/**
	 * 
	 * @param filename
	 * @param docs
	 */
	public void parse(String filename, Collection<WikipediaDocument> docs) {

		// String rootDir = props.getProperty("root.dir");
		// String fileName = props.getProperty("dump.filename");
		// String xmlFile = rootDir + fileName;
		
		StringBuffer textContent = new StringBuffer();
		String content = null;
		Boolean istextContent = false;
		Boolean isheaderContent = false;
		String title = null;
		int id = 0;
		String timeStamp = null;
		String author = null;
		String docContent = null;
		
		try {
			
			if(filename == null || filename.isEmpty()) {
				throw new Exception("XML Filename not passed for parsing. Unable to proceed.");
			} 
			//System.out.println(filename);
			FileInputStream fis = new FileInputStream(filename);
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader reader = factory.createXMLStreamReader(filename,
					fis);
			while (reader.hasNext()) {
				int constants = reader.next();

				switch (constants) {
				case XMLStreamConstants.START_ELEMENT:
					// System.out.println("reader.getLocalName() : "+
					// reader.getLocalName());
					switch (reader.getLocalName()) {
					case "page":
						// System.out.println("page entered");
						isheaderContent = true;
						break;
					case "text":
						//System.out.println("text");
						textContent = new StringBuffer();
						content = null;
						istextContent = true;
						break;
					}
					break;
				case XMLStreamConstants.CHARACTERS:
					// System.out.println("characters");
					if (istextContent) {
						textContent.append(reader.getText().trim());
						// System.out.println(textContent);
					} else {
						content = reader.getText().trim();
					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					// System.out.println("reader.getLocalName() : "+
					// reader.getLocalName());
					switch (reader.getLocalName()) {
					case "title":
						title = content;
						//System.out.println("title : " + title);
						break;
					case "id":
						if (isheaderContent) {
							id = Integer.parseInt(content);
							isheaderContent = false;
						}
						// System.out.println("id : " + id);
						break;
					case "timestamp":
						timeStamp = content;
						// System.out.println("timestamp : " + timeStamp);
						break;
					case "username":
						author = content;
						// System.out.println("username : " + author);
						break;
					case "text":
						// System.out.println("text");
						//System.out.println("content : " + textContent);
						//System.out.println("---------------------------------------------------------------------------------------------------------");
						
						docContent = WikipediaParser.textCleaning(textContent);
						istextContent = false;
						break;
					case "page":
						//System.out.println("page ");
						WikipediaDocument singleDoc = new WikipediaDocument(id,
								timeStamp, author, title);
						singleDoc = WikipediaParser.textParse(singleDoc, docContent);
						add(singleDoc, docs);
						break;
					}
					break;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Method to add the given document to the collection. PLEASE USE THIS
	 * METHOD TO POPULATE THE COLLECTION AS YOU PARSE DOCUMENTS For better
	 * performance, add the document to the collection only after you have
	 * completely populated it, i.e., parsing is complete for that document.
	 * 
	 * @param doc
	 *            : The WikipediaDocument to be added
	 * @param documents
	 *            : The collection of WikipediaDocuments to be added to
	 */
	private synchronized void add(WikipediaDocument doc,
			Collection<WikipediaDocument> documents) {
		documents.add(doc);
	}
}
