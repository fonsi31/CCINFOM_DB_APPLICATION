package util;
import java.sql.*;

/**
 * Establishes and manages connection to the MySQL Database
 * 
 * @param url the JDBC URL of the database
 * @param user the database username
 * @param password the database password
 */
public class DBConnection {
	private String url;
	private String user;
	private String password;
	
	public DBConnection (String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
	}
	
	/**
	 * Established a connection to the MySQL Database
	 * 
	 * @return an active database connection
	 * @throws SQLException if a database access error occurs
	 */
	public Connection connect() throws SQLException{
		return DriverManager.getConnection(this.url, this.user, this.password);
	}
}