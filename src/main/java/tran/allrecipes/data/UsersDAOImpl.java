package tran.allrecipes.data;

import java.time.LocalDateTime;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import tran.allrecipes.presentation.model.User;
import tran.allrecipes.presentation.model.UserRole;

@Repository
public class UsersDAOImpl implements UsersDAO {
	
	private static final String USERS_TABLE = "Users";
	private static final String USERS_NAME_COLUMN = "userName";
	private static final String USERS_PASSWORD_COLUMN = "userPassword";
	private static final String ENABLED_COLUMN = "enabled";
	private static final String LAST_POSTED_RECIPE_TIME_COLUMN = "lastPostedRecipeTime";
	private static final String LAST_POSTED_REVIEW_TIME_COLUMN = "lastPostedReviewTime";
	private static final String USER_EMAIL_COLUMN = "userEmail";
	private static final String USER_ID_COLUMN = "userId";
	
	private static final String USERS_ROLES_TABLE = "UsersRoles";
	private static final String USERS_ROLE_NAME_COLUMN = "roleName";
	private static final String USERS_ROLE_ID_COLUMN = "userRoleId";
	
	private static final String GET_USER_QUERY = "SELECT * FROM " + USERS_TABLE + " WHERE " + USERS_NAME_COLUMN + " = ?";
	private static final String GET_USER_PASSWORD = "SELECT " + USERS_PASSWORD_COLUMN + " FROM " + USERS_TABLE + " WHERE " + USERS_NAME_COLUMN + " = ?";
	private static final String GET_USER_ENABLED = "SELECT " + ENABLED_COLUMN + " FROM " + USERS_TABLE + " WHERE " + USERS_NAME_COLUMN + " = ?";
	private static final String GET_USER_LAST_POSTED_RECIPE_TIME = "SELECT " + LAST_POSTED_RECIPE_TIME_COLUMN + " FROM " + USERS_TABLE + " WHERE " + USERS_NAME_COLUMN + " = ?";
	private static final String GET_USER_LAST_POSTED_REVIEW_TIME = "SELECT " + LAST_POSTED_REVIEW_TIME_COLUMN + " FROM " + USERS_TABLE + " WHERE " + USERS_NAME_COLUMN + " = ?";
	private static final String GET_USER_NAME = "SELECT " + USERS_NAME_COLUMN + " FROM " + USERS_TABLE + " WHERE " + USERS_NAME_COLUMN + " = ?";
	private static final String GET_USER_ID = "SELECT " + USER_ID_COLUMN + " FROM " + USERS_TABLE + " WHERE " + USERS_NAME_COLUMN + " = ?";
	private static final String INSERT_USER = "INSERT INTO " + USERS_TABLE + "(" + USERS_NAME_COLUMN + ", " + USERS_PASSWORD_COLUMN + ", " + ENABLED_COLUMN + ") VALUES(?, ?, ?)"; 
	private static final String DELETE_USER = "DELETE FROM " + USERS_TABLE + " WHERE " + USERS_NAME_COLUMN + " = ?";
	private static final String UPDATE_USER_PASSWORD = "UPDATE " + USERS_TABLE + " SET " + USERS_PASSWORD_COLUMN + " = ?" + " WHERE " + USERS_NAME_COLUMN + " = ? AND " + USER_ID_COLUMN + " = ?";
	private static final String UPDATE_USER_ENABLED = "UPDATE " + USERS_TABLE + " SET " + ENABLED_COLUMN + " = ? WHERE " + USERS_NAME_COLUMN + " = ? AND " + USER_ID_COLUMN + " = ?";
	private static final String UPDATE_USER_LAST_POSTED_RECIPE_TIME = "UPDATE " + USERS_TABLE + " SET " + LAST_POSTED_RECIPE_TIME_COLUMN + " = ? WHERE " + USERS_NAME_COLUMN + " = ?";
	private static final String UPDATE_USER_LAST_POSTED_REVIEW_TIME = "UPDATE " + USERS_TABLE + " SET " + LAST_POSTED_REVIEW_TIME_COLUMN + " = ? WHERE " + USERS_NAME_COLUMN + " = ?";
	
	private static final String GET_USER_ROLE = "SELECT " + USERS_ROLE_NAME_COLUMN + " FROM " + USERS_ROLES_TABLE + " WHERE " + USERS_NAME_COLUMN + " = ?";
	private static final String INSERT_USER_ROLE = "INSERT INTO " + USERS_ROLES_TABLE + "(" + USERS_ROLE_NAME_COLUMN + ", " + USERS_NAME_COLUMN + ") VALUES(?, ?)";  
	private static final String DELETE_USER_ROLE = "DELETE FROM " + USERS_ROLES_TABLE + "WHERE " + USERS_ROLE_ID_COLUMN + " = ? AND " + USERS_NAME_COLUMN + " = ?";
	private static final String UPDATE_USER_ROLE = "UPDATE " + USERS_ROLES_TABLE + " SET " + USERS_ROLE_NAME_COLUMN + " = ?" + " WHERE " + USERS_ROLE_NAME_COLUMN + " = ? AND " + USERS_NAME_COLUMN + " = ? ";
	
	/** A data member to get the database driver. */
	private DataSource dataSource;
	/** A data member to perform SQL related queries such as insertions, removals, and updates. */
	private JdbcTemplate jdbcTemplateObject;
	/** Enforces a transaction (all or nothing) when modifying the database. */
	private PlatformTransactionManager transactionManager;
	
	/**
	 * @param dataSource An object holding information to make a database connection.
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplateObject = new JdbcTemplate(this.dataSource);
	}
	
	/**
	 * @param transactionManager An object managing the all or nothing transaction.
	 */
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	/**
	 * This method is unused for earlier versions, this will be useful when creating a user profile page.
	 * @param userName The name of the user to retrieve
	 * @return A user object with populated data fields.
	 */
	public User getUser(String userName) {
		User user = null;
		try {
			user = jdbcTemplateObject.queryForObject(GET_USER_QUERY, new Object[] {userName}, new UserMapper(USERS_NAME_COLUMN, USERS_PASSWORD_COLUMN, ENABLED_COLUMN, LAST_POSTED_RECIPE_TIME_COLUMN,
					LAST_POSTED_REVIEW_TIME_COLUMN, USER_EMAIL_COLUMN, USER_ID_COLUMN));
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not get user: " + userName);
		}
		return user;
	}
	
	/**
	 * @param userName The user for the targeted password.
	 * @return Gets the encrypted user password for a user.
	 */
	public String getUserPassword(String userName) {
		// TODO Auto-generated method stub
		String userPassword = null;
		try {
			userPassword = jdbcTemplateObject.queryForObject(GET_USER_PASSWORD, new Object[]{userName}, new UserPasswordMapper(USERS_PASSWORD_COLUMN));
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not retrieve user password for: " + userName);
		}
		
		return userPassword;
	}
	
	/**
	 * @param userName The name of the user
	 * @return true if the user can post, false if not.
	 */
	public boolean getEnabled(String userName) {
		// TODO Auto-generated method stub
		boolean isEnabled = false;
		try {
			isEnabled = jdbcTemplateObject.queryForObject(GET_USER_ENABLED, new Object[]{userName}, new UserEnabledMapper(ENABLED_COLUMN));
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not get enabled status for user: " + userName);
		}
		return isEnabled;
	}
	
	/**
	 * @param userName The name of the user
	 * @return The last time a user posted a recipe.
	 */
	public LocalDateTime getLastPostedRecipeTime(String userName) {
		// TODO Auto-generated method stub
		LocalDateTime lastPostedRecipeTime = null;
		try {
			lastPostedRecipeTime = jdbcTemplateObject.queryForObject(GET_USER_LAST_POSTED_RECIPE_TIME, new Object[]{userName}, new UserLastPostedRecipeTimeMapper(LAST_POSTED_RECIPE_TIME_COLUMN));
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not get the last time user: " + userName + " posted a recipe.");
		}
		return lastPostedRecipeTime;
	}
	
	/**
	 * @param userName The name of the user
	 * @return The last time a user reviewed a recipe.
	 */
	public LocalDateTime getLastPostedReviewTime(String userName) {
		// TODO Auto-generated method stub
		LocalDateTime lastPostedReviewTime = null;
		try {
			lastPostedReviewTime = jdbcTemplateObject.queryForObject(GET_USER_LAST_POSTED_REVIEW_TIME, new Object[]{userName}, new UserLastPostedReviewTimeMapper(LAST_POSTED_REVIEW_TIME_COLUMN));
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not get the last time user: " + userName + " posted a review.");
		}
		return lastPostedReviewTime;
	}
	
	/**
	 * @param userName The user name to be checked
	 * @return null if the user name does not exist, and the user name if it does.
	 */
	public String getUserName(String userName) {
		// TODO Auto-generated method stub
		String user_Name = null;
		try {
			user_Name = jdbcTemplateObject.queryForObject(GET_USER_NAME, new Object[]{userName}, new UserNameMapper(USERS_NAME_COLUMN));
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("error retrieving the username: " + userName);
		}
		return user_Name;
	}
	
	/**
	 * @param userName The name of the user.
	 * @return the user's ID given the userName
	 */
	public Integer getUserId(String userName) {
		Integer usersId = null;
		try {
			usersId = jdbcTemplateObject.queryForObject(GET_USER_ID, new Object[]{userName}, new UserIdMapper(USER_ID_COLUMN));
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("error retrieving user id for user: " + userName);
		}
		return usersId;
	}
	
    /**
     * @param userName The name of the user to be added.
     * @param userPassword The encrypted password of the user.
     * @param enabled A value indicating if the user can post or a review or recipe.
     * @param numberShoppingLists The number of shopping lists the user curreently has created.
     * @param userEmail The email of the user.
     * @return 1 if the user has been added to the database, -1 if not.
     */
	public int createUser(String userName, String userPassword, boolean enabled) {
		// TODO Auto-generated method stub
		int returnCode = -1;
		try {
			returnCode = jdbcTemplateObject.update(INSERT_USER, new Object[] {userName, userPassword, enabled});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			throw new DataIntegrityViolationException("Could not create user: " + userName);
		}
		return returnCode;
	}
	
	/**
	 * @param userName Name of the user to delete.
	 * @return 1 if the user was deleted, 0 implies no deletion, -1 implies a database related error.
	 */
	public int deleteUser(String userName) {
		int deleteCode = -1;
		try {
			deleteCode = jdbcTemplateObject.update(DELETE_USER, new Object[]{userName});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not deelete user: " + userName);
		}
		return deleteCode;
	}
	
	/**
	 * @param newPassword The encrypted password to be stored.
	 * @param userName The name of the user.
	 * @param userId The id of the user.
	 * @return 1 if the user password has been updated, -1 if not.
	 */
	public int updateUserPassword(String newPassword, String userName, int userId) {
		// TODO Auto-generated method stub
		int returnCode = -1;
		try {
			returnCode = jdbcTemplateObject.update(UPDATE_USER_PASSWORD, new Object[] {newPassword, userName, userId});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("Could not update user: " + userName + "'s password");
		}
		return returnCode;
	}
	
	/**
	 * An admin tool, not used by the user.
	 * @param enabled The status if the user can post a recipe or a review.
	 * @param userName The name of the user.
	 * @return 1 if the user enabled status has been updated, -1 if not.
	 */
	public int updateUserEnabled(boolean enabled, String userName) {
		// TODO Auto-generated method stub
		int returnCode = -1;
		try {
			returnCode = jdbcTemplateObject.update(UPDATE_USER_ENABLED, new Object[] {enabled, userName});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("Could not update user: " + userName + "'s enabled status.");
		}
		return returnCode;
	}
	
	/**
	 * @param currentTime The current time the user has posted, removed, or modified a review.
	 * @param userName The name of the user whose recipe post time is to be updated.
	 * @return 1 if the last posted review time for the user was updated, -1 if not.
	 */
	public int updateUserLastPostedRecipeTime(LocalDateTime currentTime, String userName) {
		// TODO Auto-generated method stub
		int returnCode = -1;
		try {
			returnCode = jdbcTemplateObject.update(UPDATE_USER_LAST_POSTED_RECIPE_TIME, new Object[] {currentTime, userName});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			throw new DataIntegrityViolationException("Could not update user: " + userName + "'s last posted recipe time.");
		}
		return returnCode;
	}
	
	/**
	 * @param currentTime The current time the user has posted, removed, or modified a review.
	 * @param userName The name of the user whose review post time is to be updated.
	 * @return 1 if the last posted review time for the user was updated, -1 if not.
	 */
	public int updateUserLastPostedReviewTime(LocalDateTime currentTime, String userName) {
		// TODO Auto-generated method stub
		int returnCode = -1;
		try {
			returnCode = jdbcTemplateObject.update(UPDATE_USER_LAST_POSTED_REVIEW_TIME, new Object[] {currentTime, userName});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			throw new DataIntegrityViolationException("Could not update user: " + userName + "'s last posted review time.");
		}
		return returnCode;
	}
	
	/**
	 * @userName The name of the user.
	 * @return a list of roles for a specified username
	 */
	public List<UserRole> getUserRoles(String userName) {
		// TODO Auto-generated method stub
		List<UserRole> listOfRoles;
		try {
			listOfRoles = jdbcTemplateObject.query(GET_USER_ROLE, new Object[]{userName}, new UserRoleMapper(USERS_ROLE_NAME_COLUMN));
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not retrieve the role(s) for user: " + userName);
			return null;
		}
		return listOfRoles;
	}
	
	/**
	 * @param roleName The name of the role assigned to the user
	 * @param userName The name of the user.
	 * @return 1 if the role was inserted, -1 if not.
	 */
	public int insertUserRole(String roleName, String userName) {
		// TODO Auto-generated method stub
		int returnCode = -1;
		try {
			returnCode = jdbcTemplateObject.update(INSERT_USER_ROLE, new Object[] {roleName, userName});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			throw new DataIntegrityViolationException("Could not insert role: " + roleName + ", for user: " + userName);
		}
		return returnCode;
	}
	
	/**
	 * in earlier versions not used, when I implement an admin tool I will use this.
	 * @param userRoleId The id of the user.
	 * @param userName The user name to remove.
	 * @return 1 indicating a user role has been removed, -1 if not.
	 */
	public int deleteUserRole(int userRoleId, String userName) {
		// TODO Auto-generated method stub
		int returnCode = -1;
		try {
			returnCode = jdbcTemplateObject.update(DELETE_USER_ROLE, new Object[] {userRoleId, userName});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("Could not remove role for user: " + userName);
		}
		return returnCode;
	}

	/**
	 * @param roleName The newly assigned role.
	 * @param oldRoleName The former role.
	 * @param userName The name of the user whose role is to be modified.
	 * @return 1 if the role was updated.
	 */
	public int updateUserRole(String roleName, String oldRoleName, String userName) {
		// TODO Auto-generated method stub
		int updateReturnCode = -1;
		try {
			updateReturnCode = jdbcTemplateObject.update(UPDATE_USER_ROLE, new Object[] {roleName, oldRoleName, userName});
		}
		catch(DataAccessException e) {
			System.out.println(e.getMessage());
			System.out.println("could not update role for user: " + userName);
		}
		return updateReturnCode;
	}
	
	/**
	 * @param userName
	 * @param encryptedPassword
	 * @param shoppingListDAO
	 * @param shoppingListName
	 * @param pantryListDAO
	 * @param pantryListName
	 * @return
	 */
	public int createUserTransaction(String userName, String encryptedPassword, ShoppingListDAOImpl shoppingListDAO, String shoppingListName, PantryListDAOImpl pantryListDAO, String pantryListName) {
		int createUserCode = -1;
		TransactionDefinition def = new DefaultTransactionDefinition();
		TransactionStatus status = transactionManager.getTransaction(def);
		try {
			createUser(userName, encryptedPassword, true);
			shoppingListDAO.addList(shoppingListName, userName);
			pantryListDAO.addList(pantryListName, userName);
			insertUserRole("ROLE_USER", userName);
			transactionManager.commit(status);
			createUserCode = 1;
		}
		catch(DataIntegrityViolationException e) {
			System.out.println(e.getMessage());
	    	transactionManager.rollback(status);
	    	createUserCode = -1;
		}
		return createUserCode;
	}
	
}
