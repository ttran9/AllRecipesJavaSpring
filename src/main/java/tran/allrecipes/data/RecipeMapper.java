package tran.allrecipes.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import tran.allrecipes.presentation.model.Recipe;
import tran.allrecipes.service.RecipeServiceImpl;

/**
 * @author Todd
 * A class to populate a recipe object.
 */
public class RecipeMapper implements RowMapper<Recipe> {

	// column names.
	private String recipeIdColumn = "recipeId";
	private String recipeNameColumn = "recipeName";
	private String numberServingsColumn = "numberServings";
	private String prepTimeColumn = "prepTime";
	private String cookTimeColumn = "cookTime";
	private String dishTypeColumn = "dishType";
	private String imageURLColumn = "imageURL";
	private String userPostedByColumn = "userPostedBy";
	private String dateCreatedColumn = "dateCreated";
	private String recipeDescriptionColumn = "recipeDescription";
	
	private String oneStarReviewColumn = "numberOneStarReviews";
	private String TwoStarReviewColumn = "numberTwoStarReviews";
	private String ThreeStarReviewColumn = "numberThreeStarReviews";
	private String FourStarReviewColumn = "numberFourStarReviews";
	private String FiveStarReviewColumn = "numberFiveStarReviews";
	
	// allows the recipe name to be passed into JavaScript function(s).
	private String encoded_double_quote = "&quot;";
	private String encoded_single_quote = "&#8216;";
	
	/**
	 * @param rs An object holding a cursor to the current row.
	 * @param rowNum The current row number.
	 * @return A recipe object with populated data members, or null if there was a SQL error.
	 */
	public Recipe mapRow(ResultSet rs, int rowNum) {
		// TODO Auto-generated method stub
		Recipe recipe = new Recipe();
		Integer recipeId = null;
		String recipeName = null;
		Integer numberServings = null;
		Integer prepTime = null;
		Integer cookTime = null;
		String dishType = null;
		String imageURL = null;
		String userPostedBy = null;
		Integer oneStarReviews = null;
		Integer twoStarReviews = null;
		Integer threeStarReviews = null;
		Integer fourStarReviews = null;
		Integer fiveStarReviews = null;
		Timestamp recipeOriginalDateCreated = null;
		String recipeDescription = null;
		try {
			recipeId = rs.getInt(recipeIdColumn);
			recipeName = rs.getString(recipeNameColumn);
			numberServings = rs.getInt(numberServingsColumn);
			prepTime = rs.getInt(prepTimeColumn);
			cookTime = rs.getInt(cookTimeColumn);
			dishType = rs.getString(dishTypeColumn);
			imageURL = rs.getString(imageURLColumn);
			userPostedBy = rs.getString(userPostedByColumn);
			oneStarReviews = rs.getInt(oneStarReviewColumn);
			twoStarReviews = rs.getInt(TwoStarReviewColumn);
			threeStarReviews = rs.getInt(ThreeStarReviewColumn);
			fourStarReviews = rs.getInt(FourStarReviewColumn);
			fiveStarReviews = rs.getInt(FiveStarReviewColumn);
			recipeOriginalDateCreated = rs.getTimestamp(dateCreatedColumn);
			recipeDescription = rs.getString(recipeDescriptionColumn);
			
			if(recipeId != null) recipe.setRecipeId(recipeId);
			if(recipeName != null) {
				recipe.setRecipeName(recipeName);
				String recipeNameDelimited = recipeName.replaceAll("'", encoded_single_quote);
				recipeNameDelimited = recipeNameDelimited.replaceAll("\"", encoded_double_quote);
				recipe.setRecipeNameDelimited(recipeNameDelimited);
			}
			if(numberServings != null) recipe.setNumServings(numberServings);
			if(prepTime != null) recipe.setPrepTime(prepTime);
			if(cookTime != null) recipe.setCookTime(cookTime);
			if(dishType != null) recipe.setDishType(dishType);
			if(imageURL != null) recipe.setImageURL(imageURL);
			if(prepTime != null && cookTime != null) recipe.calculateReadyTime();
			if(userPostedBy != null) recipe.setUserOwner(userPostedBy);
			if(oneStarReviews != null) recipe.setNumberOneStarReviews(oneStarReviews);
			if(twoStarReviews != null) recipe.setNumberTwoStarReviews(twoStarReviews);
			if(threeStarReviews != null) recipe.setNumberThreeStarReviews(threeStarReviews);
			if(fourStarReviews != null) recipe.setNumberFourStarReviews(fourStarReviews);
			if(fiveStarReviews != null) recipe.setNumberFiveStarReviews(fiveStarReviews);
			if(recipeOriginalDateCreated != null)  {
				RecipeServiceImpl recipeService = new RecipeServiceImpl();
				recipe.setRecipeCreationDate(recipeService.convertTimeObjectForDisplay(recipeOriginalDateCreated.toLocalDateTime()));
				recipeService = null;
			}
			if(recipeDescription != null) { 
				recipe.setRecipeDescription(recipeDescription);
				String receiptDescriptionDelimited = recipeDescription.replaceAll("'", encoded_single_quote);
				receiptDescriptionDelimited = receiptDescriptionDelimited.replaceAll("\"", encoded_double_quote);
				recipe.setRecipeDescriptionDelimited(receiptDescriptionDelimited);
			}
		}
		catch(SQLException e) {
			System.out.println("not able to retrieve recipe");
			return null;
		}
		return recipe;
	}

}

