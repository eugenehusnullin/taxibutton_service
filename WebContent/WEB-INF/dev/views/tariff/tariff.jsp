<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Add tariff</title>
</head>
<body>
	<form method="post">
		<br />Tariff<input type="hidden" name="brokerId" value="${brokerId}">
		<br /><textarea name="tariff"></textarea>
		<br /><input type="submit" value="save" />
	</form>
</body>
</html>