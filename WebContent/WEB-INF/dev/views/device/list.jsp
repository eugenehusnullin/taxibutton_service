<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Devices list</title>
</head>
<link media="screen" href="<c:url value="/resources/css/main.css"/>"
	type="text/css" rel="stylesheet" />
<script type="text/javascript"
	src="<c:url value="/resources/js/jquery-1.11.0.min.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/dev.js"/>"></script>
<body>
	<table class="mainTable">
		<thead>
			<tr>
				<th>#</th>
				<th>Api id</th>
				<th>Api key</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${devices}" var="device">
				<tr class="infoTr">
					<td>${device.getId()}</td>
					<td>${device.getApiId()}</td>
					<td>${device.getApiKey()}</td>
				</tr>
				<tr class="actionTr">
					<td class="actionTd" colspan="3"><a href="#">Edit</a></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<br />
	<a href="create">Create</a>
</body>
</html>