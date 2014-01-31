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
	<c:forEach items="${statusList}" var="status">
		<p>${status.getId()}---${status.getLatitude()}---${status.getLongitude()}---${status.getDate()}</p>
	</c:forEach>
</body>
</html>