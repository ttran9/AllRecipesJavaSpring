<%@ include file="preprocessorIncludes.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<meta name="${_csrf.parameterName}" content="${_csrf.token}"/>
	<meta name="currentPath" content="${pageContext.request.contextPath}"/>
	<%@ include file = "cssInclude.jsp"%>
	<script type="text/javascript" src="<c:url value="/resources/javaScript/jquery-3.1.1.min.js"/>"></script>
	<script type="text/javascript" src = "<c:url value="/resources/javaScript/bootstrap.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/javaScript/single_recipe.js"/>"></script>
	<title>List Displayer</title>
</head>
<body>
<%@ include file ="navbar_include_1.jsp" %>
	       			<li><a href="<c:url value="/"/>">Home!</a></li>
	       			<c:if test="${loggedInName != null}">
	       				<li><a href="<c:url value="/showCreateRecipe"/>">Create Recipe!</a></li>
	       			</c:if>
	       			<c:if test= "${recipeObject != null}">
		       			<li class = "dropdown">
							<a class="dropdown-toggle" data-toggle="dropdown" href="#">
								Recipe Options
								<span class="caret"></span>
							</a>
							<ul class="dropdown-menu">
								<li><a href="<c:url value="/showSingleRecipe?recipeName=${recipeObject.getRecipeName()}"/>">Refresh Page</a></li>
								<c:if test="${loggedInName != null}">
		       						<c:if test="${recipeObject.getUserOwner() != null}">
			       						<c:if test="${loggedInName eq recipeObject.getUserOwner()}">
			       							<li><a href="<c:url value="/editRecipe?recipeName=${recipeObject.getRecipeName()}"/>">Edit Recipe Details!</a></li>
			       							<li><a href="<c:url value="/deleteRecipe?recipeName=${recipeObject.getRecipeName()}"/>">Delete This Recipe!</a></li>
			       						</c:if>
		       						</c:if>
		       					</c:if>
							</ul>
						</li>	 
					</c:if>      			
<%@ include file = "navbar_include_2.jsp" %>
					<c:choose>
						<c:when test="${loggedInName != null}">
							<li class = "dropdown">
								<a class="dropdown-toggle" data-toggle="dropdown" href="#">
									${loggedInName}
									<span class="caret"></span>
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


		<div class="container">
			<div class = "row subHeader">
				<c:if test="${dbBoundError != null}">
					<div class = "row listOuterPadding">
						<div class="col-xs-12 col-sm-6">
							${dbBoundError}
						</div>
					</div>
				</c:if>
			</div>
			<div class = "row subHeader listOuterPadding">
				<c:choose>
					<c:when test ="${recipeObject != null && recipeObject.getUserOwner() != null}">
						<c:set var="recipeName" value="${recipeObject.getRecipeName()}"></c:set>
						<c:if test="${userOwner != null}">
							<c:set var="recipePoster" value = "${userOwner}"></c:set>
						</c:if>
						
							<div id = "recipeDetailsSection">
								<div class = "row">
									<div class = "col-xs-12 col-sm-6">
										<div>
											${recipeName} (${recipeObject.getNumServings()} servings)
										</div>
										<div>
											<c:if test="${recipePoster != null}">
												Posted by: ${recipePoster}
											</c:if>
										</div>
									</div>
									<div class = "col-xs-12 col-sm-6">
										<c:if test="${recipeObject.getUserOwner() eq loggedInName}">
											<div>
												<form id = "modifyRecipeServingsForm" method = "post" action="<c:url value="modifyRecipeIngredients"/>">
													<input type = "text" id = "newServings" name = "newServings" maxlength = "3" size = "3"> 
													<button class = "btn btn-primary" type="submit" onclick = "return checkServings()">Update Servings</button>
													<input type ="hidden" name = "recipeNameField" value = "${recipeName}">	
													<input type ="hidden" name = "oldServings" value = "${recipeObject.getNumServings()}">
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />	
												</form>
											</div>
										</c:if>
									</div>
								</div>
								<div class = "row">
									<div class ="col-xs-12 col-sm-6">
										<c:if test="${recipeImage}">
											<img alt="sample recipe image" src="${recipeImage}">
										</c:if>
									</div>
									<div class = "col-sm-6"></div>
								</div>
							</div> 
							<div id = "recipeIngredientsSection">
								<div class ="row">								
									<div class = "col-xs-12 col-sm-6">
										<h2>Ingredients</h2> 
										<div id = "ingredientRemovalInfo">
										</div>
										<c:set var="ingredientWholeNumber" value="1"></c:set>
										<c:set var="ingredientMixedFraction" value="2"></c:set>
										<c:set var="ingredientFraction" value="3"></c:set>
										<ul id = "${recipeName}-ingredients-list" class = "list-group no-list-style">
											<c:if test="${ingredientsList != null}">
											<c:forEach var = "ingredient" items="${ingredientsList}">
												<c:choose>
													<c:when test="${ingredient.getDisplayType() == ingredientWholeNumber}">
														<li id = "ingredient-${ingredient.getIngredientID()}" class = "list-group-item listInnerPadding fractionFontSize">
															${ingredient.getWholeNumber()} ${ingredient.getIngredientUnit()} ${ingredient.getIngredientName()} <br>
															<c:if test="${loggedInName != null}">	
																<c:if test="${recipeObject.getUserOwner() eq loggedInName}">
																	<button class="btn btn-danger" onclick = "removeListItem('${ingredient.getIngredientID()}', '${recipeName}')">remove</button>  	
																	<button class="btn btn-danger" onclick = "fillUpdateField('${ingredient.getIngredientNameParsed()}', '1', '${ingredient.getWholeNumber()}', '${ingredient.getNumerator()}', '${ingredient.getDenominator()}', '${ingredient.getIngredientUnit()}', '${ingredient.getIngredientType()}')">update this</button> 	  	
																</c:if>
															</c:if>
														</li>
												 	</c:when>
												 	<c:when test="${ingredient.getDisplayType() == ingredientMixedFraction}">
														<li id = "ingredient-${ingredient.getIngredientID()}" class = "list-group-item listInnerPadding fractionFontSizeTwo">
															${ingredient.getWholeNumber()}<sup>${ingredient.getNumerator()}</sup><span id = "ingredientSpan-${ingredient.getIngredientID()}">&frasl;</span><sub>${ingredient.getDenominator()}</sub> ${ingredient.getIngredientUnit()} ${ingredient.getIngredientName()} <br>
															<c:if test="${loggedInName != null}">	
																<c:if test="${recipeObject.getUserOwner() eq loggedInName}">		
																	<button class="btn btn-danger" onclick = "removeListItem('${ingredient.getIngredientID()}', '${recipeName}')">remove</button> 
																	<button class="btn btn-danger" onclick = "fillUpdateField('${ingredient.getIngredientNameParsed()}', '2', '${ingredient.getWholeNumber()}', '${ingredient.getNumerator()}', '${ingredient.getDenominator()}', '${ingredient.getIngredientUnit()}', '${ingredient.getIngredientType()}')">update this</button>
																</c:if>
															</c:if>	   	
														</li>
												 	</c:when>
												 	<c:when test="${ingredient.getDisplayType() == ingredientFraction}">
														<li id = "ingredient-${ingredient.getIngredientID()}" class = "list-group-item listInnerPadding fractionFontSizeTwo">
															<sup>${ingredient.getNumerator()}</sup><span id = "ingredientSpan-${ingredient.getIngredientID()}">&frasl;</span><sub>${ingredient.getDenominator()}</sub> ${ingredient.getIngredientUnit()} ${ingredient.getIngredientName()} <br>
															<c:if test="${loggedInName != null}">	
																<c:if test="${recipeObject.getUserOwner() eq loggedInName}">		
																	<button class="btn btn-danger" onclick = "removeListItem('${ingredient.getIngredientID()}', '${recipeName}')">remove</button>
																	<button class="btn btn-danger" onclick = "fillUpdateField('${ingredient.getIngredientNameParsed()}', '3', '${ingredient.getWholeNumber()}', '${ingredient.getNumerator()}', '${ingredient.getDenominator()}', '${ingredient.getIngredientUnit()}', '${ingredient.getIngredientType()}')">update this</button>  
																</c:if>
															</c:if>
														</li>
												 	</c:when>
												 </c:choose>
											</c:forEach>
											</c:if>
										</ul>
										<c:if test="${loggedInName != null}">
											<form method="POST" action="<c:url value="/processRecipeList"/>">
												<input type="hidden" name="recipeName" value="${recipeName}"/>
												<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
												<button type="submit" class="btn btn-info">Add Ingredients To List</button>
												<button type="button" class="btn btn-info" data-toggle="modal" data-target="#conversionsModal">Supported Conversions</button>
											</form>
											<%@ include file = "conversionsModal.jsp"%>
										</c:if>
									</div>
		
									<c:if test="${loggedInName != null}">	
										<c:if test="${recipeObject.getUserOwner() eq loggedInName}">
											<div class = "col-xs-12 col-sm-6">
												<div>
													<h2>Add Ingredients!</h2>
													<c:set var="addIngredientsFormId" value="addIngredientsForm"/>
													<c:set var="addErrorMessageId" value="addErrorMessage"/>
													<form:form id = "${addIngredientsFormId}" modelAttribute="recipeIngredientAddForm">
														<form:input type="text" id="ingredientName" name = "ingredientName" placeholder="ingredient name" maxlength = "40" size = "40" path="ingredientName"/> <br/>
														<label for="wholeNumberQuantity">Whole Number:</label>
														<form:select id="wholeNumberQuantity" name="wholeNumberQuantity" path="wholeNumber">
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
														<label for="ingredientQuantity">Quantity (Fraction):</label>
														<form:select id="ingredientQuantity" name="ingredientQuantity" path="ingredientFractionQuantity">
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
														<label for="ingredientUnit">Unit:</label>
														<form:select id="ingredientUnit" name="ingredientUnit" path="ingredientUnit">
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
														<label for="ingredientType">Type:</label>
														<form:select id="ingredientType" name="ingredientType" path="ingredientType">
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
														<input class = "btn btn-primary" type="submit" onclick = "return addIngredient('${recipeName}', '${_csrf.parameterName}')" value="Add">
														<input class = "btn btn-primary" type="submit" onclick = "return resetForm('${addIngredientsFormId}', '${addErrorMessageId}')" value="Reset This Form"> <br/>					
														<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
														<form:input path="ingredientListName" type="hidden" value='${recipeName}'/>
														<div id ="${addErrorMessageId}"></div>
													</form:form>
												</div>
												
												<div>
													<h2>Modify Ingredient!</h2>
													<c:set var="modifySingleIngredientFormId" value="modifySingleIngredientForm"/> 
													<c:set var="modifyIngredientErrorMessageId" value="modifyIngredientErrorMessage"/>
													<spring:url value="/updateSingleIngredient" var="updateIngredientURL"/>
													<form:form id = "${modifySingleIngredientFormId}" method = "POST" action="${updateIngredientURL}" modelAttribute="recipeIngredientUpdateForm">
														<form:input type = "text" id = "currentIngredientName" name = "currentIngredientName" placeholder="ingredient name" maxlength = "40" size = "40" readonly="true" path="ingredientName" /> <br/>
														<label for = "newWholeNumberQuantity">Whole Number:</label>
														<form:select id="newWholeNumberQuantity" name="newWholeNumberQuantity" path="wholeNumber">
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
														<label for="newIngredientQuantity">Quantity (Fraction):</label>
														<form:select id="newIngredientQuantity" name="newIngredientQuantity" path="ingredientFractionQuantity">
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
														<label for="newIngredientUnit">Unit:</label>
														<form:select id="newIngredientUnit" name="newIngredientUnit" path="ingredientUnit">
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
														<label for="newIngredientType">Type:</label>
														<form:select id="newIngredientType" name="newIngredientType" path="ingredientType">
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
														<input class = "btn btn-primary" type="submit" onclick = "return updateQuantity()" value="Update Ingredient">	
														<input class = "btn btn-primary" type="submit" onclick = "return resetForm('${modifySingleIngredientFormId}', '${modifyIngredientErrorMessageId}')" value="Reset This Form"> <br/>	
														<div id="modifyIngredientErrorMessage"></div>
														<form:input type="hidden" path="ingredientListName" value = "${recipeName}"/>
														<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
													</form:form>
												</div>
											</div>
										</c:if>
									</c:if>
								</div>	
							</div>
							
							<div id="recipeDirectionsSection" class = "row">
								<div class = "col-xs-12 col-sm-6">
									<h2>Directions</h2>
									<div class = "row">
										<c:if test="${recipeDirections != null}">
											<div class = "col-xs-4">
												<c:if test="${prepTime != null}">
													<div>
														Prep Time:
													</div>
													<div>
														${prepTime}
													</div>
												</c:if>
											</div>
											<div class = "col-xs-4">
												<c:if test="${cookTime != null}">
													<div>
														Cook Time:
													</div>
													<div>
														${cookTime}
													</div>
												</c:if>
											</div>
											<div class = "col-xs-4">
												<c:if test="${readyTime != null}">
													<div>
														Ready In:
													</div>
													<div>
														${readyTime}
													</div>
												</c:if>
											</div>
										</c:if>
									</div>
									<ol id = "${recipeName}-directions-list" class = "list-group listGroupLabelInside">
										<c:if test="${recipeDirections != null}">
											<c:forEach var = "recipeDirection" items="${recipeDirections}">
												<li id = "direction-${recipeDirection.getDirectionId()}" class = "list-group-item listInnerPadding listGroupShowLabel">
													${recipeDirection.getDirection()}
													<c:if test="${loggedInName != null}">	
														<c:if test="${recipeObject.getUserOwner() eq loggedInName}">
															<br>
															<a href = "<c:url value="removeRecipeDirection?directionId=${recipeDirection.getDirectionId()}&recipeName=${recipeName}&directionNumber=${recipeDirection.getDirectionNumber()}"/>"><button class="btn btn-danger">remove</button></a>
															<button class="btn btn-danger" onclick = "setDirectionUpdate('${recipeDirection.getDirectionNumber()}', '${recipeDirection.getDirectionsDelimited()}')">update</button>
														</c:if>
													</c:if>
												</li>
											</c:forEach>
										</c:if>
									</ol>
								</div>
				
								<c:if test="${loggedInName != null}">	
									<c:if test="${recipeObject.getUserOwner() eq loggedInName}">
										<div class = "col-xs-12 col-sm-6">
											<div>
												<h2>Add Directions!</h2>
												<c:set var="addDirectionsFormId" value="addDirectionsForm"/>
												<c:set var="addDirectionMessageId" value="addDirectionMessage"/>
												<form:form id = "${addDirectionsFormId}" modelAttribute="recipeAddDirectionForm">
												<form:textarea id = "directionContent" name = "directionContent" path="direction" rows = "4" cols = "40"></form:textarea> <br/>
												<input class = "btn btn-primary" type="submit" onclick = "return addDirectionItem()" value="Add Direction">
												<input class = "btn btn-primary" type="submit" onclick = "return resetForm('${addDirectionsFormId}', '${addDirectionMessageId}')" value="Reset This Form"> <br/>		
												<form:input type ="hidden" id = "recipeNameDirection" path = "recipeName" value = "${recipeName}" />					
												<div id ="${addDirectionMessageId}"></div>
												<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
												</form:form>
											</div>
											
											<div>
												<h2>Modify A Direction!</h2>
												<c:set var="updateDirectionsFormId" value="updateDirectionsForm"/>
												<c:set var="modifyIngredientErrorMessageId" value="modifyDirectionErrorMessage"/>
												<spring:url value="/updateRecipeDirectionContent" var="updateDirectionURL"/>
												<form:form id = "${updateDirectionsFormId}" method = "POST" action="${updateDirectionURL}" modelAttribute="updateRecipeDirectionForm">
												<form:textarea id = "modifyDirectionContent" path="direction" rows = "4" cols = "40"></form:textarea> <br>
												<form:input id = "modifiedDirectionNumber" path="directionNumber" type = "text" readonly="true"/> <br>
												<input class = "btn btn-primary" type="submit" onclick = "return checkDirectionUpdateField()" value="Update Direction">
												<input class = "btn btn-primary" type="submit" onclick = "return resetForm('${updateDirectionsFormId}', '${modifyIngredientErrorMessageId}')" value="Reset This Form"> <br/>		
												<div id ="${modifyIngredientErrorMessageId}"></div>
												<form:input type ="hidden" path="recipeName" value = "${recipeName}" />	
												<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
												</form:form>
											</div>
										</div>
									</c:if>
								</c:if>
							</div>
							
							<div id= "recipeReviewsSection" class = "row">
								<div class = "col-xs-12 col-sm-6">
									<h2>Reviews</h2>
									<form id = "sortForm" method = "GET" action = "<c:url value="showSingleRecipe"/>">
										<select id="sortType" name = "sortType">
											<option value = "1">Sort by most recent review</option>
											<option value = "2">Sort by oldest review</option>
											<option value = "3">Highest reviews first</option>
											<option value = "4">Lowest reviews</option>
										</select>
										<input type = "hidden" name = "recipeName" value = "${recipeName}" />
										<input type = "submit" value = "Sort Reviews">
									</form>
									<div id = "reviewAndRating" class = "row">
										<div id = "totalReviews" class = "col-xs-12">
											${totalReviews} reviews	
										</div>
										<div id = "averageRating" class = "col-xs-12">
											${averageRating}
										</div>
									</div>
									<div class = "row">
										<div id = "fiveStarReviews" class = "col-xs-12">
											${numberFiveStarReviews} 5 star reviews
										</div>
									</div>
									<div class = "row">
										<div id = "fourStarReviews" class = "col-xs-12">
											${numberFourStarReviews} 4 star reviews
										</div>
									</div>
									<div class = "row">
										<div id = "threeStarReviews" class = "col-xs-12">
											${numberThreeStarReviews} 3 star reviews
										</div>
									</div>
									<div class = "row">
										<div id = "twoStarReviews" class = "col-xs-12">
											${numberTwoStarReviews} 2 star reviews
										</div>
									</div>
									<div class = "row">
										<div id = "oneStarReviews" class = "col-xs-12">
											${numberOneStarReviews} 1 star reviews
										</div>
									</div>
									<div class="list-group">
										<ul id = "${recipeName}-reviews-list" class = "list-unstyled">
											<c:if test="${recipeReviews != null}">
												<c:forEach var="recipeReview" items="${recipeReviews}">
													<li id = "review-${recipeReview.getReviewId()}" class = "listInnerPadding">
														<div class="list-group-item list-group-item-action flex-column align-items-start">
															<div class="d-flex w-100 justify-content-between">
																<h5 class="mb-1">${recipeReview.getReviewRating()} star(s) ${recipeReview.getReviewTitle()}</h5>
																<small>${recipeReview.getUserNamePosted()} on ${recipeReview.getParsedReviewPostedTime()}</small>
															</div>
															<p class="mb-1 wordWrapClass">${recipeReview.getReviewContent()}</p> <br>
															<c:if test="${loggedInName != null}">	
																<c:if test="${recipeReview.getUserNamePosted() != null}">
																	<c:if test="${recipeReview.getUserNamePosted() eq loggedInName}">
																		<small>
																			<button class = "btn btn-danger" onclick = "removeRecipeReview('${recipeReview.getReviewId()}', '${recipeReview.getReviewRating()}', '${recipeObject.getRecipeNameDelimited()}')">remove</button> 
																			<button class = "btn btn-danger" onclick = "setReviewUpdate('${recipeReview.getReviewContentDelimited()}', '${recipeReview.getReviewRating()}', '${recipeReview.getReviewTitleDelimited()}', '${recipeReview.getReviewId()}')">update</button>
																		</small> 
																	</c:if>
																</c:if>
															</c:if>
														</div>
													</li>
												</c:forEach>
											</c:if>
										</ul>
									</div>
								</div>
								<div class = "col-xs-12 col-sm-6">
									<c:if test="${loggedInName != null}">	
										<div>
											<h2>Add Reviews!</h2>
											<c:set var="addReviewFormId" value="addReviewForm"/>
											<c:set var="addReviewMessageId" value="addReviewMessage"/>
											<form:form id = "${addReviewFormId}" modelAttribute="recipeAddReviewForm"> 
												<form:input type = "text" id = "reviewTitle" path = "reviewTitle" placeholder="review title" maxlength = "70" size = "40"/> <br>
												<form:select id="reviewRatingOption" path="reviewRating">
													<option value = "1">1 star</option>
													<option value = "2">2 stars</option>
													<option value = "3">3 stars</option>
													<option value = "4">4 stars</option>
													<option value = "5">5 stars</option>
												</form:select> <br>
												<form:textarea id="reviewContent" path="reviewContent" rows="4" cols="41"></form:textarea> <br/>
												<form:input type="hidden" id="reviewPosterName" path="userNamePosted" value="${loggedInName}"/>	
												<form:input type="hidden" id="reviewRecipeName" path="recipeName" value="${recipeName}"/>	
												<input class = "btn btn-primary" type="submit" onclick = "return addRecipeReview()" value="Add Review">
												<input class = "btn btn-primary" type="submit" onclick = "return resetForm('${addReviewFormId}', '${addReviewMessageId}')" value="Reset This Form"> <br/>
												<div id ="${addReviewMessageId}"></div>
												<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
											</form:form>
										</div>
									
									
										<div>
											<h2>Modify Reviews!</h2>
											<c:set var="modifyReviewFormId" value="modifyReviewForm"/>
											<c:set var="editReviewMessageId" value="editReviewMessage"/>
											<spring:url value="/updateRecipeReviewContent" var="updateReviewURL"> </spring:url>
											<form:form id = "${modifyReviewFormId}" method = "POST" action="${updateReviewURL}" modelAttribute="recipeEditReviewForm">
												<form:input type = "text" id="modifyReviewTitle" path="reviewTitle" maxlength="70" size="40" readonly="true"/> <br>
												<form:select id="modifyReviewRatingOption" path="reviewRating">
													<option value = "1">1 star</option>
													<option value = "2">2 stars</option>
													<option value = "3">3 stars</option>
													<option value = "4">4 stars</option>
													<option value = "5">5 stars</option>
												</form:select> <br>
												<form:textarea id="modifiedReviewContent" path="reviewContent" rows = "4" cols = "41"></form:textarea> <br>
												<input class = "btn btn-primary" type="submit" onclick = "return checkUpdatedReview()" value="Update Review"> 
												<input class = "btn btn-primary" type="submit" onclick = "return resetForm('${modifyReviewFormId}', '${editReviewMessageId}')" value="Reset This Form"> <br/>
												<form:input type ="hidden" path="recipeName" value = "${recipeName}"/>		
												<form:input type = "hidden" path="reviewId" id="modifiedReviewId"/>		 
												<div id ="${editReviewMessageId}"></div>
												<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
											</form:form>
										</div>
									</c:if>	
								</div>
							</div>
					</c:when>
					
					<c:otherwise>
						<div class = "row">
							<pre><code>Cannot find the recipe.</code></pre>
						</div>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</body>
</html>