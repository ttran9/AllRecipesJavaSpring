package tran.allrecipes.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import tran.allrecipes.presentation.model.Ingredient;

/**
 * @author Todd
 * A class to populate a shopping list ingredient.
 */
public class ShoppingListIngredientsMapper implements RowMapper<Ingredient> {

	// column names.
	private String ingredientNameColumn;
	private String ingredientWholeNumberColumn;
	private String ingredientNumeratorColumn;
	private String ingredientDenominatorColumn;
	private String ingredientUnitColumn;
	private String ingredientIdColumn;
	private String ingredientTypeColumn;
	
	// allows the shopping ingredient name to be passed into JavaScript function(s).
	private String encoded_double_quote = "&quot;";
	private String encoded_single_quote = "&#8216;";

	/**
	 * @param ingredientNameColumn The ingredient name column.
	 * @param ingredientWholeNumberColumn The ingredient whole number column.
	 * @param ingredientNumeratorColumn The ingredient numerator column.
	 * @param ingredientDenominatorColumn The ingredient denominator column.
	 * @param ingredientUnitColumn The ingredient unit column.
	 * @param ingredientIdColumn The ingreident ID column.
	 * @param ingredientTypeColumn The ingredient type column.
	 */
	public ShoppingListIngredientsMapper(String ingredientNameColumn, String ingredientWholeNumberColumn,
		String ingredientNumeratorColumn, String ingredientDenominatorColumn, String ingredientUnitColumn,
		String ingredientIdColumn, String ingredientTypeColumn) {
		super();
		this.ingredientNameColumn = ingredientNameColumn;
		this.ingredientWholeNumberColumn = ingredientWholeNumberColumn;
		this.ingredientNumeratorColumn = ingredientNumeratorColumn;
		this.ingredientDenominatorColumn = ingredientDenominatorColumn;
		this.ingredientUnitColumn = ingredientUnitColumn;
		this.ingredientIdColumn = ingredientIdColumn;
		this.ingredientTypeColumn = ingredientTypeColumn;
	}

	/**
	 * @param rs An object holding a cursor to the current row.
	 * @param rowNum The current row number.
	 * @return An ingredient with populated data members, or null if there was a SQL error. 
	 */
	public Ingredient mapRow(ResultSet rs, int rowNum) {
		// TODO Auto-generated method stub
		Ingredient ingredient = new Ingredient();
		String ingredientName = null;
		Integer ingredientWholeNumber = null;
		Integer ingredientQuantityNumerator = null;
		Integer ingredientQuantityDenominator = null;
		String ingredientUnit = null;
		Integer ingredientId = null;
		String ingredientType = null;
		try {
			ingredientName = rs.getString(ingredientNameColumn);
			ingredientWholeNumber = rs.getInt(ingredientWholeNumberColumn);
			ingredientQuantityNumerator = rs.getInt(ingredientNumeratorColumn);
			ingredientQuantityDenominator = rs.getInt(ingredientDenominatorColumn);
			ingredientUnit = rs.getString(ingredientUnitColumn);
			ingredientId = rs.getInt(ingredientIdColumn);
			ingredientType = rs.getString(ingredientTypeColumn);
			
			if(ingredientQuantityDenominator == 0) throw new SQLException("stored fraction quantity has a denominator of 0.");
			
			if(ingredientName != null) {
				ingredient.setIngredientName(ingredientName);
				String encodedIngredientName = ingredientName.replaceAll("'", encoded_single_quote);
				encodedIngredientName = ingredientName.replaceAll("\"", encoded_double_quote);
				ingredient.setIngredientNameParsed(encodedIngredientName);
			}
			if(ingredientWholeNumber == 0 && ingredientQuantityNumerator == 0 && ingredientQuantityDenominator == 0) {
				throw new SQLException("all the shopping list ingredient quantities cannot be retrieved.");
			}
			else {
				// 1) whole number only.
				if(ingredientWholeNumber > 0 && ingredientQuantityNumerator == 0 && ingredientQuantityDenominator == 1)
					ingredient.setDisplayType("1");
				
				// 2) mixed fraction.
				else if(ingredientWholeNumber > 0 && ingredientQuantityNumerator < ingredientQuantityDenominator)
					ingredient.setDisplayType("2");
				
				// 3) fraction, less than one.
				else if(ingredientWholeNumber == 0 && ingredientQuantityNumerator < ingredientQuantityDenominator)
					ingredient.setDisplayType("3");
				ingredient.setWholeNumber(String.valueOf(ingredientWholeNumber));
				ingredient.setNumerator(String.valueOf(ingredientQuantityNumerator));
				ingredient.setDenominator(String.valueOf(ingredientQuantityDenominator));
			}
			if(ingredientUnit != null) ingredient.setIngredientUnit(ingredientUnit);
			if(ingredientId != null) ingredient.setIngredientID(ingredientId);
			if(ingredientType != null) ingredient.setIngredientType(ingredientType);
		}
		catch(SQLException e) {
			if(e.getMessage() != null) {
				System.out.println(e.getMessage());
			}
			else {
				System.out.println("could not retrieve shopping list ingredient");
			}
			return null;
		}
		return ingredient;
	}
}
