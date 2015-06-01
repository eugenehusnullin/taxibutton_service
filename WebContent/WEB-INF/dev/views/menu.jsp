<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="mainDiv">
	<a href="<c:url value="/admin/order/list"/>">1. Заказы</a> <br />
	<a href="<c:url value="/admin/order/create"/>">2. Создать заказ</a> <br />
	<a href="<c:url value="/admin/broker/list"/>">3. Диспетчерские</a>	<br />
	<a href="<c:url value="/admin/device/list"/>">4. Устройства</a> <br />
	<a href="<c:url value="/admin/maparea/list"/>">5. Геозоны Кнопки</a> <br />
	<a href="<c:url value="/admin/info/tariff"/>">6(a). readme (Тарифы общие)</a> <br />
	<a href="<c:url value="/admin/tariffdef/list"/>">6. Тарифы общие</a> <br />
	<a href="<c:url value="/admin/tariffdefmaparea/list"/>">7. Геозоны Тарифов</a> <br />
</div>