<%@ include file="preprocessorIncludes.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<meta name="${_csrf.parameterName}" content="${_csrf.token}"/>
	<meta name="currentPath" content="${pageContext.request.contextPath}"/>
	<%@ include file = "cssInclude.jsp"%>
	<script type="text/javascript" src="<c:url value="/resources/javaScript/jquery-3.1.1.min.js"/>"></script>
	<script type="text/javascript" src = "<c:url value="/resources/javaScript/bootstrap.min.js"/>"></script>
	<script type="text/javascript" src="resources/javaScript/check_shopping_list_input.js"></script>
	<title>Your Shopping List!</title>
</head>
<body>
<%@ include file ="navbar_include_1.jsp" %>
	       			<li><a href="<c:url value="/"/>">Home!</a></li>
	       			<c:if test="${loggedInName != null}">
	       				<li><a href="<c:url value="/showCreateRecipe"/>">Create Recipe!</a></li>
	       			</c:if>
<%@ include file = "navbar_include_2.jsp" %>
					<c:choose>
						<c:when test="${loggedInName != null}">
							<li class = "dropdown">
								<a class="dropdown-toggle" data-toggle="dropdown" href="#">
									${loggedInName}
								</a>
								<ul class="dropdown-menu">
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

	<div class="container">
		<div class="row listOuterPadding">
		<c:if test="${message != null}">
			<div class="row subHeader btn-warning">
				${message}
			</div>
		</c:if>
			<c:choose>
				<c:when test="${userListName != null}">
					<input id="shoppingListNameElement" type="hidden" value="${userListName}">
					<div class="col-xs-12 col-sm-6">
						<div id="ingredientRemovalInfo"></div>
						<h2>Ingredients:</h2>
						<ul id="${userListName}-ingredients-list" class = "list-group">
							<c:if test="${userList != null}">
								<c:set var="ingredientWholeNumber" value="1"></c:set>
								<c:set var="ingredientMixedFraction" value="2"></c:set>
								<c:set var="ingredientFraction" value="3"></c:set>								
								<c:forEach var = "ingredientType" items = "${userList}" varStatus="counter">
									<div id="category-${counter.count}">
										<h1> ${ingredientType.key}</h1>
										<c:forEach var = "ingredient" items = "${ingredientType.value}">
											<c:choose>
												<c:when test="${ingredient.getDisplayType() == ingredientWholeNumber}">
													<li id = "${userListName}-ingredient-${ingredient.getIngredientID()}" class = "list-group-item listInnerPadding fractionFontSize">
														${ingredient.getWholeNumber()} ${ingredient.getIngredientUnit()} ${ingredient.getIngredientName()} <br>
														<button class="btn btn-danger" onclick="removeShoppingListItem('${ingredient.getIngredientID()}', '${counter.count}')">remove</button>
														<button class="btn btn-danger" onclick="setUpdateIngredientFields('${ingredient.getIngredientNameParsed()}', '1', '${ingredient.getWholeNumber()}', '${ingredient.getNumerator()}', '${ingredient.getDenominator()}', '${ingredient.getIngredientUnit()}', '${ingredient.getIngredientType()}')">update</button>	
													</li>
											 	</c:when>
											 	<c:when test="${ingredient.getDisplayType() == ingredientMixedFraction}">
													<li id = "${userListName}-ingredient-${ingredient.getIngredientID()}" class = "list-group-item listInnerPadding fractionFontSizeTwo">
														${ingredient.getWholeNumber()}<sup>${ingredient.getNumerator()}</sup>&frasl;<sub>${ingredient.getDenominator()}</sub> ${ingredient.getIngredientUnit()} ${ingredient.getIngredientName()} <br>
														<button class="btn btn-danger" onclick="removeShoppingListItem('${ingredient.getIngredientID()}', '${counter.count}')">remove</button>
														<button class="btn btn-danger" onclick="setUpdateIngredientFields('${ingredient.getIngredientNameParsed()}', '2', '${ingredient.getWholeNumber()}', '${ingredient.getNumerator()}', '${ingredient.getDenominator()}', '${ingredient.getIngredientUnit()}', '${ingredient.getIngredientType()}')">update</button>  	
													</li>
											 	</c:when>
											 	<c:when test="${ingredient.getDisplayType() == ingredientFraction}">
													<li id = "${userListName}-ingredient-${ingredient.getIngredientID()}" class = "list-group-item listInnerPadding fractionFontSizeTwo">
														<sup>${ingredient.getNumerator()}</sup>&frasl;<sub>${ingredient.getDenominator()}</sub> ${ingredient.getIngredientUnit()} ${ingredient.getIngredientName()} <br>
														<button class="btn btn-danger" onclick="removeShoppingListItem('${ingredient.getIngredientID()}', '${counter.count}')">remove</button>
														<button class="btn btn-danger" onclick="setUpdateIngredientFields('${ingredient.getIngredientNameParsed()}', '3', '${ingredient.getWholeNumber()}', '${ingredient.getNumerator()}', '${ingredient.getDenominator()}', '${ingredient.getIngredientUnit()}', '${ingredient.getIngredientType()}')">update</button>
													</li>
										 		</c:when>
											</c:choose>
										</c:forEach>
									</div>
								</c:forEach>
							</c:if>
						</ul>
					</div>
					
					<div class="col-xs-12 col-sm-6">
						<div class="row">
							<div>
								<h2>Add Ingredient Here</h2>
								<c:set var="addShoppingIngredientsFormId" value="addShoppingIngredientsForm"/>
								<c:set var="shoppingListAddErrorMessageId" value="shoppingListAddErrorMessage"/>
								<spring:url value="/addShoppingListIngredient" var="addShoppingListIngredientURL" />
								<form:form id="${addShoppingIngredientsFormId}" method = "POST" action="${addShoppingListIngredientURL}" modelAttribute="addShoppingListIngredientForm">
									<form:input type="text" id="shoppingListIngredientName" path="ingredientName" placeholder="ingredient name" maxlength="40" size="40" /> <br/>
									<label for="shoppingListIngredientWholeNumberQuantity">Whole Number:</label>
									<form:select id="shoppingListIngredientWholeNumberQuantity" path="wholeNumber">
										<option value="0">no whole number</option>
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
									<label for="shoppingListIngredientQuantity">Quantity (Fraction):</label>
									<form:select id="shoppingListIngredientQuantity" path="ingredientFractionQuantity">
										<option value="1/1">no fraction</option>
										<option value="1/16">1/16</option>
										<option value="1/8">1/8</option>
										<option value="1/4">1/4</option>
										<option value="1/3">1/3</option>
										<option value="3/8">3/8</option>
										<option value="1/2">1/2</option>
										<option value="5/8">5/8</option>
										<option value="2/3">2/3</option>
										<option value="3/4">3/4</option>
									</form:select> <br>
									<label for="shoppingListIngredientUnit">Unit:</label>
									<form:select id="shoppingListIngredientUnit" path="ingredientUnit">
										<option value="">no unit(s)</option>
										<option value="c.">c.</option>
										<option value="gal">gal</option>
										<option value="L.">L.</option>
										<option value="lbs.">lbs.</option>
										<option value="ml.">ml.</option>
										<option value="oz.">oz.</option>
										<option value="pcs.">pcs.</option>
										<option value="qt">qt</option>
										<option value="tbsp.">tbsp.</option>
										<option value="tsp.">tsp.</option>
										<option value="bag">bag</option>
										<option value="bags">bags</option>
									</form:select>
									<label for="shoppingListIngredientType">Type:</label>
									<form:select id="shoppingListIngredientType" path="ingredientType">
										<option value="Other">Other</option>
										<option value="Meat">Meat</option>
										<option value="Dairy">Dairy</option>
										<option value="Vegetables">Vegetables</option>
										<option value="Snacks">Snacks</option>
										<option value="Poultry">Poultry</option>
										<option value="Seasoning">Seasoning</option>
										<option value="Fruits">Fruits</option>
										<option value="Pet Food">Pet Food</option>
										<option value="Beverages">Beverages</option>
									</form:select> <br>
									<input class = "btn btn-primary" type="submit" onclick = "return addToShoppingList('${_csrf.parameterName}')" value="Add">
									<input class = "btn btn-primary" type="submit" onclick = "return resetForm('${addShoppingIngredientsFormId}', '${shoppingListAddErrorMessageId}')" value="Reset This Form"> <br>						
									<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
									<form:input type="hidden" path="ingredientListName" id="shoppingListName" value="${userListName}" />
									<div id ="${shoppingListAddErrorMessageId}"></div>
								</form:form>
							</div>
						</div>
						
						<div class="row">
							<div>
								<h2>Update Ingredient Here.</h2>
								<c:set var="modifyShoppingListIngredientFormId" value="modifyShoppingListIngredientForm"/>
								<c:set var="shoppingListModifyIngredientErrorMessageId" value="shoppingListModifyIngredientErrorMessage"/>
								<spring:url value = "/updateShoppingListIngredient" var="updateShoppingListIngredientURL"/>
								<form:form id="${modifyShoppingListIngredientFormId}" method="POST" action="${updateShoppingListIngredientURL}" modelAttribute="updateShoppingListIngredientForm">
									<form:input type="text" id="shoppingListCurrentIngredientName" path="ingredientName" placeholder="ingredient name" maxlength="40" size="40" readonly="true" /> <br/>
									<label for="shoppingListNewWholeNumberQuantity">Whole Number:</label>
									<form:select id="shoppingListNewWholeNumberQuantity" path="wholeNumber">
										<option value="0">no whole number</option>
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
									<label for="shoppingListNewIngredientQuantity">Quantity (Fraction):</label>
									<form:select id="shoppingListNewIngredientQuantity" path="ingredientFractionQuantity">
										<option value="1/1">no fraction</option>
										<option value="1/16">1/16</option>
										<option value="1/8">1/8</option>
										<option value="1/4">1/4</option>
										<option value="1/3">1/3</option>
										<option value="3/8">3/8</option>
										<option value="1/2">1/2</option>
										<option value="5/8">5/8</option>
										<option value="2/3">2/3</option>
										<option value="3/4">3/4</option>
									</form:select> <br>
									<label for="shoppingListNewIngredientUnit">Unit:</label>
									<form:select id="shoppingListNewIngredientUnit" path="ingredientUnit">
										<option value="">no unit(s)</option>
										<option value="c.">c.</option>
										<option value="gal">gal</option>
										<option value="L.">L.</option>
										<option value="lbs.">lbs.</option>
										<option value="ml.">ml.</option>
										<option value="oz.">oz.</option>
										<option value="pcs.">pcs.</option>
										<option value="qt">qt</option>
										<option value="qt">tbsp.</option>
										<option value="tsp.">tsp.</option>
										<option value="bag">bag</option>
										<option value="bags">bags</option>
									</form:select>
									<label for="shoppingListNewIngredientType">Type:</label>
									<form:select id="shoppingListNewIngredientType" path="ingredientType">
										<option value="Other">Other</option>
										<option value="Meat">Meat</option>
										<option value="Dairy">Dairy</option>
										<option value="Vegetables">Vegetables</option>
										<option value="Snacks">Snacks</option>
										<option value="Poultry">Poultry</option>
										<option value="Seasoning">Seasoning</option>
										<option value="Fruits">Fruits</option>
										<option value="Pet Food">Pet Food</option>
										<option value="Beverages">Beverages</option>
									</form:select> <br>
									<input class = "btn btn-primary" type="submit" onclick = "return updateShoppingListIngredientQuantity()" value="Update Ingredient">	
									<input class = "btn btn-primary" type="submit" onclick = "return resetForm('${modifyShoppingListIngredientFormId}', '${shoppingListModifyIngredientErrorMessageId}')" value="Reset This Form"> <br>		
									<div id ="${shoppingListModifyIngredientErrorMessageId}"></div>
									<form:input type ="hidden" path="ingredientListName" id="currentShoppingListName" value="${userListName}" />	
									<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
								</form:form>
							</div>
						</div>
					</div>
				</c:when>
			
				<c:otherwise>
					<div class="row">
						<h2>Cannot get your shopping list.</h2>
					</div>
				</c:otherwise>
				
			</c:choose>
		</div>
	</div>
</body>
</html>