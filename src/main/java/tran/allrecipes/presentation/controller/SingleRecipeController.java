package tran.allrecipes.presentation.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tran.allrecipes.presentation.model.Ingredient;
import tran.allrecipes.presentation.model.RecipeDirection;
import tran.allrecipes.presentation.model.RecipeReview;
import tran.allrecipes.service.RecipeServiceImpl;

/**
 * @author Todd
 * A class to handle requests for a user viewing a specific recipe that was created by that same user.
 */
@Controller
public class SingleRecipeController {
	/** The URL mapping to show a single recipe. */
	private static final String SHOW_SINGLE_RECIPE_PAGE = "/showSingleRecipe";
	/** The URL mapping to add an ingredient. */
	private static final String ADD_RECIPE_INGREDIENT = "/addRecipeIngredient";
	/** The URL mapping to remove an ingredient. */
	private static final String DELETE_SINGLE_INGREDIENT = "/removeRecipeIngredient";
	/** The URL mapping to update a single ingredient. */
	private static final String UPDATE_SINGLE_INGREDIENT = "/updateSingleIngredient";
	/** The URL mapping to modify all ingredients. */
	private static final String MODIFY_RECIPE_INGREDIENTS = "modifyRecipeIngredients";
	/** The URL mapping to add a direction. */
	private static final String ADD_RECIPE_DIRECTION = "/addRecipeDirection";
	/** The URL mapping to remove a direction. */
	private static final String REMOVE_RECIPE_DIRECTION = "/removeRecipeDirection";
	/** The URL mapping to modify a direction. */
	private static final String UPDATE_RECIPE_DIRECTION = "/updateRecipeDirectionContent";
	/** The URL mapping to add a review. */
	private static final String ADD_RECIPE_REVIEW = "/addRecipeReview";
	/** The URL mapping to remove a review.  */
	private static final String REMOVE_RECIPE_REVIEW = "/removeRecipeReview";
	/** The URL mapping to update a review. */
	private static final String UPDATE_RECIPE_REVIEW = "/updateRecipeReviewContent";
	/** URL mapping to delete a specified recipe. */
	private static final String DELETE_RECIPE_URL = "/deleteRecipe";
	/** The parameter to take in the recipe name. */
	private static final String RECIPE_NAME_PARAM = "recipeName";
	/** The parameter to specify the message. */
	private static final String MESSAGE_PARAM = "message";
	/** The parameter to specify how reviews will be sorted.*/
	private static final String SORT_TYPE_PARAM = "sortType";
	/** The parameter to set the ingredient ID used as a unique identifier for a specific ingredient */
	private static final String INGREDIENT_ID_PARAM = "ingredientID";
	/** The name of the recipe add ingredient form. */
	private static final String RECIPE_ADD_INGREDIENT_FORM_NAME = "recipeIngredientAddForm";
	/** The name of the recipe update ingredient form. */
	private static final String RECIPE_UPDATE_INGREDIENT_FORM_NAME = "recipeIngredientUpdateForm";
	/** The name of the recipe add direction form. */
	private static final String RECIPE_ADD_DIRECTION_FORM_NAME = "recipeAddDirectionForm";
	/** The name of the recipe update direction form. */
	private static final String RECIPE_UPDATE_DIRECTION_FORM_NAME = "updateRecipeDirectionForm";
	/** The name of the recipe add review form. */
	private static final String RECIPE_ADD_REVIEW_FORM_NAME = "recipeAddReviewForm";
	/** The name of the recipe edit review form. */
	private static final String RECIPE_EDIT_REVIEW_FORM_NAME = "recipeEditReviewForm";
	/** The new servings parameter for modifying all the ingredients in the recipe. */
	private static final String NEW_SERVINGS_PARAM = "newServings";
	/** The old servings parameter for modifying all the ingredients in the recipe. */
	private static final String OLD_SERVINGS_PARAM = "oldServings";
	/** The parameter to specify what direction will be deleted.  */
	private static final String DIRECTION_ID_PARAM = "directionId";
	/** The parameter to specify what direction will be deleted. */
	private static final String DIRECTION_NUMBER_PARAM = "directionNumber";
	/** The parameter to uniquely identify a review. */
	private static final String REVIEW_ID_PARAM = "reviewId";
	/** The parameter to set the review's rating of the recipe. */
	private static final String REVIEW_RATING_PARAM = "reviewRating";
	
	/** Initializes recipe ingredient data members from the recipe add ingredient form. */
	@ModelAttribute(RECIPE_ADD_INGREDIENT_FORM_NAME) 
	public Ingredient getAddRecipeIngredient() {
		return new Ingredient();
	}
	/** Initializes recipe ingredient data members from the recipe update ingredient form. */
	@ModelAttribute(RECIPE_UPDATE_INGREDIENT_FORM_NAME) 
	public Ingredient getUpdateRecipeIngredient() {
		return new Ingredient();
	}
	/** Initializes recipe direction data members from the recipe add direction form. */
	@ModelAttribute(RECIPE_ADD_DIRECTION_FORM_NAME)
	public RecipeDirection getAddRecipeDirection() {
		return new RecipeDirection();
	}
	/** Initializes recipe direction data members from the recipe update direction form. */
	@ModelAttribute(RECIPE_UPDATE_DIRECTION_FORM_NAME)
	public RecipeDirection getUpdateRecipeDirection() {
		return new RecipeDirection();
	}
	/** Initializes recipe review data members from the recipe add review form. */
	@ModelAttribute(RECIPE_ADD_REVIEW_FORM_NAME)
	public RecipeReview getAddRecipeReview() {
		return new RecipeReview();
	}
	/** Initializes recipe review data members from the recipe edit review form. */
	@ModelAttribute(RECIPE_EDIT_REVIEW_FORM_NAME)
	public RecipeReview getEditRecipeReview() {
		return new RecipeReview();
	}
	
	@RequestMapping(value = SHOW_SINGLE_RECIPE_PAGE, method = RequestMethod.GET)
	public String showRecipe(Principal principal, ModelMap model, @RequestParam(value=RECIPE_NAME_PARAM, required=true) String recipeName, @RequestParam(value=MESSAGE_PARAM, required=false) String errorMessage, 
		@RequestParam(value=SORT_TYPE_PARAM, required=false) String sortType) {
		RecipeServiceImpl recipeService = new RecipeServiceImpl();
		return recipeService.showSingleRecipe(principal, model, errorMessage, sortType, recipeName);
	}
	
	@RequestMapping(value=UPDATE_SINGLE_INGREDIENT, method=RequestMethod.POST)
	public String updateSingleRecipeIngredient(@ModelAttribute(RECIPE_UPDATE_INGREDIENT_FORM_NAME) Ingredient ingredient, RedirectAttributes redirectAttrs, Principal principal) {
		RecipeServiceImpl recipeService = new RecipeServiceImpl();
		return recipeService.updateRecipeIngredientAmount(ingredient.getIngredientName(), ingredient.getIngredientListName(), ingredient.getWholeNumber(), ingredient.getIngredientFractionQuantity(), ingredient.getIngredientUnit(), ingredient.getIngredientType(), redirectAttrs, principal);
	}
	
	@ResponseBody
	@RequestMapping(value=DELETE_SINGLE_INGREDIENT, method = RequestMethod.POST)
	public ResponseEntity<String> deleteRecipeIngredient(Principal principal, @RequestParam(value=INGREDIENT_ID_PARAM) String ingredientId, 
		@RequestParam(value=RECIPE_NAME_PARAM) String recipeName) {
		RecipeServiceImpl recipeService = new RecipeServiceImpl();
		return recipeService.deleteRecipeIngredient(principal, ingredientId, recipeName);
	}
	
	@RequestMapping(value=MODIFY_RECIPE_INGREDIENTS, method=RequestMethod.POST)
	public String modifyRecipeIngredients(Principal principal, @RequestParam(value=NEW_SERVINGS_PARAM) String newServings, @RequestParam(value=RECIPE_NAME_PARAM) String recipeName, 
	@RequestParam(value=OLD_SERVINGS_PARAM) String oldServings, RedirectAttributes redirectAttrs) {
		RecipeServiceImpl recipeService = new RecipeServiceImpl();
		return recipeService.modifyRecipeIngredients(principal, newServings, recipeName, oldServings, redirectAttrs);
	}
		
	@ResponseBody
	@RequestMapping(value=ADD_RECIPE_INGREDIENT, method = RequestMethod.POST)
	public ResponseEntity<String> addRecipeIngredient(Principal principal, @ModelAttribute(RECIPE_ADD_INGREDIENT_FORM_NAME) Ingredient ingredient) {
		RecipeServiceImpl recipeService = new RecipeServiceImpl();
		return recipeService.addRecipeIngredient(principal, ingredient.getIngredientListName(), ingredient.getIngredientName(), ingredient.getIngredientUnit(), ingredient.getWholeNumber(), ingredient.getIngredientFractionQuantity(), ingredient.getIngredientType());
	}
	
	@ResponseBody
	@RequestMapping(value=ADD_RECIPE_DIRECTION, method = RequestMethod.POST)
	public ResponseEntity<String> addRecipeDirection(Principal principal, @ModelAttribute(RECIPE_ADD_DIRECTION_FORM_NAME) RecipeDirection recipeDirection) {
		RecipeServiceImpl recipeService = new RecipeServiceImpl();
		return recipeService.addRecipeDirection(principal, recipeDirection.getRecipeName(), recipeDirection.getDirection());
	}
	
	@RequestMapping(value=REMOVE_RECIPE_DIRECTION, method = RequestMethod.GET)
	public String deleteRecipeDirection(Principal principal, @RequestParam(value=DIRECTION_ID_PARAM) String directionId, @RequestParam(value=RECIPE_NAME_PARAM) String recipeName, 
		@RequestParam(value=DIRECTION_NUMBER_PARAM) String directionNumber, RedirectAttributes redirectAttrs) {
		RecipeServiceImpl recipeService = new RecipeServiceImpl();
		return recipeService.deleteRecipeDirection(principal, directionId, recipeName, directionNumber, redirectAttrs);
	}
	
	@RequestMapping(value=UPDATE_RECIPE_DIRECTION, method = RequestMethod.POST)
	public String updateRecipeDirectionContent(Principal principal, RedirectAttributes redirectAttrs, @ModelAttribute(RECIPE_UPDATE_DIRECTION_FORM_NAME) RecipeDirection recipeDirection) {
		RecipeServiceImpl recipeService = new RecipeServiceImpl();
		return recipeService.updateRecipeDirectionContent(principal, recipeDirection.getDirection(), String.valueOf(recipeDirection.getDirectionNumber()), recipeDirection.getRecipeName(), redirectAttrs);
	}
	
	@ResponseBody
	@RequestMapping(value=ADD_RECIPE_REVIEW, method = RequestMethod.POST)
	public ResponseEntity<String> addRecipeReview(Principal principal, @ModelAttribute(RECIPE_ADD_REVIEW_FORM_NAME) RecipeReview recipeReview) {
		RecipeServiceImpl recipeService = new RecipeServiceImpl();
		return recipeService.addRecipeReview(principal, recipeReview.getReviewContent(), String.valueOf(recipeReview.getReviewRating()), recipeReview.getReviewTitle(), recipeReview.getUserNamePosted(), recipeReview.getRecipeName());
	}
	
	@ResponseBody
	@RequestMapping(value=REMOVE_RECIPE_REVIEW, method = RequestMethod.GET)
	public ResponseEntity<String> deleteRecipeReview(Principal principal, @RequestParam(value=REVIEW_ID_PARAM) String reviewId, @RequestParam(value=REVIEW_RATING_PARAM) String reviewRating,
		@RequestParam(value=RECIPE_NAME_PARAM) String recipeName) {
		RecipeServiceImpl recipeService = new RecipeServiceImpl();
		return recipeService.deleteRecipeReview(principal, reviewId, reviewRating, recipeName);
	}
	
	@RequestMapping(value=UPDATE_RECIPE_REVIEW, method = RequestMethod.POST)
	public String updateRecipeReviewContent(Principal principal, @ModelAttribute(value=RECIPE_EDIT_REVIEW_FORM_NAME) RecipeReview recipeReview, RedirectAttributes redirectAttrs) {
		RecipeServiceImpl recipeService = new RecipeServiceImpl();
		return recipeService.updateRecipeReviewContent(principal, String.valueOf(recipeReview.getReviewRating()), recipeReview.getReviewContent(), String.valueOf(recipeReview.getReviewId()), recipeReview.getRecipeName(), redirectAttrs);
	}
	
	@RequestMapping(value=DELETE_RECIPE_URL, method=RequestMethod.GET)
	public String deleteRecipeMapper(Principal principal, @RequestParam(value=RECIPE_NAME_PARAM) String recipeName, ModelMap model, RedirectAttributes redirectAttrs) {
		RecipeServiceImpl recipeService = new RecipeServiceImpl();
		return recipeService.deleteRecipe(principal, recipeName, model, redirectAttrs);
	}
}
