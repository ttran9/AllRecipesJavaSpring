package tran.allrecipes.test;

import static org.junit.Assert.assertEquals;
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
import tran.allrecipes.presentation.model.Recipe;
import tran.allrecipes.presentation.model.User;
import tran.allrecipes.presentation.model.UserRole;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=TestConfig.class)
@WebAppConfiguration
public class EditRecipeTest {
	/** The URL to display the recipe edit page. */
	private static final String EDIT_RECIPE_INFORMATION_MAPPING = "editRecipe";
	/** The URL to process editing the recipe. */
	private static final String PROCESS_EDIT_RECIPE_INFORMATION_MAPPING = "/processEditRecipe";
	/** The parameter to indicate what recipe will be edited. */
	private static final String RECIPE_NAME_PARAM = "recipeName";
	/** The parameter to edit the recipe's prep time. */
	private static final String PREP_TIME_PARAM = "prepTimeUnparsed";
	/** The parameter to edit the recipe's cook time. */
	private static final String COOK_TIME_PARAM = "cookTimeUnparsed";
	/** The parameter to edit the recipe's dish type. */
	private static final String DISH_TYPE_PARAM = "dishType";
	/** The parameter to edit the recipe's image URL. */
	private static final String IMAGE_URL_PARAM = "imageURL";
	/** The parameter to edit the recipe's description. */
	private static final String RECIPE_DESCRIPTION_PARAM = "recipeDescription";
	/** The location of the data source file. */
	private static final String DATABASE_SOURCE_FILE = "database/Datasource.xml";
	/** Name of the recipes DAO bean. */
	private static final String RECIPE_DAO_BEAN_NAME = "RecipesDAO";
	/** Object to access utility methods used for testing. */
	private static final TestUtility TEST_UTILITY = new TestUtility();
	/** Object to hold a user's information. */
	private static final User TEST_USER = TEST_UTILITY.getTestAccount();
	/** List to hold a user's role(s). */
	private static final List<UserRole> TEST_ACCOUNT_ROLE = TEST_UTILITY.getTestAccountRoles();
	/** A test account's user name. */
	private static final String USER_NAME = TEST_USER.getUsername();
	/** A test account's encrypted password. */
	private static final String PASSWORD = TEST_USER.getPassword();
	/** The name of the test recipe */
	private static final String TEST_RECIPE_NAME = TEST_UTILITY.getTestRecipeName();
	/** Test prep time. */
	private static final String TEST_PREP_TIME = TEST_UTILITY.getTestPrepTime();
	/** Test cook time. */
	private static final String TEST_COOK_TIME = TEST_UTILITY.getTestCookTime();
	/** The recipe dish type. */
	private static final String DISH_TYPE = TEST_UTILITY.getTestDishType();
	/** A test description of a recipe. */
	private static final String recipeDescription = TEST_UTILITY.getTestDescription();
	/** String indicating that a user is not the owner of a recipe. */
	private static final String NOT_OWNER_OF_RECIPE = "you are not the owner of the recipe so you cannot edit it.";
	/** The message parameter of a HTTP redirect. */
	private static final String MESSAGE_PARAM = "message";
	/** An object to get access to the current class's application context. */
	private static ClassPathXmlApplicationContext appContext;
	/** An object to add and remove a recipe. */
	private static RecipeDAOImpl recipesDAO;
	/** The name of the jsp page for the edit recipe details. */
	private static final String EDIT_PAGE_NAME = "editRecipeDetails";
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
		recipesDAO = (RecipeDAOImpl) appContext.getBean(RECIPE_DAO_BEAN_NAME);
	}
	
	/** Closes data members. */
	@AfterClass
	public static void cleanUp() {
		recipesDAO = null;
		((ConfigurableApplicationContext) appContext).close();
	}
	
	/**
	 * Tests if a user can view the edit recipe page of a recipe owned by the user, expected to not return any error messages and expects to return the
	 * editRecipe string as a view name.
	 * @throws Exception If there is a get or post error.
	 */
	@Test
	public void testViewRecipeEditPage() throws Exception {
		recipesDAO.removeList(TEST_RECIPE_NAME, USER_NAME); // make sure the recipe doesn't exist before the test.
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(USER_NAME, PASSWORD, TEST_ACCOUNT_ROLE));
		TEST_UTILITY.helpMakeRecipe(mvc, recipesDAO, TEST_RECIPE_NAME, String.valueOf(TEST_UTILITY.getOldServings()), TEST_PREP_TIME, TEST_COOK_TIME, DISH_TYPE, recipeDescription);
	
		MvcResult mvcResult = mvc.perform(get("/" + EDIT_RECIPE_INFORMATION_MAPPING).param(RECIPE_NAME_PARAM, TEST_RECIPE_NAME)).andReturn();
		ModelAndView modelAndView = mvcResult.getModelAndView();
		ModelMap modelMap = modelAndView.getModelMap();
		assertEquals(EDIT_PAGE_NAME, mvcResult.getModelAndView().getViewName());
		assertEquals(null, modelMap.get(MESSAGE_PARAM));
	}
	
	/**
	 * Tests to see if a recipe can be edited that is existing.
	 * Test to see if a change can be made, then test to see if the change can be reverted.
	 * @throws Exception If there is a post error.
	 */
	@Test
	public void testProcessRecipeEdit() throws Exception {
		recipesDAO.removeList(TEST_RECIPE_NAME, USER_NAME); // make sure the recipe doesn't exist before the test.
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(USER_NAME, PASSWORD, TEST_ACCOUNT_ROLE));
		TEST_UTILITY.helpMakeRecipe(mvc, recipesDAO, TEST_RECIPE_NAME, String.valueOf(TEST_UTILITY.getOldServings()), TEST_PREP_TIME, TEST_COOK_TIME, DISH_TYPE, recipeDescription);
		
		// blank image URL.
		String editedDishType = "Salad";
		editRecipeHelper(TEST_RECIPE_NAME, TEST_PREP_TIME, TEST_COOK_TIME, editedDishType, "", recipeDescription, null);
		editRecipeHelper(TEST_RECIPE_NAME, TEST_PREP_TIME, TEST_COOK_TIME, DISH_TYPE, "", recipeDescription, null);
	}
	
	/**
	 * Tests to see if a non existent recipe can be edited, an error message stating that the user is not the owner of the recipe is expected. 
	 * @throws Exception If there is a post error.
	 */
	@Test
	public void testProcessNonExistingRecipeEdit() throws Exception {
		TestUtility testUtility = new TestUtility();
		User testUser = testUtility.getTestAccount();
		List<UserRole> testAccountRole = testUtility.getTestAccountRoles();
		String userName = testUser.getUsername();
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userName, testUser.getPassword(), testAccountRole));
		
		Recipe nonExistantRecipe = recipesDAO.getRecipe(testUtility.getInvalidRecipeName());
		assertEquals(null, nonExistantRecipe);
		editRecipeHelper(TEST_RECIPE_NAME, TEST_PREP_TIME, TEST_COOK_TIME, DISH_TYPE, "", recipeDescription, NOT_OWNER_OF_RECIPE);
	}
	
	/**
	 * @param recipeName The name of the recipe to modify.
	 * @param prepTime The time to prep the recipe.
	 * @param cookTime The time to cook the recipe.
	 * @param dishType The dish type of the recipe.
	 * @param imageURL The image's url.
	 * @param recipeDescription The expected description.
	 * @param expectedError Expected error string.
	 * @throws Exception If a post request fails.
	 */
	private void editRecipeHelper(String recipeName, String prepTime, String cookTime, String dishType, String imageURL, String recipeDescription, String expectedError) throws Exception {
		
		Recipe recipeToEdit = recipesDAO.getRecipe(TEST_RECIPE_NAME);
		
		// only one field was modified.
		MvcResult mvcResult = mvc.perform(post(PROCESS_EDIT_RECIPE_INFORMATION_MAPPING).param(RECIPE_NAME_PARAM, recipeName).param(PREP_TIME_PARAM, prepTime).
			param(COOK_TIME_PARAM, cookTime).param(DISH_TYPE_PARAM, dishType).param(IMAGE_URL_PARAM, imageURL).param(RECIPE_DESCRIPTION_PARAM, 
			recipeDescription).with(csrf())).andReturn();
		
		Recipe editedRecipe = recipesDAO.getRecipe(TEST_RECIPE_NAME); // the updated recipe.
		
		if(recipeToEdit != null && editedRecipe != null) {
			assertEquals(recipeToEdit.getRecipeName(), editedRecipe.getRecipeName());
			assertEquals(recipeToEdit.getPrepTime(), editedRecipe.getPrepTime());
			assertEquals(recipeToEdit.getCookTime(), editedRecipe.getCookTime());
			assertEquals(dishType, editedRecipe.getDishType());
			assertEquals(recipeToEdit.getRecipeDescription(), editedRecipe.getRecipeDescription());
		}
		else {
			assertEquals(expectedError, mvcResult.getModelAndView().getModel().get(MESSAGE_PARAM));
		}
	}
}
