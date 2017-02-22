/**
 * 
 */
function checkHiddenField() {
	var checkBoxInput = document.getElementById("rememberMeCheckBox");
	var rememberMeInput = document.getElementById("remember-me");
	/*
	alert("object" + rememberMeInput.getAttribute("disabled"));
	rememberMeInput.removeAttribute("disabled");
	*/
	if(checkBoxInput.checked === true) {
		//rememberMeInput.removeAttribute("disabled"); 
		//document.getElementById("remember-me").disabled = false;
		rememberMeInput.disabled = false;
		//alert("remember me has been enabled");
	}
	else if(checkBoxInput.checked === false) {
		//rememberMeInput.addAttribute("disabled"); 
		//document.getElementById("remember-me").disabled = true;
		rememberMeInput.disabled = true;
		//alert("remember me has been disabled");
	}
	// if null implies that the input is not disabled, so add an attribute.
	//return false;
}