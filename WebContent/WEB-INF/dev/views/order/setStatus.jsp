<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<form method="POST">
	<input type="hidden" name="id" value="${orderId}" />
	broker clid <input type="text" name="clid" /><br />
	broker apikey <input type="text" name="apikey" /><br />
	status <input type="text" name="status" /> <br />
	extra (completed-sum | cancel,failed-reason | driving-uuid) <input type="text" name="extra" /><br />
	newcar (uuid) <input type="text" name="newcar" /><br /> 
	<input type="submit" value="Send" /><br />
</form>