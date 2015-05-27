package tb.domain;

import java.util.Set;

import tb.domain.maparea.MapArea;
import tb.domain.order.VehicleClass;

public class TariffDefinition {
	private String idName;
	private VehicleClass vehicleClass;
	private Set<MapArea> mapAreas;
	private String body;
	private String routingServiceName;

	public VehicleClass getVehicleClass() {
		return vehicleClass;
	}

	public void setVehicleClass(VehicleClass vehicleClass) {
		this.vehicleClass = vehicleClass;
	}

	public Set<MapArea> getMapAreas() {
		return mapAreas;
	}

	public void setMapAreas(Set<MapArea> mapAreas) {
		this.mapAreas = mapAreas;
	}

	public String getIdName() {
		return idName;
	}

	public void setIdName(String idName) {
		this.idName = idName;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getRoutingServiceName() {
		return routingServiceName;
	}

	public void setRoutingServiceName(String routingServiceName) {
		this.routingServiceName = routingServiceName;
	}
}
