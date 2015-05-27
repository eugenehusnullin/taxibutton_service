<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


	<form method="POST">
		<br /> IdName:
		<input type="text" name="idname" />
		
		<br />
		<br /> RoutingServiceName:
		<br />
		<input type="text" name="routingservicename" value="YandexMapsService"/>
		
		<br />
		<br /> Vehicle class:
		<br />
		<c:forEach items="${vcl}" var="vc">
			<input type="radio" name="vc" value="${vc}" />${vc}<br />
		</c:forEach>
		
		<br /> Map areas:
		<br />
		<c:forEach items="${mapareas}" var="maparea">
			<input type="checkbox" name="mapareas" value="${maparea.getId()}" />${maparea.getName()}<br />
		</c:forEach>
		
		<br /> Tariff json body:
		<br />
		<textarea rows="10" cols="120" name="body"></textarea>
		
		<br />
		<input type="submit" value="save" />
	</form>