package com.aghioul.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;

import org.bson.Document;

import com.aghioul.DB.DBtools;
import com.aghioul.DB.Database;
import com.aghioul.tools.ErrorAghioul;


public class Friends {

	/**
	 * 
	 * @param key
	 * @param userid
	 * @param followid
	 * @return
	 */

	//vaudrait mieux utiliser les utilsateur eux même au lieux des ids
	public static Document follow(String key, int userid ,int followid) {
		if(userid == followid){
			return ErrorAghioul.serviceRefused("Cannot follor yourself",ErrorAghioul.SERVICE_ERROR);
		}
		Connection c = null;
		try {
			c = Database.getMySQLConnection();
		} catch (SQLException e){
		    e.printStackTrace();
		    return ErrorAghioul.serviceRefused("SQL database error", ErrorAghioul.SQL_ERROR);
		}
		if(!DBtools.verifConnexion(c, key, userid))
			return ErrorAghioul.serviceRefused("authentification required", ErrorAghioul.SERVICE_ERROR);
		if(!DBtools.verifyTimeOut(c, key)) {
			DBtools.logout(c, key);
			return ErrorAghioul.serviceRefused("session expired", ErrorAghioul.SERVICE_ERROR);
		}

		if(DBtools.follow(c, userid, followid))
			// ajouter peut être un petit message
			return ErrorAghioul.serviceAccepted();
		else {
			return ErrorAghioul.serviceRefused("SQL database error", ErrorAghioul.SQL_ERROR);
		}
	}


	public static Document follow(String key, String user, String follow){
		if(user.equals(follow)){
			return ErrorAghioul.serviceRefused("Cannot follow yourself",ErrorAghioul.SERVICE_ERROR);
		}
		Connection c = null;
		try {
			c = Database.getMySQLConnection();
		} catch (SQLException e){
			e.printStackTrace();
			return ErrorAghioul.serviceRefused("SQL database error", ErrorAghioul.SQL_ERROR);
		}
		if(!DBtools.verifyTimeOut(c, key)) {
			DBtools.logout(c, key);
			return ErrorAghioul.serviceRefused("session expired", ErrorAghioul.SERVICE_ERROR);
		}
		
		int userid = DBtools.getUserFromUsers(c,user).getInteger("id");
		int followid = DBtools.getUserFromUsers(c,follow).getInteger("id");
		return follow(key, userid, followid);
	}



	/**
	 *
	 * @param key
	 * @param userid
	 * @param followid
	 * @return
	 */
	//Pas encore utilisée créer SERVLET
	public static Document unFollow(String key, int userid ,int followid) {
		Connection c = null;
		try {
			c = Database.getMySQLConnection();
		} catch (SQLException e){
		    //e.printStackTrace();
		    return ErrorAghioul.serviceRefused("SQL database error", ErrorAghioul.SQL_ERROR);
		}
		if(!DBtools.verifConnexion(c, key, userid))
			return ErrorAghioul.serviceRefused("authentification required", ErrorAghioul.SERVICE_ERROR);
		if(!DBtools.verifyTimeOut(c, key)) {
			DBtools.logout(c, key);
			return ErrorAghioul.serviceRefused("session expired", ErrorAghioul.SERVICE_ERROR);
		}
		if(DBtools.unfollow(c, userid, followid))
			return ErrorAghioul.serviceAccepted();
		else
			return ErrorAghioul.serviceRefused("SQL database error", ErrorAghioul.SQL_ERROR);
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	public static Document getFollowers(String key, String username) {
		Document res = ErrorAghioul.serviceAccepted();
		
		Connection c = null;
		try {
			c = Database.getMySQLConnection();
		} catch (SQLException e){
		    e.printStackTrace();
		    return ErrorAghioul.serviceRefused("SQL database error", ErrorAghioul.SQL_ERROR);
		}
		Document followers = DBtools.getFollowers(c, username);
		//fermeture connexion
		try {
			c.close();
		} catch (SQLException e) {e.printStackTrace();}
		if(followers != null) {
			res.append("users", followers);
			return res;
		}
		return ErrorAghioul.serviceRefused("SQL database error", ErrorAghioul.SQL_ERROR);
	}


	public static Document getFollowings(String key, String username) {
		Document res = ErrorAghioul.serviceAccepted();
		
		Connection c = null;
		try {
			c = Database.getMySQLConnection();
		} catch (SQLException e){
		    e.printStackTrace();
		    return ErrorAghioul.serviceRefused("SQL database error", ErrorAghioul.SQL_ERROR);
		}
		Document followings = DBtools.getFollowings(c, username);
		//fermeture connexion
		try {
			c.close();
		} catch (SQLException e) {e.printStackTrace();}
		if(followings != null) {
			res.append("users", followings);
			return res;
		}
		return ErrorAghioul.serviceRefused("SQL database error", ErrorAghioul.SQL_ERROR);
	}
	
}
