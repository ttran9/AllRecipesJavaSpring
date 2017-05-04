package tran.allrecipes.service;

import java.security.Principal;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tran.allrecipes.data.PersistentTokensDAOImpl;

/**
 * @author Todd
 * A service to log an authenticated user out, or notifies that the user was never logged in.
 */
@Service
public class LogoutServiceImpl {
	/** redirect path to the show recipes page.*/
	private static final String REDIRECT_TO_MAIN_PAGE = "redirect:/";
	/** redirect message parameter. */
	private static final String REDIRECT_MESSAGE_PARAM = "message";
	/** the remember me cookie name. */
	private static final String REMEMBER_ME_TOKEN_NAME = "AR-Remember-Me-Cookie";
	/** The persistent tokens bean name. */
	private static final String PERSISTENT_TOKENS_BEAN_NAME = "PersistentTokensDAO";
	/** The location of the data source file. */
	private static final String DATABASE_SOURCE_FILE = "database/Datasource.xml";	
	/** A separating character in the remember me cookie. */
	private static final String DELIMITER = ":";
	/** Cookie not base64 encoded error message. */
	private static final String COOKIE_ENCODING_ERROR = "the cookie from this logged in user was not Base64 encoded.";
	/** Debugging error message for a log out that may have failed or had a side effect. */
	private static final String LOG_OUT_DEBUG_MESSAGE = "a remember-me log out error.";
	
	public LogoutServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param principal An object holding authentication information of the current user.
	 * @param request An object holding information about the HTTP request
	 * @param response An object holding information about the HTTP response
	 * @param redirectAttrs An object holding the attributes for when a redirection is done.
	 */
	public String processLogout(Principal principal, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttrs) {
		String informationMessage = "you never logged in";
		UtilityServiceImpl utilityService = new UtilityServiceImpl();
		if(utilityService.isUserAuthenticated(principal)) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if(auth != null) {
				// log the user out.
				new SecurityContextLogoutHandler().logout(request, response, auth);
				if(SecurityContextHolder.getContext().getAuthentication() != null) {
					informationMessage = "unable to log you out";
				}
				else {
					String unparsedPersistentTokens = getRememberMeCookieValue(request);
					String seriesIdentifier = decodeCookie(unparsedPersistentTokens);
					ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
					PersistentTokensDAOImpl persistentTokensDAO = (PersistentTokensDAOImpl)appContext.getBean(PERSISTENT_TOKENS_BEAN_NAME);
					
					if(persistentTokensDAO.deleteUniqueUser(seriesIdentifier) == 1) {
						informationMessage = "logged out!";
					}
					else {
						System.out.println(LOG_OUT_DEBUG_MESSAGE);
						informationMessage = LOG_OUT_DEBUG_MESSAGE;
					}
					persistentTokensDAO = null;
					((ConfigurableApplicationContext) appContext).close();
				}
			}
		}
		redirectAttrs.addAttribute(REDIRECT_MESSAGE_PARAM, informationMessage);
		return REDIRECT_TO_MAIN_PAGE;
	}
	
	/**
	 * @param request An object holding user request information. 
	 * purpose of this method is to get a string which will be used to decode the unique series identifier to delete
	 * the remember token for this user name on a SPECIFIC device.
	 * @return The cookie value for this current user on a specific device.
	 */
	private String getRememberMeCookieValue(HttpServletRequest request) {
		Cookie[] sentCookies = request.getCookies();
		for(Cookie cookie: sentCookies)  {
			if(cookie.getName().equals(REMEMBER_ME_TOKEN_NAME)) {
				// remove the cookie from the browser if it exists.
				return cookie.getValue();
			}
		}
		return "";
	}
	
	/**
	 * code taken and modified from: org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices 
	 * modified to get the series ID unique identifier.
	 * Decodes the cookie and splits it into a set of token strings using the ":" delimiter.
	 * @param cookieValue the value obtained from the submitted cookie
	 * @return The unique series identifier for a given user on a specific device.
	 */
	private String decodeCookie(String cookieValue) {
		try {
			if (!Base64.isBase64(cookieValue.getBytes())) {
				throw new InvalidCookieException(COOKIE_ENCODING_ERROR);
			}
		}
		catch(InvalidCookieException e) {
			System.out.println(e.getMessage());
			return ""; // invalid series identifier for when the string is not Base64 encoded. */
		}
		
		for (int j = 0; j < cookieValue.length() % 4; j++) {
			cookieValue = cookieValue + "=";
		}

		String plainTextCookie = new String(Base64.decode(cookieValue.getBytes()));

		String[] tokens = StringUtils.delimitedListToStringArray(plainTextCookie, DELIMITER);

		return tokens[0]; // first token is the series identifier, second is the "token"
	}
	
}
