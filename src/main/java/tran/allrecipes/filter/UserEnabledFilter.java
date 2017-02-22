package tran.allrecipes.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import tran.allrecipes.data.UsersDAOImpl;
import tran.allrecipes.presentation.model.User;

/**
 * @author Todd
 * This class ensures that a user that has been disabled cannot make changes once they are logged in and have been disabled.
 */
@WebFilter(value={"/showShoppingList", "/showCreateRecipe", "/makeRecipe", "/editRecipe", "/processEditRecipe", "/addShoppingListIngredient", "/removeShoppingListIngredient", "/updateShoppingListIngredient", "/processRecipeList", "/addRecipeIngredient", 
"/removeRecipeIngredient", "/updateSingleIngredient", "/modifyRecipeIngredients", "/addRecipeDirection", "/removeRecipeDirection", "/updateRecipeDirectionContent", "/addRecipeReview", "/removeRecipeReview", "/updateRecipeReviewContent", "/deleteRecipe"})	
public class UserEnabledFilter implements Filter {

	/** the url for the home page. */
	private static final String HOME_PAGE_URL = "/";
	/** message parameter for redirect message. */
	private static final String MESSAGE_PARAM = "?message=";
	/** login page URL. */
	private static final String LOG_IN_PAGE_URL = "signin";
	/** The location of the data source file. */
	private static final String DATABASE_SOURCE_FILE = "database/Datasource.xml";
	/** Name of the users dao bean.*/
	private static final String USER_DAO_BEAN_NAME = "UsersDAO";
	/** not logged in user name. */
	private static final String NOT_LOGGED_IN_USER = "anonymousUser";
	
	/**
	 * see javax.servlet.Filter documentation.
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

	
	/**
	 * see javax.servlet.Filter documentation.
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String failedMessage = "no authentication object found.";
		if(authentication != null) {
			String userName = authentication.getName();
			
			if(userName != null) {
				if(userName.equals(NOT_LOGGED_IN_USER)) {
					failedMessage = "you must log in first.";
				}
				else {
					ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
					UsersDAOImpl userDAO = (UsersDAOImpl) appContext.getBean(USER_DAO_BEAN_NAME);
					
					User userTwo = userDAO.getUser(userName);
					
					userDAO = null;
					((ConfigurableApplicationContext) appContext).close();
					
					if(userTwo != null) {
						if(userTwo.isEnabled()) {	
							chain.doFilter(request, response);
							return ;
						}
						else {
							failedMessage = "your account has been disabled";
							//((HttpServletResponse) response).sendRedirect(HOME_PAGE_URL + MESSAGE_PARAM + failedMessage);
							RequestDispatcher rd = (RequestDispatcher) request.getRequestDispatcher(HOME_PAGE_URL + MESSAGE_PARAM + failedMessage);
							rd.forward(request, response);
							return ;
						}
					}
					else {
						failedMessage = "cannot retrieve your credentials, try to log in.";
					}
				}
			}
			else {
				failedMessage = "cannot verify you as a user, log in please.";
			}
		}
		RequestDispatcher rd = (RequestDispatcher) request.getRequestDispatcher("/" + LOG_IN_PAGE_URL + MESSAGE_PARAM + failedMessage);
		rd.forward(request, response);
	}

	/**
	 * see javax.servlet.Filter documentation.
	 */
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
