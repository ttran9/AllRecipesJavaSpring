<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML>
<html>
<head> 
	<%@ include file = "cssInclude.jsp"%>
	<script type="text/javascript" src="<c:url value="/resources/javaScript/login_check.js"/>"></script>
	<title>Login Page</title>
</head>
<body>
<%@ include file ="navbar_include_1.jsp" %>	
	       			<li>
	       				<a href="<c:url value="/"/>">Home!</a>
	       			</li>
	       			<li>
	       				<a href="<c:url value="/register"/>">Create An Account!</a>
	       			</li>
<%@ include file ="navbar_include_3.jsp" %>
	
    <div class="container">
        <form class="form-signin" method="POST" action = "<c:url value="/login"/>">
			<h2 class="form-signin-heading">Login Below</h2>
			<c:choose>
				<c:when test="${userNameEntered != null}">
					<input type="text" name="username" class="form-control" value = "${userNameEntered}" maxlength="35" size="16">
				</c:when>
				<c:otherwise>
					<input type="text" name="username" class="form-control" placeholder= "user name here" maxlength="20" size="16">				
				</c:otherwise>
			</c:choose>
			<input type="password" name="password" class="form-control" placeholder = "password here" maxlength = "20" size = "16">
			<div class="checkbox">
		    	<label><input type="checkbox" name="AR-remember-me">Remember Me</label>
		    </div>
			<button class="btn btn-lg btn-primary btn-block" type="submit" onclick="">Login!</button>
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<c:if test="${error != null}">
				<p class="lead blog-description">${error}</p>
			</c:if>
		</form>
	</div>

</body>
</html>