$(document).ready(function() {
	$("div.actionTr").hide();

	$("tr.infoTr").mouseenter(function(e) {
		$("div.actionTr").hide();

		$(this).find("div.actionTr").show();
	});

	$("div.actionTr").mouseleave(function() {
		$(this).hide();
	});

	$("table.mainTable").mouseleave(function() {
		$("div.actionTr").hide();
	});

	$("table.mainTable").find("th").mouseenter(function() {
		$("div.actionTr").hide();
	});
});