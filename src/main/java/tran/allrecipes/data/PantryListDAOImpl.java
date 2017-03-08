package tran.allrecipes.data;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tran.allrecipes.presentation.model.PantryIngredient;

@Repository
public class PantryListDAOImpl implements GenericUserListDAO, PantryListShoppingListDAO {

	// table names and column names. 
	private static final String PANTRY_LIST_TABLE = "PantryList";
	private static final String PANTRY_LIST_NAME_COLUMN = "pantryListName";
	private static final String PANTRY_LIST_OWNER_COLUMN = "pantryListOwner";
	
	private static final String PANTRY_LIST_INGREDIENTS_TABLE = "PantryListIngredients";
	private static final String PANTRY_LIST_INGREDIENT_ID_COLUMN = "ingredientId";
	private static final String PANTRY_LIST_INGREDIENT_NAME_COLUMN = "ingredientName";
	private static final String PANTRY_LIST_INGREDIENT_NUMBER_COLUMN = "ingredientQNumber";
	private static final String PANTRY_LIST_INGREDIENT_NUMERATOR_COLUMN = "ingredientQNumerator";
	private static final String PANTRY_LIST_INGREDIENT_DENOMINATOR_COLUMN = "ingredientQDenominator";
	private static final String PANTRY_LIST_INGREDIENT_THRESHOLD_NUMBER_COLUMN = "ingredientThresholdQNumber";
	private static final String PANTRY_LIST_INGREDIENT_THRESHOLD_NUMERATOR_COLUMN = "ingredientThresholdQNumerator";
	private static final String PANTRY_LIST_INGREDIENT_THRESHOLD_DENOMINATOR_COLUMN = "ingredientThresholdQDenominator";
	private static final String PANTRY_LIST_INGREDIENT_UNIT_COLUMN = "ingredientUnit";
	private static final String PANTRY_LIST_INGREDIENT_TYPE_COLUMN = "ingredientType";
	private static final String PANTRY_LIST_INGREDIENT_CAN_BE_ADDED_TO_SHOPPING_LIST_COLUMN = "ingredientCanBeAddedToShoppingList";
	private static final String PANTRY_LIST_INGREDIENT_HAS_THRESHOLD_BEEN_HIT_COLUMN = "isThresholdHit";
	
	// notification messages.
	private static final String GET_PANTRY_LIST_NAME_EXCEPTION_MESSAGE = "cannot map and retrieve the pantry list name.";
	private static final String GET_PANTRY_LIST_OWNER_NAME_EXCEPTION_MESSAGE = "cannot map and retrieve the pantry list's owner.";
	
	// SQL statements. 
	private static final String GET_PANTRY_LIST_NAME = "SELECT " + PANTRY_LIST_NAME_COLUMN + " FROM " + PANTRY_LIST_TABLE + " WHERE " + PANTRY_LIST_OWNER_COLUMN + " = ?";
	private static final String GET_PANTRY_LIST_OWNER = "SELECT " + PANTRY_LIST_OWNER_COLUMN + " FROM " + PANTRY_LIST_TABLE + " WHERE " + PANTRY_LIST_NAME_COLUMN + " = ?";
	private static final String INSERT_PANTRY_LIST = "INSERT INTO " + PANTRY_LIST_TABLE + "(" + PANTRY_LIST_NAME_COLUMN + ", " + PANTRY_LIST_OWNER_COLUMN + ") VALUES(?, ?)";
	private static final String DELETE_PANTRY_LIST = "DELETE" + " FROM " + PANTRY_LIST_TABLE + " WHERE " + PANTRY_LIST_NAME_COLUMN + " = ? AND " + PANTRY_LIST_OWNER_COLUMN + " = ?";
	
	private static final String GET_PANTRY_LIST_INGREDIENTS_QUERY = "SELECT * FROM " + PANTRY_LIST_INGREDIENTS_TABLE + " WHERE " + PANTRY_LIST_NAME_COLUMN + " = ?";
	private static final String GET_PANTRY_LIST_SINGLE_INGREDIENT_QUERY = "SELECT * FROM " + PANTRY_LIST_INGREDIENTS_TABLE + " WHERE " + PANTRY_LIST_NAME_COLUMN + " = ? AND " + PANTRY_LIST_INGREDIENT_NAME_COLUMN + " = ?";
	private static final String INSERT_PANTRY_LIST_INGREDIENTS = "INSERT INTO " + PANTRY_LIST_INGREDIENTS_TABLE + "(" + PANTRY_LIST_INGREDIENT_NAME_COLUMN + ", " + PANTRY_LIST_INGREDIENT_NUMBER_COLUMN + ", " + PANTRY_LIST_INGREDIENT_NUMERATOR_COLUMN 
	+ ", " + PANTRY_LIST_INGREDIENT_DENOMINATOR_COLUMN + ", " + PANTRY_LIST_INGREDIENT_THRESHOLD_NUMBER_COLUMN + ", " + PANTRY_LIST_INGREDIENT_THRESHOLD_NUMERATOR_COLUMN + ", " + PANTRY_LIST_INGREDIENT_THRESHOLD_DENOMINATOR_COLUMN + ", " + 
	PANTRY_LIST_INGREDIENT_UNIT_COLUMN + ", " + PANTRY_LIST_INGREDIENT_TYPE_COLUMN + ", " + PANTRY_LIST_NAME_COLUMN + ", " + PANTRY_LIST_INGREDIENT_CAN_BE_ADDED_TO_SHOPPING_LIST_COLUMN + ", " + PANTRY_LIST_INGREDIENT_HAS_THRESHOLD_BEEN_HIT_COLUMN + ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String REMOVE_PANTRY_LIST_INGREDIENT = "DELETE FROM " + PANTRY_LIST_INGREDIENTS_TABLE + " WHERE " + PANTRY_LIST_INGREDIENT_ID_COLUMN + " = ?";
	private static final String REMOVE_PANTRY_LIST_INGREDIENTS = "DELETE FROM " + PANTRY_LIST_INGREDIENTS_TABLE + " WHERE " + PANTRY_LIST_NAME_COLUMN + " = ?";
	private static final String UPDATE_PANTRY_LIST_INGREDIENTS_QUANTITY = "UPDATE " + PANTRY_LIST_INGREDIENTS_TABLE + " SET "  + PANTRY_LIST_INGREDIENT_NUMBER_COLUMN + " = ?, " + PANTRY_LIST_INGREDIENT_NUMERATOR_COLUMN + " = ?, "
	+ PANTRY_LIST_INGREDIENT_DENOMINATOR_COLUMN + " = ? WHERE " + PANTRY_LIST_INGREDIENT_ID_COLUMN + " = ?";
	private static final String UPDATE_PANTRY_LIST_INGREDIENT = "UPDATE " + PANTRY_LIST_INGREDIENTS_TABLE + " SET "  + PANTRY_LIST_INGREDIENT_NUMBER_COLUMN + " = ?, " + PANTRY_LIST_INGREDIENT_NUMERATOR_COLUMN + " = ?, "
	+ PANTRY_LIST_INGREDIENT_DENOMINATOR_COLUMN + " = ?, " + PANTRY_LIST_INGREDIENT_THRESHOLD_NUMBER_COLUMN + " = ?, "  + PANTRY_LIST_INGREDIENT_THRESHOLD_NUMERATOR_COLUMN + " = ?, " + PANTRY_LIST_INGREDIENT_THRESHOLD_DENOMINATOR_COLUMN + " = ?, " 
	+ PANTRY_LIST_INGREDIENT_UNIT_COLUMN + " = ?, " + PANTRY_LIST_INGREDIENT_TYPE_COLUMN + " = ?, " + PANTRY_LIST_INGREDIENT_CAN_BE_ADDED_TO_SHOPPING_LIST_COLUMN + " = ?, " + PANTRY_LIST_INGREDIENT_HAS_THRESHOLD_BEEN_HIT_COLUMN + " = ?"+ " WHERE " + PANTRY_LIST_INGREDIENT_ID_COLUMN + " = ?";
	private static final String GET_MOST_RECENT_PANTRY_LIST_INGREDIENT_ID = "SELECT " + PANTRY_LIST_INGREDIENT_ID_COLUMN + " FROM " + PANTRY_LIST_INGREDIENTS_TABLE + " WHERE " + PANTRY_LIST_INGREDIENT_NAME_COLUMN + " = ? AND " +
	PANTRY_LIST_NAME_COLUMN + " = ?";
	private static final String SET_CAN_BE_TRANSFERRED_TO_SHOPPING_LIST = "UPDATE " + PANTRY_LIST_INGREDIENTS_TABLE + " SET " + PANTRY_LIST_INGREDIENT_CAN_BE_ADDED_TO_SHOPPING_LIST_COLUMN + " = ? WHERE " + PANTRY_LIST_INGREDIENT_ID_COLUMN + " = ?";
	private static final String UPDATE_HAS_THRESHOLD_BEEN_HIT = "UPDATE " + PANTRY_LIST_INGREDIENTS_TABLE + " SET " + PANTRY_LIST_INGREDIENT_HAS_THRESHOLD_BEEN_HIT_COLUMN + " = ? WHERE " + PANTRY_LIST_INGREDIENT_ID_COLUMN + " = ?";

	/** A data member to get the database driver. */
	private DataSource dataSource;
	/** A data member to perform SQL related queries such as insertions, removals, and updates. */
	private JdbcTemplate jdbcTemplateObject;
	
	/**
	 * @param dataSource The Datasource object to be set with properties such as: driver class name, url, username, and password, 
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplateObject = new JdbcTemplate(this.dataSource);
	}
	
	/**
	 * @param userOwnerOfInventoryList The owner of the pantry list.
	 * @return Retrieves the name of the pantry list of the specified owner.
	 */
	public String getListName(String userOwnerOfInventoryList) {
		// TODO Auto-generated method stub
		String pantryListName = null;
		try {
			pantryListName = jdbcTemplateObject.queryForObject(GET_PANTRY_LIST_NAME, new Object[]{userOwnerOfInventoryList}, new UserListNameMapper(PANTRY_LIST_NAME_COLUMN, GET_PANTRY_LIST_NAME_EXCEPTION_MESSAGE));
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("cannot retrieve pantry list name for user: " + userOwnerOfInventoryList);
		}
		return pantryListName;
	}
	
	/**
	 * @param shoppingListName The name of the pantry list.
	 * @return The owner of the requested pantry list.
	 */
	public String getUserOwnerOfList(String pantryName) {
		// TODO Auto-generated method stub
		String userName = null;
		try {
			userName = jdbcTemplateObject.queryForObject(GET_PANTRY_LIST_OWNER, new Object[]{pantryName}, new UserListOwnerMapper(PANTRY_LIST_OWNER_COLUMN, GET_PANTRY_LIST_OWNER_NAME_EXCEPTION_MESSAGE));
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("cannot retrieve pantry list's owner for pantry list: " + pantryName);
		}
		return userName;
	}

	/**
	 * @param pantryName The name of the pantry list.
	 * @param userOwnerOfInventoryList The name of the user to create the pantry list for.
	 * @return 1 if the pantry list has been created, otherwise the pantry list was not properly created.
	 */
	public int addList(String pantryName, String userOwnerOfInventoryList) {
		// TODO Auto-generated method stub
		int returnCode = -1;
		try {
			returnCode = jdbcTemplateObject.update(INSERT_PANTRY_LIST, new Object[] {pantryName, userOwnerOfInventoryList});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			throw new DataIntegrityViolationException("could not add pantry list for user: " + userOwnerOfInventoryList);
		}
		return returnCode;
		
	}
	
	/**
	 * @param shoppingListName The name of the shopping list to remove.
	 * @param userName The name of the user that created the shopping list.
	 * @return 1 if the pantry list has been deleted, otherwise the shopping list isn't deleted.
	 */
	public int removeList(String pantryName, String userOwnerOfInventoryList) {
		// TODO Auto-generated method stub
		// unused but implemented (may remove in future version).
		int returnCode = -1;
		try {
			returnCode = jdbcTemplateObject.update(DELETE_PANTRY_LIST, new Object[] {pantryName, userOwnerOfInventoryList});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not remove pantry list for user: " + userOwnerOfInventoryList);
		}
		return returnCode;
		
	}

	/**
	 * @param pantryName The name of the pantry list.
	 * @return A pantry list with ingredients, if an error occurs this will return null.
	 */
	public List<PantryIngredient> getListIngredients(String pantryName) {
		// TODO Auto-generated method stub
		List<PantryIngredient> pantryIngredientList = null;
		try {
			pantryIngredientList = jdbcTemplateObject.query(GET_PANTRY_LIST_INGREDIENTS_QUERY, new Object[]{pantryName}, new PantryListIngredientMapper(PANTRY_LIST_INGREDIENT_NAME_COLUMN, PANTRY_LIST_INGREDIENT_NUMBER_COLUMN, PANTRY_LIST_INGREDIENT_NUMERATOR_COLUMN, PANTRY_LIST_INGREDIENT_DENOMINATOR_COLUMN, 
				PANTRY_LIST_INGREDIENT_THRESHOLD_NUMBER_COLUMN, PANTRY_LIST_INGREDIENT_THRESHOLD_NUMERATOR_COLUMN, PANTRY_LIST_INGREDIENT_THRESHOLD_DENOMINATOR_COLUMN, PANTRY_LIST_INGREDIENT_UNIT_COLUMN, PANTRY_LIST_INGREDIENT_ID_COLUMN, PANTRY_LIST_INGREDIENT_TYPE_COLUMN, PANTRY_LIST_INGREDIENT_CAN_BE_ADDED_TO_SHOPPING_LIST_COLUMN,
				PANTRY_LIST_INGREDIENT_HAS_THRESHOLD_BEEN_HIT_COLUMN));
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not get pantry list contents for " + pantryName);
			return null;
		}
		return pantryIngredientList;
	}

	/**
	 * @param pantryName The name of the pantry that this ingredient is a part of.
	 * @param ingredientName The name of the pantry ingredient for retrieval.
 	 * @return The pantry ingredient's object, or null if it cannot be retrieved.
	 */
	public PantryIngredient getSingleIngredient(String pantryName, String ingredientName) {
		// TODO Auto-generated method stub
		PantryIngredient pantryIngredient = null;
		try {
			pantryIngredient = jdbcTemplateObject.queryForObject(GET_PANTRY_LIST_SINGLE_INGREDIENT_QUERY, new Object[]{pantryName, ingredientName}, new PantryListIngredientMapper(PANTRY_LIST_INGREDIENT_NAME_COLUMN, PANTRY_LIST_INGREDIENT_NUMBER_COLUMN, PANTRY_LIST_INGREDIENT_NUMERATOR_COLUMN, PANTRY_LIST_INGREDIENT_DENOMINATOR_COLUMN, 
			PANTRY_LIST_INGREDIENT_THRESHOLD_NUMBER_COLUMN, PANTRY_LIST_INGREDIENT_THRESHOLD_NUMERATOR_COLUMN, PANTRY_LIST_INGREDIENT_THRESHOLD_DENOMINATOR_COLUMN, PANTRY_LIST_INGREDIENT_UNIT_COLUMN, PANTRY_LIST_INGREDIENT_ID_COLUMN, PANTRY_LIST_INGREDIENT_TYPE_COLUMN, PANTRY_LIST_INGREDIENT_CAN_BE_ADDED_TO_SHOPPING_LIST_COLUMN,
			PANTRY_LIST_INGREDIENT_HAS_THRESHOLD_BEEN_HIT_COLUMN));
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not get pantry list ingredient: " + ingredientName);
			return null;
		}
		return pantryIngredient;
	}

	/**
	 * @param pantryName The name of the pantry that this ingredient is a part of.
	 * @param ingredientName The name of the ingredient whose id will be retrieved.
	 * @return Returns the pantry ingredient id, null if it cannot be found.
	 */
	public Integer getIngredientId(String pantryName, String ingredientName) {
		// TODO Auto-generated method stub
		Integer ingredientId = null;
		try {
			ingredientId = jdbcTemplateObject.queryForObject(GET_MOST_RECENT_PANTRY_LIST_INGREDIENT_ID, new Object[]{ingredientName, pantryName}, new IngredientMapper(PANTRY_LIST_INGREDIENT_ID_COLUMN));
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not get ingredient: " + ingredientName + "'s id from " + pantryName);
			return null;
		}
		return ingredientId;
	}

	/**
	 * @param ingredientName The ingredient's name.
	 * @param ingredientWholeNumber The ingredient's whole number value.
	 * @param ingredientNumerator The ingredient's numerator value.
	 * @param ingredientDenominator The ingredient's denominator value.
	 * @param ingredientThresholdWholeNumber The ingredient's threshold whole number value.
	 * @param ingredientThresholdNumerator The ingredient's threshold numerator value.
	 * @param ingredientThresholdDenominator The ingredient's threshold denominator value.
	 * @param ingredientUnit The ingredient unit type (lbs, oz., etc.).
	 * @param ingredientType The ingredient type (meat, vegetable, etc.).
	 * @param pantryListName The name of the pantry to insert the ingredient into.
	 * @param canIngredientBeAddedToShoppingList A flag to indicate if the ingredient can be added to a shopping list after its threshold value is hit, by default this is true.
	 * @param isThresholdHit A flag to indicate if the ingredient's threshold has been hit, by default false.
	 * @return 1 if the ingredient was added to the pantry list, otherwise it was not properly added.
	 */
	public int addListIngredient(String ingredientName, int ingredientWholeNumber, int ingredientNumerator, int ingredientDenominator, int ingredientThresholdWholeNumber, 
		int ingredientThresholdNumerator, int ingredientThresholdDenominator, String ingredientUnit, String ingredientType, String pantryListName, boolean canIngredientBeAddedToShoppingList,
		boolean isThresholdHit) {
		int ingredientAddCode = -1;
		try {
			ingredientAddCode = jdbcTemplateObject.update(INSERT_PANTRY_LIST_INGREDIENTS, new Object[]{ingredientName, ingredientWholeNumber, ingredientNumerator, ingredientDenominator, ingredientThresholdWholeNumber, ingredientThresholdNumerator, ingredientThresholdDenominator,
					ingredientUnit, ingredientType, pantryListName, canIngredientBeAddedToShoppingList, isThresholdHit});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not add ingredient: " + ingredientName + " to pantry list: " + pantryListName);
		}
		return ingredientAddCode;
	}
	
	/**
	 * @param ingredientId The unique identifier to determine which ingredient will be reemoved.
	 * @return 1 if the ingredient is removed from the proper pantry list, any other value would imply a failed removal or an improper number of ingredients removed.
	 */
	public int removeListIngredient(int ingredientId) {
		// TODO Auto-generated method stub
		int ingredientRemoveCode = -1;
		try {
			ingredientRemoveCode = jdbcTemplateObject.update(REMOVE_PANTRY_LIST_INGREDIENT, new Object[]{ingredientId});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not remove ingredient:");
		}
		return ingredientRemoveCode;
	}
	
	/**
	 * @param pantryListName The pantry list to remove ingredients from.
	 * @return >= 1 if ingredients were removed, otherwise ingredients were not removed.
	 */
	public int removeAllListIngredients(String pantryListName) {
		int removeIngredientCode = -1;
		try {
			removeIngredientCode = jdbcTemplateObject.update(REMOVE_PANTRY_LIST_INGREDIENTS, new Object[]{pantryListName});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not remove ingredients from pantry list: " + pantryListName);
		}
		return removeIngredientCode;
	}

	/**
	 * Used when the user has bought more of the same amount and wants to add to the pantry list, new threshold values would need to be recomputed.
	 * @param wholeNumberAmount The whole number amount to update to.
	 * @param numeratorAmount The numerator amount to update to.
	 * @param denominatorAmount The denominator amount to update to.
	 * @param ingredientThresholdWholeNumber The new thershold whole number value.
	 * @param ingredientThresholdNumerator The new threshold numerator value.
	 * @param ingredientThresholdDenominator The new threshold denominator value.
	 * @param ingredientUnit The new unit type (lbs., oz., etc).
	 * @param ingredientType The new ingredient type (meat, vegetable, etc.).
	 * @param ingredientId The unique identifier for this pantry ingredient to be updated.
	 * @param canBeAddedToShoppingList A flag indicating if this ingredient can still be transferred to a shopping list, by default it is true.
	 * @param isThresholdHit A flag indicating if the threshold ingredient has been hit.
	 * @return 1 if the pantry ingredient was successfully updated, -1 would imply a database related error, 0 would imply no change, and >1 would imply other potential updates (unwanted side effect).
	 */
	public int updateListIngredient(int wholeNumberAmount, int numeratorAmount, int denominatorAmount, int ingredientThresholdWholeNumber, 
		int ingredientThresholdNumerator, int ingredientThresholdDenominator, String ingredientUnit, String ingredientType, int ingredientId, boolean canBeAddedToShoppingList,
		boolean isThresholdHit) {
		// TODO Auto-generated method stub
		int ingredientUpdateCode = -1;
		try {
			ingredientUpdateCode = jdbcTemplateObject.update(UPDATE_PANTRY_LIST_INGREDIENT, new Object[]{wholeNumberAmount, numeratorAmount, denominatorAmount, ingredientThresholdWholeNumber, 
			ingredientThresholdNumerator, ingredientThresholdDenominator, ingredientUnit, ingredientType, canBeAddedToShoppingList, isThresholdHit, ingredientId});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not update pantry ingredient quantity");
		}
		return ingredientUpdateCode;
	}
	
	/**
	 * Used for when the user wants to subtract an ingredient's amount from the pantry list.
	 * @param wholeNumberAmount The new whole number amount.
	 * @param numeratorAmount The new numerator amount.
	 * @param denominatorAmount The new denominator amount.
	 * @param ingredientId The unique identifier for this pantry ingredient list.
	 * @return 1 if the ingredient was properly modified, -1 would imply a database related error, 0 would imply no change, and >1 would imply other potential ingredient quantity subtractions (unwanted side effect).
	 */
	public int updateListIngredientAmount(int wholeNumberAmount, int numeratorAmount, int denominatorAmount, int ingredientId) {
		// TODO Auto-generated method stub
		int ingredientUpdateCode = -1;
		try {
			ingredientUpdateCode = jdbcTemplateObject.update(UPDATE_PANTRY_LIST_INGREDIENTS_QUANTITY, new Object[]{wholeNumberAmount, numeratorAmount, denominatorAmount, ingredientId});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not update new pantry ingredient quantity (subtraction operation).");
		}
		return ingredientUpdateCode;
	}
	
	/**
	 * @param canBeUpdatedValue The flag to indicate if this ingredient can be transferred to a shopping list from a pantry list.
	 * @param ingredientId The unique identifier of the ingredient.
	 * @return 1 if the ingredient's status was updated, otherwise it was not.
	 */
	public int updateIngredientTransferStatus(boolean canBeUpdatedValue, int ingredientId) {
		int returnUpdateCode = -1;
		try {
			returnUpdateCode = jdbcTemplateObject.update(SET_CAN_BE_TRANSFERRED_TO_SHOPPING_LIST, new Object[]{canBeUpdatedValue, ingredientId});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not update pantry ingredient updated status.");
		}
		return returnUpdateCode;
	}
	
	/**
	 * @param isThresholdHit Flag to indicate if this ingredient's threshold value has been hit.
	 * @param ingredientId The unique identifier of the ingredient.
	 * @return 1 if the ingredient's threshold hit status has been updated, otherwise it has not.
	 */
	public int updateIngredientThresholdHitStatus(boolean isThresholdHit, int ingredientId) {
		int returnThresholdStatusCode = -1;
		try {
			returnThresholdStatusCode = jdbcTemplateObject.update(UPDATE_HAS_THRESHOLD_BEEN_HIT, new Object[]{isThresholdHit, ingredientId});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not update pantry ingredient updated status.");
		}
		return returnThresholdStatusCode;
		
	}
}
