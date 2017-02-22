package tran.allrecipes.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * @author Todd
 * Retrieves if a user account is enabled or disabled.
 */
public class UserEnabledMapper implements RowMapper<Boolean> {

	// enabled column name.
	private String enabledColumn;

	/**
	 * @param enabledColumn The enabled column.
	 */
	public UserEnabledMapper(String enabledColumn) {
		super();
		this.enabledColumn = enabledColumn;
	}

	/**
	 * @param rs An object holding a cursor to the current row.
	 * @param rowNum The current row number.
	 * @return True if the account is not disabled, false if the account is disabled.
	 */
	public Boolean mapRow(ResultSet rs, int rowNum) {
		// TODO Auto-generated method stub
		boolean isUserEnabled = false;
		try {
			if(rs.getObject(enabledColumn) != null) isUserEnabled = rs.getBoolean(enabledColumn);
		}
		catch(SQLException e) {
			System.out.println("could not map and retrieve user enabled status");
		}
		return isUserEnabled;
	}
	
	
}
