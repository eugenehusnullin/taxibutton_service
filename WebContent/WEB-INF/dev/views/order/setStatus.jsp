<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<form method="POST">
    <input type="hidden" name="id" value="${orderId}" /> 
    <br /> broker clid
    <input type="text" name="clid" value="${apiId}"/>
    <br /> broker apikey
    <input type="text" name="apikey" value="${apiKey}"/>
    <br /> status
    <select name="status">
    	<option value="driving">driving</option>
    	<option value="waiting">waiting</option>
    	<option value="transporting">transporting</option>
    	<option value="complete">complete</option>
    	<option value="cancelled">cancelled</option>
    	<option value="failed">failed</option>
   </select>
   
    <br /> extra (completed-sum | cancel,failed-reason | driving-uuid)
    <input type="text" name="extra" value="${extra}"/>
    <br /> newcar (uuid)
    <input type="text" name="newcar" />
    <br />
    <input type="submit" value="Send" />
    <br />
</form>