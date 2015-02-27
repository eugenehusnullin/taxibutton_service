<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


	<table class="mainTable">
		<thead>
			<tr>
				<th>#</th>
				<th>Latitude</th>
				<th>Longitude</th>
				<th>Date</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${geoList}" var="geo">
				<tr class="infoTr">
					<td>${geo.getId()}</td>
					<td>${geo.getLat()}</td>
					<td>${geo.getLon()}</td>
					<td>${geo.getDate()}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
