package tran.allrecipes.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.fraction.Fraction;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tran.allrecipes.data.PantryListDAOImpl;
import tran.allrecipes.data.RecipeDAOImpl;
import tran.allrecipes.data.ShoppingListDAOImpl;
import tran.allrecipes.presentation.model.Ingredient;
import tran.allrecipes.presentation.model.PantryIngredient;

/**
 * @author Todd
 * A class to handle logical operations on a user's shopping list and pantry list and the corresponding ingredients.
 */
@Service
public class UserListServiceImpl implements UserListService {
	/** The location of the data source file. */
	private static final String DATABASE_SOURCE_FILE = "database/Datasource.xml";
	/** Name of the shopping list DAO bean. */
	private static final String SHOPPING_LIST_DAO_BEAN_NAME = "ShoppingListDAO";
	/** Name of the recipe DAO bean. */
	private static final String RECIPE_DAO_BEAN_NAME = "RecipesDAO";
	/** Name of the recipe DAO bean. */
	private static final String PANTRY_DAO_BEAN_NAME = "PantryListDAO";
	/** Arbitrary value to be used to check if a recipe is being checked for. */
	private static final int RECIPE_TYPE = 200;
	/** Arbitrary value to be used to check if a shopping list is being checked for. */
	private static final int SHOPPING_LIST_TYPE = 350;
	/** Arbitrary value to be used to check if a shopping list is being checked for. */
	private static final int PANTRY_LIST_TYPE = 450;
	/** String used to redirect to the shopping list. */
	private static final String REDIRECT_TO_SHOPPING_LIST = "redirect:/showShoppingList";
	/** String used to redirect to the shopping list. */
	private static final String REDIRECT_TO_PANTRY_LIST = "redirect:/showPantryList";
	/** redirects to the login page. */
	private static final String REDIRECT_TO_LOGIN = "redirect:/signin";
	/** redirect back to the single recipe. */
	private static final String REDIRECT_TO_RECIPE = "redirect:/showSingleRecipe";
	/** redirect to home. */
	private static final String REDIRECT_TO_HOME = "redirect:/";
	/** recipe name parameter. */
	private static final String RECIPE_NAME_PARAM = "recipeName";
	/** message parameter. */
	private static final String MESSAGE_PARAM = "message";
	/** The pantry list page name. */
	private static final String PANTRY_LIST_PAGE = "showPantryList";
	/** The shopping list page name. */
	private static final String SHOPPING_LIST_PAGE = "showShoppingList";
	/** The threshold numerator value. */
	private static final int THRESHOLD_NUMERATOR = 1;
	/** The threshold denominator value. */
	private static final int THRESHOLD_DENOMINATOR = 5;
	
	public UserListServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param principal An object holding authentication information of the current user.
	 * @param model An object holding UI information.
	 * @param messageInformation An informative message (when applicable) when the user is displayed his/her shopping list.
	 * @param redirectAttrs An object holding the attributes for when a redirection is done.
	 * @param listTypeValue A value indicating if shopping list or an inventory list is to be tested for.
	 * @return Displays the shopping list if a user is logged in and authenticated.
	 */
	public String showUserList(Principal principal, ModelMap model, String messageInformation, RedirectAttributes redirectAttrs, int listTypeValue) {
		String errorMessage = null;
	    if(principal != null) {
			String userName = principal.getName();
			if(userName != null) {
				ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
				ShoppingListDAOImpl shoppingListDAO = (ShoppingListDAOImpl) appContext.getBean(SHOPPING_LIST_DAO_BEAN_NAME);
				PantryListDAOImpl pantryListDAO = (PantryListDAOImpl) appContext.getBean(PANTRY_DAO_BEAN_NAME);
				
				String userListName = null;
				if(listTypeValue == SHOPPING_LIST_TYPE) {
					userListName = shoppingListDAO.getListName(userName);
				}
				else if(listTypeValue == PANTRY_LIST_TYPE) {
					userListName = pantryListDAO.getListName(userName);
				}
				else {
					/**
					 * unexpected case, print a special unique debug message.
					 */
					System.out.println("user selected improper list type and also has no pantry or shopping list while logged in.\nJust a debugging statement.");
					errorMessage = "improper list type selected.";
					model.addAttribute(MESSAGE_PARAM, errorMessage);
					shoppingListDAO = null;
					pantryListDAO = null;
					((ConfigurableApplicationContext)appContext).close();
					return REDIRECT_TO_HOME;
				}
				if(userListName != null) {
					if(listTypeValue == SHOPPING_LIST_TYPE) {
						List<Ingredient> listIngredients = null;
						listIngredients = shoppingListDAO.getListIngredients(userListName);
						Map<String, LinkedList<Ingredient>> subListsMap = new HashMap<String, LinkedList<Ingredient>>();
						if(listIngredients != null) {
							for(Ingredient ingredient : listIngredients) {
								if(subListsMap.containsKey(ingredient.getIngredientType())) {
									subListsMap.get(ingredient.getIngredientType()).add(ingredient);
								}
								else { 
									// first time you've seen the unit. create a new list and then add to it.
									subListsMap.put(ingredient.getIngredientType(), new LinkedList<Ingredient>()); 
									subListsMap.get(ingredient.getIngredientType()).add(ingredient);
								}
							}
							if(subListsMap.size() > 0) {
								model.addAttribute("userList", subListsMap);
							}
						}
					}
					else if(listTypeValue == PANTRY_LIST_TYPE) {
						List<PantryIngredient> pantryIngredients = null;
						pantryIngredients = pantryListDAO.getListIngredients(userListName);
						Map<String, LinkedList<PantryIngredient>> subListsMap = new HashMap<String, LinkedList<PantryIngredient>>();
						if(pantryIngredients != null) {
							for(PantryIngredient pantryIngredient : pantryIngredients) {
								if(subListsMap.containsKey(pantryIngredient.getIngredientType())) {
									subListsMap.get(pantryIngredient.getIngredientType()).add(pantryIngredient);
								}
								else { 
									// first time you've seen the unit. create a new list and then add to it.
									subListsMap.put(pantryIngredient.getIngredientType(), new LinkedList<PantryIngredient>()); 
									subListsMap.get(pantryIngredient.getIngredientType()).add(pantryIngredient);
								}
							}
							if(subListsMap.size() > 0) {
								model.addAttribute("userList", subListsMap);
							}
						}
					}
					model.addAttribute("userListName", userListName);
					if(messageInformation != null) {
						if(messageInformation.length() > 0) {
							model.addAttribute(MESSAGE_PARAM, messageInformation);
						}
					}
				}
				else {
					if(listTypeValue == SHOPPING_LIST_TYPE) {
						errorMessage = "could not retrieve your shopping list, if this continues, contact an admin.";
					}
					else if(listTypeValue == PANTRY_LIST_TYPE) {
						errorMessage = "could not retrieve your pantry list, if this continues, contact an admin.";
					}
					model.addAttribute(MESSAGE_PARAM, errorMessage);
				}
				model.addAttribute("loggedInName", userName);
				shoppingListDAO = null;
				pantryListDAO = null;
				((ConfigurableApplicationContext)appContext).close();
				if(listTypeValue == SHOPPING_LIST_TYPE) {
					return SHOPPING_LIST_PAGE;
				}
				else if(listTypeValue == PANTRY_LIST_TYPE) {
					return PANTRY_LIST_PAGE;
				}
			}
		}
	    if(listTypeValue == SHOPPING_LIST_TYPE) {
	    	errorMessage = "you cannot view your shopping list unless you are logged in.";
	    }
	    else if(listTypeValue == PANTRY_LIST_TYPE) {
	    	errorMessage = "you cannot view your pantry list unless you are logged in.";
	    }
	    else {
	    	errorMessage = "unknown list typre quest";
	    }
	    redirectAttrs.addAttribute(MESSAGE_PARAM, errorMessage);
	    return REDIRECT_TO_LOGIN;
	}
	
	/**
	 * @param PantryListDAOImpl An object to access the PantryListIngredients table.
	 * @param listName The name of the pantry list.
	 * @return Returns a list of ingredients for processing.
	 */
	private List<Ingredient> getListIngredients(PantryListDAOImpl pantryListDAO, String listName) {
		// unused for now.
		List<Ingredient> listIngredients = null;
		List<PantryIngredient> tempList = pantryListDAO.getListIngredients(listName);
		if(tempList != null) {
			listIngredients = new ArrayList<Ingredient>();
			for(PantryIngredient pantryIngredient : tempList) {
				if(pantryIngredient == null) {
					listIngredients = null;
					break;
				}
				listIngredients.add(new Ingredient(pantryIngredient.getIngredientName(), pantryIngredient.getWholeNumber(), pantryIngredient.getNumerator(), pantryIngredient.getDenominator(), pantryIngredient.getIngredientUnit(), 
						pantryIngredient.getIngredientID(), pantryIngredient.getIngredientType(), pantryIngredient.getDisplayType()));
			}
		}
		return listIngredients;
	}
	
	/**
	 * @param principal An object holding authentication information of the current user.
	 * @param ;istName The name of the list (shopping or pantry) of the logged in user.
	 * @param ingredientName The name of the ingredient to be added.
	 * @param ingredientUnit The unit (ml, L., etc of this ingredient).
	 * @param ingredientWholeNumber The whole number portion of the ingredient.
	 * @param ingredientFractionQuantity The numerator and denominator delimited.
	 * @param ingredientType The type of the ingredient (meat, vegetable, etc).
	 * @param redirectAttrs An object holding the attributes for when a redirection is done.
	 * @param listTypeValue A value indicating if shopping list or an inventory list is to be tested for.
	 * @return The user's shopping list and a message if there was an error adding the ingredient.
	 */
	public String addUserListIngredient(Principal principal, String listName, String ingredientName, String ingredientUnit,
		String ingredientWholeNumber,String ingredientFractionQuantity, String ingredientType, RedirectAttributes redirectAttrs, int listTypeValue) {
		if(!(listTypeValue == PANTRY_LIST_TYPE || listTypeValue == SHOPPING_LIST_TYPE)) {
			redirectAttrs.addAttribute(MESSAGE_PARAM, "you have requested for an incorrect list type to add to.");
			return REDIRECT_TO_HOME;
		}
		String returnMessage = "all required parameters are not provided.";
		if(listName != null && ingredientName != null && ingredientUnit != null && ingredientWholeNumber != null && ingredientFractionQuantity != null 
			&& ingredientType != null) {
			UtilityServiceImpl utilityService = new UtilityServiceImpl();
			int listType = listTypeValue;
			if(utilityService.isOwner(principal, listType, listName)) {
				ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
				ShoppingListDAOImpl shoppingListDAO = (ShoppingListDAOImpl) appContext.getBean(SHOPPING_LIST_DAO_BEAN_NAME);
				PantryListDAOImpl pantryListDAO = (PantryListDAOImpl) appContext.getBean(PANTRY_DAO_BEAN_NAME);
				
				String[] ingredientFractionValue = ingredientFractionQuantity.split("/");
				if(ingredientFractionValue.length == 2) {
					if(ingredientFractionValue[0] != null && ingredientFractionValue[1] != null) {
						int wholeNumber = Integer.parseInt(ingredientWholeNumber);
						int numeratorValue = Integer.parseInt(ingredientFractionValue[0]);
						int denominatorValue = Integer.parseInt(ingredientFractionValue[1]);
						
						String validateFractionInput = utilityService.validateUserInput(wholeNumber, numeratorValue, denominatorValue);
						if(!validateFractionInput.equals("")) {
							returnMessage = validateFractionInput;
						}
						else {
							// before inserting make sure the one case of a whole number without a fraction is accounted for.
							if(wholeNumber > 0 && (numeratorValue == 1 && denominatorValue == 1)) {
								numeratorValue = 0;
							}
							else if(wholeNumber == 0 &&(numeratorValue == 1 && denominatorValue == 1)) {
								wholeNumber = 1;
								numeratorValue = 0;
							}
							
							int addIngredientCode = -1;
							
							if(listTypeValue == SHOPPING_LIST_TYPE) {
								addIngredientCode = shoppingListDAO.addListIngredient(ingredientName, wholeNumber, numeratorValue, denominatorValue, ingredientUnit, ingredientType, listName);
							}
							else if(listTypeValue == PANTRY_LIST_TYPE) {
								Fraction thresholdFraction = getThresholdValues(wholeNumber, numeratorValue, denominatorValue);
								int ingredientThresholdWholeNumber = thresholdFraction.getNumerator() / thresholdFraction.getDenominator();
								int ingredientThresholdNumerator = thresholdFraction.getNumerator() % thresholdFraction.getDenominator();
								int ingredientThresholdDenominator = thresholdFraction.getDenominator();
								addIngredientCode = pantryListDAO.addListIngredient(ingredientName, wholeNumber, numeratorValue, denominatorValue, ingredientThresholdWholeNumber, ingredientThresholdNumerator, ingredientThresholdDenominator, ingredientUnit, ingredientType, listName, true, false);
							}
							if(addIngredientCode == 1) {
								shoppingListDAO = null;
								pantryListDAO = null;
								((ConfigurableApplicationContext)appContext).close();
								if(listTypeValue == SHOPPING_LIST_TYPE) {
									return REDIRECT_TO_SHOPPING_LIST;
								}
								else
									return REDIRECT_TO_PANTRY_LIST;
							}
							else {
								returnMessage = "cannot add the ingredient: " + ingredientName;
							}
						}
					}
					else {
						returnMessage = "cannot retrieve the fraction values.";
					}
				}
				else {
					returnMessage = "The fraction field is not in fraction format.";
				}
				shoppingListDAO = null;
				pantryListDAO = null;
				((ConfigurableApplicationContext)appContext).close();
			}
			else {
				if(listTypeValue == SHOPPING_LIST_TYPE) {
					returnMessage = "you are not the owner of this shopping list.";
				}
				else if(listTypeValue == PANTRY_LIST_TYPE) { 
					returnMessage = "you are not the owner of this pantry list.";
				}
			}
			utilityService = null;
		}
		redirectAttrs.addAttribute(MESSAGE_PARAM, returnMessage);
		if(listTypeValue == SHOPPING_LIST_TYPE) {
			return REDIRECT_TO_SHOPPING_LIST;
		}
		return REDIRECT_TO_PANTRY_LIST;
	}
	
	/**
	 * @param wholeNumber The whole number to compute the threshold fraction.
	 * @param numerator The numerator to compute the threshold fraction.
	 * @param denominator The denominator to compute the threshold fraction.
	 * @return A newly computed fraction using a static threshold value assigned by the class.
	 */
	public Fraction getThresholdValues(int wholeNumber, int numerator, int denominator) {
		// the input is already checked for certain fraction cases such as having a numerator greater than a denominator.
		// however a whole number of zero and a proper fraction such as (3/4) is not tested for.
		
		Fraction inputFraction = null;
		Fraction thresholdFraction = new Fraction(THRESHOLD_NUMERATOR, THRESHOLD_DENOMINATOR);
		
		if(wholeNumber == 0) {
			inputFraction = new Fraction(numerator, denominator);
		}
		else {
			inputFraction = new Fraction(((denominator * wholeNumber) + numerator), denominator);
		}
		
		return inputFraction.multiply(thresholdFraction);
	}
	
	
	
	/**
	 * @param principal An object holding authentication information of the current user.
	 * @param ingredientId A unique identifier of the ingredient to be removed.
	 * @param listName The name of the shopping or pantry list to remove the ingredient from.
	 * @param listTypeValue A value indicating if shopping list or an inventory list is to be tested for.
	 * @return A message indicating the ingredient could not be removed, or no message if the operation was successful.
	 */
	public ResponseEntity<String> removeUserListIngredient(Principal principal, String ingredientId, String listName, int listTypeValue) {
		ResponseEntity<String> responseCode = null;
		if (ingredientId != null && listName != null) {
			if(!(listTypeValue == PANTRY_LIST_TYPE || listTypeValue == SHOPPING_LIST_TYPE)) {
				responseCode = new ResponseEntity<String>("you have not requested to remove from a shopping or pantry list.", HttpStatus.NOT_ACCEPTABLE);
			}
			else {
				UtilityServiceImpl utilityService = new UtilityServiceImpl();
				if(utilityService.isOwner(principal, listTypeValue, listName)) {
					int ingredientIdValue = Integer.parseInt(ingredientId);
					ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
					ShoppingListDAOImpl shoppingListDAO = (ShoppingListDAOImpl) appContext.getBean(SHOPPING_LIST_DAO_BEAN_NAME);
					PantryListDAOImpl pantryListDAO = (PantryListDAOImpl) appContext.getBean(PANTRY_DAO_BEAN_NAME);
					int deleteCode = -1;
					if(listTypeValue == SHOPPING_LIST_TYPE) {
						deleteCode = shoppingListDAO.removeListIngredient(ingredientIdValue);
					}
					else if(listTypeValue == PANTRY_LIST_TYPE) {
						deleteCode = pantryListDAO.removeListIngredient(ingredientIdValue);
					}
					if(deleteCode == -1)
						responseCode = new ResponseEntity<String>("ingredient deletion error", HttpStatus.INTERNAL_SERVER_ERROR);
					else if(deleteCode == 0)
						responseCode = new ResponseEntity<String>("no matching ingredient", HttpStatus.INTERNAL_SERVER_ERROR);
					else
						responseCode = new ResponseEntity<String>(HttpStatus.OK);
					pantryListDAO = null;
					shoppingListDAO = null;
					((ConfigurableApplicationContext)appContext).close();
				}
				else {
					responseCode = new ResponseEntity<String>("you are not the owner of this shopping list.", HttpStatus.UNAUTHORIZED);
				}
			}
		}
		else {
			responseCode = new ResponseEntity<String>("all required parameters are not provided.", HttpStatus.NOT_ACCEPTABLE);
		}
		return responseCode;
	}
	
	/**
	 * This method specifies a new amount of an ingredient in a pantry list, the use case is when the user wants to update how much
	 * is in the pantry list, for subtracting an ingredient view the method updateListIngredientAmount.
	 * @param principal An object holding authentication information of the current user.
	 * @param ingredientName The name of the ingredient to be updated.
	 * @param listName The name of the shopping or pantry list whose ingredient will be updated.
	 * @param wholeNumberQuantity The whole number portion of the ingredient quantity.
	 * @param fractionInput A numerator and denominator delimited string.
	 * @param ingredientUnit The units of the ingredient (ml, lbs., etc.).
	 * @param ingredientType The type of the ingredient (meat, vegetable, etc.).
	 * @param redirectAttrs An object holding the attributes for when a redirection is done.
	 * @param listTypeValue A value indicating if shopping list or an inventory list is to be tested for.
	 * @return The shopping or pantry list with the updated contents if successful, and if unsuccessful a message indicating that the ingredient could not be updated.
	 */
	public String updateUserListIngredient(Principal principal, String ingredientName, String listName, String wholeNumberQuantity, String fractionInput, 
		String ingredientUnit, String ingredientType, RedirectAttributes redirectAttrs, int listTypeValue) {
		if(!(listTypeValue == PANTRY_LIST_TYPE || listTypeValue == SHOPPING_LIST_TYPE)) {
			redirectAttrs.addAttribute(MESSAGE_PARAM, "you have requested for an incorrect list type to update.");
			return REDIRECT_TO_HOME;
		}
		String message = "not all required parameters were passed in.";
		if(ingredientName != null && listName != null && wholeNumberQuantity != null && fractionInput != null) {
			UtilityServiceImpl utilityService = new UtilityServiceImpl();
			if(utilityService.isOwner(principal, listTypeValue, listName)) {
				String[] parsedFractionInput = fractionInput.split("/");
				if(parsedFractionInput.length == 2) {
					if(parsedFractionInput[0] != null && parsedFractionInput[1] != null) {
						ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
						ShoppingListDAOImpl shoppingListDAO = (ShoppingListDAOImpl) appContext.getBean(SHOPPING_LIST_DAO_BEAN_NAME);
						PantryListDAOImpl pantryListDAO = (PantryListDAOImpl) appContext.getBean(PANTRY_DAO_BEAN_NAME);
						int updateCode = -1;
						
						Integer ingredientId = null;
						if(listTypeValue == SHOPPING_LIST_TYPE) {
							ingredientId = shoppingListDAO.getIngredientId(listName, ingredientName);
						}
						else {
							ingredientId = pantryListDAO.getIngredientId(listName, ingredientName);
						}
						String errorCheckString = null;
						
						if(ingredientId != null && ingredientId >= 1) {
							// now attempt to update
							int wholeNumber = Integer.parseInt(wholeNumberQuantity);
							int numerator = Integer.parseInt(parsedFractionInput[0]);
							int denominator = Integer.parseInt(parsedFractionInput[1]);
							errorCheckString = utilityService.validateUserInput(wholeNumber, numerator, denominator);
							if(errorCheckString.equals("")) {
								if(wholeNumber > 0 && (numerator == 1 && denominator == 1)) {
									numerator = 0;
								}
								else if(wholeNumber == 0 && (numerator == 1 && denominator == 1)) {
									wholeNumber = 1;
									numerator = 0;
								}
								if(listTypeValue == SHOPPING_LIST_TYPE) {
									updateCode = shoppingListDAO.updateListIngredient(wholeNumber, numerator, denominator, ingredientUnit, ingredientType, ingredientId);
								}
								else {
									Fraction thresholdFraction = getThresholdValues(wholeNumber, numerator, denominator);
									int ingredientThresholdWholeNumber = thresholdFraction.getNumerator() / thresholdFraction.getDenominator();
									int ingredientThresholdNumerator = thresholdFraction.getNumerator() % thresholdFraction.getDenominator();
									int ingredientThresholdDenominator = thresholdFraction.getDenominator();
									/**
									 * by default allow a newly updated pantry ingredient to be transferred to a shopping list when a threshold value is hit.
									 * also mark the threshold value as not having been hit yet.
									 */
									updateCode = pantryListDAO.updateListIngredient(wholeNumber, numerator, denominator, ingredientThresholdWholeNumber, ingredientThresholdNumerator, ingredientThresholdDenominator, ingredientUnit, ingredientType, ingredientId, true, false);
								}
								if(updateCode == 1) {
									shoppingListDAO = null;
									pantryListDAO = null;
									((ConfigurableApplicationContext)appContext).close();
									message = null;
									if(listTypeValue == SHOPPING_LIST_TYPE) {
										return REDIRECT_TO_SHOPPING_LIST;
									}
									else {
										return REDIRECT_TO_PANTRY_LIST;
									}
								}
								else {
									message = "was not able to update the ingredient: " + ingredientName;
								}
							}
							else {
								message = errorCheckString;
							}
						}
						else {
							message = "was not able to retrieve the ingredient: " + ingredientName + " for updating";
						}
						shoppingListDAO = null;
						pantryListDAO = null;
						((ConfigurableApplicationContext)appContext).close();
					}
					else {
						message = "cannot get fraction values.";
					}
				}
				else {
					message = "the fraction input is not in proper format";
				}
			}
			else {
				if(listTypeValue == SHOPPING_LIST_TYPE) {
					message = "you must be the owner of the shopping list to update its contents.";
				}
				else {
					message = "you must be the owner of the pantry list to update its contents.";
				}
			}
		}
		redirectAttrs.addAttribute(MESSAGE_PARAM, message);
		if(listTypeValue == SHOPPING_LIST_TYPE) {
			return REDIRECT_TO_SHOPPING_LIST;
		}
		return REDIRECT_TO_PANTRY_LIST;
	}
	
	/**
	 * This is to be used for when the user wants to update how much of the ingredient is left in the pantry list.
	 * This method subtracts a specified amount from an ingredient.
	 * @param principal An object holding authentication information of the current user.
	 * @param newWholeNumber The updated whole number amount of the ingredient.
	 * @param fractionInput The updated numerator and denominator amount of the ingredient.
	 * @param listName The name of the list to be updated, in early releases this is the name of the pantry list with the ingredient to be updated.
	 * @param ingredientName The name of the ingredient to be updated.
	 * @param redirectAttrs An object for holding attributes for when a redirection is necessary.
	 * @param listType The list type, in early releases only a pantry list should be calling this method.
	 * @return Displays the list with the ingredient to be updated if successful, if unsuccessful then a message indicating the error will be displayed.
	 */
	public String updateListIngredientAmount(Principal principal, String newWholeNumber, String fractionInput, String listName, String ingredientName, RedirectAttributes redirectAttrs, int listType) {
		if(!(listType == PANTRY_LIST_TYPE)) {
			redirectAttrs.addAttribute(MESSAGE_PARAM, "You have not performed this request on a pantry list.");
			return REDIRECT_TO_HOME;
		}
		String notificationMessage = "Missing required parameters";
		if(listName != null && ingredientName != null && newWholeNumber != null && fractionInput != null) {
			notificationMessage = "you must be the owner of this pantry list to update(subtract) an ingredient in it.";
			UtilityServiceImpl utilityService = new UtilityServiceImpl();
			if(utilityService.isOwner(principal, listType, listName)) {			
				notificationMessage = "the fraction input is not in proper format.";
				String[] parsedFractionInput = fractionInput.split("/");
				if(parsedFractionInput.length == 2) {
					notificationMessage = "cannot get the fraction values.";
					if(parsedFractionInput[0] != null && parsedFractionInput[1] != null) {
						ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
						PantryListDAOImpl pantryDAO = (PantryListDAOImpl) appContext.getBean(PANTRY_DAO_BEAN_NAME);
						
						Integer ingredientId = pantryDAO.getIngredientId(listName, ingredientName);
						
						notificationMessage = "cannot get the ingredient to be updated.";
						if(ingredientId > 0) {
							String errorCheckString = "";
							int newWholeNumberAmount = Integer.parseInt(newWholeNumber);
							int newNumeratorAmount = Integer.parseInt(parsedFractionInput[0]);
							int newDenominatorAmount = Integer.parseInt(parsedFractionInput[1]);
							errorCheckString = utilityService.validateUserInput(newWholeNumberAmount, newNumeratorAmount, newDenominatorAmount);
							if(errorCheckString.equals("")) {
								if(newWholeNumberAmount > 0 && (newNumeratorAmount == 1 && newDenominatorAmount == 1)) {
									newNumeratorAmount = 0;
								}
								else if(newWholeNumberAmount == 0 && (newNumeratorAmount == 1 && newDenominatorAmount == 1)) {
									newWholeNumberAmount = 1;
									newNumeratorAmount = 0;
								}
								// first see if you can directly updated the quantity.
								PantryIngredient ingredientToUpdate = pantryDAO.getSingleIngredient(listName, ingredientName);
								notificationMessage = "cannot retrieve the ingredient you are attempting to update the quantity of.";
								if(ingredientToUpdate != null) {
									/**
									 * must get the amount of the ingredient to update and check to see if the incoming amount to subtract is less than 
									 * or equal to the current ingredient amount.
									 * cases!:
									 * 1) if it is more than the amount, redirect the user back with an error notification saying you cannot subtract more 
									 * than what you have in the list.
									 * 2) if it is equal to what you have you must do the following:
									 * move it to the shopping list and delete it from the pantry list. (print some kind of notification upon redirection).
									 * 3) if it is less than what you have then you must check if the threshold has been hit and move the ingredient over if so,
									 * and update it. 
									 * 4) if the threshold is not hit simply just update it. (no notification message here!)
									 * given the only time you do not print a message back to the user is if you update it and the threshold was NOT HIT.
									 */
									notificationMessage = "you cannot subtract more than the ingredient amount that you currently own.";
									int currentIngredientWholeNumber = Integer.parseInt(ingredientToUpdate.getWholeNumber());
									int currentIngredientNumeratorAmount = Integer.parseInt(ingredientToUpdate.getNumerator());
									int currentIngredientDenominator = Integer.parseInt(ingredientToUpdate.getDenominator());
									int checkIngredientSubtraction = utilityService.checkIfSubtractionIsPossible(newWholeNumberAmount, newNumeratorAmount, newDenominatorAmount, currentIngredientWholeNumber, currentIngredientNumeratorAmount, currentIngredientDenominator);
									if(checkIngredientSubtraction != 1) {
										int thresholdWholeNumber = Integer.parseInt(ingredientToUpdate.getThresholdWholeNumber());
										int thresholdNumerator = Integer.parseInt(ingredientToUpdate.getThresholdNumerator());
										int thresholdDenominator = Integer.parseInt(ingredientToUpdate.getThresholdDenominator());
										String currentUserName = principal.getName(); // the logged in user's name.
										if(checkIngredientSubtraction == 0) {
											/**
											 * user has specified to subtract as much as there is in the pantry list.
											 * doesn't make sense to update, just delete then transfer the threshold amount times five over.
											 */
											int removeIngredientCode = pantryDAO.removeListIngredient(ingredientId);
											if(removeIngredientCode == 1) {
												if(ingredientToUpdate.canIngredientBeAddedToShoppingList()) {
													// future release: allow the user to specify how much to add to the shopping list.
													notificationMessage = addPantryIngredientToShoppingList(appContext, currentUserName, ingredientName, ingredientToUpdate, thresholdWholeNumber, thresholdNumerator, thresholdDenominator);
												}
												else {
													notificationMessage = null;
												}
											}
											else {
												notificationMessage = "could not remove the ingredient!";
											}
										}
										else {
											/** 
											 * the user subtracted an amount less than the amount currently left of the ingredient.
											 */
											int updateCode = -1;
											Fraction newQuantityAmount = utilityService.getNewQuantityAfterSubtraction(currentIngredientWholeNumber, currentIngredientNumeratorAmount, currentIngredientDenominator, newWholeNumberAmount, newNumeratorAmount, newDenominatorAmount);
											
											int updatedIngredientWholeNumber = newQuantityAmount.getNumerator() / newQuantityAmount.getDenominator();
											int updatedIngredientNumerator = newQuantityAmount.getNumerator() % newQuantityAmount.getDenominator();
											int upgradedIngredientDenominator = newQuantityAmount.getDenominator();
											
											updateCode = pantryDAO.updateListIngredientAmount(updatedIngredientWholeNumber, updatedIngredientNumerator, upgradedIngredientDenominator, ingredientId);
											if(updateCode == 1)  { // success
												// check to see if the threshold has been hit. 
												// so grab this individual ingredient object.
												// store the new whole number, numerator, and denominators into one fraction.
												// then get the fraction of the ingredient object's threshold value.
												// compare these two fraction objects.
												// if you get >= 0 then the threshold has been hit and multiply the ingredient's threshold fraction by 5 and insert into shopping list.
												if(utilityService.checkIfThresholdHit(thresholdWholeNumber, thresholdNumerator, thresholdDenominator, newWholeNumberAmount, newNumeratorAmount, newDenominatorAmount)) {
													if(ingredientToUpdate.canIngredientBeAddedToShoppingList()) {
														// future release: let user choose how much to add to the shopping list.
														notificationMessage = addPantryIngredientToShoppingList(appContext, currentUserName, ingredientName, ingredientToUpdate, thresholdWholeNumber, thresholdNumerator, thresholdDenominator);
														pantryDAO.updateIngredientTransferStatus(false, ingredientId);
														pantryDAO.updateIngredientThresholdHitStatus(true, ingredientId); // mark the threshold as having been hit.
													}
												}
												else {
													// amount has been updated and threshold wasn't hit, so no notification message is used.
													pantryDAO = null;
													utilityService = null;
													((ConfigurableApplicationContext) appContext).close();
													return REDIRECT_TO_PANTRY_LIST;
												}
											}
											else if(updateCode == 0) {
												notificationMessage = "nothing was updated.";
											}
											else if(updateCode <= -1) {
												notificationMessage = "an error has occured and the ingredient could not be updated.";
											}
											else if(updateCode > 1) {
												notificationMessage = "more than one ingredient was updated, contact an admin.";
											}
										}
									}
								}
							}
							else {
								notificationMessage = errorCheckString;
							}
						}
						pantryDAO = null;
						utilityService = null;
						((ConfigurableApplicationContext) appContext).close();
					}
				}
			}
		}		
		if(notificationMessage != null) {
			redirectAttrs.addAttribute(MESSAGE_PARAM, notificationMessage);
		}
		return REDIRECT_TO_PANTRY_LIST;
	}
	
	private String addPantryIngredientToShoppingList(ApplicationContext appContext, String currentUserName, String ingredientName, PantryIngredient ingredientToUpdate, int thresholdWholeNumber, int thresholdNumerator, int thresholdDenominator) {
		String notificationMessage = "";
		ShoppingListDAOImpl shoppingListDAO = (ShoppingListDAOImpl) appContext.getBean(SHOPPING_LIST_DAO_BEAN_NAME);
		// the fraction quantity to add based off the threshold values times 5. this is the original amount of the current ingredient entered by the user.
		Fraction fractionQuantityToAdd = computeFractionToAdd(thresholdWholeNumber, thresholdNumerator, thresholdDenominator);
		
		int newWholeNumberQuantity = fractionQuantityToAdd.getNumerator() / fractionQuantityToAdd.getDenominator();
		int newNumeratorQuantity = fractionQuantityToAdd.getNumerator() % fractionQuantityToAdd.getDenominator();
		int newDenomintorQuantity = fractionQuantityToAdd.getDenominator();
		
		// must check for valid "type". for example both types must be meats or vegetables.
		String shoppingListName = shoppingListDAO.getListName(currentUserName);
		List<Ingredient> shoppingListContents = shoppingListDAO.getListIngredients(shoppingListName);
		
		Ingredient matchingIngredient = null; // this will point to something if this item is already in the user's shopping list.
		
		for(Ingredient ingredient : shoppingListContents) {
			if(ingredientName.equals(ingredient.getIngredientName())) {
				matchingIngredient = ingredient;
				break;
			}
		}
		
		if(matchingIngredient == null) { // no potential clash so just simply add.
			int addToShoppingListCode = shoppingListDAO.addListIngredient(ingredientName, newWholeNumberQuantity, newNumeratorQuantity, newDenomintorQuantity, ingredientToUpdate.getIngredientUnit(), ingredientToUpdate.getIngredientType(), shoppingListName);
			if(addToShoppingListCode == 1) {
				notificationMessage = "ingredient has been updated, threshold value has been hit and the ingredient," + ingredientName + ", has been added!";
			}
			else if(addToShoppingListCode == -1 || addToShoppingListCode == 0) {
				notificationMessage = "ingredient has been updated, threshold value has been hit but the ingredient," + ingredientName + ", wasn't added!";
			}
			else if(addToShoppingListCode > 1){
				notificationMessage = "ingredient has been updated, threshold value has been hit." + "contact an administrator if updating pantry list ingredients keeps displaying this message!";
			}
		}
		else {
			// the ingredient already exists, now check for supported unit conversion.
			if(matchingIngredient.getIngredientType().equals(ingredientToUpdate.getIngredientType())) {
				Fraction updatedQuantity = null;
				if(matchingIngredient.getIngredientUnit().equals(ingredientToUpdate.getIngredientUnit())) {
					// no conversion needed. simply add.
					updatedQuantity = getConvertedFraction(1, 1, matchingIngredient, newWholeNumberQuantity, newNumeratorQuantity, newDenomintorQuantity);
				}
				else if(matchingIngredient.getIngredientUnit().equals("tbsp.") && ingredientToUpdate.getIngredientUnit().equals("tsp.")) {
					// first get to the same units.
					updatedQuantity = getConvertedFraction(1, 3, matchingIngredient, newWholeNumberQuantity, newNumeratorQuantity, newDenomintorQuantity);
				}
				else if(matchingIngredient.getIngredientUnit().equals("tsp.") && ingredientToUpdate.getIngredientUnit().equals("tbsp.")) {
					updatedQuantity = getConvertedFraction(3, 1, matchingIngredient, newWholeNumberQuantity, newNumeratorQuantity, newDenomintorQuantity);
				}
				else if(matchingIngredient.getIngredientUnit().equals("lbs.") && ingredientToUpdate.getIngredientUnit().equals("oz.")) {
					updatedQuantity = getConvertedFraction(1, 16, matchingIngredient, newWholeNumberQuantity, newNumeratorQuantity, newDenomintorQuantity);
				}
				else if(matchingIngredient.getIngredientUnit().equals("oz.") && ingredientToUpdate.getIngredientUnit().equals("lbs.")) {
					updatedQuantity = getConvertedFraction(16, 1, matchingIngredient, newWholeNumberQuantity, newNumeratorQuantity, newDenomintorQuantity);
				}
				else if(matchingIngredient.getIngredientUnit().equals("qt") && ingredientToUpdate.getIngredientUnit().equals("oz.")) {
					updatedQuantity = getConvertedFraction(1, 32, matchingIngredient, newWholeNumberQuantity, newNumeratorQuantity, newDenomintorQuantity);
				}
				else if(matchingIngredient.getIngredientUnit().equals("oz.") && ingredientToUpdate.getIngredientUnit().equals("qt")) {
					updatedQuantity = getConvertedFraction(32, 1, matchingIngredient, newWholeNumberQuantity, newNumeratorQuantity, newDenomintorQuantity);
				}
				else if(matchingIngredient.getIngredientUnit().equals("c.") && ingredientToUpdate.getIngredientUnit().equals("tbsp.")) {
					updatedQuantity = getConvertedFraction(1, 16, matchingIngredient, newWholeNumberQuantity, newNumeratorQuantity, newDenomintorQuantity);
				}
				else if(matchingIngredient.getIngredientUnit().equals("tbsp.") && ingredientToUpdate.getIngredientUnit().equals("c.")) {
					updatedQuantity = getConvertedFraction(16, 1, matchingIngredient, newWholeNumberQuantity, newNumeratorQuantity, newDenomintorQuantity);
				}
				else if(matchingIngredient.getIngredientUnit().equals("c.") && ingredientToUpdate.getIngredientUnit().equals("tsp.")) {
					updatedQuantity = getConvertedFraction(1, 48, matchingIngredient, newWholeNumberQuantity, newNumeratorQuantity, newDenomintorQuantity);
				}
				else if(matchingIngredient.getIngredientUnit().equals("tsp.") && ingredientToUpdate.getIngredientUnit().equals("c.")) {
					updatedQuantity = getConvertedFraction(48, 1, matchingIngredient, newWholeNumberQuantity, newNumeratorQuantity, newDenomintorQuantity);
				}
				if(updatedQuantity != null) {
					int newShoppingListIngredientWholeNumber = updatedQuantity.getNumerator() / updatedQuantity.getDenominator();
					int newShoppingListIngredientNumerator = updatedQuantity.getNumerator() % updatedQuantity.getDenominator();
					int newShoppingListIngredientDenominator = updatedQuantity.getDenominator();
					int updateShoppingIngredientCode = shoppingListDAO.updateListIngredientAmount(newShoppingListIngredientWholeNumber, newShoppingListIngredientNumerator, newShoppingListIngredientDenominator, matchingIngredient.getIngredientID());
					if(newShoppingListIngredientWholeNumber > 0 && (newShoppingListIngredientNumerator == 1 && newShoppingListIngredientDenominator == 1)) {
						newShoppingListIngredientNumerator = 0;
					}
					else if(newShoppingListIngredientWholeNumber == 0 && (newShoppingListIngredientNumerator == 1 && newShoppingListIngredientDenominator == 1)) {
						newShoppingListIngredientWholeNumber = 1;
						newShoppingListIngredientNumerator = 0;
					}
					if(updateShoppingIngredientCode == 1) {
						notificationMessage = "ingredient has been updated, threshold value has been hit and the ingredient," + ingredientName + ", has been added to your shopping list!";
					}
					else if(updateShoppingIngredientCode == 0) {
						notificationMessage = "ingredient has been updated, threshold value has been hit and the ingredient," + ingredientName + ", was not added to your shopping list!";
					}
					else if(updateShoppingIngredientCode <= -1) {
						notificationMessage = "ingredient has been updated, threshold value has been hit and the ingredient," + ingredientName + ", could not be added to your shopping list!";
					}
					else if(updateShoppingIngredientCode > 1) {
						notificationMessage = "ingredient has been updated, threshold value has been hit and the ingredient," + ingredientName + ", has been added. If this message persists, contact an admin.";
					}
				}
				else {
					notificationMessage = "ingredient has been updated, threshold value has been hit but the ingredient," + ingredientName + ", cannot be added. The unit types do not match. View which unit type conversions are supported.";
				}
			}
			else {
				notificationMessage = "ingredient has been updated, threshold value has been hit but the ingredient," + ingredientName + ", cannot be added. The type on the pantry list must match the type of the shopping list.";
			}
		}
		shoppingListDAO = null;
		return notificationMessage;
	}
	
	/**
	 * @param refactorNumerator The numerator value of the refactoring value.
	 * @param refactorDenominator The denominator value of the refactoring value.
	 * @param shoppingListIngredient The ingredient from the shopping list.
	 * @param pantryThresholdWholeNumber The threshold whole number portion.
	 * @param pantryThresholdNumerator The threshold's numerator portion.
	 * @param pantryThresholdDenominator The threshold's denominator portion.
	 * @return A fraction with a converted whole number, numerator, and denominator quantity.
	 */
	public Fraction getConvertedFraction(int refactorNumerator, int refactorDenominator, Ingredient shoppingListIngredient, int pantryThresholdWholeNumber, int pantryThresholdNumerator, int pantryThresholdDenominator) {
		// you want to convert the units of the pantry list into that of the shopping list ingredient.
		Fraction shoppingListIngredientQuantity = new Fraction(((Integer.parseInt(shoppingListIngredient.getWholeNumber()) * Integer.parseInt(shoppingListIngredient.getDenominator())) 
			+ Integer.parseInt(shoppingListIngredient.getNumerator())), Integer.parseInt(shoppingListIngredient.getDenominator()));
		Fraction pantryListIngredientQuantity = new Fraction(((pantryThresholdWholeNumber * pantryThresholdDenominator) + pantryThresholdNumerator), pantryThresholdDenominator);
		
		Fraction refactoringFraction = new Fraction(refactorNumerator, refactorDenominator);
		pantryListIngredientQuantity = pantryListIngredientQuantity.multiply(refactoringFraction);
				
		return shoppingListIngredientQuantity.add(pantryListIngredientQuantity);
	}

	/**
	 * @param thresholdWholeNumber The threshold whole number value of the pantry ingredient.
	 * @param thresholdNumerator The threshold numerator of the pantry ingredient.
	 * @param thresholdDenominator The threshold denominator of the pantry ingredient.
	 * @return A fraction with a value computed from a pantry ingredient's threshold values.
	 */
	private Fraction computeFractionToAdd(int thresholdWholeNumber, int thresholdNumerator, int thresholdDenominator) {
		Fraction thresholdFraction = new Fraction(((thresholdWholeNumber * thresholdDenominator) + thresholdNumerator), thresholdDenominator);
		return thresholdFraction.multiply(THRESHOLD_DENOMINATOR);
	}
		
	/**
	 * @param principal An object holding authentication information of the current user.
	 * @param listName The name of the list to transfer over, recipe or pantry list.
	 * @param redirectAttrs An object holding the attributes for when a redirection is done.
	 * @param listTypeValue A value indicating if shopping list or an inventory list is to be tested for.
	 * @return The shopping list if successful and the current recipe page with a notification if not able to
	 * move the contents.
	 */
	public String listToShoppingList(Principal principal, String listName, RedirectAttributes redirectAttrs, int listTypeValue) {
		String notificationMessage = "you are not logged in so you cannot move this to your shopping list.";
		if(principal != null) {
			String userName = principal.getName();
			if(userName != null) {
				notificationMessage = "a list name is required.";
				if(listName != null) {
					notificationMessage = "you have no shopping list to move contents to, contact an administrator.";
					
					ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
					RecipeDAOImpl recipesDAO = (RecipeDAOImpl) appContext.getBean(RECIPE_DAO_BEAN_NAME);
					PantryListDAOImpl pantryDAO = (PantryListDAOImpl) appContext.getBean(PANTRY_DAO_BEAN_NAME);
					ShoppingListDAOImpl shoppingListDAO = (ShoppingListDAOImpl) appContext.getBean(SHOPPING_LIST_DAO_BEAN_NAME);
					
					String shoppingListName = shoppingListDAO.getListName(userName);
					
					if(shoppingListName != null) {
						List<Ingredient> ingredientsList = null;
						
						if(listTypeValue == RECIPE_TYPE) {
							ingredientsList = recipesDAO.getListIngredients(listName);
						}
						else if(listTypeValue == PANTRY_LIST_TYPE) {
							ingredientsList = getListIngredients(pantryDAO, listName);
						}
						else {
							System.out.println("user tries to transfer an improper list type while logged in.\nJust a debugging statement.");
							notificationMessage = "improper list type selected.";
							redirectAttrs.addAttribute(MESSAGE_PARAM, notificationMessage);
							shoppingListDAO = null;
							pantryDAO = null;
							((ConfigurableApplicationContext)appContext).close();
							return REDIRECT_TO_HOME;
						}
						boolean transferContents = true;
						
						if(ingredientsList != null) {
							if(ingredientsList.size() < 1) {
								notificationMessage = "there are no ingredients to move over";
							}
							else {
								notificationMessage = "unable to move the contents to your shopping list";
								List<Ingredient> shoppingListIngredients = shoppingListDAO.getListIngredients(shoppingListName);
								
								/**
								 * a hash map to retrieve names from the shopping list in constant time.
								 * this holds the contents of the recipe or pantry list the user is trying to add from that does not appear on the shopping list.
								 */
								HashMap<String, Ingredient> listMap = new HashMap<String, Ingredient>();
								
								/*
								 * this list will hold the ingredient(s) that appear on the shopping list and the list the user is adding from (pantry or recipe list).
								 * conversions will be attempted before being inserted into this list.
								 */
								List<Ingredient> updateList = new LinkedList<Ingredient>();
								
								for(Ingredient ingredient : ingredientsList) {
									listMap.put(ingredient.getIngredientName(), ingredient);
								}
								
								/*
								 * go through the shopping list and see if there are any matches to the recipe ingredients.
								 * if there is a match, make sure the types match and also see if the units can be converted.
								 * at the end of this, assuming all the conversions can be done, the recipeListMap will have items that can be
								 * inserted into the shopping list and the updateList will have the proper items to be updated.
								 */
								for(Ingredient shoppingListIngredient : shoppingListIngredients) {
									if(listMap.containsKey(shoppingListIngredient.getIngredientName())) {
										Ingredient listIngredient = listMap.get(shoppingListIngredient.getIngredientName());
										Ingredient shoppingListIngredientCopy = new Ingredient(shoppingListIngredient.getIngredientName(), shoppingListIngredient.getWholeNumber(), shoppingListIngredient.getNumerator(), shoppingListIngredient.getDenominator(), 
										shoppingListIngredient.getIngredientUnit(), shoppingListIngredient.getIngredientID(), shoppingListIngredient.getIngredientType());
										Ingredient recipeListIngredientCopy = new Ingredient(listIngredient.getWholeNumber(), listIngredient.getNumerator(), listIngredient.getDenominator());
										if(listIngredient.getIngredientType().equals(shoppingListIngredient.getIngredientType())) {
											// the types DO match, now check to see if the conversion is possible.
											// for example: let's assume the recipe ingredient is in tablespoons and the shopping list has the ingredient in teaspoons.
											if(listIngredient.getIngredientUnit().equals(shoppingListIngredient.getIngredientUnit())) {
												// no conversion needed. simply add.
												convertContents(listMap, updateList, 1, 1, recipeListIngredientCopy, shoppingListIngredientCopy);
											}
											else if(listIngredient.getIngredientUnit().equals("tbsp.") && shoppingListIngredient.getIngredientUnit().equals("tsp.")) {
												// first get to the same units.
												convertContents(listMap, updateList, 3, 1, recipeListIngredientCopy, shoppingListIngredientCopy);
											}
											else if(listIngredient.getIngredientUnit().equals("tsp.") && shoppingListIngredient.getIngredientUnit().equals("tbsp.")) {
												convertContents(listMap, updateList, 1, 3, recipeListIngredientCopy, shoppingListIngredientCopy);
											}
											else if(listIngredient.getIngredientUnit().equals("lbs.") && shoppingListIngredient.getIngredientUnit().equals("oz.")) {
												convertContents(listMap, updateList, 16, 1, recipeListIngredientCopy, shoppingListIngredientCopy);
											}
											else if(listIngredient.getIngredientUnit().equals("oz.") && shoppingListIngredient.getIngredientUnit().equals("lbs.")) {
												convertContents(listMap, updateList, 1, 16, recipeListIngredientCopy, shoppingListIngredientCopy);
											}
											else if(listIngredient.getIngredientUnit().equals("qt") && shoppingListIngredient.getIngredientUnit().equals("oz.")) {
												convertContents(listMap, updateList, 32, 1, recipeListIngredientCopy, shoppingListIngredientCopy);
											}
											else if(listIngredient.getIngredientUnit().equals("oz.") && shoppingListIngredient.getIngredientUnit().equals("qt")) {
												convertContents(listMap, updateList, 1, 32, recipeListIngredientCopy, shoppingListIngredientCopy);
											}
											else if(listIngredient.getIngredientUnit().equals("c.") && shoppingListIngredient.getIngredientUnit().equals("tbsp.")) {
												convertContents(listMap, updateList, 16, 1, recipeListIngredientCopy, shoppingListIngredientCopy);
											}
											else if(listIngredient.getIngredientUnit().equals("tbsp.") && shoppingListIngredient.getIngredientUnit().equals("c.")) {
												convertContents(listMap, updateList, 1, 16, recipeListIngredientCopy, shoppingListIngredientCopy);
											}
											else if(listIngredient.getIngredientUnit().equals("c.") && shoppingListIngredient.getIngredientUnit().equals("tsp.")) {
												convertContents(listMap, updateList, 48, 1, recipeListIngredientCopy, shoppingListIngredientCopy);
											}
											else if(listIngredient.getIngredientUnit().equals("tsp.") && shoppingListIngredient.getIngredientUnit().equals("c.")) {
												convertContents(listMap, updateList, 1, 48, recipeListIngredientCopy, shoppingListIngredientCopy);
											}
											else {
												// units cannot be converted.
												transferContents = false;
												shoppingListIngredientCopy = null;
												recipeListIngredientCopy = null;
												notificationMessage = "could not add to shopping list as ingredient: " + shoppingListIngredient.getIngredientName() + " could not be converted.";
												break;
											}			
										}
										else {
											// the type do not match. for example: converting a meat type to a vegetable type.
											transferContents = false;
											shoppingListIngredientCopy = null;
											recipeListIngredientCopy = null;
											notificationMessage = "make sure all ingredient types match before adding to your shopping list.";
											break;
										}
										shoppingListIngredientCopy = null;
										recipeListIngredientCopy = null;
										
									}
								}
								if(transferContents) {
									notificationMessage = transferContents(listMap, updateList, shoppingListName);
									
									if(notificationMessage.equals("")) { // no errors.
										shoppingListDAO = null;
										pantryDAO = null;
										recipesDAO = null;
										((ConfigurableApplicationContext)appContext).close();
										return REDIRECT_TO_SHOPPING_LIST;
									}
								}
							}
						}
						else {
							shoppingListDAO = null;
							pantryDAO = null;
							recipesDAO = null;
							((ConfigurableApplicationContext)appContext).close();
							if(listTypeValue == PANTRY_LIST_TYPE) {
								notificationMessage = "could not retrieve the pantry list you are attempting to transfer over.";
								redirectAttrs.addAttribute(MESSAGE_PARAM, notificationMessage);
								return REDIRECT_TO_PANTRY_LIST;
							}
							else if(listTypeValue == RECIPE_TYPE) {
								notificationMessage = "could not retrieve the recipe list you are attempting to transfer over.";
								redirectAttrs.addAttribute(RECIPE_NAME_PARAM, listName);
								redirectAttrs.addAttribute(MESSAGE_PARAM, notificationMessage);
								return REDIRECT_TO_RECIPE;
							}
						}
					}
					shoppingListDAO = null;
					pantryDAO = null;
					recipesDAO = null;
					((ConfigurableApplicationContext)appContext).close();
				}
				if(listTypeValue == PANTRY_LIST_TYPE) {
					redirectAttrs.addAttribute(MESSAGE_PARAM, notificationMessage);
					return REDIRECT_TO_PANTRY_LIST;
				}
				else if(listTypeValue == RECIPE_TYPE) {
					redirectAttrs.addAttribute(RECIPE_NAME_PARAM, listName);
					redirectAttrs.addAttribute(MESSAGE_PARAM, notificationMessage);
					return REDIRECT_TO_RECIPE;
				}
			}
			notificationMessage = "unable to retrieve your username. make sure you are logged in.";
		}
		redirectAttrs.addAttribute(MESSAGE_PARAM, notificationMessage);
		return REDIRECT_TO_LOGIN;
	}
	
	
	/**
	 * @param recipeListMap A map of ingredients to be inserted into the shopping list.
	 * @param updateList The list of ingredients to be updated into the shopping list.
	 * @param shoppingListName The name of the shopping list to add the contents to.
	 * @return a string indicating if the transfer was successful or not.
	 */
	public String transferContents(HashMap<String, Ingredient> listMap, List<Ingredient> updateList, String shoppingListName) {
		// insert the contents of the recipe list ingredients map (without any duplicates) into the shopping list.
		int addIngredientCode = 1; 
		String notificationMessage = "";
		ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
		ShoppingListDAOImpl shoppingListDAO = (ShoppingListDAOImpl) appContext.getBean(SHOPPING_LIST_DAO_BEAN_NAME);
		
		for(Ingredient ingredient : listMap.values()) {
			addIngredientCode = shoppingListDAO.addListIngredient(ingredient.getIngredientName(), Integer.parseInt(ingredient.getWholeNumber()), Integer.parseInt(ingredient.getNumerator()), Integer.parseInt(ingredient.getDenominator()), 
					ingredient.getIngredientUnit(), ingredient.getIngredientType(), shoppingListName);
			if(addIngredientCode != 1) {
				notificationMessage = "could not add ingredient: " + ingredient.getIngredientName() + ". No ingredients were updated.";
				break;
			}
		}
		
		int updateIngredientCode = 1;
		if(addIngredientCode == 1) {
			// go through the update list and add to the shopping list using update statements.
			// can use a stringbuilder to print a list of all the ingredient names that can not be updated?
			for(Ingredient ingredient : updateList) {
				updateIngredientCode = shoppingListDAO.updateListIngredientAmount(Integer.parseInt(ingredient.getWholeNumber()), Integer.parseInt(ingredient.getNumerator()), Integer.parseInt(ingredient.getDenominator()), ingredient.getIngredientID());
				if(updateIngredientCode != 1) {
					notificationMessage = "all ingredients were added but ingredient: " + ingredient.getIngredientName() + " could not be updated.";
					break;
				}
			}
		}
		shoppingListDAO = null;
		((ConfigurableApplicationContext)appContext).close();
		return notificationMessage;
	}
	
	/**
	 * @param recipeListMap a map holding the contents of the current recipe.
	 * @param updatedIngredients a list holding the contents of any updated ingredients between the recipe and shopping list ingredients.
	 * @param refactorNumerator a value to refactor the amount between the recipe map and ingredients list.
	 * @param refactorDenominator a denominator value to refactor the amount between the recipe map and ingredients list.
	 * @param recipeListIngredient A copy of an object holding the values of the recipe list ingredient object.
	 * @param shoppingListIngredient A copy of an object holding information about the current shopping list ingredient object.
	 */
	public void convertContents(HashMap<String, Ingredient> recipeListMap, List<Ingredient> updatedIngredientsList, int refactorNumerator, int refactorDenominator, Ingredient recipeListIngredient, Ingredient shoppingListIngredient) {
		Ingredient updatedIngredient = new Ingredient();
		Fraction recipeListIngredientQuantity = new Fraction((Integer.parseInt(recipeListIngredient.getDenominator()) * Integer.parseInt(recipeListIngredient.getWholeNumber())) + Integer.parseInt(recipeListIngredient.getNumerator()), Integer.parseInt(recipeListIngredient.getDenominator()));
		recipeListIngredientQuantity = recipeListIngredientQuantity.multiply(new Fraction(refactorNumerator, refactorDenominator));
		Fraction shoppingListIngredientQuantity = new Fraction((Integer.parseInt(shoppingListIngredient.getDenominator()) * Integer.parseInt(shoppingListIngredient.getWholeNumber())) + Integer.parseInt(shoppingListIngredient.getNumerator()), Integer.parseInt(shoppingListIngredient.getDenominator()));

		Fraction updatedQuantityAmount = recipeListIngredientQuantity.add(shoppingListIngredientQuantity);
		// extract a mixed fraction.
		
		int updatedWholeNumber = updatedQuantityAmount.getNumerator() / updatedQuantityAmount.getDenominator();
		int updatedNumerator = updatedQuantityAmount.getNumerator() % updatedQuantityAmount.getDenominator();
		
		updatedIngredient.setWholeNumber(String.valueOf(updatedWholeNumber));
		updatedIngredient.setNumerator(String.valueOf(updatedNumerator));
		updatedIngredient.setDenominator(String.valueOf(updatedQuantityAmount.getDenominator()));
		
		updatedIngredient.setIngredientType(shoppingListIngredient.getIngredientType());
		updatedIngredient.setIngredientUnit(shoppingListIngredient.getIngredientUnit());
		updatedIngredient.setIngredientName(shoppingListIngredient.getIngredientName());
		updatedIngredient.setIngredientID(shoppingListIngredient.getIngredientID());
		
		updatedIngredientsList.add(updatedIngredient);
		recipeListMap.remove(shoppingListIngredient.getIngredientName()); // remove the duplicate from the recipe list map.
	}
			
}
