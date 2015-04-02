<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

	<form method="post">
		<input type="hidden" name="brokerId" value="${brokerId}">
		<br /> name <input type="text" name="name" value="${name}" />
		<br /> clid<input type="text" name="apiId" value="${apiId}" />
		<br /> apikey <input type="text" name="apiKey" value="${apiKey}" />
		<br /> url <input type="text" name="apiUrl" value="${apiUrl}" />
		<br/>
		<input type="radio" name="smsMethod" value="0" <c:if test="${smsMethod == 0}">checked</c:if> /> sms48
		<input type="radio" name="smsMethod" value="1" <c:if test="${smsMethod == 1}">checked</c:if> /> self		
		<input type="radio" name="smsMethod" value="2" <c:if test="${smsMethod == 2}">checked</c:if> /> crm
		<br/> tariff
		<input type="radio" name="tarifftype" value="0" <c:if test="${tarifftype == 0}">checked</c:if> /> xml
		<input type="radio" name="tarifftype" value="1" <c:if test="${tarifftype == 1}">checked</c:if> /> json
		<br/>tariff url <input type="text" name="tariffUrl" value="${tariffUrl}"/>
		<br/>driver url <input type="text" name="driverUrl" value="${driverUrl}"/>
		<br/>time zone offset <input type="text" name="timezoneOffset" value="${timezoneOffset}"/>
		<br /><input type="submit" value="Save" />
	</form>