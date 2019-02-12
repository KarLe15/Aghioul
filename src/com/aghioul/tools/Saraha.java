package com.aghioul.tools;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

import org.bson.Document;

public class Saraha extends Document {
	public static final int DAY = 0;
	public static final int WEEK = 1;
	public static final int NEVER = 2;
	
	private static final long serialVersionUID = 1L;
	
	public Saraha(String title, Document user, int expire, ArrayList<String> questions) {
		super();
		this.append("title", title);
		this.append("user", user);
		this.append("date", new Date());
		if(expire == DAY)
			this.append("expire_date", LocalDateTime.now().plusDays(1));
		if(expire == WEEK)
			this.append("expire_date", LocalDateTime.now().plusWeeks(1));
		if(expire == NEVER)
			this.append("expire_date", null);
		ArrayList<Document> q = new ArrayList<Document>();
		for(String s : questions) {
			q.add(new Document("question", s).append("votes", 0));
		}
		this.append("questions", q);
	}

}
