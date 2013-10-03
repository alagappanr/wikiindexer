/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import edu.buffalo.cse.ir.wikiindexer.FileUtil;
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
@RuleClass(className = RULENAMES.STOPWORDS)
public class Stopwords implements TokenizerRule {

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
					token = removeStopword(token);
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
	
	public String removeStopword(String token){
//		System.out.println("Received-"+token);
		ArrayList<String> stopword_list = new ArrayList<String>();
		
        Properties props = new Properties();
		try {
			props = FileUtil.loadProperties("../../../../../../../../files/stopwords.config");
		} catch (FileNotFoundException e) {
			System.err.println("Unable to open or load the specified file: " + "stopwords.config");
		} catch (IOException e) {
			System.err.println("Error while reading properties from the specified file: " + "stopwords.config");
		}
		
		for (String key : props.stringPropertyNames()) {
		    stopword_list.add(key);
		}
//		System.out.println(stopword_list.toString());
		if (stopword_list.contains(token)){
			token="";
		}
//		System.out.println("Returned-"+token);
		return token;
	}
}
