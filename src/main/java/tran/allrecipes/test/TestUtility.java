package tran.allrecipes.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.math3.fraction.Fraction;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;

import tran.allrecipes.data.PantryListDAOImpl;
import tran.allrecipes.data.RecipeDAOImpl;
import tran.allrecipes.data.ShoppingListDAOImpl;
import tran.allrecipes.data.UsersDAOImpl;
import tran.allrecipes.presentation.model.Ingredient;
import tran.allrecipes.presentation.model.PantryIngredient;
import tran.allrecipes.presentation.model.Recipe;
import tran.allrecipes.presentation.model.User;
import tran.allrecipes.presentation.model.UserRole;
import tran.allrecipes.service.UserListServiceImpl;
import tran.allrecipes.service.UtilityServiceImpl;

/**
 * @author Todd
 * A class holding methods to help with test cases.
 */
public class TestUtility {
	/** A test account name for testing the shopping and pantry list functionalities. */
	private static final String TEST_SHOPPING_AND_PANTRY_LIST_ACCOUNT = "testuser";
	/** The URL to create a recipe.*/
	private static final String MAKE_RECIPE_URL = "/makeRecipe";
	/** The test account name. */
	private static final String TEST_ACCOUNT_NAME = "testuser12";
	/** The other test account name to test operations when not the owner of a specified list. */
	private static final String TEST_ACCOUNT_NAME_TWO = "junittestuser";
	/** The key to access the stored password. */
	private static final String PASSWORD_KEY = "testAccountPassword";
	/** The location of the data source file. */
	private static final String DATABASE_SOURCE_FILE = "database/Datasource.xml";
	/** The path to the file containing the test account's password. */
	private static final String TEST_ACCOUNT_PASSWORD_FILE_PATH = "/properties/testAccountCredentials.properties";
	/** Name of the recipes DAO bean. */
	private static final String RECIPE_DAO_BEAN_NAME = "RecipesDAO";
	/** Name of the users DAO bean. */
	private static final String USER_DAO_BEAN_NAME = "UsersDAO";
	/** Name of the recipe DAO bean. */
	private static final String PANTRY_DAO_BEAN_NAME = "PantryListDAO";
	/** Name of the shopping list DAO bean. */
	private static final String SHOPPING_LIST_DAO_BEAN_NAME = "ShoppingListDAO";
	/** 
	 * The name of an already existing recipe. 
	 * This is necessary because the unit tests have no guarantee to execute in sequence.
	 */
	private static final String EXISTING_RECIPE_NAME = "chicken salad recipe";
	/** A test recipe name for creation purposes only. */
	private static final String TEST_RECIPE_NAME = "testuser12's junit test recipe";
	/** The recipe name with the ingredients with proper units and types. */
	private static final String RECIPE_WITH_PROPER_TYPES_AND_UNITS = "junit test recipe";
	/** The recipe with an ingredient that has a type that is non matching. */
	private static final String RECIPE_WITH_TYPE_MISMATCH = "recipe with type mismatch";
	/** The recipe with an ingredient that has a unit that is non matching. */
	private static final String RECIPE_WITH_UNIT_MISMATCH = "recipe with unit mismatch";
	/** The recipe with ingredients to test all allowed unit conversions. */
	private static final String RECIPE_WITH_ALL_UNIT_CONVERSIONS = "recipe to test unit conversions";
	/** A test recipe name that does not exist in the database. */
	private static final String NONEXISTANT_RECIPE_NAME = "this recipe doesn't exist";
	/** An existing recipe created by another user that has one review in it. */
	private static final String EXISTING_RECIPE_OTHER_USER = "recipe tester";
	/** An updated test servings amount. */
	private static final int TEST_SERVINGS_AMOUNT_UPDATED = 4;
	/** An old test servings amount. */
	private static final int OLD_TEST_SERVINGS_AMOUNT = 2;
	/** A prep time string, hours/minutes/seconds. */
	private static final String TEST_PREP_TIME = "1/0/0";
	/** A cook time string, hours/minutes/seconds. */
	private static final String TEST_COOK_TIME = "1/0/0";
	/** The test recipe's dish type. */
	private static final String TEST_DISH_TYPE = "World Cuisine";
	/** The test recipe's description. */
	private static final String TEST_DESCRIPTION = "Brief description.";
	/** Invalid recipe name. */
	private static final String INVALID_RECIPE_NAME = "not a recipe name";
	/** The test ingredient name. */
	private static final String TEST_INGREDIENT_NAME = "pepperoni slice";
	/** The test image URL. */
	private static final String TEST_URL = ""; // no image
	/** The test ingredient unit. */
	private static final String TEST_INGREDIENT_UNIT = "lbs.";
	/** The whole number portion of the ingredient quantity. */
	private static final String TEST_INGREDIENT_WHOLE_NUMBER = "2";
	/** The fraction portion of the ingredient quantity.*/
	private static final String TEST_INGREDIENT_FRACTION_QUANTITY = "0/1";
	/** The type of the ingredient. */
	private static final String TEST_INGREDIENT_TYPE = "Meat";
	/** A test type for the ingredient, useful for updates. */
	private static final String TEST_INGREDIENT_TYPE_TWO = "Other";
	/** Invalid ingredient Id. */
	private static final String INVALID_INGREDIENT_ID = "-1";
	/** Test recipe direction content. */
	private static final String TEST_DIRECTION_CONTENT = "This is a test direction string.";
	/** An test direction number, this is an invalid value. */
	private static final String INVALID_TEST_DIRECTION_NUMBER = "-1";
	/** A test direction ID value, this is an invalid value. */
	private static final String INVALID_TEST_DIRECTION_ID = "-1";
	/** A value for the review, used for incorrect test cases. */
	private static final String INVALID_REVIEW_ID = "-1";
	/** A value for the review rating. */
	private static final String REVIEW_RATING = "5";
	/** Test review contents. */
	private static final String REVIEW_CONTENT = "This is test review content.";
	/** Test review contents. */
	private static final String REVIEW_TITLE = "This is a test title";
	/** A message to indicate that the review contents were changed but the rating stayed the same. */
	private static final String REVIEW_CONTENT_CHANGED_RATING_SAME = "error updating the review.";
	/** The parameter to indicate what recipe to add to. */
	private static final String RECIPE_NAME_PARAM = "recipeName";
	/** The parameter to set the recipe's servings value. */
	private static final String SERVINGS_FIELD_PARAM = "numServings";
	/** The parameter to set the recipe's prep time. */
	private static final String PREP_TIME_PARAM = "prepTimeUnparsed";
	/** The parameter to set the recipe's cook time. */
	private static final String COOK_TIME_PARAM = "cookTimeUnparsed";
	/** The parameter to set the recipe's dish type. */
	private static final String DISH_TYPE_PARAM = "dishType";
	/** The parameter to set the recipe's image URL. */
	private static final String IMAGE_URL_PARAM = "imageURL";
	/** The parameter to set the recipe's description. */
	private static final String RECIPE_DESCRIPTION_PARAM = "recipeDescription";
	/** Expected list size value after addition. */
	private static final int EXPECTED_LIST_SIZE_AFTER_ADDITION = 1;
	/** Expected list size after deletion. */
	private static final int EXPECTED_EMPTY_LIST_SIZE = 0;
	
	/** Retrieve the contents of the test user account. */
	public User getTestAccount() {
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
		UsersDAOImpl usersDAO = (UsersDAOImpl)appContext.getBean(USER_DAO_BEAN_NAME);
		User user = usersDAO.getUser(TEST_ACCOUNT_NAME); 
		usersDAO = null;
		((ConfigurableApplicationContext) appContext).close();
		return user;
	}
	
	/** Retrieve the permissions associated with the test account. */
	public List<UserRole> getTestAccountRoles() {
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
		UsersDAOImpl usersDAO = (UsersDAOImpl)appContext.getBean(USER_DAO_BEAN_NAME);
		List<UserRole> userRoles = usersDAO.getUserRoles(TEST_ACCOUNT_NAME);
		usersDAO = null;
		((ConfigurableApplicationContext) appContext).close();
		return userRoles;
	}
	
	/**
	 * @return The existing recipe name.
	 */
	public String getExistingRecipeName() {
		return EXISTING_RECIPE_NAME;
	}
	
	/**
	 * @return The test recipe name.
	 */
	public String getTestRecipeName() {
		return TEST_RECIPE_NAME;
	}
	
	/**
	 * @return A recipe name created by another user.
	 */
	public String getExistingRecipeFromOtherUser() {
		return EXISTING_RECIPE_OTHER_USER;
	}
	
	/**
	 * @return The account name for testing the pantry and shopping lists.
	 */
	public String getShoppingAndPantryTestAccountName() {
		return TEST_SHOPPING_AND_PANTRY_LIST_ACCOUNT;
	}
	
	/**
	 * @return The updated test number of servings.
	 */
	public int getTestServingsUpdated() {
		return TEST_SERVINGS_AMOUNT_UPDATED;
	}
	
	public int getOldServings() {
		return OLD_TEST_SERVINGS_AMOUNT;
	}
	
	/**
	 * @return The test prep time.
	 */
	public String getTestPrepTime() {
		return TEST_PREP_TIME;
	}
	
	/**
	 * @return The test cook time.
	 */
	public String getTestCookTime() {
		return TEST_COOK_TIME;
	}
	
	/**
	 * @return The test dish type.
	 */
	public String getTestDishType() {
		return TEST_DISH_TYPE;
	}
	
	/**
	 * @return The test description.
	 */
	public String getTestDescription() {
		return TEST_DESCRIPTION;
	}
	
	/**
	 * @return The invalid recipe name.
	 */
	public String getInvalidRecipeName() {
		return INVALID_RECIPE_NAME;
	}
	
	/**
	 * @return The test account name.
	 */
	public String getTestAccountName() {
		return TEST_ACCOUNT_NAME;
	}
	
	/**
	 * @return The test account name that does not own a recipe.
	 */
	public String getTestAccountNameTwo() {
		return TEST_ACCOUNT_NAME_TWO; 
	}
	
	/**
	 * @return non-existant recipe name.
	 */
	public String getNonExistantRecipeName() {
		return NONEXISTANT_RECIPE_NAME;
	}
	
	/**
	 * @return The test ingredient's name.
	 */
	public String getTestIngredientName() {
		return TEST_INGREDIENT_NAME;
	}
	
	/**
	 * @return The test ingredient's unit.
	 */
	public String getTestIngredientUnit() {
		return TEST_INGREDIENT_UNIT;
	}
	
	/**
	 * @return The test ingredient's whole number quantity.
	 */
	public String getTestIngredientWholeNumber() {
		return TEST_INGREDIENT_WHOLE_NUMBER;
	}
	
	/**
	 * @return The test ingredient's fraction quantity.
	 */
	public String getTestIngredientFractionQuantity() {
		return TEST_INGREDIENT_FRACTION_QUANTITY;
	}
	
	/**
	 * @return The categorical type of the ingredient.
	 */
	public String getTestIngredientType() {
		return TEST_INGREDIENT_TYPE;
	}
	
	/**
	 * @return A categorical type for testing purposes.
	 */
	public String getTestIngredientOtherType() {
		return TEST_INGREDIENT_TYPE_TWO;
	}

	/**
	 * @return An invalid ingredient id for testing a delete that will not work.
	 */
	public String getInvalidIngredientId() {
		return INVALID_INGREDIENT_ID;
	}
	
	/**
	 * @return The test direction content.
	 */
	public String getTestDirectionContent() {
		return TEST_DIRECTION_CONTENT;
	}
	
	/**
	 * @return The invalid test direction number.
	 */
	public String getInvalidTestDirectionNumber() {
		return INVALID_TEST_DIRECTION_NUMBER;
	}
	
	/**
	 * @return The invalid test direction Id.
	 */
	public String getInvalidTestDirectionId() {
		return INVALID_TEST_DIRECTION_ID;
	}
	
	/**
	 * @return An invalid review Id.
	 */
	public String getInvalidReviewId() {
		return INVALID_REVIEW_ID;
	}
	
	/**
	 * @return A review rating string.
	 */
	public String getReviewRating() {
		return REVIEW_RATING;
	}
	
	/**
	 * @return The review's content.
	 */
	public String getTestReviewContent() {
		return REVIEW_CONTENT;
	}
	
	/**
	 * @return The review's title.
	 */
	public String getTestReviewTitle() {
		return REVIEW_TITLE;
	}
	
	/**
	 * @return The size of the list after adding an element.
	 */
	public int getExpectedListSizeAfterAddition() {
		return EXPECTED_LIST_SIZE_AFTER_ADDITION;
	}
	
	/**
	 * @return The size of the list when empty
	 */
	public int getExpectedEmptyListSize() {
		return EXPECTED_EMPTY_LIST_SIZE;
	}
	
	/**
	 * @return The expected message when the rating is not changed but the review content is.
	 */
	public String getReviewContentChangedRatingSameMessage() {
		return REVIEW_CONTENT_CHANGED_RATING_SAME;
	}
	
	/**
	 * @return Gets the password from the file if it exists, otherwise will return null if a password cannot be retrieved.
	 */
	public String getTestAccountPassword() {
		Properties prop = new Properties();
		InputStream input = null;
		String password = null;
		try {
			input = UtilityServiceImpl.class.getResourceAsStream(TEST_ACCOUNT_PASSWORD_FILE_PATH);
			if(input != null) {
				prop.load(input); 
				password= prop.getProperty(PASSWORD_KEY);
			}
		}
		catch(IOException e) {
			System.out.println("cannot retrieve the test account's password");
		}
		return password;
	}
	
	/**
	 * @param timeStringToConvert The string to parse into an integer value.
	 * @return An integer value in seconds of the passed in string.
	 */
	public int convertTimeToSeconds(String timeStringToConvert) {
		String[] parsedTimeString = timeStringToConvert.split("/");
		if(parsedTimeString.length == 3) {
			return new UtilityServiceImpl().convertEnteredTime(parsedTimeString[0], parsedTimeString[1], parsedTimeString[2]);
		}
		return 0;
	}
	
	/**
	 * Recipe Test Section
	 */
	
	/**
	 * @param recipeToRemove The name of the recipe to remove.
	 * @param recipeOwnerName The owner of the recipe.
	 * helper method to remove a recipe.
	 */
	public void removeTestRecipe(String recipeToRemove, String recipeOwnerName) {
		// if the recipe already exists, remove it and set last creation time to null.
		// create the recipe with the necessary ingredient(s) and set last creation time to null.
		// add the ingredients.
		ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
		RecipeDAOImpl recipeDAO = (RecipeDAOImpl) appContext.getBean(RECIPE_DAO_BEAN_NAME);
		UsersDAOImpl usersDAOImpl = (UsersDAOImpl) appContext.getBean(USER_DAO_BEAN_NAME);
		Recipe testRecipe = recipeDAO.getRecipe(recipeToRemove);
		if(testRecipe != null) {
			recipeDAO.removeList(testRecipe.getRecipeName(), testRecipe.getUserOwner());
			usersDAOImpl.updateUserLastPostedRecipeTime(null, recipeOwnerName);
		}
		usersDAOImpl = null;
		recipeDAO = null;
		((ConfigurableApplicationContext) appContext).close();
	}
	
	/**
	 * @param recipeToAdd The name of the recipe to create.
	 * @param recipeCreator The user name that has created the recipe.
	 * helper method to add a recipe for testing. all recipes created in this way will have alot of similar values.
	 */
	public void addTestRecipe(String recipeToAdd, String recipeCreator) {
		ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
		RecipeDAOImpl recipeDAO = (RecipeDAOImpl) appContext.getBean(RECIPE_DAO_BEAN_NAME);
		UsersDAOImpl usersDAOImpl = (UsersDAOImpl) appContext.getBean(USER_DAO_BEAN_NAME);
		Recipe testRecipe = recipeDAO.getRecipe(recipeToAdd);
		if(testRecipe == null) {
			int prepTime = convertTimeToSeconds(TEST_PREP_TIME);
			int cookTime  = convertTimeToSeconds(TEST_COOK_TIME);
			int defaultNumberOfReviews = 0; // since this recipe is being created the amount of review ratings should all be zero.
			recipeDAO.insertRecipe(recipeToAdd, OLD_TEST_SERVINGS_AMOUNT, recipeCreator, prepTime, cookTime, TEST_DISH_TYPE, TEST_URL, null, TEST_DESCRIPTION, 
					defaultNumberOfReviews, defaultNumberOfReviews, defaultNumberOfReviews, defaultNumberOfReviews, defaultNumberOfReviews, defaultNumberOfReviews, defaultNumberOfReviews);
			usersDAOImpl.updateUserLastPostedRecipeTime(null, recipeCreator);
		}
		usersDAOImpl = null;
		recipeDAO = null;
		((ConfigurableApplicationContext) appContext).close();
	}
	
	/**
	 * @param ingredientList The ingredients to be added to the recipe.
	 * @param recipeName The name of the recipe to add to.
	 * helper method that takes in an ingredient object and adds it to a specified recipe, used for testing purposes only.
	 */
	public void addIngredientToTestRecipe(List<Ingredient> ingredientList, String recipeName) {
		ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
		RecipeDAOImpl recipeDAO = (RecipeDAOImpl) appContext.getBean(RECIPE_DAO_BEAN_NAME);
		Recipe testRecipe = recipeDAO.getRecipe(recipeName);
		if(testRecipe != null) { // recipe must exist before adding to it.
			for(Ingredient ingredient : ingredientList) {
				recipeDAO.addListIngredient(ingredient.getIngredientName(), Integer.parseInt(ingredient.getWholeNumber()), Integer.parseInt(ingredient.getNumerator()), 
						Integer.parseInt(ingredient.getDenominator()), ingredient.getIngredientUnit(), ingredient.getIngredientType(), recipeName);
			}
		}
		recipeDAO = null;
		((ConfigurableApplicationContext) appContext).close();
	}
		
	/**
	 * Adds ingredients into the junit test recipe recipe.
	 */
	public void getIngredientsForTestRecipeListWithItems() {
		String[] ingredientPortion = getTestFractionPortion(TEST_INGREDIENT_FRACTION_QUANTITY);
		assertNotEquals(null, ingredientPortion);
		
		String testIngredientName = "celery";
		String testWholeNumber = "1";
		String testNumerator = ingredientPortion[0];
		String testDenominator = ingredientPortion[1];
		String testIngredientUnit = "lbs.";
		String testIngredientType = "Vegetables"; 
		Ingredient ingredient = new Ingredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, testIngredientUnit, testIngredientType);
		List<Ingredient> testIngredients = new LinkedList<Ingredient>();
		testIngredients.add(ingredient);
		testIngredientName = "sugar";
		testIngredientUnit = "tsp.";
		testIngredientType = "Other";
		ingredient = new Ingredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, testIngredientUnit, testIngredientType);
		testIngredients.add(ingredient);
			
		addIngredientToTestRecipe(testIngredients, RECIPE_WITH_PROPER_TYPES_AND_UNITS);
	}
	
	/**
	 * Inserts an ingredient into the recipe with type mismatch recipe for testing.
	 */
	public void getIngredientForTestRecipeListWithTypeMisMatch() {
		String[] ingredientPortion = getTestFractionPortion(TEST_INGREDIENT_FRACTION_QUANTITY);
		assertNotEquals(null, ingredientPortion);
		
		String testIngredientName = "sugar";
		String testWholeNumber = "1";
		String testNumerator = ingredientPortion[0];
		String testDenominator = ingredientPortion[1];
		String testIngredientUnit = "pcs.";
		String testIngredientType = "Meat"; 
		Ingredient ingredient = new Ingredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, testIngredientUnit, testIngredientType);
		List<Ingredient> testIngredients = new LinkedList<Ingredient>();
		testIngredients.add(ingredient);

		addIngredientToTestRecipe(testIngredients, RECIPE_WITH_TYPE_MISMATCH);
	}
			
	/**
	 * Inserts an ingredient into the recipe with unit mismatch recipe for testing.
	 * @return An ingredient which can be used to get the ingredient's name with the unit mismatch.
	 */
	public Ingredient getIngredientForTestRecipeListWithUnitMisMatch() {
		String[] ingredientPortion = getTestFractionPortion(TEST_INGREDIENT_FRACTION_QUANTITY);
		assertNotEquals(null, ingredientPortion);
		
		String testIngredientName = "celery";
		String testWholeNumber = "1";
		String testNumerator = ingredientPortion[0];
		String testDenominator = ingredientPortion[1];
		String testIngredientUnit = "bags";
		String testIngredientType = "Vegetables"; 
		Ingredient ingredient = new Ingredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, testIngredientUnit, testIngredientType);
		List<Ingredient> testIngredients = new LinkedList<Ingredient>();
		testIngredients.add(ingredient);

		addIngredientToTestRecipe(testIngredients, RECIPE_WITH_UNIT_MISMATCH);
		
		return testIngredients.size() == 1 ? testIngredients.get(0) : null;
	}
		
	/**
	 * Inserts ingredients into the recipe to test unit conversions recipe to test for all allowed unit conversions.
	 */
	public void populateRecipeWithAllUnitConversionTests() {
		String[] ingredientPortion = getTestFractionPortion(TEST_INGREDIENT_FRACTION_QUANTITY);
		assertNotEquals(null, ingredientPortion);
		
		String testIngredientName = "petFoodOne";
		String testWholeNumber = "1";
		String testNumerator = ingredientPortion[0];
		String testDenominator = ingredientPortion[1];
		String testIngredientUnit = "tbsp.";
		String testIngredientType = "Pet Food"; 
		Ingredient ingredient = new Ingredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, testIngredientUnit, testIngredientType);
		List<Ingredient> testIngredients = new LinkedList<Ingredient>();
		testIngredients.add(ingredient);
		
		testIngredientName = "petFoodTwo";
		testIngredientUnit = "tsp.";
		ingredient = new Ingredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, testIngredientUnit, testIngredientType);
		testIngredients.add(ingredient);
		
		testIngredientName = "petFoodThree";
		testIngredientUnit = "lbs.";
		ingredient = new Ingredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, testIngredientUnit, testIngredientType);
		testIngredients.add(ingredient);
		
		testIngredientName = "petFoodFour";
		testIngredientUnit = "oz.";
		ingredient = new Ingredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, testIngredientUnit, testIngredientType);
		testIngredients.add(ingredient);
		
		testIngredientName = "petFoodFive";
		testIngredientUnit = "qt";
		ingredient = new Ingredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, testIngredientUnit, testIngredientType);
		testIngredients.add(ingredient);
		
		testIngredientName = "petFoodSix";
		testIngredientUnit = "oz.";
		ingredient = new Ingredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, testIngredientUnit, testIngredientType);
		testIngredients.add(ingredient);
		
		testIngredientName = "petFoodSeven";
		testIngredientUnit = "c.";
		ingredient = new Ingredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, testIngredientUnit, testIngredientType);
		testIngredients.add(ingredient);
		
		testIngredientName = "petFoodEight";
		testIngredientUnit = "tbsp.";
		ingredient = new Ingredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, testIngredientUnit, testIngredientType);
		testIngredients.add(ingredient);
		
		
		testIngredientName = "petFoodNine";
		testIngredientUnit = "c.";
		ingredient = new Ingredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, testIngredientUnit, testIngredientType);
		testIngredients.add(ingredient);
		
		testIngredientName = "petFoodTen";
		testIngredientUnit = "tsp.";
		ingredient = new Ingredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, testIngredientUnit, testIngredientType);
		testIngredients.add(ingredient);
		
		addIngredientToTestRecipe(testIngredients, RECIPE_WITH_ALL_UNIT_CONVERSIONS);
	}
	
	/** 
	 * Shopping List Section!! 
	 */
	
	/**
	 * @param shoppingListName The name of the shopping list to add to.
	 * @param shoppingListOwner The owner of the shopping list.
	 * Inserts ingredients into a shopping list.
	 */
	public void populateShoppingListWithAllUnitConversionTests(String shoppingListName, String shoppingListOwner) {
		String[] ingredientPortion = getTestFractionPortion(TEST_INGREDIENT_FRACTION_QUANTITY);
		assertNotEquals(null, ingredientPortion);
		
		String testIngredientName = "petFoodOne";
		String testWholeNumber = "1";
		String testNumerator = ingredientPortion[0];
		String testDenominator = ingredientPortion[1];
		String testIngredientUnit = "tsp.";
		String testIngredientType = "Pet Food"; 
		Ingredient ingredient = new Ingredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, testIngredientUnit, testIngredientType);
		List<Ingredient> testIngredients = new LinkedList<Ingredient>();
		testIngredients.add(ingredient);
		
		testIngredientName = "petFoodTwo";
		testIngredientUnit = "tbsp.";
		ingredient = new Ingredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, testIngredientUnit, testIngredientType);
		testIngredients.add(ingredient);
		
		testIngredientName = "petFoodThree";
		testIngredientUnit = "oz.";
		ingredient = new Ingredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, testIngredientUnit, testIngredientType);
		testIngredients.add(ingredient);
		
		testIngredientName = "petFoodFour";
		testIngredientUnit = "lbs.";
		ingredient = new Ingredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, testIngredientUnit, testIngredientType);
		testIngredients.add(ingredient);
		
		testIngredientName = "petFoodFive";
		testIngredientUnit = "oz.";
		ingredient = new Ingredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, testIngredientUnit, testIngredientType);
		testIngredients.add(ingredient);
		
		testIngredientName = "petFoodSix";
		testIngredientUnit = "qt";
		ingredient = new Ingredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, testIngredientUnit, testIngredientType);
		testIngredients.add(ingredient);
		
		testIngredientName = "petFoodSeven";
		testIngredientUnit = "tbsp.";
		ingredient = new Ingredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, testIngredientUnit, testIngredientType);
		testIngredients.add(ingredient);
		
		testIngredientName = "petFoodEight";
		testIngredientUnit = "c.";
		ingredient = new Ingredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, testIngredientUnit, testIngredientType);
		testIngredients.add(ingredient);
		
		
		testIngredientName = "petFoodNine";
		testIngredientUnit = "tsp.";
		ingredient = new Ingredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, testIngredientUnit, testIngredientType);
		testIngredients.add(ingredient);
		
		testIngredientName = "petFoodTen";
		testIngredientUnit = "c.";
		ingredient = new Ingredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, testIngredientUnit, testIngredientType);
		testIngredients.add(ingredient);
		
		addIngredientToTestShoppingList(testIngredients, shoppingListName, shoppingListOwner);
	}
	
	/**
	 * @param ingredientList The ingredients to be added to the recipe.
	 * @param shoppingListName The name of the shopping list to add to.
	 * @param shoppingListOwner The owner of the shopping list.
	 * helper method that takes in an ingredient object and adds it to a specified recipe, used for testing purposes only.
	 */
	public void addIngredientToTestShoppingList(List<Ingredient> ingredientList, String shoppingListName, String shoppingListOwner) {
		ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
		ShoppingListDAOImpl shoppingListDAO = (ShoppingListDAOImpl) appContext.getBean(SHOPPING_LIST_DAO_BEAN_NAME);
		String listOwner = shoppingListDAO.getUserOwnerOfList(shoppingListName);
		if(shoppingListOwner.equals(listOwner)) { // this will be true if a shopping list exists and the owner is the same as the shopping list's owner.
			for(Ingredient ingredient : ingredientList) {
				try {
					shoppingListDAO.addListIngredient(ingredient.getIngredientName(), Integer.parseInt(ingredient.getWholeNumber()), Integer.parseInt(ingredient.getNumerator()), 
						Integer.parseInt(ingredient.getDenominator()), ingredient.getIngredientUnit(), ingredient.getIngredientType(), shoppingListName);
				}
				catch(DataIntegrityViolationException e) {
					System.out.println(e.getMessage());
					break;
				}
			}
		}
		shoppingListDAO = null;
		((ConfigurableApplicationContext) appContext).close();
	}
	
	/**
	 * @param shoppingListName The shopping list name to add to.
	 * @param shoppingListOwner The name of the owner of the shopping list.
	 * helper method to add to a shopping list with a type mismatch for testing.
	 */
	public void addToShoppingListTypeMismatch(String shoppingListName, String shoppingListOwner) {
		String[] ingredientPortion = getTestFractionPortion(TEST_INGREDIENT_FRACTION_QUANTITY);
		assertNotEquals(null, ingredientPortion);
		
		String testIngredientName = "sugar";
		String testWholeNumber = "1";
		String testNumerator = ingredientPortion[0];
		String testDenominator = ingredientPortion[1];
		String testIngredientUnit = "pcs.";
		String testIngredientType = "Other"; 
		Ingredient ingredient = new Ingredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, testIngredientUnit, testIngredientType);
		List<Ingredient> testIngredients = new LinkedList<Ingredient>();
		testIngredients.add(ingredient);
		addIngredientToTestShoppingList(testIngredients, shoppingListName, shoppingListOwner);
	}
	
	/**
	 * @param shoppingListName The shopping list name to add to.
	 * @param shoppingListOwner The name of the owner of the shopping list.
	 * helper method to add to a shopping list with a unit mismatch for testing.
	 */
	public void addToShoppingListUnitMismatch(String shoppingListName, String shoppingListOwner) {
		String[] ingredientPortion = getTestFractionPortion(TEST_INGREDIENT_FRACTION_QUANTITY);
		assertNotEquals(null, ingredientPortion);
		
		String testIngredientName = "celery";
		String testWholeNumber = "1";
		String testNumerator = ingredientPortion[0];
		String testDenominator = ingredientPortion[1];
		String testIngredientUnit = "pcs.";
		String testIngredientType = "Vegetables"; 
		Ingredient ingredient = new Ingredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, testIngredientUnit, testIngredientType);
		List<Ingredient> testIngredients = new LinkedList<Ingredient>();
		testIngredients.add(ingredient);
		addIngredientToTestShoppingList(testIngredients, shoppingListName, shoppingListOwner);
	}
	
	/**
	 * @param shoppingListName The shopping list to delete from.
	 * @param shoppingListOwner The owner of the shopping list.
	 * helper method to delete from a specified shopping list.
	 */
	public void removeShoppingListIngredients(String shoppingListName, String shoppingListOwner) {
		ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
		ShoppingListDAOImpl shoppingListDAO = (ShoppingListDAOImpl) appContext.getBean(SHOPPING_LIST_DAO_BEAN_NAME);
		List<Ingredient> shoppingListIngredients = shoppingListDAO.getListIngredients(shoppingListName);
		String listOwner = shoppingListDAO.getUserOwnerOfList(shoppingListName);
		if(shoppingListIngredients != null && listOwner.equals(shoppingListOwner)) {
			shoppingListDAO.removeAllListIngredients(shoppingListName);
		}
		shoppingListDAO = null;
		((ConfigurableApplicationContext) appContext).close();
	}
	
	/** 
	 * Pantry List Section!! 
	 */
	
	/**
	 * @param ingredientOne The pantry ingredient with the expected values.
	 * @param ingredientTwo The pantry ingredient with the subtraction update done.
	 * @return -1 if the first ingredient is less than the second, 0 if they are equal, and 1 if the first is greater.
	 */
	public int compareFractionObjects(PantryIngredient ingredientOne, PantryIngredient ingredientTwo) {
		Fraction ingredientOneQuantity = new Fraction((Integer.parseInt(ingredientOne.getWholeNumber()) * Integer.parseInt(ingredientOne.getDenominator())) + Integer.parseInt(ingredientOne.getNumerator()), Integer.parseInt(ingredientOne.getDenominator()));
		Fraction ingredientTwoQuantity = new Fraction((Integer.parseInt(ingredientTwo.getWholeNumber()) * Integer.parseInt(ingredientTwo.getDenominator())) + Integer.parseInt(ingredientTwo.getNumerator()), Integer.parseInt(ingredientTwo.getDenominator()));
		return ingredientOneQuantity.compareTo(ingredientTwoQuantity);
	}
	
	/**
	 * Inserts a pantry ingredient into the recipe with type mismatch recipe for testing.
	 */
	public void addPantryIngredientForTestPantryListWithTypeMisMatch(String pantryListName, String pantryListOwner) {
		String[] ingredientPortion = getTestFractionPortion(TEST_INGREDIENT_FRACTION_QUANTITY);
		assertNotEquals(null, ingredientPortion);
		UserListServiceImpl userService = new UserListServiceImpl();
		
		String testIngredientName = "sugar";
		String testWholeNumber = "1";
		String testNumerator = ingredientPortion[0];
		String testDenominator = ingredientPortion[1];
		Fraction thresholdFraction = userService.getThresholdValues(Integer.parseInt(testWholeNumber), Integer.parseInt(testNumerator), Integer.parseInt(testDenominator));
		String ingredientThresholdWholeNumber = String.valueOf(thresholdFraction.getNumerator() / thresholdFraction.getDenominator());
		String ingredientThresholdNumerator = String.valueOf(thresholdFraction.getNumerator() % thresholdFraction.getDenominator());
		String ingredientThresholdDenominator = String.valueOf(thresholdFraction.getDenominator());
		String testIngredientUnit = "pcs.";
		String testIngredientType = "Meat"; 
		PantryIngredient ingredient = new PantryIngredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, ingredientThresholdWholeNumber, ingredientThresholdNumerator, ingredientThresholdDenominator, testIngredientUnit, testIngredientType);
		List<PantryIngredient> testIngredients = new LinkedList<PantryIngredient>();
		testIngredients.add(ingredient);

		addIngredientToTestPantryList(testIngredients, pantryListName, pantryListOwner);
	}
	
	/**
	 * Inserts a pantry ingredient into a pantry list with unit mismatch recipe for testing.
	 * @return A pantry ingredient which can be used to get the ingredient's name with the unit mismatch.
	 */
	public PantryIngredient addPantryIngredientForTestPantryListWithUnitMisMatch(String pantryListName, String pantryListOwner) {
		String[] ingredientPortion = getTestFractionPortion(TEST_INGREDIENT_FRACTION_QUANTITY);
		assertNotEquals(null, ingredientPortion);
		UserListServiceImpl userService = new UserListServiceImpl();
		
		String testIngredientName = "celery";
		String testWholeNumber = "1";
		String testNumerator = ingredientPortion[0];
		String testDenominator = ingredientPortion[1];
		Fraction thresholdFraction = userService.getThresholdValues(Integer.parseInt(testWholeNumber), Integer.parseInt(testNumerator), Integer.parseInt(testDenominator));
		String ingredientThresholdWholeNumber = String.valueOf(thresholdFraction.getNumerator() / thresholdFraction.getDenominator());
		String ingredientThresholdNumerator = String.valueOf(thresholdFraction.getNumerator() % thresholdFraction.getDenominator());
		String ingredientThresholdDenominator = String.valueOf(thresholdFraction.getDenominator());
		String testIngredientUnit = "bags";
		String testIngredientType = "Vegetables"; 
		PantryIngredient ingredient = new PantryIngredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, ingredientThresholdWholeNumber, ingredientThresholdNumerator, ingredientThresholdDenominator, testIngredientUnit, testIngredientType);
		List<PantryIngredient> testIngredients = new LinkedList<PantryIngredient>();
		testIngredients.add(ingredient);

		addIngredientToTestPantryList(testIngredients, pantryListName, pantryListOwner);
		
		return testIngredients.size() == 1 ? testIngredients.get(0) : null;
	}
	
	/**
	 * @param pantryListName The name of the pantry list that will be populated.
	 * @param pantryListOwner The owner of the pantry list.
	 * inserts pantry ingredients into a list.
	 */
	public void populatePantryListWithAllUnitConversionsForTesting(String pantryListName, String pantryListOwner) {
		String[] ingredientPortion = getTestFractionPortion(TEST_INGREDIENT_FRACTION_QUANTITY);
		assertNotEquals(null, ingredientPortion);
		UserListServiceImpl userService = new UserListServiceImpl();
		
		String testIngredientName = "petFoodOne";
		String testWholeNumber = "1";
		String testNumerator = ingredientPortion[0];
		String testDenominator = ingredientPortion[1];
		Fraction thresholdFraction = userService.getThresholdValues(Integer.parseInt(testWholeNumber), Integer.parseInt(testNumerator), Integer.parseInt(testDenominator));
		String ingredientThresholdWholeNumber = String.valueOf(thresholdFraction.getNumerator() / thresholdFraction.getDenominator());
		String ingredientThresholdNumerator = String.valueOf(thresholdFraction.getNumerator() % thresholdFraction.getDenominator());
		String ingredientThresholdDenominator = String.valueOf(thresholdFraction.getDenominator());
		String testIngredientUnit = "tbsp.";
		String testIngredientType = "Pet Food"; 
		PantryIngredient ingredient = new PantryIngredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, ingredientThresholdWholeNumber, ingredientThresholdNumerator, ingredientThresholdDenominator, testIngredientUnit, testIngredientType);
		List<PantryIngredient> testIngredients = new LinkedList<PantryIngredient>();
				
		testIngredients.add(ingredient);
		
		testIngredientName = "petFoodTwo";
		testIngredientUnit = "tsp.";
		ingredient = new PantryIngredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, ingredientThresholdWholeNumber, ingredientThresholdNumerator, ingredientThresholdDenominator, testIngredientUnit, testIngredientType);
		testIngredients.add(ingredient);
		
		testIngredientName = "petFoodThree";
		testIngredientUnit = "lbs.";
		ingredient = new PantryIngredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, ingredientThresholdWholeNumber, ingredientThresholdNumerator, ingredientThresholdDenominator, testIngredientUnit, testIngredientType);
		testIngredients.add(ingredient);
		
		testIngredientName = "petFoodFour";
		testIngredientUnit = "oz.";
		ingredient = new PantryIngredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, ingredientThresholdWholeNumber, ingredientThresholdNumerator, ingredientThresholdDenominator, testIngredientUnit, testIngredientType);
		testIngredients.add(ingredient);
		
		testIngredientName = "petFoodFive";
		testIngredientUnit = "qt";
		ingredient = new PantryIngredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, ingredientThresholdWholeNumber, ingredientThresholdNumerator, ingredientThresholdDenominator, testIngredientUnit, testIngredientType);
		testIngredients.add(ingredient);
		
		testIngredientName = "petFoodSix";
		testIngredientUnit = "oz.";
		ingredient = new PantryIngredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, ingredientThresholdWholeNumber, ingredientThresholdNumerator, ingredientThresholdDenominator, testIngredientUnit, testIngredientType);
		testIngredients.add(ingredient);
		
		testIngredientName = "petFoodSeven";
		testIngredientUnit = "c.";
		ingredient = new PantryIngredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, ingredientThresholdWholeNumber, ingredientThresholdNumerator, ingredientThresholdDenominator, testIngredientUnit, testIngredientType);
		testIngredients.add(ingredient);
		
		testIngredientName = "petFoodEight";
		testIngredientUnit = "tbsp.";
		ingredient = new PantryIngredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, ingredientThresholdWholeNumber, ingredientThresholdNumerator, ingredientThresholdDenominator, testIngredientUnit, testIngredientType);
		testIngredients.add(ingredient);
		
		
		testIngredientName = "petFoodNine";
		testIngredientUnit = "c.";
		ingredient = new PantryIngredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, ingredientThresholdWholeNumber, ingredientThresholdNumerator, ingredientThresholdDenominator, testIngredientUnit, testIngredientType);
		testIngredients.add(ingredient);
		
		testIngredientName = "petFoodTen";
		testIngredientUnit = "tsp.";
		ingredient = new PantryIngredient(testIngredientName, testWholeNumber, testNumerator, testDenominator, ingredientThresholdWholeNumber, ingredientThresholdNumerator, ingredientThresholdDenominator, testIngredientUnit, testIngredientType);
		testIngredients.add(ingredient);
		
		addIngredientToTestPantryList(testIngredients, pantryListName, pantryListOwner);
	}
	
	/**
	 * @param pantryListName The name of the pantry list that will be populated.
	 * @param pantryListOwner The owner of the pantry list.
	 * inserts pantry ingredients into a database to be used for testing.
	 */
	public void addIngredientToTestPantryList(List<PantryIngredient> pantryList, String pantryListName, String pantryListOwner) {
		ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
		PantryListDAOImpl pantryListDAO = (PantryListDAOImpl) appContext.getBean(PANTRY_DAO_BEAN_NAME);
		String listOwner = pantryListDAO.getUserOwnerOfList(pantryListName);
		if(pantryListOwner.equals(listOwner)) { // this will be true if a pantry list exists and the owner is the same as the pantry list's owner. 
			for(PantryIngredient ingredient : pantryList) {
				pantryListDAO.addListIngredient(ingredient.getIngredientName(), Integer.parseInt(ingredient.getWholeNumber()), Integer.parseInt(ingredient.getNumerator()), 
					Integer.parseInt(ingredient.getDenominator()), Integer.parseInt(ingredient.getThresholdWholeNumber()), Integer.parseInt(ingredient.getThresholdNumerator()), 
					Integer.parseInt(ingredient.getThresholdDenominator()), ingredient.getIngredientUnit(), ingredient.getIngredientType(), pantryListName, true, false);
			}
		}
		pantryListDAO = null;
		((ConfigurableApplicationContext) appContext).close();
	}
	
	/**
	 * @param pantryListName The name of the pantry list whose ingredients will be removed.
	 * @param pantryListOwner The owner of the pantry list.
	 * removes pantry list ingredients from a specified pantry list.
	 */
	public void removePantryListIngredients(String pantryListName, String pantryListOwner) {
		ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
		PantryListDAOImpl pantryListDAO = (PantryListDAOImpl) appContext.getBean(PANTRY_DAO_BEAN_NAME);
		String listOwner = pantryListDAO.getUserOwnerOfList(pantryListName);
		List<PantryIngredient> pantryListIngredients = pantryListDAO.getListIngredients(pantryListName);
		if(pantryListIngredients != null && listOwner.equals(pantryListOwner)) {
			pantryListDAO.removeAllListIngredients(pantryListName);
		}
		pantryListDAO = null;
		((ConfigurableApplicationContext) appContext).close();
	}
	
	/**
	 * @param mvc An object to perform a post request.
	 * @param recipesDAO An object to retrieve a recipe.
	 * @param recipeName The name of the recipe.
	 * @param servings The servings of the recipe.
	 * @param prepTime The amount of time to prep the recipe.
	 * @param cookTime The amount of time to cook the recipe.
	 * @param dishType The type of the recipe (salad, world cuisine).
	 * @param recipeDescription The description of the recipe.
	 * @throws Exception If there is a post error.
	 */
	public void helpMakeRecipe(MockMvc mvc, RecipeDAOImpl recipesDAO, String recipeName, String servings, String prepTime, String cookTime, String dishType, String recipeDescription) throws Exception {
		mvc.perform(post(MAKE_RECIPE_URL).param(RECIPE_NAME_PARAM, recipeName).param(SERVINGS_FIELD_PARAM, servings).param(PREP_TIME_PARAM, prepTime).
			param(COOK_TIME_PARAM, cookTime).param(DISH_TYPE_PARAM, dishType).param(IMAGE_URL_PARAM, "").param(RECIPE_DESCRIPTION_PARAM, recipeDescription).with(csrf()));
		
		Recipe newlyCreatedRecipe = recipesDAO.getRecipe(recipeName);
		
		assertEquals(recipeName, newlyCreatedRecipe.getRecipeName());
		assertEquals(Integer.parseInt(servings), newlyCreatedRecipe.getNumServings());
		assertEquals(convertTimeToSeconds(prepTime), newlyCreatedRecipe.getPrepTime());
		assertEquals(convertTimeToSeconds(cookTime), newlyCreatedRecipe.getCookTime());
		assertEquals(dishType, newlyCreatedRecipe.getDishType());
		assertEquals(recipeDescription, newlyCreatedRecipe.getRecipeDescription());
	}
	
	/**
	 * @return Splits up the TEST_INGREDIENT_FRACTION_QUANTITY (0/1) data member into two strings to be used for insertion purposes.
	 */
	public String[] getTestFractionPortion(String fractionToSplit) {
		String[] fractionQuantity = fractionToSplit.split("/");
		if(fractionQuantity.length == 2) {
			//if(fractionQuantity[0].equals("0") && fractionQuantity[1].equals("1"))
				return fractionQuantity;
		}
		return null;
	}
	
}
