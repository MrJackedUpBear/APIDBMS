

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import token.TokenStore;

import java.io.IOException;
import java.util.Base64;

import database.Database;

/**
 * Servlet implementation class AuthServlet
 */
@WebServlet("")
public class AuthServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AuthServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String authHeader = request.getHeader("authorization");
		
		if (authHeader == null) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		String encodedAuth = authHeader.substring(authHeader.indexOf(' ') + 1);
		String decodedAuth = new String(Base64.getDecoder().decode(encodedAuth));
		String username = decodedAuth.substring(0, decodedAuth.indexOf(":"));
		String password = decodedAuth.substring(decodedAuth.indexOf(":") + 1, decodedAuth.length());
		
		String dbPassword = Database.getInstance().getPassword(username);
		
		if (!dbPassword.equalsIgnoreCase(password)) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		
		String token = TokenStore.getInstance().putToken(username);
		
		response.getOutputStream().print(token);
	}

}
