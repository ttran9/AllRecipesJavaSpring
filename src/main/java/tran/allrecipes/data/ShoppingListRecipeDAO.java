package tran.allrecipes.data;

import java.util.List;

import tran.allrecipes.presentation.model.Ingredient;

/**
 * @author Todd
 * A DAO interface to enforce method implementation(s) shared between Recipe(s) and shopping lists.
 */
public interface ShoppingListRecipeDAO {

	/**
	 * Enforces a method implementation to add an ingredient to a recipe or a shopping list.
	 * @param ingredientName The ingredient name.
	 * @param ingredientWholeNumber The whole number quantity.
	 * @param ingredientNumerator The numerator quantity.
	 * @param ingredientDenominator The denominator quantity.
	 * @param ingredientUnit The ingredient's unit (oz., lbs., etc.).
	 * @param ingredientType The ingredient type (meat, vegetable, etc.).
	 * @param listName The name of the recipe or shopping list to add to.
	 * @return 1 if the ingredient was added to the recipe or the shopping list, another value would imply some unwanted result.
	 */
	public int addListIngredient(String ingredientName, int ingredientWholeNumber, int ingredientNumerator, int ingredientDenominator, String ingredientUnit, String ingredientType, String listName);
	
	/**
	 * @param listName The name of the list to be obtained.
	 * @return A list of ingredients given a list name, such as a list of ingredients for: a recipe, a shopping list, or an inventory list belonging to a user.
	 */
	public List<Ingredient> getListIngredients(String listName);
	
	/**
	 * This is a generic method to update a list ingredient for a recipe, a shopping list, or a pantry list.
	 * @param wholeNumberAmount The new whole number amount.
	 * @param numeratorAmount The new numerator amount.
	 * @param denominatorAmount The new denominator amount.
 	 * @param ingredientUnit The new unit (lbs, oz., etc.).
	 * @param ingredientType The new type (meat, vegetable, etc.).
	 * @param ingredientId The id of the new ingredient.
	 * @return 1 if the ingredient is updated, <= 0 means not updated.
	 */
	public int updateListIngredient(int wholeNumberAmount, int numeratorAmount, int denominatorAmount, String ingredientUnit, String ingredientType, int ingredientId);
	
	/**
	 * @param listName The name of the list to get the ingredient from: some recipe, some pantry list, or a shopping list.
	 * @param ingredientName The name of the ingredient.
	 * @return A populated ingredient, null otherwise.
	 */
	public Ingredient getSingleIngredient(String listName, String ingredientName);
}
