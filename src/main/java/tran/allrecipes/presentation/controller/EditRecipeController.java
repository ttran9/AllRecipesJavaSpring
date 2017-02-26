package tran.allrecipes.presentation.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tran.allrecipes.presentation.model.Recipe;
import tran.allrecipes.service.RecipeServiceImpl;

/**
 * @author Todd
 * This class provides an authenticated user access to modify a recipe.
 */
@Controller
public class EditRecipeController {
	/** URL mapping to display the edit recipe page.*/
	private static final String EDIT_RECIPE_INFORMATION_MAPPING = "/editRecipe";
	/** URL mapping to edit the recipe. */
	private static final String PROCESS_EDIT_RECIPE_INFORMATION_MAPPING = "/processEditRecipe";

	@RequestMapping(value=EDIT_RECIPE_INFORMATION_MAPPING, method=RequestMethod.GET)
	public String displayEditRecipe(@RequestParam(value="recipeName") String recipeName, Principal principal, ModelMap model, RedirectAttributes redirectAttrs) {
		RecipeServiceImpl recipeService = new RecipeServiceImpl();
		return recipeService.showEditRecipe(recipeName, principal, model, redirectAttrs);
	}
	
	@RequestMapping(value=PROCESS_EDIT_RECIPE_INFORMATION_MAPPING, method=RequestMethod.POST)
	public String processEditRecipe(Principal principal, @ModelAttribute Recipe recipe, RedirectAttributes redirectAttrs) {
		RecipeServiceImpl recipeService = new RecipeServiceImpl();
		return recipeService.processRecipeEdit(principal, recipe.getRecipeName(), recipe.getPrepTimeUnparsed(), recipe.getCookTimeUnparsed(), recipe.getDishType(), 
			recipe.getImageURL(), recipe.getRecipeDescription(), redirectAttrs);
	}
	
}
