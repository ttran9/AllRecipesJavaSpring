package tran.allrecipes.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * @author Todd
 * A class that implements a method to get an inserted recipe direction's ID.
 */
public class RecentRecipeDirectionMapper implements RowMapper<Integer> {

	// direction ID column name.
	private String directionIdColumn = "directionId";
	
	/**
	 * @param rs An object holding a cursor to the current row.
	 * @param rowNum The current row of the corresponding SQL statement.
	 * @return The id of the recent inserted direction, null if there was a SQL error.
	 */
	public Integer mapRow(ResultSet rs, int rowNum) {
		// TODO Auto-generated method stub
		Integer directionId = null;
		try {
			directionId = rs.getInt(directionIdColumn);
		}
		catch(SQLException e) {
			System.out.println("error mapping the recipe direction id.");
			return null;
		}
		return directionId;
	}
}
