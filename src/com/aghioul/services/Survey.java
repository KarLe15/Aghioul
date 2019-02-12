package com.aghioul.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.aghioul.DB.DBtools;
import com.aghioul.DB.Database;
import com.aghioul.tools.ErrorAghioul;
import com.aghioul.tools.Saraha;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class Survey {
	
	
	public static Document createSurvey(String key, String title, int expire_date, ArrayList<String> questions) {
		
		Connection c = null;
		try {
			c = Database.getMySQLConnection();
		} catch (SQLException e){
		    e.printStackTrace();
		    return ErrorAghioul.serviceRefused("SQL database error", ErrorAghioul.SQL_ERROR);
		}
		//récupérer l'utilisateur
		Document u = DBtools.getUser(c, key);
		//si l'utilisateur n'est pas authentifié
		if(u == null || u.isEmpty()){
			return ErrorAghioul.serviceRefused("authentification required", ErrorAghioul.SERVICE_ERROR);
		}
		if(!DBtools.verifyTimeOut(c, key)) {
			DBtools.logout(c, key);
			return ErrorAghioul.serviceRefused("session expired", ErrorAghioul.SERVICE_ERROR);
		}
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		MongoDatabase database = mongoClient.getDatabase("aghioul");
		
		MongoCollection<Document> collection = database.getCollection("surveys");
		Saraha saraha = new Saraha(title, u, Saraha.NEVER, questions);
		collection.insertOne(saraha);
		mongoClient.close();
		try {
			c.close();
		} catch (SQLException e) {}
		
		return ErrorAghioul.serviceAccepted().append("survey", saraha);
	}

	public static Document getSurveys(String key) {
		Connection c = null;
		try {
			c = Database.getMySQLConnection();
		} catch (SQLException e){
		    e.printStackTrace();
		    return ErrorAghioul.serviceRefused("SQL database error", ErrorAghioul.SQL_ERROR);
		}
		//récupérer l'utilisateur
		Document u = DBtools.getUser(c, key);
		//si l'utilisateur n'est pas authentifié
		if(u == null || u.isEmpty()){
			return ErrorAghioul.serviceRefused("authentification required", ErrorAghioul.SERVICE_ERROR);
		}
		if(!DBtools.verifyTimeOut(c, key)) {
			DBtools.logout(c, key);
			return ErrorAghioul.serviceRefused("session expired", ErrorAghioul.SERVICE_ERROR);
		}
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		MongoDatabase database = mongoClient.getDatabase("aghioul");
		
		MongoCollection<Document> collection = database.getCollection("surveys");
		ArrayList<Document> surveys = new ArrayList<Document>();
		
		Document mask = new Document();
		mask.append("date",new Document("$lte", new Date()));

		FindIterable<Document> iterable = collection.find(mask).limit(15).sort(new Document("created_at",-1));
		MongoCursor<Document> cursor = iterable.iterator();

		while (cursor.hasNext()) {
			//ajouter les messages
			surveys.add(cursor.next());
		}
		
		mongoClient.close();
		try {
			c.close();
		} catch (SQLException e) {}
		
		return ErrorAghioul.serviceAccepted().append("surveys", surveys);
	}
	
	public static Document vote(String key, String surveyId, int question) {
		Connection c = null;
		try {
			c = Database.getMySQLConnection();
		} catch (SQLException e){
		    e.printStackTrace();
		    return ErrorAghioul.serviceRefused("SQL database error", ErrorAghioul.SQL_ERROR);
		}
		//récupérer l'utilisateur
		Document u = DBtools.getUser(c, key);
		//si l'utilisateur n'est pas authentifié
		if(u == null || u.isEmpty()){
			return ErrorAghioul.serviceRefused("authentification required", ErrorAghioul.SERVICE_ERROR);
		}
		if(!DBtools.verifyTimeOut(c, key)) {
			DBtools.logout(c, key);
			return ErrorAghioul.serviceRefused("session expired", ErrorAghioul.SERVICE_ERROR);
		}
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		MongoDatabase database = mongoClient.getDatabase("aghioul");
		
		MongoCollection<Document> collection = database.getCollection("surveys");
		Document newDocument = new Document("$inc", new Document("questions."+question+".votes", 1));
		
		collection.updateOne(new Document("_id", new ObjectId(surveyId)), newDocument);
		mongoClient.close();
		return ErrorAghioul.serviceAccepted();
	}
}
