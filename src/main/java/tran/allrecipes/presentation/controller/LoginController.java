package tran.allrecipes.presentation.controller;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tran.allrecipes.service.LoginServiceImpl;

/**
 * @author Todd
 * A class to provide mappings for when a user tries to login, fails to login, or has been redirected when not logged in and trying to view a protected page.
 */
@Controller
public class LoginController {
	/** URL mapping to the signin page. */
	private static final String DISPLAY_LOG_IN_MAPPING = "/signin";
	/** URL mapping to the signin page with an error message.*/
	private static final String DISPLAY_LOG_IN_ERROR_MAPPING = "/signinError";
	/** Message parameter when being redirected to the login page. */
	private static final String REDIRECT_MESSAGE_PARAM = "message";
	
	@RequestMapping(value=DISPLAY_LOG_IN_MAPPING, method=RequestMethod.GET)
	public String displayLogin(Principal principal, ModelMap model, @RequestParam(value=REDIRECT_MESSAGE_PARAM, required=false) String errorMessage, RedirectAttributes redirectAttributes) {
		LoginServiceImpl loginService = new LoginServiceImpl();
		return loginService.displayLogin(principal, model, errorMessage, redirectAttributes);
	}
	
	@RequestMapping(value=DISPLAY_LOG_IN_ERROR_MAPPING, method=RequestMethod.GET)
	public String displayLoginError(ModelMap model, HttpServletRequest request) {
		LoginServiceImpl loginService = new LoginServiceImpl();
		return loginService.displayLoginError(model, request);
	}
}
