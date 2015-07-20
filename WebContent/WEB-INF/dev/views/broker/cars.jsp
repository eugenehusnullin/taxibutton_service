<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<table border="1">
	<thead>
		<tr>
			<th>disp</th>
			<th>uuid</th>
			<th>name</th>
			<th>state</th>
			<th>date</th>
			<th>lat</th>
			<th>lon</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${cars}" var="car">
			<tr>
				<td>${car[1].getRealName()}</td>
				<td>${car[1].getUuid()}</td>
				<td>${car[1].getDriverDisplayName()}</td>
				<td>${car[0].getState()}</td>
				<td> ${car[0].getDate()}</td>
				<td>${car[0].getLatitude()}</td>
				<td>${car[0].getLongitude()}</td>
			</tr>
		</c:forEach>
	</tbody>
</table>
<br />
<br />
<br />
