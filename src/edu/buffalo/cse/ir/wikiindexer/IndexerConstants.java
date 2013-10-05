/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author nikhillo
 * This interface simply defines constants that are used to load the properties.
 * DO NOT EDIT THIS FILE. We will be replacing this with our version of the file
 * when we run tests.
 */
public interface IndexerConstants {
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface RequiredConstant {
		
	}
	
	/* Total number of properties */
	public static final int NUM_PROPERTIES = 18;
	
	/* Root directory */
	@RequiredConstant
	public static final String ROOT_DIR = "root.dir";
	
	/* Dump file name */
	@RequiredConstant
	public static final String DUMP_FILENAME = "dump.filename";
	
	/* Total number of threads */
	@RequiredConstant
	public static final String NUM_TOKENIZER_THREADS = "tknizer.nthreads";
	
	/* Fully qualified temporary directory name */
	@RequiredConstant
	public static final String TEMP_DIR = "tmp.dir";
	
	/* comma separated name of rules that must be tested pre-tokenization */
	public static final String PRETKNRULES = "pretkn.rules";
	
	/* tokenizer rule class names */
	public static final String PUNCTUATIONRULE = "punct.rule";
	public static final String APOSTROPHERULE = "apos.rule";
	public static final String HYPHENRULE = "hyph.rule";
	public static final String SPECIALCHARRULE = "spch.rule";
	public static final String WHITESPACERULE = "space.rule";
	public static final String DATERULE = "date.rule";
	public static final String STOPWORDSRULE = "stwrds.rule";
	public static final String ACCENTRULE = "accnt.rule";
	public static final String CAPITALIZATIONRULE = "captl.rule";
	public static final String NUMBERSRULE = "nmbrs.rule";
	public static final String DELIMRULE = "delim.rule";
	public static final String STEMMERRULE = "stem.rule";
	public static final String DELIMDOTRULE = "ddot.rule";
	
	
}
