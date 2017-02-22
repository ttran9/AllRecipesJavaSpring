package tran.allrecipes.test;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import tran.allrecipes.data.UsersDAOImpl;
import tran.allrecipes.presentation.model.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=TestConfig.class)
@WebAppConfiguration
public class RegisterTest {
	/** the name of the register.jsp page. */
	private static final String EXPECTED_VIEW_NAME = "register";
	/** the url to process the user registration.  */
	private static final String PROCESS_REGISTRATION_URL = "processRegistration";
	/** the url to check the user name field on the registration form using an AJax call. */
	private static final String CHECK_REGISTRATION_USER_NAME_URL = "checkRegistrationUserNameField";
	/* the user name input field on the registration form. */
	private static final String REGISTER_USER_NAME_PARAM = "userName";
	/* the password input field on the registration form. */
	private static final String REGISTER_PASSWORD_PARAM = "password";
	/** the verify password input field on the registration form. */
	private static final String REGISTER_VERIFY_PASSWORD_PARAM = "validatePassword";
	/** the user name input field on the registration for the check user name on the registration page.*/
	private static final String USER_NAME_PARAM = "userName";
	/** An object to get access to utility methods. */
	private static final TestUtility TEST_UTILITY = new TestUtility();
	/** the password for the user registration. */
	private static final String VALID_PASSWORD = TEST_UTILITY.getTestAccountPassword(); // meets regex requirements.
	/** the account name for the test account. */
	private static final String TEST_ACCOUNT_NAME = "testuser012345";
	/** the account name for a test account that is invalid. */
	private static final String INVALID_TEST_ACCOUNT_NAME = "short";
	/** The location of the data source file. */
	private static final String DATABASE_SOURCE_FILE = "database/Datasource.xml";
	/** Name of the users DAO bean. */
	private static final String USER_DAO_BEAN_NAME = "UsersDAO";
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
	
	/**
	 * The view name, register, is expected to be returned because the user is not logged in and attempting to view the
	 * registration page.
	 * @throws Exception If there is a get error.
	 */
	@Test
	public void testViewRegisterPage() throws Exception {

		MvcResult mvcResult = mvc.perform(get("/" + EXPECTED_VIEW_NAME)).andReturn();
		ModelAndView modelAndView = mvcResult.getModelAndView();
		assertEquals(EXPECTED_VIEW_NAME, modelAndView.getViewName());

	}
	
	/** 
	 * The user object is expected to not be null because a user was just created.
	 * @throws Exception If there is a post error.
	 */
	@Test
	public void testRegisterUser() throws Exception {
		ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
		UsersDAOImpl usersDAO = (UsersDAOImpl) appContext.getBean(USER_DAO_BEAN_NAME);
		mvc.perform(post("/" + PROCESS_REGISTRATION_URL).param(REGISTER_USER_NAME_PARAM, TEST_ACCOUNT_NAME).param(REGISTER_PASSWORD_PARAM, VALID_PASSWORD)
				.param(REGISTER_VERIFY_PASSWORD_PARAM, VALID_PASSWORD).with(csrf())).andReturn();
		User user = usersDAO.getUser(TEST_ACCOUNT_NAME); // should return an object because the user was created. 
		assertNotEquals(null, user);
		deleteTestRegisterUser();
		usersDAO = null;
		((ConfigurableApplicationContext) appContext).close();
	}
	
	/**
	 * Expected to return an internal server error code as the user name does not exist.
	 * @throws Exception If a get error occurs.
	 */
	@Test
	public void testCheckUserNameRegistrationField() throws Exception {

		MvcResult mvcResult = mvc.perform(get("/" + CHECK_REGISTRATION_USER_NAME_URL).param(USER_NAME_PARAM, INVALID_TEST_ACCOUNT_NAME)).andReturn();
		int server_error = Integer.parseInt(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		assertEquals(server_error, mvcResult.getResponse().getStatus());

	}
	
	/**
	 * A method to delete the brand new created test user.
	 */
	private void deleteTestRegisterUser() {
		ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
		UsersDAOImpl usersDAO = (UsersDAOImpl) appContext.getBean(USER_DAO_BEAN_NAME);
		User user = usersDAO.getUser(TEST_ACCOUNT_NAME);
		if(user != null) {
			// user detected, so delete.
			usersDAO.deleteUser(TEST_ACCOUNT_NAME);
		}
		usersDAO = null;
		((ConfigurableApplicationContext) appContext).close();
	}
		
}
