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

function servingsCheck(wholeNumber) {
	var wholeNumberParsed = parseInt(wholeNumber);
	if(wholeNumberParsed > 0 && wholeNumberParsed < 21) {
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

function validateRecipeCreate(csrfValue) {

	// for now imageurl checking isn't done.
	var createRecipeForm = document.getElementById("createRecipeForm");
	
	var recipeName = createRecipeForm.elements["recipeName"];
	var servingsFieldWidget = createRecipeForm.elements["servingsField"];
	var prepHoursWidget = createRecipeForm.elements["prepHours"];
	var prepMinutesWidget = createRecipeForm.elements["prepMinutes"];
	var prepSecondsWidget = createRecipeForm.elements["prepSeconds"];
	var cookHoursWidget = createRecipeForm.elements["cookHours"];
	var cookMinutesWidget = createRecipeForm.elements["cookMinutes"];
	var cookSecondsWidget = createRecipeForm.elements["cookSeconds"];
	var imageURLInput = createRecipeForm.elements["imageURL"];
	var dishTypeInput = createRecipeForm.elements["dishType"];
	var recipeDescriptionInput = createRecipeForm.elements["recipeDescription"];
	
	var prepTimeInput = createRecipeForm.elements["prepTime"];
	var cookTimeInput = createRecipeForm.elements["cookTime"];
	
	var servingsField = servingsFieldWidget.options[servingsFieldWidget.selectedIndex].value;
	var prepHoursField = prepHoursWidget.options[prepHoursWidget.selectedIndex].value;
	var prepMinutesField = prepMinutesWidget.options[prepMinutesWidget.selectedIndex].value;
	var prepSecondsField = prepSecondsWidget.options[prepSecondsWidget.selectedIndex].value;
	var cookHoursField = cookHoursWidget.options[cookHoursWidget.selectedIndex].value;
	var cookMinutesField = cookMinutesWidget.options[cookMinutesWidget.selectedIndex].value;
	var cookSecondsField = cookSecondsWidget.options[cookSecondsWidget.selectedIndex].value;
	
	
	
	var dishTypeField = dishTypeInput.value;
	var recipeDescriptionField = recipeDescriptionInput.value;
	
	if(!recipeNameCheck(recipeName.value)) {
		document.getElementById("createRecipeInfo").className = "registrationError";
		document.getElementById("createRecipeInfo").innerHTML = "The recipe name must be 6 to 60 characters only.";
		return false;
	}
	if(!servingsCheck(servingsField)) {
		document.getElementById("createRecipeInfo").className = "registrationError";
		document.getElementById("createRecipeInfo").innerHTML = "The servings must be between 1 and 20.";
		return false;
	}
	if(!hoursCheck(prepHoursField)) {
		document.getElementById("createRecipeInfo").className = "registrationError";
		document.getElementById("createRecipeInfo").innerHTML = "The prep hours must be between 0 and 11.";
		return false;
	}
	if(!minutesCheck(prepMinutesField)) {
		document.getElementById("createRecipeInfo").className = "registrationError";
		document.getElementById("createRecipeInfo").innerHTML = "The prep minutes must be between 0 and at most 55.";
		return false;
	}
	if(!secondsCheck(prepSecondsField)) {
		document.getElementById("createRecipeInfo").className = "registrationError";
		document.getElementById("createRecipeInfo").innerHTML = "The prep seconds must be between 0 and at most 60.";
		return false;
	}
	if(!hoursCheck(cookHoursField)) {
		document.getElementById("createRecipeInfo").className = "registrationError";
		document.getElementById("createRecipeInfo").innerHTML = "The cook hours must be between 0 and 11.";
		return false;
	}
	if(!minutesCheck(cookMinutesField)) {
		document.getElementById("createRecipeInfo").className = "registrationError";
		document.getElementById("createRecipeInfo").innerHTML = "The cook minutes must be between 0 and at most 55.";
		return false;
	}
	if(!secondsCheck(cookSecondsField)) {
		document.getElementById("createRecipeInfo").className = "registrationError";
		document.getElementById("createRecipeInfo").innerHTML = "The cook seconds must be between 0 and at most 60.";
		return false;
	}
	if(!dishTypeCheck(dishTypeField)) {
		document.getElementById("createRecipeInfo").className = "registrationError";
		document.getElementById("createRecipeInfo").innerHTML = "The dish type must be between 4 and 20 characters and be letters and white space(s) only.";
		return false;
	}
	if(imageURLInput === null || imageURLInput === undefined) {
		document.getElementById("createRecipeInfo").className = "registrationError";
		document.getElementById("createRecipeInfo").innerHTML = "The URL image field is missing.";
		return false;
	}
	if(recipeDescriptionField.length < 4) {
		document.getElementById("createRecipeInfo").className = "registrationError";
		document.getElementById("createRecipeInfo").innerHTML = "The description should be at least 4 letters.";
		return false;
	}
	
	prepTimeInput.value = prepHoursField + "/" + prepMinutesField + "/" + prepSecondsField;
	cookTimeInput.value = cookHoursField + "/" + cookMinutesField + "/" + cookSecondsField;
	
	document.getElementById("createRecipeInfo").className = "";
	document.getElementById("createRecipeInfo").innerHTML = "";
	return true;

}