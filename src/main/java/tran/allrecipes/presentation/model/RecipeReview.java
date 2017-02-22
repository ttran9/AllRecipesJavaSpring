package tran.allrecipes.presentation.model;

import java.time.LocalDateTime;

/**
 * @author Todd
 * A representation of an individual review of a recipe.
 */
public class RecipeReview {
	/** The detailed description of the review. */
	private String reviewContent;
	/** The description with the quotation characters accounted for. */
	private String reviewContentDelimited;
	/** The rating of the recipe, from 1-5. */
	private int reviewRating;
	/** The title of the review. */
	private String reviewTitle;
	/** Accounts for single quotes/apostrophes and double quotes. */
	private String reviewTitleDelimited;
	/** The ID of a review of some recipe. */
	private int reviewId;
	/** The date of the review. */
	private LocalDateTime reviewPostedTime;
	/** In a readable string format. */
	private String parsedReviewPostedTime;
	/** The date of the edited review. */
	private LocalDateTime reviewEditTime;
	/** The name of the user that posted the review. */
	private String userNamePosted;
	/** The name of the recipe that the review is a part of. */
	private String recipeName;
	
	public String getReviewContent() {
		return reviewContent;
	}

	public void setReviewContent(String reviewContent) {
		this.reviewContent = reviewContent;
	}
	
	public String getReviewContentDelimited() {
		return reviewContentDelimited;
	}

	public void setReviewContentDelimited(String reviewContentDelimited) {
		this.reviewContentDelimited = reviewContentDelimited;
	}

	public int getReviewRating() {
		return reviewRating;
	}

	public void setReviewRating(int reviewRating) {
		this.reviewRating = reviewRating;
	}

	public String getReviewTitle() {
		return reviewTitle;
	}

	public void setReviewTitle(String reviewTitle) {
		this.reviewTitle = reviewTitle;
	}
	
	public String getReviewTitleDelimited() {
		return reviewTitleDelimited;
	}

	public void setReviewTitleDelimited(String reviewTitleDelimited) {
		this.reviewTitleDelimited = reviewTitleDelimited;
	}
	
	public int getReviewId() {
		return reviewId;
	}

	public void setReviewId(int reviewId) {
		this.reviewId = reviewId;
	}
	
	public LocalDateTime getReviewPostedTime() {
		return reviewPostedTime;
	}

	public void setReviewPostedTime(LocalDateTime reviewPostedTime) {
		this.reviewPostedTime = reviewPostedTime;
	}
	
	public String getParsedReviewPostedTime() {
		return parsedReviewPostedTime;
	}

	public void setParsedReviewPostedTime(String parsedReviewPostedTime) {
		this.parsedReviewPostedTime = parsedReviewPostedTime;
	}
	
	public LocalDateTime getReviewEditTime() {
		return reviewEditTime;
	}

	public void setReviewEditTime(LocalDateTime reviewEditTime) {
		this.reviewEditTime = reviewEditTime;
	}
	
	public String getUserNamePosted() {
		return userNamePosted;
	}

	public void setUserNamePosted(String userNamePosted) {
		this.userNamePosted = userNamePosted;
	}
	
	public String getRecipeName() {
		return recipeName;
	}

	public void setRecipeName(String recipeName) {
		this.recipeName = recipeName;
	}
	
	public RecipeReview(String reviewContent, int reviewRating, String reviewTitle, int reviewId,
			LocalDateTime reviewPostedTime, String userNamePosted) {
		super();
		this.reviewContent = reviewContent;
		this.reviewRating = reviewRating;
		this.reviewTitle = reviewTitle;
		this.reviewId = reviewId;
		this.reviewPostedTime = reviewPostedTime;
		this.userNamePosted = userNamePosted;
	}

	public RecipeReview() {
		// TODO Auto-generated constructor stub
	}
	
}
