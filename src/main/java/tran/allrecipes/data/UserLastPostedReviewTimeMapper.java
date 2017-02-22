package tran.allrecipes.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.RowMapper;

/**
 * @author Todd
 * Retrieves the last time a user posted a review.
 */
public class UserLastPostedReviewTimeMapper implements RowMapper<LocalDateTime>  {

	private String lastPostedReviewTimeColumn; // the last posted review time column.
	
	/**
	 * @param lastPostedReviewTimeColumn The last posted review time column.
	 */
	public UserLastPostedReviewTimeMapper(String lastPostedReviewTimeColumn) {
		super();
		this.lastPostedReviewTimeColumn = lastPostedReviewTimeColumn;
	}

	/**
	 * @param rs An object holding a cursor to the current row.
	 * @param rowNum The current row number.
	 * @return The time the user last posted a review, or null if there was a retrieval error.
	 */
	public LocalDateTime mapRow(ResultSet rs, int rowNum) {
		// TODO Auto-generated method stub
		LocalDateTime lastPostedReviewTime = null;
		try {
			if(rs.getTimestamp(lastPostedReviewTimeColumn) != null) 
				lastPostedReviewTime = rs.getTimestamp(lastPostedReviewTimeColumn).toLocalDateTime();
		}
		catch(SQLException e) {
			System.out.println("cannot map and retrieve the last time a user reviewed a recipe.");
			return null;
		}
		return lastPostedReviewTime;
	}

}
