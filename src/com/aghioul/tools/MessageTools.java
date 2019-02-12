package com.aghioul.tools;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MessageTools {

 	public static final Pattern PATTERN_HASHTAG = Pattern.compile("\\B#\\w*[a-zA-Z]+\\w*");
	public static final Pattern PATTERN_MENTION = Pattern.compile("\\B@\\w*[a-zA-Z]+\\w*");



	/**
	 *
	 * @param text
	 * @return
	 */
	public static LinkedList<String> getHashtags(String text) {
		LinkedList<String> hashtags = new LinkedList<String>();
		
		Matcher m = PATTERN_HASHTAG.matcher(text);
		while (m.find()) {
		    hashtags.add(m.group());
		}
		
		return hashtags;	
	}


	/**
	 *
	 * @param text
	 * @return
	 */
	public static LinkedList<String> getMentions(String text) {
		LinkedList<String> mentions = new LinkedList<String>();
		Matcher m = PATTERN_MENTION.matcher(text);
		while (m.find()) {
		    mentions.add(m.group());
		}
		
		return mentions;	
	}
}
