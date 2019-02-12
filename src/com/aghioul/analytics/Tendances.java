package com.aghioul.analytics;

import java.util.ArrayList;

import org.bson.Document;

import com.aghioul.tools.ErrorAghioul;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.MapReduceAction;

public class Tendances {
	
	private static String hashtagMap = "function(){" + 
			"for(let i in this.weshEntities.hashtags){" + 
			"emit(this.weshEntities.hashtags[i], 1);" + 
			"}" + 
			"}";
	
	private static String mentionMap = "function(){" + 
			"for(let i in this.weshEntities.mentions){" + 
			"emit(this.weshEntities.mentions[i], 1);" + 
			"}" + 
			"}";
	
	private static String reduce = "function(key, values){" + 
			"return values.length;" + 
			"}";
	
	/*
	 * renvoie les 10 hashtags les plus populaires et les 10 utilisateurs les plus populaires
	 * si une date est précisée, comptabilise la popularité de cette date à maintenant.
	 */

	public static void main(String[] args) {
		System.out.println(getTendances().toJson());
	}
	public static Document getTendances() {

		
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		MongoDatabase database = mongoClient.getDatabase("aghioul");
		
		/*on applique map-reduce*/
		database.getCollection("messages").mapReduce(hashtagMap, reduce).collectionName("hashtagIndex").action(MapReduceAction.REPLACE);
		database.getCollection("messages").mapReduce(mentionMap, reduce).collectionName("mentionIndex").action(MapReduceAction.REPLACE);
		
		ArrayList<Document> hashtags = new ArrayList<Document>();
		ArrayList<Document> mentions = new ArrayList<Document>();
		
		/*on va recuperer les 10 hashtags les plus cités*/
		MongoCollection<Document> collection = database.getCollection("hashtagIndex");
		
		FindIterable<Document> iterable = collection.find(new Document()).sort(new Document("value", -1)).limit(10);
		MongoCursor<Document> cursor = iterable.iterator();
		
		while(cursor.hasNext()) {
			hashtags.add(cursor.next());
		}
		
		/*on va recuperer les 10 utilisateurs les plus cités*/
		collection = database.getCollection("mentionIndex");
		
		iterable = collection.find(new Document()).sort(new Document("value", -1)).limit(10);
		cursor = iterable.iterator();
		
		while(cursor.hasNext()) {
			mentions.add(cursor.next());
		}
		
		mongoClient.close();
		
		return ErrorAghioul.serviceAccepted().append("hashtags", hashtags).append("mentions", mentions);
	}
}
