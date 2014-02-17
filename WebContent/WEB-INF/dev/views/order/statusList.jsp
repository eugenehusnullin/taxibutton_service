<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Orders list</title>
</head>
<link media="screen" href="<c:url value="/resources/css/main.css"/>"
	type="text/css" rel="stylesheet" />
<body>
	<table class="mainTable">
		<thead>
			<tr>
				<th>#</th>
				<th>Date</th>
				<th>Status</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${statusList}" var="status">
				<tr class="infoTr">
					<td>${status.getId()}</td>
					<td>${status.getDate()}</td>
					<td>${status.getStatus().toString()}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</body>
</html>