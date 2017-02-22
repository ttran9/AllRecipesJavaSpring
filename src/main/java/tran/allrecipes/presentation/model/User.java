package tran.allrecipes.presentation.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author Todd
 * This class represents what a user can have such as posted recipes, shopping list(s), 
 * and review(s) about recipe(s).
 */
public class User implements UserDetails {
	/** An ID to assist with RMI for deserializing purposes between JVMs. */
	private static final long serialVersionUID = -7424110789515969995L;
	/** The user name. */
	private String userName;
	/** The password of the user. */
	private String password;
	/** A field used to assist with password validation.*/
	private String validatePassword;
	/** A field to track when the user last posted a recipe. */
	private LocalDateTime lastPostedRecipeTime;
	/** A field to track when the user last posted a review. */
	private LocalDateTime lastPostedReviewTime;
	/**
	 *  A data member field to track the userId.
	 *  Not currently used in earlier releases.
	 */
	private int userId;
	/* Spring Security related fields*/
	/** A list to hold the role(s) of a user. */
    private List<UserRole> authorities;
    /* The next three fields are not currently being used. Will make an update to integrate these eventually. */
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    /** A data member to track of the user is able to post recipe(s) or review(s). */
    private boolean enabled;
    /** The email address of the user. */
    private String userEmail;
    
	public User() {
		// TODO Auto-generated constructor stub
    	accountNonExpired = true;
    	accountNonLocked = true;
    	credentialsNonExpired = true;
	}
	
	public String getUsername() {
		// TODO Auto-generated method stub
		// need this method definition to implement the UserDetails interface.
		return userName;
	}
	
	public String getUserName() {
		// TODO Auto-generated method stub
		// need this method for form binding.
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	

	public String getValidatePassword() {
		return validatePassword;
	}

	public void setValidatePassword(String validatePassword) {
		this.validatePassword = validatePassword;
	}
	
	public LocalDateTime getLastPostedRecipeTime() {
		return lastPostedRecipeTime;
	}
	
	public void setLastPostedRecipeTime(LocalDateTime lastPostedRecipeTime) {
		this.lastPostedRecipeTime = lastPostedRecipeTime;
	}

	public LocalDateTime getLastPostedReviewTime() {
		return lastPostedReviewTime;
	}

	public void setLastPostedReviewTime(LocalDateTime lastPostedReviewTime) {
		this.lastPostedReviewTime = lastPostedReviewTime;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public List<UserRole> getAuthorities() {
		// TODO Auto-generated method stub
		return authorities;
	}
	
    public void setAuthorities(List<UserRole> authorities) {
		this.authorities = authorities;
	}

	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return accountNonExpired;
	}
	
	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return accountNonLocked;
	}
	
	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return credentialsNonExpired;
	}
	
	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	
	/**
	 * @return A string to put into a message to email for informational purposes about a user registration.
	 */
	@Override
	public String toString() {
		return "user name: " + this.userName + "\n" + "enabled permission: " + this.enabled;
	}

}
