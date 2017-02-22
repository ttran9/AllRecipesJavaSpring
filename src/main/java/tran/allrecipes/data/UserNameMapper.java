package tran.allrecipes.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * @author Todd
 * Gets the user name.
 */
public class UserNameMapper implements RowMapper<String> {

	private String userNameColumn; // user name column.
	
	/**
	 * @param userNameColumn The user name column.
	 */
	public UserNameMapper(String userNameColumn) {
		super();
		this.userNameColumn = userNameColumn;
	}

	/**
	 * @param rs An object holding a cursor to the current row.
	 * @param rowNum The current row number.
	 * @return The user name, or null if there was a SQL error.
	 */
	public String mapRow(ResultSet rs, int rowNum) {
		// TODO Auto-generated method stub
		String userName = null;
		String getUserName = null;
		try {
			getUserName = rs.getString(userNameColumn);
			if(getUserName != null) userName = getUserName;
		}
		catch(SQLException e) {
			System.out.println("error mapping and getting userName");
		}
		return userName;
	}

}
