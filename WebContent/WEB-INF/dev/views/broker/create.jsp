<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Add broker</title>
</head>
<body>
	<form method="post">
		<br/>Name
		<input type="text" name="name" value="Желтое такси" />
		<br/>Apiurl
		<input type="text" name="apiurl" value="http://localhost:8080/tb2014/test" />
		<br/>Api id
		<input type="text" name="apiId" value="1"/>
		<br/>Api key
		<input type="text" name="apiKey" value="1"/>
		<br />
		<input type="submit" value="save" />
	</form>
</body>
</html>