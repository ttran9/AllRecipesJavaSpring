<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
	<%@ include file = "cssInclude.jsp"%>
	<script type="text/javascript" src="<c:url value="/resources/javaScript/jquery-3.1.1.min.js"/>"></script>
	<script type="text/javascript" src = "<c:url value="/resources/javaScript/bootstrap.min.js"/>"></script>
	<meta name="currentPath" content="${pageContext.request.contextPath}"/>
	<title>Recipes!</title>
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
    
	<div class = "container">
		<div class = "row listOuterPadding">
		<c:if test="${errorMessage != null}">
			<div class="list-group">
				<ul class = "list-unstyled">
					<li class = "listInnerPadding">
						${errorMessage}
					</li>
				</ul>
			</div>
        </c:if>
        <c:choose>
        	<c:when test="${recipesList != null}">      		
	    		<div class="list-group">
	    			<c:choose>
	    				<c:when test="${recipesList.size() gt 0}">
	    					<ul id = "${recipes.getRecipeName()}-reviews-list" class = "list-unstyled">
	        					<c:forEach var = "recipes" items="${recipesList}">
									<li id = "${recipes.getRecipeName()}" class = "listInnerPadding">
										<div class="list-group-item list-group-item-action flex-column align-items-start">
											<div class="d-flex w-100 justify-content-between">
												<h5 class="mb-1"><a href = "<c:url value = "showSingleRecipe?recipeName=${recipes.getRecipeName()}"/>">${recipes.getRecipeName()}</a></h5>
												<c:choose>
													<c:when test="${recipes.getTotalNumberOfReviews() gt 0}">
														<h5 class="mb-1">${recipes.getTotalNumberOfReviews()} (${recipes.getAverageRating()})</h5>
													</c:when>
													<c:otherwise>
														<h5 class="mb-1">This recipe has not been reviewed.</h5>
													</c:otherwise>
												</c:choose>
												<small>${recipes.getUserOwner()} on ${recipes.getRecipeCreationDate()}</small>
											</div>
											<p class="mb-1">${recipes.getRecipeDescription()}</p><br>
										</div>
									</li>
								</c:forEach>
							</ul>	
    					</c:when>
	    			
	    				<c:otherwise>
	    						<ul class = "list-unstyled">
									<li class = "listInnerPadding">
										<div class="list-group-item list-group-item-action flex-column align-items-start">
											<div class="d-flex w-100 justify-content-between">
												<h5 class="mb-1">There have been no recipes created by any users.</h5>
											</div>
										</div>
									</li>
								</ul>
	    				</c:otherwise>
	    			</c:choose>
				</div>
        	</c:when>
        	
	        <c:otherwise>
	        	<div class="list-group">
					<ul class = "list-unstyled">
						<li class = "listInnerPadding">
							<div class="list-group-item list-group-item-action flex-column align-items-start">
								<div class="d-flex w-100 justify-content-between">
									<h5 class="mb-1">Error retrieving recipes.</h5>
								</div>
							</div>
						</li>
					</ul>
				</div>
	        </c:otherwise>
        </c:choose>        
		</div>
	</div>

</body>
</html>