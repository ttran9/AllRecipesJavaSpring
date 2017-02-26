package tran.allrecipes.presentation.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tran.allrecipes.presentation.model.User;
import tran.allrecipes.service.RegisterServiceImpl;

/**
 * @author Todd
 * A class to handle the case of when the user attempts to register.
 */
@Controller
public class RegisterController {
	/** URL mapping to the registration page. */
	private static final String REGISTER_PAGE = "/register";
	/** URL mapping to check if a user name is taken for registration purposes. */
	private static final String CHECK_USER_REGISTRATION_NAME = "/checkRegistrationUserNameField";
	/** URL mapping to submit registration data fields. */
	private static final String PROCESS_REGISTRATION = "/processRegistration";
	/** Parameter to display error messages related to the attempted registration. */
	private static final String ERROR_PARAM = "error";
	/** The parameter to take in a user name. */
	private static final String USER_NAME_PARAM = "userName";

	@RequestMapping(value=REGISTER_PAGE, method=RequestMethod.GET)
	public String showRegistration(Principal principal, ModelMap model, @RequestParam(value=ERROR_PARAM, required=false) String error, RedirectAttributes redirectAttrs) {
		RegisterServiceImpl registerService = new RegisterServiceImpl();
		return registerService.showRegistration(principal, model, error, redirectAttrs);
	}
		
	@ResponseBody
	@RequestMapping(value=CHECK_USER_REGISTRATION_NAME, method = RequestMethod.GET)
	public ResponseEntity<String> checkRegistrationUserName(Principal principal, @RequestParam(value=USER_NAME_PARAM) String userName) {
		RegisterServiceImpl registerService = new RegisterServiceImpl();
		return registerService.checkRegistrationUserName(principal, userName);
	}
	
	@RequestMapping(value=PROCESS_REGISTRATION, method = RequestMethod.POST)
	public String processRegistration(Principal principal, @ModelAttribute User user, RedirectAttributes redirectAttrs) {
		RegisterServiceImpl registerService = new RegisterServiceImpl();
		return registerService.processRegistration(principal, user.getUsername(), user.getPassword(), user.getValidatePassword(), redirectAttrs);
	}
}
