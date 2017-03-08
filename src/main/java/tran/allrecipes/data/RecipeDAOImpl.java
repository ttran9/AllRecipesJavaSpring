package tran.allrecipes.data;

import java.time.LocalDateTime;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.math3.fraction.Fraction;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import tran.allrecipes.presentation.model.Ingredient;
import tran.allrecipes.presentation.model.Recipe;
import tran.allrecipes.presentation.model.RecipeDirection;
import tran.allrecipes.presentation.model.RecipeReview;
import tran.allrecipes.service.UtilityServiceImpl;

/**
 * @author Todd
 * A class to implement required functionalities of a list holding ingredients.
 */
@Repository
public class RecipeDAOImpl implements GenericUserListDAO {
	
	// table names and columns.
	private static final String RECIPE_NAME_COLUMN = "recipeName";
	private static final String INGREDIENT_ID_COLUMN = "ingredientId";
	
	private static final String RECIPE_DIRECTIONS_TABLE = "RecipeDirections";
	private static final String DIRECTIONS_DESCRIPTION_COLUMN_NAME = "directionDescription";
	private static final String DIRECTIONS_NUMBER_COLUMN_NAME = "directionNumber";
	private static final String DIRECTION_ID_COLUMN = "directionId";
	
	private static final String RECIPE_INGREDIENTS_TABLE = "RecipeIngredients";
	private static final String RECIPE_INGREDIENT_NAME_COLUMN = "ingredientName";
	private static final String RECIPE_INGREDIENT_WHOLE_NUMBER_COLUMN = "ingredientQNumber";
	private static final String RECIPE_INGREDIENT_NUMERATOR_COLUMN = "ingredientQNumerator";
	private static final String RECIPE_INGREDIENT_DENOMINATOR_COLUMN = "ingredientQDenominator";
	private static final String RECIPE_INGREDIENT_UNIT_COLUMN = "ingredientUnit";
	private static final String RECIPE_INGREDIENT_TYPE_COLUMN = "ingredientType";
	
	private static final String RECIPE_REVIEWS_TABLE = "RecipeReviews";
	private static final String RECIPE_REVIEW_CONTENT_COLUMN = "reviewContent";
	private static final String RECIPE_REVIEW_RATING_COLUMN = "recipeRating";
	private static final String RECIPE_REVIEW_TITLE_COLUMN = "reviewTitle";
	private static final String RECIPE_REVIEW_ID_COLUMN = "reviewId";
	private static final String RECIPE_REVIEW_POSTED_TIME_COLUMN = "reviewPostedTime";
	private static final String RECIPE_REVIEW_EDIT_TIME_COLUMN = "reviewEditedTime";
	
	private static final String RECIPE_TABLE = "Recipes";
	private static final String RECIPE_NUMBER_SERVINGS_COLUMN = "numberServings";
	private static final String RECIPE_USER_POSTED_BY_COLUMN = "userPostedBy";
	private static final String RECIPE_PREP_TIME_COLUMN = "prepTime";
	private static final String RECIPE_COOK_TIME_COLUMN = "cookTime";
	private static final String RECIPE_DISH_TYPE_COLUMN = "dishType";
	private static final String RECIPE_IMAGE_URL_COLUMN = "imageURL";
	private static final String RECIPE_DATE_CREATED_COLUMN = "dateCreated";
	private static final String RECIPE_DESCRIPTION_COLUMN = "recipeDescription";
	private static final String RECIPE_NUMBER_ONE_STAR_REVIEWS_COLUMN = "numberOneStarReviews";
	private static final String RECIPE_NUMBER_TWO_STAR_REVIEWS_COLUMN = "numberTwoStarReviews";
	private static final String RECIPE_NUMBER_THREE_STAR_REVIEWS_COLUMN = "numberThreeStarReviews";
	private static final String RECIPE_NUMBER_FOUR_STAR_REVIEWS_COLUMN = "numberFourStarReviews";
	private static final String RECIPE_NUMBER_FIVE_STAR_REVIEWS_COLUMN = "numberFiveStarReviews";
	private static final String RECIPE_AVERAGE_RATING_COLUMN = "averageRating";
	private static final String RECIPE_TOTAL_REVIEWS_COLUMN = "totalNumberOfReviews";
	
	// SQL statements.
	private static final String GET_RECIPE_INGREDIENTS  = "SELECT * FROM " + RECIPE_INGREDIENTS_TABLE + " WHERE " + RECIPE_NAME_COLUMN + " = ?";
	private static final String ADD_RECIPE_INGREDIENT = "INSERT INTO " + RECIPE_INGREDIENTS_TABLE + "(" + RECIPE_INGREDIENT_NAME_COLUMN + ", " + RECIPE_INGREDIENT_WHOLE_NUMBER_COLUMN + ", " +  RECIPE_INGREDIENT_NUMERATOR_COLUMN + ", " + RECIPE_INGREDIENT_DENOMINATOR_COLUMN +
	", " + RECIPE_INGREDIENT_UNIT_COLUMN + ", " + RECIPE_INGREDIENT_TYPE_COLUMN + ", " + RECIPE_NAME_COLUMN + ")" + " VALUES(?, ?, ?, ?, ?, ?, ?)";
	private static final String REMOVE_RECIPE_INGREDIENT = "DELETE FROM " + RECIPE_INGREDIENTS_TABLE +  " WHERE " + INGREDIENT_ID_COLUMN + " = ?";
	private static final String DELETE_RECIPE_INGREDIENTS = "DELETE FROM " + RECIPE_INGREDIENTS_TABLE +  " WHERE " + RECIPE_NAME_COLUMN + " = ?";
	private static final String UPDATE_RECIPE_INGREDIENT = "UPDATE RecipeIngredients SET " + RECIPE_INGREDIENT_WHOLE_NUMBER_COLUMN + " = ?, " + RECIPE_INGREDIENT_NUMERATOR_COLUMN + " = ?, " + RECIPE_INGREDIENT_DENOMINATOR_COLUMN + " = ?, " + RECIPE_INGREDIENT_UNIT_COLUMN +
	" = ?, " + RECIPE_INGREDIENT_TYPE_COLUMN + " = ?" + " WHERE " + INGREDIENT_ID_COLUMN + " = ?";
	private static final String GET_RECIPE_INGREDIENT = "SELECT * FROM " +  RECIPE_INGREDIENTS_TABLE + " WHERE " + RECIPE_NAME_COLUMN + " = ?" + " AND " + RECIPE_INGREDIENT_NAME_COLUMN + " = ?";
	
	private static final String GET_SINGLE_RECIPE = "SELECT * FROM " + RECIPE_TABLE + " WHERE " + RECIPE_NAME_COLUMN + " = ? ";
	private static final String GET_ALL_RECIPES = "SELECT * FROM " + RECIPE_TABLE;
	private static final String GET_ALL_RECIPES_SORT_BY_HIGHEST_RATING = "SELECT * FROM " + RECIPE_TABLE + " ORDER BY " + RECIPE_AVERAGE_RATING_COLUMN + " DESC, " + RECIPE_TOTAL_REVIEWS_COLUMN + " DESC";
	private static final String UPDATE_RECIPE_SERVINGS = "UPDATE Recipes SET " + RECIPE_NUMBER_SERVINGS_COLUMN + " = ?" + " WHERE " + RECIPE_NAME_COLUMN + " = ?";
	private static final String ADD_RECIPE_CONTENT = "INSERT INTO " + RECIPE_TABLE + "(" + RECIPE_NAME_COLUMN + ", " + RECIPE_NUMBER_SERVINGS_COLUMN + ", " + RECIPE_USER_POSTED_BY_COLUMN + ", " + RECIPE_PREP_TIME_COLUMN + ", " + RECIPE_COOK_TIME_COLUMN + ", " + RECIPE_DISH_TYPE_COLUMN + ", " 
	+ RECIPE_IMAGE_URL_COLUMN + ", " + RECIPE_DATE_CREATED_COLUMN + ", " + RECIPE_DESCRIPTION_COLUMN + ", " + RECIPE_NUMBER_ONE_STAR_REVIEWS_COLUMN + ", " + RECIPE_NUMBER_TWO_STAR_REVIEWS_COLUMN + ", " + RECIPE_NUMBER_THREE_STAR_REVIEWS_COLUMN + ", " + RECIPE_NUMBER_FOUR_STAR_REVIEWS_COLUMN
	+ ", " + RECIPE_NUMBER_FIVE_STAR_REVIEWS_COLUMN + ", " + RECIPE_AVERAGE_RATING_COLUMN + ", " + RECIPE_TOTAL_REVIEWS_COLUMN + ") " + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String DELETE_RECIPE = "DELETE FROM " + RECIPE_TABLE + " WHERE " + RECIPE_NAME_COLUMN + " = ? AND " + RECIPE_USER_POSTED_BY_COLUMN + " = ?"; 
	private static final String GET_USER_RECIPES = "SELECT * FROM " + RECIPE_TABLE + " WHERE " + RECIPE_USER_POSTED_BY_COLUMN + " = ?";
	private static final String UPDATE_RECIPE_ONE_STAR_RATING = "UPDATE " + RECIPE_TABLE + " SET " + RECIPE_NUMBER_ONE_STAR_REVIEWS_COLUMN + " = ?" + " WHERE " + RECIPE_NAME_COLUMN + " = ? ";
	private static final String UPDATE_RECIPE_TWO_STAR_RATING = "UPDATE " + RECIPE_TABLE + " SET " + RECIPE_NUMBER_TWO_STAR_REVIEWS_COLUMN + " = ?" + " WHERE " + RECIPE_NAME_COLUMN + " = ? ";
	private static final String UPDATE_RECIPE_THREE_STAR_RATING = "UPDATE " + RECIPE_TABLE + " SET " + RECIPE_NUMBER_THREE_STAR_REVIEWS_COLUMN + " = ?" + " WHERE " + RECIPE_NAME_COLUMN + " = ? ";
	private static final String UPDATE_RECIPE_FOUR_STAR_RATING = "UPDATE " + RECIPE_TABLE + " SET " + RECIPE_NUMBER_FOUR_STAR_REVIEWS_COLUMN + " = ?" + " WHERE " + RECIPE_NAME_COLUMN + " = ? ";
	private static final String UPDATE_RECIPE_FIVE_STAR_RATING = "UPDATE " + RECIPE_TABLE + " SET " + RECIPE_NUMBER_FIVE_STAR_REVIEWS_COLUMN + " = ?" + " WHERE " + RECIPE_NAME_COLUMN + " = ? ";
	private static final String UPDATE_RECIPE_CONTENTS = "UPDATE " + RECIPE_TABLE + " SET " + RECIPE_PREP_TIME_COLUMN + " = ?, " + RECIPE_COOK_TIME_COLUMN + " = ?, " + RECIPE_DISH_TYPE_COLUMN + " = ?, "
	+ RECIPE_IMAGE_URL_COLUMN + " = ?, " + RECIPE_DESCRIPTION_COLUMN + " = ? WHERE " + RECIPE_NAME_COLUMN + " = ?";
	private static final String UPDATE_AVERAGE_RECIPE_RATING = "UPDATE " + RECIPE_TABLE + " SET " + RECIPE_AVERAGE_RATING_COLUMN + " = ? WHERE " + RECIPE_NAME_COLUMN + " = ?";
	private static final String UPDATE_TOTAL_REVIEWS = "UPDATE " + RECIPE_TABLE + " SET " + RECIPE_TOTAL_REVIEWS_COLUMN + " = ? WHERE " + RECIPE_NAME_COLUMN + " = ?";
	
	private static final String GET_RECIPE_DIRECTIONS = "SELECT * FROM " + RECIPE_DIRECTIONS_TABLE + " WHERE " + RECIPE_NAME_COLUMN + " = ? ORDER BY " + DIRECTIONS_NUMBER_COLUMN_NAME + " ASC";
	private static final String INSERT_RECIPE_DIRECTION = "INSERT INTO " + RECIPE_DIRECTIONS_TABLE + "(" + DIRECTIONS_NUMBER_COLUMN_NAME + ", " + DIRECTIONS_DESCRIPTION_COLUMN_NAME + ", " + RECIPE_NAME_COLUMN + ")" + " VALUES(?, ?, ?)";
	private static final String DELETE_RECIPE_DIRECTION = "DELETE FROM " + RECIPE_DIRECTIONS_TABLE + " WHERE " + DIRECTION_ID_COLUMN + " = ?";
	private static final String DELETE_RECIPE_DIRECTIONS = "DELETE FROM " + RECIPE_DIRECTIONS_TABLE + " WHERE " + RECIPE_NAME_COLUMN + " = ?";
	private static final String UPDATE_RECIPE_DIRECTION = "UPDATE " + RECIPE_DIRECTIONS_TABLE + " SET " + DIRECTIONS_NUMBER_COLUMN_NAME + " = ?, " + DIRECTIONS_DESCRIPTION_COLUMN_NAME + " = ? WHERE " + DIRECTIONS_NUMBER_COLUMN_NAME + " = ? AND " + RECIPE_NAME_COLUMN + " = ?";
	private static final String UPDATE_RECIPE_DIRECTION_CONTENT = "UPDATE " + RECIPE_DIRECTIONS_TABLE + " SET " + DIRECTIONS_DESCRIPTION_COLUMN_NAME + " = ? WHERE " + DIRECTIONS_NUMBER_COLUMN_NAME + " = ? AND " + RECIPE_NAME_COLUMN + " = ?";
	private static final String GET_SINGLE_RECIPE_DIRECTION_ID = "SELECT " + DIRECTION_ID_COLUMN + " FROM " + RECIPE_DIRECTIONS_TABLE + " WHERE " + RECIPE_NAME_COLUMN + " = ? AND " + DIRECTIONS_NUMBER_COLUMN_NAME + " = ?";
	private static final String GET_NUMBER_OF_DIRECTIONS = "SELECT COUNT(" + DIRECTIONS_DESCRIPTION_COLUMN_NAME + ") " + "FROM " + RECIPE_DIRECTIONS_TABLE + " WHERE " + RECIPE_NAME_COLUMN + " = ?"; // necessary to be able to assign the directionNumber of the next direction.
	
	private static final String GET_RECIPE_REVIEWS = "SELECT * FROM " + RECIPE_REVIEWS_TABLE + " WHERE " + RECIPE_NAME_COLUMN + " = ? ORDER BY " + RECIPE_REVIEW_POSTED_TIME_COLUMN + " DESC";
	private static final String GET_SINGLE_RECIPE_REVIEW = "SELECT * FROM " + RECIPE_REVIEWS_TABLE + " WHERE " + RECIPE_REVIEW_ID_COLUMN + " = ?";
	private static final String INSERT_RECIPE_REVIEW = "INSERT INTO " + RECIPE_REVIEWS_TABLE + "(" + RECIPE_REVIEW_CONTENT_COLUMN + ", " + RECIPE_REVIEW_RATING_COLUMN + ", " + RECIPE_REVIEW_TITLE_COLUMN + ", " + RECIPE_REVIEW_POSTED_TIME_COLUMN + ", " + RECIPE_REVIEW_EDIT_TIME_COLUMN + 
	", " + RECIPE_USER_POSTED_BY_COLUMN + ", " + RECIPE_NAME_COLUMN + ") VALUES(?, ?, ?, ?, ?, ?, ?)";
	private static final String DELETE_RECIPE_REVIEW = "DELETE FROM " + RECIPE_REVIEWS_TABLE + " WHERE " + RECIPE_REVIEW_ID_COLUMN + " = ?";
	private static final String DELETE_RECIPE_REVIEWS = "DELETE FROM " + RECIPE_REVIEWS_TABLE + " WHERE " + RECIPE_NAME_COLUMN + " = ?";
	private static final String DELETE_RECIPE_REVIEWS_FROM_USER = "DELETE FROM " + RECIPE_REVIEWS_TABLE + " WHERE " + RECIPE_NAME_COLUMN + " = ? AND " + RECIPE_USER_POSTED_BY_COLUMN + " = ?";
	private static final String UPDATE_RECIPE_REVIEW = "UPDATE " + RECIPE_REVIEWS_TABLE + " SET " + RECIPE_REVIEW_RATING_COLUMN + " = ?, " + RECIPE_REVIEW_CONTENT_COLUMN + " = ?, " + RECIPE_REVIEW_EDIT_TIME_COLUMN + " = ? WHERE " + RECIPE_REVIEW_ID_COLUMN + " = ?";
	private static final String GET_SINGLE_RECIPE_REVIEW_ID = "SELECT " + RECIPE_REVIEW_ID_COLUMN + " FROM " + RECIPE_REVIEWS_TABLE + " WHERE " + RECIPE_USER_POSTED_BY_COLUMN + " = ? " + "AND " + RECIPE_NAME_COLUMN + " = ? " + "ORDER BY " + RECIPE_REVIEW_POSTED_TIME_COLUMN
	+ " DESC LIMIT 1";
	
	/** A data member to get the database driver. */
	private DataSource dataSource;
	/** A data member to perform SQL related queries such as insertions, removals, and updates. */
	private JdbcTemplate jdbcTemplateObject;
	/** Enforces a transaction (all or nothing) when modifying the database. */
	private PlatformTransactionManager transactionManager;
	
	/**
	 * @param dataSource An object holding information to make a database connection.
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplateObject = new JdbcTemplate(this.dataSource);
	}
	
	/**
	 * @param transactionManager An object managing the all or nothing transaction.
	 */
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	/**
	 * @param recipeName The name of the recipe.
	 * @return A list of ingredients of the specified recipe.
	 */
	public List<Ingredient> getListIngredients(String recipeName) {
		List<Ingredient> ingredientsList = null;
		try {
			ingredientsList = jdbcTemplateObject.query(GET_RECIPE_INGREDIENTS, new Object[]{recipeName}, new RecipeIngredientsMapper());
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("error retrieving recipe ingredients.");
			return null;
		}
		return ingredientsList;
	}
	
	/**
	 * @param recipeName The name of the recipe is stored in.
	 * @param ingredientName The name of the ingredient.
	 * @return Returns an ingredient specified by the recipe and ingredient names.
	 */
	public Ingredient getSingleIngredient(String recipeName, String ingredientName) {
		Ingredient ingredient = null;
		try {
			ingredient = jdbcTemplateObject.queryForObject(GET_RECIPE_INGREDIENT, new Object[]{recipeName, ingredientName}, new RecipeIngredientsMapper());
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			throw new DataIntegrityViolationException("cannot get single ingredient with name: " + ingredientName + " for recipe " + recipeName);
		}
		return ingredient;
	}
	
	/**
	 * @param recipeName The name of the recipe to retrieve the ingredient from.
	 * @param ingredientName The name of the ingredient.
	 * @return A value >= 1 would indicate a success, or null if the ingredient id's cannot be retrieved.
	 */
	public Integer getIngredientId(String recipeName, String ingredientName) {
		Integer ingredientId = null;
		try {
			ingredientId = jdbcTemplateObject.queryForObject(GET_RECIPE_INGREDIENT, new Object[]{recipeName, ingredientName}, new IngredientMapper(INGREDIENT_ID_COLUMN));
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("cannot get ingredient inserted ingredient Id.");
		}
		return ingredientId;
	}
	
	/**
	 * @param ingredientName The name of the ingredient to add.
	 * @param ingredientWholeNumber The whole number quantity.
	 * @param ingredientNumerator The numerator quantity.
	 * @param ingredientDenominator The denominator quantity.
	 * @param ingredientUnit The unit type (lbs., oz., etc.).
 	 * @param ingredientType The type of the ingredient (meat, vegetables, etc.).
	 * @param recipeName The name of the recipe to add the ingredient to.
	 * @return 1 if the ingredient was added to the recipe successfully, a value greater than 1 indicates there was some additional unwanted addition.
	 * -1 would imply some database access error.
	 */
	public int addListIngredient(String ingredientName, int ingredientWholeNumber, int ingredientNumerator, int ingredientDenominator, String ingredientUnit, String ingredientType, String recipeName) {
		int insertionReturnCode = -1;
		try {
			insertionReturnCode = jdbcTemplateObject.update(ADD_RECIPE_INGREDIENT, new Object[]{ingredientName, ingredientWholeNumber, ingredientNumerator, ingredientDenominator, ingredientUnit, ingredientType, recipeName});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			throw new DataIntegrityViolationException("add ingredients error");
		}
		return insertionReturnCode;
	}
	
	/**
	 * @param ingredientId The unique identifier of the ingredient to be used for removal.
	 * @return 1 would imply a successful ingredient removal, -1 would imply some sort of database error, and a value greater than 1 would imply some sort of side effect.
	 */
	public int removeListIngredient(int ingredientId) {
		int deleteReturnCode = -1;
		try {
			deleteReturnCode = jdbcTemplateObject.update(REMOVE_RECIPE_INGREDIENT, new Object[]{ingredientId});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("deletion ingredients error");
		}
		return deleteReturnCode;
	}
	
	/**
	 * @param wholeNumberAmount The new whole number value.
	 * @param numeratorAmount The new numerator value.
	 * @param denominatorAmount The new denominator value.
	 * @param ingredientUnit The new units (lbs, oz., etc.).
	 * @param ingredientType The new type (meat, vegetable, etc.).
	 * @param ingredientId The unique identifier to specify the ingredient to update.
	 * @return 1 if the ingredient was updated, -1 implies some sort of database/SQL related error.
	 */
	public int updateListIngredient(int wholeNumberAmount, int numeratorAmount, int denominatorAmount, String ingredientUnit, String ingredientType, int ingredientId) {
		int updateReturnCode = -1;
		try {
			updateReturnCode = jdbcTemplateObject.update(UPDATE_RECIPE_INGREDIENT, new Object[]{wholeNumberAmount, numeratorAmount, denominatorAmount, ingredientUnit, ingredientType, ingredientId});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			throw new DataIntegrityViolationException("update ingredients error");
		}
		return updateReturnCode;
	}
	
	/**
	 * @param userName The user name.
	 * @return Returns a list of all the recipes created by the specified user.
	 */
	public List<Recipe> getAllUserRecipes(String userName) {
		List<Recipe> userRecipes = null;
		try {
			userRecipes = jdbcTemplateObject.query(GET_USER_RECIPES, new Object[]{userName}, new RecipeMapper());
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("cannot retrieve recipes for user: " + userName);
			return null;
		}
		return userRecipes;
	}
		
	/**
	 * @param newRatingQuantity The new number of one star ratings.
	 * @param recipeName The name of the recipe the review is written for.
	 * @return 1 if the number of one star reviews was updated, otherwise -1 implies a SQL related error.
	 */
	
	public int updateRecipeOneStarRating(int newRatingQuantity, String recipeName) {
		int updateRecipeRatingCode = -1;
		try {
			updateRecipeRatingCode = jdbcTemplateObject.update(UPDATE_RECIPE_ONE_STAR_RATING, new Object[]{newRatingQuantity, recipeName});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			throw new DataIntegrityViolationException("error updating one star rating column for recipe: " + recipeName);
		}
		return updateRecipeRatingCode;
	}
	
	/**
	 * @param newRatingQuantity The new number of one star ratings.
	 * @param recipeName The name of the recipe the review is written for.
	 * @return 1 if the number of one star reviews was updated, otherwise -1 implies a SQL related error.
	 */
	public int updateRecipeTwoStarRating(int newRatingQuantity, String recipeName) {
		int updateRecipeRatingCode = -1;
		try {
			updateRecipeRatingCode = jdbcTemplateObject.update(UPDATE_RECIPE_TWO_STAR_RATING, new Object[]{newRatingQuantity, recipeName});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			throw new DataIntegrityViolationException("error updating two star rating column for recipe: " + recipeName);
		}
		return updateRecipeRatingCode;
	}
	
	/**
	 * @param newRatingQuantity The new number of two star ratings.
	 * @param recipeName The name of the recipe the review is written for.
	 * @return 1 if the number of one two reviews was updated, otherwise -1 implies a SQL related error.
	 */
	public int updateRecipeThreeStarRating(int newRatingQuantity, String recipeName) {
		int updateRecipeRatingCode = -1;
		try {
			updateRecipeRatingCode = jdbcTemplateObject.update(UPDATE_RECIPE_THREE_STAR_RATING, new Object[]{newRatingQuantity, recipeName});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			throw new DataIntegrityViolationException("error updating three star rating column for recipe: " + recipeName);
		}
		return updateRecipeRatingCode;
	}
	
	/**
	 * @param newRatingQuantity The new number of three star ratings.
	 * @param recipeName The name of the recipe the review is written for.
	 * @return 1 if the number of three star reviews was updated, otherwise -1 implies a SQL related error.
	 */
	public int updateRecipeFourStarRating(int newRatingQuantity, String recipeName) {
		int updateRecipeRatingCode = -1;
		try {
			updateRecipeRatingCode = jdbcTemplateObject.update(UPDATE_RECIPE_FOUR_STAR_RATING, new Object[]{newRatingQuantity, recipeName});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			throw new DataIntegrityViolationException("error updating four star rating column for recipe: " + recipeName);
		}
		return updateRecipeRatingCode;
	}
	
	/**
	 * @param newRatingQuantity The new number of four star ratings.
	 * @param recipeName The name of the recipe the review is written for.
	 * @return 1 if the number of four star reviews was updated, otherwise -1 implies a SQL related error.
	 */
	public int updateRecipeFiveStarRating(int newRatingQuantity, String recipeName) {
		int updateRecipeRatingCode = -1;
		try {
			updateRecipeRatingCode = jdbcTemplateObject.update(UPDATE_RECIPE_FIVE_STAR_RATING, new Object[]{newRatingQuantity, recipeName});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			throw new DataIntegrityViolationException("error updating five star rating column for recipe: " + recipeName);
		}
		return updateRecipeRatingCode;
	}

	/**
	 * @param prepTime The updated recipe prep time in seconds.
	 * @param cookTime The updated recipe cook time in seconds.
	 * @param dishType The updated type of dish the recipe is (salad, cuisine, etc).
	 * @param imageURL The updated URL to an image of the recipe.
	 * @param recipeDescription An updated brief description of the recipe.
	 * @param recipeName The name of the recipe.
	 * @return 1 if the recipe could be updated, -1 implies a SQL related error.
	 */
	public int updateRecipeContents(int prepTime, int cookTime, String dishType, String imageURL, String recipeDescription, String recipeName) {
		int updateRecipeContentsCode = -1;
		try {
			updateRecipeContentsCode = jdbcTemplateObject.update(UPDATE_RECIPE_CONTENTS, new Object[]{prepTime, cookTime, dishType, imageURL, recipeDescription, recipeName});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			throw new DataIntegrityViolationException("error updating recipe contents for recipe: " + recipeName);
		}
		return updateRecipeContentsCode;
	}
	
	/**
	 * @param averageRating The new average rating.
	 * @param recipeName The name of the recipe.
	 * @return 1 if the average was updated, -1 implies a SQL related error, 0 means nothing was updated.
	 */
	public int updateAverageRecipeRating(double averageRating, String recipeName) {
		int updateAverageRatingCode = -1;
		try {
			updateAverageRatingCode = jdbcTemplateObject.update(UPDATE_AVERAGE_RECIPE_RATING, new Object[]{averageRating, recipeName});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			throw new DataIntegrityViolationException("error updating average recipe rating for recipe: " + recipeName);
		}
		return updateAverageRatingCode;
	}
		
	public int updateTotalNumberOfReviews(int newTotalOfReviews, String recipeName) {
		int updateTotalReviewsCode = -1;
		try {
			jdbcTemplateObject.update(UPDATE_TOTAL_REVIEWS, new Object[]{newTotalOfReviews, recipeName});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			throw new DataIntegrityViolationException("error updating the total number of reviews for recipe: " + recipeName);
		}
		return updateTotalReviewsCode;
	}
	
	/**
	 * @return All the recipes in the database.
	 */
	public List<Recipe> getAllRecipes() {
		List<Recipe> recipes = null;
		try {
			recipes = jdbcTemplateObject.query(GET_ALL_RECIPES, new RecipeMapper());
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not get all the recipes.");
			return null;
		}
		return recipes;
	}
	
	/**
	 * @return All the recipes in the database sorted by the highest rated recipes being displayed first.
	 */
	public List<Recipe> getRecipesByHighestRating() {
		List<Recipe> recipes = null;
		try {
			recipes = jdbcTemplateObject.query(GET_ALL_RECIPES_SORT_BY_HIGHEST_RATING, new RecipeMapper());
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not get all the recipe sorted by highest rating");
			return null;
		}
		return recipes;
	}
	
	/**
	 * @param recipeName The name of the recipe.
	 * @return Returns a recipe specified by the recipe name.
	 */
	public Recipe getRecipe(String recipeName) {
		Recipe recipe = null;
		try {
			recipe = jdbcTemplateObject.queryForObject(GET_SINGLE_RECIPE, new Object[]{recipeName}, new RecipeMapper());
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("recipe retrieval error");
		}
		return recipe;
	}
	
	/**
	 * @param recipeName The name of the recipe.
	 * @param numberServings The number of servings of the recipe.
	 * @param userPostedBy The name of the user that created the recipe.
	 * @param prepTime The time to prep the recipe in seconds.
	 * @param cookTime The time to cook the recipe in seconds.
	 * @param dishType The dish type (salad, cuisine, etc.).
	 * @param imageURL The URL to an image of the recipe.
	 * @param dateCreated The date at which the recipe was created.
	 * @param recipeDescription The description of the recipe.
	 * @param numberOneStarReviews The number of one star reviews, defautls to 0.
	 * @param numberTwoStarReviews The number of two star reviews, defaults to 0.
	 * @param numberThreeStarReviews The number of three star reviews, defaults to 0.
	 * @param numberFourStarReviews The number of four star reviews, defaults to 0.
	 * @param numberFiveStarReviews The number of five star reviews, defaults to 0.
	 * @param averageRating The average rating of the reviews for this recipe, defaults to 0.
	 * @param totalNumberOfReviews The total number of reviews for this recipe, defaults to 0.
	 * @return 1 if the recipe was inserted, -1 would imply the recipe was not inserted due to a SQL related error.
	 */
	public int insertRecipe(String recipeName, int numberServings, String userPostedBy, int prepTime, int cookTime, String dishType, String imageURL, LocalDateTime dateCreated, String recipeDescription,
			int numberOneStarReviews, int numberTwoStarReviews, int numberThreeStarReviews, int numberFourStarReviews, int numberFiveStarReviews, double averageRating, Object totalNumberOfReviews) {
		int addRecipeCode = -1;
		try {
			addRecipeCode = jdbcTemplateObject.update(ADD_RECIPE_CONTENT, new Object[]{recipeName, numberServings, userPostedBy, prepTime, cookTime, dishType, imageURL, dateCreated, recipeDescription,
					numberOneStarReviews, numberTwoStarReviews, numberThreeStarReviews, numberFourStarReviews, numberFiveStarReviews, averageRating, totalNumberOfReviews});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			throw new DataIntegrityViolationException("error inserting recipe: " + recipeName);
		}
		return addRecipeCode;
	}
	
	/**
	 * @param newNumServings The new number of servings.
	 * @param recipeName The name of the recipe to update.
	 * @return 1 if the recipe servings was updated, -1 implies that there was some sort of SQL related error.
	 */
	public int updateRecipeServings(int newNumServings, String recipeName) {
		int updateReturnCode = -1;
		try {
			updateReturnCode = jdbcTemplateObject.update(UPDATE_RECIPE_SERVINGS, new Object[]{newNumServings, recipeName});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			throw new DataIntegrityViolationException("update recipe servings error");
		}
		return updateReturnCode;
	}
	
	/**
	 * @param recipeName The name of the recipe to be removed.
	 * @param userPoster The name of the user that owns the recipe to be removed.
	 * @return 1 if the recipe was removed, -1 implies there was some SQL related error.
	 */
	public int removeList(String recipeName, String userPoster) {
		int deleteRecipeReturnCode = -1;
		try {
			deleteRecipeReturnCode = jdbcTemplateObject.update(DELETE_RECIPE, new Object[]{recipeName, userPoster});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("cannot delete recipe: " + recipeName);
		}
		return deleteRecipeReturnCode;
	}
	
	/**
	 * @param recipeName The name of the recipe to remove the ingredients from.
	 * 1 if all the ingredients were removed from the specified recipe name, -1 implies there was a SQL related error.
	 */
	public int removeAllListIngredients(String recipeName) {
		int deleteReturnCode = -1;
		try {
			deleteReturnCode = jdbcTemplateObject.update(DELETE_RECIPE_INGREDIENTS, new Object[]{recipeName});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("cannot delete recipe " + recipeName + "'s ingredients");
		}
		return deleteReturnCode;
	}
	
	/**
	 * @param recipeName The name of the recipe.
	 * @param numberServings The number of servings of the recipe.
	 * @param userName The name of the user creating the recipe.
	 * @param prepTimeConverted The time to prepare the recipe (in seconds).
	 * @param cookTimeConverted The time to cook the recipe (in seconds).
	 * @param dishType The dish type of the recipe.
	 * @param imageURL The URL of the image.
	 * @param current_time The time at which the user is creating the recipe.
	 * @param recipeDescription The description of the recipe.
	 * @param defaultQuantity The default value for the number of reviews and total reviews.
	 * @param defaultAverageRating The default average rating value.
	 * @param userDAO An object to be used to update the user's recipe posted time.
	 * @return 1 if the recipe was created and the last posted time was updated for the user, any other value implies an error or side effect.
	 */
	public int addRecipeTransaction(String recipeName, int numberServings, String userName, int prepTimeConverted, int cookTimeConverted, String dishType, String imageURL, LocalDateTime current_time, String recipeDescription, int defaultQuantity,
		double defaultAverageRating, UsersDAOImpl userDAO) {
		int addRecipeCode = -1;
		TransactionDefinition def = new DefaultTransactionDefinition();
	    TransactionStatus status = transactionManager.getTransaction(def);
	    try {
			insertRecipe(recipeName, numberServings, userName, prepTimeConverted, cookTimeConverted, dishType, imageURL, current_time, 
				recipeDescription, defaultQuantity, defaultQuantity, defaultQuantity, defaultQuantity, defaultQuantity, defaultAverageRating, defaultQuantity);
			addRecipeCode = userDAO.updateUserLastPostedRecipeTime(current_time, userName);
			transactionManager.commit(status);
	    }
	    catch(DataIntegrityViolationException e) {
	    	System.out.println(e.getMessage());
	    	transactionManager.rollback(status);
	    	addRecipeCode = -1;
	    }
	    return addRecipeCode;
	}
	
	/**
	 * @param prepTimeConverted The updated time to prepare the recipe (in seconds).
	 * @param cookTimeConverted The updated time to cook the recipe (in seconds).
	 * @param dishType The updated dish type for the recipe.
	 * @param imageURL The newly updated image URL.
	 * @param recipeDescription The newly updated recipe description.
	 * @param recipeName The name of the recipe to be updated.
	 * @param userDAO An object to be used to update the user's recipe posted time.
	 * @param current_time The time at which the recipe was updated.
	 * @param userName The name of the user owning the recipe.
	 * @return 1 if the recipe was edited and the last posted recipe time was updated, any other value implies an error or side effect.
	 */
	public int editRecipeTransaction(int prepTimeConverted, int cookTimeConverted, String dishType, String imageURL, String recipeDescription, String recipeName, UsersDAOImpl userDAO, LocalDateTime current_time, String userName) {
		int editRecipeCode = -1;
		TransactionDefinition def = new DefaultTransactionDefinition();
	    TransactionStatus status = transactionManager.getTransaction(def);
		try {
			updateRecipeContents(prepTimeConverted, cookTimeConverted, dishType, imageURL, recipeDescription, recipeName);
			editRecipeCode = userDAO.updateUserLastPostedRecipeTime(current_time, userName);
			transactionManager.commit(status);
		}
		catch(DataIntegrityViolationException e) {
			System.out.println(e.getMessage());
	    	transactionManager.rollback(status);
	    	editRecipeCode = -1;
		}
		return editRecipeCode;
	}
	
	/**
	 * @param ingredientName The name of the ingredient to add.
	 * @param wholeNumber The whole number quantity of the ingredient.
	 * @param numeratorValue The numerator quantity of the ingredient.
	 * @param denominatorValue The denominator quantity of the ingredient.
	 * @param ingredientUnit The unit of the ingredient.
	 * @param ingredientType The type of the ingredient.
	 * @param recipeName The name of the recipe to add the ingredient to.
	 * @return An ingredient object with data members populated.
	 */
	public Ingredient addRecipeIngredientTransaction(String ingredientName, int wholeNumber, int numeratorValue, int denominatorValue, String ingredientUnit, String ingredientType, String recipeName) {
		Ingredient ingredient = null;
		TransactionDefinition def = new DefaultTransactionDefinition();
	    TransactionStatus status = transactionManager.getTransaction(def);
	    try {
			addListIngredient(ingredientName, wholeNumber, numeratorValue, denominatorValue, ingredientUnit, ingredientType, recipeName);
			ingredient = getSingleIngredient(recipeName, ingredientName);
			transactionManager.commit(status);
	    }
	    catch(DataIntegrityViolationException e) {
	    	System.out.println(e.getMessage());
	    	transactionManager.rollback(status);
	    	ingredient = null;
	    }
	    return ingredient;
	}
	
	/**
	 * @param ingredientsToModify The list of ingredients to update.
	 * @param refactorFraction The amount to modify the ingredients by.
	 * @param newServings The new servings of this recipe.
	 * @param recipeName The name of the recipe to modify.
	 * @return 1 if the ingredients could be re-factored and updated, any other value implies an error or a side effect.
	 */
	public int modifyRecipeIngredientsTransaction(List<Ingredient> ingredientsToModify, Fraction refactorFraction, String newServings, String recipeName) {
		TransactionDefinition def = new DefaultTransactionDefinition();
	    TransactionStatus status = transactionManager.getTransaction(def);
	    int modifyRecipeIngredientsCode = -1;
	    Fraction oldQuantity = null;
	    UtilityServiceImpl utilityService = new UtilityServiceImpl();
	    String errorMessage = null;
	    try {
			for(Ingredient ingredients : ingredientsToModify) {
				
				int oldWholeNumber = Integer.parseInt(ingredients.getWholeNumber());
				int oldNumerator = Integer.parseInt(ingredients.getNumerator());
				int oldDenominator = Integer.parseInt(ingredients.getDenominator());
				
				oldQuantity = new Fraction((oldDenominator * oldWholeNumber) + oldNumerator, oldDenominator);
				
				errorMessage = utilityService.validateUserInput(oldWholeNumber, oldNumerator, oldDenominator);
				if(!errorMessage.equals("")) {
					modifyRecipeIngredientsCode = -1;
					break;
				}
				
				oldQuantity = oldQuantity.multiply(refactorFraction);
				
				int newWholeNumber = oldQuantity.getNumerator() / oldQuantity.getDenominator();
				int newNumerator = oldQuantity.getNumerator() % oldQuantity.getDenominator();
				int newDenominator = oldQuantity.getDenominator();
				
				modifyRecipeIngredientsCode = updateListIngredient(newWholeNumber, newNumerator, newDenominator, ingredients.getIngredientUnit(), ingredients.getIngredientType(), ingredients.getIngredientID());
			}
			modifyRecipeIngredientsCode = updateRecipeServings(Integer.parseInt(newServings), recipeName);
			transactionManager.commit(status);
	    }
	    catch(DataIntegrityViolationException e) {
	    	System.out.println(e.getMessage());
	    	transactionManager.rollback(status);
	    	modifyRecipeIngredientsCode = -1;
	    }
	    return modifyRecipeIngredientsCode;
	}
	
	
	/**
	 * @param recipeName The name of the recipe to remove all the reviews from.
	 * @return 1 if all the reviews were removed from the recipe, -1 implies some sort of SQL related error.
	 */
	public int deleteAllRecipeReviews(String recipeName) {
		int deleteReturnCode = -1;
		try {
			deleteReturnCode = jdbcTemplateObject.update(DELETE_RECIPE_REVIEWS, new Object[]{recipeName});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("cannot delete recipe " + recipeName + "'s reviews");
		}
		return deleteReturnCode;
	}
	
	/**
	 * @param recipeName The name of the recipe to delete reviews from.
	 * @param userName The name of the user to delete reviews from.
	 * @return 1 if all the reviews of a specified user were deleted.
	 */
	public int deleteAllRecipeReviewsFromUser(String recipeName, String userName) {
		int deleteReturnCode = -1;
		try {
			deleteReturnCode = jdbcTemplateObject.update(DELETE_RECIPE_REVIEWS_FROM_USER, new Object[]{recipeName, userName});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("cannot delete reviews from user: " + userName + " on recipe: " + recipeName);
		}
		return deleteReturnCode;
	}
	
	/**
	 * @param recipeName The name of the recipe to remove all directions from.
	 * @return 1 if all the directions were removed, -1 implies that there was a SQL related error.
	 */
	public int deleteAllRecipeDirections(String recipeName) {
		int deleteReturnCode = -1;
		try {
			deleteReturnCode = jdbcTemplateObject.update(DELETE_RECIPE_DIRECTIONS, new Object[]{recipeName});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("cannot delete recipe " + recipeName + "'s directions");
		}
		return deleteReturnCode;
	}
	
	/**
	 * @param recipeName The name of the recipe to get directions of.
	 * @return A list of recipe directions, null if there was some SQL related error.
	 */
	public List<RecipeDirection> getRecipeDirections(String recipeName) {
		List<RecipeDirection> recipeDirections = null;
		try {
			recipeDirections = jdbcTemplateObject.query(GET_RECIPE_DIRECTIONS, new Object[]{recipeName}, new RecipeDirectionMapper());
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			throw new DataIntegrityViolationException("cannot get directions for recipe: " + recipeName);
		}
		return recipeDirections;
	}
	
	/**
	 * @param directionNumber The number of the direction.
	 * @param directionContent The description of the direction.
	 * @param recipeName The recipe the direction is being inserted into.
	 * @return 1 if the direction was added, -1 implies that there was a SQL related error.
	 */
	public int addRecipeDirection(int directionNumber, String directionContent, String recipeName) {
		int addReturnCode = -1;
		try {
			addReturnCode = jdbcTemplateObject.update(INSERT_RECIPE_DIRECTION, new Object[]{directionNumber, directionContent, recipeName});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			throw new DataIntegrityViolationException("error adding recipe direction");
		}
		return addReturnCode;
	}
	
	/**
	 * @param directionId The id of the direction to remove
	 * @return 1 if the direction was removed, -1 implies a SQL related error.
	 */
	public int removeRecipeDirection(int directionId) {
		int removeReturnCode = -1;
		try {
			removeReturnCode = jdbcTemplateObject.update(DELETE_RECIPE_DIRECTION, new Object[]{directionId});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			throw new DataIntegrityViolationException("error removing recipe direction");
		}
		return removeReturnCode;
	}
	
	/**
	 * @param directionNumber The new direction number.
	 * @param directionContent The new direction content.
	 * @param oldDirectionNumber The old direction number.
	 * @param recipeName The name of the recipe of the direction being updated.
	 * @return 1 if the direction number was modified successfully, -1 implies there was a SQL error.
	 */
	public int updateRecipeDirection(int directionNumber, String directionContent, int oldDirectionNumber, String recipeName) {
		int updateReturnCode = -1;
		try {
			updateReturnCode = jdbcTemplateObject.update(UPDATE_RECIPE_DIRECTION, new Object[]{directionNumber, directionContent, oldDirectionNumber, recipeName});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			throw new DataIntegrityViolationException("error update recipe direction");
		}
		return updateReturnCode;
	}
	
	/**
	 * @param directionContent The new description of the direction.
	 * @param directionNumber The number of the direction to be modified.
	 * @param recipeName The name of the recipe of the direction being modified.
	 * @return 1 implies the direction content description was updated, -1 implies there was a SQL error.
	 */
	public int updateRecipeDirectionContent(String directionContent, int directionNumber, String recipeName) {
		int updateReturnCode = -1;
		try {
			updateReturnCode = jdbcTemplateObject.update(UPDATE_RECIPE_DIRECTION_CONTENT, new Object[]{directionContent, directionNumber, recipeName});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("error update recipe direction content");
		}
		return updateReturnCode;
	}
	
	/**
	 * @param recipeName The name of the recipe of the most recent added direction.
	 * @param directionNumber The direction number.
	 * @return 1 if the direction's ID could be retrieved, -1 implies that there was a SQL related error.
	 */
	public Integer getMostRecentDirectionId(String recipeName, int directionNumber) {
		Integer mostRecentDirectionId = null;
		try {
			mostRecentDirectionId = jdbcTemplateObject.queryForObject(GET_SINGLE_RECIPE_DIRECTION_ID, new Object[]{recipeName, directionNumber}, new RecentRecipeDirectionMapper());
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			throw new DataIntegrityViolationException("cannot get the recent added direction");
		}
		return mostRecentDirectionId;
	}
	
	/**
	 * @param recipeName The name of the recipe.
	 * @return 1 if the number of directions of a specified recipe could be retrieved, -1 implies there was a SQL related error.
	 */
	public Integer getNumberOfDirections(String recipeName) {
		Integer numberofRecipeDirections = null;
		try {
			numberofRecipeDirections = jdbcTemplateObject.queryForObject(GET_NUMBER_OF_DIRECTIONS, new Object[]{recipeName}, new NumberRecipeDirectionsMapper());
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("cannot get number of directions");
		}
		return numberofRecipeDirections;
	}
	
	/**
	 * @param newDirectionNumber The direction number of the added direction.
	 * @param directionDescription The direction description.
	 * @param recipeName The name of the recipe to add the direction to.
	 * @return A value of > 1 if the direction is properly added, otherwise -1 to indicate some error.
	 */
	public int addRecipeDirectionTransaction(int newDirectionNumber, String directionDescription, String recipeName) {
		int addRecipeDirectionCode = -1;
		TransactionDefinition def = new DefaultTransactionDefinition();
	    TransactionStatus status = transactionManager.getTransaction(def);
		try {
			addRecipeDirection(newDirectionNumber, directionDescription, recipeName);
			addRecipeDirectionCode = getMostRecentDirectionId(recipeName, newDirectionNumber);
			transactionManager.commit(status);
		}
		catch(DataIntegrityViolationException e) {
			System.out.println(e.getMessage());
			transactionManager.rollback(status);
			addRecipeDirectionCode = -1; // if the roll back occurs ensure that this code indicates an error.
		}
		
		return addRecipeDirectionCode;
	}
	
	/**
	 * @param directionIdValue The unique identifier of the direction to be removed.
	 * @param recipeName The recipe holding the direction to be removed.
	 * @param parsedDirectionNumber The direction number to be removed.
	 * @return 1 if the direction was deleted and the other directions were updated, -1 implies there was an error.
	 */
	public int deleteRecipeDirectionTransaction(int directionIdValue, String recipeName, int parsedDirectionNumber) {
		int deleteRecipeDirectionCode = -1;
		TransactionDefinition def = new DefaultTransactionDefinition();
	    TransactionStatus status = transactionManager.getTransaction(def);
	    try {
			removeRecipeDirection(directionIdValue);
			List<RecipeDirection> recipeDirections = getRecipeDirections(recipeName);
			// must do minus one as one direction item has been deleted already.
			for(int i = parsedDirectionNumber - 1; i < recipeDirections.size(); i++) {
				RecipeDirection modifiedDirectionsObject = recipeDirections.get(i);
				updateRecipeDirection(modifiedDirectionsObject.getDirectionNumber() - 1, modifiedDirectionsObject.getDirection(), modifiedDirectionsObject.getDirectionNumber(), recipeName);
			}
			transactionManager.commit(status);
			deleteRecipeDirectionCode = 1;
		}
	    catch(DataIntegrityViolationException e) {
	    	System.out.println(e.getMessage());
	    	deleteRecipeDirectionCode = -1;
	    	transactionManager.rollback(status);
	    }
		return deleteRecipeDirectionCode;
	}
	
	
	/**
	 * @param recipeName The name of the recipe.
	 * @return 1 if the reviews could be retrieved for the specified recipe, -1 implies there was a SQL related error.
	 */
	public List<RecipeReview> getRecipeReviews(String recipeName) {
		List<RecipeReview> recipeReviewsList = null;
		try {
			recipeReviewsList = jdbcTemplateObject.query(GET_RECIPE_REVIEWS, new Object[]{recipeName}, new RecipeReviewMapper());
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("cannot get reviews for recipe: " + recipeName);
			return null;
		}
		return recipeReviewsList;
	}
	
	/**
	 * @param reviewId The review's id.
	 * @return 1 if the review could be retrieved, -1 implies there was a SQL related error.
	 */
	public RecipeReview getRecipeReview(int reviewId) {
		RecipeReview recipeReview = null;
		try {
			recipeReview = jdbcTemplateObject.queryForObject(GET_SINGLE_RECIPE_REVIEW, new Object[]{reviewId}, new RecipeReviewMapper());
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("cannot get recipe review with id: " + reviewId);
		}
		return recipeReview;
	}	
	
	/**
	 * @param userPostedBy The name of the user that wrote the review.
	 * @param recipeName The name of the recipe.
	 * @return 1 if the recipe with the most recent posted time can be retrieved, -1 implies there was a SQL related error.
	 */
	public Integer getSingleRecipeReviewId(String userPostedBy, String recipeName) {
		Integer reviewId = null;
		try {
			reviewId = jdbcTemplateObject.queryForObject(GET_SINGLE_RECIPE_REVIEW_ID, new Object[]{userPostedBy, recipeName}, new RecipeReviewIdMapper());
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("cannot get review Id by user: " + userPostedBy);
		}
		return reviewId;
	}
	
	/**
	 * @param reviewContent The review's content.
	 * @param recipeRating The review's rating of the recipe.
	 * @param reviewTitle The title of the review.
	 * @param reviewPostedTime The time the recipe was added.
	 * @param reviewEditedTime The time the recipe was edited (defaults to the same time as the posted time).
	 * @param userPostedBy The name of the user that added the recipe.
	 * @param recipeName The name of the recipe.
	 * @return 1 if the review could be added, -1 implies there was a SQL related error.
	 */
	public int addRecipeReview(String reviewContent, int recipeRating, String reviewTitle, LocalDateTime reviewPostedTime, LocalDateTime reviewEditedTime, String userPostedBy, String recipeName) {
		int addReviewCode = -1;
		try {
			addReviewCode = jdbcTemplateObject.update(INSERT_RECIPE_REVIEW, new Object[]{reviewContent, recipeRating, reviewTitle, reviewPostedTime, reviewEditedTime, userPostedBy, recipeName});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			throw new DataIntegrityViolationException("cannot add review posted by: " + userPostedBy);
		}
		return addReviewCode;
	}
	
	/**
	 * @param reviewId The id of the review to remove.
	 * @return 1 if the specified review could be removed, -1 implies there was a SQL related error.
	 */
	public int deleteRecipeReview(int reviewId) {
		int deleteReviewCode = -1;
		try {
			deleteReviewCode = jdbcTemplateObject.update(DELETE_RECIPE_REVIEW, new Object[]{reviewId});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			throw new DataIntegrityViolationException("cannot delete review with id: " + reviewId);
		}
		return deleteReviewCode;
	}
	
	/**
	 * @param recipeRating The updated review rating of the recipe.
	 * @param reviewContent The updated review content of the recipe.
	 * @param reviewEditedTime The current time at which the review is being edited.
	 * @param reviewId The id of the review.
	 * @return 1 if the review could be updated, -1 implies there was a SQL related error.
	 */
	public int updateRecipeReview(int recipeRating, String reviewContent, LocalDateTime reviewEditedTime, int reviewId) {
		int updateReviewCode = -1;
		try {
			updateReviewCode = jdbcTemplateObject.update(UPDATE_RECIPE_REVIEW, new Object[]{recipeRating, reviewContent, reviewEditedTime, reviewId});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			throw new DataIntegrityViolationException("cannot update review with id: " + reviewId);
		}
		return updateReviewCode;
	}
	
	/**
	 * @param reviewContent The content of the review.
	 * @param recipeRating The rating of the recipe.
	 * @param reviewTitle The title of the review.
	 * @param currentTime The current time at which the review was written.
	 * @param userPostedBy The name of the user writing the review.
	 * @param recipeName The name of the recipe the review is written for.
	 * @param usersDAO An object allowing an update of when the user is writing the review.
	 * @param recipeRatingColumnName A column name to indicate the rating of the review of the recipe.
	 * @param updatedReviewQuantity The new number of ratings for this particular recipe (if the rating was 1 star, then the 1 star column value is updated by 1 more than the previous value).
	 * @param averageRating The new average rating of the recipe.
	 * @param newTotalReviews The new total number of reviews written for this recipe.
	 * @return 1 if the transaction was successful, any other value implies the transaction was not successful.
	 */
	public int addReviewTransaction(String reviewContent, int recipeRating, String reviewTitle, LocalDateTime currentTime, String userPostedBy, String recipeName, UsersDAOImpl usersDAO, String recipeRatingColumnName, 
			int updatedReviewQuantity, double averageRating, int newTotalReviews) {
		TransactionDefinition def = new DefaultTransactionDefinition();
	    TransactionStatus status = transactionManager.getTransaction(def);
	    int addReviewTransactionCode = -1;
	    try {
			addRecipeReview(reviewContent, recipeRating, reviewTitle, currentTime, currentTime, userPostedBy, recipeName);
			usersDAO.updateUserLastPostedReviewTime(currentTime, userPostedBy);
			updateRecipeRatingQuantity(recipeRatingColumnName, updatedReviewQuantity, recipeName);
			updateAverageRecipeRating(averageRating, recipeName);
			updateTotalNumberOfReviews(newTotalReviews, recipeName);
			transactionManager.commit(status);
			addReviewTransactionCode = 1;
	    }
	    catch(DataIntegrityViolationException e) {
	    	System.out.println(e.getMessage());
	    	transactionManager.rollback(status);
	    }
	    return addReviewTransactionCode;
	}
	
	/**
	 * @param reviewId The id of the review to be removed.
	 * @param usersDAO An object allowing an update of when the user is deleting the recipe.
	 * @param currentTime The current at which the review is being removed.
	 * @param userPostedBy The name of the person that wrote the review.
	 * @param recipeRatingColumnName The name of a column to have a value decremented.
	 * @param updatedReviewQuantity The new value of the number of reviews of a certain rating.
	 * @param recipeName The name of the recipe holding the review to be removed.
	 * @param averageRating The new average rating after removing the review.
	 * @param newTotalReviews The new number of reviews written after removing the review.
	 * @return 1 if the review is removed, any other value indicates an error while attempting to remove the review.
	 */
	public int removeReviewTransaction(int reviewId, UsersDAOImpl usersDAO, LocalDateTime currentTime, String userPostedBy, String recipeRatingColumnName, int updatedReviewQuantity, String recipeName, 
			double averageRating, int newTotalReviews) {
		TransactionDefinition def = new DefaultTransactionDefinition();
	    TransactionStatus status = transactionManager.getTransaction(def);
	    int removeReviewTransactionCode = -1;
	    try {
	    	deleteRecipeReview(reviewId);
	    	usersDAO.updateUserLastPostedReviewTime(currentTime, userPostedBy);
	    	updateRecipeRatingQuantity(recipeRatingColumnName, updatedReviewQuantity, recipeName);
	    	updateAverageRecipeRating(averageRating, recipeName);
	    	updateTotalNumberOfReviews(newTotalReviews, recipeName);
	    	transactionManager.commit(status);
	    	removeReviewTransactionCode = 1;
	    }
	    catch(DataIntegrityViolationException e) {
	    	System.out.println(e.getMessage());
	    	transactionManager.rollback(status);
	    }
	    return removeReviewTransactionCode;
	}
	
	/**
	 * @param recipeRating The rating of the recipe.
	 * @param reviewContent The newly entered review content of the recipe.
	 * @param currentTime The time at which the review is being updated.
	 * @param reviewIdValue A value to identify the review to be updated.
	 * @param usersDAO An object to modify when the user last updated a review.
	 * @param userName The name of the user that wrote the review.
	 * @return 1 if the review can be updated, any other value indicates an error or side effect.
	 */
	public int updateReviewTransactionNoNewRating(int recipeRating, String reviewContent, LocalDateTime currentTime, int reviewIdValue, UsersDAOImpl usersDAO, String userName) {
		TransactionDefinition def = new DefaultTransactionDefinition();
	    TransactionStatus status = transactionManager.getTransaction(def);
	    int updateReviewTransactionCode = -1;
	    try {
			updateRecipeReview(recipeRating, reviewContent, currentTime, reviewIdValue);
			usersDAO.updateUserLastPostedReviewTime(currentTime, userName);
			transactionManager.commit(status);
			updateReviewTransactionCode = 1;
	    }
	    catch(DataIntegrityViolationException e) {
	    	System.out.println(e.getMessage());
	    	transactionManager.rollback(status);
	    }
	    return updateReviewTransactionCode;
	}
	
	/**
	 * @param newRating The newly entered rating value of the recipe.
	 * @param reviewContent The newly entered review content of the recipe.
	 * @param currentTime The time at which the review is being updated.
	 * @param reviewIdValue A value to identify the review to be updated.
	 * @param usersDAO An object to modify when the user last updated a review.
	 * @param userName The name of the user that wrote the review.
	 * @param oldRatingColumnName The name of the database column to decrement a rating value from.
	 * @param oldUpdatedReviewQuantity The newly decremented value of the old rating column.
	 * @param recipeName The name of the recipe holding the review to be updated.
	 * @param newRatingColumnName The name of the database column to increment a rating value.
	 * @param newUpdatedReviewQuantity The newly incremented value to the new rating column.
	 * @param newAverageRating The new average rating of the recipe.
	 * @return 1 if the review was updated, any other value indicates an error or side effect.
	 */
	public int updateReviewTransactionWithNewRating(int newRating, String reviewContent, LocalDateTime currentTime, int reviewIdValue, UsersDAOImpl usersDAO, String userName, String oldRatingColumnName, int oldUpdatedReviewQuantity, 
		String recipeName, String newRatingColumnName, int newUpdatedReviewQuantity, double newAverageRating) {
		TransactionDefinition def = new DefaultTransactionDefinition();
	    TransactionStatus status = transactionManager.getTransaction(def);
	    int updateReviewTransactionCode = -1;
	    try {
			updateRecipeReview(newRating, reviewContent, currentTime, reviewIdValue);
			usersDAO.updateUserLastPostedReviewTime(currentTime, userName);
			updateRecipeRatingQuantity(oldRatingColumnName, oldUpdatedReviewQuantity, recipeName); // subtract from a rating column.
			updateRecipeRatingQuantity(newRatingColumnName, newUpdatedReviewQuantity, recipeName); // add to a rating column.
			updateAverageRecipeRating(newAverageRating, recipeName);
			transactionManager.commit(status);
			updateReviewTransactionCode = 1;
	    }
	    catch(DataIntegrityViolationException e) {
	    	System.out.println(e.getMessage());
	    	transactionManager.rollback(status);
	    }
	    return updateReviewTransactionCode;
	}
	
	/**
	 * @param recipeRatingColumnName The name of the rating column to be updated.
	 * @param updatedColumnQuantity The new quantity of the column.
	 * @param recipeName The recipe name that has a rating column being updated.
	 */
	private void updateRecipeRatingQuantity(String recipeRatingColumnName, int updatedColumnQuantity, String recipeName) {
		try {
			if(recipeRatingColumnName.equals(RECIPE_NUMBER_ONE_STAR_REVIEWS_COLUMN)) updateRecipeOneStarRating(updatedColumnQuantity, recipeName);
			else if(recipeRatingColumnName.equals(RECIPE_NUMBER_TWO_STAR_REVIEWS_COLUMN)) updateRecipeTwoStarRating(updatedColumnQuantity, recipeName);
			else if(recipeRatingColumnName.equals(RECIPE_NUMBER_THREE_STAR_REVIEWS_COLUMN)) updateRecipeThreeStarRating(updatedColumnQuantity, recipeName);
			else if(recipeRatingColumnName.equals(RECIPE_NUMBER_FOUR_STAR_REVIEWS_COLUMN)) updateRecipeFourStarRating(updatedColumnQuantity, recipeName);
			else if(recipeRatingColumnName.equals(RECIPE_NUMBER_FIVE_STAR_REVIEWS_COLUMN)) updateRecipeFiveStarRating(updatedColumnQuantity, recipeName);
		}
		catch(DataIntegrityViolationException e) {
			// although this is repetitive, this allows the transaction to be rolled back.
			throw new DataIntegrityViolationException(e.getMessage());
		}
	}
}
