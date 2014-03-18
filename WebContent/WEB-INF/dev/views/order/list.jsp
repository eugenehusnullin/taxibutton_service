<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Orders list</title>
</head>
<link media="screen" href="<c:url value="/resources/css/main.css"/>"
	type="text/css" rel="stylesheet" />
<script type="text/javascript"
	src="<c:url value="/resources/js/jquery-1.11.0.min.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/dev.js"/>"></script>
<script type="text/javascript">
	$(document)
			.ready(

					function() {

						$(
								"select#countSelect [value='"
										+ $("input#count").val() + "']").attr(
								"selected", "selected");

						$("a#setCountRef").click(
								function() {

									$("input#count").val(
											$("select#countSelect").val());

									$(this).attr("href", getListRef());
								});

						$("a.setPageRef").click(
								function() {

									var firstRowIndex = (parseInt($(this)
											.text().trim()) - 1)
											* parseInt($("input#count").val());

									$(this).attr("href",
											getListRef(firstRowIndex));
								});

						$("tr.sortableHead")
								.find("a:not(.notSorted)")
								.click(
										function() {

											if ($("input#orderDirection").val() == "desc") {
												$("input#orderDirection").val(
														"asc");
											} else {
												$("input#orderDirection").val(
														"desc");
											}

											$(this).attr(
													"href",
													getListRef($("input#start")
															.val(), $(this)
															.attr("id")),
													$("input#orderDirection")
															.val());
										});

					});

	function getListRef(start, orderField, orderDirection, count) {

		orderField = typeof orderField !== 'undefined' ? orderField : $(
				"input#orderField").val();
		orderDirection = typeof orderDirection !== 'undefined' ? orderDirection
				: $("input#orderDirection").val();
		start = typeof start !== 'undefined' ? start : $("input#start").val();
		count = typeof count !== 'undefined' ? count : $("input#count").val();

		return "list?orderField=" + orderField + "&orderDirection="
				+ orderDirection + "&start=" + start + "&count=" + count;
	}
</script>
<body>
	<div class="tableSizeRow">
		<input type="hidden" id="orderField" value="${orderField}"> <input
			type="hidden" id="orderDirection" value="${orderDirection}">
		<input type="hidden" id="start" value="${start}"> <input
			type="hidden" id="count" value="${count}"> Show by <select
			name="countSelect" id="countSelect">
			<option value="3">3</option>
			<option value="10">10</option>
			<option value="50">50</option>
			<option value="100">100</option>
		</select> rows <a href="#" id="setCountRef">apply</a><br />
	</div>
	<div class="grouping">
		Group by status:<br /> <input type="checkbox" name="statusGroup[]"
			value="Created"> Created <br /> <input type="checkbox"
			name="statusGroup[]" value="Taked"> Taked <br /> <input
			type="checkbox" name="statusGroup[]" value="Driving"> Driving
		<br /> <input type="checkbox" name="statusGroup[]" value="Waiting">
		Waiting <br /> <input type="checkbox" name="statusGroup[]"
			value="Transporting"> Transporting <br /> <input
			type="checkbox" name="statusGroup[]" value="Completed">
		Completed <br /> <input type="checkbox" name="statusGroup[]"
			value="Cancelled"> Cancelled <br /> <input type="checkbox"
			name="statusGroup[]" value="Failed"> Failed <br /> <a
			href="#">Group</a>
	</div>
	<div>
		<table class="mainTable" id="mainTable">
			<thead>
				<tr class="sortableHead">
					<th><a href="#" id="id">#</a></th>
					<th><a href="#" id="bookingDate">Time</a></th>
					<th><a href="#" class="notSorted">Source</a></th>
					<th><a href="#" id="urgent">Urgent</a></th>
					<th><a href="#" class="notSorted">Broker</a></th>
					<th><a href="#" id="phone">Phone</a></th>
					<th><a href="#" class="notSorted">Status</a></th>
					<th>Uuid</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${orders}" var="order">
					<tr class="infoTr">
						<td>${order.getId()}</td>
						<td>${order.getBookingDate()}</td>
						<td>${order.getSourceShortAddress()}</td>
						<td>${order.getUrgent()}</td>
						<td>${order.getBrokerName()}</td>
						<td>${order.getPhone()}</td>
						<td>${order.getLastStatus()}
							<div class="actionTr">
								<a href="send?id=${order.getId()}">Send</a>---<a
									href="give?id=${order.getId()}">Give</a>---<a
									href="setStatus?id=${order.getId()}">Send status</a>---<a
									href="showStatus?id=${order.getId()}">Show status</a>---<a
									href="getStatus?id=${order.getId()}">Get status</a>---<a
									href="setGeoData?id=${order.getId()}">Send geo</a>---<a
									href="showGeoData?id=${order.getId()}">Show geo</a>---<a
									href="getGeoData?id=${order.getId()}">Get geo data</a>---<a
									href="cancel?id=${order.getId()}">Cancel</a>---<a
									href="delete?id=${order.getId()}">Delete</a>---<a
									href="alacrity?id=${order.getId()}">Alacrity</a>
							</div>
						</td>
						<td>${order.getUuid()}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<span>Text</span> Pages:
	<c:forEach items="${pages}" var="page">
		<a href="#" class="setPageRef">${page}</a>
	</c:forEach>
	<br />
	<a href="create">Create</a>
</body>
</html>