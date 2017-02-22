package tran.allrecipes.data;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tran.allrecipes.presentation.model.Ingredient;

/**
 * @author Todd
 * A class that implements required ingredient list operations.
 */
@Repository
public class ShoppingListDAOImpl implements GenericUserListDAO, PantryListShoppingListDAO, ShoppingListRecipeDAO {

	// table and column names.
	private static final String SHOPPING_LIST_TABLE = "ShoppingList";
	private static final String SHOPPING_LIST_NAME_COLUMN = "shoppingListName";
	private static final String SHOPPING_LIST_USER_POSTED_BY_COLUMN = "userPostedBy";
	
	private static final String SHOPPING_LIST_INGREDIENTS_TABLE = "ShoppingListIngredients";
	private static final String SHOPPING_LIST_INGREDIENT_ID_COLUMN = "ingredientId";
	private static final String SHOPPING_LIST_INGREDIENT_NAME_COLUMN = "ingredientName";
	private static final String SHOPPING_LIST_INGREDIENT_NUMBER_COLUMN = "ingredientQNumber";
	private static final String SHOPPING_LIST_INGREDIENT_NUMERATOR_COLUMN = "ingredientQNumerator";
	private static final String SHOPPING_LIST_INGREDIENT_DENOMINATOR_COLUMN = "ingredientQDenominator";
	private static final String SHOPPING_LIST_INGREDIENT_UNIT_COLUMN = "ingredientUnit";
	private static final String SHOPPING_LIST_INGREDIENT_TYPE_COLUMN = "ingredientType";
	private static final String GET_SHOPPING_LIST_NAME_EXCEPTION_MESSAGE = "cannot map and retrieve the shopping list name";
	private static final String GET_SHOPPING_LIST_OWNER_NAME_EXCEPTION_MESSAGE = "cannot map and retrieve the shopping list's owner.";
	
	// SQL statements.
	private static final String GET_SHOPPING_LIST_NAME = "SELECT " + SHOPPING_LIST_NAME_COLUMN + " FROM " + SHOPPING_LIST_TABLE + " WHERE " + SHOPPING_LIST_USER_POSTED_BY_COLUMN + " = ?";
	private static final String GET_SHOPPING_LIST_OWNER = "SELECT " + SHOPPING_LIST_USER_POSTED_BY_COLUMN + " FROM " + SHOPPING_LIST_TABLE + " WHERE " + SHOPPING_LIST_NAME_COLUMN + " = ?";
	private static final String INSERT_SHOPPING_LIST = "INSERT INTO " + SHOPPING_LIST_TABLE + "(" + SHOPPING_LIST_NAME_COLUMN + ", " + SHOPPING_LIST_USER_POSTED_BY_COLUMN + ") VALUES(?, ?)";
	private static final String DELETE_SHOPPING_LIST = "DELETE" + " FROM " + SHOPPING_LIST_TABLE + " WHERE " + SHOPPING_LIST_NAME_COLUMN + " = ? AND " + SHOPPING_LIST_USER_POSTED_BY_COLUMN + " = ?";
	
	private static final String GET_SHOPPING_LIST_INGREDIENTS_QUERY = "SELECT * FROM " + SHOPPING_LIST_INGREDIENTS_TABLE + " WHERE " + SHOPPING_LIST_NAME_COLUMN + " = ?";
	private static final String GET_SHOPPING_LIST_SINGLE_INGREDIENT_QUERY = "SELECT * FROM " + SHOPPING_LIST_INGREDIENTS_TABLE + " WHERE " + SHOPPING_LIST_NAME_COLUMN + " = ? AND " + SHOPPING_LIST_INGREDIENT_NAME_COLUMN + " = ?";
	private static final String INSERT_SHOPPING_LIST_INGREDIENTS = "INSERT INTO " + SHOPPING_LIST_INGREDIENTS_TABLE + "(" + SHOPPING_LIST_INGREDIENT_NAME_COLUMN + ", " + SHOPPING_LIST_INGREDIENT_NUMBER_COLUMN + ", " + SHOPPING_LIST_INGREDIENT_NUMERATOR_COLUMN 
	+ ", " + SHOPPING_LIST_INGREDIENT_DENOMINATOR_COLUMN + ", " + SHOPPING_LIST_INGREDIENT_UNIT_COLUMN + ", " + SHOPPING_LIST_INGREDIENT_TYPE_COLUMN + ", " + SHOPPING_LIST_NAME_COLUMN + ") VALUES(?, ?, ?, ?, ?, ?, ?)";
	private static final String REMOVE_SHOPPING_LIST_INGREDIENT = "DELETE FROM " + SHOPPING_LIST_INGREDIENTS_TABLE + " WHERE " + SHOPPING_LIST_INGREDIENT_ID_COLUMN + " = ?";
	private static final String REMOVE_SHOPPING_LIST_INGREDIENTS = "DELETE FROM " + SHOPPING_LIST_INGREDIENTS_TABLE + " WHERE " + SHOPPING_LIST_NAME_COLUMN + " = ?";
	private static final String UPDATE_SHOPPING_LIST_INGREDIENTS_QUANTITY = "UPDATE " + SHOPPING_LIST_INGREDIENTS_TABLE + " SET "  + SHOPPING_LIST_INGREDIENT_NUMBER_COLUMN + " = ?, " + SHOPPING_LIST_INGREDIENT_NUMERATOR_COLUMN + " = ?, "
	+ SHOPPING_LIST_INGREDIENT_DENOMINATOR_COLUMN + " = ? WHERE " + SHOPPING_LIST_INGREDIENT_ID_COLUMN + " = ?";
	private static final String UPDATE_SHOPPING_LIST_INGREDIENTS = "UPDATE " + SHOPPING_LIST_INGREDIENTS_TABLE + " SET "  + SHOPPING_LIST_INGREDIENT_NUMBER_COLUMN + " = ?, " + SHOPPING_LIST_INGREDIENT_NUMERATOR_COLUMN + " = ?, "
	+ SHOPPING_LIST_INGREDIENT_DENOMINATOR_COLUMN + " = ?, " + SHOPPING_LIST_INGREDIENT_UNIT_COLUMN + " = ?, " + SHOPPING_LIST_INGREDIENT_TYPE_COLUMN + " = ? WHERE " + SHOPPING_LIST_INGREDIENT_ID_COLUMN + " = ?";
	private static final String GET_MOST_RECENT_SHOPPING_LIST_INGREDIENT_ID = "SELECT " + SHOPPING_LIST_INGREDIENT_ID_COLUMN + " FROM " + SHOPPING_LIST_INGREDIENTS_TABLE + " WHERE " + SHOPPING_LIST_INGREDIENT_NAME_COLUMN + " = ? AND " +
	SHOPPING_LIST_NAME_COLUMN + " = ?";
	
	/** A data member to get the database driver. */
	private DataSource dataSource;
	/** A data member to perform SQL related queries such as insertions, removals, and updates. */
	private JdbcTemplate jdbcTemplateObject;
	
	/**
	 * @param dataSource An object holding information to connect to a database.
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplateObject = new JdbcTemplate(this.dataSource);
	}
		
	/**
	 * @param userOwnerOfShoppingList The name of the user that owns the shopping list.
	 * @return Returns the name of the shopping list belonging to the user.
	 */
	public String getListName(String userOwnerOfShoppingList) {
		String shoppingListName = null;
		try {
			shoppingListName = jdbcTemplateObject.queryForObject(GET_SHOPPING_LIST_NAME, new Object[]{userOwnerOfShoppingList}, new UserListNameMapper(SHOPPING_LIST_NAME_COLUMN, GET_SHOPPING_LIST_NAME_EXCEPTION_MESSAGE));
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("cannot retrieve shopping list name for user: " + userOwnerOfShoppingList);
		}
		return shoppingListName;
	}
	
	/**
	 * @param shoppingListName The name of the shopping list.
	 * @return The owner of the requested shopping list.
	 */
	public String getUserOwnerOfList(String shoppingListName) {
		String userName = null;
		try {
			userName = jdbcTemplateObject.queryForObject(GET_SHOPPING_LIST_OWNER, new Object[]{shoppingListName}, new UserListOwnerMapper(SHOPPING_LIST_USER_POSTED_BY_COLUMN, GET_SHOPPING_LIST_OWNER_NAME_EXCEPTION_MESSAGE));
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("cannot retrieve shopping list's owner for shopping list: " + shoppingListName);
		}
		return userName;
	}
	
	/**
	 * creates a shopping list.
	 * @param shoppingListName The name of the shopping list.
	 * @param userName The name of the user to create the shopping list for.
	 * @return -1 if the user cannot create a shopping list, 1 if the user is able to.
	 */
	public int addList(String shoppingListName, String userOwnerOfShoppingList) {
		// TODO Auto-generated method stub
		int returnCode = -1;
		try {
			returnCode = jdbcTemplateObject.update(INSERT_SHOPPING_LIST, new Object[] {shoppingListName, userOwnerOfShoppingList});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not add shopping list: " + shoppingListName);
		}
		return returnCode;
	}
	
	/**
	 * @param shoppingListName The name of the shopping list to remove.
	 * @param userName The name of the user that created the shopping list.
	 * @return 1 if the shopping list has been deleted, otherwise the shopping list isn't deleted.
	 */
	public int removeList(String shoppingListName, String userOwnerOfShoppingList) {
		// TODO Auto-generated method stub
		// unused but implemented (may remove in future version).
		int returnCode = -1;
		try {
			returnCode = jdbcTemplateObject.update(DELETE_SHOPPING_LIST, new Object[] {shoppingListName, userOwnerOfShoppingList});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not remove shopping list: " + shoppingListName);
		}
		return returnCode;
	}
		
	/**
	 * @param shoppingListName The name of the shopping list to retrieve.
	 * @return a list of ingredients of a shopping list provided a name.
	 */
	public List<Ingredient> getListIngredients(String shoppingListName) {
		// TODO Auto-generated method stub
		List<Ingredient> shoppingListIngredients = null;
		try {
			shoppingListIngredients = jdbcTemplateObject.query(GET_SHOPPING_LIST_INGREDIENTS_QUERY, new Object[]{shoppingListName}, new ShoppingListIngredientsMapper(
					SHOPPING_LIST_INGREDIENT_NAME_COLUMN, SHOPPING_LIST_INGREDIENT_NUMBER_COLUMN, SHOPPING_LIST_INGREDIENT_NUMERATOR_COLUMN, SHOPPING_LIST_INGREDIENT_DENOMINATOR_COLUMN, 
					SHOPPING_LIST_INGREDIENT_UNIT_COLUMN, SHOPPING_LIST_INGREDIENT_ID_COLUMN, SHOPPING_LIST_INGREDIENT_TYPE_COLUMN));
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not get ingredients for shopping list: " + shoppingListName);
			return null;
		}
		return shoppingListIngredients;
	}
	
	/**
	 * @param shoppingListName The name of the shopping list to which the ingredient belongs to.
	 * @param ingredientName The name of the ingredient.
	 * @return An ingredient object specified for a shopping list name and an ingredient name, null if it cannot be retrieved.
	 */
	public Ingredient getSingleIngredient(String shoppingListName, String ingredientName)  {
		Ingredient ingredient = null;
		try {
			ingredient = jdbcTemplateObject.queryForObject(GET_SHOPPING_LIST_SINGLE_INGREDIENT_QUERY, new Object[]{shoppingListName, ingredientName}, new ShoppingListIngredientsMapper(
				SHOPPING_LIST_INGREDIENT_NAME_COLUMN, SHOPPING_LIST_INGREDIENT_NUMBER_COLUMN, SHOPPING_LIST_INGREDIENT_NUMERATOR_COLUMN, SHOPPING_LIST_INGREDIENT_DENOMINATOR_COLUMN, 
				SHOPPING_LIST_INGREDIENT_UNIT_COLUMN, SHOPPING_LIST_INGREDIENT_ID_COLUMN, SHOPPING_LIST_INGREDIENT_TYPE_COLUMN));
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("can not retrieve ingredient: " + ingredientName + " from shopping list: " + shoppingListName);
		}
		return ingredient;
	}
	
	/**
	 * @param shoppingListName The name of the shopping list to which the ingredient belongs to.
	 * @param ingredientName The name of the ingredient.
	 * @return The id of the ingredient specified by the shopping list and ingredient name, null if there is a retrieval error.
	 */
	public Integer getIngredientId(String shoppingListName, String ingredientName) {
		// use ingredientMapper.
		Integer recentIngredientId = null;
		try {
			recentIngredientId = jdbcTemplateObject.queryForObject(GET_MOST_RECENT_SHOPPING_LIST_INGREDIENT_ID, new Object[]{ingredientName, shoppingListName}, new IngredientMapper(SHOPPING_LIST_INGREDIENT_ID_COLUMN));
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not get the id for ingredient: " + ingredientName);
		}
		return recentIngredientId;
	}
	
	/**
	 * @param ingredientName The name of the ingredient.
	 * @param ingredientWholeNumber The whole number amount of the ingredient entered by the user.
	 * @param ingredientNumerator The numerator amount entered by the user.
	 * @param ingredientDenominator The denominator amount entered by the user.
	 * @param ingredientUnit The unit of the ingredient, i.e: tablespoon, teaspoon, etc.
	 * @param shoppingListName The name of the shopping list to add this ingredient to.
	 * @return 1 if the shopping list ingredient was added, -1 if not.
	 */
	public int addListIngredient(String ingredientName, int ingredientWholeNumber, int ingredientNumerator, int ingredientDenominator,
	String ingredientUnit, String ingredientType, String shoppingListName) {
		// TODO Auto-generated method stub
		int returnCode = -1;
		try {
			returnCode = jdbcTemplateObject.update(INSERT_SHOPPING_LIST_INGREDIENTS, new Object[] {ingredientName, ingredientWholeNumber, ingredientNumerator, ingredientDenominator, ingredientUnit, ingredientType, shoppingListName});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not add ingredient: " + ingredientName + " to shopping list: " + shoppingListName);
		}
		return returnCode;
	}
	
	/**
	 * @param ingredientId The id of the ingredient to remove.
	 * @return 1 if the ingredient was removed from the shopping list, -1 if not.
	 */
	public int removeListIngredient(int ingredientId) {
		// TODO Auto-generated method stub
		int returnCode = -1;
		try {
			returnCode = jdbcTemplateObject.update(REMOVE_SHOPPING_LIST_INGREDIENT, new Object[]{ingredientId});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not remove ingredient with id: " + ingredientId);
		}
		return returnCode;
	}
	
	/**
	 * @param shoppingListName The shopping list to remove all ingredients from
	 * @return >= 1 if the ingredients were removed, otherwise ingredients were not removed.
	 */
	public int removeAllListIngredients(String shoppingListName) {
		int removeReturnCode = -1;
		try {
			removeReturnCode = jdbcTemplateObject.update(REMOVE_SHOPPING_LIST_INGREDIENTS, new Object[]{shoppingListName});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not remove ingredients from shopping list: " + shoppingListName);
		}
		return removeReturnCode;
	}
	
	/**
	 * @param newIngredientWholeNumber The new whole number entered by the user.
	 * @param newIngredientNumerator The new numerator amount entered by the user.
	 * @param newIngredientDenominator The new denominator amount entered by the user.
	 * @param ingredientUnit The unit of the ingredient (lbs. ml, etc.).
	 * @param ingredientType The type of the ingredient (meat, vegetable, etc.).
	 * @param ingredientId The id of the ingredient to update.
	 * @return 1 if the ingredient was updated, -1 if not.
	 */
	public int updateListIngredient(int newIngredientWholeNumber, int newIngredientNumerator, int newIngredientDenominator,
	String ingredientUnit, String ingredientType, int ingredientId) {
		// TODO Auto-generated method stub
		int returnCode = -1;
		try {
			returnCode = jdbcTemplateObject.update(UPDATE_SHOPPING_LIST_INGREDIENTS, new Object[]{newIngredientWholeNumber, newIngredientNumerator, newIngredientDenominator, ingredientUnit, ingredientType, ingredientId});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not update ingredient with ID: " + ingredientId);
		}
		return returnCode;
	}
	
	/**
	 * only used by the shopping list DAO, other lists will require a type and unit to be added with the ingredient.
	 * @param newIngredientWholeNumber The new whole number entered by the user.
	 * @param newIngredientNumerator The new numerator amount entered by the user.
	 * @param newIngredientDenominator The new denominator amount entered by the user.
	 * @param ingredientId The id of the ingredient to update.
	 * @return 1 if the ingredient was updated, -1 if not.
	 */
	public int updateListIngredientAmount(int newIngredientWholeNumber, int newIngredientNumerator, int newIngredientDenominator, int ingredientId) {
		int returnCode = -1;
		try {
			returnCode = jdbcTemplateObject.update(UPDATE_SHOPPING_LIST_INGREDIENTS_QUANTITY, new Object[]{newIngredientWholeNumber, newIngredientNumerator, newIngredientDenominator, ingredientId});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not update ingredient amount with ID: " + ingredientId);
		}
		return returnCode;
	}
	
}
