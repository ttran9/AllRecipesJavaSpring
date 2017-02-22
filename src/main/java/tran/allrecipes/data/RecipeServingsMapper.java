package tran.allrecipes.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * @author Todd
 * A class to get the number of servings of a recipe.
 */
public class RecipeServingsMapper implements RowMapper<Integer> {

	// column name.
	private String numberServingsColumn;
	
	/**
	 * @param numberServingsColumn The number of servings column name.
	 */
	public RecipeServingsMapper(String numberServingsColumn) {
		super();
		this.numberServingsColumn = numberServingsColumn;
	}

	/**
	 * @param rs An object holding a cursor to he current row.
	 * @param rowNum The current row number.
	 * @return The number of servings of a recipe, or null if there was a SQL error.
	 */
	public Integer mapRow(ResultSet rs, int rowNum) {
		// TODO Auto-generated method stub
		Integer numberOfServings = null;
		try {
			if(rs.getObject(numberServingsColumn) != null)
				numberOfServings = rs.getInt(numberServingsColumn);
		}
		catch(SQLException e) {
			System.out.println("error mapping and retrieving recipe servings");
			return null;
		}
		return numberOfServings;
	}

}
