<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<table border="1">
	<thead>
		<tr>
			<th>name</th>
			<th>actions</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${tariffdefmapareas}" var="tariffdefmaparea">
			<tr>
				<td>${tariffdefmaparea.getName()}</td>
				<td><a href="edit?name=${tariffdefmaparea.getName()}">edit</a> | <a href="del?name=${tariffdefmaparea.getName()}">delete</a></td>
			</tr>
		</c:forEach>
	</tbody>
</table>


<br />
<br />
<a href="add">Add new</a>
<br />