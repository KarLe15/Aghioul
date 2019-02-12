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
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class Comments {
	
	/*
	 * renvoie un Json contenant tous les commentaires d'un message donné
	 * 
	 */
	public static Document getComments(String key, String messageId) {
		Connection c = null;
		try {
			c = Database.getMySQLConnection();
		} catch (SQLException e){
		    e.printStackTrace();
		    return ErrorAghioul.serviceRefused("SQL database error", ErrorAghioul.SQL_ERROR);
		}
		//récupérer l'utilisateur
		Document u = DBtools.getUser(c, key);
		if(u == null || u.isEmpty()){
			return ErrorAghioul.serviceRefused("authentification required", ErrorAghioul.SERVICE_ERROR);
		}
		if(!DBtools.verifyTimeOut(c, key)) {
			DBtools.logout(c, key);
			return ErrorAghioul.serviceRefused("session expired", ErrorAghioul.SERVICE_ERROR);
		}
		
		try {
			c.close();
		} catch(SQLException e) {}
		
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		MongoDatabase database = mongoClient.getDatabase("aghioul");
		MongoCollection<Document> collection = database.getCollection("messages");	
		
		FindIterable<Document> iterable = collection.find(new Document("_id", new ObjectId(messageId)));
		MongoCursor<Document> cursor = iterable.iterator();
		
		mongoClient.close();
		if (cursor.hasNext()) {
			//ajouter les messages
			return ErrorAghioul.serviceAccepted().append("comments", (ArrayList<Document>) cursor.next().get("comments"));
		}
		else{
			return ErrorAghioul.serviceRefused("messageId inexisting", ErrorAghioul.SERVICE_ERROR);
		}
	}
	
	
	/*
	 * ajoute un commentaire à un message donné.
	 * 
	 */
	public static Document addComment(String key, String messageId, String text) {
		Document o = ErrorAghioul.serviceAccepted();
		
		Connection c = null;
		try {
			c = Database.getMySQLConnection();
		} catch (SQLException e){
		    e.printStackTrace();
		    return ErrorAghioul.serviceRefused("SQL database error", ErrorAghioul.SQL_ERROR);
		}
		//récupérer l'utilisateur
		Document u = DBtools.getUser(c, key);
		if(u == null || u.isEmpty()){
			return ErrorAghioul.serviceRefused("authentification required", ErrorAghioul.SERVICE_ERROR);
		}
		if(!DBtools.verifyTimeOut(c, key)) {
			DBtools.logout(c, key);
			return ErrorAghioul.serviceRefused("session expired", ErrorAghioul.SERVICE_ERROR);
		}
		
		try {
			c.close();
		} catch(SQLException e) {}
		
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		MongoDatabase database = mongoClient.getDatabase("aghioul");
		MongoCollection<Document> collection = database.getCollection("messages");	
		
		Document comment = new Document("created_at", new Date()).append("text", text).append("user", u);
		collection.updateOne(new Document("_id", new ObjectId(messageId)), new Document("$addToSet", new Document("comments", comment)));
		mongoClient.close();
		
		return o.append("comment", comment); 
	}
}
