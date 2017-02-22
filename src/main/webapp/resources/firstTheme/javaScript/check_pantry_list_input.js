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

function removePantryListItem(id, categoryId) {
	var pantryListNameInput = document.getElementById("pantryListNameElement");
	
	if(pantryListNameInput === undefined || pantryListNameInput === null)
		return false;
	
	var pantryListName = pantryListNameInput.value;
	var csrfTokenValue = document.head.querySelector("[name=_csrf]").content;
	var currentPath = document.head.querySelector("[name=currentPath]").content;
	
	$.ajax({
		url: currentPath + "/removePantryListIngredient",
		type : "POST",
		data: "pantryListIngredientID=" + id + "&pantryListName=" + pantryListName + "&_csrf=" + csrfTokenValue,
		success: function(data) {
			var element = document.getElementById(pantryListName + "-" + "ingredient-" + id);
			element.remove();
			var categoryElement = document.getElementById("category-" + categoryId);
			if(!(categoryElement === undefined || categoryElement === null)) {
				if(categoryElement.children.length <= 1) {
					categoryElement.remove();
				}
				document.getElementById("pantryIngredientRemovalInfo").className = "";
				document.getElementById("pantryIngredientRemovalInfo").innerHTML = "";
			}
			else {
				document.getElementById("pantryIngredientRemovalInfo").className = "alert alert-danger";
				document.getElementById("pantryIngredientRemovalInfo").innerHTML = "could not remove ingredient.";
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			document.getElementById("pantryIngredientRemovalInfo").className = "alert alert-danger";
			document.getElementById("pantryIngredientRemovalInfo").innerHTML = jqXHR.responseText;
		}
	});
}

function addToPantryList(csrfName) {
	var ingredientAddForm = document.getElementById("addPantryIngredientsForm");
	var ingredientName = ingredientAddForm.elements["pantryListIngredientName"].value;
	
	var wholeNumberOption = ingredientAddForm.elements["pantryListIngredientWholeNumberQuantity"];
	var wholeNumber = wholeNumberOption.options[wholeNumberOption.selectedIndex].value;
	
	var fractionNumberOption = ingredientAddForm.elements["pantryListIngredientQuantity"];
	var fractionNumber = fractionNumberOption.options[fractionNumberOption.selectedIndex].value;
	var fractionNumberParsed = fractionNumber.split("/");
	var numeratorValue;
	var denominatorValue;
	
	var ingredientUnitOption = ingredientAddForm.elements["pantryListIngredientUnit"];
	var ingredientUnit = ingredientUnitOption.options[ingredientUnitOption.selectedIndex].value;
	
	var ingredientTypeOption = ingredientAddForm.elements["pantryListIngredientType"];
	var ingredientType = ingredientTypeOption.options[ingredientTypeOption.selectedIndex].value;
	
	var csrfHidden = ingredientAddForm.elements[csrfName];
	var pantryListNameInput = ingredientAddForm.elements["pantryListName"];
	
	if(csrfHidden === null || csrfHidden === undefined) {
		document.getElementById("pantryListAddErrorMessage").className = "alert alert-danger";
        document.getElementById("pantryListAddErrorMessage").innerHTML = "Csrf token is necessary.";
		return false;
	}
	
	if(fractionNumberParsed.length !== 2) {
		document.getElementById("pantryListAddErrorMessage").className = "alert alert-danger";
        document.getElementById("pantryListAddErrorMessage").innerHTML = "The fraction input is not in fraction form.";
        return false;
	}
	else {
		numeratorValue = fractionNumberParsed[0];
		denominatorValue = fractionNumberParsed[1];
		if(!(checkFractionForm(numeratorValue, denominatorValue))) {
			document.getElementById("pantryListAddErrorMessage").className = "alert alert-danger";
	        document.getElementById("pantryListAddErrorMessage").innerHTML = "The numerator should be less than the denominator";
	        return false;
		}
	}
	if(ingredientUnit === null || ingredientUnit === undefined) {
		document.getElementById("pantryListAddErrorMessage").className = "alert alert-danger";
        document.getElementById("pantryListAddErrorMessage").innerHTML = "The ingredient unit is missing.";
		return false;
	}
    if (!stringInput(ingredientName))
    {
        document.getElementById("pantryListAddErrorMessage").className = "alert alert-danger";
        document.getElementById("pantryListAddErrorMessage").innerHTML = "The ingredient name must be letters only and up to 40 letters.";
        return false;
    }
    
    if(!stringInput(ingredientType)) {
    	document.getElementById("pantryListAddErrorMessage").className = "alert alert-danger";
        document.getElementById("pantryListAddErrorMessage").innerHTML = "The ingredient type must be letters only and up to 40 letters.";
        return false;
    }
    
    if(!wholeNumberCheck(wholeNumber)) {
    	document.getElementById("pantryListAddErrorMessage").className = "alert alert-danger";
        document.getElementById("pantryListAddErrorMessage").innerHTML = "The whole number must be between 0 and 20.";
        return false;
    }
	    
    if(!numberInput(numeratorValue))
    {
    	document.getElementById("pantryListAddErrorMessage").className = "alert alert-danger";
        document.getElementById("pantryListAddErrorMessage").innerHTML = "The ingredient numerator must be a number and up to 2 digits.";
        return false;
    }
    if(!numberInput(denominatorValue))
    {
    	document.getElementById("pantryListAddErrorMessage").className = "alert alert-danger";
        document.getElementById("pantryListAddErrorMessage").innerHTML = "The ingredient denominator must be a number and up to 2 digits.";
        return false;
    }
	if(shoppingListNameInput === null || shoppingListNameInput === undefined) {
		document.getElementById("pantryListAddErrorMessage").className = "alert alert-danger";
        document.getElementById("pantryListAddErrorMessage").innerHTML = "The shopping list name has not been provided.";
		return false;
	}

	return true;
}

function setUpdateIngredientFields(ingredientName, outputType, wholeNumber, numeratorQuantity, denominatorQuantity, 
		ingredientUnit, ingredientType) {
	
	var modifyForm = document.getElementById("modifyPantryListIngredientForm");
	var ingredientNameField = document.getElementById("pantryListCurrentIngredientName");
	var wholeNumberQuantityOption = modifyForm.elements["pantryListNewWholeNumberQuantity"];
	var ingredientQuantityOption = modifyForm.elements["pantryListNewIngredientQuantity"];
	var ingredientUnitOption = modifyForm.elements["pantryListNewIngredientUnit"];
	var ingredientTypeOption = modifyForm.elements["pantryListNewIngredientType"];
	
	if(ingredientNameField === undefined || ingredientNameField === null) {
		document.getElementById("pantryListModifyIngredientErrorMessage").className = "alert alert-danger";
		document.getElementById("pantryListModifyIngredientErrorMessage").innerHTML = "unable to set the ingredient name.";
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
	
	document.getElementById("pantryListModifyIngredientErrorMessage").className = "";
	document.getElementById("pantryListModifyIngredientErrorMessage").innerHTML = "";
	return false;
	
}

function updatePantryListIngredientQuantity() {
	
	var updateIngredientForm = document.getElementById("modifyPantryListIngredientForm");
	
	var currentIngredientName = updateIngredientForm.elements["pantryListCurrentIngredientName"].value;
	
	var wholeNumberQuantityOption = updateIngredientForm.elements["pantryListNewWholeNumberQuantity"];
	var wholeNumberQuantity = wholeNumberQuantityOption.options[wholeNumberQuantityOption.selectedIndex].value;
	
	var newFractionQuantityOption = updateIngredientForm.elements["pantryListNewIngredientQuantity"];
	var newFractionQuantity = newFractionQuantityOption.options[newFractionQuantityOption.selectedIndex].value;
	
	var newFractionParsed = newFractionQuantity.split("/");
	
	var ingredientNumeratorQuantity;
	var ingredientDenominatorQuantity;
	var currentPantryListNameInput = updateIngredientForm.elements["currentPantryListName"];
	
	if(newFractionParsed.length !== 2) {
		document.getElementById("pantryListModifyIngredientErrorMessage").className = "alert alert-danger";
        document.getElementById("pantryListModifyIngredientErrorMessage").innerHTML = "The fraction input is not in fraction form.";
        return false;
	}
	else {
		ingredientNumeratorQuantity = newFractionParsed[0];
		ingredientDenominatorQuantity = newFractionParsed[1];
		if(!(checkFractionForm(ingredientNumeratorQuantity, ingredientDenominatorQuantity))) {
			document.getElementById("pantryListModifyIngredientErrorMessage").className = "alert alert-danger";
	        document.getElementById("pantryListModifyIngredientErrorMessage").innerHTML = "The numerator should be less than the denominator";
	        return false;
		}
	}
	
	if(!stringInput(currentIngredientName)) {
		document.getElementById("pantryListModifyIngredientErrorMessage").className = "alert alert-danger";
        document.getElementById("pantryListModifyIngredientErrorMessage").innerHTML = "The ingredient name must be letters only and up to 40 letters.";
        return false;
	}
    
    if(!wholeNumberCheck(wholeNumberQuantity))
    {
    	document.getElementById("pantryListModifyIngredientErrorMessage").className = "alert alert-danger";
        document.getElementById("pantryListModifyIngredientErrorMessage").innerHTML = "The whole number must be between 0 and 20.";
        return false;
    }
    
    if(!numberInput(ingredientNumeratorQuantity))
    {
    	document.getElementById("pantryListModifyIngredientErrorMessage").className = "alert alert-danger";
        document.getElementById("pantryListModifyIngredientErrorMessage").innerHTML = "The ingredient numerator must be a number and up to 2 digits.";
        return false;
    }
    if(!numberInput(ingredientDenominatorQuantity))
    {
    	document.getElementById("pantryListModifyIngredientErrorMessage").className = "alert alert-danger";
        document.getElementById("pantryListModifyIngredientErrorMessage").innerHTML = "The ingredient denominator must be a number and up to 2 digits.";
        return false;
    }
    if(currentPantryListNameInput === undefined || currentPantryListNameInput === null) {
    	document.getElementById("pantryListModifyIngredientErrorMessage").className = "alert alert-danger";
        document.getElementById("pantryListModifyIngredientErrorMessage").innerHTML = "The shopping list name is missing.";
    	return false;
    }
    
	document.getElementById("pantryListModifyIngredientErrorMessage").className = "";
    document.getElementById("pantryListModifyIngredientErrorMessage").innerHTML = "";
	
	return true;
}


function setSubtractPantryIngredientFields(ingredientName, outputType, ingredientWholeNumber, ingredientNumerator, ingredientDenominator) { 
	
	var subtractForm = document.getElementById("subtractPantryListIngredientForm");
	var ingredientNameField = document.getElementById("ingredientNameToSubtract");
	var wholeNumberQuantityOption = subtractForm.elements["wholeNumberSubtractionQuantity"];
	var ingredientQuantityOption = subtractForm.elements["fractionSubtractionQuantity"];
	
	var originalIngredientWholeNumber = document.getElementById("originalIngredientWholeNumber");
	var originalIngredientNumerator = document.getElementById("originalIngredientNumerator");
	var originalIngredientDenominator = document.getElementById("originalIngredientDenominator");
	
	// set the hidden fields to allow for the subtraction check.
	
	if(originalIngredientWholeNumber === undefined || originalIngredientWholeNumber === null) {
		document.getElementById("subtractPantryListIngredientErrorMessage").className = "alert alert-danger";
		document.getElementById("subtractPantryListIngredientErrorMessage").innerHTML = "unable to set the ingredient whole number.";
		return false;
	}	
	originalIngredientWholeNumber.value = ingredientWholeNumber;
	
	if(originalIngredientNumerator === undefined || originalIngredientNumerator === null) {
		document.getElementById("subtractPantryListIngredientErrorMessage").className = "alert alert-danger";
		document.getElementById("subtractPantryListIngredientErrorMessage").innerHTML = "unable to set the ingredient numerator.";
		return false;
	}
	originalIngredientNumerator.value = ingredientNumerator;
	
	if(originalIngredientDenominator === undefined || originalIngredientDenominator === null) {
		document.getElementById("subtractPantryListIngredientErrorMessage").className = "alert alert-danger";
		document.getElementById("subtractPantryListIngredientErrorMessage").innerHTML = "unable to set the ingredient denominator.";
		return false;
	}
	originalIngredientDenominator.value = ingredientDenominator;
	
	if(ingredientNameField === undefined || ingredientNameField === null) {
		document.getElementById("subtractPantryListIngredientErrorMessage").className = "alert alert-danger";
		document.getElementById("subtractPantryListIngredientErrorMessage").innerHTML = "unable to set the ingredient name.";
		return false;
	}
	
	var ingredientNameParsed = ingredientName.replace(/&#8216;/g, "'");
	ingredientNameParsed = ingredientNameParsed.replace(/&quot;/g, "\"");
	ingredientNameField.value = ingredientNameParsed;
	
	var fractionQuantity = ingredientNumerator + "/" + ingredientDenominator;
	
	if(outputType === "1") {
		for(var i = 0; i < wholeNumberQuantityOption.length; i++) {
			if(wholeNumberQuantityOption[i].value === ingredientWholeNumber) {
				wholeNumberQuantityOption.selectedIndex = i;
				break;
			}
		}
		ingredientQuantityOption.selectedIndex = 0;
		
	}
	else if(outputType === "2") {
		for(var i = 0; i < wholeNumberQuantityOption.length; i++) {
			if(wholeNumberQuantityOption[i].value === ingredientWholeNumber) {
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
		document.getElementById("subtractPantryListIngredientErrorMessage").className = "alert alert-danger";
		document.getElementById("subtractPantryListIngredientErrorMessage").innerHTML = "invalid ingredient input type.";
		return false;
	}
		
	document.getElementById("subtractPantryListIngredientErrorMessage").className = "";
	document.getElementById("subtractPantryListIngredientErrorMessage").innerHTML = "";
	return false;
}
		


function subtractPantryListIngredient() {
	
	var subtractIngredientForm = document.getElementById("subtractPantryListIngredientForm");
	
	var currentIngredientName = updateIngredientForm.elements["ingredientNameToSubtract"].value;
	
	var wholeNumberQuantityOption = updateIngredientForm.elements["wholeNumberSubtractionQuantity"];
	var wholeNumberQuantity = wholeNumberQuantityOption.options[wholeNumberQuantityOption.selectedIndex].value;
	
	var newFractionQuantityOption = updateIngredientForm.elements["fractionSubtractionQuantity"];
	var newFractionQuantity = newFractionQuantityOption.options[newFractionQuantityOption.selectedIndex].value;
	
	var newFractionParsed = newFractionQuantity.split("/");
	
	var ingredientNumeratorQuantity;
	var ingredientDenominatorQuantity;
	var currentShoppingListNameInput = updateIngredientForm.elements["currentPantryListName"];
	
	var originalIngredientWholeNumber = document.getElementById("originalIngredientWholeNumber");
	var originalIngredientNumerator = document.getElementById("originalIngredientNumerator");
	var originalIngredientDenominator = document.getElementById("originalIngredientDenominator");
	
	if(originalIngredientWholeNumber === undefined || originalIngredientWholeNumber === null) {
		document.getElementById("subtractPantryListIngredientErrorMessage").className = "alert alert-danger";
		document.getElementById("subtractPantryListIngredientErrorMessage").innerHTML = "unable to retrieve the ingredient whole number.";
		return false;
	}	
	
	if(originalIngredientNumerator === undefined || originalIngredientNumerator === null) {
		document.getElementById("subtractPantryListIngredientErrorMessage").className = "alert alert-danger";
		document.getElementById("subtractPantryListIngredientErrorMessage").innerHTML = "unable to retrieve the ingredient numerator.";
		return false;
	}
	
	if(originalIngredientDenominator === undefined || originalIngredientDenominator === null) {
		document.getElementById("subtractPantryListIngredientErrorMessage").className = "alert alert-danger";
		document.getElementById("subtractPantryListIngredientErrorMessage").innerHTML = "unable to retrieve the ingredient denominator.";
		return false;
	}
	
	if(newFractionParsed.length !== 2) {
		document.getElementById("subtractPantryListIngredientErrorMessage").className = "alert alert-danger";
        document.getElementById("subtractPantryListIngredientErrorMessage").innerHTML = "The fraction input is not in fraction form.";
        return false;
	}
	else {
		ingredientNumeratorQuantity = newFractionParsed[0];
		ingredientDenominatorQuantity = newFractionParsed[1];
		if(!(checkFractionForm(ingredientNumeratorQuantity, ingredientDenominatorQuantity))) {
			document.getElementById("subtractPantryListIngredientErrorMessage").className = "alert alert-danger";
	        document.getElementById("subtractPantryListIngredientErrorMessage").innerHTML = "The numerator should be less than the denominator";
	        return false;
		}
	}
	
	if(!stringInput(currentIngredientName)) {
		document.getElementById("subtractPantryListIngredientErrorMessage").className = "alert alert-danger";
        document.getElementById("subtractPantryListIngredientErrorMessage").innerHTML = "The ingredient name must be letters only and up to 40 letters.";
        return false;
	}
    
    if(!wholeNumberCheck(wholeNumberQuantity))
    {
    	document.getElementById("subtractPantryListIngredientErrorMessage").className = "alert alert-danger";
        document.getElementById("subtractPantryListIngredientErrorMessage").innerHTML = "The whole number must be between 0 and 20.";
        return false;
    }
    
    if(!numberInput(ingredientNumeratorQuantity))
    {
    	document.getElementById("subtractPantryListIngredientErrorMessage").className = "alert alert-danger";
        document.getElementById("subtractPantryListIngredientErrorMessage").innerHTML = "The ingredient numerator must be a number and up to 2 digits.";
        return false;
    }
    if(!numberInput(ingredientDenominatorQuantity))
    {
    	document.getElementById("subtractPantryListIngredientErrorMessage").className = "alert alert-danger";
        document.getElementById("subtractPantryListIngredientErrorMessage").innerHTML = "The ingredient denominator must be a number and up to 2 digits.";
        return false;
    }
    if(currentShoppingListNameInput === undefined || currentShoppingListNameInput === null) {
    	document.getElementById("subtractPantryListIngredientErrorMessage").className = "alert alert-danger";
        document.getElementById("subtractPantryListIngredientErrorMessage").innerHTML = "The shopping list name is missing.";
    	return false;
    }
    
    // must verify if the amount to be subtracted is a valid amount. less than or equal to the amount currently on the pantry list.
	// grab the values from the hidden fields to do this check!
    if(!(canSubtractIngredientQuantity(wholeNumberQuantity, ingredientNumeratorQuantity, ingredientDenominatorQuantity, originalIngredientWholeNumber, originalIngredientNumerator, originalIngredientDenominator))) {
    	document.getElementById("subtractPantryListIngredientErrorMessage").className = "alert alert-danger";
        document.getElementById("subtractPantryListIngredientErrorMessage").innerHTML = "You cannot subtract more than the quantity that you have of the ingredient.";
    	return false;
    }

	document.getElementById("subtractPantryListIngredientErrorMessage").className = "";
    document.getElementById("subtractPantryListIngredientErrorMessage").innerHTML = "";
	
	return true;
}

function canSubtractIngredientQuantity(newWholeNumber, newNumerator, newDenominator, originalWholeNumber, originalNumerator, originalDenominator) {
    var wholeNumberQuantityParsed = parseInt(newWholeNumber);
	var ingredientNumeratorQuantityParsed = parseInt(newNumerator);
	var ingredientDenominatorQuantityParsed = parseInt(newDenominator);
	
	var originalIngredientWholeNumberParsed = parseInt(originalWholeNumber);
	var originalIngredientNumeratorParsed = parseInt(originalNumerator);
	var originalIngredientDenominatorParsed = parseInt(originalDenominator);
	
	var newAmountFraction = math.fraction((wholeNumberQuantityParsed * ingredientDenominatorQuantityParsed) + ingredientNumeratorQuantityParsed, ingredientDenominatorQuantityParsed);
	var originalAmountFraction = math.fraction((originalIngredientWholeNumberParsed * originalIngredientDenominatorParsed) + originalIngredientNumeratorParsed, originalIngredientDenominatorParsed);
	
	var comparisonResult = math.compare(newAmountFraction, originalAmountFraction);
	
	if(math.number(comparisonResult) === 1) {
		return false;
	}
	else {
		return true;
	}
	
	
}