package tran.allrecipes.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.RowMapper;

/**
 * @author Todd
 * Retrieves the time that a user last posted at.
 */
public class UserLastPostedRecipeTimeMapper implements RowMapper<LocalDateTime> {

	// column name of last posted recipe.
	private String lastPostedRecipeTimeColumn;
	
	/**
	 * @param lastPostedRecipeTimeColumn The last posted recipe time column.
	 */
	public UserLastPostedRecipeTimeMapper(String lastPostedRecipeTimeColumn) {
		super();
		this.lastPostedRecipeTimeColumn = lastPostedRecipeTimeColumn;
	}

	/**
	 * @param rs An object holding a cursor to the current row.
	 * @param rowNum The current row number.
	 * @return The time the user last posted at, or null if there was a SQL error.
	 */
	public LocalDateTime mapRow(ResultSet rs, int rowNum) {
		// TODO Auto-generated method stub
		LocalDateTime lastPostedRecipeTime = null;
		try {
			if(rs.getTimestamp(lastPostedRecipeTimeColumn) != null) 
				lastPostedRecipeTime = rs.getTimestamp(lastPostedRecipeTimeColumn).toLocalDateTime();
		}
		catch(SQLException e) {
			System.out.println("cannot map and retrieve the last time a user posted a recipe.");
		}
		return lastPostedRecipeTime;
	}

}
