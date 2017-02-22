package tran.allrecipes.data;

import java.time.LocalDateTime;
import java.util.List;

import tran.allrecipes.presentation.model.User;
import tran.allrecipes.presentation.model.UserRole;

/**
 * @author Todd
 * An interface to declare methods to get contents from the Users table.
 */
public interface UsersDAO {
	/**
	 * @param userName The name of the user to retrieve.
	 * @return A populated user object, or null if the user object cannot be retrieved.
	 */
	public User getUser(String userName);

	/**
	 * @param userName The name of the user to retrieve the password for.
	 * @return An encrypted password or null if the password cannot be retrieved..
	 */
	public String getUserPassword(String userName);
	
	/**
	 * @param userName The name of the user to retrieve account permission(s).
	 * @return True if the account is enabled, false if teh account is disabled.
	 */
	public boolean getEnabled(String userName);
	
	/**
	 * @param userName The name of the user that last posted a recipe.
	 * @return An object holding a time object or null if the user never posted a recipe.
	 */
	public LocalDateTime getLastPostedRecipeTime(String userName);
	
	/**
	 * @param userName The name of the user that last posted a review.
	 * @return An object holding a time object or null if the user never posted a review.
	 */
	public LocalDateTime getLastPostedReviewTime(String userName);
	
	/**
	 * @param userName The user name of an account.
	 * @return A string indicating if the user name exists.
	 */
	public String getUserName(String userName);
	
	/**
	 * @param userName The name of the user to retrieve the id of.
	 * @return An integer value of a specified user name or null if it cannot be found.
	 */
	public Integer getUserId(String userName);
	
	/**
	 * @param userName The user name of the account.
	 * @param userPassword The encrypted password of the account.
	 * @param enabled A flag indicating the status of the user account.
	 * @return 1 if the user could be created, -1 if there was SQL related error(s).
	 */
	public int createUser(String userName, String userPassword, boolean enabled);
	
	/**
	 * @param newPassword The new encrypted password.
	 * @param userName The name of the user to be updated.
	 * @param userId The id of the user to be updated.
	 * @return 1 if the password was updated, -1 if there was SQL related error(s).
	 */
	public int updateUserPassword(String newPassword, String userName, int userId);
	
	/**
	 * @param enabled A flag to indicate if the account is to be enabled or disabled.
	 * @param userName The name of the user to be updated.
	 * @return 1 if the enabled status updated, -1 if there was SQL related error(s).
	 */
	public int updateUserEnabled(boolean enabled, String userName);
	
	/**
	 * @param currentTime The current time at which the user posted the recipe.
	 * @param userName The name of the user to be updated.
	 * @return 1 if the last posted recipe time was updated, -1 if there was SQL related error(s).
	 */
	public int updateUserLastPostedRecipeTime(LocalDateTime currentTime, String userName);
	
	/**
	 * @param currentTime The current time at which the user posted a review.
	 * @param userName The name of the user to be updated.
	 * @return 1 if the last posted review time was updated, -1 if there was SQL related error(s).
	 */
	public int updateUserLastPostedReviewTime(LocalDateTime currentTime, String userName);
	
	// below interacts with the UsersRoles table.
	
	/**
	 * @param userName The name of the user to retrieve a user role.
	 * @return A list of user role(s), or null if there was SQL related error(s).
	 */
	public List<UserRole> getUserRoles(String userName);
	
	/**
	 * @param roleName The role of a user.
	 * @param userName The name of the user.
	 * @return 1 if the role was inserted, -1 if there was SQL related error(s).
	 */
	public int insertUserRole(String roleName, String userName);
	
	/**
	 * @param userRoleId The ID of the user role to remove.
	 * @param userName The name of the user.
	 * @return 1 if the user role was deleted, -1 if there was SQL related error(s).
	 */
	public int deleteUserRole(int userRoleId, String userName);
	
	/**
	 * @param roleName The updated user role.
	 * @param oldRoleName The former user role.
	 * @param userName The name of the user.
	 * @return 1 if the user role was updated, -1 if there was SQL related error(s).
	 */
	public int updateUserRole(String roleName, String oldRoleName, String userName);
}
