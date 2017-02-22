package tran.allrecipes.service;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.stereotype.Service;
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
					Cookie[] sentCookies = request.getCookies();
					String userName = principal.getName();
					if(sentCookies != null) { 
						for(Cookie cookie: sentCookies)  {
							if(cookie.getName().equals(REMEMBER_ME_TOKEN_NAME)) {
								// remove the cookie from the browser if it exists.
								utilityService.removeARCookieFromBrowser(request, response);
							}
						}
					}
					ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
					PersistentTokensDAOImpl persistentTokensDAO = (PersistentTokensDAOImpl)appContext.getBean(PERSISTENT_TOKENS_BEAN_NAME);
					List<PersistentRememberMeToken> tokenList = persistentTokensDAO.getPersistentTokens(userName);
					
					for(PersistentRememberMeToken token : tokenList) {
						if(token != null) {
							utilityService.getRepositoryTokenWithDataSource().removeUserTokens(userName);
						}
					}
					informationMessage = "logged out!";
					persistentTokensDAO = null;
					((ConfigurableApplicationContext) appContext).close();
				}
			}
		}
		redirectAttrs.addAttribute(REDIRECT_MESSAGE_PARAM, informationMessage);
		return REDIRECT_TO_MAIN_PAGE;
	}
}
