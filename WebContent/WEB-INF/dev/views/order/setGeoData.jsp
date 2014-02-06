<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Send order geo data</title>
</head>
<body>
	<form method="POST">
		<input type="hidden" name="orderId" value="${orderId}" /> Broker api
		id <br /> <input type="text" name="apiId" /> <br /> Broker api url
		<br /> <input type="text" name="apiKey" /> <br /> Lon <br /> <input
			type="text" name="lon" /> <br /> Lat <br /> <input type="text"
			name="lat" /> <br /> Direction <br /> <input type="text"
			name="direction" /> <br /> Speed <br /> <input type="text"
			name="speed" /> <br /> Category <br /> <input type="text"
			name="category" /> <br /> <input type="submit" value="Set" />
	</form>
</body>
</html>