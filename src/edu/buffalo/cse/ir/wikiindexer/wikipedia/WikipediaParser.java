/**
 * Test Commit from Eclipse. 
 */
package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author nikhillo This class implements Wikipedia markup processing. Wikipedia
 *         markup details are presented here: http: methods marked "todo" will
 *         be implemented by students. All methods are static as the class is
 *         not expected to maintain any state.
 */
public class WikipediaParser {

	/**
	 * Method to parse section titles or headings. Refer: http:
	 * 
	 * @param titleStr
	 *            : The string to be parsed
	 * @return The parsed string with the markup removed
	 */
	private static ArrayList<String> categoryList;

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

	/**
	 * Method to parse list items (ordered, unordered and definition lists).
	 * Refer: http:
	 * 
	 * @param itemText
	 *            : The string to be parsed
	 * @return The parsed string with markup removed
	 */
	public static String parseListItem(String itemText) {
		try {
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

				return itemText;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Method to parse text formatting: bold and italics. Refer: http:
	 * 
	 * @param text
	 *            : The text to be parsed
	 * @return The parsed text with the markup removed
	 */
	public static String parseTextFormatting(String text) {
		try {
			if (text != null) {
				text = text.replaceAll("'", "");
				text = text.replaceAll("^\\s+|\\s+$|\\s*(\n)\\s*|(\\s)\\s*",
						"$1$2");
				text = text.trim();
				return text;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Method to parse *any* HTML style tags like: <xyz ...> </xyz> For most
	 * cases, simply removing the tags should work.
	 * 
	 * @param text
	 *            : The text to be parsed
	 * @return The parsed text with the markup removed.
	 */
	public static String parseTagFormatting(String text) {
		try {
			if (text != null) {

				text = text.replaceAll("<(.*?)>", "");
				text = text.replaceAll("&lt;(.*?)&gt;", "");
				text = text.replaceAll("^\\s+|\\s+$|\\s*(\n)\\s*|(\\s)\\s*",
						"$1$2");
				text = text.trim();
				return text;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

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
		try {
			if (text != null) {

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
					if (completeText[i] == '}' && count > 0)
						if (i + 1 != completeText.length
								&& completeText[i + 1] == '}') {
							count--;

							if (count == 0) {
								sourceString += completeText[i + 1];

								text = text.replace(sourceString, "");

							}
						}
				}

				return text;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Method to parse links and URLs. Refer: http:
	 * 
	 * @param text
	 *            : The text to be parsed
	 * @return An array containing two elements as follows - The 0th element is
	 *         the parsed text as visible to the user on the page The 1st
	 *         element is the link url
	 */
	public static String[] parseLinks(String text) {

		ArrayList<String> result_list = new ArrayList<String>();

		if (categoryList == null) {
			categoryList = new ArrayList<String>();
		}
		String matched_text;
		String[] result = new String[] { "", "" };
		if (text == null || text == "") {
			return result;
		} else {
			try {
				text = text.replaceAll("\n", "");
				Pattern wikilink_pattern = Pattern
						.compile("(\\[\\[(.*)+\\]\\])");
				Matcher matcher = wikilink_pattern.matcher(text);
				if (matcher.find()) {
					matched_text = matcher.group(0).replaceAll("\\[", "");
					matched_text = matched_text.replaceAll("\\]", "");

					String temp = new String();
					if (matcher.group(0).contains("|")) {
						matched_text = matched_text.trim();
						result = matched_text.split("\\|");
						if (result.length <= 1) {
							result[0] = result[0].replaceAll("\\|", "");
							if (result[0].contains(":")
									&& !result[0].contains("#")) {

								if (result[0].contains("Category:")) {
									categoryList.add(result[0]
											.split("Category:")[1]);
								}
								temp = result[0];
								result[0] = temp.replaceAll(temp.split(":")[0]
										+ ":", "");
								result[0] = result[0].split("\\(")[0];
								temp = "";
							}
							if (result[0].contains(",")) {

								temp = result[0];
								result[0] = result[0].split(",")[0];
							}
							if (result[0].contains("(")) {

								temp = result[0];
								result[0] = result[0].split("\\(")[0];
							}
							result_list.add(result[0]);
							result_list.add(temp);
						} else if (result.length == 2) {
							temp = result[0];
							result[0] = result[1];
							result[1] = temp;
							if (result[1].contains(":")) {
								result[1] = "";
							}
							result_list.add(result[0]);
							result_list.add(result[1]);
						} else {
							result_list.add(result[result.length - 1]);
							result_list.add("");
						}
					} else {
						if (matcher.group(0).contains(":Category:")) {
							matched_text = matched_text.replaceAll(
									":Category:", "Category:");
							result_list.add(matched_text);
						} else if (matcher.group(0).contains("Category:")) {
							result_list.add("");

							categoryList.add(matched_text.split("gory:")[1]);

						} else if (matcher.group(0).contains("File:")) {
							result_list.add("");

						} else {
							result_list.add(matched_text);

						}
						if (matcher.group(0).contains(":")) {

							result_list.add("");
						} else {
							result_list.add(matched_text);
						}
					}
					text = text.replace(matcher.group(0), result_list.get(0));

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
							if (t.split("\\s").length > 1) {
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

				return resultStr;

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return result;
	}

	public static String textCleaning(StringBuffer textContent) {
		String content = textContent.toString();
		long startTime = System.currentTimeMillis();

		content = parseTagFormatting(content);

		content = parseTextFormatting(content);

		content = parseTemplates(content);

		long endTime = System.currentTimeMillis();

		return content;
	}

	public static WikipediaDocument textParse(WikipediaDocument singleDoc,
			String docContent) {

		String secTitle = null;
		String secText = "";
		String[] parsedLink = null;
		Boolean isFirstGroup = true;
		ArrayList<String> allLinks;
		ArrayList<String> linkColl = new ArrayList<String>();
		ArrayList<String> categoryColl = new ArrayList<String>();
		Map<String, String> mapSec = new LinkedHashMap<String, String>();
		try {

			Pattern pattern = Pattern
					.compile("(==+[A-Za-z0-9\\s\n]*==+)?(.*\n*[^==]*)");
			Matcher matcher = pattern.matcher(docContent);
			while (matcher.find()) {

				if (matcher.group() != null && !matcher.group().isEmpty()) {
					if (matcher.group(2) != null) {
						if (matcher.group(1) == null)
							secText += matcher.group(2);
						else
							secText = matcher.group(2);

					} else {
						secText = null;
					}

					if (matcher.group(1) != null) {
						secTitle = parseSectionTitle(matcher.group(1));
					} else {
						if (isFirstGroup) {
							secTitle = "Default";
							isFirstGroup = false;
						}

					}

					mapSec.put(secTitle, secText);

				}

			}

			if (mapSec != null) {
				for (String mapSecTitle : mapSec.keySet()) {
					if (mapSecTitle != null) {

						String mapSecText = mapSec.get(mapSecTitle);
						allLinks = callLink(mapSecText);
						for (String link : allLinks) {
							long startTime = System.currentTimeMillis();

							parsedLink = parseLinks(link);
							long endTime = System.currentTimeMillis();

							if (parsedLink[1] != null
									&& !parsedLink[1].equalsIgnoreCase("")) {
								linkColl.add(parsedLink[1]);

							}

							if (parsedLink[0] != null) {
								mapSecText = mapSecText.replace(link,
										parsedLink[0]);

							}

						}

						singleDoc.addSection(mapSecTitle, mapSecText);

					}
				}
			}

			singleDoc.addLInks(linkColl);

			categoryColl = categoryList;
			categoryList = null;
			singleDoc.addCategories(categoryColl);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return singleDoc;
	}

	private static ArrayList<String> callLink(String secText) {
		int count1 = 0, count2 = 0;
		ArrayList<String> linkText = new ArrayList<String>();

		try {
			if (secText != null) {

				char completeText[] = secText.toCharArray();
				String sourceString = "";
				Boolean changeIndex = false;
				for (int i = 0; i < completeText.length; i++) {
					if (completeText[i] == '[') {

						count1++;
						if (count1 == 1) {
							sourceString = "";
						}

						if (i + 1 != completeText.length
								&& completeText[i + 1] == '[') {

							count2++;

							sourceString += completeText[i];
							changeIndex = true;
						}

					}
					if (count1 != 0) {
						sourceString += completeText[i];
					}

					if (completeText[i] == ']' && count1 > 0) {
						count1--;

						if (i + 1 != completeText.length
								&& completeText[i + 1] == ']' && count2 > 0) {
							count2--;

							sourceString += completeText[i];
							changeIndex = true;
						}
					}
					if (count1 == 0 && count2 == 0) {

						if (!linkText.contains(sourceString)
								&& sourceString != "") {

							Stack<Character> charStack = new Stack<Character>();
							String individualLink = null;
							ArrayList<String> links = new ArrayList<String>();

							Character c = null, d = null;

							String test = sourceString;
							boolean isNextChar = false;

							for (int iter2 = 0; iter2 < test.length(); iter2++) {
								c = test.charAt(iter2);

								if (c != ']') {

									charStack.push(c);
								} else {

									d = charStack.pop();

									while (d != '[') {

										if (individualLink == null) {
											individualLink = new String();
											individualLink = new StringBuilder(
													individualLink)
													.insert(0, d).toString();

										} else {
											individualLink = new StringBuilder(
													individualLink)
													.insert(0, d).toString();

										}
										d = charStack.pop();
									}
									if (test.length() - 1 != iter2) {
										Character nextChar = test
												.charAt(iter2 + 1);
										if (nextChar == ']') {
											charStack.pop();
											iter2 = iter2 + 1;
											isNextChar = true;
										}
									}

									if (isNextChar == true) {
										links.add("[[" + individualLink + "]]");
									} else {
										links.add("[" + individualLink + "]");
									}
									individualLink = null;

								}

							}

							if (links.size() > 1) {

								for (int iter1 = 0; iter1 < links.size() - 1; iter1++) {

									linkText.add(links.get(iter1));
									String[] internal_parse_result = parseLinks(links
											.get(iter1));
									test = test.replace(links.get(iter1),
											internal_parse_result[0]);
								}

								linkText.add(test);
							} else {

								linkText.add(test);
							}

						}

					}

					if (changeIndex) {
						i = i + 1;
						changeIndex = false;
					}

				}

				return linkText;

			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}
