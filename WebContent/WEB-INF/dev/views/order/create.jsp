<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Create order</title>
</head>
<body>
	<form method="POST">
		<h3>Order</h3>
		Type:<br />
		<input type="radio" name="orderType" value="urgent" checked /> urgent order
		<input type="radio" name="orderType" value="nonUrgent" /> non-urgent order
		<br />
		Client phone:<br />
		<input type="text" name="phone" /><br />
		<h3>Source</h3>
		Full address:
		<br />
		<input type="text" name="sFullAddress" />
		<br />
		Short address:
		<br />
		<input type="text" name="sShortAddress" />
		<br />
		Closest station:
		<br />
		<input type="text" name="sClosestStation" />
		<br />
		Point longitude:
		<br />
		<input type="text" name="sourceLon" />
		<br />
		Point latitude:
		<br />
		<input type="text" name="sourceLat" />
		<br />
		Country:
		<br />
		<input type="text" name="sCountry" />
		<br />
		Locality:
		<br />
		<input type="text" name="sLocality" />
		<br />
		Street:
		<br />
		<input type="text" name="sStreet" />
		<br />
		Housing:
		<br />
		<input type="text" name="sHousing" />
		<br />
		<h3>Destination</h3>
		Full address:
		<br />
		<input type="text" name="dFullAddress" />
		<br />
		Short address:
		<br />
		<input type="text" name="dShortAddress" />
		<br />
		Closest station:
		<br />
		<input type="text" name="dClosestStation" />
		<br />
		Point longitude:
		<br />
		<input type="text" name="destinationLon" />
		<br />
		Point latitude:
		<br />
		<input type="text" name="destinationLat" />
		<br />
		Country:
		<br />
		<input type="text" name="dCountry" />
		<br />
		Locality:
		<br />
		<input type="text" name="dLocality" />
		<br />
		Street:
		<br />
		<input type="text" name="dStreet" />
		<br />
		Housing:
		<br />
		<input type="text" name="dHousing" />
		<br />
		<h3>Booking</h3>
		Date:
		<br />
		<input type="text" name="bookingDate">
		<br />
		Hour:
		<br />
		<input type="text" name="bookingHour">
		<br />
		Minute:
		<br />
		<input type="text" name="bookingMin">
		<br />
		<h3>Requirements</h3>
		<input type="checkbox" name="requirements" value="isAnimalTransport"> animals<br />
		<input type="checkbox" name="requirements" value="isCheck"> check<br />
		<input type="checkbox" name="requirements" value="isChildChair"> child chair <input type="text" name="childAge"/><br />
		<input type="checkbox" name="requirements" value="isConditioner"> conditioner<br />
		<input type="checkbox" name="requirements" value="noSmoking"> no smoking<br />
		<input type="checkbox" name="requirements" value="isUniversal"> universal<br />
		<input type="checkbox" name="requirements" value="isCoupon"> coupon<br />
		<br />
		<input type="submit" value="save" />
	</form>
</body>
</html>