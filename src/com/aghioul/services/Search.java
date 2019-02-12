package com.aghioul.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;


import com.aghioul.DB.DBtools;
import com.mongodb.*;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.aghioul.DB.Database;
import com.aghioul.tools.ErrorAghioul;

import com.mongodb.client.model.MapReduceAction;


public class Search {


	public static void main(String[] args) {
		System.out.println(search("dkkSA5gTH7K92q3mjNhmrAM7jSf7EgbS", "salut").toJson());
	}

	public static Document searchUser(String key, String toSearch){
		Connection c = null;
		try {
			c = Database.getMySQLConnection();
		} catch (SQLException e){
			e.printStackTrace();
			return ErrorAghioul.serviceRefused("SQL database error", ErrorAghioul.SQL_ERROR);
		}
		Document u = DBtools.getUser(c, key);
		//si l'utilisateur n'est pas authentifié
		if(u == null || u.isEmpty()){
			return ErrorAghioul.serviceRefused("authentification required", ErrorAghioul.SERVICE_ERROR);
		}
		if(!DBtools.verifyTimeOut(c, key)) {
			DBtools.logout(c, key);
			return ErrorAghioul.serviceRefused("session expired", ErrorAghioul.SERVICE_ERROR);
		}
		Document user = DBtools.getUserFromUsers(c,toSearch);
		if(user.getString("username") == null){
			return ErrorAghioul.serviceRefused("user not found",ErrorAghioul.SERVICE_ERROR);
		}

		Document res = ErrorAghioul.serviceAccepted().append("user",user.get("weshs"));
		res.append("messages",Message.getByUser(key, toSearch));
		return res;

	}

	private static String indexMap = "function(){\n" +
			"\tvar tokens = this.text.trim().replace(/[.,\\/#!$%\\^&\\*;:{}=\\-_`~()?]/g,\"\").toLowerCase().split(\" \");\n" +
			"\tvar words = {};\n" +
			"\tfor(var i=0; i<tokens.length; i++){\n" +
			"\t\tif(words[tokens[i]] == undefined){\n" +
			"\t\t\twords[tokens[i]] = 1;\n" +
			"\t\t}\n" +
			"\t\telse{\n" +
			"\t\t\twords[tokens[i]] += 1;\n" +
			"\t\t}\n" +
			"\t}\n" +
			"\tfor(let w in words){\n" +
			"\t\tvar ret = {};\n" +
			"\t\tret[this._id.valueOf()] = words[w] / tokens.length;\n" +
			"\t\temit(w, ret);\n" +
			"\t}\n" +
			"}";

	private static String indexMap2 = "function(){" +
			" var Ttokens = this.text.replace('@/[.,\\/!$%\\^&\\*;:{}=\\-_`~()]/g', ''); " +
			" var tokens = Ttokens.replace('.', '').trim().split();"+
			" var words = {}; " + 
			" for(var i=0; i<tokens.length; i++){ " + 
			" if(words[tokens[i]] == undefined){ " + 
			" words[tokens[i]] = 1; " + 
			" } " + 
			" else{ " + 
			" words[tokens[i]] += 1; " + 
			" } " + 
			" } " + 
			" for(let w in words){ " + 
			" var ret = {}; " + 
			" ret[this._id] = words[w] / tokens.length; " + 
			" emit(w, ret); " + 
			" } " + 
			" } ";
	private static String indexReduce = "function(key, values){ " + 
			" var idf = Math.log(N/(values.length)); " + 
			" var ret = {}; " + 
			" for(var i=0; i<values.length; i++){ " + 
			" for(let d in values[i]){ " + 
			" ret[d] = values[i][d] * idf; " + 
			" } " + 
			" } " + 
			" return ret; " + 
			" } ";

	/**
	 * renvoie une liste de documents qui correspondent à la recherche query
	 */
	public static Document search(String key, String query) {
		
		Connection c = null;
		try {
			c = Database.getMySQLConnection();
		} catch (SQLException e){
		    e.printStackTrace();
		    return ErrorAghioul.serviceRefused("SQL database error", ErrorAghioul.SQL_ERROR);
		}
		/*
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
		*/
		try {
			c.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		MongoDatabase database = mongoClient.getDatabase("aghioul");
		
		/*interpretation de la requete - elimination de la ponctuation et des blancs et passage en minuscule*/
	    String [] tokens = query.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
	    /*----------------------------*/
	    /*map-reduce en 1 ligne*/
	    MapReduceIterable<Document> result = database.getCollection("messages").mapReduce(indexMap, indexReduce).scope(new Document("N", database.getCollection("messages").count())).collectionName("messageIndex").action(MapReduceAction.REPLACE);
		result.toCollection();
		/*----------*/


		MongoCollection<Document> collection = database.getCollection("messageIndex");

		ArrayList<String> l = new ArrayList<String>();
		Collections.addAll(l, tokens);
		Map<String, Double> indexmap = new HashMap<String, Double>();
		LinkedList<Document> messages = new LinkedList<Document>();
		/*chercher le mot et les documents correspondants*/
		FindIterable<Document> iterable = collection.find(new Document("_id", new Document("$in", l)));
		MongoCursor<Document> cursor = iterable.iterator();			
		while (cursor.hasNext()) {
			//ajouter les messages
			Document d = (Document)cursor.next().get("value");
			Set<String> keyset = d.keySet();
			
			for(String k : keyset) {
				indexmap.put(k, d.getDouble(k));
			}
		}
		indexmap = sortByValue(indexmap);
		//on a maintenant une collection de messages triée par ordre décroissant de tf-idf
		
		//chercher les messages correspondants
		ArrayList<ObjectId> messageIds = new ArrayList<ObjectId>();
		for(Map.Entry<String,Double> entry : indexmap.entrySet()) {
			messageIds.add(new ObjectId(entry.getKey()));
		}
		collection = database.getCollection("messages");
		iterable = collection.find(new Document("_id", new Document("$in", messageIds)));
		cursor = iterable.iterator();			
		while (cursor.hasNext()) {
			//ajouter les messages
			messages.add(cursor.next());
		}
		
		mongoClient.close();
		
		return ErrorAghioul.serviceAccepted().append("weshs", messages);
	}
	
	
	/*
	 * merci à https://www.mkyong.com/java/how-to-sort-a-map-in-java/
	 * permet de trier une hashMap par valeurs
	 * je l'ai modifié pour trier dans le sens décroissant
	 */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> unsortMap) {
    	
        List<Map.Entry<K, V>> list =
                new LinkedList<Map.Entry<K, V>>(unsortMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;

    }

	
}