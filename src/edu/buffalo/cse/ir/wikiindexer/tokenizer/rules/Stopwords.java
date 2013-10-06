/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import edu.buffalo.cse.ir.wikiindexer.FileUtil;
import edu.buffalo.cse.ir.wikiindexer.IndexerConstants;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

/**
 * An implementation of the Porter stemmer for English THis is from the author's
 * website directly Wrapped in the framework class
 * 
 * @author nikhillo
 * 
 */

@RuleClass(className = RULENAMES.STOPWORDS)
public class Stopwords implements TokenizerRule {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerRule#apply(edu.buffalo
	 * .cse.ir.wikiindexer.tokenizer.TokenStream)
	 */
	ArrayList<String> stopword_list;

	public Stopwords(Properties props) {

		try {
			String stopConfigFileName = "stopwords.config";
			String stopConfigFilePath = FileUtil.getRootFilesFolder(props);
			props = FileUtil.loadProperties(stopConfigFilePath
					+ stopConfigFileName);

			stopword_list = new ArrayList<String>();
			for (String key : props.stringPropertyNames()) {
				stopword_list.add(key);
			}

		} catch (FileNotFoundException e) {
			System.err.println("Unable to open or load the specified file: "
					+ "stopwords.config");
		} catch (IOException e) {
			System.err
					.println("Error while reading properties from the specified file: "
							+ "stopwords.config");
		}
	}

	public void apply(TokenStream stream) throws TokenizerException {
		if (stream != null) {
			String token;
			while (stream.hasNext()) {
				token = stream.next();
				if (token != null) {
					stream.previous();

					token = removeStopword(token);
					if (!token.isEmpty()) {
						stream.set(token);
						stream.next();

					} else {
						stream.remove();

					}
				}

			}

			stream.reset();
		}

	}

	public String removeStopword(String token) {

		String tokens[];

		if (stopword_list.contains(token.toLowerCase())) {
			token = "";
		}

		if (token.contains(" ")) {
			tokens = token.split(" ");
			for (String word : tokens) {
				if (stopword_list.contains(word.toLowerCase())) {
					token = token.replace(word, " ");
				}
			}

			token = token.trim().replace("  ", "");
		}

		return token;
	}
}
