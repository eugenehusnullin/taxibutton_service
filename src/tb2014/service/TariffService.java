package tb2014.service;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tb2014.business.IDeviceBusiness;
import tb2014.business.ISimpleTariffBusiness;
import tb2014.domain.Device;
import tb2014.domain.tariff.SimpleTariff;

@Service
public class TariffService {

	@Autowired
	private ISimpleTariffBusiness simpleTariffBusiness;
	@Autowired
	private IDeviceBusiness deviceBusiness;

	@Transactional
	public JSONArray getAll(JSONObject requestJson) throws DeviceNotFoundException {

		String apiId = requestJson.optString("apiId");
		Device device = deviceBusiness.get(apiId);
		if (device == null) {
			throw new DeviceNotFoundException(apiId);
		}

		List<SimpleTariff> tariffs = simpleTariffBusiness.getAll();
		JSONArray tariffsJsonArray = new JSONArray();
		for (SimpleTariff simoleTariff : tariffs) {
			JSONObject tariffJson = new JSONObject();
			tariffJson.put("brokerId", simoleTariff.getBroker().getUuid());
			tariffJson.put("tariff", simoleTariff.getTariffs());
			tariffsJsonArray.put(tariffJson);
		}
		return tariffsJsonArray;
	}
}
