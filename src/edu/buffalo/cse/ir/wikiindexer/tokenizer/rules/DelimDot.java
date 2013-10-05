/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.Arrays;

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
@RuleClass(className = RULENAMES.DELIMDOT)
public class DelimDot implements TokenizerRule {

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerRule#apply(edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream)
	 */
	
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream != null) {
			String token;
			String[] after_split;
			while (stream.hasNext()) { 
				token = stream.next(); //read next token
				if (token != null) {
					stream.previous(); //move token back as we need to change last read token
					after_split = DelimSplitter(token, "\\.");//Where does this delim come from???
					if(after_split.length!=0){
						stream.set(after_split);	// change value
						stream.next();					 // move iter to next position, beyond the token we just changed
					}
					else{
						stream.remove(); //move not required because the remove method automagically does this for us.
					}					}
				}
				
			}
			
			stream.reset();
		}

	public static String[] DelimSplitter(String token, String delim){
		String[] after_split = null;
//		System.out.println("Received token->|"+token+"|");
		token = token.replaceAll("\\n|\\r", "");
//		token = token.replaceAll("^\\s+|\\s+$|\\s*(\n)\\s*|(\\s)\\s*", "$1$2");
        String multi_delim = "^XXX+|XXX$|XXX*(\n)XXX*|(XXX)XXX*";
        multi_delim = multi_delim.replace("XXX", delim);
//        System.out.println(multi_delim);
		token = token.replaceAll(multi_delim, "$1$2");

//		System.out.println("After removing extra delim->"+token);
		after_split = token.split(delim);
//		System.out.println("After split->"+Arrays.toString(after_split));

		return after_split;
	}	
}