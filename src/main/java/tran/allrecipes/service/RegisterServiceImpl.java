package tran.allrecipes.service;

import java.security.Principal;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tran.allrecipes.data.PantryListDAOImpl;
import tran.allrecipes.data.ShoppingListDAOImpl;
import tran.allrecipes.data.UsersDAOImpl;
import tran.allrecipes.presentation.model.User;
import tran.allrecipes.presentation.model.UserRole;

/**
 * @author Todd
 * A service to handle logical operations to register a user and perform necessary checks for validation.
 */
@Service
public class RegisterServiceImpl {
	/** The location of the data source file. */
	private static final String DATABASE_SOURCE_FILE = "database/Datasource.xml";
	/** Name of the users DAO bean. */
	private static final String USER_DAO_BEAN_NAME = "UsersDAO";
	/** Name of the shopping list DAO bean. */
	private static final String SHOPPING_LIST_DAO_BEAN_NAME = "ShoppingListDAO";
	/** Name of the pantry list DAO bean. */
	private static final String PANTRY_LIST_DAO_BEAN_NAME = "PantryListDAO";
	/** String used to redirect to the registration page. */
	private static final String REDIRECT_TO_REGISTRATION_PAGE = "redirect:/register";
	/** redirects to the login page. */
	private static final String REDIRECT_TO_LOGIN = "redirect:/signin";
	/** show all the recipes. */
	private static final String REDIRECT_ALL_RECIPES = "redirect:/";
	/** message parameter. */
	private static final String MESSAGE_PARAMETER = "message";
	/** error parameter. */
	private static final String ERROR_PARAMETER = "error";
	/** Notification for logged in user. */
	private static final String USER_ALREADY_LOGGED_IN = "You cannot be logged in when trying to register another account, log out first.";;
	/** The user object. */
	private static final String USER_REGISTRATION = "registerUser";
	/** The title of the page attribute. */
	private static final String PAGE_TITLE_ATTRIBUTE = "title";
    /** Page title. */
	private static final String PAGE_TITLE = "Login Page!";
	/** The attribute to have a second hyper link on the navigation bar. */
	private static final String SECOND_NAVBAR_LINK = "secondNavbarLink";
	/** The attribute to display text of the second hyper link on the navigation bar. */
	private static final String SECOND_NAVBAR_LINK_TEXT_ATTRIBUTE = "secondNavbarLinkText";
	/** The URL of the second hyper link on the navigation bar. */
	private static final String SECOND_NAVBAR_LINK_URL = "signin";
	/** The text of the second hyper link on the navigation bar. */
	private static final String SECOND_NAVBAR_LINK_TEXT = "LogIn!";
	
	/**
	 * @param principal An object holding authentication information of the current user.
	 * @param model An object holding UI information.
	 * @param error A message informing the user if relevant.
	 * @param redirectAttrs An object holding the attributes for when a redirection is done.
	 * @return The registration page if the user is not logged in and redirects the user to the recipes page if user is already logged in.
	 */
	public String showRegistration(Principal principal, ModelMap model, String error, RedirectAttributes redirectAttrs) {
		if(principal != null) {
			redirectAttrs.addAttribute(MESSAGE_PARAMETER, USER_ALREADY_LOGGED_IN);
			return REDIRECT_ALL_RECIPES;
		}
		else {
			if(error != null) model.addAttribute("error", error);
    		model.addAttribute(SECOND_NAVBAR_LINK, SECOND_NAVBAR_LINK_URL);
    		model.addAttribute(SECOND_NAVBAR_LINK_TEXT_ATTRIBUTE, SECOND_NAVBAR_LINK_TEXT);
    		model.addAttribute(PAGE_TITLE_ATTRIBUTE, PAGE_TITLE);
    		model.addAttribute(USER_REGISTRATION, new User());
			return "register";
		}
	}
	
	/**
	 * @param principal An object holding authentication information of the current user.
	 * @param userNameContent The user name being checked.
	 * @return Returns no message if the user name can be made, if the user name has the wrong format or has been created already returns a message.
	 */
	public ResponseEntity<String> checkRegistrationUserName(Principal principal, String userNameContent) {
		if(principal != null) {
			String userAlreadyLoggedIn = "You cannot be logged in when trying to register another account, log out first.";
			HttpHeaders headers = new HttpHeaders();
			headers.add("Location", "/" +  "?message=" + userAlreadyLoggedIn);;
			return new ResponseEntity<String>(headers, HttpStatus.FOUND);
		}
		else {
			ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
			UsersDAOImpl usersDAO = (UsersDAOImpl) appContext.getBean(USER_DAO_BEAN_NAME);
			ResponseEntity<String> returnCode = null;
			User user = usersDAO.getUser(userNameContent);
			if(user != null) {
				// user exists
				returnCode = new ResponseEntity<String>("User name already exists.", HttpStatus.INTERNAL_SERVER_ERROR);
			}
			else {
				// check it against some regex.
				UserServiceImpl userService = new UserServiceImpl(userNameContent);
				if(userService.checkUserName(userNameContent)) {
					returnCode = new ResponseEntity<String>(HttpStatus.OK);
				}
				else {
					returnCode = new ResponseEntity<String>(userService.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
				userService = null;
			}			
			usersDAO = null;
			((ConfigurableApplicationContext)appContext).close();
			return returnCode;
		}
	}
	
	/**
	 * @param principal AN object holding authentication information of the user.
	 * @param userName The name of the user being registered.
	 * @param password The password to be encrypted.
	 * @param verifyPassword A field to verify the password.
	 * @param redirectAttrs An object holding the attributes for when a redirection is done.
	 * @return Displays the login page if the registration is successful, and if not displays the registration page with a message.
	 */
	public String processRegistration(Principal principal, String userName, String password, String verifyPassword, RedirectAttributes redirectAttrs) {
		if(principal != null) {
			redirectAttrs.addAttribute(MESSAGE_PARAMETER, USER_ALREADY_LOGGED_IN);
			return REDIRECT_ALL_RECIPES;
		}
		else {
			String errorMessage = "missing parameters";
			if(userName!= null && password != null && verifyPassword != null) {
				UserServiceImpl userService = new UserServiceImpl(userName, password, verifyPassword);
				ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
				UsersDAOImpl usersDAO = (UsersDAOImpl) appContext.getBean(USER_DAO_BEAN_NAME);
				
				if(usersDAO.getUserName(userName) == null) {
					if(userService.validate()) {
						String encryptedPassword = userService.encryptPassword(password);
						int createUserCode = usersDAO.createUser(userName, encryptedPassword, true);
						if(createUserCode == 1) {
							ShoppingListDAOImpl shoppingListDAO = (ShoppingListDAOImpl) appContext.getBean(SHOPPING_LIST_DAO_BEAN_NAME);
							PantryListDAOImpl pantryListDAO = (PantryListDAOImpl) appContext.getBean(PANTRY_LIST_DAO_BEAN_NAME);
							String shoppingListName = userName + "'s " + "shopping list";
							int createShoppingList = shoppingListDAO.addList(shoppingListName, userName);
							String pantryListName = userName + "'s " + "pantry list";
							int createPantryList = pantryListDAO.addList(pantryListName, userName);
							int createUserRole = usersDAO.insertUserRole("ROLE_USER", userName);
							// at this point a different object will be used to print the notification to the user.
							StringBuilder notificationMessage = new StringBuilder();
							String lineSeparator = System.lineSeparator();
							notificationMessage.append("user successfully created!" + lineSeparator + "if there are any other messages below contact an admin." + lineSeparator);
							
							if(createShoppingList != 1) {
								notificationMessage.append("no shopping list was created.");
							}
							if(createUserRole != 1) {
								notificationMessage.append("no role could be created.");
							}
							if(createPantryList != 1) {
								notificationMessage.append("no pantry list was created.");
							}
							
							sendVerificationEmail(usersDAO, shoppingListDAO, pantryListDAO, userName);
							shoppingListDAO = null;
							usersDAO = null;
							userService = null;
							((ConfigurableApplicationContext)appContext).close();
							redirectAttrs.addAttribute(MESSAGE_PARAMETER, notificationMessage.toString());
							return REDIRECT_TO_LOGIN;
						}
						else {
							errorMessage = "user could not be created.";
						}
					}
					else {
						errorMessage = userService.getMessage();
					}
				} 
				else {
					errorMessage = "user name already exists.";
				}
				usersDAO = null;
				userService = null;
				((ConfigurableApplicationContext)appContext).close();
			}
			redirectAttrs.addAttribute(ERROR_PARAMETER, errorMessage);
			return REDIRECT_TO_REGISTRATION_PAGE;
		}
	}
	
	/**
	 * @param usersDAO An object to allow access to the Users and UsersRoles data tables.
	 * @param shoppingListDAO An object to allow access to the ShoppingList data table.
	 * @param pantryListDAO An object to allow access to the PantryList data table.
	 * @param registeredUserName The user name that the user has registered as.
	 */
	public void sendVerificationEmail(UsersDAOImpl usersDAO, ShoppingListDAOImpl shoppingListDAO, PantryListDAOImpl pantrylistDAO, String registeredUserName) {
		User userDetails = usersDAO.getUser(registeredUserName);
		String shoppingListName = shoppingListDAO.getListName(registeredUserName);
		String pantryListName = pantrylistDAO.getListName(registeredUserName);
		List<UserRole> userRoles = usersDAO.getUserRoles(registeredUserName);
		GmailService emailService = new GmailService();
		UtilityServiceImpl utilityService = new UtilityServiceImpl();
		String emailSubject = "New User Registered!";
		if(userDetails != null && shoppingListName != null && pantryListName != null && userRoles != null) {
			StringBuilder userRolesString = new StringBuilder();
			for(int i = 0; i < userRoles.size(); i++) {
				if(i == userRoles.size() - 1) {
					userRolesString.append(userRoles.get(i).getAuthority());
				}
				else {
					userRolesString.append(userRoles.get(i).getAuthority() + ", ");
				}
			}
			String bodyText = "User: " + registeredUserName + " has registered. The information of the user is below. \n" + userDetails.toString() + "\n" + "Role(s): " + userRolesString;
			emailService.sendEmail(bodyText, utilityService.getAdminUserNames(), emailSubject);
		}
		else {
			String bodyText = "User: " + registeredUserName + " has registered. " + "Some user detail could not be retrieved, check the Users table, the shopping list or pantry list table with the user name, or user roles table.";
			emailService.sendEmail(bodyText, utilityService.getAdminUserNames(), emailSubject);
		}
		utilityService = null;
		emailService = null;
	}

}
