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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

@RuleClass(className = RULENAMES.HYPHEN)
public class Hyphen implements TokenizerRule {

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
					String[] groups;

					Pattern alpha_numeric = Pattern
							.compile("([a-zA-Z]*\\-[a-zA-Z]*[0-9][a-zA-Z0-9]*|[a-zA-Z]*[0-9][a-zA-Z0-9]*\\-[a-zA-Z]*|[a-zA-Z]*[0-9][a-zA-Z0-9]*\\-[a-zA-Z]*[0-9][a-zA-Z0-9]*)");
					Matcher alnum_matcher = alpha_numeric.matcher(token);
					boolean m = alnum_matcher.find();
					StringBuilder s = new StringBuilder();
					StringBuilder r = new StringBuilder();
					int i = 0;
					while (m) {

						s.append(alnum_matcher.group(0));

						r.append(alnum_matcher.group(0).replaceAll("\\-", " "));
						i += 1;
						m = alnum_matcher.find();
					}
					String ta = s.toString();
					String ra = r.toString();

					token = token.replaceAll("\\-", " ");
					token = token.trim();
					token = token.replace(ra, ta);

					token = token.replaceAll("  +", " ");

					if (token.isEmpty() || token.equals(" ")) {

						stream.previous();
						stream.remove();
						stream.next();
					} else {

						stream.previous();
						stream.set(token);
						stream.next();
					}

				}

			}
		}
		stream.reset();

	}

	public boolean splitToken(String token) {

		if (token.matches("(.*?)[-+](.*)")) {

			return true;
		}
		return false;
	}

	public String RemoveHyphen(String token) {

		token = token.replaceAll("[-+]", "");

		return token;
	}

	/*
	 * method which will return a String Array when the special character occurs
	 * in the middle of the token
	 */

	public String[] RemoveHyphenArray(String token) {

		String[] token_array = token.split("[-+]");

		return token_array;
	}

}