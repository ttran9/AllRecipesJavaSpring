package tran.allrecipes.service;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.math3.fraction.Fraction;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.stereotype.Service;

import tran.allrecipes.data.PantryListDAOImpl;
import tran.allrecipes.data.RecipeDAOImpl;
import tran.allrecipes.data.ShoppingListDAOImpl;
import tran.allrecipes.data.UsersDAOImpl;
import tran.allrecipes.presentation.model.Recipe;
import tran.allrecipes.presentation.model.User;
import tran.allrecipes.presentation.model.UserRole;

/**
 * @author Todd
 * A class that has methods which are used across multiple services.
 * This class authentication related methods which may have to be moved.
 */
@Service
public class UtilityServiceImpl {
	/** The location of the data source file. */
	private static final String DATABASE_SOURCE_FILE = "database/Datasource.xml";
	/** Name of the recipes DAO bean. */
	private static final String RECIPE_DAO_BEAN_NAME = "RecipesDAO";
	/** Name of the shopping list DAO bean. */
	private static final String SHOPPING_LIST_DAO_BEAN_NAME = "ShoppingListDAO";
	/** Name of the pantry list DAO bean. */
	private static final String PANTRY_LIST_DAO_BEAN_NAME = "PantryListDAO";
	/** Name of the users DAO bean.*/
	private static final String USER_DAO_BEAN_NAME = "UsersDAO";
	/** Arbitrary value to be used to check if a recipe belongs to a certain user. */
	private static final int RECIPE_TYPE = 200;
	/** Arbitrary value to be used to check if a shopping list belongs to a certain user. */
	private static final int SHOPPING_LIST_TYPE = 350;
	/** Arbitrary value for a pantry list. */
	private static final int PANTRY_LIST_TYPE = 450;
	/** number of properties from the database properties file. */
	private int NUMBER_OF_DB_PROPERTIES = 4;
	/** admin files prefix string. */
	private final String ADMIN_PREFIX_NAME = "admin";
	/** The admin role string. */
	private static final String ADMIN_ROLE = "ROLE_ADMIN";
	/** The user role string. */
	private static final String USER_ROLE = "ROLE_USER";
	/** database.properties prefix. */
	private final String DATABASE_PROPERTY_PREFIX = "jdbc.";
	/** the remember me cookie name. */
	private static final String REMEMBER_ME_TOKEN_NAME = "AR-Remember-Me-Cookie";
	
	public UtilityServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param principal An object holding the user's credentials.
	 * @param listType The type of list
	 * @param listName The name of the recipe or the name of the shopping list.
	 * @return True if the the user has created the recipe or owns the shopping list, false if not.
	 */
	public boolean isOwner(Principal principal, int listType, String listName) {
		boolean isUserOwner = false;
	    if(isUserAuthenticated(principal)) {
	    	String currentlyLoggedInUserName = principal.getName();
			ApplicationContext appContext = new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
			if(listType == RECIPE_TYPE) {
				RecipeDAOImpl recipeDAO = (RecipeDAOImpl) appContext.getBean(RECIPE_DAO_BEAN_NAME);
				Recipe recipe = recipeDAO.getRecipe(listName);
				if(recipe != null) {
					String recipeOwner = recipe.getUserOwner();
					if(recipeOwner != null) {
						if(recipeOwner.equals(currentlyLoggedInUserName)) {
							isUserOwner = true;
						}
					}
				}
				recipe = null;
				recipeDAO = null;
			}
			else if(listType == SHOPPING_LIST_TYPE) {
				ShoppingListDAOImpl shoppingListCheck = (ShoppingListDAOImpl) appContext.getBean(SHOPPING_LIST_DAO_BEAN_NAME);
				String shoppingListOwnerName = shoppingListCheck.getUserOwnerOfList(listName);
				isUserOwner = userListOwner(shoppingListOwnerName, currentlyLoggedInUserName);
				shoppingListOwnerName = null;
				shoppingListCheck = null;
			}
			else if(listType == PANTRY_LIST_TYPE) {
				PantryListDAOImpl pantryListCheck = (PantryListDAOImpl) appContext.getBean(PANTRY_LIST_DAO_BEAN_NAME);
				String pantryListOwnerName = pantryListCheck.getUserOwnerOfList(listName);
				isUserOwner = userListOwner(pantryListOwnerName, currentlyLoggedInUserName);
				pantryListOwnerName = null;
				pantryListCheck = null;
			}
			((ConfigurableApplicationContext)appContext).close();
		}
		return isUserOwner;
	}
	
	/**
	 * @param listOwnerName The name of the list being tested for, pantry list or shopping list.
	 * @param currentUserName The name of the currently logged in user.
	 * @return True if the currently logged in user owns the list, false otherwise..
	 */
	public boolean userListOwner(String listOwnerName, String currentUserName) {
		boolean isUserOwner = false;
		if(listOwnerName != null) {
			if(listOwnerName.equals(currentUserName)) {
				isUserOwner = true;
			}
		}
		return isUserOwner;
	}
	
	/**
	 * @param lastPostedTime The last time the user has added an ingredient or a review.
	 * @param currentTime The current time of this request.
	 * @return True if the last posted time and current time differ by 30 seconds or more.
	 */
	public boolean canUserModify(LocalDateTime lastPostedTime, LocalDateTime currentTime) {
		if(lastPostedTime != null) {
			Duration difference = Duration.between(lastPostedTime, currentTime);
	        return difference.getSeconds() >= 30;
		}
		else {
			return true;
		}
	}
	
	/**
	 * @param principal An object holding authentication information of the current user.
	 * @return True if the user has been authenticated, false if not.
	 */
	public boolean isUserAuthenticated(Principal principal) {
		boolean isAuthenticated = false;
		if(principal != null) {
			String userName = principal.getName();
			if(userName != null) {	
				/**
				 * check if the user is disabled. this is an important check as the user can be logged in
				 * and have been disabled, without this check and with the removal of a filter that
				 * disabled user could still modify reviews, recipes, his/her own shopping and pantry list.
				 */
				ApplicationContext appContext = new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
				UsersDAOImpl userDAO = (UsersDAOImpl) appContext.getBean(USER_DAO_BEAN_NAME);
				User user = userDAO.getUser(userName);
				if(user != null) {
					if(userDAO.getEnabled(userName)) {
						/**
						 * the extra check is added here because to ensure that the current user must have an actual role, ROLE_USER or ROLE_ADMIN
						 * instead of an anonymous role for example.
						 */
						Collection<? extends GrantedAuthority> userRoles = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
						for(GrantedAuthority userRole : userRoles) {
							if(userRole.getAuthority().equals(USER_ROLE) || userRole.getAuthority().equals(ADMIN_ROLE)) {
								isAuthenticated = true;
								break;
							}
						}
					}
				}
				userDAO = null;
				((ConfigurableApplicationContext) appContext).close();
			}
		}
		return isAuthenticated;
	}
	
	// helper method to verify the quantity fields.
	// returns a descriptive string to indicate what the error is.
	public String validateUserInput(int wholeNumber, int numerator, int denominator) {
		if(denominator <= 0)
			return "denominator must be at least 1 or greater.";
		else if(wholeNumber >= 0 && (numerator > denominator)) {
			return "please make sure the numerator is less than the denominator.";
		}
		else if(wholeNumber < 0) {
			return "the whole number must be at least zero.";
		}
		return ""; // no error.
	}
	
	/**
	 * @return A list of admin names.
	 * This method is useful for emailing any related bugs to admin email(s).
	 */
	public List<String> getAdminUserNames() {
		List<String> adminNames = new LinkedList<String>();
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = UtilityServiceImpl.class.getResourceAsStream("/properties/adminEmailList.properties");
			
			prop.load(input);
			boolean hasKey = true;
			int currentKeyNumber = 1;
			// get all the admin names, dependent on how the key names are formatted.
			while(hasKey) {
				String currentKey = prop.getProperty(ADMIN_PREFIX_NAME + currentKeyNumber);
				if(currentKey == null) {
					break;
				}
				adminNames.add(currentKey);
				currentKeyNumber++;
			}
		}
		catch(IOException e) {
			System.out.println("cannot retrieve admin names");
		}
		if(adminNames.size() > 0) return adminNames;
		else return null;
	}
	
	/**
	 * @return The contents in the database.properties file.
	 * This is necessary to populate the SimpleDataSource object with the necessary
	 * credentials to connect to the database for authentication purposes.
	 */
	public ArrayList<String> getDatabaseProperties() {
		ArrayList<String> databasePropertiesList = new ArrayList<String>();
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = UtilityServiceImpl.class.getResourceAsStream("/properties/database.properties");
			
			prop.load(input);
			// get all the admin names, dependent on how the key names are formatted.
			String driverClassName = prop.getProperty(DATABASE_PROPERTY_PREFIX + "driverClassName");
			if(driverClassName != null) databasePropertiesList.add(driverClassName);
			String databaseURL = prop.getProperty(DATABASE_PROPERTY_PREFIX + "url");
			if(databaseURL != null) databasePropertiesList.add(databaseURL);
			String databaseUserName = prop.getProperty(DATABASE_PROPERTY_PREFIX + "username");
			if(databaseUserName != null) databasePropertiesList.add(databaseUserName);
			String databasePassword = prop.getProperty(DATABASE_PROPERTY_PREFIX + "password");
			if(databasePassword != null) databasePropertiesList.add(databasePassword);
		}
		catch(IOException e) {
			System.out.println("cannot retrieve DB credential(s).");
		}
		if(databasePropertiesList.size() == NUMBER_OF_DB_PROPERTIES) 
			return databasePropertiesList;
		else 
			return null;
	}
	
	/**
	 * @param driverClassName The name of the type of DB driver (PostgreSQL, MySQL, etc).
	 * @param databaseURL The URL to the database
	 * @param userName The username to log in to the database.
	 * @param password The password to log in to the databse. 
	 * @return A datasource object to connect to the database.
	 */
	public DriverManagerDataSource getDataSource(String driverClassName, String databaseURL, String userName, String password) {
		DriverManagerDataSource dataSource = new DriverManagerDataSource(databaseURL, userName, password);
		dataSource.setDriverClassName(driverClassName);
		return dataSource;
	}
	
	/**
	 * @return A jdbcTokenRepository token with a data source set for access to the database to modify the persistent_logins table.
	 */
	public JdbcTokenRepositoryImpl getRepositoryTokenWithDataSource() {
		// TODO Auto-generated method stub
		JdbcTokenRepositoryImpl jdbcTokenRepositoryImpl = new JdbcTokenRepositoryImpl();
		ArrayList<String> databasePropertiesList = getDatabaseProperties();
		if(databasePropertiesList != null) {
			// if the list doesn't return null it can be assumed it has returned the four properties.
			DriverManagerDataSource dataSource = getDataSource(databasePropertiesList.get(0), databasePropertiesList.get(1), databasePropertiesList.get(2), databasePropertiesList.get(3));
			jdbcTokenRepositoryImpl.setDataSource(dataSource);
		}
		return jdbcTokenRepositoryImpl;
	}
	
	/**
	 * convenience method to remove the remember-me cookie from the browser.
	 * @param request The request object from the logout.
	 * @param response The response object with a max age set to 0 to disable persisent login.
	 */
	public void removeARCookieFromBrowser(HttpServletRequest request, HttpServletResponse response) {
		Cookie cookie = new Cookie(REMEMBER_ME_TOKEN_NAME, null);
		cookie.setMaxAge(0);
		cookie.setPath(getCookiePath(request));

		response.addCookie(cookie);
	}
	
	/**
	 * @param request The request object.
	 * @return The path for the cookie.
	 */
	private String getCookiePath(HttpServletRequest request) {
		String contextPath = request.getContextPath();
		return contextPath.length() > 0 ? contextPath : "/";
	}
	
	/**
	 * @param principal An object holding authentication information of the current user.
	 * @param posterOfReview The name of the person that wrote the review.
	 * @return True if the user can modify the review, false otherwise.
	 */
	public boolean doesUserOwnReview(Principal principal, String posterOfReview) {
		boolean canModify = false;
		if(isUserAuthenticated(principal)) {
			String userName = principal.getName();
			if(userName.equals(posterOfReview)) {
				canModify = true;
			}
		}
		return canModify;
	}
	
   /**
    * This is an admin tool, it will require an admin role to access and be used.
    * For now this only will disable registered users, not admin users too.
    * @param userName The name of the user.
    * @return A string to indicate if the user enabled column has been modified.
    */
    public void updateUserRole(String userName) {
    	ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
		UsersDAOImpl usersDAOImpl = (UsersDAOImpl) appContext.getBean(USER_DAO_BEAN_NAME);
    	String userNameCheck = usersDAOImpl.getUserName(userName);
    	if(userNameCheck != null) {
			int updateCodeCheck = usersDAOImpl.updateUserRole("DISABLED", "ROLE_USER", userName);
			if(updateCodeCheck != 1) {
				System.out.println("user: " + userName + "'s role was not updated.");
			}
		}
    	else {
    		System.out.println("update is not valid because user: " + userName + " doesn't exist.");
    	}
		usersDAOImpl = null;
		((ConfigurableApplicationContext)appContext).close();
    }
	    
    
	/**
	 * @param hours The number of hour(s) to prep/cook a recipe.
	 * @param minutes The number of minute(s) to prep/cook a recipe.
	 * @param seconds The number of second(s) to prep/cook a recipe.
	 * @return An integer value in seconds of how long it takes to cook or prep something, -1 indicates this was not filled out.
	 */
	public int convertEnteredTime(String hours, String minutes, String seconds) {
		int convertedSeconds = -1;
		int hoursParsed = -1;
		int minutesParsed = -1;
		int secondsParsed = -1;
		
		if(hours != null && minutes != null && seconds != null) {
			convertedSeconds = 0;
			hoursParsed = Integer.parseInt(hours);
			minutesParsed = Integer.parseInt(minutes);
			secondsParsed = Integer.parseInt(seconds);
		}
		
		if(hoursParsed >= 0 && hoursParsed <= 11) {
			convertedSeconds += hoursParsed * 3600;
		}
		if(minutesParsed >= 0 && minutesParsed <= 55) {
			convertedSeconds += minutesParsed * 60;
		}
		if(secondsParsed >= 0 && secondsParsed <= 60) {
			convertedSeconds += secondsParsed;
		}
		return convertedSeconds;
	}
	
	/**
	 * @param ingredientThresholdWholeNumber The threshold value's whole number.
	 * @param ingredientThresholdNumerator The threshold value's numerator.
	 * @param ingredientThresholdDenominator The threshold value's denominator.
	 * @param newWholeNumber The whole number quantity of the ingredient at the time of an update.
	 * @param newNumerator The numerator quantity of the ingredient at the time of an update.
	 * @param newDenominator The denominator quantity of the ingredient at the time of an update.
	 * @return True if the threshold value has been hit or exceeded.
	 */
	public boolean checkIfThresholdHit(int ingredientThresholdWholeNumber, int ingredientThresholdNumerator, int ingredientThresholdDenominator, int newWholeNumber, int newNumerator, int newDenominator) {
		Fraction storedThresholdValue = new Fraction(((ingredientThresholdWholeNumber * ingredientThresholdDenominator) + ingredientThresholdNumerator), ingredientThresholdDenominator);
		Fraction newIngredientQuantity = new Fraction(((newWholeNumber * newDenominator) + newNumerator), newDenominator);
		if(storedThresholdValue.compareTo(newIngredientQuantity) >= 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * @param newWholeNumber The whole number entered by the user.
	 * @param newNumerator The numerator entered by the user.
	 * @param newDenominator The denominator entered by the user.
	 * @param currentNumber The whole number current amount of the ingredient.
	 * @param currentNumerator The numerator amount of the ingredient.
	 * @param currentDenominator The denominator amount of the ingredient.
	 * @return 1 implies that the subtraction is not possible, 0 implies that the user is removing all of the ingredient but will also have it moved to the shopping list.
	 * -1 implies that the amount to subtract was less than the current quantity.
	 */
	public int checkIfSubtractionIsPossible(int newWholeNumber, int newNumerator, int newDenominator, int currentNumber, int currentNumerator, int currentDenominator) {
		Fraction newFractionAmount = new Fraction((newDenominator * newWholeNumber) + newNumerator, newDenominator);
		Fraction currentFractionAmount = new Fraction((currentDenominator * currentNumber) + currentNumerator, currentDenominator);
		return newFractionAmount.compareTo(currentFractionAmount);
	}
	
	/** 
	 * @param currentIngredientWholeNumber The whole number current amount of the ingredient.
	 * @param currentIngredientNumerator The numerator amount of the ingredient.
	 * @param currentIngredientDenominator The denominator amount of the ingredient.
	 * @param newIngredientWholeNumber The whole number entered by the user.
	 * @param newIngredientNumerator The numerator entered by the user.
	 * @param newIngredientDenominator The denominator entered by the user.
	 * @return A fraction with the newly computed numerator and denominator for updating the ingredient amount.
	 */
	public Fraction getNewQuantityAfterSubtraction(int currentIngredientWholeNumber, int currentIngredientNumerator, int currentIngredientDenominator, int newIngredientWholeNumber, int newIngredientNumerator, int newIngredientDenominator) {
		Fraction currentFractionAmount = new Fraction((currentIngredientDenominator * currentIngredientWholeNumber) + currentIngredientNumerator, currentIngredientDenominator);
		Fraction newFractionAmount = new Fraction((newIngredientDenominator * newIngredientWholeNumber) + newIngredientNumerator, newIngredientDenominator);
		return currentFractionAmount.subtract(newFractionAmount);
	}
	
	/**
	 * @param servingsValue The number of servings for the recipe
	 * @return True if the servings valuee is in a certain range.
	 */
	public boolean validateServings(String servingsValue) {
		int parsedServingsValue = Integer.parseInt(servingsValue);
		return (parsedServingsValue >=1 && parsedServingsValue <= 20);
	}
	
	
	/**
	 * @param recipeName The name of the entered recipe.
	 * @return Returns true if the recipe name meets conditions.
	 */
	public boolean validateRecipeName(String recipeName) {
		String recipeNameRegex = "^[a-zA-Z0-9 ']{6,60}$";
		return recipeName.matches(recipeNameRegex);
	}
	
	/**
	 * @param recipeDescription The description of the entered recipe.
	 * @return True matches a regex that allows english letters, numbers, single quote, regular quote and must be 4-300 characters in length.
	 */
	public boolean validateRecipeDescription(String recipeDescription) {
		return recipeDescription.length() >= 4 && recipeDescription.length() <= 300;
		/*
		String descriptionRegex = "^[a-zA-Z0-9\'\". ]{4,300}$";
		return recipeDescription.matches(descriptionRegex);
		*/
	}
	
	/**
	 * @param recipeType The type of the recipe.
	 * @return Returns true if the recipe type meets conditions.
	 */
	public boolean validateRecipeType(String recipeType) {
		String recipeTypeRegex = "^[a-zA-Z ]{4,20}$";
		return recipeType.matches(recipeTypeRegex);
	}

	/**
	 * @param principal An object holding authentication information of the current user.
	 * @param userName The name of the user to disable.
	 * @return An admin only page indicating the status of the user's enabled permission.
	 */
	public String setUserDisabled(Principal principal, String userName) {
		// will be implemented after database transactions are implemented and tested.
		if(principal != null) {
			
		}
		return null;
	}
	
	/**
	 * @param principal An object holding authentication information of the current user.
	 * @param userName The name of the user to be checked.
	 * @return True if the user is an admin user, false if not.
	 */
	public boolean isAdminUser(Principal principal) {
		boolean isAdmin = false;
		if(principal != null) {
			String userName = principal.getName();
			ApplicationContext appContext = new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
			User user = (User) new UserDetailsServiceImpl().loadUserByUsername(userName);
			if(user != null) {
				List<UserRole> authorities = user.getAuthorities();
				if(authorities != null) {
					for(UserRole userRole : authorities) {
						if(userRole.getAuthority().equals(ADMIN_ROLE)) {
							isAdmin = true;
							break;
						}
					}
				}
			}
			((ConfigurableApplicationContext) appContext).close();
		}
		return isAdmin;
	}
		
}
