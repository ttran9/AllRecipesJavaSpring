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

import tran.allrecipes.data.PantryListDAOImpl;
import tran.allrecipes.data.ShoppingListDAOImpl;
import tran.allrecipes.presentation.model.PantryIngredient;
import tran.allrecipes.presentation.model.User;
import tran.allrecipes.presentation.model.UserRole;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=TestConfig.class)
@WebAppConfiguration
public class PantryListTest {
	/** URL mapping to display a pantry list. */
	private static final String SHOW_PANTRY_LIST_URL = "/showPantryList";
	/** URL mapping to add to a pantry list. */
	private static final String ADD_PANTRY_LIST_INGREDIENT_URL = "/addPantryListIngredient";
	/** URL mapping to remove an ingredient from a pantry list. */
	private static final String REMOVE_PANTRY_LIST_INGREDIENT_URL = "/removePantryListIngredient";
	/** URL mapping to update an ingredient amount, recalculates the ingredient threshold. */
	private static final String UPDATE_PANTRY_LIST_INGREDIENT_URL = "/updatePantryListIngredient";
	/** URL mapping to update how much of an ingredient is left. */
	private static final String UPDATE_PANTRY_LIST_INGREDIENT_AMOUNT_URL = "/updatePantryListIngredientAmount";
	/** URL mapping to add items in a pantry list to a shopping list. */
	private static final String TRANSFER_PANTRY_LIST_TO_SHOPPING_LIST_URL = "/processPantryList";
	/** Parameter to attach a message for a redirect. */
	private static final String MESSAGE_PARAM = "message";
	/** Parameter to specify the pantry list. */
	private static final String PANTRY_LIST_NAME_PARAM = "ingredientListName";
	/** Parameter to specify the ingredient's name. */
	private static final String PANTRY_LIST_INGREDIENT_NAME_PARAM = "ingredientName";
	/** Parameter to specify the ingredient's unit. */
	private static final String PANTRY_LIST_INGREDIENT_UNIT_PARAM = "ingredientUnit";
	/** Parameter to specify the ingredient's whole number quantity. */
	private static final String PANTRY_LIST_INGREDIENT_WHOLE_NUMBER_PARAM = "wholeNumber";
	/** Parameter to specify the ingredient's fraction quantity. */
	private static final String PANTRY_LIST_INGREDIENT_QUANTITY_PARAM = "ingredientFractionQuantity";
	/** Parameter to specify the ingredient's type. */
	private static final String PANTRY_LIST_INGREDIENT_TYPE_PARAM = "ingredientType";
	/** Parameter to specify the pantry's list ingredient ID for deletion. */
	private static final String REMOVE_PANTRY_LIST_INGREDIENT_ID_PARAM = "pantryListIngredientID";
	/** Parameter to specify the pantry list's name for removal. */
	private static final String THE_PANTRY_LIST_NAME_PARAM = "pantryListName";
	/** Expected redirect message when not the owner of the shopping list. */
	private static final String REDIRECTED_TO_LOGIN_MESSAGE = "you cannot view your pantry list unless you are logged in.";
	/** Expected redirect message when user is not logged in and attempting to move ingredients from a pantry list to a shopping list. */
	private static final String TRANSFER_PANTRY_INGREDIENTS_TO_SHOPPING_LIST_NOT_LOGGED_IN_MESSAGE = "you are not logged in so you cannot move this to your shopping list.";
	/** Notification to indicate that the pantry has no ingredients to move over. */
	private static final String TRANSFER_EMPTY_PANTRY_INGREDIENTS_ERROR_MESSAGE = "there are no ingredients to move over";
	/** Notification to indicate the user is not the owner of the pantry list. */
	private static final String USER_NOT_OWNER_OF_PANTRY_LIST = "you are not the owner of this pantry list.";
	/** Message to notify the user is not the owner of a pantry list that is being updated. */
	private static final String USER_NOT_OWNER_UPDATE_PANTRY_LIST_INGREDIENTS_MESSAGE = "you must be the owner of the pantry list to update its contents.";
	/** Message to notify the user is not an owner of a pantry list that is going to have an ingredient be subtracted. */
	private static final String USER_NOT_OWNER_UPDATE_SUBTRACTION_PANTRY_LIST_INGREDIENT_MESSAGE = "you must be the owner of this pantry list to update(subtract) an ingredient in it.";
	/** Expected redirect status code. */
	private static final int REDIRECTED_STATUS_CODE = Integer.parseInt(HttpStatus.FOUND.toString());
	/** Expected OK status code. */
	private static final int REQUEST_SUCCESSFUL_CODE = Integer.parseInt(HttpStatus.OK.toString());
	/** Expected unauthorized status code. */
	private static final int REQUEST_UNAUTHORIZED_CODE = Integer.parseInt(HttpStatus.UNAUTHORIZED.toString());
	/** Expected server error when removing an ingredient that does not exist. */
	private static final int DELETE_OPERATION_NO_INGREDIENT_FOUND = Integer.parseInt(HttpStatus.INTERNAL_SERVER_ERROR.toString());
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
	/** The location of the data source file. */
	private static final String DATABASE_SOURCE_FILE = "database/Datasource.xml";
	/** Name of the shopping list DAO bean. */
	private static final String SHOPPING_LIST_DAO_BEAN_NAME = "ShoppingListDAO";
	/** Name of the recipe DAO bean. */
	private static final String PANTRY_DAO_BEAN_NAME = "PantryListDAO";
	/** An object to get access to the current class's application context. */
	private static ClassPathXmlApplicationContext appContext;
	/** An object to add/remove/update pantry list contents. */
	private static PantryListDAOImpl pantryListDAO;
	/** An object to add/remove/update shopping list contents. */
	private static ShoppingListDAOImpl shoppingListDAO;
	/** The pantry list name of a user. */
	private static String pantryListName;
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
		pantryListDAO = (PantryListDAOImpl) appContext.getBean(PANTRY_DAO_BEAN_NAME);
		shoppingListDAO = (ShoppingListDAOImpl) appContext.getBean(SHOPPING_LIST_DAO_BEAN_NAME);
		pantryListName = pantryListDAO.getListName(USER_NAME);
		shoppingListName = shoppingListDAO.getListName(USER_NAME);
	}
	
	/** Closes data members. */
	@AfterClass
	public static void cleanUp() {
		pantryListDAO = null;
		shoppingListDAO = null;
		((ConfigurableApplicationContext) appContext).close();
	}
	
	/**
	 * A redirect status code and a message notifying that the user has been redirected because the user is not logged in
	 * are expected.
	 * @throws Exception If there is a get failure.
	 */
	@Test
	public void testShowPantryList_NotLoggedIn() throws Exception {
		showPantryListHelper(REDIRECTED_STATUS_CODE, REDIRECTED_TO_LOGIN_MESSAGE);
	}
	
	/**
	 * Since the user is logged in, a success HTTP status code and no error message can be expected.
	 * @throws Exception If there is a get failure.
	 */
	@Test
	public void testShowPantryList_LoggedIn() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(USER_NAME, PASSWORD, TEST_ACCOUNT_ROLE));
		showPantryListHelper(REQUEST_SUCCESSFUL_CODE, null);
	}
	
	/**
	 * An error notifying the user is not logged in is expected.
	 * @throws Exception If there is a post failure.
	 */
	@Test
	public void testTransferPantryIngredientsToShoppingList_NotLoggedIn() throws Exception {
		transferPantryIngredientsToShoppingList(TRANSFER_PANTRY_INGREDIENTS_TO_SHOPPING_LIST_NOT_LOGGED_IN_MESSAGE);
	}
	
	/**
	 * No error message is expected because the user is logged in and all the conversions are assumed to be supported conversions.
	 * @throws Exception If there is a post failure.
	 */
	@Test
	public void testTransferPantryIngredientsToShoppingList_LoggedIn_AllConversions() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(USER_NAME, PASSWORD, TEST_ACCOUNT_ROLE));
		shoppingListDAO.removeAllListIngredients(shoppingListName);
		pantryListDAO.removeAllListIngredients(pantryListName);
		TEST_UTILITY.populateShoppingListWithAllUnitConversionTests(shoppingListName, USER_NAME);
		TEST_UTILITY.populatePantryListWithAllUnitConversionsForTesting(pantryListName, USER_NAME);
		transferPantryIngredientsToShoppingList(null);
	}
	
	/**
	 * An error message indicating that the types of the pantry list ingredient does not match that of the shopping list.
	 * @throws Exception If there is a post failure.
	 */
	@Test
	public void testTransferPantryIngredientsToShoppingList_IngredientTypeMisMatch() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(USER_NAME, PASSWORD, TEST_ACCOUNT_ROLE));
		shoppingListDAO.removeAllListIngredients(shoppingListName);
		pantryListDAO.removeAllListIngredients(pantryListName);
		TEST_UTILITY.addToShoppingListTypeMismatch(shoppingListName, USER_NAME); // populate the shopping list.
		TEST_UTILITY.addPantryIngredientForTestPantryListWithTypeMisMatch(pantryListName, USER_NAME); // populate the pantry list.
		String expectedReturnMessage = "make sure all ingredient types match before adding to your shopping list.";
		transferPantryIngredientsToShoppingList(expectedReturnMessage);
	}
	
	/**
	 * An error message indicating that the unit of the pantry ingredient list does not match that of the shopping list.
	 * @throws Exception If there is a post failure.
	 */
	@Test
	public void testTransferPantryIngredientsToShoppingList_IngredientUnitMisMatch() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(USER_NAME, PASSWORD, TEST_ACCOUNT_ROLE));
		shoppingListDAO.removeAllListIngredients(shoppingListName);
		pantryListDAO.removeAllListIngredients(pantryListName);
		TEST_UTILITY.addToShoppingListUnitMismatch(shoppingListName, USER_NAME); // populate the shopping list.
		PantryIngredient pantryIngredient = TEST_UTILITY.addPantryIngredientForTestPantryListWithUnitMisMatch(pantryListName, USER_NAME); // populate the pantry list.
		String expectedErrorMessage = "could not add to shopping list as ingredient: " + pantryIngredient.getIngredientName() + " could not be converted.";
		transferPantryIngredientsToShoppingList(expectedErrorMessage);
	}
	
	/**
	 * An error message indicating that the pantry list has no ingredients is expected. 
	 * @throws Exception If there is a post failure.
	 */
	@Test
	public void transferPantryIngredientsToShoppingList_LoggedIn_EmptyPantryList() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(USER_NAME, PASSWORD, TEST_ACCOUNT_ROLE));
		shoppingListDAO.removeAllListIngredients(shoppingListName);
		pantryListDAO.removeAllListIngredients(pantryListName);
		transferPantryIngredientsToShoppingList(TRANSFER_EMPTY_PANTRY_INGREDIENTS_ERROR_MESSAGE);
	}
	
	/**
	 * @param expectedStatusCode The expected HTTP status code.
	 * @param expectedErrorMessage The expected error if any.
	 * @throws Exception If there is a get failure.
	 */
	private void showPantryListHelper(int expectedStatusCode, String expectedErrorMessage) throws Exception {
		MvcResult mvcResult = mvc.perform(get(SHOW_PANTRY_LIST_URL)).andReturn();
		ModelAndView modelAndView = mvcResult.getModelAndView();
		ModelMap modelMap = modelAndView.getModelMap();
		assertEquals(expectedStatusCode, mvcResult.getResponse().getStatus());
		assertEquals(expectedErrorMessage, modelMap.get(MESSAGE_PARAM));
	}
	
	/**
	 * @param expectedErrorMessage The expected error if any.
	 * @throws Exception If there is a post failure.
	 */
	private void transferPantryIngredientsToShoppingList(String expectedErrorMessage) throws Exception {
		MvcResult mvcResult = mvc.perform(post(TRANSFER_PANTRY_LIST_TO_SHOPPING_LIST_URL).param(THE_PANTRY_LIST_NAME_PARAM, pantryListName).with(csrf())).andReturn();
		ModelAndView modelAndView = mvcResult.getModelAndView();
		ModelMap modelMap = modelAndView.getModelMap();
		assertEquals(expectedErrorMessage, modelMap.get(MESSAGE_PARAM));
	}
	
	/**
	 * Since the user for the add, update ingredient, and subtract ingredient operations are expected to return error messages indicating that the user
	 * is not the owner of the pantry list and the ingredient value should be null since there is no pantry ingredient. The remove pantry ingredient operation
	 * should also return a null ingredient value as well as return an unauthorized HTTP status code.
	 * @throws Exception If there is a get or post failure.
	 */
	@Test
	public void testPantryListOperations_NotLoggedIn() throws Exception {
		addPantryIngredientHelper(USER_NOT_OWNER_OF_PANTRY_LIST);
		updatePantryIngredientHelper(USER_NOT_OWNER_UPDATE_PANTRY_LIST_INGREDIENTS_MESSAGE, null);
		subtractPantryIngredientHelper(USER_NOT_OWNER_UPDATE_SUBTRACTION_PANTRY_LIST_INGREDIENT_MESSAGE, TEST_INGREDIENT_WHOLE_NUMBER, TEST_INGREDIENT_FRACTION);
		removePantryIngredientHelper(TEST_UTILITY.getInvalidIngredientId(), REQUEST_UNAUTHORIZED_CODE);
	}
	
	/**
	 * The add operation is expected to not return an error message as the user is logged in. The update ingredient operation should not return an error message and is expected
	 * to not have matching ingredient types. The subtraction operation is not expected to return any error message as the threshold is not hit and the ingredient should not point to null
	 * as it still remains. The remove operation should return a successful HTTP code as the ingredient was able to be removed.
	 * @throws Exception If there is a get or post failure.
	 */
	@Test
	public void testPantryListOperations_LoggedIn_NoThresholdHit() throws Exception {
		String wholeNumberToSubtract = "1";
		String fractionToSubtract = "1/2";
		int expectedFractionComparisonResult = 0;
		String expectedWholeNumber = "0";
		String[] subtractionFraction = TEST_UTILITY.getTestFractionPortion(fractionToSubtract);
		assertNotEquals(null, subtractionFraction);
		PantryIngredient expectedPantryQuantity = new PantryIngredient(expectedWholeNumber, subtractionFraction[0], subtractionFraction[1]); // expected quantity amount after the subtraction operation.
		shoppingListDAO.removeAllListIngredients(shoppingListName);
		pantryListDAO.removeAllListIngredients(pantryListName);
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(USER_NAME, PASSWORD, TEST_ACCOUNT_ROLE));
		PantryIngredient addedIngredient = addPantryIngredientHelper(null);
		updatePantryIngredientHelper(null, addedIngredient);
		PantryIngredient updatedSubtractedIngredient = subtractPantryIngredientHelper(null, wholeNumberToSubtract, fractionToSubtract);
		assertNotEquals(null, updatedSubtractedIngredient);
		int actualUpdateComparisonCode = TEST_UTILITY.compareFractionObjects(expectedPantryQuantity, updatedSubtractedIngredient);
		assertEquals(expectedFractionComparisonResult, actualUpdateComparisonCode);
		removePantryIngredientHelper(String.valueOf(addedIngredient.getIngredientID()), REQUEST_SUCCESSFUL_CODE);
	}
	
	/**
	 * The add operation is expected to not return an error message as the user is logged in. The update ingredient operation should not return an error message and is expected
	 * to not have matching ingredient types. The subtraction operation is expected to return a message stating that the ingredient's threshold has been hit and moved to the shopping list.
	 * The remove operation is expected to return an HTTP error code as the ingredient cannot be found for removal.
	 * @throws Exception If there is a get or post failure.
	 */
	@Test
	public void testPantryListOperations_LoggedIn_ThresholdHitAfterOneSubtraction() throws Exception {
		String expectedSubtractionOperationReturnMessage = "ingredient has been updated, threshold value has been hit and the ingredient," + TEST_INGREDIENT_NAME + ", has been added!";
		shoppingListDAO.removeAllListIngredients(shoppingListName);
		pantryListDAO.removeAllListIngredients(pantryListName);
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(USER_NAME, PASSWORD, TEST_ACCOUNT_ROLE));
		PantryIngredient addedIngredient = addPantryIngredientHelper(null);
		updatePantryIngredientHelper(null, addedIngredient);
		subtractPantryIngredientHelper(expectedSubtractionOperationReturnMessage, TEST_INGREDIENT_WHOLE_NUMBER, TEST_INGREDIENT_FRACTION);
		removePantryIngredientHelper(String.valueOf(addedIngredient.getIngredientID()), DELETE_OPERATION_NO_INGREDIENT_FOUND);
	}
	
	/**
	 * The add operation is expected to not return an error message as the user is logged in. The update ingredient operation should not return an error message and is expected
	 * to not have matching ingredient types. The first subtraction operation should not return an error message as the threshold is not hit and the ingredient object should not be null.
	 * The second subtraction operation should return a message indicating that the ingredient has hit the threshold and the ingredient object should not point to null as the ingredient
	 * still remains. The remove operation is expected to return a successful HTTP code as the ingredient was found for removal.
	 * @throws Exception If there is a get or post failure.
	 */
	@Test
	public void testPantryListOperations_LoggedIn_ThresholdHitAfterTwoSubtractions() throws Exception {
		String wholeNumberToSubtract = "1";
		String fractionToSubtract = "1/2";
		String secondWholeNumberToSubtract = "0";
		String secondFractionToSubtract = "1/3";
		String expectedSubtractionOperationReturnMessage = "ingredient has been updated, threshold value has been hit and the ingredient," + TEST_INGREDIENT_NAME + ", has been added!";
		shoppingListDAO.removeAllListIngredients(shoppingListName);
		pantryListDAO.removeAllListIngredients(pantryListName);
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(USER_NAME, PASSWORD, TEST_ACCOUNT_ROLE));
		PantryIngredient addedIngredient = addPantryIngredientHelper(null);
		updatePantryIngredientHelper(null, addedIngredient);
		PantryIngredient updatedSubtractedIngredient = subtractPantryIngredientHelper(null, wholeNumberToSubtract, fractionToSubtract);
		assertNotEquals(null, updatedSubtractedIngredient);
		updatedSubtractedIngredient = subtractPantryIngredientHelper(expectedSubtractionOperationReturnMessage, secondWholeNumberToSubtract, secondFractionToSubtract);
		assertNotEquals(null, updatedSubtractedIngredient);
		removePantryIngredientHelper(String.valueOf(addedIngredient.getIngredientID()), REQUEST_SUCCESSFUL_CODE);
	}
	
	/**
	 * The add operation is expected to not return an error message as the user is logged in. The update ingredient operation should not return an error message and is expected
	 * to not have matching ingredient types. The first subtraction operation should not return an error message as the threshold is not hit and the ingredient object should not be null.
	 * The second subtraction operation should return a message indicating that the ingredient has hit the threshold and the ingredient object should point to null as the ingredient has been
	 * completely subtracted. The remove operation is expected to return an HTTP error code stating that the ingredient could not be found.
	 * @throws Exception If there is a get or post failure.
	 */
	@Test
	public void testPantryListOperations_LoggedIn_ThresholdHitAfterTwoWholeSubtractions() throws Exception {
		String wholeNumberToSubtract = "1";
		String fractionToSubtract = "1/2";
		String secondWholeNumberToSubtract = "0";
		String expectedSubtractionOperationReturnMessage = "ingredient has been updated, threshold value has been hit and the ingredient," + TEST_INGREDIENT_NAME + ", has been added!";
		shoppingListDAO.removeAllListIngredients(shoppingListName);
		pantryListDAO.removeAllListIngredients(pantryListName);
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(USER_NAME, PASSWORD, TEST_ACCOUNT_ROLE));
		PantryIngredient addedIngredient = addPantryIngredientHelper(null);
		updatePantryIngredientHelper(null, addedIngredient);
		PantryIngredient updatedSubtractedIngredient = subtractPantryIngredientHelper(null, wholeNumberToSubtract, fractionToSubtract);
		assertNotEquals(null, updatedSubtractedIngredient);
		updatedSubtractedIngredient = subtractPantryIngredientHelper(expectedSubtractionOperationReturnMessage, secondWholeNumberToSubtract, fractionToSubtract);
		assertEquals(null, updatedSubtractedIngredient);
		removePantryIngredientHelper(String.valueOf(addedIngredient.getIngredientID()), DELETE_OPERATION_NO_INGREDIENT_FOUND);
	}
	
	/**
	 * @param expectedMessage The expected message after performing the add pantry ingredient request.
	 * @throws Exception If there is a post error.
	 */
	private PantryIngredient addPantryIngredientHelper(String expectedMessage) throws Exception {
		MvcResult mvcResult = mvc.perform(post(ADD_PANTRY_LIST_INGREDIENT_URL).param(PANTRY_LIST_NAME_PARAM, pantryListName).param(PANTRY_LIST_INGREDIENT_NAME_PARAM, TEST_INGREDIENT_NAME)
			.param(PANTRY_LIST_INGREDIENT_UNIT_PARAM, TEST_INGREDIENT_UNIT).param(PANTRY_LIST_INGREDIENT_WHOLE_NUMBER_PARAM, TEST_INGREDIENT_WHOLE_NUMBER)
			.param(PANTRY_LIST_INGREDIENT_QUANTITY_PARAM, TEST_INGREDIENT_FRACTION).param(PANTRY_LIST_INGREDIENT_TYPE_PARAM, TEST_INGREDIENT_TYPE).with(csrf())).andReturn();
		
		ModelAndView modelAndView = mvcResult.getModelAndView();
		ModelMap modelMap = modelAndView.getModelMap();
		PantryIngredient addedIngredient = pantryListDAO.getSingleIngredient(pantryListName, TEST_INGREDIENT_NAME);
		assertEquals(expectedMessage, modelMap.get(MESSAGE_PARAM));
		if(addedIngredient != null) {
			assertNotEquals(null, addedIngredient);
		}
		return addedIngredient;
	}
	
	/**
	 * @param expectedMessage The expected message after performing the update pantry ingredient request.
	 * @param pantryIngredient A pantry ingredient for type comparison.
	 * @throws Exception If there is a post error. 
	 */
	private void updatePantryIngredientHelper(String expectedMessage, PantryIngredient pantryIngredient) throws Exception {
		MvcResult mvcResult = mvc.perform(post(UPDATE_PANTRY_LIST_INGREDIENT_URL).param(PANTRY_LIST_INGREDIENT_NAME_PARAM, TEST_INGREDIENT_NAME).param(PANTRY_LIST_NAME_PARAM, pantryListName)
				.param(PANTRY_LIST_INGREDIENT_WHOLE_NUMBER_PARAM, TEST_INGREDIENT_WHOLE_NUMBER).param(PANTRY_LIST_INGREDIENT_QUANTITY_PARAM, TEST_INGREDIENT_FRACTION)
				.param(PANTRY_LIST_INGREDIENT_UNIT_PARAM, TEST_INGREDIENT_UNIT).param(PANTRY_LIST_INGREDIENT_TYPE_PARAM, TEST_INGREDIENT_OTHER_TYPE).with(csrf())).andReturn();
		
		ModelAndView modelAndView = mvcResult.getModelAndView();
		ModelMap modelMap = modelAndView.getModelMap();
		PantryIngredient updatedIngredient = pantryListDAO.getSingleIngredient(pantryListName, TEST_INGREDIENT_NAME);
		assertEquals(expectedMessage, modelMap.get(MESSAGE_PARAM));
		if(updatedIngredient != null) {
			assertNotEquals(pantryIngredient.getIngredientType(), updatedIngredient.getIngredientType());
		}
	}
	
	/**
	 * @param expectedMessage The expected message after performing the subtract pantry ingredient request.
	 * @param wholeNumberToSubtract The whole number amount to subtract from a pantry ingredient.
	 * @param fractionToSubtract The fraction amount to subtract from a pantry ingredient.
	 * @throws Exception If there is a post error.
	 */
	private PantryIngredient subtractPantryIngredientHelper(String expectedMessage, String wholeNumberToSubtract, String fractionToSubtract) throws Exception {
		MvcResult mvcResult = mvc.perform(post(UPDATE_PANTRY_LIST_INGREDIENT_AMOUNT_URL).param(PANTRY_LIST_INGREDIENT_NAME_PARAM, TEST_INGREDIENT_NAME).param(PANTRY_LIST_NAME_PARAM, pantryListName)
				.param(PANTRY_LIST_INGREDIENT_WHOLE_NUMBER_PARAM, wholeNumberToSubtract).param(PANTRY_LIST_INGREDIENT_QUANTITY_PARAM, fractionToSubtract).with(csrf())).andReturn();
		ModelAndView modelAndView = mvcResult.getModelAndView();
		ModelMap modelMap = modelAndView.getModelMap();
		PantryIngredient updatedSubtractedIngredient = pantryListDAO.getSingleIngredient(pantryListName, TEST_INGREDIENT_NAME);
		assertEquals(expectedMessage, modelMap.get(MESSAGE_PARAM));
		return updatedSubtractedIngredient;
	}
	
	/**
	 * @param ingredientId The ingredient id to specify which ingredient to move.
	 * @throws Exception If there is a post error.
	 */
	private void removePantryIngredientHelper(String ingredientId, int expectedErrorCode) throws Exception {
		MvcResult mvcResult = mvc.perform(post(REMOVE_PANTRY_LIST_INGREDIENT_URL).param(REMOVE_PANTRY_LIST_INGREDIENT_ID_PARAM, ingredientId).param(THE_PANTRY_LIST_NAME_PARAM, pantryListName)
				.with(csrf())).andReturn();
		int deleteReturnCode = mvcResult.getResponse().getStatus();
		PantryIngredient removedIngredient = pantryListDAO.getSingleIngredient(pantryListName, TEST_INGREDIENT_NAME);
		assertEquals(null, removedIngredient);
		assertEquals(expectedErrorCode, deleteReturnCode);
	}
}
