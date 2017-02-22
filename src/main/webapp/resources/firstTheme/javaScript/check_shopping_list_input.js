/**
 * 
 */
function ratingInput(ratingValue) {
	var inputValueParsed = parseInt(ratingValue);
	if(inputValueParsed > 0 && inputValueParsed <= 5) {
		return true;
	}
	return false;
}

function numberInput(inputValue) {
	var numberRegex= new RegExp("^[0-9]{1,2}$");
	
	if(!numberRegex.test(inputValue)) {
		return false;
	}
	return true;
}

function wholeNumberCheck(wholeNumber) {
	var wholeNumberParsed = parseInt(wholeNumber);
	if(wholeNumberParsed > -1 && wholeNumberParsed < 21) {
		return true;
	}
	else {
		return false;
	}
}

function dishTypeCheck(inputValue) {
	var letterRegex= new RegExp("^[a-zA-Z ]{2,20}$");
	
	if(letterRegex.test(inputValue)) {
		return true;
	}
	else {
		return false;
	}
}

function directionNumberInput(inputValue) {
	var numberRegex= new RegExp("^[1-9]{1,2}$");
	
	if(!numberRegex.test(inputValue)) {
		return false;
	}
	return true;
}

function stringInput(inputValue) {
	var letterRegex= new RegExp("^[a-zA-Z ]{4,40}$");
	
	if(!letterRegex.test(inputValue)) {
		return false;
	}
	return true;
}

function checkDenominatorInput(denominatorValue) {
	var denominatorParsed = parseInt(denominatorValue);
	if(denominatorParsed <= 0) {
		return true;
	}
	return false;
}

function checkFractionForm(numeratorValue, denominatorValue) {
	var numeratorParsed = parseInt(numeratorValue);
	var denominatorParsed = parseInt(denominatorValue);
	if(numeratorParsed > denominatorParsed) {
		return false;
	}
	return true;
}

function resetForm(formName, errorMessageFieldId) {
	var formToClear = document.getElementById(formName);
	if(formToClear === undefined || formToClear === null) {
		document.getElementById(errorMessageFieldId).className = "alert alert-danger";
		document.getElementById(errorMessageFieldId).innerHTML = "form could not be cleared.";
	}
	formToClear.reset();
	document.getElementById(errorMessageFieldId).className = "";
	document.getElementById(errorMessageFieldId).innerHTML = "";
	return false;
}

function setUpdateIngredientFields(ingredientName, outputType, wholeNumber, numeratorQuantity, denominatorQuantity, 
		ingredientUnit, ingredientType) {
	
	var modifyForm = document.getElementById("modifyShoppingListIngredientForm");
	var ingredientNameField = document.getElementById("shoppingListCurrentIngredientName");
	var wholeNumberQuantityOption = modifyForm.elements["shoppingListNewWholeNumberQuantity"];
	var ingredientQuantityOption = modifyForm.elements["shoppingListNewIngredientQuantity"];
	var ingredientUnitOption = modifyForm.elements["shoppingListNewIngredientUnit"];
	var ingredientTypeOption = modifyForm.elements["shoppingListNewIngredientType"];
	
	if(ingredientNameField === undefined || ingredientNameField === null) {
		document.getElementById("shoppingListModifyIngredientErrorMessage").className = "alert alert-danger";
		document.getElementById("shoppingListModifyIngredientErrorMessage").innerHTML = "unable to set the ingredient name.";
		return false;
	}
	
	var ingredientNameParsed = ingredientName.replace(/&#8216;/g, "'");
	ingredientNameParsed = ingredientNameParsed.replace(/&quot;/g, "\"");
	ingredientNameField.value = ingredientNameParsed;
	
	var fractionQuantity = numeratorQuantity + "/" + denominatorQuantity;
	
	if(outputType === "1") {
		for(var i = 0; i < wholeNumberQuantityOption.length; i++) {
			if(wholeNumberQuantityOption[i].value === wholeNumber) {
				wholeNumberQuantityOption.selectedIndex = i;
				break;
			}
		}
		ingredientQuantityOption.selectedIndex = 0;
		
	}
	else if(outputType === "2") {
		for(var i = 0; i < wholeNumberQuantityOption.length; i++) {
			if(wholeNumberQuantityOption[i].value === wholeNumber) {
				wholeNumberQuantityOption.selectedIndex = i;
				break;
			}
		}
		for(var i = 0; i < ingredientQuantityOption.length; i++) {
			if(ingredientQuantityOption[i].value === fractionQuantity) {
				ingredientQuantityOption.selectedIndex = i;
				break;
			}
		}
	}
	else if(outputType === "3") {
		for(var i = 0; i < ingredientQuantityOption.length; i++) {
			if(ingredientQuantityOption[i].value === fractionQuantity) {
				ingredientQuantityOption.selectedIndex = i;
				break;
			}
		}
		wholeNumberQuantityOption.selectedIndex = 0;
	}
	else {
		return false;
	}
	
	for(var i = 0; i < ingredientUnitOption.length; i++) {
		if(ingredientUnitOption[i].value === ingredientUnit) {
			ingredientUnitOption.selectedIndex = i;
			break;
		}
	}
	
	for(var i = 0; i < ingredientTypeOption.length; i++) {
		if(ingredientTypeOption[i].value === ingredientType) {
			ingredientTypeOption.selectedIndex = i;
			break;
		}
	}
	
	document.getElementById("shoppingListModifyIngredientErrorMessage").className = "";
	document.getElementById("shoppingListModifyIngredientErrorMessage").innerHTML = "";
	return false;
	
}

function removeShoppingListItem(id, categoryId) {
	var shoppingListNameInput = document.getElementById("shoppingListNameElement");
	
	if(shoppingListNameInput === undefined || shoppingListNameInput === null)
		return false;
	
	var shoppingListName = shoppingListNameInput.value;
	var csrfTokenValue = document.head.querySelector("[name=_csrf]").content;
	var currentPath = document.head.querySelector("[name=currentPath]").content;
	
	$.ajax({
		url: currentPath + "/removeShoppingListIngredient",
		type : "POST",
		data: "shoppingListIngredientID=" + id + "&shoppingListName=" + shoppingListName + "&_csrf=" + csrfTokenValue,
		success: function(data) {
			var element = document.getElementById(shoppingListName + "-" + "ingredient-" + id);
			element.remove();
			var categoryElement = document.getElementById("category-" + categoryId);
			if(!(categoryElement === undefined || categoryElement === null)) {
				if(categoryElement.children.length <= 1) {
					categoryElement.remove();
				}
				document.getElementById("ingredientRemovalInfo").className = "";
				document.getElementById("ingredientRemovalInfo").innerHTML = "";
			}
			else {
				document.getElementById("ingredientRemovalInfo").className = "alert alert-danger";
				document.getElementById("ingredientRemovalInfo").innerHTML = "could not remove ingredient.";
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			document.getElementById("ingredientRemovalInfo").className = "alert alert-danger";
			document.getElementById("ingredientRemovalInfo").innerHTML = jqXHR.responseText;
		}
	});
}

function addToShoppingList(csrfName) {
	var ingredientAddForm = document.getElementById("addShoppingIngredientsForm");
	var ingredientName = ingredientAddForm.elements["shoppingListIngredientName"].value;
	
	var wholeNumberOption = ingredientAddForm.elements["shoppingListIngredientWholeNumberQuantity"];
	var wholeNumber = wholeNumberOption.options[wholeNumberOption.selectedIndex].value;
	
	var fractionNumberOption = ingredientAddForm.elements["shoppingListIngredientQuantity"];
	var fractionNumber = fractionNumberOption.options[fractionNumberOption.selectedIndex].value;
	var fractionNumberParsed = fractionNumber.split("/");
	var numeratorValue;
	var denominatorValue;
	
	var ingredientUnitOption = ingredientAddForm.elements["shoppingListIngredientUnit"];
	var ingredientUnit = ingredientUnitOption.options[ingredientUnitOption.selectedIndex].value;
	
	var ingredientTypeOption = ingredientAddForm.elements["shoppingListIngredientType"];
	var ingredientType = ingredientTypeOption.options[ingredientTypeOption.selectedIndex].value;
	
	var csrfHidden = ingredientAddForm.elements[csrfName];
	var shoppingListNameInput = ingredientAddForm.elements["shoppingListName"];
	
	if(csrfHidden === null || csrfHidden === undefined) {
		document.getElementById("shoppingListAddErrorMessage").className = "alert alert-danger";
        document.getElementById("shoppingListAddErrorMessage").innerHTML = "Csrf token is necessary.";
		return false;
	}
	
	if(fractionNumberParsed.length !== 2) {
		document.getElementById("shoppingListAddErrorMessage").className = "alert alert-danger";
        document.getElementById("shoppingListAddErrorMessage").innerHTML = "The fraction input is not in fraction form.";
        return false;
	}
	else {
		numeratorValue = fractionNumberParsed[0];
		denominatorValue = fractionNumberParsed[1];
		if(!(checkFractionForm(numeratorValue, denominatorValue))) {
			document.getElementById("shoppingListAddErrorMessage").className = "alert alert-danger";
	        document.getElementById("shoppingListAddErrorMessage").innerHTML = "The numerator should be less than the denominator";
	        return false;
		}
	}
	if(ingredientUnit === null || ingredientUnit === undefined) {
		document.getElementById("shoppingListAddErrorMessage").className = "alert alert-danger";
        document.getElementById("shoppingListAddErrorMessage").innerHTML = "The ingredient unit is missing.";
		return false;
	}
    if (!stringInput(ingredientName))
    {
        document.getElementById("shoppingListAddErrorMessage").className = "alert alert-danger";
        document.getElementById("shoppingListAddErrorMessage").innerHTML = "The ingredient name must be letters only and up to 40 letters.";
        return false;
    }
    
    if(!stringInput(ingredientType)) {
    	document.getElementById("shoppingListAddErrorMessage").className = "alert alert-danger";
        document.getElementById("shoppingListAddErrorMessage").innerHTML = "The ingredient type must be letters only and up to 40 letters.";
        return false;
    }
    
    if(!wholeNumberCheck(wholeNumber)) {
    	document.getElementById("shoppingListAddErrorMessage").className = "alert alert-danger";
        document.getElementById("shoppingListAddErrorMessage").innerHTML = "The whole number must be between 0 and 20.";
        return false;
    }
	    
    if(!numberInput(numeratorValue))
    {
    	document.getElementById("shoppingListAddErrorMessage").className = "alert alert-danger";
        document.getElementById("shoppingListAddErrorMessage").innerHTML = "The ingredient numerator must be a number and up to 2 digits.";
        return false;
    }
    if(!numberInput(denominatorValue))
    {
    	document.getElementById("shoppingListAddErrorMessage").className = "alert alert-danger";
        document.getElementById("shoppingListAddErrorMessage").innerHTML = "The ingredient denominator must be a number and up to 2 digits.";
        return false;
    }
	if(shoppingListNameInput === null || shoppingListNameInput === undefined) {
		document.getElementById("shoppingListAddErrorMessage").className = "alert alert-danger";
        document.getElementById("shoppingListAddErrorMessage").innerHTML = "The shopping list name has not been provided.";
		return false;
	}
	
	return true;
}

// will require a page refresh.
function updateShoppingListIngredientQuantity() {
	
	var updateIngredientForm = document.getElementById("modifyShoppingListIngredientForm");
	
	var currentIngredientName = updateIngredientForm.elements["shoppingListCurrentIngredientName"].value;
	
	var wholeNumberQuantityOption = updateIngredientForm.elements["shoppingListNewWholeNumberQuantity"];
	var wholeNumberQuantity = wholeNumberQuantityOption.options[wholeNumberQuantityOption.selectedIndex].value;
	
	var newFractionQuantityOption = updateIngredientForm.elements["shoppingListNewIngredientQuantity"];
	var newFractionQuantity = newFractionQuantityOption.options[newFractionQuantityOption.selectedIndex].value;
	
	var newFractionParsed = newFractionQuantity.split("/");
	
	var ingredientNumeratorQuantity;
	var ingredientDenominatorQuantity;
	var currentShoppingListNameInput = updateIngredientForm.elements["currentShoppingListName"];
	
	if(newFractionParsed.length !== 2) {
		document.getElementById("shoppingListModifyIngredientErrorMessage").className = "alert alert-danger";
        document.getElementById("shoppingListModifyIngredientErrorMessage").innerHTML = "The fraction input is not in fraction form.";
        return false;
	}
	else {
		ingredientNumeratorQuantity = newFractionParsed[0];
		ingredientDenominatorQuantity = newFractionParsed[1];
		if(!(checkFractionForm(ingredientNumeratorQuantity, ingredientDenominatorQuantity))) {
			document.getElementById("shoppingListModifyIngredientErrorMessage").className = "alert alert-danger";
	        document.getElementById("shoppingListModifyIngredientErrorMessage").innerHTML = "The numerator should be less than the denominator";
	        return false;
		}
	}
	
	if(!stringInput(currentIngredientName)) {
		document.getElementById("shoppingListModifyIngredientErrorMessage").className = "alert alert-danger";
        document.getElementById("shoppingListModifyIngredientErrorMessage").innerHTML = "The ingredient name must be letters only and up to 40 letters.";
        return false;
	}
    
    if(!wholeNumberCheck(wholeNumberQuantity))
    {
    	document.getElementById("shoppingListModifyIngredientErrorMessage").className = "alert alert-danger";
        document.getElementById("shoppingListModifyIngredientErrorMessage").innerHTML = "The whole number must be between 0 and 20.";
        return false;
    }
    
    if(!numberInput(ingredientNumeratorQuantity))
    {
    	document.getElementById("shoppingListModifyIngredientErrorMessage").className = "alert alert-danger";
        document.getElementById("shoppingListModifyIngredientErrorMessage").innerHTML = "The ingredient numerator must be a number and up to 2 digits.";
        return false;
    }
    if(!numberInput(ingredientDenominatorQuantity))
    {
    	document.getElementById("shoppingListModifyIngredientErrorMessage").className = "alert alert-danger";
        document.getElementById("shoppingListModifyIngredientErrorMessage").innerHTML = "The ingredient denominator must be a number and up to 2 digits.";
        return false;
    }
    if(currentShoppingListNameInput === undefined || currentShoppingListNameInput === null) {
    	document.getElementById("shoppingListModifyIngredientErrorMessage").className = "alert alert-danger";
        document.getElementById("shoppingListModifyIngredientErrorMessage").innerHTML = "The shopping list name is missing.";
    	return false;
    }
    
	
	document.getElementById("shoppingListModifyIngredientErrorMessage").className = "";
    document.getElementById("shoppingListModifyIngredientErrorMessage").innerHTML = "";
	
	return true;
}