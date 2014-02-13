$(document).ready(function() {
	$("tr.actionTr").hide();

	$("tr.infoTr").mouseenter(function() {
		$("tr.actionTr").hide();
		$(this).next().show();
	});

	$("tr.actionTr").mouseleave(function() {
		$(this).hide();
	});
});