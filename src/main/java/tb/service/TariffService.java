package tb.service;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

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
	private ITariffDao simpleTariffDao;
	@Autowired
	private IDeviceDao deviceDao;
	@Autowired
	private IBrokerDao brokerDao;

	@Transactional
	public JSONArray getAll(JSONObject requestJson) throws DeviceNotFoundException {

		String apiId = requestJson.optString("apiId");
		Device device = deviceDao.get(apiId);
		if (device == null) {
			throw new DeviceNotFoundException(apiId);
		}

		List<Tariff> tariffs = simpleTariffDao.getAll();
		JSONArray tariffsJsonArray = new JSONArray();
		for (Tariff simoleTariff : tariffs) {
			JSONObject tariffJson = new JSONObject();
			tariffJson.put("brokerId", simoleTariff.getBroker().getUuid());
			tariffJson.put("tariff", simoleTariff.getTariff());
			tariffsJsonArray.put(tariffJson);
		}
		return tariffsJsonArray;
	}

	@Transactional
	public String getTariff(Long brokerId) {
		Broker broker = brokerDao.get(brokerId);
		Tariff simpleTariff = simpleTariffDao.getActive(broker).get(0);
		return simpleTariff.getTariff();
	}

	@Transactional
	public void create(String tariff, Long brokerId) throws ParserConfigurationException, SAXException, IOException {
	}

	@Transactional
	public void pullBrokersTariffs() {

	}
}
