package tran.allrecipes.service;

import java.util.LinkedList;
import java.util.List;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import tran.allrecipes.data.UsersDAOImpl;
import tran.allrecipes.presentation.model.UserRole;

/**
 * @author Todd
 * A class to assist with spring security's authentication process.
 */
@Service
public class AuthenticationService implements AuthenticationProvider {
	/** Message indicating the user has entered an incorrect user name and password combination of some sort. */
	private static final String INCORRECT_CREDENTIALS_STRING = "Incorrect credientials";
	/** message indicating the user has had the account disabled. */
	private static final String ACCOUNT_DISABLED = "Account has been disabled";
	/** error indicating that the user has no role assigned. */
	private static final String CANNOT_GET_ROLES_ERROR = "Error retrieving role, an email has been sent to an admin.";
	/** a message to be sent to the admin (me) in this case regarding that the user needs a role change to ROLE_USER. */
	private static final String CHECK_USER_ROLE_SUBJECT = "User Role Change Needed";
	/** for now this will be the email that receives information from users. */
	private static final String EMAIL_TO_SEND_TO = "toddblog1@gmail.com";
	/** notification message for the admin. */
	private static final String CHECK_MESSAGE = "Please check the role for user: ";
	/** The location of the data source file. */
	private static final String DATABASE_SOURCE_FILE = "database/Datasource.xml";
	/** Name of the users DAO bean. */
	private static final String USER_DAO_BEAN_NAME = "UsersDAO";
	/** the user role. */
	private static final String USER_ROLE = "ROLE_USER";
	
	/**
	 * refer to org.springframework.security.authentication.ProviderManager for how the authenticationexception is caught
	 * and used to redirect the user back to the login page with an error message.
	*/
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		// TODO Auto-generated method stub
		// grab the username and password from the login form.
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
		UsersDAOImpl usersDAO = (UsersDAOImpl)appContext.getBean(USER_DAO_BEAN_NAME);
    	
		String username = authentication.getName();
    	String password = (String) authentication.getCredentials();
    	String hashedPassword = null;
    	UserServiceImpl userService = null;
    	UserDetails userdetails = null;
    	
    	userdetails = new UserDetailsServiceImpl().loadUserByUsername(username);
    	
    	if(userdetails == null) {
    		// user doesn't exist.
    		usersDAO = null;
        	((ConfigurableApplicationContext)appContext).close();
    		throw new BadCredentialsException(INCORRECT_CREDENTIALS_STRING);
    	}
    	
    	if(!userdetails.isEnabled()) {
    		usersDAO = null;
        	((ConfigurableApplicationContext)appContext).close();
    		throw new BadCredentialsException(ACCOUNT_DISABLED);
    	}
    	
    	userService = new UserServiceImpl(username, password);
    	
    	if(!userService.validateLogin()) {
    		// user name regex or password violated.
    		usersDAO = null;
        	((ConfigurableApplicationContext)appContext).close();
    		throw new BadCredentialsException(INCORRECT_CREDENTIALS_STRING);
    	}
    	
    	hashedPassword = usersDAO.getUserPassword(username);
    	if(!(BCrypt.checkpw(password, hashedPassword))) {
    		// bad password.
    		usersDAO = null;
        	((ConfigurableApplicationContext)appContext).close();
    		throw new BadCredentialsException(INCORRECT_CREDENTIALS_STRING);
    	}
    	
    	List<UserRole> userRoles = usersDAO.getUserRoles(username);
    	
    	boolean doesUserHaveRole = false;
    	
    	for(UserRole userRole : userRoles) {
    		if(userRole.getAuthority().equals(USER_ROLE)) {
    			doesUserHaveRole = true;
    			break;
    		}
    	}
    	
		usersDAO = null;
    	((ConfigurableApplicationContext)appContext).close();
    	
    	if(doesUserHaveRole) {
    		return new UsernamePasswordAuthenticationToken(username, password, userRoles);
    	}
    	else {
    		// must test this snippet of code.
    		List<String> listOfUsersToSendTo = new LinkedList<String>();
    		listOfUsersToSendTo.add(EMAIL_TO_SEND_TO);
    		GmailService gmailService = new GmailService();
			gmailService.sendEmail(CHECK_MESSAGE + username, listOfUsersToSendTo, CHECK_USER_ROLE_SUBJECT);
			throw new BadCredentialsException(CANNOT_GET_ROLES_ERROR);
    	}
	}

	/**
	 * see org.springframework.security.authentication.AuthenticationProvider documentation.
	 */
	public boolean supports(Class<?> authentication) {
		// TODO Auto-generated method stub
		return true;
	}
	
}
