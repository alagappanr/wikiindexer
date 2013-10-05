/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import edu.buffalo.cse.ir.wikiindexer.IndexerConstants;
import edu.buffalo.cse.ir.wikiindexer.indexer.INDEXFIELD;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.RuleClass;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

/**
 * Factory class to instantiate a Tokenizer instance The expectation is that you
 * need to decide which rules to apply for which field Thus, given a field type,
 * initialize the applicable rules and create the tokenizer
 * 
 * @author nikhillo
 * 
 */
public class TokenizerFactory {
	// private instance, we just want one factory
	private static TokenizerFactory factory;

	// properties file, if you want to read soemthing for the tokenizers
	private static Properties props;

	// Instantiation of different rules
	private static ArrayList<TokenizerRule> rules;

	/**
	 * Private constructor, singleton
	 * 
	 */
	private TokenizerFactory() {
		rules = new ArrayList<TokenizerRule>();
	}

	/**
	 * MEthod to get an instance of the factory class
	 * 
	 * @return The factory instance
	 */
	public static TokenizerFactory getInstance(Properties idxProps) {
		if (factory == null) {
			factory = new TokenizerFactory();
			props = idxProps;
		}

		return factory;
	}

	/**
	 * Method to get a fully initialized tokenizer for a given field type
	 * 
	 * @param field
	 *            : The field for which to instantiate tokenizer
	 * @return The fully initialized tokenizer
	 */
	public Tokenizer getTokenizer(INDEXFIELD field) {
		//
		/*
		 * For example, for field F1 I want to apply rules R1, R3 and R5 For F2,
		 * the rules are R1, R2, R3, R4 and R5 both in order So the pseudo-code
		 * will be like: if (field == F1) return new Tokenizer(new R1(), new
		 * R3(), new R5()) else if (field == F2) return new TOkenizer(new R1(),
		 * new R2(), new R3(), new R4(), new R5()) ... etc
		 */

		Tokenizer tknizer = null;
		TokenizerRule[] applyRules = null;
		try {
			instantiateRules();
			switch (field) {
			case TERM:
				applyRules = new TokenizerRule[] { rules.get(3), rules.get(6),
						rules.get(2), rules.get(1), rules.get(0), rules.get(4), 
						rules.get(11), rules.get(9), rules.get(8),
						rules.get(12), rules.get(7),
						rules.get(10) };
				return new Tokenizer(applyRules);
			case AUTHOR:
				applyRules = new TokenizerRule[] { 
//						rules.get(3), rules.get(6),
//						rules.get(2), rules.get(1), rules.get(0), rules.get(4), 
//						rules.get(11), rules.get(9), rules.get(8),
//						rules.get(12), rules.get(7),
//						rules.get(10) 
						};
				return new Tokenizer(applyRules);
			case CATEGORY:
				applyRules = new TokenizerRule[] { 
//						rules.get(3), rules.get(6),
//						rules.get(2), rules.get(1), rules.get(0),
//						rules.get(11), rules.get(9), rules.get(8),
//						rules.get(4), rules.get(12), rules.get(7),
//						rules.get(10) 
						};
				return new Tokenizer(applyRules);
			case LINK:
				applyRules = new TokenizerRule[] { 
//						rules.get(3) 
						};
				return new Tokenizer(applyRules);
			}

		} catch (TokenizerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tknizer;
	}

	/*
	 * Order in which rules are called. Index = Rule 0 = punct.rule 1 =
	 * apos.rule 2 = hyph.rule 3 = spch.rule 4 = space.rule 5 = date.rule 6 =
	 * accnt.rule 7 = stwrds.rule 8 = captl.rule 9 = nmbrs.rule 10 = stem.rule
	 * 11 = delim.rule, 12 = ddot.rule
	 */

	private void instantiateRules() {
		String[] constant = { IndexerConstants.PUNCTUATIONRULE,
				IndexerConstants.APOSTROPHERULE, IndexerConstants.HYPHENRULE,
				IndexerConstants.SPECIALCHARRULE,
				IndexerConstants.WHITESPACERULE, IndexerConstants.DATERULE,
				IndexerConstants.ACCENTRULE, IndexerConstants.STOPWORDSRULE,
				IndexerConstants.CAPITALIZATIONRULE,
				IndexerConstants.NUMBERSRULE, IndexerConstants.STEMMERRULE,
				IndexerConstants.DELIMRULE, IndexerConstants.DELIMDOTRULE };
		// String[] constant = { IndexerConstants.HYPHENRULE,
		// IndexerConstants.SPECIALCHARRULE,
		// IndexerConstants.WHITESPACERULE,
		// IndexerConstants.ACCENTRULE};
		for (String constantName : constant) {
			String className = props.getProperty(constantName);
			if (className != null) {
				try {
					Class cls = Class.forName(className);
					Constructor[] cnstrs = cls.getDeclaredConstructors();
					Class[] ptypes;
					RuleClass rclass = (RuleClass) cls
							.getAnnotation(RuleClass.class);
					String rval = rclass.className().toString();

					for (Constructor temp : cnstrs) {
						ptypes = temp.getParameterTypes();
						if (ptypes.length == 0) {
							rules.add((TokenizerRule) temp.newInstance(null));
							break;
						} else if (ptypes.length == 1
								&& "java.util.Properties".equals(ptypes[0]
										.getName())) {
							rules.add((TokenizerRule) temp.newInstance(props));
							break;
						} else {
							System.err
									.println("Unsupported constructor: Should be parameter less or use Properties");
						}
					}

				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
