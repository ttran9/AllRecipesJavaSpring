package tran.allrecipes.presentation.model;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author Todd
 * A class to store a user's role(s) to be used with an AuthenticationProvider.
 */
public class UserRole implements GrantedAuthority {
	/** An ID to assist with RMI for deserializing purposes between JVMs. */
	private static final long serialVersionUID = 1901626862487020693L;
	private String authority; // the role.
	
	public void setAuthority(String authority) {
		this.authority = authority;
	}
	
	public String getAuthority() {
		// TODO Auto-generated method stub
		return authority;
	}
}
