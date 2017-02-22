package tran.allrecipes.presentation.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

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

	/**
	 * @param exception The object holding the contents of the reason of the redirect.
	 * @param request An object holding request data, such as the URL that was being requested.
	 * @return An object with a view name, in this case the error not found page, and a model object containing
	 * an object with the exception message.
	 */
	@ExceptionHandler(value=Exception.class)
	public ModelAndView notFoundHandler(Exception exception, HttpServletRequest request) {
		// logging for debugging purposes.
		System.out.println(ATTEMPTED_REQUEST_URL + request.getServletPath());
		System.out.println(REDIRECTED_NOTIFICATION + exception.getMessage());
		ModelAndView modelAndView = new ModelAndView(ERROR_PAGE_NAME);
		// to do: use switch cases to print a more detailed message such as not found, bad request, server error etc.
		// how do I get the response code from Tomcat.
		modelAndView.addObject(MESSAGE_PARAM, GENERIC_ERROR_MESSAGE);
		return modelAndView;
	}
		
}
