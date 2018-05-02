package db.mysql;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;

public class MySQLTableCreation {
	// Run this as Java application to reset db schema.
	public static void main(String[] args) {
		try {
			// This is java.sql.Connection. Not com.mysql.jdbc.Connection.
			Connection conn = null;

			// Step 1 Connect to MySQL.
			try {
				System.out.println("Connecting to " + MySQLDBUtil.URL);
				// force to load Driver class, call the static block
				Class.forName("com.mysql.jdbc.Driver").getConstructor().newInstance();
				// Same as this line, but driver will never get used 
				// com.mysql.jdbc.Driver driver = new com.mysql.jdbc.Driver();
				conn = DriverManager.getConnection(MySQLDBUtil.URL);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (conn == null) {
				return;
			}
			
			// Step 2 Drop tables in case they exist
			// Drop order history > category > items > users due to foreign keys
			Statement stmt = conn.createStatement();
			String sql = "DROP TABLE IF EXISTS history";
			stmt.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS categories";
			stmt.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS items";
			stmt.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS users";
			stmt.executeUpdate(sql);
			
			// step 3 Create tables
			sql = "CREATE TABLE items (item_id VARCHAR(255) NOT NULL, name VARCHAR(255), " + 
				  "rating FLOAT, address VARCHAR(255), image_url VARCHAR(255), url VARCHAR(255), " + 
				  "distance FLOAT, PRIMARY KEY (item_id))";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE users (user_id VARCHAR(255) NOT NULL, password VARCHAR(255) NOT NULL, " + 
				  "firstname VARCHAR(255), lastname VARCHAR(255), PRIMARY KEY(user_id))";
			stmt.executeUpdate(sql);
			
			// The combination of user_id & item_id must be unique
			sql = "CREATE TABLE history (user_id VARCHAR(255) NOT NULL, item_id VARCHAR(255) NOT NULL, " +
				  "last_favor_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (user_id, item_id)," +
				  "FOREIGN KEY (item_id) REFERENCES items(item_id)," +
				  "FOREIGN KEY (user_id) REFERENCES users(user_id))";
			stmt.executeUpdate(sql);
			
			// The combination of item_id & category must be unique
			sql = "CREATE TABLE categories (item_id VARCHAR(255) NOT NULL, category VARCHAR(255) NOT NULL, " +
			      "PRIMARY KEY ( item_id, category), FOREIGN KEY (item_id) REFERENCES items(item_id))";
			stmt.executeUpdate(sql);
			
			// Step 4 insert a test user
			// username 1111 password 2222 (encrypted)
			sql = "INSERT INTO users VALUES (\"1111\", \"3229c1097c00d497a0fd282d586be050\", \"John\", \"Smith\")";
			stmt.executeUpdate(sql);

			System.out.println("Import is done successfully.");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}

