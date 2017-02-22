package tran.allrecipes.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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

import tran.allrecipes.presentation.model.User;
import tran.allrecipes.presentation.model.UserRole;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=TestConfig.class)
@WebAppConfiguration
public class LogoutTest {
	/** redirect message parameter. */
	private static final String REDIRECT_MESSAGE_PARAM = "message";
	/** The expected redirect message.*/
	private static final String EXPECT_REDIRECT_MESSAGE = "logged out!";
	/** The expected not logged in message. */
	private static final String EXPECT_NOT_LOGGED_IN_MESSAGE = "you never logged in";
	/** The URL mapping to process a log out attempt. */
	private static final String LOGOUT_URL = "/logout";
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
	 * Tests if there is an authentication object before and after a logout attempt.
	 * Also test for an expected message upon successful log out.
	 */
	@Test
	public void testSuccessfulLogout() {
		TestUtility testUtility = new TestUtility();
		User testUser = testUtility.getTestAccount();
		List<UserRole> testAccountRole = testUtility.getTestAccountRoles();
		String userName = testUser.getUsername();
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userName, testUser.getPassword(), testAccountRole));
		assertNotEquals(null, SecurityContextHolder.getContext().getAuthentication());
		try {
			MvcResult mvcResult = mvc.perform(get(LOGOUT_URL)).andReturn();
			ModelAndView modelAndView = mvcResult.getModelAndView();
			ModelMap uiMap = modelAndView.getModelMap();
			assertEquals(null, SecurityContextHolder.getContext().getAuthentication());
			assertEquals(EXPECT_REDIRECT_MESSAGE, uiMap.get(REDIRECT_MESSAGE_PARAM));
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Tests for an expected message after an unsuccessful log out.
	 */
	@Test
	public void testLogout_NotLoggedIn() {
		try {
			MvcResult mvcResult = mvc.perform(get(LOGOUT_URL)).andReturn();
			ModelAndView modelAndView = mvcResult.getModelAndView();
			ModelMap uiMap = modelAndView.getModelMap();
			assertEquals(EXPECT_NOT_LOGGED_IN_MESSAGE, uiMap.get(REDIRECT_MESSAGE_PARAM));
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
