package token;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TokenStore {
	private Map<String, TokenData> tokenMap = new HashMap<>();
	
	private static TokenStore instance = new TokenStore();
	
	public static TokenStore getInstance() {
		return instance;
	}
	
	private TokenStore() {
		
	}
	
	public String putToken(String username) {
		String token = UUID.randomUUID().toString();
		tokenMap.put(token, new TokenData(username));
		
		return token;
	}
	
	public String getUsername(String token) {
		if (tokenMap.containsKey(token)) {
			if (tokenMap.get(token).expirationTime > System.currentTimeMillis()){
				return tokenMap.get(token).username;
			}
			else {
				tokenMap.remove(token);
			}
		}
		return null;
	}
}

class TokenData{
	String username;
	long expirationTime;
	
	TokenData(String username) {
		this.username = username;
		
		expirationTime = System.currentTimeMillis() + 15 * 60 * 1000;
	}
	
}
