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
		<c:forEach items="${mapareas}" var="maparea">
			<tr>
				<td>${maparea.getName()}</td>
				<td><a href="edit?id=${maparea.getId()}">edit</a> | <a href="del?id=${maparea.getId()}">delete</a></td>
			</tr>
		</c:forEach>
	</tbody>
</table>


<br />
<br />
<a href="add">Add new</a>
<br />