var map;
var mapArea;

var polygonOptions = {
	fillColor : '#FF0000',
	fillOpacity : 0.4,
	strokeWeight : 1,
	clickable : true,
	zIndex : 1,
	editable : true
};

function initialize(serverUrl) {
	var mapOptions = {
		center : new google.maps.LatLng(46.8475925, 29.6140016),
		zoom : 12,
		mapTypeId : google.maps.MapTypeId.ROADMAP
	};
	map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);
	var drawingManager = new google.maps.drawing.DrawingManager({
		drawingControl : true,
		drawingControlOptions : {
			position : google.maps.ControlPosition.TOP_CENTER,
			drawingModes : [ 
			        //google.maps.drawing.OverlayType.MARKER,
					//google.maps.drawing.OverlayType.CIRCLE,
					google.maps.drawing.OverlayType.POLYGON //,
					//google.maps.drawing.OverlayType.POLYLINE,
					//google.maps.drawing.OverlayType.RECTANGLE
					]
		},
		drawingMode : google.maps.drawing.OverlayType.POLYGON,
		polygonOptions : polygonOptions
	});
	drawingManager.setMap(map);

	google.maps.event.addListener(drawingManager, 'polygoncomplete', function(
			obj) {
		mapArea = obj;
		showModal(serverUrl);
	});
}

function drawMapArea(mapArea) {
	mapArea.setMap(map);
}

function showModal(serverUrl) {
	$('#newMapzone').modal();

	$('#newMapzone .btn-closem').click(function(event) {
		event.preventDefault();
		$('#newMapzone').modal('hide');
	});

	$('#newMapzone .btn-save').click(function(event) {
		event.preventDefault();

		var vertices = mapArea.getPath();
		var newMapzone = {
			'name' : $('#newMapzoneName').val(),
			'points' : compilePoint(vertices)
		};

		addMapZone(serverUrl, newMapzone);
		$('#newMapzone').modal('hide');
	});
}

function compilePoint(vertices) {
	var arrPoint = [];
	for (var i = 0; i < vertices.length; i++) {
		var xy = vertices.getAt(i);
		arrPoint[arrPoint.length] = [ xy.lat(), xy.lng() ];
	}
	return arrPoint;
}

function addMapZone(serverUrl, mapZone) {

	$.ajax({
		url : serverUrl + "/maparea/add",
		dataType : "html",
		type : "POST",
		data : mapZone,
		success : function(r) {
		}
	});
}
