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
		Pattern alpha_numeric = Pattern.compile("\\d{8}");
		Matcher alnum_matcher = alpha_numeric.matcher(token);
		boolean m = alnum_matcher.find();
		StringBuilder s = new StringBuilder();
		StringBuilder r = new StringBuilder();
		int i=0;
		String ta = new String();
		String ra = null;
//		System.out.println(ta);
//		System.out.println(ra);
		token = token.trim();
		if(m){
			System.out.println("Found!");
			s.append(alnum_matcher.group(0));
			System.out.println(s);
			//alnum_matcher.replaceAll("$1");
			token = token.replace(s,"###");
			i+=1;
			m = alnum_matcher.find();
			ta = s.toString();
			
		}
		token = token.replaceAll("([0-9]+[,\\.]*[0-9]*)", "");
		if (token.contains("  ")) {
			token = token.trim().replace("  ", " ");
		}
		if(!ta.isEmpty())
			token = token.replace("###", ta);
		
		System.out.println(token);

		return token;
	}
}
