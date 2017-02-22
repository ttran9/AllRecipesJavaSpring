package tran.allrecipes.data;

import java.util.List;

import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

/**
 * @author Todd
 * Enforces that inheriting classes have an implementation to get a persistent token of a user.
 */
public interface PersistentTokensDAO {
	/**
	 * @param userName The name of the user to retrieve tokens for.
	 * @return Returns a list of persistent login tokens for a specified user.
	 */
	public List<PersistentRememberMeToken> getPersistentTokens(String userName);
}
