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
		<input type="text" name="name" />
		<br/>Apiurl
		<input type="text" name="apiurl" />
		<br/>
		<input type="submit" value="save" />
	</form>
</body>
</html>