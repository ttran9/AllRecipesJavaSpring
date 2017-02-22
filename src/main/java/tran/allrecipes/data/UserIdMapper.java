package tran.allrecipes.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * @author Todd
 * Retrieves the ID associated with a user account.
 */
public class UserIdMapper implements RowMapper<Integer> {
	
	// column name.
	private String userIdColumn;
	 
	/**
	 * @param userIdColumn The user ID column.
	 */
	public UserIdMapper(String userIdColumn) {
		super();
		this.userIdColumn = userIdColumn;
	}

	/**
	 * @param rs An object holding a cursor to the current row.
	 * @param rowNum The current row number.
	 * @return The ID of the user account, or null if there was a SQL error.
	 */
	public Integer mapRow(ResultSet rs, int rowNum) {
		// TODO Auto-generated method stub
		Integer usersId = null;
		try {
			if(rs.getObject(userIdColumn) != null) 
				usersId = rs.getInt(userIdColumn);
		}
		catch(SQLException e) {
			System.out.println("error mapping and retriving user's id.");
			return null;
		}
		return usersId;
	}

}
