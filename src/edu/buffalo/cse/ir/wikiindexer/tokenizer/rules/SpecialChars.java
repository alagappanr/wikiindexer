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
@RuleClass(className = RULENAMES.SPECIALCHARS)
public class SpecialChars implements TokenizerRule {

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerRule#apply(edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream)
	 */
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream != null) {
//			System.out.println("Entry Stream Size->"+stream.getSize());
			String token;
			while (stream.hasNext()) { 
				token = stream.next(); //read next token
				if (token != null) {
					stream.previous(); //move token back as we need to change last read token
					boolean split = splitToken(token);
//					System.out.println("To split or not to split");
					if(!split){
//						System.out.println("NO split");
						token = RemoveSpecialChars(token);
//						System.out.println("|"+token+"|");
						if(token.isEmpty()){
							stream.remove();
						} else {
							stream.set(token);		
						}
						// change value
						stream.next();					// move iter to next position, beyond the token we just changed
//						System.out.println("Set just string");
					}
					else{
//						System.out.println("Split");
						String[] token_array = RemoveSpecialCharsArray(token);
//						System.out.println(token_array.length);
						if(token_array.length!=0){
							for(int i=0;i<token_array.length;i++){
//								System.out.println(token_array[i]);
								if(token_array[i].isEmpty()){
									java.util.List<String> list = new ArrayList<String>(Arrays.asList(token_array));
									list.remove(i);
									token_array = list.toArray(new String[0]);
								}
							}
							if(stream.getSize()==1){
//								System.out.println("Before"+stream.getFullTokenStream());
//								System.out.println("Token Array|"+token_array.toString()+"|");
								//Joining the token_array to a single element
								StringBuilder ta = new StringBuilder();
								for(int ite=0;ite<token_array.length;ite++){
									if(ite!=0)
										ta.append(" "+token_array[ite]);
									else
										ta.append(token_array[ite]);
								}
								
								String to = ta.toString().trim().replaceAll(" +", " ");
//								System.out.println("YO"+to+"YO");
//								stream.set(token_array);
								if(to.isEmpty()){
									stream.remove();
								} else {
									stream.set(to);
								}
//								stream.previous();
//								String current = stream.next();
//								System.out.println("CUR"+current+"CUR");
//								System.out.println("Midway"+stream.getFullTokenStream());
								
//								stream.mergeWithPrevious();
//								System.out.println("After"+stream.getFullTokenStream());
							} else {
//							System.out.println(stream.getFullTokenStream());
								stream.set(token_array); 
								stream.next();
							}
//							System.out.println("Set array");
						} else {
							stream.remove();
						}
					}
					
//					if(!token.isEmpty()){
//						stream.set(token);	// change value
//						stream.next();					 // move iter to next position, beyond the token we just changed
//					}
//					else{
//						stream.remove(); //move not required because the remove method automagically does this for us.
//					}
				}
			}

		}
//		System.out.println("Exit stream size++++"+stream.getSize());
//		System.out.println(stream.getFullTokenStream());
		stream.reset();
//		String token2;
//		while (stream.hasNext()) { 
//			token2 = stream.next(); 
//		    System.out.println("****"+token2+"***");
//		}
//		stream.reset();
	}
	 
	public boolean splitToken(String token){		
//		System.out.println("CHECKING"+token);
		if (token.matches("(.*?)[^\\dA-Za-z\\-\\. ](.*)")) {
//			System.out.println("Matches pattern -> "+token);
//			System.out.println("True");
			return true;
		}
		return false;
	}
	public String RemoveSpecialChars(String token){
//		System.out.println("Received token->|"+token+"|");
		token = token.replaceAll("[^\\dA-Za-z\\-\\. ]", ""); 
//		System.out.println("Returning->|"+token+"|");
		return token;
	}
	
	/*
	 *method which will return a String Array
	 * when the special character occurs in the middle of the 
	 * token
	 */
	
	public String[] RemoveSpecialCharsArray(String token){
//		System.out.println("Received token->|"+token+"|");
		String[] token_array = token.split("[^\\dA-Za-z\\-\\. ]"); 
//		System.out.println("Normalized->|"+token+"|");
//		System.out.println("Returning->|"+token_array.length+"|");
//		String test_string = new String();
//		for(int i=0;i<token_array.length;i++){
////			test_string = new String();
////			token_array[i] = token_array[i].replaceAll(" +", "");
//			test_string.concat(token_array[i]);
////			System.out.println(test_string+" after appending |"+token_array[i]+"|");
//		}
////		System.out.println("|"+test_string+"|at the end");
//		if(token_array.length==2){
//			System.out.println("----"+token_array[0]+"-----"+token_array[1]+"----");
//			System.out.println("|"+token_array[1].trim()+"|");
//		}
		
		return token_array;
	}

}




