package tran.allrecipes.service;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tran.allrecipes.data.RecipeDAOImpl;
import tran.allrecipes.data.UsersDAOImpl;

/**
 * @author Todd
 * A class to allow an authenticated user to create a recipe.
 */
@Service
public class CreateRecipeServiceImpl {
	/** The location of the data source file. */
	private static final String DATABASE_SOURCE_FILE = "database/Datasource.xml";
	/** Name of the recipes DAO bean. */
	private static final String RECIPE_DAO_BEAN_NAME = "RecipesDAO";
	/** Name of the users DAO bean. */
	private static final String USER_DAO_BEAN_NAME = "UsersDAO";
	/** redirects to show create recipe. */
	private static final String REDIRECT_TO_CREATE_RECIPE = "redirect:/showCreateRecipe";
	/** redirects to the show all recipes page. */
	private static final String REDIRECT_TO_SHOW_ALL_RECIPES = "redirect:/";
	/** redirects to the login page. */
	private static final String REDIRECT_TO_LOGIN = "redirect:/signin";
	/** message parameter. */
	private static final String MESSAGE_PARAM ="message";
	
	public CreateRecipeServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param principal An object holding authentication information of the current user.
	 * @param model An object holding UI information.
	 * @param message A message notifying the user of why the user was sent to either the create a recipe page, or the login page.
	 * @return If the user is authenticated then shows a page where the user can create a recipe, if not redirects the user to the login page with a notification.
	 */
	public String showCreateRecipe(Principal principal, ModelMap model, String message, HttpServletRequest request, RedirectAttributes redirectAttrs) {
	    if(principal != null) {
	    	String userName = principal.getName();
			if(userName != null) {
				model.addAttribute("loggedInName", userName);
			}
			if(message != null) {
				model.addAttribute("message", message);
			}
			return "showCreateRecipe";
		}
	    String errorMessage = "you must be logged in to create a recipe.";
	    redirectAttrs.addAttribute(MESSAGE_PARAM, errorMessage);
	    return REDIRECT_TO_LOGIN;
	}
	
	public String processCreateRecipe(Principal principal, String recipeName, String numberServings, String prepTime,
		String cookTime, String dishType, String imageURL, String recipeDescription, RedirectAttributes redirectAttrs) {
		String errorMessage = "parameters are missing.";
		UtilityServiceImpl utilityService = new UtilityServiceImpl();
		LocalDateTime current_time = Timestamp.from(Instant.now()).toLocalDateTime();
	    if(recipeName != null && numberServings != null && prepTime != null && cookTime != null && dishType != null && imageURL != null && recipeDescription != null) {
			if(utilityService.isUserAuthenticated(principal)) {
				String[] prepTimeParsed = prepTime.split("/");
				String[] cookTimeParsed = cookTime.split("/");
				if(prepTimeParsed.length == 3 && cookTimeParsed.length == 3) {
					if(utilityService.validateServings(numberServings) && utilityService.validateRecipeName(recipeName) && utilityService.validateRecipeDescription(recipeDescription)) {
						ApplicationContext appContext =  new ClassPathXmlApplicationContext(DATABASE_SOURCE_FILE);
						RecipeDAOImpl recipesDAO = (RecipeDAOImpl) appContext.getBean(RECIPE_DAO_BEAN_NAME);
						UsersDAOImpl userDAO = (UsersDAOImpl) appContext.getBean(USER_DAO_BEAN_NAME);
						int numberServingsConverted = Integer.parseInt(numberServings);
						// convert the prepTime and cookTime
						int prepTimeConverted = utilityService.convertEnteredTime(prepTimeParsed[0], prepTimeParsed[1], prepTimeParsed[2]);
						int cookTimeConverted = utilityService.convertEnteredTime(cookTimeParsed[0], cookTimeParsed[1], cookTimeParsed[2]);
						if(prepTimeConverted >= 0) {
							if(cookTimeConverted >= 0) {
								String userName = principal.getName();
								int recipeCreateCode = recipesDAO.insertRecipe(recipeName, numberServingsConverted, userName, prepTimeConverted, cookTimeConverted, dishType, imageURL, current_time, recipeDescription, 0, 0, 0, 0, 0);
								if(recipeCreateCode == 1) {
									// use userDAO to update the last posted time for create recipe.
									int updateLastPostedRecipeTimeCode = userDAO.updateUserLastPostedRecipeTime(current_time, userName);
									/**
									 * this check is done to prevent the user from creating too many recipes at once.
									 */
									if(updateLastPostedRecipeTimeCode != 1) {
										errorMessage = "successfully created recipe! but could not update when you last created the recipe!";
									}
									else {
										errorMessage = "successfully created recipe!";
									}
									recipesDAO = null;
									userDAO = null;
									((ConfigurableApplicationContext)appContext).close();
									redirectAttrs.addAttribute(MESSAGE_PARAM, errorMessage);
									return REDIRECT_TO_SHOW_ALL_RECIPES;
								}
								else {
									errorMessage = "failed to create recipe. if all fields were properly filled in contact an administrator.";
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
						errorMessage = "The number of servings must be between 1 and 20. The recipe name must be at least 4 characters and at most 60. The description must be at least four letters.";
					}
				}
				else {
					errorMessage = "incorrect format of prep time or cook time.";
				}
				redirectAttrs.addAttribute(MESSAGE_PARAM, errorMessage);
				return REDIRECT_TO_CREATE_RECIPE;
			}
			else {
				errorMessage = "you must be logged in to create a recipe.";
			}
		}
	    redirectAttrs.addAttribute(MESSAGE_PARAM, errorMessage);
	    return REDIRECT_TO_LOGIN;
	}
		
}
