package com.aghioul.services;

import com.aghioul.DB.DBtools;
import com.aghioul.DB.Database;
import com.aghioul.tools.ErrorAghioul;
import com.aghioul.tools.Utilisateur;

//import com.mysql.jdbc.Connection;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang.RandomStringUtils;
import org.bson.Document;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Authentification {
    public static Pattern PATTERN_MAIL = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+(\\.[a-z0-9-]+)+$");
    public static Pattern PATTERN_NOM = Pattern.compile("^[a-zA-Z\\s-]{1,25}$");

    /*implémente le service "createuser" spécifié, nous passons un user au lieu de nom + prenom*/
    public static Document signup(Utilisateur utilisateur){
    	/*un utilisateur : nom prenom username password mail age sexe*/
    	
        //verification des parametres
        ArrayList<String> errors = new ArrayList<String>();
        Matcher m = PATTERN_MAIL.matcher(utilisateur.getEmail());
        if(!m.matches()) {
            errors.add("mailError");
        }
        m = PATTERN_NOM.matcher(utilisateur.getNom());
        if(!m.matches()) {
            errors.add("lastNameError");
        }
        m = PATTERN_NOM.matcher(utilisateur.getPrenom());
        if(!m.matches()) {
            errors.add("nameError");
        }
        if(!errors.isEmpty()) {
            Document ret = ErrorAghioul.serviceRefused("invalid fields", ErrorAghioul.SERVICE_ERROR);
            ret.append("errors", errors);
            return ret;
        }
        // appel a la base de donnee
        Connection c = null;
		try {
			c = Database.getMySQLConnection();
		} catch (SQLException e) {e.printStackTrace();}
        boolean userExists = !DBtools.verifUser(c, utilisateur.getUserName());
        boolean mailExists = !DBtools.verifUserMail(c, utilisateur.getEmail());
        if(userExists)
        	errors.add("username already taken");
        if(mailExists)
        	errors.add("mail already taken");
        
        if(!errors.isEmpty()) {
            Document ret = ErrorAghioul.serviceRefused("invalid fields", ErrorAghioul.SERVICE_ERROR);
            ret.append("errors", errors);
            return ret;
        } 
        
        utilisateur.setPassword(utilisateur.getPassword()); // à quoi sert cette ligne
        if(!DBtools.insertUser(c, utilisateur)) {
        	return ErrorAghioul.serviceRefused("SQL error", ErrorAghioul.SQL_ERROR);
        }

        try {
            c.close();
        } catch (SQLException e) {
            return ErrorAghioul.serviceRefused("SQL error", ErrorAghioul.SQL_ERROR);
        }
        //
        // modifier le json de retour pour avoir le résultat de tout
        return  ErrorAghioul.serviceAccepted();
    }

    public static Document login(String userName, String password){
        Document res = ErrorAghioul.serviceAccepted();

        Connection c = null;
		try {
			c = Database.getMySQLConnection();
		} catch (SQLException e){
		    e.printStackTrace();
		}
        // verification en bdd de l'utilisateur ET connexion
		res = DBtools.connect(c, userName, password);
		try {
			c.close();
		} catch (SQLException e) {e.printStackTrace();}
        return res;
        
    }

    public static Document logout(String username,String key) {
    	Connection c = null;
		try {
			c = Database.getMySQLConnection();
		} catch (SQLException e){
		    e.printStackTrace();
		}
    	DBtools.logout(c, key);
    	try {
			c.close();
		} catch (SQLException e) {e.printStackTrace();}
    	return ErrorAghioul.serviceAccepted();
    }


}
