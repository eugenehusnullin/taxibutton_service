<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


	<form method="POST">
		<input type="hidden" name="oldidname" value="${idname}">
		<br /> IdName:
		<input type="text" name="idname" value="${idname}"/>
		
		<br />
		<br /> RoutingServiceName (для предварительного расчета):
		<br />
		<c:forEach items="${routingservicenames}" var="routingservicename">
			<input type="radio" name="routingservicename" value="${routingservicename}"
				<c:if test="${routingservicename == currentroutingservicename}">
					checked="checked"
				</c:if>
			 />${routingservicename}<br />
		</c:forEach>
		
		<br />
		<br /> Vehicle class:
		<br />
		<c:forEach items="${vcl}" var="vc">
			<input type="radio" name="vc" value="${vc}"
				<c:if test="${vc == currentvc}">
					checked="checked"
				</c:if>
			 />${vc}<br />
		</c:forEach>
		
		<br /> Map areas:
		<br />
		<c:forEach items="${mapareas}" var="maparea">
			<input type="checkbox" name="mapareas" value="${maparea.getId()}" 
				<c:if test="${mapareasids.contains(maparea.getId())}">
					checked="checked"
				</c:if>
			/>${maparea.getName()}<br />
		</c:forEach>
		
		<br /> Tariff json body:
		<br />
		<textarea rows="10" cols="120" name="body">${body}</textarea>
		
		<br />
		<input type="submit" value="save" />
	</form>