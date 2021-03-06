package tran.allrecipes.presentation.controller;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import tran.allrecipes.service.UtilityServiceImpl;

/**
 * @author Todd
 * A class to handle all request errors, such as not entering the required parameters to access a page, or going to a page
 * without a URL mapping (a 404 error).
 */
@ControllerAdvice
public class ControllerAdviceImpl  {
	/** The name of the error page. */
	private static final String ERROR_PAGE_NAME = "errorPage";
	/** message parameter name. */
	private static final String MESSAGE_PARAM = "message";
	/** string to notify that the user has been re-directed. */
	private static final String REDIRECTED_NOTIFICATION = "error handler, the reason is: ";
	/** string to indicate this was a bad request, 400 error. */
	private static final String GENERIC_ERROR_MESSAGE = "Unable to process your request!";
	/** string to indicate the web page the user was attempting to request. */
	private static final String ATTEMPTED_REQUEST_URL = "error handler! the requested path/resources was: ";
	/** The attribute to have a second hyper link on the navigation bar. */
	private static final String SECOND_NAVBAR_LINK = "secondNavbarLink";
	/** The URL of the second hyper link on the navigation bar. */
	private static final String SECOND_NAVBAR_LINK_URL = "showCreateRecipe";
	/** The attribute to display text of the second hyper link on the navigation bar. */
	private static final String SECOND_NAVBAR_LINK_TEXT_ATTRIBUTE = "secondNavbarLinkText";
	/** The text of the second hyper link on the navigation bar. */
	private static final String SECOND_NAVBAR_LINK_TEXT = "Create Recipe!";
	/** Attribute name to indicate if there is content on the right side of the navigation bar. */
	private static final String RIGHT_BAR_ATTRIBUTE = "isRightBar";
	/** Flag to indicate if there is content on the right side of the navigation bar. */
	private static final boolean RIGHT_BAR_CONTENT = true;
	/** Attribute name to specify the appearance of the right menu items. */
	private static final String RIGHT_MENU_TYPE = "rightMenuType";
	/** String to describe the appearance of the right hand side's drop down menu. */
	private static final String RIGHT_MENU_ITEMS_APPEARANCE = "genericRightMenu";
	/** The title of the page attribute. */
	private static final String PAGE_TITLE_ATTRIBUTE = "title";
    /** Page title. */
	private static final String PAGE_TITLE = "Requested Page not found";
	/** logged in user name attribute. */
	private static final String LOGGED_IN_NAME_ATTRIBUTE = "loggedInName";
	

	/**
	 * @param exception The object holding the contents of the reason of the redirect.
	 * @param request An object holding request data, such as the URL that was being requested.
	 * @return An object with a view name, in this case the error not found page, and a model object containing
	 * an object with the exception message.
	 */
	@ExceptionHandler(value=Exception.class)
	public ModelAndView notFoundHandler(Exception exception, HttpServletRequest request, Principal principal) {
		// logging for debugging purposes.
		System.out.println(ATTEMPTED_REQUEST_URL + request.getServletPath());
		System.out.println(REDIRECTED_NOTIFICATION + exception.getMessage());
		ModelAndView modelAndView = new ModelAndView(ERROR_PAGE_NAME);
		// to do: use switch cases to print a more detailed message such as not found, bad request, server error etc.
		// how do I get the response code from Tomcat.
		modelAndView.addObject(MESSAGE_PARAM, GENERIC_ERROR_MESSAGE);
		UtilityServiceImpl utilityService = new UtilityServiceImpl();
		if(utilityService.isUserAuthenticated(principal)) { // verify the user is authenticated.
			modelAndView.addObject(SECOND_NAVBAR_LINK, SECOND_NAVBAR_LINK_URL);
			modelAndView.addObject(SECOND_NAVBAR_LINK_TEXT_ATTRIBUTE, SECOND_NAVBAR_LINK_TEXT);
			modelAndView.addObject(RIGHT_MENU_TYPE, RIGHT_MENU_ITEMS_APPEARANCE);
			modelAndView.addObject(LOGGED_IN_NAME_ATTRIBUTE, principal.getName());
		}
		modelAndView.addObject(RIGHT_BAR_ATTRIBUTE, RIGHT_BAR_CONTENT);
		modelAndView.addObject(PAGE_TITLE_ATTRIBUTE, PAGE_TITLE);
		return modelAndView;
	}
		
}
