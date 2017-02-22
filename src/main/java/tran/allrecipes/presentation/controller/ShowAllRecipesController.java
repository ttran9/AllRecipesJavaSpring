package tran.allrecipes.presentation.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import tran.allrecipes.service.RecipeServiceImpl;

/**
 * @author Todd
 * This controller acts as the mapping for the root web page (/).
 */
@Controller
public class ShowAllRecipesController {
	/** URL mapping to show all the recipes.*/
	private static final String SHOW_ALL_RECIPES = "/";
	/** A parameter to show any messages from a redirect. */
	private static final String MESSAGE_PARAM = "message"; 

	@RequestMapping(value=SHOW_ALL_RECIPES, method={RequestMethod.GET})
	public String showAllRecipes(Principal principal, ModelMap model, @RequestParam(value=MESSAGE_PARAM, required=false) String errorMessage) {
		RecipeServiceImpl recipeService = new RecipeServiceImpl();
		return recipeService.showAllRecipes(principal, model, errorMessage);
	}
}
