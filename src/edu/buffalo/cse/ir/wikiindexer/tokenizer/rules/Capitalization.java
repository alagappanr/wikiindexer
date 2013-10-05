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
// example of annotation, for classes you write annotate accordingly
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
			token = stream.next(); // read next token
			if (token != null) {
				if (token.trim().contains(" ")) {
					//System.out.println("token ::" + token );
					tempString = sentenceCapitalization(token);
					//System.out.println("tempString ::" + tempString );
					stream.previous();
					stream.set(tempString);
					stream.next();
				} else {
					stream.previous(); // move token back as we need to change
										// last read token
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
						stream.set(token); // change value
						stream.next(); // move iter to next position, beyond the
										// token we just changed
					} else {
						stream.remove(); // move not required because the remove
											// method automagically does this
											// for us.
					}
				}
				

			}

		}
		
		
		//System.out.println("stream finally ::" + stream.getFullTokenStream());
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
//					System.out.println("tempStream :: "+ tempStream.getFullTokenStream());
					tempStream = invokeTokenizer(tempStream);
//					System.out.println("tempStream :: "+ tempStream.getFullTokenStream());
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
		// System.out.println("Received-"+token);
		if (!token.matches("[a-z]+[A-Za-z0-9]*")) {
			if (prev) { // Convert to lower case only if it is the first word.
			// token=token.toLowerCase();
				return true;
			}
		} else {
			;
		}
		// System.out.println("Returned-"+token);
		return false;
	}

}
