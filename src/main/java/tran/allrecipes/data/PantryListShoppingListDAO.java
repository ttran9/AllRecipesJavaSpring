package tran.allrecipes.data;

/**
 * @author Todd
 * A DAO interface to enforce method implementations only used by shopping and pantry lists.
 */
public interface PantryListShoppingListDAO {
	
	/**
	 * @param userName The owner of the list to retrieve.
	 * @return The name of a shopping or pantry list belonging to the specified user.
	 */
	public String getListName(String userName);
	
	/**
	 * @param listName The name of the list to create.
	 * @param nameOfListOwner The name of the user that owns the created list.
	 * @return 1 implies the desired list was created, -1 implies a database related error, > 1 implies some unwanted creation.
	 */
	public int addList(String listName, String nameOfListOwner);
	
	/**
	 * @param listName The name of the list to get the owner of.
	 * @return The user name that owns the list.
	 */
	public String getUserOwnerOfList(String listName);
}
