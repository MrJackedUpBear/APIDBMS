package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Users {
	private static String env = "";
	
	private static String DB_URL = "jdbc:mysql://localhost:3306/Wire?" + env;
	
	private static Users instance = new Users();
	
	private Users() {
		try {
	        Class.forName("com.mysql.cj.jdbc.Driver");
	    } catch (ClassNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
		
		String users = "CREATE TABLE IF NOT EXISTS users ("
				+ " Username CHAR(40) PRIMARY KEY NOT NULL,"
				+ " Password CHAR(40) NOT NULL,"
				+ " ModifyBoxWireTableAccess BOOL NOT NULL,"
				+ " ModifyPartsTableAccess BOOL NOT NULL,"
				+ " AddUsersAccess BOOL NOT NULL,"
				+ " DeleteUsersAccess BOOL NOT NULL);";
		
		try (Connection conn = DriverManager.getConnection(DB_URL);
				Statement stmt = conn.createStatement()){
			stmt.execute(users);
		} catch (SQLException e) {
			System.out.println("Error creating table: " + e.getMessage());
		}
	}
	
	public static Users getInstance() {
		return instance;
	}
	
	public String getPassword(String username) {
		String password = "";
		String sqlQuery = "SELECT Users.Password FROM Users WHERE Username=?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL);
				PreparedStatement pstmt = conn.prepareStatement(sqlQuery)){
			pstmt.setString(1, username);
			
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				password = rs.getString(1);
			}
			else {
				password = "";
			}
			
		} catch (SQLException e) {
			System.out.println("Unable to connect: " + e.getMessage());
		}
		
		
		return password;
	}
	
	public int changePassword(String username, String password) {
		String sql = "UPDATE Users SET Password=? WHERE Username=?;";
		
		try (Connection conn = DriverManager.getConnection(DB_URL);
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, password);
			pstmt.setString(2, username);
			
			int rowsAffected = pstmt.executeUpdate();
			
			return rowsAffected;
			
		} catch (SQLException e) {
			System.out.println("Error connecting to table: " + e.getMessage());
		}
		return 0;
	}
	
	public void addUser(String username, String password, boolean updateWireTableAccess, boolean updatePartsTableAccess,boolean addUserAccess, boolean deleteUserAccess) {
		String sqlQuery = "INSERT INTO Users VALUES (?, ?, ?, ?, ?, ?)";
		
		try (Connection conn = DriverManager.getConnection(DB_URL);
				PreparedStatement pstmt = conn.prepareStatement(sqlQuery)){
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			pstmt.setBoolean(3,  updateWireTableAccess);
			pstmt.setBoolean(4, updatePartsTableAccess);
			pstmt.setBoolean(5, addUserAccess);
			pstmt.setBoolean(6, deleteUserAccess);			
			int rowsAffected = pstmt.executeUpdate();
			
			if (!(rowsAffected > 0)) {
				System.out.println("Could not create user");
			}
			
		} catch (SQLException e) {
			System.out.println("Unable to add user: " + e.getMessage());
		}
	}
	
	public void deleteUser(String username) {
		String sql = "DELETE FROM Users WHERE Username=?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL);
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, username);
		} catch (SQLException e) {
			System.out.println("Unable to delete user: " + e.getMessage());
		}
	}
	
	public boolean hasAccess(String username, String accessor) {
		String sqlQuery = "SELECT Users." + accessor + " FROM Users WHERE Username=?";
		boolean hasAccess = false;
		
		try(Connection conn = DriverManager.getConnection(DB_URL);
				PreparedStatement pstmt = conn.prepareStatement(sqlQuery)){
			pstmt.setString(1, username);
			
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				hasAccess = rs.getBoolean(1);
			}
		} catch (SQLException e) {
			System.out.println("Error accessing database: " + e.getMessage());
		}
		
		return hasAccess;
	}
}
