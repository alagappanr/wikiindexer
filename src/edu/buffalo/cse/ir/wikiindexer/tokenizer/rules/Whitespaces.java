/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.Arrays;

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

@RuleClass(className = RULENAMES.WHITESPACE)
public class Whitespaces implements TokenizerRule {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerRule#apply(edu.buffalo
	 * .cse.ir.wikiindexer.tokenizer.TokenStream)
	 */
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream != null) {
			String token;
			String[] after_split;
			while (stream.hasNext()) {
				token = stream.next();
				if (token != null) {
					stream.previous();
					after_split = Splitter(token);
					if (after_split.length != 0) {
						stream.set(after_split);
						stream.next();
					} else {
						stream.remove();
					}
				}
			}

		}

		stream.reset();
	}

	public static String[] Splitter(String token) {
		String[] after_split = null;

		token = token.replaceAll("^\\s+|\\s+$|\\s*(\n)\\s*|(\\s)\\s*", "$1$2");

		after_split = token.trim().split("\\s");

		return after_split;
	}
}
