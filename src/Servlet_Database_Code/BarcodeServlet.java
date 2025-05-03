

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import token.TokenStore;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;

import database.Database;

/**
 * Servlet implementation class BarcodeServlet
 */
@WebServlet("/Table")
public class BarcodeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public BarcodeServlet() {
        // TODO Auto-generated constructor stub
    	
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String authHeader = request.getHeader("authorization");
		
		if (authHeader == null) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		String encodedToken = authHeader.substring(authHeader.indexOf(' ') + 1);
		String decodedToken = new String(Base64.getDecoder().decode(encodedToken));
		String username = TokenStore.getInstance().getUsername(decodedToken);
		
		if (username == null) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		String baseURL = "/BarcodeServlet/Table/";
		String requestURL = request.getRequestURI();
		String table = requestURL.substring(baseURL.length(), requestURL.length());
		
		HashMap<Integer, HashMap<String, String>> hash = new HashMap<>();
		
		if (table.isEmpty()) {
			return;
		}
		
		if (table.equals("BoxTable")) {
			hash = Database.getInstance().showTable();
		}
		else if (table.equals("PartsTable")) {
			hash = Database.getInstance().getParts();
		}
		
		response.getOutputStream().println(String.valueOf(hash));
		
		}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String authHeader = request.getHeader("authorization");
		
		if (authHeader == null || authHeader.equals("Basic")) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
	
		String encodedToken = authHeader.substring(authHeader.indexOf(' ') + 1);
		String decodedToken = new String(Base64.getDecoder().decode(encodedToken));
		String username = TokenStore.getInstance().getUsername(decodedToken);
		
		if (username == null) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		String action = request.getParameter("Action");
		String wireType = request.getParameter("Wire Type");
		String wireLength = request.getParameter("Wire Length");
		String boxID = request.getParameter("Box ID");
		
		String partNumber = request.getParameter("Part Number");
		String partType = request.getParameter("Part Type");
		String partDesc = request.getParameter("Part Description");
		String partNote = request.getParameter("Part Note");
		
		String userToAdd = request.getParameter("New Username");
		String passwordToAdd = request.getParameter("New Password");
		String modifyWireTableAccess = request.getParameter("Modify Wire Table");
		String modifyPartsTableAccess = request.getParameter("Modify Parts Table");
		String modifyUserAccess = request.getParameter("Modify Users");
		String deleteUserAccess = request.getParameter("Delete Users");
		
		String oldPassword = request.getParameter("Old Password");
		
		String userToDelete = request.getParameter("Delete Username");
		
		String baseURL = "/BarcodeServlet/Table/";
		String requestURL = request.getRequestURI();
		String table = requestURL.substring( baseURL.length(), requestURL.length());
		
		if (table.equalsIgnoreCase("BoxTable")) {
			if (action.equalsIgnoreCase("Update")) {
				Database.getInstance().updateTable(Double.parseDouble(wireLength), wireType, Integer.parseInt(boxID), username);
			}
			else if (action.equalsIgnoreCase("Add")) {
				Database.getInstance().addToTable(wireType, Double.parseDouble(wireLength), Integer.parseInt(boxID), username);
			}
			else if (action.equalsIgnoreCase("Delete")) {
				if (!Database.getInstance().deleteFromTable(wireType, Integer.parseInt(boxID), username)) {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				}
			}
			else if (action.equalsIgnoreCase("getboxid")) {
				response.getOutputStream().println(String.valueOf(Database.getInstance().getValues("BoxNumber", boxID, username)));
			}
		}
		else if (table.equalsIgnoreCase("PartsTable")) {
			if (action.equalsIgnoreCase("Add")){			
				if (!Database.getInstance().addToParts(username, partNumber, partType, partDesc, partNote)) {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				}
			}
			else if (action.equalsIgnoreCase("Delete")) {
				if (!Database.getInstance().deletePart(username, partNumber)) {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				}
			}
			else if (action.equalsIgnoreCase("Update")) {
				if (!Database.getInstance().updatePart(username, partType, partDesc, partNote, partNumber)) {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				}
			}
			else if (action.equalsIgnoreCase("Get Part")) {
				response.getOutputStream().println(String.valueOf(Database.getInstance().getSpecificPart(partNumber)));
			}
		}
		

		if (action.equalsIgnoreCase("Add User")) {
			if (userToAdd == null || passwordToAdd == null || modifyWireTableAccess == null || modifyPartsTableAccess == null || modifyUserAccess == null) {
				response.getOutputStream().println("Incorrect usage. Try again.");
			}
			if (!Database.getInstance().addUser(username, userToAdd, passwordToAdd, Integer.parseInt(modifyWireTableAccess) != 0,
					Integer.parseInt(modifyPartsTableAccess) != 0,
					Integer.parseInt(modifyUserAccess) != 0,
					Integer.parseInt(deleteUserAccess) != 0)) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			}
		}
		else if (action.equalsIgnoreCase("Change Password")) {
			if (!Database.getInstance().changePassword(username, oldPassword, passwordToAdd)) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			}
		}
		else if (action.equalsIgnoreCase("Delete User")) {
			if (!Database.getInstance().deleteUser(username, userToDelete)) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			}
		}
		
		
	}

}
