<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Cancelling order</title>
</head>
<body>
	<form method="POST">
		<input type="hidden" name="orderId" value="${orderId}" />
		Api id:<br />
		<input type="text" name="apiId" />
		<br />
		Reason:<br />
		<input type="text" name="reason" />
		<br />
		<input type="submit" value="Get" />
	</form>
</body>
</html>