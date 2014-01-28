<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Brokers list</title>
</head>
<body>
	<c:forEach items="${brokers}" var="broker">
		<p>${broker.id}
			--- <a href="../tariff/tariff?id=${broker.id}">Tariff</a> --- <a
				href="../phone/blackList?id=${broker.id}">Black list</a> ---
			${broker.name} --- ${broker.apiurl}
		</p>
	</c:forEach>
	<br />
	<a href="create">Create</a>
</body>
</html>