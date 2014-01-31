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
	<form action="sendStatus" method="POST">
		<input type="hidden" name="orderId" value="${orderId}" /> Broker api
		id <br /> <input type="text" name="apiId" /> <br /> Broker api url
		<br /> <input type="text" name="apiKey" /> <br /> Order status <br />
		<input type="text" name="status" /> <br /> Latitude <br /> <input
			type="text" name="latitude" /> <br /> Longitude <br /> <input
			type="text" name="longitude" /> <br /> Direction <br /> <input
			type="text" name="direction" /> <br /> Speed <br /> <input
			type="text" name="speed" /> <br /> Category <br /> <input
			type="text" name="category" /> <br /> <input type="submit"
			value="Send" />
	</form>
</body>
</html>