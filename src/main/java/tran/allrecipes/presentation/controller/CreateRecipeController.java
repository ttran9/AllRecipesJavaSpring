package tran.allrecipes.presentation.controller;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tran.allrecipes.presentation.model.Recipe;
import tran.allrecipes.service.CreateRecipeServiceImpl;

/**
 * @author Todd
 * This class provides mapping for an authenticated user to create a recipe.
 */
@Controller
public class CreateRecipeController {
	/** The message parameter. */
	private static final String MESSAGE_PARAM = "message";
	/** URL mapping to show the create a recipe page. */
	private static final String SHOW_CREATE_RECIPE = "/showCreateRecipe";
	/** URL mapping to create a recipe. */
	private static final String MAKE_RECIPE = "/makeRecipe";
	
	@RequestMapping(value=SHOW_CREATE_RECIPE, method = RequestMethod.GET)
	public String showCreateRecipe(Principal principal, ModelMap model, @RequestParam(value=MESSAGE_PARAM, required=false) String message, HttpServletRequest request, RedirectAttributes redirectAttrs) {
		CreateRecipeServiceImpl showRecipeCreate = new CreateRecipeServiceImpl();
		return showRecipeCreate.showCreateRecipe(principal, model, message, request, redirectAttrs);
	} 

	@RequestMapping(value=MAKE_RECIPE, method = RequestMethod.POST)
	public String processCreateRecipe(Principal principal, @ModelAttribute Recipe recipe, RedirectAttributes redirectAttrs) {
		CreateRecipeServiceImpl showRecipeCreate = new CreateRecipeServiceImpl();
		return showRecipeCreate.processCreateRecipe(principal, recipe.getRecipeName(), String.valueOf(recipe.getNumServings()), recipe.getPrepTimeUnparsed(), recipe.getCookTimeUnparsed(), 
			recipe.getDishType(), recipe.getImageURL(), recipe.getRecipeDescription(), redirectAttrs);
	}
}
