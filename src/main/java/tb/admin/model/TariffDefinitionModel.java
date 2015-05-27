package tb.admin.model;

public class TariffDefinitionModel {

	private String idName;
	private String vehicleClass;
	private String mapAreasNames;
	private String routingServiceName;

	public String getIdName() {
		return idName;
	}

	public void setIdName(String idName) {
		this.idName = idName;
	}

	public String getVehicleClass() {
		return vehicleClass;
	}

	public void setVehicleClass(String vehicleClass) {
		this.vehicleClass = vehicleClass;
	}

	public String getMapAreasNames() {
		return mapAreasNames;
	}

	public void setMapAreasNames(String mapAreasNames) {
		this.mapAreasNames = mapAreasNames;
	}

	public String getRoutingServiceName() {
		return routingServiceName;
	}

	public void setRoutingServiceName(String routingServiceName) {
		this.routingServiceName = routingServiceName;
	}
}
