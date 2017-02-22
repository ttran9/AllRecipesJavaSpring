package tran.allrecipes.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * @author Todd
 * A class to allow for retrieval of the owner's name of a shopping or pantry list.
 */
public class UserListOwnerMapper implements RowMapper<String> {

	private String shoppingListUserOwnerName; // owner of shopping list column name.
	private String exceptionMessage; // expected message if the user name cannot be retrieeved.
	
	/**
	 * @param shoppingListUserOwnerName The column of the owner of the shopping list.
	 * @param exceptionMessage The expected message if there is a retrieval error.
	 */
	public UserListOwnerMapper(String shoppingListUserOwnerName, String exceptionMessage) {
		super();
		this.shoppingListUserOwnerName = shoppingListUserOwnerName;
		this.exceptionMessage = exceptionMessage;
	}

	/**
	 * @param rs An object holding a cursor to the current row.
	 * @param rowNum The current row number.
	 * @return The name of the owner of a shopping list, or null if there was a SQL error.
	 */
	public String mapRow(ResultSet rs, int rowNum) {
		// TODO Auto-generated method stub
		String userName = null;
		try {
			userName = rs.getString(shoppingListUserOwnerName);
		}
		catch(SQLException e) {
			System.out.println(exceptionMessage);
			return null;
		}
		return userName;
	}

}

