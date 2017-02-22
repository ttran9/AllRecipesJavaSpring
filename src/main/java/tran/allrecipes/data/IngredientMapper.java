package tran.allrecipes.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * @author Todd
 * Mapper to get both an ingredient from a shopping ingredient list or a recipe ingredient list.
 */
public class IngredientMapper implements RowMapper<Integer> {

	/** String to reference the ingredient ID column in the database. */
	private String ingredientIdColumn;
	
	/** Sets the ingredient ID column. */
	public IngredientMapper(String ingredientIdColumn) {
		super();
		this.ingredientIdColumn = ingredientIdColumn;
	}

	/**
	 * @param rs The current ingredient's contents, as a row of data.
	 * @param rowNum The current row.
	 * @return The ingredient's id, if null returns 0.
	 */
	public Integer mapRow(ResultSet rs, int rowNum) {
		// TODO Auto-generated method stub
		Integer ingredientId = 0;
		try {
			ingredientId = rs.getInt(ingredientIdColumn);
		}
		catch(SQLException e) {
			System.out.println("cannot get ingredient id the requested ingredient.");
		}
		return ingredientId;
	}

}
