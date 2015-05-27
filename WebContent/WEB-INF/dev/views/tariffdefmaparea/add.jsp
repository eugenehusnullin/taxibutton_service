<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


	<form method="POST">
		<br /> Name:
		<input type="text" name="name" />
		
		<br />	
		<br /> Map area json body:
		<br />
		<textarea rows="10" cols="120" name="body"></textarea>
		
		<br />
		<input type="submit" value="save" />
	</form>