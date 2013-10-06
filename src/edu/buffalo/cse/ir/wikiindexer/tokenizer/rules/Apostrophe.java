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

			contrac = new HashMap<String, String>();
			for (String key : props.stringPropertyNames()) {
				String value = props.getProperty(key);
				contrac.put(key, value);
			}

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
				token = stream.next();
				if (token != null) {
					stream.previous();

					boolean split_token = splitToken(token);
					if (!split_token) {
						token = RemoveApostrophe(token);
					} else {
						token_array = RemoveApostropheArray(token);
					}
					if (!split_token) {
						stream.set(token);
						stream.next();

					} else {
						stream.set(token_array);

						stream.next();
					}
				}

			}

		}

		stream.reset();
	}

	public boolean splitToken(String token) {

		String updated_token = null;

		if (token.matches("(.*?)'(.*)")) {

			updated_token = contrac.get(token.toLowerCase());

		}

		if (updated_token == null) {

			return false;
		} else {

			return true;
		}
	}

	public String RemoveApostrophe(String token) {

		if (token.matches(".*?('s).*")) {
			token = token.replaceAll("('s)", "");
		} else if (token.matches(".*?(s'|')(.*?){0,}")) {
			token = token.replaceAll("'", "");
		}

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

		tempArr = updated_token.split(" ");
		return tempArr;
	}
}
