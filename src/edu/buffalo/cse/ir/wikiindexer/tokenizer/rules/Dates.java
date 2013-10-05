/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
@RuleClass(className = RULENAMES.DATES)
public class Dates implements TokenizerRule {

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerRule#apply(edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream)
	 */
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream != null) {
//			String token;
//			String full_stream = stream.getFullTokenStream();
////			System.out.println(stream.getFullTokenStream());
//			
//			full_stream = full_stream.replace("[", "");
//			full_stream = full_stream.replace("]", "");
//			full_stream = full_stream.replace(", "," ");
//			String[] newArr = full_stream.split(" ");
//			for( int i = 0; i < newArr.length; i++)
//			{
//			    stream.append(newArr[i]);
//			}

			
			stream.reset();
		}

	}
	@SuppressWarnings("deprecation")
	public static String ConvertDate(String token){
		String[] date_formats = {
				"yyyy-MM-dd",
				"yyyy MM dd",
				"yyyy/MM/dd", 
				"dd/MM/yyyy",
				"dd-MM-yyyy",
				"yyyy MMMMM dd",
				"yyyy dd MMM",
				"dd MMMMM yyyy",
				"dd MMMMM",
				"MMMMM dd yyyy",
				"MMMMM dd",
				"yyyy GG",
				"yyyy"};

//		String output_date = null;
		for (String formatString : date_formats)
		{
			try
			{   SimpleDateFormat sdf = new SimpleDateFormat(formatString);
			    sdf.setLenient(false);
				Date mydate = sdf.parse(token);
				SimpleDateFormat outdate = new SimpleDateFormat("yyyyMMdd");
//				Calendar c = Calendar.getInstance(); 
//				c.setTime(mydate);
//				c.set(Calendar.YEAR, 1900);
//				System.out.println(mydate.getYear());
//				System.out.println(c.get(Calendar.YEAR));
//				System.out.println(formatString);
//				mydate = c.getTime();
				return outdate.format(mydate);
//				break;
			}
			catch (ParseException e) {
				System.out.println("Next!");
			}
		}
		System.out.println("Nothing");
		return token;
	}
}
