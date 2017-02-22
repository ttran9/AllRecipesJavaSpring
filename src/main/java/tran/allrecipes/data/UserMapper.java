package tran.allrecipes.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import tran.allrecipes.presentation.model.User;

/**
 * @author Todd
 * Populates a user object.
 */
public class UserMapper implements RowMapper<User> {
	
	// user table column names.
	private String userNameColumn;
	private String userPasswordColumn;
	private String enabledColumn;
	private String lastPostedRecipeTimeColumn;
	private String lastPostedReviewTimeColumn;
	private String userEmailColumn;
	private String userIdColumn;
	 
	/**
	 * @param userNameColumn The user name column.
	 * @param userPasswordColumn The encrypted password column.
	 * @param enabledColumn The enabled column.
	 * @param lastPostedRecipeTimeColumn The last posted recipe time column.
	 * @param lastPostedReviewTimeColumn The last posted review time column.
	 * @param userEmailColumn The user email column.
	 * @param userIdColumn The user id column.
	 */
	public UserMapper(String userNameColumn, String userPasswordColumn, String enabledColumn,
			String lastPostedRecipeTimeColumn, String lastPostedReviewTimeColumn,
			String userEmailColumn, String userIdColumn) {
		super();
		this.userNameColumn = userNameColumn;
		this.userPasswordColumn = userPasswordColumn;
		this.enabledColumn = enabledColumn;
		this.lastPostedRecipeTimeColumn = lastPostedRecipeTimeColumn;
		this.lastPostedReviewTimeColumn = lastPostedReviewTimeColumn;
		this.userEmailColumn = userEmailColumn;
		this.userIdColumn = userIdColumn;
	}

	/**
	 * @param rs An object holding a cursor to the current row.
	 * @param rowNum The current row number.
	 * @return A user object with populated data members, or null if there was a SQL error.
	 */
	public User mapRow(ResultSet rs, int rowNum) {
		// TODO Auto-generated method stub
		User user = new User();
		try {
			String userName = null;
			String userPassword = null;
			Boolean enabled = null;
			Timestamp lastPostedRecipeTime = null;
			Timestamp lastPostedReviewTime = null;
			String userEmail = null;
			Integer userId = null;
			
			userName = rs.getString(userNameColumn);
			userPassword = rs.getString(userPasswordColumn);
			enabled = rs.getBoolean(enabledColumn);
			lastPostedRecipeTime = rs.getTimestamp(lastPostedRecipeTimeColumn); 
			lastPostedReviewTime = rs.getTimestamp(lastPostedReviewTimeColumn);
			userEmail = rs.getString(userEmailColumn);
			userId = rs.getInt(userIdColumn);
			
			if(userName != null) user.setUserName(userName);
			if(userPassword != null) user.setPassword(userPassword);
			if(enabled != null) user.setEnabled(enabled);
			if(lastPostedRecipeTime != null) user.setLastPostedRecipeTime(lastPostedRecipeTime.toLocalDateTime());
			if(lastPostedReviewTime != null) user.setLastPostedReviewTime(lastPostedReviewTime.toLocalDateTime());
			if(userEmail != null) user.setUserEmail(userEmail);
			if(userId != null) user.setUserId(userId);
		}
		catch(SQLException e) {
			System.out.println("error mapping and retrieving user.");
			return null;
		}
		return user;
	}

}
