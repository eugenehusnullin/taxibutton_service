<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><tiles:insertAttribute name="title" ignore="true" /></title>
<link media="screen" href="<c:url value="/admin/resources/css/main.css"/>" type="text/css" rel="stylesheet" />

<link type="text/css" rel="stylesheet" href="<c:url value="/admin/resources/css/bootstrap.min.css"/>">
<link type="text/css" rel="stylesheet" href="<c:url value="/admin/resources/css/dataTables.bootstrap.css"/>">
<link type="text/css" rel="stylesheet" href="<c:url value="/admin/resources/css/datatables.colvis.min.css"/>">

<script type="text/javascript" src="<c:url value="/admin/resources/js/jquery-1.11.0.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/admin/resources/js/jquery.dataTables.js"/>"></script>
<script type="text/javascript" src="<c:url value="/admin/resources/js/bootstrap.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/admin/resources/js/datatables.colvis.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/admin/resources/js/dataTables.bootstrap.js"/>"></script>

<script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?sensor=false&libraries=drawing"></script>
<script type="text/javascript" src="<c:url value="/admin/resources/js/mapzone.js"/>"></script>

</head>
<body>
	<tiles:insertAttribute name="menu" ignore="true" />
	<tiles:insertAttribute name="body" />
</body>
</html>