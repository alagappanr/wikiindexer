/**
 * Test Commit from Eclipse. 
 */
package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author nikhillo
 * This class implements Wikipedia markup processing.
 * Wikipedia markup details are presented here: http://en.wikipedia.org/wiki/Help:Wiki_markup
 * It is expected that all methods marked "todo" will be implemented by students.
 * All methods are static as the class is not expected to maintain any state.
 */
public class WikipediaParser {
	/* TODO */
	/**
	 * Method to parse section titles or headings.
	 * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Sections
	 * @param titleStr: The string to be parsed
	 * @return The parsed string with the markup removed
	 */
	public static String parseSectionTitle(String titleStr) {
		if(titleStr!=null){
			titleStr = titleStr.replaceAll("=", "");	
			titleStr = titleStr.replaceAll("^\\s+|\\s+$|\\s*(\n)\\s*|(\\s)\\s*", "$1$2");
			titleStr = titleStr.trim();
			return titleStr;
		}
		else{
			return null;
		}
		
	}
	
	/* TODO */
	/**
	 * Method to parse list items (ordered, unordered and definition lists).
	 * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Lists
	 * @param itemText: The string to be parsed
	 * @return The parsed string with markup removed
	 */
	public static String parseListItem(String itemText) {
		if(itemText!=null){
			if(itemText.startsWith("#")){
			    itemText = itemText.replaceAll("#", "");
			}
			else if (itemText.startsWith(";")){
				itemText = itemText.replaceAll(";","");
			}
			else if (itemText.startsWith("*")){
				itemText = itemText.replaceAll("\\*", "");
			}
			else if (itemText.startsWith(":")){
				itemText = itemText.replaceAll(":", "");
			}
			itemText = itemText.replaceAll("^\\s+|\\s+$|\\s*(\n)\\s*|(\\s)\\s*", "$1$2");
			itemText = itemText.trim();
//			System.out.println("Returning------------"+itemText);
			return itemText;
		}
		else{
			return null;
		}
		
	}
	
	/* TODO */
	/**
	 * Method to parse text formatting: bold and italics.
	 * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Text_formatting first point
	 * @param text: The text to be parsed
	 * @return The parsed text with the markup removed
	 */
	public static String parseTextFormatting(String text) {
		if(text!=null){
			text = text.replaceAll("'", "");	
			text = text.replaceAll("^\\s+|\\s+$|\\s*(\n)\\s*|(\\s)\\s*", "$1$2");
			text = text.trim();
			return text;
		}
		else{
			return null;
		}
	}
	
	/* TODO */
	/**
	 * Method to parse *any* HTML style tags like: <xyz ...> </xyz>
	 * For most cases, simply removing the tags should work.
	 * @param text: The text to be parsed
	 * @return The parsed text with the markup removed.
	 */
	public static String parseTagFormatting(String text) {
		if(text!=null){
			//System.out.println(text);
//			text = escapeHtml4(text);
			text =  text.replaceAll("<(.*?)>", "");
			text = text.replaceAll("^\\s+|\\s+$|\\s*(\n)\\s*|(\\s)\\s*", "$1$2");
			text = text.trim();
			return text;			
		}
		else{
			return null;
		}
	}
	
	/* TODO */
	/**
	 * Method to parse wikipedia templates. These are *any* {{xyz}} tags
	 * For most cases, simply removing the tags should work.
	 * @param text: The text to be parsed
	 * @return The parsed text with the markup removed
	 */
	public static String parseTemplates(String text) {
		if(text!=null){
			return text.replaceAll("\\{\\{(.*?)\\}\\}", "");	
		}
		else{
			return null;
		}
	}
	
	
	/* TODO */
	/**
	 * Method to parse links and URLs.
	 * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Links_and_URLs
	 * @param text: The text to be parsed
	 * @return An array containing two elements as follows - 
	 *  The 0th element is the parsed text as visible to the user on the page
	 *  The 1st element is the link url
	 */
	public static String[] parseLinks(String text) {
		return null;
	}
	
	public static String textCleaning(StringBuffer textContent) {
		String content = textContent.toString();
		content = parseTagFormatting(content);
		//System.out.println("parseTagFormatting : " + content);
		//System.out.println("---------------------------------------------------------------------------------------------------------");
		content = parseTextFormatting(content);
		//System.out.println("parseTextFormatting : " + content);
		//System.out.println("---------------------------------------------------------------------------------------------------------");
		content = parseTemplates(content);
		//System.out.println("parseListItem : " + content);
		//System.out.println("---------------------------------------------------------------------------------------------------------");
		return content;
	}

	public static WikipediaDocument textParse(WikipediaDocument singleDoc, String docContent) {
		
		String secTitle = null;
		String secText = null;
		String[] link = null;
		Pattern pattern = Pattern.compile("(==+[A-Za-z0-9\\s\n]+==+)?([^=][^=]*[=]?[^=][^=]*)?");
		Matcher matcher = pattern.matcher(docContent);
		while(matcher.find()) {
			//System.out.println("Group :" + matcher.group()+":");
			//System.out.println(matcher.group() != null);
			
			//System.out.println(!(matcher.group().equalsIgnoreCase("")));
			if(matcher.group() != null && !matcher.group().isEmpty() ) {
				if(matcher.group(1) != null){
					secTitle = parseSectionTitle(matcher.group(1));
				} else {
					secTitle = "Default";
				}
				if(matcher.group(2) !=null) {
					secText = matcher.group(2);
					pattern = Pattern.compile("([[]{1,2}[A-Za-z0-9\\s\n]*[^[]*]{1,2})");
					matcher = pattern.matcher(secText);
					while(matcher.find()) {
						if(matcher.group(1)!=null) {
							link = parseLinks(matcher.group(1));
						}
					}
				} else {
					secText = null;
				}
				System.out.println("secTitle :"+ secTitle);
				System.out.println("secText :"+ secText);
				singleDoc.addSection(secTitle, secText);
			}
			
		}
		return singleDoc;
	}
}
