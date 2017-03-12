function checkUserNameRegistration() {
	var registrationForm  = document.getElementById("registrationForm");
	var userNameField = registrationForm.elements["registerUserName"].value;
	var currentPath = document.head.querySelector("[name=currentPath]").content;
	$.ajax({
		url: currentPath + "/checkRegistrationUserNameField",
		type : "GET",
		data: 'userName=' + userNameField,
		success: function(data) {
			document.getElementById("registrationErrorField").className = "";
			document.getElementById("registrationErrorField").innerHTML = "";
			document.getElementById("register").disabled = false;
		},
		error: function(jqXHR) {
			document.getElementById("registrationErrorField").className = "registrationError";
			document.getElementById("registrationErrorField").innerHTML = jqXHR.responseText;
			document.getElementById("register").disabled = true;
		}
	});
	return false;
}

function verifyPassword() {
	var passwordRegex= new RegExp("(((?=.*\\d)?)(?=.*[a-z])((?=.*[A-Z])?)((?=.*[@#$%])?)(?!.*[\\s]).{6,20})");
	
	var registrationForm  = document.getElementById("registrationForm");
	var passwordField = registrationForm.elements["registerPassword"].value;

	if(passwordRegex.test(passwordField)) {
		document.getElementById("registrationErrorField").className = "";
		document.getElementById("registrationErrorField").innerHTML = "";
		return true;
	} 
	else {
		document.getElementById("registrationErrorField").className = "registrationError";
		document.getElementById("registrationErrorField").innerHTML = "The password must have at least 6 letters and cannot have space(s) in it.";
		return false;
	}
}

function verifyCheckPassword() {
	var passwordRegex= new RegExp("(((?=.*\\d)?)(?=.*[a-z])((?=.*[A-Z])?)((?=.*[@#$%])?)(?!.*[\\s]).{6,20})");
	var registrationForm  = document.getElementById("registrationForm");
	var passwordField = registrationForm.elements["registerPassword"].value;
	var verifyPasswordField = registrationForm.elements["registerValidatePassword"].value;
	
	if(!(passwordField === verifyPasswordField)) {
		document.getElementById("registrationErrorField").className = "registrationError";
		document.getElementById("registrationErrorField").innerHTML = "Passwords must match."
		return false;
	}
	else if(!(passwordRegex.test(verifyPasswordField))) {
		document.getElementById("registrationErrorField").className = "registrationError";
		document.getElementById("registrationErrorField").innerHTML = "The password must have at least 6 letters and cannot have space(s) in it.";
		return false;
	}
	else {
		document.getElementById("registrationErrorField").className = "";
		document.getElementById("registrationErrorField").innerHTML = "";
		return true;
	}
}

function verifyPasswords() {
	if(verifyPassword() && verifyCheckPassword()) 
		return true;
	else {
		return false;
	}
}
