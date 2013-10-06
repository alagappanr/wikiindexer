/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.text.Normalizer;

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

@RuleClass(className = RULENAMES.ACCENTS)
public class Accents implements TokenizerRule {

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
					token = ConvertAccents(token);
					if (!token.isEmpty()) {
						stream.set(token);
						stream.next();
					} else {
						stream.remove();
					}
				}

			}

		}

		stream.reset();
	}

	public String ConvertAccents(String token) {

		token = Normalizer.normalize(token, Normalizer.Form.NFD);

		token = token.replaceAll("[^\\p{ASCII}]", "");

		return token;
	}

}
