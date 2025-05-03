package database;

import java.util.HashMap;

public class Database {
	private static Database db = new Database();
	
	/*
	 * Returns an instance of the database class
	 */
	public static Database getInstance() {
		return db;
	}
	
	/*
	 * Calls the add to table method from the wire box table class.
	 */
	public void addToTable(String wireType, double wireLength, int boxNumber, String username) {
		WireBoxTable.getInstance().addToTable(wireType, wireLength, boxNumber);
	}
	
	/*
	 * Calls the show table method from the wire box table class
	 */
	public HashMap<Integer, HashMap<String, String>> showTable() {
		return WireBoxTable.getInstance().showTable();
	}
	
	/*
	 * Calls the update table method from the wire box table class
	 */
	public void updateTable(double wireLength, String wireType, int boxNumber, String username) {
		WireBoxTable.getInstance().updateTable(wireLength, wireType, boxNumber);
	}
	
	public boolean deleteFromTable(String wireType, int boxNumber, String username) {
		if (Users.getInstance().hasAccess(username, "ModifyBoxWireTableAccess")){
			WireBoxTable.getInstance().deleteFromTable(wireType, boxNumber);
			return true;
		}
		return false;
	}
	
	public HashMap<Integer, HashMap<String, String>> getValues(String updateCommand, String ID, String username) {
		return WireBoxTable.getInstance().getValues(updateCommand, ID);
	}
	
	public String getPassword(String username) {
		return Users.getInstance().getPassword(username);
	}
	
	public boolean addUser(String currentUser, String username, String password, boolean updateWireTableAccess, boolean updatePartsTableAccess, boolean addUsersAccess, boolean deleteUserAccess) {
		if (Users.getInstance().hasAccess(currentUser, "AddUsersAccess")) {
			Users.getInstance().addUser(username, password, updateWireTableAccess, updatePartsTableAccess, addUsersAccess, deleteUserAccess);
		} else {
			return false;
		}
		return true;
	}
	
	public boolean deleteUser(String currentUser, String username) {
		if (!Users.getInstance().hasAccess(username, "DeleteUsersAccess")) {
			return false;
		}
		
		Users.getInstance().deleteUser(username);
		return true;
	}
	
	public boolean changePassword(String username, String oldPassword, String newPassword) {
		String password = getPassword(username);
		
		if (!password.equals(oldPassword)) {
			return false;
		}
		
		int rowsAffected = Users.getInstance().changePassword(username, newPassword);
		if (rowsAffected == 0) {
			return false;
		}
		else {
			return true;
		}
	}
	
	public HashMap<Integer, HashMap<String, String>> getParts() {
		return PartsTable.getInstance().showTable();
	}
	
	public boolean addToParts(String username, String partNumber, String partType, String partDesc, String partNote) {
		if (!Users.getInstance().hasAccess(username, "ModifyPartsTableAccess")) {
			return false;
		}
		
		PartsTable.getInstance().addToTable(partNumber, partType, partDesc, partNote);
		
		return true;
	}
	
	public boolean deletePart(String username, String partNumber) {
		if (!Users.getInstance().hasAccess(username, "ModifyPartsTableAccess")) {
			return false;
		}
		
		PartsTable.getInstance().deleteFromTable(partNumber);
		
		return true;
	}
	
	public HashMap<Integer, HashMap<String, String>> getSpecificPart(String partNumber){
		return PartsTable.getInstance().getValues("PartNumber", partNumber);
	}
	
	public boolean updatePart(String username, String partType, String partDesc, String partNote, String partNumber) {
		if (!Users.getInstance().hasAccess(username, "ModifyPartsTableAccess")) {
			return false;
		}
		PartsTable.getInstance().updateTable(partType, partDesc, partNote, partNumber);
		
		return true;
	}
}
