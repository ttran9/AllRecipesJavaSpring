package tran.allrecipes.service;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author Todd
 * A class to provide methods to check if the user is logged in and redirects the user with notifications accordingly.
 */
public class LoginServiceImpl {
	/** Redirect to the show recipes page.*/
	private static final String REDIRECT_SHOW_RECIPES = "redirect:/";
	/** The name of the signin page. */
    private static final String SIGNIN_PAGE = "signin";
    /** Parameter to display error notifications.*/
    private static final String MESSAGE_KEY = "error";
    /** A parameter to display messages when redirected. */
    private static final String REDIRECT_MESSAGE_KEY = "message";
    /** A message indicating that a user is already logged in.*/
    private static final String LOGGED_IN_ERROR_MESSAGE = "you are already logged in, there is no need for you to log in again.";
    /** A generic message indicating that the login has failed.*/
    private static final String GENERIC_LOGIN_ERROR_MESSAGE = "an error has occurred please try again";
    /** An invalid credentials message. */
    private static final String INVALID_CREDENTIALS_ERROR_MESSAGE = "invalid login credentials";
    /** A string to get the badcredentials exception error message. */
    private static final String SPRING_SECURITY_LAST_EXCEPTION_STRING = "SPRING_SECURITY_LAST_EXCEPTION";

    public LoginServiceImpl() {}
    
    /**
     * @param principal An object holding authentication information of the current user.
     * @param model An object holding UI information.
     * @param errorMessage An object holding a description of any error(s) (when applicable).
     * @return Returns the user to the sign in page if not logged in, if already logged in redirects the user to the recipes page with a notification.
     */
    public String displayLogin(Principal principal, ModelMap model, String errorMessage, RedirectAttributes redirectAttributes) {
		if(principal != null) {
			redirectAttributes.addAttribute(REDIRECT_MESSAGE_KEY, LOGGED_IN_ERROR_MESSAGE);
            return REDIRECT_SHOW_RECIPES;
        }
        else {
            // not logged in
        	if(errorMessage != null)
        		model.addAttribute(MESSAGE_KEY, errorMessage);
            return SIGNIN_PAGE;
        }
    }

    /**
     * @param model An object holding UI interaction.
     * @param request An object holding information about the HTTP request.
     * @return Returns the user to the signin page with a notification related to the error.
     */
    public String displayLoginError(ModelMap model, HttpServletRequest request) {
        if(request.getSession().getAttribute(SPRING_SECURITY_LAST_EXCEPTION_STRING) != null) {
            BadCredentialsException customError = (BadCredentialsException) request.getSession().
                    getAttribute(SPRING_SECURITY_LAST_EXCEPTION_STRING);
            if(customError != null) {
                if(customError.getMessage() != null) {
                	model.addAttribute(MESSAGE_KEY, customError.getMessage());
                }
            }
            else {
                model.addAttribute(MESSAGE_KEY, GENERIC_LOGIN_ERROR_MESSAGE);
            }
        }
        else {
            model.addAttribute(MESSAGE_KEY, INVALID_CREDENTIALS_ERROR_MESSAGE);
        }
        return SIGNIN_PAGE;
    }
}
