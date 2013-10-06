/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.parsers;

import java.io.File;
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

	/**
	 * 
	 * @param filename
	 * @param docs
	 */
	public void parse(String filename, Collection<WikipediaDocument> docs) {

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

			if (filename == null || filename.isEmpty()) {
				System.out
						.println("XML Filename not passed for parsing. Unable to proceed.");
				return;
			}

			File xmlDoc = new File(filename);
			if (!xmlDoc.exists()) {
				System.out.println("File : " + filename
						+ " not present for reading");
				return;
			}
			FileInputStream fis = new FileInputStream(xmlDoc);

			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader reader = factory.createXMLStreamReader(filename,
					fis);
			int Docnum = 0;
			while (reader.hasNext()) {
				int constants = reader.next();

				switch (constants) {
				case XMLStreamConstants.START_ELEMENT:

					switch (reader.getLocalName()) {
					case "page":

						isheaderContent = true;
						title = timeStamp = author = null;
						id = 0;
						break;
					case "text":

						textContent = new StringBuffer();
						content = null;
						istextContent = true;
						break;
					}
					break;
				case XMLStreamConstants.CHARACTERS:

					if (istextContent) {
						textContent.append(reader.getText());

					} else {
						content = reader.getText().trim();
					}
					break;
				case XMLStreamConstants.END_ELEMENT:

					switch (reader.getLocalName()) {
					case "title":
						title = content;
						break;
					case "id":
						if (isheaderContent) {
							id = Integer.parseInt(content);
							isheaderContent = false;
						}

						break;
					case "timestamp":
						timeStamp = content;

						break;
					case "username":
						author = content;

						break;
					case "ip":
						author = content;
						break;
					case "text":

						docContent = WikipediaParser.textCleaning(textContent);
						istextContent = false;
						break;
					case "page":

						WikipediaDocument singleDoc = new WikipediaDocument(id,
								timeStamp, author, title);
						singleDoc = WikipediaParser.textParse(singleDoc,
								docContent);
						add(singleDoc, docs);
						break;
					}
					break;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (ParseException e) {
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
