function testCharacters(charToTest) {
	alert(charToTest);
}

function ratingInput(ratingValue) {
	var inputValueParsed = parseInt(ratingValue);
	if(inputValueParsed > 0 && inputValueParsed <= 5) {
		return true;
	}
	return false;
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

function fillUpdateField(currentIngredientName, outputType, wholeNumber, numeratorQuantity, denominatorQuantity, 
	ingredientUnit, ingredientType) {
	// set update fields to relevant content.
	
	var modifyForm = document.getElementById("modifySingleIngredientForm");
	var ingredientName = modifyForm.elements["currentIngredientName"];
	var wholeNumberQuantityOption = modifyForm.elements["newWholeNumberQuantity"];
	var ingredientQuantityOption = modifyForm.elements["newIngredientQuantity"];
	var ingredientUnitOption = modifyForm.elements["newIngredientUnit"];
	var ingredientTypeOption = modifyForm.elements["newIngredientType"];
	
	if(ingredientName === undefined || ingredientName === null) {
		document.getElementById("modifyIngredientErrorMessage").className = "alert alert-danger";
		document.getElementById("modifyIngredientErrorMessage").innerHTML = "ingredient name can not be retrieved";
		return false;
	}
	
	var currentIngredientNameParsed = currentIngredientName.replace(/&#8216;/g, "'");
	currentIngredientNameParsed = currentIngredientNameParsed.replace(/&quot;/g, "\"");
	ingredientName.value = currentIngredientNameParsed;
	
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
	
	document.getElementById("modifyIngredientErrorMessage").className = "";
	document.getElementById("modifyIngredientErrorMessage").innerHTML = "";
	
	return false;
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

function checkServings() {
	var modifyServingsForm = document.getElementById("modifyRecipeServingsForm");
	var newServingsAmount = modifyServingsForm.elements["newServings"].value;
	return numberInput(newServingsAmount);
}

function getCSRFToken(csrfTokenKey) {
	var pageCookies = document.cookie;
	if(pageCookies !== undefined && pageCookies !== null) {
		var cookies = pageCookies.split(";");
		// look for the csrftoken
		for(var i = 0; cookies.length; i++) {
			var cookieUnparsed = cookies[i].split("=");
			if(cookieUnparsed.length === 2) {
				if(cookieUnparsed[0] === csrfTokenKey) {
					return cookieUnparsed[1];
				}
			}
		}
	}
	return null;
}

function callDeleteRecipe() {
	document.getElementById("deleteRecipeForm").submit();
}

function removeListItem(id, recipeName) {
	var csrfTokenValue = document.head.querySelector("[name=_csrf]").content;
	var currentPath = document.head.querySelector("[name=currentPath]").content;
	$.ajax({
		url: currentPath + "/removeRecipeIngredient",
		type : "POST",
		data: "ingredientID=" + id + "&recipeName=" + recipeName + "&_csrf=" + csrfTokenValue,
		success: function(data) {
			var element = document.getElementById("ingredient-" + id);
			element.remove();
			document.getElementById("ingredientRemovalInfo").className = "";
			document.getElementById("ingredientRemovalInfo").innerHTML = "";
		},
		error: function(jqXHR, textStatus, errorThrown) {
			document.getElementById("ingredientRemovalInfo").className = "alert alert-danger";
			document.getElementById("ingredientRemovalInfo").innerHTML = jqXHR.responseText;
		}
	});
	return false;
}

function addIngredient(recipeName, csrfName) {
	
	var ingredientAddForm = document.getElementById("addIngredientsForm");
	var ingredientName = ingredientAddForm.elements["ingredientName"].value;
	
	var wholeNumberOption = ingredientAddForm.elements["wholeNumberQuantity"];
	var wholeNumber = wholeNumberOption.options[wholeNumberOption.selectedIndex].value;
	
	var fractionNumberOption = ingredientAddForm.elements["ingredientQuantity"];
	var fractionNumber = fractionNumberOption.options[fractionNumberOption.selectedIndex].value;
	var fractionNumberParsed = fractionNumber.split("/");
	var numeratorValue;
	var denominatorValue;
	
	var ingredientUnitOption = ingredientAddForm.elements["ingredientUnit"];
	var ingredientUnit = ingredientUnitOption.options[ingredientUnitOption.selectedIndex].value;
	
	var ingredientTypeOption = ingredientAddForm.elements["ingredientType"];
	var ingredientType = ingredientTypeOption.options[ingredientTypeOption.selectedIndex].value;
	
	var csrfHidden = ingredientAddForm.elements[csrfName];
	var currentPath = document.head.querySelector("[name=currentPath]").content;
	
	
	if(csrfHidden === null || csrfHidden === undefined) {
		document.getElementById("addErrorMessage").className = "alert alert-danger";
        document.getElementById("addErrorMessage").innerHTML = "Csrf token is necessary.";
		return false;
	}
	
	if(fractionNumberParsed.length !== 2) {
		document.getElementById("addErrorMessage").className = "alert alert-danger";
        document.getElementById("addErrorMessage").innerHTML = "The fraction input is not in fraction form.";
        return false;
	}
	else {
		numeratorValue = fractionNumberParsed[0];
		denominatorValue = fractionNumberParsed[1];
		if(!(checkFractionForm(numeratorValue, denominatorValue))) {
			document.getElementById("addErrorMessage").className = "alert alert-danger";
	        document.getElementById("addErrorMessage").innerHTML = "The numerator should be less than the denominator";
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
        document.getElementById("addErrorMessage").className = "alert alert-danger";
        document.getElementById("addErrorMessage").innerHTML = "The ingredient name must be letters only and up to 40 letters.";
        return false;
    }
    
    if(!stringInput(ingredientType)) {
    	document.getElementById("addErrorMessage").className = "alert alert-danger";
        document.getElementById("addErrorMessage").innerHTML = "The ingredient type must be letters only and up to 40 letters.";
        return false;
    }
    
    if(!wholeNumberCheck(wholeNumber)) {
    	document.getElementById("addErrorMessage").className = "alert alert-danger";
        document.getElementById("addErrorMessage").innerHTML = "The whole number must be between 0 and 20.";
        return false;
    }
	    
    if(!numberInput(numeratorValue))
    {
    	document.getElementById("addErrorMessage").className = "alert alert-danger";
        document.getElementById("addErrorMessage").innerHTML = "The ingredient numerator must be a number and up to 2 digits.";
        return false;
    }
    if(!numberInput(denominatorValue))
    {
    	document.getElementById("addErrorMessage").className = "alert alert-danger";
        document.getElementById("addErrorMessage").innerHTML = "The ingredient denominator must be a number and up to 2 digits.";
        return false;
    }
    
    var serializedAddIngredientFormData = $('#addIngredientsForm');
    $.ajax({
		url: currentPath + "/addRecipeIngredient",
		type : "POST",
		/*
		data: 'recipeName=' + recipeName + '&ingredientName=' + ingredientName + '&ingredientUnit=' + ingredientUnit + 
		'&wholeNumberQuantity=' + wholeNumber + '&ingredientQuantity=' + fractionNumber + '&ingredientType=' + ingredientType +
		'&' + csrfHidden.name + '=' + csrfHidden.value,
		*/
		data: serializedAddIngredientFormData.serialize(),
		success: function(data) {
			var returnedData = data.split(",");
			if(returnedData.length == 2) {
				var ingredientId = returnedData[0];
				var displayType = returnedData[1];
				if(displayType === "1") {
					if(wholeNumber > 0 && (numeratorValue == 1 && denominatorValue == 1)) {
						numeratorValue = 0;
					}
					else if(wholeNumber == 0 &&(numeratorValue == 1 && denominatorValue == 1)) {
						wholeNumber = 1;
						numeratorValue = 0;
					}
					addItemWholeNumberOnly(recipeName, ingredientId, ingredientName, ingredientUnit, wholeNumber, ingredientType);
				}
				else if(displayType === "2") {
					addItemMixedFraction(recipeName, ingredientId, ingredientName, ingredientUnit, wholeNumber, numeratorValue, denominatorValue, ingredientType);
				}
				else if(displayType === "3") {
					addItemFractionForm(recipeName, ingredientId, ingredientName, ingredientUnit, numeratorValue, denominatorValue, ingredientType);
				}
				document.getElementById("addIngredientsForm").reset();
			}
			document.getElementById("addErrorMessage").className = '';
			document.getElementById("addErrorMessage").innerHTML = '';
		},
		error: function(jqXHR) {
			document.getElementById("addErrorMessage").className = "alert alert-danger";
			document.getElementById("addErrorMessage").innerHTML = jqXHR.responseText;
		}
	});
	return false;
}

function addItemFractionForm(recipeName, ingredientId, ingredientName, ingredientUnit, ingredientNumeratorQuantity, ingredientDenominatorQuantity, ingredientType) {
	var ul = document.getElementById(recipeName + "-ingredients-list");
	var li = document.createElement("li");
	var breakTag = document.createElement("br");
	var fractionSeparator = "&frasl;";
	var fractionSpanTag = document.createElement("span");
	var superScriptTag = document.createElement("sup");
	var subScriptTag = document.createElement("sub");
	var ingredientNameDelimited = ingredientName.replace(/&#8216;/g, "'");
	ingredientNameDelimited = ingredientNameDelimited.replace(/&quot;/g, "\"");
	var recipeNameDelimited = recipeName.replace(/&#8216;/g, "'");
	recipeNameDelimited = recipeNameDelimited.replace(/&quot;/g, "\"");	
	
	var removeButton = document.createElement("button");
	removeButton.setAttribute("class", "btn btn-danger");
	removeButton.setAttribute("onclick", "removeListItem" + "('" + ingredientId + "', " + "'" + recipeNameDelimited + "')");
	removeButton.appendChild(document.createTextNode("remove"));
	
	var updateButton = document.createElement("button");
	updateButton.setAttribute("class", "btn btn-danger");
	updateButton.setAttribute("onclick", "fillUpdateField" + "('" + ingredientNameDelimited + "', " + "'3', " + "'0', '" + ingredientNumeratorQuantity + "', '" + ingredientDenominatorQuantity + "', '" + ingredientUnit + "', '" + ingredientType + "')");
	updateButton.appendChild(document.createTextNode("update this"));
	
	superScriptTag.appendChild(document.createTextNode(ingredientNumeratorQuantity));
	subScriptTag.appendChild(document.createTextNode(ingredientDenominatorQuantity));
	
	li.setAttribute("id", "ingredient-" + ingredientId); 
	li.setAttribute("class", "list-group-item listInnerPadding fractionFontSizeTwo"); 
	
	fractionSpanTag.setAttribute("id", "ingredientSpan-" + ingredientId);
	fractionSpanTag.innerHTML = fractionSeparator;
	
	li.appendChild(superScriptTag);
	li.appendChild(fractionSpanTag);
	li.appendChild(subScriptTag);
	
	li.appendChild(document.createTextNode(" " + ingredientUnit + " " + ingredientName + " "));
	li.appendChild(breakTag);
	li.appendChild(removeButton);
	li.appendChild(document.createTextNode(" "));
	li.appendChild(updateButton);
	
	ul.appendChild(li);
}

function addItemMixedFraction(recipeName, ingredientId, ingredientName, ingredientUnit, ingredientWholeNumberQuantity, ingredientNumeratorQuantity, ingredientDenominatorQuantity, ingredientType) {
	var ul = document.getElementById(recipeName + "-ingredients-list");
	var li = document.createElement("li");
	var breakTag = document.createElement("br");
	var fractionSeparator = "&frasl;";
	var fractionSpanTag = document.createElement("span");
	var superScriptTag = document.createElement("sup");
	var subScriptTag = document.createElement("sub");
	var ingredientNameDelimited = ingredientName.replace(/&#8216;/g, "'");
	ingredientNameDelimited = ingredientNameDelimited.replace(/&quot;/g, "\"");
	var recipeNameDelimited = recipeName.replace(/&#8216;/g, "'");
	recipeNameDelimited = recipeNameDelimited.replace(/&quot;/g, "\"");
	
	var removeButton = document.createElement("button");
	removeButton.setAttribute("class", "btn btn-danger");
	removeButton.setAttribute("onclick", "removeListItem" + "('" + ingredientId + "', " + "'" + recipeNameDelimited + "')");
	removeButton.appendChild(document.createTextNode("remove"));
	
	var updateButton = document.createElement("button");
	updateButton.setAttribute("class", "btn btn-danger");
	updateButton.setAttribute("onclick", "fillUpdateField" + "('" + ingredientNameDelimited + "', " + "'2', '" + ingredientWholeNumberQuantity + "', '" + ingredientNumeratorQuantity + "', '" + ingredientDenominatorQuantity + "', '" + ingredientUnit + "', '" + ingredientType + "')");
	updateButton.appendChild(document.createTextNode("update this"));
	
	superScriptTag.appendChild(document.createTextNode(ingredientNumeratorQuantity));
	subScriptTag.appendChild(document.createTextNode(ingredientDenominatorQuantity));
	
	li.setAttribute("id", "ingredient-" + ingredientId); 
	li.setAttribute("class", "list-group-item listInnerPadding fractionFontSizeTwo"); 
	
	fractionSpanTag.setAttribute("id", "ingredientSpan-" + ingredientId);
	fractionSpanTag.innerHTML = fractionSeparator;
	
	li.appendChild(document.createTextNode(ingredientWholeNumberQuantity));
	li.appendChild(superScriptTag);
	li.appendChild(fractionSpanTag);
	li.appendChild(subScriptTag);
	
	li.appendChild(document.createTextNode(" " + ingredientUnit + " " + ingredientName + " "));
	li.appendChild(breakTag);
	li.appendChild(removeButton);
	li.appendChild(document.createTextNode(" "));
	li.appendChild(updateButton);
	
	ul.appendChild(li);
}

function addItemWholeNumberOnly(recipeName, ingredientId, ingredientName, ingredientUnit, ingredientWholeNumberQuantity, ingredientType) {
	var ul = document.getElementById(recipeName + "-ingredients-list");
	var li = document.createElement("li");
	var breakTag = document.createElement("br");
	var ingredientNameDelimited = ingredientName.replace(/&#8216;/g, "'");
	ingredientNameDelimited = ingredientNameDelimited.replace(/&quot;/g, "\"");
	var recipeNameDelimited = recipeName.replace(/&#8216;/g, "'");
	recipeNameDelimited = recipeNameDelimited.replace(/&quot;/g, "\"");
		
	var removeButton = document.createElement("button");
	removeButton.setAttribute("class", "btn btn-danger");
	removeButton.setAttribute("onclick", "removeListItem" + "('" + ingredientId + "', " + "'" + recipeNameDelimited + "')");
	removeButton.appendChild(document.createTextNode("remove"));
	
	var updateButton = document.createElement("button");
	updateButton.setAttribute("class", "btn btn-danger");
	updateButton.setAttribute("onclick", "fillUpdateField" + "('" + ingredientNameDelimited + "', " + "'1', '" + ingredientWholeNumberQuantity + "', '1', " + "'1', '" + ingredientUnit + "', '" + ingredientType + "')");
	updateButton.appendChild(document.createTextNode("update this"));
	
	li.setAttribute("id", "ingredient-" + ingredientId); 
	li.setAttribute("class", "list-group-item listInnerPadding fractionFontSize"); 
	
	li.appendChild(document.createTextNode(ingredientWholeNumberQuantity + " " + ingredientUnit + " " + ingredientName + " "));
	li.appendChild(breakTag);
	li.appendChild(removeButton);
	li.appendChild(document.createTextNode(" "));
	li.appendChild(updateButton);
	ul.appendChild(li);
}

// will require a page refresh.
function updateQuantity() {
	
	var updateIngredientForm = document.getElementById("modifySingleIngredientForm");
	
	var currentIngredientName = updateIngredientForm.elements["currentIngredientName"].value;
	
	var wholeNumberQuantityOption = updateIngredientForm.elements["newWholeNumberQuantity"];
	var wholeNumberQuantity = wholeNumberQuantityOption.options[wholeNumberQuantityOption.selectedIndex].value;
	
	var newFractionQuantityOption = updateIngredientForm.elements["newIngredientQuantity"];
	var newFractionQuantity = newFractionQuantityOption.options[newFractionQuantityOption.selectedIndex].value;
	
	var newFractionParsed = newFractionQuantity.split("/");
	
	var ingredientNumeratorQuantity;
	var ingredientDenominatorQuantity;
	
	
	if(newFractionParsed.length !== 2) {
		document.getElementById("modifyDirectionErrorMessage").className = "alert alert-danger";
        document.getElementById("modifyDirectionErrorMessage").innerHTML = "The fraction input is not in fraction form.";
        return false;
	}
	else {
		ingredientNumeratorQuantity = newFractionParsed[0];
		ingredientDenominatorQuantity = newFractionParsed[1];
		if(!(checkFractionForm(ingredientNumeratorQuantity, ingredientDenominatorQuantity))) {
			document.getElementById("modifyDirectionErrorMessage").className = "alert alert-danger";
	        document.getElementById("modifyDirectionErrorMessage").innerHTML = "The numerator should be less than the denominator";
	        return false;
		}
	}
	if(!stringInput(currentIngredientName)) {
		document.getElementById("modifyDirectionErrorMessage").className = "alert alert-danger";
        document.getElementById("modifyDirectionErrorMessage").innerHTML = "The ingredient name must be letters only and up to 40 letters.";
        return false;
	}
    
    if(!wholeNumberCheck(wholeNumberQuantity))
    {
    	document.getElementById("modifyDirectionErrorMessage").className = "alert alert-danger";
        document.getElementById("modifyDirectionErrorMessage").innerHTML = "The whole number must be between 0 and 20.";
        return false;
    }
    
    if(!numberInput(ingredientNumeratorQuantity))
    {
    	document.getElementById("modifyDirectionErrorMessage").className = "alert alert-danger";
        document.getElementById("modifyDirectionErrorMessage").innerHTML = "The ingredient numerator must be a number and up to 2 digits.";
        return false;
    }

    if(!numberInput(ingredientDenominatorQuantity))
    {
    	document.getElementById("modifyDirectionErrorMessage").className = "alert alert-danger";
        document.getElementById("modifyDirectionErrorMessage").innerHTML = "The ingredient denominator must be a number and up to 2 digits.";
        return false;
    }
	
	document.getElementById("modifyDirectionErrorMessage").className = "";
    document.getElementById("modifyDirectionErrorMessage").innerHTML = "";
	
	return true;
}


// recipe directions functions.

function addDirectionItem() {
	var directionAddForm = document.getElementById("addDirectionsForm");
	var directionContent = directionAddForm.elements["directionContent"].value;
	var recipeName = directionAddForm.elements["recipeNameDirection"].value;
	
	var csrfValue = directionAddForm.elements["_csrf"].value;
	var currentPath = document.head.querySelector("[name=currentPath]").content;
	
	var serializedAddDirectionFormData = $('#addDirectionsForm');
	 $.ajax({
		 url: currentPath + "/addRecipeDirection",
		 type : "POST",
		 /*
		 data: 'recipeNameDirection=' + recipeName + '&directionContent=' + directionContent + '&_csrf=' + csrfValue,
		 */
		 data: serializedAddDirectionFormData.serialize(),
		 success: function(data) {
			 var returnedData = data.split(",");
			 if(returnedData.length == 2) {
				 var newDirectionNumber = returnedData[0];
				 var recentDirectionId = returnedData[1];
				 
				 // add the direction to the directions list
				 addDirectionToList(newDirectionNumber, recentDirectionId, recipeName, directionContent);
				 document.getElementById("addDirectionsForm").reset();
			 }
			 document.getElementById("addDirectionMessage").className = "";
			 document.getElementById("addDirectionMessage").innerHTML = "";
		 },
		 error: function(jqXHR) {
			 document.getElementById("addDirectionMessage").className = "alert alert-danger";
			 document.getElementById("addDirectionMessage").innerHTML = jqXHR.responseText;
		 }
	 });
	 return false;
}

function addDirectionToList(newDirectionNumber, recentDirectionId, recipeName, directionContent) {
	var directionsList = document.getElementById(recipeName + "-directions-list");
	var directionItem = document.createElement("li");
	var removeLinkTag = document.createElement("a");
	var breakTag = document.createElement("br");
	var removeButton = document.createElement("button");
	var updateButton = document.createElement("button");
	var directionContentDelimited = directionContent.replace(/&#8216;/g, "'");
	directionContentDelimited.replace(/&quot;/g, "\"");
		
	var removeLinkContent = "removeRecipeDirection?directionId=" + recentDirectionId + "&recipeName=" + recipeName +  "&directionNumber=" + newDirectionNumber;
	
	directionItem.setAttribute("id", "direction-" + recentDirectionId);
	directionItem.setAttribute("class", "list-group-item listInnerPadding listGroupShowLabel");
	directionItem.appendChild(document.createTextNode(directionContent + " "));
	removeButton.setAttribute("class", "btn btn-danger");
	removeButton.appendChild(document.createTextNode("remove"));
	
	removeLinkTag.setAttribute("href", removeLinkContent);
	removeLinkTag.appendChild(removeButton);
	updateButton.setAttribute("class", "btn btn-danger");
	updateButton.setAttribute("onclick", "setDirectionUpdate(" + "'" + newDirectionNumber  + "', " + "'" + directionContentDelimited + "')");
	updateButton.appendChild(document.createTextNode("update"));
	
	directionItem.appendChild(breakTag);
	directionItem.appendChild(removeLinkTag);
	directionItem.appendChild(document.createTextNode(" "));
	directionItem.appendChild(updateButton);
	directionsList.appendChild(directionItem);
}


function setDirectionUpdate(directionNumber, directionContents) {
	if(document.getElementById("modifiedDirectionNumber") === undefined || document.getElementById("modifiedDirectionNumber") === null) {
		document.getElementById("modifyDirectionErrorMessage").className = "alert alert-danger";
		document.getElementById("modifyDirectionErrorMessage").innerHTML = "cannot set the direction number.";
		return false;
	}
	document.getElementById("modifiedDirectionNumber").value = directionNumber;
	
	if(document.getElementById("modifyDirectionContent") === undefined || document.getElementById("modifyDirectionContent") === null) {
		document.getElementById("modifyDirectionErrorMessage").className = "alert alert-danger";
		document.getElementById("modifyDirectionErrorMessage").innerHTML = "cannot set the direction content.";
		return false;
	}
	
	var directionContentsParsed = directionContents.replace(/&#8216;/g, "'");
	directionContentsParsed = directionContentsParsed.replace(/&quot;/g, "\"");
	document.getElementById("modifyDirectionContent").value = directionContentsParsed;
	
	document.getElementById("modifyDirectionErrorMessage").className = "";
	document.getElementById("modifyDirectionErrorMessage").innerHTML = "";
	return false;
}

function checkDirectionUpdateField() {
	var updateDirectionsForm = document.getElementById("updateDirectionsForm");
	var directionNumber = updateDirectionsForm.elements["modifiedDirectionNumber"].value;
	if(!numberInput(directionNumber)) {
		document.getElementById("modifyDirectionErrorMessage").className = "alert alert-danger";
		document.getElementById("modifyDirectionErrorMessage").innerHTML = 'The direction number must be a valid between 1 and 99.';
		return false;
	}
	document.getElementById("modifyDirectionErrorMessage").className = "";
	document.getElementById("modifyDirectionErrorMessage").innerHTML = "";
	return true;
}

// recipe reviews functions.

function addRecipeReview() {
	// get all the contents from the form.
	var addReviewForm = document.getElementById("addReviewForm");
	var selectWidget = addReviewForm.elements["reviewRatingOption"];
	var reviewAmount = selectWidget.options[selectWidget.selectedIndex].value;
	var reviewTitle = addReviewForm.elements["reviewTitle"].value;
	var reviewContent = addReviewForm.elements["reviewContent"].value;
	var reviewPosterName = addReviewForm.elements["reviewPosterName"].value;
	var reviewRecipeName = addReviewForm.elements["reviewRecipeName"].value;
	var csrfTokenValue = addReviewForm.elements["_csrf"].value;
	var currentPath = document.head.querySelector("[name=currentPath]").content;
	
	if(reviewTitle.length < 1 || reviewTitle.length > 70) {
		document.getElementById("addReviewMessage").className = "alert alert-danger";
		document.getElementById("addReviewMessage").innerHTML = "The title must be between 1 and 70 characters.";
		return false;
	}

	if(ratingInput(reviewAmount)) {
		var serializedAddReviewFormData = $('#addReviewForm');
		$.ajax({
			 url: currentPath + "/addRecipeReview",
			 type : "POST",
			 //data: 'reviewContent=' + reviewContent + '&recipeRating=' + reviewAmount  + '&reviewTitle=' + reviewTitle + '&userPostedBy=' + reviewPosterName + '&recipeName=' + reviewRecipeName + '&_csrf=' + csrfTokenValue,
			 data: serializedAddReviewFormData.serialize(),
			 success: function(data) {
				 var returnedData = data.split(",");
				 if(returnedData.length == 5) {
					 var reviewId = returnedData[0];
					 var reviewPostedDate = returnedData[1];
					 var newAverageRating = returnedData[2];
					 var newNumberOfReviews = returnedData[3];
					 var newQuantityAmount = returnedData[4];
					 
					 // add the review to the reviews list					 
					 addReviewToFront(reviewRecipeName, reviewContent, reviewId, reviewAmount, reviewTitle, reviewPostedDate, reviewPosterName, newAverageRating, newNumberOfReviews, newQuantityAmount);
				 
					 document.getElementById("addReviewForm").reset();
				 }
			 },
			 error: function(jqXHR) {
				 document.getElementById("addReviewMessage").className = "alert alert-danger";
				 document.getElementById("addReviewMessage").innerHTML = jqXHR.responseText;
			 }
		 });
	}
	else {
		document.getElementById("addReviewMessage").className = "alert alert-danger";
		document.getElementById("addReviewMessage").innerHTML = "The review amount must be betweeen 1 and 5";
		return false;
	}
	
	document.getElementById("addReviewMessage").className = "";
	document.getElementById("addReviewMessage").innerHTML = "";
	return false;
}

function removeRecipeReview(reviewId, reviewRating, recipeName) {
	var recipeNameParsed = recipeName.replace(/&#8216;/g, "'");
	recipeNameParsed = recipeNameParsed.replace(/&quot;/g, "\"");
	var currentPath = document.head.querySelector("[name=currentPath]").content;
	$.ajax({
		url: currentPath + "/removeRecipeReview",
		type : "GET",
		data: 'reviewId=' + reviewId + '&reviewRating=' + reviewRating + '&recipeName=' + recipeNameParsed,
		success: function(data) {
			var returnedData = data.split(",");
			if(returnedData.length == 3) {
				var averageReviewRating = returnedData[0];
				var updatedReviewQuantity = returnedData[1];
				var totalReviews = returnedData[2];
				
				document.getElementById("averageRating").innerHTML = averageReviewRating;
				document.getElementById("totalReviews").innerHTML = totalReviews + " reviews";
				
				if(reviewRating === '1') {
					document.getElementById("oneStarReviews").innerHTML = updatedReviewQuantity + ' 1 star reviews';
				}
				else if(reviewRating === '2') {
					document.getElementById("twoStarReviews").innerHTML = updatedReviewQuantity + ' 2 star reviews';
				}
				else if(reviewRating === '3') {
					document.getElementById("threeStarReviews").innerHTML = updatedReviewQuantity + ' 3 star reviews';
				}
				else if(reviewRating === '4') {
					document.getElementById("fourStarReviews").innerHTML = updatedReviewQuantity + ' 4 star reviews';			
				}
				else if(reviewRating === '5') {
					document.getElementById("fiveStarReviews").innerHTML = updatedReviewQuantity + ' 5 star reviews';
				}
			}
			var element = document.getElementById("review-" + reviewId);
			element.remove();
			document.getElementById("editReviewMessage").className = "";
			document.getElementById("editReviewMessage").innerHTML = "";
		},
		error: function(jqXHR, textStatus, errorThrown) {
			document.getElementById("editReviewMessage").className = "alert alert-danger";
			document.getElementById("editReviewMessage").innerHTML = jqXHR.responseText;
		}
	});
	return false;
}

function setReviewUpdate(reviewContent, ratingField, reviewTitle, reviewId) {
	if(document.getElementById("modifiedReviewContent") === undefined || document.getElementById("modifiedReviewContent") === null) {
		document.getElementById("editReviewMessage").className = "alert alert-danger";
		document.getElementById("editReviewMessage").innerHTML = "cannot set review contents.";
		return false;
	}
	
	var reviewContentParsed = reviewContent.replace(/&#8216;/g, "'");
	reviewContentParsed = reviewContentParsed.replace(/&quot;/g, "\"");
	document.getElementById("modifiedReviewContent").value = reviewContentParsed;
	
	if(document.getElementById("modifyReviewTitle") === undefined || document.getElementById("modifyReviewTitle") === null) {
		document.getElementById("editReviewMessage").className = "alert alert-danger";
		document.getElementById("editReviewMessage").innerHTML = "cannot set review title.";
		return false;
	}
	
	var reviewTitleParsed = reviewTitle.replace(/&#8216;/g, "'");
	reviewTitleParsed = reviewTitleParsed.replace(/&quot;/g, "\"");
	document.getElementById("modifyReviewTitle").value = reviewTitleParsed;
	
	if(ratingInput(ratingField)) {
		var ratingParsed = parseInt(ratingField);
		var selectWidget = document.getElementById("modifyReviewRatingOption");
		if(selectWidget === undefined || selectWidget === null) {
			document.getElementById("editReviewMessage").className = "alert alert-danger";
			document.getElementById("editReviewMessage").innerHTML = "cannot set review rating field.";
			return false;
		}
		selectWidget.selectedIndex = ratingParsed - 1;
	}
	else {
		document.getElementById("editReviewMessage").className = "alert alert-danger";
		document.getElementById("editReviewMessage").innerHTML = "cannot set review rating.";
		return false;
	}
	
	document.getElementById("modifiedReviewId").value = reviewId;
		
	document.getElementById("editReviewMessage").className = "";
	document.getElementById("editReviewMessage").innerHTML = "";
	return false;
}

function checkUpdatedReview() {
	var addReviewForm = document.getElementById("modifyReviewForm");
	var selectWidget = addReviewForm.elements["modifyReviewRatingOption"];
	var reviewAmount = selectWidget.options[selectWidget.selectedIndex].value;
	var reviewTitle = addReviewForm.elements["reviewTitle"];
	var reviewId = addReviewForm.elements["modifiedReviewId"];

	var parsedReviewId = parseInt(reviewId);
	
	if(parsedReviewId < 1) {
		document.getElementById("editReviewMessage").className = "alert alert-danger";
		document.getElementById("editReviewMessage").innerHTML = "Editing a non-existant review.";
		return false;
	}
	
	if(reviewTitle.length < 1 || reviewTitle.length > 70) {
		document.getElementById("editReviewMessage").className = "alert alert-danger";
		document.getElementById("editReviewMessage").innerHTML = "The review's title must be between 1 and 70 characters.";
		return false;
	}
	
	if (!(ratingInput(reviewAmount))) {
		document.getElementById("editReviewMessage").className = "alert alert-danger";
		document.getElementById("editReviewMessage").innerHTML = "The review rating must be between 1 and 5.";
		return false;
	}
	
	document.getElementById("editReviewMessage").className = "";
	document.getElementById("editReviewMessage").innerHTML = ""; 
	return true;
}


function addReviewToFront(recipeName, reviewContent, reviewId, reviewRating, reviewTitle, reviewPostedTime, userName, newAverageReviewRating, totalReviews, updatedReviewQuantity) {
	var reviewList = document.getElementById(recipeName + "-reviews-list");
	var listItem = document.createElement("li");
	var divListGroup = document.createElement("div");
	var divContentBetween = document.createElement("div");
	var innerHeaderFive = document.createElement("h5");
	var innerSmall = document.createElement("small");
	var paragraphTag = document.createElement("p");
	var outerSmall = document.createElement("small");
	var removeButton = document.createElement("button");
	var updateButton = document.createElement("button");
	var breakLineItem = document.createElement("br");
	var reviewContentDelimited = reviewContent.replace(/"/g, "&quot;");
	reviewContentDelimited = reviewContentDelimited.replace(/'/g, "&#8216;");
	var reviewTitleDelimited = reviewTitle.replace(/"/g, "&quot;");
	reviewTitleDelimited = reviewTitleDelimited.replace(/'/g, "&#8216;");
	var recipeNameDelimited = recipeName.replace(/"/g, "&quot;");
	recipeNameDelimited = recipeName.replace(/'/g, "&#8216;");

	document.getElementById("averageRating").innerHTML = newAverageReviewRating;
	document.getElementById("totalReviews").innerHTML = totalReviews + ' reviews';
	
	if(reviewRating === '1') {
		document.getElementById("oneStarReviews").innerHTML = updatedReviewQuantity + ' 1 star reviews';
	}
	else if(reviewRating === '2') {
		document.getElementById("twoStarReviews").innerHTML = updatedReviewQuantity + ' 2 star reviews';
	}
	else if(reviewRating === '3') {
		document.getElementById("threeStarReviews").innerHTML = updatedReviewQuantity + ' 3 star reviews';
	}
	else if(reviewRating === '4') {
		document.getElementById("fourStarReviews").innerHTML = updatedReviewQuantity + ' 4 star reviews';			
	}
	else if(reviewRating === '5') {
		document.getElementById("fiveStarReviews").innerHTML = updatedReviewQuantity + ' 5 star reviews';
	}
	
	innerSmall.appendChild(document.createTextNode(userName + " on " + reviewPostedTime));
	innerHeaderFive.setAttribute("class", "mb-1");
	innerHeaderFive.appendChild(document.createTextNode(reviewRating + " star(s) " + reviewTitle));
	
	divContentBetween.setAttribute("class", "d-flex w-100 justify-content-between");
	
	divContentBetween.appendChild(innerHeaderFive);
	divContentBetween.appendChild(innerSmall);

	paragraphTag.appendChild(document.createTextNode(reviewContent));
	
	removeButton.setAttribute("class", "btn btn-danger");
	removeButton.setAttribute("onclick", "removeRecipeReview(" + "'" + reviewId + "', '" + reviewRating + "', '" + recipeNameDelimited + "')");
	removeButton.appendChild(document.createTextNode("remove"));
	
	updateButton.setAttribute("class", "btn btn-danger");
	updateButton.setAttribute("onclick", "setReviewUpdate(" + "'" + reviewContentDelimited + "', '" + reviewRating + "', '" + reviewTitleDelimited + "', '" + reviewId + "')");
	updateButton.appendChild(document.createTextNode("update"));
	
	outerSmall.appendChild(removeButton);
	outerSmall.appendChild(document.createTextNode(" "));
	outerSmall.appendChild(updateButton);
	
	divListGroup.setAttribute("class", "list-group-item list-group-item-action flex-column align-items-start");
	divListGroup.appendChild(divContentBetween);
	divListGroup.appendChild(paragraphTag);
	divListGroup.appendChild(breakLineItem);
	divListGroup.appendChild(outerSmall);
	
	listItem.setAttribute("class", "listInnerPadding");
	listItem.setAttribute("id", "review-" + reviewId);
	listItem.appendChild(divListGroup);

	reviewList.insertBefore(listItem, reviewList.childNodes[0]);
}
