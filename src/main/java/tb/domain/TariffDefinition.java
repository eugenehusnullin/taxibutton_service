package tb.domain;

import java.util.Set;

import tb.domain.maparea.MapArea;
import tb.domain.order.VehicleClass;

public class TariffDefinition {
	private Long id;
	private String idName;
	private VehicleClass vehicleClass;
	private Set<MapArea> mapAreas;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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
}
