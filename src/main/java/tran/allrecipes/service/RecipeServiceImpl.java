package tran.allrecipes.service;

import java.security.Principal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math3.fraction.Fraction;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tran.allrecipes.data.RecipeDAOImpl;
import tran.allrecipes.data.UsersDAOImpl;
import tran.allrecipes.presentation.model.Ingredient;
import tran.allrecipes.presentation.model.Recipe;
import tran.allrecipes.presentation.model.RecipeDirection;
import tran.allrecipes.presentation.model.RecipeReview;

/**
 * @author Todd
 * A class to provide logical operations to create, delete, and modify a recipe and its information.
 */
@Service
public class RecipeServiceImpl {
	/** A value to indicate the user wants to sort by the most recent reviews first. */
	private static final int SORT_BY_MOST_RECENT_REVIEW_TIME = 1;
	/** A value to indicate the user wants to sort by the oldest reviews first. */
	private static final int SORT_BY_OLDEST_REVIEW_TIME = 2;
	/** A value to indicate the user wants to sort by the highest rated reviews first. */
	private static final int SORT_BY_HIGHEST_REVIEWS = 3;
	/** A value to indicate the user wants to sort by the lowest rated reviews first. */
	private static final int SORT_BY_LOWEST_REVIEWS = 4;
	/** The location of the data source file. */
	private static final String DATABASE_SOURCE_FILE = "database/Datasource.xml";
	/** Name of the recipes DAO bean. */
	private static final String RECIPE_DAO_BEAN_NAME = "RecipesDAO";
	/** Name of the users DAO bean. */
	private static final String USER_DAO_BEAN_NAME = "UsersDAO";
	/** Arbitrary value to be used to check if a recipe belongs to a certain user. */
	private static final int RECIPE_TYPE = 200;
	/** the showSingleRecipe page. */
	private static final String SHOW_SINGLE_RECIPE = "showSingleRecipe";
	/** redirects to the recipe page with a message. */
	private static final String REDIRECT_TO_RECIPE = "redirect:/showSingleRecipe";
	/** redirect to the show all recipes page with a message if specified. */
	private static final String REDIRECT_TO_ALL_RECIPES = "redirect:/";
	/** the message parameter portion of the URL. */
	private static final String MESSAGE_PARAMETER = "message";
	/** recipe name parameter. */
	private static final String RECIPE_NAME_PARAMETER = "recipeName";
	/** name of the edit recipe page. */
	private static final String EDIT_RECIPE_PAGE = "editRecipeDetails";
	/** The logged in user name parameter. */
	private static final String LOGGED_IN_USER_NAME_PARAM = "loggedInName";

	public RecipeServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	/* 
	 * Recipe Directions Section 
	 */
	
	/**
	 * @param principal An object holding authentication information of the current user.
	 * @param recipeName The name of the recipe.
	 * @param directionDescription The description of this specific direction step.
	 * @return An object with a HTTP status indicating if a direction was added to the recipee.
	 */
	public ResponseEntity<String> addRecipeDirection(Principal principal, String recipeName, String directionDescription) {
		ResponseEntity<String> returnCode = null;
		if(recipeName != null && directionDescription != null) {
			UtilityServiceImpl utilityService = new UtilityServiceImpl();
		    if(utilityService.isOwner(principal, RECIPE_TYPE, recipeName)) {
		    	ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
				RecipeDAOImpl recipesObject = (RecipeDAOImpl)appContext.getBean(RECIPE_DAO_BEAN_NAME);
				Recipe recipeToUpdate = recipesObject.getRecipe(recipeName);
				if(recipeToUpdate != null) {
					// get the current number of directions and add 1 to this.
					int directionNumber = recipesObject.getNumberOfDirections(recipeName);
					if(directionNumber >= 0) {
						int newDirectionNumber = directionNumber + 1;
						int insertDirectionCode = recipesObject.addRecipeDirection(newDirectionNumber, directionDescription, recipeName);
						// use the direction number that is inserted above and recipeName to get the direction Id.
						if(insertDirectionCode == 1) {
							Integer recentDirectionIdCode = recipesObject.getMostRecentDirectionId(recipeName, newDirectionNumber);
							if(recentDirectionIdCode != null) {
								// pass the direction id and current direction number if there is a 200 status code.
								returnCode = new ResponseEntity<String>(newDirectionNumber + "," + recentDirectionIdCode, HttpStatus.OK);
							}
							else {
								returnCode = new ResponseEntity<String>("Direction added, refresh the page to see it.", HttpStatus.INTERNAL_SERVER_ERROR);
							}
						}
						else {
							returnCode = new ResponseEntity<String>("Inserting direction failed.", HttpStatus.INTERNAL_SERVER_ERROR);
						}
					}
					else {
						returnCode = new ResponseEntity<String>("Cannot get number of directions", HttpStatus.INTERNAL_SERVER_ERROR);
					}
					recipesObject = null;
					((ConfigurableApplicationContext)appContext).close();
				}
				else {
					recipesObject = null;
					((ConfigurableApplicationContext)appContext).close();
					returnCode = new ResponseEntity<String>("can't get the recipe you are trying to add directions to.", HttpStatus.NOT_ACCEPTABLE);
				}
		    }
		    else {
		    	returnCode = new ResponseEntity<String>("you must be the owner of the recipe to add directions.", HttpStatus.UNAUTHORIZED);
		    }
		    utilityService = null;
		}
	    else {
	    	returnCode = new ResponseEntity<String>("Fields must be filled out properly", HttpStatus.NOT_ACCEPTABLE);
	    }
		return returnCode;
	}
	
	/**
	 * finds the direction to be deleted and decrements each succeeding direction number by one.
	 * @param principal An object holding authentication information of the current user.
	 * @param directionId The unique identifier of a recipe direction.
	 * @param recipeName The name of the recipe.
	 * @param directionNumber The direction number of the to be removed.
	 * @param redirectAttrs An object holding the attributes for when a redirection is done.
	 * @return The recipe page with updated directions or a message notifying the user that the direction could not be removed.
	 */
	public String deleteRecipeDirection(Principal principal, String directionId, String recipeName, String directionNumber, RedirectAttributes redirectAttrs) {
		String errorMessage = "invalid parameters";
		if(directionId != null && recipeName != null && directionNumber != null) {
			UtilityServiceImpl utilityService = new UtilityServiceImpl();
		    if(utilityService.isOwner(principal, RECIPE_TYPE, recipeName)) {
	    		ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
				RecipeDAOImpl recipesObject = (RecipeDAOImpl)appContext.getBean(RECIPE_DAO_BEAN_NAME);
				Recipe recipeToUpdate = recipesObject.getRecipe(recipeName);
				if(recipeToUpdate != null) {
					Integer ingredientIdValue = Integer.parseInt(directionId);
					int deleteCode = recipesObject.removeRecipeDirection(ingredientIdValue);
					if(deleteCode == -1) {
						errorMessage = "direction deletion error";
					}
					else if(deleteCode == 0) {
						errorMessage = "no matching direction";
					}
					else {
						errorMessage = "";
						int parsedDirectionNumber = Integer.parseInt(directionNumber);                                                                                                            
						List<RecipeDirection> recipeDirections = recipesObject.getRecipeDirections(recipeName);
						int returnCodeValue = -1;
						// must do minus one as one direction item has been deleted already.
						for(int i = parsedDirectionNumber - 1; i < recipeDirections.size(); i++) {
							RecipeDirection modifiedDirectionsObject = recipeDirections.get(i);
							returnCodeValue = recipesObject.updateRecipeDirection(modifiedDirectionsObject.getDirectionNumber() - 1, modifiedDirectionsObject.getDirection(), modifiedDirectionsObject.getDirectionNumber(), recipeName);
							if(returnCodeValue != 1) {
								errorMessage = "update recipe direction error"; 
							}
						}
						recipesObject = null;
						((ConfigurableApplicationContext)appContext).close();
						if(errorMessage.equals("")) {
							redirectAttrs.addAttribute(RECIPE_NAME_PARAMETER, recipeName);
							return REDIRECT_TO_RECIPE;
						}
					}
				}
				else {
					errorMessage = "cannot find the recipe you are trying to remove directions from.";
					recipesObject = null;
					((ConfigurableApplicationContext)appContext).close();
				}
		    }
		    else {
		    	errorMessage = "you must be the owner of the recipe to remove directions from it.";
		    }
		    utilityService = null;
		}
		redirectAttrs.addAttribute(RECIPE_NAME_PARAMETER, recipeName);
		redirectAttrs.addAttribute(MESSAGE_PARAMETER, errorMessage);
		return REDIRECT_TO_RECIPE;
	}
	
	/**
	 * Helper method to update contents of a step/direction of a recipe.
	 * @param principal An object holding authentication information of the current user.
	 * @param directionContent The modified directions.
	 * @param directionNumber The direction to be modified.
	 * @param recipeName The name of the recipe.
	 * @param redirectAttrs An object holding the attributes for when a redirection is done.
	 * @return The recipe page with the direction updated or a message indicating that the direction could not be modified.
	 */
	public String updateRecipeDirectionContent(Principal principal, String directionContent, String directionNumber,
		String recipeName, RedirectAttributes redirectAttrs) {
		String errorMessage = "invalid parameters";
		if(directionContent != null && directionNumber != null && recipeName != null) {
			UtilityServiceImpl utilityService = new UtilityServiceImpl();
		    if(utilityService.isOwner(principal, RECIPE_TYPE, recipeName)) {		
	    		ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
				RecipeDAOImpl recipesObject = (RecipeDAOImpl)appContext.getBean(RECIPE_DAO_BEAN_NAME);
				Recipe recipeToUpdate = recipesObject.getRecipe(recipeName);
				if(recipeToUpdate != null) {
					int updateContentCode = recipesObject.updateRecipeDirectionContent(directionContent, Integer.parseInt(directionNumber), recipeName);
					if(updateContentCode != 1) {
						errorMessage = "failed to update direction content for direction number: " + directionNumber;
					}
					else {
						utilityService = null;
						recipesObject = null;
						((ConfigurableApplicationContext)appContext).close();
						redirectAttrs.addAttribute(RECIPE_NAME_PARAMETER, recipeName);
						return REDIRECT_TO_RECIPE;
					}
				}
				else {
					errorMessage = "cannot find the recipe you are trying to modify directions for.";
				}
				recipesObject = null;
				((ConfigurableApplicationContext)appContext).close();
		    }
		    else {
		    	errorMessage = "you must be the owner of the recipe to update a direction.";
		    }
		    utilityService = null;
		}
		redirectAttrs.addAttribute(RECIPE_NAME_PARAMETER, recipeName);
		redirectAttrs.addAttribute(MESSAGE_PARAMETER, errorMessage);
		return REDIRECT_TO_RECIPE;
	}
	
	/* 
	 * Recipe Ingredients Section 
	*/
	
	/**
	 * @param principal An object holding authentication information of the current user.
	 * @param recipeName The name of the recipe with the ingredient being added to.
	 * @param ingredientName The name of the ingredient being added.
	 * @param ingredientUnit The unit of the ingredient (ml., L., etc).
	 * @param ingredientWholeNumber The whole number portion of the ingredient.
	 * @param ingredientQuantity A fraction quantity of the ingredient.
	 * @param ingredientType The type of in the ingredient (meat, vegetable, etc).
	 * @return An object with a HTTP status code notifying the user if the ingredient was added or not able to be added.
	 */
	public ResponseEntity<String> addRecipeIngredient(Principal principal, String recipeName, String ingredientName, String ingredientUnit, String ingredientWholeNumber, 
		String ingredientQuantity, String ingredientType) {
		ResponseEntity<String> returnCode = null;
		if(recipeName != null && ingredientName != null && ingredientUnit != null && ingredientWholeNumber != null && ingredientQuantity != null) {
		    UtilityServiceImpl utilityService = new UtilityServiceImpl();
			if(utilityService.isOwner(principal, RECIPE_TYPE, recipeName)) {
	    		ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
				RecipeDAOImpl recipesObject = (RecipeDAOImpl)appContext.getBean(RECIPE_DAO_BEAN_NAME);
				Recipe recipeToUpdate = recipesObject.getRecipe(recipeName);
				if(recipeToUpdate != null) {
					// check input fields, the whole number, numerator, and denominator. 
					int wholeNumber = Integer.parseInt(ingredientWholeNumber);
					String[] fractionValue = ingredientQuantity.split("/");
					if(fractionValue.length == 2) {
						if(fractionValue[0] != null && fractionValue[1] != null) {
							int numeratorValue = Integer.parseInt(fractionValue[0]);
							int denominatorValue = Integer.parseInt(fractionValue[1]);
							String errorCheckString = utilityService.validateUserInput(wholeNumber, numeratorValue, denominatorValue);
							if(!errorCheckString.equals("")) {
								returnCode = new ResponseEntity<String>(errorCheckString, HttpStatus.NOT_ACCEPTABLE);
							}
							else {
								// before inserting make sure the one case of a whole number without a fraction is accounted for.
								if(wholeNumber > 0 && (numeratorValue == 1 && denominatorValue == 1)) {
									numeratorValue = 0;
								}
								else if(wholeNumber == 0 && (numeratorValue == 1 && denominatorValue == 1)) {
									wholeNumber = 1;
									numeratorValue = 0;
								}
								int addCode = recipesObject.addListIngredient(ingredientName, wholeNumber, numeratorValue, denominatorValue, 
										ingredientUnit, ingredientType, recipeName);
								if(addCode == -1)
									returnCode = new ResponseEntity<String>("database error", HttpStatus.INTERNAL_SERVER_ERROR);
								else if(addCode == 0)
									returnCode = new ResponseEntity<String>("cannot add " + ingredientName, HttpStatus.INTERNAL_SERVER_ERROR);
								else if(addCode == 1) {
									Ingredient ingredient = recipesObject.getSingleIngredient(recipeName, ingredientName);
									if(ingredient != null) {
										Integer ingredientId = ingredient.getIngredientID() != 0 ? ingredient.getIngredientID() : null;
										String ingredientDisplayType = ingredient.getDisplayType() != null ? ingredient.getDisplayType() : null; 
										if(ingredient != null && ingredientId != null && ingredientDisplayType != null) {
											returnCode = new ResponseEntity<String>(ingredientId + "," + ingredientDisplayType, HttpStatus.OK);
										}
										else
											returnCode = new ResponseEntity<String>("error retrieving the added ingredient.", HttpStatus.INTERNAL_SERVER_ERROR);
									}
									else {
										returnCode = new ResponseEntity<String>("error retrieving the added ingredient: " + ingredientName, HttpStatus.INTERNAL_SERVER_ERROR);
									}
								}
							}
						}
						else {
							returnCode = new ResponseEntity<String>("can't parse fractional input.", HttpStatus.INTERNAL_SERVER_ERROR);
						}
					}
					else {
						returnCode = new ResponseEntity<String>("incorrect fractional input.", HttpStatus.INTERNAL_SERVER_ERROR);
					}
				}
				else {
					returnCode = new ResponseEntity<String>("can't get the recipe to be added to.", HttpStatus.NOT_ACCEPTABLE);
				}
				recipeToUpdate = null;
				recipesObject = null;
				((ConfigurableApplicationContext)appContext).close();
		    }
		    else {
		    	returnCode = new ResponseEntity<String>("you must be the owner of the recipe to add to it.", HttpStatus.UNAUTHORIZED);
		    }
			utilityService = null;
		}
		else {
			returnCode = new ResponseEntity<String>("Fields must be filled out properly", HttpStatus.NOT_ACCEPTABLE);
		}
		return returnCode;
	}
	
	/**
	 * @param principal An object holding authentication information of the current user.
	 * @param ingredientId The unique identifer of the ingredient to be removed.
	 * @param recipeName The recipe with an ingredient being removed.
	 * @return An object with an HTTP status code indicating if the AJAX call was able to successfully remove the ingredient.
	 */
	public ResponseEntity<String> deleteRecipeIngredient(Principal principal, String ingredientId, String recipeName) {
		ResponseEntity<String> returnCode = null;
		if(recipeName != null && ingredientId != null) {
			UtilityServiceImpl utilityService = new UtilityServiceImpl();
		    if(utilityService.isOwner(principal, RECIPE_TYPE, recipeName)) {
	    		ApplicationContext appContext = new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
				RecipeDAOImpl recipesObject = (RecipeDAOImpl)appContext.getBean(RECIPE_DAO_BEAN_NAME);
				Recipe recipeToUpdate = recipesObject.getRecipe(recipeName);
				if(recipeToUpdate != null) {
					Integer ingredientIdValue = Integer.parseInt(ingredientId);
					int deleteCode = recipesObject.removeListIngredient(ingredientIdValue);
					if(deleteCode == -1)
						returnCode = new ResponseEntity<String>("ingredient deletion error", HttpStatus.INTERNAL_SERVER_ERROR);
					else if(deleteCode == 0)
						returnCode = new ResponseEntity<String>("no matching ingredient", HttpStatus.INTERNAL_SERVER_ERROR);
					else
						returnCode = new ResponseEntity<String>(HttpStatus.OK);
				}
				else {
					returnCode = new ResponseEntity<String>("cannot delete item as the recipe can't be found.", HttpStatus.NOT_ACCEPTABLE);
				}
				recipesObject = null;
				recipeToUpdate = null;
				((ConfigurableApplicationContext)appContext).close();
		    }
		    else {
		    	returnCode = new ResponseEntity<String>("you must be the owner of the recipe to remove an ingredient from it.", HttpStatus.UNAUTHORIZED);
		    }
		    utilityService = null;
		}
		else {
			returnCode = new ResponseEntity<String>("recipe name is not provided and/or the ingredient could not be identified.", HttpStatus.NOT_ACCEPTABLE);
		}
		return returnCode;
	}
	
	/**
	 * Updates an ingredient amount.
	 * @param ingredientName The name of the ingredient.
	 * @param recipeName The name of the recipe.
	 * @param wholeNumberQuantity The whole number (if applicable) of the ingredient.
	 * @param fractionInput The numerator and denominator portion of the ingredient.
	 * @param ingredientUnit The updated unit type.
	 * @param ingredientType The updated ingredient type.
	 * @param redirectAttrs An object holding the attributes for when a redirection is done.
	 * @param principal An object holding authentication information of the current user.
	 * @return The recipe page with the ingredient updated, or a notification message indicating what went wrong while trying to update the ingredient quantity.
	 */
	public String updateRecipeIngredientAmount(String ingredientName, String recipeName, String wholeNumberQuantity, String fractionInput, String ingredientUnit, String ingredientType,  
		RedirectAttributes redirectAttrs, Principal principal) {
		String errorMessage = "invalid parameter(s).";
		if(recipeName != null && wholeNumberQuantity != null && ingredientName != null && fractionInput != null) {
			UtilityServiceImpl utilityService = new UtilityServiceImpl();
		    if(utilityService.isOwner(principal, RECIPE_TYPE, recipeName)) {
	    		ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
				RecipeDAOImpl recipesObject = (RecipeDAOImpl)appContext.getBean(RECIPE_DAO_BEAN_NAME);
				Recipe recipeToUpdate = recipesObject.getRecipe(recipeName);
				if(recipeToUpdate != null) {
					String[] parsedFractionInput = fractionInput.split("/");
					if(parsedFractionInput.length == 2) {
						if(parsedFractionInput[0] != null && parsedFractionInput[1] != null) {
							int updateCode = -2;
							
							Integer ingredientId = recipesObject.getIngredientId(recipeName, ingredientName);
							String errorCheckString = null;
							
							if(ingredientId != null && ingredientId >= 1) {
								// now attempt to update
								int wholeNumber = Integer.parseInt(wholeNumberQuantity);
								int numerator = Integer.parseInt(parsedFractionInput[0]);
								int denominator = Integer.parseInt(parsedFractionInput[1]);
								
								errorCheckString = utilityService.validateUserInput(wholeNumber, numerator, denominator);
								if(errorCheckString.equals("")) {
									// before inserting make sure the one case of a whole number without a fraction is accounted for.
									if(wholeNumber > 0 && (numerator == 1 && denominator == 1)) {
										numerator = 0;
									}
									else if(wholeNumber == 0 && (numerator == 1 && denominator == 1)) {
										wholeNumber = 1;
										numerator = 0;
									}
									updateCode = recipesObject.updateListIngredient(wholeNumber, numerator, denominator, ingredientUnit, ingredientType, ingredientId);
								}
							}
							if(updateCode == 1) {
								recipeToUpdate = null;
								recipesObject = null;
								((ConfigurableApplicationContext)appContext).close();
								redirectAttrs.addAttribute(RECIPE_NAME_PARAMETER, recipeName);
								return REDIRECT_TO_RECIPE;
							}
							else {
								errorMessage = "error modifying ingredient " + ingredientName + "'s quantity";
							}
						}
						else {
							errorMessage = "cannot read the fraction input.";
						}
					}
					else {
						errorMessage = "incorrect fraction input.";
					}
				}
				else {
					errorMessage = "recipe doesn't exist.";
				}
				recipeToUpdate = null;
				recipesObject = null;
				((ConfigurableApplicationContext)appContext).close();
		    }
		    else {
		    	errorMessage = "you must be the owner of the recipe to update an ingredient.";
		    }
		    utilityService = null;
		}
		if(recipeName != null) {
			redirectAttrs.addAttribute(MESSAGE_PARAMETER, errorMessage);
			redirectAttrs.addAttribute(RECIPE_NAME_PARAMETER, recipeName);
			return REDIRECT_TO_RECIPE;
		}
		else {
			errorMessage = "the recipe name could not be retrieved.";
			redirectAttrs.addAttribute(MESSAGE_PARAMETER, errorMessage);
			return REDIRECT_TO_ALL_RECIPES;
		}
	}
	
	/**
	 * @param principal An object holding authentication information of the current user.
	 * @param newServings The new servings value of the recipe.
	 * @param recipeName The name of the recipe to modify.
	 * @param oldServings The current servings value of the recipe before modification.
	 * @param redirectAttrs An object holding the attributes for when a redirection is done.
	 * @return The recipe that is being modified, un-successful an error message will be displayed, if successful no message.
	 */
	public String modifyRecipeIngredients(Principal principal, String newServings, String recipeName, String oldServings, RedirectAttributes redirectAttrs) {
		String errorMessage = "invalid parameters";
		if(recipeName != null && newServings != null && oldServings != null) {
			UtilityServiceImpl utilityService = new UtilityServiceImpl();
			if(utilityService.isOwner(principal, RECIPE_TYPE, recipeName)) {
		    	ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
				RecipeDAOImpl recipesObject = (RecipeDAOImpl)appContext.getBean(RECIPE_DAO_BEAN_NAME);
				Recipe recipeToUpdate = recipesObject.getRecipe(recipeName);
				if(recipeToUpdate != null) {
					int newServingsParsed = Integer.parseInt(newServings);
					int oldServingsParsed = Integer.parseInt(oldServings);
					
					if(newServingsParsed > 0 && oldServingsParsed > 0) {
						
						List<Ingredient> ingredientsToModify = recipesObject.getListIngredients(recipeName);
						Fraction refactorFraction = new Fraction(newServingsParsed, oldServingsParsed);
						Fraction oldQuantity = null;
						boolean wasUpdated = true;
						
						int updateCode = -1;
						
						if(ingredientsToModify != null) {
							for(Ingredient ingredients : ingredientsToModify) {
								
								int oldWholeNumber = Integer.parseInt(ingredients.getWholeNumber());
								int oldNumerator = Integer.parseInt(ingredients.getNumerator());
								int oldDenominator = Integer.parseInt(ingredients.getDenominator());
								
								oldQuantity = new Fraction((oldDenominator * oldWholeNumber) + oldNumerator, oldDenominator);
								
								errorMessage = utilityService.validateUserInput(oldWholeNumber, oldNumerator, oldDenominator);
								if(!errorMessage.equals("")) {
									wasUpdated = false;
									break;
								}
								
								oldQuantity = oldQuantity.multiply(refactorFraction);
								
								int newWholeNumber = oldQuantity.getNumerator() / oldQuantity.getDenominator();
								int newNumerator = oldQuantity.getNumerator() % oldQuantity.getDenominator();
								int newDenominator = oldQuantity.getDenominator();
								
								updateCode = recipesObject.updateListIngredient(newWholeNumber, newNumerator, newDenominator, ingredients.getIngredientUnit(), ingredients.getIngredientType(), ingredients.getIngredientID());
								if(updateCode != 1) {
									wasUpdated = false;
									errorMessage = "could not update an ingredient's quantity.";
									break;
								}
							}
							if(wasUpdated) {
								updateCode = recipesObject.updateRecipeServings(Integer.parseInt(newServings), recipeName);
							}
						}
						if(updateCode == 1) {
							recipesObject = null;
							((ConfigurableApplicationContext)appContext).close();
							redirectAttrs.addAttribute(RECIPE_NAME_PARAMETER, recipeName);
							return REDIRECT_TO_RECIPE;
						}
					}
					else {
						errorMessage = "The servings of a recipe cannot be 0.";
					}
				}
				else {
					errorMessage = "cannot retrieve the recipe.";
				}
				recipesObject = null;
				((ConfigurableApplicationContext)appContext).close();
		    }
		    else {
		    	errorMessage = "you are not the owner of the recipe so you cannot modify the servings.";
		    }
		}
		redirectAttrs.addAttribute(RECIPE_NAME_PARAMETER, recipeName);
		redirectAttrs.addAttribute(MESSAGE_PARAMETER, errorMessage);
		return REDIRECT_TO_RECIPE;
	}
	
	
	/* 
	 * Recipe Reviews Section 
	 */
	
	/**
	 * @param principal An object holding authentication information of the current user.
	 * @param reviewContent The content of the review.
	 * @param recipeRating The rating of the review.
	 * @param reviewTitle The title of the review.
	 * @param userPostedBy The name of the user posting the review.
	 * @param recipeName The recipe the review is being written about.
	 * @return An object with an HTTP status code indicating if the review was updated or not.
	 */
	public ResponseEntity<String> addRecipeReview(Principal principal, String reviewContent, String recipeRating, String reviewTitle,  String userPostedBy, String recipeName) {
		ResponseEntity<String> returnCode = null;
		// add to the recipe rating of the recipes.
		if(reviewContent != null && recipeRating != null && reviewTitle != null && userPostedBy != null && recipeName != null) {
			LocalDateTime currentTime = Timestamp.from(Instant.now()).toLocalDateTime();
			ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
			RecipeDAOImpl recipesObject = (RecipeDAOImpl)appContext.getBean(RECIPE_DAO_BEAN_NAME);
			UsersDAOImpl usersDAO = (UsersDAOImpl) appContext.getBean(USER_DAO_BEAN_NAME);
			UtilityServiceImpl utilityService = new UtilityServiceImpl();
		    if(utilityService.isUserAuthenticated(principal)) {
		    	LocalDateTime lastPostedReviewTime = usersDAO.getLastPostedReviewTime(userPostedBy);
		    	if(utilityService.canUserModify(lastPostedReviewTime, currentTime)) {
					Recipe recipeToUpdate = recipesObject.getRecipe(recipeName);
					if(recipeToUpdate != null) {
						int recipeRatingParsed = Integer.parseInt(recipeRating);
						int addRecipeReviewCode = -1;
						
						if(recipeRatingParsed > 0 && recipeRatingParsed <= 5) {
							addRecipeReviewCode = recipesObject.addRecipeReview(reviewContent, recipeRatingParsed, reviewTitle, currentTime, currentTime, userPostedBy, recipeName);
						}
						
						if(addRecipeReviewCode == 1) {
							int modifyLastReviewTimeCode = usersDAO.updateUserLastPostedReviewTime(currentTime, userPostedBy);
							if(modifyLastReviewTimeCode != 1) {
								// send an email to myself with the problem.
								GmailService emailService = new GmailService();
								String emailSubject = "AR: Add Review Issue";
								String bodyText = "User: " + userPostedBy + " has added a review but the reviewPostedTime column is not able to be updated.";
								emailService.sendEmail(bodyText, utilityService.getAdminUserNames(), emailSubject);
								emailService = null;
							}
							// now you must be able to get the review's id and send it back to the ajax call.
							Integer getRecipeReviewIdCode = recipesObject.getSingleRecipeReviewId(userPostedBy, recipeName);
							
							if(getRecipeReviewIdCode != null) {
								// since the review is added, update the rating in the recipe table.
								Recipe recipeObject = recipesObject.getRecipe(recipeName);
								
								int updateRatingCode = -1;
								int updatedReviewQuantity = 0;
								
								if(recipeRatingParsed == 1) {
									updatedReviewQuantity = recipeObject.getNumberOneStarReviews() + 1;
									updateRatingCode = recipesObject.updateRecipeOneStarRating(updatedReviewQuantity, recipeName);
								}
								else if(recipeRatingParsed == 2) {
									updatedReviewQuantity = recipeObject.getNumberTwoStarReviews() + 1;
									updateRatingCode = recipesObject.updateRecipeTwoStarRating(updatedReviewQuantity, recipeName);
								}
								else if(recipeRatingParsed == 3) {
									updatedReviewQuantity = recipeObject.getNumberThreeStarReviews() + 1;
									updateRatingCode = recipesObject.updateRecipeThreeStarRating(updatedReviewQuantity, recipeName);					
								}
								else if(recipeRatingParsed == 4) {
									updatedReviewQuantity = recipeObject.getNumberFourStarReviews() + 1;
									updateRatingCode = recipesObject.updateRecipeFourStarRating(updatedReviewQuantity, recipeName);
								}
								else if(recipeRatingParsed == 5) {
									updatedReviewQuantity = recipeObject.getNumberFiveStarReviews() + 1;
									updateRatingCode = recipesObject.updateRecipeFiveStarRating(updatedReviewQuantity, recipeName);
								}
								
								if(updateRatingCode > 0) {
									// now get this review and get the time it was posted at and return that.
									RecipeReview addedRecipeReview = recipesObject.getRecipeReview(getRecipeReviewIdCode);
									if(addedRecipeReview != null && addedRecipeReview.getParsedReviewPostedTime() != null) {
										Recipe updatedRecipe = recipesObject.getRecipe(recipeName);
										
										// recompute the average.
										int numberOneStarReviews = updatedRecipe.getNumberOneStarReviews();
										int numberTwoStarReviews = updatedRecipe.getNumberTwoStarReviews();
										int numberThreeStarReviews = updatedRecipe.getNumberThreeStarReviews();
										int numberFourStarReviews = updatedRecipe.getNumberFourStarReviews();
										int numberFiveStarReviews = updatedRecipe.getNumberFiveStarReviews();
										
										int totalReviews = numberOneStarReviews + numberTwoStarReviews + numberThreeStarReviews + numberFourStarReviews + numberFiveStarReviews;
										
										DecimalFormat ratingFormat = new DecimalFormat("#.#");
										double summationValue = (double) ((numberOneStarReviews * 1) + (numberTwoStarReviews * 2) + (numberThreeStarReviews * 3) + (numberFourStarReviews * 4) + (numberFiveStarReviews * 5));
										
										double averageValue = Double.valueOf(ratingFormat.format(((double)summationValue / (double)totalReviews)));
										
										returnCode = new ResponseEntity<String>(getRecipeReviewIdCode + "," + addedRecipeReview.getParsedReviewPostedTime() + "," + averageValue + "," + totalReviews + "," + updatedReviewQuantity, HttpStatus.OK);
									}
									else {
										returnCode = new ResponseEntity<String>("error getting the added recipe review's time, refresh your page to see the change.", HttpStatus.INTERNAL_SERVER_ERROR);
									}
								}
								else {
									returnCode = new ResponseEntity<String>("error updating the recipe rating amount, refresh your page to see the change.", HttpStatus.INTERNAL_SERVER_ERROR);
								}
							}
							else {
								returnCode = new ResponseEntity<String>("could not get the recipe review's id, refresh your page to see the change.", HttpStatus.INTERNAL_SERVER_ERROR);
							}
						}
						else {
							returnCode = new ResponseEntity<String>("could not add the recipe review, make sure you have entered valid input.", HttpStatus.INTERNAL_SERVER_ERROR);
						}
					}
					else {
						returnCode = new ResponseEntity<String>("cannot find the recipe you are trying to add a review to.", HttpStatus.INTERNAL_SERVER_ERROR);
					}
		    	}
		    	else {
		    		returnCode = new ResponseEntity<String>("you must wait 30 seconds before being able to write/delete/edit another review.", HttpStatus.INTERNAL_SERVER_ERROR);
		    	}
		    }
		    else {
		    	returnCode = new ResponseEntity<String>("you are not logged in so you cannot create a review.", HttpStatus.UNAUTHORIZED);
		    }
		    utilityService = null;
			recipesObject = null;
			usersDAO = null;
			((ConfigurableApplicationContext)appContext).close();
		}
		else {
			returnCode = new ResponseEntity<String>("Fields must be filled out properly", HttpStatus.NOT_ACCEPTABLE);
		}
		return returnCode;
	}	
	
	/**
	 * @param principal An object holding authentication information of the current user.
	 * @param reviewId The unique identifier of the review to be removed.
	 * @param reviewRating The rating of the review to be removed, used to compute the new average.
	 * @param recipeName The name of the recipe with the review to be deleted.
	 * @return The recipe page displaying the recipe with the reviewd removed and if unsuccessful a message indicating that the review could not be deleted.
	 */
	public ResponseEntity<String> deleteRecipeReview(Principal principal, String reviewId, String reviewRating, String recipeName) {
		System.out.println("TESTING " + SecurityContextHolder.getContext().getAuthentication());
		ResponseEntity<String> returnCode = null;
		if(reviewId != null && reviewRating != null && recipeName != null) {
			LocalDateTime currentTime = Timestamp.from(Instant.now()).toLocalDateTime();
			ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
			RecipeDAOImpl recipesObject = (RecipeDAOImpl)appContext.getBean(RECIPE_DAO_BEAN_NAME);
			UsersDAOImpl usersDAO = (UsersDAOImpl) appContext.getBean(USER_DAO_BEAN_NAME);
			UtilityServiceImpl utilityService = new UtilityServiceImpl();
			Recipe recipeToUpdate = recipesObject.getRecipe(recipeName);
			Integer reviewIdValue = Integer.parseInt(reviewId);
		    if(recipeToUpdate != null) {
		    	RecipeReview reviewTomodify = recipesObject.getRecipeReview(reviewIdValue);
		    	if(reviewTomodify != null) {
		    		if(utilityService.doesUserOwnReview(principal, reviewTomodify.getUserNamePosted())) {
		    			String userName = principal.getName();
						LocalDateTime lastReviewedTime = usersDAO.getLastPostedReviewTime(userName);
						if(utilityService.canUserModify(lastReviewedTime, currentTime)) {
							int deleteCode = recipesObject.deleteRecipeReview(reviewIdValue);
							if(deleteCode == -1)
								returnCode = new ResponseEntity<String>("review deletion error", HttpStatus.INTERNAL_SERVER_ERROR);
							else if(deleteCode == 0)
								returnCode = new ResponseEntity<String>("no matching review", HttpStatus.INTERNAL_SERVER_ERROR);
							else {
								// modify the time that the user has last posted a review.
								int modifyLastReviewTimeCode = usersDAO.updateUserLastPostedReviewTime(currentTime, userName);
								if(modifyLastReviewTimeCode != 1) {
									// send an email to myself with the problem.
									GmailService emailService = new GmailService();
									String emailSubject = "AR: Delete Review Issue";
									String bodyText = "User: " + userName + " has deleted a review but the reviewPostedTime column is not able to be updated.";
									emailService.sendEmail(bodyText, utilityService.getAdminUserNames(), emailSubject);
									emailService = null;
								}
								Integer ratingValue = Integer.parseInt(reviewRating);
								int updateRatingCode = -1;
								Recipe updatedRecipe = recipesObject.getRecipe(recipeName);
								int updatedReviewQuantity = 0;
								
								if(ratingValue == 1) {
									int numOneStarReviews = updatedRecipe.getNumberOneStarReviews();
									if(numOneStarReviews > 0) {
										numOneStarReviews = numOneStarReviews - 1;
										updateRatingCode = recipesObject.updateRecipeOneStarRating(numOneStarReviews, recipeName);
										updatedReviewQuantity = numOneStarReviews;
									}
								}
								else if(ratingValue == 2) {
									int numTwoStarReviews = updatedRecipe.getNumberTwoStarReviews();
									if(numTwoStarReviews > 0) {
										numTwoStarReviews = numTwoStarReviews - 1;
										updateRatingCode = recipesObject.updateRecipeTwoStarRating(numTwoStarReviews, recipeName);
										updatedReviewQuantity = numTwoStarReviews;
									}
								}
								else if(ratingValue == 3) {
									int numThreeStarReviews = updatedRecipe.getNumberThreeStarReviews();
									if(numThreeStarReviews > 0) {
										numThreeStarReviews = numThreeStarReviews - 1;
										updateRatingCode = recipesObject.updateRecipeThreeStarRating(numThreeStarReviews, recipeName);
										updatedReviewQuantity = numThreeStarReviews;
									}
								}
								else if(ratingValue == 4) {
									int numFourStarReviews = updatedRecipe.getNumberFourStarReviews();
									if(numFourStarReviews > 0) {
										numFourStarReviews = numFourStarReviews - 1;
										updateRatingCode = recipesObject.updateRecipeFourStarRating(numFourStarReviews, recipeName);
										updatedReviewQuantity = numFourStarReviews;
									}
								}
								else if(ratingValue == 5) {
									int numFiveStarReviews = updatedRecipe.getNumberFiveStarReviews();
									if(numFiveStarReviews > 0) {
										numFiveStarReviews = numFiveStarReviews - 1;
										updateRatingCode = recipesObject.updateRecipeFiveStarRating(numFiveStarReviews, recipeName);
										updatedReviewQuantity = numFiveStarReviews;
									}
								}
								
								if(updateRatingCode == 1) {
									Recipe recipeObject = recipesObject.getRecipe(recipeName);
									
									if(recipeObject != null) {
										computeRecipeAverageRatingandTotalReviews(recipeObject);
										int totalReviews = recipeObject.getTotalNumberOfReviews();
										double averageRating = recipeObject.getAverageRating();
										
										if(totalReviews > 0) {
											returnCode = new ResponseEntity<String>(averageRating + "," + updatedReviewQuantity + "," + totalReviews, HttpStatus.OK);
										}
										else {
											returnCode = new ResponseEntity<String>(0 + "," + updatedReviewQuantity + "," + totalReviews, HttpStatus.OK);
										}
									}
									else {
										returnCode = new ResponseEntity<String>("could not get the new average rating and review values, refresh your page to see the changes.", HttpStatus.INTERNAL_SERVER_ERROR);
									}
								}
								else {
									returnCode = new ResponseEntity<String>("could not update the rating, refresh your page to see the changes.", HttpStatus.INTERNAL_SERVER_ERROR);
								}
								updatedRecipe = null;
							}
						}
						else {
			    			returnCode = new ResponseEntity<String>("you must wait 30 seconds after deleting/writing/editing a review to remove one.", HttpStatus.INTERNAL_SERVER_ERROR);
			    		}
		    		}
					else {
						returnCode = new ResponseEntity<String>("you must be the owner of the review to remove it.", HttpStatus.UNAUTHORIZED);
					}
		    	}
		    	else {
		    		returnCode = new ResponseEntity<String>("cannot get the review.", HttpStatus.INTERNAL_SERVER_ERROR);
		    	}
		    }
		    else {
		    	returnCode = new ResponseEntity<String>("cannot get the recipe.", HttpStatus.INTERNAL_SERVER_ERROR);
		    }
			recipeToUpdate = null;
			utilityService = null;
			usersDAO = null;
			recipesObject = null;
			((ConfigurableApplicationContext)appContext).close();
		}
		else {
			returnCode = new ResponseEntity<String>("invalid parameters", HttpStatus.NOT_ACCEPTABLE);
		}
		return returnCode;
	}
	
	/**
	 * @param principal An object holding the authentication information of the current user.
	 * @param recipeRating The modified rating of the review.
	 * @param reviewContent The modified review content.
	 * @param reviewId The unique identifier of the review to be modified.
	 * @param recipeName The name of the recipe with the review to be modified.
	 * @param redirectAttrs An object holding the attributes for when a redirection is done.
	 * @return The recipe page the review updated, if not updated then a message is displayed indicating the review could not be modified.
	 */
	public String updateRecipeReviewContent(Principal principal, String recipeRating, String reviewContent, String reviewId, String recipeName, RedirectAttributes redirectAttrs) {
		/*
		 * must modify the amount of "stars" in a review. for example, check what old review's rating to see if it is different from the newly entered one.
		 * if so you need to recompute the average rating for the recipe.
		 */
		String errorMessage = "invalid parameters";
		if(recipeRating != null && reviewContent != null && reviewId != null && recipeName != null) {
			LocalDateTime currentTime = Timestamp.from(Instant.now()).toLocalDateTime();
			ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
			RecipeDAOImpl recipesObject = (RecipeDAOImpl)appContext.getBean(RECIPE_DAO_BEAN_NAME);
			UsersDAOImpl usersDAO = (UsersDAOImpl) appContext.getBean(USER_DAO_BEAN_NAME);
			Recipe recipeToUpdate = recipesObject.getRecipe(recipeName);
			UtilityServiceImpl utilityService = new UtilityServiceImpl();
			Integer reviewIdValue = Integer.parseInt(reviewId);
			RecipeReview reviewTomodify = recipesObject.getRecipeReview(reviewIdValue);
			if(recipeToUpdate != null) {
				if(reviewTomodify != null) {
					int newRecipeRating = Integer.parseInt(recipeRating);
					// must check if the person modifying the review is the person that wrote the review, not necessarily the person that created the recipe.
					int oldRating = reviewTomodify.getReviewRating();
					if(utilityService.doesUserOwnReview(principal, reviewTomodify.getUserNamePosted())) {
						String userName = principal.getName(); 
						LocalDateTime lastReviewedTime = usersDAO.getLastPostedReviewTime(userName);
						if(utilityService.canUserModify(lastReviewedTime, currentTime)) {
							int recipeReviewUpdateCode = recipesObject.updateRecipeReview(newRecipeRating, reviewContent, currentTime, reviewIdValue);
							
							if(recipeReviewUpdateCode == 1) {
								int modifyLastReviewTimeCode = usersDAO.updateUserLastPostedReviewTime(currentTime, userName);
								if(modifyLastReviewTimeCode != 1) {
									// send an email to myself with the problem.
									GmailService emailService = new GmailService();
									String emailSubject = "AR: Edit Review Issue";
									String bodyText = "User: " + userName + " has edited a review but the reviewPostedTime column is not able to be updated.";
									emailService.sendEmail(bodyText, utilityService.getAdminUserNames(), emailSubject);
									emailService = null;
								}
	
								if(oldRating != newRecipeRating) {
									int updateReviewCountCode = -1;
									// must increment and decrement ratings.
									
									if(oldRating == 1) {
										updateReviewCountCode = recipesObject.updateRecipeOneStarRating(recipeToUpdate.getNumberOneStarReviews() - 1, recipeName);
									}
									else if(oldRating == 2) {
										updateReviewCountCode = recipesObject.updateRecipeTwoStarRating(recipeToUpdate.getNumberTwoStarReviews() - 1, recipeName);
									}
									else if(oldRating == 3) {
										updateReviewCountCode = recipesObject.updateRecipeThreeStarRating(recipeToUpdate.getNumberThreeStarReviews() - 1, recipeName);
									}
									else if(oldRating == 4) {
										updateReviewCountCode = recipesObject.updateRecipeFourStarRating(recipeToUpdate.getNumberFourStarReviews() - 1, recipeName);
									}
									else if(oldRating == 5) {
										updateReviewCountCode = recipesObject.updateRecipeFiveStarRating(recipeToUpdate.getNumberFiveStarReviews() - 1, recipeName);
									}
									
									if(newRecipeRating == 1) {
										updateReviewCountCode = recipesObject.updateRecipeOneStarRating(recipeToUpdate.getNumberOneStarReviews() + 1, recipeName);
									}
									else if(newRecipeRating == 2) {
										updateReviewCountCode = recipesObject.updateRecipeTwoStarRating(recipeToUpdate.getNumberTwoStarReviews() + 1, recipeName);
									}
									else if(newRecipeRating == 3) {
										updateReviewCountCode = recipesObject.updateRecipeThreeStarRating(recipeToUpdate.getNumberThreeStarReviews() + 1, recipeName);
									}
									else if(newRecipeRating == 4) {
										updateReviewCountCode = recipesObject.updateRecipeFourStarRating(recipeToUpdate.getNumberFourStarReviews() + 1, recipeName);
									}
									else if(newRecipeRating == 5) {
										updateReviewCountCode = recipesObject.updateRecipeFiveStarRating(recipeToUpdate.getNumberFiveStarReviews() + 1, recipeName);
									}
									if(updateReviewCountCode == 1) {
										recipesObject = null;
										usersDAO = null;
										((ConfigurableApplicationContext)appContext).close();
										redirectAttrs.addAttribute(RECIPE_NAME_PARAMETER, recipeName);
										return REDIRECT_TO_RECIPE;
									}
									else {
										errorMessage = "could not update the recipe review counter(s).";
									}
								}
								else {
									errorMessage = "review changed, but rating stayed the same.";
								}
							}		
							else {
								errorMessage = "could not update review";
							}
						}
						else {
							errorMessage = "you must wait 30 seconds before editing/adding/removing a review.";
						}
					}
					else {
						errorMessage = "you are not the poster so you cannot edit the review.";
					}
				}
				else {
					errorMessage = "cannot get the review you are attempting to edit.";
				}
			}
			else {
				errorMessage = "cannot get the recipe.";
			}		
			recipesObject = null;
			usersDAO = null;
			((ConfigurableApplicationContext)appContext).close();
		}
		redirectAttrs.addAttribute(RECIPE_NAME_PARAMETER, recipeName);
		redirectAttrs.addAttribute(MESSAGE_PARAMETER, errorMessage);
		return REDIRECT_TO_RECIPE;
	}

	/**
	 * Helper method to sort the reviews by time or rating.
	 * @param recipeReviews The list of reviews to sort.
	 * @param sortType An integer value indicating what type of sort needs to be done.
	 */
	public void sortRecipeReviews(List<RecipeReview> recipeReviews, int sortType) {
		switch(sortType) {
			case SORT_BY_MOST_RECENT_REVIEW_TIME:
				Collections.sort(recipeReviews, new Comparator<RecipeReview>() {
					public int compare(RecipeReview o1, RecipeReview o2) {
						// TODO Auto-generated method stub
						return o2.getReviewPostedTime().compareTo(o1.getReviewPostedTime());
					}
				});
				break;
			case SORT_BY_OLDEST_REVIEW_TIME:
				Collections.sort(recipeReviews, new Comparator<RecipeReview>() {
					public int compare(RecipeReview o1, RecipeReview o2) {
						// TODO Auto-generated method stub
						return o1.getReviewPostedTime().compareTo(o2.getReviewPostedTime());
					}
				});
				break;
			case SORT_BY_HIGHEST_REVIEWS:
				Collections.sort(recipeReviews, new Comparator<RecipeReview>() {
					public int compare(RecipeReview o1, RecipeReview o2) {
						// TODO Auto-generated method stub
						return Integer.compare(o2.getReviewRating(), o1.getReviewRating());
					}
				});
				break;
			case SORT_BY_LOWEST_REVIEWS:
				Collections.sort(recipeReviews, new Comparator<RecipeReview>() {
					public int compare(RecipeReview o1, RecipeReview o2) {
						// TODO Auto-generated method stub
						return Integer.compare(o1.getReviewRating(), o2.getReviewRating());
					}
				});
				break;
			default:
				break;
		}
	}
	
	
	/* 
	 * Recipe Section
	 * This will overlap with the recipe ingredients section because if the number
	 * of servings is changed all the recipe ingredients must be changed as well.
	*/
	
	/**
	 * @param principal An object holding authentication related information.
	 * @param model An object holding UI relevant information.
	 * @param errorMessage An optional message that notifies the user of potential errors.
	 * @return The list of all the recipes.
	 */
	public String showAllRecipes(Principal principal, ModelMap model, String errorMessage) {
		if(principal != null) {
			String userName = principal.getName();
			if(userName != null) model.addAttribute(LOGGED_IN_USER_NAME_PARAM, userName);
		}
		ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
		RecipeDAOImpl recipesObject = (RecipeDAOImpl)appContext.getBean(RECIPE_DAO_BEAN_NAME);
		
		List<Recipe> recipesList = recipesObject.getAllRecipes();
		
		if(recipesList != null) {
			computeRecipeAverageRatingandTotalReviews(recipesList);
			model.addAttribute("recipesList", recipesList);
		}
		recipesObject = null;
		((ConfigurableApplicationContext)appContext).close();
		
		if(errorMessage != null) {
			model.addAttribute("errorMessage", errorMessage);
		}
		return "showRecipes";
	}
	
	/**
	 * @param principal An object holding authentication related information.
	 * @param model An object holding UI information.
	 * @param errorMessage A message to notify the user of any potential error.
	 * @param sortType A value to determine how the reviews will be sorted.
	 * @param recipeName The name of the recipe.
	 * @return A jsp page with sections displaying a recipe's information.
	 */
	public String showSingleRecipe(Principal principal, ModelMap model, String errorMessage, String sortType, String recipeName) {
		ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
		RecipeDAOImpl recipesObject = (RecipeDAOImpl)appContext.getBean(RECIPE_DAO_BEAN_NAME);
		
		List<Ingredient> recipeIngredients = recipesObject.getListIngredients(recipeName);
		Recipe recipe = recipesObject.getRecipe(recipeName);
		
		// grab the directions if any.
		List<RecipeDirection> recipeDirections = recipesObject.getRecipeDirections(recipeName);
		
		// grab the reviews if any.
		List<RecipeReview> recipeReviews = recipesObject.getRecipeReviews(recipeName);
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(principal != null) {
			String userName = principal.getName();
			if(userName != null) model.addAttribute(LOGGED_IN_USER_NAME_PARAM, authentication.getName());
		}
		if(recipe != null)  {
			model.addAttribute("recipeObject", recipe);
			computeRecipeAverageRatingandTotalReviews(recipe);
			int totalReviews = recipe.getTotalNumberOfReviews();
			double averageRating = recipe.getAverageRating();
		
			model.addAttribute("totalReviews", totalReviews);
			model.addAttribute("numberFiveStarReviews", recipe.getNumberFiveStarReviews());
			model.addAttribute("numberFourStarReviews", recipe.getNumberFourStarReviews());
			model.addAttribute("numberThreeStarReviews", recipe.getNumberThreeStarReviews());
			model.addAttribute("numberTwoStarReviews", recipe.getNumberTwoStarReviews());
			model.addAttribute("numberOneStarReviews", recipe.getNumberOneStarReviews());
			
			if(totalReviews == 0)
				model.addAttribute("averageRating", 0);
			else {
				model.addAttribute("averageRating", averageRating);
			}
			
			String posterName = recipe.getUserOwner();
			if(posterName != null) model.addAttribute("userOwner", posterName);
			
			String prepTime = convertTimeToStringDisplay(recipe.getPrepTime());
			if(prepTime != null) model.addAttribute("prepTime", prepTime);
			String cookTime = convertTimeToStringDisplay(recipe.getCookTime());
			if(cookTime != null) model.addAttribute("cookTime", cookTime);
			String readyInTime = convertTimeToStringDisplay(recipe.getPrepTime() + recipe.getCookTime());
			if(readyInTime != null) model.addAttribute("readyTime", readyInTime);
			
			if(recipeIngredients != null)  {
				model.addAttribute("ingredientsList", recipeIngredients);
			}
			
			if(recipeDirections != null) {
				model.addAttribute("recipeDirections", recipeDirections);
			}
			if(recipeReviews != null) {
				if(sortType != null) {
					int sortTypeValue = Integer.parseInt(sortType);
					if(sortTypeValue == 1 || sortTypeValue == 2 || sortTypeValue == 3 || sortTypeValue == 4) {
						sortRecipeReviews(recipeReviews, sortTypeValue);
					}
				}
				model.addAttribute("recipeReviews", recipeReviews);
			}

			// load the image URL.
			String recipeImage = recipe.getImageURL();
			if(recipeImage != null &&recipeImage.length() > 6 ) {
				model.addAttribute("recipeImage", recipeImage);
			}
		}
		
		if(errorMessage != null) model.addAttribute("dbBoundError", errorMessage);
		
		recipesObject = null;
		((ConfigurableApplicationContext)appContext).close();
		return SHOW_SINGLE_RECIPE;
	}
	
	/**
	 * @param recipeToEdit The name of the recipe to edit.
	 * @param principal An object holding authentication information of the current user.
	 * @param model An object to hold UI information.
	 * @param redirectAttrs An object holding the attributes for when a redirection is done.
	 * @return The recipe edit page if able to retrieve the recipe, otherwise redirects the user to the show all recipes page.
	 */
	public String showEditRecipe(String recipeName, Principal principal, ModelMap model, RedirectAttributes redirectAttrs) {
		UtilityServiceImpl utilityService = new UtilityServiceImpl();
		String errorMessage = "you are not the owner of the recipe so you cannot edit it.";
	    if(utilityService.isOwner(principal, RECIPE_TYPE, recipeName)) {
	    	ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
	    	RecipeDAOImpl recipeDAO = (RecipeDAOImpl) appContext.getBean(RECIPE_DAO_BEAN_NAME);
			Recipe recipeToEdit = recipeDAO.getRecipe(recipeName); // at this point we know it exists, so no need for a null check.
			
			String formattedCookTime = convertTimeForModification(recipeToEdit.getCookTime());
			String formattedPrepTime = convertTimeForModification(recipeToEdit.getPrepTime());
			
			String cookTimeHour = "0";
			String cookTimeMinute = "0";
			String cookTimeSecond = "0";
			
			String prepTimeHour = "0";
			String prepTimeMinute = "0";
			String prepTimeSecond = "0";
			
			String[] cookTimeDelimited = formattedCookTime.split("/");
			if(cookTimeDelimited.length == 3) {
				cookTimeHour = cookTimeDelimited[0];
				cookTimeMinute = cookTimeDelimited[1];
				cookTimeSecond = cookTimeDelimited[2];
			}
			
			String[] prepTimeDelimited = formattedPrepTime.split("/");
			if(prepTimeDelimited.length == 3) {
				prepTimeHour = prepTimeDelimited[0];
				prepTimeMinute = prepTimeDelimited[1];
				prepTimeSecond = prepTimeDelimited[2];
			}
			
			model.addAttribute("recipeToEdit", recipeToEdit);
			model.addAttribute("cookTimeHour", cookTimeHour);
			model.addAttribute("cookTimeMinute", cookTimeMinute);
			model.addAttribute("cookTimeSecond", cookTimeSecond);
			model.addAttribute("prepTimeHour", prepTimeHour);
			model.addAttribute("prepTimeMinute", prepTimeMinute);
			model.addAttribute("prepTimeSecond", prepTimeSecond);
			model.addAttribute("loggedInName", principal.getName());
			
			recipeDAO = null;
			((ConfigurableApplicationContext)appContext).close();
			return EDIT_RECIPE_PAGE;
	    }
	    redirectAttrs.addAttribute(RECIPE_NAME_PARAMETER, recipeName);
	    redirectAttrs.addAttribute(MESSAGE_PARAMETER, errorMessage);
		return REDIRECT_TO_RECIPE;
	}
	
	/**
	 * @param principal An object holding authentication information of the current user.
	 * @param model An object holding UI information
	 * @param recipeName The name of the recipe to edit.
	 * @param prepTime The time to prep the recipe.
	 * @param cookTime The time to cook the recipe.
	 * @param dishType The type of dish (cuisine, salad, etc.).
	 * @param imageURL A link to a url of the recipe (optional).
	 * @param recipeDescription A brief description of the recipe.
	 * @param redirectAttrs An object holding the attributes for when a redirection is done.
	 * @return Displays the recipe that the user was attempting to edit with some notification message.
	 */
	public String processRecipeEdit(Principal principal, String recipeName, String prepTime, String cookTime, String dishType, 
		String imageURL, String recipeDescription, RedirectAttributes redirectAttrs) {
		UtilityServiceImpl utilityService = new UtilityServiceImpl();
		String errorMessage = "you are not the owner of the recipe so you cannot edit it.";
		LocalDateTime current_time = Timestamp.from(Instant.now()).toLocalDateTime();
	    if(utilityService.isOwner(principal, RECIPE_TYPE, recipeName)) {
	    	if(recipeName != null && prepTime != null && cookTime != null && dishType != null && imageURL != null && recipeDescription != null) {
	    		String[] prepTimeParsed = prepTime.split("/");
				String[] cookTimeParsed = cookTime.split("/");
				if(prepTimeParsed.length == 3 && cookTimeParsed.length == 3) {
					if(utilityService.validateRecipeName(recipeName) && utilityService.validateRecipeDescription(recipeDescription) && utilityService.validateRecipeType(dishType)) {
						ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
						RecipeDAOImpl recipesDAO = (RecipeDAOImpl) appContext.getBean(RECIPE_DAO_BEAN_NAME);
						UsersDAOImpl userDAO = (UsersDAOImpl) appContext.getBean(USER_DAO_BEAN_NAME);
						int prepTimeConverted = utilityService.convertEnteredTime(prepTimeParsed[0], prepTimeParsed[1], prepTimeParsed[2]);
						int cookTimeConverted = utilityService.convertEnteredTime(cookTimeParsed[0], cookTimeParsed[1], cookTimeParsed[2]);
						if(prepTimeConverted >= 0) {
							if(cookTimeConverted >= 0) {
								int recipeUpdateCode = recipesDAO.updateRecipeContents(prepTimeConverted, cookTimeConverted, dishType, imageURL, recipeDescription, recipeName);
								if(recipeUpdateCode == 1) {
									int updateLastPostedRecipeTimeCode = userDAO.updateUserLastPostedRecipeTime(current_time, principal.getName());
									if(updateLastPostedRecipeTimeCode == 1) {
										errorMessage = "successfully edited recipe!";
									}
									else {
										errorMessage = "updated the recipe, but could not update your last posted time.";
									}
								}
								else {
									errorMessage = "Could not update the recipe.";
								}
							}
							else {
								errorMessage = "The cook time could not be properly formatted.";
							}
						}
						else {
							errorMessage = "The prep time could not be properly formatted.";
						}
						recipesDAO = null;
						userDAO = null;
						((ConfigurableApplicationContext)appContext).close();
					}
					else {
						errorMessage = "The recipe name must be at least 4 characters and at most 60. The description must be at least four letters. The dish type must be 4 to 20 characters long.";
					}
				}
				else {
					errorMessage = "cannot retrieve the prep time or the cook time.";
				}
	    	}
	    	else {
	    		errorMessage = "missing parameter(s)";
	    	}
	    }
	    redirectAttrs.addAttribute(RECIPE_NAME_PARAMETER, recipeName);
	    redirectAttrs.addAttribute(MESSAGE_PARAMETER, errorMessage);
	    return REDIRECT_TO_RECIPE;
	}
	
	/**
	 * It is assumed something will not take over a day to cook and prepare for simplicity.
	 * @param timeValue The prep/cook time in seconds
	 * @return A formatted string for displaying the prep or cook time, a blank string indicates this should not be displayed./.
	 */
	public String convertTimeToStringDisplay(Integer timeValue) {
		if(timeValue != -1) {
			StringBuilder sb = new StringBuilder();
			if(timeValue > 0 && timeValue != null) {
				int seconds_in_hour = 3600;
				int seconds_in_minute = 60;
				if(timeValue / seconds_in_hour > 0) {
					sb.append(timeValue / seconds_in_hour + "h");
					timeValue = timeValue % seconds_in_hour;
				}
				if(timeValue / seconds_in_minute > 0) {
					if(sb.length() > 0) sb.append(" ");
					sb.append(timeValue / seconds_in_minute + "m");
					timeValue = timeValue % seconds_in_minute;
				}
				if(timeValue > 0) {
					if(sb.length() > 0) sb.append(" ");
					sb.append(timeValue + "s");
				}
			}
			return sb.toString();
		}
		else {
			return "";
		}
	}
	
	/**
	 * Return the stored cook and prep times to string to be used for editing.
	 * @param timeValue The cook or prep time to be converted.
	 * @return A string delimited representation of the timeValue.
	 */
	public String convertTimeForModification(Integer timeValue) {
		if(timeValue >= 0) {
			StringBuilder sb = new StringBuilder();
			int seconds_in_hour = 3600;
			int seconds_in_minute = 60;
			if(timeValue / seconds_in_hour > 0) {
				sb.append(timeValue / seconds_in_hour + "/");
				timeValue = timeValue % seconds_in_hour;
			}
			else {
				sb.append("0/");
			}
			if(timeValue / seconds_in_minute > 0) {
				sb.append(timeValue / seconds_in_minute + "/");
				timeValue = timeValue % seconds_in_minute;
			}
			else {
				sb.append("0/");
			}
			sb.append(timeValue + "");
			return sb.toString();
		}
		else {
			return "0/0/0";
		}
	}
	 

	/**
	 * @param datetimeobject A time object to be converted.
	 * @return A converted date time object 
	 */
	public String convertTimeObjectForDisplay(LocalDateTime datetimeobject) {
		StringBuilder formatted_date = new StringBuilder();
        DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("M/d/u");
        formatted_date.append(datetimeobject.format(dateformat));
        return formatted_date.toString();
	}
	
	/**
	 * helper method to get the average rating of all recipes.
	 * @param recipesList A list holding all the recipes.
	 */
	private void computeRecipeAverageRatingandTotalReviews(List<Recipe> recipesList) {
		for(Recipe recipe : recipesList) {
			computeRecipeAverageRatingandTotalReviews(recipe);
		}
	}
	
	/**
	 * Gets the average for the specified recipe object.
	 * @param recipe The recipe whose average rating will be computed and total reviews will be shown.
	 */
	private void computeRecipeAverageRatingandTotalReviews(Recipe recipe) {
		if(recipe != null) {
			Integer numberOfOneStarReviews = null;
			Integer numberOfTwoStarReviews = null;
			Integer numberOfThreeStarReviews = null;
			Integer numberOfFourStarReviews = null;
			Integer numberOfFiveStarReviews = null;

			int totalNumberOfRatings = 0;
			double averageRating = 0.0;
			int summation = 0;
			
			numberOfOneStarReviews = recipe.getNumberOneStarReviews();
			if(numberOfOneStarReviews != null) {
				totalNumberOfRatings += numberOfOneStarReviews;
				summation += (numberOfOneStarReviews * 1);
			}
			numberOfTwoStarReviews = recipe.getNumberTwoStarReviews();
			if(numberOfTwoStarReviews != null) {
				totalNumberOfRatings += numberOfTwoStarReviews;
				summation += (numberOfTwoStarReviews * 2);
			}
			numberOfThreeStarReviews = recipe.getNumberThreeStarReviews();
			if(numberOfThreeStarReviews != null) {
				totalNumberOfRatings += numberOfThreeStarReviews;
				summation += (numberOfThreeStarReviews * 3);
			}
			numberOfFourStarReviews = recipe.getNumberFourStarReviews();
			if(numberOfFourStarReviews != null) {
				totalNumberOfRatings += numberOfFourStarReviews;
				summation += (numberOfFourStarReviews * 4);
			}
			numberOfFiveStarReviews = recipe.getNumberFiveStarReviews();
			if(numberOfFiveStarReviews != null) {
				totalNumberOfRatings += numberOfFiveStarReviews;
				summation += (numberOfFiveStarReviews * 5);
			}
			
			if(totalNumberOfRatings > 0) {
				DecimalFormat ratingFormat = new DecimalFormat("#.#");
				averageRating = Double.valueOf(ratingFormat.format(((double)summation / (double)totalNumberOfRatings)));
				recipe.setAverageRating(averageRating);
				recipe.setTotalNumberOfReviews(totalNumberOfRatings);
			}
			else {
				recipe.setAverageRating(0);
				recipe.setTotalNumberOfReviews(0);
			}
		}
	}

	/**
	 * @param principal An object holding authentication information of the current user.
	 * @param recipeName The name of the recipe to delete.
	 * @param model An object holding UI information.
	 * @param redirectAttrs An object holding redirect parameter(s).
	 * @return Redirects the user to the show all recipes page if successful, otherwise redirects the user to the recipe page attempting to be deleted
	 * with an informative message.
	 */
	public String deleteRecipe(Principal principal, String recipeName, ModelMap model, RedirectAttributes redirectAttrs) {
		// TODO Auto-generated method stub
		// check if the user is logged in.
		// check if the user owns the recipe
		// if the return code is 1, then the recipe was deleted, and redirect back to the show all recipes page!@!
		// if anything else happens redirect to the recipe and print a message saying it wasn't deleted.
		// recipedaoimpl deleteRecipe to delete the recipe!
		UtilityServiceImpl utilityService = new UtilityServiceImpl();
		String returnMessage = "you are not the owner of this recipe so you cannot remove it.";
		if(utilityService.isOwner(principal, RECIPE_TYPE, recipeName)) {
			String userName = principal.getName();
			ApplicationContext appContext = new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
			RecipeDAOImpl recipesObject = (RecipeDAOImpl)appContext.getBean(RECIPE_DAO_BEAN_NAME);
			
			int recipeDeleteCode = recipesObject.removeList(recipeName, userName);
			recipesObject = null;
			((ConfigurableApplicationContext) appContext).close();
			
			if(recipeDeleteCode == 1) {
				returnMessage = "successfully deleted recipe!";
				redirectAttrs.addAttribute(MESSAGE_PARAMETER, returnMessage);
				return REDIRECT_TO_ALL_RECIPES;
			}
			else {
				returnMessage = "not able to delete the recipe.";
			}
		}
		redirectAttrs.addAttribute(MESSAGE_PARAMETER, returnMessage);
		redirectAttrs.addAttribute(RECIPE_NAME_PARAMETER, recipeName);
		return REDIRECT_TO_RECIPE;
	}
			
}
