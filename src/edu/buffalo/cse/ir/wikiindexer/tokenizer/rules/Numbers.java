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

@RuleClass(className = RULENAMES.NUMBERS)
public class Numbers implements TokenizerRule {

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
					token = removeNumbers(token);
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

	public String removeNumbers(String token) {
		Pattern alpha_numeric = Pattern.compile("\\d{8}");
		Matcher alnum_matcher = alpha_numeric.matcher(token);
		boolean m = alnum_matcher.find();
		StringBuilder s = new StringBuilder();
		StringBuilder r = new StringBuilder();
		int i = 0;
		String ta = new String();
		String ra = null;

		token = token.trim();
		if (m) {

			s.append(alnum_matcher.group(0));

			token = token.replace(s, "###");
			i += 1;
			m = alnum_matcher.find();
			ta = s.toString();

		}
		token = token.replaceAll("([0-9]+[,\\.]*[0-9]*)", "");
		if (token.contains("  ")) {
			token = token.trim().replace("  ", " ");
		}
		if (!ta.isEmpty())
			token = token.replace("###", ta);

		return token;
	}
}
