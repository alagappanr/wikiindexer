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
@RuleClass(className = RULENAMES.HYPHEN)
public class Hyphen implements TokenizerRule {

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
					token = RemoveHyphen(token);
					if(token!=null&&token!=""){
						stream.set(RemoveHyphen(token));	// change value
					}
					stream.next(); // move iter to next position, beyond the token we just changed
				}
			}

		}

		stream.reset();
	}
	
	public String RemoveHyphen(String token){
		String old_val, new_val;
		//Alpha Numeric Only
		Pattern alpha_numeric = Pattern.compile("([a-zA-Z]+\\-[0-9]+)|([0-9]+\\-[a-zA-z]+)");
		Matcher alnum_matcher = alpha_numeric.matcher(token);
		
		//Alphabets only
		Pattern alpha_only = Pattern.compile("[a-zA-Z]+\\-[a-zA-Z]+");
		Matcher alonly_matcher = alpha_only.matcher(token);
		
		//Spaces Padded on either side
		Pattern padded_spaces = Pattern.compile("([\\s]+\\-+[\\s]*)|([\\s]*\\-+[\\s]+)");
		Matcher spaces_matcher = padded_spaces.matcher(token);
		
		//Code Style
		Pattern code_style = Pattern.compile("([a-zA-Z0-9]+\\-+)|(\\-+[a-zA-Z0-9]+)");
		Matcher code_matcher = code_style.matcher(token);
		
		if(alnum_matcher.find()){
//			System.out.println("Alnum Match found");
//			System.out.println(alnum_matcher.group(0));
			;
		}
		else if(alonly_matcher.find()){
//			System.out.println("Alpha only");
//			System.out.println(alonly_matcher.group(0));
			old_val = alonly_matcher.group(0);
			new_val = old_val.replaceAll("\\-", " ");
			token = token.replace(old_val, new_val);
		}
		else if(spaces_matcher.find()){
//			System.out.println("Spaces Padded");
//			System.out.println(spaces_matcher.group(0));
			old_val = spaces_matcher.group(0);
			new_val = old_val.replaceAll("([\\s]+\\-+[\\s]*)|([\\s]*\\-+[\\s]+)", "");
			token = token.replace(old_val, new_val);
		}
		else if(code_matcher.find()){
//			System.out.println("Coding Style");
//			System.out.println(code_matcher.group(0));
			old_val = code_matcher.group(0);
			new_val = old_val.replaceAll("\\-","");
			token = token.replace(old_val, new_val);
		}

//		System.out.println("Returning->|"+token+"|");
		return token;
	}


}




