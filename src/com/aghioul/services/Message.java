package com.aghioul.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.aghioul.DB.DBtools;
import com.aghioul.DB.Database;
import com.aghioul.tools.ErrorAghioul;
import com.aghioul.tools.Wesh;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class Message {


    /**
     *
     * @param key
     * @param objectId
     * @return
     */
	public static Document like(String key, String objectId) {
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		MongoDatabase database = mongoClient.getDatabase("aghioul");


		Document newDocument = new Document("$inc", new Document("likes", 1));
		MongoCollection<Document> collection = database.getCollection("messages");	
		collection.updateOne(new Document("_id", new ObjectId(objectId)), newDocument);
		mongoClient.close();
		return ErrorAghioul.serviceAccepted();
	}


    /**
     *
     * @param key
     * @param objectId
     * @return
     */
	public static Document dislike(String key, String objectId) {

        // vérifier la connexion de l'utilisateur

	    MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		MongoDatabase database = mongoClient.getDatabase("aghioul");
		Document newDocument = new Document("$inc", new Document("dislikes", 1));
		MongoCollection<Document> collection = database.getCollection("messages");	
		collection.updateOne(new Document("_id", new ObjectId(objectId)), newDocument);
		mongoClient.close();
		return ErrorAghioul.serviceAccepted();
	}

    /**
     *
     * @param key
     * @param messageId
     * @return
     */
	public static Document share(String key, String messageId) {
		/*partager un wesh sur le réseau*/
		
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
		try {
			c.close();
		} catch(SQLException e) {}


		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		MongoDatabase database = mongoClient.getDatabase("aghioul");
		/*incrementer le nombre de rewesh du wesh partagé*/
		Document newDocument = new Document("$inc", new Document("reweshes", 1));
		MongoCollection<Document> collection = database.getCollection("messages");	
		
		FindIterable<Document> iterable = collection.find(new Document("_id", new ObjectId(messageId)));
		MongoCursor<Document> cursor = iterable.iterator();
		if(cursor.hasNext()) {
			//ajouter les messages
			Document d = cursor.next();
			@SuppressWarnings("unchecked")
			Document wesh = new Wesh(new Date(), d.getString("text"), true, d.getInteger("likes"), d.getInteger("dislikes"), d.getInteger("reweshes")+1, u, messageId, (Document) d.get("user"), (ArrayList<Document>)d.get("comments"));
			collection.insertOne(wesh);
			collection.updateOne(new Document("_id", new ObjectId(messageId)), newDocument);
			
			mongoClient.close();
			Document o = ErrorAghioul.serviceAccepted();
			o.append("wesh", wesh);
			return o;
		}
		mongoClient.close();
		return ErrorAghioul.serviceRefused("", ErrorAghioul.SERVICE_ERROR);
		
		
	}
	/**
	 *
	 * @param key
	 * @param text
	 * @return
	 */
	public static Document publish(String key, String text) {
		/*publier un wesh sur le réseau*/
		
		//verifier la longueur du texte
		if(text.length() == 0 || text.length() > 340)
			return ErrorAghioul.serviceRefused("messageLengthError", ErrorAghioul.SERVICE_ERROR);
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
		//creer le wesh à la date courante
		Document message = new Wesh(text, u);
		//insérer le wesh
		MongoCollection<Document> collection = database.getCollection("messages");
		
		collection.insertOne(message);
		mongoClient.close();
		try {
			c.close();
		} catch (SQLException e) {}
		
		return new Document("wesh", message); 
	}
	
	public static Document getTimeline(String key, Date anteriorTo, Date posteriorTo) {
		LinkedList<Document> messages = new LinkedList<Document>();
		
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
		MongoCollection<Document> collection = database.getCollection("messages");
		//recherche et ajout des messages ici...
		Document mask = new Document();
		if(anteriorTo != null)
			mask.append("created_at",new Document("$lte", anteriorTo)); //si pas spécifié on renvoie juste les derniers messages
		else if(posteriorTo != null)
			mask.append("created_at",new Document("$gt", posteriorTo)); //
		else
			mask.append("created_at",new Document("$lte", new Date())); //si pas spécifié on renvoie juste les derniers messages
		
		
		FindIterable<Document> iterable = collection.find(mask).limit(15).sort(new Document("created_at",-1));
		MongoCursor<Document> cursor = iterable.iterator();

		while (cursor.hasNext()) {
			//ajouter les messages
			messages.add(cursor.next());
		}
		try {
			c.close();
		} catch (SQLException e) {}
		mongoClient.close();
		
		return new Document("weshs", messages);
	}

	
	public static Document getByHashtag(String key, String hashtag) {
		LinkedList<Document> messages = new LinkedList<Document>();
		
		
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
			try {
				c.close();
			} catch(SQLException e) {}
			return ErrorAghioul.serviceRefused("authentification required", ErrorAghioul.SERVICE_ERROR);
		}
		if(!DBtools.verifyTimeOut(c, key)) {
			DBtools.logout(c, key);
			try {
				c.close();
			} catch(SQLException e) {}
			return ErrorAghioul.serviceRefused("session expired", ErrorAghioul.SERVICE_ERROR);
		}
		//recherche et ajout des messages ici...
		try {
			c.close();
		} catch(SQLException e) {}
		return new Document("weshs", messages);
	}

	public static Document getByUser(String key, String username) {
		
		Document res = new Document();
		LinkedList<Document> messages = new LinkedList<Document>();
		
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
		MongoCollection<Document> collection = database.getCollection("messages");
		//masque de requête
		Document mask = new Document("user.username", username);
		
		//recherche et ajout des messages ici...
		FindIterable<Document> iterable = collection.find(mask);
		MongoCursor<Document> cursor = iterable.iterator();
				
		while (cursor.hasNext()) {
			//ajouter les messages
			messages.add(cursor.next());
		}
		u = DBtools.getUserFromUsers(c, username);
		try {
			c.close();
		} catch (SQLException e) {}
		mongoClient.close();
		res.append("user", u);
		res.append("weshs", messages);
		
		return res;
	}

}
