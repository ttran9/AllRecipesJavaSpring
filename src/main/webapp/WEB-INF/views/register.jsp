<%@ include file="preprocessorIncludes.jsp"%>
<!DOCTYPE HTML>
<html>
<head> 
	<meta name="currentPath" content="${pageContext.request.contextPath}"/>
	<%@ include file = "cssInclude.jsp"%>
	<script type="text/javascript" src="<c:url value="/resources/javaScript/jquery-3.1.1.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/javaScript/register_check.js"/>"></script>
	<title>Registration Page</title>
</head>

<body>
<%@ include file ="navbar_include_1.jsp" %>
	       			<li>
	       				<a href="<c:url value="/"/>">Home!</a>
	       			</li>
	       			<li>
	       				<a href="<c:url value="/signin"/>">Login!</a>
	       			</li>
<%@ include file ="navbar_include_3.jsp" %>
    
    <div class="container">
    	<spring:url value="/processRegistration" var="processRegistrationURL"/>
    	<form:form class="form-signin" id = "registrationForm" action="${processRegistrationURL}" method="post" modelAttribute="registerForm">
			<h2 class="form-signin-heading">Register Below!</h2>
			<form:input type="text" id="registerUserName" path="userName" class="form-control" placeholder="enter user name" maxlength = "35" onfocusout = "checkUserNameRegistration()"/>
			<form:input type="password" id="registerPassword" path="password" class="form-control" placeholder="password" maxlength = "20"/>
    		<form:input type="password" id="registerValidatePassword" path="validatePassword" class="form-control" placeholder="enter password again" maxlength = "20"/>
			<button class="btn btn-lg btn-primary btn-block" type="submit" id = "register" name = "register" onclick = "return verifyPasswords()" disabled>Register User!</button>
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"> <br>
   			<div id ="registrationErrorField"> </div>
   			<c:if test="${error != null}">
   				<div class = "registrationError">
   					${error}
   				</div>
   			</c:if>
      </form:form>
    </div> <!-- /container -->
    
</body>
</html>

