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

import tran.allrecipes.presentation.model.PantryIngredient;
import tran.allrecipes.service.UserListServiceImpl;

/**
 * @author Todd
 * A class to provide URL mappings to perform add/remove/update/subtraction on pantry list ingredients.
 */
@Controller
public class PantryListController {
	/** Arbitrary value for a pantry list. */
	private static final int PANTRY_LIST_TYPE = 450;
	/** URL mapping to display a pantry list. */
	private static final String SHOW_PANTRY_LIST_URL = "/showPantryList";
	/** URL mapping to add to a pantry list. */
	private static final String ADD_PANTRY_LIST_INGREDIENT_URL = "/addPantryListIngredient";
	/** URL mapping to remove an ingredient from a pantry list. */
	private static final String REMOVE_PANTRY_LIST_INGREDIENT_URL = "/removePantryListIngredient";
	/** URL mapping to update an ingredient amount, recalculates the ingredient threshold. */
	private static final String UPDATE_PANTRY_LIST_INGREDIENT_URL = "/updatePantryListIngredient";
	/** URL mapping to update how much of an ingredient is left. */
	private static final String UPDATE_PANTRY_LIST_INGREDIENT_AMOUNT_URL = "/updatePantryListIngredientAmount";
	/** URL mapping to add items in a pantry list to a shopping list. */
	private static final String TRANSFER_PANTRY_LIST_TO_SHOPPING_LIST_URL = "/processPantryList";
	/** Parameter to attach a message for a redirect. */
	private static final String MESSAGE_PARAM = "message";
	/** Parameter to specify the pantry list. */
	private static final String PANTRY_LIST_NAME_PARAM = "pantryListName";
	/** Parameter to specify the ingredient's ID. */
	private static final String PANTRY_LIST_INGREDIENT_ID_PARAM = "pantryListIngredientID";
	/** The add pantry ingredient form name. */
	private static final String ADD_PANTRY_INGREDIENT_FORM = "addPantryIngredientForm";
	/** The subtract pantry ingredient form name. */
	private static final String SUBTRACT_PANTRY_INGREDIENT_FORM = "subtractPantryIngredientForm";
	/** The update pantry ingredient form name. */
	private static final String UPDATE_PANTRY_INGREDIENT_FORM = "updatePantryIngredientForm";
	
	/** Initializes pantry ingredient data members from the add pantry ingredient form. */
	@ModelAttribute(ADD_PANTRY_INGREDIENT_FORM)
	public PantryIngredient getAddPantryIngredient() {
		return new PantryIngredient();
	}
	
	/** Initializes pantry ingredient data members from the subtract pantry ingredient form. */
	@ModelAttribute(SUBTRACT_PANTRY_INGREDIENT_FORM)
	public PantryIngredient getUpdatePantryIngredient() {
		return new PantryIngredient();
	}
	
	/** Initializes pantry ingredient data members from the update pantry ingredient form. */
	@ModelAttribute(UPDATE_PANTRY_INGREDIENT_FORM)
	public PantryIngredient getSubtractPantryIngredient() {
		return new PantryIngredient();
	}
	
	@RequestMapping(value=SHOW_PANTRY_LIST_URL, method={RequestMethod.GET, RequestMethod.POST})
	public String showPantryList(Principal principal, ModelMap model, @RequestParam(value=MESSAGE_PARAM, required=false) String message, RedirectAttributes redirectAttrs) {
		UserListServiceImpl pantryListService = new UserListServiceImpl();
		return pantryListService.showUserList(principal, model, message, redirectAttrs, PANTRY_LIST_TYPE);
	}
	
	@RequestMapping(value=ADD_PANTRY_LIST_INGREDIENT_URL, method=RequestMethod.POST) 
	//public String addPantryListIngredient(Principal principal, @ModelAttribute(ADD_PANTRY_INGREDIENT_FORM) PantryIngredient pantryIngredient, RedirectAttributes redirectAttrs) {
	public String addPantryListIngredient(Principal principal, @ModelAttribute PantryIngredient pantryIngredient, RedirectAttributes redirectAttrs) {
		UserListServiceImpl pantryListService = new UserListServiceImpl();
		return pantryListService.addUserListIngredient(principal, pantryIngredient.getIngredientListName(), pantryIngredient.getIngredientName(), pantryIngredient.getIngredientUnit(), pantryIngredient.getWholeNumber(), 
			pantryIngredient.getIngredientFractionQuantity(), pantryIngredient.getIngredientType(), redirectAttrs, PANTRY_LIST_TYPE);
	}
	
	@ResponseBody
	@RequestMapping(value=REMOVE_PANTRY_LIST_INGREDIENT_URL, method=RequestMethod.POST)
	public ResponseEntity<String> removePantryListIngredient(Principal principal, @RequestParam(value=PANTRY_LIST_INGREDIENT_ID_PARAM) String ingredientId, 
		@RequestParam(value=PANTRY_LIST_NAME_PARAM) String pantryListName) {
		UserListServiceImpl pantryListService = new UserListServiceImpl();
		return pantryListService.removeUserListIngredient(principal, ingredientId, pantryListName, PANTRY_LIST_TYPE);
	}
	
	@RequestMapping(value=UPDATE_PANTRY_LIST_INGREDIENT_AMOUNT_URL, method=RequestMethod.POST)
	//public String updatePantryListIngredientAmount(Principal principal, @ModelAttribute(SUBTRACT_PANTRY_INGREDIENT_FORM) PantryIngredient pantryIngredient, RedirectAttributes redirectAttrs) {
	public String updatePantryListIngredientAmount(Principal principal, @ModelAttribute PantryIngredient pantryIngredient, RedirectAttributes redirectAttrs) {
		UserListServiceImpl pantryListService = new UserListServiceImpl();
		return pantryListService.updateListIngredientAmount(principal, pantryIngredient.getWholeNumber(), pantryIngredient.getIngredientFractionQuantity(), pantryIngredient.getIngredientListName(), 
			pantryIngredient.getIngredientName(), redirectAttrs, PANTRY_LIST_TYPE);
	}
	
	@RequestMapping(value=UPDATE_PANTRY_LIST_INGREDIENT_URL, method=RequestMethod.POST)
	//public String updatePantryListIngredient(Principal principal, @ModelAttribute(UPDATE_PANTRY_INGREDIENT_FORM) PantryIngredient pantryIngredient, RedirectAttributes redirectAttrs) {
	public String updatePantryListIngredient(Principal principal, @ModelAttribute PantryIngredient pantryIngredient, RedirectAttributes redirectAttrs) {
		UserListServiceImpl pantryListService = new UserListServiceImpl();
		return pantryListService.updateUserListIngredient(principal, pantryIngredient.getIngredientName(), pantryIngredient.getIngredientListName(), pantryIngredient.getWholeNumber(), pantryIngredient.getIngredientFractionQuantity(), 
			pantryIngredient.getIngredientUnit(), pantryIngredient.getIngredientType(), redirectAttrs, PANTRY_LIST_TYPE);
	}
	
	@RequestMapping(value=TRANSFER_PANTRY_LIST_TO_SHOPPING_LIST_URL, method=RequestMethod.POST)
	public String pantryListToShoppingList(Principal principal, @RequestParam(value=PANTRY_LIST_NAME_PARAM) String listName, RedirectAttributes redirectAttrs) {
		UserListServiceImpl pantryListService = new UserListServiceImpl();
		return pantryListService.listToShoppingList(principal, listName, redirectAttrs, PANTRY_LIST_TYPE);
	}
}
