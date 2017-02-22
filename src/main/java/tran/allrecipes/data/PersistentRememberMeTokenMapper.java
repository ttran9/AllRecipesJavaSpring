package tran.allrecipes.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
/**
 * @author Todd
 * A class to get the persistent logins row(s) for a user.
 */
public class PersistentRememberMeTokenMapper implements RowMapper<PersistentRememberMeToken> {

	/** The user name column. */
	private static final String USER_NAME_COLUMN = "username";
	/* The series column. */
	private static final String SERIES_COLUMN = "series";
	/** The token column. */
	private static final String TOKEN_COLUMN = "token";
	/** The last_used column. */
	private static final String LAST_USED_COLUMN = "last_used";
	/** A message indicating that one of the fields could not be retrieved. */
	private static final String COULD_NOT_GET_PERSISTENT_TOKEN_FIELD_MESSAGE = "could not retrieve persistent token field(s)!";
	
	/**
	 * @param rs An object holding a cursor to the current row.
	 * @param rowNum The current row number.
	 * @return A PersistentRememberMeToken object with populated data members, or null if there was a SQL error.
	 */
	public PersistentRememberMeToken mapRow(ResultSet rs, int rowNum) {
		// TODO Auto-generated method stub
		PersistentRememberMeToken persistentToken = null;
		String username = null;
		String series = null;
		String tokenValue = null;
		Date date = null;
		try {
			username = rs.getString(USER_NAME_COLUMN);
			series = rs.getString(SERIES_COLUMN);
			tokenValue = rs.getString(TOKEN_COLUMN);
			date = rs.getDate(LAST_USED_COLUMN);
			if(username != null && series != null && tokenValue != null && date != null) {
				persistentToken = new PersistentRememberMeToken(username, series, tokenValue, date);
			}
			else {
				throw new SQLException(COULD_NOT_GET_PERSISTENT_TOKEN_FIELD_MESSAGE);
			}
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}
		return persistentToken;
	}

}
