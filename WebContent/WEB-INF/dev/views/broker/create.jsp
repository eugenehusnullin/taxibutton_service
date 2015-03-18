<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


	<form method="post">
		<br/>Name
		<input type="text" name="name" value="Желтое такси" />
		<br/>Apiurl
		<input type="text" name="apiurl" value="http://ip:port/GUID" />
		<br/>clid
		<input type="text" name="apiId" value="1"/>
		<br/>apikey
		<input type="text" name="apiKey" value="1"/>
		<br/>
		<input type="radio" name="smsMethod" value="0" checked /> sms48
		<input type="radio" name="smsMethod" value="1" /> self
		<br/>Tariff url
		<input type="text" name="tariffurl" value="http://"/>
		<br/>Driver url
		<input type="text" name="driverurl" value="http://"/>
		<br />		
		<input type="submit" value="save" />
	</form>