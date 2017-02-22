package tran.allrecipes.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import tran.allrecipes.data.RecipeDAOImpl;
import tran.allrecipes.data.UsersDAOImpl;
import tran.allrecipes.presentation.model.Ingredient;
import tran.allrecipes.presentation.model.RecipeDirection;
import tran.allrecipes.presentation.model.RecipeReview;
import tran.allrecipes.presentation.model.User;
import tran.allrecipes.presentation.model.UserRole;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=TestConfig.class)
@WebAppConfiguration
public class SingleRecipeTest {
	/** The URL mapping to show a single recipe. */
	private static final String SHOW_SINGLE_RECIPE_PAGE = "showSingleRecipe";
	/** The URL mapping to add an ingredient to a recipe. */
	private static final String ADD_RECIPE_INGREDIENT = "addRecipeIngredient";
	/** The URL mapping to modify all ingredients on a recipe. */
	private static final String MODIFY_RECIPE_INGREDIENTS = "modifyRecipeIngredients";
	/** The URL mapping to update a single ingredient from a recipe. */
	private static final String UPDATE_SINGLE_INGREDIENT = "updateSingleIngredient";
	/** The URL mapping to remove an ingredient from a recipe. */
	private static final String DELETE_SINGLE_INGREDIENT = "removeRecipeIngredient";
	/** The URL mapping to add a direction to a recipe. */
	private static final String ADD_RECIPE_DIRECTION = "addRecipeDirection";
	/** The URL mapping to modify a direction in a given recipe. */
	private static final String UPDATE_RECIPE_DIRECTION = "updateRecipeDirectionContent";
	/** The URL mapping to remove a direction from a recipe. */
	private static final String REMOVE_RECIPE_DIRECTION = "removeRecipeDirection";
	/** The URL mapping to add a review. */
	private static final String ADD_RECIPE_REVIEW = "addRecipeReview";
	/** The URL mapping to remove a review.  */
	private static final String REMOVE_RECIPE_REVIEW = "removeRecipeReview";
	/** The URL mapping to update a review. */
	private static final String UPDATE_RECIPE_REVIEW = "updateRecipeReviewContent";
	/** The logged in user name parameter. */
	private static final String LOGGED_IN_USER_NAME_PARAM = "loggedInName";
	/** The parameter to take in the recipe name. */
	private static final String RECIPE_NAME_PARAM = "recipeName";
	/** The parameter to take in the ingredient's recipe name. */
	private static final String INGREDIENT_RECIPE_NAME_PARAM = "ingredientListName";
	/** The ingredient name parameter. */
	private static final String INGREDIENT_NAME_PARAM = "ingredientName";
	/** The ingredient unit parameter. */
	private static final String INGREDIENT_UNIT_PARAM = "ingredientUnit";
	/** The whole number quantity parameter. */
	private static final String WHOLE_NUMBER_QUANTITY_PARAM = "wholeNumber";
	/** The fraction quantity parameter. */
	private static final String INGREDIENT_QUANTITY_PARAM = "ingredientFractionQuantity";
	/** The ingredient type parameter. */
	private static final String INGREDIENT_TYPE_PARAM = "ingredientType";
	/** The new servings parameter for modifying all the ingredients in the recipe. */
	private static final String NEW_SERVINGS_PARAM = "newServings";
	/** The old servings parameter for modifying all the ingredients in the recipe. */
	private static final String OLD_SERVINGS_PARAM = "oldServings";
	/** The parameter to set the ingredient ID used as a unique identifier for a specific ingredient. */
	private static final String INGREDIENT_ID_PARAM = "ingredientID";
	/** The parameter to specify what recipe's directions will have content added. */
	private static final String DIRECTION_CONTENT_PARAM = "direction";
	/** The parameter to specify what direction will be deleted.  */
	private static final String DIRECTION_ID_PARAM = "directionId";
	/** The parameter to specify what direction will be deleted. */
	private static final String DIRECTION_NUMBER_PARAM = "directionNumber";
	/** The parameter to set the review's content. */
	private static final String REVIEW_CONTENT_PARAM = "reviewContent";
	/** The parameter to set the review's rating of the recipe. */
	private static final String RECIPE_REVIEW_RATING_PARAM = "reviewRating";
	/** The parameter to set the review of the title. */
	private static final String REVIEW_TITLE_PARAM = "reviewTitle";
	/** The parameter to specify who wrote the review. */
	private static final String USER_POSTED_BY_PARAM = "userNamePosted";
	/** The parameter to uniquely identify a review. */
	private static final String REVIEW_ID_PARAM = "reviewId";
	/** The message parameter. */
	private static final String MESSAGE_PARAM = "message";
	/** A message that is displayed when the user is not the owner of the recipe and trying to update individual ingredient(s). */
	private static final String UPDATE_RECIPE_INGREDIENT_ERROR = "you must be the owner of the recipe to update an ingredient.";
	/** A message that is displayed when attempting to update recipe servings when the user is not the owner of the recipe.*/
	private static final String UPDATE_RECIPE_INGREDIENTS_ERROR = "you are not the owner of the recipe so you cannot modify the servings.";
	/** A message to notify the user that the recipe direction cannot be updated. */
	private static final String RECIPE_DIRECTION_CANNOT_BE_UPDATED = "you must be the owner of the recipe to update a direction.";
	/** A message to notify the user that the recipe direction cannot be removed. */
	private static final String RECIPE_DIRECTION_CANNOT_BE_REMOVED = "you must be the owner of the recipe to remove directions from it.";
	/** A message indicating the review cannot be updated. */
	private static final String REVIEW_UPDATE_FAILED_MESSAGE = "you are not the poster so you cannot edit the review.";
	/** A message to indicate the review cannot be found while trying to update. */
	private static final String REVIEW_UPDATE_CANNOT_BE_FOUND = "cannot get the review you are attempting to edit.";
	/** A message indicating the review cannot be found. */
	private static final String REVIEW_RECIPE_NOT_FOUND = "cannot get the recipe.";
	/** The location of the data source file. */
	private static final String DATABASE_SOURCE_FILE = "database/Datasource.xml";
	/** Name of the users DAO bean. */
	private static final String USER_DAO_BEAN_NAME = "UsersDAO";
	/** Name of the recipes DAO bean. */
	private static final String RECIPE_DAO_BEAN_NAME = "RecipesDAO";
	/** Object to access utility methods used for testing. */
	private static final TestUtility TEST_UTILITY = new TestUtility();
	/** HTTP status code for an unauthorized access. */
	private static final int EXPECTED_UNAUTHORIZED_CODE = Integer.parseInt(HttpStatus.UNAUTHORIZED.toString());
	/** HTTP status code for a successful get or post request. */
	private static final int EXPECTED_SUCCESS_CODE = Integer.parseInt(HttpStatus.OK.toString());;
	/** HTTP status code for a server error when attempting to perform an add/remove/update operation. */
	private static final int EXPECTED_INTERNAL_SERVER_ERROR_CODE = Integer.parseInt(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	/** An existing recipe name. */
	private static final String EXISTING_RECIPE_NAME = TEST_UTILITY.getExistingRecipeName();
	/** Object to hold a user's information. */
	private static final User TEST_USER = TEST_UTILITY.getTestAccount();;
	/** List to hold a user's role(s). */
	private static final List<UserRole> TEST_ACCOUNT_ROLE = TEST_UTILITY.getTestAccountRoles();;
	/** A test account's user name. */
	private static final String USER_NAME = TEST_USER.getUsername();
	/** Another test account's user name. */
	private static final String SECOND_ACCOUNT_USER_NAME = TEST_UTILITY.getTestAccountNameTwo();
	/** A test account's encrypted password. */
	private static final String PASSWORD = TEST_USER.getPassword();
	/** A non-existent recipe name for testing. */
	private static final String INVALID_RECIPE_NAME = TEST_UTILITY.getInvalidRecipeName();
	/** An ingredient name for testing. */
	private static final String TEST_INGREDIENT_NAME = TEST_UTILITY.getTestIngredientName();
	/** An ingredient's unit for testing. */
	private static final String TEST_INGREDIENT_UNIT = TEST_UTILITY.getTestIngredientUnit();
	/** An ingredient whole number for testing. */
	private static final String TEST_INGREDIENT_WHOLE_NUMBER = TEST_UTILITY.getTestIngredientWholeNumber();
	/** A fraction (represented as a string) for testing. */
	private static final String TEST_INGREDIENT_FRACTION = TEST_UTILITY.getTestIngredientFractionQuantity();
	/** An ingredient's type for testing. */
	private static final String TEST_INGREDIENT_TYPE = TEST_UTILITY.getTestIngredientType();
	/** An ingredient's updated type for testing. */
	private static final String TEST_INGREDIENT_UPDATE_TYPE = TEST_UTILITY.getTestIngredientOtherType();
	/** The new servings amount when refactoring ingredients in a recipe. */
	private static final String NEW_SERVINGS_AMOUNT = String.valueOf(TEST_UTILITY.getTestServingsUpdated());
	/** The old servings amount when refactoring ingredients in a recipe. */
	private static final String OLD_SERVINGS_AMOUNT = String.valueOf(TEST_UTILITY.getOldServings());
	/** A test string for a direction. */
	private static final String DIRECTION_CONTENT = TEST_UTILITY.getTestDirectionContent();
	/** An invalid direction number. */
	private static final String INVALID_DIRECTION_NUMBER = TEST_UTILITY.getInvalidTestDirectionNumber();
	/** An invalid direction ID. */
	private static final String INVALID_DIRECTION_ID = TEST_UTILITY.getInvalidTestDirectionId();
	/** Test contents of a review. */
	private static final String TEST_REVIEW_CONTENTS = TEST_UTILITY.getTestReviewContent();
	/** A test rating of a recipe from a review. */
	private static final String TEST_REVIEW_RATING = TEST_UTILITY.getReviewRating();
	/** A review title for a test review. */
	private static final String TEST_REVIEW_TITLE = TEST_UTILITY.getTestReviewTitle();
	/** A non existent recipe*/
	private static final String NON_EXISTENT_RECIPE_NAME = TEST_UTILITY.getNonExistantRecipeName();
	/** An invalid id for a review. */
	private static final String INVALID_REVIEW_ID = TEST_UTILITY.getInvalidReviewId();
	/** A message indicating that a review has been changed by the rating was not modified. */
	private static final String REVIEW_UPDATE_CONTENT_CHANGED_REVIEW_SAME_MESSAGE = TEST_UTILITY.getReviewContentChangedRatingSameMessage();
	/** An object to get access to the current class's application context. */
	private static ClassPathXmlApplicationContext appContext;
	/** An object to gain access to the Recipes, RecipeReviews, RecipeIngredients and RecipeDirections database tables. */
	private static RecipeDAOImpl recipesObject;
	/** An object to allow tests to modify a user's last posted time, used as a hack to bypass the 30 second review wait time. */
	private static UsersDAOImpl usersDAO;
	/** The expected size of a list that is empty. */
	private static final int EXPECTED_EMPTY_LIST_SIZE = TEST_UTILITY.getExpectedEmptyListSize();
	/** The expected size of a list after an item is added. */
	private static final int EXPECTED_LIST_SIZE_AFTER_ADDITION = TEST_UTILITY.getExpectedListSizeAfterAddition();
	/** Object to set the application context for the tests below. */
	@Autowired
	private WebApplicationContext context;
	/** Object to allow testing for Spring MVC. */
	private MockMvc mvc;
	
	/** Initial assignment of a data member before every test. */
	@Before
	public void setup() {
		mvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.build();
	}
	
	/** Initial assignment of a data member before test execution. */
	@BeforeClass
	public static void setUp() {
		// database related objects.
		appContext = new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
		recipesObject = (RecipeDAOImpl)appContext.getBean(RECIPE_DAO_BEAN_NAME);
		usersDAO = (UsersDAOImpl) appContext.getBean(USER_DAO_BEAN_NAME);
	}
	
	/** Closes data members. */
	@AfterClass
	public static void cleanUp() {
		recipesObject = null;
		usersDAO = null;
		((ConfigurableApplicationContext) appContext).close();
	}
	
	/**
	 * Tests if a selected recipe can be viewed when not logged in. Expected to get back the showSinglePage.
	 * @throws Exception If there is a get failure.
	 */
	@Test
	public void viewSpecifiedRecipe() throws Exception {
		MvcResult mvcResult = mvc.perform(get("/" + SHOW_SINGLE_RECIPE_PAGE).param(RECIPE_NAME_PARAM, EXISTING_RECIPE_NAME)).andReturn();
		ModelAndView modelAndView = mvcResult.getModelAndView();
		assertEquals(SHOW_SINGLE_RECIPE_PAGE, modelAndView.getViewName());
	}
	
	/**
	 * Tests if a selected recipe can be viewed when logged in. A model attribute with the user's name and
	 * the view name of showSingleRecipe is expected to be returned.
	 * @throws Exception If there is a get failure.
	 */
	@Test
	public void viewSpecifiedRecipe_LoggedIn() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(USER_NAME, PASSWORD, TEST_ACCOUNT_ROLE));
		MvcResult mvcResult = mvc.perform(get("/" + SHOW_SINGLE_RECIPE_PAGE).param(RECIPE_NAME_PARAM, EXISTING_RECIPE_NAME)).andReturn();
		ModelAndView modelAndView = mvcResult.getModelAndView();
		ModelMap modelMap = modelAndView.getModelMap();
		assertEquals(USER_NAME, modelMap.get(LOGGED_IN_USER_NAME_PARAM));
		assertEquals(SHOW_SINGLE_RECIPE_PAGE, modelAndView.getViewName());
	}
	
	/**
	 * For each of the operations since the user is not logged in, an unauthorized error message and status code is expected.
	 * @throws Exception If there is a get or post failure.
	 */
	@Test
	public void testRecipeIngredientFunctionality_NotLoggedIn_WithInvalidRecipeName() throws Exception {
		addRecipeIngredientHelper(EXPECTED_UNAUTHORIZED_CODE, INVALID_RECIPE_NAME, TEST_INGREDIENT_NAME, TEST_INGREDIENT_UNIT, TEST_INGREDIENT_WHOLE_NUMBER, TEST_INGREDIENT_FRACTION, TEST_INGREDIENT_TYPE);
		modifyRecipeIngredientsHelper(UPDATE_RECIPE_INGREDIENTS_ERROR, NEW_SERVINGS_AMOUNT, INVALID_RECIPE_NAME, OLD_SERVINGS_AMOUNT);
		modifySingleRecipeIngredientHelper(UPDATE_RECIPE_INGREDIENT_ERROR, TEST_INGREDIENT_NAME, INVALID_RECIPE_NAME, TEST_INGREDIENT_WHOLE_NUMBER, TEST_INGREDIENT_FRACTION, TEST_INGREDIENT_UNIT, TEST_INGREDIENT_UPDATE_TYPE);
		deleteSingleRecipeIngredientHelper(EXPECTED_UNAUTHORIZED_CODE, TEST_UTILITY.getInvalidIngredientId(), INVALID_RECIPE_NAME);
	}
		
	/**
	 * Since the user is not the owner of a non-existing recipe an unauthorized status message and status code is expected to be returned from the tests.
	 * @throws Exception If there is a get or post failure.
	 */
	@Test
	public void testRecipeIngredientFunctionality_LoggedIn_WithInvalidRecipeName() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(USER_NAME, PASSWORD, TEST_ACCOUNT_ROLE));
		addRecipeIngredientHelper(EXPECTED_UNAUTHORIZED_CODE, INVALID_RECIPE_NAME, TEST_INGREDIENT_NAME, TEST_INGREDIENT_UNIT, TEST_INGREDIENT_WHOLE_NUMBER, TEST_INGREDIENT_FRACTION, TEST_INGREDIENT_TYPE);
		modifyRecipeIngredientsHelper(UPDATE_RECIPE_INGREDIENTS_ERROR, NEW_SERVINGS_AMOUNT, INVALID_RECIPE_NAME, OLD_SERVINGS_AMOUNT);
		modifySingleRecipeIngredientHelper(UPDATE_RECIPE_INGREDIENT_ERROR, TEST_INGREDIENT_NAME, INVALID_RECIPE_NAME, TEST_INGREDIENT_WHOLE_NUMBER, TEST_INGREDIENT_FRACTION, TEST_INGREDIENT_UNIT, TEST_INGREDIENT_UPDATE_TYPE);
		deleteSingleRecipeIngredientHelper(EXPECTED_UNAUTHORIZED_CODE, TEST_UTILITY.getInvalidIngredientId(), INVALID_RECIPE_NAME);
	}

	/**
	 * Since the user is not logged in, for the operations an unauthorized status code and message is expected from all the tests.
	 * @throws Exception If there is a get or post failure.
	 */
	@Test
	public void testRecipeIngredientFunctionality_NotLoggedIn_WithValidRecipeName() throws Exception {
		// for all the cases the "not acceptable" code is expected to be returned as the user is not logged in.
		addRecipeIngredientHelper(EXPECTED_UNAUTHORIZED_CODE, EXISTING_RECIPE_NAME, TEST_INGREDIENT_NAME, TEST_INGREDIENT_UNIT, TEST_INGREDIENT_WHOLE_NUMBER, TEST_INGREDIENT_FRACTION, TEST_INGREDIENT_TYPE);
		modifyRecipeIngredientsHelper(UPDATE_RECIPE_INGREDIENTS_ERROR, NEW_SERVINGS_AMOUNT, EXISTING_RECIPE_NAME, OLD_SERVINGS_AMOUNT);
		modifySingleRecipeIngredientHelper(UPDATE_RECIPE_INGREDIENT_ERROR, TEST_INGREDIENT_NAME, EXISTING_RECIPE_NAME, TEST_INGREDIENT_WHOLE_NUMBER, TEST_INGREDIENT_FRACTION, TEST_INGREDIENT_UNIT, TEST_INGREDIENT_UPDATE_TYPE);
		deleteSingleRecipeIngredientHelper(EXPECTED_UNAUTHORIZED_CODE, TEST_UTILITY.getInvalidIngredientId(), EXISTING_RECIPE_NAME);
	}
	
	/**
	 * Since the logged in user is not the recipe owner an unauthorized status code and message are expected to be returned from all tests.
	 * @throws Exception If there is a get or post failure.
	 */
	@Test
	public void testRecipeIngredientFunctionality_LoggedIn_WithValidRecipeName() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(SECOND_ACCOUNT_USER_NAME, PASSWORD, TEST_ACCOUNT_ROLE));
		addRecipeIngredientHelper(EXPECTED_UNAUTHORIZED_CODE, EXISTING_RECIPE_NAME, TEST_INGREDIENT_NAME, TEST_INGREDIENT_UNIT, TEST_INGREDIENT_WHOLE_NUMBER, TEST_INGREDIENT_FRACTION, TEST_INGREDIENT_TYPE);
		modifyRecipeIngredientsHelper(UPDATE_RECIPE_INGREDIENTS_ERROR, NEW_SERVINGS_AMOUNT, EXISTING_RECIPE_NAME, OLD_SERVINGS_AMOUNT);
		modifySingleRecipeIngredientHelper(UPDATE_RECIPE_INGREDIENT_ERROR, TEST_INGREDIENT_NAME, EXISTING_RECIPE_NAME, TEST_INGREDIENT_WHOLE_NUMBER, TEST_INGREDIENT_FRACTION, TEST_INGREDIENT_UNIT, TEST_INGREDIENT_UPDATE_TYPE);
		deleteSingleRecipeIngredientHelper(EXPECTED_UNAUTHORIZED_CODE, TEST_UTILITY.getInvalidIngredientId(), EXISTING_RECIPE_NAME);
	}
	
	/**
	 * Since the user is logged in and the recipe owner the expected return error messages should be null (no messages indicating failure should be returned).
	 * The recipe name returned from each operation should be the recipe name that the user is the owner of.
	 * @throws Exception If there is a get or post failure.
	 */
	@Test
	public void testRecipeIngredientFunctionality_LoggedIn_AsRecipeOwner() throws Exception {
		ModelMap modelMap = null;
		try {
			recipesObject.removeAllListIngredients(EXISTING_RECIPE_NAME);
			SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(USER_NAME, PASSWORD, TEST_ACCOUNT_ROLE));
			addRecipeIngredientHelper(EXPECTED_SUCCESS_CODE, EXISTING_RECIPE_NAME, TEST_INGREDIENT_NAME, TEST_INGREDIENT_UNIT, TEST_INGREDIENT_WHOLE_NUMBER, TEST_INGREDIENT_FRACTION, TEST_INGREDIENT_TYPE);
			Ingredient addedIngredient = recipesObject.getSingleIngredient(EXISTING_RECIPE_NAME, TEST_INGREDIENT_NAME);
			assertNotEquals(null, addedIngredient);
			modelMap = modifyRecipeIngredientsHelper(null, NEW_SERVINGS_AMOUNT, EXISTING_RECIPE_NAME, OLD_SERVINGS_AMOUNT);
			assertEquals(EXISTING_RECIPE_NAME, modelMap.get(RECIPE_NAME_PARAM));
			modelMap = modifySingleRecipeIngredientHelper(null, TEST_INGREDIENT_NAME, EXISTING_RECIPE_NAME, TEST_INGREDIENT_WHOLE_NUMBER, TEST_INGREDIENT_FRACTION, TEST_INGREDIENT_UNIT, TEST_INGREDIENT_UPDATE_TYPE);
			assertEquals(EXISTING_RECIPE_NAME, modelMap.get(RECIPE_NAME_PARAM));
			deleteSingleRecipeIngredientHelper(EXPECTED_SUCCESS_CODE, String.valueOf(addedIngredient.getIngredientID()), EXISTING_RECIPE_NAME);
			addedIngredient = recipesObject.getSingleIngredient(EXISTING_RECIPE_NAME, TEST_INGREDIENT_NAME);
			assertEquals(null, addedIngredient);
		}
		finally {
			recipesObject.removeAllListIngredients(EXISTING_RECIPE_NAME);
		}
	}
	
	/**
	 * @param expectedStatus The expected status code
	 * @param recipeName The name of the recipe this ingredient is being added.
	 * @param ingredientName The ingredient's name.
	 * @param ingredientUnit The ingredient unit.
	 * @param ingredientWholeNumber The ingredient whole number.
	 * @param ingredientFractionQuantity The ingredient quantity's fraction.
	 * @param ingredientType The ingredient type.
	 * @throws Exception If there is a post failure.
	 */
	private void addRecipeIngredientHelper(int expectedStatus, String recipeName, String ingredientName, String ingredientUnit, String ingredientWholeNumber, String ingredientFractionQuantity, String ingredientType) throws Exception {
		MvcResult mvcResult = mvc.perform(post("/" + ADD_RECIPE_INGREDIENT).param(INGREDIENT_RECIPE_NAME_PARAM, recipeName).param(INGREDIENT_NAME_PARAM, ingredientName)
				.param(INGREDIENT_UNIT_PARAM, ingredientUnit).param(WHOLE_NUMBER_QUANTITY_PARAM, ingredientWholeNumber).param(INGREDIENT_QUANTITY_PARAM, ingredientFractionQuantity)
				.param(INGREDIENT_TYPE_PARAM, ingredientType).with(csrf())).andReturn(); 
		assertEquals(expectedStatus, mvcResult.getResponse().getStatus()); 
	}
	
	/**
	 * @param expectedMessageString The expected message from the recipe ingredients update.
	 * @param newServingsAmount The new servings amount.
	 * @param recipeName The recipe name to modify.
	 * @param oldServingsAmount The old servings amount.
	 * @throws Exception If there is a post failure.
	 * @return A model map object to get a message for testing.
	 */
	private ModelMap modifyRecipeIngredientsHelper(String expectedMessageString, String newServingsAmount, String recipeName, String oldServingsAmount) throws Exception {
		MvcResult mvcResult = mvc.perform(post("/" + MODIFY_RECIPE_INGREDIENTS).param(NEW_SERVINGS_PARAM, newServingsAmount).param(RECIPE_NAME_PARAM, recipeName)
				.param(OLD_SERVINGS_PARAM, oldServingsAmount).with(csrf())).andReturn(); 
		ModelAndView modelAndView = mvcResult.getModelAndView();
		ModelMap modelMap = modelAndView.getModelMap();
		assertEquals(expectedMessageString, modelMap.get(MESSAGE_PARAM)); 
		return modelMap;
	}
	
	/**
	 * @param expectedReturnMessaage The expected reeturn message when attempting to update a recipe single ingredient.
	 * @param ingredientName The name of the ingredient to update.
	 * @param recipeName The name of the recipe to update.
	 * @param wholeNumber The whole number quantity to update to.
	 * @param ingredientFraction The fraction amount to update to.
	 * @param ingredientUnit The unit of the updated ingredient.
	 * @param ingredientType The type of the updated ingredient.
	 * @throws Exception If there is a post failure.
	 * @return A model map object to get a message for testing.
	 */
	private ModelMap modifySingleRecipeIngredientHelper(String expectedReturnMessaage, String ingredientName, String recipeName, String wholeNumber, String ingredientFraction, String ingredientUnit, String ingredientType) throws Exception {
		MvcResult mvcResult = mvc.perform(post("/" + UPDATE_SINGLE_INGREDIENT).param(INGREDIENT_NAME_PARAM, ingredientName).param(INGREDIENT_RECIPE_NAME_PARAM, recipeName).param(WHOLE_NUMBER_QUANTITY_PARAM, wholeNumber)
				.param(INGREDIENT_QUANTITY_PARAM, ingredientFraction).param(INGREDIENT_UNIT_PARAM, ingredientUnit).param(INGREDIENT_TYPE_PARAM, ingredientType).with(csrf())).andReturn(); 
		ModelAndView modelAndView = mvcResult.getModelAndView();
		ModelMap modelMap = modelAndView.getModelMap();
		assertEquals(expectedReturnMessaage, modelMap.get(MESSAGE_PARAM)); 
		return modelMap;
	}
	
	/**
	 * @param expectedReturnCode The expected HTTP status code.
	 * @param ingredientId The id of the ingredient to delete.
	 * @param recipeName The name of the recipe to delete the ingredient from.
	 * @throws Exception If there is a post failure.
	 */
	private void deleteSingleRecipeIngredientHelper(int expectedReturnCode, String ingredientId, String recipeName) throws Exception {
		MvcResult mvcResult = mvc.perform(post("/" + DELETE_SINGLE_INGREDIENT).param(INGREDIENT_ID_PARAM, ingredientId).param(RECIPE_NAME_PARAM, recipeName).with(csrf())).andReturn(); 
		assertEquals(expectedReturnCode, mvcResult.getResponse().getStatus()); 
	}
	
	
	/**
	 * Since the user is not logged in and the recipe does not exist it is expected that the operations are expected to return a 
	 * status code of unauthorized and messages of a user being unauthorized.
	 * @throws Exception If there is a get or post failure.
	 */
	@Test
	public void testRecipeDirectionFunctionality_NotLoggedIn_OnNonExistentRecipe() throws Exception {
		addRecipeDirectionHelper(EXPECTED_UNAUTHORIZED_CODE, EXPECTED_EMPTY_LIST_SIZE, INVALID_RECIPE_NAME, DIRECTION_CONTENT, null);
		updateRecipeDirectionHelper(RECIPE_DIRECTION_CANNOT_BE_UPDATED, INVALID_DIRECTION_NUMBER, INVALID_RECIPE_NAME, DIRECTION_CONTENT);
		removeRecipeDirectionHelper(RECIPE_DIRECTION_CANNOT_BE_REMOVED, EXPECTED_EMPTY_LIST_SIZE, INVALID_DIRECTION_ID, INVALID_RECIPE_NAME, INVALID_DIRECTION_NUMBER, null);
	}
	
	/**
	 * Although the user is logged in, since the recipe does not exist the operations are expected to return a status code
	 * of unauthorized and messages of a user being unauthorized.
	 * @throws Exception If there is a get or post failure.
	 */
	@Test
	public void testRecipeDirectionFunctionality_LoggedIn_OnNonExistentRecipe() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(USER_NAME, PASSWORD, TEST_ACCOUNT_ROLE));
		addRecipeDirectionHelper(EXPECTED_UNAUTHORIZED_CODE, EXPECTED_EMPTY_LIST_SIZE, INVALID_RECIPE_NAME, DIRECTION_CONTENT, null);
		updateRecipeDirectionHelper(RECIPE_DIRECTION_CANNOT_BE_UPDATED, INVALID_DIRECTION_NUMBER, INVALID_RECIPE_NAME, DIRECTION_CONTENT);
		removeRecipeDirectionHelper(RECIPE_DIRECTION_CANNOT_BE_REMOVED, EXPECTED_EMPTY_LIST_SIZE, INVALID_DIRECTION_ID, INVALID_RECIPE_NAME, INVALID_DIRECTION_NUMBER, null);
	}
	
	/**
	 * Test single recipe direction operations when not logged in on an existing recipe.
	 * @throws Exception If there is a get or post failure.
	 */
	@Test
	public void testRecipeDirectionFunctionality_NotLoggedIn_OnExistingRecipe() throws Exception {
		addRecipeDirectionHelper(EXPECTED_UNAUTHORIZED_CODE, EXPECTED_EMPTY_LIST_SIZE, EXISTING_RECIPE_NAME, DIRECTION_CONTENT, null);
		updateRecipeDirectionHelper(RECIPE_DIRECTION_CANNOT_BE_UPDATED, INVALID_DIRECTION_NUMBER, EXISTING_RECIPE_NAME, DIRECTION_CONTENT);
		removeRecipeDirectionHelper(RECIPE_DIRECTION_CANNOT_BE_REMOVED, EXPECTED_EMPTY_LIST_SIZE, INVALID_DIRECTION_ID, EXISTING_RECIPE_NAME, INVALID_DIRECTION_NUMBER, null);
	}
	
	/**
	 * It is expected that adding a direction will return an unauthorized status code, the update and remove operations will return
	 * a message indicating the user is not the owner.
	 * @throws Exception If there is a get or post failure.
	 */
	@Test
	public void testRecipeDirectionFunctionality_LoggedIn_OnExistingRecipe_NotOwner() throws Exception {
		addRecipeDirectionHelper(EXPECTED_UNAUTHORIZED_CODE, EXPECTED_EMPTY_LIST_SIZE, EXISTING_RECIPE_NAME, DIRECTION_CONTENT, null);
		updateRecipeDirectionHelper(RECIPE_DIRECTION_CANNOT_BE_UPDATED, INVALID_DIRECTION_NUMBER, EXISTING_RECIPE_NAME, DIRECTION_CONTENT);
		removeRecipeDirectionHelper(RECIPE_DIRECTION_CANNOT_BE_REMOVED, EXPECTED_EMPTY_LIST_SIZE, INVALID_DIRECTION_ID, EXISTING_RECIPE_NAME, INVALID_DIRECTION_NUMBER, null);
	}
	
 	/**
	 * The list size is first expected to be 1 after a direction is inserted, a successful update should not return any error messages,
	 * and a successful removal will result in a direction list size of 0 without an error message.
	 * @throws Exception If there is a get or post failure.
	 */
	@Test
	public void testRecipeDirectionFuctionality_LoggedIn_AsRecipeOwner() throws Exception {
		List<RecipeDirection> directionList = null;
		recipesObject.deleteAllRecipeDirections(EXISTING_RECIPE_NAME);
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(USER_NAME, PASSWORD, TEST_ACCOUNT_ROLE));
		directionList = addRecipeDirectionHelper(EXPECTED_SUCCESS_CODE, EXPECTED_LIST_SIZE_AFTER_ADDITION, EXISTING_RECIPE_NAME, DIRECTION_CONTENT, recipesObject);
		RecipeDirection getAddedDirection = directionList.get(0); // allows access to the direction's number and id for update and delete operations.
		String directionNumber = String.valueOf(getAddedDirection.getDirectionNumber());
		updateRecipeDirectionHelper(null, directionNumber, EXISTING_RECIPE_NAME, DIRECTION_CONTENT);
		removeRecipeDirectionHelper(null, EXPECTED_EMPTY_LIST_SIZE, String.valueOf(getAddedDirection.getDirectionId()), EXISTING_RECIPE_NAME, directionNumber, recipesObject);
	}
	
	/**
	 * @param expectedCode The expected HTTP status code.
	 * @param expectedListSize The expected size of the list after the direction is inserted.
	 * @param recipeName The recipe name to add the direction to.
	 * @param directionContent The description of the direction.
	 * @param recipesObject Object to interface with the RecipeDirections table.
	 * @throws Exception If there is a post error.
	 */
	private List<RecipeDirection> addRecipeDirectionHelper(int expectedCode, int expectedListSize, String recipeName, String directionContent, RecipeDAOImpl recipesObject) throws Exception {
		MvcResult mvcResult = mvc.perform(post("/" + ADD_RECIPE_DIRECTION).param(RECIPE_NAME_PARAM, recipeName).
			param(DIRECTION_CONTENT_PARAM, directionContent).with(csrf())).andReturn(); 
		assertEquals(expectedCode, mvcResult.getResponse().getStatus()); 
		if(recipesObject != null) {
			List<RecipeDirection> directionList = recipesObject.getRecipeDirections(recipeName);
			assertEquals(expectedListSize, directionList.size());
			return directionList;
		}
		else {
			return null;
		}
	}
	
	/**
	 * @param expectedMessage The expected message after attempting to update the recipe direction.
	 * @param directionNumber The direction number of the recipe direction to update.
	 * @param recipeName The name of the recipe of the direction to update.
	 * @param directionContent The new content of the direction.
	 * @throws Exception If there is a post error.
	 */
	private void updateRecipeDirectionHelper(String expectedMessage, String directionNumber, String recipeName, String directionContent) throws Exception {
		MvcResult mvcResult = mvc.perform(post("/" + UPDATE_RECIPE_DIRECTION).param(DIRECTION_CONTENT_PARAM, directionContent).param(DIRECTION_NUMBER_PARAM, directionNumber)
			.param(RECIPE_NAME_PARAM, recipeName).with(csrf())).andReturn(); 
		ModelAndView modelAndView = mvcResult.getModelAndView();
		ModelMap modelMap = modelAndView.getModelMap();
		assertEquals(recipeName, modelMap.get(RECIPE_NAME_PARAM));
		assertEquals(expectedMessage, modelMap.get(MESSAGE_PARAM)); 
	}

	/**
	 * @param expectedMessage The expected message after attempting to delete the recipe direction.
	 * @param expectedListSize The expected list size after the attempted deletion operation.
	 * @param directionId The id of the direction to remove.
	 * @param recipeName The name of the recipe of the direction to delete.
	 * @param directionNumber The direction number to remove.
	 * @param recipesObject Object to interface with the RecipeDirections table.
	 * @throws Exception If there is a get error.
	 */
	private void removeRecipeDirectionHelper(String expectedMessage, int expectedListSize, String directionId, String recipeName, String directionNumber, RecipeDAOImpl recipesObject) throws Exception {
		MvcResult mvcResult = mvc.perform(get("/" + REMOVE_RECIPE_DIRECTION).param(DIRECTION_ID_PARAM, directionId).param(RECIPE_NAME_PARAM, recipeName)
			.param(DIRECTION_NUMBER_PARAM, directionNumber)).andReturn(); 
		ModelAndView modelAndView = mvcResult.getModelAndView();
		ModelMap modelMap = modelAndView.getModelMap();
		assertEquals(recipeName, modelMap.get(RECIPE_NAME_PARAM));
		assertEquals(expectedMessage, modelMap.get(MESSAGE_PARAM)); 
		if(recipesObject != null) {
			List<RecipeDirection> directionList = recipesObject.getRecipeDirections(recipeName);
			assertEquals(expectedListSize, directionList.size());
		}
	}
	
	/**
	 * The add operation expects to return an unauthorized HTTP status code as the user is not logged in. The update operation
	 * is expected to return a recipe cannot be found message. The remove operation first checks if the recipe exists, since this
	 * is a test on a non existent recipe it first checks to see if the recipe can be found before checking if the user wrote the review,
	 * so an internal server error code is expected.
	 * @throws Exception If there is a get or post failure.
	 */
	@Test
	public void testRecipeReviewFunctionality_UserNotLoggedIn_NonExistentRecipe() throws Exception {
		addRecipeReviewHelper(EXPECTED_UNAUTHORIZED_CODE, EXPECTED_EMPTY_LIST_SIZE, TEST_REVIEW_CONTENTS, TEST_REVIEW_RATING, TEST_REVIEW_TITLE, USER_NAME, NON_EXISTENT_RECIPE_NAME, null);
		updateRecipeReviewHelper(REVIEW_RECIPE_NOT_FOUND, TEST_REVIEW_RATING, TEST_REVIEW_CONTENTS, INVALID_REVIEW_ID, NON_EXISTENT_RECIPE_NAME, null);
		removeRecipeReviewHelper(EXPECTED_INTERNAL_SERVER_ERROR_CODE, EXPECTED_EMPTY_LIST_SIZE, INVALID_REVIEW_ID, TEST_REVIEW_RATING, NON_EXISTENT_RECIPE_NAME, null);
	}
		
	/**
	 * Since the recipe name is non-existent the add and remove operations are expected to return an internal server error HTTP code 
	 * as the recipe cannot be found. The update operation is expected to return a message indicating the recipe could not be found.
	 * @throws Exception If there is a get or post failure.
	 */
	@Test
	public void testRecipeReviewFunctionality_UserLoggedIn_NonExistentRecipe() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(USER_NAME, PASSWORD, TEST_ACCOUNT_ROLE));
		addRecipeReviewHelper(EXPECTED_INTERNAL_SERVER_ERROR_CODE, EXPECTED_EMPTY_LIST_SIZE, TEST_REVIEW_CONTENTS, TEST_REVIEW_RATING, TEST_REVIEW_TITLE, USER_NAME, NON_EXISTENT_RECIPE_NAME, null);
		updateRecipeReviewHelper(REVIEW_RECIPE_NOT_FOUND, TEST_REVIEW_RATING, TEST_REVIEW_CONTENTS, INVALID_REVIEW_ID, NON_EXISTENT_RECIPE_NAME, null);
		removeRecipeReviewHelper(EXPECTED_INTERNAL_SERVER_ERROR_CODE, EXPECTED_EMPTY_LIST_SIZE, INVALID_REVIEW_ID, TEST_REVIEW_RATING, NON_EXISTENT_RECIPE_NAME, null);
	}
	
	/**
	 * Since the user is not logged in, it is expected that the user will get an unauthorized status code, for the update operation
	 * it is expected that review cannot be found although the user is not logged in the log in check is done after attempting to find the review, and
	 * the remove operation attempts to find the recipe before verifying if the user is the owner and logged in so it is expected that
	 * an internal server error will occur.
	 * @throws Exception If there is a get or post failure.
	 */
	@Test
	public void testRecipeReviewFunctionality_UserNotLoggedIn_ExistentRecipe() throws Exception {
		addRecipeReviewHelper(EXPECTED_UNAUTHORIZED_CODE, EXPECTED_EMPTY_LIST_SIZE, TEST_REVIEW_CONTENTS, TEST_REVIEW_RATING, TEST_REVIEW_TITLE, USER_NAME, EXISTING_RECIPE_NAME, null);
		updateRecipeReviewHelper(REVIEW_UPDATE_CANNOT_BE_FOUND, TEST_REVIEW_RATING, TEST_REVIEW_CONTENTS, INVALID_REVIEW_ID, EXISTING_RECIPE_NAME, null);
		removeRecipeReviewHelper(EXPECTED_INTERNAL_SERVER_ERROR_CODE, EXPECTED_EMPTY_LIST_SIZE, INVALID_REVIEW_ID, TEST_REVIEW_RATING, EXISTING_RECIPE_NAME, null);
	}
	
	/**
	 * Since the user is logged in and writing on an existing recipe it is assumed that all these tests will succeed without
	 * any error messages or error codes.
	 * @throws Exception If there is a get or post failure.
	 */
	@Test
	public void testRecipeReviewFunctionality_UserLoggedIn_ExistentRecipe() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(USER_NAME, PASSWORD, TEST_ACCOUNT_ROLE));
		usersDAO.updateUserLastPostedReviewTime(null, USER_NAME); // hack to get around the 30 second wait time.
		recipesObject.deleteAllRecipeReviewsFromUser(EXISTING_RECIPE_NAME, USER_NAME);
		addRecipeReviewHelper(EXPECTED_SUCCESS_CODE, EXPECTED_LIST_SIZE_AFTER_ADDITION, TEST_REVIEW_CONTENTS, TEST_REVIEW_RATING, TEST_REVIEW_TITLE, USER_NAME, EXISTING_RECIPE_NAME, recipesObject);
		usersDAO.updateUserLastPostedReviewTime(null, USER_NAME); // hack to get around the 30 second wait time.
		List<RecipeReview> reviewsList = recipesObject.getRecipeReviews(EXISTING_RECIPE_NAME);
		RecipeReview addedReview = reviewsList.get(0);
		String reviewId = String.valueOf(addedReview.getReviewId());
		updateRecipeReviewHelper(REVIEW_UPDATE_CONTENT_CHANGED_REVIEW_SAME_MESSAGE, TEST_REVIEW_RATING, TEST_REVIEW_CONTENTS, reviewId, EXISTING_RECIPE_NAME, recipesObject);
		usersDAO.updateUserLastPostedReviewTime(null, USER_NAME); // hack to get around the 30 second wait time.
		removeRecipeReviewHelper(EXPECTED_SUCCESS_CODE, EXPECTED_EMPTY_LIST_SIZE, reviewId, TEST_REVIEW_RATING, EXISTING_RECIPE_NAME, recipesObject);
	}
	
	/**
	 * This test first creates a review that is written by another user, the list size is expected to be one after the review addition.
	 * The update review is expected to return an error message stating that the current user is not the owner of the review.
	 * The remove review is expected to return an unauthorized http status code as the current user is not the owner of the review.
	 * @throws Exception If there is a get or post failure.
	 */
	@Test
	public void testRecipeReviewFunctionality_UserLoggedIn_ExistentRecipe_NotReviewOwner() throws Exception {
		try {
			SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(USER_NAME, PASSWORD, TEST_ACCOUNT_ROLE));
			usersDAO.updateUserLastPostedReviewTime(null, USER_NAME); // hack to get around the 30 second wait time.
			recipesObject.addRecipeReview(TEST_UTILITY.getTestReviewContent(), Integer.parseInt(TEST_UTILITY.getReviewRating()), TEST_UTILITY.getTestReviewTitle(), null, null, SECOND_ACCOUNT_USER_NAME, EXISTING_RECIPE_NAME);
			usersDAO.updateUserLastPostedReviewTime(null, SECOND_ACCOUNT_USER_NAME); // hack to get around the 30 second wait time.
			List<RecipeReview> reviewsFromRecipe = recipesObject.getRecipeReviews(EXISTING_RECIPE_NAME);
			assertEquals(EXPECTED_LIST_SIZE_AFTER_ADDITION, reviewsFromRecipe.size());
			
			RecipeReview reviewToEdit = reviewsFromRecipe.get(0);
			String reviewId = String.valueOf(reviewToEdit.getReviewId());
			updateRecipeReviewHelper(REVIEW_UPDATE_FAILED_MESSAGE, TEST_REVIEW_RATING, TEST_REVIEW_CONTENTS, reviewId, EXISTING_RECIPE_NAME, recipesObject);
			removeRecipeReviewHelper(EXPECTED_UNAUTHORIZED_CODE, EXPECTED_LIST_SIZE_AFTER_ADDITION, reviewId, TEST_REVIEW_RATING, EXISTING_RECIPE_NAME, null);
		}
		finally {
			recipesObject.deleteAllRecipeReviewsFromUser(EXISTING_RECIPE_NAME, SECOND_ACCOUNT_USER_NAME);
		}
	}
	
	/**
	 * @param expectedReturnCode The expected HTTP status code on the attempted add review operation.
	 * @param expectedReviewListSize The expected number of reviews after the addition operation.
	 * @param reviewContent The content of the review.
	 * @param reviewRating The rating of the recipe from the review.
	 * @param reviewTitle The title of the review.
	 * @param reviewPoster The name of the user posting the review.
	 * @param recipeName The name of the recipe of the review.
	 * @param recipesObject An object to interface with the RecipeReviews table.
	 * @throws Exception If there is a post failure.
	 */
	private void addRecipeReviewHelper(int expectedReturnCode, int expectedReviewListSize, String reviewContent, String reviewRating, String reviewTitle, String reviewPoster, String recipeName, RecipeDAOImpl recipesObject) throws Exception {
		MvcResult mvcResult = mvc.perform(post("/" + ADD_RECIPE_REVIEW).param(REVIEW_CONTENT_PARAM, reviewContent).param(RECIPE_REVIEW_RATING_PARAM, reviewRating)
			.param(REVIEW_TITLE_PARAM, reviewTitle).param(USER_POSTED_BY_PARAM, reviewPoster).param(RECIPE_NAME_PARAM, recipeName).with(csrf())).andReturn();
		assertEquals(expectedReturnCode, mvcResult.getResponse().getStatus());
		if(recipesObject != null) {
			List<RecipeReview> reviewsList = recipesObject.getRecipeReviews(recipeName);
			assertEquals(expectedReviewListSize, reviewsList.size());
		}
	}
	
	/**
	 * @param expectedMessage The expected message when attempting to update a review.
	 * @param reviewRating The new review rating.
	 * @param reviewContents The new review content.
	 * @param reviewId The unique identifer of the review.
	 * @param recipeName The name of the recipe of the review to update.
	 * @param recipesDAO An object to interface with the RecipReviews table.
	 * @throws Exception If there is a post failure.
	 */
	private void updateRecipeReviewHelper(String expectedMessage, String reviewRating, String reviewContents, String reviewId, String recipeName, RecipeDAOImpl recipesObject) throws Exception {
		MvcResult mvcResult = mvc.perform(post("/" + UPDATE_RECIPE_REVIEW).param(RECIPE_REVIEW_RATING_PARAM, reviewRating).param(REVIEW_CONTENT_PARAM, reviewContents + " end.")
			.param(REVIEW_ID_PARAM, reviewId).param(RECIPE_NAME_PARAM, recipeName).with(csrf())).andReturn();
		ModelAndView modelAndView = mvcResult.getModelAndView();
		ModelMap modelMap = modelAndView.getModelMap();
		assertEquals(expectedMessage, modelMap.get(MESSAGE_PARAM));
	}
	
	/**
	 * @param expectedReturnCode The expected HTTP status code after the remove operation.
	 * @param expectedReviewListSize The expected review list size.
	 * @param reviewId The id of the review to remove.
	 * @param reviewRating The rating of the recipe from the review.
	 * @param recipeName The name of the recipe containing the review to remove.
	 * @throws Exception If there is a get failure.
	 */
	private void removeRecipeReviewHelper(int expectedReturnCode, int expectedReviewListSize, String reviewId, String reviewRating, String recipeName, RecipeDAOImpl recipesObject) throws Exception {
		MvcResult mvcResult = mvc.perform(get("/" + REMOVE_RECIPE_REVIEW).param(REVIEW_ID_PARAM, reviewId).param(RECIPE_REVIEW_RATING_PARAM, reviewRating)
				.param(RECIPE_NAME_PARAM, recipeName)).andReturn();
		assertEquals(expectedReturnCode, mvcResult.getResponse().getStatus());
		if(recipesObject != null) {
			List<RecipeReview> reviewsList = recipesObject.getRecipeReviews(recipeName);
			assertEquals(expectedReviewListSize, reviewsList.size());
		}
	}
	
}
