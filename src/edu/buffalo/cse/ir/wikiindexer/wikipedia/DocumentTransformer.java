/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import edu.buffalo.cse.ir.wikiindexer.indexer.INDEXFIELD;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.Tokenizer;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;

/**
 * A Callable document transformer that converts the given WikipediaDocument
 * object into an IndexableDocument object using the given Tokenizer
 * 
 * @author nikhillo
 * 
 */
public class DocumentTransformer implements Callable<IndexableDocument> {

	WikipediaDocument transformWikiDoc;
	IndexableDocument transformIdxDoc;
	Map<INDEXFIELD, Tokenizer> transformTknizer;

	/**
	 * Default constructor, DO NOT change
	 * 
	 * @param tknizerMap
	 *            : A map mapping a fully initialized tokenizer to a given field
	 *            type
	 * @param doc
	 *            : The WikipediaDocument to be processed
	 */
	public DocumentTransformer(Map<INDEXFIELD, Tokenizer> tknizerMap,
			WikipediaDocument doc) {
		transformIdxDoc = new IndexableDocument();
		transformWikiDoc = doc;
		transformTknizer = tknizerMap;
	}

	/**
	 * Method to trigger the transformation
	 * 
	 * @throws TokenizerException
	 *             Inc ase any tokenization error occurs
	 */
	public IndexableDocument call() throws TokenizerException {
		INDEXFIELD allIndexFields[] = INDEXFIELD.values();
		Tokenizer tkn;
		TokenStream stream = null;
		ArrayList<String> tokens = null;
		String firstToken = null;
		transformIdxDoc.setDocId(transformWikiDoc.getTitle());
		for (INDEXFIELD field : allIndexFields) {
			switch (field) {
			case TERM:
				tkn = transformTknizer.get(field);
				tokens = new ArrayList<String>();
				List<WikipediaDocument.Section> transformWikiSec = transformWikiDoc
						.getSections();
				if (transformWikiSec != null && transformWikiSec.size() > 0) {
					for (WikipediaDocument.Section wikiSec : transformWikiSec) {
						tokens.add(wikiSec.getTitle());
						tokens.add(wikiSec.getText());
					}
					firstToken = tokens.get(0);
					if (stream == null) {
						stream = new TokenStream(firstToken);
						tokens.remove(0);
					}
					stream.append(tokens.toArray(new String[tokens.size()]));
					tkn.tokenize(stream);
					System.out.println("Term Field :: " + stream.tokenStream);
					transformIdxDoc.addField(field, stream);
				}
				tkn = null;
				stream = null;
				tokens = null;
				break;
			case AUTHOR:
				tkn = transformTknizer.get(field);
				stream = new TokenStream(transformWikiDoc.getAuthor());
				tkn = transformTknizer.get(field);
				tkn.tokenize(stream);
				transformIdxDoc.addField(field, stream);
				System.out.println("Author Field :: " + stream.tokenStream);
				tkn = null;
				stream = null;
				break;
			case CATEGORY:
				tokens = new ArrayList<String>();

				List<String> wikiCategory = transformWikiDoc.getCategories();
				if (wikiCategory != null && wikiCategory.size() > 0) {
					tkn = transformTknizer.get(field);
					for (String category : wikiCategory) {
						tokens.add(category);
					}

					firstToken = tokens.get(0);
					if (stream == null) {
						stream = new TokenStream(firstToken);
						tokens.remove(0);
					}
					stream.append(tokens.toArray(new String[tokens.size()]));
					tkn = transformTknizer.get(field);
					tkn.tokenize(stream);
					transformIdxDoc.addField(field, stream);
					System.out.println("Category Field :: " + stream.tokenStream);
				}
				
					tkn = null;
					stream = null;
					tokens = null;
				
				break;
			case LINK:
				Set<String> wikiLinks = transformWikiDoc.getLinks();
				tokens = new ArrayList<String>();
				if (wikiLinks != null && wikiLinks.size() > 0) {
					tkn = transformTknizer.get(field);
					for (String link : wikiLinks) {
						tokens.add(link);
					}
					firstToken = tokens.get(0);
					if (stream == null) {
						stream = new TokenStream(firstToken);
						tokens.remove(0);
					}
					stream.append(tokens.toArray(new String[tokens.size()]));
					tkn = transformTknizer.get(field);
					tkn.tokenize(stream);
					transformIdxDoc.addField(field, stream);
					System.out.println("Link Field :: " + stream.tokenStream);
				}
				
					tkn = null;
					stream = null;
				
				break;
			}
		}
		return transformIdxDoc;
	}
}
