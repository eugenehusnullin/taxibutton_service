<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<table border="1">
	<thead>
		<tr>
			<th>idname</th>
			<th>Routing Service Name</th>
			<th>vehicle class</th>
			<th>map areas names</th>
			<th>actions</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${tariffdefs}" var="tariffdef">
			<tr>
				<td>${tariffdef.getIdName()}</td>
				<td>${tariffdef.getRoutingServiceName()}</td>
				<td>${tariffdef.getVehicleClass()}</td>
				<td>${tariffdef.getMapAreasNames()}</td>
				<td><a href="edit?idname=${tariffdef.getIdName()}">edit</a> | <a href="del?idname=${tariffdef.getIdName()}">delete</a></td>
			</tr>
		</c:forEach>
	</tbody>
</table>


<br />
<br />
<a href="add">Add new</a>
<br />