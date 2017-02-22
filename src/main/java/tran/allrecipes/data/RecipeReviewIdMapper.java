package tran.allrecipes.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * @author Todd
 * A class to get the ID of a recipe review.
 */
public class RecipeReviewIdMapper implements RowMapper<Integer> {
	
	// column name.
	private String reviewIdColumn = "reviewId";

	/**
	 * @param rs An object holding a cursor to the current row.
	 * @param rowNum The current row number
	 * @return The id of a recipe review, or nul if there was a SQL error.
	 */
	public Integer mapRow(ResultSet rs, int rowNum) {
		// TODO Auto-generated method stub
		Integer reviewId = null;
		try {
			reviewId = rs.getInt(reviewIdColumn);
		}
		catch(SQLException e ) {
			System.out.println("error mapping recipe review.");
			return null;
		}
		return reviewId;
	}

}
