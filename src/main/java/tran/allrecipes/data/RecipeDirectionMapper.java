package tran.allrecipes.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import tran.allrecipes.presentation.model.RecipeDirection;

/**
 * @author Todd
 * Gets contents from the current returned row and populates a recipe direction object.
 */
public class RecipeDirectionMapper implements RowMapper<RecipeDirection> {

	// column names.
	private String directionIdColumn = "directionId";
	private String directionNumberColumn = "directionNumber";
	private String directionDescriptionColumn = "directionDescription";
	
	// characters to allow direction contents to be passed into JavaScript function(s).
	private String encoded_double_quote = "&quot;";
	private String encoded_single_quote = "&#8216;";
	
	/**
	 * @param rs An object holding a cursor to the current row.
	 * @param rowNum The current row.
	 * @return A recipe direction with populated data members, null if there was a SQL error.
	 */
	public RecipeDirection mapRow(ResultSet rs, int rowNum){
		// TODO Auto-generated method stub
		RecipeDirection recipeDirection = new RecipeDirection();
		Integer directionId = null;
		Integer directionNumber = null;
		String directionDescription = null;
		try {
			directionId = rs.getInt(directionIdColumn);
			directionNumber = rs.getInt(directionNumberColumn);
			directionDescription = rs.getString(directionDescriptionColumn);
			if(directionId != null) recipeDirection.setDirectionId(directionId);
			if(directionNumber != null) recipeDirection.setDirectionNumber(directionNumber);
			if(directionDescription != null) {
				recipeDirection.setDirection(directionDescription);
				String directionsDelimited = directionDescription.replaceAll("'", encoded_single_quote);
				directionsDelimited = directionsDelimited.replaceAll("\"", encoded_double_quote);
				recipeDirection.setDirectionsDelimited(directionsDelimited);
			}
		}
		catch(SQLException e) {
			System.out.println("error mapping recipe direction(s)");
			return null;
		}
		return recipeDirection;
	}

}
