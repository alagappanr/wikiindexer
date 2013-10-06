/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.ArrayList;
import java.util.Iterator;

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

@RuleClass(className = RULENAMES.CAPITALIZATION)
public class Capitalization implements TokenizerRule {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerRule#apply(edu.buffalo
	 * .cse.ir.wikiindexer.tokenizer.TokenStream)
	 */
	public void apply(TokenStream stream) throws TokenizerException {

		stream = invokeTokenizer(stream);
		stream.reset();
	}

	public TokenStream invokeTokenizer(TokenStream stream) {
		String token;
		String tempString;
		boolean previousWordIsLast = true, fixCaps = false;
		while (stream.hasNext()) {
			token = stream.next();
			if (token != null) {
				if (token.trim().contains(" ")) {

					tempString = sentenceCapitalization(token);

					stream.previous();
					stream.set(tempString);
					stream.next();
				} else {
					stream.previous();

					fixCaps = fixCapitalization(token, previousWordIsLast);
					if (fixCaps) {
						token = token.toLowerCase();
					}
					if (!token.isEmpty()) {
						if (token.endsWith(".")) {
							previousWordIsLast = true;
						} else {
							previousWordIsLast = false;
						}
						stream.set(token);
						stream.next();

					} else {
						stream.remove();

					}
				}

			}

		}

		return stream;
	}

	private String sentenceCapitalization(String tokenString) {

		ArrayList<String> tokens;
		ArrayList<String> tempTokens;
		TokenStream tempStream;
		String[] tokenSplit;
		Boolean stringStart = true;
		String tempString = null;
		if (tokenString != null) {

			if (tokenString.contains(" ")) {
				tokenSplit = tokenString.split(" ");
				if (tokenSplit.length != 0 && tokenSplit[0] != null) {
					tempStream = new TokenStream(tokenSplit[0]);
					tempStream.append(tokenSplit);
					tempStream.remove();

					tempStream = invokeTokenizer(tempStream);

					tempTokens = (ArrayList<String>) tempStream.getAllTokens();
					Iterator<String> tempTokenIte = tempTokens.iterator();
					tempString = "";
					while (tempTokenIte.hasNext()) {
						if (!stringStart) {
							tempString += " ";
							stringStart = false;
						}
						tempString += tempTokenIte.next();
						stringStart = false;
					}

				}

			}
		}

		return tempString;
	}

	public boolean fixCapitalization(String token, boolean prev) {

		if (!token.matches("[a-z]+[A-Za-z0-9]*")) {
			if (prev) {

				return true;
			}
		} else {
			;
		}

		return false;
	}

}
