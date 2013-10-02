/**
 * Test Commit from Eclipse. 
 */
package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import edu.buffalo.cse.ir.wikiindexer.FileUtil;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import org.apache.commons.lang.StringEscapeUtils;


import com.sun.xml.internal.ws.util.CompletedFuture;

/**
 * @author nikhillo This class implements Wikipedia markup processing. Wikipedia
 *         markup details are presented here:
 *         http://en.wikipedia.org/wiki/Help:Wiki_markup It is expected that all
 *         methods marked "todo" will be implemented by students. All methods
 *         are static as the class is not expected to maintain any state.
 */
public class WikipediaParser {
	/* TODO */
	/**
	 * Method to parse section titles or headings. Refer:
	 * http://en.wikipedia.org/wiki/Help:Wiki_markup#Sections
	 * 
	 * @param titleStr
	 *            : The string to be parsed
	 * @return The parsed string with the markup removed
	 */
	public static String parseSectionTitle(String titleStr) {
		try {
			if (titleStr != null) {
				titleStr = titleStr.replaceAll("=", "");
				titleStr = titleStr.replaceAll(
						"^\\s+|\\s+$|\\s*(\n)\\s*|(\\s)\\s*", "$1$2");
				titleStr = titleStr.trim();
				return titleStr;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/* TODO */
	/**
	 * Method to parse list items (ordered, unordered and definition lists).
	 * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Lists
	 * 
	 * @param itemText
	 *            : The string to be parsed
	 * @return The parsed string with markup removed
	 */
	public static String parseListItem(String itemText) {
		try{
		if (itemText != null) {
			if (itemText.startsWith("#")) {
				itemText = itemText.replaceAll("#", "");
			} else if (itemText.startsWith(";")) {
				itemText = itemText.replaceAll(";", "");
			} else if (itemText.startsWith("*")) {
				itemText = itemText.replaceAll("\\*", "");
			} else if (itemText.startsWith(":")) {
				itemText = itemText.replaceAll(":", "");
			}
			itemText = itemText.replaceAll(
					"^\\s+|\\s+$|\\s*(\n)\\s*|(\\s)\\s*", "$1$2");
			itemText = itemText.trim();
			// System.out.println("Returning------------"+itemText);
			return itemText;
		} else {
			return null;
		}} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/* TODO */
	/**
	 * Method to parse text formatting: bold and italics. Refer:
	 * http://en.wikipedia.org/wiki/Help:Wiki_markup#Text_formatting first point
	 * 
	 * @param text
	 *            : The text to be parsed
	 * @return The parsed text with the markup removed
	 */
	public static String parseTextFormatting(String text) {
		try{if (text != null) {
			text = text.replaceAll("'", "");
			text = text
					.replaceAll("^\\s+|\\s+$|\\s*(\n)\\s*|(\\s)\\s*", "$1$2");
			text = text.trim();
			return text;
		} else {
			return null;
		}} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/* TODO */
	/**
	 * Method to parse *any* HTML style tags like: <xyz ...> </xyz> For most
	 * cases, simply removing the tags should work.
	 * 
	 * @param text
	 *            : The text to be parsed
	 * @return The parsed text with the markup removed.
	 */
	public static String parseTagFormatting(String text) {
		try{if (text != null) {
			// System.out.println(text);
//			text = escapeHtml4(text);
//			Map<String, String> unescape = new HashMap<String, String>();
//			Properties props = new Properties();
//			try {
//				props = FileUtil.loadProperties("/tmp/unescape.config");
//			} catch (FileNotFoundException e) {
//				System.err.println("Unable to open or load the specified file: " + "contractions.config");
//			} catch (IOException e) {
//				System.err.println("Error while reading properties from the specified file: " + "contractions.config");
//			}
//			
//			for (String key : props.stringPropertyNames()) {
//			    String value = props.getProperty(key);
//			    unescape.put(key, value);
//			}
//			
//			for (Map.Entry<String, String> entry : unescape.entrySet()) {
//			    String key = entry.getKey();
//			    String value = entry.getValue();
//			    text = text.replaceAll(key, value);
//			    // ...
//			}
			text = text.replaceAll("<(.*?)>", "");
			text = text.replaceAll("&lt;(.*?)&gt;", "");
			text = text
					.replaceAll("^\\s+|\\s+$|\\s*(\n)\\s*|(\\s)\\s*", "$1$2");
			text = text.trim();
			return text;
		} else {
			return null;
		}} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/* TODO */
	/**
	 * Method to parse wikipedia templates. These are *any* {{xyz}} tags For
	 * most cases, simply removing the tags should work.
	 * 
	 * @param text
	 *            : The text to be parsed
	 * @return The parsed text with the markup removed
	 */
	public static String parseTemplates(String text) {
		int count = 0;
		try{
		if (text != null) {
			// return text.replaceAll("\\{\\{(.*?)\\}\\}", "");

			char completeText[] = text.toCharArray();
			String sourceString = "";
			for (int i = 0; i < completeText.length; i++) {
				if (completeText[i] == '{')
					if (i + 1 != completeText.length
							&& completeText[i + 1] == '{') {
						count++;
						if (count == 1) {
							sourceString = "";
						}
					}
				sourceString += completeText[i];
				if (completeText[i] == '}')
					if (i + 1 != completeText.length
							&& completeText[i + 1] == '}') {
						count--;

						if (count == 0) {
							sourceString += completeText[i + 1];
							// System.out.println("before replacing "+
							// sourceString + "\ntext:" +text);
							text = text.replace(sourceString, "");
							// System.out.println("entered for replacing "+
							// sourceString + "\ntext:" +text);
						}
					}
			}
			// System.out.println("text : "+ text );
			return text;
		} else {
			return null;
		}} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/* TODO */
	/**
	 * Method to parse links and URLs. Refer:
	 * http://en.wikipedia.org/wiki/Help:Wiki_markup#Links_and_URLs
	 * 
	 * @param text
	 *            : The text to be parsed
	 * @return An array containing two elements as follows - The 0th element is
	 *         the parsed text as visible to the user on the page The 1st
	 *         element is the link url
	 */
	public static String[] parseLinks(String text) {

		ArrayList<String> result_list = new ArrayList<String>();
		// System.out.println("called with "+text);
		String matched_text;
		String[] result = new String[] { "", "" };
		if (text == null || text == "") {
			return result;
		} else {
			try {
				Pattern wikilink_pattern = Pattern
						.compile("(\\[\\[(.*)+\\]\\])");
				Matcher matcher = wikilink_pattern.matcher(text);
				if (matcher.find()) {
					matched_text = matcher.group(0).replaceAll("\\[", "");
					matched_text = matched_text.replaceAll("\\]", "");

					String temp = new String();
					if (matcher.group(0).contains("|")) {
						result = matched_text.split("\\|");
						if (result.length <= 1) {
							result[0] = result[0].replaceAll("\\|", "");
							if (result[0].contains(":")
									&& !result[0].contains("#")) {// Outside
																	// Namespace
								temp = result[0];
								result[0] = temp.replaceAll(temp.split(":")[0]
										+ ":", "");
								result[0] = result[0].split("\\(")[0];
								temp = "";
							}
							if (result[0].contains(",")) {// [[Seattle,
															// Washington|]]
								temp = result[0];
								result[0] = result[0].split(",")[0];
							}
							if (result[0].contains("(")) {// [[kingdom
															// (biology)|]]
								temp = result[0];
								result[0] = result[0].split("\\(")[0];
							}
							result_list.add(result[0]);
							result_list.add(temp);
						} else if (result.length == 2) {
							temp = result[0];
							result[0] = result[1];
							result[1] = temp;
							if (result[1].contains(":")) {// Outside Namespace
								result[1] = "";
							}
							result_list.add(result[0]);
							result_list.add(result[1]);
						} else {
							result_list.add(result[result.length - 1]);
							result_list.add("");
						}
					} else { // All simple links without |
						if (matcher.group(0).contains(":Category:")) {
							matched_text = matched_text.replaceAll(
									":Category:", "Category:");
							result_list.add(matched_text);
						} else if (matcher.group(0).contains("Category")
								|| matcher.group(0).contains("File")) {
							result_list.add("");
						} else {
							result_list.add(matched_text);// First Element=What
															// the user sees
						}
						if (matcher.group(0).contains(":")) {// Outside
																// Namespace
							result_list.add("");
						} else {
							result_list.add(matched_text);
						}
					}
					text = text.replace(matcher.group(0), result_list.get(0));

					// System.out.println("not null*"+result[0]+"*"+result[1]+"*");
				} else {
					Pattern ext_pattern = Pattern.compile("(\\[(.*)+\\])");
					Matcher ext_matcher = ext_pattern.matcher(text);
					if (ext_matcher.find()) {
						String ext_matched_text;
						ext_matched_text = ext_matcher.group(0).replaceAll(
								"\\[", "");
						ext_matched_text = ext_matched_text.replaceAll("\\]",
								"");
						if (ext_matched_text.contains(" ")) {
							String t;
							t = ext_matched_text;
							if(t.split("\\s").length>1){
								result_list.add(t.split("\\s")[1]);
							} else {
								result_list.add("");
							}
							result_list.add("");

						} else {
							result_list.add("");
							result_list.add("");
						}
						text = text.replace(ext_matcher.group(0),
								result_list.get(0));

					} else {
						throw new Exception("Doesn't contain a link");
					}
				}
				if (!result_list.get(0).isEmpty()) {
					result_list.set(0, result_list.get(0).trim());
					result_list.set(
							0,
							result_list.get(0).replaceAll(
									"^\\s+|\\s+$|\\s*(\n)\\s*|(\\s)\\s*",
									"$1$2"));
				}
				if (!result_list.get(1).isEmpty()) {
					result_list.set(1,
							Character.toUpperCase(result_list.get(1).charAt(0))
									+ result_list.get(1).substring(1));
					result_list.set(1, result_list.get(1)
							.replaceAll("\\s", "_"));
					result_list.set(1, parseTagFormatting(result_list.get(1)));
				}
				result_list.set(0, text);
				result_list.set(0, parseTagFormatting(result_list.get(0)));
				String[] resultStr = new String[result_list.size()];
				resultStr = result_list.toArray(resultStr);
				// System.out.println("Returning "+resultStr[0]+" and "+resultStr[1]);
				return resultStr;
				// return result;
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return result;
	}

	public static String textCleaning(StringBuffer textContent) {
		String content = textContent.toString();
		content = parseTagFormatting(content);
		// System.out.println("parseTagFormatting : " + content);
		// System.out.println("---------------------------------------------------------------------------------------------------------");
		content = parseTextFormatting(content);
		// content = content.replaceAll("\n", "");
		// System.out.println("parseTextFormatting : " + content);
		// System.out.println("---------------------------------------------------------------------------------------------------------");

		content = parseTemplates(content);
		// System.out.println("parseListItem : " + content);
		// System.out.println("---------------------------------------------------------------------------------------------------------");
		return content;
	}

	public static WikipediaDocument textParse(WikipediaDocument singleDoc,
			String docContent) {

		String secTitle = null;
		String secText = null;
		String[] link = null;
		ArrayList<String> linkColl = new ArrayList<String>();
		ArrayList<String> categoryColl = new ArrayList<String>();
		try {
			Pattern pattern = Pattern
					.compile("(==+[A-Za-z0-9\\s\n]+==+)?([^=][^=]*[=]?[^=][^=]*)?");
			Matcher matcher = pattern.matcher(docContent);
			while (matcher.find()) {
				// System.out.println("Group :" + matcher.group()+":");
				// System.out.println(matcher.group() != null);

				// System.out.println(!(matcher.group().equalsIgnoreCase("")));
				if (matcher.group() != null && !matcher.group().isEmpty()) {
					if (matcher.group(1) != null) {
						secTitle = parseSectionTitle(matcher.group(1));
					} else {
						secTitle = "Default";
					}
					if (matcher.group(2) != null) {
						secText = matcher.group(2);
						Pattern linkPattern = Pattern
								.compile("([\\[]{1,2}[A-Za-z0-9\\s\n]*[^\\[]*]{1,2})");
						Matcher linkMatcher = linkPattern.matcher(secText);
						while (linkMatcher.find()) {
							if (linkMatcher.group(1) != null) {
								// System.out.println(linkMatcher.group(1));
								link = parseLinks(linkMatcher.group(1));
								if (link[1] != null
										&& !link[1].equalsIgnoreCase(""))
									linkColl.add(link[1]);
								// System.out.println(linkMatcher.group(1) +"\n"
								// + link[0]);
								secText = secText.replace(linkMatcher.group(1),
										link[0]);
								if (link.length > 2
										&& link[2].equalsIgnoreCase("Category")) {
									if (link[2] != null
											&& !link[2].equalsIgnoreCase(""))
										categoryColl.add(link[0]);
								}
							}
						}
					} else {
						secText = null;
					}
					// System.out.println("secTitle :" + secTitle);
					// System.out.println("secText :" + secText);
					singleDoc.addSection(secTitle, secText);

				}
			}
			singleDoc.addLInks(linkColl);
			// System.out.println("linkColl  : " + linkColl);
			singleDoc.addCategories(categoryColl);
			// System.out.println("categoryColl  : " + categoryColl);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return singleDoc;
	}
}
