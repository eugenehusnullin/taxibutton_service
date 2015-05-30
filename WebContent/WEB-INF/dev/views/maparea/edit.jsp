<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div id="map_canvas" style="width: 100%; height: 500px"></div>

<script type="text/javascript" src="<c:url value="/admin/resources/js/jquery-1.11.0.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/admin/resources/js/bootstrap.min.js"/>"></script>
<script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?sensor=false&libraries=drawing"></script>
<script>
	var mapOptions = {
	    zoom: 12,
	    center: new google.maps.LatLng(${maparea.getPoints().get(0).getLatitude()}, ${maparea.getPoints().get(0).getLongitude()}),
	    mapTypeId: google.maps.MapTypeId.TERRAIN
	  };
	var map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);	
	var editMapAreaCoords = [
		<c:forEach items="${maparea.getPoints()}" var="point">
			new google.maps.LatLng(${point.getLatitude()}, ${point.getLongitude()}),
		</c:forEach>
	  ];
	
	var editMapArea  = new google.maps.Polygon({
		paths: editMapAreaCoords,
	    strokeColor: '#FF0000',
	    strokeOpacity: 0.8,
	    strokeWeight: 2,
	    fillColor: '#FF0000',
	    fillOpacity: 0.35
	});	
	
	serverUrl = "<c:url value="/admin"/>";	
	editMapArea.setMap(map);
</script>