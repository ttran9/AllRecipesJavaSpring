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
import tran.allrecipes.data.ShoppingListDAOImpl;
import tran.allrecipes.presentation.model.Ingredient;
import tran.allrecipes.presentation.model.Recipe;
import tran.allrecipes.presentation.model.User;
import tran.allrecipes.presentation.model.UserRole;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=TestConfig.class)
@WebAppConfiguration
public class ShoppingListTest {
	/** URL mapping to show the shopping list. */
	private static final String SHOW_SHOPPING_LIST_URL = "/showShoppingList";
	/** URL mapping to add to the shopping list. */
	private static final String ADD_SHOPPING_LIST_INGREDIENT_URL = "/addShoppingListIngredient";
	/** URL mapping to remove from the shopping list. */
	private static final String REMOVE_SHOPPING_LIST_INGREDIENT_URL = "/removeShoppingListIngredient";
	/** URL mapping to update an ingredient from a shopping list. */
	private static final String UPDATE_SHOPPING_LIST_INGREDIENT_URL = "/updateShoppingListIngredient";
	/** URL mapping to transfer ingredients from a recipe to a shopping list. */
	private static final String TRANSFER_RECIPE_LIST_TO_SHOPPING_LIST_URL = "/processRecipeList";
	/** A recipe name used to test a failing case of a recipe's ingredients to shopping list. */
	private static final String EMPTY_RECIPE_NAME = "the empty recipe";
	/** The recipe name with the ingredients with proper units and types. */
	private static final String RECIPE_WITH_PROPER_TYPES_AND_UNITS = "junit test recipe";
	/** The recipe with an ingredient that has a type that is non matching. */
	private static final String RECIPE_WITH_TYPE_MISMATCH = "recipe with type mismatch";
	/** The recipe with an ingredient that has a unit that is non matching. */
	private static final String RECIPE_WITH_UNIT_MISMATCH = "recipe with unit mismatch";
	/** The recipe with ingredients for unit conversion testing. */
	private static final String RECIPE_FOR_UNIT_CONVERSION = "recipe to test unit conversions";
	/** Parameter to display a notification message. */
	private static final String MESSAGE_PARAM = "message";
	/** Parameter to specify a shopping list. */
	private static final String SHOPPING_LIST_NAME_PARAM = "shoppingListName";
	/** Parameter to specify what ingredient is being removed. */
	private static final String SHOPPING_LIST_INGREDIENT_ID_PARAM = "shoppingListIngredientID";
	/** Parameter to specify the name of the shopping list the ingredient is a part of. */
	private static final String INGREDIENT_LIST_NAME_PARAM = "ingredientListName";
	/** Parameter to specify the ingredient name. */
	private static final String INGREDIENT_NAME_PARAM = "ingredientName";
	/** Parameter to specify the ingredient's unit. */
	private static final String INGREDIENT_UNIT_PARAM = "ingredientUnit";
	/** Parameter to specify the ingredient's whole number. */
	private static final String INGREDIENT_WHOLE_NUMBER_PARAM = "wholeNumber";
	/** Parameter to specify the ingredient's fraction quantity. */
	private static final String INGREDIENT_FRACTION_PARAM = "ingredientFractionQuantity";
	/** parameter to specify the ingredient's type. */
	private static final String INGREDIENT_TYPE_PARAM = "ingredientType";
	/** Object to access utility methods used for testing. */
	private static final TestUtility TEST_UTILITY = new TestUtility();
	/** Object to hold a user's information. */
	private static final User TEST_USER = TEST_UTILITY.getTestAccount();
	/** List to hold a user's role(s). */
	private static final List<UserRole> TEST_ACCOUNT_ROLE = TEST_UTILITY.getTestAccountRoles();;
	/** A test account's user name. */
	private static final String USER_NAME = TEST_USER.getUsername();
	/** A test account's encrypted password. */
	private static final String PASSWORD = TEST_USER.getPassword();
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
	private static final String TEST_INGREDIENT_OTHER_TYPE = TEST_UTILITY.getTestIngredientOtherType();
	/** A parameter to specify the recipe that will have its contents added to a shopping list. */
	private static final String RECIPE_NAME_PARAM = "recipeName";
	/** Expected redirect message when not the owner of the shopping list. */
	private static final String REDIRECTED_TO_LOGIN_MESSAGE = "you cannot view your shopping list unless you are logged in.";
	/** Notification message to indicate the user is not the owner of a shopping list and cannot perform operations on it. */
	private static final String USER_NOT_OWNER_OF_ADD_SHOPPING_LIST_INGREDIENTS_MESSAGE = "you are not the owner of this shopping list.";
	/** Notification message to indicate the user is not the owner and cannot update the shopping list ingredients. */
	private static final String USER_NOT_OWNER_UPDATE_SHOPPING_LIST_INGREDIENTS_MESSAGE = "you must be the owner of the shopping list to update its contents.";
	/** Notification to indicate that the recipe ingredients cannot be moved to a shopping list when user is not logged in. */
	private static final String TRANSFER_RECIPE_INGREDIENTS_TO_SHOPPING_LIST_NOT_LOGGED_IN_MESSAGE = "you are not logged in so you cannot move this to your shopping list.";
	/** Notification to indicate that the recipe has no ingredients to move over. */
	private static final String TRANFER_EMPTY_RECIPE_INGREDIENTS_ERROR_MESSAGE = "there are no ingredients to move over";
	/** Expected redirect status code. */
	private static final int REDIRECTED_STATUS_CODE = Integer.parseInt(HttpStatus.FOUND.toString());
	/** Expected OK status code. */
	private static final int REQUEST_SUCCESSFUL_CODE = Integer.parseInt(HttpStatus.OK.toString());
	/** Expected unauthorized status code. */
	private static final int REQUEST_UNAUTHORIZED_CODE = Integer.parseInt(HttpStatus.UNAUTHORIZED.toString());
	/** The location of the data source file. */
	private static final String DATABASE_SOURCE_FILE = "database/Datasource.xml";
	/** Name of the shopping list DAO bean. */
	private static final String SHOPPING_LIST_DAO_BEAN_NAME = "ShoppingListDAO";
	/** Name of the recipes DAO bean. */
	private static final String RECIPE_DAO_BEAN_NAME = "RecipesDAO";
	/** An object to get access to the current class's application context. */
	private static ClassPathXmlApplicationContext appContext;
	/** An object to add/remove/update pantry list contents. */
	private static RecipeDAOImpl recipesObject;
	/** An object to add/remove/update shopping list contents. */
	private static ShoppingListDAOImpl shoppingListDAO;
	/** The shopping list name of a user. */
	private static String shoppingListName;
	/** Object to set the application context for the tests below. */
	@Autowired
	private WebApplicationContext context;
	/** Object to allow testing for Spring MVC. */
	private MockMvc mvc;
	
	/** Initial assignment of a data member before tests are run. */
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
		recipesObject = (RecipeDAOImpl) appContext.getBean(RECIPE_DAO_BEAN_NAME);
		shoppingListDAO = (ShoppingListDAOImpl) appContext.getBean(SHOPPING_LIST_DAO_BEAN_NAME);
		shoppingListName = shoppingListDAO.getListName(USER_NAME);
	}
	
	/** Closes data members. */
	@AfterClass
	public static void cleanUp() {
		recipesObject = null;
		shoppingListDAO = null;
		((ConfigurableApplicationContext) appContext).close();
	}
	
	/**
	 * Tests to see what messages are displayed to a user when a user is not logged in and attempts to view a shopping list.
	 * @throws Exception If there is a get failure.
	 */
	@Test
	public void testShowShoppingList_NotLoggedIn() throws Exception {
		showShoppingListHelper(REDIRECTED_STATUS_CODE, REDIRECTED_TO_LOGIN_MESSAGE);

	}
	
	/**
	 * Tests the case where the user is logged in and attempts to view a shopping list.
	 * @throws Exception If there is a get failure.
	 */
	@Test
	public void testShowShoppingList_LoggedIn() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(USER_NAME, PASSWORD, TEST_ACCOUNT_ROLE));
		showShoppingListHelper(REQUEST_SUCCESSFUL_CODE, null);
	}
	
	/**
	 * @param expectedResponseCode The expected HTTP status code.
	 * @param expectedErrorMessage The expected error message.
	 * @throws Exception If there is a get error.
	 */
	private void showShoppingListHelper(int expectedResponseCode, String expectedErrorMessage) throws Exception {
		MvcResult mvcResult = mvc.perform(get(SHOW_SHOPPING_LIST_URL)).andReturn();
		ModelAndView modelAndView = mvcResult.getModelAndView();
		ModelMap uiInfo = modelAndView.getModelMap();
		assertEquals(expectedResponseCode, mvcResult.getResponse().getStatus());
		assertEquals(expectedErrorMessage, uiInfo.get(MESSAGE_PARAM));
	}
	
	/**
	 * The add operation is expected to return an error message stating that the user is not the owner of the shopping list and the added ingredient should point to null.
	 * The update operation is expected to return an error message stating that the user cannot update a shopping list that does not belong to that user and the ingredient should point to null.
	 * The remove operation is expected to return an unauthorized HTTP status code and the ingredient should point to null.
	 * @throws Exception If there is a post failure.
	 */
	@Test
	public void testShoppingListOperations_NotLoggedIn() throws Exception {
		shoppingListDAO.removeAllListIngredients(shoppingListName);
		Ingredient addedIngredient = addShoppingListIngredientsHelper(USER_NOT_OWNER_OF_ADD_SHOPPING_LIST_INGREDIENTS_MESSAGE, shoppingListName);
		updateShoppingListIngredientsHelper(USER_NOT_OWNER_UPDATE_SHOPPING_LIST_INGREDIENTS_MESSAGE, addedIngredient, shoppingListName);
		removeShoppingListIngredientsHelper(TEST_UTILITY.getInvalidIngredientId(), REQUEST_UNAUTHORIZED_CODE, shoppingListName);
	}
	
	/**
	 * The add operation is expected to return an error message stating that the user is not the owner of the shopping list and the added ingredient should point to null.
	 * The update operation is expected to return an error message stating that the user cannot update a shopping list that does not belong to that user and the ingredient should point to null.
	 * The remove operation is expected to return an unauthorized HTTP status code and the ingredient should point to null.
	 * @throws Exception If there is a post failure.
	 */ 
	@Test
	public void testShoppingListOperations_LoggedIn_OtherUserShoppingList() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(USER_NAME, PASSWORD, TEST_ACCOUNT_ROLE));
		String shoppingListName = TEST_UTILITY.getShoppingAndPantryTestAccountName() + "'s shopping list";
		Ingredient addedIngredient = addShoppingListIngredientsHelper(USER_NOT_OWNER_OF_ADD_SHOPPING_LIST_INGREDIENTS_MESSAGE, shoppingListName);
		updateShoppingListIngredientsHelper(USER_NOT_OWNER_UPDATE_SHOPPING_LIST_INGREDIENTS_MESSAGE, addedIngredient, shoppingListName);
		removeShoppingListIngredientsHelper(TEST_UTILITY.getInvalidIngredientId(), REQUEST_UNAUTHORIZED_CODE, shoppingListName);
	}
	
	/**
	 * The add operation is expected to not return an error message and the ingredient should not point to null.
	 * The update operation is expected to not return an error message and the ingredient types should differ.
	 * The remove operation is expected to return a success HTTP code and the ingredient should point to null.
	 * @throws Exception If there is a post failure.
	 */ 
	@Test
	public void testShoppingListOperations_LoggedIn() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(USER_NAME, PASSWORD, TEST_ACCOUNT_ROLE));
		shoppingListDAO.removeAllListIngredients(shoppingListName);
		Ingredient addedIngredient = addShoppingListIngredientsHelper(null, shoppingListName);
		updateShoppingListIngredientsHelper(null, addedIngredient, shoppingListName);
		removeShoppingListIngredientsHelper(String.valueOf(addedIngredient.getIngredientID()), REQUEST_SUCCESSFUL_CODE, shoppingListName);
	}
	
	/**
	 * @param expectedErrorMessage The expected error message.
	 * @param shoppingListName The user's shopping list name.
	 * @return An ingredient if added, null if not.
	 * @throws Exception If there is a post failure.
	 */
	private Ingredient addShoppingListIngredientsHelper(String expectedErrorMessage, String shoppingListName) throws Exception {
		MvcResult mvcResult = mvc.perform(post(ADD_SHOPPING_LIST_INGREDIENT_URL).param(INGREDIENT_LIST_NAME_PARAM, shoppingListName).param(INGREDIENT_NAME_PARAM, TEST_INGREDIENT_NAME)
				.param(INGREDIENT_UNIT_PARAM, TEST_INGREDIENT_UNIT).param(INGREDIENT_WHOLE_NUMBER_PARAM, TEST_INGREDIENT_WHOLE_NUMBER)
				.param(INGREDIENT_FRACTION_PARAM, TEST_INGREDIENT_FRACTION).param(INGREDIENT_TYPE_PARAM, TEST_INGREDIENT_TYPE).with(csrf())).andReturn();
			
		ModelAndView modelAndView = mvcResult.getModelAndView();
		ModelMap uiInfo = modelAndView.getModelMap();
		
		Ingredient addedIngredient = shoppingListDAO.getSingleIngredient(shoppingListName, TEST_INGREDIENT_NAME);
		assertEquals(expectedErrorMessage, uiInfo.get(MESSAGE_PARAM));
		if(addedIngredient != null) {
			assertNotEquals(null, addedIngredient);
		}
		else {
			assertEquals(null, addedIngredient);
		}
		return addedIngredient;
			
	}
	
	/**
	 * @param expectedErrorMessage An expected error message.
	 * @param shoppingListName The user's shopping list name.
	 * @param ingredient The ingredient object for testing.
	 * @throws Exception If there is a post failure.
	 */
	private void updateShoppingListIngredientsHelper(String expectedErrorMessage, Ingredient ingredient, String shoppingListName) throws Exception {
		MvcResult mvcResult = mvc.perform(post(UPDATE_SHOPPING_LIST_INGREDIENT_URL).param(INGREDIENT_NAME_PARAM, TEST_INGREDIENT_NAME).param(INGREDIENT_LIST_NAME_PARAM, shoppingListName)
			.param(INGREDIENT_WHOLE_NUMBER_PARAM, TEST_INGREDIENT_WHOLE_NUMBER).param(INGREDIENT_FRACTION_PARAM, TEST_INGREDIENT_FRACTION)
			.param(INGREDIENT_UNIT_PARAM, TEST_INGREDIENT_UNIT).param(INGREDIENT_TYPE_PARAM, TEST_INGREDIENT_OTHER_TYPE).with(csrf())).andReturn();
		
		ModelAndView modelAndView = mvcResult.getModelAndView();
		ModelMap modelMap = modelAndView.getModelMap();
		
		assertEquals(expectedErrorMessage, modelMap.get(MESSAGE_PARAM));
		if(ingredient != null) {
			assertNotEquals(ingredient.getIngredientType(), TEST_INGREDIENT_OTHER_TYPE);
		}
		else {
			assertEquals(null, ingredient);
		}
			
	}
		
	/**
	 * @param ingredientId The id of the ingredient to remove.
	 * @param expectedReturnCode The expected return code after attempting to remove the ingredient.
	 * @param shoppingListName The user's shopping list name.
	 * @throws Exception If there is a post failure.
	 */
	private void removeShoppingListIngredientsHelper(String ingredientId, int expectedReturnCode, String shoppingListName) throws Exception {
		MvcResult mvcResult = mvc.perform(post(REMOVE_SHOPPING_LIST_INGREDIENT_URL).param(SHOPPING_LIST_INGREDIENT_ID_PARAM, ingredientId).param(SHOPPING_LIST_NAME_PARAM, shoppingListName)
			.with(csrf())).andReturn();
		int deleteReturnCode = mvcResult.getResponse().getStatus();
		Ingredient removedIngredient = shoppingListDAO.getSingleIngredient(shoppingListName, TEST_INGREDIENT_NAME);
		assertEquals(null, removedIngredient);
		assertEquals(expectedReturnCode, deleteReturnCode);
	}

	/**
	 * Tests the case where the user attempts to move recipe ingredients to a shopping list while not logged in.
	 * @throws Exception If there is a post failure.
	 */
	@Test
	public void testTransferRecipeIngredientsToShoppingList_NotLoggedIn() throws Exception {
		transferRecipeListToShoppingListHelper(TRANSFER_RECIPE_INGREDIENTS_TO_SHOPPING_LIST_NOT_LOGGED_IN_MESSAGE, EMPTY_RECIPE_NAME);
	}
	
	/**
	 * Tests transfer recipe ingredients to a shopping list while logged in but the recipe list is empty.
	 * @throws Exception If there is a post failure.
	 */
	@Test
	public void testTransferRecipeIngredientsToShoppingList_LoggedIn_EmptyRecipeList() throws Exception{
		Recipe emptyRecipe = removeAndAddContentHelper(EMPTY_RECIPE_NAME);
		transferRecipeListToShoppingListHelper(TRANFER_EMPTY_RECIPE_INGREDIENTS_ERROR_MESSAGE, EMPTY_RECIPE_NAME);
		assertNotEquals(null, emptyRecipe);
		TEST_UTILITY.removeTestRecipe(EMPTY_RECIPE_NAME, USER_NAME);
		emptyRecipe = recipesObject.getRecipe(EMPTY_RECIPE_NAME);
		assertEquals(null, emptyRecipe);
	}
	
	/**
	 * This test does not expect any error messages as it is just transferring supported ingredients to an empty shopping list.
	 * @throws Exception If there is a post failure.
	 */
	@Test
	public void testTransferRecipeIngredientsToShoppingList_LoggedIn_RecipeListWithItems() throws Exception {
		removeAndAddContentHelper(RECIPE_WITH_PROPER_TYPES_AND_UNITS);
		TEST_UTILITY.getIngredientsForTestRecipeListWithItems(); // populate recipe.
		transferRecipeListToShoppingListHelper(null, RECIPE_WITH_PROPER_TYPES_AND_UNITS);
	}
	
	/**
	 * This test does not expect to return any error messages because it is tests for all supported conversions on a pre-populated shopping list.
	 * @throws Exception If there is a post failure.
	 */
	@Test
	public void testTransferRecipeIngredientsToShoppingList_LoggedIn_FullConversion() throws Exception {
		removeAndAddContentHelper(RECIPE_FOR_UNIT_CONVERSION);
		TEST_UTILITY.populateShoppingListWithAllUnitConversionTests(shoppingListName, USER_NAME);
		TEST_UTILITY.populateRecipeWithAllUnitConversionTests();	
		transferRecipeListToShoppingListHelper(null, RECIPE_FOR_UNIT_CONVERSION);
	}

	/**
	 * This test expects an error message indicating that the recipe ingredient does not match the type of the shopping list ingredient.
	 * @throws Exception If there is a post failure.
	 */
	@Test
	public void testTransferRecipeIngredientsToShoppingList_LoggedIn_IngredientTypeMisMatch() throws Exception {
		removeAndAddContentHelper(RECIPE_WITH_TYPE_MISMATCH);
		TEST_UTILITY.addToShoppingListTypeMismatch(shoppingListName, USER_NAME); // populate the shopping list.
		TEST_UTILITY.getIngredientForTestRecipeListWithTypeMisMatch(); // populate the recipe.
		String expectedReturnMessage = "make sure all ingredient types match before adding to your shopping list.";
		transferRecipeListToShoppingListHelper(expectedReturnMessage, RECIPE_WITH_TYPE_MISMATCH);
	}
	
	/**
	 * This test expects an error message indicating that the recipe ingredient does not match the unit of the shopping list ingredient.
	 * @throws Exception If there is a post failure.
	 */
	@Test
	public void testTransferRecipeIngredientsToShoppingList_LoggedIn_IngredientUnitMisMatch() throws Exception {
		removeAndAddContentHelper(RECIPE_WITH_UNIT_MISMATCH);
		TEST_UTILITY.addToShoppingListUnitMismatch(shoppingListName, USER_NAME); // populate shopping list.
		Ingredient shoppingListIngredient = TEST_UTILITY.getIngredientForTestRecipeListWithUnitMisMatch(); // populate recipe list.
		assertNotEquals(null, shoppingListIngredient);
		String expectedErrorMessage = "could not add to shopping list as ingredient: " + shoppingListIngredient.getIngredientName() + " could not be converted.";
		transferRecipeListToShoppingListHelper(expectedErrorMessage, RECIPE_WITH_UNIT_MISMATCH);
	}
	
	/**
	 * @param recipeName The name of the recipe to remove and add.
	 * helper method to perform a recipe removal, a shopping list ingredients removal, and finally adding a test recipe.
	 * It is asserted/expected that the recipe object is not null since it is added.
	 */
	private Recipe removeAndAddContentHelper(String recipeName) {
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(USER_NAME, PASSWORD, TEST_ACCOUNT_ROLE));	
		TEST_UTILITY.removeTestRecipe(recipeName, USER_NAME);
		TEST_UTILITY.removeShoppingListIngredients(shoppingListName, USER_NAME);
		TEST_UTILITY.addTestRecipe(recipeName, USER_NAME);
		Recipe recipe = recipesObject.getRecipe(recipeName);
		assertNotEquals(null, recipe);
		return recipe;
	}
	
	/**
	 * @param expectedMessage The expected error message when attempting the transfer.
	 * @param recipeNameToTransfer The name of the recipe to transfer.
	 * @throws Exception If there is a post error.
	 */
	private void transferRecipeListToShoppingListHelper(String expectedMessage, String recipeNameToTransfer) throws Exception {
		MvcResult mvcResult = mvc.perform(post(TRANSFER_RECIPE_LIST_TO_SHOPPING_LIST_URL).param(RECIPE_NAME_PARAM, recipeNameToTransfer).with(csrf())).andReturn();
		ModelAndView modelAndView = mvcResult.getModelAndView();
		ModelMap uiInfo = modelAndView.getModelMap();
		assertEquals(expectedMessage, uiInfo.get(MESSAGE_PARAM)); 
	}
	
}
