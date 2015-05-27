package tb.tariffdefinition;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tb.dao.ITariffDefinitionDao;
import tb.domain.TariffDefinition;
import tb.domain.maparea.MapArea;
import tb.domain.order.VehicleClass;
import tb.service.MapAreaAssist;

@Service
public class TariffDefinitionHelper {
	@Autowired
	private MapAreaAssist mapAreaAssist;
	@Autowired
	private ITariffDefinitionDao tariffDefinitionDao;

	@Transactional
	public String getTariffIdName(double lat, double lon, VehicleClass vehicleClass) {
		TariffDefinition thatTariffDefinition = getTariffDefinition(lat, lon, vehicleClass);

		if (thatTariffDefinition != null) {
			return thatTariffDefinition.getIdName();
		} else {
			return null;
		}
	}

	@Transactional
	public TariffDefinition getTariffDefinition(double lat, double lon, VehicleClass vehicleClass) {
		List<TariffDefinition> tariffDefinitions = tariffDefinitionDao.get(vehicleClass);

		TariffDefinition thatTariffDefinition = null;
		for (TariffDefinition tariffDefinition : tariffDefinitions) {
			Set<MapArea> mapAreas = tariffDefinition.getMapAreas();
			for (MapArea mapArea : mapAreas) {
				if (mapAreaAssist.contains(mapArea, lat, lon)) {
					thatTariffDefinition = tariffDefinition;
					break;
				}
			}
			if (thatTariffDefinition != null) {
				break;
			}
		}

		return thatTariffDefinition;
	}
}
