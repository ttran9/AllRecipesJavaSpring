package tran.allrecipes.presentation.model;

import java.util.List;

/**
 * @author Todd
 * A class representing a recipe, which consists of: a list of ingredients, a
 * list of directions, number of servings, preparation time, and time to cook.
 */
public class Recipe {
	/** A list of ingredients of a recipe.*/
	private List<Ingredient> listOfIngredients;
	/** number of servings for this recipe. */
	private int numServings;
	/** Name of the recipe. */
	private String recipeName;
	/** Recipe name delimited to account for apostrophes and quotes. */
	private String recipeNameDelimited;
	/** The ID of a recipe, can be used to unique identify a recipe. */
	private int recipeId;
	/** A link to an image of the recipe provided by the user. */
	private String imageURL;
	/** The type of dish: salad, soup, etc. for simplicity this is only going to be 1 string, instead of a list of strings.*/
	private String dishType;
	/** The time to prep the recipe. */
	private int prepTime;
	/** The time to cook the recipe. */
	private int cookTime;
	/** The unparsed time to prep the recipe. */
	private String prepTimeUnparsed;
	/** The unparsed time to cook the recipe. */
	private String cookTimeUnparsed;
	/** Summation of the cook and prep time. */
	private int readyTime;
	/** Name of the user that posted this recipe. */
	private String userOwner;
	/** number of 1 star reviews. */
	private Integer numberOneStarReviews;
	/** number of 2 star reviews. */
	private Integer numberTwoStarReviews;
	/** number of 3 star reviews. */
	private Integer numberThreeStarReviews;
	/** number of 4 star reviews. */
	private Integer numberFourStarReviews;
	/** number of 5 star reviews.*/
	private Integer numberFiveStarReviews;
	/** The average rating for this recipe. */
	private double averageRating;
	/** The total number of reviews. */
	private int totalNumberOfReviews;
	/** The date the recipe was originally created on. */
	private String recipeCreationDate;
	/** The recipe description */
	private String recipeDescription;
	/** Recipe description delimited to accouunt for apostrophes and quotes. */
	private String recipeDescriptionDelimited;
	
	public List<Ingredient> getListOfIngredients() {
		return listOfIngredients;
	}

	public void setListOfIngredients(List<Ingredient> listOfIngredients) {
		this.listOfIngredients = listOfIngredients;
	}

	public int getNumServings() {
		return numServings;
	}

	public void setNumServings(int numServings) {
		this.numServings = numServings;
	}

	public String getRecipeName() {
		return recipeName;
	}

	public void setRecipeName(String recipeName) {
		this.recipeName = recipeName;
	}
	
	public String getRecipeNameDelimited() {
		return recipeNameDelimited;
	}

	public void setRecipeNameDelimited(String recipeNameDelimited) {
		this.recipeNameDelimited = recipeNameDelimited;
	}

	public int getRecipeId() {
		return recipeId;
	}

	public void setRecipeId(int recipeId) {
		this.recipeId = recipeId;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public String getDishType() {
		return dishType;
	}

	public void setDishType(String dishType) {
		this.dishType = dishType;
	}

	public int getPrepTime() {
		return prepTime;
	}

	public void setPrepTime(int prepTime) {
		this.prepTime = prepTime;
	}

	public int getCookTime() {
		return cookTime;
	}

	public void setCookTime(int cookTime) {
		this.cookTime = cookTime;
	}
	
	public String getPrepTimeUnparsed() {
		return prepTimeUnparsed;
	}

	public void setPrepTimeUnparsed(String prepTimeUnparsed) {
		this.prepTimeUnparsed = prepTimeUnparsed;
	}

	public String getCookTimeUnparsed() {
		return cookTimeUnparsed;
	}

	public void setCookTimeUnparsed(String cookTimeUnparsed) {
		this.cookTimeUnparsed = cookTimeUnparsed;
	}

	public int getReadyTime() {
		return readyTime;
	}

	public void setReadyTime(int readyTime) {
		this.readyTime = readyTime;
	}
	
	public void calculateReadyTime() {
		this.readyTime = prepTime + cookTime;
	}
	
	public String getUserOwner() {
		return userOwner;
	}

	public void setUserOwner(String userOwner) {
		this.userOwner = userOwner;
	}
	
	public Integer getNumberOneStarReviews() {
		return numberOneStarReviews;
	}

	public void setNumberOneStarReviews(Integer numberOneStarReviews) {
		this.numberOneStarReviews = numberOneStarReviews;
	}

	public Integer getNumberTwoStarReviews() {
		return numberTwoStarReviews;
	}

	public void setNumberTwoStarReviews(Integer numberTwoStarReviews) {
		this.numberTwoStarReviews = numberTwoStarReviews;
	}

	public Integer getNumberThreeStarReviews() {
		return numberThreeStarReviews;
	}

	public void setNumberThreeStarReviews(Integer numThreeStarReviews) {
		this.numberThreeStarReviews = numThreeStarReviews;
	}

	public Integer getNumberFourStarReviews() {
		return numberFourStarReviews;
	}

	public void setNumberFourStarReviews(Integer numFourStarReviews) {
		this.numberFourStarReviews = numFourStarReviews;
	}

	public Integer getNumberFiveStarReviews() {
		return numberFiveStarReviews;
	}

	public void setNumberFiveStarReviews(Integer numFiveStarReviews) {
		this.numberFiveStarReviews = numFiveStarReviews;
	}
	
	public double getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(double averageRating) {
		this.averageRating = averageRating;
	}
	
	public int getTotalNumberOfReviews() {
		return totalNumberOfReviews;
	}

	public void setTotalNumberOfReviews(int totalNumberOfReviews) {
		this.totalNumberOfReviews = totalNumberOfReviews;
	}
	
	public String getRecipeCreationDate() {
		return recipeCreationDate;
	}

	public void setRecipeCreationDate(String recipeCreationDate) {
		this.recipeCreationDate = recipeCreationDate;
	}
	
	public String getRecipeDescription() {
		return recipeDescription;
	}

	public void setRecipeDescription(String recipeDescription) {
		this.recipeDescription = recipeDescription;
	}
	
	public String getRecipeDescriptionDelimited() {
		return recipeDescriptionDelimited;
	}

	public void setRecipeDescriptionDelimited(String recipeDescriptionDelimited) {
		this.recipeDescriptionDelimited = recipeDescriptionDelimited;
	}
}
