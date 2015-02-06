<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


	<form method="post">
		<br/>Name
		<input type="text" name="name" value="Желтое такси" />
		<br/>Apiurl
		<input type="text" name="apiurl" value="http://ip:port/GUID" />
		<br/>Api id
		<input type="text" name="apiId" value="1"/>
		<br/>Api key
		<input type="text" name="apiKey" value="1"/>
		<br/>
		<input type="radio" name="smsMethod" value="0" checked /> sms48
		<input type="radio" name="smsMethod" value="1" /> self
		<br />
		<input type="submit" value="save" />
	</form>