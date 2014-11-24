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

	<table class="mainTable">
		<thead>
			<tr>
				<th>#</th>
				<th>Api id</th>
				<th>Phone</th>
				<th>Key</th>
				<th>Action</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${devices}" var="device">
				<tr class="infoTr">
					<td>${device.getId()}</td>
					<td>${device.getApiId()}</td>
					<td>${device.getPhone()}</td>
					<td>${device.getConfirmKey()}</td>
					<td><a href="tariffs?apiId=${device.getApiId()}">Tariffs</a></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<br />
	<a href="create">Create</a><br />