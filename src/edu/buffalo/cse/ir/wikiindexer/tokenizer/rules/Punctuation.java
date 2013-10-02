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
 * An implementation of the Porter stemmer for English
 * THis is from the author's website directly
 * Wrapped in the framework class
 * @author nikhillo
 *
 */
//example of annotation, for classes you write annotate accordingly
@RuleClass(className = RULENAMES.PUNCTUATION)
public class Punctuation implements TokenizerRule {

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerRule#apply(edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream)
	 */
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream != null) {
			String token;
			while (stream.hasNext()) { 
				token = stream.next(); //read next token
				if (token != null) {
					stream.previous(); //move token back as we need to change last read token
					token = RemovePunctuation(token);
					if(!token.isEmpty()){
						stream.set(token);	// change value
						stream.next();					 // move iter to next position, beyond the token we just changed
					}
					else{
						stream.remove(); //move not required because the remove method automagically does this for us.
					}
				}
				
			}
			
			stream.reset();
		}

	}
	
	public String RemovePunctuation(String token){
//		System.out.println(token);
		if (token.matches(".*?[A-Za-z0-9]+\\.[A-Za-z0-9]+\\.?[A-Za-z0-9]*\\.?[A-Za-z0-9]*(\\?|!)")) {
//			System.out.println("Token matches 1-"+token);
			token = token.replaceAll("(\\?|!)", "");

		} else if (token.matches(".*?(\\?|\\.|!)")) {
//			System.out.println("Token matches 2-"+token);
			token = token.replaceAll("(\\?|\\.|!)", "");

		}
		else{
//			System.out.println("Doesn't match pattern-"+token);
			;
		}
		return token;
	}

}
