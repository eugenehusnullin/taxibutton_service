package tb.dao;

import java.util.List;

import tb.domain.TariffDefinition;
import tb.domain.order.VehicleClass;

public interface ITariffDefinitionDao {
	List<TariffDefinition> get(VehicleClass vehicleClass);
}
