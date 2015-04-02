<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div>
	<table border="1">
		<thead>
			<tr>
				<th>id</th>
				<th>bookingDate</th>
				<th>source</th>
				<th>notlater</th>
				<th>brokerId</th>
				<th>phone</th>
				<th>orderid</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${orders}" var="order">
				<tr>
					<td>${order.getId()}</td>
					<td>${order.getBookingDate()}</td>
					<td>${order.getSourceShortAddress()}</td>
					<td>${order.getUrgent()}</td>
					<td>${order.getBrokerName()}</td>
					<td>${order.getPhone()}</td>
					<td>${order.getUuid()}</td>
				</tr>
				<tr>
					<td>client actions:</td>
					<td><a href="getStatus?id=${order.getId()}">Get status</a> | <a href="getGeodata?id=${order.getId()}">geodata</a></td>
				</tr>
				<tr>
					<td>admin actions:</td>
					<td><a href="showStatuses?id=${order.getId()}">Show status</a></td>
				</tr>
				<tr>
					<td>yandex actions:</td>
					<td><a href="cancel?id=${order.getId()}">Cancel</a></td>
				</tr>
				<tr>
					<td>taxi actions:</td>
					<td><a href="alacrity?id=${order.getId()}">alacrity</a> | <a href="setStatus?id=${order.getId()}">set status</a>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
From:
<c:forEach items="${pages}" var="page">
	<a href="list?start=${page}">${page}</a>
</c:forEach>


