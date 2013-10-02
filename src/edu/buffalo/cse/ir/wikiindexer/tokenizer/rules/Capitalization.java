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
@RuleClass(className = RULENAMES.CAPITALIZATION)
public class Capitalization implements TokenizerRule {

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerRule#apply(edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream)
	 */
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream != null) {
			String token;
			boolean previousWordIsLast=true,fixCaps=false;
			while (stream.hasNext()) { 
				token = stream.next(); //read next token
				if (token != null) {
					stream.previous(); //move token back as we need to change last read token
					fixCaps = fixCapitalization(token, previousWordIsLast);
					if (fixCaps){
						token = token.toLowerCase();
					}
					if(!token.isEmpty()){
						if(token.endsWith(".")){
							previousWordIsLast = true;
						} else {
							previousWordIsLast = false;
						}
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
	
	public boolean fixCapitalization(String token, boolean prev){
//		System.out.println("Received-"+token);
		if(!token.matches("[a-z]+[A-Za-z0-9]*")){
			if(prev){ //Convert to lower case only if it is the first word.
//			    token=token.toLowerCase();
				return true;
			}
		} else {
			;
		}
//		System.out.println("Returned-"+token);
		return false;
	}

	
}
