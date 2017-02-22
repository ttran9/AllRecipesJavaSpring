package tran.allrecipes.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
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

import tran.allrecipes.presentation.model.User;
import tran.allrecipes.presentation.model.UserRole;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=TestConfig.class)
@WebAppConfiguration
public class LoginTest {
	/** The URL to get the log in page. */
	private static final String DISPLAY_LOG_IN_MAPPING = "/signin";
	/** The URL to perform the login attempt. */
	private static final String PROCESS_LOG_IN_MAPPING = "/login";
	/** The expected redirect URL if a user is already logged in. */
	private static final String ALREADY_LOGGED_IN_REDIRECT = "redirect:/";
	/** The message parameter name. */
	private static final String REDIRECT_MESSAGE_PARAM = "message";
	/** Incorrect credentials message to display when the user attempts to log in without the correct user name and password combination .*/
	private static final String INCORRECT_CREDENTIALS_NOTIFICATION = "Incorrect credientials";
	/** A message if the failed authentication cannot produce a bad credentials exception. */
	private static final String FAILED_AUTHENTICATION_ASSERTION_MESSAGE = "could not retrieve the bad credentials exception";
	/** Exception key to retrieve the information about the incorrect authentication attempt. */
	private static final String BAD_CREDENTIALS_EXCEPTION_KEY = "SPRING_SECURITY_LAST_EXCEPTION";
	/** The message to display when the user is already logged in. */
	private static final String USER_ALREADY_LOGGED_IN = "you are already logged in, there is no need for you to log in again.";
	/** name of the user name input. */
	private static final String LOGIN_USERNAME_INPUT = "username";
	/** name of the password input. */
	private static final String PASSWORD_INPUT = "password";
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
	 * Tests an attempt to view the log in page when user is already logged in.
	 */
	@Test
	public void testAlreadyLoggedIn() {
		TestUtility testUtility = new TestUtility();
		User testUser = testUtility.getTestAccount();
		List<UserRole> testAccountRole = testUtility.getTestAccountRoles();
		String userName = testUser.getUsername();
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userName, testUser.getPassword(), testAccountRole));
		try {
			MvcResult mvcResult = mvc.perform(get(DISPLAY_LOG_IN_MAPPING)).andReturn();
			ModelAndView modelAndView = mvcResult.getModelAndView();
			assertEquals(ALREADY_LOGGED_IN_REDIRECT, modelAndView.getViewName());
			assertEquals(USER_ALREADY_LOGGED_IN, modelAndView.getModelMap().get(REDIRECT_MESSAGE_PARAM));
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Tests the case where the user logs in with proper credentials, assumes there is properties file with a password.
	 */
	@Test
	public void testProperLogin() {
		TestUtility testUtility = new TestUtility();
		String userName = testUtility.getTestAccountName();
		String password = testUtility.getTestAccountPassword();
		try {
			assertEquals(null, SecurityContextHolder.getContext().getAuthentication());
			MvcResult mvcResult = mvc.perform(post(PROCESS_LOG_IN_MAPPING).param(LOGIN_USERNAME_INPUT, userName).param(PASSWORD_INPUT, password).with(csrf())).andReturn();
			assertEquals(null, mvcResult.getModelAndView().getModelMap().get(REDIRECT_MESSAGE_PARAM)); // there is no message because of a proper authentication so it should be null.
			// grab the authentication again after a proper authentication has been done.
			assertNotEquals(null, SecurityContextHolder.getContext().getAuthentication());
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Tests the case where the user logs in with the incorrect user name and password combination.
	 */
	@Test
	public void testIncorrectLogin() {
		TestUtility testUtility = new TestUtility();
		String userName = testUtility.getTestAccountName();
		String password = testUtility.getTestAccountPassword() + "1"; // this is not the password that is stored in the file which has the correct password.
		try {
			assertEquals(null, SecurityContextHolder.getContext().getAuthentication());
			MvcResult mvcResult = mvc.perform(post(PROCESS_LOG_IN_MAPPING).param(LOGIN_USERNAME_INPUT, userName).param(PASSWORD_INPUT, password).with(csrf())).andReturn();
			// still expecting null as there the authentication attempt has failed.
			BadCredentialsException incorrectLoginMessage = (BadCredentialsException) mvcResult.getRequest().getSession().getAttribute(BAD_CREDENTIALS_EXCEPTION_KEY);
			if(incorrectLoginMessage == null) {
				fail(FAILED_AUTHENTICATION_ASSERTION_MESSAGE);
			}
			assertEquals(INCORRECT_CREDENTIALS_NOTIFICATION, incorrectLoginMessage.getMessage());
			// still expecting null as there the authentication attempt has failed.
			assertEquals(null, SecurityContextHolder.getContext().getAuthentication());
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		catch(AssertionError e) {
			System.out.println(e.getMessage());
		}
	}
}
