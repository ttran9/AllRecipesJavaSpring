<%@ include file="preprocessorIncludes.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<%@ include file = "cssInclude.jsp"%>
	<script type="text/javascript" src="<c:url value="/resources/javaScript/jquery-3.1.1.min.js"/>"></script>
	<script type="text/javascript" src = "<c:url value="/resources/javaScript/bootstrap.min.js"/>"></script>
	<script type="text/javascript" src = "<c:url value="/resources/javaScript/edit_recipe.js"/>" ></script>
	<title>Edit Recipe Contents</title>
</head>
<body onload="fillRecipeFields('${recipeToEdit.getRecipeNameDelimited()}', '${cookTimeHour}', '${cookTimeMinute}', '${cookTimeSecond}', '${prepTimeHour}', '${prepTimeMinute}', '${prepTimeSecond}', '${recipeToEdit.getImageURL()}', '${recipeToEdit.getDishType()}', '${recipeToEdit.getRecipeDescriptionDelimited()}')">
<%@ include file ="navbar_include_1.jsp" %>
	       			<li><a href="<c:url value="/"/>">Home!</a></li>
	       			<li><a href="<c:url value="/showSingleRecipe?recipeName=${recipeToEdit.getRecipeName()}"/>">Back To Recipe</a></li>
<%@ include file = "navbar_include_2.jsp" %>
					<c:choose>
						<c:when test="${loggedInName != null}">
							<li class = "dropdown">
								<a class="dropdown-toggle" data-toggle="dropdown" href="#">
									${loggedInName}
									<span class="caret">
									</span>
								</a>
								<ul class="dropdown-menu">
									<li><a href="<c:url value="/showShoppingList"/>">Go To Shopping List</a></li>
									<li><a href="<c:url value="/showPantryList"/>">Go To Pantry List</a></li>
								</ul>
							</li>
							<li>
								<a href = "<c:url value="/logout"/>">logout</a>
							</li>
						</c:when>
						<c:otherwise>
							<li>
								<a href = "<c:url value="/signin"/>">login</a>
							</li>
							<li>
								<a href = "<c:url value="/register"/>">register</a>
							</li>
						</c:otherwise>					
					</c:choose>
<%@ include file = "navbar_include_3.jsp" %>
	
	<div class ="container subHeader">
		<c:set var="csrfName" value = "_csrf"></c:set>
		<div class = "row listOuterPadding">
			<div class = "col-xs-12">
				<spring:url value="/processEditRecipe" var="processEditRecipeURL"/>
				<form:form class="form-signin" id="editRecipeForm" method="POST" action="${processEditRecipeURL}" modelAttribute="editRecipeForm">
					<form:input class="form-control" type="text" maxlength ="60" size="40" id="recipeName" path="recipeName" readonly="true" />
					<label for="newPrepHours">Prep Time Hours:</label>
					<select id="newPrepHours" name="newPrepHours" required>
						<option value="0">0</option>
						<option value="1">1</option>
						<option value="2">2</option>
						<option value="3">3</option>
						<option value="4">4</option>
						<option value="5">5</option>
						<option value="6">6</option>
						<option value="7">7</option>
						<option value="8">8</option>
						<option value="9">9</option>
						<option value="10">10</option>
						<option value="11">11</option>
					</select> <br>
					<label for="newPrepMinutes">Prep Time Minutes:</label>
					<select id="newPrepMinutes" name="newPrepMinutes" required>
						<option value="0">0</option>
						<option value="1">1</option>
						<option value="2">2</option>
						<option value="3">3</option>
						<option value="4">4</option>
						<option value="5">5</option>
						<option value="10">10</option>
						<option value="15">15</option>
						<option value="20">20</option>
						<option value="25">25</option>
						<option value="30">30</option>
						<option value="35">35</option>
						<option value="40">40</option>
						<option value="45">45</option>
						<option value="50">50</option>
						<option value="55">55</option>
					</select> <br>
					<label for="newPrepSeconds">Prep Time Seconds:</label>
					<select id="newPrepSeconds" name="newPrepSeconds" required>
						<option value="0">0</option>
						<option value="5">5</option>
						<option value="10">10</option>
						<option value="15">15</option>
						<option value="20">20</option>
						<option value="25">25</option>
						<option value="30">30</option>
						<option value="35">35</option>
						<option value="40">40</option>
						<option value="45">45</option>
						<option value="50">50</option>
						<option value="55">55</option>
						<option value="60">60</option>
					</select> <br>
					<label for="newCookHours">Cook Time Hours:</label>
					<select id="newCookHours" name="newCookHours" required>
						<option value="0">0</option>
						<option value="1">1</option>
						<option value="2">2</option>
						<option value="3">3</option>
						<option value="4">4</option>
						<option value="5">5</option>
						<option value="6">6</option>
						<option value="7">7</option>
						<option value="8">8</option>
						<option value="9">9</option>
						<option value="10">10</option>
						<option value="11">11</option>
					</select> <br>
					<label for="newCookMinutes">Cook Time Minutes:</label>
					<select id="newCookMinutes" name="newCookMinutes" required>
						<option value="0">0</option>
						<option value="1">1</option>
						<option value="2">2</option>
						<option value="3">3</option>
						<option value="4">4</option>
						<option value="5">5</option>
						<option value="10">10</option>
						<option value="15">15</option>
						<option value="20">20</option>
						<option value="25">25</option>
						<option value="30">30</option>
						<option value="35">35</option>
						<option value="40">40</option>
						<option value="45">45</option>
						<option value="50">50</option>
						<option value="55">55</option>
					</select> <br>
					<label for="newCookSeconds">Cook Time Seconds:</label>
					<select id="newCookSeconds" name="newCookSeconds" required>
						<option value="0">0</option>
						<option value="5">5</option>
						<option value="10">10</option>
						<option value="15">15</option>
						<option value="20">20</option>
						<option value="25">25</option>
						<option value="30">30</option>
						<option value="35">35</option>
						<option value="40">40</option>
						<option value="45">45</option>
						<option value="50">50</option>
						<option value="55">55</option>
						<option value="60">60</option>
					</select> <br>
					<label for="newDishType">Dish Type:</label><br>
					<form:input class="form-control" type="text" maxlength ="20" size="20" id="newDishType" path="dishType" placeholder = "type: cuisine, salad, etc." required="true"/>
					<label for="newImageURL">Picture of the recipe (URLs only):</label><br>
					<form:textarea class="form-control" id="newImageURL" path="imageURL" rows="4" cols="50"></form:textarea>
					<label for="newRecipeDescription">Brief recipe description:</label><br>
					<form:textarea class="form-control" id="newRecipeDescription" path="recipeDescription" rows="4" cols="50" required="true"></form:textarea> <br>
					<button class="btn btn-lg btn-primary btn-block" type="submit" onclick = "return validateRecipeEdit()">Edit Recipe.</button> <br>
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" required>
					<form:input type="hidden" path="prepTimeUnparsed" id ="newPrepTime" required="true"/>
					<form:input type="hidden" path="cookTimeUnparsed" id ="newCookTime" required="true"/>
					<c:if test="${message != null}">
						<div class = "alert alert-warning">
							${message}
						</div>
					</c:if>
					<div id="modifyRecipeInfo"></div>
				</form:form>
			</div>
		</div>
	</div>

</body>
</html>