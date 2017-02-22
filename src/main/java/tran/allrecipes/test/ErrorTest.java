package tran.allrecipes.test;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Todd
 * This is meant to test the ControllerAdvice implementation to see if it can catch
 * exceptions and return a message parameter with an informative message instead of just
 * printing a generic Tomcat error page.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=TestConfig.class)
@WebAppConfiguration
public class ErrorTest {
	/** URL mapping to a non existent URL. */
	private static final String NON_EXISTENT_URL = "/showCreateRecipe";
	/** URL mapping to show a recipe but missing a required parameter. */
	private static final String MISSING_PARAMETER_URL = "/showSingleRecipe";
	/** expected message when requesting a web page that is missing a required parameter. */
	private static final String GENERIC_ERROR_MESSAGE = "Unable to process your request!";
	/** expected HTTP status when requesting a web page that doesn't exist. */
	private static final int EXPECTED_REDIRECT_STATUS_CODE = Integer.parseInt(HttpStatus.FOUND.toString());
	/** parameter to get the expected message. */
	private static final String MESSAGE_PARAMETER = "message";
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
	 * This test makes a request to a page that doesn't exist, expected to have a redirection.
	 * @throws Exception If there is a get request error.
	 */
	@Test
	public void testNonExistingPage() throws Exception {
		helpMakeNonExistentURLRequest(NON_EXISTENT_URL, EXPECTED_REDIRECT_STATUS_CODE);
	}
	
	/**
	 * @param URLPath The URL mapping to the requested page.
	 * @param expectedHttpStatusCode The expected HTTP Status code after making the request.
	 * @throws Exception If there is a get request error.
	 */
	private void helpMakeNonExistentURLRequest(String URLPath, int expectedHttpStatusCode) throws Exception {
		MvcResult mvcResult = mvc.perform(get(URLPath)).andReturn();
		assertEquals(expectedHttpStatusCode, mvcResult.getResponse().getStatus());
	}
	
	/**
	 * This test makes a request to a page that requires a specified parameter,
	 * expected to return the generic error message.
	 * @throws Exception If there is a get request error.
	 */
	@Test
	public void testPageWithInvalidRequest() throws Exception {
		helpMakeInvalidRequest(MISSING_PARAMETER_URL, GENERIC_ERROR_MESSAGE);
	}
	
	/**
	 * @param URLPath The URL mapping to the requested page.
	 * @param expectedMessage The expected error message.
	 * @throws Exception If there is a get request error.
	 */
	private void helpMakeInvalidRequest(String URLPath, String expectedMessage) throws Exception {
		MvcResult mvcResult = mvc.perform(get(URLPath)).andReturn();
		ModelAndView modelAndView = mvcResult.getModelAndView();
		ModelMap modelMap = modelAndView.getModelMap();
		assertEquals(expectedMessage, modelMap.get(MESSAGE_PARAMETER));
	}
	
}
