<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Send order status</title>
</head>
<body>
	<form method="POST">
		<input type="hidden" name="orderId" value="${orderId}" /> Broker api
		id <br /> <input type="text" name="apiId" /> <br /> Broker api key
		<br /> <input type="text" name="apiKey" /> <br /> Order status <br />
		<input type="text" name="status" /><input type="submit" value="Send" />
	</form>
</body>
</html>