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
import tran.allrecipes.service.UserListServiceImpl;

/**
 * @author Todd
 * A class to handle URL mappings for operations on an authenticated user's shopping list.
 */
@Controller
public class ShoppingListController {
	/** URL mapping to show the shopping list. */
	private static final String SHOW_SHOPPING_LIST_URL = "/showShoppingList";
	/** URL mapping to add to the shopping list. */
	private static final String ADD_SHOPPING_LIST_INGREDIENT_URL = "/addShoppingListIngredient";
	/** URL mapping to remove from the shopping list. */
	private static final String REMOVE_SHOPPING_LIST_INGREDIENT_URL = "/removeShoppingListIngredient";
	/** URL mapping to update an ingredient from a shopping list. */
	private static final String UPDATE_SHOPPING_LIST_INGREDIENT_URL = "/updateShoppingListIngredient";
	/** URL mapping to transfer ingredients from a recipe to a shopping list. */
	private static final String TRANSFER_RECIPE_LIST_TO_SHOPPING_LIST_URL = "/processRecipeList";
	/** Parameter to display a notification message. */
	private static final String MESSAGE_PARAM = "message";
	/** Parameter to specify a shopping list. */
	private static final String SHOPPING_LIST_NAME_PARAM = "shoppingListName";
	/** Parameter to specify what ingredient is being removed. */
	private static final String SHOPPING_LIST_INGREDIENT_ID_PARAM = "shoppingListIngredientID";
	/** A parameter to specify the recipe that will have its contents added to a shopping list. */
	private static final String RECIPE_NAME_PARAM = "recipeName";
	/** Arbitrary value to be used to check if a shopping list is being checked for. */
	private static final int SHOPPING_LIST_TYPE = 350;
	/** Arbitrary value to be used to check if a recipe is being checked for. */
	private static final int RECIPE_TYPE = 200;
	/** The add shopping list ingredient form name. */
	private static final String ADD_SHOPPING_LIST_INGREDIENT_FORM = "addShoppingListIngredientForm";
	/** The update shopping list ingredient form name. */
	private static final String UPDATE_SHOPPING_LIST_INGREDIENT_FORM = "updateShoppingListIngredientForm";
	
	/** Initializes pantry ingredient data members from the add pantry ingredient form. */
	@ModelAttribute(ADD_SHOPPING_LIST_INGREDIENT_FORM)
	public Ingredient getAddShoppingIngredient() {
		return new Ingredient();
	}
	
	/** Initializes pantry ingredient data members from the add pantry ingredient form. */
	@ModelAttribute(UPDATE_SHOPPING_LIST_INGREDIENT_FORM)
	public Ingredient getUpdateShoppingIngredient() {
		return new Ingredient();
	}
	
	@RequestMapping(value=SHOW_SHOPPING_LIST_URL, method={RequestMethod.GET, RequestMethod.POST})
	public String showShoppingList(Principal principal, ModelMap model, @RequestParam(value=MESSAGE_PARAM, required=false) String message, RedirectAttributes redirectAttrs) {
		UserListServiceImpl shoppingListService = new UserListServiceImpl();
		return shoppingListService.showUserList(principal, model, message, redirectAttrs, SHOPPING_LIST_TYPE);
	}
	
	@RequestMapping(value=ADD_SHOPPING_LIST_INGREDIENT_URL, method=RequestMethod.POST) 
	//public String addShoppingListIngredient(Principal principal, @ModelAttribute(ADD_SHOPPING_LIST_INGREDIENT_FORM) Ingredient ingredient, RedirectAttributes redirectAttrs) {
	public String addShoppingListIngredient(Principal principal, @ModelAttribute Ingredient ingredient, RedirectAttributes redirectAttrs) {
		UserListServiceImpl shoppingListService = new UserListServiceImpl();
		return shoppingListService.addUserListIngredient(principal, ingredient.getIngredientListName(), ingredient.getIngredientName(), ingredient.getIngredientUnit(), ingredient.getWholeNumber(), ingredient.getIngredientFractionQuantity(), 
			ingredient.getIngredientType(), redirectAttrs, SHOPPING_LIST_TYPE);
	}
	
	@ResponseBody
	@RequestMapping(value=REMOVE_SHOPPING_LIST_INGREDIENT_URL, method=RequestMethod.POST)
	public ResponseEntity<String> removeShoppingListIngredient(Principal principal, @RequestParam(value=SHOPPING_LIST_INGREDIENT_ID_PARAM) String ingredientId, 
		@RequestParam(value=SHOPPING_LIST_NAME_PARAM) String shoppingListName) {
		UserListServiceImpl shoppingListService = new UserListServiceImpl();
		return shoppingListService.removeUserListIngredient(principal, ingredientId, shoppingListName, SHOPPING_LIST_TYPE);
	}
	
	@RequestMapping(value=UPDATE_SHOPPING_LIST_INGREDIENT_URL, method=RequestMethod.POST)
	//public String updateShoppingListIngredient(Principal principal, @ModelAttribute(UPDATE_SHOPPING_LIST_INGREDIENT_FORM) Ingredient ingredient, RedirectAttributes redirectAttrs) {
	public String updateShoppingListIngredient(Principal principal, @ModelAttribute Ingredient ingredient, RedirectAttributes redirectAttrs) {
		UserListServiceImpl shoppingListService = new UserListServiceImpl();
		return shoppingListService.updateUserListIngredient(principal, ingredient.getIngredientName(), ingredient.getIngredientListName(), ingredient.getWholeNumber(), ingredient.getIngredientFractionQuantity(), ingredient.getIngredientUnit(), 
			ingredient.getIngredientType(), redirectAttrs, SHOPPING_LIST_TYPE);
	}
	
	@RequestMapping(value=TRANSFER_RECIPE_LIST_TO_SHOPPING_LIST_URL, method=RequestMethod.POST)
	public String recipeListToShoppingList(Principal principal, @RequestParam(value=RECIPE_NAME_PARAM) String recipeName, RedirectAttributes redirectAttrs) {
		UserListServiceImpl shoppingListService = new UserListServiceImpl();
		return shoppingListService.listToShoppingList(principal, recipeName, redirectAttrs, RECIPE_TYPE);
	}
	
}
