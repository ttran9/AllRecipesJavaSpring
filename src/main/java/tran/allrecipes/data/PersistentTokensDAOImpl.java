package tran.allrecipes.data;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.stereotype.Repository;

/**
 * @author Todd
 * This class allows access to the persistent_logins table to extract data.
 */
@Repository
public class PersistentTokensDAOImpl implements PersistentTokensDAO {

	/** The persistent logins table name. */
	private static final String PERSISTENT_LOGINS_TABLE = "persistent_logins";
	/** The user name column. */
	private static final String USER_NAME_COLUMN = "username";
	/** Statement to test if a specified user has a persistent token. */
	private static final String GET_USER_TOKENS = "SELECT * FROM " + PERSISTENT_LOGINS_TABLE + " WHERE " + USER_NAME_COLUMN + " = ?";
	
	/** A data member to get the database driver. */
	private DataSource dataSource;
	/** A data member to perform SQL related queries such as insertions, removals, and updates. */
	private JdbcTemplate jdbcTemplateObject;
	/**
	 * @param dataSource An object holding credentials.
	 * Sets the jdbc template object allowing it to connect to a specified database.
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplateObject = new JdbcTemplate(this.dataSource);
	}
	
	/**
	 * @param userName The name of the user to get tokens for.
	 * @return A list of persistent tokens if any exists, a list with null tokens if not.
	 */
	public List<PersistentRememberMeToken> getPersistentTokens(String userName) {
		// TODO Auto-generated method stub
		List<PersistentRememberMeToken> persistentTokens = null;
		try {
			persistentTokens = jdbcTemplateObject.query(GET_USER_TOKENS, new Object[]{userName}, new PersistentRememberMeTokenMapper());
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("error retrieving persistent login token(s).");
		}
		return persistentTokens;
	}

}
