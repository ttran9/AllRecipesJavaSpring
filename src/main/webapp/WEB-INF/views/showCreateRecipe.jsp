<%@ include file="preprocessorIncludes.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<%@ include file = "cssInclude.jsp"%>
	<script type="text/javascript" src="<c:url value="/resources/javaScript/jquery-3.1.1.min.js"/>"></script>
	<script type="text/javascript" src = "<c:url value="/resources/javaScript/bootstrap.min.js"/>"></script>
	<script type="text/javascript" src = "resources/javaScript/create_recipe_check.js"></script>
	<title>Create Your Own Recipe!</title>
</head>
<body>
<%@ include file ="navbar_include_1.jsp" %>
	       			<li><a href="<c:url value="/"/>">Home!</a></li>
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
				<spring:url value="/makeRecipe" var="makeRecipeURL"/>
				<form:form class="form-signin" id="createRecipeForm" method="POST" action="${makeRecipeURL}" modelAttribute="createRecipeForm">
					<form:input class="form-control" type="text" maxlength="60" size="40" id="recipeName" path="recipeName" placeholder="recipe name here" required="true"/>
					<label for="servingsField">Servings:</label>
					<form:select id="servingsField" path="numServings" required="true">
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
						<option value="12">12</option>
						<option value="13">13</option>
						<option value="14">14</option>
						<option value="15">15</option>
						<option value="16">16</option>
						<option value="17">17</option>
						<option value="18">18</option>
						<option value="19">19</option>
						<option value="20">20</option>
					</form:select> <br>
					<label for="prepHours">Prep Time Hours:</label>
					<select id="prepHours" name="prepHours" required>
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
					<label for="prepMinutes">Prep Time Minutes:</label>
					<select id="prepMinutes" name="prepMinutes" required>
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
					<label for="prepSeconds">Prep Time Seconds:</label>
					<select id="prepSeconds" name="prepSeconds" required>
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
					<label for="cookHours">Cook Time Hours:</label>
					<select id="cookHours" name="cookHours" required>
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
					<label for="cookMinutes">Cook Time Minutes:</label>
					<select id="cookMinutes" name="cookMinutes" required>
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
					<label for="cookSeconds">Cook Time Seconds:</label>
					<select id="cookSeconds" name="cookSeconds" required>
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
					<form:input class="form-control" type="text" maxlength="20" size="20" id="dishType" path="dishType" placeholder="type: cuisine, salad, etc." required="true"/>
					<label for="imageURl">Picture of the recipe (URLs only):</label><br>
					<form:textarea class="form-control" id="imageURL" path="imageURL" rows="4" cols="50"></form:textarea>
					<label for="recipeDescription">Brief recipe description:</label><br>
					<form:textarea class="form-control" id="recipeDescription" path="recipeDescription" rows="4" cols="50" required="true"></form:textarea> <br>
					<button class="btn btn-lg btn-primary btn-block" type="submit" onclick = "return validateRecipeCreate('${csrfName}')">Create Recipe.</button> <br>
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" required>
					<form:input type="hidden" path="prepTimeUnparsed" id="prepTime" required="true"/>
					<form:input type="hidden" path="cookTimeUnparsed" id="cookTime" required="true"/>
					<c:if test="${message != null}">
						<div class = "alert alert-warning">
							${message}
						</div>
					</c:if>
					<div id="createRecipeInfo"></div>
				</form:form>
			</div>
		</div>
	</div>
	

</body>
</html>