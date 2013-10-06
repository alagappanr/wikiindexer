/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
@RuleClass(className = RULENAMES.DATES)
public class Dates implements TokenizerRule {

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerRule#apply(edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream)
	 */
	public void apply(TokenStream stream) throws TokenizerException {
//		String[] regex_patterns = {
//				"\\d{2,4}(\\s|\\-|/|,)\\d{2}(\\s|\\-|/|,)\\d{2}",
//				"\\d{2}/\\d{2}/\\d{2}",
//				"\\d{2}/\\d{2}/\\d{2,4}",
//				"\\d{2}-\\d{2}-\\d{2,4}",
//				"\\d{4} [a-zA-Z]{3,} \\d{2}",
//				"\\d{4} \\d{2} [a-zA-Z]{3,}",
//				"\\d{1,2} [a-zA-Z]{3,} \\d{4}",
//				"[a-zA-Z]{3,}(\\s|\\-|/|,)+\\d{1,2}(\\s|\\-|/|,)+\\d{4}",
//				"\\d{1,4} [a-zA-Z]{3,}",
//				"[a-zA-Z]{3,} \\d{2} \\d{4}",
//				"[a-zA-Z]{3,} \\d{2}",
//				"\\d{2,4} [AD|BC]{2}",
//				"\\d{4}"};
//		
//		if (stream != null) {
//			String full_stream, full_text;
//			if(stream.getSize()!=1){
////				System.out.println("Size not 1");
//				full_stream = stream.getFullTokenStream();
//				for(String pattern: regex_patterns){
//					Pattern date_pattern = Pattern.compile(pattern);
//					Matcher date_matcher = date_pattern.matcher(full_stream);
//					boolean m = date_matcher.find();
//					String s=null;
//					String r=null;
//					int i=0;
//					while(m){
////						System.out.println("Found!");
//						s = date_matcher.group(0);
//						//date_matcher.replaceAll("$1");
//						r = ConvertDate(s);
//						//r.append(date_matcher.group(0).replaceAll("\\-", " "));
//					//	i+=1;
//						m = date_matcher.find();
//					}
////					System.out.println(s);
////					System.out.println(r);
//				}
////				full_stream = full_stream.replace("[", "");
////				full_stream = full_stream.replace("]", "");
////				full_stream = full_stream.replace(", "," ");
////				String[] newArr = full_stream.split(" ");
////				for( int i = 0; i < newArr.length; i++)
////				{
////				    stream.append(newArr[i]);
////				}
//				stream.set(full_stream);
//				stream.next();
//			} else {
//				full_text = stream.next();
//				String s=null;
//				String r=null;
//				boolean forBreak = false;
////				System.out.println("Size=1");
//				for(String pattern: regex_patterns){
//					Pattern date_pattern = Pattern.compile(pattern);
//					System.out.println(pattern);
//					Matcher date_matcher = date_pattern.matcher(full_text);
//					boolean m = date_matcher.find();
//					int i=0;
//					while(m){
////						System.out.println("Found!");
////						System.out.println(pattern);
//						s = date_matcher.group(0);
////						System.out.println(s);
//						//date_matcher.replaceAll("$1");
//						r = ConvertDate(s);
//						//r.append(date_matcher.group(0).replaceAll("\\-", " "));
//					//	i+=1;
//						if(s.contains("BC")){
//							full_text = full_text.replace(s, "-"+r);
//						} else {
//							full_text = full_text.replace(s, r);
//						}
//						forBreak = true;
//						m = date_matcher.find();
//					}
//					if(forBreak==true){
//						break;
//					}
//				}
//				
////				System.out.println(s);
////				System.out.println(r);
////				System.out.println(full_text);
//				
////				System.out.println(stream.getFullTokenStream());
//				stream.removeAll();
////				System.out.println(stream.getFullTokenStream());
//				stream.append(full_text);
////				System.out.println(stream.getFullTokenStream());
//			}
//			
//			stream.reset();
//		}

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
				"MMMMM dd, yyyy",
				"MMMMM dd",
				"yyyy GG",
				"yyyy"};
//		System.out.println("Called with -"+token);
//		String output_date = null;
		for (String formatString : date_formats)
		{
			try
			{   SimpleDateFormat sdf = new SimpleDateFormat(formatString);
			    sdf.setLenient(false);
				Date mydate = sdf.parse(token);
				SimpleDateFormat outdate = new SimpleDateFormat("yyyyMMdd");
				Calendar c = Calendar.getInstance(); 
				c.setTime(mydate);
//				System.out.println("YEAR"+c.get(Calendar.YEAR));
				if(c.get(Calendar.YEAR)==1970){
					c.set(Calendar.YEAR, 1900);
				}
					
//				System.out.println(mydate.getYear());
//				System.out.println(c.get(Calendar.YEAR));
//				System.out.println(formatString);
				mydate = c.getTime();
//				System.out.println(formatString);
//				System.out.println("returning-"+outdate.format(mydate));
				return outdate.format(mydate);
//				break;
			}
			catch (ParseException e) {
//				System.out.println("Next!");
				;
			}
		}
//		System.out.println("Nothing");
		return token;
	}

}
