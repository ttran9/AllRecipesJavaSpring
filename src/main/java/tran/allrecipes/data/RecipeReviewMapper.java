package tran.allrecipes.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import tran.allrecipes.presentation.model.RecipeReview;
import tran.allrecipes.service.RecipeServiceImpl;

/**
 * @author Todd
 * This class will allow a recipe review object to be populated.
 */
public class RecipeReviewMapper implements RowMapper<RecipeReview> {

	// column names.
	private String reviewIdColumn = "reviewId";
	private String reviewContentColumn = "reviewContent";
	private String recipeRatingColumn = "recipeRating";
	private String reviewTitleColumn = "reviewTitle";
	private String reviewPostedTimeColumn = "reviewPostedTime";
	private String userPostedByColumn = "userPostedBy";
	
	// allows the review contents and title to be passed into JavaScript function(s).
	private String encoded_double_quote = "&quot;";
	private String encoded_single_quote = "&#8216;";
	
	/**
	 * @param rs An object holding a cursor to the current row.
	 * @param rowNum The current row number.
	 * @return A populated recipe review, or null if there was a SQL error.
	 */
	public RecipeReview mapRow(ResultSet rs, int rowNum) {
		// TODO Auto-generated method stub
		RecipeReview recipeReview = new RecipeReview();
		Integer reviewId = null;
		String reviewContent = null;
		Integer reviewRating = null;
		String reviewTitle = null;
		Timestamp postedTime = null;
		String userName = null;
		try {
			reviewId = rs.getObject(reviewIdColumn) != null ? rs.getInt(reviewIdColumn) : null;
			reviewContent = rs.getString(reviewContentColumn);
			reviewRating = rs.getObject(recipeRatingColumn) != null ? rs.getInt(recipeRatingColumn) : null;
			reviewTitle = rs.getString(reviewTitleColumn);
			postedTime = rs.getTimestamp(reviewPostedTimeColumn);
			userName = rs.getString(userPostedByColumn);
			
			if(reviewId != null) recipeReview.setReviewId(reviewId);
			if(reviewContent != null) {
				recipeReview.setReviewContent(reviewContent);
				String recipeReviewContentDelimited = reviewContent.replaceAll("\"", encoded_double_quote);
				recipeReviewContentDelimited = recipeReviewContentDelimited.replaceAll("'", encoded_single_quote);
				recipeReview.setReviewContentDelimited(recipeReviewContentDelimited);
			}
			if(reviewRating != null) recipeReview.setReviewRating(reviewRating);
			if(reviewTitle != null) {
				recipeReview.setReviewTitle(reviewTitle);
				String recipeReviewTitleDelimited = reviewTitle.replaceAll("\"", encoded_double_quote);
				recipeReviewTitleDelimited = recipeReviewTitleDelimited.replaceAll("'", encoded_single_quote);
				recipeReview.setReviewTitleDelimited(recipeReviewTitleDelimited);
			}
			if(postedTime != null) {
				RecipeServiceImpl recipeService = new RecipeServiceImpl();
				recipeReview.setReviewPostedTime(postedTime.toLocalDateTime());
				recipeReview.setParsedReviewPostedTime(recipeService.convertTimeObjectForDisplay(recipeReview.getReviewPostedTime()));
				recipeService = null;
			}
			if(userName != null) recipeReview.setUserNamePosted(userName);
		}
		catch(SQLException e) {
			System.out.println("error mapping recipe review");
			return null;
		}
		return recipeReview;
	}

}
