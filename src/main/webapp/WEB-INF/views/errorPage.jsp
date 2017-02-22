<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
	<%@ include file = "cssInclude.jsp"%>
	<title>Requested Page not found.</title>
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
			<div class = "row listOuterPadding">
				<div class="col-xs-12 centered-text">
					<c:choose>
						<c:when test="${message != null}">
							${message}
						</c:when>
						<c:otherwise>
							The web page could not be found.
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
	</div>
	
</body>
</html>