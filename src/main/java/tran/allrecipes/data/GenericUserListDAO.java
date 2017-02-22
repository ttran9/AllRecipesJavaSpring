package tran.allrecipes.data;

/**
 * @author Todd
 * A user's list of ingredients must be able to retrieve an individual ingredient's ID and have remove operations
 * such as removing a single ingredient or all of the ingredients from a specified list.
 */
public interface GenericUserListDAO {
	/**
	 * @param listName The name of the list the ingredient is part of.
	 * @param ingredientName The name of the ingredient to retrieve.
	 * @return An integer value corresponding to the ingredient, null if the ingredient cannot be retrieved.
	 */
	public Integer getIngredientId(String listName, String ingredientName);
	
	/**
	 * @param ingredientId The unique identifier of the ingredient.
	 * @return 1 if the ingredient was removed properly, any other value indicates unwanted behavior.
	 */
	public int removeListIngredient(int ingredientId);
	
	/**
	 * @param listName The name of the list to have all ingredients removed
	 */
	public int removeAllListIngredients(String listName);	
}
