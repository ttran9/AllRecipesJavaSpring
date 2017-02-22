package tran.allrecipes.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import tran.allrecipes.presentation.model.PantryIngredient;

/**
 * @author Todd
 * A class to grab a row of data and set a PantryIngredient's data members.
 */
public class PantryListIngredientMapper implements RowMapper<PantryIngredient> {

	// column names.
	private String ingredientNameColumn;
	private String ingredientWholeNumberColumn;
	private String ingredientNumeratorColumn;
	private String ingredientDenominatorColumn;
	private String ingredientThresholdWholeNumberColumn;
	private String ingredientThresholdNumeratorColumn;
	private String ingredientThresholdDenominatorColumn;
	private String ingredientUnitColumn;
	private String ingredientIdColumn;
	private String ingredientTypeColumn;
	private String ingredientCanBeAddedToShoppingListColumn;
	private String isThresholdHitColumn;
	
	// These two data members allow strings to be passed into JavaScript functions.
	private String encoded_double_quote = "&quot;";
	private String encoded_single_quote = "&#8216;";
	
	/**
	 * @param ingredientNameColumn The ingredient name column.
	 * @param ingredientWholeNumberColumn The whole number column.
	 * @param ingredientNumeratorColumn The numerator column.
	 * @param ingredientDenominatorColumn The denominator column.
	 * @param ingredientThresholdWholeNumberColumn The threshold whole number column.
	 * @param ingredientThresholdNumeratorColumn The threshold numerator column.
	 * @param ingredientThresholdDenominatorColumn The threshold denominator column.
	 * @param ingredientUnitColumn The unit column.
	 * @param ingredientIdColumn The ID column.
	 * @param ingredientTypeColumn The type column.
	 * @param ingredientCanBeAddedToShoppingListColumn The column to determine if the ingredient can be added to the shopping list.
	 * @param isThresholdHitColumn The column to determine if the threshold value has been hit.
	 */
	public PantryListIngredientMapper(String ingredientNameColumn, String ingredientWholeNumberColumn,
		String ingredientNumeratorColumn, String ingredientDenominatorColumn,
		String ingredientThresholdWholeNumberColumn, String ingredientThresholdNumeratorColumn,
		String ingredientThresholdDenominatorColumn, String ingredientUnitColumn, String ingredientIdColumn,
		String ingredientTypeColumn, String ingredientCanBeAddedToShoppingListColumn, String isThresholdHitColumn) {
		super();
		this.ingredientNameColumn = ingredientNameColumn;
		this.ingredientWholeNumberColumn = ingredientWholeNumberColumn;
		this.ingredientNumeratorColumn = ingredientNumeratorColumn;
		this.ingredientDenominatorColumn = ingredientDenominatorColumn;
		this.ingredientThresholdWholeNumberColumn = ingredientThresholdWholeNumberColumn;
		this.ingredientThresholdNumeratorColumn = ingredientThresholdNumeratorColumn;
		this.ingredientThresholdDenominatorColumn = ingredientThresholdDenominatorColumn;
		this.ingredientUnitColumn = ingredientUnitColumn;
		this.ingredientIdColumn = ingredientIdColumn;
		this.ingredientTypeColumn = ingredientTypeColumn;
		this.ingredientCanBeAddedToShoppingListColumn = ingredientCanBeAddedToShoppingListColumn;
		this.isThresholdHitColumn = isThresholdHitColumn;
	}
	
	/**
	 * @param rs An object holding a cursor to the current row.
	 * @param rowNum The current row number.
	 * @return A pantry ingredient with populated data members, or null if there was a SQL error.
	 */
	public PantryIngredient mapRow(ResultSet rs, int rowNum) {
		// TODO Auto-generated method stub
		PantryIngredient ingredient = new PantryIngredient();
		String ingredientName = null;
		// ingredient quantities should be integers for checking purposes.
		Integer ingredientWholeNumber = null;
		Integer ingredientQuantityNumerator = null;
		Integer ingredientQuantityDenominator = null;
		String ingredientThresholdWholeNumber = null;
		String ingredientThresholdNumerator = null;
		String ingredientThresholdDenominator = null;
		String ingredientUnit = null;
		Integer ingredientId = null;
		String ingredientType = null;
		Boolean canIngredientBeAddedToShoppingList = null;
		Boolean hasThresholdBeenHit = null;
		try {
			ingredientName = rs.getString(ingredientNameColumn);
			ingredientWholeNumber = rs.getInt(ingredientWholeNumberColumn);
			ingredientQuantityNumerator = rs.getInt(ingredientNumeratorColumn);
			ingredientQuantityDenominator = rs.getInt(ingredientDenominatorColumn);
			
			
			ingredientThresholdWholeNumber = rs.getString(ingredientThresholdWholeNumberColumn);
			ingredientThresholdNumerator = rs.getString(ingredientThresholdNumeratorColumn);
			ingredientThresholdDenominator = rs.getString(ingredientThresholdDenominatorColumn);
			
			ingredientUnit = rs.getString(ingredientUnitColumn);
			ingredientId = rs.getInt(ingredientIdColumn);
			ingredientType = rs.getString(ingredientTypeColumn);
			
			canIngredientBeAddedToShoppingList = rs.getBoolean(ingredientCanBeAddedToShoppingListColumn);
			hasThresholdBeenHit = rs.getBoolean(isThresholdHitColumn);
			
			if(ingredientQuantityDenominator == 0) throw new SQLException("stored fraction quantity has a denominator of 0.");
			
			if(Integer.parseInt(ingredientThresholdDenominator) <= 0) {
				throw new SQLException("threshold denominator cannot be zero!");
			}

			if(ingredientName != null) {
				ingredient.setIngredientName(ingredientName);
				String encodedIngredientName = ingredientName.replaceAll("'", encoded_single_quote);
				encodedIngredientName = ingredientName.replaceAll("\"", encoded_double_quote);
				ingredient.setIngredientNameParsed(encodedIngredientName);
			}
			if(ingredientWholeNumber == 0 && ingredientQuantityNumerator == 0 && ingredientQuantityDenominator == 0) {
				throw new SQLException("all the pantry ingredient quantities cannot be retrieved.");
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
			if(ingredientThresholdWholeNumber != null) ingredient.setThresholdWholeNumber(ingredientThresholdWholeNumber);
			if(ingredientThresholdNumerator != null) ingredient.setThresholdNumerator(ingredientThresholdNumerator);
			if(ingredientThresholdDenominator != null) ingredient.setThresholdDenominator(ingredientThresholdDenominator);
			
			if(ingredientUnit != null) ingredient.setIngredientUnit(ingredientUnit);
			if(ingredientId != null) ingredient.setIngredientID(ingredientId);
			if(ingredientType != null) ingredient.setIngredientType(ingredientType);
			
			// guaranteed to be true or false, even if it is null it will default to false.
			ingredient.setCanIngredientBeAddedToShoppingList(canIngredientBeAddedToShoppingList);
			ingredient.setHasIngredientThresholdBeenHit(hasThresholdBeenHit);
		}
		catch(SQLException e) {
			if(e.getMessage() != null) {
				System.out.println(e.getMessage());
			}
			return null;
		}
		return ingredient;
	}

}
