package tran.allrecipes.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * @author Todd
 * A class to get the number of directions in a recipe.
 * The SQL statement associated with this returns a column, count, with the number of directions in the specified recipe.
 */
public class NumberRecipeDirectionsMapper implements RowMapper<Integer> {

	/** Allows access to the count column. */
	private String countColumn = "count";
	
	/**
	 * @param rs The current ingredient's contents, as a row of data.
	 * @param rowNum The current row.
	 * @return The number of directions, if null returns 0.
	 */
	public Integer mapRow(ResultSet rs, int rowNum) {
		// TODO Auto-generated method stub
		Integer recentDirectionId = 0;
		try {
			recentDirectionId =  rs.getInt(countColumn);
		}catch(SQLException e) {
			System.out.println("error mapping for recent id");
		}
		return recentDirectionId;
	}

}
