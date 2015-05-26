<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div id="map_canvas" style="width: 100%; height: 500px"></div>

<script type="text/javascript" src="<c:url value="/admin/resources/js/jquery-1.11.0.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/admin/resources/js/bootstrap.min.js"/>"></script>
<script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?sensor=false&libraries=drawing"></script>
<script type="text/javascript" src="<c:url value="/admin/resources/js/maparea.js"/>"></script>
<script>
	serverUrl = "<c:url value="/admin"/>";
	initialize(serverUrl);
</script>


<div class="modal fade" id="newMapzone">
	<div class="modal-dialog">
		<div class="modal-content ">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
				<h4 class="modal-title" id="myModalLabel">Описание геозоны</h4>
			</div>
			<div class="modal-body form-horizontal">
				<input type="hidden" id="MapzoneI">
				<div class="form-group">
					<div class="col-lg-12">
						<input type="text" class="form-control" placeholder="Название геозоны" name="" id="newMapzoneName">
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button class="btn btn-closem" data-dismiss="modal" aria-hidden="true">Отмена</button>
				<button class="btn btn-primary btn-save">Сохранить</button>
			</div>
		</div>
	</div>
</div>