<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

	<form method="post">
		<input type="hidden" name="brokerId" value="${brokerId}"> Name
		<br /> <input type="text" name="name" value="${name}" /> <br /> Api
		id <br /> <input type="text" name="apiId" value="${apiId}" /> <br />
		Api key <br /> <input type="text" name="apiKey" value="${apiKey}" />
		<br /> Api url <br /> <input type="text" name="apiUrl"
			value="${apiUrl}" /> <br /> <input type="submit" value="Save" />
	</form>