package com.aghioul.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class Database {
	private static DataSource dataSource;
	
	public Database(String jndiname) throws SQLException {
		try {
			dataSource = (DataSource) new InitialContext().lookup("java:comp/env/" + jndiname);
		} catch(NamingException e) {
			throw new SQLException(jndiname + "is missing in JNDI! : " + e.getMessage());
		}
	}
	
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
	
	public static Connection getMySQLConnection() throws SQLException {
		if(!DBstatic.mysql_pooling) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
			}
			return (DriverManager.getConnection("jdbc:mysql://" + DBstatic.mysql_host + "/" + DBstatic.mysql_db, DBstatic.mysql_username,DBstatic.mysql_password));
		}
		else {

			return new Database("jdbc/db").getConnection();
		}
	}
}
