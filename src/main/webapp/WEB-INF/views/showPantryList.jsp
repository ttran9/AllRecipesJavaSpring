<%@ include file="preprocessorIncludes.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<meta name="${_csrf.parameterName}" content="${_csrf.token}"/>
	<meta name="currentPath" content="${pageContext.request.contextPath}"/>
	<%@ include file = "cssInclude.jsp"%>
	<script type="text/javascript" src="<c:url value="/resources/javaScript/jquery-3.1.1.min.js"/>"></script>
	<script type="text/javascript" src = "<c:url value="/resources/javaScript/bootstrap.min.js"/>"></script>
	<script type="text/javascript" src="resources/javaScript/math.min.js"></script>
	<script type="text/javascript" src="resources/javaScript/check_pantry_list_input.js"></script>
	<title>Your Pantry List!</title>
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
									<li><a href="<c:url value="/showShoppingList"/>">Go To Shopping List</a></li>
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
					<input id="pantryListNameElement" type="hidden" value="${userListName}">
					<div class="col-xs-12 col-sm-6">
						<div id="pantryIngredientRemovalInfo"></div>
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
													<c:choose>
														<c:when test="${ingredient.isHasIngredientThresholdBeenHit() eq true}">
															<li id = "${userListName}-ingredient-${ingredient.getIngredientID()}" class = "list-group-item list-item-background-color listInnerPadding fractionFontSize">
														</c:when>
														<c:otherwise>
															<li id = "${userListName}-ingredient-${ingredient.getIngredientID()}" class = "list-group-item listInnerPadding fractionFontSize">
														</c:otherwise>
													</c:choose>
														${ingredient.getWholeNumber()} ${ingredient.getIngredientUnit()} ${ingredient.getIngredientName()} <br>
														<button class="btn btn-danger" onclick="removePantryListItem('${ingredient.getIngredientID()}', '${counter.count}')">remove</button>
														<button class="btn btn-danger" onclick="setUpdateIngredientFields('${ingredient.getIngredientNameParsed()}', '1', '${ingredient.getWholeNumber()}', '${ingredient.getNumerator()}', '${ingredient.getDenominator()}', '${ingredient.getIngredientUnit()}', '${ingredient.getIngredientType()}')">update</button>	
														<button class="btn btn-danger" onclick="setSubtractPantryIngredientFields('${ingredient.getIngredientNameParsed()}', '1', '${ingredient.getWholeNumber()}', '${ingredient.getNumerator()}', '${ingredient.getDenominator()}')">subtract quantity</button>	
													</li>
											 	</c:when>
											 	<c:when test="${ingredient.getDisplayType() == ingredientMixedFraction}">
													<c:choose>
														<c:when test="${ingredient.isHasIngredientThresholdBeenHit() eq true}">
															<li id = "${userListName}-ingredient-${ingredient.getIngredientID()}" class = "list-group-item list-item-background-color listInnerPadding fractionFontSize">
														</c:when>
														<c:otherwise>
															<li id = "${userListName}-ingredient-${ingredient.getIngredientID()}" class = "list-group-item listInnerPadding fractionFontSize">
														</c:otherwise>
													</c:choose>
														${ingredient.getWholeNumber()}<sup>${ingredient.getNumerator()}</sup>&frasl;<sub>${ingredient.getDenominator()}</sub> ${ingredient.getIngredientUnit()} ${ingredient.getIngredientName()} <br>
														<button class="btn btn-danger" onclick="removePantryListItem('${ingredient.getIngredientID()}', '${counter.count}')">remove</button>
														<button class="btn btn-danger" onclick="setUpdateIngredientFields('${ingredient.getIngredientNameParsed()}', '2', '${ingredient.getWholeNumber()}', '${ingredient.getNumerator()}', '${ingredient.getDenominator()}', '${ingredient.getIngredientUnit()}', '${ingredient.getIngredientType()}')">update</button>
														<button class="btn btn-danger" onclick="setSubtractPantryIngredientFields('${ingredient.getIngredientNameParsed()}', '2', '${ingredient.getWholeNumber()}', '${ingredient.getNumerator()}', '${ingredient.getDenominator()}')">subtract quantity</button>  	
													</li>
											 	</c:when>
											 	<c:when test="${ingredient.getDisplayType() == ingredientFraction}">
													<c:choose>
														<c:when test="${ingredient.isHasIngredientThresholdBeenHit() eq true}">
															<li id = "${userListName}-ingredient-${ingredient.getIngredientID()}" class = "list-group-item list-item-background-color listInnerPadding fractionFontSize">
														</c:when>
														<c:otherwise>
															<li id = "${userListName}-ingredient-${ingredient.getIngredientID()}" class = "list-group-item listInnerPadding fractionFontSize">
														</c:otherwise>
													</c:choose>
														<sup>${ingredient.getNumerator()}</sup>&frasl;<sub>${ingredient.getDenominator()}</sub> ${ingredient.getIngredientUnit()} ${ingredient.getIngredientName()} <br>
														<button class="btn btn-danger" onclick="removePantryListItem('${ingredient.getIngredientID()}', '${counter.count}')">remove</button>
														<button class="btn btn-danger" onclick="setUpdateIngredientFields('${ingredient.getIngredientNameParsed()}', '3', '${ingredient.getWholeNumber()}', '${ingredient.getNumerator()}', '${ingredient.getDenominator()}', '${ingredient.getIngredientUnit()}', '${ingredient.getIngredientType()}')">update</button>
														<button class="btn btn-danger" onclick="setSubtractPantryIngredientFields('${ingredient.getIngredientNameParsed()}', '3', '${ingredient.getWholeNumber()}', '${ingredient.getNumerator()}', '${ingredient.getDenominator()}')">subtract quantity</button>
													</li>
										 		</c:when>
											</c:choose>
										</c:forEach>
									</div>
								</c:forEach>
							</c:if>
						</ul>
						<c:if test="${loggedInName != null}">
							<form method="POST" action="<c:url value="/processPantryList"/>">
								<input type="hidden" name="pantryListName" value="${userListName}"/>
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
								<button type="submit" class="btn btn-info">Add Ingredients To Shopping List</button>
								<button type="button" class="btn btn-info" data-toggle="modal" data-target="#conversionsModal">Supported Conversions</button>
							</form>
							<%@ include file = "conversionsModal.jsp"%>
						</c:if>
					</div>
					
					<div class="col-xs-12 col-sm-6">
						<div class="row">
							<h2>Add Ingredient Here</h2>
							<c:set var="addPantryIngredientsFormId" value="addPantryIngredientsForm"/>
							<c:set var="pantryListAddErrorMessageId" value="pantryListAddErrorMessage"/>
							<spring:url value="/addPantryListIngredient" var ="addPantryIngredientURL"/>
							<form:form id="${addPantryIngredientsFormId}" method = "POST" action="${addPantryIngredientURL}" modelAttribute="addPantryIngredientForm">
								<form:input type="text" id="pantryListIngredientName" path="ingredientName" placeholder="ingredient name" maxlength="40" size="40" /> <br/>
								<label for="pantryListIngredientWholeNumberQuantity">Whole Number:</label>
								<form:select id="pantryListIngredientWholeNumberQuantity" path="wholeNumber">
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
								<label for="pantryListIngredientQuantity">Quantity (Fraction):</label>
								<form:select id="pantryListIngredientQuantity" path="ingredientFractionQuantity">
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
								<label for="pantryListIngredientUnit">Unit:</label>
								<form:select id="pantryListIngredientUnit" path="ingredientUnit">
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
								<label for="pantryListIngredientType">Type:</label>
								<form:select id="pantryListIngredientType" path="ingredientType">
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
								<input class = "btn btn-primary" type="submit" onclick = "return addToPantryList('${_csrf.parameterName}')" value="Add">
								<input class = "btn btn-primary" type="submit" onclick = "return resetForm('${addPantryIngredientsFormId}', '${pantryListAddErrorMessageId}')" value="Reset This Form"> <br>						
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
								<form:input type="hidden" path="ingredientListName" id="pantryListName" value="${userListName}"/>
								<div id ="${pantryListAddErrorMessageId}"></div>
							</form:form>
						</div>
						
						<div class="row">
							<h2>Update Ingredient Here.</h2>
							<c:set var="modifyPantryListIngredientFormId" value="modifyPantryListIngredientForm"/>
							<c:set var="pantryListModifyIngredientErrorMessageId" value="pantryListModifyIngredientErrorMessage"/>
							<spring:url value="/updatePantryListIngredient" var ="updatePantryListIngredientURL"/>
							<form:form id="${modifyPantryListIngredientFormId}" method="POST" action="${updatePantryListIngredientURL}" modelAttribute="updatePantryIngredientForm">
								<form:input type="text" id="pantryListCurrentIngredientName" path="ingredientName" placeholder="ingredient name" maxlength="40" size="40" readonly="true"/> <br/>
								<label for="pantryListNewWholeNumberQuantity">Whole Number:</label>
								<form:select id="pantryListNewWholeNumberQuantity" path="wholeNumber">
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
								<label for="pantryListNewIngredientQuantity">Quantity (Fraction):</label>
								<form:select id="pantryListNewIngredientQuantity" path="ingredientFractionQuantity">
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
								<label for="pantryListNewIngredientUnit">Unit:</label>
								<form:select id="pantryListNewIngredientUnit" path="ingredientUnit">
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
								<label for="pantryListNewIngredientType">Type:</label>
								<form:select id="pantryListNewIngredientType" path="ingredientType">
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
								<input class = "btn btn-primary" type="submit" onclick = "return updatePantryListIngredientQuantity()" value="Update Ingredient">	
								<input class = "btn btn-primary" type="submit" onclick = "return resetForm('${modifyPantryListIngredientFormId}', '${pantryListModifyIngredientErrorMessageId}')" value="Reset This Form"> <br>		
								<div id ="${pantryListModifyIngredientErrorMessageId}"></div>
								<input type="hidden" id="currentPantryListName" name ="ingredientListName" value="${userListName}"/>	
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							</form:form>
						</div>
						
						<div class="row">
							<h2>Subtract Ingredient Amount Here.</h2>
							<c:set var="subtractPantryListIngredientFormId" value="subtractPantryListIngredientForm"/>
							<c:set var="subtractPantryListIngredientErrorMessageId" value="subtractPantryListIngredientErrorMessage"/>
							<spring:url value="/updatePantryListIngredientAmount" var="updatePantryListIngredientAmountURL"/>
							<form:form id="${subtractPantryListIngredientFormId}" method="POST" action="${updatePantryListIngredientAmountURL}" modelAttribute="subtractPantryIngredientForm">
								<form:input type="text" id="ingredientNameToSubtract" path="ingredientName" placeholder="ingredient name" maxlength="40" size="40" readonly="true" /> <br/>
								<label for="wholeNumberSubtractionQuantity">Whole Number Subtraction Amount:</label>
								<form:select id="wholeNumberSubtractionQuantity" path="wholeNumber">
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
								<label for="fractionSubtractionQuantity">Quantity (Fraction) Subtraction Amount:</label>
								<form:select id="fractionSubtractionQuantity" path="ingredientFractionQuantity">
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
								<input class = "btn btn-primary" type="submit" onclick = "return subtractPantryListIngredient()" value="Subtract Ingredient">	
								<input class = "btn btn-primary" type="submit" onclick = "return resetForm('${subtractPantryListIngredientFormId}', '${subtractPantryListIngredientErrorMessageId}')" value="Reset This Form"> <br>		
								<div id ="${subtractPantryListIngredientErrorMessageId}"></div>
								<form:input type="hidden" path="ingredientListName" value="${userListName}" />	
								<input type="hidden" id="originalIngredientWholeNumber" />
								<input type="hidden" id="originalIngredientNumerator" />
								<input type="hidden" id="originalIngredientDenominator" />
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							</form:form>
						</div>
					</div>
				</c:when>
			
				<c:otherwise>
					<div class="row">
						<h2>Cannot get your inventory list.</h2>
					</div>
				</c:otherwise>
				
			</c:choose>
		</div>
	</div>
</body>
</html>