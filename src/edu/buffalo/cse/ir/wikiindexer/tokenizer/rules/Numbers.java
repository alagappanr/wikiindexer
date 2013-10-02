/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

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
@RuleClass(className = RULENAMES.NUMBERS)
public class Numbers implements TokenizerRule {

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
					token = removeNumbers(token);
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
	public String removeNumbers(String token){
//		System.out.println("Received-"+token);
//		token = token.replaceAll("([0-9]*[,\\.]*[0-9]*)", "");
		token = token.replaceAll("([0-9]+[,\\.]*[0-9]*)", "");
//		System.out.println("Returned-"+token);
		return token;
	}
}
