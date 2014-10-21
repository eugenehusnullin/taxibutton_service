<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


	<form method="POST">
		<input type="hidden" name="orderId" value="${orderId}" /> Broker api
		id <br /> <input type="text" name="apiId" /> <br /> Broker api key
		<br /> <input type="text" name="apiKey" /> <br /> Driver name <br />
		<input type="text" name="driverName" /> <br /> Driver second name <br />
		<input type="text" name="driverSecondName" /> <br /> Driver third
		name <br /> <input type="text" name="driverThirdName" /> <br />
		Driver phone <br /> <input type="text" name="driverPhone" /> <br />
		Car number <br /> <input type="text" name="carNumber" /> <br /> Car
		color <br /> <input type="text" name="carColor" /> <br /> Car mark
		<br /> <input type="text" name="carMark" /> <br /> Car model <br />
		<input type="text" name="carModel" /> <br /> <input type="submit"
			value="Send" />
	</form>
	<h3>Added alacrities</h3>
	<c:forEach items="${alacrities}" var="alacrity">
		<p>${alacrity.getId()}---${alacrity.getDate()}</p>
	</c:forEach>
