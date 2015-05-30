<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="mainDiv">
	<a href="<c:url value="/admin/order/list"/>">Заказы</a> <br />
	<a href="<c:url value="/admin/order/create"/>">Создать заказ</a> <br />
	<a href="<c:url value="/admin/broker/list"/>">Диспетчерские</a>	<br />
	<a href="<c:url value="/admin/device/list"/>">Устройства</a> <br />
	<a href="<c:url value="/admin/maparea/list"/>">Геозоны Кнопки</a> <br />
	<a href="<c:url value="/admin/tariffdef/list"/>">Тарифы общие</a> <br />
	<a href="<c:url value="/admin/tariffdefmaparea/list"/>">Геозоны Тарифов</a> <br />
</div>