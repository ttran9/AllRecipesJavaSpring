/**
 * 
 */
function wholeNumberCheck(wholeNumber) {
	var wholeNumberParsed = parseInt(wholeNumber);
	if(wholeNumberParsed > -1 && wholeNumberParsed < 21) {
		return true;
	}
	else {
		return false;
	}
}

function recipeNameCheck(inputValue) {
	var letterRegex= new RegExp("^[a-zA-Z0-9' ]{6,60}$");
	
	if(letterRegex.test(inputValue)) {
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

function numberInput(inputValue) {
	var numberRegex= new RegExp("^[0-9]{1,2}$");
	
	if(numberRegex.test(inputValue)) {
		return true;
	}
	else {
		return false;
	}
}

function secondsCheck(wholeNumber) {
	var wholeNumberParsed = parseInt(wholeNumber);
	if(wholeNumberParsed >= 0 && wholeNumberParsed <= 60) {
		return true;
	}
	else {
		return false;
	}
}

function minutesCheck(wholeNumber) {
	var wholeNumberParsed = parseInt(wholeNumber);
	if(wholeNumberParsed >= 0 && wholeNumberParsed <= 55) {
		return true;
	}
	else {
		return false;
	}
}

function hoursCheck(wholeNumber) {
	var wholeNumberParsed = parseInt(wholeNumber);
	if(wholeNumberParsed >= 0 && wholeNumberParsed <= 11) {
		return true;
	}
	else {
		return false;
	}
}

function testerCaller() {
	alert("hey jimbo");
}

function fillRecipeFields(recipeNameDelimited, cookTimeHour, cookTimeMinute, cookTimeSecond, prepTimeHour, prepTimeMinute, 
		prepTimeSecond, imageURLContent, dishType, recipeDescriptionDelimited) {
	
	var editForm = document.getElementById("editRecipeForm");
	var recipeName = editForm.elements["recipeName"];
	var prepHoursWidget = editForm.elements["newPrepHours"];
	var prepMinutesWidget = editForm.elements["newPrepMinutes"];
	var prepSecondsWidget = editForm.elements["newPrepSeconds"];
	var cookHoursWidget = editForm.elements["newCookHours"];
	var cookMinutesWidget = editForm.elements["newCookMinutes"];
	var cookSecondsWidget = editForm.elements["newCookSeconds"];
	var imageURLInput = editForm.elements["newImageURL"];
	var dishTypeInput = editForm.elements["newDishType"];
	var recipeDescriptionInput = editForm.elements["newRecipeDescription"];
	var recipeNameParsed = recipeNameDelimited.replace(/&#8216;/g, "'");
	recipeNameParsed = recipeNameParsed.replace(/&quot;/g, "\"");
	recipeName.value = recipeNameParsed;
	
	for(var i = 0; i < prepHoursWidget.length; i++) {
		if(prepHoursWidget[i].value === prepTimeHour) {
			prepHoursWidget.selectedIndex = i;
			break;
		}
	}
	
	for(var i = 0; i < prepMinutesWidget.length; i++) {
		if(prepMinutesWidget[i].value === prepTimeMinute) {
			prepMinutesWidget.selectedIndex = i;
			break;
		}
	}
	
	for(var i = 0; i < prepSecondsWidget.length; i++) {
		if(prepSecondsWidget[i].value === prepTimeSecond) {
			prepSecondsWidget.selectedIndex = i;
			break;
		}
	}
	
	for(var i = 0; i < cookHoursWidget.length; i++) {
		if(cookHoursWidget[i].value === cookTimeHour) {
			cookHoursWidget.selectedIndex = i;
			break;
		}
	}
	
	for(var i = 0; i < cookMinutesWidget.length; i++) {
		if(cookMinutesWidget[i].value === cookTimeMinute) {
			cookMinutesWidget.selectedIndex = i;
			break;
		}
	}
	
	for(var i = 0; i < cookSecondsWidget.length; i++) {
		if(cookSecondsWidget[i].value === cookTimeSecond) {
			cookSecondsWidget.selectedIndex = i;
			break;
		}
	}
	
	imageURLInput.value = imageURLContent;
	dishTypeInput.value = dishType;
	
	var recipeDescriptionParsed = recipeDescriptionDelimited.replace(/&#8216;/g, "'");
	recipeDescriptionParsed = recipeDescriptionParsed.replace(/&quot;/g, "\"");
	recipeDescriptionInput.value = recipeDescriptionParsed;
	
	return false;
}


function validateRecipeEdit() {
	// for now imageurl checking isn't done.
	var editRecipeForm = document.getElementById("editRecipeForm");
	
	var recipeName = editRecipeForm.elements["recipeName"];
	var prepHoursWidget = editRecipeForm.elements["newPrepHours"];
	var prepMinutesWidget = editRecipeForm.elements["newPrepMinutes"];
	var prepSecondsWidget = editRecipeForm.elements["newPrepSeconds"];
	var cookHoursWidget = editRecipeForm.elements["newCookHours"];
	var cookMinutesWidget = editRecipeForm.elements["newCookMinutes"];
	var cookSecondsWidget = editRecipeForm.elements["newCookSeconds"];
	var imageURLInput = editRecipeForm.elements["newImageURL"];
	var dishTypeInput = editRecipeForm.elements["newDishType"];
	var recipeDescriptionInput = editRecipeForm.elements["newRecipeDescription"];
	
	var prepTimeInput = editRecipeForm.elements["newPrepTime"];
	var cookTimeInput = editRecipeForm.elements["newCookTime"];
	
	var prepHoursField = prepHoursWidget.options[prepHoursWidget.selectedIndex].value;
	var prepMinutesField = prepMinutesWidget.options[prepMinutesWidget.selectedIndex].value;
	var prepSecondsField = prepSecondsWidget.options[prepSecondsWidget.selectedIndex].value;
	var cookHoursField = cookHoursWidget.options[cookHoursWidget.selectedIndex].value;
	var cookMinutesField = cookMinutesWidget.options[cookMinutesWidget.selectedIndex].value;
	var cookSecondsField = cookSecondsWidget.options[cookSecondsWidget.selectedIndex].value;
	
	var dishTypeField = dishTypeInput.value;
	var recipeDescriptionField = recipeDescriptionInput.value;
	
	if(!recipeNameCheck(recipeName.value)) {
		document.getElementById("modifyRecipeInfo").className = "registrationError";
		document.getElementById("modifyRecipeInfo").innerHTML = "The recipe name must be 6 to 40 characters and letters only.";
		return false;
	}
	if(!hoursCheck(prepHoursField)) {
		document.getElementById("modifyRecipeInfo").className = "registrationError";
		document.getElementById("modifyRecipeInfo").innerHTML = "The prep hours must be between 0 and 11.";
		return false;
	}
	if(!minutesCheck(prepMinutesField)) {
		document.getElementById("modifyRecipeInfo").className = "registrationError";
		document.getElementById("modifyRecipeInfo").innerHTML = "The prep minutes must be between 0 and at most 55.";
		return false;
	}
	if(!secondsCheck(prepSecondsField)) {
		document.getElementById("modifyRecipeInfo").className = "registrationError";
		document.getElementById("modifyRecipeInfo").innerHTML = "The prep seconds must be between 0 and at most 60.";
		return false;
	}
	if(!hoursCheck(cookHoursField)) {
		document.getElementById("modifyRecipeInfo").className = "registrationError";
		document.getElementById("modifyRecipeInfo").innerHTML = "The cook hours must be between 0 and 11.";
		return false;
	}
	if(!minutesCheck(cookMinutesField)) {
		document.getElementById("modifyRecipeInfo").className = "registrationError";
		document.getElementById("modifyRecipeInfo").innerHTML = "The cook minutes must be between 0 and at most 55.";
		return false;
	}
	if(!secondsCheck(cookSecondsField)) {
		document.getElementById("modifyRecipeInfo").className = "registrationError";
		document.getElementById("modifyRecipeInfo").innerHTML = "The cook seconds must be between 0 and at most 60.";
		return false;
	}
	if(!dishTypeCheck(dishTypeField)) {
		document.getElementById("modifyRecipeInfo").className = "registrationError";
		document.getElementById("modifyRecipeInfo").innerHTML = "The dish type must be between 4 and 20 characters and be letters and white space(s) only.";
		return false;
	}
	if(imageURLInput === null || imageURLInput === undefined) {
		document.getElementById("modifyRecipeInfo").className = "registrationError";
		document.getElementById("modifyRecipeInfo").innerHTML = "The URL image field is missing.";
		return false;
	}
	if(recipeDescriptionField.length < 4) {
		document.getElementById("modifyRecipeInfo").className = "registrationError";
		document.getElementById("modifyRecipeInfo").innerHTML = "The description should be at least 4 letters.";
		return false;
	}
	
	prepTimeInput.value = prepHoursField + "/" + prepMinutesField + "/" + prepSecondsField;
	cookTimeInput.value = cookHoursField + "/" + cookMinutesField + "/" + cookSecondsField;
	
	document.getElementById("modifyRecipeInfo").className = "";
	document.getElementById("modifyRecipeInfo").innerHTML = "";
	return true;

}