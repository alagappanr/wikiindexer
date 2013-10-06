/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

@RuleClass(className = RULENAMES.PUNCTUATION)
public class Punctuation implements TokenizerRule {

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
			while (stream.hasNext()) {
				token = stream.next();
				if (token != null) {
					stream.previous();
					token = RemovePunctuation(token);
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

	public String RemovePunctuation(String token) {

		if (token
				.matches(".*?[A-Za-z0-9]+\\.[A-Za-z0-9]+\\.?[A-Za-z0-9]*\\.?[A-Za-z0-9]*(\\?|!)")) {

			token = token.replaceAll("(\\?|!)", "");

		} else if (token.matches(".*?(\\?|\\.|!)")) {

			token = token.replaceAll("(\\?|\\.|!)", "");

		} else {

			;
		}
		return token;
	}

}
