<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Orders list</title>
</head>
<body>
	<c:forEach items="${orders}" var="order">
		<p>${order.getId()}---${order.getType()}---<a
				href="send?id=${order.getId()}">Send</a>---<a
				href="give?id=${order.getId()}">Give</a>---<a
				href="sendStatus?id=${order.getId()}">Send status</a>---<a
				href="showStatus?id=${order.getId()}">Show status</a>---<a
				href="cancel?id=${order.getId()}">Cancel</a>
		</p>
	</c:forEach>
	<br />
	<a href="create">Create</a>
</body>
</html>