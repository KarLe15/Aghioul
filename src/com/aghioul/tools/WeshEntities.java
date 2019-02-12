package com.aghioul.tools;

import java.util.LinkedList;
import java.util.List;

import org.bson.Document;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class WeshEntities extends Document {

	public WeshEntities() {
		super();
	}
	
	public WeshEntities(String text) {
		super();
		LinkedList<String> hashtags = MessageTools.getHashtags(text);
		LinkedList<String> mentions = MessageTools.getMentions(text);
		this.put("hashtags", hashtags);
		this.put("mentions", mentions);
	}
}

