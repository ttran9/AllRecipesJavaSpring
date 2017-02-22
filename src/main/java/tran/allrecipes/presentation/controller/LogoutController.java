package tran.allrecipes.presentation.controller;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tran.allrecipes.service.LogoutServiceImpl;

/**
 * @author Todd
 * A class that provides the mapping to log out an authenticated in user.
 */
@Controller
public class LogoutController {

	/** The URL mapping to process a log out attempt. */
	private static final String LOGOUT_URL = "/logout";
	
	@RequestMapping(value=LOGOUT_URL, method = RequestMethod.GET)
	public String processLogout(Principal principal, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttrs) {
		LogoutServiceImpl logoutService = new LogoutServiceImpl();
		return logoutService.processLogout(principal, request, response, redirectAttrs);
	}
}
