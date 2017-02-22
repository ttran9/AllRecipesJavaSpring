package tran.allrecipes.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * @author Todd
 * A class allowing for the retrieval of a user's list name, such as a shopping or pantry list name.
 */
public class UserListNameMapper implements RowMapper<String> {

	private String shoppingListNameColumn; // the name of the shoping list.
	private String exceptionMessage; // expected error if there is a SQL related error.
	
	/**
	 * @param listName The shopping list name column.
	 * @param SQLExceptionMessage An expected error message.
	 */
	public UserListNameMapper(String listName, String SQLExceptionMessage) {
		shoppingListNameColumn = listName;
		exceptionMessage = SQLExceptionMessage;
	}
	
	/**
	 * @param rs An object holding a cursor to the current row.
	 * @param rowNum The current row number.
	 * @return The name of the shopping list, or null if there was a SQL error.
	 */
	public String mapRow(ResultSet rs, int rowNum) {
		// TODO Auto-generated method stub
		String userListName = null;
		try {
			userListName = rs.getString(shoppingListNameColumn);
		}
		catch(SQLException e) {
			System.out.println(exceptionMessage);
			return null;
		}
		return userListName;
	}

}
