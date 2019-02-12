package com.aghioul.DB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.commons.lang.RandomStringUtils;
import org.bson.Document;

import com.aghioul.tools.ErrorAghioul;
import com.aghioul.tools.Utilisateur;

public class DBtools {
	
	private static final String verifUserSQL = "SELECT id FROM users where username=?";
	private static final String verifMailSQL = "SELECT id FROM users where mail=?";
	private static final String logoutSQL = "DELETE from connexion WHERE cle=?";
	private static final String verifConnexionSQL = "SELECT * FROM connexion where cle=? AND id=?";
	private static final String verifPasswordSQL = "SELECT * FROM users WHERE password=? AND username=?";
	private static final String verifKeySQL = "SELECT * FROM connexion WHERE cle=?";
	private static final String verifFollowSQL = "SELECT * FROM friends WHERE userid=? AND followsid=?";
	private static final String verifConnexionExistanteSQL = "SELECT users.id, connexion.cle as cle from connexion, users WHERE connexion.id=users.id AND users.username=?";
	private static final String verifyTimeOut = "SELECT * FROM connexion where cle=?";
	private static final String insertTimeOut = "UPDATE connexion SET last_used=? where cle=?";
	private static final String insertUserSQL = "INSERT INTO users"
			+ "(id, username, password, nom, prenom, mail, sexe) VALUES"
			+ "(?,?,?,?,?,?,?)";
	private static final String addConnectionSQL = "INSERT INTO connexion(id, cle, last_used) VALUES(?,?, NOW())";
	private static final String disconnectSQL = "DELETE FROM connexion WHERE id=?";
	private static final String getFollowersSQL = "SELECT username, id FROM users, friends WHERE followsid=(select id from users where username=?) AND userid=id";
	private static final String getFollowingsSQL = "SELECT username, id FROM users, friends WHERE userid=(select id from users where username=?) AND followsid=id";
	private static final String getUserSQL = "SELECT users.id AS id, username, nom, prenom, sexe FROM users, connexion WHERE cle=? AND connexion.id=users.id";
	private static final String getUserFromUsersSQL = "SELECT * FROM users WHERE username=?";
	private static final String followSQL = "INSERT INTO friends (userid,followsid,since) VALUES (?,?,NOW())";
	private static final String unfollowSQL = "DELETE FROM friends where userid=? AND followsid=?";
	/**
	 *	Permet de connecter un utilisateur si celui ci existe et n'est pas connecte
	 * @param c
	 * @param username
	 * @param password
	 * @return	JSon du résultat de la connexion
	 */
	public static Document connect(Connection c, String username, String password) {
		try {
			/*verifier que le mot de passe et de nom d'utilisateur sont bons*/
			PreparedStatement preparedStatement = c.prepareStatement(verifPasswordSQL);
			preparedStatement.setString(1, password);
			preparedStatement.setString(2, username);
			ResultSet res = preparedStatement.executeQuery();
			if(!res.next()) {
				return ErrorAghioul.serviceRefused("invalid username or password", ErrorAghioul.SERVICE_ERROR);
			}
			Document json = new Document();
			//si l'utilisateur est deja connecté, se connecter mais ne pas définir de nouvelle clé
			String cle = null;
			if((cle = verifConnexionExistante(c, username)) != null) {
				reconnect(c, cle);
				System.out.println("RECONNEXION");
				json.append("id", res.getInt("id"));
		        json.append("username", username);
		        json.append("name", res.getString("prenom")+" "+res.getString("nom"));
		        json.append("sex", res.getString("sexe"));
		        json.append("key",cle);
		        return json;
			}
			/*verifier que la clé n'est pas dans la table de connexion déjà*/
			String key;
			do {
				key = RandomStringUtils.randomAlphanumeric(32);
			} while(keyExists(c, key));
	        //ajouter la key a la bdd connexion
			
			PreparedStatement update = null;
			try {
				update = c.prepareStatement(addConnectionSQL);
				update.setInt(1, res.getInt("id"));
				update.setString(2, key);
				update.executeUpdate();
			} catch(SQLException e){
				return ErrorAghioul.serviceRefused("SQL error", ErrorAghioul.SQL_ERROR);
			}
	        json.append("id", res.getInt("id"));
	        json.append("username", username);
	        json.append("name", res.getString("prenom")+" "+res.getString("nom"));
	        json.append("sex", res.getString("sexe"));
	        json.append("key",key);
			
			return json;
		} catch (SQLException e) {
			e.printStackTrace();
			return ErrorAghioul.serviceRefused("SQL error", ErrorAghioul.SQL_ERROR);
		}
	}

	public static boolean logout(Connection c, String key) {
		
		try {
			PreparedStatement preparedStatement =  c.prepareStatement(logoutSQL);
			preparedStatement.setString(1, key);
			preparedStatement.executeUpdate();
			return true;
		}catch (SQLException e) {
			return false;
		}
	}


	public static Document disconnect(Connection c, String username, String key){
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = c.prepareStatement(verifUserSQL);
			preparedStatement.setString(1, username);
			ResultSet res = preparedStatement.executeQuery();
			if(!res.next()){
				return ErrorAghioul.serviceRefused("User Unknown", ErrorAghioul.SERVICE_ERROR);
			}

			int id = res.getInt("id");

			preparedStatement = c.prepareStatement(verifConnexionSQL);
			preparedStatement.setString(1,key);
			preparedStatement.setInt(2,id);
			res = preparedStatement.executeQuery();
			if(!res.next()){
				return ErrorAghioul.serviceRefused("User not Connected", ErrorAghioul.SERVICE_ERROR);
			}


			PreparedStatement update = c.prepareStatement(disconnectSQL);
			update.setInt(1,id);
			if(update.executeUpdate() != 1){
				return ErrorAghioul.serviceRefused("SQL error", ErrorAghioul.SQL_ERROR);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ErrorAghioul.serviceAccepted();
	}
	
	/**
	 * renvoie true si l'utilisateur est deja logué
	 * @param c
	 * @param username
	 * @return
	 */
	private static String verifConnexionExistante(Connection c, String username) {
		try {
			PreparedStatement preparedStatement =  c.prepareStatement(verifConnexionExistanteSQL);
			preparedStatement.setString(1, username);
			ResultSet res = preparedStatement.executeQuery();
			if(res.next())
				return res.getString("cle");
			else
				return null;
		} catch (SQLException e) {
			return null;
		}	
	}
	
	/**
	 * renvoie true si username n'existe pas
	 * @param c
	 * @param username
	 * @return
	 */
	public static boolean verifUser(Connection c, String username) {

		try {
			PreparedStatement preparedStatement =  c.prepareStatement(verifUserSQL);
			preparedStatement.setString(1, username);
			ResultSet res = preparedStatement.executeQuery();
			if(res.next())
				return false;
			else
				return true;
		} catch (SQLException e) {
			return false;
		}
	}


	/**
	 *	renvoie true si le mail est deja utilise
	 * @param c
	 * @param mail
	 * @return
	 */
	public static boolean verifUserMail(Connection c, String mail) {

		try {
			PreparedStatement preparedStatement =  c.prepareStatement(verifMailSQL);
			preparedStatement.setString(1, mail);
			ResultSet res = preparedStatement.executeQuery();
			//int count = getNbResultat(res);

//			int nb = res.getFetchSize(); // ça renvoie le nombre de lignes lue
			if(res.next())
				return false;
			else
				return true;
		} catch (SQLException e) {
			return false;
		}
	}


	/**
	 * permet de creer un utilisateur et de l'inserer dans la BD
	 * @param c
	 * @param utilisateur
	 * @return
	 */
	public static boolean insertUser(Connection c, Utilisateur utilisateur) {

		try {
			PreparedStatement preparedStatement = c.prepareStatement(insertUserSQL);
			preparedStatement.setNull(1, java.sql.Types.INTEGER);
			preparedStatement.setString(2, utilisateur.getUserName());
			preparedStatement.setString(3, utilisateur.getPassword());
			preparedStatement.setString(4, utilisateur.getNom());
			preparedStatement.setString(5, utilisateur.getPrenom());
			preparedStatement.setString(6, utilisateur.getEmail());
			preparedStatement.setString(7, utilisateur.getGender());
			preparedStatement .executeUpdate();
		} catch (SQLException e) {
			return false;
		}
		return true;
	}


	/**
	 * renvoie true si la cle existe dans la table de connection
	 * @param c
	 * @param key
	 * @return
	 */
	private static boolean keyExists(Connection c, String key) {
		PreparedStatement preparedStatement;
		try {
			preparedStatement = c.prepareStatement(verifKeySQL);
			preparedStatement.setString(1, key);
			ResultSet res = preparedStatement.executeQuery();
			//int nb = getNbResultat(res);
			if(res.next())
				return true;
			else
				return false;
		} catch (SQLException e) {e.printStackTrace(); return false;}

	}


	/**
	 * donne le nombre de resultat de la requete SQL
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	public static int getNbResultat(ResultSet resultSet) throws SQLException {
		int count = 0;
		while (resultSet.next()) {
			++count;
		}
		return count;
	}


	/**
	 * verifie si l'utilisateur ID est connecte avec la bonne cle
	 * @param c
	 * @param key
	 * @param id
	 * @return
	 */
	public static boolean verifConnexion(Connection c, String key, int id) {
		try {
			PreparedStatement preparedStatement = c.prepareStatement(verifConnexionSQL);
			preparedStatement.setString(1, key);
			preparedStatement.setInt(2, id);
			ResultSet res = preparedStatement.executeQuery();
			if(res.next())
				return true;
			return false;
		} catch(SQLException e) {
			return false;
		}
	}


	/***
	 *
	 * @param c
	 * @param userid
	 * @param followid
	 * @return
	 */
	public static boolean verifFollow(Connection c, int userid, int followid){
		try {
			PreparedStatement preparedStatement = c.prepareStatement(verifFollowSQL);
			preparedStatement.setInt(1,userid );
			preparedStatement.setInt(2, followid);
			ResultSet res = preparedStatement.executeQuery();
			return res.next();
		} catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 *
	 * @param c
	 * @param userid
	 * @param followid
	 * @return
	 */
	public static boolean follow(Connection c, int userid, int followid) {
		try {
			if(verifFollow(c,userid,followid)){
				return false;
			}

			PreparedStatement preparedStatement = c.prepareStatement(followSQL);
			preparedStatement.setInt(1, userid);
			preparedStatement.setInt(2, followid);
			preparedStatement.executeUpdate();
			return true;
		} catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
	}


	/**
	 *
	 * @param c
	 * @param userid
	 * @param followid
	 * @return
	 */
	public static boolean unfollow(Connection c, int userid, int followid) {
		try {
			PreparedStatement preparedStatement = c.prepareStatement(unfollowSQL);
			preparedStatement.setInt(1, userid);
			preparedStatement.setInt(2, followid);
			int res = preparedStatement.executeUpdate();
			return res == 1;
		} catch(SQLException e) {
			return false;
		}
	}



	/**
	 *
	 * @param c
	 * @param id
	 * @return
	 */
	public static Document getFollowings(Connection c, String username) {
		Document followings = new Document();
		try {
			PreparedStatement preparedStatement = c.prepareStatement(getFollowingsSQL);
			preparedStatement.setString(1, username);
			ResultSet res = preparedStatement.executeQuery();
			while(res.next()) {
				followings.append(res.getString("username"), true);
			}
			return followings;
		} catch(SQLException e) {
			return null;
		}
	}

	/**
	 *
	 * @param c
	 * @param id
	 * @return
	 */
	public static Document getFollowers(Connection c, String username) {
		Document followers = new Document();
		try {
			PreparedStatement preparedStatement = c.prepareStatement(getFollowersSQL);
			preparedStatement.setString(1, username);
			ResultSet res = preparedStatement.executeQuery();
			while(res.next()) {
				Document f = new Document();
				followers.append(res.getString("username"), true);
			}
			return followers;
		} catch(SQLException e) {
			return null;
		}
	}


	/**
	 * pour recuperer l'utilisateur a partir de la cle de connection
	 * @param c
	 * @param key
	 * @return
	 */
	public static Document getUser(Connection c, String key) {
		try {
			PreparedStatement preparedStatement = c.prepareStatement(getUserSQL);
			preparedStatement.setString(1, key);
			ResultSet res = preparedStatement.executeQuery();
			Document u = new Document();
			while(res.next()) {
				u.append("id", res.getInt("id"));
				u.append("name", res.getString("prenom") + " " + res.getString("nom"));
				u.append("username", res.getString("username"));
				u.append("sex", res.getString("sexe"));
			}
			return u;
		} catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Document getUserFromUsers(Connection c, String username){
		try {
			PreparedStatement preparedStatement = c.prepareStatement(getUserFromUsersSQL);
			preparedStatement.setString(1, username);
			ResultSet res = preparedStatement.executeQuery();
			Document u = new Document();
			while(res.next()) {
				u.append("id", res.getInt("id"));
				u.append("name", res.getString("prenom") + " " + res.getString("nom"));
				u.append("username", res.getString("username"));
				u.append("sex", res.getString("sexe"));
			}
			return u;
		} catch(SQLException e) {
//			ErrorJson.serviceRefused("SQL ERROR",ErrorJson.SQL_ERROR);
			return null;
		}
	}
	
	public static boolean reconnect(Connection c, String key) {
		try {
			PreparedStatement p = c.prepareStatement(insertTimeOut);
			p.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			p.setString(2, key);
			p.executeUpdate();
			return true;
		}catch(SQLException e) {
			return false;
		}
	}
	
	public static boolean verifyTimeOut(Connection c, String key) {
		try {
			PreparedStatement preparedStatement = c.prepareStatement(verifyTimeOut);
			preparedStatement.setString(1, key);
			ResultSet res = preparedStatement.executeQuery();
			while(res.next()) {
				Timestamp d = res.getTimestamp("last_used");
				//il y a 30 minutes
				Timestamp timestamp = new Timestamp(System.currentTimeMillis() - 1000*60*30);
				//vérifier si d est après maintenant - 30 minutes
				if(d.compareTo(timestamp) > 0) {
					PreparedStatement p = c.prepareStatement(insertTimeOut);
					//se rajouter 30 minutes de temps
					p.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
					p.setString(2, key);
					p.executeUpdate();
					return true;
				}
				else {
					return false;
				}
			}
		} catch(SQLException e) {
			return false;
		}
		return false;
	}
}


