<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<link media="screen" href="<c:url value="/resources/css/main.css"/>"
	type="text/css" rel="stylesheet" />
<script type="text/javascript"
	src="<c:url value="/resources/js/jquery-1.11.0.min.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/dev.js"/>"></script>
<body>
	<table border="1">
		<thead>
			<tr>
				<th>#</th>
				<th>Api id</th>
				<th>Api key</th>
				<th>Name</th>
				<th>Api url</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${brokers}" var="broker">
				<tr class="infoTr">
					<td>${broker.getId()}</td>
					<td>${broker.getApiId()}</td>
					<td>${broker.getApiKey()}</td>
					<td>${broker.getName()}</td>
					<td>${broker.getApiurl()}</td>
				</tr>
				<tr class="actionTr">
					<td class="actionTd" colspan="5"><a
						href="../tariff/tariff?id=${broker.getId()}">Tariff</a>---<a
						href="../phone/blackList?id=${broker.getId()}">Black list</a>---<a
						href="qiwi?id=${broker.getId()}">QIWI</a>--- <a
						href="delete?id=${broker.getId()}">Delete</a>---<a
						href="edit?id=${broker.getId()}">Edit</a></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<br />
	<a href="create">Create</a>	
	<br />
	<a href="carsynch">Car synch (pull cars from dispatchers)</a>
	<br />
	<a href="tariffsynch">Tariff synch (pull tariffs from dispatchers)</a>
	<br />
	<br />
	<br />