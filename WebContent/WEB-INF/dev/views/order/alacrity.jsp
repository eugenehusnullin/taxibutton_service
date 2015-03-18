<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<form method="POST">
	<input type="hidden" name="orderId" value="${orderId}" /> 
	Broker api id 
	<br />
	<input type="text" name="apiId" />
	<br />
	Broker api key
	<br />
	<input type="text" name="apiKey" />
	<br />
	Driver uuid
	<br />
	<input 	type="text" name="uuid" />
	<br />
	<input type="submit" value="Send" />
</form>
<h3>Added alacrities</h3>
<c:forEach items="${alacrities}" var="alacrity">
	<p>${alacrity.getId()}---${alacrity.getDate()}</p>
</c:forEach>
