package tran.allrecipes.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * @author Todd
 * Gets the user's password.
 */
public class UserPasswordMapper implements RowMapper<String> {

	private String userPasswordColumn; // the user password column.

	/**
	 * @param userPasswordColumn Column holding the user's encrypted password.
	 */
	public UserPasswordMapper(String userPasswordColumn) {
		super();
		this.userPasswordColumn = userPasswordColumn;
	}

	/**
	 * @param rs An object holding a cursor to the current row.
	 * @param rowNum The current row number.
	 * @return The user's encrypted password or null if there was a SQL error.
	 */
	public String mapRow(ResultSet rs, int rowNum) {
		// TODO Auto-generated method stub
		String userPassword = null;
		try {
			if(rs.getString(userPasswordColumn) != null) userPassword = rs.getString(userPasswordColumn);
		}
		catch(SQLException e) {
			System.out.println("could not map and retrieve user password");
			return null;
		}
		return userPassword;
	}
}
