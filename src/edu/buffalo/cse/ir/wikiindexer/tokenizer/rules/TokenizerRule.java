/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;

/**
 * Interface that must be implemented by any tokenizer rule class
 * 
 * @author nikhillo
 * 
 */
public interface TokenizerRule {
	/**
	 * Method to trigger transformation on the stream based on the current rule
	 * 
	 * @param stream
	 *            : The stream to be transformed
	 * @throws TokenizerException
	 *             IF any tokenization exception occurs
	 */
	public void apply(TokenStream stream) throws TokenizerException;

	/**
	 * The enum of pre-defined rules, you may add more rules if you like Refer
	 * the specifications for documentation on each rule
	 * 
	 * @author nikhillo
	 * 
	 */
	public enum RULENAMES {
		PUNCTUATION {
			public String toString() {
				return "PUNCTUATION";
			}
		},
		APOSTROPHE {
			public String toString() {
				return "APOSTROPHE";
			}
		},
		HYPHEN {
			public String toString() {
				return "HYPHEN";
			}
		},
		SPECIALCHARS {
			public String toString() {
				return "SPECIALCHARS";
			}
		},
		DATES {
			public String toString() {
				return "DATES";
			}
		},
		NUMBERS {
			public String toString() {
				return "NUMBERS";
			}
		},
		CAPITALIZATION {
			public String toString() {
				return "CAPITALIZATION";
			}
		},
		ACCENTS {
			public String toString() {
				return "ACCENTS";
			}
		},
		WHITESPACE {
			public String toString() {
				return "WHITESPACE";
			}
		},
		DELIM {
			public String toString() {
				return "DELIM";
			}
		},
		STEMMER {
			public String toString() {
				return "STEMMER";
			}
		},
		STOPWORDS {
			public String toString() {
				return "STOPWORDS";
			}
		},
		DELIMDOT {
			public String toString() {
				return "DELIMDOT";
			}
		}

	};
}
