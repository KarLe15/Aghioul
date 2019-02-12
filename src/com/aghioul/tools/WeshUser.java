package com.aghioul.tools;

import org.bson.Document;

import com.google.gson.JsonObject;

public class WeshUser extends Document {
	
	public WeshUser(String id, String username, String name, String sex) {
		super();
		append("id", id);
		append("username", username);
		append("name", name);
		append("sex", sex);
	}
}
