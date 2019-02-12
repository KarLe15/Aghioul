package com.aghioul.analytics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.bson.Document;

import com.aghioul.DB.DBtools;
import com.aghioul.DB.Database;
import com.aghioul.tools.ErrorAghioul;

public class AghioulNetwork {
	
	private static String getEntireNetworkSQL = "SELECT p1.username as u1, p2.username as u2 FROM friends, users p1, users p2 WHERE p1.id=friends.userid AND p2.id=friends.followsid"; 
	static String get2ndNeighborhoodEdges = "SELECT DISTINCT p1.username as u1, p2.username as u2 from friends, users p1, users p2 " + 
			"WHERE p1.id=userid and p2.id=followsid " + 
			"AND (p1.username IN (SELECT p6.username from friends, users p5, users p6 where p5.id=userid AND p6.id=followsid AND p5.username=?) " + 
			"OR p1.username IN (SELECT p4.username from friends, users p3, users p4 where p3.id=followsid AND p4.id=userid AND p3.username=?) " + 
			"OR p2.username IN (SELECT p3.username from friends, users p3, users p4 where p3.id=userid AND p4.id=followsid AND p4.username=?) " + 
			"OR p2.username IN (SELECT p3.username from friends, users p3, users p4 where p3.id=followsid AND p4.id=userid AND p4.username=?) " + 
			"OR p1.username=? OR p2.username=?)";
	
	
	/*
	 * renvoie une représentation JSON du graphe des followers & followings de "username", et leurs followers & followings
	 * sert pour l'affichage graphique
	 */
	public static Document getNeighborhood(String key, String username) {
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
		}*/
		Document d = new Document();
		ArrayList<Document> E = new ArrayList<Document>();
		ArrayList<Document> V = new ArrayList<Document>();
		HashSet<String> hV = new HashSet<String>();
		PreparedStatement preparedStatement = null;
		try {
			
			preparedStatement = c.prepareStatement(get2ndNeighborhoodEdges);
			for(int i=1; i<7; i++)
				preparedStatement.setString(i, username);
			
			ResultSet res = preparedStatement.executeQuery();
			//ajouter les aretes du graphe
			int i = 0;
			while(res.next()) {
				E.add(new Document("id", "e"+i).append("source", res.getString("u1")).append("target", res.getString("u2")));
				hV.add(res.getString("u1"));
				hV.add(res.getString("u2"));
			}
			Iterator<String> it = hV.iterator();
			while(it.hasNext()) {
				V.add(new Document("id", it.next()));
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		return d.append("nodes", V).append("edges", E);
	}
	
}
