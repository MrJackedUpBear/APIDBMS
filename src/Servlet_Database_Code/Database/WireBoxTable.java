package database;

import java.sql.*;
import java.util.HashMap;

public class WireBoxTable {

	private static WireBoxTable table = new WireBoxTable();
	
	/*
	 * Initializes a statement to create the wirebox table if it does not exist with the variables
	 * WireType, WireLength, and BoxNumber
	 */
	private static String createWireBoxTable = "CREATE TABLE IF NOT EXISTS WireBox ("
			+ " WireType CHAR(40) NOT NULL,"
			+ " WireLength DECIMAL(5, 2) NOT NULL,"
			+ " BoxNumber INTEGER NOT NULL,"
			+ " PRIMARY KEY (WireType, BoxNumber));";
	
	private static String env = "";
	
	/*
	 * Initializes the URL used to connect to the msyql database.
	 */
	private static String DB_URL = "jdbc:mysql://localhost:3306/Wire?" + env;
	
	/*
	 * Creates an instance for the wire box table that can be accessed outside of this class.
	 */
	public static WireBoxTable getInstance(){
		/*
		 * Instantiates the driver being used to connect to the mysql database
		 */
		try {
	        Class.forName("com.mysql.cj.jdbc.Driver");
	    } catch (ClassNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
		/*
		 * Connects to the mysql database and creates the table if it has not already been created.
		 */
		try (Connection conn = DriverManager.getConnection(DB_URL);
				Statement stmt = conn.createStatement()){
			stmt.execute(createWireBoxTable);
			
		}catch(SQLException e) {
			System.out.println("Error creating table: " + e.getMessage());
		}
		/*
		 * Returns the instance of the table.
		 */
		return table;
	}
	
	/*
	 * Adds elements to the wirebox table with the given input
	 */
	public void addToTable(String wireType, Double wireLength, int boxNumber) {
		/*
		 * Creates an sql query for inserting values into the table
		 */
		String sqlQuery = "INSERT INTO Wirebox VALUES (?, ?, ?)";
		
		/*
		 * Tries to connect to the database and create a prepared statement to insert the values
		 * into the table
		 */
		try (Connection conn = DriverManager.getConnection(DB_URL);
				PreparedStatement pstmt = conn.prepareStatement(sqlQuery)){
			/*
			 * Sets the values for the question marks above that will be added to the table
			 */
			pstmt.setString(1,wireType);
			pstmt.setDouble(2, wireLength);
			pstmt.setInt(3, boxNumber);
			
			/*
			 * Checks how many columns were affected by this change and it is not more than 1, then 
			 * declares the item could not be added to the table
			 */
			int columnsAffected = pstmt.executeUpdate();
			
			if (!(columnsAffected > 0)) {
				System.out.println("Could not add item.");
			}
		}catch(SQLException e) {
			System.out.println("Error connecting to database: " + e.getMessage());
		}
	}
	
	/*
	 * Returns the values in the table as a string
	 */
	public HashMap<Integer, HashMap<String, String>>  showTable() {
		/*
		 * Creates an sql query to get all columns from the wire box table
		 */
		String sqlQuery = "SELECT * FROM Wirebox";
		HashMap<Integer, HashMap<String, String>> finalHashMap = new HashMap<>();
		
		/*
		 * Tries to connect to the database and create a statement with the sql query above.
		 */
		try (Connection conn = DriverManager.getConnection(DB_URL);
				PreparedStatement pstmt = conn.prepareStatement(sqlQuery)){
			/*
			 * Executes the query and stores the values from the query in the result set
			 */
			ResultSet rs = pstmt.executeQuery();
			
			/*
			 * Continues to iterate through the result set until the next item in the result set is 
			 * void
			 */
			int i = 0;
			while (rs.next()) {
				/*
				 * Gets the wire type, wire length, and box id from the result set. Appends all 
				 * of these values to the final string that will be returned.
				 */
				HashMap<String, String> innerHashMap = new HashMap<>();
				
				
				
				innerHashMap.put("Wire Type", rs.getString(1));
				innerHashMap.put("Wire Length", rs.getString(2));
				innerHashMap.put("Box ID", rs.getString(3));
				
				finalHashMap.put(i, innerHashMap);
				
				i++;
			}
		}catch (SQLException e) {
			System.out.println("Error connecting to database: " + e.getMessage());
		}
		
		return finalHashMap;
	}
	
	/*
	 * Updates the wire length value depending on the wire type and box number
	 */
	public void updateTable(Double wireLength, String wireType, int boxNumber) {
		/*
		 * Creates an sql query that will update the wire box table's wire length where the
		 * wire type and box number are equal to the value input.
		 */
		String sqlQuery = "UPDATE Wirebox SET WireLength=? WHERE WireType=? AND BoxNumber=?";
		
		/*
		 * Tries to connect to the database and create a prepared statement with the sql query above
		 */
		try (Connection conn = DriverManager.getConnection(DB_URL);
				PreparedStatement pstmt = conn.prepareStatement(sqlQuery)){
			
			/*
			 * Adds the wire length to the first question mark that sets the value of the wire length,
			 * then adds the wire type and box number to the last two question marks to check if they
			 * exist in the table
			 */
			pstmt.setDouble(1, wireLength);
			pstmt.setString(2, wireType);
			pstmt.setInt(3, boxNumber);
			
			/*
			 * Executes the query and checks how many columns are affected.
			 */
			int columnsAffected = pstmt.executeUpdate();
			
			/*
			 * If more than one column is affected, the column is updated successfully,
			 * otherwise it was unable to find the item in the table.
			 */
			if (!(columnsAffected > 0)) {
				System.out.println("Could not find item.");
			}
			
		} catch (SQLException e) {
			System.out.println("Error updating: " + e.getMessage());
		}
	}
	
	/*
	 * Deletes a specified task based on the wire type and box number
	 */
	public void deleteFromTable(String wireType, int boxNumber) {
		/*
		 * Creates an sql query to delete a row from the wire box table given the wire type and box
		 * number
		 */
		String sqlQuery = "DELETE FROM Wirebox WHERE WireType=? AND BoxNumber=?";
		
		/*
		 * Tries to connect to the database and create a prepared statement with the query above
		 */
		try (Connection conn = DriverManager.getConnection(DB_URL);
				PreparedStatement pstmt = conn.prepareStatement(sqlQuery)){
			/*
			 * Sets the first question mark to be equal to the wire type to check if the wire type
			 * is equal to the input and if the box number input is equal to the box type in the 
			 * table
			 */
			pstmt.setString(1, wireType);
			pstmt.setInt(2, boxNumber);
			
			/*
			 * Executes the sql query and checks for the number of affected rows.
			 */
			int affectedRows = pstmt.executeUpdate();
			
			/*
			 * If the number of affected rows is greater than one, then the item was deleted successfully,
			 * otherwise the item was not found
			 */
			if (!(affectedRows > 0)) {
				System.out.println("Could not locate item");
			}
			
		} catch (SQLException e) {
			System.out.println("Error connecting to database: " + e.getMessage());
		}
		
	}
	public HashMap<Integer, HashMap<String, String>> getValues(String updateCommand,String ID){
		HashMap<Integer, HashMap<String, String>> hash = new HashMap<>();
		
		String sqlQuery = "SELECT * FROM WireBox WHERE " + updateCommand + "=?;";
		
		try (Connection conn = DriverManager.getConnection(DB_URL);
				PreparedStatement pstmt = conn.prepareStatement(sqlQuery)){
			pstmt.setString(1, ID);			
			ResultSet rs = pstmt.executeQuery();

			int i = 0;
			
			while (rs.next()){
				HashMap<String, String> insideHash = new HashMap<>();
				insideHash.put("Wire Type", rs.getString(1));
				insideHash.put("Wire Length", rs.getString(2));
				insideHash.put("Box ID", rs.getString(3));
				
				hash.put(i, insideHash);
				i++;
	
			}
			
		} catch (SQLException e) {
			System.out.println("SQL Exception: " + e.getMessage());
		}
		
		return hash;
	}

}
