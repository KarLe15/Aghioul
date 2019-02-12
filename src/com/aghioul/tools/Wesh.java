package com.aghioul.tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.bson.Document;


/*représente un message (un wesh) en java tel que renvoyé en JSON*/

public class Wesh extends Document{
	
	private static final long serialVersionUID = 1L;
	static DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

	public Wesh(Date d, String t, boolean rewesh, int likes, int dislikes, int reweshes, Document user, String rewesh_from, Document rewesh_user, ArrayList<Document> comments){
		super();
		this.append("created_at", d);
		this.append("text", t);
		this.append("is_rewesh", rewesh);
		this.append("rewesh_from", rewesh); // mettre rewesh_from
		this.append("rewesh_user", rewesh_user);
		this.append("likes", likes);
		this.append("dislikes", dislikes);
		this.append("reweshes", reweshes);
		this.append("user", user);
		this.append("weshEntities", new WeshEntities(t));
		this.append("comments", comments);
	}
	
	
	public Wesh(String t, Document user) {
		this.append("created_at", new Date());
		this.append("text", t);
		this.append("is_rewesh", false);
		this.append("rewesh_from", null);
		this.append("rewesh_user", null);
		this.append("likes", 0);
		this.append("dislikes", 0);
		this.append("reweshes", 0);
		this.append("user", user);
		this.append("weshEntities", new WeshEntities(t));
		this.append("comments", new ArrayList<Document>());
	}
}
