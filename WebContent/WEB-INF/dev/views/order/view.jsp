<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


	Order id:
	<br />
	${order.getId()}
	<br />
	Destinations:
	<br />
	<c:forEach items="${destinations}" var="destination">
		<p>Id: ${destination.getId()}</p>s
		<p>Full address: ${destination.getFullAddress()}</p>
		<br />
	</c:forEach>
	<br />