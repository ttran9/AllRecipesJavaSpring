package tran.allrecipes.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import tran.allrecipes.data.UsersDAOImpl;
import tran.allrecipes.presentation.model.User;
import tran.allrecipes.presentation.model.UserRole;

/**
 * @author Todd
 * A class to get detailed user information to help with creating authentication token(s).
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	/** The location of the data source file. */
	private static final String DATABASE_SOURCE_FILE = "database/Datasource.xml";
	/** Name of the users DAO bean. */
	private static final String USER_DAO_BEAN_NAME = "UsersDAO";
	/** The role of a user not logged in. */
	private static final String ANONYMOUS_USER_ROLE = "ROLE_ANONYMOUS";
	/** The name of a user not logged in. */
	private static final String ANONYMOUS_USER_NAME = "anonymousUser";

	public UserDetailsServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @userName The name of the user to get information of.
	 * @return A detailed object with user relevant information.
	 */
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
		UsersDAOImpl usersDAO = (UsersDAOImpl)appContext.getBean(USER_DAO_BEAN_NAME);
		
		User user = usersDAO.getUser(username);
		
		if(user != null) {
			user.setAccountNonExpired(true);
			user.setAccountNonLocked(user.isEnabled());
			user.setCredentialsNonExpired(true);
			List<UserRole> authorities = usersDAO.getUserRoles(username);
			user.setAuthorities(authorities);
		}
		usersDAO = null;
    	((ConfigurableApplicationContext)appContext).close();
		return user;
	}
	
	/**
	 * This is not really practical and only used in the JUnit tests.
	 * @return A user details object with limited information
	 */
	public UserDetails getAnonymousUser() {
		User user = new User();
		List<UserRole> authorities = new ArrayList<UserRole>();
		UserRole anonymousUserRole = new UserRole();
		anonymousUserRole.setAuthority(ANONYMOUS_USER_ROLE);
	    authorities.add(anonymousUserRole);
	    user.setAuthorities(authorities);
	    user.setUserName(ANONYMOUS_USER_NAME);
	    return user;
	}
}
