/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
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

@RuleClass(className = RULENAMES.SPECIALCHARS)
public class SpecialChars implements TokenizerRule {

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
					boolean split = splitToken(token);

					if (!split) {

						token = RemoveSpecialChars(token);

						if (token.isEmpty()) {
							stream.remove();
						} else {
							stream.set(token);
						}

						stream.next();

					} else {

						String[] token_array = RemoveSpecialCharsArray(token);

						if (token_array.length != 0) {
							for (int i = 0; i < token_array.length; i++) {

								if (token_array[i].isEmpty()) {
									java.util.List<String> list = new ArrayList<String>(
											Arrays.asList(token_array));
									list.remove(i);
									token_array = list.toArray(new String[0]);
								}
							}
							if (stream.getSize() == 1) {

								StringBuilder ta = new StringBuilder();
								for (int ite = 0; ite < token_array.length; ite++) {
									if (ite != 0)
										ta.append(" " + token_array[ite]);
									else
										ta.append(token_array[ite]);
								}

								String to = ta.toString().trim()
										.replaceAll(" +", " ");

								if (to.isEmpty()) {
									stream.remove();
								} else {
									stream.set(to);
								}

							} else {

								stream.set(token_array);
								stream.next();
							}

						} else {
							stream.remove();
						}
					}

				}
			}

		}

		stream.reset();

	}

	public boolean splitToken(String token) {

		if (token.matches("(.*?)[^\\dA-Za-z\\-\\. ](.*)")) {

			return true;
		}
		return false;
	}

	public String RemoveSpecialChars(String token) {

		token = token.replaceAll("[^\\dA-Za-z\\-\\. ]", "");

		return token;
	}

	/*
	 * method which will return a String Array when the special character occurs
	 * in the middle of the token
	 */

	public String[] RemoveSpecialCharsArray(String token) {

		String[] token_array = token.split("[^\\dA-Za-z\\-\\. ]");

		return token_array;
	}

}
