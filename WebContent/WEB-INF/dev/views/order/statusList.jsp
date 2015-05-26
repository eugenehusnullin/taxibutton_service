<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<table border="1">
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