package tb.service;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tb.dao.IBrokerDao;
import tb.dao.IDeviceDao;
import tb.dao.ITariffDao;
import tb.domain.Broker;
import tb.domain.Device;
import tb.domain.Tariff;
import tb.service.exceptions.DeviceNotFoundException;

@Service
public class TariffService {
	@Autowired
	private ITariffDao tariffDao;
	@Autowired
	private IDeviceDao deviceDao;
	@Autowired
	private IBrokerDao brokerDao;

	@Transactional
	public JSONArray getByBroker(JSONObject requestJson) throws DeviceNotFoundException {

		String apiId = requestJson.optString("apiId");
		Device device = deviceDao.get(apiId);
		if (device == null) {
			throw new DeviceNotFoundException(apiId);
		}

		String uuid = requestJson.optString("uuid");
		Broker broker = brokerDao.get(uuid);

		List<Tariff> tariffs = tariffDao.get(broker);
		JSONArray tariffsJsonArray = new JSONArray();
		for (Tariff tariff : tariffs) {
			JSONObject tariffJson = new JSONObject();
			tariffJson.put("brokerId", tariff.getBroker().getUuid());
			tariffJson.put("tariff", tariff.getTariff());
			tariffsJsonArray.put(tariffJson);
		}
		return tariffsJsonArray;
	}

	@Transactional
	public String getTariff(Long brokerId) {
		Broker broker = brokerDao.get(brokerId);
		Tariff simpleTariff = tariffDao.get(broker).get(0);
		return simpleTariff.getTariff();
	}
}
