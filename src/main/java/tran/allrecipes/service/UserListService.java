package tran.allrecipes.service;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public interface UserListService {

	/**
	 * @param principal An object to hold user authentication information.
	 * @param model An object holding UI information.
	 * @param messageInformation A string to display the message when redirected.
	 * @param redirectAttrs An object to hold the redirect message.
	 * * @param listTypeValue A value indicating if shopping list or an inventory list is to be tested for.
	 * @return A user list of ingredients, such as a shopping list.
	 */
	public String showUserList(Principal principal, ModelMap model, String messageInformation, RedirectAttributes redirectAttrs, int listTypeValue);
	
	/**
	 * @param principal An object to hold user authentication information.
	 * @param listName The name of the list to add the ingredient to.
	 * @param ingredientName The name of the ingredient.
	 * @param ingredientUnit The units of the ingredient (lbs., oz., etc).
	 * @param ingredientWholeNumber The whole number portion of the ingredient.
	 * @param ingredientFractionQuantity A delimited string representing the fraction quantity of the ingredient.
	 * @param ingredientType The type of the ingredient (vegetable, meat, etc).
	 * @param redirectAttrs An object to hold a redirect message if necessary.
	 * @param listTypeValue A value indicating if shopping list or an inventory list is to be tested for.
	 * @return Returns an updated list with the ingredient added in, if not successful a message indicating so will be present.
	 */
	public String addUserListIngredient(Principal principal, String listName, String ingredientName, String ingredientUnit,
		String ingredientWholeNumber,String ingredientFractionQuantity, String ingredientType, RedirectAttributes redirectAttrs, int listTypeValue);
	
	/**
	 * @param principal An object to hold user authentication information.
	 * @param ingredientId A unique identifier of the ingredient.
	 * @param listName The name of the list to remove the ingredient from.
	 * @param listTypeValue A value indicating if shopping list or an inventory list is to be tested for.
	 * @return Returns an HttpStatus OK code if the ingredient was removed from the list, otherwise an HTTPStatus code indicating an error.
	 */
	public ResponseEntity<String> removeUserListIngredient(Principal principal, String ingredientId, String shoppingListName, int listTypeValue);

	/**
	 * 
	 * @param principal An object to hold user authentication information.
	 * @param ingredientName ingredientName The name of the ingredient.
	 * @param listName The name of the list to update the ingredient from.
	 * @param wholeNumberQuantity The whole number portion of the ingredient.
	 * @param fractionInput A delimited string representing the fraction quantity of the ingredient.
	 * @param ingredientUnit The units of the ingredient (lbs., oz., etc).
	 * @param ingredientType The type of the ingredient (vegetable, meat, etc).
	 * @param redirectAttrs An object to hold a redirect message if necessary.
	 * @param listTypeValue A value indicating if shopping list or an inventory list is to be tested for.
	 * @return Returns a list with the ingredient updated, if not successful a message indicating so will be present.
	 */
	public String updateUserListIngredient(Principal principal, String ingredientName, String shoppingListName, String wholeNumberQuantity, String fractionInput, String ingredientUnit, String ingredientType, RedirectAttributes redirectAttrs, int listTypeValue);
}