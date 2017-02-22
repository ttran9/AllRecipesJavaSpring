package tran.allrecipes.test;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import tran.allrecipes.data.RecipeDAOImpl;
import tran.allrecipes.presentation.model.Recipe;
import tran.allrecipes.presentation.model.User;
import tran.allrecipes.presentation.model.UserRole;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=TestConfig.class)
@WebAppConfiguration
public class CreateRecipeTest {
	/** The URL to view the recipe creation page. */
	private static final String GET_CREATE_RECIPE_URL = "showCreateRecipe";
	/** URL mapping to delete a specified recipe. */
	private static final String DELETE_RECIPE_URL = "deleteRecipe";
	/** The parameter to indicate what recipe to add to. */
	private static final String RECIPE_NAME_PARAM = "recipeName";
	/** The parameter to specify a redirect message. */
	private static final String REDIRECT_MESSAGE_PARAMETER = "message";
	/** Redirected to show all recipes page. */
	private static final String REDIRECTED_TO_ALL_RECIPES_PAGE = "redirect:/";
	/** Message to indicate the recipe was successfully deleted. */
	private static final String RECIPE_DELETION_SUCCESS_MESSAGE = "successfully deleted recipe!";
	/** Object to access utility methods used for testing. */
	private static final TestUtility TEST_UTILITY = new TestUtility();
	/** Object to hold a user's information. */
	private static final User TEST_USER = TEST_UTILITY.getTestAccount();
	/** List to hold a user's role(s). */
	private static final List<UserRole> TEST_ACCOUNT_ROLES = TEST_UTILITY.getTestAccountRoles();
	/** A test account's user name. */
	private static final String USER_NAME = TEST_USER.getUsername();
	/** A test account's encrypted password. */
	private static final String PASSWORD = TEST_USER.getPassword();
	/** The name of the test recipe */
	private static final String TEST_RECIPE_NAME = TEST_UTILITY.getTestRecipeName();
	/** A test string for the time to prep a recipe. */
	private static final String PREP_TIME = TEST_UTILITY.getTestPrepTime();
	/** A test string for the time to cook a recipe. */
	private static final String COOK_TIME = TEST_UTILITY.getTestCookTime();
	/** The recipe dish type. */
	private static final String DISH_TYPE = TEST_UTILITY.getTestDishType();
	/** A test description of a recipe. */
	private static final String recipeDescription = TEST_UTILITY.getTestDescription();
	/** The location of the data source file. */
	private static final String DATABASE_SOURCE_FILE = "database/Datasource.xml";
	/** Name of the recipes DAO bean. */
	private static final String RECIPE_DAO_BEAN_NAME = "RecipesDAO";
	/** An object to get access to the current class's application context. */
	private static ClassPathXmlApplicationContext appContext;
	/** An object to add and remove a recipe. */
	private static RecipeDAOImpl recipesDAO;
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
	 * tests to see if an authenticated user can view the recipe creation page.
	 * @throws Exception If there is a get failure.
	 */
	@Test
	public void testCreateRecipePage() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(USER_NAME, PASSWORD, TEST_ACCOUNT_ROLES));
		MvcResult mvcResult = mvc.perform(get("/" + GET_CREATE_RECIPE_URL)).andReturn();
		ModelAndView modelAndView = mvcResult.getModelAndView();
		assertEquals(GET_CREATE_RECIPE_URL, modelAndView.getViewName());
	}
		
	/**
	 * tests if an authenticated user can create a recipe.
	 * to re-run this test deleting the existing recipe will be necessary after creation.
	 * @throws Exception If there is a post failure.
	 */
	@Test
	public void testMakeRecipe() throws Exception {
		recipesDAO.removeList(TEST_RECIPE_NAME, USER_NAME); // make sure the recipe doesn't exist before the test.
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(USER_NAME, PASSWORD, TEST_ACCOUNT_ROLES));
		TEST_UTILITY.helpMakeRecipe(mvc, recipesDAO, TEST_RECIPE_NAME, String.valueOf(TEST_UTILITY.getOldServings()), PREP_TIME, COOK_TIME, DISH_TYPE, recipeDescription);
		
		MvcResult mvcResult = mvc.perform(get("/" + DELETE_RECIPE_URL).param(RECIPE_NAME_PARAM, TEST_RECIPE_NAME)).andReturn();
		ModelAndView modelAndView = mvcResult.getModelAndView();
		String resultingViewName = modelAndView.getViewName();
		Object resultingDeletionMessage = modelAndView.getModelMap().get(REDIRECT_MESSAGE_PARAMETER);
		Recipe newlyCreatedRecipe = recipesDAO.getRecipe(TEST_RECIPE_NAME);
		
		assertEquals(null, newlyCreatedRecipe);
		assertEquals(REDIRECTED_TO_ALL_RECIPES_PAGE, resultingViewName);
		assertEquals(RECIPE_DELETION_SUCCESS_MESSAGE, resultingDeletionMessage);
	}
}
