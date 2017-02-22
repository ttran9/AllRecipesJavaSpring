package tran.allrecipes.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import tran.allrecipes.presentation.model.UserRole;

/**
 * @author Todd
 * Gets the role assigned to a user.
 */
public class UserRoleMapper implements RowMapper<UserRole> {

	private String roleNameColumn; // the user role column.
	
	/**
	 * @param roleNameColumn The user role column.
	 */
	public UserRoleMapper(String roleNameColumn) {
		super();
		this.roleNameColumn = roleNameColumn;
	}

	/**
	 * @param rs An object holding a cursor to the current row.
	 * @param rowNum The current row number.
	 * @return An object holding the user's role, or null if there was a SQL error.
	 */
	public UserRole mapRow(ResultSet rs, int rowNum) {
		// TODO Auto-generated method stub
		UserRole userRole = new UserRole();
		String usersRole = null;
		try {
			usersRole = rs.getString(roleNameColumn);
			if(usersRole != null) userRole.setAuthority(usersRole);
		}
		catch(SQLException e) {
			System.out.println("could not retrieve the role(s).");
			return null;
		}
		return userRole;
	}

}
