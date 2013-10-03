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
// example of annotation, for classes you write annotate accordingly
@RuleClass(className = RULENAMES.APOSTROPHE)
public class Apostrophe implements TokenizerRule {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerRule#apply(edu.buffalo
	 * .cse.ir.wikiindexer.tokenizer.TokenStream)
	 */

	Map<String, String> contrac;

	public Apostrophe(Properties props) {

		try {
			String stopConfigFileName = "contractions.config";
			String stopConfigFilePath = FileUtil.getRootFilesFolder(props);
			props = FileUtil.loadProperties(stopConfigFilePath
					+ stopConfigFileName);
			//System.out.println(stopConfigFilePath + stopConfigFileName);
			contrac = new HashMap<String, String>();
			for (String key : props.stringPropertyNames()) {
				String value = props.getProperty(key);
				contrac.put(key, value);
			}
			//System.out.println(contrac);
		} catch (FileNotFoundException e) {
			System.err.println("Unable to open or load the specified file: "
					+ "contractions.config");
		} catch (IOException e) {
			System.err
					.println("Error while reading properties from the specified file: "
							+ "contractions.config");
		}
	}

	public void apply(TokenStream stream) throws TokenizerException {

		if (stream != null) {
			String token;
			String[] token_array = null;
			while (stream.hasNext()) {
				token = stream.next(); // read next token
				if (token != null) {
					stream.previous(); // move token back as we need to change
										// last read token
					boolean split_token = splitToken(token);
					if (!split_token) {
						token = RemoveApostrophe(token);
					} else {
						token_array = RemoveApostropheArray(token);
					}
					if (!split_token) {
						stream.set(token); // change value
						stream.next(); // move iter to next position, beyond the
										// token we just changed
					} else {
						stream.set(token_array); // move not required because
													// the remove method
													// automagically does this
													// for us.
						stream.next();
					}
				}
				// System.out.println(stream.tokenStream);
			}

		}

		stream.reset();
	}

	public boolean splitToken(String token) {
		
		String updated_token = null;
		
		// System.out.println(token);
		if (token.matches("(.*?)'(.*)")) {
			// System.out.println("Matches pattern -> "+token);
			updated_token = contrac.get(token.toLowerCase());
			// System.out.println(updated_token);
		}

		if (updated_token == null) {
			// System.out.println("No Split");
			return false;
		} else {
			// System.out.println("Splitting");
			return true;
		}
	}

	public String RemoveApostrophe(String token) {
		// Matched simple Apostrophe
		if (token.matches(".*?('s).*")) {
			token = token.replaceAll("('s)", "");
		} else if (token.matches(".*?(s'|')(.*?){0,}")) {
			token = token.replaceAll("'", "");
		}
		// System.out.println("Returning without split "+token);
		return token;
	}

	public String[] RemoveApostropheArray(String token) {
		String[] tempArr = null;

		String updated_token = null;

		updated_token = contrac.get(token);
		if (updated_token == null) {
			updated_token = contrac.get(token.toLowerCase());
			updated_token = updated_token.substring(0, 1).toUpperCase()
					+ updated_token.substring(1);
		}
		// System.out.println("Matched something in the contractions list");
		// System.out.println("updated_token before split"+updated_token);
		tempArr = updated_token.split(" ");
		return tempArr;
	}
}
